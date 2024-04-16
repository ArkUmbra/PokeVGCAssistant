package com.arkumbra.pokesearcher.neo4j.search;

import com.arkumbra.pokesearcher.scraper.BaseStats;
import com.arkumbra.pokesearcher.scraper.Name;
import com.arkumbra.pokesearcher.scraper.TypeValue;

import java.util.LinkedHashSet;
import java.util.List;

public class SearchByNameResponsePokemonRecord implements SearchByNameResponseRecord {

  private static final SearchResultRecordType recordType = SearchResultRecordType.pokemon;
  private final String id;
  private final Name name;
  private final String formName;
  private final List<TypeValue> types;
//  private final BaseStats baseStats;


  public SearchByNameResponsePokemonRecord(String id, Name name, String formName, List<TypeValue> types) {
    this.id = id;
    this.name = name;
    this.formName = formName;
    this.types = types;
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

  public List<TypeValue> getTypes() {
    return types;
  }

  public String getFormName() {
    return formName;
  }

  @Override
  public String toString() {
    return "SearchByNameResponsePokemonRecord{" +
        "id='" + id + '\'' +
        ", name=" + name +
        ", formName='" + formName + '\'' +
        ", types=" + types +
        '}';
  }
}
