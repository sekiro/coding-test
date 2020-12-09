package com.example.codingtest.service;

import com.example.codingtest.model.DispKeyword;
import com.example.codingtest.model.KeywordTree;
import com.example.codingtest.utility.KeywordTreeUtil;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WikiKeywordTreeService implements KeywordTreeService {

  private final KeywordTreeUtil util;

  public WikiKeywordTreeService(KeywordTreeUtil util) {
    this.util = util;
  }

  /** WikipediaのURL */
  private final String BASE_URL = "https://ja.wikipedia.org/wiki/";

  /** 最大探索回数 */
  private final int LIMIT_NUM = 20;

  /**
   * 引数のキーワードに関連するキーワードを探索してキーワードツリーの形式で返却
   *
   * @param beginKeyword
   * @return
   */
  @Override
  public List<DispKeyword> createDispKeywordTree(String beginKeyword)
      throws IOException, InterruptedException {

    // 画面から入力されたキーワードをルートノードとしてツリー構造を作成
    KeywordTree rootNode = new KeywordTree();
    rootNode.setSerialNumber(0);
    rootNode.setKeyword(beginKeyword);

    // 探索管理用のキュー、初期はルートノードを入れる
    Deque<KeywordTree> keywordQue = new ArrayDeque<>();
    keywordQue.add(rootNode);

    util.searchKeywordTree(rootNode, keywordQue, BASE_URL, LIMIT_NUM);

    // 画面表示用に加工
    List<DispKeyword> dispKeywordTree = new ArrayList<>();
    util.processingToDispKeywordTree(rootNode, rootNode, dispKeywordTree);

    return dispKeywordTree;
  }
}
