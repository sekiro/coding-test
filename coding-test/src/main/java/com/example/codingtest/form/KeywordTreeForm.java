package com.example.codingtest.form;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class KeywordTreeForm {

  @NotBlank(message = "検索キーワードの入力は必須です")
  private String keyword;
}
