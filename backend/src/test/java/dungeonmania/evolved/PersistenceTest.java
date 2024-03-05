// package dungeonmania.evolved;

// import dungeonmania.DungeonManiaController;
// import dungeonmania.response.models.DungeonResponse;
// import dungeonmania.util.Direction;
// import dungeonmania.util.Position;

// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Tag;
// import org.junit.jupiter.api.Test;
// import dungeonmania.mvp.TestUtils;

// import static org.junit.jupiter.api.Assertions.*;

// public class PersistenceTest {
//     @Test
//     @Tag("2-1")
//     @DisplayName("Test position of entities are the same as after loading")
//     public void positionSave() {
//         DungeonManiaController dmc1 = new DungeonManiaController();
//         DungeonResponse res = dmc1.newGame("d_allyMoveTest", "c_allyMoveTest");

//         // collect treasure
//         res = dmc1.tick(Direction.LEFT);
//         assertEquals(new Position(7, 1), getMercPos(res));

//         res = dmc1.saveGame("savedGame");

//         DungeonManiaController dmc2 = new DungeonManiaController();
//         res = dmc2.loadGame("savedGame");

//         // check the position is the same
//         assertEquals(new Position(7, 1), getMercPos(res));
//     }

//     private Position getMercPos(DungeonResponse res) {
//         return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
//     }
// }
