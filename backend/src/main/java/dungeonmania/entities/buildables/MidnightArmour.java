package dungeonmania.entities.buildables;

import java.util.LinkedHashMap;
import java.util.Map;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;

public class MidnightArmour extends Buildable {
    private int attackBuff;
    private int defenseBuff;
    private Map<String, Integer> recipe = new LinkedHashMap<>() {{
        put("Sword", 1);
        put("SunStone", 1);
    }};

    public MidnightArmour(int attackBuff, int defenseBuff) {
        super(null);
        this.attackBuff = attackBuff;
        this.defenseBuff = defenseBuff;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            attackBuff,
            defenseBuff,
            1,
            1));
        }

    @Override
    public void use(Game game) {
        return;
    }

    @Override
    public int getDurability() {
        return 2147483647;
    }

    public Map<String, Integer> getRecipe() {
        return recipe;
    }
}
