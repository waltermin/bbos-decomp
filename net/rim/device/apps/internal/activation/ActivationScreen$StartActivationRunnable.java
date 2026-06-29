package net.rim.device.apps.internal.activation;

import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener2;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;

final class ActivationScreen$StartActivationRunnable implements Runnable, ServiceRoutingListener2 {
   boolean _waitingForServiceConnection;
   private final ActivationScreen this$0;

   public final synchronized void notifyIfActivationServiceAvailable() {
      String email = this.this$0._emailEditField.getText();
      if (ServiceRouting.getInstance().isServiceCapable(8, email, -1)) {
         this.notify();
      }
   }

   @Override
   public final void serviceRouteStateChanged(int routeHandle, boolean routeState) {
      this.notifyIfActivationServiceAvailable();
   }

   @Override
   public final void serviceRoutingCapabilitiesChanged(String service) {
      this.notifyIfActivationServiceAvailable();
   }

   @Override
   public final void serviceRoutingStateChanged(String service, boolean serviceState) {
      this.notifyIfActivationServiceAvailable();
   }

   @Override
   public final void run() {
      ServiceRouting serviceRouting = ServiceRouting.getInstance();
      String email = this.this$0._emailEditField.getText();
      if (Application.isEventDispatchThread()) {
         if (this.this$0._app.getCurrentState() == 1) {
            if (serviceRouting.isServiceCapable(8, email, -1)) {
               if (this._waitingForServiceConnection) {
                  this.this$0._statusInformation.append(((StringBuffer)(new Object())).append('\n').append(ActivationApp._resources.getString(173)).toString());
                  this.this$0.updateStatus();
               }

               String password = this.this$0._passwordEditField.getText();
               String activationServerAddress = null;
               if (this.this$0._activationServerField != null) {
                  activationServerAddress = this.this$0._activationServerField.getText();
               }

               this.this$0._transactionID = this.this$0._activationService.attemptActivation(email, password, activationServerAddress);
               if (this.this$0._transactionID != 0) {
                  this.this$0._app.setCurrentState(2);
                  this.this$0._activationInputFields.deleteAll();
                  return;
               }

               this.this$0.resetActivationFields();
               this.this$0._emailEditField.setFocus();
               return;
            }

            this.this$0._activationService.resetOtaKeyGenSR();
            if (RadioInfo.getNetworkType() == 6) {
               Dialog.alert(ActivationApp._resources.getString(172));
               this.this$0.resetActivationFields();
               return;
            }

            Dialog.alert(ActivationApp._resources.getString(172));
            this.this$0.resetActivationFields();
         }
      } else {
         serviceRouting.addListener(this);
         if (!serviceRouting.isServiceCapable(8, email, -1)) {
            this._waitingForServiceConnection = true;
            synchronized (this) {
               label81:
               try {
                  this.wait(20000);
               } finally {
                  break label81;
               }
            }
         }

         ServiceRouting.getInstance().removeListener(this);
         if (this.this$0._app.getCurrentState() == 1) {
            this.this$0._app.invokeLater(this);
         }

         this.this$0._connWaiterThread = null;
      }
   }

   ActivationScreen$StartActivationRunnable(ActivationScreen _1, boolean waitingForConnection) {
      this.this$0 = _1;
      this._waitingForServiceConnection = false;
      this._waitingForServiceConnection = waitingForConnection;
      ServiceRouting.getInstance().addListener(this);
   }
}
