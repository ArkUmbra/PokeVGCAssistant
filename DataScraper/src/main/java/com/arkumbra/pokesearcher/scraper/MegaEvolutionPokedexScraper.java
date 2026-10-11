package com.arkumbra.pokesearcher.scraper;


import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

// TODO Refactor this to handle dex by generation
public class MegaEvolutionPokedexScraper extends SingleFormPokedexScraper {

  public MegaEvolutionPokedexScraper() {
    super(new ChampionsScraperUtil());
  }

  @Override
  protected void extractExtraPokemonInfo(Pokemon pokemon, Document doc) {
    PokemonDetails mega = new PokemonDetails();

    Elements megaNameDexTables = doc.select("table.dextable:contains(Mega )");
    Element megaNameDexTable = megaNameDexTables.last();
//    Element megaNameDexTable = doc.selectFirst("table.dextable:contains(Mega )");
    LinkedHashSet<TypeValue> types = extractTypes(megaNameDexTable.select("a"));
//    LinkedHashSet<TypeValue> types = extractTypes(megaNameDexTable.nextElementSibling().select("a"));
    mega.setTypes(types);

    // Base Stats
    mega.setBaseStats(parseBaseStats(doc.select("table.dextable:contains(Base Stats - Total)").get(1)));

    // Abilities
    Element abilityHeaderBlockTd = doc.select("tr:contains(Abilities:)").get(1);
    mega.setAbilities(parseAbilitiesFromTextString(abilityHeaderBlockTd.text()));


    pokemon.setMegaEvolutions(List.of(mega));
    // megaNameDexTable.nextElementSibling().select("table > tbody > tr").get(1)

//    megaNameDexTable.select("table > tbody > tr").get(1).select("tr > td").get(3);
//    statsTable
//    megaNameDexTable.nextElementSibling()
//    LinkedHashSet<TypeValue> typeValues = extractTypes(megaNameDexTable.nextElementSiblings());
//
//    System.out.println(megaNameDexTable);
  }


}
