package net.rim.device.apps.internal.setupwizard;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

final class SetupWizardOptionsItem$1 implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public final Vector getOptionsItems() {
      Vector items = new Vector(1);
      items.addElement(new SetupWizardOptionsItem());
      return items;
   }
}
