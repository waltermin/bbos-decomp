package net.rim.device.apps.internal.activation;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.provisioning.ActivationService;

final class ActivationScreen$ActivationVerb extends Verb implements Runnable {
   private final ActivationScreen this$0;

   ActivationScreen$ActivationVerb(ActivationScreen _1) {
      super(524800, ActivationApp._resources, 113);
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.invoke(null);
   }

   @Override
   public final Object invoke(Object anObject) {
      String email = this.this$0._emailEditField.getText();
      String password = this.this$0._passwordEditField.getText();
      if (!this.this$0.validate(email, password)) {
         return null;
      }

      this.this$0._emailAddress = email;
      this.this$0._statusInformation.setLength(0);
      this.this$0.updateStatus();
      ServiceRecord[] records = ServiceBook.getSB().findRecordsByCid("CMIME");
      boolean emailAddressisActive = this.this$0.checkEmailAddressActive(email);
      if (ActivationService.hasThisDeviceBeenActivated() && !emailAddressisActive) {
         String questionMessage = null;
         if (records != null && records.length > 0) {
            questionMessage = ActivationApp._resources.getString(148);
         } else {
            questionMessage = ActivationApp._resources.getString(120);
         }

         if (Dialog.ask(3, questionMessage, 4) == 4 && Dialog.ask(3, ActivationApp._resources.getString(131), 4) == 4) {
            this.this$0.wipeHandheld(email, password);
         }
      }

      String activationServerAddress = null;
      if (this.this$0._activationServerField != null) {
         activationServerAddress = this.this$0._activationServerField.getText();
         ((ActivationServiceImpl)this.this$0._activationService)._lastActivationServerAddress = activationServerAddress;
      }

      ((ActivationServiceImpl)this.this$0._activationService)._lastEmailAddress = email;
      this.this$0._activationService.createOtaKeyGenSR(email, email, activationServerAddress);
      emailAddressisActive = RadioInfo.getActiveWAFs() != 0;
      boolean waitingForConnection = false;
      if (!emailAddressisActive || !ServiceRouting.getInstance().isServiceCapable(8, email, -1)) {
         if (!emailAddressisActive && this.this$0._app.displayRadioToggleDialog() != 0) {
            return null;
         }

         waitingForConnection = true;
         this.this$0._mainManager.delete(this.this$0._activationInputFields);
         this.this$0._statusInformation.append(ActivationApp._resources.getString(171));
         this.this$0.updateStatus();
      }

      this.this$0._app.setCurrentState(1);
      ((ActivationServiceImpl)ActivationService.getInstance()).iconRefresh();
      ActivationScreen$StartActivationRunnable startActRunnable = new ActivationScreen$StartActivationRunnable(this.this$0, waitingForConnection);
      this.this$0._connWaiterThread = new Thread(startActRunnable);
      this.this$0._connWaiterThread.start();
      return null;
   }
}
