package net.rim.device.apps.internal.calendar.ota;

import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailSendUtility;

public final class CICALMeetingEmailer$SendEmailThreaded implements Runnable {
   EmailMessageModel _message;
   ServiceRecord _emailServiceRecord;

   CICALMeetingEmailer$SendEmailThreaded(EmailMessageModel message, ServiceRecord emailServiceRecord) {
      this._message = message;
      this._emailServiceRecord = emailServiceRecord;
   }

   @Override
   public final void run() {
      this._message.setTimestamp(System.currentTimeMillis());
      EmailSendUtility.sendMessage(this._message, this._emailServiceRecord, null);
   }
}
