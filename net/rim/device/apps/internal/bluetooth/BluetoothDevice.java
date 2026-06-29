package net.rim.device.apps.internal.bluetooth;

import java.util.Enumeration;
import java.util.Vector;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.api.quincy.QuincyManager;
import net.rim.device.internal.bluetooth.BluetoothDeviceManager;
import net.rim.device.internal.bluetooth.BluetoothEvents;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.ui.IconCollection;

public final class BluetoothDevice implements Runnable {
   private BluetoothDeviceData _data;
   private int _pageScanInfo;
   private int _rssi;
   private BluetoothDeviceManagerImpl _btManager;
   private int _serviceDiscoveryLinkHandle = -1;
   private boolean _authorizeAfterServiceDiscovery;
   private boolean _connectAfterServiceDiscovery;
   private boolean _dumpCachedSDPData;
   private DataBuffer _partialQueryData;
   private int _waitingForConnection;
   private boolean _waitingForPINRequest;
   private boolean _waitingForPINResponse;
   private boolean _shouldBeInSniffMode;
   private boolean _serviceDiscoveryInProgress;
   private boolean _sniffModeSupported;
   private int _sniffModeAttempts;
   private BluetoothDevice$StatusDialog _statusDialog;
   private BluetoothDevice$QuestionDialog _questionDialog;
   private BluetoothDevice$PINDialog _pinDialog;
   private RemoteDevice _remoteDevice;
   private Vector _sdpQueryQueue;
   private static final int SERVICE_INDEX_HANDSFREE = 0;
   private static final int SERVICE_INDEX_HEADSET = 1;
   private static final int SERVICE_INDEX_SERIAL_PORT = 2;
   private static final int SERVICE_INDEX_OBJECT_PUSH = 3;
   private static final int SERVICE_INDEX_AVRCP_TARGET = 4;
   private static final int SERVICE_INDEX_AVRCP_CONTROLLER = 5;
   private static final int SERVICE_INDEX_A2DP_SINK = 6;
   private static final UUID[][][] SERVICE_CLASS_UUIDS = new UUID[][][]{
      {ServiceRecordImpl.HANDSFREE_UUID, ServiceRecordImpl.GENERIC_AUDIO_UUID},
      {ServiceRecordImpl.HEADSET_UUID, ServiceRecordImpl.GENERIC_AUDIO_UUID},
      {ServiceRecordImpl.SERIAL_PORT_UUID, null},
      {ServiceRecordImpl.OBJECT_PUSH_UUID, null},
      {ServiceRecordImpl.AVRCP_TARGET_UUID, null},
      {ServiceRecordImpl.AVRCP_CONTROLLER_UUID, null},
      {ServiceRecordImpl.A2DP_SINK_UUID, null}
   };
   static final int PROFILE_FLAG_HANDSFREE = 1;
   static final int PROFILE_FLAG_HEADSET = 2;
   static final int PROFILE_FLAG_SERIAL_PORT = 4;
   static final int PROFILE_FLAG_OBJECT_PUSH = 8;
   static final int PROFILE_FLAG_AVRCP_TARGET = 16;
   static final int PROFILE_FLAG_AVRCP_CONTROLLER = 32;
   static final int PROFILE_FLAG_A2DP_SINK = 64;
   private static final int[] SDP_QUERY_ATTRIBUTE_IDS = new int[]{
      0,
      1,
      2,
      3,
      4,
      256,
      -805044213,
      775162112,
      774909491,
      3420721,
      -805044199,
      1699878656,
      1918985587,
      1226860643,
      1867325550,
      1852795252,
      1685343264,
      46,
      -805044040,
      1126894093,
      977555017,
      1931618336,
      1769370213,
      740451683
   };
   public static final int AUTH_ASK = 0;
   public static final int AUTH_YES = 1;
   public static final int AUTH_NO = 2;
   static final int NREC_MODE_AUTO = 0;
   static final int NREC_MODE_ON = 1;
   static final int NREC_MODE_OFF = 2;
   private static IconCollection _icons = IconCollection.get("net_rim_bluetooth_device", 1);
   private static final int CONNECTION_NONE = 0;
   private static final int CONNECTION_FOR_PAIRING = 1;
   private static final int CONNECTION_FOR_SDP = 2;
   private static final int CONNECTION_FOR_UNKNOWN_REASON = 3;

   final void init() {
      this._waitingForPINRequest = false;
      this._waitingForPINResponse = false;
      this._serviceDiscoveryInProgress = false;
      this._sdpQueryQueue = (Vector)(new Object());
   }

   final BluetoothDeviceData getData() {
      return this._data;
   }

   final byte[] getAddress() {
      return this._data._address;
   }

   public final int getPageScanInfo() {
      return this._pageScanInfo;
   }

   final void setPageScanInfo(int pageScanInfo) {
      this._pageScanInfo = pageScanInfo;
   }

   final int getRSSI() {
      return this._rssi;
   }

   final void setRSSI(int rssi) {
      if (rssi == 127) {
         this._rssi = -255;
      } else {
         this._rssi = rssi;
      }
   }

   public final int getDeviceClass() {
      return this._data._deviceClass;
   }

   final void setDeviceClass(int deviceClass) {
      this._data._deviceClass = deviceClass;
   }

   public final String getName() {
      return this._data._name;
   }

   public final synchronized boolean fetchName() {
      if (!this._btManager.fetchDeviceName(this)) {
         return false;
      }

      try {
         this.wait(60000);
         return true;
      } finally {
         ;
      }
   }

   final synchronized void setName(String name) {
      this._data._name = name == null ? null : name.trim();
      this.notifyAll();
      if (this._waitingForPINRequest) {
         this._waitingForPINRequest = false;
         if (name == null) {
            BluetoothME.sendPIN(this._data._address, null);
            return;
         }

         this.getPIN();
      }
   }

   public final String getFriendlyName() {
      return this._data._friendlyName == null ? this._data._name : this._data._friendlyName;
   }

   final void setFriendlyName(String friendlyName) {
      this._data._friendlyName = friendlyName;
   }

   public final int getAuthorized() {
      return this._data._authorized;
   }

   final void setAuthorized(int authorized) {
      this._data._authorized = authorized;
   }

   final int getNRECMode() {
      return this._data._nrecMode;
   }

   final void setNRECMode(int nrecMode) {
      this._data._nrecMode = nrecMode;
   }

   final boolean isEncryptionEnabled() {
      return this._data._encrypt;
   }

   final void setEncryptionEnabled(boolean enabled) {
      this._data._encrypt = enabled;
   }

   final boolean isPaired() {
      return this._data._linkKey != null;
   }

   final byte[] getLinkKey() {
      return this._data._linkKey;
   }

   final boolean toggleSniffModeEnabled() {
      this._data._sniffModeEnabled = !this._data._sniffModeEnabled;
      return this._data._sniffModeEnabled;
   }

   final synchronized boolean pairDevice() {
      this._waitingForConnection = 1;
      this.notifyOperationStart();
      int[] handle = new int[1];
      int rc = BluetoothME.connectDevice(this._data._address, this._pageScanInfo, handle);
      switch (rc) {
         case 0:
            this._serviceDiscoveryLinkHandle = handle[0];
            this.connectSucceeded();
            return true;
         case 2:
            this._serviceDiscoveryLinkHandle = handle[0];
            return true;
         default:
            this.connectFailed();
            return false;
      }
   }

   final boolean canConnect() {
      BluetoothProfileManager[] profileManagers = this._btManager.getProfileManagers();
      boolean rc = false;

      for (int i = profileManagers.length - 1; i >= 0; i--) {
         if (profileManagers[i].isConnected(this)) {
            return false;
         }

         if (profileManagers[i].canConnect(this)) {
            rc = true;
         }
      }

      return rc;
   }

   final void connect(boolean notify) {
      if (notify) {
         this.notifyOperationStart();
      }

      BluetoothProfileManager[] managers = this._btManager.getProfileManagers();
      boolean connectionInProgress = false;
      String errorMsg = null;
      int numManagers = managers.length;

      for (int i = 0; i < numManagers; i++) {
         if (managers[i].canConnect(this)) {
            switch (managers[i].connect(this)) {
               case -1:
               case 1:
                  break;
               case 0:
               default:
                  connectionInProgress = true;
                  break;
               case 2:
                  if (errorMsg == null) {
                     String itPolicyName = managers[i].getName();
                     Object[] args = new Object[]{itPolicyName};
                     errorMsg = MessageFormat.format(BluetoothMainScreen.getString(44), args);
                  }
                  break;
               case 3:
                  if (errorMsg == null) {
                     String device = managers[i].getDevice().toString();
                     Object[] args = new Object[]{device};
                     errorMsg = MessageFormat.format(BluetoothMainScreen.getString(40), args);
                  }
            }
         }
      }

      if (!connectionInProgress && notify) {
         if (errorMsg == null) {
            this.displayDisconnectMessage(90);
            return;
         }

         this.notifyOperationComplete(errorMsg);
      }
   }

   final Verb addVerbs(VerbToMenu verbToMenu) {
      Verb defaultVerb = new BluetoothDevice$DevicePropertiesVerb(this);
      verbToMenu.addVerb(defaultVerb);
      if (this.isConnected()) {
         defaultVerb = new BluetoothDevice$DisconnectVerb(this);
         verbToMenu.addVerb(defaultVerb);
      } else {
         if (this.canConnect()) {
            defaultVerb = new BluetoothDevice$ConnectVerb(this);
            verbToMenu.addVerb(defaultVerb);
         }

         verbToMenu.addVerb(new BluetoothDevice$DeleteVerb(this));
      }

      if (this._btManager.getAddressBookTransferMode() != 0 && (this.hasHandsfree() || this.hasObjectPush())) {
         verbToMenu.addVerb(new PushAddressBookVerb(this));
      }

      return defaultVerb;
   }

   public final synchronized boolean doServiceAttributeRequest(RemoteServiceRecord remoteServiceRecord, int[] attributeSet) {
      this._sdpQueryQueue.addElement(new SdpQuery(remoteServiceRecord, attributeSet));
      return this.startServiceDiscovery(false);
   }

   public final synchronized boolean doServiceSearchAttributeRequest(int[] attributeSet, UUID[] uuidSet, int transactionID) {
      this._sdpQueryQueue.addElement(new SdpQuery(attributeSet, uuidSet, 0, transactionID));
      return this.startServiceDiscovery(false);
   }

   public final boolean isConnected() {
      return BluetoothME.isDeviceConnected(this._data._address);
   }

   public final boolean isConnectionEncrypted() {
      return BluetoothME.isConnectionEncrypted(this._data._address);
   }

   public final int encryptConnection() {
      return BluetoothME.encryptConnection(this._data._address, true);
   }

   final synchronized boolean startServiceDiscovery(boolean clearCache) {
      if (clearCache) {
         this._dumpCachedSDPData = true;
         this.addStandardSDPQueries();
         this.notifyOperationStart();
      }

      if (this._sdpQueryQueue.size() == 0) {
         return true;
      }

      if (this.isConnected()) {
         if (this._serviceDiscoveryInProgress) {
            return false;
         }

         this._serviceDiscoveryInProgress = true;
         if (this.doNextQuery(null)) {
            if (this._dumpCachedSDPData) {
               this._dumpCachedSDPData = false;
               this._data._serviceRecords = new RemoteServiceRecord[0];
               this._data._supportedProfiles = 0;
            }

            return true;
         } else {
            this._serviceDiscoveryInProgress = false;
            return false;
         }
      } else {
         this._waitingForConnection = 2;
         int[] handle = new int[1];
         int rc = BluetoothME.connectDevice(this._data._address, this._pageScanInfo, handle);
         switch (rc) {
            case 0:
               this._serviceDiscoveryLinkHandle = handle[0];
               this.connectSucceeded();
               return true;
            case 2:
               this._serviceDiscoveryLinkHandle = handle[0];
               return true;
            default:
               Object[] args = new Object[]{this};
               String prompt = MessageFormat.format(BluetoothMainScreen.getString(49), args);
               this.notifyOperationComplete(prompt);
               BluetoothEvents.log(1145260367);

               for (int i = this._sdpQueryQueue.size() - 1; i >= 0; i--) {
                  SdpQuery query = (SdpQuery)this._sdpQueryQueue.elementAt(i);
                  query.connectError();
               }

               this._sdpQueryQueue.removeAllElements();
               this._dumpCachedSDPData = false;
               return true;
         }
      }
   }

   final synchronized void serviceDiscoveryComplete(boolean param1, byte[] param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._serviceDiscoveryInProgress Z
      // 004: ifne 008
      // 007: return
      // 008: iload 1
      // 009: ifne 040
      // 00c: aload 0
      // 00d: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._sdpQueryQueue Ljava/util/Vector;
      // 010: invokevirtual java/util/Vector.size ()I
      // 013: bipush 1
      // 014: isub
      // 015: istore 3
      // 016: iload 3
      // 017: iflt 032
      // 01a: aload 0
      // 01b: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._sdpQueryQueue Ljava/util/Vector;
      // 01e: iload 3
      // 01f: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 022: checkcast net/rim/device/apps/internal/bluetooth/SdpQuery
      // 025: astore 4
      // 027: aload 4
      // 029: invokevirtual net/rim/device/apps/internal/bluetooth/SdpQuery.sdpError ()V
      // 02c: iinc 3 -1
      // 02f: goto 016
      // 032: aload 0
      // 033: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._sdpQueryQueue Ljava/util/Vector;
      // 036: invokevirtual java/util/Vector.removeAllElements ()V
      // 039: aload 0
      // 03a: aconst_null
      // 03b: invokespecial net/rim/device/apps/internal/bluetooth/BluetoothDevice.doNextQuery ([B)Z
      // 03e: pop
      // 03f: return
      // 040: new java/lang/Object
      // 043: dup
      // 044: aload 2
      // 045: bipush 0
      // 046: aload 2
      // 047: arraylength
      // 048: bipush 1
      // 049: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 04c: astore 3
      // 04d: aload 3
      // 04e: invokevirtual net/rim/device/api/util/DataBuffer.readUnsignedShort ()I
      // 051: istore 4
      // 053: aload 2
      // 054: iload 4
      // 056: bipush 2
      // 058: iadd
      // 059: baload
      // 05a: istore 5
      // 05c: iload 5
      // 05e: ifne 068
      // 061: aload 0
      // 062: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._partialQueryData Lnet/rim/device/api/util/DataBuffer;
      // 065: ifnull 0bf
      // 068: aload 0
      // 069: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._partialQueryData Lnet/rim/device/api/util/DataBuffer;
      // 06c: ifnonnull 07b
      // 06f: aload 0
      // 070: new java/lang/Object
      // 073: dup
      // 074: bipush 1
      // 075: invokespecial net/rim/device/api/util/DataBuffer.<init> (Z)V
      // 078: putfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._partialQueryData Lnet/rim/device/api/util/DataBuffer;
      // 07b: aload 0
      // 07c: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._partialQueryData Lnet/rim/device/api/util/DataBuffer;
      // 07f: aload 3
      // 080: iload 4
      // 082: invokevirtual net/rim/device/api/util/DataBuffer.write (Lnet/rim/device/api/util/DataBuffer;I)V
      // 085: aload 3
      // 086: iload 4
      // 088: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 08b: pop
      // 08c: iload 5
      // 08e: ifne 0a2
      // 091: aload 0
      // 092: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._partialQueryData Lnet/rim/device/api/util/DataBuffer;
      // 095: astore 3
      // 096: aload 3
      // 097: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 09a: aload 0
      // 09b: aconst_null
      // 09c: putfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._partialQueryData Lnet/rim/device/api/util/DataBuffer;
      // 09f: goto 0c8
      // 0a2: ldc_w 1396985938
      // 0a5: invokestatic net/rim/device/internal/bluetooth/BluetoothEvents.log (I)V
      // 0a8: iload 5
      // 0aa: bipush 1
      // 0ab: iadd
      // 0ac: newarray 8
      // 0ae: astore 6
      // 0b0: aload 3
      // 0b1: aload 6
      // 0b3: invokevirtual net/rim/device/api/util/DataBuffer.read ([B)I
      // 0b6: pop
      // 0b7: aload 0
      // 0b8: aload 6
      // 0ba: invokespecial net/rim/device/apps/internal/bluetooth/BluetoothDevice.doNextQuery ([B)Z
      // 0bd: pop
      // 0be: return
      // 0bf: aload 3
      // 0c0: aload 2
      // 0c1: bipush 2
      // 0c3: iload 4
      // 0c5: invokevirtual net/rim/device/api/util/DataBuffer.setData ([BII)V
      // 0c8: ldc_w 542327875
      // 0cb: invokestatic net/rim/device/internal/bluetooth/BluetoothEvents.log (I)V
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._data Lnet/rim/device/apps/internal/bluetooth/BluetoothDeviceData;
      // 0d2: getfield net/rim/device/apps/internal/bluetooth/BluetoothDeviceData._address [B
      // 0d5: invokestatic net/rim/device/internal/bluetooth/BluetoothME.stopServiceDiscovery ([B)I
      // 0d8: pop
      // 0d9: aload 0
      // 0da: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._sdpQueryQueue Ljava/util/Vector;
      // 0dd: bipush 0
      // 0de: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 0e1: checkcast net/rim/device/apps/internal/bluetooth/SdpQuery
      // 0e4: astore 6
      // 0e6: aload 6
      // 0e8: aload 0
      // 0e9: aload 3
      // 0ea: invokevirtual net/rim/device/apps/internal/bluetooth/SdpQuery.processResponse (Lnet/rim/device/apps/internal/bluetooth/BluetoothDevice;Lnet/rim/device/api/util/DataBuffer;)V
      // 0ed: goto 101
      // 0f0: astore 3
      // 0f1: ldc_w 1112756804
      // 0f4: invokestatic net/rim/device/internal/bluetooth/BluetoothEvents.log (I)V
      // 0f7: goto 101
      // 0fa: astore 3
      // 0fb: ldc_w 1112756804
      // 0fe: invokestatic net/rim/device/internal/bluetooth/BluetoothEvents.log (I)V
      // 101: aload 0
      // 102: getfield net/rim/device/apps/internal/bluetooth/BluetoothDevice._sdpQueryQueue Ljava/util/Vector;
      // 105: bipush 0
      // 106: invokevirtual java/util/Vector.removeElementAt (I)V
      // 109: aload 0
      // 10a: aconst_null
      // 10b: invokespecial net/rim/device/apps/internal/bluetooth/BluetoothDevice.doNextQuery ([B)Z
      // 10e: pop
      // 10f: return
      // try (4 -> 31): 123 null
      // try (32 -> 99): 123 null
      // try (100 -> 122): 123 null
      // try (4 -> 31): 127 null
      // try (32 -> 99): 127 null
      // try (100 -> 122): 127 null
   }

   final void addServiceRecord(RemoteServiceRecord record) {
      this._data.addServiceRecord(record);
   }

   public final RemoteServiceRecord getServiceRecord(UUID uuid) {
      UUID[] uuids = new UUID[]{uuid};
      synchronized (this._data._serviceRecords) {
         int numRecords = this._data._serviceRecords.length;

         for (int i = 0; i < numRecords; i++) {
            if (this._data._serviceRecords[i].containsClasses(uuids)) {
               return this._data._serviceRecords[i];
            }
         }

         return null;
      }
   }

   final void addSupportedProfile(int profile) {
      this._data._supportedProfiles |= profile;
   }

   public final Enumeration getServiceNames() {
      synchronized (this._data._serviceRecords) {
         int numRecords = this._data._serviceRecords.length;
         Vector names = (Vector)(new Object());

         for (int i = 0; i < numRecords; i++) {
            String name = this.getServiceName(this._data._name, this._data._serviceRecords[i]);
            if (name != null) {
               names.addElement(name);
            }
         }

         return names.elements();
      }
   }

   public final void getSerialPortInfo(Vector v) {
      synchronized (this._data._serviceRecords) {
         int numRecords = this._data._serviceRecords.length;

         for (int i = 0; i < numRecords; i++) {
            RemoteServiceRecord record = this._data._serviceRecords[i];
            if (record.containsClasses(SERVICE_CLASS_UUIDS[2])) {
               String serviceName = this.getServiceName(null, record);
               int serverID = record.getRFCOMMServerChannel();
               if (serverID != 0) {
                  v.addElement(new Object(this.getAddress(), this.getPageScanInfo(), this.getName(), serverID, serviceName));
               }
            }
         }
      }
   }

   final boolean hasHandsfree() {
      return (this._data._supportedProfiles & 1) != 0;
   }

   final boolean hasHeadset() {
      return (this._data._supportedProfiles & 2) != 0;
   }

   final boolean hasObjectPush() {
      return (this._data._supportedProfiles & 8) != 0;
   }

   final boolean hasA2DPSink() {
      return (this._data._supportedProfiles & 64) != 0;
   }

   final boolean hasAVRCPController() {
      return (this._data._supportedProfiles & 32) != 0;
   }

   final void paint(Graphics graphics, int y, int width) {
      Font originalFont = graphics.getFont();
      Font font = originalFont;
      if (this.isConnected()) {
         font = originalFont.derive(1);
         graphics.setFont(font);
      }

      int iconWidth = _icons.getWidth(font);
      _icons.paint(graphics, 0, y, this.getIconIndex());
      graphics.drawText(this.toString(), iconWidth, y, 70, width - iconWidth);
      graphics.setFont(originalFont);
   }

   final synchronized void authorizationRequired(int securityRecordID) {
      if (securityRecordID == -4) {
         BluetoothProfileManager a2dpManager = this._btManager.getProfileManager(1);
         if (a2dpManager != null && a2dpManager.isConnected(this)) {
            BluetoothME.authorizeDevice(this._data._address, true);
            return;
         }
      }

      if (this._serviceDiscoveryInProgress) {
         this._authorizeAfterServiceDiscovery = true;
      } else {
         switch (this.getAuthorized()) {
            case 0:
               if (ApplicationManager.getApplicationManager().isSystemLocked()) {
                  BluetoothME.authorizeDevice(this._data._address, false);
                  return;
               } else {
                  if (this._questionDialog != null) {
                     this._questionDialog.close();
                  }

                  this._questionDialog = new BluetoothDevice$QuestionDialog(this, 0);
               }
            case -1:
               return;
            case 1:
            default:
               BluetoothME.authorizeDevice(this._data._address, true);
               return;
            case 2:
               BluetoothME.authorizeDevice(this._data._address, false);
         }
      }
   }

   final synchronized void notifyOperationStart() {
      this.notifyOperationStart(null);
   }

   final synchronized void notifyOperationStart(String msg) {
      BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
      if (btManager.isPowerOn() && !btManager.isAutoReconnect()) {
         if (this._statusDialog == null) {
            this._statusDialog = new BluetoothDevice$StatusDialog(this, msg);
            return;
         }

         this._statusDialog.setPrompt(msg, false, false);
      }
   }

   final void notifyOperationComplete(String msg) {
      this.notifyOperationComplete(msg, false);
   }

   final synchronized void notifyOperationComplete(String msg, boolean showConnectionStatus) {
      BluetoothDeviceManagerImpl btManager = (BluetoothDeviceManagerImpl)BluetoothDeviceManager.getInstance();
      if (btManager.isPowerOn() && !btManager.isAutoReconnect()) {
         if (this._statusDialog == null) {
            if (msg == null) {
               return;
            }

            this._statusDialog = new BluetoothDevice$StatusDialog(this, msg, true, showConnectionStatus);
            return;
         }

         if (msg == null) {
            this._statusDialog.dismiss();
            return;
         }

         this._statusDialog.setPrompt(msg, true, showConnectionStatus);
      }
   }

   final synchronized void getPIN() {
      if (!this._waitingForPINRequest) {
         this._waitingForPINRequest = true;
         if (this.getName() != null || !this._btManager.fetchDeviceName(this)) {
            synchronized (this) {
               this.notifyOperationComplete(null);
               if (this._pinDialog == null) {
                  this._pinDialog = new BluetoothDevice$PINDialog(this);
               }
            }
         }
      }
   }

   final synchronized void pairingSucceeded(byte[] linkKey, int linkKeyType) {
      this._waitingForPINResponse = false;
      this._data._linkKey = linkKey;
      this._data._linkKeyType = linkKeyType;
      this._connectAfterServiceDiscovery = true;
      this.addStandardSDPQueries();
   }

   final void deleteLinkKey() {
      this._data._linkKey = null;
      this._data._linkKeyType = 0;
      BluetoothME.deleteLinkKey(this._data._address);
   }

   final synchronized void connectSucceeded() {
      this._waitingForConnection = 0;
      if (this._data._encrypt) {
         int result = this.encryptConnection();
         BluetoothEvents.logResult(1162018816, result);
      }

      this.startServiceDiscovery(false);
   }

   final synchronized void connectFailed() {
      if (this._waitingForPINRequest) {
         this._waitingForPINRequest = false;
         BluetoothME.sendPIN(this._data._address, null);
      }

      if (this._questionDialog != null) {
         this._questionDialog.disconnected();
      }

      if (this._pinDialog != null) {
         this._pinDialog.dismiss();
      }

      if (this._waitingForConnection != 0 || this._waitingForPINResponse) {
         this.connectError();
         this._waitingForConnection = 0;
         this._waitingForPINResponse = false;

         for (int i = this._sdpQueryQueue.size() - 1; i >= 0; i--) {
            SdpQuery query = (SdpQuery)this._sdpQueryQueue.elementAt(i);
            query.connectError();
         }

         this._sdpQueryQueue.removeAllElements();
      }

      this._serviceDiscoveryLinkHandle = -1;
   }

   final synchronized void disconnected() {
      if (this._questionDialog != null) {
         this._questionDialog.disconnected();
      }

      if (this._pinDialog != null) {
         this._pinDialog.dismiss();
      }

      if (this._waitingForConnection == 3) {
         this.displayDisconnectMessage(91);
      }

      this._waitingForPINRequest = false;
      this._waitingForConnection = 0;
      this._waitingForPINResponse = false;
      this._serviceDiscoveryInProgress = false;
      this._partialQueryData = null;
   }

   final synchronized void updateSniffMode() {
      BluetoothProfileManager[] profileManagers = this._btManager.getProfileManagers();
      this._shouldBeInSniffMode = true;

      for (int i = profileManagers.length - 1; i >= 0; i--) {
         if (!profileManagers[i].sniffModeDesired()) {
            this._shouldBeInSniffMode = false;
            break;
         }
      }

      if (this._shouldBeInSniffMode) {
         this.startSniff();
      } else {
         this.stopSniff();
      }
   }

   final void linkModeChanged(int result, int mode) {
      switch (mode) {
         case 0:
            if (this._shouldBeInSniffMode) {
               this.invokeSniff();
            }
            break;
         case 2:
            if (!this._sniffModeSupported) {
               this._sniffModeSupported = true;
            }

            if (!this._shouldBeInSniffMode && result != 0) {
               this.stopSniffFailed(result, false);
               return;
            }
      }
   }

   final synchronized void connectionUpdate(BluetoothProfileManager manager, int state, boolean connectFailed) {
      if (state == 2) {
         BluetoothProfileManager[] managers = this._btManager.getProfileManagers();
         int numManagers = managers.length;

         for (int i = 0; i < numManagers; i++) {
            if (managers[i].getState(this) == 1) {
               return;
            }
         }

         Object[] args = new Object[]{this};
         String prompt = MessageFormat.format(BluetoothMainScreen.getString(89), args);
         this.notifyOperationComplete(prompt, true);
         if (manager.getID() == 0 && this.hasA2DPSink()) {
            this.connect(false);
            return;
         }
      } else if (state == 0) {
         BluetoothProfileManager[] managers = this._btManager.getProfileManagers();
         int numManagers = managers.length;

         for (int i = 0; i < numManagers; i++) {
            if (managers[i].getState(this) != 0) {
               return;
            }
         }

         this.displayDisconnectMessage(connectFailed ? 90 : 91);
      }
   }

   public final synchronized RemoteDevice getRemoteDevice() {
      if (this._remoteDevice == null) {
         this._remoteDevice = new RemoteDeviceImpl(BluetoothME.deviceAddressToString(this._data._address));
      }

      return this._remoteDevice;
   }

   @Override
   public final void run() {
      if (this._serviceDiscoveryLinkHandle != -1) {
         BluetoothME.disconnectDevice(this._serviceDiscoveryLinkHandle);
         this._serviceDiscoveryLinkHandle = -1;
      } else if (this._shouldBeInSniffMode && BluetoothME.getLinkMode(this._data._address) != 2) {
         if (this._data._sniffModeEnabled) {
            this._sniffModeAttempts++;
            int rc = BluetoothME.startSniff(this._data._address, 16, 800, 2, 1);
            if (rc != 0 && rc != 2) {
               BluetoothEvents.log(1398035270);
            } else {
               BluetoothEvents.log(1398035283);
            }
         } else {
            BluetoothEvents.log(1397638724);
         }
      }
   }

   private final synchronized boolean doNextQuery(byte[] continuationData) {
      if (this._sdpQueryQueue.size() != 0) {
         SdpQuery query = (SdpQuery)this._sdpQueryQueue.elementAt(0);
         byte[] requestData = query.getRequestData();
         int rc = BluetoothME.startServiceDiscovery(this._data._address, query.getType(), requestData, continuationData);
         if (rc != 0 && rc != 2) {
            BluetoothEvents.log(1396986694);
            return false;
         } else {
            BluetoothEvents.log(542327891);
            return true;
         }
      } else {
         this._serviceDiscoveryInProgress = false;
         if (this._authorizeAfterServiceDiscovery) {
            this.authorizationRequired(-5);
            this._authorizeAfterServiceDiscovery = false;
            this.notifyOperationComplete(null);
         } else if (this._connectAfterServiceDiscovery) {
            Object[] args = new Object[]{this};
            if (this.canConnect()) {
               this.notifyOperationComplete(null);
               if (this._questionDialog == null) {
                  this._questionDialog = new BluetoothDevice$QuestionDialog(this, 1);
               }
            } else {
               String prompt = MessageFormat.format(BluetoothMainScreen.getString(8), args);
               this.notifyOperationComplete(prompt);
            }

            this._connectAfterServiceDiscovery = false;
         } else {
            this.notifyOperationComplete(null);
         }

         if (this._serviceDiscoveryLinkHandle != -1) {
            Application.getApplication().invokeLater(this, 5000, false);
         }

         this._btManager.commitDeviceData();
         return true;
      }
   }

   private final synchronized void connectError() {
      Object[] args = new Object[]{this};
      switch (this._waitingForConnection) {
         case 1:
            if (this._questionDialog == null) {
               this.notifyOperationComplete(null);
               this._questionDialog = new BluetoothDevice$QuestionDialog(this, 2);
               return;
            }
         case 0: {
            String prompt = MessageFormat.format(BluetoothMainScreen.getString(11), args);
            this.notifyOperationComplete(prompt);
            return;
         }
         case 2:
         default: {
            String prompt = MessageFormat.format(BluetoothMainScreen.getString(49), args);
            this.notifyOperationComplete(prompt);
         }
      }
   }

   private final synchronized void addStandardSDPQueries() {
      for (int i = 0; i < SERVICE_CLASS_UUIDS.length; i++) {
         this._sdpQueryQueue.addElement(new SdpQuery(SDP_QUERY_ATTRIBUTE_IDS, SERVICE_CLASS_UUIDS[i], 1 << i, 0));
      }
   }

   BluetoothDevice(BluetoothDeviceManagerImpl btManager, byte[] address) {
      this._btManager = btManager;
      this._data = new BluetoothDeviceData();
      this._data._address = address;
      this.init();
   }

   private final void startSniff() {
      this._sniffModeAttempts = 0;
      this._sniffModeSupported = false;
      this.invokeSniff();
   }

   private final void stopSniff() {
      if (BluetoothME.getLinkMode(this._data._address) == 2) {
         int rc = BluetoothME.stopSniff(this._data._address);
         if (rc != 0 && rc != 2) {
            this.stopSniffFailed(rc, true);
         } else {
            BluetoothEvents.log(1397773139);
         }
      }
   }

   private final void stopSniffFailed(int result, boolean statusCode) {
      BluetoothEvents.logResult(1397948416, result);
      if (!statusCode || result != 19) {
         QuincyManager.sendJavaLogworthy("BT stopSniffFailed");
      }
   }

   private final int getIconIndex() {
      switch (this._data._deviceClass & 7936) {
         case 256:
            return 3;
         case 512:
            return 2;
         case 1024:
            switch (this._data._deviceClass & 252) {
               case 4:
               case 16:
                  return 0;
               case 24:
                  return 5;
               default:
                  return 1;
            }
         default:
            return 4;
      }
   }

   private final void invokeSniff() {
      if (!this._sniffModeSupported && this._sniffModeAttempts >= 3) {
         BluetoothEvents.log(1397571928);
      } else {
         Application.getApplication().invokeLater(this, 10000, false);
      }
   }

   private final String getServiceName(String deviceName, RemoteServiceRecord record) {
      String serviceName = record.getServiceName();
      if (serviceName != null && serviceName.equals(deviceName)) {
         serviceName = null;
      }

      if (serviceName == null) {
         int rc;
         if (record.containsClasses(SERVICE_CLASS_UUIDS[2])) {
            rc = 36;
         } else if (record.containsClasses(SERVICE_CLASS_UUIDS[1])) {
            rc = 37;
         } else if (record.containsClasses(SERVICE_CLASS_UUIDS[0])) {
            rc = 38;
         } else if (record.containsClasses(SERVICE_CLASS_UUIDS[3])) {
            rc = 64;
         } else if (record.containsClasses(SERVICE_CLASS_UUIDS[4])) {
            rc = 79;
         } else if (record.containsClasses(SERVICE_CLASS_UUIDS[5])) {
            rc = 80;
         } else if (record.containsClasses(SERVICE_CLASS_UUIDS[6])) {
            rc = 78;
         } else {
            rc = 39;
         }

         serviceName = BluetoothMainScreen.getString(rc);
      }

      return serviceName;
   }

   BluetoothDevice(BluetoothDeviceManagerImpl btManager, BluetoothDeviceData data) {
      this._btManager = btManager;
      this._data = data;
      BluetoothME.restoreLinkKey(this._data._address, this._data._linkKey, this._data._linkKeyType);
      this.init();
   }

   @Override
   public final String toString() {
      String name = this.getFriendlyName();
      if (name == null) {
         int rc;
         switch (this._data._deviceClass & 7936) {
            case 256:
               rc = 13;
               break;
            case 512:
               rc = 24;
               break;
            case 768:
               rc = 25;
               break;
            case 1024:
               rc = 26;
               break;
            case 1280:
               rc = 27;
               break;
            case 1536:
               rc = 28;
               break;
            default:
               rc = 29;
         }

         name = BluetoothMainScreen.getString(rc);
      }

      return name;
   }

   private final void displayDisconnectMessage(int rc) {
      Object[] args = new Object[]{this};
      String prompt = MessageFormat.format(BluetoothMainScreen.getString(rc), args);
      this.notifyOperationComplete(prompt);
   }
}
