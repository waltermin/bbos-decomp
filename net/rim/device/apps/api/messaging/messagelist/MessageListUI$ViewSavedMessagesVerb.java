package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.resources.MessageResources;

final class MessageListUI$ViewSavedMessagesVerb extends Verb {
   public MessageListUI$ViewSavedMessagesVerb() {
      super(16861504, MessageResources.getBundle(), 194);
   }

   @Override
   public final Object invoke(Object context) {
      ShowMessageApp.showMessageApp(-1676600994, null);
      return null;
   }
}
