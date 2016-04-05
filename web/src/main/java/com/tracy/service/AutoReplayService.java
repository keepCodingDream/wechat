package com.tracy.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tracy.dto.AutoReplyResponse;
import com.tracy.util.HttpRequestUtil;

public class AutoReplayService implements InitializingBean {
  public static final Logger LOG = LoggerFactory.getLogger(AccessTokenService.class);
  @Autowired
  private AccessTokenService accessTokenService;
  private LoadingCache<Integer, AutoReplyResponse> autoReplyCache;
  private String AUTO_REPLAY_REQUEST_URL = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info?access_token=";

  @Override
  public void afterPropertiesSet() throws Exception {
    autoReplyCache = CacheBuilder.newBuilder().maximumSize(1L).expireAfterWrite(10, TimeUnit.MINUTES)
        .build(new CacheLoader<Integer, AutoReplyResponse>() {
          String url = AUTO_REPLAY_REQUEST_URL + accessTokenService.getAccessToken();

          @Override
          public AutoReplyResponse load(Integer key) throws Exception {
            LOG.info("Reload auto_replay_infod start! url:{}", url);
            AutoReplyResponse response = null;
            try {
              byte[] result = HttpRequestUtil.getHttpsRequest(url);
              String str = new String(result, "utf-8");
              response = JSON.parseObject(str, AutoReplyResponse.class);
            } catch (Exception e) {
              LOG.error("Reload auto replay fail!");
            }
            return response;
          }

        });
  }

  /**
   * @return 完整的自动回复
   */
  public AutoReplyResponse getAutoReplayInfo() {
    AutoReplyResponse response = null;
    try {
      response = autoReplyCache.get(1);
    } catch (ExecutionException e) {
      LOG.error("Get aotu replay info fail!");
    }
    return response;
  }
}
