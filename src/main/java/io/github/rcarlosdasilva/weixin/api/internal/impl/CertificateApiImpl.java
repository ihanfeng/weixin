package io.github.rcarlosdasilva.weixin.api.internal.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.rcarlosdasilva.weixin.api.internal.BasicApi;
import io.github.rcarlosdasilva.weixin.api.internal.CertificateApi;
import io.github.rcarlosdasilva.weixin.core.cache.impl.AccessTokenCache;
import io.github.rcarlosdasilva.weixin.core.cache.impl.AccountCache;
import io.github.rcarlosdasilva.weixin.core.cache.impl.JsTicketCache;
import io.github.rcarlosdasilva.weixin.core.handler.NotificationHandlerProxy;
import io.github.rcarlosdasilva.weixin.model.Account;
import io.github.rcarlosdasilva.weixin.model.request.certificate.AccessTokenRequest;
import io.github.rcarlosdasilva.weixin.model.request.certificate.JsTicketRequest;
import io.github.rcarlosdasilva.weixin.model.request.certificate.WaAccessTokenRefreshRequest;
import io.github.rcarlosdasilva.weixin.model.request.certificate.WaAccessTokenRequest;
import io.github.rcarlosdasilva.weixin.model.request.certificate.WaAccessTokenVerifyRequest;
import io.github.rcarlosdasilva.weixin.model.response.certificate.AccessTokenResponse;
import io.github.rcarlosdasilva.weixin.model.response.certificate.JsTicketResponse;
import io.github.rcarlosdasilva.weixin.model.response.certificate.WaAccessTokenResponse;

/**
 * 认证相关API实现
 *
 * @author Dean Zhao (rcarlosdasilva@qq.com)
 */
public class CertificateApiImpl extends BasicApi implements CertificateApi {

  private static final Logger logger = LoggerFactory.getLogger(NotificationHandlerProxy.class);

  private final Object lock = new Object();

  @Override
  public String askAccessToken() {
    AccessTokenResponse token = AccessTokenCache.instance().get(this.accountKey);

    if (null == token || token.expiredOrUseless()) {
      synchronized (this.lock) {
        if (null == token || token.expiredOrUseless()) {
          token = requestAccessToken();
        }
      }
    }

    return null == token ? null : token.getAccessToken();
  }

  @Override
  public void refreshAccessToken() {
    requestAccessToken();
  }

  /**
   * 真正请求access_token代码.
   *
   * @return 请求结果
   */
  private synchronized AccessTokenResponse requestAccessToken() {
    Account account = AccountCache.instance().get(this.accountKey);
    AccessTokenRequest requestModel = new AccessTokenRequest();
    requestModel.setAppId(account.getAppId());
    requestModel.setAppSecret(account.getAppSecret());

    AccessTokenResponse responseModel = get(AccessTokenResponse.class, requestModel);

    if (responseModel != null) {
      responseModel.updateExpireAt();
      AccessTokenCache.instance().put(this.accountKey, responseModel);
      logger.debug("获取access_token：[{}]", responseModel.getAccessToken());
      return responseModel;
    }

    return null;
  }

  @Override
  public final String askJsTicket() {
    JsTicketResponse ticket = JsTicketCache.instance().get(this.accountKey);

    if (ticket == null || ticket.expiredOrUseless()) {
      synchronized (this.lock) {
        if (null == ticket || ticket.expiredOrUseless()) {
          ticket = requestJsTicket();
        }
      }
    }

    return null == ticket ? null : ticket.getJsTicket();
  }

  /**
   * 真正请求jsapi_ticket代码.
   *
   * @return 请求结果
   */
  private synchronized JsTicketResponse requestJsTicket() {
    JsTicketRequest requestModel = new JsTicketRequest();

    JsTicketResponse responseModel = get(JsTicketResponse.class, requestModel);

    if (responseModel != null) {
      responseModel.updateExpireAt();
      JsTicketCache.instance().put(this.accountKey, responseModel);
      logger.debug("获取jsapi_ticket：[{}]", responseModel.getJsTicket());
      return responseModel;
    }

    return null;
  }

  @Override
  public WaAccessTokenResponse askWebAuthorizeAccessToken(String code) {
    Account account = AccountCache.instance().get(this.accountKey);
    WaAccessTokenRequest requestModel = new WaAccessTokenRequest();
    requestModel.setAppId(account.getAppId());
    requestModel.setAppSecret(account.getAppSecret());
    requestModel.setCode(code);

    return get(WaAccessTokenResponse.class, requestModel);
  }

  @Override
  public WaAccessTokenResponse refreshWebAuthorizeAccessToken(String refreshToken) {
    Account account = AccountCache.instance().get(this.accountKey);
    WaAccessTokenRefreshRequest requestModel = new WaAccessTokenRefreshRequest();
    requestModel.setAppId(account.getAppId());
    requestModel.setRefreshToken(refreshToken);

    return get(WaAccessTokenResponse.class, requestModel);
  }

  @Override
  public boolean verifyWebAuthorizeAccessToken(String accessToken, String openId) {
    WaAccessTokenVerifyRequest requestModel = new WaAccessTokenVerifyRequest();
    requestModel.setAccessToken(accessToken);
    requestModel.setOpenId(openId);

    return get(Boolean.class, requestModel);
  }

}
