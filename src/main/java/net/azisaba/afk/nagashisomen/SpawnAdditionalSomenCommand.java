package net.azisaba.afk.nagashisomen;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class SpawnAdditionalSomenCommand implements CommandExecutor {

    private final NagashiSomen plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int amount = 1;
        if (args.length > 0) {
            try {
                amount = Integer.parseInt(args[0]);

                if (amount <= 0) {
                    sender.sendMessage(ChatColor.RED + "1以上の数を入力してください！");
                    return true;
                }
                if (amount > 30) {
                    sender.sendMessage(ChatColor.RED + "1度に追加できるそうめんの量は30までです！");
                    return true;
                }
            } catch (Exception e) {
                // pass
            }
        }

        final int finalAmount = amount;

        if (amount == 1) {
            plugin.spawnAdditionalSomen();
            sender.sendMessage(ChatColor.GREEN + "追加のそうめんをスポーンしました");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "追加のそうめんをスポーンしています...");
            new BukkitRunnable() {
                int amount2 = finalAmount;

                public void run() {
                    plugin.spawnAdditionalSomen();
                    amount2--;

                    if (amount2 <= 0) {
                        cancel();
                        sender.sendMessage(ChatColor.GREEN + "追加のそうめんをスポーンしました");
                        return;
                    }
                }
            }.runTaskTimer(plugin, 5L, 5L);
        }
        return true;
    }
}
