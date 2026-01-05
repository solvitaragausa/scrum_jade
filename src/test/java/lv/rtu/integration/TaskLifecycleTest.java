package lv.rtu.integration;

import lv.rtu.domain.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskLifecycleTest {

    @Test
    void shouldFollowCompleteLifecycle() {
        Task task = createTask();

        assertEquals(Task.Status.ASSIGNED, task.status);

        task.status = Task.Status.IN_PROGRESS;
        assertEquals(Task.Status.IN_PROGRESS, task.status);

        task.status = Task.Status.COMPLETED;
        assertEquals(Task.Status.COMPLETED, task.status);

        task.status = Task.Status.TESTING;
        assertEquals(Task.Status.TESTING, task.status);

        task.status = Task.Status.DOCUMENTED;
        assertEquals(Task.Status.DOCUMENTED, task.status);
    }

    @Test
    void shouldSerializeWithStatus() {
        Task task = createTask();
        task.status = Task.Status.IN_PROGRESS;

        String payload = task.serializeToPayload();
        Task deserialized = Task.parseFromPayload(payload);

        assertEquals(Task.Status.IN_PROGRESS, deserialized.status);
    }

    @Test
    void shouldMaintainDataIntegrityThroughLifecycle() {
        Task task = createTask();
        String originalDesc = task.description;
        int originalComplexity = task.complexity;

        task.status = Task.Status.IN_PROGRESS;
        task.status = Task.Status.COMPLETED;
        task.status = Task.Status.TESTING;

        assertEquals(originalDesc, task.description);
        assertEquals(originalComplexity, task.complexity);
    }

    private Task createTask() {
        return new Task("Integrācijas tests", Task.Type.FEATURE, 5,
            Task.Priority.HIGH, LocalDate.now().plusDays(7));
    }
}

