package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.ui.PhoneEventListenerStatusDialog;

public class SSRequestStatusDialog extends PhoneEventListenerStatusDialog {
   private boolean _suppressMessageDialogs = true;
   public static final int TIMEOUT = 10;

   public SSRequestStatusDialog() {
      this(10, true);
   }

   public SSRequestStatusDialog(int timeout) {
      this(timeout, true);
   }

   public SSRequestStatusDialog(boolean suppressMessageDialogs, boolean closeButton) {
      this(10, suppressMessageDialogs, closeButton);
   }

   public SSRequestStatusDialog(int timeout, boolean suppressMessageDialogs) {
      this(timeout, suppressMessageDialogs, false);
   }

   public SSRequestStatusDialog(int timeout, boolean suppressMessageDialogs, boolean closeButton) {
      super(CommonResources.getString(9156), true, closeButton, timeout);
      this._suppressMessageDialogs = suppressMessageDialogs;
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         if (this._suppressMessageDialogs) {
            SSManager.suppressMessageDialogs(true);
         }
      } else if (this._suppressMessageDialogs) {
         SSManager.suppressMessageDialogs(false);
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected void onCloseButton() {
      label17:
      try {
         System.out.println("User request to cancel USSD session.");
         Phone.getInstance().setUSSDResponse(null);
      } finally {
         break label17;
      }

      super.onCloseButton();
   }

   @Override
   protected void onEvent(int eventId, int param, Object context) {
      if (eventId >= 5000 && eventId <= 5004) {
         this.close(eventId);
      } else {
         if (eventId == 1000) {
            this.close(eventId);
         }
      }
   }
}
