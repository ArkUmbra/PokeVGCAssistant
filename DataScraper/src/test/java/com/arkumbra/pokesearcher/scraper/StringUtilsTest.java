package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringUtilsTest {

  @Test
  public void testStringPad() {
    assertEquals("0004", StringUtils.padLeftToFourDigits(4));
    assertEquals("0044", StringUtils.padLeftToFourDigits(44));
    assertEquals("0444", StringUtils.padLeftToFourDigits(444));
    assertEquals("4444", StringUtils.padLeftToFourDigits(4444));
    assertEquals("4000", StringUtils.padLeftToFourDigits(4000));
  }
}
