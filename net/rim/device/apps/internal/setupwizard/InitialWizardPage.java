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
import net.rim.tid.awt.im.InputContext;

final class InitialWizardPage extends BasicWizardPage {
   public InitialWizardPage() {
      super(SetupWizardResources.getBundle(), 20, 50, SetupWizardOrdering.INITIAL_PAGE_CATEGORY, 16);
   }

   @Override
   protected final void populateContent(AppsMainScreen screen, Manager content) {
      WizardLayoutManager wizardLayoutManager = (WizardLayoutManager)(new Object());
      wizardLayoutManager.setScrollbarEnabled(true);
      ScrollingVerticalFieldManager scrollingContent = (ScrollingVerticalFieldManager)(new Object(0));
      content.setFont(this.getHeaderFont());
      long style = 36028797018963968L;
      content.setFont(this.getHeaderFont());
      int introResource;
      if (InputContext.getInstance().isSureType()) {
         introResource = 23;
      } else {
         introResource = 21;
      }

      RichTextField label = (RichTextField)(new Object(SetupWizardResources.getString(introResource), style));
      label.setBorder(0, 0, 10, 0);
      scrollingContent.add(label);
      label = (RichTextField)(new Object(SetupWizardResources.getString(22), style));
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
