package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;
    private boolean allied = false;
    private int mindControlduration = -1;

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius) {
        super(position, health, attack, new HostileMove());
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
    }

    public boolean isAllied() {
        return allied;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied) return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     * @param player
     * @return
     */
    private boolean canBeBribed(Player player) {
        //check if player is in merc radius
        if (!(Math.abs(player.getPositionX() - this.getPosition().getX()) <= bribeRadius)) return false;
        if (!(Math.abs(player.getPositionY() - this.getPosition().getY()) <= bribeRadius)) return false;
        return bribeRadius >= 0 && player.countEntityOfType(Treasure.class) >= bribeAmount;
    }

    /**
     * bribe the merc
     */
    protected void bribe(Player player) {
        if (player.countEntityOfType(Sceptre.class) > 0) {
            Sceptre sceptre = player.getFirstInventoryItem(Sceptre.class);
            mindControlduration = sceptre.getDuration();
            setMovement(new AllyMove(player));
            allied = true;
            return;
        }
        for (int i = 0; i < bribeAmount; i++) {
            player.use(Treasure.class);
        }
        setMovement(new AllyMove(player));
        allied = true;
    }

    @Override
    public void interact(Player player, Game game) {
        bribe(player);
    }

    @Override
    public boolean isInteractable(Player player) {
        return (!allied && (canBeBribed(player) || canBeMindControlled(player)));
    }

    private boolean canBeMindControlled(Player player) {
        //return true;
        if (player.countEntityOfType(Sceptre.class) > 0) return true;
        return false;
    }

    public void mindControlTick() {
        if (mindControlduration >= 0) {
            mindControlduration--;
            if (mindControlduration < 0) {
                allied = false;
                super.setMovement(new HostileMove());
            }
        }
    }

    protected int getBribeAmount() {
        return bribeAmount;
    }

    protected void setAllied(boolean allied) {
        this.allied = allied;
    }

    protected void setMindControlduration(int mindControlduration) {
        this.mindControlduration = mindControlduration;
    }
}
