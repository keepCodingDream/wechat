package com.tracy.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.tracy.util.XStreamCDATA;

public class MediaIdMessage {
  @XStreamAlias("MediaId")
  @XStreamCDATA
  private String MediaId;

  public String getMediaId() {
    return MediaId;
  }

  public void setMediaId(String mediaId) {
    MediaId = mediaId;
  }

}