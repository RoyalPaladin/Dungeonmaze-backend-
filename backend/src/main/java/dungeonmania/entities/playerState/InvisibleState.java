package dungeonmania.entities.playerState;

import dungeonmania.battles.BattleStatistics;

public class InvisibleState implements PlayerState {
    public BattleStatistics applyBuffState(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
            0,
            1,
            1,
            false,
            false));
    }
}
