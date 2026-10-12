package com.arkumbra.pokesearcher.neo4jnew;

import java.time.ZonedDateTime;
import java.util.Objects;

public class RegulationSummary {

  private final String id;
  private final String name;
  private final ZonedDateTime startDate;
  private final ZonedDateTime endDate;
  private final int numberOfPokemon;

  public RegulationSummary(String id, String name, ZonedDateTime startDate, ZonedDateTime endDate, int numberOfPokemon) {
    this.id = id;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.numberOfPokemon = numberOfPokemon;
  }

  public String getId() {
    return id;
  }

  public String id() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String name() {
    return name;
  }

  public ZonedDateTime getStartDate() {
    return startDate;
  }

  public ZonedDateTime startDate() {
    return startDate;
  }

  public ZonedDateTime getEndDate() {
    return endDate;
  }

  public ZonedDateTime endDate() {
    return endDate;
  }

  public int getNumberOfPokemon() {
    return numberOfPokemon;
  }

  public int numberOfPokemon() {
    return numberOfPokemon;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RegulationSummary that = (RegulationSummary) o;
    return numberOfPokemon == that.numberOfPokemon &&
        Objects.equals(id, that.id) &&
        Objects.equals(name, that.name) &&
        Objects.equals(startDate, that.startDate) &&
        Objects.equals(endDate, that.endDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, startDate, endDate, numberOfPokemon);
  }

  @Override
  public String toString() {
    return "RegulationSummary{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", startDate=" + startDate +
        ", endDate=" + endDate +
        ", numberOfPokemon=" + numberOfPokemon +
        '}';
  }
}
