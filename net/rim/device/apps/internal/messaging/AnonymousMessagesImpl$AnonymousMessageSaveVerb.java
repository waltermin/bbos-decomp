package net.rim.device.apps.internal.messaging;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class AnonymousMessagesImpl$AnonymousMessageSaveVerb extends Verb {
   private AnonymousMessagesImpl$AnonymousMessageModel _message;

   AnonymousMessagesImpl$AnonymousMessageSaveVerb(AnonymousMessagesImpl$AnonymousMessageModel message) {
      super(602480);
      this._message = message;
   }

   @Override
   public final Object invoke(Object context) {
      this._message.markSaved();
      return null;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(18);
   }
}
