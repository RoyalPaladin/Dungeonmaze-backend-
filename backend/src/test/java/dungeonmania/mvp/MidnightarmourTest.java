package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MidnightarmourTest {

    @Test
    @Tag("17-1")
    @DisplayName("Test a midnightarmour can be built when no zombies exist")
    public void canBuildMidnightArmour() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_MidnightArmourTest_noZombies", "c_MidnightarmourTest_buff");

        // pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // player now has all necessary items to build midnightarmour
        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        TestUtils.getInventory(res, "midnight_armour");
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());

        // materials used disappear
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("17-2")
    @DisplayName("Test a midnightarmour cannot be built when zombies exist")
    public void cannotBuildMidnightArmourBecauseZombies() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_MidnightArmourTest_yesZombies", "c_MidnightarmourTest_buff");

        // pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // player now has all necessary items to build midnightarmour
        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());
        assertThrows(InvalidActionException.class, () ->
                dmc.build("midnight_armour")
        );

        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());

        // materials is not used
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
    }

    @Test
    @Tag("17-3")
    @DisplayName("Test a midnightarmour buffs player correctly")
    public void midnightArmourBuff() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_MidnightArmourTest_noZombies", "c_MidnightarmourTest_buff");

        // pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // player now has all necessary items to build midnightarmour
        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());

        // materials used disappear
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sunstone").size());

        res = dmc.tick(Direction.RIGHT);

        BattleResponse battle = res.getBattles().get(0);
        //RoundResponse firstRound = battle.getRounds().size();

        // Player is set to 1 health and 1 attack so without armour will die in 1 rounds
        // armour is set to 50 defense and 50 attack
        // Mercenary has 100 health and 50 attack so without armour player will not be able to kill him in 10 rounds
        // Thus, testing that the buff works for both armour and flat damage increase
        assertEquals(10, battle.getRounds().size());
    }

    @Test
    @Tag("17-3")
    @DisplayName("Test a midnightarmour lasts forever through a large number of fights")
    public void midnightArmourDurability() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_MidnightArmourTest_noZombies", "c_MidnightarmourTest_infiniteDuration");

        // pick up sword
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // player now has all necessary items to build midnightarmour
        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());

        // materials used disappear
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sunstone").size());

        res = dmc.tick(Direction.RIGHT);

        BattleResponse battle = res.getBattles().get(0);

        // Player is set to 1 health and 1 attack so without armour will die in 10 rounds
        // armour is set to 50 defense and 0 attack
        // Mercenary has 100 health and 1 attack and armour has 0 attack so player will kill him in 5000(large) rounds
        // Thus, testing that the durability works as player would be dead without the armour
        assertEquals(5000, battle.getRounds().size());
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
}
