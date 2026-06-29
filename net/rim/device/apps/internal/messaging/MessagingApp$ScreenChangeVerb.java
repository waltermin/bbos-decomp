package net.rim.device.apps.internal.messaging;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;

final class MessagingApp$ScreenChangeVerb extends Verb {
   int _screenType;

   MessagingApp$ScreenChangeVerb(int screenType) {
      super(0);
      this._screenType = screenType;
   }

   @Override
   public final Object invoke(Object context) {
      ShowMessageApp.showMessageApp(this._screenType, null);
      return null;
   }
}
