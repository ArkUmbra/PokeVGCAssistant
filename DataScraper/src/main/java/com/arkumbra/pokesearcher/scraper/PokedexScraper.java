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
  private final MegaEvolutionPokedexScraper megaScraper = new MegaEvolutionPokedexScraper();

  private final Set<PokemonSpecificScraper> pokemonSpecificScrapers = Set.of(
      new TaurosSpecificPokedexScraper(),
      new MiniorSpecificPokedexScraper(),
      new BasculinSpecificPokedexScraper(),
      new OgerponSpecificPokedexScraper(),
      new FormChangesWithGenderPokedexScraper(),
      new RegionalFormSpecificPokedexScraper(),
      new FormsWithOnlyVisualDifferencesPokedexScraper()
  );

  protected String getPokedexBaseUrl() {
    return Constants.serebiiPokedexScarletViolet;
  }

  public Set<String> getAllPokemonDexUrls() throws IOException {
    String baseUrl = getPokedexBaseUrl();
    Set<String> result = Util.getUrlsForAllSelectURLObjectsOnPage(baseUrl, null, ".shtml");
    result.remove(baseUrl + "/");
    return result;
  }

  public Pokemon extractPokemonInfo(String url) throws IOException {
    Document doc = getPageWithDelay(url);

    String idName = url.replaceAll(getPokedexBaseUrl(), "").split(".shtml")[0].replace("/", "");
    Pokemon pokemon = new Pokemon();
    pokemon.setIdName(idName);

    for (PokemonSpecificScraper pokemonSpecificScraper : pokemonSpecificScrapers) {
      if (pokemonSpecificScraper.validForUrl(url))
        return pokemonSpecificScraper.extractPokemonInfo(doc, pokemon);
    }

    return determineRequiredScraper(doc).extractPokemonInfo(doc, pokemon);
  }

  private SingleFormPokedexScraper determineRequiredScraper(Document doc) {
    if (isMultiFormPokemon(doc)) return getMultiFormScraper();
    if (isMegaEvolutionPokemon(doc)) return getMegaEvoScraper();
    return getSingleFormScraper();
  }

  private boolean isMegaEvolutionPokemon(Document doc) {
    return !doc.select("table.dextable:contains(Mega )").isEmpty();
  }


  protected boolean isMultiFormPokemon(Document doc) {
    Elements forms = getAlternateFormDexTable(doc);
    return !forms.isEmpty();
  }

  protected Elements getAlternateFormDexTable(Document doc) {
    return doc.select("table.dextable:contains(Alternate Forms)");
  }

  protected MultiFormPokedexScraper getMultiFormScraper() {
    return multiFormPokedexScraper;
  }

  protected SingleFormPokedexScraper getSingleFormScraper() {
    return pokedexScraper;
  }

  private SingleFormPokedexScraper getMegaEvoScraper() {
    return megaScraper;
  }


}
