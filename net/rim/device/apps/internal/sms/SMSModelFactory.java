package net.rim.device.apps.internal.sms;

import net.rim.device.apps.internal.sms.message.SMSMessageModel;
import net.rim.device.apps.internal.sms.voicemail.VoicemailSMSModel;

public class SMSModelFactory {
   public static SMSModel createSMSModel(int type, Object context) {
      Object contextToUse = context;
      switch (type) {
         case 1:
            return new VoicemailSMSModel(contextToUse);
         default:
            return new SMSMessageModel(contextToUse);
      }
   }
}
