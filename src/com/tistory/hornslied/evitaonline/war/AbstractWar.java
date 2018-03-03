package com.tistory.hornslied.evitaonline.war;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.palmergames.bukkit.towny.object.Nation;

public interface AbstractWar {
	public ArrayList<Nation> participatingNations = new ArrayList<Nation>();
	
	public default void start() {
		notifyWaiting();
		Timer waitingTimer = new Timer();
		
		class WaitingTimer extends TimerTask {
			@Override
			public void run() {
				if(getWaitingTime() < 1) {
					init();
					waitingTimer.cancel();
				} else {
					setWaitingTime(getWaitingTime() -1);
				}
			}
		}
		
		waitingTimer.schedule(new WaitingTimer(), 1000, 1000);
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
