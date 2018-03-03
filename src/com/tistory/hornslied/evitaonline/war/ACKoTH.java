package com.tistory.hornslied.evitaonline.war;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.utils.Resources;

import subside.plugins.koth.gamemodes.RunningKoth;

public class ACKoTH extends KoTH {

	public ACKoTH(RunningKoth koth, Town ancientcity) {
		super(koth, ancientcity);
		
	}
	
	@Override
	protected void notifyAdditionalTime() {
		Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + getCapperNation()
		+ " 국가에게 추가 시간이 주어집니다, 고대 도시 " + getAC() + " 를 점령할수 있는 마지막 기회입니다!");
	}
	
	public Nation getCapperNation() {
		try {
			return TownyUniverse.getDataSource().getResident(getCapperName()).getTown().getNation();
		} catch (NotRegisteredException | NullPointerException e) {
			return null;
		}
	}

	@Override
	public String getCapperObjectName() {
		return getCapperNation().getName();
	}

	@Override
	public KoTHType getType() {
		return KoTHType.ANCIENTCITY;
	}
}
