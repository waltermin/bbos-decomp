package net.rim.device.apps.internal.phone.control;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.callcontrol.CallCommandHandler;

class FDNPolicyHandler$FDNCommandHandler extends CallCommandHandler {
   private final FDNPolicyHandler this$0;

   public FDNPolicyHandler$FDNCommandHandler(FDNPolicyHandler _1, int order) {
      super(order);
      this.this$0 = _1;
   }

   @Override
   public int startCall(String number, int clir) {
      if (this.this$0._outgoingTable != null && this.this$0.isMyLine() && this.this$0._outgoingTable.isRejected(number)) {
         String emsg = "FDNPolicy rejected startCall.";
         System.out.println(emsg);
         throw new Object(emsg);
      } else {
         return super.startCall(number, clir);
      }
   }

   @Override
   public void flash(String number) {
      if (number != null
         && number.length() > 0
         && this.this$0._outgoingTable != null
         && this.this$0.isMyLine()
         && this.this$0._outgoingTable.isRejected(number)) {
         String emsg = "FDNPolicy rejected flash.";
         System.out.println(emsg);
         super.flash(null);
         super.flash(null);
         this.notifyFailed(number);
      } else {
         super.flash(number);
      }
   }

   private void notifyFailed(String number) {
      String errorString = MessageFormat.format(PhoneResources.getString(146), new Object[]{number});
      Dialog.alert(errorString);
   }
}
