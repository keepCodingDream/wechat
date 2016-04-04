package com.tracy.util;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Tracy
 */
@SuppressWarnings("deprecation")
public class HttpRequestUtil {
  public static final Logger LOG = LoggerFactory.getLogger(HttpRequestUtil.class);
  private static HttpClient httpClient = new DefaultHttpClient();
  private static HttpClient httpClientForSSL = new DefaultHttpClient();
  static {
    try {
      SSLContext ctx = SSLContext.getInstance("SSL");
      X509TrustManager xtm = new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
          return new X509Certificate[] {};
        }
      };
      // 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
      ctx.init(null, new TrustManager[] { xtm }, null);
      SSLSocketFactory sf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
      Scheme sch = new Scheme("https", 443, sf);
      httpClientForSSL.getConnectionManager().getSchemeRegistry().register(sch);
    } catch (Exception e) {
      LOG.error("Init https for SSL error:", e);
    }
  }

  /**
   * 发送Post方式的json数据
   * 
   * @param url url
   * @param content json格式的请求参数
   * @return HttpResponse
   */
  public static HttpResponse postJsonRequest(String url, String content) {
    HttpPost method = new HttpPost(url);
    HttpResponse result = null;
    try {
      if (null != content) {
        StringEntity entity = new StringEntity(content, "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        method.setEntity(entity);
      }
      result = httpClient.execute(method);
    } catch (Exception e) {
      LOG.error("POST request fial! url:{}", url, e);
    }
    return result;
  }

  /**
   * GET 请求
   * 
   * @param url
   * @return
   */
  public static HttpResponse getRequest(String url) {
    HttpGet method = new HttpGet(url);
    HttpResponse result = null;
    try {
      result = httpClient.execute(method);
    } catch (IOException e) {
      LOG.error("GET request fial! url:{}", url, e);
    }
    return result;
  }

  /**
   * ssl Get
   * 
   * @param url
   * @return
   */
  public static byte[] getHttpsRequest(String url) {
    byte[] bs = null;
    try {
      HttpGet httpGet = new HttpGet(url);
      HttpResponse response = httpClientForSSL.execute(httpGet);
      HttpEntity entity = response.getEntity();
      bs = IOUtils.toByteArray(entity.getContent());
      if (null != entity) {
        EntityUtils.consume(entity); // Consume response content
      }
    } catch (Exception e) {
      LOG.error("HTTPS reuqest fail!", e);
    }
    return bs;
  }

  /**
   * HTTPS 方式上传图片
   * 
   * @param url
   * @param httpResponse
   * @return 图片mediaId
   * @throws IllegalStateException
   * @throws IOException
   */
  public static String postHttpsPic(String url, File file) {
    String mediaId = null;
    try {
      HttpPost httpPost = new HttpPost(url);
      HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("media", file).addTextBody("nonce", "").build();
      httpPost.setEntity(entity);
      httpPost.setHeader("Content-Type", ContentType.MULTIPART_FORM_DATA.toString());
      HttpResponse response = null;
      response = httpClient.execute(httpPost);
      HttpEntity entitys = response.getEntity();
      String responseString = EntityUtils.toString(entitys);
      JSONObject object = JSON.parseObject(responseString);
      mediaId = object.getString("media_id");
      LOG.info("Upload pic success!MediaId is {}", mediaId);
    } catch (Exception e) {
      LOG.error("Upload fail!", e);
    }
    return mediaId;
  }
}
