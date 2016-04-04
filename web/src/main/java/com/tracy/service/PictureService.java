package com.tracy.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.tracy.dto.ActionInfo;
import com.tracy.dto.GeneratePicture;
import com.tracy.dto.ResponsePicture;
import com.tracy.dto.Scene;
import com.tracy.util.Constants;
import com.tracy.util.HttpRequestUtil;

@Service
public class PictureService {
  public static final Logger LOG = LoggerFactory.getLogger(ResponsePicture.class);
  /**
   * 生成二维码
   */
  public static String GENER_PIC_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
  /**
   * 上传图片
   */
  public static String UPLOAD_PIC_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=";
  @Autowired
  private AccessTokenService accessTokenService;

  /**
   * @param uid 二维码参数
   * @param isLimit 是否永久
   * @param info 介绍
   * @return 图片信息
   */
  public ResponsePicture generatePicture(Integer uid) {

    ResponsePicture pictureResponse = null;
    GeneratePicture picture = new GeneratePicture();
    picture.setAction_name(Constants.QR_SCENE);// 临时二维码
    picture.setExpire_seconds(2591999);
    ActionInfo actionInfo = new ActionInfo();
    Scene scene = new Scene();
    scene.setScene_id(uid);
    actionInfo.setScene(scene);
    picture.setAction_info(actionInfo);
    String jsonPic = JSON.toJSONString(picture);
    HttpResponse result = HttpRequestUtil.postJsonRequest(GENER_PIC_URL + accessTokenService.getAccessToken(), jsonPic);
    if (result != null) {
      if (result.getStatusLine().getStatusCode() == 200) {
        try {
          String str = EntityUtils.toString(result.getEntity());
          pictureResponse = JSON.parseObject(str, ResponsePicture.class);
        } catch (Exception e) {
          LOG.error("Generate pic fail!", e);
        }
      }
    }
    if (pictureResponse == null) {
      LOG.error("Generate pic fail!");
    }
    return pictureResponse;
  }

  /**
   * 将图片流写入服务器。
   * 
   * @param bytes 图片流
   * @return 文件地址
   */
  public String savePictureOfTicket(byte[] bytes) {
    OutputStream os = null;
    String fileName = null;
    try {
      fileName = "/usr/pic/img" + System.currentTimeMillis() + ".jpg";
      File file = new File(fileName);
      if (!file.exists()) {
        file.createNewFile();
      }
      os = new FileOutputStream(file);
      os.write(bytes, 0, bytes.length);
    } catch (Exception e) {
      LOG.error("Write pic to file :{} fail", fileName, e);
      fileName = null;
    } finally {
      try {
        os.close();
      } catch (IOException e) {
      }
    }
    return fileName;
  }
}
