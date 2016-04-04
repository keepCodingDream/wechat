package com.tracy.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.thoughtworks.xstream.XStream;
import com.tracy.dto.InputMessage;
import com.tracy.dto.OutputMessage;
import com.tracy.service.EventService;
import com.tracy.service.MessageService;
import com.tracy.util.Constants;
import com.tracy.util.Decript;
import com.tracy.util.ResponseUtil;
import com.tracy.util.SerializeXmlUtil;

/**
 * @author Tracy
 */
@Controller
public class MainController {
  public static final Logger LOG = LoggerFactory.getLogger(MainController.class);
  @Autowired
  private MessageService messageService;
  @Autowired
  private EventService eventService;
  public static final String TOKEN = "123456";

  @RequestMapping(value = "/user/test", method = RequestMethod.GET)
  @ResponseBody
  public Map<String, String> testHelloWorld() {
    Map<String, String> response = new HashMap<String, String>();
    response.put("data", "helloworld");
    return response;
  }

  @RequestMapping(value = "/authentication", method = RequestMethod.GET)
  @ResponseBody
  public String authentication(HttpServletRequest request, HttpServletResponse response) {
    PrintWriter out = null;
    String signature = request.getParameter("signature");
    String timestamp = request.getParameter("timestamp");
    String nonce = request.getParameter("nonce");
    String echostr = request.getParameter("echostr");
    try {
      out = response.getWriter();
    } catch (IOException e) {
      LOG.error("", e);
    }
    String sortString = Decript.sort(TOKEN, timestamp, nonce);
    String mytoken = Decript.SHA1(sortString);
    if (mytoken != null && mytoken != "" && mytoken.equals(signature)) {
      LOG.info("authentication success!{}", mytoken);
      out.write(echostr);
    } else {
      out.write(mytoken);
      LOG.error("authentication error!{}", mytoken);
    }
    return null;
  }

  @RequestMapping(value = "/authentication", method = RequestMethod.POST)
  @ResponseBody
  public void redirect(HttpServletRequest request, HttpServletResponse response) {
    try {
      response.setCharacterEncoding("UTF-8");
      XStream xs = SerializeXmlUtil.createXstream();
      xs.processAnnotations(InputMessage.class);
      xs.processAnnotations(OutputMessage.class);
      xs.alias("xml", InputMessage.class);
      InputMessage inputMessage = messageService.convertMessageRequest(request);
      LOG.info("Myserver:{} message from {} said :{}", inputMessage.getToUserName(), inputMessage.getFromUserName(),
          inputMessage.getContent());
      switch (inputMessage.getMsgType()) {
        case Constants.TEXT:
          ResponseUtil.returnTextResponse(response, inputMessage.getFromUserName(), inputMessage.getToUserName(),
              inputMessage.getContent());
          break;
        case Constants.IMAGE:
          ResponseUtil.returnPicResponse(response, inputMessage.getFromUserName(), inputMessage.getToUserName(),
              inputMessage.getMediaId());
          break;
        case Constants.EVENT:
          eventService.enventDispatcher(response, inputMessage);
          break;
        default:
          ResponseUtil.returnTextResponse(response, inputMessage.getFromUserName(), inputMessage.getToUserName(),
              "我听不懂你在说什么");
          break;
      }
    } catch (Exception e) {
      LOG.error("Error in prase request!", e);
    }
  }
}
