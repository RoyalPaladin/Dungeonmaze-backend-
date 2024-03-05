package dungeonmania.goals.GoalConditions;

import dungeonmania.Game;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalConditionsInterface;

public class EnemiesGoal implements GoalConditionsInterface {

    @Override
    public boolean playerHasWon(Game game, Goal goal) {
        return game.getDestroyedEnemies() >= goal.getTarget()
               && game.getMap().getEntities(ZombieToastSpawner.class).size() == 0;
    }

    @Override
    public String toString(Game game, Goal goal) {
        return ":enemies";
    }
}
