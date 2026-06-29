package net.rim.device.apps.api.messaging.messagelist;

import java.util.Hashtable;
import net.rim.vm.Persistable;

final class MessageListOptions$PersistedMessageListOptions implements Persistable {
   int _flags = -665;
   short _keepMessagesDuration = MessageListOptions.KEEP_MESSAGES_DURATION_CHOICES[1];
   Hashtable _serviceSettings = (Hashtable)(new Object());
   short _displayMessageCount = 1;
   int _messageListLineMode = 0;
   short _SMSEmailInbox = 0;
   short _autoDownloadAttachments = MessageListOptions.getAutoDownloadAttachmentsDefault();
   short _listSeparatorAppearance = 2;
   private static final int DEFAULT_OFF;

   MessageListOptions$PersistedMessageListOptions() {
      boolean highSpeedNetworkDefaultValue = MessageListOptions.getHighSpeedNetworkOnlyDefault();
      if (highSpeedNetworkDefaultValue) {
         this._flags |= 2048;
      } else {
         this._flags &= -2049;
      }
   }
}
