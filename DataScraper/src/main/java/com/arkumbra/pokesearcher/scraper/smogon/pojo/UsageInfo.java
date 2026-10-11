package com.arkumbra.pokesearcher.scraper.smogon.pojo;

public class UsageInfo {
  private final String teamType;
  private final int cutoff;
  private final int cutoffDeviation;
  private final String metagame;
  private final int numberOfBattles;

  public UsageInfo(String teamType, int cutoff, int cutoffDeviation, String metagame, int numberOfBattles) {
    this.teamType = teamType;
    this.cutoff = cutoff;
    this.cutoffDeviation = cutoffDeviation;
    this.metagame = metagame;
    this.numberOfBattles = numberOfBattles;
  }

  public String getTeamType() {
    return teamType;
  }

  public int getCutoff() {
    return cutoff;
  }

  public int getCutoffDeviation() {
    return cutoffDeviation;
  }

  public String getMetagame() {
    return metagame;
  }

  public int getNumberOfBattles() {
    return numberOfBattles;
  }
}
