package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PokemonDbPokedexScraperTest {

  private final PokemonDbPokedexScraper pokedexScraper = new PokemonDbPokedexScraper();

  @Test
  public void testSinglePokemon() throws IOException {
    Pokemon pokemon = pokedexScraper.extractPokemonInfo("https://pokemondb.net/pokedex/sprigatito");
    System.out.println(pokemon);

    assertEquals("Sprigatito", pokemon.getName().getEnglish());
  }
}
