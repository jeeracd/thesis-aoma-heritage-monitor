import java.util.Objects;

public final class ProjectDetailsController {
    private Project baseline;
    private Project draft;
    private boolean editing;
    private boolean dirty;

    public ProjectDetailsController(Project baseline) {
        this.baseline = Objects.requireNonNull(baseline, "baseline");
        this.draft = baseline;
        this.editing = false;
        this.dirty = false;
    }

    public boolean isEditing() {
        return editing;
    }

    public boolean isDirty() {
        return dirty;
    }

    public Project getBaseline() {
        return baseline;
    }

    public Project getDraft() {
        return draft;
    }

    public void enterEditMode() {
        this.editing = true;
        this.draft = baseline;
        this.dirty = false;
    }

    public void cancelEdits() {
        this.draft = baseline;
        this.dirty = false;
        this.editing = false;
    }

    public void setDraftDetails(
            String projectName,
            String buildingName,
            String dateConstructed,
            String materialsUsed,
            String function,
            String conservationStatus,
            String address,
            String description
    ) {
        Project next = baseline.withUpdatedDetails(
                projectName,
                buildingName,
                dateConstructed,
                materialsUsed,
                function,
                conservationStatus,
                address,
                description
        );
        this.draft = next;
        this.dirty = !equalsDetails(baseline, next);
    }

    public void validateDraft() {
        ProjectValidation.validateProjectDetails(
                draft.getProjectName(),
                draft.getBuildingName(),
                draft.getDateConstructed()
        );
    }

    public void applySaveSuccess(Project persisted) {
        this.baseline = Objects.requireNonNull(persisted, "persisted");
        this.draft = this.baseline;
        this.dirty = false;
        this.editing = false;
    }

    public void applySaveFailureRevert() {
        this.draft = this.baseline;
        this.dirty = false;
    }

    private static boolean equalsDetails(Project a, Project b) {
        return a.getProjectName().equals(b.getProjectName())
                && a.getBuildingName().equals(b.getBuildingName())
                && a.getDateConstructed().equals(b.getDateConstructed())
                && a.getMaterialsUsed().equals(b.getMaterialsUsed())
                && a.getFunction().equals(b.getFunction())
                && a.getConservationStatus().equals(b.getConservationStatus())
                && a.getAddress().equals(b.getAddress())
                && a.getDescription().equals(b.getDescription());
    }
}

