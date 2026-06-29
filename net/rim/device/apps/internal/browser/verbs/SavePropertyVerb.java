package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.browser.options.IBrowserProperty;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class SavePropertyVerb extends Verb {
   private IBrowserProperty _property;

   public SavePropertyVerb(IBrowserProperty property) {
      super(268435456);
      this._property = property;
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(120);
   }

   @Override
   public final Object invoke(Object context) {
      this._property.saveProperty();
      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      return null;
   }
}
