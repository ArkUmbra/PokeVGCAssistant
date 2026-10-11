package com.arkumbra.pokesearcher.scraper.generator;

import com.arkumbra.pokesearcher.scraper.BasePokedexScraper;
import com.arkumbra.pokesearcher.scraper.Game;
import com.arkumbra.pokesearcher.scraper.serebii.SerebiiScraper;

public class PokedexScraperFactory {

  public BasePokedexScraper forGame(Game game) {
   return switch (game) {
      case Champions -> new SerebiiScraper(game);
      default -> throw new RuntimeException("Not yet implemented");
    };
  }

}
