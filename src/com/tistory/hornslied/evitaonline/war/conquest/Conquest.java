package com.tistory.hornslied.evitaonline.war.conquest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.object.Town;
import com.tistory.hornslied.evitaonline.utils.Resources;
import com.tistory.hornslied.evitaonline.war.AbstractWar;

public abstract class Conquest<T> implements AbstractWar {
	protected Comparator<T> scoreComparator;

	protected Timer counterTimer;

	protected ArrayList<T> participatingObjects;
	protected ConcurrentHashMap<CaptureZone, Integer> areas;
	protected HashMap<T, Integer> scores;

	protected Town ancientCity;

	protected int runningTime;
	protected int waitingTime;

	public Conquest() {
		participatingObjects = new ArrayList<>();
		areas = new ConcurrentHashMap<>();
		scores = new HashMap<>();

		waitingTime = 30;
	}

	class Counter extends TimerTask {
		@Override
		public void run() {
			if (runningTime > 0) {
				for (CaptureZone cz : areas.keySet()) {
					if (cz.isEmpty()) {
						areas.put(cz, 30);
					} else {
						if (areas.get(cz) > 0) {
							areas.put(cz, areas.get(cz) - 1);
						} else {
							scores.put(getObject(cz.getCapper()), scores.get(getObject(cz.getCapper())) + 10);
							Bukkit.broadcastMessage(Resources.tagWar + ChatColor.YELLOW + getObject(cz.getCapper()) + " 득점!");
							participatingObjects.sort(scoreComparator);
							areas.put(cz, 30);
						}
					}
				}

				runningTime--;
			} else {
				stop();
			}
		}
	}

	@Override
	public void init() {
		ancientCity.setAdminEnabledPVP(true);
		counterTimer = new Timer();
		counterTimer.schedule(new Counter(), 1000, 1000);
		initMessage();
	}

	@Override
	public boolean isWaiting() {
		return waitingTime > 0;
	}

	@Override
	public int getRunningTime() {
		return runningTime;
	}

	@Override
	public int getWaitingTime() {
		return waitingTime;
	}

	@Override
	public void setWaitingTime(int time) {
		waitingTime = time;
	}

	abstract protected void initMessage();

	public int getScore(Object t) {
		return scores.get(t);
	}

	public T getByIndex(int i) {
		try {
			return participatingObjects.get(i - 1);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public Set<CaptureZone> getCaptureZones() {
		return areas.keySet();
	}

	public void setTime(CaptureZone cz, int time) {
		areas.put(cz, time);
	}

	public Town getAncientCity() {
		return ancientCity;
	}
	
	abstract public void setScore(Object t, int i);

	abstract public String getName();

	abstract public boolean hasObject(Player player);

	abstract public T getObject(Player player);
}
