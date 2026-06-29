package net.rim.device.apps.internal.browser.wml;

import com.fourthpass.wmls.Interpreter;
import com.fourthpass.wmls.URL;
import java.util.Vector;
import net.rim.device.api.browser.field.HistoryEvent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingOptions;
import net.rim.device.api.browser.field.UrlRequestedEvent;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EventLogger;
import net.rim.device.apps.internal.browser.common.RenderingUtilities;

final class WMLScriptBrowserContent implements Runnable {
   private URL _url;
   private com.fourthpass.wmls.WMLScript _wmlScript;
   private String _method;
   private String[] _argString;
   private Interpreter _interpreter;
   private int _postScriptAction;
   private String _postScriptUrl;
   private String _label;
   private WMLBrowserContent _browserContent;
   private RenderingOptions _renderingOptions;
   private RenderingApplication _renderingApplication;
   private boolean _aborted;
   static final int ACTION_PREV = 1;
   static final int ACTION_GO = 2;

   final void halt() {
      if (!this._aborted) {
         this._aborted = true;
         this._interpreter.abort();
      }
   }

   final int getPostScriptAction() {
      return this._postScriptAction;
   }

   final void addPostScriptAction(String url, int action) {
      if (action == 2 && url != null && url.length() == 0) {
         action = -1;
      }

      this._postScriptAction = action;
      this._postScriptUrl = url;
   }

   @Override
   public final void run() {
      WMLScriptBrowserContent$ConfirmExecuteRunnable confirm = new WMLScriptBrowserContent$ConfirmExecuteRunnable(this._label, this._renderingOptions);
      Application.getApplication().invokeAndWait(confirm);
      if (!confirm.executeScript()) {
         this._aborted = true;
      } else {
         label49:
         try {
            EventLogger.logEvent(1907089860548946979L, 1113744755, 5);
            this._interpreter.exec(this._url, this._wmlScript, this._method, this._argString);
            EventLogger.logEvent(1907089860548946979L, 1113679219, 5);
         } finally {
            break label49;
         }

         if (!this._aborted) {
            if (this._postScriptAction == 1 && this._renderingApplication != null && this._browserContent != null) {
               HistoryEvent event = new HistoryEvent(this._browserContent, -1, true, 0);
               this._renderingApplication.eventOccurred(event);
               return;
            }

            if (this._postScriptAction == 2 && this._renderingApplication != null && this._browserContent != null) {
               HttpHeaders requestHeaders = new HttpHeaders();
               RenderingUtilities.setReferrer(requestHeaders, this._browserContent.getURL());
               int flags = this._browserContent.getSharedFlags() | 1;
               UrlRequestedEvent event = new UrlRequestedEvent(this._browserContent, this._postScriptUrl, null, requestHeaders, true, flags);
               this._renderingApplication.eventOccurred(event);
            }
         }
      }
   }

   WMLScriptBrowserContent(
      URL url,
      String label,
      com.fourthpass.wmls.WMLScript wmlScript,
      Interpreter interpreter,
      RenderingOptions renderingOptions,
      RenderingApplication renderingApplication,
      WMLBrowserContent browserContent,
      int flags
   ) {
      this._renderingOptions = renderingOptions;
      this._renderingApplication = renderingApplication;
      this._url = url;
      this._wmlScript = wmlScript;
      this._interpreter = interpreter;
      this._label = label;
      this._browserContent = browserContent;
      Vector argVector = new Vector();
      String argString = null;
      int index = url._anchor != null ? url._anchor.indexOf(40) : -1;
      if (index != -1) {
         this._method = url._anchor.substring(0, index).trim();
         argString = url._anchor.substring(index + 1);
      }

      if (argString != null) {
         if (argString.indexOf(44) > 0) {
            argVector.addElement(argString.substring(0, argString.indexOf(44)));
            argString = argString.substring(argString.indexOf(44) + 1);
         }

         while (argString.indexOf(44) > 0) {
            argVector.addElement(argString.substring(0, argString.indexOf(44)));
            argString = argString.substring(argString.indexOf(44) + 1);
         }

         argString = argString.substring(0, argString.indexOf(41));
         if (argString.trim().length() > 0) {
            argVector.addElement(argString);
         }
      }

      if (argVector.size() > 0) {
         this._argString = new String[argVector.size()];
         argVector.copyInto(this._argString);
      } else {
         this._argString = new String[0];
      }
   }
}
