package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;

public class BaseState implements PlayerState {
    public BattleStatistics applyBuffState(BattleStatistics origin) {
        return origin;
    }
}
