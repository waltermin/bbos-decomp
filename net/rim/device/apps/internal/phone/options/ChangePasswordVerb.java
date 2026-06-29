package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.SimpleInputDialog;

final class ChangePasswordVerb extends Verb implements PhoneEventListener {
   private static final int BARRING_PASSWORD_MIN_LENGTH;
   private static final int BARRING_PASSWORD_MAX_LENGTH;

   ChangePasswordVerb() {
      super(20000, CommonResources.getResourceBundle(), 1780);
   }

   @Override
   public final Object invoke(Object parameter) {
      SimpleInputDialog dlg = (SimpleInputDialog)(new Object(6, null, 4, 4, 0));
      dlg.setModal(true);
      String old = null;
      String new1 = null;
      String new2 = null;
      dlg.show(CommonResources.getString(2009));
      old = dlg.getText().trim();
      if (dlg.getCloseReason() == -1) {
         return null;
      } else {
         dlg.show(CommonResources.getString(2010));
         new1 = dlg.getText().trim();
         dlg.show(CommonResources.getString(2011));
         new2 = dlg.getText().trim();
         if (new1.equals(new2)) {
            VoiceServices.addPhoneEventListener(this);
            VoiceServices.setCallBarringPassword(old, new1);
            new SSRequestStatusDialog().show();
            Runnable timeoutRunnable = new ChangePasswordVerb$1(this);
            Application.getApplication().invokeLater(timeoutRunnable, 10000, false);
            return null;
         } else {
            Dialog.alert(CommonResources.getString(6001));
            return null;
         }
      }
   }

   private final void stopListening() {
      VoiceServices.removePhoneEventListener(this);
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 5000:
         default:
            Status.show(PhoneResources.getString(144));
            this.stopListening();
            return;
         case 5001:
            if (param1 == 6 || param1 == 26) {
               Status.show(PhoneResources.getString(6131));
               this.stopListening();
               return;
            }
         case 5002:
         case 5003:
            Status.show(PhoneResources.getString(145));
            this.stopListening();
            return;
         case 5004:
            Status.show(CommonResources.getString(6006));
            this.stopListening();
         case 4999:
      }
   }
}
