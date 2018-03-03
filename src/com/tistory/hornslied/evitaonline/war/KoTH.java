package com.tistory.hornslied.evitaonline.war;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.utils.Resources;

import subside.plugins.koth.gamemodes.RunningKoth;
import subside.plugins.koth.gamemodes.RunningKoth.EndReason;

public abstract class KoTH implements AbstractWar {
	
	private int waitingTime;
	private int runningTime;
	private boolean additionalTime;
	private Counter counter;
	private RunningKoth runningKoth;
	private Town ancientCity;

	public KoTH(RunningKoth koth, Town ancientcity) {
		waitingTime = 30;
		runningTime = 3600;
		runningKoth = koth;
		this.ancientCity = ancientcity;

		for (Nation n : TownyUniverse.getDataSource().getNations()) {
			if (!TownyUniverse.getDataSource().hasAC(n))
				participatingNations.add(n);
		}
	}
	
	class Counter extends BukkitRunnable {
		@Override
		public void run() {
			if (runningTime > 0) {
				runningTime--;
			} else if (isBeingCapped()) {
				if (!additionalTime) {
					additionalTime = true;
					notifyAdditionalTime();
					runningTime = -1;
				}
			} else {
				runningKoth.endKoth(EndReason.TIMEUP);
			}
		}
	}
	
	@Override
	public void notifyWaiting() {
		Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + waitingTime + "초 후 고대 도시 " + ancientCity + " 에서 점령전이 시작됩니다.");
	}
	
	@Override
	public void init() {
		counter = new Counter();
		counter.runTaskTimer(EvitaWarMain.getInstance(), 20, 20);
	}

	@Override
	public int getRunningTime() {
		return runningTime;
	}
	
	public void setRunningTime(int sec) {
		runningTime = sec;
	}

	@Override
	public void stop() {
		WarManager.getInstance().setKoth(null);
		counter.cancel();
	}
	
	@Override
	public boolean isWaiting() {
		return waitingTime > 0;
	}
	
	@Override
	public int getWaitingTime() {
		return waitingTime;
	}

	@Override
	public void setWaitingTime(int time) {
		waitingTime = time;
	}
	
	public boolean isAdditionalTime() {
		return additionalTime;
	}

	public boolean isBeingCapped() {
		return runningKoth.getCapper() != null;
	}

	public int getOccupyTime() {
		return runningKoth.getTimeObject().getTotalSecondsLeft();
	}

	public String getTimeLeftFormatted() {
		return runningKoth.getTimeObject().getTimeLeftFormatted();
	}

	public RunningKoth getRunningKoth() {
		return runningKoth;
	}

	public String getCapperName() {
		if (runningKoth.getCapper() == null) {
			return null;
		} else {
			return runningKoth.getCapper().getName();
		}
	}

	public Town getAC() {
		return ancientCity;
	}
	
	abstract public KoTHType getType();
	
	abstract public String getCapperObjectName();
	
	abstract protected void notifyAdditionalTime();
	
	public enum KoTHType {
		ANCIENTCITY,
		GENERAL
	}
}
