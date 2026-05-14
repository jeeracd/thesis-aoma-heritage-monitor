import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
 
public final class BuildingProfileInformationPanel extends JPanel {
    private final Frame owner;
 
    private final JButton editButton;
    private final JButton saveButton;
    private final JLabel statusLabel;
    private final JPanel datePanel = new JPanel(new BorderLayout());
    private final JButton dateCalendarBtn;
 
    private final JTextField projectNameField = new JTextField();
    private final JTextField buildingNameField = new JTextField();
    private final JTextField dateConstructedField = new JTextField();
    private final JTextField materialsUsedField = new JTextField();
    private final JTextField conservationStatusField = new JTextField();
    private final JTextField functionField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea();
    private JScrollPane descriptionScroller;
 
    private boolean editMode = false;
    private boolean dirty = false;
 
    private UUID projectId;
    private Project loadedProject;
 
    private String baselineProjectName = "";
    private String baselineBuildingName = "";
    private String baselineDateConstructed = "";
    private String baselineMaterialsUsed = "";
    private String baselineFunction = "";
    private String baselineConservationStatus = "";
    private String baselineAddress = "";
    private String baselineDescription = "";
 
    private Runnable removeRepoListener = () -> {};
 
    public BuildingProfileInformationPanel(Frame owner) {
        this.owner = owner;
 
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
 
        JLabel title = new JLabel("Building Profile Information");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
 
        editButton = new JButton("Edit");
        editButton.setFocusPainted(true);
        editButton.setMargin(new Insets(4, 10, 4, 10));
        editButton.setFont(new Font("Arial", Font.BOLD, 12));
 
        saveButton = new JButton("Save to Dashboard");
        saveButton.setFocusPainted(true);
        saveButton.setMargin(new Insets(4, 10, 4, 10));
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
 
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        headerButtons.setOpaque(false);
        headerButtons.add(editButton);
        headerButtons.add(saveButton);
 
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
        header.setBackground(new Color(230, 230, 230));
        header.add(title, BorderLayout.WEST);
        header.add(headerButtons, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
 
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 10, 16));
 
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.DARK_GRAY);
 
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setRows(4);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
 
        projectNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        buildingNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        dateConstructedField.setFont(new Font("Arial", Font.PLAIN, 12));
        materialsUsedField.setFont(new Font("Arial", Font.PLAIN, 12));
        conservationStatusField.setFont(new Font("Arial", Font.PLAIN, 12));
        functionField.setFont(new Font("Arial", Font.PLAIN, 12));
        addressField.setFont(new Font("Arial", Font.PLAIN, 12));
        dateConstructedField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        datePanel.setOpaque(true);
        datePanel.setBackground(Color.WHITE);
        datePanel.add(dateConstructedField, BorderLayout.CENTER);
        ImageIcon calendarIcon = new ImageIcon(
                new ImageIcon("calendaricon.png")
                        .getImage()
                        .getScaledInstance(18, 18, Image.SCALE_SMOOTH)
        );
        dateCalendarBtn = new JButton(calendarIcon);
        dateCalendarBtn.setEnabled(false);
        dateCalendarBtn.setFocusPainted(false);
        dateCalendarBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        dateCalendarBtn.setContentAreaFilled(false);
        dateCalendarBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dateCalendarBtn.setPreferredSize(new Dimension(36, 30));
        dateCalendarBtn.setToolTipText("Select date");
        dateCalendarBtn.addActionListener(e -> {
            if (!editMode) {
                return;
            }
            CalendarDatePicker.show(owner == null ? SwingUtilities.getWindowAncestor(this) : owner, dateConstructedField);
        });
        datePanel.add(dateCalendarBtn, BorderLayout.EAST);
 
        int row = 0;
        row = addFieldRow(form, row, "Project Name:", projectNameField);
        row = addFieldRow(form, row, "Building Name:", buildingNameField);
        row = addComponentRow(form, row, "Year Constructed:", datePanel);
        row = addFieldRow(form, row, "Material Used Type:", materialsUsedField);
        row = addFieldRow(form, row, "Conservation Status:", conservationStatusField);
        row = addFieldRow(form, row, "Function:", functionField);
        row = addFieldRow(form, row, "Address:", addressField);
        row = addAreaRow(form, row, "Description:", descriptionArea);
        row = addStatusRow(form, row, statusLabel);
        addBottomSpacer(form, row);
 
        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
 
        setProjectId(AppSession.getActiveProjectId());
        reloadFromRepositoryOrClear();
        setEditMode(false);
 
        DocumentListener dirtyListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onTextChanged();
            }
 
            @Override
            public void removeUpdate(DocumentEvent e) {
                onTextChanged();
            }
 
            @Override
            public void changedUpdate(DocumentEvent e) {
                onTextChanged();
            }
 
            private void onTextChanged() {
                if (!editMode) {
                    return;
                }
                updateDirtyFromFields();
                updateControls();
            }
        };
 
        projectNameField.getDocument().addDocumentListener(dirtyListener);
        buildingNameField.getDocument().addDocumentListener(dirtyListener);
        dateConstructedField.getDocument().addDocumentListener(dirtyListener);
        materialsUsedField.getDocument().addDocumentListener(dirtyListener);
        conservationStatusField.getDocument().addDocumentListener(dirtyListener);
        functionField.getDocument().addDocumentListener(dirtyListener);
        addressField.getDocument().addDocumentListener(dirtyListener);
        descriptionArea.getDocument().addDocumentListener(dirtyListener);
 
        editButton.addActionListener(e -> {
            if (!editMode) {
                setEditMode(true);
                return;
            }
            if (dirty) {
                int choice = JOptionPane.showConfirmDialog(
                        owner == null ? this : owner,
                        "Discard unsaved changes?",
                        "Unsaved Changes",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            reloadFromRepositoryOrClear();
            setEditMode(false);
        });
 
        saveButton.addActionListener(e -> saveToDashboard());
 
        updateControls();
    }
 
    @Override
    public void addNotify() {
        super.addNotify();
        removeRepoListener = ProjectRepository.addChangeListener(() -> SwingUtilities.invokeLater(() -> {
            if (editMode) {
                return;
            }
            UUID id = projectId;
            if (id == null) {
                return;
            }
            Optional<Project> p = ProjectRepository.findById(id);
            p.ifPresent(this::loadFromProject);
        }));
    }
 
    @Override
    public void removeNotify() {
        try {
            removeRepoListener.run();
        } catch (Exception ignored) {
        }
        removeRepoListener = () -> {};
        super.removeNotify();
    }
 
    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
 
    public UUID getProjectId() {
        return projectId;
    }
 
    public boolean isEditMode() {
        return editMode;
    }
 
    public boolean hasUnsavedChanges() {
        return editMode && dirty;
    }
 
    public boolean confirmDiscardIfNeeded() {
        if (!hasUnsavedChanges()) {
            return true;
        }
        int choice = JOptionPane.showConfirmDialog(
                owner == null ? this : owner,
                "You have unsaved changes in Building Profile Information. Discard them?",
                "Unsaved Changes",
                JOptionPane.YES_NO_OPTION
        );
        return choice == JOptionPane.YES_OPTION;
    }
 
    private int addFieldRow(JPanel panel, int row, String label, JTextField field) {
        return addComponentRow(panel, row, label, field);
    }
 
    private int addComponentRow(JPanel panel, int row, String label, Component component) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = row;
 
        gbc.gridx = 0;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lbl, gbc);
 
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(component, gbc);
        return row + 1;
    }
 
    private int addAreaRow(JPanel panel, int row, String label, JTextArea area) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 4, 8, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = row;
 
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lbl, gbc);
 
        gbc.gridx = 1;
        gbc.weightx = 1;
 
        descriptionScroller = new JScrollPane(area);
        descriptionScroller.setPreferredSize(new Dimension(0, 90));
        panel.add(descriptionScroller, gbc);
        return row + 1;
    }
 
    private int addStatusRow(JPanel panel, int row, JLabel statusLabel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 4, 0, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(statusLabel, gbc);
        return row + 1;
    }
 
    private void addBottomSpacer(JPanel panel, int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(Box.createVerticalGlue(), gbc);
    }
 
    private void setEditMode(boolean on) {
        editMode = on;
        if (on) {
            snapshotBaselineFromFields();
            dirty = false;
        } else {
            dirty = false;
        }
        applyFieldMode(projectNameField);
        applyFieldMode(buildingNameField);
        applyFieldMode(dateConstructedField);
        applyFieldMode(materialsUsedField);
        applyFieldMode(conservationStatusField);
        applyFieldMode(functionField);
        applyFieldMode(addressField);
        applyFieldMode(descriptionArea);
        editButton.setText(on ? "Cancel" : "Edit");
        updateControls();
    }
 
    private void applyFieldMode(JTextComponent c) {
        if (c == dateConstructedField) {
            if (editMode) {
                c.setEnabled(true);
                c.setOpaque(true);
                c.setBackground(Color.WHITE);
                c.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                dateCalendarBtn.setEnabled(true);
                datePanel.setOpaque(true);
                datePanel.setBackground(Color.WHITE);
                datePanel.setBorder(UIManager.getBorder("TextField.border"));
            } else {
                c.setEnabled(false);
                c.setDisabledTextColor(Color.BLACK);
                c.setOpaque(false);
                c.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                dateCalendarBtn.setEnabled(false);
                datePanel.setOpaque(false);
                datePanel.setBackground(getBackground());
                datePanel.setBorder(null);
            }
            return;
        }
        if (editMode) {
            c.setEnabled(true);
            if (c instanceof JComponent jc) {
                jc.setOpaque(true);
                jc.setBackground(Color.WHITE);
                jc.setBorder(UIManager.getBorder("TextField.border"));
            }
            if (c == descriptionArea && descriptionScroller != null) {
                descriptionScroller.setBorder(UIManager.getBorder("TextField.border"));
                descriptionScroller.getViewport().setOpaque(true);
                descriptionScroller.getViewport().setBackground(Color.WHITE);
            }
        } else {
            c.setEnabled(false);
            c.setDisabledTextColor(Color.BLACK);
            if (c instanceof JComponent jc) {
                jc.setOpaque(false);
                jc.setBorder(null);
            }
            if (c == descriptionArea && descriptionScroller != null) {
                descriptionScroller.setBorder(null);
                descriptionScroller.getViewport().setOpaque(false);
                descriptionScroller.getViewport().setBackground(getBackground());
            }
        }
    }
 
    private void updateControls() {
        saveButton.setEnabled(editMode && dirty);
        if (editMode) {
            statusLabel.setText(dirty ? "Unsaved changes" : "Editing");
        } else {
            if (projectId == null) {
                statusLabel.setText("Not saved to dashboard");
            } else {
                statusLabel.setText("");
            }
        }
    }
 
    private void reloadFromRepositoryOrClear() {
        UUID id = projectId;
        if (id == null) {
            loadedProject = null;
            loadFromProject(null);
            return;
        }
        Optional<Project> p = ProjectRepository.findById(id);
        loadedProject = p.orElse(null);
        loadFromProject(loadedProject);
    }
 
    private void loadFromProject(Project p) {
        if (p == null) {
            projectNameField.setText("");
            buildingNameField.setText("");
            dateConstructedField.setText("");
            materialsUsedField.setText("");
            functionField.setText("");
            conservationStatusField.setText("");
            addressField.setText("");
            descriptionArea.setText("");
        } else {
            projectNameField.setText(Objects.requireNonNullElse(p.getProjectName(), ""));
            buildingNameField.setText(Objects.requireNonNullElse(p.getBuildingName(), ""));
            dateConstructedField.setText(Objects.requireNonNullElse(p.getDateConstructed(), ""));
            materialsUsedField.setText(Objects.requireNonNullElse(p.getMaterialsUsed(), ""));
            functionField.setText(Objects.requireNonNullElse(p.getFunction(), ""));
            conservationStatusField.setText(Objects.requireNonNullElse(p.getConservationStatus(), ""));
            addressField.setText(Objects.requireNonNullElse(p.getAddress(), ""));
            descriptionArea.setText(Objects.requireNonNullElse(p.getDescription(), ""));
        }
        snapshotBaselineFromFields();
        dirty = false;
        updateControls();
    }
 
    private void snapshotBaselineFromFields() {
        baselineProjectName = projectNameField.getText();
        baselineBuildingName = buildingNameField.getText();
        baselineDateConstructed = dateConstructedField.getText();
        baselineMaterialsUsed = materialsUsedField.getText();
        baselineFunction = functionField.getText();
        baselineConservationStatus = conservationStatusField.getText();
        baselineAddress = addressField.getText();
        baselineDescription = descriptionArea.getText();
    }
 
    private void updateDirtyFromFields() {
        dirty = !Objects.equals(baselineProjectName, projectNameField.getText())
                || !Objects.equals(baselineBuildingName, buildingNameField.getText())
                || !Objects.equals(baselineDateConstructed, dateConstructedField.getText())
                || !Objects.equals(baselineMaterialsUsed, materialsUsedField.getText())
                || !Objects.equals(baselineFunction, functionField.getText())
                || !Objects.equals(baselineConservationStatus, conservationStatusField.getText())
                || !Objects.equals(baselineAddress, addressField.getText())
                || !Objects.equals(baselineDescription, descriptionArea.getText());
    }
 
    private void saveToDashboard() {
        if (!editMode) {
            return;
        }
        try {
            validateInputs();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    owner == null ? this : owner,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            if (owner != null) {
                Toast.show(owner, ex.getMessage(), new Color(160, 40, 40), 2200);
            }
            return;
        }
 
        editButton.setEnabled(false);
        saveButton.setEnabled(false);
        statusLabel.setText("Saving...");
 
        String projectName = projectNameField.getText().trim();
        String buildingName = buildingNameField.getText().trim();
        String dateConstructed = ProjectValidation.normalizeDateOrReturnOriginal(dateConstructedField.getText());
        String materialsUsed = materialsUsedField.getText().trim();
        String function = functionField.getText().trim();
        String conservationStatus = conservationStatusField.getText().trim();
        String address = addressField.getText().trim();
        String description = descriptionArea.getText().trim();
 
        UUID currentId = projectId;
        Project currentProject = loadedProject;
 
        SwingWorker<UUID, Void> worker = new SwingWorker<>() {
            @Override
            protected UUID doInBackground() throws Exception {
                if (currentId == null || currentProject == null) {
                    UUID newId = ProjectRepository.createProject(
                            projectName,
                            buildingName,
                            dateConstructed,
                            materialsUsed,
                            function,
                            conservationStatus,
                            address,
                            description
                    );
                    return newId;
                }
                Project updated = currentProject.withUpdatedDetails(
                        projectName,
                        buildingName,
                        dateConstructed,
                        materialsUsed,
                        function,
                        conservationStatus,
                        address,
                        description
                );
                ProjectRepository.updateProject(updated);
                return updated.getId();
            }
 
            @Override
            protected void done() {
                try {
                    UUID savedId = get();
                    projectId = savedId;
                    AppSession.setActiveProjectId(savedId);
                    loadedProject = ProjectRepository.findById(savedId).orElse(loadedProject);
                    statusLabel.setText("Saved to dashboard");
                    if (owner != null) {
                        Toast.show(owner, "Saved to dashboard", new Color(0, 128, 0), 1600);
                    }
                    reloadFromRepositoryOrClear();
                    setEditMode(false);
                } catch (Exception ex) {
                    String msg;
                    if (ex.getCause() instanceof IOException io) {
                        msg = io.getMessage() == null ? "Failed to save project." : io.getMessage();
                    } else {
                        msg = ex.getMessage() == null ? "Failed to save project." : ex.getMessage();
                    }
                    statusLabel.setText("Save failed");
                    JOptionPane.showMessageDialog(
                            owner == null ? BuildingProfileInformationPanel.this : owner,
                            msg,
                            "Save Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                    if (owner != null) {
                        Toast.show(owner, "Save failed", new Color(160, 40, 40), 2200);
                    }
                } finally {
                    editButton.setEnabled(true);
                    updateControls();
                }
            }
        };
        worker.execute();
    }
 
    private void validateInputs() {
        String projectName = projectNameField.getText();
        String buildingName = buildingNameField.getText();
        String dateConstructed = dateConstructedField.getText();
        ProjectValidation.validateProjectDetails(projectName, buildingName, dateConstructed);
 
        String materialsUsed = materialsUsedField.getText();
        String function = functionField.getText();
        String conservationStatus = conservationStatusField.getText();
        String address = addressField.getText();
        String description = descriptionArea.getText();
 
        enforceMaxLength(materialsUsed, 200, "Material Used Type");
        enforceMaxLength(function, 120, "Function");
        enforceMaxLength(conservationStatus, 120, "Conservation Status");
        enforceMaxLength(address, 200, "Address");
        enforceMaxLength(description, 2000, "Description");
    }
 
    private static void enforceMaxLength(String value, int max, String fieldName) {
        if (value == null) {
            return;
        }
        if (value.trim().length() > max) {
            throw new IllegalArgumentException(fieldName + " must be " + max + " characters or fewer.");
        }
    }
}
