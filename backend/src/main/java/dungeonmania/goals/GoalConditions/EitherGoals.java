package dungeonmania.goals.GoalConditions;

import dungeonmania.Game;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalConditionsInterface;

public class EitherGoals implements GoalConditionsInterface {
    @Override
    public boolean playerHasWon(Game game, Goal goal) {
        return goal.hasFirstGoalAchieved(game) || goal.hasSecondGoalAchieved(game);
    }

    @Override
    public String toString(Game game, Goal goal) {
        return "(" + goal.goalOneToString(game) + " OR " + goal.goalTwoToString(game) + ")";
    }
}
