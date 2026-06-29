package net.rim.device.apps.api.setupwizard;

import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.apps.api.setupwizard.resources.SetupWizardAPIResources;

public class WizardButtonBar$WizardButtonField extends ButtonField {
   private int _command;
   private final WizardButtonBar this$0;

   public WizardButtonBar$WizardButtonField(WizardButtonBar _1, int command) {
      super(12884901888L);
      this.this$0 = _1;
      this._command = command;
      this.loadLabel();
   }

   @Override
   public boolean trackwheelClick(int status, int time) {
      this.this$0.handleButtonClick(this);
      return true;
   }

   @Override
   protected boolean invokeAction(int action) {
      switch (action) {
         case 1:
            this.this$0.handleButtonClick(this);
            return true;
         default:
            return super.invokeAction(action);
      }
   }

   public int getCommand() {
      return this._command;
   }

   public void loadLabel() {
      String label;
      switch (this._command) {
         case 0:
            label = SetupWizardAPIResources.getString(4);
            break;
         case 1:
            label = "< " + SetupWizardAPIResources.getString(2);
            break;
         case 2:
         default:
            label = SetupWizardAPIResources.getString(3) + " >";
            break;
         case 3:
            label = SetupWizardAPIResources.getString(8);
      }

      this.setLabel(label);
   }
}
