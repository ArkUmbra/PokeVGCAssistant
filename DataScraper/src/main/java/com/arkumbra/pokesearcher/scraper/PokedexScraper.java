package com.arkumbra.pokesearcher.scraper;


import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    String idName = url.replaceAll(Constants.serebiiPokedexScarletViolet, "").split(".shtml")[0].replace("/", "");
    Pokemon pokemon = new Pokemon();
    pokemon.setIdName(idName);

    return isMultiFormPokemon(doc) ? extractPokemonInfoMultiForm(doc, pokemon) : extractPokemonInfoSingleForm(doc, pokemon);
  }

  private boolean isMultiFormPokemon(Document doc) {
    Elements elements = doc.select("table.dextable:contains(Alternate Forms)");
    return !elements.isEmpty();
  }

  private Pokemon extractPokemonInfoSingleForm(Document doc, Pokemon pokemon) {
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

  private Pokemon extractPokemonInfoMultiForm(Document doc, Pokemon pokemon) {
    Elements formImages = doc.select("table.dextable:contains(Alternate Forms)").select("img");
    List<String> pokemonFormNames = formImages.stream()
        // Note: Sometimes the spelling form or forme is used, so this should handle both
        // alt value should be something like: "Kantonian Form", "Incarnate Forme", etc
        .map(formImg -> formImg.attr("alt"))
        .toList();

    // First form can also be considered 'Normal', so we can use that when searching for matching data
    Element abilityHeaderBlockTd = doc.select("tr:contains(Abilities:)").first(); // get header td
    List<String> abilityTextSplitByForm = splitUpAbilitiesHeaderStringForMultiFormPokemon(abilityHeaderBlockTd.text());

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

      // Abilities
      // Some multi form Pokemon just share one set of abilities, so  make sure we don't go out of bounds by trying
      // to select an ability string that doesn't exist.
      String abilityTextString = abilityTextSplitByForm.get(Math.min(formOrder, abilityTextSplitByForm.size() - 1));
      pokemonDetails.setAbilities(parseAbilitiesFromTextString(abilityTextString));

      pokemonDetails.setTypes(extractTypes(subTypeTd.select("a")));
      pokemonDetails.setMoveset(extractMovesetForMultiForm(doc,
          formOrder == 0 ? "Normal" : pokemonFormName));

      // Base Stats
      Elements allStatsTable = doc.select("table.dextable:contains(Base Stats - Total)");
      pokemonDetails.setBaseStats(parseBaseStats(allStatsTable.get(formOrder)));

      forms.add(pokemonDetails);
    }

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
    pokemon.setDetails(forms.removeFirst());
    pokemon.setAlternateForms(forms);

    return pokemon;
  }

  protected List<String> splitUpAbilitiesHeaderStringForMultiFormPokemon(String text) {
    // Example abilities header text for multi form Pokemon before process
    // Abilities: Levitate - Neutralizing Gas - Stench (Hidden) (Normal Forme) - Levitate- Neutralizing Gas - Misty Surge (Hidden) (Galarian Form)
    // Abilities: Defiant (Teal Mask) - Water Absorb (Wellspring Mask) - Mold Breaker (Hearthflame Mask) - Sturdy (Cornerstone Mask)

    // Because matching the form names is unreliable due to seemingly random use of form vs forme, we will just detect
    // brackets and split there. Except in the case of '(Hidden)', which we need to include in the output

    // Remove the leading 'Abilities:  ' string
    text = text.replace("Abilities: ", "");

    List<String> results = new ArrayList<>();
    StringBuilder currentAbilityString = new StringBuilder();

    String hiddenTag = "(Hidden)";

    for (int i = 0; i < text.length(); i++) {

      if (text.charAt(i) == '(') {
        // Start of either (Hidden) or (form name)
        String bracketedWord = parseBracketedWord(text, i);

        // Fast-forward the index to the end of this bracketed word we've found
        i += bracketedWord.length();

        if (bracketedWord.equals(hiddenTag)) {
          // We found the hidden ability, so let's just add it to the current word and
          currentAbilityString.append(hiddenTag);

        } else {
          // Seems like we found the form name, so lets store the current word and reset
          results.add(currentAbilityString.toString().trim());
          currentAbilityString = new StringBuilder();

          // Sometimes there's some formatting junk after the form name, so fast forward until we get to the next
          // actual word
          while (i < text.length() - 1 && ! Character.isAlphabetic(text.charAt(i + 1)))
            i++;
        }


      } else {
        // Append to the end of our current ability string
        currentAbilityString.append(text.charAt(i));
      }
    }

    if (! currentAbilityString.isEmpty())
      results.add(currentAbilityString.toString());

    return results;
  }

  private String parseBracketedWord(String word, int indexOfOpenBracket) {
    for (int i = indexOfOpenBracket; i < word.length(); i++) {
      if (word.charAt(i) == ')') {
        return word.substring(indexOfOpenBracket, i + 1);
      }
    }

    throw new RuntimeException("Couldn't find closing bracket of bracketed word");
  }

  private Abilities parseAbilitiesFromTextString(String abilityHeaderBlockText) {
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

  private Map<PokedexRegion, Integer> extractDexInfo(Elements tds) {
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

    Element tmMovesTable = doc.selectFirst("table.dextable:contains(Technical Machine Attacks)");
    Elements tmMovesTableSubHeadingTds = tmMovesTable.select("tbody > tr").get(1).select("th");
    // Check if the final column of the TM table is 'Form'. If so, some TMs may be form-exclusive
    boolean tmsFilteredByForm = tmMovesTableSubHeadingTds.get(tmMovesTableSubHeadingTds.size() - 1).text().equals("Form");
    parseLevelOrTmMoveset(moveset.getTmMoves(), tmMovesTable, (tmsFilteredByForm ? formName : null));

    // Egg moves are seemingly the same regardless of form
    Element eggMovesTable = doc.selectFirst("table.dextable:contains(Egg Moves)");
    if (eggMovesTable != null) {
      parseEggMovesMoveset(moveset.getEggMoves(), eggMovesTable);
    }

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
    parseLevelOrTmMoveset(moves, dexTable, null);
  }

  private void parseLevelOrTmMoveset(List<Pair<MoveAcquire, String>> moves, Element dexTable, String optionalFormFilter) {
    // Get a better match when searching by form name. Sometimes the form filter name will be 'alola' but the move will
    // be listed as available for 'Alolan'...
    String optionalFormFilterLeadingChars = optionalFormFilter == null ? null : optionalFormFilter.substring(0, 5);

    // For now, we just want the levels and move names. The full move/attack data will be scraped
    // from https://www.serebii.net/attackdex-sv/ later
    Elements movesetsInfoTrs = dexTable.select("> tbody > tr");

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

      String levelOrTm = dataTds.get(0).text();
      MoveAcquire acquire = MoveAcquire.of(levelOrTm);

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

  private BaseStats parseBaseStats(Element statsDexTable) {
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

  private void log(String... msg) {
    System.out.println("log -> " + System.lineSeparator() + String.join(System.lineSeparator(), msg));
  }

}
