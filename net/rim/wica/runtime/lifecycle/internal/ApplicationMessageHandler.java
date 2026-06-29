package net.rim.wica.runtime.lifecycle.internal;

import java.util.Vector;
import net.rim.wica.runtime.lifecycle.LifecycleException;
import net.rim.wica.runtime.management.ClientAdminPolicy;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessageConsumer;
import net.rim.wica.runtime.messaging.MessageException;
import net.rim.wica.runtime.messaging.MessagingException;
import net.rim.wica.runtime.messaging.MessagingService;
import net.rim.wica.runtime.messaging.ReadableDataStream;
import net.rim.wica.runtime.messaging.WritableDataStream;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.util.LongVector;

final class ApplicationMessageHandler implements MessageConsumer {
   private LifecycleServiceImpl _lifecycleService;
   private MessagingService _messagingService;
   private ManagementService _managementService;
   private LongVector _managementListeners;
   private LongVector _lifecycleListeners;
   private static String START_PROVISIONING_MSG_NAME = "startProvisioningRequest";
   private static final String REGISTER_MANAGEMENT_EVENTS_MSG = "outRegisterManagementEvents";
   private static final String UNREGISTER_MANAGEMENT_EVENTS_MSG = "outUnregisterManagementEvents";
   private static final String REGISTER_LIFECYCLE_EVENTS_MSG = "outRegisterLifecycleEvents";
   private static final String UNREGISTER_LIFECYCLE_EVENTS_MSG = "outUnregisterLifecycleEvents";
   private static final String CLIENT_ADMIN_POLICY_REQ_MSG = "outGetClientAdminPolicy";
   private static final String CLIENT_ADMIN_POLICY_RESP_MSG = "inGetClientAdminPolicy";
   private static final String APPLICATION_INFO_REQ_MSG = "outGetApplicationInfo";
   private static final String APPLICATION_INFO_RESP_MSG = "inGetApplicationInfo";
   private static final String LIFECYCLE_ACTION_MSG = "outLifecycleAction";
   private static final String CLIENT_ADMIN_POLICY_UPDATE_MSG = "inClientAdminPolicyUpdated";
   private static final String APPLICATION_INSTALL_MSG = "inApplicationInstalled";
   private static final String APPLICATION_UPDATE_MSG = "inApplicationUpdated";
   private static final String APPLICATION_STATE_MSG = "inApplicationState";
   private static final String APPLICATION_UNINSTALL_MSG = "inApplicationUninstalled";
   private static final int LIFECYCLE_START_ACTION = 1;
   private static final int LIFECYCLE_UNINSTALL_ACTION = 2;
   private static final int LIFECYCLE_UPGRADE_ACTION = 3;
   private static final int LIFECYCLE_CLEAR_CACHE_ACTION = 4;
   static Class class$net$rim$wica$runtime$management$ManagementService;
   static Class class$net$rim$wica$runtime$messaging$MessagingService;

   ApplicationMessageHandler(LifecycleServiceImpl lifecycleService) {
      this._lifecycleService = lifecycleService;
      this._managementService = (ManagementService)lifecycleService.getService(
         class$net$rim$wica$runtime$management$ManagementService == null
            ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
            : class$net$rim$wica$runtime$management$ManagementService
      );
      this._messagingService = (MessagingService)lifecycleService.getService(
         class$net$rim$wica$runtime$messaging$MessagingService == null
            ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
            : class$net$rim$wica$runtime$messaging$MessagingService
      );
      this._messagingService.registerServiceMessageConsumer("local://" + this._lifecycleService.toString(), this);
      this._managementListeners = new LongVector(5);
      this._lifecycleListeners = new LongVector(5);
   }

   final void stop() {
      this._messagingService.deregisterServiceMessageConsumer(this);
      this._managementService = null;
      this._messagingService = null;
      this._lifecycleService = null;
   }

   final void onClientAdminPolicyUpdated() {
      for (int i = this._managementListeners.size() - 1; i >= 0; i--) {
         this.sendClientAdminPolicyUpdatedMessage(this._managementListeners.elementAt(i));
      }
   }

   final void onApplicationInstalled(WicletImpl application) {
      for (int i = this._lifecycleListeners.size() - 1; i >= 0; i--) {
         this.sendApplicationInstalledMessage(this._lifecycleListeners.elementAt(i), application);
      }
   }

   final void onApplicationUpdated(WicletImpl application) {
      for (int i = this._lifecycleListeners.size() - 1; i >= 0; i--) {
         this.sendApplicationUpdatedMessage(this._lifecycleListeners.elementAt(i), application);
      }
   }

   final void onApplicationState(WicletImpl application) {
      for (int i = this._lifecycleListeners.size() - 1; i >= 0; i--) {
         this.sendApplicationStateMessage(this._lifecycleListeners.elementAt(i), application);
      }
   }

   final void onApplicationUninstalled(WicletImpl application) {
      for (int i = this._lifecycleListeners.size() - 1; i >= 0; i--) {
         this.sendApplicationUninstalledMessage(this._lifecycleListeners.elementAt(i), application);
      }
   }

   final void sendProvisioningMessage(DeploymentDescriptor dd) {
      Message message = this._messagingService.createMessageInstance();
      message.setDestinationType(1);
      message.setWicletID(this._lifecycleService.getWiclet("rim.net/mds/provisioning").getId());
      message.setMessageName(START_PROVISIONING_MSG_NAME);
      WritableDataStream stream = message.openWritableDataStream();
      DeploymentDescriptor.writeToStream(dd, stream);
      this._messagingService.sendMessage(message);
   }

   private static final void writeApplicationInfo(WritableDataStream stream, WicletImpl application) {
      stream.startComponentWrite(false);
      stream.writeString(Long.toString(application.getId()));
      stream.writeString(application.getName());
      stream.writeString(application.getVersion());
      stream.writeString(application.getDefaultLocale().getDisplayName());
      stream.writeLong(application.getInstallDate());
      stream.writeInt(application.getMetadataSize());
      stream.writeInt(application.getDataSize());
      stream.writeInt(application.getCacheSize());
      stream.writeBoolean(application.isQuarantined());
      stream.writeBoolean(application.hasPendingAlerts());
      stream.writeString(application.getVendor());
      stream.writeString(application.getDescription());
      String iconUri = application.getIconUri() == null ? "default" : "local://" + application.getUri() + "/" + application.getIconUri();
      stream.writeString(iconUri);
      stream.writeBoolean(application.isRunning());
      stream.writeBoolean(application.hasUpgrade());
      stream.writeBoolean(application.isUpgrading());
      writeApplicationUpgradeInfo(stream, application);
   }

   private final void writeClientAdminPolicy(WritableDataStream stream) {
      ClientAdminPolicy policy = this._managementService.getRuntimeInfo().getClientAdminPolicy();
      stream.startComponentWrite(false);
      stream.writeBoolean(policy.isDeleteAllowed());
      stream.writeBoolean(policy.isDiscoveryAllowed());
      stream.writeInt(policy.getPullProvisioningAllowed());
      stream.writeBoolean(policy.isMultiDomainAllowed());
   }

   private static final void writeApplicationUpgradeInfo(WritableDataStream stream, WicletImpl application) {
      if (!application.hasUpgrade()) {
         stream.startComponentWrite(true);
      } else {
         stream.startComponentWrite(false);
         stream.writeLong(application.getUpgradeExpiryDate());
         stream.writeString(application.getUpgradeDescriptor().getVersion());
         stream.writeBoolean(application.getUpgradeDescriptor().isEndorsed());
         stream.writeString(application.getUpgradeDescriptor().getVendor());
         stream.writeString(application.getUpgradeDescriptor().getDescription());
      }
   }

   private final Message processGetApplicationInfoMessage(Message message) {
      Message response = this._messagingService.createMessageInstance();
      response.setDestinationType(1);
      response.setWicletID(message.getWicletID());
      response.setMessageName("inGetApplicationInfo");
      WritableDataStream stream = response.openWritableDataStream();
      Vector applications = this._lifecycleService.getUserApplications();
      int length = applications.size();
      stream.startComponentArrayWrite(length);
      WicletImpl application = null;

      for (int i = 0; i < length; i++) {
         application = (WicletImpl)applications.elementAt(i);
         writeApplicationInfo(stream, application);
      }

      return response;
   }

   private final Message processGetClientAdminPolicyMessage(Message message) {
      Message response = this._messagingService.createMessageInstance();
      response.setDestinationType(1);
      response.setWicletID(message.getWicletID());
      response.setMessageName("inGetClientAdminPolicy");
      WritableDataStream stream = response.openWritableDataStream();
      this.writeClientAdminPolicy(stream);
      return response;
   }

   private final Message processLifecycleActionMessage(Message message) {
      try {
         ReadableDataStream readStream = message.openReadableDataStream();
         long id = Long.parseLong(readStream.readString());
         int action = readStream.readInt();
         switch (action) {
            case 0:
               break;
            case 1:
            default:
               ((WicletImpl)this._lifecycleService.getWiclet(id)).run();
               break;
            case 2:
               this._lifecycleService.uninstallWiclet(id, 0);
               break;
            case 3:
               this._lifecycleService.upgradeWiclet(id);
               break;
            case 4:
               this._lifecycleService.clearApplicationResourceCache(id);
               WicletImpl application = (WicletImpl)this._lifecycleService.getWiclet(id);
               Message msg = this._messagingService.createMessageInstance();
               msg.setDestinationType(1);
               msg.setWicletID(message.getWicletID());
               msg.setMessageName("inApplicationState");
               WritableDataStream stream = msg.openWritableDataStream();
               stream.writeString(Long.toString(application.getId()));
               stream.writeBoolean(application.isRunning());
               stream.writeInt(application.getDataSize());
               stream.writeInt(application.getCacheSize());
               return msg;
         }
      } catch (MessageException var9) {
         return null;
      } catch (LifecycleException var10) {
      }

      return null;
   }

   private final void sendClientAdminPolicyUpdatedMessage(long listenerId) {
      Message message = this._messagingService.createMessageInstance();

      try {
         message.setDestinationType(1);
         message.setWicletID(listenerId);
         message.setMessageName("inClientAdminPolicyUpdated");
         WritableDataStream stream = message.openWritableDataStream();
         this.writeClientAdminPolicy(stream);
         this._messagingService.sendMessage(message);
      } catch (MessagingException var5) {
      }
   }

   private final void sendApplicationInstalledMessage(long listenerId, WicletImpl application) {
      Message message = this._messagingService.createMessageInstance();

      try {
         message.setDestinationType(1);
         message.setWicletID(listenerId);
         message.setMessageName("inApplicationInstalled");
         WritableDataStream stream = message.openWritableDataStream();
         writeApplicationInfo(stream, application);
         this._messagingService.sendMessage(message);
      } catch (MessagingException var6) {
      }
   }

   private final void sendApplicationUninstalledMessage(long listenerId, WicletImpl application) {
      Message message = this._messagingService.createMessageInstance();

      try {
         message.setDestinationType(1);
         message.setWicletID(listenerId);
         message.setMessageName("inApplicationUninstalled");
         WritableDataStream stream = message.openWritableDataStream();
         stream.writeString(Long.toString(application.getId()));
         this._messagingService.sendMessage(message);
      } catch (MessagingException var6) {
      }
   }

   private final void sendApplicationUpdatedMessage(long listenerId, WicletImpl application) {
      Message message = this._messagingService.createMessageInstance();

      try {
         message.setDestinationType(1);
         message.setWicletID(listenerId);
         message.setMessageName("inApplicationUpdated");
         WritableDataStream stream = message.openWritableDataStream();
         stream.writeString(Long.toString(application.getId()));
         stream.writeBoolean(application.isQuarantined());
         stream.writeBoolean(application.hasPendingAlerts());
         stream.writeBoolean(application.hasUpgrade());
         stream.writeBoolean(application.isUpgrading());
         writeApplicationUpgradeInfo(stream, application);
         this._messagingService.sendMessage(message);
      } catch (MessagingException var6) {
      }
   }

   private final void sendApplicationStateMessage(long listenerId, WicletImpl application) {
      Message message = this._messagingService.createMessageInstance();

      try {
         message.setDestinationType(1);
         message.setWicletID(listenerId);
         message.setMessageName("inApplicationState");
         WritableDataStream stream = message.openWritableDataStream();
         stream.writeString(Long.toString(application.getId()));
         stream.writeBoolean(application.isRunning());
         stream.writeInt(application.getDataSize());
         stream.writeInt(application.getCacheSize());
         this._messagingService.sendMessage(message);
      } catch (MessagingException var6) {
      }
   }

   @Override
   public final Message processMessage(Message message) {
      String name = message.getMessageName();
      if (name.equals("outRegisterLifecycleEvents")) {
         this._lifecycleListeners.addElement(message.getWicletID());
         return null;
      } else if (name.equals("outUnregisterLifecycleEvents")) {
         this._lifecycleListeners.removeElement(message.getWicletID());
         return null;
      } else if (name.equals("outRegisterManagementEvents")) {
         this._managementListeners.addElement(message.getWicletID());
         return null;
      } else if (name.equals("outUnregisterManagementEvents")) {
         this._managementListeners.removeElement(message.getWicletID());
         return null;
      } else if (name.equals("outGetApplicationInfo")) {
         return this.processGetApplicationInfoMessage(message);
      } else if (name.equals("outGetClientAdminPolicy")) {
         return this.processGetClientAdminPolicyMessage(message);
      } else {
         return name.equals("outLifecycleAction") ? this.processLifecycleActionMessage(message) : null;
      }
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
