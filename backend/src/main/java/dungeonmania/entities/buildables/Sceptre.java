package dungeonmania.entities.buildables;

import java.util.LinkedHashMap;
import java.util.Map;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;

public class Sceptre extends Buildable {
    private int duration;

    private Map<String, Integer> recipe = new LinkedHashMap<>() {{
        put("Wood", 1);
        put("OR", 1);
        put("Arrow", 2);

        put("Key", 1);
        put("OR", 1);
        put("Treasure", 1);

        put("SunStone", 1);
    }};

    public Sceptre(int duration) {
        super(null);
        this.duration = duration;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
            0,
            0,
            0));
    }

    @Override
    public void use(Game game) {
    }

    @Override
    public int getDurability() {
        return 9999;
    }
    public int getDuration() {
        return duration;
    }

    public Map<String, Integer> getRecipe() {
        return recipe;
    }
}
