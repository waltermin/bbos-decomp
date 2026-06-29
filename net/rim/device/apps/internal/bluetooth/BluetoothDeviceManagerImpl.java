package net.rim.device.apps.internal.bluetooth;

import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.DeviceClass;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CloneableVector;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoryList;
import net.rim.device.apps.internal.commonmodels.categories.CategoryModel;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.Hotlist;
import net.rim.device.apps.internal.phone.data.HotlistItem;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothEvents;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.bluetooth.BluetoothMEListener2;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.LEDEngine;
import net.rim.device.internal.system.USBPasswordRedirectManager;
import net.rim.device.internal.ui.Image;
import net.rim.vm.Message;

public final class BluetoothDeviceManagerImpl extends BluetoothDeviceManager implements BluetoothMEListener2, SystemListener2, GlobalEventListener {
   private PersistentObject _persistentObject;
   private BluetoothOptionsData _bluetoothOptionsData;
   private RetryConnection _retryConnection;
   private Vector _devicesToLookup;
   private CloneableVector _pairedDevices;
   private SimpleSortingVector _inRangeDevices;
   private boolean _inquiryInProgress;
   private boolean _isPowerOn;
   private BluetoothDevice _deviceToPair;
   private boolean _isConnected;
   private IndicatorManager _indicatorManager;
   private Image _bluetoothDialogImage;
   private BluetoothProfileManager[] _profileManagers;
   private BluetoothDeviceManagerImpl$BluetoothSyncItem _syncItem;
   private BluetoothPowerAction _bluetoothPowerAction;
   private boolean _powerIconOnRibbon;
   private BluetoothSetupAction _bluetoothSetupAction;
   private boolean _setupIconOnRibbon;
   private long _lastRibbonRequest;
   private boolean _overrideConnectOnPowerUp;
   private PBAPServer _pbapServer;
   private boolean _tckTestMode;
   private Object _radioOnLock = new Object();
   private static final long BLUETOOTH_OPTIONS_DATA_KEY;
   static final int EVENT_INQUIRY_COMPLETE;
   static final int EVENT_INQUIRY_CANCELLED;
   static final int EVENT_DEVICE_ADDED;
   static final int EVENT_DEVICE_REMOVED;
   static final int EVENT_DEVICE_LIST_UPDATED;
   static final int EVENT_DEVICE_INFO_UPDATED;
   static final int EVENT_STATE_CHANGE;
   static final int EVENT_INQUIRY_RESULT;
   static final int EVENT_SERVICES_DISCOVERED;
   static final int EVENT_SERVICE_SEARCH_COMPLETED;
   static final int EVENT_PAIRING_IN_PROGRESS;
   public static final int MAX_DEVICE_NAME_LENGTH;
   public static final int CLASS_OF_DEVICE;
   public static final int ADDRESS_BOOK_TRANSFER_MODE_DISABLED;
   public static final int ADDRESS_BOOK_TRANSFER_MODE_ALL_ENTRIES;
   public static final int ADDRESS_BOOK_TRANSFER_MODE_HOTLIST;
   public static final int ADDRESS_BOOK_TRANSFER_MODE_SELECTED_CATEGORIES;
   public static final int L2CAP_MTU;
   public static final int CONNECTED_DEVICES_MAX;
   public static final int SD_ATTR_RETRIEVABLE_MAX;
   static final int SECURITY_MODE_3;
   static final int SECURITY_MODE_3_WITH_ENCRYPTION;
   private static final byte[] TCK_PIN = new byte[]{48, 48, 48, 48};

   final Image getDialogImage() {
      return this._bluetoothDialogImage;
   }

   final boolean isConnected() {
      return this._isConnected;
   }

   public final BluetoothDevice getPairedDevice(byte[] address) {
      synchronized (this._pairedDevices) {
         for (int i = this._pairedDevices.size() - 1; i >= 0; i--) {
            BluetoothDevice device = (BluetoothDevice)this._pairedDevices.elementAt(i);
            if (Arrays.equals(address, device.getAddress())) {
               return device;
            }
         }

         return null;
      }
   }

   public final BluetoothDevice getInRangeDevice(byte[] address) {
      synchronized (this._inRangeDevices) {
         for (int i = this._inRangeDevices.size() - 1; i >= 0; i--) {
            BluetoothDevice device = (BluetoothDevice)this._inRangeDevices.elementAt(i);
            if (Arrays.equals(address, device.getAddress())) {
               return device;
            }
         }

         return null;
      }
   }

   public final BluetoothDevice getDevice(byte[] address, int deviceClass) {
      synchronized (this._inRangeDevices) {
         BluetoothDevice device = this.getInRangeDevice(address);
         if (device == null) {
            device = this.getPairedDevice(address);
            if (device == null) {
               device = new BluetoothDevice(this, address);
            }

            this._inRangeDevices.addElement(device);
         }

         if (deviceClass != 0) {
            device.setDeviceClass(deviceClass);
         }

         return device;
      }
   }

   final synchronized boolean fetchDeviceName(BluetoothDevice device) {
      if (this._devicesToLookup.contains(device)) {
         return true;
      }

      this._devicesToLookup.addElement(device);
      return this._devicesToLookup.size() == 1 ? this.fetchNextDeviceName() : true;
   }

   final void commitDeviceData() {
      this._bluetoothOptionsData._pairedDeviceData = (Vector)(new Object());
      synchronized (this._pairedDevices) {
         for (int i = this._pairedDevices.size() - 1; i >= 0; i--) {
            BluetoothDevice device = (BluetoothDevice)this._pairedDevices.elementAt(i);
            this._bluetoothOptionsData._pairedDeviceData.addElement(device.getData());
            this.addRibbonPowerIcon();
         }
      }

      this._persistentObject.commit();
      this._syncItem.fireSyncItemUpdated();
      this.postEvent(5);
   }

   public final boolean isDiscoverable() {
      return ITPolicy.getBoolean(34, 6, false) ? false : this._bluetoothOptionsData._discoverable;
   }

   final int getAllowOutgoingCalls() {
      return Math.max(ITPolicy.getInteger(34, 7, 0), this._bluetoothOptionsData._allowOutgoingCalls);
   }

   final void setAllowOutgoingCalls(int allowOutgoingCalls) {
      if (this._bluetoothOptionsData._allowOutgoingCalls != allowOutgoingCalls) {
         this._bluetoothOptionsData._allowOutgoingCalls = allowOutgoingCalls;
         this._persistentObject.commit();
      }
   }

   final boolean allowOutgoingCalls() {
      switch (this.getAllowOutgoingCalls()) {
         case -1:
            return false;
         case 0:
         default:
            return true;
         case 1:
            return !ApplicationManager.getApplicationManager().isSystemLocked();
      }
   }

   final int getSecurityMode() {
      return ITPolicy.getBoolean(34, 13, false) ? 1 : this._bluetoothOptionsData._securityMode;
   }

   final void setSecurityMode(int securityMode) {
      if (this._bluetoothOptionsData._securityMode != securityMode) {
         this._bluetoothOptionsData._securityMode = securityMode;
         this._persistentObject.commit();
         if (BluetoothME.isPowerOn()) {
            this.updateSecurityMode();
         }
      }
   }

   final int getAddressBookTransferMode() {
      return ITPolicy.getBoolean(34, 8, false) ? 0 : this._bluetoothOptionsData._addressBookTransferMode;
   }

   final void setAddressBookTransferMode(int addressBookTransferMode) {
      if (this._bluetoothOptionsData._addressBookTransferMode != addressBookTransferMode) {
         this._bluetoothOptionsData._addressBookTransferMode = addressBookTransferMode;
         this._persistentObject.commit();
      }
   }

   final CategoriesModel getAddressBookCategories() {
      return this._bluetoothOptionsData._addressBookCategories;
   }

   final AddressCardModel[] getAddressCards(boolean includePhoneNumbers) {
      long startTime = System.currentTimeMillis();
      int mode = this.getAddressBookTransferMode();
      if (mode == 0) {
         return null;
      }

      AddressBook ab = AddressBookServices.getAddressBook(true);
      if (ab == null) {
         return null;
      }

      int numCategories = 0;
      String[] categoryKeys = null;
      if (mode == 3) {
         int[] categoryIDs = new int[0];
         this._bluetoothOptionsData._addressBookCategories.getCategoryIds(categoryIDs);
         numCategories = categoryIDs.length;
         CategoryList categoryList = CategoryList.getInstance();
         if (numCategories != 0) {
            categoryKeys = new Object[0];

            for (int i = 0; i < numCategories; i++) {
               CategoryModel model = categoryList.getCategory(categoryIDs[i]);
               if (model != null) {
                  Arrays.add(categoryKeys, model.getKey());
               }
            }

            numCategories = categoryKeys.length;
         }
      }

      AddressCardModel[] list = new Object[0];
      if (mode == 2) {
         Hotlist hotlist = Hotlist.getInstance();
         int count = hotlist.getCount();

         for (int i = 0; i < count; i++) {
            try {
               HotlistItem item = (HotlistItem)hotlist.get(i);
               if (item != null) {
                  Object o = item.getAddress();
                  if (o instanceof Object) {
                     CallerIDInfo info = (CallerIDInfo)o;
                     Object address = info.getAddress();
                     if (address instanceof Object) {
                        AddressCardModel card = (AddressCardModel)address;
                        int numPhoneNumbers = card.getNumPhoneNumberModels();
                        if (numPhoneNumbers != 0) {
                           this.addCard(list, card, numPhoneNumbers, includePhoneNumbers);
                        }
                     }
                  }
               }
            } finally {
               continue;
            }
         }
      } else if (numCategories == 0) {
         Enumeration e = ab.getAddressCards();

         while (e.hasMoreElements()) {
            try {
               AddressCardModel card = (AddressCardModel)e.nextElement();
               int numPhoneNumbers = card.getNumPhoneNumberModels();
               if (numPhoneNumbers != 0) {
                  this.addCard(list, card, numPhoneNumbers, includePhoneNumbers);
               }
            } finally {
               continue;
            }
         }
      } else {
         AddressBookOptions abo = ab.getAddressBookOptions();
         KeywordFilterList kfl = ab.getView(abo.getSortOrder());

         for (int i = 0; i < numCategories; i++) {
            kfl.setSuffix(categoryKeys[i]);
            int numCards = kfl.size();

            for (int j = 0; j < numCards; j++) {
               try {
                  AddressCardModel card = (AddressCardModel)kfl.getAt(j);
                  int numPhoneNumbers = card.getNumPhoneNumberModels();
                  if (numPhoneNumbers != 0) {
                     for (int k = list.length - 1; k >= 0; k--) {
                        if (list[k] == card) {
                           card = null;
                           break;
                        }
                     }

                     if (card != null) {
                        this.addCard(list, card, numPhoneNumbers, includePhoneNumbers);
                     }
                  }
               } finally {
                  continue;
               }
            }
         }
      }

      BluetoothEvents.log(((StringBuffer)(new Object("AB: "))).append(list.length).append(' ').append(System.currentTimeMillis() - startTime).toString());
      return list;
   }

   final boolean isLEDIndicatorEnabled() {
      return ITPolicy.getBoolean(34, 15, false) ? true : this._bluetoothOptionsData._ledIndicatorEnabled;
   }

   final void enableLEDIndicator(boolean enable) {
      if (this._bluetoothOptionsData._ledIndicatorEnabled != enable) {
         this._bluetoothOptionsData._ledIndicatorEnabled = enable;
         this._persistentObject.commit();
         this.updateLEDIndicator();
      }
   }

   final boolean isConnectOnPowerUpEnabled() {
      return this._bluetoothOptionsData._connectOnPowerUpEnabled;
   }

   final void enableConnectOnPowerUp(boolean enable) {
      if (this._bluetoothOptionsData._connectOnPowerUpEnabled != enable) {
         this._bluetoothOptionsData._connectOnPowerUpEnabled = enable;
         this._persistentObject.commit();
      }
   }

   final boolean setPowerOn(boolean on, boolean overrideConnectOnPowerUp) {
      if (on) {
         this._overrideConnectOnPowerUp = overrideConnectOnPowerUp;
         if (!this.requestPowerOn()) {
            Proxy.getInstance().invokeLater(new BluetoothDeviceManagerImpl$ShowDisabledByITPolicyMessage(this));
            this._overrideConnectOnPowerUp = false;
            return false;
         }
      } else {
         this.requestPowerOff();
      }

      this._bluetoothOptionsData._powerOn = on;
      this._persistentObject.commit();
      return true;
   }

   final void setDiscoverable(boolean on) {
      if (this._bluetoothOptionsData._discoverable != on) {
         this._bluetoothOptionsData._discoverable = on;
         this._persistentObject.commit();
         if (BluetoothME.isPowerOn()) {
            this.updateDiscoverableMode();
         }
      }
   }

   final void updateDiscoverableMode() {
      int rc = BluetoothME.setDiscoverable(this._bluetoothOptionsData._discoverable);
      if (rc != 0 && rc != 2) {
         BluetoothEvents.log(1396984134);
      }
   }

   public final String getLocalName() {
      return this._bluetoothOptionsData._localName;
   }

   final void setLocalName(String name) {
      String currentLocalName = this.getLocalName();
      if (!currentLocalName.equals(name)) {
         this._bluetoothOptionsData._localName = name;
         this._persistentObject.commit();
         BluetoothME.setLocalDeviceName(this._bluetoothOptionsData.getLocalNameAsBytes());
      }
   }

   public final Vector getPairedDevices() {
      return (Vector)this._pairedDevices.clone();
   }

   public final Vector getInRangeDevices(boolean ignorePairedDevices) {
      Vector v = (Vector)(new Object());
      synchronized (this._inRangeDevices) {
         for (int i = 0; i < this._inRangeDevices.size(); i++) {
            BluetoothDevice device = (BluetoothDevice)this._inRangeDevices.elementAt(i);
            if (!ignorePairedDevices || !device.isPaired()) {
               v.addElement(device);
            }
         }

         return v;
      }
   }

   public final void addListener(BluetoothDeviceManagerListener listener) {
      Application.getApplication().addListener(29, listener);
   }

   public final void removeListener(BluetoothDeviceManagerListener listener) {
      Application.getApplication().removeListener(29, listener);
   }

   public final Object[] getListeners() {
      return Application.getApplication().getListeners(29);
   }

   final void postEvent(int event) {
      this.postEvent(event, 0, 0, null, null);
   }

   final void postEvent(int event, Object object0, Object object1) {
      this.postEvent(event, 0, 0, object0, object1);
   }

   final void postEvent(int event, int data0, int data1) {
      this.postEvent(event, data0, data1, null, null);
   }

   final void postEvent(int event, int data0, int data1, Object object0, Object object1) {
      ApplicationManagerInternal am = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      Message msg = (Message)(new Object(29, event, 0, data0, data1, object0, object1));
      am.postMessage(msg);
   }

   public final synchronized boolean startInquiry(int iac) {
      if (this._inquiryInProgress) {
         return false;
      }

      this._inRangeDevices.removeAllElements();
      this.cancelNameLookups();
      int rc = BluetoothME.startInquiry(iac);
      if (rc != 0 && rc != 2) {
         return false;
      }

      this._inquiryInProgress = true;
      return true;
   }

   public final void cancelInquiry() {
      BluetoothME.stopInquiry();
   }

   final synchronized void cancelNameLookups() {
      if (this._devicesToLookup.size() != 0) {
         BluetoothDevice device = (BluetoothDevice)this._devicesToLookup.elementAt(0);
         BluetoothME.cancelRetrieveDeviceName(device.getAddress());
         this._devicesToLookup.removeAllElements();
      }
   }

   final boolean pairDevice(byte[] address) {
      BluetoothDevice device = this.getDevice(address, 0);
      return device.pairDevice();
   }

   final boolean pairDevice(BluetoothDevice device) {
      synchronized (this) {
         if (this._devicesToLookup.size() != 0) {
            if (this._deviceToPair != null) {
               return false;
            }

            this._deviceToPair = device;
            return true;
         }
      }

      return device.pairDevice();
   }

   final boolean deletePairedDevice(BluetoothDevice device) {
      if (device.isConnected()) {
         return false;
      }

      device.deleteLinkKey();
      this._pairedDevices.removeElement(device);
      this._inRangeDevices.removeElement(device);
      this.commitDeviceData();
      if (this._pairedDevices.size() == 0) {
         this.removeRibbonPowerIcon();
      }

      this.postEvent(3);
      return true;
   }

   final void devicePropertiesUpdated(BluetoothDevice device) {
      this.commitDeviceData();

      for (int i = this._profileManagers.length - 1; i >= 0; i--) {
         this._profileManagers[i].devicePropertiesUpdated(device);
      }
   }

   final BluetoothProfileManager getProfileManager(int id) {
      for (int i = this._profileManagers.length - 1; i >= 0; i--) {
         if (this._profileManagers[i].getID() == id) {
            return this._profileManagers[i];
         }
      }

      return null;
   }

   final BluetoothProfileManager[] getProfileManagers() {
      return this._profileManagers;
   }

   final void updateSecurityMode() {
      boolean enableMode3 = true;
      boolean encrypt = false;
      int securityMode = this.getSecurityMode();
      switch (securityMode) {
         case -1:
            break;
         case 0:
         default:
            BluetoothEvents.log(542330163);
            break;
         case 1:
            BluetoothEvents.log(1397568325);
            encrypt = true;
      }

      int rc = BluetoothME.enableSecurityMode3(enableMode3, encrypt);
      if (rc != 0 && rc != 2) {
         BluetoothEvents.log(1397572422);
      }
   }

   final synchronized void fetchDeviceNames() {
      synchronized (this._inRangeDevices) {
         for (int i = 0; i < this._inRangeDevices.size(); i++) {
            BluetoothDevice device = (BluetoothDevice)this._inRangeDevices.elementAt(i);
            if (device.getName() == null) {
               this._devicesToLookup.addElement(device);
            }
         }
      }

      this.fetchNextDeviceName();
   }

   final boolean disconnectPBAPClient(byte[] address) {
      return this._pbapServer.disconnect(address);
   }

   public final boolean isPowerOn() {
      return this._isPowerOn;
   }

   public final boolean tryConnection() {
      BluetoothDevice device = this.getPairedDevice(this._bluetoothOptionsData._lastConnectedDevice);
      if (device != null && device.canConnect()) {
         device.connect(true);
         return true;
      } else {
         return false;
      }
   }

   public final boolean isAutoReconnect() {
      return this._retryConnection != null && this._retryConnection.isActive();
   }

   @Override
   public final void powerOffComplete() {
      this.handlePowerOff();
      BluetoothEvents.log(542066246);
      this.updateRibbonPowerIcon();
   }

   @Override
   public final synchronized void inquiryComplete() {
      this._inquiryInProgress = false;
      synchronized (this._inRangeDevices) {
         this._inRangeDevices.reSort();
      }

      this.postEvent(0);
   }

   @Override
   public final void inquiryCancelled() {
      this._inquiryInProgress = false;
      this.postEvent(1);
   }

   @Override
   public final void inquiryResult(byte[] address, int rssi, int deviceClass, int pageScanInfo) {
      BluetoothDevice device = this.getInRangeDevice(address);
      if (device == null) {
         device = this.getDevice(address, deviceClass);
         this.postEvent(7, device.getRemoteDevice(), new DeviceClass(device.getDeviceClass()));
      }

      device.setDeviceClass(deviceClass);
      device.setPageScanInfo(pageScanInfo);
      device.setRSSI(rssi);
   }

   @Override
   public final void deviceConnected(byte[] address, int deviceClass, int result) {
      BluetoothDevice device = this.getDevice(address, deviceClass);
      BluetoothEvents.logResult(1145241600, result);
      if (result == 0) {
         this._retryConnection.cancel();
         this._isConnected = true;
         this.stateChanged();
         device.connectSucceeded();
         this._bluetoothOptionsData._lastConnectedDevice = address;
         this._persistentObject.commit();
      } else {
         device.connectFailed();
      }
   }

   @Override
   public final void deviceDisconnected(byte[] address, int reason) {
      BluetoothEvents.logResult(1145307136, reason);
      BluetoothDevice device = this.getPairedDevice(address);
      if (device != null) {
         for (int i = this._profileManagers.length - 1; i >= 0; i--) {
            if (this._profileManagers[i].isConnected(device)) {
               this._profileManagers[i].cleanup();
            }
         }

         device.disconnected();
      }

      if (!BluetoothME.isAnyDeviceConnected()) {
         this._isConnected = false;
         this.stateChanged();
         if (ITPolicy.getBoolean(34, 1, false)) {
            this.requestPowerOff();
         }
      }
   }

   @Override
   public final void deviceNameRetrieved(byte[] address, byte[] name) {
      BluetoothDevice device = null;
      boolean commitRequired = false;
      synchronized (this) {
         device = this.getInRangeDevice(address);
         if (device == null) {
            return;
         }

         this._devicesToLookup.removeElement(device);
      }

      if (name == null) {
         BluetoothEvents.log(541478470);
         device.setName(null);
      } else {
         label64:
         try {
            String str = (String)(new Object(name, "UTF8"));
            device.setName(str);
            commitRequired = true;
            this.postEvent(4);
         } finally {
            break label64;
         }
      }

      synchronized (this) {
         if (commitRequired) {
            this.commitDeviceData();
         }

         if (this._deviceToPair != null) {
            this._deviceToPair.pairDevice();
            this._deviceToPair = null;
         } else {
            this.fetchNextDeviceName();
         }
      }
   }

   @Override
   public final void pairingComplete(byte[] address, int result) {
      BluetoothDevice device = this.getDevice(address, 0);
      if (result == 0) {
         if (this.getPairedDevice(address) == null) {
            this._pairedDevices.addElement(device);
         }

         byte[] linkKey = BluetoothME.getLinkKey(address);
         int linkKeyType = BluetoothME.getLinkKeyType(address);
         device.pairingSucceeded(linkKey, linkKeyType);
         this.commitDeviceData();
         this.postEvent(2);
      } else {
         BluetoothEvents.logResult(1145241600, result);
         device.connectFailed();
      }
   }

   @Override
   public final void pinCodeRequired(byte[] address, int deviceClass) {
      if (this._tckTestMode) {
         BluetoothME.sendPIN(address, TCK_PIN);
      } else {
         BluetoothDevice device = this.getDevice(address, deviceClass);
         if (ITPolicy.getBoolean(34, 2, false)) {
            BluetoothME.sendPIN(address, null);
         } else {
            this.postEvent(10);
            device.getPIN();
         }
      }
   }

   @Override
   public final void authorizationRequired(byte[] address, int securityRecordID) {
      if (this._tckTestMode) {
         BluetoothME.authorizeDevice(address, true);
      } else {
         BluetoothDevice device = this.getPairedDevice(address);
         if (device == null) {
            BluetoothME.authorizeDevice(address, false);
         } else {
            device.authorizationRequired(securityRecordID);
         }
      }
   }

   @Override
   public final void serviceDiscoveryComplete(byte[] address, int result, byte[] data) {
      BluetoothDevice device = this.getPairedDevice(address);
      if (device != null) {
         boolean success = true;
         if (result != 0) {
            BluetoothEvents.log(1397900358);
            success = false;
         }

         device.serviceDiscoveryComplete(success, data);
      }
   }

   @Override
   public final void linkModeChanged(byte[] address, int result, int mode) {
      BluetoothDevice device = this.getPairedDevice(address);
      if (device != null) {
         device.linkModeChanged(result, mode);
      }
   }

   @Override
   public final void connectionAccepted(byte[] address) {
   }

   @Override
   public final void linkKeyChangeComplete(byte[] address, int result) {
   }

   @Override
   public final void authenticationComplete(byte[] address, int result) {
   }

   @Override
   public final void encryptionComplete(byte[] address, int result) {
   }

   @Override
   public final void hciFatalError(byte[] address, int result) {
   }

   @Override
   public final void powerOff() {
      this._isPowerOn = false;
      if (BluetoothME.isPowerOn()) {
         this.requestPowerOff();
      }
   }

   @Override
   public final void powerUp() {
      this._isPowerOn = true;
      if (this._bluetoothOptionsData._powerOn) {
         this.requestPowerOn();
      }
   }

   @Override
   public final void powerOnComplete(boolean success) {
      if (!success) {
         BluetoothEvents.log(542068294);
         Status.show(BluetoothMainScreen.getString(51), this.getDialogImage(), 3000, 0, true, false, 0);
      } else {
         BluetoothEvents.log(538988366);
         this.updateSecurityMode();
         this.stateChanged();
         this.updateRibbonPowerIcon();
         synchronized (this._radioOnLock) {
            this._radioOnLock.notifyAll();
         }

         if (this._bluetoothOptionsData._connectOnPowerUpEnabled && this._bluetoothOptionsData._lastConnectedDevice != null && !this._overrideConnectOnPowerUp) {
            this._retryConnection.submit(1000, 2);
         }

         this._overrideConnectOnPowerUp = false;
      }
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryStatusChange(int status) {
      for (int i = this._profileManagers.length - 1; i >= 0; i--) {
         this._profileManagers[i].updateBatteryIndicators(status);
      }
   }

   @Override
   public final void powerOffRequested(int reason) {
   }

   @Override
   public final void cradleMismatch(boolean mismatch) {
   }

   @Override
   public final void fastReset() {
      this.handlePowerOff();
      this.init();
   }

   @Override
   public final void backlightStateChange(boolean on) {
   }

   @Override
   public final void usbConnectionStateChange(int state) {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8508406279413621091L) {
         if (BluetoothME.isPowerOn()) {
            if (ITPolicy.getBoolean(34, 1, false)) {
               if (!this._isConnected) {
                  this.requestPowerOff();
               }

               this.removeRibbonPowerIcon();
            } else {
               this.addRibbonPowerIcon();
            }

            if (ITPolicy.getBoolean(34, 2, false)) {
               this.removeRibbonSetupIcon();
            } else {
               this.addRibbonSetupIcon();
            }

            if (ITPolicy.getBoolean(34, 6, false)) {
               this.setDiscoverable(false);
            }

            this.updateSecurityMode();
            this.updateLEDIndicator();
            return;
         }
      } else if (guid == 2573494863350550132L) {
         this._bluetoothSetupAction.updateSetupAction();
      }
   }

   public static final void libMain(String[] args) {
      if (args.length != 0) {
         if (args[0].equals("btonoff")) {
            ((BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance()).ribbonPowerOnOff();
         } else {
            if (args[0].equals("btsetup")) {
               Application app = new BluetoothSetupApplication();
               app.enterEventDispatcher();
            }
         }
      } else {
         BluetoothEvents.init();
         if (InternalServices.isDeviceCapable(8)) {
            ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
            BluetoothDeviceManagerImpl btManager = new BluetoothDeviceManagerImpl();
            ar.put(-4148425341967320934L, btManager);
            BluetoothMainScreen.init();
            btManager._pbapServer = new PBAPServer();
            ((Thread)(new Object(btManager._pbapServer))).start();

            while (true) {
               try {
                  btManager.enterEventDispatcher();
               } finally {
                  continue;
               }
            }
         }

         BluetoothEvents.log(1313817172);
      }
   }

   private BluetoothDeviceManagerImpl() {
      this._isPowerOn = true;
      BluetoothME.addListener(this, this);
      this.addSystemListener(this);
      this.addGlobalEventListener(this);
      this._bluetoothPowerAction = new BluetoothPowerAction();
      this._bluetoothSetupAction = new BluetoothSetupAction();
      this._profileManagers = new BluetoothProfileManager[0];
      BluetoothProfileManager manager = new BluetoothPhoneManager(this);
      if (manager.init()) {
         Arrays.add(this._profileManagers, manager);
      }

      manager = new BluetoothA2DPManager(this);
      if (manager.init()) {
         Arrays.add(this._profileManagers, manager);
      }

      BluetoothProfileManager var8 = new BluetoothAVRCPManager(this);
      if (var8.init()) {
         Arrays.add(this._profileManagers, var8);
      }

      this._persistentObject = RIMPersistentStore.getPersistentObject(7045685465310336373L);
      synchronized (this._persistentObject) {
         this._bluetoothOptionsData = (BluetoothOptionsData)this._persistentObject.getContents();
         if (this._bluetoothOptionsData == null) {
            this._bluetoothOptionsData = new BluetoothOptionsData();
            this._persistentObject.setContents(this._bluetoothOptionsData, 51, false);
            this._persistentObject.commit();
         }
      }

      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         dispatchManager.setDispatcher(29, new BluetoothDeviceManagerEventDispatcher());
      }

      this._syncItem = new BluetoothDeviceManagerImpl$BluetoothSyncItem(this);
      SyncManager syncManager = SyncManager.getInstance();
      if (syncManager != null) {
         syncManager.enableSynchronization(this._syncItem);
      }

      this._bluetoothDialogImage = ThemeManager.getThemeAwareImage("net_rim_bluetooth_dialog");
      this._indicatorManager = IndicatorManager.getInstance();
      if (this._indicatorManager != null) {
         this._indicatorManager.addIndicator(new BluetoothIndicator(this));
      }

      this._inRangeDevices = (SimpleSortingVector)(new Object());
      this._inRangeDevices.setSortComparator(new RSSIComparator());
      this._retryConnection = new RetryConnection(this);
      this.addRibbonSetupIcon();
      this.init();
   }

   private final void handlePowerOff() {
      this._isConnected = false;
      this.stateChanged();
      if (this._inquiryInProgress) {
         this._inquiryInProgress = false;
         this.postEvent(1);
      }

      for (int i = this._profileManagers.length - 1; i >= 0; i--) {
         this._profileManagers[i].cleanup();
      }

      this._retryConnection.cancel();
   }

   @Override
   public final void enableTCKTestMode(boolean enable) {
      this._tckTestMode = enable;
      if (enable) {
         this.setDiscoverable(true);
         this.setPowerOn(true);
      }
   }

   private final void addRibbonPowerIcon() {
      if (CodeModuleManager.getModuleHandle("net_rim_bb_manage_connections") == 0 && !ITPolicy.getBoolean(34, 1, false)) {
         if (!this._powerIconOnRibbon) {
            RibbonLauncher rl = RibbonLauncher.getInstance();
            if (rl != null) {
               rl.registerAction(this._bluetoothPowerAction.get(1, (String)((Object)null)), this._bluetoothPowerAction);
            }

            this._powerIconOnRibbon = true;
         }
      }
   }

   private final void addRibbonSetupIcon() {
      if (!ITPolicy.getBoolean(34, 2, false)) {
         if (!this._setupIconOnRibbon) {
            RibbonLauncher rl = RibbonLauncher.getInstance();
            if (rl != null) {
               rl.registerAction(this._bluetoothSetupAction.get(1, (String)((Object)null)), this._bluetoothSetupAction);
            }

            this._setupIconOnRibbon = true;
         }
      }
   }

   private final synchronized boolean fetchNextDeviceName() {
      if (this._devicesToLookup.size() == 0) {
         return false;
      } else {
         BluetoothDevice device = (BluetoothDevice)this._devicesToLookup.elementAt(0);
         int rc = BluetoothME.retrieveDeviceName(device.getAddress(), device.getPageScanInfo());
         if (rc != 0 && rc != 2) {
            BluetoothEvents.log(541478470);
            this._devicesToLookup.removeElementAt(0);
            return false;
         } else {
            return true;
         }
      }
   }

   @Override
   public final BluetoothSerialPortInfo[] getSerialPortInfo() {
      Vector v = (Vector)(new Object());
      synchronized (this._pairedDevices) {
         for (int i = this._pairedDevices.size() - 1; i >= 0; i--) {
            BluetoothDevice device = (BluetoothDevice)this._pairedDevices.elementAt(i);
            device.getSerialPortInfo(v);
         }
      }

      BluetoothSerialPortInfo[] a = new Object[v.size()];
      v.copyInto(a);
      return a;
   }

   private final void updateRibbonPowerIcon() {
      if (this._powerIconOnRibbon) {
         RibbonLauncher rl = RibbonLauncher.getInstance();
         if (rl != null) {
            rl.updateRegisteredAction(this._bluetoothPowerAction.get(1, (String)((Object)null)));
         }
      }
   }

   private final void addCard(AddressCardModel[] list, AddressCardModel card, int numPhoneNumbers, boolean includePhoneNumbers) {
      Arrays.add(list, card);
      if (includePhoneNumbers) {
         for (int i = 1; i < numPhoneNumbers; i++) {
            Arrays.add(list, card);
         }
      }
   }

   private final void removeRibbonPowerIcon() {
      if (this._powerIconOnRibbon) {
         RibbonLauncher rl = RibbonLauncher.getInstance();
         if (rl != null) {
            rl.unregisterAction(this._bluetoothPowerAction.get(1, (String)((Object)null)));
         }

         this._powerIconOnRibbon = false;
      }
   }

   @Override
   public final boolean setPowerOn(boolean on) {
      return this.setPowerOn(on, false);
   }

   private final void removeRibbonSetupIcon() {
      if (this._setupIconOnRibbon) {
         RibbonLauncher rl = RibbonLauncher.getInstance();
         if (rl != null) {
            rl.unregisterAction(this._bluetoothSetupAction.get(1, (String)((Object)null)));
         }

         this._setupIconOnRibbon = false;
      }
   }

   private final void init() {
      this._pairedDevices = (CloneableVector)(new Object());
      this._isConnected = false;
      this._devicesToLookup = (Vector)(new Object());
      boolean disabledByITPolicy = ITPolicy.getBoolean(34, 1, false);
      if (this._bluetoothOptionsData._powerOn && disabledByITPolicy) {
         this._bluetoothOptionsData._powerOn = false;
         this._persistentObject.commit();
      }

      Vector pairedDeviceData = this._bluetoothOptionsData._pairedDeviceData;
      int numPairedDevices = pairedDeviceData.size();

      for (int i = 0; i < numPairedDevices; i++) {
         BluetoothDevice device = new BluetoothDevice(this, (BluetoothDeviceData)pairedDeviceData.elementAt(i));
         this._pairedDevices.addElement(device);
      }

      if (numPairedDevices != 0 && !disabledByITPolicy) {
         this.addRibbonPowerIcon();
      } else {
         this.removeRibbonPowerIcon();
      }

      this.postEvent(4);
   }

   private final boolean requestPowerOn() {
      if (ITPolicy.getBoolean(34, 1, false)) {
         return false;
      }

      if (!USBPasswordRedirectManager.getInstance().isRadioAllowedOn()) {
         return false;
      }

      BluetoothME.setLocalDeviceName(this._bluetoothOptionsData.getLocalNameAsBytes());
      BluetoothME.setClassOfDevice(5243404);
      int rc = BluetoothME.setDiscoverable(this.isDiscoverable());
      if (rc != 0 && rc != 2) {
         BluetoothEvents.log(1396984134);
      }

      rc = BluetoothME.requestPowerOn();
      if (rc != 0 && rc != 2) {
         BluetoothEvents.log(542068294);
      }

      return true;
   }

   private final void requestPowerOff() {
      int rc = BluetoothME.requestPowerOff();
      if (rc != 0 && rc != 2) {
         BluetoothEvents.log(1330005574);
      }
   }

   private final void stateChanged() {
      if (this._indicatorManager != null) {
         this._indicatorManager.updateIndicators();
      }

      this.updateLEDIndicator();
      this.postEvent(6);
   }

   private final void updateLEDIndicator() {
      LEDEngine ledEngine = LEDEngine.getInstance();
      if (this.isLEDIndicatorEnabled()) {
         if (this.isConnected()) {
            ledEngine.setFlag(2);
         } else {
            ledEngine.clearFlag(2);
         }
      } else {
         ledEngine.clearFlag(2);
      }
   }

   private final void ribbonPowerOnOff() {
      boolean newStateOn = !BluetoothME.isPowerOn();
      if (newStateOn && ITPolicy.getBoolean(34, 11, false)) {
         BluetoothDeviceManagerImpl$PasswordPromptApplication app = new BluetoothDeviceManagerImpl$PasswordPromptApplication(
            this, BluetoothMainScreen.getString(59)
         );
         app.invokeLater(app);
         app.enterEventDispatcher();
      } else {
         long now = System.currentTimeMillis();
         if (now - this._lastRibbonRequest >= 2000) {
            this.setPowerOn(newStateOn);
            this._lastRibbonRequest = now;
         }
      }
   }

   @Override
   public final boolean isRadioOnPromptIfOff(boolean forcePrompt) {
      boolean result = true;
      if (!BluetoothME.isPowerOn() && (forcePrompt || !ControlledAccess.verifyRRISignatures(false))) {
         Application app = null;

         label154:
         try {
            app = Application.getApplication();
         } finally {
            break label154;
         }

         ApplicationDescriptor currentAppDescriptor = ApplicationDescriptor.currentApplicationDescriptor();
         String callingApplication = currentAppDescriptor.getLocalizedName();
         if (callingApplication == null || callingApplication.trim().length() == 0) {
            callingApplication = currentAppDescriptor.getName();
         }

         if (callingApplication == null || callingApplication.trim().length() == 0) {
            callingApplication = currentAppDescriptor.getModuleName();
         }

         BluetoothDeviceManagerImpl$RadioOnPromptRunnable radioPromptRunnable = new BluetoothDeviceManagerImpl$RadioOnPromptRunnable(this, callingApplication);
         if (app != null && app.hasEventThread() && app.isEventThread()) {
            radioPromptRunnable.run(true);
         } else {
            synchronized (radioPromptRunnable) {
               if (app != null && app.hasEventThread()) {
                  app.invokeLater(radioPromptRunnable);
               } else {
                  this.invokeLater(radioPromptRunnable);
               }

               label131:
               try {
                  radioPromptRunnable.wait();
               } finally {
                  break label131;
               }
            }
         }

         result = radioPromptRunnable.getResult();
      }

      return result;
   }
}
