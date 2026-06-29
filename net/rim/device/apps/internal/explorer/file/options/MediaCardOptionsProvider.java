package net.rim.device.apps.internal.explorer.file.options;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;
import net.rim.device.internal.system.InternalServices;

public final class MediaCardOptionsProvider implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector items = new Vector();
      items.addElement(new MediaCardOptionsItem());
      return items;
   }

   public static final void register() {
      if (InternalServices.isDeviceCapable(19)) {
         OptionsProviderRegistration.registerOptionsProvider(new MediaCardOptionsProvider());
      }
   }
}
