package dungeonmania.entities.buildables;


import java.util.LinkedHashMap;
import java.util.Map;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;


public class Shield extends Buildable {
    private int durability;
    private double defence;
    private Map<String, Integer> recipe = new LinkedHashMap<>() {{
        put("Wood", 1);
        put("Treasure", 1);
        put("OR", 1);
        put("Key", 1);
    }};

    public Shield(int durability, double defence) {
        super(null);
        this.durability = durability;
        this.defence = defence;
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
            defence,
            1,
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
