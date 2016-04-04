package com.tracy.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tracy.util.HttpRequestUtil;

@Service
public class AccessTokenService implements InitializingBean {
  public static final Logger LOG = LoggerFactory.getLogger(AccessTokenService.class);
  private LoadingCache<Integer, String> accessTokenCache;
  @Value("${appid}")
  private static String appid;
  @Value("${secret}")
  private static String secret;
  private static final String REQUEST_ACCESS_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
      + appid + "&secret=" + secret;

  public String getAccessToken() {
    String result = null;
    try {
      result = accessTokenCache.get(1);
    } catch (ExecutionException e) {
      LOG.error("GET access_token fail!", e);
    }
    return result;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    accessTokenCache = CacheBuilder.newBuilder().maximumSize(1L).expireAfterWrite(30, TimeUnit.MINUTES)
        .build(new CacheLoader<Integer, String>() {
          @Override
          public String load(Integer key) throws Exception {
            String value = null;
            try {
              LOG.info("Get access_token start");
              byte[] result = HttpRequestUtil.getHttpsRequest(REQUEST_ACCESS_URL);
              if (result != null) {
                try {
                  String str = new String(result, "utf-8");
                  JSONObject object = JSON.parseObject(str);
                  value = object.getString("access_token");
                  LOG.info("Get access_token :{} success!", value);
                } catch (Exception e) {
                  LOG.error("GET access_token fail!", e);
                }
              }
            } catch (Exception e) {
              LOG.error("GET access_token fail!", e);
            }
            return value;
          }
        });

  }

}
