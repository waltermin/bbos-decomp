package net.rim.device.apps.internal.options;

import java.util.Vector;
import net.rim.device.apps.api.setupwizard.WizardPageProvider;
import net.rim.device.apps.internal.options.items.ApplicationControlSetupWizard;
import net.rim.device.apps.internal.options.items.DateTimeSetupWizard;
import net.rim.device.apps.internal.options.items.FontsSetupWizard;
import net.rim.device.apps.internal.options.items.LocalizationSetupWizard;
import net.rim.device.apps.internal.options.items.OwnerSetupWizard;

final class OptionsApp$OptionsWizardPageProvider implements WizardPageProvider {
   @Override
   public final Vector getWizardPages() {
      Vector pages = new Vector();
      pages.addElement(new LocalizationSetupWizard());
      pages.addElement(new DateTimeSetupWizard());
      pages.addElement(new OwnerSetupWizard());
      pages.addElement(new FontsSetupWizard());
      pages.addElement(new ApplicationControlSetupWizard());
      return pages;
   }
}
