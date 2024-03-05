package dungeonmania.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.BaseState;
import dungeonmania.entities.playerState.InvincibleState;
import dungeonmania.entities.playerState.InvisibleState;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable, Overlappable {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> queue = new LinkedList<>();
    private Potion inEffective = null;
    private int nextTrigger = 0;

    private PlayerState state;

    public Player(Position position, double health, double attack) {
        super(position);
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        inventory = new Inventory();
        state = new BaseState();
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public BattleItem getWeapon() {
        return inventory.getWeapon();
    }

    public List<String> getBuildables(GameMap map) {
        return inventory.getBuildables(map);
    }

    public boolean build(GameMap map, String entity, EntityFactory factory) {
        InventoryItem item = inventory.checkBuildCriteria(this, true, entity.equals("shield"), factory, entity, map);
        if (item == null) return false;
        return inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof Mercenary) {
                if (((Mercenary) entity).isAllied()) return;
            }
            map.getGame().battle(this, (Enemy) entity);
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
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public boolean pickUp(Entity item) {
        return inventory.add((InventoryItem) item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null) inventory.remove(item);
    }

    public void use(Bomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public void triggerNext(int currentTick) {
        if (queue.isEmpty()) {
            inEffective = null;
            this.changeState(new BaseState());
            return;
        }
        inEffective = queue.remove();
        if (inEffective instanceof InvincibilityPotion) {
            this.changeState(new InvincibleState());
        } else {
            this.changeState(new InvisibleState());
        }
        nextTrigger = currentTick + inEffective.getDuration();
    }

    public void changeState(PlayerState playerState) {
        state = playerState;
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        queue.add(potion);
        if (inEffective == null) {
            triggerNext(tick);
        }
    }

    public void onTick(int tick) {
        if (inEffective == null || tick == nextTrigger) {
            triggerNext(tick);
        }
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        return state.applyBuffState(origin);
    }

    @Override
    public double getHealth() {
        battleStatistics.getHealth();
        return 0;
    }
    @Override
    public double setHealth(Double health) {
        battleStatistics.setHealth(health);
        return 0;
    }
    @Override
    public boolean isEnabled() {
        return battleStatistics.isEnabled();
    }
    public void setBattleStatistics(BattleStatistics battleStatistics) {
        this.battleStatistics = battleStatistics;
    }
    public <T> List<T> getInventoryEntities(Class<T> clz) {
        return inventory.getItems().stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public <T extends InventoryItem> T getFirstInventoryItem(Class<T> itemType) {
        return inventory.getFirst(itemType);
    }

    public double setAttack(Double attack) {
        battleStatistics.setHealth(attack);
        return 0;
    }
    public double getAttack() {
        battleStatistics.getAttack();
        return 0;
    }
    public Queue<Potion> getQueue() {
        return queue;
    }
    public Potion getInEffective() {
        return inEffective;
    }
    public PlayerState getState() {
        return state;
    }
    public int getNextTrigger() {
        return nextTrigger;
    }
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    public void setQueue(Queue<Potion> queue) {
        this.queue = queue;
    }
    public void setInEffective(Potion inEffective) {
        this.inEffective = inEffective;
    }
    public void setNextTrigger(int nextTrigger) {
        this.nextTrigger = nextTrigger;
    }
    public void setState(PlayerState state) {
        this.state = state;
    }

    public Player clone(Player player) {
        BattleStatistics battleStats = new BattleStatistics(player.getHealth(), player.getAttack(), 1, 1, 1);
        Inventory inventoryToAdd = new Inventory();
        inventoryToAdd.setItems(inventory.getItems());
        player.setPosition(this.getPosition());
        player.setHealth(this.getHealth());
        player.setAttack(this.getAttack());
        player.setBattleStatistics(battleStats);
        player.setInventory(inventoryToAdd);
        player.setQueue(queue);
        player.setInEffective(inEffective);
        player.setNextTrigger(nextTrigger);
        player.setState(state);
        return player;
    }

    public int getPositionX() {
        return this.getPosition().getX();
    }
    public int getPositionY() {
        return this.getPosition().getY();
    }
}
