package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class MessageListOptionsVerb extends Verb {
   MessageListOptionsVerb() {
      super(16986368, CommonResource.getBundle(), 20);
   }

   @Override
   public final Object invoke(Object context) {
      UiApplication.getUiApplication().pushScreen(new MessageListMainOptionsScreen());
      return null;
   }
}
