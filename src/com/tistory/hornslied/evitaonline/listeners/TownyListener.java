package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.event.PlayerEnterTownEvent;
import com.palmergames.bukkit.towny.event.PlayerLeaveTownEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.palmergames.bukkit.util.Colors;
import com.tistory.hornslied.evitaonline.combat.PvPManager;
import com.tistory.hornslied.evitaonline.utils.Resources;
import com.tistory.hornslied.evitaonline.war.WarManager;

public class TownyListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEnterTown(PlayerEnterTownEvent event) {
		if (event.getPlayerMoveEvent().isCancelled())
			return;

		if (TownySettings.isNotificationUsingTitles()) {
			Player player = event.getPlayer();
			Resident resident;
			try {
				resident = TownyUniverse.getDataSource().getResident(player.getName());
			} catch (NotRegisteredException e1) {
				return;
			}
			Town enteredTown = event.getEnteredtown();

			try {
				if (enteredTown.isAC()) {
					if (WarManager.getInstance().isKoTHObjective(enteredTown)) {
						TownyMessaging.sendTitleMessageToResident(resident,
								Colors.Gold + ChatColor.BOLD + enteredTown.getName(),
								(WarManager.getInstance().isKoTHObjective(enteredTown))
										? ChatColor.BOLD + Colors.Rose + "점령전 중(PvP)"
										: ChatColor.BOLD + Colors.Yellow + "고대 도시");
					} else {
						TownyMessaging.sendTitleMessageToResident(resident,
								Colors.Gold + ChatColor.BOLD + enteredTown.getName(),
								ChatColor.BOLD + Colors.Yellow + "고대 도시");
					}
				} else {
					TownyMessaging.sendTitleMessageToResident(resident,
							Colors.Yellow + ChatColor.BOLD + enteredTown.getName(),
							ChatColor.BOLD + Colors.LightGray + enteredTown.getTownBoard());
				}
			} catch (TownyException e) {
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onLeaveTown(PlayerLeaveTownEvent event) {
		if (event.getPlayerMoveEvent().isCancelled())
			return;

		if (TownySettings.isNotificationUsingTitles()) {
			if(!event.getFrom().getBukkitWorld().equals(event.getTo().getBukkitWorld()))
				return;
			
			try {
				Resident resident = TownyUniverse.getDataSource().getResident(event.getPlayer().getName());
				TownyMessaging.sendTitleMessageToResident(resident, Colors.LightGreen + ChatColor.BOLD + "야생",
						ChatColor.BOLD + Colors.Rose + "PvP, 약탈 가능");
			} catch (TownyException e) {
				return;
			}
			
		}
	}
	

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCombatEnterTown(PlayerEnterTownEvent e) {
		if(e.getPlayerMoveEvent().isCancelled())
			return;
		
		Town enteredTown = e.getEnteredtown();
		
		if(PvPManager.getInstance().isCombatTag(e.getPlayer()) && enteredTown.isAC() && !WarManager.getInstance().isKoTHObjective(enteredTown)) {
			e.getPlayerMoveEvent().setCancelled(true);
			e.getPlayer().sendMessage(Resources.tagCombat + ChatColor.RED + "전투 상태에서 고대 도시에 들어갈 수 없습니다!");
		}
	}
}
