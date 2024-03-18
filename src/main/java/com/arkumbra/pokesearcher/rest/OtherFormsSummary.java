package com.arkumbra.pokesearcher.rest;

import com.arkumbra.pokesearcher.scraper.Pair;

import java.util.List;

public class OtherFormsSummary {
  private final List<Pair<String, String>> formNamesToFormId;

  public OtherFormsSummary(List<Pair<String, String>> formNamesToFormId) {
    this.formNamesToFormId = formNamesToFormId;
  }

  public List<Pair<String, String>> getFormNamesToFormId() {
    return formNamesToFormId;
  }
}
