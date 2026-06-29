package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.setupwizard.logging.LogManagerImpl;

final class SetupWizard$1 implements Runnable {
   private final SetupWizard this$0;

   SetupWizard$1(SetupWizard _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      LogManagerImpl log = this.this$0.startLog();
      SetupWizardScreen rootPage = new SetupWizardScreen(SetupWizard._mode == 1, log);
      if (SetupWizard._mode != 1 && SetupWizardOptions.getOptions().getWizardCompleted()) {
         UiApplication.getUiApplication().pushModalScreen(rootPage);
      } else {
         rootPage.startAutoRun();
      }

      this.this$0.stopLog(log);
      System.exit(0);
   }
}
