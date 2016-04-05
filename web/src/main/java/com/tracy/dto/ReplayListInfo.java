package com.tracy.dto;

public class ReplayListInfo {
  private String type;
  private String content;
  private NewsInfo news_info;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public NewsInfo getNews_info() {
    return news_info;
  }

  public void setNews_info(NewsInfo news_info) {
    this.news_info = news_info;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
