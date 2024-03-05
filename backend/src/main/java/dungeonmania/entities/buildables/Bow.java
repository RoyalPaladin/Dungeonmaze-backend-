package dungeonmania.entities.buildables;

import java.util.LinkedHashMap;
import java.util.Map;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;


public class Bow extends Buildable  {

    private int durability;
    private Map<String, Integer> recipe = new LinkedHashMap<>() {{
        put("Wood", 1);
        put("Arrow", 3);
    }};

    public Bow(int durability) {
        super(null);
        this.durability = durability;
    }

    @Override
    public void use(Game game) {
        durability--;
        if (durability <= 0) {
            game.getPlayer().remove(this);
        }
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
            0,
            2,
            1));
    }

    @Override
    public int getDurability() {
        return durability;
    }
    public Map<String, Integer> getRecipe() {
        return recipe;
    }
}
