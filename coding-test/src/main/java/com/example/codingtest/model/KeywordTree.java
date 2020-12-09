package com.example.codingtest.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class KeywordTree {
  private int serialNumber;
  private String keyword;
  private boolean isDuplicate = false;
  private boolean isYet = false;
  private int level = 0;
  private List<KeywordTree> children = new ArrayList<>();
}
