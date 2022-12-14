package net.azisaba.afk.nagashisomen;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class NagashiSomen extends JavaPlugin implements Listener {

    private final List<Item> somenItems = new ArrayList<>();
    private boolean somenSpawned = false;
    private List<SomenData> dataList = new ArrayList<>();
    private BukkitTask task;

    @Override
    public void onEnable() {
        World world = Bukkit.getWorld("afk");
        dataList = Arrays.asList(
                new SomenData(new Location(world, 10.5, 65, -9.5), new Vector(-1, 0, 0).multiply(0.2)),
                new SomenData(new Location(world, -10.5, 65, -9.5), new Vector(0, 0, 1).multiply(0.2)),
                new SomenData(new Location(world, -10.5, 65, 11.5), new Vector(1, 0, 0).multiply(0.2)),
                new SomenData(new Location(world, 10.5, 65, 11.5), new Vector(0, 0, -1).multiply(0.2))
        );

        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getPluginCommand("respawnsomen").setExecutor(new RespawnSomenCommand(this));
        Bukkit.getPluginCommand("spawnadditionalsomen").setExecutor(new SpawnAdditionalSomenCommand(this));

        world.getEntitiesByClass(Item.class).forEach(Entity::remove);

        for (int i = Bukkit.getOnlinePlayers().size(); i > 0; i--) {
            if (!somenSpawned) {
                spawnSomen();
            } else {
                spawnAdditionalSomen();
            }
        }

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        if (somenSpawned) {
            somenSpawned = false;
            for (Item item : somenItems) {
                item.remove();
            }
        }
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    public void spawnSomen() {
        if (somenSpawned) {
            return;
        }

        World world = Bukkit.getWorld("afk");

        for (SomenData data : dataList) {
            Item item = world.dropItem(data.getLocation(), getItemStack());
            somenItems.add(item);
            item.setVelocity(data.getVector());
        }

        task = new ChangeSomenVectorTask(this).runTaskTimer(this, 3, 3);

        somenSpawned = true;
    }

    public void clearSomen() {
        if (!somenSpawned) {
            return;
        }

        somenSpawned = false;
        for (Item somen : somenItems) {
            somen.remove();
        }
        somenItems.clear();

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void spawnAdditionalSomen() {
        if (!somenSpawned) {
            return;
        }

        World world = Bukkit.getWorld("afk");
        for (SomenData data : dataList) {
            Item item = world.dropItem(data.getLocation(), getItemStack());
            somenItems.add(item);
            item.setVelocity(data.getVector());
        }
    }

    private ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.STRING);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("流しそうめん");
        meta.setLore(Collections.singletonList("" + System.nanoTime()));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onItemDespawnEvent(ItemDespawnEvent e) {
        Item item = e.getEntity();
        if (somenSpawned && somenItems.contains(item)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickUp(PlayerPickupItemEvent e) {
        if (somenItems.contains(e.getItem())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!somenSpawned) {
            spawnSomen();
        } else if (Bukkit.getOnlinePlayers().size() <= 40) {
            spawnAdditionalSomen();
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (Bukkit.getOnlinePlayers().size() > 40) {
            return;
        }
        if (Bukkit.getOnlinePlayers().size() <= 1 && somenSpawned) {
            clearSomen();
        } else {
            for (int i = 0; i < 4; i++) {
                if (somenItems.isEmpty()) {
                    break;
                }
                somenItems.get(0).remove();
                somenItems.remove(0);
            }
        }
    }
}
