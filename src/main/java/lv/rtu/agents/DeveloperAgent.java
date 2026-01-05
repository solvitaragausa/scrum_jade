package lv.rtu.agents;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import lv.rtu.domain.Task;
import lv.rtu.util.CostModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Developer agent.
 * Responds to Contract Net protocol for task allocation.
 * Arguments: speed=<double>, knowledge=<string>
 */
public class DeveloperAgent extends Agent {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperAgent.class);
    private double speed;
    private String knowledge;

    @Override
    protected void setup() {
        parseArguments();
        logger.info("{} started (speed={}, knowledge={})", getLocalName(), speed, knowledge);

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(jade.domain.FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP)
        );

        addBehaviour(new ContractNetTaskResponder(this, template));
    }

    private void parseArguments() {
        Object[] args = getArguments();
        if (args == null || args.length < 2) {
            logger.warn("{} missing arguments, using defaults", getLocalName());
            speed = 1.0;
            knowledge = "general";
            return;
        }

        try {
            for (Object arg : args) {
                String argStr = arg.toString();
                if (argStr.startsWith("speed=")) {
                    speed = Double.parseDouble(argStr.substring(6));
                } else if (argStr.startsWith("knowledge=")) {
                    knowledge = argStr.substring(10);
                }
            }
        } catch (Exception e) {
            logger.error("{} error parsing arguments: {}", getLocalName(), e.getMessage());
            speed = 1.0;
            knowledge = "general";
        }
    }

    @Override
    protected void takeDown() {
        logger.info("{} terminating", getLocalName());
    }

    @SuppressWarnings("squid:S110")
    private class ContractNetTaskResponder extends ContractNetResponder {

        public ContractNetTaskResponder(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException {
            logger.info("{} received CFP from {}", getLocalName(), cfp.getSender().getLocalName());

            try {
                Task task = Task.parseFromPayload(cfp.getContent());
                double cost = CostModel.compute(task, speed, knowledge);
                logger.info("{} computed cost: {}", getLocalName(), cost);

                ACLMessage propose = cfp.createReply();
                propose.setPerformative(ACLMessage.PROPOSE);
                propose.setContent(String.valueOf(cost));
                return propose;

            } catch (Exception e) {
                logger.error("{} error parsing task: {}", getLocalName(), e.getMessage());
                throw new RefuseException("Failed to parse task");
            }
        }

        @Override
        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept)
                throws FailureException {
            logger.info("{} proposal ACCEPTED", getLocalName());

            try {
                logger.info("{} executing task", getLocalName());
                Thread.sleep(1000);
                logger.info("{} task completed", getLocalName());

                ACLMessage inform = accept.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                inform.setContent("completed");
                return inform;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new FailureException("Task execution interrupted");
            }
        }

        @Override
        protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
            logger.info("{} proposal REJECTED", getLocalName());
        }
    }
}

