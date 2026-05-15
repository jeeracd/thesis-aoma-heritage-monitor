import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.swing.SwingUtilities;

public class SensorDataManager {
    private static SensorDataManager instance;
    
    private List<Sensor> sensors;
    private List<ChangeListener> listeners;
    private Map<UUID, String> buildingConnectionStatus;
    private Map<UUID, String> buildingOperationalStatus;

    private static final DateTimeFormatter DEVICE_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static class Sensor {
        private String sensorId;
        private String location;
        private String status;
        private String deviceType;
        private String timestamp;
        private String timeSync;
        
        public Sensor(String sensorId, String location, String status, String deviceType) {
            this.sensorId = sensorId;
            this.location = location;
            this.status = status;
            this.deviceType = deviceType;
            this.timestamp = "00:01:00";
            this.timeSync = "Synced (<1ms)";
        }
        
        public String getSensorId() { return sensorId; }
        public void setSensorId(String sensorId) { this.sensorId = sensorId; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public String getTimeSync() { return timeSync; }
        public void setTimeSync(String timeSync) { this.timeSync = timeSync; }
        
        public boolean isConnected() {
            return "Connected".equalsIgnoreCase(status);
        }
    }
    
    public interface ChangeListener {
        void onSensorDataChanged();
    }
    
    private SensorDataManager() {
        sensors = new ArrayList<>();
        listeners = new ArrayList<>();
        buildingConnectionStatus = new HashMap<>();
        buildingOperationalStatus = new HashMap<>();
    }
    
    public static synchronized SensorDataManager getInstance() {
        if (instance == null) {
            instance = new SensorDataManager();
        }
        return instance;
    }
    
    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
        notifyListeners();
    }
    
    public void removeSensor(String sensorId) {
        sensors.removeIf(s -> s.getSensorId().equals(sensorId));
        notifyListeners();
    }
    
    public void updateSensor(String sensorId, Sensor updatedSensor) {
        for (int i = 0; i < sensors.size(); i++) {
            if (sensors.get(i).getSensorId().equals(sensorId)) {
                sensors.set(i, updatedSensor);
                notifyListeners();
                return;
            }
        }
    }
    
    public void updateSensorStatus(String sensorId, String newStatus) {
        for (Sensor sensor : sensors) {
            if (sensor.getSensorId().equals(sensorId)) {
                sensor.setStatus(newStatus);
                if ("Disconnected".equalsIgnoreCase(newStatus)) {
                    sensor.setTimeSync("Error (>5ms)");
                } else {
                    sensor.setTimeSync("Synced (<1ms)");
                }
                notifyListeners();
                return;
            }
        }
    }
    
    public void updateSensorDeviceType(String sensorId, String newDeviceType) {
        for (Sensor sensor : sensors) {
            if (sensor.getSensorId().equals(sensorId)) {
                sensor.setDeviceType(newDeviceType);
                notifyListeners();
                return;
            }
        }
    }
    
    public Sensor getSensor(String sensorId) {
        for (Sensor sensor : sensors) {
            if (sensor.getSensorId().equals(sensorId)) {
                return sensor;
            }
        }
        return null;
    }
    
    public List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors);
    }

    public void setSensors(List<Sensor> nextSensors) {
        List<Sensor> safeNext = nextSensors == null ? List.of() : nextSensors;
        if (Objects.equals(signatureForSensors(sensors), signatureForSensors(safeNext))) {
            return;
        }
        sensors = new ArrayList<>(safeNext);
        notifyListeners();
    }

    public List<Sensor> scanActiveEsp32Devices() {
        List<Sensor> out = new ArrayList<>();
        try {
            Class<?> serialPortClass = Class.forName("com.fazecast.jSerialComm.SerialPort");
            Object ports = serialPortClass.getMethod("getCommPorts").invoke(null);
            if (!(ports instanceof Object[])) {
                return List.of();
            }
            for (Object port : (Object[]) ports) {
                String sysName = safeInvokeString(port, "getSystemPortName");
                String desc = safeInvokeString(port, "getDescriptivePortName");
                String descLower = desc.toLowerCase();
                if (descLower.contains("bluetooth") || descLower.contains("dial") || descLower.contains("debug")) {
                    continue;
                }
                if (!looksLikeEsp32(sysName.toLowerCase(), descLower)) {
                    continue;
                }

                boolean verified = safeInvokeBoolean(port, "openPort");
                if (!verified) {
                    continue;
                }
                safeInvokeVoid(port, "closePort");

                String id = sysName.isBlank() ? desc : sysName;
                Sensor sensor = new Sensor(id, "N/A", "Connected", "ESP32");
                sensor.setTimestamp(LocalDateTime.now().format(DEVICE_TIMESTAMP_FORMAT));
                sensor.setTimeSync("N/A");
                out.add(sensor);
            }
        } catch (Throwable ignored) {
            return List.of();
        }
        return out;
    }
    
    public int getTotalSensorCount() {
        return sensors.size();
    }
    
    public int getConnectedSensorCount() {
        int count = 0;
        for (Sensor sensor : sensors) {
            if (sensor.isConnected()) {
                count++;
            }
        }
        return count;
    }
    
    public int getDisconnectedSensorCount() {
        return getTotalSensorCount() - getConnectedSensorCount();
    }
    
    public void addChangeListener(ChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners() {
        SwingUtilities.invokeLater(() -> {
            for (ChangeListener listener : listeners) {
                listener.onSensorDataChanged();
            }
        });
    }
    
    public Object[][] getSensorDataArray() {
        Object[][] data = new Object[sensors.size()][6];
        for (int i = 0; i < sensors.size(); i++) {
            Sensor sensor = sensors.get(i);
            data[i][0] = sensor.getSensorId();
            data[i][1] = sensor.getLocation();
            data[i][2] = sensor.getStatus();
            data[i][3] = sensor.getDeviceType();
            data[i][4] = sensor.getTimestamp();
            data[i][5] = sensor.getTimeSync();
        }
        return data;
    }
    
    public String[] getSensorColumns() {
        return new String[]{"Sensor ID", "Location", "Status", "Device Type", "Timestamp", "Time Sync"};
    }
    
    public void validateCounts() {
        int total = getTotalSensorCount();
        int connected = getConnectedSensorCount();
        int disconnected = getDisconnectedSensorCount();
        
        System.out.println("[SensorDataManager] Validation - Total: " + total + 
                         ", Connected: " + connected + 
                         ", Disconnected: " + disconnected);
        
        if (connected + disconnected != total) {
            System.err.println("[SensorDataManager] COUNT MISMATCH DETECTED!");
        }
    }
    
    public void reset() {
        sensors.clear();
        buildingConnectionStatus.clear();
        buildingOperationalStatus.clear();
        notifyListeners();
    }

    public synchronized String getBuildingConnectionStatus(UUID projectId) {
        if (projectId == null) {
            return "Disconnected";
        }
        String existing = buildingConnectionStatus.get(projectId);
        if (existing != null && isValidConnectionStatus(existing)) {
            return normalizeConnectionStatus(existing);
        }
        String derived = scanActiveEsp32Devices().isEmpty() ? "Disconnected" : "Connected";
        buildingConnectionStatus.put(projectId, derived);
        return derived;
    }

    public synchronized void setBuildingConnectionStatus(UUID projectId, String nextStatus) {
        if (projectId == null) {
            return;
        }
        if (!isValidConnectionStatus(nextStatus)) {
            return;
        }
        String normalized = normalizeConnectionStatus(nextStatus);
        String prev = buildingConnectionStatus.get(projectId);
        if (Objects.equals(prev, normalized)) {
            return;
        }
        buildingConnectionStatus.put(projectId, normalized);
        notifyListeners();
    }

    public synchronized String getBuildingOperationalStatus(UUID projectId) {
        if (projectId == null) {
            return "NO DATA";
        }
        String existing = buildingOperationalStatus.get(projectId);
        if (existing == null) {
            buildingOperationalStatus.put(projectId, "NO DATA");
            return "NO DATA";
        }
        String trimmed = existing.trim();
        if (trimmed.isEmpty()) {
            buildingOperationalStatus.put(projectId, "NO DATA");
            return "NO DATA";
        }
        return trimmed;
    }

    public synchronized void setBuildingOperationalStatus(UUID projectId, String nextStatus) {
        if (projectId == null) {
            return;
        }
        if (nextStatus == null) {
            return;
        }
        String trimmed = nextStatus.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        String prev = buildingOperationalStatus.get(projectId);
        if (Objects.equals(prev, trimmed)) {
            return;
        }
        buildingOperationalStatus.put(projectId, trimmed);
        notifyListeners();
    }

    public synchronized int getConnectedBuildingCount(Iterable<UUID> projectIds) {
        int count = 0;
        if (projectIds == null) {
            return 0;
        }
        for (UUID id : projectIds) {
            if ("Connected".equalsIgnoreCase(getBuildingConnectionStatus(id))) {
                count++;
            }
        }
        return count;
    }

    public synchronized int getDisconnectedBuildingCount(Iterable<UUID> projectIds) {
        int total = 0;
        int connected = 0;
        if (projectIds == null) {
            return 0;
        }
        for (UUID id : projectIds) {
            total++;
            if ("Connected".equalsIgnoreCase(getBuildingConnectionStatus(id))) {
                connected++;
            }
        }
        return Math.max(0, total - connected);
    }

    private static boolean isValidConnectionStatus(String v) {
        if (v == null) {
            return false;
        }
        String t = v.trim();
        return "Connected".equalsIgnoreCase(t) || "Disconnected".equalsIgnoreCase(t);
    }

    private static String normalizeConnectionStatus(String v) {
        return "Connected".equalsIgnoreCase(v) ? "Connected" : "Disconnected";
    }

    private static String signatureForSensors(List<Sensor> sensors) {
        if (sensors == null || sensors.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Sensor s : sensors) {
            if (s == null) {
                continue;
            }
            sb.append(s.getSensorId()).append('|')
                    .append(s.getStatus()).append('|')
                    .append(s.getDeviceType()).append('\n');
        }
        return sb.toString();
    }

    private static String safeInvokeString(Object target, String methodName) {
        try {
            Object v = target.getClass().getMethod(methodName).invoke(target);
            return v == null ? "" : v.toString();
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static boolean safeInvokeBoolean(Object target, String methodName) {
        try {
            Object v = target.getClass().getMethod(methodName).invoke(target);
            if (v instanceof Boolean b) {
                return b;
            }
            return false;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static void safeInvokeVoid(Object target, String methodName) {
        try {
            target.getClass().getMethod(methodName).invoke(target);
        } catch (Throwable ignored) {
        }
    }

    private static boolean looksLikeEsp32(String sysLower, String descLower) {
        return sysLower.contains("usb")
                || sysLower.contains("serial")
                || sysLower.contains("wchusbserial")
                || descLower.contains("usb")
                || descLower.contains("uart")
                || descLower.contains("cp210")
                || descLower.contains("silicon labs")
                || descLower.contains("ch340")
                || descLower.contains("wch");
    }
}
