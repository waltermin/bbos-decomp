package net.rim.device.apps.internal.help;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.setupwizard.SetupWizardRegistration;

final class Help extends UiApplication {
   private Help() {
   }

   private Help(String topic) {
      this.pushScreen(new HelpScreen(topic));
   }

   public static final void main(String[] args) {
      if (args != null && args.length > 0) {
         if ("init".equals(args[0])) {
            registerWizards();
            HelpVerbFactory factory = new HelpVerbFactory();
            VerbFactoryRepository.addFactory(8522643724050848398L, factory);
            VerbFactoryRepository.addFactory(-1167824522183525950L, factory);
            System.exit(0);
         } else {
            new Help(args[0]).enterEventDispatcher();
         }
      }

      new Help("index").enterEventDispatcher();
   }

   private static final void registerWizards() {
      SetupWizardRegistration.registerWizardProvider(new Help$1());
   }
}
