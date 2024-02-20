package com.arkumbra.pokesearcher.scraper;


import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

import static com.arkumbra.pokesearcher.scraper.Util.getPageWithDelay;

// TODO Refactor this to handle dex by generation
public class PokedexScraper {
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
    return Util.getUrlsForAllSelectURLObjectsOnPage(Constants.serebiiPokedexScarletViolet);
  }

  public Pokemon extractPokemonInfo(String url) throws IOException {
    Document doc = getPageWithDelay(url);

    return isMultiFormPokemon(doc) ? extractPokemonInfoMultiForm(doc) : extractPokemonInfoSingleForm(doc);
  }

  private boolean isMultiFormPokemon(Document doc) {
    Elements elements = doc.select("table.dextable:contains(Alternate Forms)");
    return !elements.isEmpty();
  }

  private Pokemon extractPokemonInfoSingleForm(Document doc) {
    // TDs
    // 0 - Name
    // 1 - Other Names
    // 2 - No.
    // 3 - Gender Ratio
    // 4 - Type
    Elements dexTableNameNumberDataTds = Util.selectDataRowTdsByPartOfTrHeading(doc, "Other names");

    Pokemon pokemon = new Pokemon();
    PokemonDetails details = new PokemonDetails();

    // Name
    String englishName = dexTableNameNumberDataTds.get(0).text();
    Name name = new Name();
    name.englishName = englishName;
    pokemon.setName(name);

    // Pokedex number
    Map<String, Integer> dexToNumber = extractDexInfo(
        dexTableNameNumberDataTds.get(2)
            .select("table > tbody > tr > td")
    );

    PokedexNumber pokedexNumber = new PokedexNumber();
    pokedexNumber.dexNumbers = dexToNumber;
    pokemon.setPokedexNumber(pokedexNumber);

    // Types
    Elements typeElements = dexTableNameNumberDataTds.get(4).select(" > a");
    details.setTypes(extractTypes(typeElements));

    // Moveset
    details.setMoveset(extractMoveset(doc));

    pokemon.setDetails(details);

    return pokemon;
  }

  private void populateName(Elements dexTableNameNumberDataTds, Pokemon pokemon) {

  }

  private Pokemon extractPokemonInfoMultiForm(Document doc) {
    Elements formImages = doc.select("table.dextable:contains(Alternate Forms)").select("img");
    List<String> pokemonFormNames = formImages.stream()
        // Note: Sometimes the spelling form or forme is used, so this should handle both
        // alt value should be something like: "Kantonian Form", "Incarnate Forme", etc
//        .map(formImg -> formImg.attr("alt").split(" Form")[0])
        .map(formImg -> formImg.attr("alt"))
        .toList();

    // First form can also be considered 'Normal', so we can use that when searching for matching data

    // TDs
    // 0 - Name
    // 1 - Other Names
    // 2 - No.
    // 3 - Gender Ratio
    // 4 - Type (for each form)
    Elements dexTableNameNumberDataTds = Util.selectDataRowTdsByPartOfTrHeading(doc, "Other names");

    LinkedList<PokemonDetails> forms = new LinkedList<>();

    for (int formOrder = 0; formOrder < pokemonFormNames.size(); formOrder++) {
      String pokemonFormName = pokemonFormNames.get(formOrder);

      PokemonDetails pokemonDetails = new PokemonDetails();
      pokemonDetails.setFormName(pokemonFormName);

      // Types
//      Element typeElementsTd = dexTableNameNumberDataTds.get(4);
      // TODO some annoyance about the fact that sometimes it's "Alola Form" and sometimes it's "Alola form"

      // a. Try searching by form name (just use the first few characters)
      Element typeTd = dexTableNameNumberDataTds.get(4);
      Element subTypeTd = typeTd;
      if (! typeTd.select("tbody").isEmpty()) {
        //  if there are subtables in this tr, it means there are type differences between forms, so we need to dig out
        // only the relevant elements

        // There should be a tr for each Pokemon form, and these seem to be in the same order they are listed in the
        // "Alternate Forms" section, so we should just be able to grab the TR by its ID/order
        subTypeTd = typeTd.select("tr").get(formOrder);
      }

//      Element typeContainerTd = Util.selectFollowingTdByTdText(doc, pokemonFormName.substring(0, 5));
//       b. else, try searching for the default
//      if (typeContainerTd == null) typeContainerTd = Util.selectFollowingTdByTdText(doc, "Normal Form");
//      if (typeContainerTd == null) typeContainerTd = dexTableNameNumberDataTds.get(4);

      pokemonDetails.setTypes(extractTypes(subTypeTd.select("a")));

      pokemonDetails.setMoveset(extractMovesetForMultiForm(doc, pokemonFormName));

      forms.add(pokemonDetails);
    }

    Pokemon pokemon = new Pokemon();
    PokemonDetails details = new PokemonDetails();

    // Name
    String englishName = dexTableNameNumberDataTds.get(0).text();
    Name name = new Name();
    name.englishName = englishName;
    pokemon.setName(name);

    // Pokedex number
    Map<String, Integer> dexToNumber = extractDexInfo(
        dexTableNameNumberDataTds.get(2)
            .select("table > tbody > tr > td")
    );

    PokedexNumber pokedexNumber = new PokedexNumber();
    pokedexNumber.dexNumbers = dexToNumber;
    pokemon.setPokedexNumber(pokedexNumber);
    pokemon.setDetails(forms.removeFirst());
    pokemon.setAlternateForms(forms);

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

  private LinkedHashSet<TypeValue> extractTypes(Elements typeElements) {
    LinkedHashSet<TypeValue> types = new LinkedHashSet<>();

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

  private Moveset extractMovesetForMultiForm(Document doc, String formName) {
    Moveset moveset = new Moveset();
    // For now, we just want the levels and move names. The full move/attack data will be scraped
    // from https://www.serebii.net/attackdex-sv/ later


    Element levelUpMovesTable = selectFirstExisting(doc,
        "table.dextable:contains(" + formName + " Level Up)", "table.dextable:contains(Standard Level Up)");
    parseLevelOrTmMoveset(moveset.getLevelUpMoves(), levelUpMovesTable);

    // TODO filter by form column
//    Element tmMovesTable = doc.selectFirst("table.dextable:contains(Technical Machine Attacks)");
//    parseLevelOrTmMoveset(moveset.getTmMoves(), tmMovesTable);
//
//    // TODO filter by form column
//    Element eggMovesTable = doc.selectFirst("table.dextable:contains(Egg Moves)");
//    if (eggMovesTable != null) {
//      parseEggMovesMoveset(moveset.getEggMoves(), eggMovesTable);
//    }

    return moveset;
  }

  private Element selectFirstExisting(Document doc, String select1, String select2) {
    Elements elements = doc.select(select1);
    if (elements.isEmpty()) {
      elements = doc.select(select2);
    }
    return elements.get(0);
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

  private void log(String... msg) {
    System.out.println("log -> " + System.lineSeparator() + String.join(System.lineSeparator(), msg));
  }

}
