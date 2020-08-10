package net.azisaba.afk.nagashisomen;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class ChangeSomenVectorTask extends BukkitRunnable {

    private final NagashiSomen plugin;

    @Override
    public void run() {
        if (plugin.getSomenItems().isEmpty()) {
            return;
        }

        for (Item item : plugin.getSomenItems()) {
            Block b = item.getLocation().getBlock();
            for (SomenData data : plugin.getDataList()) {
                if (data.getLocation().distance(item.getLocation()) <= 1) {
                    item.setVelocity(data.getVector());
                }
            }
        }
    }
}
