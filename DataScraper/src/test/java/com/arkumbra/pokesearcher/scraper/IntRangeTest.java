package com.arkumbra.pokesearcher.scraper;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.arkumbra.pokesearcher.scraper.TestUtils.assertListContents;
import static org.junit.jupiter.api.Assertions.*;

public class IntRangeTest {

  @Test
  public void testOverlap() {
    IntRange range = new IntRange(100, 200);

    assertTrue(range.overlaps(new IntRange(99, 201)));
    assertTrue(range.overlaps(new IntRange(100, 200)));
    assertTrue(range.overlaps(new IntRange(200, 200)));
    assertTrue(range.overlaps(new IntRange(200, 300)));
    assertTrue(range.overlaps(new IntRange(0, 300)));

    assertFalse(range.overlaps(new IntRange(0, 99)));
    assertFalse(range.overlaps(new IntRange(201, 202)));
  }

  @Test
  public void testSplit() {
    IntRange range = new IntRange(100, 200);

    assertListContents(
        List.of(
            new IntRange(100, 129),
            new IntRange(141, 200)
        ), range.split(new IntRange(130, 140))
    );

    assertListContents(
        List.of(
            new IntRange(100, 129)
        ), range.split(new IntRange(130, 200))
    );

    assertListContents(
        List.of(
            new IntRange(174, 200)
        ), range.split(new IntRange(50, 173))
    );

    assertListContents(
        List.of(
        ), range.split(new IntRange(50, 250))
    );
  }
}
