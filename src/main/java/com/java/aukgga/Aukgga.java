package com.java.aukgga;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.ChatPaginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Aukgga extends JavaPlugin implements Listener {
    private final Random random = new Random();

    @Override
    public void onEnable() {
        getLogger().info("Aukgga Plugin Enabled!");

        // 명령어 동적 등록
        PluginCommand cmd = this.getCommand("aukgga");
        if (cmd != null) {
            cmd.setExecutor(new AukggaCommand());
            cmd.setTabCompleter(null); // 자동 완성 비활성화
            cmd.setAliases(Collections.emptyList()); // 별칭 완전 제거
        }

        Bukkit.getPluginManager().registerEvents(this, this);

    }

    @Override
    public void onDisable() {
        getLogger().info("Aukgga Plugin Disabled!");
    }

    public static class AukggaCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.GREEN + "억까크래프트 시작!");
            } else {
                sender.sendMessage("This command can only be used by a player.");
            }
            return true; // 기본 도움말이 출력되지 않도록 true 반환
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isSprinting() && random.nextDouble() < 0.0001) {
            player.sendMessage(ChatColor.DARK_BLUE + "꼬르륵... 아우 배고파.");
            player.setFoodLevel(0);
        }
        if (player.isSprinting() && random.nextDouble() < 0.0001) {
            player.sendMessage(ChatColor.DARK_BLUE +"아야! 다리가 삐였네요!ㅋ");
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 6));
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerMoveEvent event) {
        if (event.getFrom().getY() < event.getTo().getY() && random.nextDouble() < 0.005) {
            Player player = event.getPlayer();
            player.sendMessage(ChatColor.DARK_RED + "아야! 머리가 부딛혔네");
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.getDrops().clear();

        // 플레이어가 하드코어 모드일 때 리스폰을 할 수 있게 설정
        if (player.getWorld().getDifficulty() == org.bukkit.Difficulty.HARD) {
            // 하드코어 모드에서는 죽으면 리스폰이 안 되는데, 이를 수동으로 리스폰하도록 설정
            Bukkit.getScheduler().runTask(this, () -> {
                player.spigot().respawn();
            });
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.RED + "ㅋ!" + player.getName() + "님" + "죽으셨네요? 인벤토리는 제가 가져갑니다ㅋ");
        player.getInventory().clear();

        // 마지막 리스폰 위치로 순간이동
        if (player.getBedSpawnLocation() != null) {
            event.setRespawnLocation(player.getBedSpawnLocation());  // 마지막 침대 위치로 리스폰
        } else {
            // 침대 위치가 없다면 기본 월드의 스폰 위치로 리스폰
            event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
        }
    }


    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        if (random.nextDouble() < 0.1) {
            Player player = event.getPlayer();
            player.sendMessage(ChatColor.AQUA + "아이고 썩은걸 드셨나 보네요ㅠㅠㅋ");
            player.setFoodLevel(Math.max(0, player.getFoodLevel() - 10));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (random.nextDouble() < 0.05) {
            Player player = event.getPlayer();
            player.sendMessage(ChatColor.GREEN + "이런! 블럭은 환상이였나 보네요!ㅋ");
            event.setDropItems(false);
        }
        if (random.nextDouble() < 0.05) { // 5% 확률로 아이템 스왑
            swapInventory(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        if (random.nextDouble() < 0.50) {
            Player player = event.getPlayer();
            player.sendMessage(ChatColor.RED + "이런 잠을 자고 일어났는데 하루가 그냥 지났네요?ㅋㅋ");
            event.getPlayer().getWorld().setTime(13000);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (random.nextDouble() < 0.75) {
                if (!player.getInventory().isEmpty()) {
                    ItemStack[] contents = player.getInventory().getContents();
                    for (int i = 0; i < contents.length; i++) {
                        if (contents[i] != null) {
                            player.getWorld().dropItemNaturally(player.getLocation(), contents[i]);
                            player.sendMessage(ChatColor.RED + "ㅋ 아이템 드랍잼");
                            player.getInventory().setItem(i, null); // 해당 아이템 제거
                        }
                    }
                }
            }
            if (random.nextDouble() < 0.005) { // 5% 확률로 아이템 스왑
                swapInventory(player);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (random.nextDouble() < 0.005) { // 5% 확률로 인벤토리 아이템 스왑
            swapInventory(event.getPlayer());
        }
    }

    private void swapInventory(Player player) {
        List<ItemStack> inventoryContents = new ArrayList<>();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                inventoryContents.add(item);
            }
        }
        Collections.shuffle(inventoryContents);
        player.getInventory().setContents(inventoryContents.toArray(new ItemStack[0]));
        player.sendMessage(ChatColor.YELLOW + "당신의 아이템이 랜덤으로 섞였습니다!");
    }
}
