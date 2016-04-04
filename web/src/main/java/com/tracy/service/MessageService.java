package com.tracy.service;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.thoughtworks.xstream.XStream;
import com.tracy.dto.InputMessage;
import com.tracy.dto.OutputMessage;
import com.tracy.util.SerializeXmlUtil;

@Service
public class MessageService {
  public static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

  public InputMessage convertMessageRequest(HttpServletRequest request) {
    InputMessage inputMsg = null;
    try {
      ServletInputStream in = request.getInputStream();
      XStream xs = SerializeXmlUtil.createXstream();
      xs.processAnnotations(InputMessage.class);
      xs.processAnnotations(OutputMessage.class);
      xs.alias("xml", InputMessage.class);
      StringBuilder xmlMsg = new StringBuilder();
      byte[] b = new byte[4096];
      for (int n; (n = in.read(b)) != -1;) {
        xmlMsg.append(new String(b, 0, n, "UTF-8"));
      }
      // 将xml内容转换为InputMessage对象
      inputMsg = (InputMessage) xs.fromXML(xmlMsg.toString());
    } catch (Exception e) {
      LOG.error("Convert received message error!", e);
    }
    return inputMsg;
  }
}
