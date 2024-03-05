package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class AllyMove implements Move {
    private Position prevPlayerPos;

    public AllyMove(Player player) {
        prevPlayerPos = player.getPosition();
    }

    @Override
    public void moveEnemy(Enemy enemy, Game game) {
        GameMap map = game.getMap();
        Position playerPos = map.getPlayer().getPosition();
        if (Position.isAdjacent(enemy.getPosition(), prevPlayerPos)
            || Position.isAdjacent(enemy.getPosition(), playerPos)) {
            if (!prevPlayerPos.equals(playerPos)) {
                map.moveTo(enemy, prevPlayerPos);
                prevPlayerPos = playerPos;
            }
        } else {
            Position nextPos = map.dijkstraPathFind(enemy.getPosition(), playerPos, enemy);
            map.moveTo(enemy, nextPos);
        }
    }
}
