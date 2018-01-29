package com.tistory.hornslied.evitaonline.war.conquest;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CaptureZone {
	private ArrayList<Player> cappers;
	private Location min;
	private Location max;

	public CaptureZone(Location min, Location max) {
		cappers = new ArrayList<>();
		this.min = min;
		this.max = max;
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean isIn(Player player) {
		return isIn(player.getLocation());
	}
	
	public boolean isIn(Location loc) {
		return min.getBlockX() <= loc.getBlockX() && max.getBlockX() >= loc.getBlockX()
				&& min.getBlockY() <= loc.getBlockY() && max.getBlockY() >= loc.getBlockY()
				&& min.getBlockZ() <= loc.getBlockZ() && max.getBlockZ() >= loc.getBlockZ();
	}

	public Player getCapper() {
		return cappers.get(0);
	}

	public void addCapper(Player player) {
		cappers.add(player);
	}

	public void removeCapper(Player player) {
		cappers.remove(player);
	}
}
