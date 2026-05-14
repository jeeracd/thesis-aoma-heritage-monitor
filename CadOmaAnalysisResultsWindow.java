import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
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
    private final DefaultTableModel macTableModel = new DefaultTableModel();
    private final JTable macTable = new JTable(macTableModel);
    private final JLabel macInfo = new JLabel("");

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

    private JCheckBox showDockToggle;
    private JCheckBox dataDockToggle;
    private JCheckBox imagesDockToggle;

    private JSplitPane leftCenterRightSplit;
    private JSplitPane centerRightSplit;

    private boolean layerPoints = true;
    private boolean layerLabels = true;
    private boolean layerValidation = true;
    private boolean layerAnnotations = true;
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

        showDock = new DockablePanel("show", "Show", buildShowPanel(), this::applyDockLayout, this::onDockVisibilityChanged);
        dataDock = new DockablePanel("data", "Data", buildDataPanel(), this::applyDockLayout, this::onDockVisibilityChanged);
        imagesDock = new DockablePanel("images", "PyOMA2 Images", buildImagesPanel(), this::applyDockLayout, this::onDockVisibilityChanged);

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
        leftCenterRightSplit = leftCenterRight;
        centerRightSplit = centerRight;

        JPanel root = new JPanel(new BorderLayout());
        root.add(ribbon, BorderLayout.NORTH);
        root.add(leftCenterRight, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);

        restoreLayout(leftCenterRight, centerRight);
        applyDockLayout(leftCenterRight, centerRight);
        syncWorkspaceToggles();
        syncShowTreeFromState();

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

        showDockToggle = new JCheckBox("Show Panel");
        showDockToggle.setSelected(true);
        showDockToggle.setMnemonic('S');
        showDockToggle.addActionListener(e -> {
            if (showDock != null) {
                showDock.setVisibleInWorkspace(showDockToggle.isSelected());
                applyDockLayout();
            }
        });

        dataDockToggle = new JCheckBox("Data Panel");
        dataDockToggle.setSelected(true);
        dataDockToggle.setMnemonic('D');
        dataDockToggle.addActionListener(e -> {
            if (dataDock != null) {
                dataDock.setVisibleInWorkspace(dataDockToggle.isSelected());
                applyDockLayout();
            }
        });

        imagesDockToggle = new JCheckBox("Images Panel");
        imagesDockToggle.setSelected(true);
        imagesDockToggle.setMnemonic('I');
        imagesDockToggle.addActionListener(e -> {
            if (imagesDock != null) {
                imagesDock.setVisibleInWorkspace(imagesDockToggle.isSelected());
                applyDockLayout();
            }
        });

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

        JButton hiddenPanels = new JButton("Hidden...");
        hiddenPanels.setMnemonic('H');
        hiddenPanels.addActionListener(e -> showHiddenPanelsDialog());

        JButton resetLayout = new JButton("Reset Layout");
        resetLayout.setMnemonic('R');
        resetLayout.addActionListener(e -> resetWorkspaceLayout());

        tb.add(showDockToggle);
        tb.add(dataDockToggle);
        tb.add(imagesDockToggle);
        tb.addSeparator();
        tb.add(hiddenPanels);
        tb.add(resetLayout);
        tb.addSeparator();
        tb.add(showLeft);
        tb.add(showRight);
        tb.addSeparator();
        tb.add(dataLeft);
        tb.add(dataRight);
        tb.addSeparator();
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
        overlays.add(new DefaultMutableTreeNode(new LayerItem("layer_annotations", "Annotations", false, layerAnnotations)));

        root.add(natural);
        root.add(oma);
        root.add(overlays);

        JTree tree = new JTree(root);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new CheckBoxTreeRenderer());
        tree.setCellEditor(new CheckBoxTreeEditor(tree, (DefaultTreeCellRenderer) tree.getCellRenderer()));
        tree.setEditable(false);

        expandAll(tree);

        DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
        MouseAdapter toggleMouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if (path == null) {
                    return;
                }
                Object n = path.getLastPathComponent();
                if (!(n instanceof DefaultMutableTreeNode node)) {
                    return;
                }
                if (!(node.getUserObject() instanceof LayerItem li)) {
                    return;
                }
                boolean newVal = !li.isSelected();
                li.setSelected(newVal);
                if (li.isCategory()) {
                    setChildrenSelected(node, newVal);
                }
                updateParentsFromChildren(node);
                applyLayerFromTree();
                treeModel.reload();
                expandAll(tree);
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

            private void updateParentsFromChildren(DefaultMutableTreeNode node) {
                TreeNode parent = node.getParent();
                if (!(parent instanceof DefaultMutableTreeNode p)) {
                    return;
                }
                Object uo = p.getUserObject();
                if (uo instanceof LayerItem li && li.isCategory()) {
                    boolean allSelected = true;
                    boolean hasChild = false;
                    for (int i = 0; i < p.getChildCount(); i++) {
                        DefaultMutableTreeNode c = (DefaultMutableTreeNode) p.getChildAt(i);
                        Object cuo = c.getUserObject();
                        if (cuo instanceof LayerItem cli) {
                            hasChild = true;
                            if (!cli.isSelected()) {
                                allSelected = false;
                            }
                        }
                    }
                    if (hasChild) {
                        li.setSelected(allSelected);
                    }
                }
                updateParentsFromChildren(p);
            }
        };
        tree.addMouseListener(toggleMouse);

        tree.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "toggle_check");
        tree.getActionMap().put("toggle_check", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = tree.getSelectionPath();
                if (path == null) {
                    return;
                }
                Object n = path.getLastPathComponent();
                if (!(n instanceof DefaultMutableTreeNode node)) {
                    return;
                }
                if (!(node.getUserObject() instanceof LayerItem li)) {
                    return;
                }
                boolean newVal = !li.isSelected();
                li.setSelected(newVal);
                if (li.isCategory()) {
                    setChildrenSelected(node, newVal);
                }
                updateParentsFromChildren(node);
                treeModel.reload();
                expandAll(tree);
                applyLayerFromTree();
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

            private void updateParentsFromChildren(DefaultMutableTreeNode node) {
                TreeNode parent = node.getParent();
                if (!(parent instanceof DefaultMutableTreeNode p)) {
                    return;
                }
                Object uo = p.getUserObject();
                if (uo instanceof LayerItem li && li.isCategory()) {
                    boolean allSelected = true;
                    boolean hasChild = false;
                    for (int i = 0; i < p.getChildCount(); i++) {
                        DefaultMutableTreeNode c = (DefaultMutableTreeNode) p.getChildAt(i);
                        Object cuo = c.getUserObject();
                        if (cuo instanceof LayerItem cli) {
                            hasChild = true;
                            if (!cli.isSelected()) {
                                allSelected = false;
                            }
                        }
                    }
                    if (hasChild) {
                        li.setSelected(allSelected);
                    }
                }
                updateParentsFromChildren(p);
            }
        });

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

        JPanel modes = new JPanel(new BorderLayout());
        modes.add(top, BorderLayout.NORTH);
        modes.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel mac = buildMacPanel();
        updateMacPanel();

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Arial", Font.BOLD, 12));
        tabs.addTab("Modes", modes);
        tabs.addTab("MAC", mac);

        JPanel body = new JPanel(new BorderLayout());
        body.add(tabs, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildMacPanel() {
        macTable.setFillsViewportHeight(true);
        macTable.setRowSelectionAllowed(true);
        macTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        macTable.setFont(new Font("Consolas", Font.PLAIN, 12));
        macTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        macTable.setDefaultRenderer(Object.class, center);

        macInfo.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(new EmptyBorder(8, 8, 8, 8));
        top.add(macInfo, BorderLayout.CENTER);

        JScrollPane sp = new JScrollPane(macTable);
        sp.setBorder(new EmptyBorder(0, 8, 8, 8));

        JPanel p = new JPanel(new BorderLayout());
        p.add(top, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void updateMacPanel() {
        double[][] mac = model == null ? new double[0][0] : model.macMatrix();
        if (mac.length == 0) {
            macInfo.setText("MAC not available (need at least 2 modes).");
            macTableModel.setRowCount(0);
            macTableModel.setColumnCount(0);
            return;
        }

        int n = mac.length;
        String[] cols = new String[n + 1];
        cols[0] = "Mode";
        for (int i = 0; i < n; i++) {
            cols[i + 1] = String.valueOf(i + 1);
        }
        macTableModel.setDataVector(new Object[0][0], cols);

        for (int i = 0; i < n; i++) {
            Object[] row = new Object[n + 1];
            row[0] = String.valueOf(i + 1);
            for (int j = 0; j < n; j++) {
                double v = mac[i][j];
                row[j + 1] = Double.isFinite(v) ? String.format("%.3f", v) : "N/A";
            }
            macTableModel.addRow(row);
        }

        for (int i = 0; i < n + 1; i++) {
            macTable.getColumnModel().getColumn(i).setPreferredWidth(i == 0 ? 60 : 70);
        }
        macInfo.setText("MAC matrix (0–1). Diagonal should be ~1.00; off-diagonals near 0.00 indicate distinct modes.");
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

        java.util.List<JComponent> leftPanels = new java.util.ArrayList<>();
        java.util.List<JComponent> rightPanels = new java.util.ArrayList<>();

        if (showDock.isVisibleInWorkspace() && !showDock.isFloating()) {
            if (showDockPos == DockPosition.LEFT) {
                leftPanels.add(showDock.getComponent());
            } else {
                rightPanels.add(showDock.getComponent());
            }
        }

        if (dataDock.isVisibleInWorkspace() && !dataDock.isFloating()) {
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

    private void onDockVisibilityChanged(String id, Boolean visible) {
        if (id == null || visible == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            if ("show".equals(id) && showDockToggle != null) {
                showDockToggle.setSelected(visible);
            } else if ("data".equals(id) && dataDockToggle != null) {
                dataDockToggle.setSelected(visible);
            } else if ("images".equals(id) && imagesDockToggle != null) {
                imagesDockToggle.setSelected(visible);
            }
        });
    }

    private void syncWorkspaceToggles() {
        if (showDockToggle != null && showDock != null) {
            showDockToggle.setSelected(showDock.isVisibleInWorkspace());
        }
        if (dataDockToggle != null && dataDock != null) {
            dataDockToggle.setSelected(dataDock.isVisibleInWorkspace());
        }
        if (imagesDockToggle != null && imagesDock != null) {
            imagesDockToggle.setSelected(imagesDock.isVisibleInWorkspace());
        }
    }

    private void showHiddenPanelsDialog() {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        if (showDock != null && !showDock.isVisibleInWorkspace()) {
            listModel.addElement("Show");
        }
        if (dataDock != null && !dataDock.isVisibleInWorkspace()) {
            listModel.addElement("Data");
        }
        if (imagesDock != null && !imagesDock.isVisibleInWorkspace()) {
            listModel.addElement("PyOMA2 Images");
        }

        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JButton restore = new JButton("Restore");
        restore.setMnemonic('R');
        restore.addActionListener(e -> {
            String sel = list.getSelectedValue();
            if (sel == null) {
                return;
            }
            if ("Show".equals(sel) && showDock != null) {
                showDock.setVisibleInWorkspace(true);
            } else if ("Data".equals(sel) && dataDock != null) {
                dataDock.setVisibleInWorkspace(true);
            } else if ("PyOMA2 Images".equals(sel) && imagesDock != null) {
                imagesDock.setVisibleInWorkspace(true);
            }
            applyDockLayout();
            syncWorkspaceToggles();
            listModel.removeElement(sel);
        });

        JButton restoreAll = new JButton("Restore All");
        restoreAll.setMnemonic('A');
        restoreAll.addActionListener(e -> {
            if (showDock != null) showDock.setVisibleInWorkspace(true);
            if (dataDock != null) dataDock.setVisibleInWorkspace(true);
            if (imagesDock != null) imagesDock.setVisibleInWorkspace(true);
            applyDockLayout();
            syncWorkspaceToggles();
            listModel.clear();
        });

        JButton close = new JButton("Close");
        close.setMnemonic('C');

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.add(restore);
        buttons.add(restoreAll);
        buttons.add(close);

        JPanel body = new JPanel(new BorderLayout());
        body.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        body.add(new JScrollPane(list), BorderLayout.CENTER);
        body.add(buttons, BorderLayout.SOUTH);

        JDialog dialog = new JDialog(this, "Hidden Panels", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout());
        dialog.add(body, BorderLayout.CENTER);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        close.addActionListener(e -> dialog.dispose());
        dialog.getRootPane().setDefaultButton(restore);
        dialog.setVisible(true);
    }

    private void resetWorkspaceLayout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Reset workspace layout to default?", "Reset Layout", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            splitEnabled = true;
            showDockPos = DockPosition.LEFT;
            dataDockPos = DockPosition.RIGHT;
            if (showDock != null) showDock.setVisibleInWorkspace(true);
            if (dataDock != null) dataDock.setVisibleInWorkspace(true);
            if (imagesDock != null) imagesDock.setVisibleInWorkspace(true);
            syncWorkspaceToggles();
            applyDockLayout();
            if (leftCenterRightSplit != null) {
                leftCenterRightSplit.setDividerLocation(320);
            }
            if (centerRightSplit != null) {
                centerRightSplit.setDividerLocation(1020);
            }
            splitViewports.setDividerLocation(650);
            updateSplitEnabled();
            statusLeft.setText("Workspace layout reset");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage() == null ? "Failed to reset layout." : ex.getMessage(), "Reset Error", JOptionPane.ERROR_MESSAGE);
        }
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
        updateMacPanel();
    }

    private void applyPreset(String preset) {
        if ("Basic Analysis".equalsIgnoreCase(preset)) {
            layerPoints = true;
            layerLabels = false;
            layerValidation = true;
            layerAnnotations = true;
        } else if ("Advanced Diagnostics".equalsIgnoreCase(preset)) {
            layerPoints = true;
            layerLabels = true;
            layerValidation = true;
            layerAnnotations = true;
        } else {
            layerPoints = true;
            layerLabels = true;
            layerValidation = true;
            layerAnnotations = true;
        }
        applyLayerSettings();
        syncShowTreeFromState();
        statusLeft.setText("Preset: " + preset);
    }

    private void applyLayerSettings() {
        viewportA.setLayerVisibility(layerPoints, layerLabels, layerValidation);
        viewportB.setLayerVisibility(layerPoints, layerLabels, layerValidation);
        viewportA.setShowAnnotations(layerAnnotations);
        viewportB.setShowAnnotations(layerAnnotations);
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

        if (showDock != null) {
            showDock.setVisibleInWorkspace(prefs.getBoolean("dock_show_visible", true));
        }
        if (dataDock != null) {
            dataDock.setVisibleInWorkspace(prefs.getBoolean("dock_data_visible", true));
        }
        if (imagesDock != null) {
            imagesDock.setVisibleInWorkspace(prefs.getBoolean("dock_images_visible", true));
        }

        layerPoints = prefs.getBoolean("layer_points", true);
        layerLabels = prefs.getBoolean("layer_labels", true);
        layerValidation = prefs.getBoolean("layer_validation", true);
        layerAnnotations = prefs.getBoolean("layer_annotations", true);
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

        prefs.putBoolean("dock_show_visible", showDock != null && showDock.isVisibleInWorkspace());
        prefs.putBoolean("dock_data_visible", dataDock != null && dataDock.isVisibleInWorkspace());
        prefs.putBoolean("dock_images_visible", imagesDock != null && imagesDock.isVisibleInWorkspace());

        prefs.putBoolean("layer_points", layerPoints);
        prefs.putBoolean("layer_labels", layerLabels);
        prefs.putBoolean("layer_validation", layerValidation);
        prefs.putBoolean("layer_annotations", layerAnnotations);
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
        private DefaultMutableTreeNode editingNode;

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
            editingNode = null;
            if (value instanceof DefaultMutableTreeNode node && node.getUserObject() instanceof LayerItem li) {
                editingNode = node;
                checkBox.setText(li.name());
                checkBox.setSelected(li.isSelected());
                checkBox.setFont(new Font("Arial", li.isCategory() ? Font.BOLD : Font.PLAIN, 12));
            }
            return checkBox;
        }

        @Override
        public boolean stopCellEditing() {
            DefaultMutableTreeNode node = editingNode;
            if (node == null || !(node.getUserObject() instanceof LayerItem li)) {
                return super.stopCellEditing();
            }
            boolean newVal = checkBox.isSelected();
            li.setSelected(newVal);
            if (li.isCategory()) {
                setChildrenSelected(node, newVal);
            }
            updateParentsFromChildren(node);
            applyLayerFromTree();
            DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
            tm.reload();
            expandAll(tree);
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

        private void updateParentsFromChildren(DefaultMutableTreeNode node) {
            TreeNode parent = node.getParent();
            if (!(parent instanceof DefaultMutableTreeNode p)) {
                return;
            }
            Object uo = p.getUserObject();
            if (uo instanceof LayerItem li && li.isCategory()) {
                boolean allSelected = true;
                boolean hasChild = false;
                for (int i = 0; i < p.getChildCount(); i++) {
                    DefaultMutableTreeNode c = (DefaultMutableTreeNode) p.getChildAt(i);
                    Object cuo = c.getUserObject();
                    if (cuo instanceof LayerItem cli) {
                        hasChild = true;
                        if (!cli.isSelected()) {
                            allSelected = false;
                        }
                    }
                }
                if (hasChild) {
                    li.setSelected(allSelected);
                }
            }
            updateParentsFromChildren(p);
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
        Boolean a = findSelectedById(root, "layer_annotations");
        if (p != null) layerPoints = p;
        if (l != null) layerLabels = l;
        if (v != null) layerValidation = v;
        if (a != null) layerAnnotations = a;
        applyLayerSettings();
        validateShowTreeState();
    }

    private void validateShowTreeState() {
        Container c = showDock == null ? null : showDock.getComponent();
        if (c == null) {
            return;
        }
        JTree tree = findFirstTree(c);
        if (tree == null) {
            return;
        }
        DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tm.getRoot();

        boolean changed = false;
        changed |= setSelectedById(root, "layer_points", layerPoints);
        changed |= setSelectedById(root, "layer_labels", layerLabels);
        changed |= setSelectedById(root, "layer_validation", layerValidation);
        changed |= setSelectedById(root, "layer_annotations", layerAnnotations);
        changed |= updateCategoriesFromChildren(root);
        if (changed) {
            tm.reload();
            expandAll(tree);
        }
    }

    private void syncShowTreeFromState() {
        validateShowTreeState();
    }

    private static boolean setSelectedById(DefaultMutableTreeNode node, String id, boolean selected) {
        Object uo = node.getUserObject();
        boolean changed = false;
        if (uo instanceof LayerItem li && id.equals(li.id())) {
            if (li.isSelected() != selected) {
                li.setSelected(selected);
                changed = true;
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            changed |= setSelectedById((DefaultMutableTreeNode) node.getChildAt(i), id, selected);
        }
        return changed;
    }

    private static boolean updateCategoriesFromChildren(DefaultMutableTreeNode node) {
        boolean changed = false;
        for (int i = 0; i < node.getChildCount(); i++) {
            changed |= updateCategoriesFromChildren((DefaultMutableTreeNode) node.getChildAt(i));
        }
        Object uo = node.getUserObject();
        if (!(uo instanceof LayerItem li) || !li.isCategory()) {
            return changed;
        }
        boolean allSelected = true;
        boolean hasChild = false;
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode c = (DefaultMutableTreeNode) node.getChildAt(i);
            Object cuo = c.getUserObject();
            if (cuo instanceof LayerItem cli) {
                hasChild = true;
                if (!cli.isSelected()) {
                    allSelected = false;
                }
            }
        }
        if (hasChild && li.isSelected() != allSelected) {
            li.setSelected(allSelected);
            changed = true;
        }
        return changed;
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

    private final class DockablePanel {
        private final String id;
        private final String title;
        private JComponent content;
        private final Runnable dockChanged;
        private final java.util.function.BiConsumer<String, Boolean> visibilityListener;
        private final JPanel wrapper = new JPanel(new BorderLayout());
        private boolean visibleInWorkspace = true;
        private JDialog floatDialog;
        private Point dragStart;
        private boolean dockInProgress;
        private long lastSnapMillis;
        private boolean suppressCloseHandler;

        private DockablePanel(String id, String title, JComponent content, Runnable dockChanged, java.util.function.BiConsumer<String, Boolean> visibilityListener) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.dockChanged = dockChanged;
            this.visibilityListener = visibilityListener;
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
            if (visibilityListener != null) {
                visibilityListener.accept(id, visibleInWorkspace);
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
                setVisibleInWorkspace(false);
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

            MouseAdapter drag = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    dragStart = e.getPoint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (isFloating()) {
                        return;
                    }
                    if (dragStart == null) {
                        dragStart = e.getPoint();
                        return;
                    }
                    int dx = Math.abs(e.getX() - dragStart.x);
                    int dy = Math.abs(e.getY() - dragStart.y);
                    if (dx + dy > 8) {
                        floatPanel();
                        dragStart = null;
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    dragStart = null;
                }
            };
            header.addMouseListener(drag);
            header.addMouseMotionListener(drag);
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
            floatDialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentMoved(ComponentEvent e) {
                    maybeSnapDock(owner);
                }
            });
            floatDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    if (suppressCloseHandler) {
                        suppressCloseHandler = false;
                        return;
                    }
                    floatDialog = null;
                    dockPanel();
                    dockChanged.run();
                }
            });
            floatDialog.setVisible(true);
            dockChanged.run();
        }


        private void maybeSnapDock(Window owner) {
            if (dockInProgress) {
                return;
            }
            if (floatDialog == null || !floatDialog.isVisible()) {
                return;
            }
            if (owner == null || !owner.isVisible()) {
                return;
            }
            long now = System.currentTimeMillis();
            if (now - lastSnapMillis < 350) {
                return;
            }

            Rectangle o = owner.getBounds();
            Rectangle d = floatDialog.getBounds();

            DockPosition dockSide = desiredDockSide();
            int snapPx = 24;
            boolean near = false;
            Point target = null;
            if (dockSide == DockPosition.LEFT) {
                int dist = Math.abs(d.x - o.x);
                near = dist <= snapPx;
                target = new Point(o.x + 12, o.y + 110);
            } else if (dockSide == DockPosition.RIGHT) {
                int dist = Math.abs((d.x + d.width) - (o.x + o.width));
                near = dist <= snapPx;
                target = new Point(o.x + o.width - d.width - 12, o.y + 110);
            }

            if (!near || target == null) {
                return;
            }
            lastSnapMillis = now;
            animateDockTo(target);
        }

        private DockPosition desiredDockSide() {
            if ("show".equals(id)) {
                return showDockPos;
            }
            if ("data".equals(id)) {
                return dataDockPos;
            }
            return DockPosition.RIGHT;
        }

        private void animateDockTo(Point target) {
            if (floatDialog == null) {
                return;
            }
            dockInProgress = true;
            Point start = floatDialog.getLocationOnScreen();
            int steps = 10;
            int ms = 150;
            Timer timer = new Timer(ms / steps, null);
            final int[] k = new int[] {0};
            timer.addActionListener(ev -> {
                k[0]++;
                double t = k[0] / (double) steps;
                int x = (int) Math.round(start.x + (target.x - start.x) * t);
                int y = (int) Math.round(start.y + (target.y - start.y) * t);
                try {
                    if (floatDialog != null) {
                        floatDialog.setLocation(x, y);
                    }
                } catch (Exception ex) {
                }
                if (k[0] >= steps) {
                    ((Timer) ev.getSource()).stop();
                    try {
                        dockPanel();
                        dockChanged.run();
                    } finally {
                        dockInProgress = false;
                    }
                }
            });
            timer.setRepeats(true);
            timer.start();
        }
        private void dockPanel() {
            if (floatDialog != null) {
                suppressCloseHandler = true;
                floatDialog.dispose();
                floatDialog = null;
            }
            rebuild();
        }
    }
}
