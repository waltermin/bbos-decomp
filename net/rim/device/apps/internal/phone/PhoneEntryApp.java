package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.verbs.OutgoingCallConnector;

final class PhoneEntryApp {
   public static final void main(String[] args) {
      if (findIndex(args, "call") >= 0 && OutgoingCallConnector.outgoingCallPermitted()) {
         String number = getValue(args, "number=");
         if (number != null) {
            if (findIndex(args, "smartdial") >= 0) {
               startCall(number, null, null, true);
               return;
            }

            startCall(number, null, null, false);
            return;
         }

         VoiceServices.broadcastEvent(2015, 0, new Character(' '));
      }
   }

   private static final int findIndex(String[] args, String label) {
      for (int idx = 0; idx < args.length; idx++) {
         String param = args[idx];
         if (param.startsWith(label)) {
            return idx;
         }
      }

      return -1;
   }

   private static final String getValue(String[] args, String label) {
      int idx = findIndex(args, label);
      return idx >= 0 ? args[idx].substring(label.length()) : null;
   }

   private static final void startCall(String number, Object addressCard, String friendlyName, boolean smartDial) {
      Application voiceApp = (Application)VoiceServices.getVoiceApplication();
      voiceApp.invokeLater(new StartCallRunnable(number, addressCard, friendlyName, smartDial));
   }
}
