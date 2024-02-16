package com.arkumbra.pokesearcher.scraper;

public class Name {
  String englishName;
  String japanese;

  public Name() { }

  public Name(String englishName) {
    this.englishName = englishName;
  }

  @Override
  public String toString() {
    return "Name{" +
        "englishName='" + englishName + '\'' + System.lineSeparator() +
        "japaneseName='" + japanese + '\'' +
        '}';
  }
}