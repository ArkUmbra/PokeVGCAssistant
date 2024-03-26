package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Set;

public class FormChangesWithGenderPokedexScraper extends MultiFormPokedexScraper implements PokemonSpecificScraper {
  private static final Set<String> URLS = Set.of(
      "https://www.serebii.net/pokedex-sv/meowstic/",
      "https://www.serebii.net/pokedex-sv/basculegion/",
      "https://www.serebii.net/pokedex-sv/indeedee/"
  );

  @Override
  public boolean validForUrl(String url) {
    return URLS.contains(url);
  }

  @Override
  public Pokemon extractPokemonInfo(Document doc, Pokemon pokemon) {
    return super.extractPokemonInfo(doc, pokemon);
  }

  @Override
  protected Elements getAlternateFormDexTable(Document doc) {
    Elements multiForms = doc.select("table.dextable:contains(Gender Differences)");
    if (! multiForms.isEmpty())
      return multiForms;
    else
      throw new IllegalArgumentException("Could not find alternate forms table");
  }

}
