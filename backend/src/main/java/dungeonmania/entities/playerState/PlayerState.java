package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;

public interface PlayerState {
    public abstract BattleStatistics applyBuffState(BattleStatistics origin);
}
