package net.rim.device.apps.internal.qm.peer.common;

public final class SystemNotificationMessage extends NewNotificationMessage {
   public SystemNotificationMessage(Conversation conversation, String message) {
      super(conversation, null, message);
   }

   @Override
   public final String toString() {
      return this.getMessage();
   }
}
