package cn.hb712.webapp.Utils;

import android.webkit.JavascriptInterface;

import cn.hb712.webapp.MainApplication;

/**
 * @author hiYuzu
 * @version V1.0
 * @description
 * @date 2019/10/8 14:47
 */
public class ForHtmlJavascript {
    @JavascriptInterface
    public String getToken() {
        return WebViewCookiesUtils.getWebViewCookies(MainApplication.baseUrl);
    }
}
