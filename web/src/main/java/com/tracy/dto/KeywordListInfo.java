package com.tracy.dto;

public class KeywordListInfo {
  private String type;
  private String match_mode;
  private String content;// 关键词内容

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMatch_mode() {
    return match_mode;
  }

  public void setMatch_mode(String match_mode) {
    this.match_mode = match_mode;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
