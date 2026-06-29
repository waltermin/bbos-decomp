package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

public final class AutoTextOptionsItem$EditAutoTextVerb extends Verb {
   public AutoTextOptionsItem$EditAutoTextVerb() {
      super(16974080, OptionsResources.getResourceBundle(), 313);
   }

   @Override
   public final Object invoke(Object parameter) {
      AutoTextOptionsItem autoText = new AutoTextOptionsItem();
      autoText.initialize();
      AutoTextOptionsItem.access$600(autoText);
      return null;
   }
}
