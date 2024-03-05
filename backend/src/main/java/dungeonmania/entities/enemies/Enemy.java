package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.Destroyable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Overlappable;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Enemy extends Entity implements Battleable, Overlappable, Destroyable {
    private BattleStatistics battleStatistics;
    private Move movement;

    public Enemy(Position position, double health, double attack, Move movement) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER);
        this.movement = movement;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Player;
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            map.getGame().battle(player, this);
        }
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
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

    public void move(Game game) {
        movement.moveEnemy(this, game);
    }

    public void setMovement(Move movement) {
        this.movement = movement;
    }
}
