package net.rim.device.apps.internal.help;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class HelpVerb extends Verb {
   private String _topic;

   public HelpVerb(String topic) {
      super(4096, CommonResources.getResourceBundle(), 9034);
      this._topic = topic;
   }

   @Override
   public final Object invoke(Object parameter) {
      UiApplication.getUiApplication().pushScreen(new HelpScreen(this._topic));
      return null;
   }
}
