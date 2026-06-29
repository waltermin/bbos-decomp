package net.rim.device.apps.internal.help;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.setupwizard.SetupWizardOrdering;

final class ShortcutsWizardProvider extends HelpWizardProvider {
   public ShortcutsWizardProvider() {
      super(ResourceBundle.getBundle(-6124962742482799009L, "net.rim.device.apps.internal.resource.Help"), 17, 1100, SetupWizardOrdering.SHORTCUTS_CATEGORY, 2);
      this.setTopics(HelpWizardProvider.WIZARD_SHORTCUTS_TOPICS);
   }
}
