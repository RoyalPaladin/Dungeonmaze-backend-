package dungeonmania.entities.enemies;

import dungeonmania.Game;

public interface Move {
    public abstract void moveEnemy(Enemy enemy, Game game);
}
