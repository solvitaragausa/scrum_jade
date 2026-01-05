package lv.rtu.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.proto.ContractNetInitiator;
import lv.rtu.domain.Task;
import lv.rtu.util.SnifferReadySignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Product Owner agent.
 * Uses FIPA Contract Net protocol for task allocation
 * and FIPA Request protocol for testing coordination.
 */
public class ProductOwnerAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(ProductOwnerAgent.class);
    private static final String CONVERSATION_ID_PREFIX = "cnp-task-";

    private List<Task> backlog;
    private int currentTaskIndex = 0;

    @Override
    protected void setup() {
        logger.info("Product Owner started");

        SnifferReadySignal.waitForSnifferReady();

        initializeBacklog();
        logger.info("Backlog initialized: {} tasks", backlog.size());

        processNextTask();
    }

    private void initializeBacklog() {
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

    private void processNextTask() {
        if (currentTaskIndex >= backlog.size()) {
            logger.info("All tasks processed");
            return;
        }

        Task task = backlog.get(currentTaskIndex);
        String conversationId = CONVERSATION_ID_PREFIX + (currentTaskIndex + 1);

        logger.info("Processing task {}/{}", currentTaskIndex + 1, backlog.size());

        initiateContractNet(task, conversationId);
    }

    private void initiateContractNet(Task task, String conversationId) {
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        cfp.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        cfp.setConversationId(conversationId);
        cfp.setContent(task.serializeToPayload());

        cfp.addReceiver(new AID("ProgA", AID.ISLOCALNAME));
        cfp.addReceiver(new AID("ProgB", AID.ISLOCALNAME));
        cfp.addReceiver(new AID("ProgC", AID.ISLOCALNAME));
        cfp.addReceiver(new AID("ProgD", AID.ISLOCALNAME));
        cfp.addReceiver(new AID("ProgE", AID.ISLOCALNAME));

        logger.info("CFP sent to 5 developers");

        addBehaviour(new ContractNetTaskInitiator(this, cfp));
    }

    @Override
    protected void takeDown() {
        logger.info("Product Owner terminating");
    }

    @SuppressWarnings("squid:S110")
    private class ContractNetTaskInitiator extends ContractNetInitiator {

        public ContractNetTaskInitiator(Agent a, ACLMessage cfp) {
            super(a, cfp);
        }

        @Override
        protected void handlePropose(ACLMessage propose, Vector acceptances) {
            logger.info("PROPOSE from {}: {}", propose.getSender().getLocalName(), propose.getContent());
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            logger.info("REFUSE from {}", refuse.getSender().getLocalName());
        }

        @Override
        protected void handleFailure(ACLMessage failure) {
            logger.warn("FAILURE from {}", failure.getSender().getLocalName());
        }

        @Override
        protected void handleAllResponses(Vector responses, Vector acceptances) {
            ACLMessage bestProposal = selectBestProposal(responses);

            if (bestProposal != null) {
                String cost = bestProposal.getContent();
                logger.info("Best proposal: {} (cost: {})", bestProposal.getSender().getLocalName(), cost);
                sendAcceptRejectReplies(responses, acceptances, bestProposal);
            } else {
                logger.warn("No valid proposals received");
            }
        }

        private ACLMessage selectBestProposal(Vector responses) {
            double bestCost = Double.MAX_VALUE;
            ACLMessage bestProposal = null;

            for (Object obj : responses) {
                ACLMessage msg = (ACLMessage) obj;
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    try {
                        double cost = Double.parseDouble(msg.getContent());
                        if (cost < bestCost) {
                            bestCost = cost;
                            bestProposal = msg;
                        }
                    } catch (NumberFormatException e) {
                        logger.error("Invalid cost from {}", msg.getSender().getLocalName());
                    }
                }
            }
            return bestProposal;
        }

        @SuppressWarnings("unchecked")
        private void sendAcceptRejectReplies(Vector responses, Vector acceptances, ACLMessage bestProposal) {
            for (Object obj : responses) {
                ACLMessage msg = (ACLMessage) obj;
                if (msg.getPerformative() == ACLMessage.PROPOSE) {
                    ACLMessage reply = msg.createReply();

                    if (msg.equals(bestProposal)) {
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        logger.info("ACCEPT_PROPOSAL to {}", msg.getSender().getLocalName());
                    } else {
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                        logger.info("REJECT_PROPOSAL to {}", msg.getSender().getLocalName());
                    }
                    acceptances.add(reply);
                }
            }
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            logger.info("INFORM from {}: {}", inform.getSender().getLocalName(), inform.getContent());
            initiateTestingRequest(inform.getConversationId());
        }

        private void initiateTestingRequest(String conversationId) {
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            request.setConversationId(conversationId);
            request.setContent("test-completed-task");
            request.addReceiver(new AID("Testetajs", AID.ISLOCALNAME));

            logger.info("REQUEST sent to tester");

            addBehaviour(new TestingRequestInitiator(myAgent, request));
        }
    }

    @SuppressWarnings("squid:S110")
    private class TestingRequestInitiator extends AchieveREInitiator {

        public TestingRequestInitiator(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        @Override
        protected void handleAgree(ACLMessage agree) {
            logger.info("AGREE from {}", agree.getSender().getLocalName());
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            logger.warn("REFUSE from {}", refuse.getSender().getLocalName());
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            logger.info("INFORM from {}: {}", inform.getSender().getLocalName(), inform.getContent());

            currentTaskIndex++;

            if (currentTaskIndex < backlog.size()) {
                logger.info("Moving to next task");
                doWait(500);
                processNextTask();
            } else {
                logger.info("All tasks completed");
            }
        }

        @Override
        protected void handleFailure(ACLMessage failure) {
            logger.error("FAILURE from {}", failure.getSender().getLocalName());
        }
    }
}

