package net.rim.device.apps.internal.vad;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class VADOptionsScreen$1 implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector v = (Vector)(new Object(1));
      v.addElement(new VADOptionsScreen(null));
      return v;
   }
}
