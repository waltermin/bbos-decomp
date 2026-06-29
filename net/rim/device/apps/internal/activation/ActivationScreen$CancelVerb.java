package net.rim.device.apps.internal.activation;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;

final class ActivationScreen$CancelVerb extends Verb {
   private final ActivationScreen this$0;

   ActivationScreen$CancelVerb(ActivationScreen _1) {
      super(524800, ResourceBundle.getBundle(-8414468493733347764L, "net.rim.device.apps.internal.resource.Common"), 9042);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object anObject) {
      int state = this.this$0._app.getCurrentState();
      String msg = null;
      switch (state) {
         case 0:
            return null;
         case 1:
         case 2:
            msg = ActivationApp._resources.getString(125);
            break;
         case 3:
            msg = ActivationApp._resources.getString(126);
            break;
         case 4:
         default:
            msg = ActivationApp._resources.getString(124);
      }

      int result = Dialog.ask(3, msg, -1);
      state = this.this$0._app.getCurrentState();
      if (result == 4) {
         this.this$0._app.setCurrentState(5);
         switch (state) {
            case 1:
               this.this$0._activationService.abortTransaction(-1, (byte)7);
               if (this.this$0._connWaiterThread != null) {
                  synchronized (this.this$0._connWaiterThread) {
                     this.this$0._connWaiterThread.notify();
                     return null;
                  }
               }
               break;
            case 4:
               ActivationApp.setSlowSyncState(this.this$0._currentSid, 7);
               return null;
            default:
               this.this$0._activationService.abortTransaction(this.this$0._transactionID, (byte)7);
         }
      }

      return null;
   }
}
