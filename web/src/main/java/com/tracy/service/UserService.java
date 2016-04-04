package com.tracy.service;

import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.tracy.dao.ICrudUserDao;
import com.tracy.dto.UserDTO;
import com.tracy.model.User;
import com.tracy.model.UserExample;
import com.tracy.util.ConvertUtil;
import com.tracy.util.HttpRequestUtil;

@Service
public class UserService {
  public static final Logger LOG = LoggerFactory.getLogger(UserService.class);
  private static final String url = "https://api.weixin.qq.com/cgi-bin/user/info";

  @Autowired
  private ICrudUserDao crudUserDao;
  @Autowired
  private AccessTokenService accessTokenService;

  /**
   * 从微信api获取用户信息
   * 
   * @param openId
   * @return
   */
  public UserDTO queryUserFromWeChat(String openId) {
    StringBuilder builder = new StringBuilder();
    UserDTO userDTO = null;
    builder.append("?access_token=" + accessTokenService.getAccessToken());
    builder.append("&openid=" + openId);
    builder.append("&lang=zh_CN");
    HttpResponse result = HttpRequestUtil.getRequest(url + builder.toString());
    if (result != null) {
      if (result.getStatusLine().getStatusCode() == 200) {
        try {
          String str = EntityUtils.toString(result.getEntity(), Charset.forName("utf-8"));
          LOG.info("User info:{}", str);
          userDTO = JSON.parseObject(str, UserDTO.class);
          LOG.info("Get user info from wechat success!User nick name:{}", userDTO.getNickname());
        } catch (Exception e) {
          LOG.error("Get user info fail!", e);
        }
      }
    }
    return userDTO;

  }

  /**
   * 插入用户信息
   * 
   * @param userDTO
   */
  public int insertUser(UserDTO userDTO) {
    User user = ConvertUtil.convertUserDTO2User(userDTO);
    crudUserDao.insertSelective(user);
    UserExample example = new UserExample();
    example.createCriteria().andOpenidEqualTo(userDTO.getOpenid());
    return crudUserDao.selectByExample(example).get(0).getId().intValue();
  }

}
