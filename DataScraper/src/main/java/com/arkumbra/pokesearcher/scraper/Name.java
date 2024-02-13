package com.arkumbra.pokesearcher.scraper;

public class Name {
  String englishName;

  public Name() { }

  public Name(String englishName) {
    this.englishName = englishName;
  }

  @Override
  public String toString() {
    return "Name{" +
        "englishName='" + englishName + '\'' +
        '}';
  }
}