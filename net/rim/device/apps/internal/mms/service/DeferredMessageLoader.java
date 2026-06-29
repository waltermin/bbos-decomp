package net.rim.device.apps.internal.mms.service;

import java.util.Enumeration;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSMessageModel;

class DeferredMessageLoader implements Runnable {
   private Enumeration _messagesA;
   private Enumeration _messagesB;

   DeferredMessageLoader(Enumeration messagesA, Enumeration messagesB) {
      this._messagesA = messagesA;
      this._messagesB = messagesB;
   }

   @Override
   public void run() {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         MMSMessageModel message = this.getNextDeferredMessage();
         if (message != null) {
            RetrieveMessageContentTask task = new RetrieveMessageContentTask(message, false, this, this, true);
            BackgroundTaskThread.addTask(task);
         }
      }
   }

   private MMSMessageModel getNextDeferredMessage() {
      MMSMessageModel message = getNextDeferredMessage(this._messagesA);
      if (message == null) {
         message = getNextDeferredMessage(this._messagesB);
      }

      return message;
   }

   private static MMSMessageModel getNextDeferredMessage(Enumeration messages) {
      if (messages != null) {
         while (messages.hasMoreElements()) {
            MMSMessageModel message = (MMSMessageModel)messages.nextElement();
            if (message.isInbound()
               && message.getStatus() != 1
               && message.getPayload().getAttribute("x-mms-content-location") != null
               && !message.getAttachmentDataProvider().hasAttachments()
               && !MMSUtilities.isPermanentFailure(message)) {
               return message;
            }
         }
      }

      return null;
   }
}
