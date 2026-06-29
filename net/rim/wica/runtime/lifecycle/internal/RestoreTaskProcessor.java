package net.rim.wica.runtime.lifecycle.internal;

import java.util.Vector;
import net.rim.device.api.util.LongHashtable;
import net.rim.wica.packaging.PackageUtilities;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.persistence.ApplicationSyncModel;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.provisioning.ProvisioningEvent;
import net.rim.wica.runtime.provisioning.ProvisioningService;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.ui.HomeScreenUtilities;

final class RestoreTaskProcessor implements Runnable, EventListener {
   private ServiceProvider _provider;
   private PersistenceService _persistence;
   private Vector _restoredApplications;
   private LongHashtable _pendingInstalls;
   private String _serverUrl;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$provisioning$ProvisioningService;
   static Class class$net$rim$wica$runtime$management$ManagementService;

   RestoreTaskProcessor(ServiceProvider provider, Vector restoredApplications) {
      this._provider = provider;
      this._restoredApplications = restoredApplications;
      this._persistence = (PersistenceService)this._provider
         .getService(
            class$net$rim$wica$runtime$persistence$PersistenceService == null
               ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
               : class$net$rim$wica$runtime$persistence$PersistenceService
         );
   }

   @Override
   public final void run() {
      int length = this._restoredApplications.size();
      this._pendingInstalls = new LongHashtable(length);
      EventService eventService = (EventService)this._provider
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      eventService.addListener(500, this);
      ProvisioningService provisioning = (ProvisioningService)this._provider
         .getService(
            class$net$rim$wica$runtime$provisioning$ProvisioningService == null
               ? (class$net$rim$wica$runtime$provisioning$ProvisioningService = class$("net.rim.wica.runtime.provisioning.ProvisioningService"))
               : class$net$rim$wica$runtime$provisioning$ProvisioningService
         );

      for (int i = 0; i < length; i++) {
         ApplicationSyncModel model = (ApplicationSyncModel)this._restoredApplications.elementAt(i);
         DeploymentDescriptor descriptor = model.getDescriptor();
         String packageLocation = model.getPackageLocation();
         if (packageLocation == null) {
            packageLocation = PackageUtilities.constructWicletPackageURL(
               this.getServerUrl(), PackageUtilities.getFullPackageName(descriptor.getUri(), descriptor.getVersion(), descriptor.getLanguages()[0])
            );
         }

         RestoreTaskProcessor$RestoreEntrypoint entrypoint = new RestoreTaskProcessor$RestoreEntrypoint(model);
         HomeScreenUtilities.registerEntry(entrypoint);
         this._pendingInstalls.put(model.getId(), entrypoint);
         provisioning.installApplication(descriptor, packageLocation, model.getId(), -1, true, false, 1, false, model.getCollections());
      }
   }

   private final String getServerUrl() {
      if (this._serverUrl == null) {
         ManagementService management = (ManagementService)this._provider
            .getService(
               class$net$rim$wica$runtime$management$ManagementService == null
                  ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
                  : class$net$rim$wica$runtime$management$ManagementService
            );
         this._serverUrl = management.getRuntimeInfo().getDefaultAGInfo().getAgCompactMsgURL();
         this._serverUrl = this._serverUrl.substring(0, this._serverUrl.lastIndexOf(47));
      }

      return this._serverUrl;
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      ProvisioningEvent details = (ProvisioningEvent)data;
      long id = details.getProvisioningTaskInfo().getApplicationId();
      if (this._pendingInstalls.containsKey(id)) {
         RestoreTaskProcessor$RestoreEntrypoint entrypoint = (RestoreTaskProcessor$RestoreEntrypoint)this._pendingInstalls.get(id);
         ApplicationSyncModel model = entrypoint._model;
         switch (details.getType()) {
            case 1:
               break;
            case 3:
            default:
               Logger.log("Unable to restore application:\n" + model.getDescriptor().getUri());
            case 2:
               HomeScreenUtilities.unregisterEntry(entrypoint);
               this._pendingInstalls.remove(id);
               this._restoredApplications.removeElement(model);
               this._persistence.storeRestoredApplications(this._restoredApplications);
               if (this._restoredApplications.size() == 0) {
                  EventService eventService = (EventService)this._provider
                     .getService(
                        class$net$rim$wica$runtime$event$EventService == null
                           ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
                           : class$net$rim$wica$runtime$event$EventService
                     );
                  eventService.removeListener(500, this);
               }
         }
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
