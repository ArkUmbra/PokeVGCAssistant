package com.arkumbra.pokesearcher.scraper;

import java.util.Set;

public class Ability {
  private String idName;
  private Name name;
  private String ingameDescription;
  private Set<AbilityMetaData> metaData;

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public String getIngameDescription() {
    return ingameDescription;
  }

  public void setIngameDescription(String ingameDescription) {
    this.ingameDescription = ingameDescription;
  }

  public Set<AbilityMetaData> getMetaData() {
    return metaData;
  }

  public void setMetaData(Set<AbilityMetaData> metaData) {
    this.metaData = metaData;
  }

  @Override
  public String toString() {
    return "Ability{" +
        "idName='" + idName + '\'' +
        ", name=" + name +
        ", ingameDescription='" + ingameDescription + '\'' +
        ", metaData=" + metaData +
        '}';
  }
}
