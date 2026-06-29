package net.rim.wica.runtime.ui.internal;

import net.rim.device.api.ui.component.Dialog;

final class UiServiceImpl$ModalDialogShower implements Runnable {
   private String _message;
   private int _type;
   private int _result;

   UiServiceImpl$ModalDialogShower(String message, int type) {
      this._message = message;
      this._type = type;
   }

   final int getResult() {
      return this._result;
   }

   @Override
   public final void run() {
      switch (this._type) {
         case 2:
            int bbResult = Dialog.ask(3, this._message);
            if (bbResult == 4) {
               this._result = 2;
            } else {
               this._result = 3;
            }
            break;
         default:
            Dialog.alert(this._message);
            this._result = 0;
      }

      synchronized (this) {
         this.notifyAll();
      }
   }
}
