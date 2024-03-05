package dungeonmania.entities;

import java.util.ArrayList;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwampTile extends Entity implements Overlappable {
    private int slowDuration;
    private ArrayList<Entity> immuneEntities = new ArrayList<Entity>();

    public SwampTile(Position position, int slowDuration) {
        super(position);
        this.slowDuration = slowDuration;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) return;
        if (immuneEntities.contains(entity)) return;
        entity.setFrozenInPlaceForXTicks(slowDuration);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public ArrayList<Entity> getImmuneEntities() {
        return immuneEntities;
    }

    public void setImmuneEntities(ArrayList<Entity> immuneEntities) {
        this.immuneEntities = immuneEntities;
    }

    public void addImmuneEntity(Entity entity) {
        this.immuneEntities.add(entity);
    }

    public void removeImmuneEntity(Entity entity) {
        this.immuneEntities.remove(entity);
    }
}
