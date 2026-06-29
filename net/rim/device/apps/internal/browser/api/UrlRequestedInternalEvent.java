package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.javascript.JavaScriptRegistry;
import net.rim.device.apps.internal.browser.page.BrowserContentImpl;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.cldc.io.utility.URIDecoder;

public final class UrlRequestedInternalEvent extends UrlRequestedEvent {
   private boolean _submitOffline;
   private String _label;
   private String _target;
   private HttpHeaders _offlineParameters;
   private long _clickID = -1;

   public UrlRequestedInternalEvent(Object src, String url, byte[] postData, HttpHeaders headers, boolean programmatic, int flags, String label) {
      super(src, url, postData, headers, programmatic, flags);
      this._label = label;
   }

   public UrlRequestedInternalEvent(Object src, String url, String target, byte[] postData, HttpHeaders headers, boolean programmatic, int flags) {
      super(src, url, postData, headers, programmatic, flags);
      this._target = target;
   }

   public UrlRequestedInternalEvent(Object src, String url, String target, byte[] postData, HttpHeaders headers, boolean programmatic, int flags, long clickID) {
      this(src, url, target, postData, headers, programmatic, flags);
      this._clickID = clickID;
   }

   public final boolean isSubmitOffline() {
      return this._submitOffline;
   }

   public final void setSubmitOffline(boolean submitOffline) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final String getLabel() {
      return this._label;
   }

   public final String getTarget() {
      return this._target;
   }

   public final HttpHeaders getOfflineParameters() {
      return this._offlineParameters;
   }

   public final void setOfflineParameters(HttpHeaders headers) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final long getClickID() {
      return this._clickID;
   }

   public static final UrlRequestedInternalEvent processUrlRequest(
      Object src, String url, String target, byte[] postData, HttpHeaders headers, boolean programmatic, int flags
   ) {
      return processUrlRequest(src, url, target, postData, headers, programmatic, flags, -1);
   }

   public static final UrlRequestedInternalEvent processUrlRequest(
      Object src, String url, String target, byte[] postData, HttpHeaders headers, boolean programmatic, int flags, long clickID
   ) {
      return !runScript(src, url) ? new UrlRequestedInternalEvent(src, url, target, postData, headers, programmatic, flags, clickID) : null;
   }

   public static final boolean runScript(Object src, String url) {
      BrowserContentImpl browserContent = null;
      if (src instanceof BrowserContentImpl) {
         browserContent = (BrowserContentImpl)src;
      }

      if (browserContent != null && url != null && StringUtilities.startsWithIgnoreCase(url, "javascript:", 1701707776)) {
         RenderingApplication renderingApplication = browserContent.getRenderingApplication();
         RenderingOptions renderingOptions = browserContent.getRenderingOptions();
         if (!JavaScriptRegistry.isInstalled() || renderingOptions == null) {
            if (renderingApplication != null) {
               renderingApplication.eventOccurred((Event)(new Object(browserContent, BrowserResources.getString(545))));
            }

            return true;
         }

         if (!renderingOptions.getPropertyWithBooleanValue(4550690918222697397L, 2, false)) {
            if (renderingApplication != null) {
               boolean isBrowserRequest = (browserContent.getRenderingFlags() & 32) != 0;
               if (isBrowserRequest) {
                  EnableJavascriptRunnable enableJavascriptRunnable = new EnableJavascriptRunnable(browserContent);
                  Application.getApplication().invokeAndWait(enableJavascriptRunnable);
                  if (enableJavascriptRunnable.turnOnJavascript()) {
                     renderingApplication.eventOccurred(new OptionsEvent(browserContent, 2));
                     renderingOptions.setProperty(4550690918222697397L, 2, true);
                     renderingOptions.setProperty(4550690918222697397L, 42, true);
                     renderingApplication.eventOccurred(new ReloadEvent(browserContent));
                     return true;
                  }

                  renderingApplication.eventOccurred((Event)(new Object(browserContent, BrowserResources.getString(632))));
                  return true;
               }

               renderingApplication.eventOccurred((Event)(new Object(browserContent, BrowserResources.getString(632))));
            }

            return true;
         }

         if (renderingApplication != null) {
            String script = URIDecoder.decode(url, "utf-8", false);
            renderingApplication.invokeRunnable(new JSRunnable(browserContent, script));
            return true;
         }
      }

      return false;
   }
}
