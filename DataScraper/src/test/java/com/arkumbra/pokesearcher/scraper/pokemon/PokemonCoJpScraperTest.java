package com.arkumbra.pokesearcher.scraper.pokemon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PokemonCoJpScraperTest {

  private final PokemonCoJpScraper scraper = new PokemonCoJpScraper();

  @Test
  public void testScrapeJapaneseName() {
    assertEquals("フシギダネ",scraper.getJapaneseNameForPokemon(1));
    assertEquals("ビリリダマ",scraper.getJapaneseNameForPokemon(100));
    assertEquals("リリーラ",scraper.getJapaneseNameForPokemon(345));
  }

}
