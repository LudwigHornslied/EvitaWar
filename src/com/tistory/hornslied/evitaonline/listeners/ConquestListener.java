package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.tistory.hornslied.evitaonline.events.PlayerLocationEvent;
import com.tistory.hornslied.evitaonline.utils.Resources;
import com.tistory.hornslied.evitaonline.war.WarManager;
import com.tistory.hornslied.evitaonline.war.conquest.CaptureZone;
import com.tistory.hornslied.evitaonline.war.conquest.Conquest;

public class ConquestListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent e) {
		if (!e.getFrom().equals(e.getTo())) {
			Bukkit.getPluginManager().callEvent(new PlayerLocationEvent(e.getPlayer(), e.getFrom(), e.getTo()));
		}
	}

	@EventHandler
	public void onLocation(PlayerLocationEvent e) {
		WarManager warManager = WarManager.getInstance();

		if (warManager.isConquestRunning()) {
			Conquest<?> conquest = warManager.getConquest();
			Player player = e.getPlayer();
			for (CaptureZone cz : conquest.getCaptureZones()) {
				if (cz.isIn(e.getFrom())) {
					if (!cz.isIn(e.getTo())) {
						if (cz.getCapper().equals(player)) {
							Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + conquest.getObject(player)
									+ " 가 점령지에서 이탈했습니다!");
							conquest.setTime(cz, 30);
						}
						cz.removeCapper(player);
					}
				} else if (cz.isIn(e.getTo())) {
					if (conquest.hasObject(player)) {
						if (cz.isEmpty()) {
							Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + "점령지 " + cz.getName() + " 를 "
									+ conquest.getObject(player) + " 가 점령하고 있습니다.");
						}

						cz.addCapper(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		WarManager warManager = WarManager.getInstance();
		Player player = e.getEntity();

		if (warManager.isConquestRunning()) {
			Conquest<?> conquest = warManager.getConquest();

			if (conquest.hasObject(player) && conquest.getScore(conquest.getObject(player)) > 4)
				conquest.setScore(conquest.getObject(player), conquest.getScore(conquest.getObject(player)) -5);
			
			for(CaptureZone cz : conquest.getCaptureZones()) {
				if (cz.getCapper().equals(player)) {
					Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + conquest.getObject(player)
							+ " 가 점령지에서 이탈했습니다!");
					conquest.setTime(cz, 30);
				}
				cz.removeCapper(player);
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		WarManager warManager = WarManager.getInstance();
		Player player = e.getPlayer();

		if (warManager.isConquestRunning()) {
			Conquest<?> conquest = warManager.getConquest();
			for(CaptureZone cz : conquest.getCaptureZones()) {
				if(cz.hasCapper(player)) {
					if (cz.getCapper().equals(player)) {
						Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + conquest.getObject(player)
								+ " 가 점령지에서 이탈했습니다!");
						conquest.setTime(cz, 30);
					}
					cz.removeCapper(player);
				}
			}
		}
	}
}
