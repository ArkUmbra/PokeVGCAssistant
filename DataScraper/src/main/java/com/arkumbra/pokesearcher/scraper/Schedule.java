package com.arkumbra.pokesearcher.scraper;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class Schedule {
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;

  public Schedule(ZonedDateTime startTime, ZonedDateTime endTime) {
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public ZonedDateTime getEndTime() {
    return endTime;
  }
}
