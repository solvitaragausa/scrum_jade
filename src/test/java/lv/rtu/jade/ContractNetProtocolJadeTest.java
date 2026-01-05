package lv.rtu.jade;

import org.junit.jupiter.api.Test;

class ContractNetProtocolJadeTest extends JadeTestBase {

    @Test
    void shouldStartProductOwnerAndDevelopers() throws Exception {
        startAgent("PO", "lv.rtu.agents.ProductOwnerAgent", new Object[]{});
        startAgent("Dev1", "lv.rtu.agents.DeveloperAgent",
            new Object[]{"speed=2.0", "knowledge=backend"});
        startAgent("Dev2", "lv.rtu.agents.DeveloperAgent",
            new Object[]{"speed=1.5", "knowledge=frontend"});

        Thread.sleep(500);
    }

    @Test
    void shouldStartAllSystemAgents() throws Exception {
        startAgent("PO", "lv.rtu.agents.ProductOwnerAgent", new Object[]{});
        startAgent("Dev1", "lv.rtu.agents.DeveloperAgent",
            new Object[]{"speed=2.0", "knowledge=backend"});
        startAgent("Dev2", "lv.rtu.agents.DeveloperAgent",
            new Object[]{"speed=1.8", "knowledge=backend"});
        startAgent("Dev3", "lv.rtu.agents.DeveloperAgent",
            new Object[]{"speed=1.6", "knowledge=frontend"});
        startAgent("Tester", "lv.rtu.agents.TesterAgent", new Object[]{});

        Thread.sleep(1000);
    }
}

