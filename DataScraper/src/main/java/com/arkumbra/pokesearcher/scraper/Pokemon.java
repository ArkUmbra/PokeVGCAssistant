package com.arkumbra.pokesearcher.scraper;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
  private String idName;
  private Name name;
  private PokedexNumber pokedexNumber;
  private PokemonDetails details;
  private List<PokemonDetails> alternateForms = new ArrayList<>();
  private List<PokemonDetails> megaEvolutions = new ArrayList<>();

  @Override
  public String toString() {
    return "Pokemon{" +
        "idName='" + idName + '\'' +
        ", details=" + details +
        ", alternateForms=" + alternateForms +
        '}';
  }

  public boolean hasAlternateForms() {
    return !alternateForms.isEmpty();
  }

  public String getIdName() {
    return idName;
  }

  public void setIdName(String idName) {
    this.idName = idName;
  }

  public PokemonDetails getDetails() {
    return details;
  }

  public void setDetails(PokemonDetails details) {
    this.details = details;
  }

  public List<PokemonDetails> getAlternateForms() {
    return alternateForms;
  }

  public void setAlternateForms(List<PokemonDetails> alternateForms) {
    this.alternateForms = alternateForms;
  }

  public List<PokemonDetails> getMegaEvolutions() {
    return megaEvolutions;
  }

  public void setMegaEvolutions(List<PokemonDetails> megaEvolutions) {
    this.megaEvolutions = megaEvolutions;
  }

  public Name getName() {
    return name;
  }

  public void setName(Name name) {
    this.name = name;
  }

  public PokedexNumber getPokedexNumber() {
    return pokedexNumber;
  }

  public void setPokedexNumber(PokedexNumber pokedexNumber) {
    this.pokedexNumber = pokedexNumber;
  }
}
