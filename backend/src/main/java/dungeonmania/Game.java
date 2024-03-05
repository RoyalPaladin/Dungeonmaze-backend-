package dungeonmania;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;

import dungeonmania.battles.BattleFacade;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.OldPlayer;
import dungeonmania.entities.Player;
import dungeonmania.entities.TreasureForGoal;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.goals.Goal;
import dungeonmania.map.GameMap;
import dungeonmania.map.GraphNode;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Game {
    private String id;
    private String name;
    private Goal goals;
    private GameMap map;
    private Player player;
    private int destroyedEnemies = 0;
    private BattleFacade battleFacade;
    private int initialTreasureCount = 0;
    private EntityFactory entityFactory;
    private boolean isInTick = false;
    public static final int PLAYER_MOVEMENT = 0;
    public static final int PLAYER_MOVEMENT_CALLBACK = 1;
    public static final int AI_MOVEMENT = 2;
    public static final int AI_MOVEMENT_CALLBACK = 3;

    private int tickCount = 0;
    private PriorityQueue<ComparableCallback> sub = new PriorityQueue<>();
    private PriorityQueue<ComparableCallback> addingSub = new PriorityQueue<>();
    private ArrayList<String> playerActionHistory = new ArrayList<>();
    private ArrayList<Direction> directionHistory = new ArrayList<>();
    private ArrayList<String> buildablesAndIdsHistory = new ArrayList<>();
    private ArrayList<GameMap> mapsHistory = new ArrayList<>();
    private ArrayList<Player> playersHistory = new ArrayList<>();

    public Game(String dungeonName) {
        this.name = dungeonName;
        this.map = new GameMap();
        this.battleFacade = new BattleFacade();
    }

    public void init() {
        this.id = UUID.randomUUID().toString();
        map.init();
        this.tickCount = 0;
        player = map.getPlayer();
        register(() -> player.onTick(tickCount), PLAYER_MOVEMENT, "potionQueue");
        List<Entity> entities = map.getEntities();
        for (Entity entity : entities) {
            if (entity instanceof TreasureForGoal) initialTreasureCount++;
        }
    }

    public Game tick(Direction movementDirection) {
        updateDirectionTick(movementDirection);
        registerOnce(
            () -> player.move(this.getMap(), movementDirection), PLAYER_MOVEMENT, "playerMoves");
        tick();
        return this;
    }

    private void updateDirectionTick(Direction movementDirection) {
        updateTimeTravelTickState();
        playerActionHistory.add("move");
        directionHistory.add(movementDirection);
    }

    private void updateTimeTravelTickState() {
        GameMap mapToAdd = new GameMap();
        Map<Position, GraphNode> nodes = new HashMap<>();
        nodes.putAll(map.getNodes());
        mapToAdd.setNodes(map.getNodes());
        mapsHistory.add(mapToAdd);
        Player playerToAdd = new Player(player.getPosition(), 0, 0);
        player.clone(playerToAdd);
        playersHistory.add(playerToAdd);
    }

    public Game tick(String itemUsedId) throws InvalidActionException {
        updateUseItemTick(itemUsedId);
        Entity item = player.getEntity(itemUsedId);
        if (item == null)
            throw new InvalidActionException(String.format("Item with id %s doesn't exist", itemUsedId));
        if (!(item instanceof Bomb) && !(item instanceof Potion))
            throw new IllegalArgumentException(String.format("%s cannot be used", item.getClass()));

        registerOnce(() -> {
            if (item instanceof Bomb)
                player.use((Bomb) item, map);
            if (item instanceof Potion)
                player.use((Potion) item, tickCount);
        }, PLAYER_MOVEMENT, "playerUsesItem");
        tick();
        return this;
    }

    private void updateUseItemTick(String itemUsedId) {
        playerActionHistory.add("use");
        buildablesAndIdsHistory.add(itemUsedId);
    }

    /**
     * Battle between player and enemy
     * @param player
     * @param enemy
     */
    public void battle(Player player, Enemy enemy) {
        if (player instanceof OldPlayer) return;
        battleFacade.battle(this, player, enemy);
        if (player.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(player);
        }
        if (enemy.getBattleStatistics().getHealth() <= 0) {
            map.destroyEntity(enemy);
            destroyedEnemies++;
        }
    }

    public Game build(String buildable) throws InvalidActionException {
        updateBuildTick(buildable);
        List<String> buildables = player.getBuildables(map);
        if (!buildables.contains(buildable)) {
            throw new InvalidActionException(String.format("%s cannot be built", buildable));
        }
        registerOnce(() -> player.build(map, buildable, entityFactory), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    private void updateBuildTick(String buildable) {
        playerActionHistory.add("build");
        buildablesAndIdsHistory.add(buildable);
    }

    public Game interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        updateInteractTick(entityId);
        Entity e = map.getEntity(entityId);
        if (e == null || !(e instanceof Interactable))
            throw new IllegalArgumentException("Entity cannot be interacted");
        if (!((Interactable) e).isInteractable(player)) {
            throw new InvalidActionException("Entity cannot be interacted");
        }
        registerOnce(
            () -> ((Interactable) e).interact(player, this), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    private void updateInteractTick(String entityId) {
        playerActionHistory.add("interact");
        buildablesAndIdsHistory.add(entityId);
    }

    public <T extends Entity> long countEntities(Class<T> type) {
        return map.countEntities(type);
    }

    public void register(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id));
        else
            sub.add(new ComparableCallback(r, priority, id));
    }

    public void registerOnce(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id, true));
        else
            sub.add(new ComparableCallback(r, priority, id, true));
    }

    public void unsubscribe(String id) {
        for (ComparableCallback c : sub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
        for (ComparableCallback c : addingSub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
    }

    public int tick() {
        isInTick = true;
        sub.forEach(s -> s.run());
        isInTick = false;
        sub.addAll(addingSub);
        addingSub = new PriorityQueue<>();
        sub.removeIf(s -> !s.isValid());
        additionalTickFunctions();
        tickCount++;
        // update the weapons/potions duration
        return tickCount;
    }

    private void additionalTickFunctions() {
        List<Entity> entities = map.getEntities();
        for (Entity entity : entities) {
            if (entity instanceof Mercenary) {
                ((Mercenary) entity).mindControlTick();
            }
            if (entity instanceof OldPlayer) {
                ((OldPlayer) entity).oldPlayerTick(map);
            }
        }
    }

    public Game rewindTime(int numberOfTicks) {
        int targetTickNum = tickCount - numberOfTicks;
        if (targetTickNum < 0) targetTickNum = 0;
        map = mapsHistory.get(targetTickNum);
        map.setGame(this);
        map.setPlayer(player);
        entityFactory.spawnEntity("old_player", this, playersHistory.get(targetTickNum));
        playersHistory.get(targetTickNum);
        OldPlayer oldPlayer = map.getEntities(OldPlayer.class).get(0);
        oldPlayer.setTickStart(targetTickNum);
        oldPlayer.setTickEnd(tickCount);
        oldPlayer.setPlayerActionHistory(playerActionHistory);
        oldPlayer.setDirectionHistory(directionHistory);
        oldPlayer.setBuildablesAndIdsHistory(buildablesAndIdsHistory);
        return this;
    }

    public void spawnEntityFromFactory(String toSpawn, Entity configEntities) {
        entityFactory.spawnEntity(toSpawn, this, configEntities);
    }

    public int getTick() {
        return this.tickCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Goal getGoals() {
        return goals;
    }

    public void setGoals(Goal goals) {
        this.goals = goals;
    }

    public GameMap getMap() {
        return map;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public void setEntityFactory(EntityFactory factory) {
        entityFactory = factory;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BattleFacade getBattleFacade() {
        return battleFacade;
    }

    public void setBattleFacade(BattleFacade battleFacade) {
        this.battleFacade = battleFacade;
    }

    public int getInitialTreasureCount() {
        return initialTreasureCount;
    }

    public int getDestroyedEnemies() {
        return destroyedEnemies;
    }
}
