package net.rim.device.apps.internal.blackberryemail.folder;

import net.rim.device.apps.api.messaging.util.SortedCollection;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

class EmailHierarchy$MoveFiledMessagesWorker implements Runnable {
   private final EmailHierarchy this$0;

   private EmailHierarchy$MoveFiledMessagesWorker(EmailHierarchy _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      SortedCollection filed = (SortedCollection)((EmailFolder)this.this$0.getFiledFolder()).getContainedItems();
      SortedCollection unfiled = (SortedCollection)((EmailFolder)this.this$0.getUnfiledFolder()).getContainedItems();
      synchronized (unfiled) {
         if (this.this$0._missingFolderTimestamp != -1) {
            for (int i = unfiled.size() - 1; i >= 0; i--) {
               EmailMessageModel message = (EmailMessageModel)unfiled.getAt(i);
               if (message.getTimestamp() < this.this$0._missingFolderTimestamp) {
                  break;
               }

               if ((message.getFlags() & 1) != 0 && EmailHierarchy.isInPersonalFolder(message)) {
                  message.changeStatus(2, 0, 0, 0, false, false, false, false, null);
                  unfiled.remove(message);
                  filed.add(message);
               }
            }

            this.this$0._missingFolderTimestamp = -1;
         }
      }
   }

   EmailHierarchy$MoveFiledMessagesWorker(EmailHierarchy x0, EmailHierarchy$1 x1) {
      this(x0);
   }
}
