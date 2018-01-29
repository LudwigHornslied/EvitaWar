package com.tistory.hornslied.evitaonline.war.conquest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.object.Town;
import com.tistory.hornslied.evitaonline.war.AbstractWar;
import com.tistory.hornslied.evitaonline.war.EvitaWarMain;

public abstract class Conquest<T> implements AbstractWar {
	protected Comparator<T> scoreComparator;
	
	private Counter counter;
	
	private ArrayList<T> participatingObjects;
	private ConcurrentHashMap<CaptureZone, Integer> areas;
	protected HashMap<T, Integer> scores;
	
	protected Town ancientCity;
	
	public Conquest() {
		scores = new HashMap<>();
	}
	
	class Counter extends BukkitRunnable {
		@Override
		public void run() {
			for(CaptureZone cz : areas.keySet()) {
				if (cz.isEmpty()) {
					areas.put(cz, 30);
				} else {
					if(areas.get(cz) < 1) {
						scores.put(getObject(cz.getCapper()), scores.get(getObject(cz.getCapper())) + 1);
						participatingObjects.sort(scoreComparator);
						areas.put(cz, 30);
					} else {
						areas.put(cz, areas.get(cz) -1);
					}
				}
			}
		}
	}
	
	@Override
	public void init() {
		ancientCity.setAdminEnabledPVP(true);
		counter = new Counter();
		counter.runTaskTimer(EvitaWarMain.getInstance(), 20, 20);
		initMessage();
	}
	
	abstract protected void initMessage();
	
	public int getScore(T t) {
		return scores.get(t);
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
	
	abstract public T getObject(Player player);
}
