package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class ApplicationOptionsScreen$PropertiesVerb extends Verb {
   private final ApplicationOptionsScreen this$0;

   public ApplicationOptionsScreen$PropertiesVerb(ApplicationOptionsScreen _1) {
      super(598288, OptionsResources.getResourceBundle(), 1431);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.showProperties();
      return null;
   }
}
