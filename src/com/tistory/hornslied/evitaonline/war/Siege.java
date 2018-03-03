package com.tistory.hornslied.evitaonline.war;

import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.palmergames.bukkit.towny.TownyMessaging;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.tistory.hornslied.evitaonline.utils.Resources;

public class Siege implements AbstractWar {

	private Nation attacker;
	private Town defender;

	private int waitingTime;
	private int runningTime;
	private int occupyTime;
	private int occupyNumber;
	private boolean additionalTime;

	private Timer counterTimer;

	public Siege(Nation attacker, Town defender) {

		this.attacker = attacker;
		this.defender = defender;
		participatingNations.add(attacker);
		try {
			participatingNations.add(defender.getNation());
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
		waitingTime = 900;
		runningTime = 3600;
		occupyTime = 600;
		occupyNumber = 0;
		additionalTime = false;
	}

	class Counter extends TimerTask {

		@Override
		public void run() {
			if (runningTime > 0) {
				if (occupyNumber > 0) {
					runningTime--;
					occupyTime--;

					switch (runningTime) {
					case 1800:
						notifyRunningTime("30분");
						break;
					case 1200:
						notifyRunningTime("20분");
						break;
					case 600:
						notifyRunningTime("10분");
						break;
					case 300:
						notifyRunningTime("5분");
						break;
					case 60:
						notifyRunningTime("1분");
						break;
					case 30:
						notifyRunningTime("30초");
						break;
					}

					switch (occupyTime) {
					case 900:
						notifyOccupyTime("15분");
						break;
					case 600:
						notifyOccupyTime("10분");
						break;
					case 300:
						notifyOccupyTime("5분");
						break;
					case 60:
						notifyOccupyTime("1분");
						break;
					case 30:
						notifyOccupyTime("30초");
						break;
					case 5:
						notifyOccupyTime("5초");
						break;
					case 4:
						notifyOccupyTime("4초");
						break;
					case 3:
						notifyOccupyTime("3초");
						break;
					case 2:
						notifyOccupyTime("2초");
						break;
					case 1:
						notifyOccupyTime("1초");
						break;
					}

					if (occupyTime == 0)
						endWar(true);
				} else {
					runningTime--;
					setOccupyTime(1200);
				}
			} else {
				if (occupyNumber > 0) {
					if (!additionalTime) {
						additionalTime = true;
						Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + attacker.getName()
								+ " 국가에게 추가 시간이 주어집니다, " + defender.getName() + " 마을을 점령할수 있는 마지막 기회입니다!");
						runningTime = -1;
					}

					occupyTime--;

					switch (occupyTime) {
					case 900:
					case 600:
					case 300:
					case 60:
						notifyOccupyTime(occupyTime/60 + "분");
						break;
					case 30:
					case 5:
					case 4:
					case 3:
					case 2:
					case 1:
						notifyOccupyTime(occupyTime + "초");
						break;
					}

					if (occupyTime == 0)
						endWar(true);
				} else {
					endWar(false);
				}
			}
		}
	}

	@Override
	public void notifyWaiting() {
		try {
			Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + waitingTime / 60 + "분 후 " + attacker + " 국가가 "
					+ defender.getNation() + " 국가의 " + defender + " 마을을 공격합니다.");
		} catch (NotRegisteredException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		setPvP(true);

		counterTimer = new Timer();
		counterTimer.schedule(new Counter(), 1000, 1000);
	}

	@Override
	public void stop() {
		setPvP(false);

		WarManager.getInstance().setSiege(null);
		counterTimer.cancel();
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
	
	public boolean isAdditionalTime() {
		return additionalTime;
	}

	public Nation getAttacker() {
		return attacker;
	}

	public Town getDefender() {
		return defender;
	}

	public void endWar(boolean isWin) {
		if (isWin) {

		} else {

		}

		stop();
	}

	public void setOccupyTime(int sec) {
		occupyTime = sec;
	}

	public int getOccupyTime() {
		return occupyTime;
	}

	public synchronized void setOccupyNumber(int number) {
		occupyNumber = number;
	}

	public int getOccupyNumber() {
		return occupyNumber;
	}

	private void setPvP(boolean isPvP) {
		for (Nation n : participatingNations) {
			for (Town t : n.getTowns()) {
				t.setAdminEnabledPVP(isPvP);
			}
		}
	}

	private void notifyRunningTime(String time) {
		for (Nation n : participatingNations) {
			TownyMessaging.sendNationMessage(n, Resources.tagWar + ChatColor.YELLOW + "남은 전쟁 시간: " + time);
		}
	}

	private void notifyOccupyTime(String time) {
		for (Nation n : participatingNations) {
			TownyMessaging.sendNationMessage(n,
					Resources.tagWar + ChatColor.RED + defender.getName() + " 마을 점령 시간: " + time);
		}
	}
}
