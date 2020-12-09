package com.example.codingtest.utility;

import com.example.codingtest.enums.ExcludeKeyword;
import com.example.codingtest.model.DispKeyword;
import com.example.codingtest.model.KeywordTree;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class KeywordTreeUtil {

  /** 出現するキーワードが重複した場合に付与する文字 */
  private final String DUPLICATE_SYMBOL = "@";

  /** 未探索のキーワードに付与する文字 */
  private final String YET_SYMBOL = "$";

  /** キーワードの接頭辞 */
  private final String KEYWORD_PREFIX = "- ";

  /**
   * @param keywordTree
   * @param keywordQue
   * @param baseUrl
   * @param limitNum
   * @throws IOException
   * @throws InterruptedException
   */
  public void searchKeywordTree(
      KeywordTree keywordTree, Deque<KeywordTree> keywordQue, String baseUrl, int limitNum)
      throws IOException, InterruptedException {

    // 検索済
    List<String> searchedKeywords = new ArrayList<>();

    // 探索回数
    int searchCnt = 0;

    // キューが空であれば探索終了
    while (!keywordQue.isEmpty()) {

      // キューからデキュー
      KeywordTree targetNode = keywordQue.poll();

      String keyword = targetNode.getKeyword();

      // 探索回数が規定値以上で未探索フラグを立てる
      boolean isYet = searchCnt >= limitNum;

      // 探索済リストにある場合は重複フラグを立てる
      boolean isDuplicate = searchedKeywords.contains(keyword);

      targetNode.setYet(isYet);
      targetNode.setDuplicate(isDuplicate);

      // 探索済リストに追加
      searchedKeywords.add(keyword);

      // 探索回数をインクリメント
      searchCnt++;

      // 未探索フラグもしくは重複フラグが立っていれば中断
      if (isYet || isDuplicate) continue;

      // HTTPリクエストを発行して、関連キーワードを取得
      List<String> relatedKeywords = searchRelatedKeywords(baseUrl + keyword);

      // 関連キーワードをそれぞれノードの子ノードとして追加
      addChildrenKeywordTree(targetNode, relatedKeywords, keywordQue);

      // Wikipediaへの過多なアクセスを回避するため1秒間隔を空ける
      Thread.sleep(1000);
    }
  }

  /**
   * 画面に表示するキーワードツリーを作成
   *
   * @param keywordTree
   * @param rootNode
   * @param dispKeywordTree
   */
  public void processingToDispKeywordTree(
      KeywordTree keywordTree, KeywordTree rootNode, List<DispKeyword> dispKeywordTree) {

    Deque<KeywordTree> nodeQue = new ArrayDeque<>();

    nodeQue.add(rootNode);

    while (!nodeQue.isEmpty()) {

      KeywordTree node = nodeQue.poll();

      dispKeywordTree.add(convDispKeywordTreePart(node));

      // 取り出したノードの子ノードをシリアルナンバーの降順にソートして、キューにエンキュー
      List<KeywordTree> nodes = node.getChildren();

      Collections.sort(
          nodes,
          new Comparator<KeywordTree>() {
            @Override
            public int compare(KeywordTree node1, KeywordTree node2) {

              return Integer.compare(node2.getSerialNumber(), node1.getSerialNumber());
            }
          });

      nodes.stream().forEach(c -> nodeQue.addFirst(c));
    }
  }

  /**
   * 子ノードを追加
   *
   * @param targetNode
   * @param relatedKeywords
   * @param keywordQue
   */
  private void addChildrenKeywordTree(
      KeywordTree targetNode, List<String> relatedKeywords, Deque<KeywordTree> keywordQue) {

    List<KeywordTree> childrens = new ArrayList<>();

    int serialNumber = 0;

    for (String relatedKeyword : relatedKeywords) {

      KeywordTree children = new KeywordTree();
      children.setSerialNumber(serialNumber);
      children.setKeyword(relatedKeyword);
      children.setLevel(targetNode.getLevel() + 1);
      childrens.add(children);

      // 探索で取得したキーワードをキューにエンキュー
      keywordQue.add(children);

      serialNumber++;
    }

    targetNode.setChildren(childrens);
  }

  /**
   * キーワードツリーをを表示する形式に変換
   *
   * @param targetNode
   * @return
   */
  private DispKeyword convDispKeywordTreePart(KeywordTree targetNode) {

    DispKeyword dispKeyword = new DispKeyword();

    dispKeyword.setLevel(targetNode.getLevel());

    String part = KEYWORD_PREFIX + targetNode.getKeyword();

    if (targetNode.isDuplicate()) part = part + DUPLICATE_SYMBOL;

    if (targetNode.isYet()) part = part + YET_SYMBOL;

    dispKeyword.setDispKeyword(part);

    return dispKeyword;
  }

  /**
   * 関連キーワードを探索
   *
   * @param url 探索するURL
   * @return 関連キーワード
   * @throws IOException
   */
  private List<String> searchRelatedKeywords(String url) throws IOException {

    Document doc = Jsoup.connect(url).get();

    // 概要（最初の<div class="mw-parser-output">直下で最初の<p>タグのコンテンツ）
    Elements overview = doc.select(".mw-parser-output p").first().children();

    // 概要に含まれるリンク
    return overview.stream()
        .filter(e -> e.tagName().equals("a"))
        // 存在しないページは対象外
        .filter(e -> !e.parentNode().toString().contains("/w/index.php"))
        .map(e -> e.attr("title"))
        // 除外キーワードは対象外
        .filter(t -> !ExcludeKeyword.getAllKeyword().contains(t))
        .collect(Collectors.toList());
  }
}
