package com.tistory.hornslied.evitaonline.commands;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.palmergames.bukkit.towny.TownySettings;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.tistory.hornslied.evitaonline.utils.ChatTools;
import com.tistory.hornslied.evitaonline.utils.Resources;
import com.tistory.hornslied.evitaonline.war.Raid;
import com.tistory.hornslied.evitaonline.war.WarManager;

public class WarCommand implements CommandExecutor {
	private static final List<String> totalWarOutput = new ArrayList<String>();
	private static final List<String> totalWarAdmin = new ArrayList<String>();

	static {
		totalWarOutput.add(ChatTools.formatTitle("/총력전"));
		totalWarOutput.add(ChatTools.formatCommand("", "/총력전", "목록", "대치 중인 국가 목록을 봅니다."));
		totalWarOutput.add(ChatTools.formatCommand("", "/총력전", "정보 <공격국가>", "총력전 상세 정보를 봅니다."));
		totalWarOutput.add(ChatTools.formatCommand("", "/총력전", "공성전", "진행중인 공성전을 봅니다."));
		totalWarOutput.add(ChatTools.formatCommand(TownySettings.getLangString("king_sing"), "/총력전", "공격 <마을이름>",
				"적국의 마을에 침략하여 공성전을 벌입니다."));
		totalWarAdmin.add(ChatTools.formatCommand(TownySettings.getLangString("admin_sing"), "총력전", "생성 <공격국가> <방어국가>",
				"두 국가를 대치 상태로 만듭니다."));
		totalWarAdmin.add(ChatTools.formatCommand(TownySettings.getLangString("admin_sing"), "총력전", "시작 <공격국가> <방어마을>",
				"강제로 공성전을 시작합니다."));
		totalWarAdmin.add(ChatTools.formatCommand(TownySettings.getLangString("admin_sing"), "총력전", "중단",
				"강제로 공성전을 중단합니다."));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("evita.admin")) return true;
		switch (cmd.getLabel()) {
		case "totalwar":
			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
				case "목록":
				case "list":
					break;
				case "정보":
				case "info":
					break;
				case "공성전":
				case "siege":
					break;
				case "공격":
				case "attack":
					break;
				case "생성":
				case "create":
					if(args.length < 3) {
						sender.sendMessage(Resources.tagServer + ChatColor.RED + "명령어 사용 방법: /총력전 생성 <공격국가> <방어국가>");
						break;
					}
					
					Nation attacker;
					try {
						attacker = TownyUniverse.getDataSource().getNation(args[1]);
					} catch(NotRegisteredException e) {
						sender.sendMessage(Resources.tagServer + ChatColor.RED + "공격 국가가 존재하지 않습니다!");
						break;
					}
					
					if(WarManager.getInstance().isConfronting(attacker)) {
						sender.sendMessage(Resources.tagServer + ChatColor.RED + "공격 국가가 이미 대치중입니다!");
						break;
					}
					
					Nation defender;
					
					try {
						defender = TownyUniverse.getDataSource().getNation(args[2]);
					} catch(NotRegisteredException e) {
						sender.sendMessage(Resources.tagServer + ChatColor.RED + "방어 국가가 존재하지 않습니다!");
						break;
					}
					
					if(WarManager.getInstance().isConfronting(defender)) {
						sender.sendMessage(Resources.tagServer + ChatColor.RED + "방어 국가가 이미 대치중입니다!");
						break;
					}
					
					WarManager.getInstance().addConfront(attacker, defender);
					sender.sendMessage(Resources.tagWar + ChatColor.YELLOW + "대치 상태 추가: " + attacker + "(공격), " + defender + "(방어)");
					
					break;
				case "시작":
				case "start":
					break;
				case "중단":
				case "stop":
					break;
				default:
					for (String line : totalWarOutput) {
						sender.sendMessage(line);
					}

					if (sender.hasPermission("evita.mod")) {
						for (String line : totalWarAdmin) {
							sender.sendMessage(line);
						}
					}
					break;
				}
			} else {
				for (String line : totalWarOutput) {
					sender.sendMessage(line);
				}

				if (sender.hasPermission("evita.mod")) {
					for (String line : totalWarAdmin) {
						sender.sendMessage(line);
					}
				}
			}
			break;
		case "raid":
			if (args.length > 0) {
				if (!sender.hasPermission("evita.mod")) {
					break;
				}
				switch(args[0].toLowerCase()) {
				case "시작":
				case "start":
					if(WarManager.getInstance().isRaidRunning()) {
						sender.sendMessage(Resources.tagWar + ChatColor.RED + "이미 레이드가 진행중입니다.");
						break;
					}
					
					Calendar now = Calendar.getInstance();
					
					Raid raid;
					
					if(now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
						raid = new Raid(7200);
					} else {
						raid = new Raid(3600);
					}
					
					Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "관리자가 임의로 레이드를 시작시켰습니다.");
					WarManager.getInstance().setRaid(raid);
					raid.start();
					break;
				case "중단":
				case "stop":
					if(!WarManager.getInstance().isRaidRunning()) {
						sender.sendMessage(Resources.tagWar + ChatColor.RED + "레이드가 진행중이지 않습니다!");
						break;
					}
					
					Bukkit.broadcastMessage(Resources.tagWar + ChatColor.GOLD + "관리자가 강제로 레이드를 중단시켰습니다.");
					WarManager.getInstance().getRaid().stop();
					break;
				default:
				}
			} else {
				
			}
		case "conquest":
			if (args.length > 0) {
				switch (args[0].toLowerCase()) {
				case "start":
				case "area":
					if(args.length < 3) {
						break;
					}
				}
			} else {

			}
		case "koth":
		}
		return false;
	}

}
