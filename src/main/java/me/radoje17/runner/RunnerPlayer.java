package me.radoje17.runner;

import me.radoje17.runner.utils.PlayerUtils;
import me.radoje17.runner.utils.UUIDUtils;

public class RunnerPlayer {

    private String uuid;
    private String name;

    public RunnerPlayer(String playerName) {
        this.uuid = UUIDUtils.getUUID(playerName);
        this.name = playerName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void addWin() {
        PlayerUtils.setWins(name, getWins()+1);
    }

    public int getWins() {
        return PlayerUtils.getWins(name);
    }

    public void addLoss() {
        PlayerUtils.setLosses(name, getLoses()+1);
    }

    public int getLoses() {
        return PlayerUtils.getLosses(name);
    }

    public int getGamesPlayed() {
        return getLoses() + getWins();
    }



}
