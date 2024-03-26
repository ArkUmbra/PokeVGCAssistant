package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;

import java.util.Set;

public class FormsWithOnlyVisualDifferencesPokedexScraper extends SingleFormPokedexScraper implements PokemonSpecificScraper {
  private static final Set<String> URLS = Set.of(
      "https://www.serebii.net/pokedex-sv/pikachu/",
      "https://www.serebii.net/pokedex-sv/vivillon/"
  );

  @Override
  public boolean validForUrl(String url) {
    return URLS.contains(url);
  }

  @Override
  public Pokemon extractPokemonInfo(Document doc, Pokemon pokemon) {
    return super.extractPokemonInfo(doc, pokemon);
  }

}
