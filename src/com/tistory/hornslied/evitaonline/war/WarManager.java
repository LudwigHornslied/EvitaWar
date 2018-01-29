package com.tistory.hornslied.evitaonline.war;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.war.conquest.Conquest;

public class WarManager {
	private volatile static WarManager instance;

	private HashMap<Nation, Confront> confronts;

	private Siege currentSiege = null;
	private KoTH currentKoth = null;
	private Raid currentRaid = null;
	private Conquest<?> currentConquest = null;

	private WarManager() {
		confronts = new HashMap<>();

		loadConfronts();
	}

	public static WarManager getInstance() {
		if (instance == null) {
			synchronized (WarManager.class) {
				if (instance == null) {
					instance = new WarManager();
				}
			}
		}

		return instance;
	}

	private void loadConfronts() {
		FileConfiguration confrontsYml = EvitaWarMain.getInstance().getConfronts();
		if (confrontsYml.getConfigurationSection("confronts") == null) return;
		Set<String> keys = confrontsYml.getConfigurationSection("confronts").getKeys(false);
		
		if (keys != null) {
			for (String s : keys) {
				try {
					confronts.put(TownyUniverse.getDataSource().getNation(s),
							(Confront) confrontsYml.get("confronts." + s));
				} catch (NotRegisteredException e) {
					confrontsYml.set("confronts." + s, null);
				}
			}
		}
	}

	public void saveConfronts() {
		FileConfiguration confrontsYml = EvitaWarMain.getInstance().getConfronts();
		for (Nation n : confronts.keySet()) {
			confrontsYml.set("confronts." + n.getName(), confronts.get(n));
		}

		try {
			confrontsYml.save(new File(EvitaWarMain.getInstance().getDataFolder(), "confronts.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addConfront(Nation attacker, Nation defender) {
		confronts.put(attacker, new Confront(attacker, defender));
		saveConfronts();
	}

	public boolean isConfronting(Nation nation) {
		if (confronts.containsKey(nation))
			return true;

		for (Nation n : confronts.keySet()) {
			if (confronts.get(n).getDefender().equals(nation))
				return true;
		}

		return false;
	}

	public boolean isConfronting(Nation nation1, Nation nation2) {
		if (confronts.containsKey(nation1)) {
			return confronts.get(nation1).getDefender().equals(nation2);
		} else if (confronts.containsKey(nation2)) {
			return confronts.get(nation2).getDefender().equals(nation1);
		} else {
			return false;
		}
	}

	public Confront getConfront(Nation n) {
		return confronts.get(n);
	}

	public Siege getSiege() {
		return currentSiege;
	}

	public KoTH getKoth() {
		return currentKoth;
	}

	public Raid getRaid() {
		return currentRaid;
	}

	public Conquest<?> getConquest() {
		return currentConquest;
	}

	public void setSiege(Siege siege) {
		currentSiege = siege;
	}

	public void setKoth(KoTH koth) {
		currentKoth = koth;
	}

	public void setRaid(Raid raid) {
		currentRaid = raid;
	}

	public void setConquest(Conquest<?> conquest) {
		currentConquest = conquest;
	}

	public boolean isWar() {
		return currentSiege != null || currentKoth != null || currentRaid != null || currentConquest != null;
	}

	public WarType getHighPriorityWar() {
		if (isSiegeRunning()) {
			return WarType.SIEGE;
		} else if (isConquestRunning()) {
			return WarType.CONQUEST;
		} else if (isKoTHRunning()) {
			return WarType.KOTH;
		} else if (isRaidRunning()) {
			return WarType.RAID;
		} else {
			return null;
		}
	}

	public boolean isKoTHWaiting() {
		return currentKoth != null && currentKoth.isWaiting();
	}

	public boolean isConquestWaiting() {
		return currentConquest != null && currentConquest.isWaiting();
	}

	public boolean isSiegeWaiting() {
		return currentSiege != null && currentSiege.isWaiting();
	}

	public boolean isRaidWaiting() {
		return currentRaid != null && currentRaid.isWaiting();
	}

	public boolean isSiegeRunning() {
		return currentSiege != null && !currentSiege.isWaiting();
	}

	public boolean isKoTHRunning() {
		return currentKoth != null && !currentKoth.isWaiting();
	}

	public boolean isRaidRunning() {
		return currentRaid != null && !currentRaid.isWaiting();
	}

	public boolean isConquestRunning() {
		return currentConquest != null && !currentConquest.isWaiting();
	}

	public boolean isKoTHObjective(Town town) {
		return currentKoth != null && currentKoth.getAC().equals(town);
	}

	public enum WarType {
		SIEGE, RAID, KOTH, CONQUEST
	}
}
