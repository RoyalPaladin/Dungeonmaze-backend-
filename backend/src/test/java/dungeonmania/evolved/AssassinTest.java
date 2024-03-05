package dungeonmania.evolved;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import dungeonmania.mvp.TestUtils;

import static org.junit.jupiter.api.Assertions.*;



public class AssassinTest {
    @Test
    @Tag("1-1")
    @DisplayName("Test assassin with 0 bribe fail rate")
    public void noFailRate() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest", "c_assassinTestNoFail");
        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // collect treasure
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // attempt bribe 3 times, will always succeed the first time, check treasure not fully used
        res = assertDoesNotThrow(() -> dmc.interact(assId));
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(assId)
        );
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(assId)
        );
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // check battle does not occur after overlap
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("1-2")
    @DisplayName("Test assassin with 1 bribe fail rate")
    public void fullFailRate() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest", "c_assassinTestFail");
        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // collect treasure
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // attempt bribe and fail 3 times, check no treasure left
        res = assertDoesNotThrow(() -> dmc.interact(assId));
        res = assertDoesNotThrow(() -> dmc.interact(assId));
        res = assertDoesNotThrow(() -> dmc.interact(assId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // check battle occurs
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(0, res.getBattles().size());
    }
}
