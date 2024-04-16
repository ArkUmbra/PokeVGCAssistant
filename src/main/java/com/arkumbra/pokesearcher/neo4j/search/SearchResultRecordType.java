package com.arkumbra.pokesearcher.neo4j.search;

public enum SearchResultRecordType {
  move(0),
  pokemon(1),
  ability(2)
  ;

  private int category;

  SearchResultRecordType(int category) {
    this.category = category;
  }

  public int getCategory() {
    return category;
  }
}
