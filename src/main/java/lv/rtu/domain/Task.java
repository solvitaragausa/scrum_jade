package lv.rtu.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class Task {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String DELIMITER = "|";

    public String description;
    public Type type;
    public int complexity;
    public Priority priority;
    public LocalDate deadline;
    public Status status;

    public enum Type {
        FEATURE, BUGFIX, TEST, DOC
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Status {
        ASSIGNED, IN_PROGRESS, COMPLETED, TESTING, DOCUMENTED
    }

    public Task() {
    }

    public Task(String description, Type type, int complexity, Priority priority, LocalDate deadline) {
        if (complexity < 1 || complexity > 10) {
            throw new IllegalArgumentException("Complexity must be between 1 and 10");
        }
        this.description = description;
        this.type = type;
        this.complexity = complexity;
        this.priority = priority;
        this.deadline = deadline;
        this.status = Status.ASSIGNED;
    }

    public void setComplexity(int complexity) {
        if (complexity < 1 || complexity > 10) {
            throw new IllegalArgumentException("Complexity must be between 1 and 10");
        }
        this.complexity = complexity;
    }

    public String serializeToPayload() {
        return String.join(DELIMITER,
                description != null ? description : "",
                type != null ? type.name() : "",
                String.valueOf(complexity),
                priority != null ? priority.name() : "",
                deadline != null ? deadline.format(DATE_FORMATTER) : "",
                status != null ? status.name() : ""
        );
    }

    public static Task parseFromPayload(String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            throw new IllegalArgumentException("Payload cannot be empty");
        }

        String[] parts = payload.split("\\" + DELIMITER, -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid payload format");
        }

        Task task = new Task();
        task.description = parts[0].isEmpty() ? null : parts[0];
        task.type = parts[1].isEmpty() ? null : Type.valueOf(parts[1]);

        int parsedComplexity = Integer.parseInt(parts[2]);
        if (parsedComplexity < 1 || parsedComplexity > 10) {
            throw new IllegalArgumentException("Complexity must be between 1 and 10");
        }
        task.complexity = parsedComplexity;

        task.priority = parts[3].isEmpty() ? null : Priority.valueOf(parts[3]);
        task.deadline = parts[4].isEmpty() ? null : LocalDate.parse(parts[4], DATE_FORMATTER);
        task.status = parts[5].isEmpty() ? null : Status.valueOf(parts[5]);

        return task;
    }
}

