package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.api.browser.BrowserServices;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.BasicWizardPage;
import net.rim.device.apps.api.setupwizard.ScrollingVerticalFieldManager;
import net.rim.device.apps.api.setupwizard.WizardLayoutManager;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.bis.invoke.BISClientInvoke;

final class EmailSetupWizard$BisConfirmPage extends BasicWizardPage {
   private final EmailSetupWizard this$0;

   public EmailSetupWizard$BisConfirmPage(EmailSetupWizard _1) {
      super(EmailSetupWizard._resources, 160, 20, null, 24);
      this.this$0 = _1;
   }

   @Override
   public final boolean canSkipWizardInternal() {
      return this.this$0._setupMode != 1;
   }

   @Override
   protected final void populateContent(AppsMainScreen mainScreen, Manager content) {
      WizardLayoutManager layoutManager = new WizardLayoutManager();
      layoutManager.setScrollbarEnabled(true);
      ScrollingVerticalFieldManager scrollingContent = new ScrollingVerticalFieldManager(0);
      boolean thickclient = BISClientInvoke.canBeInvoked();
      int resId;
      if (Trackball.isSupported()) {
         resId = thickclient ? 179 : 170;
      } else {
         resId = thickclient ? 178 : 162;
      }

      scrollingContent.add(new LabelField(EmailSetupWizard._resources.getString(resId)));
      layoutManager.setContent(scrollingContent);
      content.add(layoutManager);
   }

   @Override
   protected final boolean saveWizard(Verb sender) {
      boolean invokeBrowser = false;
      if (BISClientInvoke.canBeInvoked()) {
         try {
            ApplicationManager.getApplicationManager().runApplication(BISClientInvoke.getWizardInvokeDescriptor());
         } finally {
            ;
         }
      } else {
         invokeBrowser = true;
      }

      if (invokeBrowser) {
         BrowserServices.showBrowser(this.this$0._bisSetupUID);
      }

      return true;
   }
}
