package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.combat.PvPManager;
import com.tistory.hornslied.evitaonline.utils.Resources;
import com.tistory.hornslied.evitaonline.war.KoTH;
import com.tistory.hornslied.evitaonline.war.WarManager;

import subside.plugins.koth.events.KothCapEvent;
import subside.plugins.koth.events.KothEndEvent;
import subside.plugins.koth.events.KothInitializeEvent;
import subside.plugins.koth.events.KothLeftEvent;
import subside.plugins.koth.gamemodes.RunningKoth;

public class KoTHListener implements Listener {
	private TownyDataSource ds;

	public KoTHListener() {
		ds = TownyUniverse.getDataSource();
	}

	@EventHandler
	public void onKoTHInit(KothInitializeEvent e) {
		RunningKoth koth = e.getRunningKoth();
		Town town;
		try {
			town = ds.getTown(e.getKoth().getName());
		} catch (NotRegisteredException e1) {
			return;
		}

		if (town.isAC()) {
			WarManager warManager = WarManager.getInstance();
			ds.setACOwner(town, null);

			if (warManager.isKoTHRunning()) {
				e.setCancelled(true);
				Bukkit.broadcastMessage(Resources.tagWar + ChatColor.RED + town.getName() + " 에서의 점령전이 취소되었습니다.");
				return;
			}

			KoTH kothWar = new KoTH(koth, town);
			warManager.setKoth(kothWar);
			kothWar.start();
			Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + town.getName() + " 에서 점령전이 시작됩니다!");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onKoTHCap(KothCapEvent e) {
		WarManager warManager = WarManager.getInstance();
		if (warManager.isKoTHRunning() && warManager.getKoth().getRunningKoth().equals(e.getRunningKoth())) {
			Player player = Bukkit.getPlayer(e.getNextCapper().getName());
			
			try {
				if (!warManager.getKoth()
						.isParticipating(ds.getResident(e.getNextCapper().getName()).getTown().getNation()) || PvPManager.getInstance().isPvPProt(player)) {
					e.setCancelled(true);
				}
			} catch (NotRegisteredException e1) {
				e.setCancelled(true);
			}
		} else if (warManager.isKoTHWaiting() && warManager.getKoth().getRunningKoth().equals(e.getRunningKoth())) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void KoTHCapMessage(KothCapEvent e) {
		WarManager warManager = WarManager.getInstance();
		if (warManager.isKoTHRunning() && warManager.getKoth().getRunningKoth().equals(e.getRunningKoth())) {
			KoTH koth = warManager.getKoth();
			String name = e.getNextCapper().getName();
			try {
				Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + ds.getResident(name).getTown().getNation()
						+ " 국가의 " + name + " 가 " + koth.getAC() + " 점령을 시도합니다!");
			} catch (NotRegisteredException e1) {
				e1.printStackTrace();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void KoTHLeftMessage(KothLeftEvent e) {
		WarManager warManager = WarManager.getInstance();
		if (warManager.isKoTHRunning() && warManager.getKoth().getRunningKoth().equals(e.getRunningKoth())) {
			Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + e.getCapper().getName() + " 가 점령지에서 이탈했습니다!");
		}
	}

	@EventHandler
	public void onKoTHEnd(KothEndEvent e) {
		WarManager warManager = WarManager.getInstance();
		if (warManager.isKoTHRunning() && warManager.getKoth().getRunningKoth().equals(e.getRunningKoth())) {
			{
				KoTH koth = warManager.getKoth();
				switch (e.getReason()) {
				case TIMEUP:
					ds.setACOwner(koth.getAC(), null);
					Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "제한시간 초과로 점령전을 종료합니다. 고대 도시 "
							+ koth.getAC().getName() + " 는 아무도 점령하지 못했습니다!");
					koth.stop();
					break;
				case WON:
					ds.setACOwner(koth.getAC(), koth.getCapperNation());
					Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "점령전에서 " + koth.getCapperNation()
							+ " 국가가 승리했습니다! 고대 도시 " + koth.getAC() + " 의 소유권은 " + koth.getCapperNation()
							+ " 국가가 가져갑니다.");
					koth.stop();
					break;
				case FORCED:
					Bukkit.broadcastMessage(
							Resources.tagWar + ChatColor.GOLD + "관리자가 " + koth.getAC() + " 에서의 점령전을 중단했습니다.");
					koth.stop();
				default:
					koth.stop();
					break;
				}
			}
		}
	}
}
