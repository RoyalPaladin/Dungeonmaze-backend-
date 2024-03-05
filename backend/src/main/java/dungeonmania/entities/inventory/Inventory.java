package dungeonmania.entities.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.Buildable;
import dungeonmania.entities.buildables.MidnightArmour;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.buildables.Shield;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;
import dungeonmania.entities.enemies.ZombieToast;
import dungeonmania.map.GameMap;
import dungeonmania.util.NameConverter;

public class Inventory {
    private List<InventoryItem> items = new ArrayList<>();
    private ArrayList<Buildable> buildables = new ArrayList<>(List.of(
        //Store buildables here
                        new Bow(0),
                        new Shield(0, 0),
                        new Sceptre(0),
                        new MidnightArmour(0, 0)));

    public boolean add(InventoryItem item) {
        items.add(item);
        return true;
    }

    public void remove(InventoryItem item) {
        items.remove(item);
    }

    public List<String> getBuildables(GameMap map) {
        List<String> result = new ArrayList<>();

        for (Buildable buildable : buildables) {
            Map<String, Integer> hashmap = buildable.getRecipe();
            boolean isBuildable = true;
            boolean missingPrevKey = false;
            boolean orConditionSatisfied = false;
            boolean buildingSubstituteUsed = false;
            if (!satisfiesAnyExtraConditions(buildable, map)) continue;
            for (String key : hashmap.keySet()) {
                if (orConditionSatisfied) {
                    orConditionSatisfied ^= true;
                    continue;
                }
                if (key == "OR") {
                    if (missingPrevKey) {
                        // if the previous collectable was the item missing resets isbuildable
                        isBuildable = true;
                        missingPrevKey = false;
                    } else if (isBuildable) orConditionSatisfied = true;
                    // ^ If it is still buildable it means the previous or condition was satisfied
                    // so the next iteration can be skipped
                    continue;
                } else if (missingPrevKey) break;
                //missingPrevKey = false;
                int invCount = hashmapCount(key);
                if (invCount < hashmap.get(key)) {
                    if (!buildingSubstituteUsed) {
                        if (checkBuildingSubstituteExist(key)) {
                            buildingSubstituteUsed = true;
                            continue;
                        }
                    }
                    if (isBuildable) missingPrevKey = true;
                    isBuildable = false;
                }
            }
            if (isBuildable) result.add(NameConverter.toSnakeCase(buildable.getClass().getSimpleName()));
        }
        return result;
    }

    private boolean satisfiesAnyExtraConditions(Buildable buildable, GameMap map) {
        if (buildable instanceof MidnightArmour) {
            List<ZombieToast> zombie = map.getEntities(ZombieToast.class);
            if (zombie.size() > 0) return false;
        }
        return true;
    }

    public InventoryItem checkBuildCriteria(Player p, boolean remove, boolean forceShield,
    EntityFactory factory, String toBuild, GameMap map) {

        List<Wood> wood = getEntities(Wood.class);
        List<Arrow> arrows = getEntities(Arrow.class);
        List<Treasure> treasure = getEntities(Treasure.class);
        List<Key> keys = getEntities(Key.class);
        List<SunStone> sunstone = getEntities(SunStone.class);
        List<Sword> sword = getEntities(Sword.class);
        List<ZombieToast> zombie = map.getEntities(ZombieToast.class);

        if (wood.size() >= 1 && arrows.size() >= 3 && !forceShield && toBuild == "bow") {
            if (remove) {
                items.remove(wood.get(0));
                items.remove(arrows.get(0));
                items.remove(arrows.get(1));
                items.remove(arrows.get(2));
            }
            return factory.buildBow();

        } else if (wood.size() >= 2 && (treasure.size() >= 1 || keys.size() >= 1
                || sunstone.size() >= 1) && toBuild == "shield") {
            if (remove) {
                items.remove(wood.get(0));
                items.remove(wood.get(1));
                if (sunstone.size() >= 1) {
                    return factory.buildShield();
                } else if (treasure.size() >= 1) {
                    items.remove(treasure.get(0));
                } else {
                    items.remove(keys.get(0));
                }
            }
            return factory.buildShield();

        } else if ((wood.size() >= 1 || arrows.size() >= 2) && (treasure.size() >= 1
        || keys.size() >= 1) && sunstone.size() >= 1 && toBuild == "sceptre") {
            if (remove) {
                if (wood.size() >= 1) {
                    items.remove(wood.get(0));
                } else {
                    items.remove(arrows.get(0));
                    items.remove(arrows.get(1));
                }
                if (sunstone.size() >= 2) {
                    items.remove(sunstone.get(0));
                } else if (sunstone.size() == 1) {
                    if (treasure.size() >= 1) {
                        items.remove(treasure.get(0));
                    } else {
                        items.remove(keys.get(0));
                    }
                    items.remove(sunstone.get(0));
                }
            }
            return factory.buildSceptre();
        } else if (zombie.size() == 0 && sword.size() >= 1 && sunstone.size() >= 1 && toBuild == "midnight_armour") {
            if (remove) {
                items.remove(sword.get(0));
                items.remove(sunstone.get(0));
            }
            return factory.buildMidnightArmour();
        }
        return null;
    }

    private boolean checkBuildingSubstituteExist(String key) {
        if (key == "Treasure" || key == "Key") {
            for (InventoryItem item : items) {
                System.out.println(item.getClass().getSimpleName());
                if (item instanceof SunStone) return true;
            }
        }
        return false;
    }

    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) count++;
        return count;
    }

    public int hashmapCount(String itemType) {
        int count = 0;
        for (InventoryItem item : items) {
            String itemClassString = item.getClass().getSimpleName();
            if (itemClassString.equals(itemType)) {
                count += 1;
            }
        }
        return count;
    }


    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (((Entity) item).getId().equals(itemUsedId)) return (Entity) item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public BattleItem getWeapon() {
        BattleItem weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

    public List<InventoryItem> getItems() {
        return items;
    }
    public void setItems(List<InventoryItem> items) {
        this.items = items;
    }
}
