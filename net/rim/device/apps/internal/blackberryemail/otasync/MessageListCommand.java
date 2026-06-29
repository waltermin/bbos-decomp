package net.rim.device.apps.internal.blackberryemail.otasync;

import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.transmission.rim.RIMMessagingFolderManagement;

final class MessageListCommand extends RIMMessagingFolderManagement implements Recognizer, Persistable {
   private int _messageListId;
   private int _ghostMessageCount;
   private boolean _restoreMessageList;

   final int getMessageListId() {
      return this._messageListId;
   }

   final int getGhostMessageCount() {
      return this._ghostMessageCount;
   }

   final void setGhostMessageCount(int ghostMessageCount) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final boolean isRestoreMessageList() {
      return this._restoreMessageList;
   }

   @Override
   public final boolean recognize(Object o) {
      return o instanceof MessageListCommand;
   }

   @Override
   public final void beginMessageListCommand(int messageListId) {
      this._messageListId = messageListId;
      super.beginMessageListCommand(messageListId);
   }

   @Override
   public final void beginRestoreMessageListCommand(int messageListId) {
      this._messageListId = messageListId;
      this._restoreMessageList = true;
      super.beginRestoreMessageListCommand(messageListId);
   }
}
