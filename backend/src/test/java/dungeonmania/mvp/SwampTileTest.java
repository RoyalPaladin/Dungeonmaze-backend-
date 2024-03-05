package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SwampTileTest {
    @Test
    @Tag("18-1")
    @DisplayName("Testing an enemy mercenary gets slowed by swamp tile, swamp at 6-1, slow 2")
    public void swampSlowEnemy() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_SwampTileTest_withBribableMercAndSwamp", "c_swampTileTest_generic");

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // pick up first treasure, merc lands on swamp
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // pick up second treasure, merc is stuck
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // merc is stuck
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(6, 1), getMercPos(res));

        // merc is no longer stuck moves
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(5, 1), getMercPos(res));

        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("18-2")
    @DisplayName("Testing an ally mercenary does not get slowed by swamp tile, swamp at 6-1, slow 2")
    public void swampDoesNotSlowCardinallyAdjacentAlly() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_SwampTileTest_withBribableMercAndSwamp", "c_swampTileTest_generic");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // pick up first treasure, merc lands on swamp
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // pick up second treasure, merc is stuck
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // merc is stuck
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(6, 1), getMercPos(res));

        // merc is no longer stuck moves
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(5, 1), getMercPos(res));

        // bribes merc who is cardinally adjacent
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // player moves to swamp
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // ally moves to swamp
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getMercPos(res));

        // ally moves out of swamp (does not get slowed)
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));
    }

    @Test
    @Tag("18-3")
    @DisplayName("Testing player does not get slowed by swamp tile")
    public void swampDoesNotSlowPlayer() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_SwampTileTest_withBribableMercAndSwamp", "c_swampTileTest_generic");

        assertEquals(new Position(1, 1), TestUtils.getPlayerPos(res));

        // moves right until arrive at swamp
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // player is now at swamp

        // player moves past swamp
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), TestUtils.getPlayerPos(res));
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

}
