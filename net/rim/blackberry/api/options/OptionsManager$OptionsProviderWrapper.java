package net.rim.blackberry.api.options;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class OptionsManager$OptionsProviderWrapper implements OptionsProviderRegistration$OptionsProvider {
   private OptionsProvider _optionsProvider;

   public final Object getOptionsItem(String id) {
      return null;
   }

   @Override
   public final Vector getOptionsItems() {
      Vector v = (Vector)(new Object(1));
      v.addElement(new OptionsManager$OptionsItemWrapper(this._optionsProvider));
      return v;
   }

   public OptionsManager$OptionsProviderWrapper(OptionsProvider optionsProvider) {
      this._optionsProvider = optionsProvider;
   }
}
