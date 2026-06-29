package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.apps.internal.qm.resource.QmResources;

public final class TypingNotificationMessage extends NotificationMessage {
   public TypingNotificationMessage(Conversation conversation, Contact contact) {
      super(conversation, contact);
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof TypingNotificationMessage ? super.equals(obj) : false;
   }

   @Override
   public final String toString() {
      String displayName = this.getContact().getDisplayName();
      return QmResources.format(69, displayName == null ? this.getContact().getId() : displayName);
   }
}
