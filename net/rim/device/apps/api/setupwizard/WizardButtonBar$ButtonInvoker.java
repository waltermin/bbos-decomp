package net.rim.device.apps.api.setupwizard;

class WizardButtonBar$ButtonInvoker implements Runnable {
   private WizardButtonBar$WizardButtonField _button;
   private final WizardButtonBar this$0;

   public WizardButtonBar$ButtonInvoker(WizardButtonBar _1, WizardButtonBar$WizardButtonField button) {
      this.this$0 = _1;
      this._button = button;
   }

   @Override
   public void run() {
      this.this$0.handleButtonClick(this._button);
   }
}
