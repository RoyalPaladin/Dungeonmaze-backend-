package dungeonmania.goals;

import java.util.HashMap;

import dungeonmania.Game;
import dungeonmania.goals.GoalConditions.BothGoals;
import dungeonmania.goals.GoalConditions.BouldersGoal;
import dungeonmania.goals.GoalConditions.EitherGoals;
import dungeonmania.goals.GoalConditions.EnemiesGoal;
import dungeonmania.goals.GoalConditions.ExitGoal;
import dungeonmania.goals.GoalConditions.TreasureGoal;

public class Goal {
    private String type;
    private GoalConditionsInterface goalConditionType;
    private int target;
    private Goal goal1;
    private Goal goal2;
    private HashMap<String, GoalConditionsInterface> goalConditionsHashMap = new HashMap<>() {{
        put("exit", new ExitGoal());
        put("boulders", new BouldersGoal());
        put("treasure", new TreasureGoal());
        put("enemies", new EnemiesGoal());
        put("AND", new BothGoals());
        put("OR", new EitherGoals());
    }};

    public Goal(String type) {
        this.type = type;
        goalConditionType = goalConditionsHashMap.get(type);
    }

    public Goal(String type, int target) {
        this.type = type;
        this.target = target;
        goalConditionType = goalConditionsHashMap.get(type);

    }

    public Goal(String type, Goal goal1, Goal goal2) {
        this.type = type;
        this.goal1 = goal1;
        this.goal2 = goal2;
        goalConditionType = goalConditionsHashMap.get(type);

    }

    /**
     * @return true if the goal has been achieved, false otherwise
     */
    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;
        return goalConditionType.playerHasWon(game, this);
    }

    public String toString(Game game) {
        if (this.achieved(game)) return "";
        return goalConditionType.toString(game, this);
    }

    public boolean hasFirstGoalAchieved(Game game) {
        return goal1.achieved(game);
    }

    public boolean hasSecondGoalAchieved(Game game) {
        return goal2.achieved(game);
    }

    public String goalOneToString(Game game) {
        return goal1.toString(game);
    }

    public String goalTwoToString(Game game) {
        return goal2.toString(game);
    }

    public int getTarget() {
        return target;
    }

    public Goal getGoal1() {
        return goal1;
    }

    public Goal getGoal2() {
        return goal2;
    }
}
