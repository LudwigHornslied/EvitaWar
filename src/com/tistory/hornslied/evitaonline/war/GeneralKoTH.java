package com.tistory.hornslied.evitaonline.war;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.palmergames.bukkit.towny.object.Town;
import com.tistory.hornslied.evitaonline.utils.Resources;

import subside.plugins.koth.gamemodes.RunningKoth;

public class GeneralKoTH extends KoTH {

	public GeneralKoTH(RunningKoth koth, Town ancientcity) {
		super(koth, ancientcity);
	}

	@Override
	public KoTHType getType() {
		return KoTHType.GENERAL;
	}

	@Override
	public String getCapperObjectName() {
		return getCapperName();
	}

	@Override
	protected void notifyAdditionalTime() {
		Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + getCapperName()
		+ " 에게 추가 시간이 주어집니다!");
	}

}
