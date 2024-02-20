package com.arkumbra.pokesearcher.scraper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtils {

  public static void assertListContents(List<?> list1, List<?> list2) {
    assertEquals(list1.size(), list2.size(), "Lists are not of equal size");

    for (int i = 0; i < list1.size(); i++) {
      assertEquals(list1.get(i), list2.get(i));
    }
  }
}
