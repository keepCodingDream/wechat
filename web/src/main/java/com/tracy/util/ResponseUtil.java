package com.tracy.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.tracy.dto.ImageMessage;
import com.tracy.dto.InputMessage;
import com.tracy.dto.OutputMessage;
import com.tracy.util.Constants;
import com.tracy.util.SerializeXmlUtil;

/**
 * 处理返回给用的信息
 * 
 * @author Tracy
 */
public class ResponseUtil {
  public static final Logger LOG = LoggerFactory.getLogger(ResponseUtil.class);
  private static XStream xs = null;
  static {
    xs = SerializeXmlUtil.createXstream();
    xs.processAnnotations(InputMessage.class);
    xs.processAnnotations(OutputMessage.class);
    xs.alias("xml", InputMessage.class);
  }

  /**
   * 返回text信息
   * 
   * @param response
   * @param content 要发送的内容
   */
  public static void returnTextResponse(HttpServletResponse response, String toUsername, String fromUserName,
      String content) {
    StringBuffer str = new StringBuffer();
    str.append("<xml>");
    str.append("<ToUserName><![CDATA[" + toUsername + "]]></ToUserName>");
    str.append("<FromUserName><![CDATA[" + fromUserName + "]]></FromUserName>");
    str.append("<CreateTime>" + System.currentTimeMillis() + "</CreateTime>");
    str.append("<MsgType><![CDATA[" + Constants.TEXT + "]]></MsgType>");
    str.append("<Content><![CDATA[" + content + "]]></Content>");
    str.append("</xml>");
    LOG.info("Message will send:{}", str);
    try {
      response.getWriter().write(str.toString());
    } catch (IOException e) {
      LOG.error("Send text message error!", e);
    }
  }

  public static void returnPicResponse(HttpServletResponse response, String toUsername, String fromUsername,
      String mediaId) {
    OutputMessage outputMsg = new OutputMessage();
    outputMsg.setFromUserName(fromUsername);
    outputMsg.setToUserName(toUsername);
    outputMsg.setCreateTime(System.currentTimeMillis());
    outputMsg.setMsgType(Constants.IMAGE);
    ImageMessage images = new ImageMessage();
    images.setMediaId(mediaId);
    outputMsg.setImage(images);
    LOG.info("Message with pic will send:{}", xs.toXML(outputMsg));
    try {
      PrintWriter out = response.getWriter();
      out.print(xs.toXML(outputMsg));
      out.flush();
      out.close();
    } catch (IOException e) {
      LOG.error("Send pic message error!", e);
    }
  }

}
