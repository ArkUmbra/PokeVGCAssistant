package com.arkumbra.pokesearcher.neo4j.search;

import com.arkumbra.pokesearcher.scraper.Name;
import com.arkumbra.pokesearcher.scraper.TypeValue;

import java.util.List;

public class SearchByNameResponseAbilityRecord implements SearchByNameResponseRecord {

  private static final SearchResultRecordType recordType = SearchResultRecordType.ability;
  private final String id;
  private final Name name;
  private final String description;

  public SearchByNameResponseAbilityRecord(String id, Name name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
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

  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return "SearchByNameResponseAbilityRecord{" +
        "id='" + id + '\'' +
        ", name=" + name +
        ", description='" + description + '\'' +
        '}';
  }
}
