package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;

public class BasicWizardPage$WizardVerb extends Verb {
   private final BasicWizardPage this$0;

   public BasicWizardPage$WizardVerb(BasicWizardPage _1, int ordering, int key) {
      super(ordering, 894458828807867933L, "net.rim.device.apps.api.setupwizard.SetupWizardAPI", key);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      if (this.this$0.confirm(this, parameter)) {
         switch (super._rbKey) {
            case 1:
            case 6:
            case 7:
               break;
            case 2:
            default:
               this.this$0._result = 1;
               break;
            case 3:
               this.this$0._result = 2;
               break;
            case 4:
            case 5:
               this.this$0._result = 0;
               break;
            case 8:
               this.this$0._result = 3;
         }

         if (Ui.getUiEngine() == this.this$0._mainScreen.getUiEngine()) {
            UiApplication.getUiApplication().popScreen(this.this$0._mainScreen);
         }
      }

      return null;
   }

   public boolean canAutoSave() {
      return super._rbKey == 3 || super._rbKey == 8;
   }
}
