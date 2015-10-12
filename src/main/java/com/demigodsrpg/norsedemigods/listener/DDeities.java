package com.demigodsrpg.norsedemigods.listener;

import com.demigodsrpg.norsedemigods.DMisc;
import com.demigodsrpg.norsedemigods.Deity;
import com.demigodsrpg.norsedemigods.util.DSave;
import com.demigodsrpg.norsedemigods.util.DSettings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;

import java.util.UUID;
import java.util.logging.Logger;

public class DDeities implements Listener {
    /*
     * Distributes all events to deities
     */
    public DDeities() {
        DMisc.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(DMisc.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (UUID name : DMisc.getFullParticipants()) {
                    Player p = DMisc.getOnlinePlayer(name);
                    if ((p != null) && p.isOnline()) {
                        if (DSettings.getEnabledWorlds().contains(p.getWorld())) {
                            for (Deity d : DMisc.getDeities(p))
                                d.onSyncTick(System.currentTimeMillis());
                        }
                    }
                }
            }
        }, 20, 5);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!DSettings.getEnabledWorlds().contains(e.getBlock().getWorld())) return;
        // Player
        Player p = e.getPlayer();
        if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
            for (Deity d : DMisc.getDeities(p))
                d.onEvent(e);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!DSettings.getEnabledWorlds().contains(e.getBlock().getWorld())) return;
        // Player
        Player p = e.getPlayer();
        if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
            for (Deity d : DMisc.getDeities(p))
                d.onEvent(e);
        }
    }

    public static void onEntityDamage(EntityDamageEvent e) {
        if (!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld())) return;
        for (Player pl : e.getEntity().getWorld().getPlayers()) {
            if (DMisc.isFullParticipant(pl)) {
                if ((DMisc.getDeities(pl) != null) && (DMisc.getDeities(pl).size() > 0)) {
                    for (Deity d : DMisc.getDeities(pl))
                        d.onEvent(e);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld())) return;
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
                for (Deity d : DMisc.getDeities(p))
                    d.onEvent(e);
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if (!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld())) return;
        if (e.isCancelled()) return;
        if (e.getTarget() instanceof Player) {
            Player p = (Player) e.getTarget();
            if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
                for (Deity d : DMisc.getDeities(p))
                    d.onEvent(e);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if (!DSettings.getEnabledWorlds().contains(e.getPlayer().getWorld())) return;
        Player p = e.getPlayer();
        if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
            for (Deity d : DMisc.getDeities(p))
                d.onEvent(e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) { // sync to master file
        final Player p = e.getPlayer();
        if (!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
        if (DSettings.getSettingBoolean("motd")) {
            p.sendMessage("This server is running NorseDemigods v" + ChatColor.YELLOW + DMisc.getPlugin().getDescription().getVersion() + ChatColor.WHITE + ".");
            p.sendMessage(ChatColor.GRAY + "Type " + ChatColor.GREEN + "/dg" + ChatColor.GRAY + " for more info.");
        }

        if (!DSave.hasPlayer(p)) {
            Logger.getLogger("Minecraft").info("[Demigods] " + p.getName() + " joined and no save was detected. Creating new file.");
            DSave.addPlayer(p);
        }
        if (DSave.hasData(p, "CHARGE")) {
            DSave.saveData(p, "CHARGE", System.currentTimeMillis());
            p.sendMessage(ChatColor.YELLOW + "Your charging attack has been reset.");
        }

        DSave.saveData(p, "LASTKNOWNNAME", p.getName());
        DSave.saveData(p, "LASTLOGINTIME", System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        if (!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
        if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
            for (Deity d : DMisc.getDeities(p))
                d.onEvent(e);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (DMisc.isFullParticipant(p)) if (DSave.hasData(p, "ALLIANCECHAT")) {
            if ((Boolean) DSave.getData(p, "ALLIANCECHAT")) {
                e.setCancelled(true);
                Logger.getLogger("Minecraft").info("[" + DMisc.getAllegiance(p) + "] " + p.getName() + ": " + e.getMessage());
                for (Player pl : DMisc.getPlugin().getServer().getOnlinePlayers()) {
                    if (DMisc.isFullParticipant(pl) && DMisc.getAllegiance(pl).equalsIgnoreCase(DMisc.getAllegiance(p)))
                        pl.sendMessage(ChatColor.GREEN + "[" + DMisc.getAscensions(p) + "] " + p.getName() + ": " + e.getMessage());
                }
            }
        }
        if (!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
        if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
            for (Deity d : DMisc.getDeities(p))
                d.onEvent(e);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
        if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
            for (Deity d : DMisc.getDeities(p))
                d.onEvent(e);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
        if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
            for (Deity d : DMisc.getDeities(p))
                d.onEvent(e);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (!DSettings.getEnabledWorlds().contains(p.getWorld())) return;
        if ((DMisc.getDeities(p) != null) && (DMisc.getDeities(p).size() > 0)) {
            for (Deity d : DMisc.getDeities(p))
                d.onEvent(e);
        }
    }
}