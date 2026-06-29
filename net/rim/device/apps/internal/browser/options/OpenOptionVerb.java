package net.rim.device.apps.internal.browser.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;

public final class OpenOptionVerb extends Verb {
   IBrowserProperty _property;
   boolean _restrictedAccess;

   public OpenOptionVerb(IBrowserProperty property, boolean restrictedAccess) {
      super(1313024, -229261654107783483L, "net.rim.device.apps.internal.resource.Browser", 132);
      this._property = property;
      this._restrictedAccess = restrictedAccess;
   }

   @Override
   public final Object invoke(Object context) {
      Screen screen = this._property.getScreen(this._restrictedAccess);
      synchronized (Application.getEventLock()) {
         UiApplication.getUiApplication().pushScreen(screen);
         return null;
      }
   }
}
