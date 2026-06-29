package net.rim.wica.runtime.lifecycle.internal;

import java.util.Hashtable;
import net.rim.device.api.i18n.HashResourceBundle;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.component.Dialog;
import net.rim.wica.runtime.event.EventService;
import net.rim.wica.runtime.lifecycle.Alert;
import net.rim.wica.runtime.lifecycle.LifecycleException;
import net.rim.wica.runtime.lifecycle.UninstallTaskInfo;
import net.rim.wica.runtime.lifecycle.UpgradeTaskInfo;
import net.rim.wica.runtime.lifecycle.Wiclet;
import net.rim.wica.runtime.lifecycle.WicletInfo;
import net.rim.wica.runtime.lifecycle.WicletUninstalledEvent;
import net.rim.wica.runtime.management.ClientAdminPolicy;
import net.rim.wica.runtime.management.ManagementService;
import net.rim.wica.runtime.persistence.PersistenceService;
import net.rim.wica.runtime.persistence.Resource;
import net.rim.wica.runtime.persistence.WicletStore;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;
import net.rim.wica.runtime.resources.RuntimeResources;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.ui.HomeScreenEntry;
import net.rim.wica.runtime.ui.HomeScreenUtilities;
import net.rim.wica.runtime.ui.ImageUtilities;
import net.rim.wica.runtime.util.Util;

final class WicletImpl implements Wiclet, HomeScreenEntry {
   private WicletStore _store;
   private WicletInfo _info;
   private ApplicationDescriptor _applicationDescriptor;
   private Bitmap _icon;
   private Bitmap _hoverIcon;
   private WicaApplication _app;
   private String _uniqueId;
   private LifecycleServiceImpl _lifecycleService;
   private AlertManager _alertManager;
   private int _state;
   private boolean _ribbonAlerts;
   private boolean _pendingAlerts;
   private Object _lock = new Object();
   private Hashtable _properties;
   private static final Integer DEFAULT_HOME_SCREEN_POSITION = (Integer)(new Object(60));
   private static final String BUNDLE_NAME = "net.rim.mds.runtime.ribbon";
   private static final Bitmap DEFAULT_ICON_RESOURCE = RuntimeResources.getBitmapResource("default_icon.png");
   private static final long BUNDLE_ID = 8037818566004125802L;
   static Class class$net$rim$wica$runtime$event$EventService;
   static Class class$net$rim$wica$runtime$persistence$PersistenceService;
   static Class class$net$rim$wica$runtime$management$ManagementService;

   final AlertManager getAlertManager() {
      return this._alertManager;
   }

   final WicletInfo getInfo() {
      return this._info;
   }

   final Bitmap getDefaultIcon() {
      if (this._icon == null) {
         this._icon = this.getIcon(this._info.getIconUri(), DEFAULT_ICON_RESOURCE);
      }

      return this._icon;
   }

   final Locale getDefaultLocale() {
      return Locale.get(this.getLanguage());
   }

   final boolean isLanguageSupported(String language) {
      return Util.arrayContains(this._info.getLanguages(), language);
   }

   final void processMessageAlert(int messageId) {
      Alert alert = this.getAlert(messageId);
      if (alert != null) {
         this._alertManager.handleAlert(alert);
      }
   }

   final boolean hasRibbonAlerts() {
      return this._ribbonAlerts;
   }

   final boolean hasPendingAlerts() {
      return this._pendingAlerts;
   }

   final void setAlerts(boolean alerted, boolean ribbon) {
      this._pendingAlerts = alerted;
      this._ribbonAlerts = ribbon;
      this.updateRibbon();
   }

   final void onActivate() {
      if (this._alertManager != null) {
         this._alertManager.cancelAlerts();
      }
   }

   final WicaApplication getApplication() {
      return this._app;
   }

   final boolean isRibbonVisible() {
      return this._info.isRibbonVisible();
   }

   final void setUninstallTask(UninstallTaskInfo uninstallTask) {
      this._info.setUninstallTask(uninstallTask);
      this.save();
   }

   final boolean hasUpgradeTask() {
      return this._info.hasUpgradeTask();
   }

   final void setUpgradeTask(UpgradeTaskInfo upgradeTask) {
      this._info.setUpgradeTask(upgradeTask);
      this.save();
      this.updateRibbon();
   }

   final Alert getAlert(int messageCode) {
      Alert[] alerts = this._info.getAlerts();
      if (alerts != null) {
         for (int i = alerts.length - 1; i >= 0; i--) {
            if (alerts[i].getMessageCode() == messageCode) {
               return alerts[i];
            }
         }
      }

      return null;
   }

   final ServiceProvider getServiceProvider() {
      return this._lifecycleService;
   }

   final synchronized void setState(int state) {
      this._info.setState(state);
      this.save();
      this.updateRibbon();
      EventService eventService = (EventService)this._lifecycleService
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      switch (state) {
         case 2:
            eventService.dispatchEvent(805, this);
            return;
         case 4:
            eventService.dispatchEvent(801, this);
      }
   }

   final synchronized void setExecutionState(int state) {
      int oldState = this._state;
      this._state = state;
      EventService eventService = (EventService)this._lifecycleService
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      switch (state) {
         case 0:
            if (oldState == 4) {
               eventService.dispatchEvent(803, this);
               if (this._info.isRibbonVisible()) {
                  this._lifecycleService.getApplicationMessageHandler().onApplicationState(this);
               }
            }
            break;
         case 2:
            eventService.dispatchEvent(802, this);
            if (this._info.isRibbonVisible()) {
               this._lifecycleService.getApplicationMessageHandler().onApplicationState(this);
               return;
            }
      }
   }

   final void start() {
      switch (this._state) {
         case 0:
            this.launch();
            return;
         case 2:
            this._app.requestForeground();
      }
   }

   final void stop(boolean forceful) {
      switch (this._state) {
         case 2:
            this.stopInternal(forceful);
      }
   }

   final void quarantine(boolean quarantine) {
      if (!quarantine && this.isQuarantined()) {
         this.setState(0);
      } else {
         if (this._state == 2) {
            label35:
            try {
               this.stopInternal(true);
               if (this.getState() == 2) {
                  return;
               }

               this._lifecycleService.getDialogManager().displayDisabledDialog(this);
            } finally {
               break label35;
            }
         }

         this.setState(4);
      }
   }

   final void uninstall() {
      UninstallTaskInfo task = this._info.getUninstallTask();
      if (task != null) {
         this.uninstallInternal(task);
      }
   }

   final void uninstallInternal(UninstallTaskInfo task) {
      if (this._state != 2) {
         this.setState(2);
         PersistenceService persistenceService = (PersistenceService)this._lifecycleService
            .getService(
               class$net$rim$wica$runtime$persistence$PersistenceService == null
                  ? (class$net$rim$wica$runtime$persistence$PersistenceService = class$("net.rim.wica.runtime.persistence.PersistenceService"))
                  : class$net$rim$wica$runtime$persistence$PersistenceService
            );
         this._lifecycleService.getManager().removeApplication(this.getId());
         if (this._info.isRibbonVisible()) {
            HomeScreenUtilities.unregisterEntry(this);
            this._lifecycleService.getApplicationMessageHandler().onApplicationUninstalled(this);
         }

         if (this._alertManager != null) {
            this._alertManager.deregister();
         }

         persistenceService.deleteApplication(this._store);
         if (task.getType() != 2) {
            if (task.getType() != 32) {
               this._lifecycleService.sendStatusMessage(this.getId(), this.getUri(), this.getVersion(), this.getLanguage(), 2, 0);
            }

            EventService eventService = (EventService)this._lifecycleService
               .getService(
                  class$net$rim$wica$runtime$event$EventService == null
                     ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
                     : class$net$rim$wica$runtime$event$EventService
               );
            eventService.dispatchEvent(this, 202, new WicletUninstalledEvent(this.getId(), task.isGraceful()));
         }
      }
   }

   final void upgrade() {
      switch (this.getState()) {
         case 0:
         case 4:
            this.setExecutionState(8);
            UpgradeTaskInfo task = this._info.getUpgradeTask();
            this._lifecycleService.startProvisioning(task.getDeploymentDescriptor());
            return;
         default:
            throw new Object("The application cannot updated.");
      }
   }

   final void cancelUpdate() {
      this.setExecutionState(0);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   final void load() {
      EventService eventService = (EventService)this._lifecycleService
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      eventService.dispatchEvent(this, 203, new Object(this._info.getId()));

      try {
         this._app = new WicaApplication(this);
         synchronized (this._lock) {
            this._lock.notifyAll();
         }

         this._app.start();
      } catch (Throwable var7) {
         this._app = null;
         this.setState(0);
         throw new LifecycleException(e.getMessage());
      }
   }

   @Override
   public final Bitmap getEntryBitmapFocus() {
      return this.createOverlay(this.getHoverIcon());
   }

   @Override
   public final Integer getEntryDefaultPosition() {
      return DEFAULT_HOME_SCREEN_POSITION;
   }

   @Override
   public final String getEntryDescription() {
      StringBuffer description = (StringBuffer)(new Object());
      description.append(this.getName());
      if (this.isInstalling() || this.isQuarantined() || this.hasUpgrade() || this.hasRibbonAlerts()) {
         description.append(" (");
         if (this.isInstalling()) {
            description.append(RuntimeResources.getString(118));
         } else if (this.hasUpgrade()) {
            UpgradeTaskInfo task = this._info.getUpgradeTask();
            if (task.isExpired()) {
               description.append(RuntimeResources.getString(20));
            } else if (this.isQuarantined()) {
               description.append(RuntimeResources.getString(5));
            } else {
               description.append(RuntimeResources.getString(20));
            }
         } else if (this.isQuarantined()) {
            description.append(RuntimeResources.getString(5));
         } else if (this.hasRibbonAlerts()) {
            description.append(RuntimeResources.getString(21));
         }

         description.append(')');
      }

      return description.toString();
   }

   @Override
   public final String getEntryId() {
      if (this._uniqueId == null) {
         this._uniqueId = HomeScreenUtilities.createEntryIdentifier(
            ((StringBuffer)(new Object())).append(this._info.getUri()).append('.').append(this._info.getVersion()).toString()
         );
      }

      return this._uniqueId;
   }

   @Override
   public final void run() {
      switch (this._state) {
         case 0:
            try {
               if (this.isInstalling()) {
                  Dialog.alert(RuntimeResources.getString(111));
               } else {
                  UpgradeTaskInfo task = this._info.getUpgradeTask();
                  if (task != null) {
                     if (task.isExpired()) {
                        this._lifecycleService.getDialogManager().displayUpgradeRequiredDialog(this, task);
                     } else if (!task.isExpired() && !task.getShownDialog()) {
                        task.setShownDialog(true);
                        this.save();
                        if (task.getExpiryDate() == Long.MAX_VALUE) {
                           this._lifecycleService.getDialogManager().displayUpgradeAvailableDialog(this, task);
                        }

                        this._lifecycleService.getDialogManager().displayUpgradeRequiredDialog(this, task);
                     } else {
                        if (this.isQuarantined()) {
                           this._lifecycleService.getDialogManager().displayDisabledDialog(this);
                        }

                        this.launch();
                     }
                  } else {
                     if (this.isQuarantined()) {
                        this._lifecycleService.getDialogManager().displayDisabledDialog(this);
                     }

                     this.launch();
                     return;
                  }
               }
            } finally {
               return;
            }
         case 2:
            this._app.requestForeground();
            return;
         case 8:
            this._lifecycleService.getDialogManager().displayUpgradingDialog(this);
      }
   }

   @Override
   public final Bitmap getEntryBitmap() {
      return this.createOverlay(this.getDefaultIcon());
   }

   @Override
   public final int getExecutionState() {
      return this._state;
   }

   @Override
   public final int getState() {
      return this._info.getState();
   }

   @Override
   public final boolean getProcessMsgsInBackground() {
      return this._info.getProcessMsgsInBackground();
   }

   @Override
   public final boolean getPersistenceMode() {
      return this._info.getPersistenceMode();
   }

   @Override
   public final String getName() {
      return this._info.getName();
   }

   @Override
   public final String getUri() {
      return this._info.getUri();
   }

   @Override
   public final int getEntryPoint() {
      return this._info.getEntryPoint();
   }

   @Override
   public final String getVersion() {
      return this._info.getVersion();
   }

   @Override
   public final String getVendor() {
      return this._info.getVendor();
   }

   @Override
   public final int getMetadataSize() {
      return this._store.getMetadataFlashSize();
   }

   @Override
   public final int getDataSize() {
      return this._store.getDataFlashSize();
   }

   @Override
   public final int getCacheSize() {
      return this._store.getCacheFlashSize();
   }

   @Override
   public final String getIconUri() {
      return this._info.getIconUri();
   }

   @Override
   public final String getLanguage() {
      String[] languages = this._info.getLanguages();
      int langIndex = this._info.getLanguageIndex();
      return languages != null && langIndex >= 0 ? languages[langIndex] : Locale.get(1701707776).getLanguage();
   }

   @Override
   public final int getMessageDelivery() {
      return this._info.getMessageDelivery();
   }

   @Override
   public final String getDescription() {
      return this._info.getDescription();
   }

   @Override
   public final int getExternalAccessType() {
      if (this.isSystemApplication()) {
         return 2;
      }

      ManagementService managementService = (ManagementService)this._lifecycleService
         .getService(
            class$net$rim$wica$runtime$management$ManagementService == null
               ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
               : class$net$rim$wica$runtime$management$ManagementService
         );
      ClientAdminPolicy cap = managementService.getRuntimeInfo().getClientAdminPolicy();
      return cap.getExternalAccessAllowed();
   }

   @Override
   public final void stopStarted() {
      this.setExecutionState(4);
   }

   @Override
   public final void stopCompleted() {
      this.setExecutionState(0);
      EventService eventService = (EventService)this._lifecycleService
         .getService(
            class$net$rim$wica$runtime$event$EventService == null
               ? (class$net$rim$wica$runtime$event$EventService = class$("net.rim.wica.runtime.event.EventService"))
               : class$net$rim$wica$runtime$event$EventService
         );
      eventService.dispatchEvent(this, 204, new Object(this._info.getId()));
      UninstallTaskInfo task = this._info.getUninstallTask();
      if (task != null) {
         this.uninstallInternal(task);
         if (task.getType() == 1) {
            this._lifecycleService.getDialogManager().displayUninstalledDialog(this);
         }
      }

      synchronized (this._lock) {
         this._lock.notifyAll();
      }

      WicaApplication var6 = this._app;
      this._app = null;
      var6.shutdown();
   }

   @Override
   public final int getInboundQueueSizeLimit() {
      if (this.isSystemApplication()) {
         return Integer.MAX_VALUE;
      }

      ManagementService managementService = (ManagementService)this._lifecycleService
         .getService(
            class$net$rim$wica$runtime$management$ManagementService == null
               ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
               : class$net$rim$wica$runtime$management$ManagementService
         );
      ClientAdminPolicy cap = managementService.getRuntimeInfo().getClientAdminPolicy();
      return cap == null ? 0 : cap.getInboundQueueSizeLimit();
   }

   @Override
   public final int getOutboundQueueSizeLimit() {
      if (this.isSystemApplication()) {
         return Integer.MAX_VALUE;
      }

      ManagementService managementService = (ManagementService)this._lifecycleService
         .getService(
            class$net$rim$wica$runtime$management$ManagementService == null
               ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
               : class$net$rim$wica$runtime$management$ManagementService
         );
      ClientAdminPolicy cap = managementService.getRuntimeInfo().getClientAdminPolicy();
      return cap == null ? 0 : cap.getOutboundQueueSizeLimit();
   }

   @Override
   public final String getTargetFolder() {
      return this._info.getTargetFolder();
   }

   @Override
   public final long getAgId() {
      long serverId = this._info.getDedicatedServerId();
      if (serverId == 0) {
         ManagementService managementService = (ManagementService)this._lifecycleService
            .getService(
               class$net$rim$wica$runtime$management$ManagementService == null
                  ? (class$net$rim$wica$runtime$management$ManagementService = class$("net.rim.wica.runtime.management.ManagementService"))
                  : class$net$rim$wica$runtime$management$ManagementService
            );
         serverId = managementService.getRuntimeInfo().getDefaultAGInfo().getAgID();
      }

      return serverId;
   }

   @Override
   public final boolean isSystemApplication() {
      return this._info.isSystemApplication();
   }

   @Override
   public final long getInstallDate() {
      return this._info.getInstallDate();
   }

   @Override
   public final long getId() {
      return this._info.getId();
   }

   @Override
   public final boolean isRunning() {
      return this._state == 2;
   }

   @Override
   public final boolean isQuarantined() {
      return this.getState() == 4;
   }

   @Override
   public final boolean isUpgrading() {
      return this._state == 8;
   }

   @Override
   public final boolean hasUpgrade() {
      return this.hasUpgradeTask();
   }

   @Override
   public final long getUpgradeExpiryDate() {
      return this._info.getUpgradeTask().getExpiryDate();
   }

   @Override
   public final DeploymentDescriptor getUpgradeDescriptor() {
      return this._info.getUpgradeTask().getDeploymentDescriptor();
   }

   @Override
   public final WicletStore getWicletStore() {
      return this._store;
   }

   @Override
   public final EventService getEventService() {
      return this._app != null ? this._app.getEventService() : null;
   }

   @Override
   public final String setProperty(String name, String value) {
      String oldValue = (String)this._properties.put(name, value);
      EventService eventService = this.getEventService();
      if (eventService != null) {
         eventService.dispatchEvent(207, this);
      }

      return oldValue;
   }

   @Override
   public final String getProperty(String name) {
      return (String)this._properties.get(name);
   }

   private final Bitmap getHoverIcon() {
      if (this._hoverIcon == null) {
         this._hoverIcon = this.getIcon(this._info.getHoverIcon(), this.getDefaultIcon());
      }

      return this._hoverIcon;
   }

   private final Bitmap getIcon(String name, Bitmap defaultReturned) {
      Bitmap icon = null;
      if (name == null) {
         return defaultReturned;
      }

      try {
         Resource resource = this._store.getResource(name);
         if (resource != null) {
            return EncodedImage.createEncodedImage(resource.getData(), 0, -1).getBitmap();
         }

         icon = defaultReturned;
      } finally {
         ;
      }

      return icon;
   }

   private final Bitmap createOverlay(Bitmap icon) {
      if (this.hasUpgrade()) {
         Bitmap overlay = RuntimeResources.getBitmapResource("upgradeOverlay.png");
         icon = ImageUtilities.paintOverlay(icon, icon.getWidth() - overlay.getWidth(), 0, overlay);
      }

      if (this.isQuarantined()) {
         Bitmap overlay = RuntimeResources.getBitmapResource("disabledOverlay.png");
         return ImageUtilities.paintOverlay(icon, icon.getWidth() - overlay.getWidth(), icon.getHeight() - overlay.getHeight(), overlay);
      }

      if (this.hasRibbonAlerts()) {
         Bitmap overlay = RuntimeResources.getBitmapResource("alertOverlay.png");
         icon = ImageUtilities.paintOverlay(icon, icon.getWidth() - overlay.getWidth(), icon.getHeight() - overlay.getHeight(), overlay);
      }

      return icon;
   }

   private final void launch() {
      if (!this._store.getDataStatus() && !Application.isEventDispatchThread()) {
         Application.getApplication().invokeLater(new WicletImpl$1(this));
      } else {
         this.launchImpl();
      }
   }

   private final void launchImpl() {
      if (!this._store.getDataStatus() && Dialog.ask(3, RuntimeResources.getString(119)) == 4) {
         this._store.wipeData();
      }

      this.setExecutionState(1);

      try {
         ApplicationManager.getApplicationManager().runApplication(this._applicationDescriptor);

         try {
            synchronized (this._lock) {
               this._lock.wait();
            }

            this.setExecutionState(2);
         } finally {
            this.setState(0);
            return;
         }
      } finally {
         this.setExecutionState(0);
         throw new LifecycleException("The application could not be started.");
      }
   }

   private final ApplicationDescriptor createApplicationDescriptor() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      HashResourceBundle bundle = (HashResourceBundle)registry.get(8037818566004125802L);
      if (bundle == null) {
         Locale rootLocale = Locale.get(null);
         bundle = (HashResourceBundle)(new Object(rootLocale));
         registry.put(8037818566004125802L, bundle);
         ResourceBundleFamily bundles = ResourceBundle.getBundle(8037818566004125802L, "net.rim.mds.runtime.ribbon");
         bundles.put(rootLocale, bundle);
      }

      bundle.put((int)this._info.getId(), this._info.getName());
      return (ApplicationDescriptor)(new Object(
         ApplicationDescriptor.currentApplicationDescriptor(),
         this.getEntryId(),
         new Object[]{String.valueOf(this._info.getUri())},
         this.getDefaultIcon(),
         DEFAULT_HOME_SCREEN_POSITION,
         "net.rim.mds.runtime.ribbon",
         (int)this._info.getId()
      ));
   }

   private final void save() {
      this._store.save();
   }

   @Override
   public final String toString() {
      return this.getName();
   }

   private final boolean isInstalling() {
      return this.getState() == 1;
   }

   private final void updateRibbon() {
      if (this._info.isRibbonVisible()) {
         HomeScreenUtilities.updateEntry(this);
         this._lifecycleService.getApplicationMessageHandler().onApplicationUpdated(this);
      }
   }

   private final void stopInternal(boolean forceful) {
      this.setExecutionState(4);
      this._app.stop(forceful);
      synchronized (this._lock) {
         try {
            this._lock.wait();
         } finally {
            return;
         }
      }
   }

   WicletImpl(WicletStore store, LifecycleServiceImpl lifecycleService) {
      this._store = store;
      this._info = store.getInfo();
      this._lifecycleService = lifecycleService;
      this._properties = (Hashtable)(new Object());
      if (!this._info.hasUninstallTask()) {
         if (this._info.getAlerts().length > 0) {
            this._alertManager = new AlertManager(this);
         }

         this._applicationDescriptor = this.createApplicationDescriptor();
         this._lifecycleService.getManager().addApplication(this);
      } else {
         this.uninstall();
      }
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
