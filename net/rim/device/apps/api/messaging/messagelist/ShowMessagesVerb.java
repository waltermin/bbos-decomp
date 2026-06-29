package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.resources.MessageResources;

public final class ShowMessagesVerb extends Verb {
   public ShowMessagesVerb(int ordering) {
      super(ordering, MessageResources.getBundle(), 208);
   }

   @Override
   public final Object invoke(Object parameter) {
      ShowMessageApp.showMessageApp();
      return null;
   }
}
