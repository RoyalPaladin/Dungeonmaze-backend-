package dungeonmania.battles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.NameConverter;

public class BattleFacade {
    private List<BattleResponse> battleResponses = new ArrayList<>();

    public void battle(Game game, Player player, Enemy enemy) {
        // 0. init
        double initialPlayerHealth = player.getHealth();
        double initialEnemyHealth = enemy.getHealth();
        String enemyString = NameConverter.toSnakeCase(enemy);


        // 1. apply buff provided by the game and player's inventory
        // getting buffing amount
        List<BattleItem> battleItems = new ArrayList<>();
        BattleStatistics playerBuff = new BattleStatistics(0, 0, 0, 1, 1);

        Potion effectivePotion = player.getEffectivePotion();
        if (effectivePotion != null) {
            playerBuff = player.applyBuff(playerBuff);
        } else {
            for (BattleItem item : player.getInventoryEntities(BattleItem.class)) {
                playerBuff = item.applyBuff(playerBuff);
                battleItems.add(item);
            }
        }

        // 2. Battle the two stats, updating the stats as they are battled
        player.setBattleStatistics(BattleStatistics.applyBuff(player.getBattleStatistics(), playerBuff));
        if (!player.isEnabled() || !enemy.isEnabled())
            return;
        List<BattleRound> rounds = BattleStatistics.battle(player.getBattleStatistics(), enemy.getBattleStatistics());
        // Removes temporary buff
        player.setBattleStatistics(BattleStatistics.removeBuff(player.getBattleStatistics(), playerBuff));

        // 3. call to decrease durability of items
        for (BattleItem item : battleItems) {
            if (item instanceof InventoryItem)
                item.use(game);
        }

        // 4. Log the battle - solidate it to be a battle response
        battleResponses.add(new BattleResponse(
                enemyString,
                rounds.stream()
                    .map(ResponseBuilder::getRoundResponse)
                    .collect(Collectors.toList()),
                battleItems.stream()
                        .map(Entity.class::cast)
                        .map(ResponseBuilder::getItemResponse)
                        .collect(Collectors.toList()),
                initialPlayerHealth,
                initialEnemyHealth));
    }

    public void battle(Game game, Player player, Player player2) {
        // 0. init
        double initialPlayerHealth = player.getHealth();
        double initialEnemyHealth = player2.getHealth();
        String enemyString = NameConverter.toSnakeCase(player2);


        // 1. apply buff provided by the game and player's inventory
        // getting buffing amount
        List<BattleItem> battleItems = new ArrayList<>();
        BattleStatistics playerBuff = new BattleStatistics(0, 0, 0, 1, 1);

        Potion effectivePotion = player.getEffectivePotion();
        if (effectivePotion != null) {
            playerBuff = player.applyBuff(playerBuff);
        } else {
            for (BattleItem item : player.getInventoryEntities(BattleItem.class)) {
                playerBuff = item.applyBuff(playerBuff);
                battleItems.add(item);
            }
        }

        // 2. Battle the two stats, updating the stats as they are battled
        player.setBattleStatistics(BattleStatistics.applyBuff(player.getBattleStatistics(), playerBuff));
        if (!player.isEnabled() || !player2.isEnabled())
            return;
        List<BattleRound> rounds = BattleStatistics.battle(player.getBattleStatistics(), player2.getBattleStatistics());
        // Removes temporary buff
        player.setBattleStatistics(BattleStatistics.removeBuff(player.getBattleStatistics(), playerBuff));

        // 3. call to decrease durability of items
        for (BattleItem item : battleItems) {
            if (item instanceof InventoryItem)
                item.use(game);
        }

        // 4. Log the battle - solidate it to be a battle response
        battleResponses.add(new BattleResponse(
                enemyString,
                rounds.stream()
                    .map(ResponseBuilder::getRoundResponse)
                    .collect(Collectors.toList()),
                battleItems.stream()
                        .map(Entity.class::cast)
                        .map(ResponseBuilder::getItemResponse)
                        .collect(Collectors.toList()),
                initialPlayerHealth,
                initialEnemyHealth));
    }

    public List<BattleResponse> getBattleResponses() {
        return battleResponses;
    }

}
