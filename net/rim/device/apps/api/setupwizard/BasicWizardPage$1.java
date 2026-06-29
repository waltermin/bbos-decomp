package net.rim.device.apps.api.setupwizard;

class BasicWizardPage$1 implements Runnable {
   private final BasicWizardPage this$0;

   BasicWizardPage$1(BasicWizardPage _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.resetFonts();
   }
}
