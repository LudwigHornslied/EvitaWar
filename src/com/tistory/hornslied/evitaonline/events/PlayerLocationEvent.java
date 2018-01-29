package com.tistory.hornslied.evitaonline.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLocationEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private Location from;
	private Location to;

	public PlayerLocationEvent(Player player, Location from, Location to) {
		this.player = player;
		this.from = from;
		this.to = to;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}
}
