package lv.rtu.integration;

import lv.rtu.domain.Task;
import lv.rtu.util.CostModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultipleTasksWorkflowTest {

    private List<Task> backlog;

    @BeforeEach
    void setUp() {
        backlog = new ArrayList<>();
        backlog.add(new Task("Izveidot autentifikācijas moduli", Task.Type.FEATURE, 5,
            Task.Priority.HIGH, LocalDate.now().plusDays(7)));
        backlog.add(new Task("Labot datubāzes savienojuma kļūdu", Task.Type.BUGFIX, 3,
            Task.Priority.MEDIUM, LocalDate.now().plusDays(3)));
        backlog.add(new Task("Uzrakstīt API dokumentāciju", Task.Type.DOC, 2,
            Task.Priority.LOW, LocalDate.now().plusDays(14)));
        backlog.add(new Task("Integrācijas testu komplekts", Task.Type.TEST, 4,
            Task.Priority.MEDIUM, LocalDate.now().plusDays(5)));
    }

    @Test
    void shouldProcessMultipleTasksInSequence() {
        assertEquals(4, backlog.size());

        for (int i = 0; i < backlog.size(); i++) {
            Task task = backlog.get(i);
            assertNotNull(task);
            assertNotNull(task.description);
            assertTrue(task.complexity >= 1 && task.complexity <= 10);
        }
    }

    @Test
    void shouldHandleDifferentTaskTypes() {
        assertEquals(Task.Type.FEATURE, backlog.get(0).type);
        assertEquals(Task.Type.BUGFIX, backlog.get(1).type);
        assertEquals(Task.Type.DOC, backlog.get(2).type);
        assertEquals(Task.Type.TEST, backlog.get(3).type);
    }

    @Test
    void shouldCalculateCostsForAllTasks() {
        double backendSpeed = 2.0;
        String backendKnowledge = "backend";

        for (Task task : backlog) {
            double cost = CostModel.compute(task, backendSpeed, backendKnowledge);
            assertTrue(cost > 0, "Cost should be positive for task: " + task.description);
        }
    }

    @Test
    void shouldFavorSpecialistsByTaskType() {
        Task featureTask = backlog.get(0);
        Task testTask = backlog.get(3);

        double backendCostForFeature = CostModel.compute(featureTask, 2.0, "backend");
        double qaCostForFeature = CostModel.compute(featureTask, 2.0, "qa");

        double backendCostForTest = CostModel.compute(testTask, 2.0, "backend");
        double qaCostForTest = CostModel.compute(testTask, 2.0, "qa");

        assertTrue(backendCostForFeature < qaCostForFeature,
                "Backend should be cheaper for FEATURE tasks");
        assertTrue(qaCostForTest < backendCostForTest,
                "QA should be cheaper for TEST tasks");
    }

    @Test
    void shouldGenerateUniqueConversationIds() {
        List<String> conversationIds = new ArrayList<>();

        for (int i = 0; i < backlog.size(); i++) {
            String conversationId = "cnp-task-" + (i + 1);
            conversationIds.add(conversationId);
        }

        assertEquals(4, conversationIds.size());
        assertEquals("cnp-task-1", conversationIds.get(0));
        assertEquals("cnp-task-2", conversationIds.get(1));
        assertEquals("cnp-task-3", conversationIds.get(2));
        assertEquals("cnp-task-4", conversationIds.get(3));

        long uniqueCount = conversationIds.stream().distinct().count();
        assertEquals(4, uniqueCount, "All conversation IDs should be unique");
    }

    @Test
    void shouldSerializeAndDeserializeAllTasks() {
        for (Task original : backlog) {
            String payload = original.serializeToPayload();
            Task deserialized = Task.parseFromPayload(payload);

            assertEquals(original.description, deserialized.description);
            assertEquals(original.type, deserialized.type);
            assertEquals(original.complexity, deserialized.complexity);
            assertEquals(original.priority, deserialized.priority);
        }
    }
}

