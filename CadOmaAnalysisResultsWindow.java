import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.prefs.Preferences;

public class CadOmaAnalysisResultsWindow extends JFrame {
    public enum DockPosition {
        LEFT,
        RIGHT,
        FLOAT
    }

    private final RoleMenuBar.Role role;
    private final Preferences prefs;

    private OmaResultsModel model;

    private final CadViewportPanel viewportA = new CadViewportPanel();
    private final CadViewportPanel viewportB = new CadViewportPanel();
    private final JSplitPane splitViewports = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    private final OmaResultsTableModel tableModel = new OmaResultsTableModel();
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<OmaResultsTableModel> sorter = new TableRowSorter<>(tableModel);

    private final JLabel statusLeft = new JLabel("Ready");
    private final JLabel statusCenter = new JLabel("");
    private final JLabel statusRight = new JLabel("");

    private final JTextField commandField = new JTextField();

    private boolean splitEnabled = true;

    private DockablePanel showDock;
    private DockablePanel dataDock;
    private DockablePanel imagesDock;

    private DockPosition showDockPos = DockPosition.LEFT;
    private DockPosition dataDockPos = DockPosition.RIGHT;

    private boolean layerPoints = true;
    private boolean layerLabels = true;
    private boolean layerValidation = true;
    private float alphaPoints = 1.0f;
    private float alphaLabels = 1.0f;

    private final JSlider alphaSlider = new JSlider(0, 100, 100);
    private String alphaTarget = "points";

    public CadOmaAnalysisResultsWindow(RoleMenuBar.Role role) {
        this.role = role == null ? RoleMenuBar.Role.ENGINEER : role;
        this.prefs = Preferences.userNodeForPackage(CadOmaAnalysisResultsWindow.class).node("cad_oma_" + this.role.name().toLowerCase());

        setTitle("AOMA-Heritage Monitor - OMA Analysis Results (CAD Workspace)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        RoleMenuBar.install(this, this.role);

        model = OmaResultsModel.loadLatestOrEmpty();
        tableModel.setRows(model.modes());

        viewportA.setModel(model);
        viewportB.setModel(model);
        viewportA.setViewType(CadViewportPanel.ViewType.FREQUENCY);
        viewportB.setViewType(CadViewportPanel.ViewType.DAMPING);

        viewportA.setViewportListener(new CadViewportPanel.ViewportListener() {
            @Override
            public void onHover(double x, double y) {
                statusCenter.setText(String.format("X=%.2f  Y=%.6f", x, y));
            }

            @Override
            public void onSelectMode(int modeIndex) {
                selectModeInTable(modeIndex);
            }

            @Override
            public void onValidationCount(int issuesCount) {
                updateStatusRight();
            }
        });
        viewportB.setViewportListener(new CadViewportPanel.ViewportListener() {
            @Override
            public void onHover(double x, double y) {
                statusCenter.setText(String.format("X=%.2f  Y=%.6f", x, y));
            }

            @Override
            public void onSelectMode(int modeIndex) {
                selectModeInTable(modeIndex);
            }

            @Override
            public void onValidationCount(int issuesCount) {
                updateStatusRight();
            }
        });

        applyLayerSettings();

        JPanel ribbon = buildRibbon();
        JPanel statusBar = buildStatusBar();
        JPanel bottom = buildCommandAndStatus(statusBar);

        showDock = new DockablePanel("show", "Show", buildShowPanel(), this::applyDockLayout);
        dataDock = new DockablePanel("data", "Data", buildDataPanel(), this::applyDockLayout);
        imagesDock = new DockablePanel("images", "PyOMA2 Images", buildImagesPanel(), this::applyDockLayout);

        splitViewports.setLeftComponent(wrapViewport(viewportA, "Viewport A"));
        splitViewports.setRightComponent(wrapViewport(viewportB, "Viewport B"));
        splitViewports.setResizeWeight(0.5);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(30, 30, 34));
        center.add(splitViewports, BorderLayout.CENTER);

        JSplitPane centerRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerRight.setLeftComponent(center);
        centerRight.setResizeWeight(0.75);

        JSplitPane leftCenterRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        leftCenterRight.setResizeWeight(0.20);

        JPanel root = new JPanel(new BorderLayout());
        root.add(ribbon, BorderLayout.NORTH);
        root.add(leftCenterRight, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);

        restoreLayout(leftCenterRight, centerRight);
        applyDockLayout(leftCenterRight, centerRight);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveLayout(leftCenterRight, centerRight);
                dispose();
            }
        });

        updateStatusRight();
    }

    private JPanel buildRibbon() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        tabs.addTab("Home", buildHomeRibbon());
        tabs.addTab("View", buildViewRibbon());
        tabs.addTab("Export", buildExportRibbon());
        tabs.addTab("Workspace", buildWorkspaceRibbon());

        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(6, 6, 6, 6));
        container.add(tabs, BorderLayout.CENTER);
        return container;
    }

    private JComponent buildHomeRibbon() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JComboBox<String> preset = new JComboBox<>(new String[] { "Basic Analysis", "Advanced Diagnostics", "Full Report View" });
        preset.addActionListener(e -> applyPreset(Objects.toString(preset.getSelectedItem(), "Basic Analysis")));

        JButton loadLatest = new JButton("Load Latest");
        loadLatest.addActionListener(e -> loadLatest());

        JButton browse = new JButton("Browse...");
        browse.addActionListener(e -> browseAndLoad());

        tb.add(new JLabel("Preset: "));
        tb.add(preset);
        tb.addSeparator();
        tb.add(loadLatest);
        tb.add(browse);
        return wrapRibbon(tb);
    }

    private JComponent buildViewRibbon() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JToggleButton select = new JToggleButton("Select");
        JToggleButton pan = new JToggleButton("Pan");
        JToggleButton annotate = new JToggleButton("Annotate");
        ButtonGroup tools = new ButtonGroup();
        tools.add(select);
        tools.add(pan);
        tools.add(annotate);
        select.setSelected(true);

        select.addActionListener(e -> setTool(CadViewportPanel.Tool.SELECT));
        pan.addActionListener(e -> setTool(CadViewportPanel.Tool.PAN));
        annotate.addActionListener(e -> setTool(CadViewportPanel.Tool.ANNOTATE));

        JButton split = new JButton("Toggle Split");
        split.addActionListener(e -> toggleSplit());

        JButton resetA = new JButton("Reset A");
        resetA.addActionListener(e -> viewportA.resetView());

        JButton resetB = new JButton("Reset B");
        resetB.addActionListener(e -> viewportB.resetView());

        tb.add(select);
        tb.add(pan);
        tb.add(annotate);
        tb.addSeparator();
        tb.add(split);
        tb.addSeparator();
        tb.add(resetA);
        tb.add(resetB);
        return wrapRibbon(tb);
    }

    private JComponent buildExportRibbon() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton exportCsv = new JButton("CSV");
        exportCsv.addActionListener(e -> exportCsv());

        JButton exportExcel = new JButton("Excel");
        exportExcel.addActionListener(e -> exportExcel());

        JButton exportPdf = new JButton("PDF");
        exportPdf.addActionListener(e -> exportPdf());

        JButton exportDxf = new JButton("DXF (2D)");
        exportDxf.addActionListener(e -> exportDxf());

        tb.add(exportCsv);
        tb.add(exportExcel);
        tb.add(exportPdf);
        tb.addSeparator();
        tb.add(exportDxf);
        return wrapRibbon(tb);
    }

    private JComponent buildWorkspaceRibbon() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);

        JButton showLeft = new JButton("Show->Left");
        showLeft.addActionListener(e -> {
            showDockPos = DockPosition.LEFT;
            applyDockLayout();
        });

        JButton showRight = new JButton("Show->Right");
        showRight.addActionListener(e -> {
            showDockPos = DockPosition.RIGHT;
            applyDockLayout();
        });

        JButton dataRight = new JButton("Data->Right");
        dataRight.addActionListener(e -> {
            dataDockPos = DockPosition.RIGHT;
            applyDockLayout();
        });

        JButton dataLeft = new JButton("Data->Left");
        dataLeft.addActionListener(e -> {
            dataDockPos = DockPosition.LEFT;
            applyDockLayout();
        });

        JCheckBox showImages = new JCheckBox("Show Images");
        showImages.setSelected(true);
        showImages.addActionListener(e -> {
            imagesDock.setVisibleInWorkspace(showImages.isSelected());
            applyDockLayout();
        });

        tb.add(showLeft);
        tb.add(showRight);
        tb.addSeparator();
        tb.add(dataLeft);
        tb.add(dataRight);
        tb.addSeparator();
        tb.add(showImages);
        return wrapRibbon(tb);
    }

    private static JComponent wrapRibbon(JToolBar tb) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(tb, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildShowPanel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new LayerItem("root", "Show", true, true));
        DefaultMutableTreeNode natural = new DefaultMutableTreeNode(new LayerItem("cat_natural", "Natural Frequencies", true, true));
        natural.add(new DefaultMutableTreeNode(new LayerItem("layer_points", "Points", false, layerPoints)));
        natural.add(new DefaultMutableTreeNode(new LayerItem("layer_labels", "Labels", false, layerLabels)));
        DefaultMutableTreeNode oma = new DefaultMutableTreeNode(new LayerItem("cat_oma", "OMA Results", true, true));
        oma.add(new DefaultMutableTreeNode(new LayerItem("layer_validation", "Validation Flags", false, layerValidation)));
        DefaultMutableTreeNode overlays = new DefaultMutableTreeNode(new LayerItem("cat_overlays", "Overlays", true, true));
        overlays.add(new DefaultMutableTreeNode(new LayerItem("layer_annotations", "Annotations", false, true)));

        root.add(natural);
        root.add(oma);
        root.add(overlays);

        JTree tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CheckBoxTreeRenderer());
        tree.setCellEditor(new CheckBoxTreeEditor(tree, (DefaultTreeCellRenderer) tree.getCellRenderer()));
        tree.setEditable(true);

        expandAll(tree);

        JTextField search = new JTextField();
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyTreeSearch(tree, search.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyTreeSearch(tree, search.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyTreeSearch(tree, search.getText());
            }
        });

        alphaSlider.setValue(100);
        alphaSlider.addChangeListener(e -> {
            float a = alphaSlider.getValue() / 100f;
            if ("points".equals(alphaTarget)) {
                alphaPoints = a;
            } else if ("labels".equals(alphaTarget)) {
                alphaLabels = a;
            }
            applyLayerSettings();
        });

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath p = e.getPath();
                if (p == null) {
                    return;
                }
                Object last = p.getLastPathComponent();
                if (!(last instanceof DefaultMutableTreeNode node)) {
                    return;
                }
                Object uo = node.getUserObject();
                if (!(uo instanceof LayerItem li)) {
                    return;
                }
                if ("layer_points".equals(li.id())) {
                    alphaTarget = "points";
                    alphaSlider.setValue(Math.round(alphaPoints * 100));
                } else if ("layer_labels".equals(li.id())) {
                    alphaTarget = "labels";
                    alphaSlider.setValue(Math.round(alphaLabels * 100));
                }
            }
        });

        JButton applyBasic = new JButton("Basic");
        JButton applyAdvanced = new JButton("Advanced");
        JButton applyFull = new JButton("Full");
        applyBasic.addActionListener(e -> applyPreset("Basic Analysis"));
        applyAdvanced.addActionListener(e -> applyPreset("Advanced Diagnostics"));
        applyFull.addActionListener(e -> applyPreset("Full Report View"));

        JPanel presets = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        presets.add(applyBasic);
        presets.add(applyAdvanced);
        presets.add(applyFull);

        JPanel alphaPanel = new JPanel(new BorderLayout(6, 0));
        alphaPanel.add(new JLabel("Alpha"), BorderLayout.WEST);
        alphaPanel.add(alphaSlider, BorderLayout.CENTER);

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.add(new JLabel("Search"), BorderLayout.WEST);
        top.add(search, BorderLayout.CENTER);

        JPanel body = new JPanel(new BorderLayout(6, 6));
        body.setBorder(new EmptyBorder(8, 8, 8, 8));
        body.add(top, BorderLayout.NORTH);
        body.add(new JScrollPane(tree), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(6, 6));
        bottom.add(presets, BorderLayout.NORTH);
        bottom.add(alphaPanel, BorderLayout.SOUTH);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(body, BorderLayout.CENTER);
        wrap.add(bottom, BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildDataPanel() {
        table.setRowSorter(sorter);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(false);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        DefaultTableCellRenderer sevRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int modelRow = table.convertRowIndexToModel(row);
                OmaResultsModel.ModeRow r = tableModel.rowAt(modelRow);
                if (r != null && !isSelected) {
                    if (r.severity() == OmaResultsModel.Severity.CRITICAL) {
                        c.setForeground(new Color(200, 40, 40));
                    } else if (r.severity() == OmaResultsModel.Severity.WARNING) {
                        c.setForeground(new Color(180, 120, 10));
                    } else {
                        c.setForeground(new Color(40, 120, 60));
                    }
                }
                return c;
            }
        };
        table.getColumnModel().getColumn(5).setCellRenderer(sevRenderer);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) {
                viewportA.setSelectedModeIndex(-1);
                viewportB.setSelectedModeIndex(-1);
                return;
            }
            int modelRow = table.convertRowIndexToModel(viewRow);
            OmaResultsModel.ModeRow r = tableModel.rowAt(modelRow);
            if (r != null) {
                viewportA.setSelectedModeIndex(r.modeIndex());
                viewportB.setSelectedModeIndex(r.modeIndex());
            }
        });

        JTextField filter = new JTextField();
        filter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyTableFilter(filter.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyTableFilter(filter.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyTableFilter(filter.getText());
            }
        });

        JButton details = new JButton("Details");
        details.addActionListener(e -> showSelectedDetails());

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.setBorder(new EmptyBorder(8, 8, 8, 8));
        top.add(new JLabel("Filter"), BorderLayout.WEST);
        top.add(filter, BorderLayout.CENTER);
        top.add(details, BorderLayout.EAST);

        JPanel body = new JPanel(new BorderLayout());
        body.add(top, BorderLayout.NORTH);
        body.add(new JScrollPane(table), BorderLayout.CENTER);
        return body;
    }

    private JComponent buildImagesPanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.setBorder(new EmptyBorder(8, 8, 8, 8));

        JLabel stab = new JLabel("No image.", SwingConstants.CENTER);
        JLabel cmif = new JLabel("No image.", SwingConstants.CENTER);
        JLabel mode = new JLabel("No image.", SwingConstants.CENTER);
        JLabel mac = new JLabel("No image.", SwingConstants.CENTER);

        setImageFromSummary(stab, "stabilization_png");
        setImageFromSummary(cmif, "cmif_png");
        setImageFromSummary(mode, "mode_shapes_png");
        setImageFromSummary(mac, "mac_png");

        p.add(wrapImage(stab, "Stabilization"));
        p.add(wrapImage(cmif, "CMIF"));
        p.add(wrapImage(mode, "Mode Shapes"));
        p.add(wrapImage(mac, "MAC"));
        return new JScrollPane(p);
    }

    private JComponent wrapImage(JLabel label, String title) {
        label.setVerticalAlignment(SwingConstants.TOP);
        JPanel box = new JPanel(new BorderLayout());
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 12));
        box.add(t, BorderLayout.NORTH);
        box.add(new JScrollPane(label), BorderLayout.CENTER);
        return box;
    }

    private JComponent wrapViewport(CadViewportPanel viewport, String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(4, 8, 4, 8));
        header.setBackground(new Color(45, 45, 52));

        JLabel t = new JLabel(title);
        t.setForeground(new Color(230, 230, 235));
        t.setFont(new Font("Arial", Font.BOLD, 12));

        JComboBox<String> type = new JComboBox<>(new String[] { "Frequency", "Damping" });
        type.setSelectedItem(viewport.getViewType() == CadViewportPanel.ViewType.FREQUENCY ? "Frequency" : "Damping");
        type.addActionListener(e -> {
            String v = Objects.toString(type.getSelectedItem(), "Frequency");
            viewport.setViewType("Damping".equalsIgnoreCase(v) ? CadViewportPanel.ViewType.DAMPING : CadViewportPanel.ViewType.FREQUENCY);
        });

        header.add(t, BorderLayout.WEST);
        header.add(type, BorderLayout.EAST);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(header, BorderLayout.NORTH);
        wrap.add(viewport, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBorder(new EmptyBorder(6, 10, 6, 10));
        bar.setBackground(new Color(245, 245, 246));

        statusLeft.setFont(new Font("Arial", Font.PLAIN, 12));
        statusCenter.setFont(new Font("Arial", Font.PLAIN, 12));
        statusRight.setFont(new Font("Arial", Font.PLAIN, 12));

        bar.add(statusLeft, BorderLayout.WEST);
        bar.add(statusCenter, BorderLayout.CENTER);
        bar.add(statusRight, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildCommandAndStatus(JPanel statusBar) {
        JPanel cmd = new JPanel(new BorderLayout(8, 0));
        cmd.setBorder(new EmptyBorder(6, 10, 6, 10));
        cmd.setBackground(new Color(235, 235, 238));

        JLabel label = new JLabel("Command");
        label.setFont(new Font("Arial", Font.BOLD, 12));

        commandField.setFont(new Font("Consolas", Font.PLAIN, 13));
        commandField.addActionListener(e -> runCommand(commandField.getText()));

        JButton run = new JButton("Run");
        run.addActionListener(e -> runCommand(commandField.getText()));

        cmd.add(label, BorderLayout.WEST);
        cmd.add(commandField, BorderLayout.CENTER);
        cmd.add(run, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(cmd, BorderLayout.NORTH);
        bottom.add(statusBar, BorderLayout.SOUTH);
        return bottom;
    }

    private void applyDockLayout() {
        Container c = getContentPane();
        if (c == null) {
            return;
        }
        Component center = ((BorderLayout) c.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (!(center instanceof JSplitPane leftCenterRight)) {
            return;
        }
        Component cr = leftCenterRight.getRightComponent();
        if (!(cr instanceof JSplitPane centerRight)) {
            return;
        }
        applyDockLayout(leftCenterRight, centerRight);
    }

    private void applyDockLayout(JSplitPane leftCenterRight, JSplitPane centerRight) {
        JPanel centerHost = new JPanel(new BorderLayout());
        centerHost.add(splitViewports, BorderLayout.CENTER);

        if (showDock.isFloating()) {
            showDockPos = DockPosition.FLOAT;
        }
        if (dataDock.isFloating()) {
            dataDockPos = DockPosition.FLOAT;
        }

        java.util.List<JComponent> leftPanels = new java.util.ArrayList<>();
        java.util.List<JComponent> rightPanels = new java.util.ArrayList<>();

        if (showDock.isVisibleInWorkspace() && showDockPos != DockPosition.FLOAT) {
            if (showDockPos == DockPosition.LEFT) {
                leftPanels.add(showDock.getComponent());
            } else {
                rightPanels.add(showDock.getComponent());
            }
        }

        if (dataDock.isVisibleInWorkspace() && dataDockPos != DockPosition.FLOAT) {
            if (dataDockPos == DockPosition.LEFT) {
                leftPanels.add(dataDock.getComponent());
            } else {
                rightPanels.add(dataDock.getComponent());
            }
        }

        if (!imagesDock.isFloating() && imagesDock.isVisibleInWorkspace()) {
            rightPanels.add(imagesDock.getComponent());
        }

        JComponent leftStack = stackVertical(leftPanels);
        JComponent rightStack = stackVertical(rightPanels);

        centerRight.setRightComponent(rightStack == null ? new JPanel() : rightStack);
        centerRight.setLeftComponent(centerHost);

        leftCenterRight.setLeftComponent(leftStack == null ? new JPanel() : leftStack);
        leftCenterRight.setRightComponent(centerRight);

        updateSplitEnabled();
        revalidate();
        repaint();
    }

    private static JComponent stackVertical(java.util.List<JComponent> panels) {
        if (panels == null || panels.isEmpty()) {
            return null;
        }
        if (panels.size() == 1) {
            return panels.get(0);
        }
        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        sp.setTopComponent(panels.get(0));
        sp.setBottomComponent(panels.get(1));
        sp.setResizeWeight(0.5);
        for (int i = 2; i < panels.size(); i++) {
            JSplitPane next = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            next.setTopComponent(sp);
            next.setBottomComponent(panels.get(i));
            next.setResizeWeight(0.66);
            sp = next;
        }
        return sp;
    }

    private void updateSplitEnabled() {
        if (!splitEnabled) {
            splitViewports.setRightComponent(new JPanel());
            splitViewports.setDividerLocation(1.0);
            viewportB.setVisible(false);
        } else {
            viewportB.setVisible(true);
            if (splitViewports.getRightComponent() == null || splitViewports.getRightComponent() instanceof JPanel) {
                splitViewports.setRightComponent(wrapViewport(viewportB, "Viewport B"));
            }
        }
    }

    private void toggleSplit() {
        splitEnabled = !splitEnabled;
        updateSplitEnabled();
        statusLeft.setText(splitEnabled ? "Split view enabled" : "Split view disabled");
    }

    private void setTool(CadViewportPanel.Tool tool) {
        viewportA.setTool(tool);
        viewportB.setTool(tool);
        statusLeft.setText("Tool: " + tool.name());
    }

    private void loadLatest() {
        OmaResultsModel m = OmaResultsModel.loadLatestOrEmpty();
        setModel(m);
        statusLeft.setText(m.outDir() == null ? "No results found" : "Loaded: " + m.outDir().getFileName());
    }

    private void browseAndLoad() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int r = fc.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File dir = fc.getSelectedFile();
        if (dir == null) {
            return;
        }
        setModel(OmaResultsModel.loadFromDirOrEmpty(dir.toPath()));
        statusLeft.setText("Loaded: " + dir.getName());
    }

    private void setModel(OmaResultsModel m) {
        model = m == null ? OmaResultsModel.loadFromDirOrEmpty(null) : m;
        tableModel.setRows(model.modes());
        viewportA.setModel(model);
        viewportB.setModel(model);
        updateStatusRight();
        imagesDock.replaceContent(buildImagesPanel());
    }

    private void applyPreset(String preset) {
        if ("Basic Analysis".equalsIgnoreCase(preset)) {
            layerPoints = true;
            layerLabels = false;
            layerValidation = true;
        } else if ("Advanced Diagnostics".equalsIgnoreCase(preset)) {
            layerPoints = true;
            layerLabels = true;
            layerValidation = true;
        } else {
            layerPoints = true;
            layerLabels = true;
            layerValidation = true;
        }
        applyLayerSettings();
        statusLeft.setText("Preset: " + preset);
    }

    private void applyLayerSettings() {
        viewportA.setLayerVisibility(layerPoints, layerLabels, layerValidation);
        viewportB.setLayerVisibility(layerPoints, layerLabels, layerValidation);
        viewportA.setLayerAlpha(alphaPoints, alphaLabels);
        viewportB.setLayerAlpha(alphaPoints, alphaLabels);
    }

    private void applyTableFilter(String text) {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) {
            sorter.setRowFilter(null);
            return;
        }
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends OmaResultsTableModel, ? extends Integer> entry) {
                int r = entry.getIdentifier();
                OmaResultsModel.ModeRow row = tableModel.rowAt(r);
                if (row == null) {
                    return false;
                }
                String s = (row.modeIndex()
                        + " " + row.frequencyHz()
                        + " " + row.dampingRatio()
                        + " " + row.mpc()
                        + " " + row.mpd()
                        + " " + row.severity().name()).toLowerCase();
                return s.contains(t.toLowerCase());
            }
        });
    }

    private void selectModeInTable(int modeIndex) {
        if (modeIndex <= 0) {
            return;
        }
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            OmaResultsModel.ModeRow r = tableModel.rowAt(i);
            if (r != null && r.modeIndex() == modeIndex) {
                int view = table.convertRowIndexToView(i);
                if (view >= 0) {
                    table.getSelectionModel().setSelectionInterval(view, view);
                    table.scrollRectToVisible(table.getCellRect(view, 0, true));
                }
                return;
            }
        }
    }

    private void showSelectedDetails() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "No row selected.", "Details", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        OmaResultsModel.ModeRow r = tableModel.rowAt(modelRow);
        if (r == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Mode: ").append(r.modeIndex()).append("\n");
        sb.append("Frequency (Hz): ").append(r.frequencyHz()).append("\n");
        sb.append("Damping ratio: ").append(r.dampingRatio()).append("\n");
        sb.append("MPC: ").append(r.mpc()).append("\n");
        sb.append("MPD: ").append(r.mpd()).append("\n");
        sb.append("Phi (AccelX): ").append(r.phiAccelX()).append("\n");
        sb.append("Phi (AccelY): ").append(r.phiAccelY()).append("\n");
        sb.append("Phi (AccelZ): ").append(r.phiAccelZ()).append("\n");
        sb.append("Severity: ").append(r.severity().name()).append("\n\n");
        if (model.outDir() != null) {
            sb.append("Source: ").append(model.outDir()).append("\n");
            sb.append("Calculation history: derived from PyOMA2 exports in modal_properties.csv and summary.properties.\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Mode Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStatusRight() {
        int modes = model.modes().size();
        int issues = model.issuesCount();
        statusRight.setText("Modes: " + modes + " | Issues: " + issues + " | Zoom A: " + String.format("%.2f", viewportA.getZoom()));
    }

    private void runCommand(String cmd) {
        String c = cmd == null ? "" : cmd.trim();
        if (c.isEmpty()) {
            return;
        }
        commandField.setText("");
        String lc = c.toLowerCase();
        try {
            if ("help".equals(lc)) {
                JOptionPane.showMessageDialog(this,
                        "Commands:\n"
                                + "help\n"
                                + "load latest\n"
                                + "split on|off\n"
                                + "preset basic|advanced|full\n"
                                + "tool select|pan|annotate\n"
                                + "export dxf|csv|excel|pdf\n"
                                + "reset view\n",
                        "Command Help",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if ("load latest".equals(lc)) {
                loadLatest();
            } else if (lc.startsWith("preset ")) {
                String p = lc.substring("preset ".length()).trim();
                if ("basic".equals(p)) {
                    applyPreset("Basic Analysis");
                } else if ("advanced".equals(p)) {
                    applyPreset("Advanced Diagnostics");
                } else {
                    applyPreset("Full Report View");
                }
            } else if (lc.startsWith("split ")) {
                String v = lc.substring("split ".length()).trim();
                splitEnabled = "on".equals(v);
                updateSplitEnabled();
            } else if (lc.startsWith("tool ")) {
                String t = lc.substring("tool ".length()).trim();
                if ("pan".equals(t)) {
                    setTool(CadViewportPanel.Tool.PAN);
                } else if ("annotate".equals(t)) {
                    setTool(CadViewportPanel.Tool.ANNOTATE);
                } else {
                    setTool(CadViewportPanel.Tool.SELECT);
                }
            } else if (lc.startsWith("export ")) {
                String t = lc.substring("export ".length()).trim();
                if ("dxf".equals(t)) {
                    exportDxf();
                } else if ("csv".equals(t)) {
                    exportCsv();
                } else if ("excel".equals(t)) {
                    exportExcel();
                } else if ("pdf".equals(t)) {
                    exportPdf();
                }
            } else if ("reset view".equals(lc)) {
                viewportA.resetView();
                viewportB.resetView();
            } else {
                JOptionPane.showMessageDialog(this, "Unknown command: " + c, "Command", JOptionPane.WARNING_MESSAGE);
            }
            statusLeft.setText("Command: " + c);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage() == null ? "Command failed." : ex.getMessage(), "Command Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("oma_results.csv"));
        int r = fc.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            OmaResultsExport.writeCsv(fc.getSelectedFile(), model.modes());
            statusLeft.setText("Exported CSV");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Export CSV", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportExcel() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("oma_results.xml"));
        int r = fc.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            OmaResultsExport.writeExcelXml(fc.getSelectedFile(), model.modes(), "OmaResults");
            statusLeft.setText("Exported Excel XML");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Export Excel", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportPdf() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("oma_results.pdf"));
        int r = fc.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            OmaResultsExport.writePdfSummary(fc.getSelectedFile(), model, model.modes());
            statusLeft.setText("Exported PDF");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Export PDF", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportDxf() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("oma_results.dxf"));
        int r = fc.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            OmaResultsExport.writeDxfFrequencyPlot(fc.getSelectedFile(), viewportA);
            statusLeft.setText("Exported DXF");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Export DXF", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setImageFromSummary(JLabel label, String key) {
        File f = model.resolveFileFromSummary(key);
        if (f == null || !f.exists()) {
            label.setText("No image.");
            label.setIcon(null);
            return;
        }
        ImageIcon icon = new ImageIcon(f.getAbsolutePath());
        int maxW = 560;
        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            int w = Math.min(icon.getIconWidth(), maxW);
            int h = (int) Math.round(icon.getIconHeight() * (w / (double) icon.getIconWidth()));
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaled));
            label.setText("");
        } else {
            label.setIcon(icon);
            label.setText("");
        }
    }

    private static void expandAll(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private static void applyTreeSearch(JTree tree, String q) {
        String query = q == null ? "" : q.trim().toLowerCase();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        markSearch(root, query);
        ((DefaultTreeModel) tree.getModel()).reload();
        expandAll(tree);
    }

    private static boolean markSearch(DefaultMutableTreeNode node, String query) {
        Object uo = node.getUserObject();
        boolean self = true;
        if (uo instanceof LayerItem li) {
            if (query.isEmpty()) {
                li.setVisibleInSearch(true);
                self = true;
            } else {
                boolean m = li.name().toLowerCase().contains(query);
                li.setVisibleInSearch(m);
                self = m;
            }
        }
        boolean childMatch = false;
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode c = (DefaultMutableTreeNode) node.getChildAt(i);
            if (markSearch(c, query)) {
                childMatch = true;
            }
        }
        if (uo instanceof LayerItem li && !query.isEmpty()) {
            li.setVisibleInSearch(self || childMatch);
        }
        return self || childMatch;
    }

    private void restoreLayout(JSplitPane leftCenterRight, JSplitPane centerRight) {
        int w = prefs.getInt("win_w", 1400);
        int h = prefs.getInt("win_h", 850);
        int x = prefs.getInt("win_x", Integer.MIN_VALUE);
        int y = prefs.getInt("win_y", Integer.MIN_VALUE);
        Rectangle bounds = new Rectangle(0, 0, w, h);
        if (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE) {
            bounds.setLocation(x, y);
        } else {
            setSize(w, h);
            setLocationRelativeTo(null);
        }
        if (isOnAnyScreen(bounds)) {
            setBounds(bounds);
        } else {
            setSize(w, h);
            setLocationRelativeTo(null);
        }

        showDockPos = parseDockPos(prefs.get("show_pos", DockPosition.LEFT.name()), DockPosition.LEFT);
        dataDockPos = parseDockPos(prefs.get("data_pos", DockPosition.RIGHT.name()), DockPosition.RIGHT);
        splitEnabled = prefs.getBoolean("split_enabled", true);

        layerPoints = prefs.getBoolean("layer_points", true);
        layerLabels = prefs.getBoolean("layer_labels", true);
        layerValidation = prefs.getBoolean("layer_validation", true);
        alphaPoints = prefs.getInt("alpha_points", 100) / 100f;
        alphaLabels = prefs.getInt("alpha_labels", 100) / 100f;

        applyLayerSettings();

        leftCenterRight.setDividerLocation(prefs.getInt("div_left", 320));
        centerRight.setDividerLocation(prefs.getInt("div_right", 1020));
        splitViewports.setDividerLocation(prefs.getInt("div_viewports", 650));
    }

    private static DockPosition parseDockPos(String s, DockPosition fallback) {
        if (s == null) {
            return fallback;
        }
        try {
            return DockPosition.valueOf(s);
        } catch (Exception ex) {
            return fallback;
        }
    }

    private void saveLayout(JSplitPane leftCenterRight, JSplitPane centerRight) {
        Rectangle b = getBounds();
        prefs.putInt("win_x", b.x);
        prefs.putInt("win_y", b.y);
        prefs.putInt("win_w", b.width);
        prefs.putInt("win_h", b.height);

        prefs.put("show_pos", showDockPos.name());
        prefs.put("data_pos", dataDockPos.name());
        prefs.putBoolean("split_enabled", splitEnabled);

        prefs.putBoolean("layer_points", layerPoints);
        prefs.putBoolean("layer_labels", layerLabels);
        prefs.putBoolean("layer_validation", layerValidation);
        prefs.putInt("alpha_points", Math.round(alphaPoints * 100));
        prefs.putInt("alpha_labels", Math.round(alphaLabels * 100));

        prefs.putInt("div_left", leftCenterRight.getDividerLocation());
        prefs.putInt("div_right", centerRight.getDividerLocation());
        prefs.putInt("div_viewports", splitViewports.getDividerLocation());
    }

    private static boolean isOnAnyScreen(Rectangle r) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice d : ge.getScreenDevices()) {
            Rectangle b = d.getDefaultConfiguration().getBounds();
            if (b.intersects(r)) {
                return true;
            }
        }
        return false;
    }

    private static final class LayerItem {
        private final String id;
        private final String name;
        private final boolean category;
        private boolean selected;
        private boolean visibleInSearch = true;

        private LayerItem(String id, String name, boolean category, boolean selected) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.selected = selected;
        }

        public String id() {
            return id;
        }

        public String name() {
            return name;
        }

        public boolean isCategory() {
            return category;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isVisibleInSearch() {
            return visibleInSearch;
        }

        public void setVisibleInSearch(boolean visibleInSearch) {
            this.visibleInSearch = visibleInSearch;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final class CheckBoxTreeRenderer extends DefaultTreeCellRenderer {
        private final JCheckBox check = new JCheckBox();

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component base = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            if (!(value instanceof DefaultMutableTreeNode node)) {
                return base;
            }
            Object uo = node.getUserObject();
            if (!(uo instanceof LayerItem li)) {
                return base;
            }
            check.setOpaque(false);
            check.setText(li.name());
            check.setSelected(li.isSelected());
            check.setForeground(li.isVisibleInSearch() ? getTextNonSelectionColor() : new Color(150, 150, 150));
            check.setFont(new Font("Arial", li.isCategory() ? Font.BOLD : Font.PLAIN, 12));
            return check;
        }
    }

    private final class CheckBoxTreeEditor extends AbstractCellEditor implements TreeCellEditor {
        private final JTree tree;
        private final JCheckBox checkBox = new JCheckBox();

        private CheckBoxTreeEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            this.tree = tree;
            checkBox.setOpaque(false);
            checkBox.addActionListener(e -> {
                stopCellEditing();
            });
        }

        @Override
        public Object getCellEditorValue() {
            return checkBox.isSelected();
        }

        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
            if (value instanceof DefaultMutableTreeNode node && node.getUserObject() instanceof LayerItem li) {
                checkBox.setText(li.name());
                checkBox.setSelected(li.isSelected());
                checkBox.setFont(new Font("Arial", li.isCategory() ? Font.BOLD : Font.PLAIN, 12));
            }
            return checkBox;
        }

        @Override
        public boolean stopCellEditing() {
            TreePath path = tree.getSelectionPath();
            if (path == null) {
                return super.stopCellEditing();
            }
            Object n = path.getLastPathComponent();
            if (!(n instanceof DefaultMutableTreeNode node) || !(node.getUserObject() instanceof LayerItem li)) {
                return super.stopCellEditing();
            }
            boolean newVal = checkBox.isSelected();
            li.setSelected(newVal);
            if (li.isCategory()) {
                setChildrenSelected(node, newVal);
            }
            applyLayerFromTree();
            ((DefaultTreeModel) tree.getModel()).nodeChanged(node);
            tree.repaint();
            return super.stopCellEditing();
        }

        private void setChildrenSelected(DefaultMutableTreeNode node, boolean selected) {
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode c = (DefaultMutableTreeNode) node.getChildAt(i);
                Object uo = c.getUserObject();
                if (uo instanceof LayerItem li) {
                    li.setSelected(selected);
                }
                if (c.getChildCount() > 0) {
                    setChildrenSelected(c, selected);
                }
            }
        }
    }

    private void applyLayerFromTree() {
        Container c = showDock.getComponent();
        if (c == null) {
            return;
        }
        JTree tree = findFirstTree(c);
        if (tree == null) {
            return;
        }
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        Boolean p = findSelectedById(root, "layer_points");
        Boolean l = findSelectedById(root, "layer_labels");
        Boolean v = findSelectedById(root, "layer_validation");
        if (p != null) layerPoints = p;
        if (l != null) layerLabels = l;
        if (v != null) layerValidation = v;
        applyLayerSettings();
    }

    private static Boolean findSelectedById(DefaultMutableTreeNode node, String id) {
        Object uo = node.getUserObject();
        if (uo instanceof LayerItem li && id.equals(li.id())) {
            return li.isSelected();
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode c = (DefaultMutableTreeNode) node.getChildAt(i);
            Boolean v = findSelectedById(c, id);
            if (v != null) {
                return v;
            }
        }
        return null;
    }

    private static JTree findFirstTree(Container c) {
        for (Component child : c.getComponents()) {
            if (child instanceof JTree t) {
                return t;
            }
            if (child instanceof JScrollPane sp) {
                Component v = sp.getViewport().getView();
                if (v instanceof JTree t) {
                    return t;
                }
            }
            if (child instanceof Container cc) {
                JTree t = findFirstTree(cc);
                if (t != null) {
                    return t;
                }
            }
        }
        return null;
    }

    private static final class DockablePanel {
        private final String id;
        private final String title;
        private JComponent content;
        private final Runnable dockChanged;
        private final JPanel wrapper = new JPanel(new BorderLayout());
        private boolean visibleInWorkspace = true;
        private JDialog floatDialog;

        private DockablePanel(String id, String title, JComponent content, Runnable dockChanged) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.dockChanged = dockChanged;
            rebuild();
        }

        public JComponent getComponent() {
            return wrapper;
        }

        public boolean isVisibleInWorkspace() {
            return visibleInWorkspace;
        }

        public void setVisibleInWorkspace(boolean visibleInWorkspace) {
            this.visibleInWorkspace = visibleInWorkspace;
            if (!visibleInWorkspace) {
                if (floatDialog != null) {
                    floatDialog.dispose();
                    floatDialog = null;
                }
            }
        }

        public boolean isFloating() {
            return floatDialog != null && floatDialog.isVisible();
        }

        public void replaceContent(JComponent newContent) {
            this.content = newContent;
            rebuild();
        }

        private void rebuild() {
            wrapper.removeAll();
            JPanel header = new JPanel(new BorderLayout());
            header.setBorder(new EmptyBorder(6, 8, 6, 8));
            header.setBackground(new Color(250, 250, 252));
            JLabel t = new JLabel(title);
            t.setFont(new Font("Arial", Font.BOLD, 12));

            JButton floatBtn = new JButton("Float");
            JButton dockBtn = new JButton("Dock");
            JButton closeBtn = new JButton("Hide");

            floatBtn.addActionListener(e -> floatPanel());
            dockBtn.addActionListener(e -> dockPanel());
            closeBtn.addActionListener(e -> {
                visibleInWorkspace = false;
                dockPanel();
                dockChanged.run();
            });

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            right.setOpaque(false);
            right.add(floatBtn);
            right.add(dockBtn);
            right.add(closeBtn);

            header.add(t, BorderLayout.WEST);
            header.add(right, BorderLayout.EAST);
            wrapper.add(header, BorderLayout.NORTH);
            wrapper.add(content, BorderLayout.CENTER);
        }

        private void floatPanel() {
            if (floatDialog != null && floatDialog.isVisible()) {
                floatDialog.toFront();
                return;
            }
            Window owner = SwingUtilities.getWindowAncestor(wrapper);
            floatDialog = new JDialog(owner);
            floatDialog.setTitle(title);
            floatDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            floatDialog.setLayout(new BorderLayout());
            floatDialog.add(content, BorderLayout.CENTER);
            floatDialog.setSize(520, 520);
            floatDialog.setLocationRelativeTo(owner);
            floatDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    floatDialog = null;
                    dockChanged.run();
                }
            });
            floatDialog.setVisible(true);
            dockChanged.run();
        }

        private void dockPanel() {
            if (floatDialog != null) {
                floatDialog.dispose();
                floatDialog = null;
            }
            rebuild();
        }
    }
}
