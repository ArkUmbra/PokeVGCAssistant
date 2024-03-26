package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ScraperUtil {


  public void parseLevelUpMoveset(Document doc, Moveset moveset) {
    Element levelUpMovesTable = doc.selectFirst("table.dextable:contains(Standard Level Up)");
    parseLevelOrTmMoveset(moveset.getLevelUpMoves(), levelUpMovesTable, null,null);
  }

  public void parseLevelUpMoveset(Document doc, Moveset moveset, String formName) {
    // Lots of variations on what text is displayed as a heading for the level up moves, so have to detect a bunch of stuff...
    Element levelUpMovesTable = selectFirstExisting(doc,
        "table.dextable:contains(" + formName + " Level Up)",
        "table.dextable:contains(Level Up - " + formName + ")",
        "table.dextable:contains(Normal Level Up)",
        "table.dextable:contains(Standard Level Up)");
    parseLevelOrTmMoveset(moveset.getLevelUpMoves(), levelUpMovesTable, null, null);
  }

  public void parseTmMoveset(Document doc, Moveset moveset) {
    parseTmMoveset(doc, moveset, null);
  }

  public void parseTmMoveset(Document doc, Moveset moveset, String formName) {
    Element tmMovesTable = doc.selectFirst("table.dextable:contains(Technical Machine Attacks)");

    if (tmMovesTable != null) {
      Elements tmMovesTableSubHeadingTds = tmMovesTable.select("tbody > tr").get(1).select("th");
      // Check if the final column of the TM table is 'Form'. If so, some TMs may be form-exclusive
      boolean tmsFilteredByForm = tmMovesTableSubHeadingTds.get(tmMovesTableSubHeadingTds.size() - 1).text().equals("Form");
      parseLevelOrTmMoveset(moveset.getTmMoves(), tmMovesTable, null, tmsFilteredByForm ? formName : null);
    }
  }

  public void parseEggMovesMoveset(Document doc, Moveset moveset) {
    parseEggMovesMoveset(doc, moveset, null);
  }

  public void parseEggMovesMoveset(Document doc, Moveset moveset, String formName) {
    Element eggMovesTable = doc.selectFirst("table.dextable:contains(Egg Moves)");
    if (eggMovesTable != null)
      parseEggMovesMoveset(moveset.getEggMoves(), eggMovesTable, formName);
  }

  public void parseSpecialMoves(Document doc, Moveset moveset) {
//    Element specialMovesTable = doc.selectFirst("table.dextable:contains(Special Moves)");
    Element specialMovesTable = doc.selectFirst("table.dextable > thead > tr > td:contains(Special Moves)");
    if (specialMovesTable != null) {
      specialMovesTable = specialMovesTable.parent().parent().parent();
      parseSpecialMovesMoveset(moveset.getLevelUpMoves(), specialMovesTable, null);
//      parseLevelOrTmMoveset(moveset.getLevelUpMoves(), specialMovesTable, MoveAcquireType.Distribution, null);
    }
  }

  public void parsePreEvolutionOnlyMoves(Document doc, Moveset moveset) {
    Element preEvoMovesTable = doc.selectFirst("table.dextable > tbody > tr > td:contains(Pre-Evolution Only Moves)");

//    Element preEvoMovesTable = doc.selectFirst("table.dextable:contains(Pre-Evolution Only Moves)");
    if (preEvoMovesTable != null) {
      System.out.println("-- Pre evo moves found!! -- ");
      preEvoMovesTable = preEvoMovesTable.parent().parent().parent();
      parsePreEvolutionMovesMoveset(moveset.getLevelUpMoves(), preEvoMovesTable, null, null);
    }
  }

  public void parsePreEvolutionOnlyMoves(Document doc, Moveset moveset, String formFilter, Supplier<Set<String>> preEvoNameSupplier) {
    Element preEvoMovesTable = doc.selectFirst("table.dextable > tbody > tr > td:contains(Pre-Evolution Only Moves)");
//    Element preEvoMovesTable = doc.selectFirst("table.dextable:contains(Pre-Evolution Only Moves)");
    if (preEvoMovesTable != null) {
      System.out.println("-- Pre evo moves found!! -- ");
      preEvoMovesTable = preEvoMovesTable.parent().parent().parent();
      Set<String> preEvoName = preEvoNameSupplier.get();
      parsePreEvolutionMovesMoveset(moveset.getLevelUpMoves(), preEvoMovesTable, formFilter, preEvoName);
    }
  }

  private void parseLevelOrTmMoveset(List<Pair<MoveAcquire, String>> moves, Element dexTable,
                                     MoveAcquireType moveAcquireOverride, String optionalFormFilter) {

    // Get a better match when searching by form name. Sometimes the form filter name will be 'alola' but the move will
    // be listed as available for 'Alolan'...
    String optionalFormFilterLeadingChars = optionalFormFilter == null ? null
        : optionalFormFilter.substring(0, Math.min(optionalFormFilter.length() - 1, 5));

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
      MoveAcquire acquire = moveAcquireOverride != null ?
          new MoveAcquire(moveAcquireOverride) : MoveAcquire.of(levelOrTm);

      String movePartialLink = dataTds.get(1).select("a").get(0).attribute("href").getValue();
      String[] splits = movePartialLink.split("/");
      String movename = splits[splits.length - 1].replaceAll(".shtml", "");

      moves.add(new Pair<>(acquire, movename));
    }
  }

  private void parseEggMovesMoveset(List<String> eggMoves, Element dexTable, String optionalFormFilter) {
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

      String movePartialLink = dataTds.get(0).select("a").get(0).attribute("href").getValue();
      String[] splits = movePartialLink.split("/");
      String movename = splits[splits.length - 1].replaceAll(".shtml", "");

      levelUpMoves.add(new Pair<>(new MoveAcquire(MoveAcquireType.Distribution), movename));
    }
  }

  protected void parsePreEvolutionMovesMoveset(List<Pair<MoveAcquire, String>> levelUpMoves, Element dexTable,
                                               String optionalFormMatchFilter, Set<String> optionalPreEvolutionNamesFilter) {

    Elements movesetsInfoTrs = dexTable.select("> tbody > tr");
//    Elements movesetsInfoTrs = dexTable.select("tbody > tr");

    // Standard level up moves
    // 0 first row - heading (skip)
    // 1 second row - subheadings (skip)
    // After that data for each move split across two TRs
    // 2 (a) - data (then by td... 0) Level, Attack Name (+ Link), Type, Category, Attack power, Accuracy, PP, Effect Percentage, (Form)
    // 3 (b) - description (one td)
    for (int i = 2; i < movesetsInfoTrs.size(); i += 2) {
      Element dataTr = movesetsInfoTrs.get(i);
      Elements dataTds = dataTr.select("> td");

      String movePartialLink = dataTds.get(0).select("a").get(0).attribute("href").getValue();
      String[] splits = movePartialLink.split("/");
      String movename = splits[splits.length - 1].replaceAll(".shtml", "");

//      Elements directTds = dataTr.select(" > td");
      List<String> formsThatCanAccessThisMove = dataTds.get(dataTds.size() - 1)
          .select("img").eachAttr("alt");
      if (dataTds.get(dataTds.size() - 1).select("img").size() > formsThatCanAccessThisMove.size()) {
        System.err.println("-- POSSIBLE MANUAL ACTION NEEDED [" + movename + "] Detected some images without alt text in pre-evo only moves table. This move might need to be manually processed for additional undetected forms");
        System.err.println(dataTds.get(dataTds.size() - 1).select("img").eachAttr("src"));
      }

      if (optionalFormMatchFilter == null && optionalPreEvolutionNamesFilter == null) {
        continue; // Single form, No filtering required
      }
      Set<String> matches;
      if (optionalFormMatchFilter == null) {
        // Multi form, pre-evo name + no form name included (such as for default)
        matches = optionalPreEvolutionNamesFilter.stream()
            .map(s -> String.format("Learn as %s", s.toLowerCase())).collect(Collectors.toSet());
        System.out.println(formsThatCanAccessThisMove + " -> is match? [" + matches + "]");
      } else {
        // Multi form, pre-evo name + form name
        matches = optionalPreEvolutionNamesFilter.stream()
            .map(s -> String.format("Learn as %s %s", s.toLowerCase(), optionalFormMatchFilter.toLowerCase())).collect(Collectors.toSet());
        // Serebii also sometimes to just not bother to put the pre-evo name in the alt-text sometimes, making me worry
        // that somebody has actually just hand-written all these web pages... anyway, it's an annoying edge case for
        // us so we need to account for it here
        matches.add(String.format("Learn as %s", optionalFormMatchFilter.toLowerCase()));
        System.out.println(formsThatCanAccessThisMove + " -> is match? [" + matches + "]");
      }

      if (formsThatCanAccessThisMove.stream().anyMatch(f -> matches.stream().noneMatch(f::startsWith)))
        continue;

      // TODO needs a rework. Sometimes form filter is null, sometimes both that and preEvo filter are null
//      if (optionalFormMatchFilter != null || optionalPreEvolutionNamesFilter == null) {
//        Elements directTds = dataTr.select(" > td");
//        List<String> formsThatCanAccessThisMove = directTds.get(directTds.size() - 1)
//            .select("img").eachAttr("alt");


//        String match = String.format("Learn as %s %s", optionalFormMatchFilter, optionalPreEvolutionNameFilter);
//        System.out.println(formsThatCanAccessThisMove.toString() + " -> is match? [" + match + "]");
//
//         e.g. Learn as Alola Form Sandslash
//        if (formsThatCanAccessThisMove.stream()
//            .noneMatch(form -> {
//              return optionalPreEvolutionNamesFilter.stream()
//                  .noneMatch()
//              form.startsWith(match)
//            })
//        )
//          continue;
//      }


      System.out.println(movename);

      levelUpMoves.add(new Pair<>(new MoveAcquire(MoveAcquireType.PreEvoOnly), movename));
    }
  }


  public void parseReminderOnlyMoveset(Document doc, Moveset moveset) {
    Elements movesetsInfoTrs = doc
        .select("table.dextable:contains(Move Reminder Only Attacks)")
        .select("> thead > tr");

    for (int i = 2; i < movesetsInfoTrs.size(); i += 2) {
      Element dataTr = movesetsInfoTrs.get(i);
      Elements dataTds = dataTr.select("td");

      String movePartialLink = dataTds.get(0).select("a").get(0).attribute("href").getValue();
      String[] splits = movePartialLink.split("/");
      String movename = splits[splits.length - 1].replaceAll(".shtml", "");

      moveset.getLevelUpMoves().add(new Pair<>(new MoveAcquire(MoveAcquireType.Relearner), movename));
    }
  }

  protected Element selectFirstExisting(Document doc, String... selects) {
    Elements elements = null;
    for (String select : selects) {
      elements = doc.select(select);
      if (! elements.isEmpty()) return elements.get(0);
    }

    throw new IllegalArgumentException(String.format("Could not find elements containing any String, %s", Arrays.toString(selects)));
  }

}
