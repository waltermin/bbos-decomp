package net.rim.device.apps.internal.memorycleaner;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class MemoryCleanerProvider implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector items = (Vector)(new Object());
      items.addElement(new MemoryCleanerOptionsItem());
      return items;
   }
}
