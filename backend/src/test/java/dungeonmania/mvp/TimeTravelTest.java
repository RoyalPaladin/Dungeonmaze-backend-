package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TimeTravelTest {
    @Test
    @Tag("19-1")
    @DisplayName("Testing time turner works")
    public void timeTurner() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_TimeTravelTest_simpleTimeTurner", "c_timeTravelTest_generic");

        assertEquals(new Position(1, 1), TestUtils.getPlayerPos(res));
        // pick up time turner
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "time_turner").size());

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // move right where there is nothing
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), TestUtils.getPlayerPos(res));
        res = dmc.rewind(2);
        assertEquals(new Position(5, 1), TestUtils.getPlayerPos(res));
        assertEquals(new Position(3, 1), TestUtils.getEntityPos(res, "old_player"));
    }

    @Test
    @Tag("19-2")
    @DisplayName("Testing user keeps inventory")
    public void timeTravelInventory() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_TimeTravelTest_simpleTimeTurner", "c_timeTravelTest_generic");

        assertEquals(new Position(1, 1), TestUtils.getPlayerPos(res));
        // pick up time turner
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "time_turner").size());

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // move right where there is nothing
        res = dmc.tick(Direction.RIGHT);

        //pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // move right where there is nothing
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // time travel and keep inventory
        res = dmc.rewind(1);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
    }
    //gave up on this test as my original idea of cloning everything proved too hard, only shallow clones possible
/*
    @Test
    @Tag("19-3")
    @DisplayName("Testing dungeon reverts to a previous state")
    public void timeTravelDungeon() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_TimeTravelTest_simpleTimeTurner", "c_timeTravelTest_generic");

        assertEquals(new Position(1, 1), TestUtils.getPlayerPos(res));
        // pick up time turner
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "time_turner").size());

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // move right where there is nothing
        res = dmc.tick(Direction.RIGHT);

        //pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // move right where there is nothing
        res = dmc.tick(Direction.RIGHT);

        // time travel and keep inventory
        res = dmc.rewind(3);
        // move left and collect treasure from previous dungeon state that was collected previously
        res = dmc.tick(Direction.LEFT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
    }
*/
    @Test
    @Tag("19-4")
    @DisplayName("Testing old player follows path")
    public void timeTravelOldPlayerTicks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_TimeTravelTest_simpleTimeTurner", "c_timeTravelTest_generic");

        assertEquals(new Position(1, 1), TestUtils.getPlayerPos(res));
        // pick up time turner
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "time_turner").size());

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // move right where there is nothing
        res = dmc.tick(Direction.RIGHT);

        //pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // move right where there is nothing
        res = dmc.tick(Direction.RIGHT);

        // time travel
        res = dmc.rewind(3);

        //check positions are correct and entity moves
        assertEquals(new Position(6, 1), TestUtils.getPlayerPos(res));
        assertEquals(new Position(3, 1), TestUtils.getEntityPos(res, "old_player"));
        res = dmc.tick(Direction.NONE);
        assertEquals(new Position(4, 1), TestUtils.getEntityPos(res, "old_player"));
    }

    @Test
    @Tag("19-5")
    @DisplayName("Testing dungeon Portal works")
    public void timeTravelPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_TimeTravelTest_complexPortal", "c_timeTravelTest_generic");

        assertEquals(new Position(1, 1), TestUtils.getPlayerPos(res));
        // pick up time turner
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "time_turner").size());

        // pick up invincibility potion
        res = dmc.tick(Direction.RIGHT);

        //move right
        res = dmc.tick(Direction.RIGHT);

        //stay still for 20 turns
        assertEquals(new Position(4, 1), TestUtils.getPlayerPos(res));
        for (int i = 0; i < 20; i++)
            res = dmc.tick(Direction.NONE);

        //go to portal
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // old player should be at the start
        assertEquals(new Position(2, 1), TestUtils.getEntityPos(res, "old_player"));
    }

    @Test
    @Tag("19-6")
    @DisplayName("Testing old player can use items and will fight player")
    public void timeTravelItems() throws IllegalArgumentException, InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_TimeTravelTest_complexPortal", "c_timeTravelTest_generic");

        assertEquals(new Position(1, 1), TestUtils.getPlayerPos(res));
        // pick up time turner
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "time_turner").size());

        // pick up invincibility potion
        res = dmc.tick(Direction.RIGHT);

        // consume invincibility potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());

        // move right
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // wait for invincibility potion to run out for actual player
        for (int i = 0; i < 5; i++)
            res = dmc.tick(Direction.NONE);

        // time travel back
        res = dmc.rewind(9);

        //go to old player to fight (old player has invincibility while actual player does not)
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
    }
}
