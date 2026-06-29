package net.rim.device.apps.internal.activation;

import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.api.browser.BrowserServices;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.apps.api.setupwizard.SetupWizardRegistration;
import net.rim.device.apps.api.setupwizard.WizardController;

public final class EmailSetupWizard extends WizardController {
   private int _setupMode = -1;
   private String _bisSetupUID;
   private static final int MODE_UNSET = -1;
   private static final int MODE_NONE = 0;
   private static final int MODE_BIS = 1;
   private static final int MODE_BES = 2;
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(6266320976869391490L, "net.rim.device.apps.internal.resource.Activation");

   static final void register() {
      SetupWizardRegistration.registerWizardProvider(new EmailSetupWizard$1());
   }

   public EmailSetupWizard() {
      super(_resources, 160, 300, SetupWizardOrdering.EMAIL_SETUP_CATEGORY, 3);
      Vector pages = (Vector)(new Object(3));
      pages.addElement(new EmailSetupWizard$EmailSetupPage(this));
      pages.addElement(new EmailSetupWizard$BisConfirmPage(this));
      pages.addElement(new EmailSetupWizard$BesConfirmPage(this));
      this.setPages(pages);
   }

   @Override
   public final int showPage(int lastCommand, int context) {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final boolean canSkipWizard() {
      return RadioInfo.getState() != 1 ? true : !isBisAvailable() && !isBesAvailable();
   }

   protected static final String findEmailSetupBrowserConfigUID() {
      return BrowserServices.getBISEmailSetupServiceUID();
   }

   protected static final boolean isBesAvailable() {
      return true;
   }

   protected static final boolean isBisAvailable() {
      return findEmailSetupBrowserConfigUID() != null;
   }
}
