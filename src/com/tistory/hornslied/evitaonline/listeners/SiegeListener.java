package com.tistory.hornslied.evitaonline.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.event.PlayerChangePlotEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.towny.object.WorldCoord;
import com.tistory.hornslied.evitaonline.utils.Resources;
import com.tistory.hornslied.evitaonline.war.EvitaWarMain;
import com.tistory.hornslied.evitaonline.war.Siege;
import com.tistory.hornslied.evitaonline.war.WarManager;

public class SiegeListener implements Listener {
	private HashMap<OfflinePlayer, Integer> blockCool;

	public SiegeListener() {
		blockCool = new HashMap<>();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkMove(PlayerChangePlotEvent e) {
		WorldCoord from = e.getFrom();
		WorldCoord to = e.getTo();

		WarManager warManager = WarManager.getInstance();

		try {
			if (from.getTownyWorld().isUsingTowny() && to.getTownyWorld().isUsingTowny()
					&& warManager.isSiegeRunning()) {
				Player player = e.getPlayer();
				Nation playerNation;
				Town fromTown;
				Town toTown;
				Siege siege = warManager.getSiege();

				try {
					playerNation = TownyUniverse.getDataSource().getResident(player.getName()).getTown().getNation();
				} catch (NotRegisteredException ex) {
					return;
				}

				if (playerNation.equals(siege.getAttacker())) {
					try {
						fromTown = from.getTownBlock().getTown();
					} catch (NotRegisteredException ex) {
						fromTown = null;
					}

					try {
						toTown = to.getTownBlock().getTown();
					} catch (NotRegisteredException ex) {
						toTown = null;
					}

					if (toTown == null) {
						if (fromTown != null && fromTown.equals(siege.getDefender())) {
							player.removePotionEffect(PotionEffectType.GLOWING);
							siege.setOccupyNumber(siege.getOccupyNumber() - 1);
							
							if (siege.getOccupyNumber() == 0)
								Bukkit.broadcastMessage(
										Resources.tagWar + ChatColor.GREEN + "모든 " + siege.getAttacker().getName() + " 의 군대가 "
												+ siege.getDefender().getName() + " 의 땅에서 쫒겨났습니다!");
						} else {
							return;
						}
					} else if ((fromTown == null
							|| !fromTown.equals(siege.getDefender()) && toTown.equals(siege.getDefender()))) {
						if (siege.getOccupyNumber() == 0)
							Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + siege.getAttacker().getName()
									+ " 국가의 군대가 " + siege.getDefender().getName() + " 점령을 시도합니다!");
						siege.setOccupyNumber(siege.getOccupyNumber() + 1);
						player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1));
					}
				}
			}
		} catch (NotRegisteredException ex) {
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		WarManager warManager = WarManager.getInstance();

		if (warManager.isSiegeRunning()) {
			Player player = e.getPlayer();
			Siege siege = warManager.getSiege();
			try {
				Resident resident = TownyUniverse.getDataSource().getResident(player.getName());

				if (!resident.getTown().getNation().equals(siege.getAttacker()))
					return;
			} catch (NotRegisteredException e1) {
				return;
			}
			WorldCoord loc = WorldCoord.parseWorldCoord(player);

			try {
				if (loc.getTownBlock().getTown().equals(siege.getDefender())) {
					player.removePotionEffect(PotionEffectType.GLOWING);
					siege.setOccupyNumber(siege.getOccupyNumber() - 1);
					if (siege.getOccupyNumber() == 0)
						Bukkit.broadcastMessage(
								Resources.tagWar + ChatColor.GREEN + "모든 " + siege.getAttacker().getName() + " 의 군대가 "
										+ siege.getDefender().getName() + " 의 땅에서 쫒겨났습니다!");
				}
			} catch (NotRegisteredException e1) {
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent e) {
		WarManager warManager = WarManager.getInstance();

		if (warManager.isSiegeRunning()) {
			Player player = e.getPlayer();
			Siege siege = warManager.getSiege();
			Resident resident;
			try {
				resident = TownyUniverse.getDataSource().getResident(player.getName());

				if (!resident.getTown().getNation().equals(siege.getAttacker()))
					return;
			} catch (NotRegisteredException e1) {
				return;
			}
			WorldCoord loc = WorldCoord.parseWorldCoord(player);

			try {
				if (loc.getTownBlock().getTown().equals(siege.getDefender()))
					e.getPlayer().teleport(resident.getTown().getSpawn());
			} catch (TownyException e1) {
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		WarManager warManager = WarManager.getInstance();

		if (warManager.isSiegeRunning()) {
			Player player = e.getPlayer();
			Siege siege = warManager.getSiege();
			Resident resident;
			try {
				resident = TownyUniverse.getDataSource().getResident(player.getName());

				if (!resident.getTown().getNation().equals(siege.getAttacker()))
					return;
			} catch (NotRegisteredException e1) {
				return;
			}
			WorldCoord loc = WorldCoord.parseWorldCoord(e.getBlock());

			try {
				if (loc.getTownBlock().getTown().equals(siege.getDefender())) {
					if (blockCool.containsKey(player)) {
						player.sendMessage(
								Resources.tagWar + ChatColor.RED + "블럭 쿨타임: " + blockCool.get(player) + "초");
						e.setCancelled(true);
					} else {
						e.setCancelled(false);
						blockCool.put(player, 30);
						new BukkitRunnable() {
							@Override
							public void run() {
								if (blockCool.get(player) == 0) {
									blockCool.remove(player);
									cancel();
								} else {
									blockCool.put(player, blockCool.get(player) - 1);
								}
							}
						}.runTaskTimer(EvitaWarMain.getInstance(), 20, 20);
					}
				}
			} catch (TownyException e1) {
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		WarManager warManager = WarManager.getInstance();

		if (warManager.isSiegeRunning()) {
			Player player = e.getPlayer();
			Siege siege = warManager.getSiege();
			Resident resident;
			try {
				resident = TownyUniverse.getDataSource().getResident(player.getName());

				if (!resident.getTown().getNation().equals(siege.getAttacker()))
					return;
			} catch (NotRegisteredException e1) {
				return;
			}
			WorldCoord loc = WorldCoord.parseWorldCoord(e.getBlock());

			try {
				if (loc.getTownBlock().getTown().equals(siege.getDefender())) {
					if (blockCool.containsKey(player)) {
						player.sendMessage(
								Resources.tagWar + ChatColor.RED + "블럭 쿨타임: " + blockCool.get(player) + "초");
						e.setCancelled(true);
					} else {
						e.setCancelled(false);
						blockCool.put(player, 30);
						new BukkitRunnable() {
							@Override
							public void run() {
								if (blockCool.get(player) == 0) {
									blockCool.remove(player);
									cancel();
								} else {
									blockCool.put(player, blockCool.get(player) - 1);
								}
							}
						}.runTaskTimer(EvitaWarMain.getInstance(), 20, 20);
					}
				}
			} catch (TownyException e1) {
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onRespawn(PlayerRespawnEvent e) {
		WarManager warManager = WarManager.getInstance();

		if (warManager.isSiegeRunning()) {
			Player player = e.getPlayer();
			Siege siege = warManager.getSiege();

			try {
				if (TownyUniverse.getDataSource().getResident(player.getName()).getTown().equals(siege.getDefender())) {

				}
			} catch (NotRegisteredException e1) {
				return;
			}
		}
	}
}
