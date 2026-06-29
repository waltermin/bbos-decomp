package net.rim.device.apps.internal.options.items;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.gps.GPS;
import net.rim.device.api.gps.GPSListener;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.location.LocationServicesOptionsProvider;
import net.rim.device.apps.api.options.SaveableMainScreenOptionsListItem;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.gps.GPSFirewall;
import net.rim.device.internal.ui.component.PropertyField;
import net.rim.device.internal.ui.container.VerticalIndentFieldManager;

public final class LocationServicesOptionsItem extends SaveableMainScreenOptionsListItem implements GPSListener, FieldChangeListener {
   private ObjectChoiceField _gpsChoiceField;
   private String[] _choiceMsg;
   private String _locationOffMsg;
   private String _locationOnMsg;
   String _initialMsg;
   private ObjectChoiceField _gpsPrivacySettingField;
   private int _prevPrivacy;
   private MainScreen _mainScreen;
   private VerticalIndentFieldManager _vfm;
   private PropertyField _gpsLatitude;
   private PropertyField _gpsLongitude;
   private PropertyField _lastFixDate;
   private PropertyField _numOfSattelites;
   private PropertyField _accuracyInMeters;
   private ObjectChoiceField _gpsDataSourceField;
   private boolean _refreshRequested;
   private static LocationServicesOptionsItem _thisItem = null;
   private static long MY_KEY = 4265083118038743996L;
   private static PersistentObject _persist;
   private static LocationInfoPersistable _locInfo;
   private static PersistentObject _gpsDataSourceStore;
   private static int INDENT_AMOUNT;

   public final void updateGPSDisplay() {
      if (this._vfm != null) {
         synchronized (Application.getEventLock()) {
            this._vfm.deleteAll();
            if (this.isRefreshRequested()) {
               this._vfm.add(new LabelField(OptionsResources.getString(1908)), INDENT_AMOUNT);
            } else {
               this._gpsLatitude.setValue(_locInfo.getLatitude());
               this._gpsLongitude.setValue(_locInfo.getLongitude());
               this._lastFixDate.setValue(_locInfo.getLastFixTime());
               this._numOfSattelites.setValue(_locInfo.getSattelites());
               this._accuracyInMeters.setValue(_locInfo.getAccuracy());
               this._vfm.add(this._gpsLatitude, INDENT_AMOUNT);
               this._vfm.add(this._gpsLongitude, INDENT_AMOUNT);
               this._vfm.add(this._lastFixDate, INDENT_AMOUNT);
               this._vfm.add(this._numOfSattelites, INDENT_AMOUNT);
               this._vfm.add(this._accuracyInMeters, INDENT_AMOUNT);
            }
         }
      }
   }

   @Override
   public final void gpsModeChangeComplete(boolean success, int mode) {
   }

   @Override
   public final void gpsResponseGetLPS(int result) {
      if (result == 0) {
         this._prevPrivacy = GPS.requestGetLPS();
         if (this._gpsPrivacySettingField != null) {
            synchronized (Application.getEventLock()) {
               this._gpsPrivacySettingField.setSelectedIndex(this._prevPrivacy);
            }
         }

         GPSFirewall.getInstance().setCurrentPrivacy(this._prevPrivacy);
      }
   }

   @Override
   public final void gpsResponseSetLPS(int result) {
      if (result != 0) {
         synchronized (Application.getEventLock()) {
            this._gpsPrivacySettingField.setSelectedIndex(this._prevPrivacy);
         }

         this.showMsg(OptionsResources.getString(1935));
      } else {
         this._prevPrivacy = this._gpsPrivacySettingField.getSelectedIndex();
      }
   }

   @Override
   public final void gpsResponseEnablePIN(int result) {
   }

   @Override
   public final void gpsResponseChangePIN(int result) {
   }

   @Override
   public final void gpsPDEChangeComplete(boolean success, int ip, int port) {
   }

   @Override
   public final void gpsEphemerisDataRequired(int format) {
   }

   @Override
   public final void gpsCredentialChangeComplete(boolean success, int clientId) {
   }

   @Override
   public final void gpsLocationAidingRequest() {
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field instanceof ObjectChoiceField && this._choiceMsg != null) {
         ChoiceField ocf = (ChoiceField)field;
         int index = ocf.getSelectedIndex();
         String choice = (String)ocf.getChoice(index);
         if (choice != null) {
            if (choice.equals(this._choiceMsg[0]) && this._locationOnMsg != null) {
               Dialog.alert(this._locationOnMsg);
            } else {
               if (choice.equals(this._choiceMsg[1]) && this._locationOffMsg != null) {
                  Dialog.alert(this._locationOffMsg);
               }
            }
         }
      }
   }

   @Override
   public final void gpsLocationUpdated(int error, int type, int mode) {
   }

   private final void showMsg(String msg) {
      Application.getApplication().invokeLater(new LocationServicesOptionsItem$StatusRunnable(msg));
   }

   @Override
   protected final Verb getSaveVerb() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   private final byte[] requestPIN(String msg) {
      return "0000".getBytes();
   }

   private final ObjectChoiceField getPrivacyField(int choice) {
      String[] choices = new String[]{OptionsResources.getString(1940), OptionsResources.getString(1927), OptionsResources.getString(1928)};
      choice = choice >= 0 && choice <= 2 ? choice : 0;
      ObjectChoiceField ocf = new ObjectChoiceField(OptionsResources.getString(1929), choices, choice);
      ocf.setChangeListener(this);
      return ocf;
   }

   private final ObjectChoiceField getGPSDatasourceField() {
      String gpsDataSource = null;
      synchronized (_gpsDataSourceStore) {
         gpsDataSource = (String)_gpsDataSourceStore.getContents();
      }

      Vector v = new Vector();
      if (GPS.isSupportedOnCurrentNetwork()) {
         v.addElement(OptionsResources.getString(1982));
      }

      if (BluetoothSerialPort.isSupported()) {
         BluetoothSerialPortInfo[] portInfo = BluetoothSerialPort.getSerialPortInfo();
         if (portInfo != null) {
            for (int port = 0; port < portInfo.length; port++) {
               String deviceName = portInfo[port].getDeviceName();
               if (deviceName != null && !deviceName.startsWith("Smart Card Reader") && !v.contains(deviceName)) {
                  v.addElement(deviceName);
               }
            }
         }
      }

      if (!GPS.isSupportedOnCurrentNetwork()) {
         String none = OptionsResources.getString(1984);
         v.insertElementAt(none, 0);
      }

      int selectedIndex = 0;
      String[] array = new String[v.size()];

      for (int i = 0; i < v.size(); i++) {
         array[i] = (String)v.elementAt(i);
         if (gpsDataSource != null && array[i].equals(gpsDataSource)) {
            selectedIndex = i;
         }
      }

      this._gpsDataSourceField = new ObjectChoiceField(OptionsResources.getString(1983), array, selectedIndex);
      return this._gpsDataSourceField;
   }

   private final boolean isPuckPaired() {
      BluetoothSerialPortInfo[] portInfo = BluetoothSerialPort.getSerialPortInfo();
      return portInfo != null && portInfo.length >= 1;
   }

   private final void initGPSLocationFields() {
      synchronized (Application.getEventLock()) {
         MainScreen scrn = this._mainScreen;
         if (RadioInfo.getNetworkType() == 5) {
            this._gpsPrivacySettingField = this.getPrivacyField(this._prevPrivacy);
            scrn.add(this._gpsPrivacySettingField);
         }

         if (RadioInfo.getNetworkType() != 4) {
            this._vfm = new VerticalIndentFieldManager();
            this._gpsLatitude = new PropertyField(OptionsResources.getString(1905), _locInfo.getLatitude(), 36028797018963968L);
            this._gpsLongitude = new PropertyField(OptionsResources.getString(1904), _locInfo.getLongitude(), 36028797018963968L);
            this._lastFixDate = new PropertyField(OptionsResources.getString(1909), _locInfo.getLastFixTime(), 36028797018963968L);
            this._numOfSattelites = new PropertyField(OptionsResources.getString(1907), _locInfo.getSattelites(), 36028797018963968L);
            this._accuracyInMeters = new PropertyField(OptionsResources.getString(1906), _locInfo.getAccuracy(), 36028797018963968L);
            scrn.add(new LabelField(OptionsResources.getString(1903)));
            scrn.add(new SeparatorField());
            scrn.add(this._vfm);
            this._mainScreen = scrn;
            Object var5 = null;
         }
      }

      this.updateGPSDisplay();
   }

   public static final boolean isAvailable() {
      return GPS.isSupportedOnCurrentNetwork() || BluetoothSerialPort.isSupported();
   }

   private final synchronized void setRefreshRequested(boolean val) {
      this._refreshRequested = val;
   }

   private final synchronized boolean isRefreshRequested() {
      return this._refreshRequested;
   }

   @Override
   protected final void populateMenuVerbs(VerbToMenu verbToMenu, int instance) {
      super.populateMenuVerbs(verbToMenu, instance);
      if ((GPS.isSupportedOnCurrentNetwork() || BluetoothSerialPort.isSupported() && this.isPuckPaired())
         && RadioInfo.getNetworkType() != 4
         && !this.isRefreshRequested()) {
         verbToMenu.addVerb(new LocationServicesOptionsItem$GPSLocationVerb(this, 1902));
      }

      if (RadioInfo.getNetworkType() == 5) {
         verbToMenu.addVerb(new LocationServicesOptionsItem$ResetVerb());
      }
   }

   private LocationServicesOptionsItem() {
      super(OptionsResources.getResourceBundle(), 2035, -1514481539159318190L);
      if ((RadioInfo.getNetworkType() == 5 || RadioInfo.getNetworkType() == 3) && GPS.isSupportedOnCurrentNetwork()) {
         GPS.addListener(Application.getApplication(), this);
         if (RadioInfo.getNetworkType() == 5) {
            this._prevPrivacy = GPS.requestGetLPS();
            GPSFirewall.getInstance().setCurrentPrivacy(this._prevPrivacy);
         }
      }
   }

   public static final LocationServicesOptionsItem createInstance() {
      if (_thisItem == null) {
         _thisItem = new LocationServicesOptionsItem();
      }

      return _thisItem;
   }

   private final void beep(boolean success) {
      if (Alert.isBuzzerSupported()) {
         short[] tuneS = new short[]{500, 100, 256, 6502};
         short[] tuneF = new short[]{600, 100, 500, 0, 2, -12280};
         Alert.startBuzzer(success ? tuneS : tuneF, 50);
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      if (BluetoothSerialPort.isSupported()) {
         ObjectChoiceField gpsDataSource = this.getGPSDatasourceField();
         if (gpsDataSource != null) {
            mainScreen.add(this.getGPSDatasourceField());
         }
      }

      this._initialMsg = null;
      int vendor = Branding.getVendorId();
      switch (vendor) {
         case 104:
            this._initialMsg = OptionsResources.getString(1937);
            break;
         case 109:
            this._initialMsg = OptionsResources.getString(511);
            this._locationOnMsg = OptionsResources.getString(512);
            this._locationOffMsg = OptionsResources.getString(513);
            break;
         case 213:
         case 225:
            this._initialMsg = OptionsResources.getString(1986);
      }

      this._choiceMsg = this.getGPSChoices();
      String label = OptionsResources.getString(505);
      int initialIndex = GPS.getMode() == 2 ? 0 : 1;
      this._gpsChoiceField = new ObjectChoiceField(label, this._choiceMsg, initialIndex);
      this._gpsChoiceField.setChangeListener(this);
      mainScreen.add(this._gpsChoiceField);
      if ((GPS.isSupportedOnCurrentNetwork() || BluetoothSerialPort.isSupported() && this.isPuckPaired()) && RadioInfo.getNetworkType() != 4) {
         this._mainScreen = mainScreen;
         this.initGPSLocationFields();
      }

      Enumeration e = LocationServicesOptionsProvider.getLocationServicesOptionsProviders();

      while (e.hasMoreElements()) {
         LocationServicesOptionsProvider locationProvider = (LocationServicesOptionsProvider)e.nextElement();
         locationProvider.populateMainScreen(mainScreen);
      }
   }

   @Override
   protected final void open() {
      super.open();
      if (this._initialMsg != null) {
         Application.getApplication().invokeLater(new LocationServicesOptionsItem$1(this));
      } else {
         Enumeration e = LocationServicesOptionsProvider.getLocationServicesOptionsProviders();

         while (e.hasMoreElements()) {
            LocationServicesOptionsProvider locationProvider = (LocationServicesOptionsProvider)e.nextElement();
            locationProvider.activationStatus();
         }
      }
   }

   private final String[] getGPSChoices() {
      switch (Branding.getVendorId()) {
         case 105:
         case 126:
         case 156:
         case 177:
         case 226:
            return OptionsResources.getStringArray(1936);
         case 109:
            return OptionsResources.getStringArray(1933);
         default:
            return OptionsResources.getStringArray(1930);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected final boolean save() {
      if (this._gpsChoiceField != null && this._gpsChoiceField.isDirty()) {
         int mode = this._gpsChoiceField.getSelectedIndex() == 1 ? 1 : 2;
         GPS.requestModeChange(mode);
      }

      if (this._gpsPrivacySettingField != null && this._gpsPrivacySettingField.isDirty()) {
         boolean var6 = false /* VF: Semaphore variable */;

         label102:
         try {
            var6 = true;
            if (!SIMCard.isValid()) {
               this.showMsg(OptionsResources.getString(1888));
               var6 = false;
            } else {
               GPS.requestSetLPS(this._gpsPrivacySettingField.getSelectedIndex(), this.requestPIN(""));
               GPSFirewall.getInstance().setCurrentPrivacy(this._gpsPrivacySettingField.getSelectedIndex());
               var6 = false;
            }
         } finally {
            if (var6) {
               this.showMsg(OptionsResources.getString(1888));
               break label102;
            }
         }
      }

      if (RadioInfo.getNetworkType() == 5 || RadioInfo.getNetworkType() == 3) {
         _persist.commit();
      }

      if (BluetoothSerialPort.isSupported() && this._gpsDataSourceField != null) {
         String gpsDataSource = (String)this._gpsDataSourceField.getChoice(this._gpsDataSourceField.getSelectedIndex());
         synchronized (_gpsDataSourceStore) {
            if (gpsDataSource != null && gpsDataSource.equals(OptionsResources.getString(1982))) {
               _gpsDataSourceStore.setContents(GPS.GPS_SOURCE_DEVICE, 51);
            } else {
               _gpsDataSourceStore.setContents(gpsDataSource, 51);
            }

            _gpsDataSourceStore.commit();
         }
      }

      Enumeration e = LocationServicesOptionsProvider.getLocationServicesOptionsProviders();

      while (e.hasMoreElements()) {
         LocationServicesOptionsProvider locationProvider = (LocationServicesOptionsProvider)e.nextElement();
         locationProvider.save();
      }

      return super.save();
   }

   @Override
   public final boolean openDevelopmentBackdoor(int backdoorCode) {
      switch (backdoorCode) {
         case 1095192658:
            return super.openDevelopmentBackdoor(backdoorCode);
         case 1095192659:
         default:
            UiApplication.getUiApplication().pushScreen(new LocationServicesOptionsItem$AgpsConfigScreen(this));
            return true;
      }
   }

   static {
      if (isAvailable()) {
         _persist = PersistentStore.getPersistentObject(MY_KEY);
         _locInfo = (LocationInfoPersistable)_persist.getContents();
         if (_locInfo == null) {
            _locInfo = new LocationInfoPersistable();
            _persist.setContents(_locInfo, 51);
            _persist.commit();
         }
      }

      if (BluetoothSerialPort.isSupported()) {
         _gpsDataSourceStore = PersistentStore.getPersistentObject(GPS.GPS_DATA_SOURCE_KEY);
      }

      INDENT_AMOUNT = Font.getDefault().getHeight() / 2;
   }
}
