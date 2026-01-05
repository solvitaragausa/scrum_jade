package lv.rtu.integration;

import lv.rtu.domain.Task;
import lv.rtu.util.CostModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContractNetWorkflowTest {

    private Task task;
    private List<Developer> developers;

    @BeforeEach
    void setUp() {
        task = new Task("Ieviest autentifikācijas moduli", Task.Type.FEATURE, 5,
            Task.Priority.HIGH, LocalDate.now().plusDays(7));

        developers = List.of(
                new Developer("Programmētājs-A", 2.0, "backend"),
                new Developer("Programmētājs-B", 1.6, "frontend"),
                new Developer("Programmētājs-C", 2.2, "qa")
        );
    }

    @Test
    void shouldSelectLowestCostDeveloper() {
        List<Proposal> proposals = new ArrayList<>();

        for (Developer dev : developers) {
            double cost = CostModel.compute(task, dev.speed, dev.knowledge);
            proposals.add(new Proposal(dev.name, cost));
        }

        Proposal winner = proposals.stream()
                .min(Comparator.comparingDouble(p -> p.cost))
                .orElseThrow();

        assertEquals("Programmētājs-A", winner.developerName);
        assertEquals(3.7, winner.cost, 0.001);
    }

    @Test
    void shouldFavorSpecialistForFeatureTask() {
        double backendCost = CostModel.compute(task, 2.0, "backend");
        double qaCost = CostModel.compute(task, 2.0, "qa");

        assertTrue(backendCost < qaCost,
            "Backend specialists should have lower cost for FEATURE tasks");
    }

    @Test
    void shouldHandleBugfixTaskEqually() {
        Task bugfix = new Task("Bugfix", Task.Type.BUGFIX, 5,
            Task.Priority.HIGH, LocalDate.now());

        double backendCost = CostModel.compute(bugfix, 2.0, "backend");
        double qaCost = CostModel.compute(bugfix, 2.0, "qa");

        assertEquals(backendCost, qaCost, 0.001,
            "All developers should have equal bonus for BUGFIX");
    }

    @Test
    void shouldFavorQAForTestTask() {
        Task testTask = new Task("Test task", Task.Type.TEST, 4,
            Task.Priority.MEDIUM, LocalDate.now());

        double qaCost = CostModel.compute(testTask, 2.0, "qa");
        double backendCost = CostModel.compute(testTask, 2.0, "backend");

        assertTrue(qaCost < backendCost,
            "QA specialists should have lower cost for TEST tasks");
    }

    @Test
    void shouldHandleTaskSerializationInWorkflow() {
        String serialized = task.serializeToPayload();

        Task deserialized = Task.parseFromPayload(serialized);

        double originalCost = CostModel.compute(task, 2.0, "backend");
        double deserializedCost = CostModel.compute(deserialized, 2.0, "backend");

        assertEquals(originalCost, deserializedCost, 0.001);
    }

    @Test
    void shouldRejectAllProposalsIfInvalidSpeed() {
        for (Developer dev : developers) {
            assertThrows(IllegalArgumentException.class,
                    () -> CostModel.compute(task, -1, dev.knowledge));
        }
    }

    private static class Developer {
        final String name;
        final double speed;
        final String knowledge;

        Developer(String name, double speed, String knowledge) {
            this.name = name;
            this.speed = speed;
            this.knowledge = knowledge;
        }
    }

    private static class Proposal {
        final String developerName;
        final double cost;

        Proposal(String developerName, double cost) {
            this.developerName = developerName;
            this.cost = cost;
        }
    }
}

