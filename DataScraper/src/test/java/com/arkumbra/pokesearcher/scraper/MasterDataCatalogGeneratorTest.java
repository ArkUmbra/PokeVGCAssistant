package com.arkumbra.pokesearcher.scraper;

import com.arkumbra.pokesearcher.scraper.generator.MasterDataCatalogGenerator;
import com.arkumbra.pokesearcher.scraper.model.Pokedex;
import org.junit.jupiter.api.Test;

public class MasterDataCatalogGeneratorTest {

  private MasterDataCatalogGenerator masterDataCatalogGenerator = new MasterDataCatalogGenerator();

  @Test
  public void test() {
    Pokedex dex = masterDataCatalogGenerator.generateFor(Game.Champions);
    for (Pokemon pokemon : dex.getAllPokemon()) {
      System.out.println(pokemon);
    }
//    System.out.println(dex.getAllPokemon().get(0));
  }
}
