package net.rim.device.apps.internal.messaging;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class AnonymousMessagesImpl$AnonymousMessageOpenVerb extends Verb {
   private AnonymousMessagesImpl$AnonymousMessageModel _message;

   AnonymousMessagesImpl$AnonymousMessageOpenVerb(AnonymousMessagesImpl$AnonymousMessageModel message) {
      super(590080);
      this._message = message;
   }

   @Override
   public final Object invoke(Object context) {
      this._message.markOpened(true);
      AnonymousMessagesImpl$AnonymousMessageViewerScreen viewer = new AnonymousMessagesImpl$AnonymousMessageViewerScreen(context);
      viewer.setModel(this._message);
      viewer.go();
      return null;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(15);
   }
}
