package io.github.rcarlosdasilva.weixin.model.request.custom;

import io.github.rcarlosdasilva.weixin.common.ApiAddress;
import io.github.rcarlosdasilva.weixin.model.request.base.BasicRequest;

/**
 * 获取在线客服列表请求模型
 * 
 * @author Dean Zhao (rcarlosdasilva@qq.com)
 */
public class CustomAccountListOnlineRequest extends BasicRequest {

  public CustomAccountListOnlineRequest() {
    this.path = ApiAddress.URL_CUSTOM_ACCOUNT_LIST_ONLINE;
  }

}
