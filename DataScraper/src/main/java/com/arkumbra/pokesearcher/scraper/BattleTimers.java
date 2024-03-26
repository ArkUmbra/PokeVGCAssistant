package com.arkumbra.pokesearcher.scraper;

import java.util.concurrent.TimeUnit;

public class BattleTimers {
  private TimeUnit totalTime;
  private TimeUnit playerTime;
  private TimeUnit previewTime;
  private TimeUnit turnTime;

  public BattleTimers(TimeUnit totalTime, TimeUnit playerTime, TimeUnit previewTime, TimeUnit turnTime) {
    this.totalTime = totalTime;
    this.playerTime = playerTime;
    this.previewTime = previewTime;
    this.turnTime = turnTime;
  }

  public TimeUnit getTotalTime() {
    return totalTime;
  }

  public TimeUnit getPlayerTime() {
    return playerTime;
  }

  public TimeUnit getPreviewTime() {
    return previewTime;
  }

  public TimeUnit getTurnTime() {
    return turnTime;
  }
}
