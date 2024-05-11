package com.arkumbra.pokesearcher.scraper;

public class BattleTimers {
  private long totalTime;
  private long playerTime;
  private long previewTime;
  private long turnTime;

  public BattleTimers(long totalTime, long playerTime, long previewTime, long turnTime) {
    this.totalTime = totalTime;
    this.playerTime = playerTime;
    this.previewTime = previewTime;
    this.turnTime = turnTime;
  }

  public long getTotalTime() {
    return totalTime;
  }

  public long getPlayerTime() {
    return playerTime;
  }

  public long getPreviewTime() {
    return previewTime;
  }

  public long getTurnTime() {
    return turnTime;
  }
}
