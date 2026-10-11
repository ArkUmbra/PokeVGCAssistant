package com.arkumbra.pokesearcher.scraper.smogon;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SmogonUsageParserTest {

  private static final SmogonUsageParser smogonUsageParser = new SmogonUsageParser();

  @Test
  public void testParseJson() throws IOException {
    smogonUsageParser.parse(9, 2024, 4, "g");
  }
}
