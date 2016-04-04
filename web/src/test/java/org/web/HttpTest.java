package org.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tracy.dto.ActionInfo;
import com.tracy.dto.GeneratePicture;
import com.tracy.dto.Scene;
import com.tracy.util.Constants;
import com.tracy.util.HttpRequestUtil;

@SuppressWarnings("deprecation")
public class HttpTest {
  public static String GENER_PIC_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
  public static String tickets = "gQG37zoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL0JqbklwcVBtbTZMaUdIQThveGVpAAIEYlUAVwME\\/4wnAA==";
  private static final String REQUEST_PIC_BY_TICKET_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
  private static String TOKEN = "BxPY6Rb3rgCyfKZFt1ejATSxS6TlR4ktI5EbkXbKsSqISofDSRruBvO7HSRrZq_5s0qiBSH-yU0QtZ6-VR0H2iCYxSCq4aT1CCnRyBA3Q9UFSXcAAAANS";
  public static String UPLOAD_PIC_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=";

  public static void main(String[] args) {
    // tickets = testGeneratePic();
    // getPicByTicket();
    // uploadPIC();
    addKefu();
  }

  public static String testGeneratePic() {
    String resString = null;
    GeneratePicture picture = new GeneratePicture();
    ActionInfo actionInfo = new ActionInfo();
    Scene scene = new Scene();
    scene.setScene_id((int) System.currentTimeMillis());
    actionInfo.setScene(scene);
    picture.setAction_info(actionInfo);
    picture.setAction_name(Constants.QR_SCENE);// 临时二维码
    picture.setExpire_seconds(2591999);
    String jsonPic = JSON.toJSONString(picture);
    HttpResponse result = HttpRequestUtil.postJsonRequest(GENER_PIC_URL + TOKEN, jsonPic);
    if (result != null) {
      try {
        String str = EntityUtils.toString(result.getEntity());
        System.out.println(str);
        JSONObject object = JSON.parseObject(str);
        resString = object.getString("ticket");
      } catch (Exception e) {
      }
    }
    return resString;
  }

  public static void getPicByTicket() {
    OutputStream os = null;
    // File tempFile = null;
    // try {
    // // TODO 使用临时文件
    // tempFile = File.createTempFile(new SimpleDateFormat("yyyy_MM_dd").format(new Date()), null);
    // } catch (IOException e1) {
    // // TODO Auto-generated catch block
    // e1.printStackTrace();
    // }
    try {
      String entickets = URLEncoder.encode(tickets, "UTF-8");
      byte httpResponse[] = HttpRequestUtil.getHttpsRequest(REQUEST_PIC_BY_TICKET_URL + entickets);
      String fileName = "/Users/tracy/img.jpg";
      File file = new File(fileName);
      if (!file.exists()) {
        file.createNewFile();
      }
      os = new FileOutputStream(file);
      os.write(httpResponse, 0, httpResponse.length);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @SuppressWarnings("resource")
  public static void uploadPIC() {
    File file = new File("/Users/tracy/img.jpg");
    HttpClient httpClient = new DefaultHttpClient();
    StringBuilder builder = new StringBuilder(UPLOAD_PIC_URL);
    builder.append(TOKEN);
    builder.append("&type=image");
    HttpPost httpPost = new HttpPost(builder.toString());
    HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody("media", file).addTextBody("nonce", "").build();
    httpPost.setEntity(entity);
    httpPost.setHeader("Content-Type", ContentType.MULTIPART_FORM_DATA.toString());
    // 执行POST请求
    HttpResponse response = null;
    try {
      response = httpClient.execute(httpPost);
      // 获取响应实体
      HttpEntity entitys = response.getEntity();
      System.out.println(EntityUtils.toString(entitys));
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void addKefu() {
    String url = "https://api.weixin.qq.com/customservice/kfaccount/add?access_token=E-7GgpcNgtTEBoKBfUcTbhcifNc7CkxsOMOcV5KkgdCYsm-dp_pZ9JAU1SU4h5yVSAFx0hhZJNL2zNo4vYsQcHkQJ9WWDZiDDIhZD5BFhNakwULvyy3uibO6dZOndoBZREXeAEAVIB";
    JSONObject object = new JSONObject();
    object.put("kf_account", "test@gh_912e6e2b8e32");
    object.put("nickname", "Tracy");
    object.put("password", "e10adc3949ba59abbe56e057f20f883e");
    System.out.println(object.toJSONString());
    HttpResponse response = HttpRequestUtil.postJsonRequest(url, object.toJSONString());
    HttpEntity entitys = response.getEntity();
    try {
      System.out.println(EntityUtils.toString(entitys, Charset.forName("utf-8")));
    } catch (ParseException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
