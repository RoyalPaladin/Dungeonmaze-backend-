package dungeonmania.entities;

import java.util.ArrayList;
import java.util.Queue;

import dungeonmania.battles.BattleFacade;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class OldPlayer extends Player {
    private Inventory inventory;
    private int tickStart;
    private int tickEnd;
    private ArrayList<String> playerActionHistory = new ArrayList<>();
    private ArrayList<Direction> directionHistory = new ArrayList<>();
    private ArrayList<String> buildablesAndIdsHistory = new ArrayList<>();

    public OldPlayer(Position position, double health, double attack, BattleStatistics battleStatistics,
    Inventory inventory, Queue<Potion> queue, Potion inEffective, int nextTrigger, PlayerState state) {
        super(position, health, attack);
        super.setBattleStatistics(battleStatistics);
        super.setInventory(inventory);
        this.inventory = inventory;
        super.setQueue(queue);
        super.setInEffective(inEffective);
        super.setNextTrigger(nextTrigger);
        super.setState(state);
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            return;
        } else if (entity instanceof Bomb) {
            if (!((Bomb) entity).hasBombSpawned()) return;
            if (!((Player) this).pickUp(entity)) return;
            ((Bomb) entity).unsubscribeSubs();
            map.destroyEntity(entity);
            Overlappable overlap = (Overlappable) entity;
            overlap.onOverlap(map, entity);
        } else if (entity instanceof InventoryItem) {
            if (!((Player) this).pickUp(entity)) return;
            map.destroyEntity(entity);
        } else if (entity instanceof Player) {
            BattleFacade battleFacade = new BattleFacade();
            battleFacade.battle(map.getGame(), this, (Player) (entity));
            if (this.getBattleStatistics().getHealth() <= 0) {
                map.destroyEntity(this);
            }
            if (((Player) entity).getBattleStatistics().getHealth() <= 0) {
                map.destroyEntity(entity);
            }
        }
    }

    public void oldPlayerTick(GameMap map) {
        if (playerActionHistory.size() > (tickEnd - tickStart)) {
            int actionsToDelete = playerActionHistory.size() - (tickEnd - tickStart);
            for (String action : playerActionHistory) {
                if (actionsToDelete == 0) break;
                actionsToDelete--;
                if (action == "move") {
                    directionHistory.remove(0);
                } else if (action == "build") {
                    buildablesAndIdsHistory.remove(0);
                } else if (action == "interact") {
                    buildablesAndIdsHistory.remove(0);
                } else if (action == "use") {
                    buildablesAndIdsHistory.remove(0);
                }
            }
            for (int i = playerActionHistory.size() - (tickEnd - tickStart); i > 0; i--) {
                playerActionHistory.remove(0);
            }
        }
        String action = playerActionHistory.get(0);
        if (action == "move") {
            this.move(map, directionHistory.get(0));
            directionHistory.remove(0);
        } else if (action == "build") {
            this.build(map, buildablesAndIdsHistory.get(0), map.getGame().getEntityFactory());
            buildablesAndIdsHistory.remove(0);
        } else if (action == "interact") {
            buildablesAndIdsHistory.remove(0);
        } else if (action == "use") {
            InventoryItem item = (InventoryItem) map.getEntity(buildablesAndIdsHistory.get(0));
            if (item != null) inventory.remove(item);
            buildablesAndIdsHistory.remove(0);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
    public void setTickStart(int tickStart) {
        this.tickStart = tickStart;
    }
    public void setTickEnd(int tickEnd) {
        this.tickEnd = tickEnd;
    }
    public void setPlayerActionHistory(ArrayList<String> playerActionHistory) {
        this.playerActionHistory = playerActionHistory;
    }
    public void setDirectionHistory(ArrayList<Direction> directionHistory) {
        this.directionHistory = directionHistory;
    }
    public void setBuildablesAndIdsHistory(ArrayList<String> buildablesAndIdsHistory) {
        this.buildablesAndIdsHistory = buildablesAndIdsHistory;
    }
}
