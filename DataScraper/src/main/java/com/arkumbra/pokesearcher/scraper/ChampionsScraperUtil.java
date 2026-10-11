package com.arkumbra.pokesearcher.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ChampionsScraperUtil extends ScraperUtil {

  @Override
  public void parseLevelUpMoveset(Document doc, Moveset moveset) {
    Element levelUpMovesTable = doc.selectFirst("table.dextable:contains(Standard Moves)");
    parseLevelOrTmMoveset(moveset.getLevelUpMoves(), levelUpMovesTable, null,null);
  }

  @Override
  protected boolean includesLevelInMoveset() {
    return false;
  }
}
