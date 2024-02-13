package com.arkumbra.pokesearcher.scraper;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// TODO Refactor this to handle dex by generation
public class Scraper {
  private static final String pokedexUrlBase = "https://www.serebii.net";
  private static final String NOT_PRESENT_IN_DEX = "-";

//  private static final List<String> selectBoxesForDexShortLinks = List.of(
//      "#content > main:nth-child(2) > div:nth-child(5) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > form:nth-child(1) > select:nth-child(1) > option",
//
//  );

  public void execute() throws IOException {
//    Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
//    log(doc.title());
//    Elements newsHeadlines = doc.select("#mp-itn b a");
//    for (Element headline : newsHeadlines) {
//      log("%s\n\t%s",
//          headline.attr("title"), headline.absUrl("href"));
//    }




    Set<String> pokedexUrls = getAllPokemonDexUrls();

    Set<Pokemon> allPokemon = new HashSet<>();

    int i = 0;
    for (String pokedexUrl : pokedexUrls) {
      if (i >= 4) break;
      log(pokedexUrl);
      Pokemon pokemon = extractPokemonInfo(pokedexUrl);
//      log(pokemon.toString());

      allPokemon.add(pokemon);

      i++;
    }

    log(allPokemon.toString());




    // Test with one prev gen mon
//    Pokemon pokemon = extractPokemonInfo("https://www.serebii.net/pokedex-sv/chesnaught/");
//    log(pokemon.toString());
//
//     Test with one new gen mon
//    pokemon = extractPokemonInfo("https://www.serebii.net/pokedex-sv/tinkaton/");
//    log(pokemon.toString());
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
//    doc.select(Evaluator.Attribute.AttributeWithValue(""))
//    Elements pokedexSelectBoxes = doc.selectXpath("/html/body/div[1]/div[2]/main/div[2]/table[1]/tbody/tr[1]/td[1]/form/select > option");
    Elements pokedexSelectBoxes = doc.select("select[name=SelectURL] > option");
//    Elements pokedexSelectBoxes = doc.select("#content > main:nth-child(2) > div:nth-child(5) > table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > form:nth-child(1) > select:nth-child(1) > option");
//    Elements pokedexSelectBoxes = doc.select("html body.celtra-mini div#wrapper div#content main div table tbody tr td form select option");
//    log(pokedexSelectBoxes.toString());

//    log(pokedexSelectBoxes.)
    for (Element element : pokedexSelectBoxes) {
      String shortUrl = element.attr("value");
      if (shortUrl != null && !shortUrl.equals("#")) {
        String combinedUrl = pokedexUrlBase + shortUrl;
        urls.add(combinedUrl);
      }
    }

//    for (String url : urls) {
//      log(url);
//    }
//    pokedexSelectBoxes.
//    pokedexSelectBoxes.eachAttr("option").forEach(System.out::println);
    return urls;
  }

  public Pokemon extractPokemonInfo(String url) throws IOException {
    Document doc = getPageWithDelay(url);

    // second occurrence of dextable class. TR inside the tbody that contains the actual data
    Element dexTableNameNumberRowInnerDataTr = doc
        .select("table.dextable").get(1)
        .select("tbody:nth-child(1) > tr:nth-child(2)").first();

    // table.dextable:nth-child(4) > tbody:nth-child(1) > tr:nth-child(2)

    // TDs
    // 0 - Name
    // 1 - Other Names
    // 2 - No.
    // 3 - Gender Ratio
    // 4 - Type
    Elements dexTableNameNumberDataTds = dexTableNameNumberRowInnerDataTr.select("> td");
//    log(dexTableNameNumberDataTds.toString());

//     table.dextable:nth-child(4)
//    Element element = dexTableNameNumberRow.select("tbody > tr").get(1)
//    Elements element = doc.select("table.dextable:nth-child(5) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(3)");
//    Elements element = doc.select("table.dextable:nth-child(5) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(3)");
//    log(element.toString());
//    return null;

    Pokemon pokemon = new Pokemon();

    // Name
    String englishName = dexTableNameNumberDataTds.get(0).text();
//    log(englishName);
    Name name = new Name();
    name.englishName = englishName;
    pokemon.name = name;
//    return null;

    // Pokedex number
    Map<String, Integer> dexToNumber = extractDexInfo(
        dexTableNameNumberDataTds.get(2)
            .select("table > tbody > tr > td")
    );
//    log(dexToNumber.toString());
    Pokedex pokedex = new Pokedex();
    pokedex.dexNumbers = dexToNumber;
    pokemon.pokedex = pokedex;
//    return null;
//
//    // Types
    Elements typeElements = dexTableNameNumberDataTds.get(4).select(" > a");
//    log(typeElements.toString());
    Set<PokemonType> types = extractTypes(typeElements);
//    log(types.toString());
    pokemon.types = types;

    Moveset moveset = extractMoveset(doc);
//    log(moveset.toString());
    pokemon.moveset = moveset;

    return pokemon;
  }

  private Map<String, Integer> extractDexInfo(Elements tds) {
    Map<String, Integer> dexToNumber = new HashMap<>();

//    Elements tds = dexTable.elem("td");
    for (int i = 0; i < tds.size() - 1; i += 2) {
//      log(tds.get(i).toString());
      String dex = tds.get(i).text().split(":")[0];
      String number = tds.get(i + 1).text().split("#")[1];

//      log(dex);
//      log(number);

      if (number.startsWith(NOT_PRESENT_IN_DEX)) {
        dexToNumber.put(dex, null);
      } else {
        dexToNumber.put(dex, Integer.valueOf(number));
      }
    }

    return dexToNumber;
  }

  private Set<PokemonType> extractTypes(Elements typeElements) {
    Set<PokemonType> types = new HashSet<>();

    for (Element typeElement : typeElements) {
      Attribute href = typeElement.attribute("href");
      String[] splits = href.getValue().split("/");
      String type = splits[splits.length - 1].replaceAll(".shtml", ""); // remove the .shtml part of the type
      PokemonType pokemonType = PokemonType.valueOf(type);
      types.add(pokemonType);
    }

    return types;
  }

  private Moveset extractMoveset(Document doc) {
    Moveset moveset = new Moveset();
    // For now, we just want the levels and move names. The full move/attack data will be scraped
    // from https://www.serebii.net/attackdex-sv/ later

    parseLevelOrTmMoveset(moveset.getLevelUpMoves(), doc.select("table.dextable").get(8));
    parseLevelOrTmMoveset(moveset.getTmMoves(), doc.select("table.dextable").get(9));
    parseEggMovesMoveset(moveset.getEggMoves(), doc.select("table.dextable").get(10));

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
//      log(dataTds.toString());

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
//      log(dataTds.toString());


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
