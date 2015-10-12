package com.demigodsrpg.norsedemigods.saveable;

import com.demigodsrpg.norsedemigods.Saveable;
import com.demigodsrpg.norsedemigods.util.FJsonSection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerDataSaveable implements Saveable {

    // -- DATA -- //

    String MOJANG_ID;
    String LAST_KNOWN_NAME;
    String ALLIANCE;
    Map<String, Double> ACTIVE_EFFECTS;
    Map<String, Integer> DEITIES;
    Map<String, Map<String, Object>> ABILITY_DATA;
    Map<String, String> BIND_DATA;

    double lastLoginTime;
    double lastLogoutTime;

    int favor;
    int maxFavor;
    int ascensions;
    int unclamedDevotion;

    int kills;
    int deaths;

    // -- CONSTRUCTORS -- //

    public PlayerDataSaveable(Player player) {
        MOJANG_ID = player.getUniqueId().toString();
        LAST_KNOWN_NAME = player.getName();
        ALLIANCE = "Human";
        ACTIVE_EFFECTS = new HashMap<>();
        DEITIES = new HashMap<>();
        ABILITY_DATA = new HashMap<>();
        BIND_DATA = new HashMap<>();

        lastLoginTime = System.currentTimeMillis();
        lastLogoutTime = -1;

        favor = -1;
        maxFavor = -1;
        ascensions = -1;
        unclamedDevotion = 0;

        kills = -1;
        deaths = -1;
    }

    @SuppressWarnings("unchecked")
    public PlayerDataSaveable(String mojangId, FJsonSection section) {
        MOJANG_ID = mojangId;
        LAST_KNOWN_NAME = section.getString("lastKnownName");
        ALLIANCE = section.getString("alliance");
        if (section.isSection("activeEffects")) {
            ACTIVE_EFFECTS = (Map) section.getSectionNullable("activeEffects").getValues();
        } else {
            ACTIVE_EFFECTS = new HashMap<>();
        }
        if (section.isSection("deityData")) {
            DEITIES = (Map) section.getSectionNullable("deityData").getValues();
        } else {
            DEITIES = new HashMap<>();
        }
        if (section.isSection("abilityData")) {
            ABILITY_DATA = (Map) section.getSectionNullable("abilityData").getValues();
        } else {
            ABILITY_DATA = new HashMap<>();
        }
        if (section.isSection("bindData")) {
            BIND_DATA = (Map) section.getSectionNullable("bindData").getValues();
        } else {
            BIND_DATA = new HashMap<>();
        }
        lastLoginTime = section.getDouble("lastLoginTime");
        lastLogoutTime = section.getDouble("lastLogoutTime");
        favor = section.getInt("favor");
        maxFavor = section.getInt("maxFavor");
        ascensions = section.getInt("ascensions");
        unclamedDevotion = section.getInt("unclaimedDevotion");
        kills = section.getInt("kills");
        deaths = section.getInt("deaths");
    }

    // -- GETTERS -- //

    public UUID getPlayerId() {
        return UUID.fromString(MOJANG_ID);
    }

    @Override
    public String getKey() {
        return MOJANG_ID;
    }

    public String getLastKnownName() {
        return LAST_KNOWN_NAME;
    }

    public String getAlliance() {
        return ALLIANCE;
    }

    public ImmutableMap<String, Double> getActiveEffects() {
        return ImmutableMap.copyOf(ACTIVE_EFFECTS);
    }

    public ImmutableList<String> getDeityList() {
        return ImmutableList.copyOf(DEITIES.keySet());
    }

    public int getDevotion(String deity) {
        return DEITIES.getOrDefault(deity, -1);
    }

    public Optional<Object> getAbilityData(String ability, String key) {
        return Optional.ofNullable(ABILITY_DATA.getOrDefault(ability, new HashMap<>()).
                getOrDefault(key, null));
    }

    public Optional<Material> getBind(String ability) {
        return Optional.ofNullable(Material.valueOf(BIND_DATA.getOrDefault(ability, "")));
    }

    public List<Material> getBound() {
        return BIND_DATA.values().stream().map(Material::valueOf).distinct().collect(Collectors.toList());
    }

    public double getLastLoginTime() {
        return lastLoginTime;
    }

    public double getLastLogoutTime() {
        return lastLogoutTime;
    }

    public int getFavor() {
        return favor;
    }

    public int getMaxFavor() {
        return maxFavor;
    }

    public int getAscensions() {
        return ascensions;
    }

    public int getUnclaimedDevotion() {
        return unclamedDevotion;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("lastKnownName", LAST_KNOWN_NAME);
        map.put("alliance", ALLIANCE);
        map.put("activeEffects", ACTIVE_EFFECTS);
        map.put("deityData", DEITIES);
        map.put("abilityData", ABILITY_DATA);
        map.put("bindData", BIND_DATA);
        map.put("lastLoginTime", lastLoginTime);
        map.put("lastLogoutTime", lastLogoutTime);
        map.put("favor", favor);
        map.put("maxFavor", maxFavor);
        map.put("ascensions", ascensions);
        map.put("unclaimedDevotion", unclamedDevotion);
        map.put("kills", kills);
        map.put("deaths", deaths);
        return map;
    }

    // -- MUTATORS -- //

    public void setLastKnownName(String name) {
        LAST_KNOWN_NAME = name;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setAlliance(String alliance) {
        ALLIANCE = alliance;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void addEffect(String effect, Long time) {
        if (!ACTIVE_EFFECTS.containsKey(effect)) {
            ACTIVE_EFFECTS.put(effect, time.doubleValue());
        }

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void removeEffect(String effect) {
        ACTIVE_EFFECTS.remove(effect);

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setActiveEffects(Map<String, Double> map) {
        ACTIVE_EFFECTS = map;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void addDeity(String deity) {
        if (!DEITIES.keySet().contains(deity)) {
            DEITIES.put(deity, 0);
        }

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void removeDeity(String deity) {
        DEITIES.remove(deity);

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setDevotion(String deity, int amount) {
        if (DEITIES.containsKey(deity)) {
            DEITIES.put(deity, amount);
        }
    }

    public void setAbilityData(String ability, String key, Object value) {
        // Get the map for the ability, and set the data
        Map<String, Object> abilityMap = ABILITY_DATA.getOrDefault(ability, new HashMap<>());
        abilityMap.put(key, value);
        ABILITY_DATA.put(ability, abilityMap);

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void removeBind(String ability) {
        // Remove the mention of this
        BIND_DATA.remove(ability);

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setBind(String ability, Material type) {
        // Set the bind data tot he map
        BIND_DATA.put(ability, type.name());

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setLastLoginTime(double lastLoginTime) {
        this.lastLoginTime = lastLoginTime;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setLastLogoutTime(double lastLogoutTime) {
        this.lastLogoutTime = lastLogoutTime;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setFavor(int favor) {
        this.favor = favor;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setMaxFavor(int maxFavor) {
        this.maxFavor = maxFavor;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setAscensions(int ascensions) {
        this.ascensions = ascensions;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setUnclamedDevotion(int unclamedDevotion) {
        this.unclamedDevotion = unclamedDevotion;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setKills(int kills) {
        this.kills = kills;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;

        // Put this version of the data object into the registry
        getBackend().getPlayerDataRegistry().put(MOJANG_ID, this);
    }
}