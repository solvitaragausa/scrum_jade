package lv.rtu.agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tester agent.
 * Responds to FIPA Request protocol for testing coordination.
 */
public class TesterAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(TesterAgent.class);

    @Override
    protected void setup() {
        logger.info("Tester started");

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(jade.domain.FIPANames.InteractionProtocol.FIPA_REQUEST),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
        );

        addBehaviour(new TestRequestResponder(this, template));
    }

    @Override
    protected void takeDown() {
        logger.info("Tester terminating");
    }

    @SuppressWarnings("squid:S110")
    private class TestRequestResponder extends AchieveREResponder {

        public TestRequestResponder(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage handleRequest(ACLMessage request) {
            logger.info("REQUEST received from {}", request.getSender().getLocalName());

            ACLMessage agree = request.createReply();
            agree.setPerformative(ACLMessage.AGREE);
            logger.info("AGREE sent to {}", request.getSender().getLocalName());
            return agree;
        }

        @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) {
            try {
                logger.info("Running tests");
                Thread.sleep(800);
                logger.info("Tests completed");

                ACLMessage inform = request.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                inform.setContent("tests-passed");
                return inform;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ACLMessage failure = request.createReply();
                failure.setPerformative(ACLMessage.FAILURE);
                failure.setContent("Testing interrupted");
                return failure;
            }
        }
    }
}

