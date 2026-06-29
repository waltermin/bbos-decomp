package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.apps.api.browser.BrowserServices;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.api.UrlRequestedInternalEvent;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.stack.FormData;
import org.w3c.dom.html2.HTMLElement;

public final class HttpAddressLinkVerb extends Verb {
   private String _url;
   private FormData _postData;
   private String _onClickAction;
   private BrowserContentImpl _browserContent;
   private String _target;
   private String _configUID;
   private HTMLElement _thisNode;
   private static int DESCRIPTION = 100;

   public HttpAddressLinkVerb(
      BrowserContentImpl browserContent, String url, String target, FormData postData, String action, HTMLElement thisNode, String configUID
   ) {
      super(341248, BrowserResources.getResourceBundle(), DESCRIPTION);
      this._url = url;
      this._postData = postData;
      this._onClickAction = action;
      this._target = target;
      this._thisNode = thisNode;
      this._browserContent = browserContent;
      this._configUID = configUID;
   }

   @Override
   public final Object invoke(Object context) {
      RenderingApplication renderingApplication = this._browserContent != null ? this._browserContent.getRenderingApplication() : null;
      long clickID = -1;
      if (renderingApplication != null && this._onClickAction != null && this._browserContent != null) {
         if (Application.isEventDispatchThread()) {
            renderingApplication.invokeRunnable(new HttpAddressLinkVerb$RunItOnAnotherThread(this, context));
            return null;
         }

         label62:
         try {
            clickID = System.currentTimeMillis();
            if (!this._browserContent.executeJavaScriptAction(this._thisNode, this._onClickAction, null, clickID)) {
               return null;
            }
         } finally {
            break label62;
         }
      }

      if (renderingApplication != null) {
         HttpHeaders requestHeaders = (HttpHeaders)(new Object());
         RenderingUtilities.setReferrer(requestHeaders, this._browserContent.getURL());
         byte[] postDataBytes = null;
         if (this._postData != null) {
            postDataBytes = this._postData.getBytes();
            String contentType = this._postData.getContentType();
            if (contentType != null) {
               requestHeaders.setProperty("Content-Type", contentType);
            }
         }

         int flags = this._browserContent.getSharedFlags() & -8 | 1;
         UrlRequestedEvent event = UrlRequestedInternalEvent.processUrlRequest(
            this._browserContent, this._url, this._target, postDataBytes, requestHeaders, false, flags, clickID
         );
         if (event != null) {
            renderingApplication.eventOccurred(event);
            return null;
         }
      } else if (this._postData == null && this._onClickAction == null) {
         BrowserServices.loadUrl(this._url, this._configUID);
      }

      return null;
   }
}
