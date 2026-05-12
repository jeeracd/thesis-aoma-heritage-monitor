import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Project implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    private final String projectName;
    private final String buildingName;
    private final String dateConstructed;
    private final String materialsUsed;
    private final String function;
    private final String conservationStatus;
    private final String address;
    private final String description;

    public Project(
            UUID id,
            Instant createdAt,
            String projectName,
            String buildingName,
            String dateConstructed,
            String materialsUsed,
            String function,
            String conservationStatus,
            String address,
            String description
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.projectName = Objects.requireNonNullElse(projectName, "");
        this.buildingName = Objects.requireNonNullElse(buildingName, "");
        this.dateConstructed = Objects.requireNonNullElse(dateConstructed, "");
        this.materialsUsed = Objects.requireNonNullElse(materialsUsed, "");
        this.function = Objects.requireNonNullElse(function, "");
        this.conservationStatus = Objects.requireNonNullElse(conservationStatus, "");
        this.address = Objects.requireNonNullElse(address, "");
        this.description = Objects.requireNonNullElse(description, "");
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getDateConstructed() {
        return dateConstructed;
    }

    public String getMaterialsUsed() {
        return materialsUsed;
    }

    public String getFunction() {
        return function;
    }

    public String getConservationStatus() {
        return conservationStatus;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Project withUpdatedDetails(
            String projectName,
            String buildingName,
            String dateConstructed,
            String materialsUsed,
            String function,
            String conservationStatus,
            String address,
            String description
    ) {
        return new Project(
                this.id,
                this.createdAt,
                projectName,
                buildingName,
                dateConstructed,
                materialsUsed,
                function,
                conservationStatus,
                address,
                description
        );
    }
}

