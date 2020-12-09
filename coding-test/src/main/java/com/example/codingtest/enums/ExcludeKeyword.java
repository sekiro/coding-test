package com.example.codingtest.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ExcludeKeyword {
  ENGLISH("英語");

  private final String keyword;

  private ExcludeKeyword(String keyword) {
    this.keyword = keyword;
  }

  public String getKeyword() {
    return keyword;
  }

  /**
   * 除外キーワードを全て取得
   *
   * @return 除外キーワードリスト
   */
  public static List<String> getAllKeyword() {

    return Arrays.stream(ExcludeKeyword.values())
        .map(e -> e.getKeyword())
        .collect(Collectors.toList());
  }
}
