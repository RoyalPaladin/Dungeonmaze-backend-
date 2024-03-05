package dungeonmania.goals.GoalConditions;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.TreasureForGoal;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalConditionsInterface;

public class TreasureGoal implements GoalConditionsInterface {
    @Override
    public boolean playerHasWon(Game game, Goal goal) {
        int numOfTreasures = 0;
        List<Entity> entities = game.getMap().getEntities();
        for (Entity entity : entities) {
            if (entity instanceof TreasureForGoal) numOfTreasures++;
        }
        return game.getInitialTreasureCount() - numOfTreasures >= goal.getTarget();
    }

    @Override
    public String toString(Game game, Goal goal) {
        return ":treasure";
    }
}
