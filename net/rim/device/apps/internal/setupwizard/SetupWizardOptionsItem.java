package net.rim.device.apps.internal.setupwizard;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;

final class SetupWizardOptionsItem extends MainScreenOptionsListItem {
   public SetupWizardOptionsItem() {
      super(SetupWizardResources.getBundle(), 0, null);
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
   }

   @Override
   protected final void open() {
      SetupWizardScreen wizard = new SetupWizardScreen(false, null);
      if (!SetupWizardOptions.getOptions().getWizardCompleted()) {
         wizard.startAutoRun();
      } else {
         UiApplication.getUiApplication().pushModalScreen(wizard);
      }
   }
}
