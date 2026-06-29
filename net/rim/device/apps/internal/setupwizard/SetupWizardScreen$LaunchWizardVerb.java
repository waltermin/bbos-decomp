package net.rim.device.apps.internal.setupwizard;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.setupwizard.WizardCategory;
import net.rim.device.apps.api.setupwizard.WizardPage;

final class SetupWizardScreen$LaunchWizardVerb extends Verb {
   private final SetupWizardScreen this$0;

   public SetupWizardScreen$LaunchWizardVerb(SetupWizardScreen _1) {
      super(0);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object wizardList) {
      WizardPage page = this.this$0._wizardList.getPage(this.this$0._wizardList.getSelectedIndex());
      if (this.this$0._wizardList.getCategoryMode() != 2 && page instanceof WizardCategory) {
         return null;
      }

      this.this$0.startWizard(page, 1);
      return page;
   }
}
