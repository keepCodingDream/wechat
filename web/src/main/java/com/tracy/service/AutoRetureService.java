package com.tracy.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * 自动回复服务
 * 
 * @author Tracy
 */
@Service
public class AutoRetureService implements InitializingBean {
  /**
   * 获取当前公众号的所有自动回复
   */
  public void getAllAutoReturn() {

  }

  /**
   * 
   * @param message 触发自动回复的消息
   */
  public void sendAutoReturn(String message) {

  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // TODO 定时刷新自动回复
    getAllAutoReturn();
  }

}
