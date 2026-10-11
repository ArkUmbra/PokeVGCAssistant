package com.arkumbra.pokesearcher.homenews;

import com.arkumbra.pokesearcher.scraper.RegulationRules;
import org.junit.jupiter.api.Test;

public class PokemonHomeNewsRegulationScraperTest {

  @Test
  public void test() {
    var scraper = new PokemonHomeNewsRegulationScraper();

    RegulationRules regulationRules = scraper.scrape("https://news.pokemon-home.com/en/page/816.html");

    System.out.println(regulationRules);
  }

}
