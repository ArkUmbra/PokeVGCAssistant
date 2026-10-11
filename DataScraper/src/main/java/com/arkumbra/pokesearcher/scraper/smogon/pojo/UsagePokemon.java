package com.arkumbra.pokesearcher.scraper.smogon.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsagePokemon implements Comparable<UsagePokemon> {
  @JsonProperty(value = "Raw count")
  private int rawCount;
  @JsonProperty(value = "usage")
  private BigDecimal usageShare;
  private String name;
  @JsonProperty(value = "Moves")
  private Map<String, BigDecimal> moves;
  @JsonProperty(value = "Abilities")
  private Map<String, BigDecimal> abilities;
  @JsonProperty(value = "Items")
  private Map<String, BigDecimal> items;
  @JsonProperty(value = "Teammates")
  private Map<String, BigDecimal> teammates;
  @JsonProperty(value = "Spreads")
  private Map<String, BigDecimal> spreads;

  public UsagePokemon() {
  }

  public UsagePokemon(int rawCount) {
    this.rawCount = rawCount;
  }

  public int getRawCount() {
    return rawCount;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getUsageShare() {
    return usageShare;
  }

  public Map<String, BigDecimal> getItems() {
    return items;
  }

  public Map<String, BigDecimal> getMoves() {
    return moves;
  }

  public Map<String, BigDecimal> getAbilities() {
    return abilities;
  }

  public Map<String, BigDecimal> getTeammates() {
    return teammates;
  }

  public Map<String, BigDecimal> getSpreads() {
    return spreads;
  }

  @Override
  public String toString() {
    StringBuilder itemsSb = new StringBuilder();
    items.forEach((k, v) -> {
      itemsSb.append(System.lineSeparator());
      itemsSb.append(k);
      itemsSb.append(" - ");
      itemsSb.append(v);
    });

    return "UsagePokemon{" +
        "rawCount=" + rawCount +
        ", usageShare=" + usageShare +
        ", name='" + name + '\'' +
//        ", moves='" + moves + '\'' +
        ", abilities='" + abilities + '\'' +
//        ", spreads='" + spreads + '\'' +
//        ", teammates='" + teammates + '\'' +
//        ", items=" + itemsSb.toString() +
        '}';
  }

  @Override
  public int compareTo(UsagePokemon o) {
    return o.usageShare.compareTo(this.usageShare);
  }
}
