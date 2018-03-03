package com.tistory.hornslied.evitaonline.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.HamiStudios.ArchonCrates.API.Libs.Glow;
import com.HamiStudios.ArchonCrates.API.Libs.ItemBuilder;
import com.HamiStudios.ArchonCrates.API.Objects.Key;
import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.combat.PvPManager;
import com.tistory.hornslied.evitaonline.utils.Resources;
import com.tistory.hornslied.evitaonline.war.ACKoTH;
import com.tistory.hornslied.evitaonline.war.EvitaWarMain;
import com.tistory.hornslied.evitaonline.war.GeneralKoTH;
import com.tistory.hornslied.evitaonline.war.KoTH;
import com.tistory.hornslied.evitaonline.war.KoTH.KoTHType;
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

			if (warManager.isKoTHRunning()) {
				e.setCancelled(true);
				Bukkit.broadcastMessage(Resources.tagWar + ChatColor.RED + town.getName() + " 에서의 점령전이 취소되었습니다.");
				return;
			}

			KoTH kothWar;

			if (e.getKoth().getName()
					.equals(EvitaWarMain.getInstance().getConfiguration().getString("regular.general"))) {
				kothWar = new GeneralKoTH(koth, town);
			} else {
				ds.setACOwner(town, null);
				kothWar = new ACKoTH(koth, town);
			}

			warManager.setKoth(kothWar);
			kothWar.start();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onKoTHCap(KothCapEvent e) {
		WarManager warManager = WarManager.getInstance();
		if (warManager.isKoTHRunning() && warManager.getKoth().getRunningKoth().equals(e.getRunningKoth())) {
			Player player = Bukkit.getPlayer(e.getNextCapper().getName());

			if (warManager.getKoth().getType() == KoTHType.ANCIENTCITY) {
				try {
					if (!warManager.getKoth()
							.isParticipating(ds.getResident(e.getNextCapper().getName()).getTown().getNation())
							|| PvPManager.getInstance().isPvPProt(player)) {
						e.setCancelled(true);
					}
				} catch (NotRegisteredException e1) {
					e.setCancelled(true);
				}
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

			new BukkitRunnable() {
				@Override
				public void run() {
					switch (koth.getType()) {
					case ANCIENTCITY:
						Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + ((ACKoTH) koth).getCapperNation()
								+ " 국가의 " + koth.getCapperName() + " 가 " + koth.getAC() + " 점령을 시도합니다!");
						break;
					case GENERAL:
						Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + koth.getCapperName() + " 가 "
								+ koth.getAC() + " 의 점령지를 점령중입니다!");
						break;
					}
				}
			}.runTaskLater(EvitaWarMain.getInstance(), 1);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void KoTHLeftMessage(KothLeftEvent e) {
		WarManager warManager = WarManager.getInstance();
		if (warManager.isKoTHRunning() && warManager.getKoth().getRunningKoth().equals(e.getRunningKoth()))
			Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GREEN + e.getCapper().getName() + " 가 점령지에서 이탈했습니다!");
	}

	@EventHandler
	public void onKoTHEnd(KothEndEvent e) {
		WarManager warManager = WarManager.getInstance();
		if (warManager.isKoTHRunning() && warManager.getKoth().getRunningKoth().equals(e.getRunningKoth())) {
			{
				KoTH koth = warManager.getKoth();
				switch (e.getReason()) {
				case TIMEUP:
					switch (koth.getType()) {
					case ANCIENTCITY:
						ds.setACOwner(koth.getAC(), null);
						Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "제한시간 초과로 점령전을 종료합니다. 고대 도시 "
								+ koth.getAC().getName() + " 는 아무도 점령하지 못했습니다!");
						break;
					case GENERAL:
						Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "제한시간 초과로 점령전을 종료합니다.");
						break;
					}
					koth.stop();
					break;
				case WON:
					switch (koth.getType()) {
					case ANCIENTCITY:
						ACKoTH acKoth = (ACKoTH) koth;
						ds.setACOwner(koth.getAC(), acKoth.getCapperNation());
						Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "점령전에서 " + acKoth.getCapperNation()
								+ " 국가가 승리했습니다! 고대 도시 " + koth.getAC() + " 의 소유권은 " + acKoth.getCapperNation()
								+ " 국가가 가져갑니다.");
						break;
					case GENERAL:
						Key key = new Key("koth");
						@SuppressWarnings("deprecation") ItemBuilder keyBuilder = new ItemBuilder()
								.setMaterial(Material.getMaterial(key.getItemID()))
								.setName(key.getName())
								.setData((short) key.getItemData())
								.setLore(key.getLore())
								.setAmount(1);
						
						if(key.glow()) {
							keyBuilder.addEnchantment(new Glow(99), 1, true);
						}
						
						Bukkit.getPlayer(koth.getCapperName()).getInventory().addItem(keyBuilder.build());
						Bukkit.broadcastMessage(
								Resources.tagWar + ChatColor.GOLD + "점령전 승자는 " + koth.getCapperName() + " 입니다!");
						break;
					}
					koth.stop();
					break;
				case FORCED:
					Bukkit.broadcastMessage(
							Resources.tagWar + ChatColor.GOLD + "관리자가 " + koth.getAC() + " 에서의 점령전을 중단했습니다.");
					koth.stop();
					break;
				default:
					koth.stop();
					break;
				}
			}
		}
	}
}
