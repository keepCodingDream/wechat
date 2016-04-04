package com.tracy.service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.tracy.dto.InputMessage;
import com.tracy.dto.ResponsePicture;
import com.tracy.dto.UserDTO;
import com.tracy.util.Constants;
import com.tracy.util.HttpRequestUtil;
import com.tracy.util.ResponseUtil;

@Service
public class EventService {
  public static final Logger LOG = LoggerFactory.getLogger(EventService.class);
  @Autowired
  private UserService userService;
  @Autowired
  private PictureService pictureService;
  @Autowired
  private AccessTokenService accessTokenService;
  @Autowired
  private RecommendService recommendService;
  private static final String REQUEST_PIC_BY_TICKET_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
  public static String UPLOAD_PIC_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=";

  /**
   * 区分事件类型
   * 
   * @param response
   * @param inputMessage
   */
  public void enventDispatcher(HttpServletResponse response, InputMessage inputMessage) {
    LOG.info("Event message received. Event type is {}", inputMessage.getEvent());
    switch (inputMessage.getEvent()) {
      case Constants.EVENT_TYPE_SUBSCRIBE:
        dealSubscribeEvent(response, inputMessage);
        break;
      default:
        break;
    }
  }

  /**
   * 处理关注事件
   * 
   * @param response
   */
  private void dealSubscribeEvent(HttpServletResponse response, InputMessage inputMessage) {
    String openId = inputMessage.getFromUserName();
    UserDTO userDTO = userService.queryUserFromWeChat(openId);
    String eventKey = inputMessage.getEventKey();
    int uid = 0;
    try {
      if (StringUtils.isEmpty(userDTO.getOpenid())) {
        userDTO.setOpenid("test" + System.currentTimeMillis());
        LOG.warn("NO OpenId!:{}", userDTO);
      }
      uid = userService.insertUser(userDTO);
    } catch (Exception e) {
      LOG.error("Insert user info fail!", e);
      return;// 如果uid不存在，就不能实现推荐功能，所以直接return
    }
    if (eventKey != null && eventKey.contains("qrscene_")) {
      // 包涵此字段是扫描了带参数的二维码
      try {
        String[] indexes = eventKey.split("_");
        int _id = Integer.valueOf(indexes[1]);
        LOG.info("Event key is:{},scene_id is:{}", eventKey, indexes[1]);
        Map<String, String> recoMap = recommendService.queryRecommendCountByUid(_id);
        int count = Integer.valueOf(recoMap.get("size"));
        count++;
        String fromOpenId = recoMap.get("openId");// 推荐人openId
        StringBuilder builder = new StringBuilder();
        builder.append("你的好友:");
        builder.append(userDTO.getNickname());
        builder.append(" 扫描了你的二维码加入社区。你已经成功推荐:");
        builder.append(count);
        if (count == 2) {
          builder.append("人。哈哈，谢谢你的推荐，活动是骗人的！～");
        } else {
          builder.append("人。只需要推荐2人,即可获得神秘礼物哦，赶快行动吧！");
        }
        // 发送提示消息
        HttpRequestUtil.sendTextMessageToUser(fromOpenId, builder.toString(), accessTokenService.getAccessToken());
        // 插入recommend
        recommendService.insertRecommend(_id, fromOpenId, inputMessage.getFromUserName());
      } catch (Exception e) {
        LOG.error("Resolve recommend fail!", e);
      }

    }
    // 根据uid获取生成二维码，拿到tickets
    ResponsePicture picture = pictureService.generatePicture(uid);
    LOG.info("Pic of userId:{} tickets is:{}", uid, picture.getTicket());
    String tickts = "";
    try {
      tickts = URLEncoder.encode(picture.getTicket(), "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOG.error("Encode tickets:{} error!", picture.getTicket(), e);
    }
    if (StringUtils.isEmpty(tickts)) {
      LOG.error("NO ticket ,tiket is {}", tickts);
      return;// ticket为空，生成二维码失败，不返回。
    }
    LOG.info("Tickets after encode is {}", tickts);
    byte picByte[] = HttpRequestUtil.getHttpsRequest(REQUEST_PIC_BY_TICKET_URL + tickts);
    String picFile = pictureService.savePictureOfTicket(picByte);
    if (StringUtils.isEmpty(picFile)) {
      return;
    }
    File file = new File(picFile);
    StringBuilder builder = new StringBuilder(UPLOAD_PIC_URL);
    builder.append(accessTokenService.getAccessToken());
    builder.append("&type=image");
    LOG.info("File upload url {}", builder.toString());
    String mediaId = HttpRequestUtil.postHttpsPic(builder.toString(), file);
    if (!StringUtils.isEmpty(mediaId)) {
      ResponseUtil.returnPicResponse(response, inputMessage.getFromUserName(), inputMessage.getToUserName(), mediaId);
      HttpRequestUtil.sendTextMessageToUser(inputMessage.getFromUserName(),
          "将图中的二维码分享给好友，只要有超过2好友扫描了你的分享的二维码以后，我们会送你神秘礼物一份！", accessTokenService.getAccessToken());
    }
  }
}
