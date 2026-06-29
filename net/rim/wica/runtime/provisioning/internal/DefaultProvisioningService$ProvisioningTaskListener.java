package net.rim.wica.runtime.provisioning.internal;

import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.WritableDataStream;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.provisioning.ProvisioningEvent;

final class DefaultProvisioningService$ProvisioningTaskListener implements EventListener {
   private final DefaultProvisioningService this$0;

   private DefaultProvisioningService$ProvisioningTaskListener(DefaultProvisioningService this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      ProvisioningEvent pe = (ProvisioningEvent)data;
      switch (pe.getType()) {
         case 1:
            this.sendStatusUpdateToProvWiclet(pe);
            return;
         case 2:
            this.onSuccess(pe, 2);
            return;
         case 3:
         default:
            this.onError(pe);
            return;
         case 4:
            if (pe.getParam() == 5) {
               this.onCancel(pe);
               return;
            }

            this.sendStatusUpdateToProvWiclet(pe);
            return;
         case 5:
            this.onSuccess(pe, 5);
         case 0:
      }
   }

   private final boolean isAGInterestedInThisEvent(int code) {
      switch (code) {
         case 900:
         case 901:
         case 902:
         case 906:
         case 951:
         case 954:
         case 955:
            return true;
         default:
            return false;
      }
   }

   private final void onCancel(ProvisioningEvent pe) {
      this.sendLifecycleStatusToAG(pe, 0, 902);
   }

   private final void onError(ProvisioningEvent pe) {
      int code = pe.getParam();
      if (this.isAGInterestedInThisEvent(code)) {
         this.sendLifecycleStatusToAG(pe, 0, code);
      }

      this.sendStatusUpdateToProvWiclet(pe);
      this.this$0.logException(pe.getErrorMessage(), pe.getProvisioningError());
   }

   private final void onSuccess(ProvisioningEvent pe, int type) {
      if (type == 2) {
         this.sendLifecycleStatusToAG(pe, 0, 900);
      } else if (type == 5) {
         this.sendLifecycleStatusToAG(pe, 1, 900);
      }

      this.sendStatusUpdateToProvWiclet(pe);
   }

   private final void sendLifecycleStatusToAG(ProvisioningEvent pe, int action, int status) {
      DefaultProvisioningService$ProvisioningTask pt = (DefaultProvisioningService$ProvisioningTask)pe.getProvisioningTaskInfo();
      DeploymentDescriptor dd = (DeploymentDescriptor)pt.getDD();
      long wicletId = pt.getApplicationId();
      long oldWicletId = 0;
      if (action == 1) {
         oldWicletId = (Long)pe.getEventCookie();
      }

      if (pt.shouldAGBeNotified()) {
         if (oldWicletId != 0) {
            this.this$0._lifecycleService.sendStatusMessage(oldWicletId, wicletId, dd.getUri(), dd.getVersion(), pt.getLanguage(), action, status);
            return;
         }

         this.this$0._lifecycleService.sendStatusMessage(wicletId, dd.getUri(), dd.getVersion(), pt.getLanguage(), action, status);
      }
   }

   private final void sendStatusUpdateToProvWiclet(ProvisioningEvent pe) {
      String errorMessage = pe.getErrorMessage();
      if (errorMessage == null) {
         errorMessage = "";
      }

      this.sendStatusUpdateToProvWiclet(pe, errorMessage);
   }

   private final void sendStatusUpdateToProvWiclet(ProvisioningEvent pe, String errorMessage) {
      DefaultProvisioningService$ProvisioningTask pt = (DefaultProvisioningService$ProvisioningTask)pe.getProvisioningTaskInfo();
      int statusUpdate = pe.getParam();
      if (pt.shouldProvWicletBeNotified()) {
         Message message = this.this$0._messagingService.createMessageInstance();
         message.setDestinationType(1);
         message.setWicletID(this.this$0._lifecycleService.getWiclet("rim.net/mds/provisioning").getId());
         message.setMessageName(DefaultProvisioningService.PROVISIONING_STATUS_UPDATE_MSG_NAME);
         WritableDataStream stream = message.openWritableDataStream();
         stream.startComponentWrite(false);
         stream.writeInt(statusUpdate);
         stream.writeString(errorMessage);

         try {
            this.this$0._messagingService.sendMessage(message);
            return;
         } catch (MessagingException e) {
            this.this$0.logException("Could not send status update message to listener ", e);
         }
      }
   }

   DefaultProvisioningService$ProvisioningTaskListener(DefaultProvisioningService x0, DefaultProvisioningService$1 x1) {
      this(x0);
   }
}
