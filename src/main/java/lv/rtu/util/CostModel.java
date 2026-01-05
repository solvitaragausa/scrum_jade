package lv.rtu.util;

import lv.rtu.domain.Task;

/**
 * Cost calculation utility.
 * Formula: (complexity / speed) + penalty - bonus
 */
public class CostModel {

    private static final double PENALTY_HIGH = 2.0;
    private static final double PENALTY_MEDIUM = 1.0;
    private static final double PENALTY_LOW = 0.3;
    private static final double KNOWLEDGE_BONUS = 0.8;

    private CostModel() {
    }

    public static double compute(Task task, double devSpeed, String knowledge) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (devSpeed <= 0) {
            throw new IllegalArgumentException("Developer speed must be positive");
        }

        double baseCost = task.complexity / devSpeed;
        double penalty = getDeadlinePenalty(task.priority);
        double bonus = knowledgeFits(task.type, knowledge) ? KNOWLEDGE_BONUS : 0.0;

        return baseCost + penalty - bonus;
    }

    private static double getDeadlinePenalty(Task.Priority priority) {
        if (priority == null) {
            return PENALTY_LOW;
        }

        return switch (priority) {
            case HIGH -> PENALTY_HIGH;
            case MEDIUM -> PENALTY_MEDIUM;
            case LOW -> PENALTY_LOW;
        };
    }

    public static boolean knowledgeFits(Task.Type type, String knowledge) {
        if (type == null || knowledge == null) {
            return false;
        }

        String normalizedKnowledge = knowledge.toLowerCase().trim();

        return switch (type) {
            case FEATURE -> normalizedKnowledge.equals("backend")
                         || normalizedKnowledge.equals("frontend")
                         || normalizedKnowledge.equals("fullstack");
            case BUGFIX -> true;
            case TEST -> normalizedKnowledge.equals("qa")
                      || normalizedKnowledge.equals("fullstack");
            case DOC -> true;
        };
    }
}

