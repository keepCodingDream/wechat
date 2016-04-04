package com.tracy.util;

import com.tracy.dto.UserDTO;
import com.tracy.model.User;

public class ConvertUtil {
  public static User convertUserDTO2User(UserDTO userDTO) {
    User user = null;
    if (userDTO != null) {
      user = new User();
      user.setCity(userDTO.getCity());
      user.setCountry(userDTO.getCountry());
      user.setGroupid(userDTO.getGroupid());
      user.setHeadimgurl(userDTO.getHeadimgurl());
      user.setHeadimgurl(userDTO.getHeadimgurl());
      user.setLanguage(userDTO.getLanguage());
      user.setNickname(userDTO.getNickname());
      user.setOpenid(userDTO.getOpenid());
      user.setProvince(userDTO.getProvince());
      user.setRemark(userDTO.getRemark());
      user.setSex(userDTO.getSex());
      user.setSubscribe(userDTO.getSubscribe());
      user.setUnionid(userDTO.getUnionid());
    }
    return user;
  }
}
