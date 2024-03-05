package dungeonmania.entities.collectables;

import dungeonmania.entities.Entity;
import dungeonmania.entities.TreasureForGoal;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SunStone extends Entity implements InventoryItem, TreasureForGoal {
    public SunStone(Position position) {
        super(position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }
}
