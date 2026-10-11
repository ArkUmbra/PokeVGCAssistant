package com.arkumbra.pokesearcher.homenews;

import org.junit.jupiter.api.Test;

public class PokemonChampionsEligiblePokemonWebAppScraperTest {

  @Test
  public void test() {
    var scraper = new PokemonChampionsEligiblePokemonWebAppScraper();

//    var res = scraper.scrape("https://web-view.app.pokemonchampions.jp/battle/pages/events/rs178713870219xeaaio/en/pokemon.html");
    scraper.scrapeWithJsoup("https://web-view.app.pokemonchampions.jp/battle/pages/events/rs178713870219xeaaio/en/pokemon.html");

//    System.out.println(res);
  }
}
