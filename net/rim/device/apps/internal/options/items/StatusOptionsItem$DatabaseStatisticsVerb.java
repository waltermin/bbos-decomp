package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class StatusOptionsItem$DatabaseStatisticsVerb extends Verb {
   StatusOptionsItem$DatabaseStatisticsVerb() {
      super(16855040, OptionsResources.getResourceBundle(), 1887);
   }

   @Override
   public final Object invoke(Object parameter) {
      UiApplication.getUiApplication().pushScreen(new DatabaseStatisticsScreen());
      return null;
   }
}
