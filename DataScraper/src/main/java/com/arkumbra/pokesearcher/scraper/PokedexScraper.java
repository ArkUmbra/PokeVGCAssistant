package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

import static com.arkumbra.pokesearcher.scraper.Util.getPageWithDelay;

// TODO Refactor this to handle dex by generation
public class PokedexScraper {

  private final SingleFormPokedexScraper pokedexScraper = new SingleFormPokedexScraper();
  private final MultiFormPokedexScraper multiFormPokedexScraper = new MultiFormPokedexScraper();

  private final Set<PokemonSpecificScraper> pokemonSpecificScrapers = Set.of(
      new TaurosSpecificPokedexScraper(),
      new MiniorSpecificPokedexScraper(),
      new BasculinSpecificPokedexScraper(),
      new OgerponSpecificPokedexScraper(),
      new FormChangesWithGenderPokedexScraper(),
      new RegionalFormSpecificPokedexScraper(),
      new FormsWithOnlyVisualDifferencesPokedexScraper()
  );

  public Set<String> getAllPokemonDexUrls() throws IOException {
    Set<String> result = Util.getUrlsForAllSelectURLObjectsOnPage(Constants.serebiiPokedexScarletViolet, null, ".shtml");
    result.remove("https://www.serebii.net/pokedex-sv//");
    return result;
  }

  public Pokemon extractPokemonInfo(String url) throws IOException {
    Document doc = getPageWithDelay(url);

    String idName = url.replaceAll(Constants.serebiiPokedexScarletViolet, "").split(".shtml")[0].replace("/", "");
    Pokemon pokemon = new Pokemon();
    pokemon.setIdName(idName);

    for (PokemonSpecificScraper pokemonSpecificScraper : pokemonSpecificScrapers) {
      if (pokemonSpecificScraper.validForUrl(url))
        return pokemonSpecificScraper.extractPokemonInfo(doc, pokemon);
    }

    return (isMultiFormPokemon(doc) ? multiFormPokedexScraper : pokedexScraper)
        .extractPokemonInfo(doc, pokemon);
  }

  protected boolean isMultiFormPokemon(Document doc) {
    Elements forms = getAlternateFormDexTable(doc);
    return !forms.isEmpty();
  }

  protected Elements getAlternateFormDexTable(Document doc) {
    return doc.select("table.dextable:contains(Alternate Forms)");
  }


}
