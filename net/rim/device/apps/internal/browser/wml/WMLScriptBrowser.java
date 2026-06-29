package net.rim.device.apps.internal.browser.wml;

import com.fourthpass.wmls.IBrowser;
import com.fourthpass.wmls.L3;
import com.fourthpass.wmls.StringValue;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RequestedResource;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.util.RendererControl;

final class WMLScriptBrowser implements IBrowser {
   private WMLContext _context;
   private String _url;
   private RenderingApplication _renderingApplication;
   private WMLBrowserContent _browserContent;
   private WMLScriptBrowserContent _scriptBrowserContent;

   public WMLScriptBrowser(WMLContext context, String url, RenderingApplication renderingApplication, WMLBrowserContent wmlBrowserContent) {
      this._context = context;
      this._url = url;
      this._renderingApplication = renderingApplication;
      this._browserContent = wmlBrowserContent;
   }

   final void setWMLScriptBrowserContent(WMLScriptBrowserContent scriptBrowserContent) {
      this._scriptBrowserContent = scriptBrowserContent;
   }

   @Override
   public final String getCurrentCard(boolean withCardId) {
      if (this._browserContent == null) {
         return "";
      }

      String cardUrl = this._browserContent.getURL();
      String baseUrl = ((StringValue)L3.getBase(this)).getValue();
      int length = Math.min(cardUrl.length(), baseUrl.length());
      int i = 0;

      while (i < length && cardUrl.charAt(i) == baseUrl.charAt(i)) {
         i++;
      }

      int lastSlashIndex = cardUrl.substring(0, i).lastIndexOf(47);
      if (lastSlashIndex > -1) {
         cardUrl = cardUrl.substring(lastSlashIndex + 1);
      }

      if (cardUrl.indexOf(35) == -1 && withCardId) {
         cardUrl = ((StringBuffer)(new Object())).append(cardUrl).append('#').append(this._browserContent.getWMLBrowserField().getCurrentCardId()).toString();
      }

      return cardUrl;
   }

   @Override
   public final void newContext() {
      this._context.clear();
   }

   @Override
   public final String getCurrentUrl() {
      return this._url;
   }

   @Override
   public final String getVar(String var) {
      return this._context.get(var);
   }

   @Override
   public final String go(String url) {
      if (this._scriptBrowserContent == null) {
         return "";
      }

      this._scriptBrowserContent.addPostScriptAction(url, 2);
      return "";
   }

   @Override
   public final String prev() {
      if (this._scriptBrowserContent == null) {
         return "";
      }

      this._scriptBrowserContent.addPostScriptAction(null, 1);
      return "";
   }

   @Override
   public final String refresh() {
      if (this._browserContent != null) {
         this._browserContent.getWMLBrowserField().refresh();
      }

      return "";
   }

   @Override
   public final void setVar(String var, String value) {
      this._context.put(var, value);
   }

   @Override
   public final String loadString(String targetContentType, String url) {
      if (!targetContentType.startsWith("text/")) {
         return null;
      }

      if (this._renderingApplication == null) {
         return null;
      }

      HttpHeaders requestHeaders = (HttpHeaders)(new Object());
      RenderingUtilities.setReferrer(requestHeaders, this._url);
      RequestedResource resource = (RequestedResource)(new Object(url, requestHeaders, 0));
      HttpConnection conn = this._renderingApplication.getResource(resource, null);
      if (conn == null) {
         return null;
      }

      try {
         int status = conn.getResponseCode();
         if (status >= 200 && status < 300) {
            String contentType = conn.getType();
            if (contentType == null) {
               return null;
            }

            if (!contentType.startsWith(targetContentType.trim())) {
               return "0xafe0c74c3e6cf312L 406";
            }

            byte[] data = RendererControl.readBytesFromInputStream(conn.openInputStream());
            return (String)(data != null ? new Object(data) : null);
         } else {
            return ((StringBuffer)(new Object("0xafe0c74c3e6cf312L "))).append(status).toString();
         }
      } finally {
         ;
      }
   }
}
