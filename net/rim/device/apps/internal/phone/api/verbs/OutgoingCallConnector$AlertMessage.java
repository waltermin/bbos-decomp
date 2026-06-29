package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.ui.component.Dialog;

class OutgoingCallConnector$AlertMessage implements Runnable {
   private String _message;

   public OutgoingCallConnector$AlertMessage(String message) {
      this._message = message;
   }

   @Override
   public void run() {
      Dialog.alert(this._message);
   }
}
