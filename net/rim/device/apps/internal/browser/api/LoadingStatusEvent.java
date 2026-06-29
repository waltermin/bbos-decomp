package net.rim.device.apps.internal.browser.api;

import net.rim.device.api.browser.field.Event;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class LoadingStatusEvent extends Event {
   private int _status;
   public static final int STATUS_LOADING;
   public static final int STATUS_REQUESTING_SCRIPT;
   public static final int STATUS_EXECUTING_SCRIPT;
   public static final int STATUS_REQUESTING_STYLE_SHEET;
   public static final int STATUS_PROCESSING_STYLE_SHEET;

   public LoadingStatusEvent(int status, Object src) {
      super(3, src);
      this._status = status;
   }

   public final int getStatus() {
      return this._status;
   }

   public final void setStatus(int status) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final String getMessage() {
      String message = null;
      switch (this._status) {
         case 0:
         default:
            return BrowserResources.getString(136);
         case 1:
            return BrowserResources.getString(709);
         case 2:
            return BrowserResources.getString(138);
         case 3:
            return BrowserResources.getString(710);
         case 4:
            message = BrowserResources.getString(711);
         case -1:
            return message;
      }
   }
}
