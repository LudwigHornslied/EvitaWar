package com.tistory.hornslied.evitaonline.war.conquest;

import java.util.Comparator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.utils.Resources;
import com.tistory.hornslied.evitaonline.war.EvitaWarMain;
import com.tistory.hornslied.evitaonline.war.WarManager;

public class ThroneWar extends Conquest<Nation> {

	public ThroneWar() {
		runningTime = 3600;
		
		try {
			ancientCity = TownyUniverse.getDataSource()
					.getTown(EvitaWarMain.getInstance().getConfiguration().getString("regular.thronewar"));
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		
		for(Nation n : TownyUniverse.getDataSource().getNations()) {
			participatingObjects.add(n);
			scores.put(n, 0);
		}

		scoreComparator = new Comparator<Nation>() {
			@Override
			public int compare(Nation o1, Nation o2) {
				if (scores.get(o1) > scores.get(o2)) {
					return -1;
				} else if (scores.get(o1) == scores.get(o2)) {
					return 0;
				} else {
					return 1;
				}
			}
		};

		loadAreas();
	}

	private void loadAreas() {
		ConfigurationSection areaStorage = EvitaWarMain.getInstance().getConquests()
				.getConfigurationSection("thronewar");

		if (areaStorage.getKeys(false) == null)
			return;

		for (String key : areaStorage.getKeys(false)) {
			areas.put(new CaptureZone(key,
					new Location(Bukkit.getWorld(areaStorage.getString(key + ".world")),
							areaStorage.getDouble(key + ".min.x"), areaStorage.getDouble(key + ".min.y"),
							areaStorage.getDouble(key + ".min.z")),
					new Location(Bukkit.getWorld(areaStorage.getString(key + ".world")),
							areaStorage.getDouble(key + ".max.x"), areaStorage.getDouble(key + ".max.y"),
							areaStorage.getDouble(key + ".max.z"))),
					30);
		}
	}

	@Override
	protected void initMessage() {
		Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "고대 도시 " + ancientCity + " 에서 왕위 쟁탈전이 벌어집니다!");
	}

	@Override
	public void notifyWaiting() {
		Bukkit.broadcastMessage(
				Resources.tagWar + ChatColor.GOLD + waitingTime + "초 후 고대 도시 " + ancientCity + " 에서 왕위 쟁탈전이 시작됩니다.");
	}

	@Override
	public void stop() {
		ancientCity.setAdminEnabledPVP(false);
		counterTimer.cancel();
		WarManager.getInstance().setConquest(null);
		
		Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "왕위 쟁탈전이 종료되었습니다.");
		if(participatingObjects.size() > 2) {
			if(scores.get(participatingObjects.get(0)) == scores.get(participatingObjects.get(1))) {
				Bukkit.broadcastMessage(Resources.tagWar + ChatColor.RED + "1등과 2등 국가의 점수가 같기 때문에 무승부 처리됩니다.");
			} else {
				Bukkit.broadcastMessage(Resources.tagWar + ChatColor.AQUA + "우승 국가는 " + participatingObjects.get(0) + " 입니다!");
			}
		}
	}
	
	@Override
	public String getName() {
		return "왕위 쟁탈전";
	}

	@Override
	public Nation getObject(Player player) {
		try {
			return TownyUniverse.getDataSource().getResident(player.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			return null;
		}
	}

	@Override
	public boolean hasObject(Player player) {
		try {
			return TownyUniverse.getDataSource().getResident(player.getName()).hasNation();
		} catch (NotRegisteredException e) {
			return false;
		}
	}

	@Override
	public void setScore(Object t, int i) {
		if(t instanceof Nation)
			scores.put((Nation) t, i);
	}
}
