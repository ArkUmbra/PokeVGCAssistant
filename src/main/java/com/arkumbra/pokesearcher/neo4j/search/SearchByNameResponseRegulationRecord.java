package com.arkumbra.pokesearcher.neo4j.search;

import com.arkumbra.pokesearcher.scraper.Name;

public class SearchByNameResponseRegulationRecord implements SearchByNameResponseRecord {

  private static final SearchResultRecordType recordType = SearchResultRecordType.regulation;
  private final String id;
  private final Name name;

  public SearchByNameResponseRegulationRecord(String id, Name name) {
    this.id = id;
    this.name = name;
  }

  @Override
  public SearchResultRecordType getSearchRecordType() {
    return recordType;
  }

  public String getId() {
    return id;
  }

  public Name getName() {
    return name;
  }

  @Override
  public String toString() {
    return "SearchByNameResponseRegulationRecord{" +
        "id='" + id + '\'' +
        ", name=" + name +
        '}';
  }
}
