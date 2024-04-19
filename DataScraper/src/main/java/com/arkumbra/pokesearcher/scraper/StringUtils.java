package com.arkumbra.pokesearcher.scraper;

public class StringUtils {

  public static String padLeftToFourDigits(int n) {
    if (n >= 10_000)
      throw new IllegalArgumentException("Number must be less than 10,000");

    int digits = 0;
    int nCopy = n;
    while (n > 0) {
      n /= 10;
      digits++;
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 4 - digits; i++)
      sb.append("0");
    sb.append(nCopy);
    return sb.toString();

//    return String.format("%" + (4 - digits) + "s", nCopy);
  }
}
