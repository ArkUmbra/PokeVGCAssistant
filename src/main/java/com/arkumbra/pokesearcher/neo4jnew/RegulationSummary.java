package com.arkumbra.pokesearcher.neo4jnew;

import java.time.ZonedDateTime;
import java.util.Objects;

public class RegulationSummary {

  private final String id;
  private final String name;
  private final ZonedDateTime startDate;
  private final ZonedDateTime endDate;
  private final int numberOfPokemon;
  private final String representativePokemonId;
  private final String representativeSpriteUrl;
  private final String representativeSpriteBgSize;
  private final String representativeSpritePosition;
  private final boolean active;

  public RegulationSummary(String id, String name, ZonedDateTime startDate, ZonedDateTime endDate, int numberOfPokemon, String representativePokemonId, String representativeSpriteUrl, String representativeSpriteBgSize, String representativeSpritePosition) {
    this(id, name, startDate, endDate, numberOfPokemon, representativePokemonId, representativeSpriteUrl, representativeSpriteBgSize, representativeSpritePosition, false);
  }

  public RegulationSummary(String id, String name, ZonedDateTime startDate, ZonedDateTime endDate, int numberOfPokemon, String representativePokemonId, String representativeSpriteUrl, String representativeSpriteBgSize, String representativeSpritePosition, boolean active) {
    this.id = id;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.numberOfPokemon = numberOfPokemon;
    this.representativePokemonId = representativePokemonId;
    this.representativeSpriteUrl = representativeSpriteUrl;
    this.representativeSpriteBgSize = representativeSpriteBgSize;
    this.representativeSpritePosition = representativeSpritePosition;
    this.active = active;
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

  public String getRepresentativePokemonId() {
    return representativePokemonId;
  }

  public String representativePokemonId() {
    return representativePokemonId;
  }

  public String getRepresentativeSpriteUrl() {
    return representativeSpriteUrl;
  }

  public String representativeSpriteUrl() {
    return representativeSpriteUrl;
  }

  public String getRepresentativeSpriteBgSize() {
    return representativeSpriteBgSize;
  }

  public String representativeSpriteBgSize() {
    return representativeSpriteBgSize;
  }

  public String getRepresentativeSpritePosition() {
    return representativeSpritePosition;
  }

  public String representativeSpritePosition() {
    return representativeSpritePosition;
  }

  public boolean isActive() {
    return active;
  }

  public boolean active() {
    return active;
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
        Objects.equals(endDate, that.endDate) &&
        Objects.equals(representativePokemonId, that.representativePokemonId) &&
        Objects.equals(representativeSpriteUrl, that.representativeSpriteUrl) &&
        Objects.equals(representativeSpriteBgSize, that.representativeSpriteBgSize) &&
        Objects.equals(representativeSpritePosition, that.representativeSpritePosition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, startDate, endDate, numberOfPokemon, representativePokemonId, representativeSpriteUrl, representativeSpriteBgSize, representativeSpritePosition);
  }

  @Override
  public String toString() {
    return "RegulationSummary{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", startDate=" + startDate +
        ", endDate=" + endDate +
        ", numberOfPokemon=" + numberOfPokemon +
        ", representativePokemonId='" + representativePokemonId + '\'' +
        ", representativeSpriteUrl='" + representativeSpriteUrl + '\'' +
        ", representativeSpriteBgSize='" + representativeSpriteBgSize + '\'' +
        ", representativeSpritePosition='" + representativeSpritePosition + '\'' +
        '}';
  }
}
