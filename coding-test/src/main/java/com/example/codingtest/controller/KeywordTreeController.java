package com.example.codingtest.controller;

import com.example.codingtest.form.KeywordTreeForm;
import com.example.codingtest.service.KeywordTreeService;
import java.io.IOException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("keyword_tree")
public class KeywordTreeController {

  private final KeywordTreeService keywordTreeService;

  public KeywordTreeController(KeywordTreeService keywordTreeService) {
    this.keywordTreeService = keywordTreeService;
  }

  /**
   * キーワード検索画面を表示
   *
   * @param keywordTreeForm
   * @param model
   * @return
   */
  @GetMapping("wiki")
  public String index(@ModelAttribute KeywordTreeForm keywordTreeForm, Model model) {

    return "keyword_tree";
  }

  /**
   * Wikipediaからキーワードを検索して、画面に表示
   *
   * @param keywordTreeForm
   * @param errorRet
   * @param model
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  @PostMapping("wiki")
  public String search(
      @Validated @ModelAttribute KeywordTreeForm keywordTreeForm,
      BindingResult errorRet,
      Model model)
      throws IOException, InterruptedException {

    if (errorRet.hasErrors()) {
      return "keyword_tree";
    }

    model.addAttribute(
        "dispKeywords", keywordTreeService.createDispKeywordTree(keywordTreeForm.getKeyword()));

    model.addAttribute("searchComplete", "探索完了");

    return "keyword_tree";
  }
}
