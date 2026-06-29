package net.rim.device.apps.internal.activation;

import java.util.Vector;
import net.rim.device.apps.api.setupwizard.WizardPageProvider;

final class EmailSetupWizard$1 implements WizardPageProvider {
   @Override
   public final Vector getWizardPages() {
      Vector pages = new Vector(1);
      pages.addElement(new EmailSetupWizard());
      return pages;
   }
}
