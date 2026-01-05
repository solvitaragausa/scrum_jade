package lv.rtu.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldCreateTask() {
        Task task = new Task("Test uzdevums", Task.Type.FEATURE, 5,
            Task.Priority.HIGH, LocalDate.of(2026, 12, 31));

        assertNotNull(task);
        assertEquals("Test uzdevums", task.description);
        assertEquals(Task.Type.FEATURE, task.type);
        assertEquals(5, task.complexity);
        assertEquals(Task.Priority.HIGH, task.priority);
    }

    @Test
    void shouldValidateComplexityBounds() {
        assertThrows(IllegalArgumentException.class,
            () -> new Task("Test", Task.Type.FEATURE, 0, Task.Priority.LOW, LocalDate.now()));

        assertThrows(IllegalArgumentException.class,
            () -> new Task("Test", Task.Type.FEATURE, 11, Task.Priority.LOW, LocalDate.now()));

        assertDoesNotThrow(() -> new Task("Test", Task.Type.FEATURE, 1, Task.Priority.LOW, LocalDate.now()));
        assertDoesNotThrow(() -> new Task("Test", Task.Type.FEATURE, 10, Task.Priority.LOW, LocalDate.now()));
    }

    @Test
    void shouldSerializeAndDeserialize() {
        Task original = new Task("Serializācijas tests", Task.Type.BUGFIX, 7,
            Task.Priority.MEDIUM, LocalDate.of(2026, 6, 15));

        String payload = original.serializeToPayload();
        Task deserialized = Task.parseFromPayload(payload);

        assertEquals(original.description, deserialized.description);
        assertEquals(original.type, deserialized.type);
        assertEquals(original.complexity, deserialized.complexity);
        assertEquals(original.priority, deserialized.priority);
        assertEquals(original.deadline, deserialized.deadline);
    }

    @Test
    void shouldRejectInvalidPayload() {
        assertThrows(IllegalArgumentException.class,
            () -> Task.parseFromPayload(null));

        assertThrows(IllegalArgumentException.class,
            () -> Task.parseFromPayload(""));

        assertThrows(IllegalArgumentException.class,
            () -> Task.parseFromPayload("invalid|format"));
    }

    @Test
    void shouldSetDefaultStatus() {
        Task task = new Task("Test", Task.Type.FEATURE, 5, Task.Priority.HIGH, LocalDate.now());
        assertEquals(Task.Status.ASSIGNED, task.status);
    }
}

