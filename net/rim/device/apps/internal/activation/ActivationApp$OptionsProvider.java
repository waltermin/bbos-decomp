package net.rim.device.apps.internal.activation;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsListItem;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class ActivationApp$OptionsProvider extends OptionsListItem implements OptionsProviderRegistration$OptionsProvider {
   public ActivationApp$OptionsProvider() {
      super(ActivationApp._resources, 0, -1514481539159318190L);
   }

   @Override
   protected final void open() {
      ActivationApp.run(null);
   }

   @Override
   public final Vector getOptionsItems() {
      Vector items = (Vector)(new Object());
      items.addElement(this);
      return items;
   }
}
