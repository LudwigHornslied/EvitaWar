package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.utils.Resources;
import com.tistory.hornslied.evitaonline.war.Raid;
import com.tistory.hornslied.evitaonline.war.WarManager;

public class RaidListener implements Listener {

	@EventHandler
	public void onKilled(PlayerDeathEvent e) {
		if (WarManager.getInstance().isRaidRunning()) {
			Entity killer = e.getEntity().getKiller();

			if (killer instanceof Player) {
				Resident killedResident;

				try {
					killedResident = TownyUniverse.getDataSource().getResident(e.getEntity().getName());
				} catch (NotRegisteredException e1) {
					return;
				}

				Town killedTown;

				try {
					killedTown = killedResident.getTown();
				} catch (NotRegisteredException e1) {
					return;
				}

				Resident killerResident;

				try {
					killerResident = TownyUniverse.getDataSource().getResident(killer.getName());
				} catch (NotRegisteredException e1) {
					return;
				}

				Town killerTown;

				try {
					killerTown = killerResident.getTown();
				} catch (NotRegisteredException e1) {
					return;
				}

				try {
					if (!killedTown.equals(killerTown) && !killedTown.getNation().equals(killerTown.getNation())) {
						Raid raid = WarManager.getInstance().getRaid();
						if (raid.isPariticipating(killerTown) && raid.isPariticipating(killedTown))
							raid.setScore(killerTown, raid.getScore(killerTown) + 1);
					} else {
						return;
					}
				} catch (NotRegisteredException e1) {
					Raid raid = WarManager.getInstance().getRaid();
					if (raid.isPariticipating(killerTown) && raid.isPariticipating(killedTown))
						raid.setScore(killerTown, raid.getScore(killerTown) + 1);
				}

				TownyMessaging.sendTownMessage(killerTown,
						Resources.tagWar + ChatColor.YELLOW + killedTown + " 마을의 주민을 죽여 1점을 획득하였습니다.");
			}
		}
	}
}
