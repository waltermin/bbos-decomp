package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class ApplicationOptionsScreen$DisplayModulesVerb extends Verb {
   private final ApplicationOptionsScreen this$0;

   public ApplicationOptionsScreen$DisplayModulesVerb(ApplicationOptionsScreen _1) {
      super(16986368, OptionsResources.getResourceBundle(), 700);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      UiApplication.getUiApplication().pushScreen(new InstalledModulesOptionsItem(this.this$0._showAll));
      return null;
   }
}
