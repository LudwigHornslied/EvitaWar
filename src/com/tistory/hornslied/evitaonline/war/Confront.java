package com.tistory.hornslied.evitaonline.war;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.palmergames.bukkit.towny.db.TownyDataSource;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownyUniverse;

public class Confront implements ConfigurationSerializable {
		private Nation attacker;
		private Nation defender;
		private ArrayList<Town> fallen;
		private ArrayList<Town> defended;

		public Confront(Nation attacker, Nation defender) {
			this.attacker = attacker;
			this.defender = defender;
			fallen = new ArrayList<>();
			defended = new ArrayList<>();
		}

		public Confront(Nation attacker, Nation defender, List<Town> fallen, List<Town> defended) {
			this.attacker = attacker;
			this.defender = defender;
			fallen = new ArrayList<>(fallen);
			defended = new ArrayList<>(defended);
		}

		public void addFallen(Town town) {
			fallen.add(town);
		}

		public void addDefended(Town town) {
			defended.add(town);
		}

		public boolean isParticipating(Nation nation) {
			return attacker.equals(nation) || defender.equals(nation);
		}
		
		public Nation getAttacker() {
			return attacker;
		}
		
		public Nation getDefender() {
			return defender;
		}

		@Override
		public Map<String, Object> serialize() {
			LinkedHashMap<String, Object> result = new LinkedHashMap<>();
			result.put("attacker", attacker.getName());
			result.put("defender", defender.getName());
			ArrayList<String> fallenNames = new ArrayList<>();
			for (Town t : fallen) {
				fallenNames.add(t.getName());
			}
			result.put("fallen", fallenNames);
			ArrayList<String> defendedNames = new ArrayList<>();
			for (Town t : defended) {
				defendedNames.add(t.getName());
			}
			result.put("defended", defendedNames);
			return result;
		}

		@SuppressWarnings("unchecked")
		public static Confront deserialize(Map<String, Object> args) {
			try {
				TownyDataSource ds = TownyUniverse.getDataSource();
				Nation attacker;
				Nation defender;

				attacker = ds.getNation(args.get("attacker").toString());
				defender = ds.getNation(args.get("defender").toString());

				ArrayList<Town> fallens = new ArrayList<>();
				ArrayList<Town> defendeds = new ArrayList<>();

				for (String s : (List<String>) args.get("fallen")) {
					fallens.add(ds.getTown(s));
				}
				
				for (String s : (List<String>) args.get("defended")) {
					defendeds.add(ds.getTown(s));
				}

				return new Confront(attacker, defender, fallens, defendeds);
			} catch (NotRegisteredException e) {
				e.printStackTrace();
				return null;
			}
		}
}
