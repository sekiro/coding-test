package com.example.codingtest.model;

import lombok.Data;

@Data
/** 画面表示するキーワード */
public class DispKeyword {
  // キーワード
  private String dispKeyword;

  // 階層
  private int level = 0;
}
