package com.tistory.hornslied.evitaonline.war.timer;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.war.EvitaWarMain;
import com.tistory.hornslied.evitaonline.war.Raid;
import com.tistory.hornslied.evitaonline.war.WarManager;

import subside.plugins.koth.KothPlugin;
import subside.plugins.koth.exceptions.KothException;
import subside.plugins.koth.gamemodes.StartParams;
import subside.plugins.koth.modules.KothHandler;

public class WarTimerHandler {
	private volatile static WarTimerHandler instance;

	private WarTimerHandler() {
		FileConfiguration config = EvitaWarMain.getInstance().getConfiguration();
		if (config.getBoolean("raidon"))
			initRaidTimer();
		if (config.getBoolean("regular.on"))
			initRegularTimer();
	}

	public static WarTimerHandler getInstance() {
		if (instance == null) {
			synchronized (WarTimerHandler.class) {
				if (instance == null) {
					instance = new WarTimerHandler();
				}
			}
		}

		return instance;
	}

	class RegularTimerTask extends TimerTask {
		@Override
		public void run() {
			FileConfiguration config = EvitaWarMain.getInstance().getConfiguration();
			List<String> acList = config.getStringList("regular.schedule");
			int index = config.getInt("regular.index");

			try {
				Town town = TownyUniverse.getDataSource().getTown(acList.get(index));

				if (town.getName().equals(config.get("regular.thronewar"))) {

				} else if (town.getName().equals(config.get("regular.holywar"))) {

				} else {
					KothHandler kh = ((KothPlugin) Bukkit.getPluginManager().getPlugin("KoTH")).getKothHandler();
					StartParams sp = new StartParams(kh, town.getName());
					sp.setCaptureTime(600);

					try {
						kh.startKoth(sp);
					} catch (KothException e) {
						e.printStackTrace();
					}
				}

				if (index++ >= acList.size()) {
					config.set("regular.index", 0);
				} else {
					config.set("regular.index", index++);
				}

				try {
					config.save(new File(EvitaWarMain.getInstance().getDataFolder(), "config.yml"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (NotRegisteredException e) {
				e.printStackTrace();
			}
		}
	}

	private void initRaidTimer() {
		Timer raidTimer = new Timer();
		Calendar now = Calendar.getInstance();
		int runningTime;

		if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			if (now.get(Calendar.HOUR_OF_DAY) >= 14)
				now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
		} else {
			if (now.get(Calendar.HOUR_OF_DAY) >= 19)
				now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
		}

		if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			now.set(Calendar.HOUR_OF_DAY, 14);
			runningTime = 7200;
		} else {
			now.set(Calendar.HOUR_OF_DAY, 19);
			runningTime = 3600;
		}

		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);

		raidTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				Raid raid = new Raid(runningTime);
				WarManager.getInstance().setRaid(raid);
				raid.start();
				initRaidTimer();
			}

		}, now.getTime());
	}

	private void initRegularTimer() {
		Timer regularTimer = new Timer();
		Calendar now = Calendar.getInstance();

		if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			if (now.get(Calendar.HOUR_OF_DAY) >= 17)
				now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
		} else {
			if (now.get(Calendar.HOUR_OF_DAY) >= 21)
				now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
		}

		if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			now.set(Calendar.HOUR_OF_DAY, 17);
		} else {
			now.set(Calendar.HOUR_OF_DAY, 21);
		}

		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);

		regularTimer.schedule(new RegularTimerTask(), now.getTime());
	}
}
