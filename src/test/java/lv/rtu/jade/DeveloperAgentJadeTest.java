package lv.rtu.jade;

import jade.wrapper.AgentController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeveloperAgentJadeTest extends JadeTestBase {

    @Test
    void shouldStartDeveloperAgent() throws Exception {
        AgentController dev = startAgent("Dev1", "lv.rtu.agents.DeveloperAgent",
            new Object[]{"speed=2.0", "knowledge=backend"});

        assertNotNull(dev);
        Thread.sleep(300);
        assertTrue(dev.getName().contains("Dev1"));
    }

    @Test
    void shouldStartMultipleDevelopers() throws Exception {
        startAgent("DevA", "lv.rtu.agents.DeveloperAgent",
            new Object[]{"speed=2.0", "knowledge=backend"});
        startAgent("DevB", "lv.rtu.agents.DeveloperAgent",
            new Object[]{"speed=1.5", "knowledge=frontend"});

        Thread.sleep(300);
    }

    @Test
    void shouldStartTesterAgent() throws Exception {
        AgentController tester = startAgent("Tester1", "lv.rtu.agents.TesterAgent",
            new Object[]{});

        assertNotNull(tester);
        Thread.sleep(300);
    }
}

