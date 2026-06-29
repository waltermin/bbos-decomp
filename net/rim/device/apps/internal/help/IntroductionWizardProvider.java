package net.rim.device.apps.internal.help;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;
import net.rim.device.internal.system.InternalServices;

final class IntroductionWizardProvider extends HelpWizardProvider {
   public IntroductionWizardProvider() {
      super(ResourceBundle.getBundle(-6124962742482799009L, "net.rim.device.apps.internal.resource.Help"), 8, 300, SetupWizardOrdering.INTRO_CATEGORY);
      if (InternalServices.getFormFactor() == 13) {
         this.setTopics(HelpWizardProvider.WIZARD_INTRO_TOPICS_POSITRON);
      } else {
         this.setTopics(HelpWizardProvider.WIZARD_INTRO_TOPICS);
      }
   }
}
