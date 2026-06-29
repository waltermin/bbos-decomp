package net.rim.device.apps.internal.help;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;

final class TipsAndTricksWizardProvider extends HelpWizardProvider {
   public TipsAndTricksWizardProvider() {
      super(
         ResourceBundle.getBundle(-6124962742482799009L, "net.rim.device.apps.internal.resource.Help"),
         10,
         1200,
         SetupWizardOrdering.TIPS_AND_TRICKS_CATEGORY,
         2
      );
      this.setTopics(HelpWizardProvider.WIZARD_TIPS_TOPICS);
   }
}
