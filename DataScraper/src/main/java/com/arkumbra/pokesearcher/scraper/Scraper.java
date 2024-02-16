package com.arkumbra.pokesearcher.scraper;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

// TODO Refactor this to handle dex by generation
public class Scraper {
  private static final String pokedexUrlBase = "https://www.serebii.net";
  private static final String NOT_PRESENT_IN_DEX = "-";

  public Set<Pokemon> execute() throws IOException {
    Set<String> pokedexUrls = getAllPokemonDexUrls();

    Set<Pokemon> allPokemon = new HashSet<>();

    int i = 0;
    for (String pokedexUrl : pokedexUrls) {
      if (i >= 4) break;
      log(pokedexUrl);
      Pokemon pokemon = extractPokemonInfo(pokedexUrl);

      allPokemon.add(pokemon);

      i++;
    }

    return allPokemon;
  }

  public Set<String> getAllPokemonDexUrls() throws IOException {
    // There is some duplication in pokemon, due to them being in each region's pokedex and also in the alphabetically
    // selected boxes
    // You could probably refine the doc select cssQuery to only grab one one of them, but this is a simpler and
    // easier to maintain solution (at least I think so)
    Set<String> urls = new HashSet<>();

    // SelectURL
    Document doc = getPageWithDelay("https://www.serebii.net/pokedex-sv/");
    log(doc.title());

    Elements pokedexSelectBoxes = doc.select("select[name=SelectURL] > option");

    for (Element element : pokedexSelectBoxes) {
      String shortUrl = element.attr("value");
      if (shortUrl != null && !shortUrl.equals("#")) {
        String combinedUrl = pokedexUrlBase + shortUrl;
        urls.add(combinedUrl);
      }
    }

    return urls;
  }

  public Pokemon extractPokemonInfo(String url) throws IOException {
    Document doc = getPageWithDelay(url);

    // second occurrence of dextable class. TR inside the tbody that contains the actual data
    Element dexTableNameNumberRowInnerDataTr = doc
        .select("table.dextable").get(1)
        .select("tbody:nth-child(1) > tr:nth-child(2)").first();

    // TDs
    // 0 - Name
    // 1 - Other Names
    // 2 - No.
    // 3 - Gender Ratio
    // 4 - Type
    Elements dexTableNameNumberDataTds = dexTableNameNumberRowInnerDataTr.select("> td");

    Pokemon pokemon = new Pokemon();

    // Name
    String englishName = dexTableNameNumberDataTds.get(0).text();
    Name name = new Name();
    name.englishName = englishName;
    pokemon.name = name;

    // Pokedex number
    Map<String, Integer> dexToNumber = extractDexInfo(
        dexTableNameNumberDataTds.get(2)
            .select("table > tbody > tr > td")
    );

    Pokedex pokedex = new Pokedex();
    pokedex.dexNumbers = dexToNumber;
    pokemon.pokedex = pokedex;

    // Types
    Elements typeElements = dexTableNameNumberDataTds.get(4).select(" > a");
    Set<TypeValue> types = extractTypes(typeElements);
    pokemon.types = types;

    Moveset moveset = extractMoveset(doc);
    pokemon.moveset = moveset;

    return pokemon;
  }

  private Map<String, Integer> extractDexInfo(Elements tds) {
    Map<String, Integer> dexToNumber = new HashMap<>();

    for (int i = 0; i < tds.size() - 1; i += 2) {
      String dex = tds.get(i).text().split(":")[0];
      String number = tds.get(i + 1).text().split("#")[1];

      if (number.startsWith(NOT_PRESENT_IN_DEX)) {
        dexToNumber.put(dex, null);
      } else {
        dexToNumber.put(dex, Integer.valueOf(number));
      }
    }

    return dexToNumber;
  }

  private Set<TypeValue> extractTypes(Elements typeElements) {
    Set<TypeValue> types = new HashSet<>();

    for (Element typeElement : typeElements) {
      Attribute href = typeElement.attribute("href");
      String[] splits = href.getValue().split("/");
      String type = splits[splits.length - 1].replaceAll(".shtml", ""); // remove the .shtml part of the type
      TypeValue typeValue = TypeValue.valueOf(type);
      types.add(typeValue);
    }

    return types;
  }

  private Moveset extractMoveset(Document doc) {
    Moveset moveset = new Moveset();
    // For now, we just want the levels and move names. The full move/attack data will be scraped
    // from https://www.serebii.net/attackdex-sv/ later


    Element levelUpMovesTable = doc.selectFirst("table.dextable:contains(Standard Level Up)");
    parseLevelOrTmMoveset(moveset.getLevelUpMoves(), levelUpMovesTable);

    Element tmMovesTable = doc.selectFirst("table.dextable:contains(Technical Machine Attacks)");
    parseLevelOrTmMoveset(moveset.getTmMoves(), tmMovesTable);

    Element eggMovesTable = doc.selectFirst("table.dextable:contains(Egg Moves)");
    if (eggMovesTable != null) {
      parseEggMovesMoveset(moveset.getEggMoves(), eggMovesTable);
    }

    return moveset;
  }

  private void parseLevelOrTmMoveset(List<Pair<MoveAcquire, String>> moves, Element dexTable) {
    // For now, we just want the levels and move names. The full move/attack data will be scraped
    // from https://www.serebii.net/attackdex-sv/ later

    Elements movesetsInfoTrs = dexTable
        .select("tbody > tr");

    // Standard level up moves
    // 0 first row - heading (skip)
    // 1 second row - subheadings (skip)
    // After that data for each move split across two TRs
    // 2 (a) - data (then by td... 0) Level, Attack Name (+ Link), Type, Category, Attack power, Accuracy, PP, Effect Percentage
    // 3 (b) - description (one td)
    for (int i = 2; i < movesetsInfoTrs.size(); i += 2) {
      Element dataTr = movesetsInfoTrs.get(i);
      Elements dataTds = dataTr.select("td");

      String level = dataTds.get(0).text();
      MoveAcquire acquire = MoveAcquire.of(level);

      String movePartialLink = dataTds.get(1).select("a").get(0).attribute("href").getValue();
      String[] splits = movePartialLink.split("/");
      String movename = splits[splits.length - 1].replaceAll(".shtml", "");

      moves.add(new Pair<>(acquire, movename));
    }
  }

  private void parseEggMovesMoveset(List<String> eggMoves, Element dexTable) {
    // For now, we just want the levels and move names. The full move/attack data will be scraped
    // from https://www.serebii.net/attackdex-sv/ later

    Elements movesetsInfoTrs = dexTable
        .select("tbody > tr");

    // Standard level up moves
    // 0 first row - heading (skip)
    // 1 second row - subheadings (skip)
    // After that data for each move split across two TRs
    // 2 (a) - data (then by td... 0) Attack Name (+ Link), Type, Category, Attack power, Accuracy, PP, Effect Percentage
    // 3 (b) - description (one td)

    // Not all Pokemon have egg moves. In the case of no egg moves, Serebii doesn't display the egg moves table
    // Therefore, we want to check we have the right table or not
    if (! movesetsInfoTrs.get(0).text().contains("Egg Moves")) {
      return;
    }

    for (int i = 2; i < movesetsInfoTrs.size(); i += 2) {
      Element dataTr = movesetsInfoTrs.get(i);
      Elements dataTds = dataTr.select("td");

      String movePartialLink = dataTds.get(0).select("a").get(0).attribute("href").getValue();
      String[] splits = movePartialLink.split("/");
      String movename = splits[splits.length - 1].replaceAll(".shtml", "");

      eggMoves.add(movename);
    }
  }

  private Document getPageWithDelay(String url) {
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace(); // TODO logger
    }

    try {
      return Jsoup.connect(url).get();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void log(String... msg) {
    System.out.println("log -> " + System.lineSeparator() + String.join(System.lineSeparator(), msg));
  }

}
