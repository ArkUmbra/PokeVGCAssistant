package com.arkumbra.pokesearcher.scraper;

public class Name {
  String english;
  String japanese;

  public Name() { }

  public Name(String english) {
    this.english = english;
  }

  public Name(String english, String japanese) {
    this.english = english;
    this.japanese = japanese;
  }


  @Override
  public String toString() {
    return "Name{" +
        "englishName='" + english + '\'' + System.lineSeparator() +
        "japaneseName='" + japanese + '\'' +
        '}';
  }

  public String getEnglish() {
    return english;
  }

  public String getJapanese() {
    return japanese;
  }
}