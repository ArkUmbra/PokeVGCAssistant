package com.arkumbra.pokesearcher.scraper.smogon.pojo;

public class Usage {
  private final UsageInfo info;
  private final UsageData data;

  public Usage(UsageInfo info, UsageData data) {
    this.info = info;
    this.data = data;
  }

  public UsageInfo getInfo() {
    return info;
  }

  public UsageData getData() {
    return data;
  }
}
