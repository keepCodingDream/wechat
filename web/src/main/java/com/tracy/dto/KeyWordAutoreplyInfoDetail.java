package com.tracy.dto;

import java.util.List;

public class KeyWordAutoreplyInfoDetail {
  private String rule_name;
  private Long create_time;
  private String reply_mode;
  private List<KeywordListInfo> keyword_list_info;
  private List<ReplayListInfo> reply_list_info;

  public String getRule_name() {
    return rule_name;
  }

  public void setRule_name(String rule_name) {
    this.rule_name = rule_name;
  }

  public Long getCreate_time() {
    return create_time;
  }

  public void setCreate_time(Long create_time) {
    this.create_time = create_time;
  }

  public String getReply_mode() {
    return reply_mode;
  }

  public void setReply_mode(String reply_mode) {
    this.reply_mode = reply_mode;
  }

  public List<KeywordListInfo> getKeyword_list_info() {
    return keyword_list_info;
  }

  public void setKeyword_list_info(List<KeywordListInfo> keyword_list_info) {
    this.keyword_list_info = keyword_list_info;
  }

  public List<ReplayListInfo> getReply_list_info() {
    return reply_list_info;
  }

  public void setReply_list_info(List<ReplayListInfo> reply_list_info) {
    this.reply_list_info = reply_list_info;
  }

}
