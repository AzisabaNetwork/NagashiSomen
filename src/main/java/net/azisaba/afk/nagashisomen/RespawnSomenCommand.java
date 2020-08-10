package net.azisaba.afk.nagashisomen;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class RespawnSomenCommand implements CommandExecutor {

    private final NagashiSomen plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.clearSomen();
        new BukkitRunnable() {
            int amount2 = Bukkit.getOnlinePlayers().size();

            public void run() {
                if (!plugin.isSomenSpawned()) {
                    plugin.spawnSomen();
                } else {
                    plugin.spawnAdditionalSomen();
                }
                amount2--;

                if (amount2 <= 0) {
                    cancel();
                    sender.sendMessage(ChatColor.GREEN + "リスポーンしました");
                    return;
                }
            }
        }.runTaskTimer(plugin, 5L, 5L);

        sender.sendMessage(ChatColor.YELLOW + "リスポーンしています...");
        return true;
    }
}
