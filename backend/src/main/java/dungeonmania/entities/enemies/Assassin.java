package dungeonmania.entities.enemies;

import java.util.Random;

import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.util.Position;

public class Assassin extends Mercenary {
    public static final int DEFAULT_BRIBE_AMOUNT = 2;
    public static final double DEFAULT_FAIL_RATE = 0.5;
    public static final double DEFAULT_ATTACK = 10.0;
    public static final double DEFAULT_HEALTH = 20.0;

    private double failRate = Assassin.DEFAULT_FAIL_RATE;

    public Assassin(Position position, double health, double attack, int bribeAmount, int bribeRadius,
                    double failRate) {
        super(position, health, attack, bribeAmount, bribeRadius);
        this.failRate = failRate;
    }


    @Override
    protected void bribe(Player player) {
        Random rand = new Random();
        if (player.countEntityOfType(Sceptre.class) > 0) {
            Sceptre sceptre = player.getFirstInventoryItem(Sceptre.class);
            setMindControlduration(sceptre.getDuration());
            setMovement(new AllyMove(player));
            setAllied(true);
            return;
        } else if (rand.nextDouble() >= failRate) {
            setMovement(new AllyMove(player));
            setAllied(true);
        }

        for (int i = 0; i < getBribeAmount(); i++) {
            player.use(Treasure.class);
        }
    }
}
