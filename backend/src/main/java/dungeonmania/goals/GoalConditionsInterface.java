package dungeonmania.goals;

import dungeonmania.Game;

public interface GoalConditionsInterface {
    public boolean playerHasWon(Game game, Goal goal);
    public String toString(Game game, Goal goal);
}
