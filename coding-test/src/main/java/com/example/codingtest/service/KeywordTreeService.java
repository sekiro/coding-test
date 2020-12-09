package com.example.codingtest.service;

import com.example.codingtest.model.DispKeyword;
import java.io.IOException;
import java.util.List;

public interface KeywordTreeService {

  public List<DispKeyword> createDispKeywordTree(String keyword)
      throws IOException, InterruptedException;
}
