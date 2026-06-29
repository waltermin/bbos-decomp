package net.rim.wica.runtime.lifecycle.internal;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.wica.common.ReservedWicletConstants;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.ApplicationVersion;
import net.rim.wica.runtime.lifecycle.LifecycleException;
import net.rim.wica.runtime.lifecycle.LifecycleService;
import net.rim.wica.runtime.lifecycle.UninstallTaskInfo;
import net.rim.wica.runtime.lifecycle.UpgradeTaskInfo;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.lifecycle.WicletInfo;
import net.rim.wica.runtime.lifecycle.WicletUpgradeEvent;
import net.rim.wica.runtime.logging.Logger;
import net.rim.wica.runtime.management.ClientAdminPolicy;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.management.RuntimeInfo;
import net.rim.wica.runtime.persistence.CollectionSyncModel;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.persistence.WicletStore;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.provisioning.ProvisioningEvent;
import net.rim.wica.runtime.provisioning.ProvisioningService;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.service.Serviceable;
import net.rim.wica.runtime.service.Startable;
import net.rim.wica.runtime.ui.HomeScreenUtilities;
import net.rim.wica.runtime.util.Util;
import net.rim.wica.runtime.util.Worker;
import net.rim.wica.runtime.util.internal.RuntimeUtilities;

public final class LifecycleServiceImpl implements LifecycleService, EventListener, Serviceable, Startable, ServiceProvider, GlobalEventListener {
   private ServiceProvider _provider;
   private EventService _eventService;
   private PersistenceService _persistenceService;
   private ManagementService _managementService;
   private LifecycleManager _manager;
   private SystemMessageHandler _systemMessageHandler;
   private ApplicationMessageHandler _applicationMessageHandler;
   private LifecycleDialogManager _dialogManager;
   private Worker _worker = new Worker();
   private int _systemAppCounter;
   private static int[] _events = new int[]{
      100,
      107,
      109,
      110,
      111,
      112,
      500,
      1000,
      -804651006,
      105,
      100,
      -804651006,
      106,
      102,
      -804651004,
      129,
      133,
      130,
      128,
      -804651003,
      404,
      403,
      406,
      405,
      1000,
      -804651005,
      200,
      201,
      202,
      -804651004,
      500,
      107
   };
   static Class class$net$rim$wica$runtime$provisioning$ProvisioningService;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$management$ManagementService;

   final LifecycleManager getManager() {
      return this._manager;
   }

   final ApplicationMessageHandler getApplicationMessageHandler() {
      return this._applicationMessageHandler;
   }

   final LifecycleDialogManager getDialogManager() {
      return this._dialogManager;
   }

   final void quarantineWiclet(long id, boolean quarantine) {
      WicletImpl wiclet = (WicletImpl)this.getWiclet(id);
      if (wiclet == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [ID: "))).append(id).append("] does not exist.").toString());
      }

      wiclet.quarantine(quarantine);
   }

   final void uninstallWiclet(long id, int type) {
      this.uninstallWiclet(id, type, 0);
   }

   final void uninstallWiclet(long id, int type, long expiryDate) {
      Logger.log("L UW");
      WicletImpl wiclet = (WicletImpl)this.getWiclet(id);
      if (wiclet == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [ID: "))).append(id).append("] does not exist.").toString());
      }

      wiclet.setUninstallTask(new UninstallTaskInfo(type, expiryDate));
      wiclet.uninstall();
   }

   final void startProvisioning(DeploymentDescriptor dd) {
      try {
         this._applicationMessageHandler.sendProvisioningMessage(dd);
         this.startWiclet("rim.net/mds/provisioning");
      } finally {
         return;
      }
   }

   final void upgradeWiclet(DeploymentDescriptor upgradeDescriptor, long expiryDate) {
      WicletImpl wiclet = (WicletImpl)this.getWiclet(upgradeDescriptor.getUri());
      if (wiclet != null && !wiclet.getVersion().equals(upgradeDescriptor.getVersion())) {
         if (this.violatesMultiDomainPolicy(upgradeDescriptor)) {
            Logger.log(
               this.toString(),
               ((StringBuffer)(new Object("Ignoring upgrade notification for ")))
                  .append(upgradeDescriptor.getName())
                  .append(", multi domain not allowed.")
                  .toString(),
               4
            );
            return;
         }

         UpgradeTaskInfo taskInfo = new UpgradeTaskInfo(upgradeDescriptor, expiryDate);
         wiclet.setUpgradeTask(taskInfo);
      }
   }

   final void installApplication(int type, long applicationId, String packageLocation, DeploymentDescriptor descriptor) {
      this.installApplication(type, applicationId, packageLocation, descriptor, null);
   }

   final void installApplication(int type, long applicationId, String packageLocation, DeploymentDescriptor descriptor, CollectionSyncModel[] collections) {
      Logger.log(this.createLogMessage("Push Install Received", descriptor.getUri(), descriptor.getVersion(), null));
      WicletImpl app = (WicletImpl)this.getWiclet(applicationId);
      if (app != null) {
         this.sendStatusMessage(applicationId, app.getUri(), app.getVersion(), app.getLanguage(), 0, 900);
      } else if (this.violatesMultiDomainPolicy(descriptor)) {
         Logger.log(this.createLogMessage("Push Install Aborted", descriptor.getUri(), descriptor.getVersion(), "Multi-domain disallowed"));
      } else if (!RuntimeUtilities.isRuntimeCompatible(descriptor)) {
         Logger.log(this.createLogMessage("Push Install Aborted", descriptor.getUri(), descriptor.getVersion(), "Incompatible version"));
      } else {
         UpgradeTaskInfo taskInfo = new UpgradeTaskInfo(descriptor, type, packageLocation, applicationId);
         ClientAdminPolicy policy = this._managementService.getRuntimeInfo().getClientAdminPolicy();
         WicletImpl wiclet = (WicletImpl)this.getWiclet(descriptor.getUri());
         if (type == 3 && policy.isSilentPushProvisioningAllowed()) {
            ProvisioningService provService = (ProvisioningService)this._provider
               .getService(
                  class$net$rim$wica$runtime$provisioning$ProvisioningService == null
                     ? (class$net$rim$wica$runtime$provisioning$ProvisioningService = class$("net.rim.wica.runtime.provisioning.ProvisioningService"))
                     : class$net$rim$wica$runtime$provisioning$ProvisioningService
               );
            if (wiclet != null) {
               if (wiclet.getExecutionState() == 2) {
                  label115:
                  try {
                     wiclet.stop(true);
                  } finally {
                     break label115;
                  }
               }

               wiclet.setExecutionState(8);
            }

            byte[] appPackage = this.getPackage(descriptor.getUri(), descriptor.getVersion());
            int language = Util.arrayFind(descriptor.getLanguages(), Locale.getDefaultForSystem().getLanguage());
            if (language == -1) {
               language = 0;
            }

            boolean systemPrivileges = ReservedWicletConstants.isReservedWiclet(descriptor.getUri());
            if (appPackage == null) {
               provService.installApplication(
                  descriptor,
                  packageLocation,
                  applicationId,
                  language,
                  !systemPrivileges || descriptor.getUri().equals("rim.net/mds/controlcentre"),
                  systemPrivileges,
                  this._managementService.getRuntimeInfo().getDoingRegistration() ? 0 : 1,
                  systemPrivileges,
                  collections
               );
            } else {
               provService.installApplication(
                  descriptor,
                  appPackage,
                  applicationId,
                  language,
                  !systemPrivileges || descriptor.getUri().equals("rim.net/mds/controlcentre"),
                  systemPrivileges,
                  this._managementService.getRuntimeInfo().getDoingRegistration() ? 0 : 1,
                  false,
                  collections
               );
            }
         } else {
            if (type == 4) {
               this.getDialogManager().displayRecommendedInstallDialog(taskInfo);
            }
         }
      }
   }

   final boolean violatesMultiDomainPolicy(DeploymentDescriptor descriptor) {
      if (descriptor.isMultiDomain()) {
         ClientAdminPolicy policy = this._managementService.getRuntimeInfo().getClientAdminPolicy();
         return !policy.isMultiDomainAllowed();
      } else {
         return false;
      }
   }

   final void installRecommended(UpgradeTaskInfo task) {
      DeploymentDescriptor desc = task.getDeploymentDescriptor();
      WicletImpl wiclet = (WicletImpl)this.getWiclet(desc.getUri());
      if (wiclet == null) {
         this.startProvisioning(desc);
      } else {
         wiclet.setUpgradeTask(task);
         if (wiclet.getExecutionState() == 2) {
            label25:
            try {
               wiclet.stop(true);
            } finally {
               break label25;
            }
         }

         wiclet.upgrade();
      }
   }

   final void clearApplicationResourceCache(long id) {
      WicletImpl wiclet = this._manager.getApplication(id);
      if (wiclet == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [ID: "))).append(id).append("] does not exist").toString());
      }

      wiclet.getWicletStore().freeResources();
   }

   public final void runTask(Runnable task) {
      this._worker.addToQueue(task);
   }

   final Vector getVisibleApplications() {
      return this._manager.getVisibleApplications();
   }

   final Vector getUserApplications() {
      return this._manager.getUserApplications();
   }

   public final Wiclet createApplication(WicletStore store) {
      return new WicletImpl(store, this);
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      RuntimeInfo runtimeInfo = this._managementService.getRuntimeInfo();
      switch (event) {
         case 100:
            if (eventParam == 1) {
               this.enableApplications();
               this.performApplicationRestore();
               return;
            }

            if (eventParam == 12) {
               this.disableApplications();
               return;
            }
            break;
         case 107:
            this._applicationMessageHandler.onClientAdminPolicyUpdated();
            if (runtimeInfo.getDoingRegistration()) {
               this.handleSystemApplicationInstall();
               return;
            }
            break;
         case 109:
            this.enableApplications();
            return;
         case 110:
            this.handleSystemApplicationInstall();
            return;
         case 111:
            this.disableApplications();
            return;
         case 112:
            this.enableApplications();
            return;
         case 500:
            ProvisioningEvent installEvent = (ProvisioningEvent)data;
            String uri = installEvent.getProvisioningTaskInfo().getApplicationUri();
            switch (installEvent.getType()) {
               case 1:
                  return;
               case 2:
               default:
                  if (runtimeInfo.getDoingRegistration() && ReservedWicletConstants.isReservedWiclet(uri)) {
                     synchronized (this) {
                        this._systemAppCounter++;
                     }

                     if (this._systemAppCounter == PreloadedApplicationInfo.PACKAGE.length) {
                        this._managementService.defaultWicletsInstalled();
                        this._managementService.sendREStatusMessage(runtimeInfo.getDefaultAGInfo().getAgID());
                        return;
                     }
                  }

                  return;
               case 3:
                  this.cancelUpdate(uri);
                  if (runtimeInfo.getDoingRegistration() && ReservedWicletConstants.isReservedWiclet(uri)) {
                     this._managementService.cancelRegistration();
                     return;
                  }

                  return;
               case 4:
                  if (installEvent.getParam() == 5) {
                     this.cancelUpdate(uri);
                  }

                  return;
            }
         case 1000:
            if (runtimeInfo.isRegistered()) {
               this.performApplicationRestore();
               return;
            }
      }
   }

   @Override
   public final void setServices(ServiceProvider provider) {
      this._provider = provider;
   }

   @Override
   public final Object getService(Class serviceInterface) {
      return this._provider.getService(serviceInterface);
   }

   @Override
   public final void start() {
      Application.getApplication().addGlobalEventListener(this);
      this._eventService = (EventService)this._provider
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      this._eventService.addListener(_events, this);
      this._persistenceService = (PersistenceService)this._provider
         .getService(
            class$net$rim$wica$runtime$persistence$PersistenceService == null
               ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
               : class$net$rim$wica$runtime$persistence$PersistenceService
         );
      this._managementService = (ManagementService)this._provider
         .getService(
            class$net$rim$wica$runtime$management$ManagementService == null
               ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
               : class$net$rim$wica$runtime$management$ManagementService
         );
      this._applicationMessageHandler = new ApplicationMessageHandler(this);
      this._dialogManager = new LifecycleDialogManager(this);
      this._systemMessageHandler = new SystemMessageHandler(this);
      Enumeration e = this._persistenceService.getApplications();

      while (e.hasMoreElements()) {
         new WicletImpl((WicletStore)e.nextElement(), this);
      }

      RuntimeInfo runtimeInfo = this._managementService.getRuntimeInfo();
      if (runtimeInfo.isRegistered()) {
         this.registerEntrypoints(this.getVisibleApplications());
         this.performApplicationRestore();
      }
   }

   @Override
   public final void stop() {
      Application.getApplication().removeGlobalEventListener(this);
      this.disableApplications();
      this._systemMessageHandler.stop();
      this._applicationMessageHandler.stop();
      Vector apps = this._manager.getApplications();
      WicletImpl app = null;

      for (int i = apps.size() - 1; i >= 0; i--) {
         app = (WicletImpl)apps.elementAt(i);
         AlertManager alertManager = app.getAlertManager();
         if (alertManager != null) {
            alertManager.deregister();
         }
      }
   }

   @Override
   public final Wiclet getWiclet(long id) {
      return this._manager.getApplication(id);
   }

   @Override
   public final Wiclet getWiclet(String uri) {
      return this._manager.getApplication(uri);
   }

   @Override
   public final Wiclet[] getWiclets() {
      return this._manager.getApplicationsAsArray(this._manager.getApplications());
   }

   @Override
   public final Wiclet[] getWicletsByAg(long agId) {
      return this._manager.getApplicationsAsArray(this._manager.getApplicationsByServer(agId));
   }

   @Override
   public final boolean hasWiclet(long id) {
      return this.getWiclet(id) != null;
   }

   @Override
   public final boolean hasWiclet(String uri) {
      return this.getWiclet(uri) != null;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7464003439710973532L && data0 != 1) {
         this.localizeApplications(Locale.getDefaultForSystem());
      }
   }

   @Override
   public final Wiclet installApplication(WicletStore store) {
      WicletInfo record = store.getInfo();
      int installType = this.installationType(record);
      long installedWicletId = 0;
      if (this.isUpgrade(installType)) {
         Wiclet alreadyInstalledWiclet = this.getWiclet(record.getUri());
         if (alreadyInstalledWiclet != null) {
            installedWicletId = alreadyInstalledWiclet.getId();
            this.uninstallWiclet(alreadyInstalledWiclet.getId(), 2);
         }
      }

      record.setInstallDate(System.currentTimeMillis());
      record.setState(0);
      WicletImpl application = (WicletImpl)this.createApplication(store);
      application.setState(0);
      if (installType == 206) {
         this._eventService.dispatchEvent(this, 200, application);
      } else if (this.isUpgrade(installType)) {
         int dataCompatible = 256;
         int msgCompatible = 512;
         boolean isDataCompatible = (installType & dataCompatible) == dataCompatible;
         boolean isMsgCompatible = (installType & msgCompatible) == msgCompatible;
         this._eventService.dispatchEvent(this, 201, new WicletUpgradeEvent(isDataCompatible, isMsgCompatible, application, installedWicletId));
      }

      if (application.isRibbonVisible() && !this._managementService.getRuntimeInfo().getDoingRegistration()) {
         HomeScreenUtilities.registerEntry(application);
         this.getApplicationMessageHandler().onApplicationInstalled(application);
      }

      return application;
   }

   @Override
   public final void startWiclet(long applicationId) {
      WicletImpl application = this._manager.getApplication(applicationId);
      if (application == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [ID: "))).append(applicationId).append("] does not exist").toString());
      }

      application.start();
   }

   @Override
   public final void startWiclet(String applicationUri) {
      WicletImpl application = this._manager.getApplication(applicationUri);
      if (application == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [URI: "))).append(applicationUri).append("] does not exist").toString());
      }

      application.start();
   }

   @Override
   public final void stopApplication(long applicationId) {
      WicletImpl application = this._manager.getApplication(applicationId);
      if (application == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [ID: "))).append(applicationId).append("] does not exist").toString());
      }

      application.stop(true);
   }

   @Override
   public final void stopApplication(String applicationUri) {
      WicletImpl application = this._manager.getApplication(applicationUri);
      if (application == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [URI: "))).append(applicationUri).append("] does not exist").toString());
      }

      application.stop(true);
   }

   @Override
   public final void processIncomingMessage(long id, int messageId) {
      WicletImpl wiclet = this._manager.getApplication(id);
      if (wiclet != null) {
         wiclet.processMessageAlert(messageId);
      }
   }

   @Override
   public final void sendStatusMessage(long id, String uri, String version, String locale, int action, int status) {
      this.sendStatusMessageInternal(id, 0, uri, version, locale, action, status, 0);
   }

   @Override
   public final void sendStatusMessage(long id, String uri, String version, String locale, int action, int status, long agId) {
      this.sendStatusMessageInternal(id, 0, uri, version, locale, action, status, agId);
      this.sendStatusMessageInternal(id, 0, uri, version, locale, action, status, 0);
   }

   @Override
   public final void sendStatusMessage(long id, long newId, String uri, String version, String locale, int action, int status) {
      this.sendStatusMessageInternal(id, newId, uri, version, locale, action, status, 0);
   }

   @Override
   public final void sendStatusMessage(long id, long newId, String uri, String version, String locale, int action, int status, long agId) {
      this.sendStatusMessageInternal(id, newId, uri, version, locale, action, status, agId);
      this.sendStatusMessageInternal(id, newId, uri, version, locale, action, status, 0);
   }

   @Override
   public final void loadWiclet(String uri) {
      WicletImpl wiclet = this._manager.getApplication(uri);
      if (wiclet == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [URI: "))).append(uri).append("] does not exist").toString());
      }

      wiclet.load();
   }

   @Override
   public final void uninstallWiclet(long id) {
      WicletImpl wiclet = this._manager.getApplication(id);
      if (wiclet == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [ID: "))).append(id).append("] does not exist").toString());
      }

      this.uninstallWiclet(id, 32);
   }

   @Override
   public final void uninstallWicletsByAg(long agId) {
      Vector apps = this._manager.getApplicationsByServer(agId);
      WicletImpl app = null;

      for (int i = apps.size() - 1; i >= 0; i--) {
         app = (WicletImpl)apps.elementAt(i);
         this.uninstallWiclet(app.getId(), 32);
      }
   }

   @Override
   public final void upgradeWiclet(long id) {
      WicletImpl wiclet = this._manager.getApplication(id);
      if (wiclet == null) {
         throw new LifecycleException(((StringBuffer)(new Object("Application [ID: "))).append(id).append("] does not exist").toString());
      }

      wiclet.upgrade();
   }

   @Override
   public final int installationType(WicletInfo wiclet) {
      return this.installationType(wiclet.getUri(), wiclet.getVersion());
   }

   @Override
   public final int installationType(String uri, String version) {
      int installType = 206;
      if (this.hasWiclet(uri)) {
         Wiclet alreadyInstalledWiclet = this.getWiclet(uri);
         String installedVersion = alreadyInstalledWiclet.getVersion();
         String newVersion = version;
         ApplicationVersion installed = null;
         ApplicationVersion newWicletVersion = null;

         try {
            installed = new ApplicationVersion(installedVersion);
            newWicletVersion = new ApplicationVersion(newVersion);
         } finally {
            ;
         }

         if (installed.isIncompatibleWith(newWicletVersion)) {
            return 205;
         }

         installType = 0;
         if (installed.isDataCompatibleWith(newWicletVersion)) {
            installType |= 256;
         }

         if (installed.isMessageCompatibleWith(newWicletVersion)) {
            installType |= 512;
         }
      }

      return installType;
   }

   private final void sendStatusMessageInternal(long id, long newId, String uri, String version, String locale, int action, int status, long agId) {
      try {
         this._systemMessageHandler.sendStatusMessage(id, newId, uri, version, locale, action, status, agId);
      } finally {
         return;
      }
   }

   public LifecycleServiceImpl() {
      this._manager = new LifecycleManager();
   }

   private final void enableApplications() {
      this.registerEntrypoints(this.getVisibleApplications());
   }

   private final void disableApplications() {
      Vector apps = this._manager.getApplications();
      WicletImpl app = null;

      for (int i = apps.size() - 1; i >= 0; i--) {
         app = (WicletImpl)apps.elementAt(i);
         app.stop(true);
      }

      this.unregisterEntrypoints(this.getVisibleApplications());
   }

   private final synchronized void handleSystemApplicationInstall() {
      this._systemAppCounter = 0;
      if (this._managementService.getRuntimeInfo().getDefaultAGInfo().getSecurityVersion() == 1) {
         int length = PreloadedApplicationInfo.URI.length;
         ProvisioningService provisioning = (ProvisioningService)this._provider
            .getService(
               class$net$rim$wica$runtime$provisioning$ProvisioningService == null
                  ? (class$net$rim$wica$runtime$provisioning$ProvisioningService = class$("net.rim.wica.runtime.provisioning.ProvisioningService"))
                  : class$net$rim$wica$runtime$provisioning$ProvisioningService
            );

         for (int i = 0; i < length; i++) {
            DeploymentDescriptor descriptor = new DeploymentDescriptor();
            descriptor.setUri(PreloadedApplicationInfo.URI[i]);
            descriptor.setLanguages(PreloadedApplicationInfo.SUPPORTED_LANGUAGES);
            int language = Util.arrayFind(descriptor.getLanguages(), Locale.getDefaultForSystem().getLanguage());
            provisioning.installApplication(
               descriptor,
               RuntimeResources.getBinaryResource(PreloadedApplicationInfo.PACKAGE[i]),
               PreloadedApplicationInfo.ID[i],
               language,
               PreloadedApplicationInfo.VISIBILITY[i],
               true,
               0,
               false,
               null
            );
         }
      }
   }

   private final void registerEntrypoints(Vector entries) {
      this.runTask(new LifecycleServiceImpl$1(this, entries));
   }

   private final void unregisterEntrypoints(Vector entries) {
      this.runTask(new LifecycleServiceImpl$2(this, entries));
   }

   private final void cancelUpdate(String uri) {
      WicletImpl wiclet = (WicletImpl)this.getWiclet(uri);
      if (wiclet != null) {
         wiclet.cancelUpdate();
         this.getApplicationMessageHandler().onApplicationUpdated(wiclet);
      }
   }

   private final void performApplicationRestore() {
      Vector restoredApps = this._persistenceService.loadRestoredApplications();
      if (restoredApps != null && restoredApps.size() > 0) {
         Logger.log("L AR");
         RestoreTaskProcessor restoreProcessor = new RestoreTaskProcessor(this._provider, restoredApps);
         this._worker.addToQueue(restoreProcessor);
      }
   }

   private final void localizeApplications(Locale locale) {
      this.runTask(new LifecycleServiceImpl$LocalizationTask(this, locale));
   }

   private final String createLogMessage(String event, String uri, String version, String details) {
      StringBuffer message = (StringBuffer)(new Object(event));
      message.append(":\nURI - ");
      message.append(uri);
      message.append("\nVersion - ");
      message.append(version);
      if (details != null) {
         message.append("\nDetails - ");
         message.append(details);
      }

      return message.toString();
   }

   private final void applicationInstalled(long id, boolean upgrade) {
      WicletImpl application = (WicletImpl)this.getWiclet(id);
      WicletInfo record = application.getInfo();
      if (upgrade) {
         Wiclet alreadyInstalledWiclet = this.getWiclet(record.getUri());
         if (alreadyInstalledWiclet != null) {
            this.uninstallWiclet(alreadyInstalledWiclet.getId(), 2);
         }
      }

      record.setInstallDate(System.currentTimeMillis());
      this._eventService.dispatchEvent(this, 200, application);
   }

   private final byte[] getPackage(String uri, String version) {
      byte[] appPackage = null;
      int index = PreloadedApplicationInfo.getIndex(uri);
      if (index >= 0 && version.equals(PreloadedApplicationInfo.VERSION[index])) {
         appPackage = RuntimeResources.getBinaryResource(PreloadedApplicationInfo.PACKAGE[index]);
      }

      return appPackage;
   }

   private final boolean isUpgrade(int installType) {
      return installType != 206;
   }

   @Override
   public final String toString() {
      return "LifecycleService";
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
