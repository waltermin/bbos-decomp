package net.rim.device.apps.internal.messaging;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class AnonymousMessagesImpl$AnonymousMessageMarkVerb extends Verb {
   private AnonymousMessagesImpl$AnonymousMessageModel _message;
   private boolean _markOpened;

   AnonymousMessagesImpl$AnonymousMessageMarkVerb(AnonymousMessagesImpl$AnonymousMessageModel message, boolean markOpened) {
      super(markOpened ? 602448 : 602450);
      this._message = message;
      this._markOpened = markOpened;
   }

   @Override
   public final Object invoke(Object context) {
      this._message.markOpened(this._markOpened);
      return null;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(this._markOpened ? 1325 : 1350);
   }
}
