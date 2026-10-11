package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Set;

import static com.arkumbra.pokesearcher.scraper.Util.getPageWithDelay;

public class PokemonDbPokedexScraper extends PokedexScraper {

  @Override
  public Set<String> getAllPokemonDexUrls() throws IOException {
    return super.getAllPokemonDexUrls();
  }

  @Override
  public Pokemon extractPokemonInfo(String url) throws IOException {
    Pokemon pokemon = new Pokemon();

    Document doc = getPageWithDelay(url);

    String name = doc.select("html body main#main.main-content.grid-container h1").text();
    pokemon.setName(new Name(name));

    PokedexNumber dex = scrapeDexNumbers(doc);
    // TODO
//    pokemon.setPokedexNumber();

    return pokemon;
  }

  private PokedexNumber scrapeDexNumbers(Document doc) {
    String national = doc.select("html body main#main.main-content.grid-container div.tabset-basics.sv-tabs-wrapper.sv-tabs-onetab div.sv-tabs-panel-list div#tab-basic-906.sv-tabs-panel.active div.grid-row div.grid-col.span-md-6.span-lg-4 table.vitals-table tbody tr td strong").text();

    // TODO
    throw new RuntimeException("not yet implemented");
  }
}
