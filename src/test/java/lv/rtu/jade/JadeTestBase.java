package lv.rtu.jade;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class JadeTestBase {

    protected AgentContainer container;

    @BeforeEach
    void setUpJade() {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "false");
        container = rt.createMainContainer(profile);
    }

    @AfterEach
    void tearDownJade() {
        if (container != null) {
            try {
                container.kill();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    protected AgentController startAgent(String name, String className, Object[] args) throws Exception {
        AgentController agent = container.createNewAgent(name, className, args);
        agent.start();
        return agent;
    }
}

