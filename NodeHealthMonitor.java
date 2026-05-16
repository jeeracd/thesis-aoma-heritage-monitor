import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.*;

/**
 * Monitors ESP32 slave node health via serial port or built-in simulator.
 *
 * Expected serial line format from the Master ESP32:
 *   NODE:S1 RSSI:-65 LOSS:2.1 LAST:234 TSF:1 INTERP:0
 */
public final class NodeHealthMonitor implements AutoCloseable {

    public enum NodeStatus { ONLINE, DEGRADED, OFFLINE }

    public record NodeState(
            String nodeId,
            NodeStatus status,
            int rssiDbm,
            double lossPercent,
            long lastSeenMs,
            boolean tsfSync,
            boolean interpolated) {}

    private static final Pattern NODE_ID   = Pattern.compile("NODE:(\\S+)");
    private static final Pattern LAST_SEEN = Pattern.compile("LAST:(\\d+)");
    private static final Pattern RSSI      = Pattern.compile("RSSI:(-?\\d+)");
    private static final Pattern LOSS      = Pattern.compile("LOSS:([\\d.]+)");
    private static final Pattern TSF       = Pattern.compile("TSF:(\\d)");
    private static final Pattern INTERP    = Pattern.compile("INTERP:(\\d)");

    static final int NODE_COUNT = 8;
    private static final long OFFLINE_THRESH_MS = 5_000;

    private final Map<String, NodeState> states = new LinkedHashMap<>();
    private final Consumer<Map<String, NodeState>> onUpdate;
    private volatile boolean running;
    private Thread worker;

    private Object serialPort;
    private InputStream serialIn;

    public NodeHealthMonitor(Consumer<Map<String, NodeState>> onUpdate) {
        this.onUpdate = onUpdate;
    }

    public synchronized void startSimulator() {
        stop();
        running = true;
        worker = new Thread(this::runSim, "node-health-sim");
        worker.setDaemon(true);
        worker.start();
    }

    public synchronized void startSerial(String portName) throws Exception {
        stop();
        serialPort = findPort(portName);
        invokeVoid(serialPort, "openPort");
        invokeVoid(serialPort, "setBaudRate",
                new Class[]{int.class}, new Object[]{115200});
        serialIn = (InputStream) serialPort.getClass()
                .getMethod("getInputStream").invoke(serialPort);
        running = true;
        worker = new Thread(this::runSerial, "node-health-serial");
        worker.setDaemon(true);
        worker.start();
    }

    public synchronized void stop() {
        running = false;
        if (worker != null) {
            worker.interrupt();
            worker = null;
        }
        if (serialPort != null) {
            try { invokeVoid(serialPort, "closePort"); } catch (Exception ignored) {}
            serialPort = null;
        }
        serialIn = null;
    }

    public synchronized Map<String, NodeState> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(states));
    }

    public static List<String> listSystemPortNames() {
        try {
            Class<?> cls = Class.forName("com.fazecast.jSerialComm.SerialPort");
            Object[] ports = (Object[]) cls.getMethod("getCommPorts").invoke(null);
            List<String> names = new ArrayList<>();
            for (Object p : ports) {
                String n = safeInvokeString(p, "getSystemPortName");
                if (n != null && !n.isBlank()) names.add(n);
            }
            return names;
        } catch (Exception e) {
            return List.of();
        }
    }

    private void runSerial() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(serialIn))) {
            String line;
            while (running && (line = br.readLine()) != null) {
                handleLine(line);
            }
        } catch (Exception ignored) {}
    }

    private void runSim() {
        Random rng = new Random();
        String[] nodeIds = new String[NODE_COUNT];
        for (int i = 0; i < NODE_COUNT; i++) nodeIds[i] = "S" + (i + 1);

        while (running) {
            for (String id : nodeIds) {
                if (!running) break;
                int rssi = -40 - rng.nextInt(55);
                double loss = rng.nextDouble() * 18.0;
                long last = rng.nextInt(4500);
                boolean tsf = rng.nextDouble() > 0.1;
                boolean interp = loss > 8.0 && rng.nextBoolean();
                String line = "NODE:" + id
                        + " RSSI:" + rssi
                        + " LOSS:" + String.format("%.1f", loss)
                        + " LAST:" + last
                        + " TSF:" + (tsf ? 1 : 0)
                        + " INTERP:" + (interp ? 1 : 0);
                handleLine(line);
            }
            try { Thread.sleep(1500); } catch (InterruptedException e) { break; }
        }
    }

    void handleLine(String line) {
        if (line == null || line.isBlank()) return;
        NodeState s = parse(line);
        if (s == null) return;
        update(s);
    }

    private void update(NodeState s) {
        synchronized (this) {
            states.put(s.nodeId(), s);
        }
        if (onUpdate != null) {
            onUpdate.accept(snapshot());
        }
    }

    static NodeState parse(String line) {
        String id = findStr(line, NODE_ID);
        if (id == null) return null;
        int rssi = (int) findLong(line, RSSI, -100);
        double loss = findDouble(line, LOSS, 100.0);
        long last = findLong(line, LAST_SEEN, OFFLINE_THRESH_MS + 1);
        boolean tsf = findBool(line, TSF, false);
        boolean interp = findBool(line, INTERP, false);

        NodeStatus status;
        if (last > OFFLINE_THRESH_MS) {
            status = NodeStatus.OFFLINE;
        } else if (rssi < -80 || loss >= 10.0) {
            status = NodeStatus.DEGRADED;
        } else {
            status = NodeStatus.ONLINE;
        }
        return new NodeState(id, status, rssi, loss, last, tsf, interp);
    }

    static String findStr(String line, Pattern p) {
        Matcher m = p.matcher(line);
        return m.find() ? m.group(1) : null;
    }

    static long findLong(String line, Pattern p, long def) {
        String s = findStr(line, p);
        if (s == null) return def;
        try { return Long.parseLong(s); } catch (Exception e) { return def; }
    }

    static double findDouble(String line, Pattern p, double def) {
        String s = findStr(line, p);
        if (s == null) return def;
        try { return Double.parseDouble(s); } catch (Exception e) { return def; }
    }

    static boolean findBool(String line, Pattern p, boolean def) {
        String s = findStr(line, p);
        if (s == null) return def;
        return Objects.equals(s, "1");
    }

    static Object findPort(String name) throws Exception {
        Class<?> cls = Class.forName("com.fazecast.jSerialComm.SerialPort");
        return cls.getMethod("getCommPort", String.class).invoke(null, name);
    }

    static void invokeVoid(Object obj, String method) throws Exception {
        obj.getClass().getMethod(method).invoke(obj);
    }

    static void invokeVoid(Object obj, String method,
                           Class<?>[] types, Object[] args) {
        try {
            obj.getClass().getMethod(method, types).invoke(obj, args);
        } catch (Exception ignored) {}
    }

    static boolean invokeBoolean(Object obj, String method) {
        try {
            return (boolean) obj.getClass().getMethod(method).invoke(obj);
        } catch (Exception e) { return false; }
    }

    static String safeInvokeString(Object obj, String method) {
        try {
            return (String) obj.getClass().getMethod(method).invoke(obj);
        } catch (Exception e) { return null; }
    }

    @Override
    public void close() {
        stop();
    }
}
