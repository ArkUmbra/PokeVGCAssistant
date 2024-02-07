package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class RunScraperGen9 {

  @Test
  public void testExecute() throws IOException {
    Scraper scraper = new Scraper();

    scraper.execute();
  }
}
