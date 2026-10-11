package com.arkumbra.pokesearcher.scraper.generator;

import com.arkumbra.pokesearcher.scraper.Game;
import com.arkumbra.pokesearcher.scraper.PokedexScraper;
import com.arkumbra.pokesearcher.scraper.model.Pokedex;

public class MasterDataCatalogGenerator {

  private final PokedexScraperFactory pokedexScraperFactory = new PokedexScraperFactory();

  public Pokedex generateFor(Game game) {
    if (game != Game.Champions) throw new RuntimeException("Not yet implemented");

    return pokedexScraperFactory.forGame(game).scrapeAll();
  }
}
