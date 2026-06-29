package net.rim.wica.runtime.lifecycle;

import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.provisioning.DeploymentDescriptor;

public final class WicletInfo implements Persistable {
   private DeploymentDescriptor _descriptor;
   private String _packageLocation;
   private String _name;
   private String _uri;
   private String _version;
   private String _vendor;
   private String _description;
   private String _targetFolder;
   private boolean _systemApplication;
   private String _icon;
   private String _hoverIcon;
   private boolean _ribbonVisible;
   private boolean _processMsgsInBackground;
   private int _entryPoint;
   private boolean _persistenceMode;
   private int _messageDelivery;
   private Alert[] _alerts;
   private long _id;
   private long _dedicatedServerId;
   private long _storeId;
   private long _installDate;
   private UninstallTaskInfo _uninstallTask;
   private UpgradeTaskInfo _upgradeTask;
   private String[] _languages;
   private int _langIndex;
   private int _state;
   public static final int INSTALLED_STATE;
   public static final int INSTALLING_STATE;
   public static final int UNINSTALLED_STATE;
   public static final int QUARANTINED_STATE;

   public final int getState() {
      return this._state;
   }

   public final void setState(int state) {
      this._state = state;
   }

   public final DeploymentDescriptor getDescriptor() {
      return this._descriptor;
   }

   public final void setDescriptor(DeploymentDescriptor descriptor) {
      this._descriptor = descriptor;
   }

   public final String getName() {
      String name = this._descriptor.getName();
      if (name == null || name.length() == 0) {
         name = this._name;
      }

      return name;
   }

   public final void setName(String name) {
      this._name = name;
   }

   public final String getUri() {
      String uri = this._descriptor.getUri();
      if (uri == null) {
         uri = this._uri;
      }

      return uri;
   }

   public final void setUri(String uri) {
      this._uri = uri;
   }

   public final String getVersion() {
      String version = this._descriptor.getVersion();
      if (version == null) {
         version = this._version;
      }

      return version;
   }

   public final void setVersion(String version) {
      this._version = version;
   }

   public final String[] getLanguages() {
      return this._descriptor.getLanguages();
   }

   public final int getLanguageIndex() {
      return this._langIndex;
   }

   public final void setLanguageIndex(int languageIndex) {
      this._langIndex = languageIndex;
   }

   public final String getPackageLocation() {
      return this._packageLocation;
   }

   public final void setPackageLocation(String packageLocation) {
      this._packageLocation = packageLocation;
   }

   public final String getVendor() {
      String vendor = this._descriptor.getVendor();
      if (vendor == null) {
         vendor = this._vendor;
      }

      return vendor;
   }

   public final void setVendor(String vendor) {
      this._vendor = vendor;
   }

   public final String getDescription() {
      String description = this._descriptor.getDescription();
      if (description == null) {
         description = this._description;
      }

      return description;
   }

   public final void setDescription(String description) {
      this._description = description;
   }

   public final String getTargetFolder() {
      return this._descriptor.getTargetFolder();
   }

   public final boolean isSystemApplication() {
      return this._systemApplication;
   }

   public final void setSystemApplication(boolean systemApplication) {
      this._systemApplication = systemApplication;
   }

   public final boolean isRibbonVisible() {
      return this._ribbonVisible;
   }

   public final void setRibbonVisible(boolean ribbonVisible) {
      this._ribbonVisible = ribbonVisible;
   }

   public final boolean getProcessMsgsInBackground() {
      return this._processMsgsInBackground;
   }

   public final void setProcessMsgsInBackground(boolean processMsgsInBackground) {
      this._processMsgsInBackground = processMsgsInBackground;
   }

   public final String getIconUri() {
      return this._icon;
   }

   public final void setIconUri(String icon) {
      this._icon = icon;
   }

   public final String getHoverIcon() {
      return this._hoverIcon;
   }

   public final void setHoverIcon(String hoverIcon) {
      this._hoverIcon = hoverIcon;
   }

   public final int getEntryPoint() {
      return this._entryPoint;
   }

   public final void setEntryPoint(int entryPoint) {
      this._entryPoint = entryPoint;
   }

   public final boolean getPersistenceMode() {
      return this._persistenceMode;
   }

   public final void setPersistenceMode(boolean persistenceMode) {
      this._persistenceMode = persistenceMode;
   }

   public final int getMessageDelivery() {
      return this._messageDelivery;
   }

   public final void setMessageDelivery(int messageDelivery) {
      this._messageDelivery = messageDelivery;
   }

   public final Alert[] getAlerts() {
      return this._alerts;
   }

   public final void setAlerts(Alert[] alerts) {
      this._alerts = alerts;
   }

   public final long getId() {
      return this._id;
   }

   public final void setId(long id) {
      this._id = id;
   }

   public final long getDedicatedServerId() {
      return this._dedicatedServerId;
   }

   public final void setDedicatedServerId(long dedicatedServerId) {
      this._dedicatedServerId = dedicatedServerId;
   }

   public final long getStoreId() {
      return this._storeId;
   }

   public final void setStoreId(long storeId) {
      this._storeId = storeId;
   }

   public final long getInstallDate() {
      return this._installDate;
   }

   public final void setInstallDate(long installDate) {
      this._installDate = installDate;
   }

   public final boolean hasUninstallTask() {
      return this._uninstallTask != null;
   }

   public final UninstallTaskInfo getUninstallTask() {
      return this._uninstallTask;
   }

   public final void setUninstallTask(UninstallTaskInfo uninstallTask) {
      this._uninstallTask = uninstallTask;
   }

   public final boolean hasUpgradeTask() {
      return this._upgradeTask != null;
   }

   public final UpgradeTaskInfo getUpgradeTask() {
      return this._upgradeTask;
   }

   public final void setUpgradeTask(UpgradeTaskInfo upgradeTask) {
      this._upgradeTask = upgradeTask;
   }
}
