package com.arkumbra.pokesearcher.regulation;

import com.arkumbra.pokesearcher.scraper.PokemonChamptionsRegulationRules;
import com.arkumbra.pokesearcher.scraper.RegulationValidPokemon;
import com.arkumbra.pokesearcher.scraper.Util;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Evaluator;

import java.util.GregorianCalendar;

public class RegulationParser {

  public RegulationValidPokemon parseValidPokemon(String url) {
    RegulationValidPokemon res = new RegulationValidPokemon();

    try (Playwright playwright = Playwright.create()) {
      Browser browser = playwright.firefox().launch();
      Page page = browser.newPage();
      page.navigate(url);

      for (Locator pokemonLocator : page.locator(".tooltip").all()) {
        String text = pokemonLocator.textContent();
        String[] splits = text.split(" ");

        int number = Integer.parseInt(splits[0].substring(3));
        String name = splits[1];
        String formeName = (splits.length == 3) ? splits[2] : null;
//        System.out.println(number + " - " + name + " - " + formeName);
        res.put(number);
      }
    }


    return res;
  }
}
