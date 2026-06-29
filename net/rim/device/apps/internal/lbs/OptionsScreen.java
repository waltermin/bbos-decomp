package net.rim.device.apps.internal.lbs;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ui.BooleanChoiceField;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.lbs.maplet.LayerOptions;
import net.rim.device.apps.internal.lbs.resources.LBSResources;
import net.rim.device.internal.bluetooth.BluetoothME;

final class OptionsScreen extends MainScreen {
   private ObjectChoiceField _gpsDevices;
   private ObjectChoiceField _batteryBacklightLevel;
   private ObjectChoiceField _measurements;
   private ObjectChoiceField _hideTitlebar;
   private LabelField emmptyLine;
   private LabelField _gpsSettingField;
   private BooleanChoiceField _allowWirelessSync;
   private MapsOptionsScreen _mapOptions;
   private LabelField _downloadedDataField;
   private LabelField _downloadedDataSize;
   private MapScreen _mapScreen;
   private CheckboxField[] _visibleLayerGroups;
   public static final ResourceBundleFamily _resources = ResourceBundle.getBundle(6514774203079918781L, "net.rim.device.apps.internal.lbs.LBS");
   public static final int CLEAR_CACHE = 65551;
   public static final int CLEAR_DOWNLOAD = 65552;
   public static final int BLUETOOTH_CONFIG = 65553;
   private static LabelField _currentCacheSize;
   private static GPSDevice _deviceSetBeforeBluetoothOptions;

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   OptionsScreen(MapScreen mapscreen) {
      this.setTitle(_resources, 9);
      this._mapScreen = mapscreen;
      if (GPSProvider.isGPSSupported()) {
         this._gpsSettingField = (LabelField)(new Object(_resources.getString(405), 4294967296L));
         this.add(this._gpsSettingField);
         this._gpsDevices = (ObjectChoiceField)(new Object());
         this._gpsDevices.setLabel(((StringBuffer)(new Object("  "))).append(_resources.getString(328)).toString());
         boolean var7 = false /* VF: Semaphore variable */;

         label49:
         try {
            var7 = true;
            this.setGPSdevices();
            var7 = false;
         } finally {
            if (var7) {
               EventLogger.logEvent(LBSApplication.UID, "in setGPSDevices".getBytes(), 0);
               break label49;
            }
         }

         this.add(this._gpsDevices);
      }

      this._batteryBacklightLevel = (ObjectChoiceField)(new Object());
      this._batteryBacklightLevel.setLabel(((StringBuffer)(new Object("  "))).append(_resources.getString(410)).toString());
      this.setBatteryLevels();
      this.add(this._batteryBacklightLevel);
      this.emmptyLine = (LabelField)(new Object(" ", 4294967296L));
      this.add(this.emmptyLine);
      this._measurements = (ObjectChoiceField)(new Object());
      this._measurements.setLabel(_resources.getString(130));
      this.setMeasurementValues();
      this.add(this._measurements);
      int ix = LBSOptions.getInt(827298922757617815L, 0);
      this._batteryBacklightLevel.setSelectedIndex(ix);
      this._hideTitlebar = (ObjectChoiceField)(new Object());
      this._hideTitlebar.setLabel(_resources.getString(406));
      this.setHideTitlebar();
      this.add(this._hideTitlebar);
      this._allowWirelessSync = (BooleanChoiceField)(new Object(CommonResources.getString(9117), 0, LBSOptions.getBoolean(-843435249973501170L, true)));
      if (SyncManager.getInstance().isOTASyncAvailable(LBSOptions.getInstance(), false)) {
         this.add(this._allowWirelessSync);
      }

      this._mapOptions = new MapsOptionsScreen();
      this._mapOptions.populateMainScreen(this);
      this.emmptyLine = (LabelField)(new Object(" ", 4294967296L));
      this.add(this.emmptyLine);
      this._downloadedDataField = (LabelField)(new Object(_resources.getString(195), 4294967296L));
      this._downloadedDataSize = (LabelField)(new Object(
         ((StringBuffer)(new Object()))
            .append(_resources.getString(407))
            .append(" ")
            .append(LBSOptions.getInt(8640332184073563572L, 0))
            .append(_resources.getString(196))
            .toString(),
         8589934592L
      ));
      this.add(this._downloadedDataField);
      this.add(this._downloadedDataSize);
      _deviceSetBeforeBluetoothOptions = null;
      if (LBSOptions.TOGGLE_LAYERS) {
         this.add((Field)(new Object()));
         this.add((Field)(new Object("Select Layers to display")));
         LayerOptions layerOps = LayerOptions.getInstance();
         String[] list = layerOps.getLayerGroupNames();
         this._visibleLayerGroups = new Object[list.length];

         for (int i = 0; i < list.length; i++) {
            this.add(this._visibleLayerGroups[i] = (CheckboxField)(new Object(list[i].toString(), layerOps.isGroupVisible(i))));
         }
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      menu.add(MenuItem.getPrefab(15));
      menu.add(new OptionsScreen$MyMenuItem(65551));
      menu.add(new OptionsScreen$MyMenuItem(65552));
      if (BluetoothME.isSupported()) {
         menu.add(new OptionsScreen$MyMenuItem(65553));
      }

      super.makeMenu(menu, instance);
   }

   private final void setMeasurementValues() {
      String[] choices = new Object[2];
      choices[0] = _resources.getString(131);
      choices[1] = _resources.getString(132);
      this._measurements.setChoices(choices);
      int ix = LBSOptions.getInt(-6817208986109478597L, 2) - 1;
      this._measurements.setSelectedIndex(ix);
   }

   private final void setBatteryLevels() {
      String[] choices = new Object[4];
      choices[0] = _resources.getString(411);
      choices[1] = _resources.getString(412);
      choices[2] = _resources.getString(413);
      choices[3] = _resources.getString(414);
      this._batteryBacklightLevel.setChoices(choices);
      int ix = LBSOptions.getInt(827298922757617815L, 0);
      this._batteryBacklightLevel.setSelectedIndex(ix);
   }

   private final void setHideTitlebar() {
      String[] choices = new Object[2];
      choices[0] = _resources.getString(398);
      choices[1] = _resources.getString(399);
      this._hideTitlebar.setChoices(choices);
      boolean bHideTitlebar = LBSOptions.getBoolean(5204834541750260038L, true);
      if (bHideTitlebar) {
         this._hideTitlebar.setSelectedIndex(0);
      } else {
         this._hideTitlebar.setSelectedIndex(1);
      }
   }

   private final void setGPSdevices() {
      if (GPSProvider.isGPSSupported()) {
         GPSProvider locProvider = GPSProvider.getInstance();
         GPSDevice gps = locProvider.getDeviceInUse();
         GPSDevice[] devices = null;
         boolean hasInternalGPS = GPS.isSupported() && GPSProvider.isGPSSupportedOnNetwork();
         if (hasInternalGPS) {
            devices = locProvider.getLocationDevices(false);
         } else {
            devices = locProvider.getLocationDevices(true);
         }

         this._gpsDevices.setChoices(devices);
         if (_deviceSetBeforeBluetoothOptions != null) {
            for (int i = hasInternalGPS ? 0 : 1; i < devices.length; i++) {
               gps = devices[i];
               if (gps.equals(_deviceSetBeforeBluetoothOptions)) {
                  locProvider.setDeviceToUse(gps);
                  LBSOptions.setString(6531936621597631078L, gps.getDeviceID().toString());
                  break;
               }

               gps = null;
            }
         }

         if (gps == null) {
            for (int i = 0; i < devices.length; i++) {
               gps = devices[i];
               if (gps.getName().indexOf("GPS") > -1) {
                  locProvider.setDeviceToUse(gps);
                  LBSOptions.setString(6531936621597631078L, gps.getDeviceID().toString());
                  break;
               }

               gps = null;
            }

            if (gps == null && !hasInternalGPS) {
               gps = GPSDevice.NO_DEVICE;
            }

            if (gps != null) {
               this._gpsDevices.setSelectedIndex(gps);
               return;
            }
         } else if (Arrays.contains(devices, gps)) {
            this._gpsDevices.setSelectedIndex(gps);
         }
      }
   }

   final void setDownloadData() {
      int downloaded = LBSOptions.getInt(8640332184073563572L, 0);
      if (this._downloadedDataSize != null) {
         this._downloadedDataSize
            .setText(
               ((StringBuffer)(new Object()))
                  .append(_resources.getString(407))
                  .append(" ")
                  .append((int)(downloaded / 1149239296 * 1092616192) / 1092616192)
                  .append(_resources.getString(196))
                  .toString()
            );
      } else {
         EventLogger.logEvent(LBSApplication.UID, "data size field is null".getBytes(), 0);
      }
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         this.setDownloadData();
         GPSProvider.getInstance().checkLAPI();
         this.setGPSdevices();
      }

      super.onUiEngineAttached(attached);
   }

   @Override
   protected final void onExposed() {
      if (_deviceSetBeforeBluetoothOptions != null) {
         GPSProvider.getInstance().checkLAPI();
         this.setGPSdevices();
         _deviceSetBeforeBluetoothOptions = null;
      }

      super.onExposed();
   }

   @Override
   public final boolean onClose() {
      this._mapScreen._gpsSelectionScreen = null;
      return super.onClose();
   }

   @Override
   public final void actionPerformed(int action, Object parameter) {
      this.setGPSdevices();
   }

   @Override
   public final void save() {
      boolean changed = false;
      if (GPSProvider.isGPSSupported()) {
         GPSProvider locProvider = GPSProvider.getInstance();
         GPSDevice device = (GPSDevice)this._gpsDevices.getChoice(this._gpsDevices.getSelectedIndex());
         GPSDevice currentDevice = locProvider.getDeviceInUse();
         if (currentDevice == null) {
            currentDevice = GPSDevice.NO_DEVICE;
         }

         if (this._gpsDevices.isDirty() && !currentDevice.equals(device)) {
            int deviceState = currentDevice.getDeviceState();
            if (deviceState == 2 || deviceState == 1 || deviceState == 8) {
               String message = MessageFormat.format(LBSResources.getString(67), new Object[]{currentDevice.toString()});
               Dialog.alert(message);
               this._mapScreen.stopTracking();
            }

            LBSOptions.setString(6531936621597631078L, device.getDeviceID().toString());
            locProvider.setDeviceToUse(device);
            changed = true;
         }
      }

      if (this._measurements.isDirty()) {
         int ix = this._measurements.getSelectedIndex();
         LBSOptions.setInt(-6817208986109478597L, ix + 1);
         changed = true;
      }

      if (this._batteryBacklightLevel.isDirty()) {
         int ix = this._batteryBacklightLevel.getSelectedIndex();
         LBSOptions.setInt(827298922757617815L, ix);
         changed = true;
      }

      if (this._hideTitlebar.isDirty()) {
         int ix = this._hideTitlebar.getSelectedIndex();
         if (ix == 0) {
            LBSOptions.setBoolean(5204834541750260038L, true);
         } else {
            LBSOptions.setBoolean(5204834541750260038L, false);
         }

         changed = true;
      }

      if (LBSOptions.TOGGLE_LAYERS) {
         LayerOptions layerOps = LayerOptions.getInstance();

         for (int i = 0; i < this._visibleLayerGroups.length; i++) {
            if (this._visibleLayerGroups[i].isDirty()) {
               layerOps.setVisible(i, this._visibleLayerGroups[i].getChecked());
               changed = true;
            }
         }

         layerOps.commit();
      }

      if (this._allowWirelessSync.isDirty()) {
         boolean useSyncOTA = this._allowWirelessSync.isAffirmative();
         if (useSyncOTA != LBSOptions.getBoolean(-843435249973501170L, true)) {
            LocationDocumentCollection ldc = LocationDocumentCollection.getInstance();
            LBSOptions lbso = LBSOptions.getInstance();
            boolean isDirty = ldc.isDirty() || lbso.isDirty();
            LBSOptions.setBoolean(-843435249973501170L, useSyncOTA);
            if (useSyncOTA) {
               if (isDirty && Dialog.ask(3, CommonResources.getString(9157), -1) == -1) {
                  useSyncOTA = false;
                  LBSOptions.setBoolean(-843435249973501170L, false);
               } else {
                  ldc.markDirty(false);
                  lbso.markDirty(false);
               }
            }

            SyncManager.getInstance().allowOTASync(ldc, useSyncOTA);
            SyncManager.getInstance().allowOTASync(lbso, useSyncOTA);
         }
      }

      changed |= this._mapOptions.save();
      if (changed) {
         this._mapScreen.optionsChanged();
      }
   }

   public static final int getMenuId(int item) {
      switch (item) {
         case 65550:
            return 0;
         case 65551:
         default:
            return 6;
         case 65552:
            return 331;
         case 65553:
            return 101;
      }
   }

   static final GPSDevice access$102(GPSDevice x0) {
      _deviceSetBeforeBluetoothOptions = x0;
      return x0;
   }
}
