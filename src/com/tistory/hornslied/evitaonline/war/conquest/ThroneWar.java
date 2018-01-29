package com.tistory.hornslied.evitaonline.war.conquest;

import java.util.Comparator;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.war.EvitaWarMain;

public class ThroneWar extends Conquest<Nation> {
	
	public ThroneWar() {
		try {
			ancientCity = TownyUniverse.getDataSource().getTown(EvitaWarMain.getInstance().getConfiguration().getString("regular.thronewar"));
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		
		scoreComparator = new Comparator<Nation>() {
			@Override
			public int compare(Nation o1, Nation o2) {
				if(scores.get(o1) > scores.get(o2)) {
					return 1;
				} else if(scores.get(o1) == scores.get(o2)) {
					return 0;
				} else {
					return -1;
				}
			}
		};
	}
	
	@Override
	protected void initMessage() {
		
	}


	@Override
	public void notifyWaiting() {
		
	}
	
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRunningTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWaitingTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setWaitingTime(int time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isWaiting() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Nation getObject(Player player) {
		try {
			return TownyUniverse.getDataSource().getResident(player.getName()).getTown().getNation();
		} catch (NotRegisteredException e) {
			return null;
		}
	}
}
