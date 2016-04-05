package com.tracy.dto;

public class AutoReplyResponse {
  private Integer is_add_friend_reply_open;
  private Integer is_autoreply_open;
  private AutoreplyInfo add_friend_autoreply_info;
  private AutoreplyInfo message_default_autoreply_info;
  private KeyWordAutoreplyInfo keyWordAutoreplyInfo;

  public Integer getIs_add_friend_reply_open() {
    return is_add_friend_reply_open;
  }

  public void setIs_add_friend_reply_open(Integer is_add_friend_reply_open) {
    this.is_add_friend_reply_open = is_add_friend_reply_open;
  }

  public Integer getIs_autoreply_open() {
    return is_autoreply_open;
  }

  public void setIs_autoreply_open(Integer is_autoreply_open) {
    this.is_autoreply_open = is_autoreply_open;
  }

  public AutoreplyInfo getAdd_friend_autoreply_info() {
    return add_friend_autoreply_info;
  }

  public void setAdd_friend_autoreply_info(AutoreplyInfo add_friend_autoreply_info) {
    this.add_friend_autoreply_info = add_friend_autoreply_info;
  }

  public AutoreplyInfo getMessage_default_autoreply_info() {
    return message_default_autoreply_info;
  }

  public void setMessage_default_autoreply_info(AutoreplyInfo message_default_autoreply_info) {
    this.message_default_autoreply_info = message_default_autoreply_info;
  }

  public KeyWordAutoreplyInfo getKeyWordAutoreplyInfo() {
    return keyWordAutoreplyInfo;
  }

  public void setKeyWordAutoreplyInfo(KeyWordAutoreplyInfo keyWordAutoreplyInfo) {
    this.keyWordAutoreplyInfo = keyWordAutoreplyInfo;
  }

}
