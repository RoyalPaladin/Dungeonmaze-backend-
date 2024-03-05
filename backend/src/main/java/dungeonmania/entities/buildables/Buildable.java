package dungeonmania.entities.buildables;

import java.util.Map;

import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.util.Position;

public abstract class Buildable extends Entity implements InventoryItem, BattleItem {

    private Map<String, Integer> recipe;

    public Buildable(Position position) {
        super(position);
    }
    public Map<String, Integer> getRecipe() {
        return recipe;
    }
}
