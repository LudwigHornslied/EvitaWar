package com.tistory.hornslied.evitaonline.war;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.utils.Resources;

public class Raid implements AbstractWar, Listener {
	private BossBar bossBar;

	private ArrayList<Town> participatingTowns;
	private HashMap<Town, Integer> scores;
	private Comparator<Town> scoreComparator;

	private final int totalTime;
	private int waitingTime;
	private int runningTime;

	private Timer counterTimer;

	public Raid(int runningTime) {
		participatingTowns = new ArrayList<Town>();
		for(Town t : TownyUniverse.getDataSource().getTowns())
			participatingTowns.add(t);
		scores = new HashMap<>();
		for(Town t : participatingTowns)
			scores.put(t, 0);
		scoreComparator = new Comparator<Town>() {

			@Override
			public int compare(Town o1, Town o2) {
				if (scores.get(o1) > scores.get(o2)) {
					return 1;
				} else if (scores.get(o1) == scores.get(o2)) {
					return 0;
				} else {
					return -1;
				}
			}
		};
		waitingTime = 30;
		this.runningTime = runningTime;
		totalTime = runningTime;

		Bukkit.getPluginManager().registerEvents(this, EvitaWarMain.getInstance());
	}

	class Counter extends TimerTask {

		@Override
		public void run() {
			if (runningTime > 0) {
				runningTime--;

				switch (runningTime) {
				case 1800:
				case 1200:
				case 600:
				case 300:
				case 60:
					notifyRunningTime(runningTime / 60 + "분");
					break;
				case 30:
					notifyRunningTime("30초");
					break;
				}
				bossBar.setTitle(ChatColor.YELLOW + ChatColor.BOLD.toString() + "레이드: " + runningTime / 60 + "분 "
						+ runningTime % 60 + "초");
				bossBar.setProgress((double) runningTime / totalTime);
			} else {
				stop();
			}
		}
	}

	@Override
	public void notifyWaiting() {
		Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + waitingTime + "초 후 레이드가 시작됩니다.");
	}

	@Override
	public void init() {
		bossBar = Bukkit.createBossBar(
				ChatColor.YELLOW + ChatColor.BOLD.toString() + "레이드: " + totalTime / 60 + "분 " + totalTime % 60 + "초",
				BarColor.BLUE, BarStyle.SOLID);

		for (Player p : Bukkit.getOnlinePlayers()) {
			bossBar.addPlayer(p);
		}

		bossBar.setVisible(true);
		setPvP(true);
		counterTimer = new Timer();
		counterTimer.schedule(new Counter(), 1000, 1000);

		Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "레이드가 시작됩니다! 이제부터 모든 마을에서 PvP가 가능하니 주의하세요.");
	}

	@Override
	public void stop() {
		bossBar.setVisible(false);
		counterTimer.cancel();
		setPvP(false);
		WarManager.getInstance().setRaid(null);
		Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "레이드가 끝났습니다! 이제부터 마을에 PvP 보호가 적용됩니다.");
		try {
			if (scores.get(participatingTowns.get(0)) == scores.get(participatingTowns.get(1))) {
				Bukkit.broadcastMessage(Resources.tagWar + ChatColor.RED + "1등과 2등 마을의 점수가 같기 때문에 레이드 보상이 지급되지 않습니다.");
			} else {
				Bukkit.broadcastMessage(
						Resources.tagWar + ChatColor.AQUA + participatingTowns.get(0) + " 마을이 1등으로 레이드 보상을 가져갑니다.");
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		PlayerJoinEvent.getHandlerList().unregister(this);
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

	@Override
	public boolean isWaiting() {
		return waitingTime > 0;
	}

	public int getScore(Town town) {
		return scores.get(town);
	}

	public void setScore(Town town, int score) {
		scores.put(town, score);
		participatingTowns.sort(scoreComparator);
	}
	
	public boolean isPariticipating(Town town) {
		return participatingTowns.contains(town);
	}

	private void setPvP(boolean isPvP) {
		WarManager warManager = WarManager.getInstance();
		for (Town t : participatingTowns) {
			try {
				if (!t.isAC() && (!warManager.isSiegeRunning() || !t.hasNation()
						|| !warManager.getSiege().isParticipating(t.getNation())))
					t.setAdminEnabledPVP(isPvP);
			} catch (NotRegisteredException e) {
				e.printStackTrace();
			}
		}
	}

	private void notifyRunningTime(String time) {
		Bukkit.broadcastMessage(Resources.tagWar + ChatColor.YELLOW + "레이드 종료 시간: " + time);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		bossBar.addPlayer(e.getPlayer());
	}
}
