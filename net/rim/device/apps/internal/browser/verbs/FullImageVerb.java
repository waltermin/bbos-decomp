package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.browser.field.BrowserContentBaseImpl;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.api.UrlRequestedInternalEvent;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class FullImageVerb extends Verb {
   private String _url;
   private BrowserContentBaseImpl _browserContent;
   private static final int DESCRIPTION = 587;

   public FullImageVerb(String url, BrowserContentBaseImpl browserContent) {
      super(341504);
      this._url = url;
      this._browserContent = browserContent;
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(587);
   }

   @Override
   public final Object invoke(Object context) {
      if (this._browserContent == null) {
         return null;
      }

      RenderingApplication renderingApplication = this._browserContent.getRenderingApplication();
      if (renderingApplication != null) {
         HttpHeaders requestHeaders = (HttpHeaders)(new Object());
         RenderingUtilities.setTranscodeHeader(requestHeaders, false);
         RenderingUtilities.setReferrer(requestHeaders, this._browserContent.getURL());
         int flags = this._browserContent.getSharedFlags() | 1;
         UrlRequestedEvent event = UrlRequestedInternalEvent.processUrlRequest(this._browserContent, this._url, null, null, requestHeaders, false, flags);
         if (event != null) {
            renderingApplication.eventOccurred(event);
         }
      }

      return null;
   }
}
