package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;

import java.io.IOException;

// For edge cases that are too specific or weird to try to encompass in the default scraper, use a Pokemon-specific
// implementation
public interface PokemonSpecificScraper {
  boolean validForUrl(String url);
  Pokemon extractPokemonInfo(Document doc, Pokemon pokemon);
}
