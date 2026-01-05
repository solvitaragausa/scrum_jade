package lv.rtu;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import lv.rtu.util.SnifferReadySignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainBoot {

    private static final Logger logger = LoggerFactory.getLogger(MainBoot.class);

    public static void main(String[] args) {
        try {
            logger.info("Starting JADE SCRUM system");

            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.GUI, "true");

            AgentContainer container = rt.createMainContainer(profile);
            createAgents(container);

            SnifferReadySignal.promptAndWaitForUser();

        } catch (Exception e) {
            logger.error("Failed to start: {}", e.getMessage(), e);
        }
    }

    private static void createAgents(AgentContainer c) throws StaleProxyException {
        c.createNewAgent("ProduktuIpasnieks", "lv.rtu.agents.ProductOwnerAgent", new Object[]{}).start();
        c.createNewAgent("ProgA", "lv.rtu.agents.DeveloperAgent", new Object[]{"speed=2.0", "knowledge=backend"}).start();
        c.createNewAgent("ProgB", "lv.rtu.agents.DeveloperAgent", new Object[]{"speed=1.8", "knowledge=backend"}).start();
        c.createNewAgent("ProgC", "lv.rtu.agents.DeveloperAgent", new Object[]{"speed=1.6", "knowledge=frontend"}).start();
        c.createNewAgent("ProgD", "lv.rtu.agents.DeveloperAgent", new Object[]{"speed=2.2", "knowledge=frontend"}).start();
        c.createNewAgent("ProgE", "lv.rtu.agents.DeveloperAgent", new Object[]{"speed=1.5", "knowledge=fullstack"}).start();
        c.createNewAgent("Testetajs", "lv.rtu.agents.TesterAgent", new Object[]{}).start();

        logger.info("7 agents started");
    }
}

