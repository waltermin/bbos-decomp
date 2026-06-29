package net.rim.device.apps.internal.blackberryemail.email;

import java.util.TimerTask;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.apps.api.framework.model.ContextObject;

class EmailMessageModelImpl$ScheduledResend extends TimerTask implements PersistentContentListener {
   private EmailMessageModelImpl _message;
   private final EmailMessageModelImpl this$0;

   public EmailMessageModelImpl$ScheduledResend(EmailMessageModelImpl _1, EmailMessageModelImpl message) {
      this.this$0 = _1;
      this._message = message;
   }

   @Override
   public void run() {
      if (!this.deleted() && !this.alreadySent()) {
         Object ticket = PersistentContent.getTicket();
         if (ticket == null) {
            PersistentContent.addListener(this);
         } else {
            EmailMessageModelImpl.access$008(this.this$0);
            ContextObject invokeContext = (ContextObject)(new Object(121));
            new EmailResendVerb(this._message).invoke(invokeContext);
         }
      }
   }

   @Override
   public void persistentContentStateChanged(int state) {
      if (state == 1) {
         PersistentContent.removeListener(this);
         this.run();
      }
   }

   @Override
   public void persistentContentModeChanged(int generation) {
   }

   private boolean deleted() {
      return (this._message.getFlags() & 262144) != 0;
   }

   private boolean alreadySent() {
      return this._message.getStatus() != 32767;
   }
}
