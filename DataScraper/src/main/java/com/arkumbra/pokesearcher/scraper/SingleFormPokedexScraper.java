package com.arkumbra.pokesearcher.scraper;


import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

// TODO Refactor this to handle dex by generation
public class SingleFormPokedexScraper {
  private static final String NOT_PRESENT_IN_DEX = "-";
  protected static final ScraperUtil scraperUtil = new ScraperUtil();

  protected Pokemon extractPokemonInfo(Document doc, Pokemon pokemon) {
    // TDs
    // 0 - Name
    // 1 - Other Names
    // 2 - No.
    // 3 - Gender Ratio
    // 4 - Type
    Elements dexTableNameNumberDataTds = Util.selectDataRowTdsByPartOfTrHeading(doc, "Other names");

    PokemonDetails details = new PokemonDetails();

    // Name
    String englishName = dexTableNameNumberDataTds.get(0).text();
    Name name = new Name();
    name.english = englishName;
    pokemon.setName(name);

    // Pokedex number
    Map<PokedexRegion, Integer> dexToNumber = extractDexInfo(
        dexTableNameNumberDataTds.get(2)
            .select("table > tbody > tr > td")
    );

    PokedexNumber pokedexNumber = new PokedexNumber();
    pokedexNumber.dexNumbers = dexToNumber;
    pokemon.setPokedexNumber(pokedexNumber);

    // Types
    Elements typeElements = dexTableNameNumberDataTds.get(4).select(" > a");
    details.setTypes(extractTypes(typeElements));

    // Abilities
    Element abilityHeaderBlockTd = doc.select("tr:contains(Abilities:)").first(); // get header td
    details.setAbilities(parseAbilitiesFromTextString(abilityHeaderBlockTd.text()));

    // Moveset
    details.setMoveset(extractMoveset(doc));

    // Base Stats
    Element statsTable = doc.selectFirst("table.dextable:contains(Base Stats - Total)");
    details.setBaseStats(parseBaseStats(statsTable));

    pokemon.setDetails(details);

    return pokemon;
  }

  protected String parseBracketedWord(String word, int indexOfOpenBracket) {
    for (int i = indexOfOpenBracket; i < word.length(); i++) {
      if (word.charAt(i) == ')') {
        return word.substring(indexOfOpenBracket, i + 1);
      }
    }

    throw new RuntimeException("Couldn't find closing bracket of bracketed word");
  }

  protected Abilities parseAbilitiesFromTextString(String abilityHeaderBlockText) {
    // For multi-form Pokemon the page uses 'Hidden' instead of 'Hidden Ability' which is kind of annoying, but that's
    // why have the partial text comparison here
    boolean hasHiddenAbility = abilityHeaderBlockText.contains("(Hidden");

    // Example text of abilityHeaderBlockTd.text() before processing
    // Abilities: Overgrow - Grassy Surge (Hidden Ability)
    List<String> abilityNameIds = Arrays.stream(
        abilityHeaderBlockText
        .replace("(Hidden Ability)", "")
        .replace("(Hidden)", "")
        .replace("Abilities:", "")
        // NOTE!! Avoid abilities with hyphen in the name, but also split correctly on the hyphen because abilities. Sometimes they are separated by " - ", and sometimes it is "- "...
        .trim().split("- ")
    ).map(humanReadableAbility -> {
      return humanReadableAbility
          .trim()
          .toLowerCase()
          .replace(" ", ""); // remove spaces
    }).collect(Collectors.toList());

    String hiddenAbilityNameId = null;
    if (hasHiddenAbility) {
      hiddenAbilityNameId = abilityNameIds.remove(abilityNameIds.size() - 1);
    }

    return new Abilities(abilityNameIds, hiddenAbilityNameId);
  }

  protected Map<PokedexRegion, Integer> extractDexInfo(Elements tds) {
    Map<PokedexRegion, Integer> dexToNumber = new HashMap<>();

    for (int i = 0; i < tds.size() - 1; i += 2) {
      String dex = tds.get(i).text().split(":")[0];
      PokedexRegion region = PokedexRegion.valueOf(dex);
      String number = tds.get(i + 1).text().split("#")[1];

      if (number.startsWith(NOT_PRESENT_IN_DEX)) {
        dexToNumber.put(region, null);
      } else {
        dexToNumber.put(region, Integer.valueOf(number));
      }
    }

    return dexToNumber;
  }

  protected LinkedHashSet<TypeValue> extractTypes(Elements typeElements) {
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

  protected Moveset extractMoveset(Document doc) {
    Moveset moveset = new Moveset();

    scraperUtil.parseLevelUpMoveset(doc, moveset);
    scraperUtil.parseTmMoveset(doc, moveset);
    scraperUtil.parseReminderOnlyMoveset(doc, moveset);
    scraperUtil.parseSpecialMoves(doc, moveset);
    scraperUtil.parseEggMovesMoveset(doc, moveset);
    scraperUtil.parsePreEvolutionOnlyMoves(doc, moveset);

//    Element specialMovesTable = doc.selectFirst("table.dextable > thead > tr > td:contains(Special Moves)");
//    specialMovesTable = specialMovesTable.parent().parent().parent();
//    parseSpecialMovesMoveset(moveset.getLevelUpMoves(), specialMovesTable, null);

//    Element tmMovesTable = doc.selectFirst("table.dextable:contains(Technical Machine Attacks)");
//    if (tmMovesTable != null) {
//      parseLevelOrTmMoveset(moveset.getTmMoves(), tmMovesTable);
//    }

//    Element eggMovesTable = doc.selectFirst("table.dextable:contains(Egg Moves)");
//    if (eggMovesTable != null) {
//      parseEggMovesMoveset(moveset.getEggMoves(), eggMovesTable);
//    }

    return moveset;
  }

//  protected Moveset extractMovesetForMultiForm(Document doc, String formName) {
//    Moveset moveset = new Moveset();
//    // For now, we just want the levels and move names. The full move/attack data will be scraped
//    // from https://www.serebii.net/attackdex-sv/ later
//
//
//    Element tmMovesTable = doc.selectFirst("table.dextable:contains(Technical Machine Attacks)");
//    if (tmMovesTable != null) {
//      Elements tmMovesTableSubHeadingTds = tmMovesTable.select("tbody > tr").get(1).select("th");
//      // Check if the final column of the TM table is 'Form'. If so, some TMs may be form-exclusive
//      boolean tmsFilteredByForm = tmMovesTableSubHeadingTds.get(tmMovesTableSubHeadingTds.size() - 1).text().equals("Form");
//      parseLevelOrTmMoveset(moveset.getTmMoves(), tmMovesTable, (tmsFilteredByForm ? formName : null));
//    }
//
//    // Egg moves are seemingly the same regardless of form
//    Element eggMovesTable = doc.selectFirst("table.dextable:contains(Egg Moves)");
//    if (eggMovesTable != null) {
//      parseEggMovesMoveset(moveset.getEggMoves(), eggMovesTable);
//    }
//
//    return moveset;
//  }





  protected void parseSpecialMovesMoveset(List<Pair<MoveAcquire, String>> levelUpMoves, Element dexTable, String optionalFormFilter) {
    // Get a better match when searching by form name. Sometimes the form filter name will be 'alola' but the move will
    // be listed as available for 'Alolan'...
    String optionalFormFilterLeadingChars = optionalFormFilter == null ? null : optionalFormFilter.substring(0, Math.min(optionalFormFilter.length() - 1, 5));

    // For some reason, special moves table is all within a thead with no tbody...
    Elements movesetsInfoTrs = dexTable.select("> thead > tr");

    // Standard level up moves
    // 0 first row - heading (skip)
    // 1 second row - subheadings (skip)
    // After that data for each move split across two TRs
    // 2 (a) - data (then by td... 0) Level, Attack Name (+ Link), Type, Category, Attack power, Accuracy, PP, Effect Percentage, (Form)
    // 3 (b) - description (one td)
    for (int i = 2; i < movesetsInfoTrs.size(); i += 2) {
      Element dataTr = movesetsInfoTrs.get(i);
      Elements dataTds = dataTr.select("> td");

      if (optionalFormFilterLeadingChars != null) {
        Elements directTds = dataTr.select(" > td");
        List<String> formsThatCanAccessThisMove = directTds.get(directTds.size() - 1)
            .select("img").eachAttr("alt");
        // Just do a partial match to work around some edge cases where optionalFormFilter will be 'Normal'
        // but the alt text will be Normal Forme
        if (formsThatCanAccessThisMove.stream().noneMatch(form -> form.startsWith(optionalFormFilterLeadingChars)))
          continue;
      }

      String movePartialLink = dataTds.get(1).select("a").get(0).attribute("href").getValue();
      String[] splits = movePartialLink.split("/");
      String movename = splits[splits.length - 1].replaceAll(".shtml", "");

      levelUpMoves.add(new Pair<>(new MoveAcquire(MoveAcquireType.Distribution), movename));
    }
  }


  protected void parseEggMovesMoveset(List<String> eggMoves, Element dexTable) {
    parseEggMovesMoveset(eggMoves, dexTable, null);
  }

  protected void parseEggMovesMoveset(List<String> eggMoves, Element dexTable, String optionalFormFilter) {
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

      if (optionalFormFilter != null) {
        Elements directTds = dataTr.select(" > td");
        List<String> formsThatCanAccessThisMove = directTds.get(directTds.size() - 1)
            .select("img").eachAttr("alt");
        if (formsThatCanAccessThisMove.stream().noneMatch(form -> form.startsWith(optionalFormFilter)))
          continue;
      }

      String movePartialLink = dataTds.get(0).select("a").get(0).attribute("href").getValue();
      String[] splits = movePartialLink.split("/");
      String movename = splits[splits.length - 1].replaceAll(".shtml", "");

      eggMoves.add(movename);
    }
  }

  protected BaseStats parseBaseStats(Element statsDexTable) {
    Elements statsTds = statsDexTable.select("tbody > tr").get(2).select("td");

    return new BaseStats(
        Integer.parseInt(statsTds.get(1).text()),
        Integer.parseInt(statsTds.get(2).text()),
        Integer.parseInt(statsTds.get(3).text()),
        Integer.parseInt(statsTds.get(4).text()),
        Integer.parseInt(statsTds.get(5).text()),
        Integer.parseInt(statsTds.get(6).text())
    );
  }

}
