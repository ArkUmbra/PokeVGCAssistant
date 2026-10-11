package com.arkumbra.pokesearcher.util;

import java.util.HashMap;
import java.util.Map;

public enum SmogonNameConverter {
  Tornadus("Tornadus", "Tornadus", "tornadus", "Incarnate Forme"),
  UrshifuRapid("Urshifu-Rapid-Strike", "Urshifu", "urshifu", "Rapid Strike Style"),
  OgerponWellspring("Ogerpon-Wellspring", "Ogerpon", "ogerpon", "Wellspring Mask")
  ;

  private static final Map<String, SmogonNameConverter> smogonNameToItem = new HashMap<>();

  static {
    for (SmogonNameConverter nameConverter : SmogonNameConverter.values()) {
      smogonNameToItem.put(nameConverter.name, nameConverter);
    }
  }

  public static SmogonNameConverter getBySmogonName(String smogonName) {
    return smogonNameToItem.get(smogonName);
  }

  private final String smogonName;
  private final String name;
  private final String id;
  private final String formName;

  SmogonNameConverter(String smogonName, String name, String id, String formName) {
    this.smogonName = smogonName;
    this.name = name;
    this.id = id;
    this.formName = formName;
  }

  public String getSmogonName() {
    return smogonName;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public String getFormName() {
    return formName;
  }
}
