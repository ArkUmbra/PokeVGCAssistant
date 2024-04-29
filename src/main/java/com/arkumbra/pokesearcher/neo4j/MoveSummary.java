package com.arkumbra.pokesearcher.neo4j;

import com.arkumbra.pokesearcher.scraper.AttackCategory;
import com.arkumbra.pokesearcher.scraper.Name;
import com.arkumbra.pokesearcher.scraper.TypeValue;

public class MoveSummary {

  private final String idName;
  private final Name name;
  private final TypeValue type;
  private final AttackCategory category;
  private final int power;
  private final int accuracy;
  private final String ingameDescription;

  public MoveSummary(
      String idName, Name name, TypeValue type,
      AttackCategory category, int power, int accuracy,
      String ingameDescription) {

    this.idName = idName;
    this.name = name;
    this.type = type;
    this.category = category;
    this.power = power;
    this.accuracy = accuracy;
    this.ingameDescription = ingameDescription;
  }

  public String getIdName() {
    return idName;
  }

  public Name getName() {
    return name;
  }

  public TypeValue getType() {
    return type;
  }

  public AttackCategory getCategory() {
    return category;
  }

  public int getPower() {
    return power;
  }

  public int getAccuracy() {
    return accuracy;
  }

  public String getIngameDescription() {
    return ingameDescription;
  }
}
