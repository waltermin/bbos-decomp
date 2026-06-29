package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.api.setupwizard.BasicWizardPage;
import net.rim.device.apps.api.setupwizard.ScrollingVerticalFieldManager;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.apps.api.setupwizard.WizardLayoutManager;
import net.rim.device.apps.api.ui.AppsMainScreen;

final class FinishWizardPage extends BasicWizardPage {
   public FinishWizardPage() {
      super(SetupWizardResources.getBundle(), 17, 1000, SetupWizardOrdering.FINISHING_WIZARD_CATEGORY, 20);
   }

   @Override
   protected final void populateContent(AppsMainScreen screen, Manager content) {
      WizardLayoutManager wizardLayoutManager = (WizardLayoutManager)(new Object());
      wizardLayoutManager.setScrollbarEnabled(true);
      ScrollingVerticalFieldManager scrollingContent = (ScrollingVerticalFieldManager)(new Object(0));
      content.setFont(this.getHeaderFont());
      SetupWizardOptions options = SetupWizardOptions.getOptions();
      if (!options.getWizardCompleted()) {
         options.setWizardCompleted(true);
         options.setSetupWizardDesired(false);
         options.commit();
      }

      RichTextField label = (RichTextField)(new Object(SetupWizardResources.getString(18), 36028797018963968L));
      scrollingContent.add(label);
      label.setBorder(0, 0, label.getFont().getHeight(), 0);
      label = (RichTextField)(new Object(SetupWizardResources.getString(19), 36028797018963968L));
      Manager hfm = (Manager)(new Object());
      Bitmap appIcon = SetupWizard.getAppIcon();
      if (appIcon != null) {
         hfm.add((Field)(new Object(appIcon)));
      }

      hfm.add(label);
      scrollingContent.add(hfm);
      wizardLayoutManager.setContent(scrollingContent);
      content.add(wizardLayoutManager);
   }
}
