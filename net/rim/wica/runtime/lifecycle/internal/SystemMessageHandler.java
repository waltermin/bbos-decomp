package net.rim.wica.runtime.lifecycle.internal;

import net.rim.wica.runtime.lifecycle.LifecycleException;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessageConsumer;
import net.rim.wica.runtime.messaging.MessageException;
import net.rim.wica.runtime.messaging.MessagingService;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.messaging.WritableDataStream;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;

final class SystemMessageHandler implements MessageConsumer {
   private LifecycleServiceImpl _lifecycleService;
   private ManagementService _managementService;
   private MessagingService _messagingService;
   static Class class$net$rim$wica$runtime$management$ManagementService;
   static Class class$net$rim$wica$runtime$messaging$MessagingService;

   final void stop() {
      this._messagingService.deregisterSystemMessageConsumer(this);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void sendStatusMessage(long id, long newId, String uri, String version, String locale, int action, int status, long agId) {
      Message message = this._messagingService.createMessageInstance();
      long defaultAgId = this._managementService.getRuntimeInfo().getDefaultAGInfo().getAgID();
      message.setAGID(agId == 0 ? defaultAgId : agId);
      message.setMessageCode(132);
      WritableDataStream stream = message.openWritableDataStream();
      stream.startComponentWrite(false);
      stream.writeLong(id);
      stream.writeString(uri);
      stream.writeString(version);
      stream.writeString(locale);
      stream.writeInt(action);
      stream.writeInt(status);
      stream.writeBoolean(agId != 0);
      stream.writeLong(newId);

      try {
         this._messagingService.sendMessage(message);
      } catch (Throwable var18) {
         Logger.log("Error encountered while sending Lifecycle status message:\n" + e);
         return;
      }
   }

   @Override
   public final Message processMessage(Message requestMessage) {
      Message responseMessage = null;
      switch (requestMessage.getMessageCode()) {
         case 128:
            responseMessage = this.processNotifyUpgradeMessage(requestMessage);
         case 127:
         case 131:
         case 132:
            return responseMessage;
         case 129:
         default:
            return this.processUninstallMessage(requestMessage);
         case 130:
            return this.processDisableMessage(requestMessage);
         case 133:
            return this.processInstallMessage(requestMessage);
      }
   }

   SystemMessageHandler(LifecycleServiceImpl lifecycleService) {
      this._lifecycleService = lifecycleService;
      this._managementService = (ManagementService)this._lifecycleService
         .getService(
            class$net$rim$wica$runtime$management$ManagementService == null
               ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
               : class$net$rim$wica$runtime$management$ManagementService
         );
      this._messagingService = (MessagingService)this._lifecycleService
         .getService(
            class$net$rim$wica$runtime$messaging$MessagingService == null
               ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
               : class$net$rim$wica$runtime$messaging$MessagingService
         );
      this._messagingService
         .registerSystemMessageConsumer(new int[]{129, 133, 130, 128, -804651003, 404, 403, 406, 405, 1000, -804651005, 200, 201, 202, -804651004, 500}, this);
   }

   private final void logMessageException(MessageException exception, int messageCode) {
      StringBuffer buffer = new StringBuffer("System Message Processing Error\n");
      buffer.append("Message code: ");
      buffer.append(messageCode);
      buffer.append('\n');
      buffer.append(exception);
      Logger.log(buffer.toString(), 3);
   }

   private final Message processDisableMessage(Message requestMessage) {
      try {
         ReadableDataStream stream = requestMessage.openReadableDataStream();
         long wicletId = stream.readLong();
         boolean quarantine = stream.readBoolean();
         this._lifecycleService.quarantineWiclet(wicletId, quarantine);
         return null;
      } catch (LifecycleException le) {
         this.sendRuntimeStatusMessage(requestMessage.getAGID());
         return null;
      } catch (MessageException me) {
         this.logMessageException(me, requestMessage.getMessageCode());
         return null;
      }
   }

   private final Message processInstallMessage(Message requestMessage) {
      try {
         ReadableDataStream stream = requestMessage.openReadableDataStream();
         DeploymentDescriptor descriptor = DeploymentDescriptor.readFromStream(stream);
         int type = stream.readInt();
         String packageLocation = null;
         long applicationId = 0;
         if (type == 3) {
            packageLocation = stream.readString();
            applicationId = stream.readLong();
         }

         this._lifecycleService.installApplication(type, applicationId, packageLocation, descriptor);
         return null;
      } catch (MessageException e) {
         this.logMessageException(e, requestMessage.getMessageCode());
         return null;
      }
   }

   private final Message processNotifyUpgradeMessage(Message requestMessage) {
      try {
         ReadableDataStream stream = requestMessage.openReadableDataStream();
         DeploymentDescriptor upgradeDescriptor = DeploymentDescriptor.readFromStream(stream);
         long expiryDate = stream.readLong();
         this._lifecycleService.upgradeWiclet(upgradeDescriptor, expiryDate);
         return null;
      } catch (MessageException e) {
         this.logMessageException(e, requestMessage.getMessageCode());
         return null;
      }
   }

   private final Message processUninstallMessage(Message requestMessage) {
      try {
         ReadableDataStream stream = requestMessage.openReadableDataStream();
         long wicletId = stream.readLong();
         boolean graceful = stream.readBoolean();
         long expiryDate = 0;
         if (graceful) {
            expiryDate = stream.readLong();
         }

         this._lifecycleService.uninstallWiclet(wicletId, 1, expiryDate);
         return null;
      } catch (LifecycleException le) {
         this.sendRuntimeStatusMessage(requestMessage.getAGID());
         return null;
      } catch (MessageException me) {
         this.logMessageException(me, requestMessage.getMessageCode());
         return null;
      }
   }

   private final void sendRuntimeStatusMessage(long agId) {
      this._managementService.sendREStatusMessage(agId);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
