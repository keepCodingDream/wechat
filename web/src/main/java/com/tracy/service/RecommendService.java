package com.tracy.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracy.dao.ICrudRecommendDao;
import com.tracy.dao.ICrudUserDao;
import com.tracy.model.Recommend;
import com.tracy.model.RecommendExample;
import com.tracy.model.User;

@Service
public class RecommendService {
  @Autowired
  private ICrudRecommendDao crudRecommendDao;
  @Autowired
  private ICrudUserDao crudUserDao;

  /**
   * 查看用户已经推荐了几个人 并返回他的openid
   * 
   * @return
   */
  public Map<String, String> queryRecommendCountByUid(Integer uid) {
    Map<String, String> response = new HashMap<String, String>();
    RecommendExample example = new RecommendExample();
    example.createCriteria().andFromIdEqualTo(uid);
    List<Recommend> recommends = crudRecommendDao.selectByExample(example);
    if (recommends != null && recommends.size() > 0) {
      response.put("size", String.valueOf(recommends.size()));
      response.put("openId", recommends.get(0).getFromOpenId());
    } else {
      response.put("size", "0");
      User user = crudUserDao.selectByPrimaryKey((long) uid);
      response.put("openId", user.getOpenid());
    }
    return response;
  }

  /**
   * @param fromId 推荐人user表的id
   * @param fromOpenId 推荐人的openid
   * @param toOpenId 被推荐人openId
   */
  public void insertRecommend(int fromId, String fromOpenId, String toOpenId) {
    Recommend recommend = new Recommend();
    recommend.setFromId(fromId);
    recommend.setFromOpenId(fromOpenId);
    recommend.setTime(System.currentTimeMillis());
    recommend.setToOpenId(toOpenId);
    crudRecommendDao.insert(recommend);
  }
}
