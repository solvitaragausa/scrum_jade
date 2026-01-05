package lv.rtu.util;

import lv.rtu.domain.Task;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CostModelTest {

    @Test
    void shouldCalculateCostCorrectly() {
        Task task = new Task("Test", Task.Type.FEATURE, 5, Task.Priority.HIGH, LocalDate.now());

        double cost = CostModel.compute(task, 2.0, "backend");

        // (5/2.0) + 2.0 - 0.8 = 3.7
        assertEquals(3.7, cost, 0.001);
    }

    @Test
    void shouldApplyPriorityPenalties() {
        Task highTask = createTask(Task.Priority.HIGH);
        Task mediumTask = createTask(Task.Priority.MEDIUM);
        Task lowTask = createTask(Task.Priority.LOW);

        double highCost = CostModel.compute(highTask, 2.0, "unknown");
        double mediumCost = CostModel.compute(mediumTask, 2.0, "unknown");
        double lowCost = CostModel.compute(lowTask, 2.0, "unknown");

        assertTrue(highCost > mediumCost);
        assertTrue(mediumCost > lowCost);
    }

    @Test
    void shouldApplyKnowledgeBonus() {
        Task task = createTask(Task.Priority.HIGH);

        double withBonus = CostModel.compute(task, 2.0, "backend");
        double withoutBonus = CostModel.compute(task, 2.0, "qa");

        assertEquals(0.8, withoutBonus - withBonus, 0.001);
    }

    @Test
    void shouldMatchKnowledgeToTaskType() {
        assertTrue(CostModel.knowledgeFits(Task.Type.FEATURE, "backend"));
        assertTrue(CostModel.knowledgeFits(Task.Type.FEATURE, "frontend"));
        assertFalse(CostModel.knowledgeFits(Task.Type.FEATURE, "qa"));

        assertTrue(CostModel.knowledgeFits(Task.Type.BUGFIX, "backend"));
        assertTrue(CostModel.knowledgeFits(Task.Type.BUGFIX, "qa"));

        assertTrue(CostModel.knowledgeFits(Task.Type.TEST, "qa"));
        assertFalse(CostModel.knowledgeFits(Task.Type.TEST, "backend"));
    }

    @Test
    void shouldValidateInputs() {
        Task task = createTask(Task.Priority.HIGH);

        assertThrows(IllegalArgumentException.class,
            () -> CostModel.compute(null, 2.0, "backend"));

        assertThrows(IllegalArgumentException.class,
            () -> CostModel.compute(task, 0, "backend"));

        assertThrows(IllegalArgumentException.class,
            () -> CostModel.compute(task, -1, "backend"));
    }

    private Task createTask(Task.Priority priority) {
        return new Task("Test", Task.Type.FEATURE, 5, priority, LocalDate.now());
    }
}

