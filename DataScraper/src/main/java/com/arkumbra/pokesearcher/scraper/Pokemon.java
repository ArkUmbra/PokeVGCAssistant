package com.arkumbra.pokesearcher.scraper;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {
  private String idName;
  private PokemonDetails details;
  private List<PokemonDetails> alternateForms = new ArrayList<>();

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
}
