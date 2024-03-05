package dungeonmania.goals.GoalConditions;

import dungeonmania.Game;
import dungeonmania.entities.Switch;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalConditionsInterface;

public class BouldersGoal implements GoalConditionsInterface {
    @Override
    public boolean playerHasWon(Game game, Goal goal) {
        return game.getMap().getEntities(Switch.class).stream().allMatch(s -> s.isActivated());
    }

    @Override
    public String toString(Game game, Goal goal) {
        return ":boulders";
    }
}
