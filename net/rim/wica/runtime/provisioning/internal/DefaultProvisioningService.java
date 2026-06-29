package net.rim.wica.runtime.provisioning.internal;

import net.rim.device.api.util.ToIntHashtable;
import net.rim.wica.runtime.access.internal.data.AccessDataServiceImpl;
import net.rim.wica.runtime.comm.CommunicationService;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.messaging.MessageConsumer;
import net.rim.wica.runtime.messaging.MessagingService;
import net.rim.wica.runtime.persistence.CollectionSyncModel;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.provisioning.ProvisioningEvent;
import net.rim.wica.runtime.provisioning.ProvisioningService;
import net.rim.wica.runtime.provisioning.ProvisioningTaskInfo;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.service.Startable;
import net.rim.wica.runtime.util.BoundedLinkedQueue;

public class DefaultProvisioningService implements ProvisioningService, Serviceable, Startable {
   private CommunicationService _commService;
   private DefaultProvisioningService$ProvisioningTask _currentProvisioningTask;
   private EventService _eventService;
   private LifecycleService _lifecycleService;
   private MessagingService _messagingService;
   private PersistenceService _persistenceService;
   private BoundedLinkedQueue _provQueue;
   private MessageConsumer _provWicletMessageConsumer;
   private UniqueCodeGenerator _uniqueCodeGenerator;
   private DefaultProvisioningService$ProvisioningTask STOP_THREAD_TASK = new DefaultProvisioningService$ProvisioningTask(this);
   private static final int MEMORY_CONSTANT_FACTOR = 3;
   private static String CANCEL_PROVISIONING_MSG_NAME = "requestCancelProvisioning";
   private static String PLAINTEXT_WICLET_XML_FILENAME = "wiclet.xml";
   private static final String PROV_SVC_URL = "local://ProvisioningService";
   private static String PROVISIONING_STATUS_UPDATE_MSG_NAME = "statusUpdateEvent";
   private static String START_PROVISIONING_MSG_NAME = "requestStartProvisioning";
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$comm$CommunicationService;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$lifecycle$LifecycleService;
   static Class class$net$rim$wica$runtime$messaging$MessagingService;

   public DefaultProvisioningService() {
      this._provQueue = new BoundedLinkedQueue();
   }

   private void cancelProvisioning(String uri) {
      if (this._currentProvisioningTask != null && !this._currentProvisioningTask._cancelled) {
         this._currentProvisioningTask.cancel();
      } else {
         this.fireProvisioningEvent(new DefaultProvisioningService$ProvisioningCancelTask(uri), 4, 5);
      }
   }

   public UniqueCodeGenerator getUniqueCodeGenerator() {
      return this._uniqueCodeGenerator;
   }

   public void installApplication(DeploymentDescriptor descriptor, String packageUrl, int language) {
      this.installApplication(descriptor, packageUrl, -1, language, true, false, false, null);
   }

   @Override
   public void installApplication(
      DeploymentDescriptor descriptor,
      String packageUrl,
      long applicationId,
      int language,
      boolean ribbonVisible,
      boolean systemPrivileges,
      boolean persistPackage,
      CollectionSyncModel[] collections
   ) {
      int notificationFlags = 0;
      notificationFlags |= 1;
      notificationFlags |= 2;
      this.installApplication(descriptor, packageUrl, applicationId, language, ribbonVisible, systemPrivileges, notificationFlags, persistPackage, collections);
   }

   @Override
   public void installApplication(
      DeploymentDescriptor descriptor,
      String packageUrl,
      long applicationId,
      int language,
      boolean ribbonVisible,
      boolean systemPrivileges,
      int notificationFlags,
      boolean persistPackage,
      CollectionSyncModel[] collections
   ) {
      DefaultProvisioningService$ProvisioningTask task = new DefaultProvisioningService$ProvisioningTask(
         this, descriptor, packageUrl, applicationId, language, ribbonVisible, systemPrivileges, notificationFlags, persistPackage, null, collections
      );
      this._provQueue.put(task);
   }

   @Override
   public void installApplication(
      DeploymentDescriptor descriptor,
      byte[] packageContents,
      long applicationId,
      int language,
      boolean ribbonVisible,
      boolean systemPrivileges,
      int notificationFlags,
      boolean persistPackage,
      CollectionSyncModel[] collections
   ) {
      DefaultProvisioningService$ProvisioningTask task = new DefaultProvisioningService$ProvisioningTask(
         this, descriptor, null, applicationId, language, ribbonVisible, systemPrivileges, notificationFlags, persistPackage, packageContents, collections
      );
      this._provQueue.put(task);
   }

   @Override
   public void setServices(ServiceProvider serviceProvider) {
      this._eventService = (EventService)serviceProvider.getService(
         class$net$rim$wica$runtime$event$EventService == null
            ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
            : class$net$rim$wica$runtime$event$EventService
      );
      this._commService = (CommunicationService)serviceProvider.getService(
         class$net$rim$wica$runtime$comm$CommunicationService == null
            ? (class$net$rim$wica$runtime$comm$CommunicationService = class$("net.rim.wica.runtime.comm.CommunicationService"))
            : class$net$rim$wica$runtime$comm$CommunicationService
      );
      this._persistenceService = (PersistenceService)serviceProvider.getService(
         class$net$rim$wica$runtime$persistence$PersistenceService == null
            ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
            : class$net$rim$wica$runtime$persistence$PersistenceService
      );
      this._lifecycleService = (LifecycleService)serviceProvider.getService(
         class$net$rim$wica$runtime$lifecycle$LifecycleService == null
            ? (class$net$rim$wica$runtime$lifecycle$LifecycleService = class$("net.rim.wica.runtime.lifecycle.LifecycleService"))
            : class$net$rim$wica$runtime$lifecycle$LifecycleService
      );
      this._messagingService = (MessagingService)serviceProvider.getService(
         class$net$rim$wica$runtime$messaging$MessagingService == null
            ? (class$net$rim$wica$runtime$messaging$MessagingService = class$("net.rim.wica.runtime.messaging.MessagingService"))
            : class$net$rim$wica$runtime$messaging$MessagingService
      );
      ToIntHashtable standardComponentCodes = (ToIntHashtable)(new Object(
         AccessDataServiceImpl.getNumStdCompDefs() + (AccessDataServiceImpl.getNumStdCompDefs() >> 1)
      ));
      AccessDataServiceImpl.getAllStdCompDefs(standardComponentCodes);
      this._uniqueCodeGenerator = new UniqueCodeGenerator(standardComponentCodes);
   }

   @Override
   public void start() {
      this._provWicletMessageConsumer = new DefaultProvisioningService$ProvWicletMessageConsumer(this, null);
      this._messagingService.registerServiceMessageConsumer("local://ProvisioningService", this._provWicletMessageConsumer);
      Thread t = new DefaultProvisioningService$1(this, "ProvisioningThread");
      t.start();
   }

   @Override
   public void stop() {
      this._messagingService.deregisterServiceMessageConsumer(this._provWicletMessageConsumer);
      this._provQueue.put(this.STOP_THREAD_TASK);
   }

   @Override
   public String toString() {
      return "ProvisioningService";
   }

   private void fireProvisioningEvent(ProvisioningTaskInfo info, int type, int param, String errorMessage, Throwable t) {
      ProvisioningEvent pe = new ProvisioningEvent(info, type, param, t, errorMessage);
      this._eventService.dispatchEvent(this, 500, pe);
   }

   private void fireProvisioningEvent(ProvisioningTaskInfo info, int type, int param, String errorMessage) {
      this.fireProvisioningEvent(info, type, param, errorMessage, null);
   }

   private void fireProvisioningEvent(ProvisioningTaskInfo info, int type, int param) {
      this.fireProvisioningEvent(info, type, param, "", null);
   }

   private void fireProvisioningEvent(ProvisioningTaskInfo info, Object cookie, int type, int param, String errorMessage, Throwable t) {
      ProvisioningEvent pe = new ProvisioningEvent(info, cookie, type, param, t, errorMessage);
      this._eventService.dispatchEvent(this, 500, pe);
   }

   private void logException(String message, Throwable t) {
      Logger.log(this.toString(), ((StringBuffer)(new Object())).append(message).append("; Exception: ").append(t).toString(), 3);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
