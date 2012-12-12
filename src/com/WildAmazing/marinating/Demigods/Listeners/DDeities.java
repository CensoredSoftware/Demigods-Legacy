package com.WildAmazing.marinating.Demigods.Listeners;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.WildAmazing.marinating.Demigods.DSave;
import com.WildAmazing.marinating.Demigods.DSettings;
import com.WildAmazing.marinating.Demigods.DUtil;
import com.WildAmazing.marinating.Demigods.Deities.Deity;
import com.clashnia.Demigods.Deities.Giants.Typhon;

public class DDeities implements Listener
{

	public DDeities() {
		DUtil.getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(DUtil.getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (String name : DUtil.getFullParticipants()) {
					Player p = DUtil.getOnlinePlayer(name);
					if ((p != null) && p.isOnline()) {
						if (DSettings.getEnabledWorlds().contains(p.getWorld())) {
							for (Deity d : DUtil.getDeities(p))
								d.onTick(System.currentTimeMillis());
						}
					}
				}
			}
		}, 20, 5);
	}
	
	@EventHandler
	public static void onBlockBreak(BlockBreakEvent e) {
		if (!DSettings.getEnabledWorlds().contains(e.getBlock().getWorld()))
			return;
		//Player
		Player p = e.getPlayer();
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
	
	@EventHandler
	public static void onBlockPlace(BlockPlaceEvent e) {
		if (!DSettings.getEnabledWorlds().contains(e.getBlock().getWorld()))
			return;
		//Player
		Player p = e.getPlayer();
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
	
	@EventHandler
	public static void onEntityDamage(EntityDamageEvent e){
		if (!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld()))
			return;
		for (Player pl : e.getEntity().getWorld().getPlayers()) {
			if (DUtil.isFullParticipant(pl)) {
				if ((DUtil.getDeities(pl)!=null) && (DUtil.getDeities(pl).size()>0)) {
					for (Deity d : DUtil.getDeities(pl))
						d.onEvent(e);
				}
			}
		}
	}
	
	@EventHandler
	public static void onEntityDamageByEntity(EntityDamageByEntityEvent e){
		if (!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld()))
			return;
		
		// Deity specific needs
		Typhon.onEntityDamageByEntity(e);
		
		for (Player pl : e.getEntity().getWorld().getPlayers()) {
			if (DUtil.isFullParticipant(pl)) {
				if ((DUtil.getDeities(pl)!=null) && (DUtil.getDeities(pl).size()>0)) {
					for (Deity d : DUtil.getDeities(pl))
						d.onEvent(e);
				}
			}
		}
	}

	@EventHandler
	public static void onEntityDeath(EntityDeathEvent e){
		if (!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld()))
			return;
		if (e.getEntity() instanceof Player) {
			Player p = (Player)e.getEntity();
			
			// Deity specific needs
			Typhon.onEntityDeath(e);
			
			if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
				for (Deity d : DUtil.getDeities(p))
					d.onEvent(e);
			}
		}
	}

	@EventHandler
	public static void onEntityTarget(EntityTargetEvent e){
		if (!DSettings.getEnabledWorlds().contains(e.getEntity().getWorld()))
			return;
		if (e.isCancelled())
			return;
		if (e.getTarget() instanceof Player) {
			Player p = (Player)e.getTarget();
			if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
				for (Deity d : DUtil.getDeities(p))
					d.onEvent(e);
			}
		}
	}

	@EventHandler
	public static void onPlayerJoin(PlayerJoinEvent e){ //sync to master file
		final Player p = e.getPlayer();
		if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if (DSettings.getSettingBoolean("motd")) {
			p.sendMessage("This server is running Demigods v"+ChatColor.YELLOW+DUtil.getPlugin().getDescription().getVersion()+ChatColor.WHITE+".");
			p.sendMessage(ChatColor.GRAY+"Type "+ChatColor.GREEN+"/dg"+ChatColor.GRAY+" for more info.");
			/*if ((!DSettings.getSettingBoolean("update")) && (DemigodsUpdate.shouldUpdate()) && DUtil.hasPermissionOrOP(p, "demigods.admin")) {
				p.sendMessage(ChatColor.RED + "There is a new, stable release for Infractions.");
				p.sendMessage(ChatColor.RED + "Please update ASAP.");
				p.sendMessage(ChatColor.GREEN + "Latest: http://clashnia.com/plugins/demigods/Demigods.jar");
			}*/
		}
		if (!DSave.hasPlayer(p)) {
			Logger.getLogger("Minecraft").info("[Demigods] "+p.getName()+" joined and no save was detected. Creating new file.");
			DSave.addPlayer(p);
		}
		DSave.saveData(p, "LASTLOGINTIME", System.currentTimeMillis());
	}

	@EventHandler
	public static void onPlayerPickupItem(PlayerPickupItemEvent e){
		Player p = e.getPlayer();
		if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public static void onPlayerChat(AsyncPlayerChatEvent e){
		Player p = e.getPlayer();
		if (DUtil.isFullParticipant(p))
			if (DSave.hasData(p, "ALLIANCECHAT")) {
				if ((Boolean)DSave.getData(p, "ALLIANCECHAT")) {
					e.setCancelled(true);
					Logger.getLogger("Minecraft").info("["+DUtil.getAllegiance(p)+"] "+p.getName()+": "+e.getMessage());
					for (Player pl : DUtil.getPlugin().getServer().getOnlinePlayers()) {
						if (DUtil.isFullParticipant(pl) && DUtil.getAllegiance(pl).equalsIgnoreCase(DUtil.getAllegiance(p)))
							pl.sendMessage(ChatColor.GREEN+"["+DUtil.getAscensions(p)+"] "+p.getName()+": "+e.getMessage());
					}
				}
			}
		if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public static void onPlayerInteract(PlayerInteractEvent e){
		Player p = e.getPlayer();
		if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public static void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}

	@EventHandler
	public static void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (!DSettings.getEnabledWorlds().contains(p.getWorld()))
			return;
		if ((DUtil.getDeities(p)!=null) && (DUtil.getDeities(p).size()>0)) {
			for (Deity d : DUtil.getDeities(p))
				d.onEvent(e);
		}
	}
}
