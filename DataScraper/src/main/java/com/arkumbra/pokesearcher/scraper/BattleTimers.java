package com.arkumbra.pokesearcher.scraper;

import java.time.Duration;

public class BattleTimers {
  private long totalTime;
  private long playerTime;
  private long previewTime;
  private long turnTime;

  private Duration totalTimeDuration;
  private Duration playerTimeDuration;
  private Duration previewTimeDuration;
  private Duration turnTimeDuration;

  public BattleTimers() {
  }

  public BattleTimers(long totalTime, long playerTime, long previewTime, long turnTime) {
    this.totalTime = totalTime;
    this.playerTime = playerTime;
    this.previewTime = previewTime;
    this.turnTime = turnTime;
    this.totalTimeDuration = Duration.ofSeconds(totalTime);
    this.playerTimeDuration = Duration.ofSeconds(playerTime);
    this.previewTimeDuration = Duration.ofSeconds(previewTime);
    this.turnTimeDuration = Duration.ofSeconds(turnTime);
  }

  public BattleTimers(
      Duration totalTime,
      Duration playerTime,
      Duration previewTime,
      Duration turnTime
  ) {
    this.totalTimeDuration = totalTime;
    this.playerTimeDuration = playerTime;
    this.previewTimeDuration = previewTime;
    this.turnTimeDuration = turnTime;
    if (totalTime != null) this.totalTime = totalTime.toSeconds();
    if (playerTime != null) this.playerTime = playerTime.toSeconds();
    if (previewTime != null) this.previewTime = previewTime.toSeconds();
    if (turnTime != null) this.turnTime = turnTime.toSeconds();
  }

  public long getTotalTime() {
    return totalTime;
  }

  public void setTotalTime(long totalTime) {
    this.totalTime = totalTime;
  }

  public long getPlayerTime() {
    return playerTime;
  }

  public void setPlayerTime(long playerTime) {
    this.playerTime = playerTime;
  }

  public long getPreviewTime() {
    return previewTime;
  }

  public void setPreviewTime(long previewTime) {
    this.previewTime = previewTime;
  }

  public long getTurnTime() {
    return turnTime;
  }

  public void setTurnTime(long turnTime) {
    this.turnTime = turnTime;
  }

  public Duration getTotalTimeDuration() {
    return totalTimeDuration;
  }

  public void setTotalTimeDuration(Duration totalTimeDuration) {
    this.totalTimeDuration = totalTimeDuration;
    if (totalTimeDuration != null) {
      this.totalTime = totalTimeDuration.toSeconds();
    }
  }

  public Duration getPlayerTimeDuration() {
    return playerTimeDuration;
  }

  public void setPlayerTimeDuration(Duration playerTimeDuration) {
    this.playerTimeDuration = playerTimeDuration;
    if (playerTimeDuration != null) {
      this.playerTime = playerTimeDuration.toSeconds();
    }
  }

  public Duration getPreviewTimeDuration() {
    return previewTimeDuration;
  }

  public void setPreviewTimeDuration(Duration previewTimeDuration) {
    this.previewTimeDuration = previewTimeDuration;
    if (previewTimeDuration != null) {
      this.previewTime = previewTimeDuration.toSeconds();
    }
  }

  public Duration getTurnTimeDuration() {
    return turnTimeDuration;
  }

  public void setTurnTimeDuration(Duration turnTimeDuration) {
    this.turnTimeDuration = turnTimeDuration;
    if (turnTimeDuration != null) {
      this.turnTime = turnTimeDuration.toSeconds();
    }
  }
}
