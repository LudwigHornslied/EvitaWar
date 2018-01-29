package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.tistory.hornslied.evitaonline.events.PlayerLocationEvent;
import com.tistory.hornslied.evitaonline.war.WarManager;
import com.tistory.hornslied.evitaonline.war.conquest.CaptureZone;
import com.tistory.hornslied.evitaonline.war.conquest.Conquest;

public class ConquestListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent e) {
		if(!e.getFrom().equals(e.getTo())) {
			Bukkit.getPluginManager().callEvent(new PlayerLocationEvent(e.getPlayer(), e.getFrom(), e.getTo()));
		}
	}
	
	@EventHandler
	public void onLocation(PlayerLocationEvent e) {
		WarManager warManager = WarManager.getInstance();
		
		if(warManager.isConquestRunning()) {
			Conquest<?> conquest = warManager.getConquest();
			Player player = e.getPlayer();
			for(CaptureZone cz : conquest.getCaptureZones()) {
				if(cz.isIn(e.getFrom())) {
					if(!cz.isIn(e.getTo())) {
						if(cz.getCapper().equals(player)) conquest.setTime(cz, 30);
						cz.removeCapper(player);
					}
				} else if(cz.isIn(e.getTo())) {
					cz.addCapper(player);
				}
			}
		}
	}
}
