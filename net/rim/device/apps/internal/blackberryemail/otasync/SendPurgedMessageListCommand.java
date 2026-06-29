package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;
import net.rim.device.apps.internal.blackberryemail.EmailSyncState;

final class SendPurgedMessageListCommand extends RIMMessagingFolderManagement implements Recognizer, Persistable {
   SendPurgedMessageListCommand(int[] refIds, int[] statuses, int messageListId) {
      if (statuses != null && refIds != null) {
         int folderId = statuses.length > 0 ? EmailSyncState.getFolderId(statuses[0]) : 0;
         this.addPurgeMessageList(refIds, statuses, messageListId, folderId);
      }
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof SendPurgedMessageListCommand;
   }
}
