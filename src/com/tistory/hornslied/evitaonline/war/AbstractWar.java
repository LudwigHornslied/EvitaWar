package com.tistory.hornslied.evitaonline.war;

import java.util.ArrayList;

import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.object.Nation;

public interface AbstractWar {
	public ArrayList<Nation> participatingNations = new ArrayList<Nation>();
	
	public default void start() {
		notifyWaiting();
		class WaitingTimer extends BukkitRunnable {
			@Override
			public void run() {
				if(getWaitingTime() < 1) {
					init();
					cancel();
				} else {
					setWaitingTime(getWaitingTime() -1);
				}
			}
		}
		
		new WaitingTimer().runTaskTimer(EvitaWarMain.getInstance(), 20, 20);
	}
	
	public default boolean isParticipating(Nation nation) {
		return participatingNations.contains(nation);
	}
	
	abstract public void notifyWaiting();
	abstract public void init();
	abstract public void stop();
	abstract public int getRunningTime();
	abstract public int getWaitingTime();
	abstract public void setWaitingTime(int time);
	abstract public boolean isWaiting();
}
