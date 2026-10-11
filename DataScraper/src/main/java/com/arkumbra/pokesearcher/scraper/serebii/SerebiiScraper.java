package com.arkumbra.pokesearcher.scraper.serebii;

import com.arkumbra.pokesearcher.scraper.*;
import com.arkumbra.pokesearcher.scraper.model.Pokedex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SerebiiScraper extends PokedexScraper implements BasePokedexScraper {

  private final SingleFormPokedexScraper singleFormPokedexScraper = new SingleFormPokedexScraper(new ChampionsScraperUtil());
  private final MultiFormPokedexScraper multiFormPokedexScraper = new MultiFormPokedexScraper(new ChampionsScraperUtil());


  private final Game game;

  public SerebiiScraper(Game game) {
    this.game = game;
  }

  @Override
  protected String getPokedexBaseUrl() {
    return Constants.serebiiPokedexChampions;
  }

  @Override
  public Pokedex scrapeAll() {

    try {
      Set<String> urls = getAllPokemonDexUrls();

      List<Pokemon> pokemonList = new ArrayList<>();
      for (String url : urls) {
        System.out.println(url);

        try {
          pokemonList.add(super.extractPokemonInfo(url));
        } catch (Exception e) {
          System.err.println(url);
          e.printStackTrace();
        }

        if (pokemonList.size() > 10) break;
      }

      return new Pokedex(game, pokemonList);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  protected MultiFormPokedexScraper getMultiFormScraper() {
    return multiFormPokedexScraper;
  }

  @Override
  protected SingleFormPokedexScraper getSingleFormScraper() {
    return singleFormPokedexScraper;
  }
}
