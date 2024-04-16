package com.arkumbra.pokesearcher.neo4j.search;

import com.arkumbra.pokesearcher.scraper.*;

public class SearchByNameResponseMoveRecord implements SearchByNameResponseRecord {
  private static final SearchResultRecordType recordType = SearchResultRecordType.move;
  private final String id;
  private final Name name;
  private final TypeValue type;
//  private final AttackCategory category;
  private final String description;
//  private final int pp;
//  private final int power;
//  private final int accuracy;
//  private final Priority priority;
//  private final MoveTarget moveTarget;

//  public SearchByNameResponseMoveRecord(
//      String id, Name name, TypeValue type, AttackCategory category, String description,
//      int pp, int power, int accuracy, Priority priority, MoveTarget moveTarget
//  ) {
//    this.id = id;
//    this.name = name;
//    this.type = type;
//    this.category = category;
//    this.description = description;
//    this.pp = pp;
//    this.power = power;
//    this.accuracy = accuracy;
//    this.priority = priority;
//    this.moveTarget = moveTarget;
//  }


  public SearchByNameResponseMoveRecord(String id, Name name, TypeValue type, String description) {
    this.id = id;
    this.name = name;
    this.type = type;
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

  public TypeValue getType() {
    return type;
  }

//  public AttackCategory getCategory() {
//    return category;
//  }

  public String getDescription() {
    return description;
  }

//  public int getPp() {
//    return pp;
//  }
//
//  public int getPower() {
//    return power;
//  }
//
//  public int getAccuracy() {
//    return accuracy;
//  }
//
//  public Priority getPriority() {
//    return priority;
//  }
//
//  public MoveTarget getMoveTarget() {
//    return moveTarget;
//  }


  @Override
  public String toString() {
    return "SearchByNameResponseMoveRecord{" +
        "id='" + id + '\'' +
        ", name=" + name +
        ", type=" + type +
        ", description='" + description + '\'' +
        '}';
  }
}
