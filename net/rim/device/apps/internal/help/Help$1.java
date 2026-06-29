package net.rim.device.apps.internal.help;

import java.util.Vector;
import net.rim.device.apps.api.setupwizard.WizardPageProvider;

final class Help$1 implements WizardPageProvider {
   @Override
   public final Vector getWizardPages() {
      Vector pages = new Vector();
      pages.addElement(new IntroductionWizardProvider());
      pages.addElement(new SureTypeWizardProvider());
      pages.addElement(new TipsAndTricksWizardProvider());
      pages.addElement(new ShortcutsWizardProvider());
      return pages;
   }
}
