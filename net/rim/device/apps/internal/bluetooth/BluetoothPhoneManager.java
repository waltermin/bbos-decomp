package net.rim.device.apps.internal.bluetooth;

import java.util.Vector;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.media.control.AudioPathControl;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.AudioRouterListener;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.PhoneCallListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookOptions;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.registration.VerbFactory;
import net.rim.device.apps.api.framework.registration.VerbFactoryRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.util.SimpleFolder;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ribbon.RibbonNetworkInfo;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;
import net.rim.device.apps.internal.phone.data.PhoneFolders;
import net.rim.device.apps.internal.phone.model.PhoneNumberConverter;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.bluetooth.HandsfreeGateway;
import net.rim.device.internal.bluetooth.HandsfreeGatewayListener;
import net.rim.device.internal.bluetooth.HeadsetGateway;
import net.rim.device.internal.bluetooth.HeadsetGatewayListener;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.vad.VADUserEventListener;
import net.rim.device.internal.vad.VADUserEvents;
import net.rim.tid.im.layout.SLKeyLayout;

final class BluetoothPhoneManager
   extends BluetoothProfileManager
   implements PhoneCallListener,
   RadioStatusListener,
   HandsfreeGatewayListener,
   HeadsetGatewayListener,
   AudioRouterListener,
   VADUserEventListener,
   VerbFactory,
   GlobalEventListener {
   private BluetoothDevice _deviceToReconnect;
   private boolean _isHandsfree;
   private int _scoState;
   private int _nextAnswerState;
   private int _nextHoldState;
   private CallInfo[] _callInfoArray;
   private Object _delayedDialingNumber;
   private boolean _lastService;
   private boolean _lastCall;
   private int _lastCallSetup;
   private int _lastCallHeld;
   private int _lastBatteryLevel;
   private int _lastRSSI;
   private boolean _lastLowBattery;
   private boolean _lastCharging;
   private boolean _lastRoaming;
   private boolean _hasCallHeldIndicator;
   private boolean _activateVADAfterSCOConnect;
   private boolean _eventReporting;
   private boolean _callWaitingReporting;
   private boolean _callerIDReporting;
   private boolean _remoteVolumeControl;
   private boolean _hasVoiceRecognition;
   private int _remoteVolume;
   private AudioRouter _audioRouter;
   private BluetoothPhoneManager$RingRunnable _ringRunnable;
   private Phone _phone;
   private boolean _nrecDisabledByHF;
   private AddressCardModel[] _addressBookCards;
   private PhoneNumberModel[] _addressBookNumbers;
   private int _networkType;
   private boolean _extendedErrors;
   private int _addressBookEncoding;
   private boolean _vadEnabledOnHF;
   private int _currentPhonebook;
   private boolean _sniffModeDesired;
   private static final int SCO_STATE_DISCONNECTED = 0;
   private static final int SCO_STATE_CONNECTING = 1;
   private static final int SCO_STATE_CONNECTED = 2;
   private static final int SCO_STATE_CONNECTING_DISCONNECT_WHEN_DONE = 3;
   private static final int STATE_NONE = 0;
   private static final int STATE_ANSWER_CALL = 1;
   private static final int STATE_RESUME_CALL = 2;
   private static final int STATE_SEND_OK = 3;
   private static final int STATE_SEND_ERROR = 4;
   private static final int STATE_SEND_OK_FORCE_CALL_HELD_UPDATE = 5;
   private static final int MAX_PHONE_NUMBERS = 8;
   private static final int ENCODING_ASCII = 0;
   private static final int ENCODING_GSM = 1;
   private static final int ENCODING_UTF8 = 2;
   private static final int ENCODING_UCS2 = 3;
   private static final int PHONEBOOK_BB = 0;
   private static final int PHONEBOOK_SIM = 1;
   private static final int PHONEBOOK_DIALED_CALLS = 2;
   private static final int PHONEBOOK_MISSED_CALLS = 3;
   private static final int PHONEBOOK_RECEIVED_CALLS = 4;
   private static final int MAX_CALL_HISTORY_ITEMS = 10;
   private static final int MAX_PHONEBOOK_NAME_LENGTH = 40;
   private static final int MAX_PHONEBOOK_NUMBER_LENGTH = 24;
   private static final byte[] BYTE_145 = new byte[]{49, 52, 53};
   private static final byte[] BYTE_129 = new byte[]{49, 50, 57};
   private static final byte[] RESPONSE_OK = new byte[]{13, 10, 79, 75, 13, 10};
   private static final byte[] RESPONSE_ERROR = new byte[]{13, 10, 69, 82, 82, 79, 82, 13, 10};
   private static final byte[] BYTE_CPBR = new byte[]{13, 10, 43, 67, 80, 66, 82, 58, 32};
   private static final byte[] RESPONSE_CIND = new byte[]{
      13,
      10,
      43,
      67,
      73,
      78,
      68,
      58,
      32,
      40,
      34,
      115,
      101,
      114,
      118,
      105,
      99,
      101,
      34,
      44,
      40,
      48,
      45,
      49,
      41,
      41,
      44,
      40,
      34,
      99,
      97,
      108,
      108,
      34,
      44,
      40,
      48,
      45,
      49,
      41,
      41,
      44,
      40,
      34,
      99,
      97,
      108,
      108,
      115,
      101,
      116,
      117,
      112,
      34,
      44,
      40,
      48,
      45,
      51,
      41,
      41,
      44,
      40,
      34,
      98,
      97,
      116,
      116,
      99,
      104,
      103,
      34,
      44,
      40,
      48,
      45,
      53,
      41,
      41,
      44,
      40,
      34,
      115,
      105,
      103,
      110,
      97,
      108,
      34,
      44,
      40,
      48,
      45,
      53,
      41,
      41,
      44,
      40,
      34,
      98,
      97,
      116,
      116,
      101,
      114,
      121,
      119,
      97,
      114,
      110,
      105,
      110,
      103,
      34,
      44,
      40,
      48,
      45,
      49,
      41,
      41,
      44,
      40,
      34,
      99,
      104,
      97,
      114,
      103,
      101,
      114,
      99,
      111,
      110,
      110,
      101,
      99,
      116,
      101,
      100,
      34,
      44,
      40,
      48,
      45,
      49,
      41,
      41,
      44,
      40,
      34,
      99,
      97,
      108,
      108,
      104,
      101,
      108,
      100,
      34,
      44,
      40,
      48,
      45,
      50,
      41,
      41,
      44,
      40,
      34,
      114,
      111,
      97,
      109,
      34,
      44,
      40,
      48,
      45,
      49,
      41,
      41,
      13,
      10
   };
   private static final byte[] RESPONSE_CPBS = new byte[]{
      13, 10, 43, 67, 80, 66, 83, 58, 32, 40, 34, 77, 69, 34, 44, 34, 83, 77, 34, 44, 34, 68, 67, 34, 44, 34, 77, 67, 34, 44, 34, 82, 67, 34, 41, 13, 10
   };
   private static final byte[] RESPONSE_CSCS = new byte[]{
      13,
      10,
      43,
      67,
      83,
      67,
      83,
      58,
      32,
      40,
      34,
      65,
      83,
      67,
      73,
      73,
      44,
      34,
      56,
      56,
      53,
      57,
      45,
      49,
      34,
      44,
      34,
      71,
      83,
      77,
      34,
      44,
      34,
      85,
      84,
      70,
      45,
      56,
      34,
      44,
      34,
      85,
      67,
      83,
      45,
      50,
      34,
      41,
      13,
      10
   };
   private static final int CME_ERROR_AG_FAILURE = 0;
   private static final int CME_ERROR_NO_CONNECTION = 1;
   private static final int CME_ERROR_OPERATION_NOT_ALLOWED = 3;
   private static final int CME_ERROR_OPERATION_NOT_SUPPORTED = 4;
   private static final int CME_ERROR_PH_SIM_PIN_REQUIRED = 5;
   private static final int CME_ERROR_SIM_NOT_INSERTED = 10;
   private static final int CME_ERROR_SIM_PIN_REQUIRED = 11;
   private static final int CME_ERROR_SIM_PUK_REQUIRED = 12;
   private static final int CME_ERROR_SIM_FAILURE = 13;
   private static final int CME_ERROR_SIM_BUSY = 14;
   private static final int CME_ERROR_INCORRECT_PASSWORD = 16;
   private static final int CME_ERROR_SIM_PIN2_REQUIRED = 17;
   private static final int CME_ERROR_SIM_PUK2_REQUIRED = 18;
   private static final int CME_ERROR_MEMORY_FULL = 20;
   private static final int CME_ERROR_INVALID_INDEX = 21;
   private static final int CME_ERROR_MEMORY_FAILURE = 23;
   private static final int CME_ERROR_TEXT_STRING_TOO_LONG = 24;
   private static final int CME_ERROR_INVALID_CHARACTERS_IN_TEXT_STRING = 25;
   private static final int CME_ERROR_DIAL_STRING_TOO_LONG = 26;
   private static final int CME_ERROR_INVALID_CHARACTERS_IN_DIAL_STRING = 27;
   private static final int CME_ERROR_NO_NETWORK_SERVICE = 30;
   private static final int CME_ERROR_NETWORK_TIMEOUT = 31;
   private static final int CME_ERROR_EMERGENCY_CALLS_ONLY = 32;
   private static final long LOG_GUID = -7580308769502781406L;
   private static final int LOG_SCO_LINK_FAILED = 1396920134;
   private static final int LOG_SCO_LINK_UP_REQUESTED = 1396921682;
   private static final int LOG_SCO_LINK_UP = 1396920149;
   private static final int LOG_SCO_LINK_DOWN_REQUESTED = 1396917330;
   private static final int LOG_SCO_LINK_DOWN = 1396920132;
   private static final int LOG_NO_ECHO_NOISE_CANCELLATION = 1313817923;
   private static final int LOG_HEADSET_CONNECT_REQUESTED = 1213416274;
   private static final int LOG_HEADSET_DISCONNECT_REQUESTED = 1213416530;
   private static final int LOG_HANDSFREE_CONNECT_REQUESTED = 1212564306;
   private static final int LOG_HANDSFREE_DISCONNECT_REQUESTED = 1212564562;
   private static final int LOG_RADIO_EXCEPTION = 542262616;
   private static final int LOG_NREC_DISABLED = 1314014531;
   private static final int LOG_DISCONNECT_REQUESTED = 1146224640;

   BluetoothPhoneManager(BluetoothDeviceManagerImpl btManager) {
      super(btManager, -7580308769502781406L, "net.rim.bluetooth.hf");
   }

   @Override
   final int getID() {
      return 0;
   }

   @Override
   final boolean init() {
      this._audioRouter = AudioRouter.getInstance();
      AudioRouter.addListener(super._btManager, this);
      VADUserEvents.addListener(super._btManager, this);
      this._ringRunnable = new BluetoothPhoneManager$RingRunnable(this);
      this._networkType = RadioInfo.getNetworkType();
      if (this._networkType != 3 && this._networkType != 7) {
         this._addressBookEncoding = 0;
      } else {
         this._addressBookEncoding = 1;
      }

      HeadsetGateway.addListener(super._btManager, this);
      HandsfreeGateway.addListener(super._btManager, this);
      super._btManager.addRadioListener(this);
      this._phone = Phone.getInstance();
      this._callInfoArray = new CallInfo[0];
      VerbFactoryRepository.addFactory(-5280468186386428176L, this);
      if (this._networkType == 4) {
         super._btManager.addGlobalEventListener(this);
      }

      return true;
   }

   @Override
   final boolean canConnect(BluetoothDevice device) {
      return super._state == 0 && (device.hasHandsfree() || device.hasHeadset());
   }

   @Override
   final boolean isConnected(BluetoothDevice device) {
      return super._device == device;
   }

   @Override
   final synchronized int connect(BluetoothDevice device) {
      if (!BluetoothME.isPowerOn()) {
         return 4;
      } else if (super._state != 0) {
         return 3;
      } else if (device.hasHandsfree() && HandsfreeGateway.isEnabled()) {
         return this.connect(device, true);
      } else if (device.hasHeadset()) {
         return HeadsetGateway.isEnabled() ? this.connect(device, false) : 2;
      } else {
         return device.hasHandsfree() ? 2 : 1;
      }
   }

   @Override
   final synchronized void disconnect(BluetoothDevice device) {
      if (device == super._device) {
         int previousState = super._state;
         this.updateState(3);
         if (this._scoState == 2) {
            this.disconnectSCO();
         } else {
            this._sniffModeDesired = false;
            super._device.updateSniffMode();
            int rc;
            if (this._isHandsfree) {
               rc = HandsfreeGateway.disconnect();
            } else {
               rc = HeadsetGateway.disconnect();
            }

            this.logResult(1146224640, rc);
            switch (rc) {
               case 0:
                  this.disconnected();
                  return;
               default:
                  this.updateState(previousState);
               case 2:
            }
         }
      }
   }

   @Override
   final void cleanup() {
      super.cleanup();
      if (super._device != null) {
         this.disconnected();
      }
   }

   @Override
   final boolean sniffModeDesired() {
      return this._sniffModeDesired;
   }

   @Override
   final void devicePropertiesUpdated(BluetoothDevice device) {
      if (super._device == device) {
         this.updateSinkProperties(true);
      }
   }

   @Override
   final void updateBatteryIndicators(int bstat) {
      if (this.canUpdateIndicators()) {
         int batteryLevel = DeviceInfo.getBatteryLevel() / 20;
         boolean charging = (bstat & 1) != 0;
         boolean lowBattery = (bstat & -1878999040) != 0;
         if (this._lastBatteryLevel != batteryLevel) {
            this._lastBatteryLevel = batteryLevel;
            HandsfreeGateway.sendIndicatorUpdate(4, batteryLevel);
         }

         if (this._lastCharging != charging) {
            this._lastCharging = charging;
            HandsfreeGateway.sendIndicatorUpdate(7, charging ? 1 : 0);
         }

         if (this._lastLowBattery != lowBattery) {
            this._lastLowBattery = lowBattery;
            HandsfreeGateway.sendIndicatorUpdate(6, lowBattery ? 1 : 0);
         }
      }
   }

   @Override
   final String getName() {
      return HandsfreeGateway.isEnabled() && this._isHandsfree ? BluetoothMainScreen.getString(38) : BluetoothMainScreen.getString(37);
   }

   private final boolean isNRECEnabled() {
      if (HandsfreeGateway.isNRECSupported() && super._state == 2) {
         switch (super._device.getNRECMode()) {
            case 0:
               if (this._nrecDisabledByHF) {
                  return false;
               }
            default:
               return true;
            case 2:
               return false;
         }
      } else {
         return false;
      }
   }

   private final void updateSinkProperties(boolean enabled) {
      String name = null;
      if (enabled) {
         name = super._device.getName();
      }

      this._audioRouter.setBluetoothSCOSinkProperties(enabled, this._remoteVolumeControl, this.isNRECEnabled(), name, this._hasVoiceRecognition);
   }

   private final int connect(BluetoothDevice device, boolean isHandsfree) {
      this.updateState(1, device);
      byte[] address = device.getAddress();
      int pageScanInfo = device.getPageScanInfo();
      this._isHandsfree = isHandsfree;
      int rc;
      if (isHandsfree) {
         this.log(1212564306);
         rc = HandsfreeGateway.connect(address, pageScanInfo);
      } else {
         this.log(1213416274);
         rc = HeadsetGateway.connect(address, pageScanInfo);
      }

      this._deviceToReconnect = null;
      this._nextAnswerState = 0;
      this._nextHoldState = 0;
      this._delayedDialingNumber = null;
      this._remoteVolumeControl = true;
      this._eventReporting = true;
      this._nrecDisabledByHF = false;
      this._hasVoiceRecognition = true;
      if (rc != 0 && rc != 2) {
         this.connected(rc);
         return 1;
      } else {
         return 0;
      }
   }

   private final boolean isIncomingCallInProgress() {
      return this.getCallID(16) != 0;
   }

   private final boolean isCallInProgress() {
      return this.getCallID(15) != 0;
   }

   private final boolean isDCCallInProgress() {
      if (DirectConnect.isSupported()) {
         try {
            return DirectConnect.getActiveCallType() != 0;
         } finally {
            this.log(542262616);
            return false;
         }
      } else {
         return false;
      }
   }

   private final synchronized void connected(int result) {
      if (result == 0) {
         this._remoteVolume = -1;
         this.updateState(2);
         this.updateSinkProperties(true);
         if (this.isCallInProgress() || this.isDCCallInProgress()) {
            this.connectSCO();
         } else if (this.isIncomingCallInProgress()) {
            super._btManager.invokeLater(this._ringRunnable);
         } else {
            this._sniffModeDesired = true;
            super._device.updateSniffMode();
         }
      } else {
         this.updateState(0);
         this._deviceToReconnect = null;
      }
   }

   private final void disconnected() {
      synchronized (this) {
         if (super._state != 0) {
            if (this._isHandsfree && this.isCallInProgress()) {
               this._deviceToReconnect = super._device;
            } else {
               this._deviceToReconnect = null;
            }

            if (this._activateVADAfterSCOConnect) {
               VADUserEvents.sendEvent(2, 3);
               this._activateVADAfterSCOConnect = false;
            }

            this.updateState(0);
            this._scoState = 0;
            this._extendedErrors = false;
            this._addressBookCards = null;
            this._addressBookNumbers = null;
            this.updateSinkProperties(false);
         }
      }
   }

   private final void scoConnected(int result) {
      synchronized (this) {
         if (result != 0) {
            if (this._audioRouter.getSink() == 2) {
               this._audioRouter.resetSink();
            }

            this._scoState = 0;
            this._activateVADAfterSCOConnect = false;
            this.log(1396920134);
            return;
         }

         int previousState = this._scoState;
         this.log(1396920149);
         this._scoState = 2;
         if (previousState == 3) {
            this.disconnectSCO();
            return;
         }
      }

      if (this._audioRouter.getSink() != 2) {
         this._audioRouter.setSink(2);
      }

      this._sniffModeDesired = false;
      super._device.updateSniffMode();
      if (this._activateVADAfterSCOConnect) {
         VADUserEvents.sendEvent(0, 2);
         this._activateVADAfterSCOConnect = false;
      }

      if (this._remoteVolume != -1) {
         this._audioRouter.setMasterVolume(this.convertVolumeToPercentage(this._remoteVolume), true);
      }
   }

   private final void scoDisconnected() {
      synchronized (this) {
         this.log(1396920132);
         this._scoState = 0;
         if (super._state == 3) {
            this.disconnect(super._device);
         }
      }

      if (this._audioRouter.getSink() == 2 && !this._vadEnabledOnHF) {
         this._audioRouter.setSink(0);
      }

      if (this._vadEnabledOnHF) {
         VADUserEvents.sendEvent(2, 3);
      }

      this._sniffModeDesired = true;
      super._device.updateSniffMode();
   }

   private final synchronized boolean answer() {
      int call = this.getCallID(48);
      if (call != 0) {
         this._nextAnswerState = this.doHoldAction(1, 0);
         if (this._nextAnswerState == 0) {
            this.updateCallIndicator();
            this.updateCallSetupIndicator();
            this.updateCallHeldIndicator(false);
         }

         this.connectSCO();
         return true;
      } else {
         return false;
      }
   }

   private final void stopCall(int callID) {
      switch (this._phone.getCallState(callID)) {
         case 4:
            this._phone.stopCall(callID);
            return;
         case 5:
         case 6:
         default:
            this._phone.stopAllCalls(false);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean hangup() {
      boolean rc = false;
      boolean var5 = false /* VF: Semaphore variable */;

      try {
         var5 = true;
         int ex = this.getCallID(13);
         int incomingCall = this.getCallID(48);
         if (incomingCall != 0 && ex == 0) {
            this._phone.rejectCall(incomingCall);
            VoiceServices.postEvent(2001, incomingCall, null);
            rc = true;
         }

         if (ex != 0) {
            this.stopCall(ex);
            return true;
         }

         var5 = false;
      } finally {
         if (var5) {
            this.log(542262616);
            return rc;
         }
      }

      return rc;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean startCall(Object obj) {
      if (!super._btManager.allowOutgoingCalls()) {
         return false;
      }

      if (this.isDCCallInProgress()) {
         return false;
      }

      int service = RadioInfo.getNetworkService();
      if ((service & 3) == 0) {
         return false;
      }

      boolean var11 = false /* VF: Semaphore variable */;

      try {
         var11 = true;
         int ex = this.getCallID(13);
         boolean flash = false;
         if (ex != 0) {
            if (this._networkType != 4) {
               this._delayedDialingNumber = obj;
               this._phone.holdCall();
               return true;
            }

            flash = true;
         }

         char[] bytes;
         if (!(obj instanceof String)) {
            String number = (String)ContextObject.get(obj, 247);
            if (number == null) {
               return false;
            }

            bytes = number.toCharArray();
         } else {
            String number = (String)obj;
            bytes = number.toCharArray();

            for (int i = bytes.length - 1; i >= 0; i--) {
               if (bytes[i] == 'p') {
                  bytes[i] = ',';
               }
            }
         }

         StringBuffer sb = new StringBuffer();
         StringBuffer dtmf = new StringBuffer();
         PhoneNumberConverter.convertForTransmission(sb, dtmf, bytes, true, false, false, false, false);
         String var14 = sb.toString();
         if (obj instanceof String) {
            obj = sb.append(dtmf).toString();
         } else if (obj instanceof ContextObject && dtmf.length() > 0) {
            ContextObject.put(obj, 247, sb.append(dtmf).toString());
         }

         if (flash) {
            if ((this._phone.getNetworkFeatures() & 128) == 0) {
               this._phone.flash(null);
            }

            this._phone.flash(var14);
            return true;
         }

         int callID = this._phone.startCall(var14);
         if (callID != 0) {
            VoiceServices.postEvent(1100, callID, obj);
            return true;
         }

         var11 = false;
      } finally {
         if (var11) {
            this.log(542262616);
            return false;
         }
      }

      return false;
   }

   private final boolean redial() {
      Object redialInfo = VoiceServices.getVoiceApplication().getRedialInfo();
      return redialInfo == null ? false : this.startCall(redialInfo);
   }

   private final int getRSSI(int signal) {
      if (signal == -256) {
         return 0;
      } else if (signal >= -77) {
         return 5;
      } else if (signal >= -86) {
         return 4;
      } else if (signal >= -92) {
         return 3;
      } else {
         return signal >= -101 ? 2 : 1;
      }
   }

   private final boolean canUpdateIndicators() {
      return super._device != null && this._isHandsfree && super._state == 2 && this._eventReporting;
   }

   private final void updateServiceIndicator(int service) {
      if (this.canUpdateIndicators()) {
         boolean hasService = (service & 2) != 0;
         if (this._lastService != hasService) {
            this._lastService = hasService;
            HandsfreeGateway.sendIndicatorUpdate(1, hasService ? 1 : 0);
         }

         boolean roaming = (service & 8) != 0;
         if (this._lastRoaming != roaming) {
            this._lastRoaming = roaming;
            HandsfreeGateway.sendIndicatorUpdate(9, roaming ? 1 : 0);
         }
      }
   }

   private final synchronized void updateCallIndicator() {
      if (this.canUpdateIndicators()) {
         boolean value = this.haveCallStatus(3);
         if (this._lastCall != value) {
            this._lastCall = value;
            HandsfreeGateway.sendIndicatorUpdate(2, value ? 1 : 0);
         }
      }
   }

   private final synchronized void updateCallSetupIndicator() {
      if (this.canUpdateIndicators()) {
         int value = 0;
         if (this.haveCallStatus(48)) {
            value = 1;
         } else if (this.haveCallStatus(4)) {
            value = 2;
         } else if (this.haveCallStatus(8)) {
            value = 3;
         }

         if (this._lastCallSetup != value) {
            this._lastCallSetup = value;
            HandsfreeGateway.sendIndicatorUpdate(3, value);
         }

         if (value == 2) {
            this.connectSCO();
         }
      }
   }

   private final synchronized void updateCallHeldIndicator(boolean force) {
      if (this.canUpdateIndicators() && this._hasCallHeldIndicator) {
         int numCalls = this._callInfoArray.length;
         int numHeldCalls = 0;
         int numActiveOrWaitingCalls = 0;

         for (int i = 0; i < numCalls; i++) {
            if (this._callInfoArray[i] != null) {
               if ((this._callInfoArray[i]._status & 37) != 0) {
                  numActiveOrWaitingCalls++;
               } else if ((this._callInfoArray[i]._status & 2) != 0) {
                  numHeldCalls++;
               }
            }
         }

         int value = 0;
         if (numHeldCalls <= 1 && (numHeldCalls <= 0 || numActiveOrWaitingCalls <= 0)) {
            if (numHeldCalls > 0) {
               value = 2;
            }
         } else {
            value = 1;
         }

         if (this._lastCallHeld != value || force) {
            this._lastCallHeld = value;
            HandsfreeGateway.sendIndicatorUpdate(8, value);
         }
      }
   }

   private final synchronized CallInfo getCallInfo(int callID) {
      int numCalls = this._callInfoArray.length;

      for (int i = 0; i < numCalls; i++) {
         if (this._callInfoArray[i] != null && this._callInfoArray[i]._callID == callID) {
            return this._callInfoArray[i];
         }
      }

      CallInfo info = new CallInfo(callID);

      for (int i = 0; i < numCalls; i++) {
         if (this._callInfoArray[i] == null) {
            this._callInfoArray[i] = info;
            return info;
         }
      }

      Arrays.add(this._callInfoArray, info);
      return info;
   }

   private final synchronized CallInfo getCallInfoByIndex(int index) {
      if (index == 0) {
         return null;
      }

      index--;
      return index >= this._callInfoArray.length ? null : this._callInfoArray[index];
   }

   private final synchronized void removeCallInfo(int callID) {
      int numCalls = this._callInfoArray.length;

      for (int i = 0; i < numCalls; i++) {
         if (this._callInfoArray[i] != null && this._callInfoArray[i]._callID == callID) {
            this._callInfoArray[i] = null;
         }
      }
   }

   private final void updateCallInfo(int callID, int status, boolean outgoing) {
      CallInfo info = this.getCallInfo(callID);
      if (this._networkType == 4) {
         info = this.cdmaCLCCKludge(info, callID, status);
      }

      info._status = status;
      info._outgoing = outgoing;
   }

   private final void updateCallInfo(int callID, int status) {
      CallInfo info = this.getCallInfo(callID);
      if (this._networkType == 4) {
         info = this.cdmaCLCCKludge(info, callID, status);
      }

      info._status = status;
   }

   private final synchronized CallInfo cdmaCLCCKludge(CallInfo info, int callID, int status) {
      switch (status) {
         case 1:
         case 16:
            try {
               info._number = this._phone.getCallPhoneNumber(callID);
               return info;
            } finally {
               ;
            }
         case 32:
            info = new CallInfo(callID);

            label63:
            try {
               info._number = this._phone.getCallPhoneNumber(callID);
            } finally {
               break label63;
            }

            if (this._callInfoArray.length == 1 && this._callInfoArray[0] != null) {
               Arrays.add(this._callInfoArray, info);
               return info;
            } else if (this._callInfoArray.length == 2 && this._callInfoArray[1] == null) {
               this._callInfoArray[1] = info;
               return info;
            } else {
               System.out.println("CDMA CLCC state error");
            }
         default:
            return info;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final synchronized void flash() {
      boolean var3 = false /* VF: Semaphore variable */;

      label17:
      try {
         var3 = true;
         this._phone.flash(null);
         var3 = false;
      } finally {
         if (var3) {
            this.log(542262616);
            break label17;
         }
      }

      VoiceServices.postEvent(3006, 0, null);
   }

   private final synchronized int getCallID(int statusMask) {
      int numCalls = this._callInfoArray.length;

      for (int i = 0; i < numCalls; i++) {
         if (this._callInfoArray[i] != null && (this._callInfoArray[i]._status & statusMask) != 0) {
            return this._callInfoArray[i]._callID;
         }
      }

      return 0;
   }

   private final boolean haveCallStatus(int statusMask) {
      return this.getCallID(statusMask) != 0;
   }

   private final synchronized void connectSCO() {
      if (this._scoState == 0 && !Audio.isHeadsetConnected()) {
         this._sniffModeDesired = false;
         super._device.updateSniffMode();
         this.log(1396921682);
         int rc;
         if (this._isHandsfree) {
            rc = HandsfreeGateway.enableAudio(true);
         } else {
            rc = HeadsetGateway.enableAudio(true);
         }

         if (rc == 2) {
            this._scoState = 1;
            return;
         }

         this.log(1396920134);
         if (this._audioRouter.getSink() == 2) {
            this._audioRouter.setSink(0);
         }
      }
   }

   private final synchronized void disconnectSCO() {
      this.log(1396917330);
      int rc;
      if (this._isHandsfree) {
         rc = HandsfreeGateway.enableAudio(false);
      } else {
         rc = HeadsetGateway.enableAudio(false);
      }

      switch (rc) {
         case 11:
            this._scoState = 3;
            return;
         default:
            this.scoDisconnected();
         case 2:
         case 19:
      }
   }

   private final int convertVolumeToPercentage(int volume) {
      return volume * 100 / 15;
   }

   private final int convertPercentageToVolume(int percentage) {
      return percentage * 15 / 100;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int doHoldAction(int holdAction, int index) {
      int activeCall = this.getCallID(13);
      int heldCall = this.getCallID(2);
      int incomingCall = this.getCallID(48);
      boolean hasActiveCall = activeCall != 0;
      boolean hasHeldCall = heldCall != 0;
      boolean hasIncomingCall = incomingCall != 0;
      int nextState = 0;
      CallInfo info = null;
      boolean var13 = false /* VF: Semaphore variable */;

      try {
         var13 = true;
         switch (holdAction) {
            case -1:
               var13 = false;
               break;
            case 0:
            default:
               if (hasIncomingCall) {
                  this._phone.rejectCall(incomingCall);
                  VoiceServices.postEvent(2001, incomingCall, null);
                  nextState = 3;
                  var13 = false;
               } else if (!hasHeldCall) {
                  var13 = false;
               } else {
                  this._phone.stopCall(heldCall);
                  nextState = 3;
                  var13 = false;
               }
               break;
            case 1:
               info = this.getCallInfoByIndex(index);
               if (info != null && info._callID != activeCall) {
                  this._phone.stopCall(info._callID);
                  nextState = 3;
                  var13 = false;
               } else if (hasActiveCall && !hasHeldCall && !hasIncomingCall) {
                  this.stopCall(activeCall);
                  nextState = 3;
                  var13 = false;
               } else if (!hasActiveCall && hasHeldCall && !hasIncomingCall) {
                  this._phone.resumeCall();
                  nextState = 3;
                  var13 = false;
               } else if (!hasActiveCall && !hasHeldCall && hasIncomingCall) {
                  this._phone.answerCall(incomingCall);
                  nextState = 3;
                  var13 = false;
               } else if (hasActiveCall && hasHeldCall && !hasIncomingCall) {
                  this.stopCall(activeCall);
                  nextState = 2;
                  var13 = false;
               } else if (hasActiveCall && !hasHeldCall && hasIncomingCall) {
                  if (this._networkType == 4) {
                     this.flash();
                     nextState = 0;
                     var13 = false;
                  } else {
                     this.stopCall(activeCall);
                     nextState = 1;
                     var13 = false;
                  }
               } else if (hasActiveCall) {
                  var13 = false;
               } else if (!hasHeldCall) {
                  var13 = false;
               } else if (!hasIncomingCall) {
                  var13 = false;
               } else {
                  this._phone.answerCall(incomingCall);
                  nextState = 3;
                  var13 = false;
               }
               break;
            case 2:
               info = this.getCallInfoByIndex(index);
               if (info != null && info.isActive()) {
                  int state = this._phone.getCallState(info._callID);
                  if (state != 5 && state != 6) {
                     var13 = false;
                  } else {
                     this._phone.removeCallFromConference(info._callID);
                     nextState = 3;
                     var13 = false;
                  }
               } else if (hasActiveCall && !hasHeldCall && !hasIncomingCall) {
                  if (this._networkType == 4) {
                     this.flash();
                     nextState = 0;
                     var13 = false;
                  } else {
                     this._phone.holdCall();
                     nextState = 3;
                     var13 = false;
                  }
               } else if (!hasActiveCall && hasHeldCall && !hasIncomingCall) {
                  if (this._networkType == 4) {
                     this.flash();
                     nextState = 0;
                     var13 = false;
                  } else {
                     this._phone.resumeCall();
                     nextState = 3;
                     var13 = false;
                  }
               } else if (!hasActiveCall && !hasHeldCall && hasIncomingCall) {
                  this._phone.answerCall(incomingCall);
                  nextState = 3;
                  var13 = false;
               } else if (hasActiveCall && hasHeldCall && !hasIncomingCall) {
                  if (this._networkType == 4) {
                     this.flash();
                     nextState = 0;
                     var13 = false;
                  } else {
                     this._phone.swapCalls();
                     nextState = 5;
                     var13 = false;
                  }
               } else if (hasActiveCall && !hasHeldCall && hasIncomingCall) {
                  if (this._networkType == 5) {
                     this._phone.answerCall(incomingCall);
                     nextState = 3;
                     var13 = false;
                  } else if (this._networkType == 4) {
                     this.flash();
                     nextState = 0;
                     var13 = false;
                  } else {
                     this._phone.holdCall();
                     nextState = 1;
                     var13 = false;
                  }
               } else if (!hasActiveCall) {
                  if (hasHeldCall) {
                     if (hasIncomingCall) {
                        this._phone.answerCall(incomingCall);
                        nextState = 3;
                        var13 = false;
                     } else {
                        var13 = false;
                     }
                  } else {
                     var13 = false;
                  }
               } else {
                  var13 = false;
               }
               break;
            case 3:
               if (this._networkType == 4) {
                  nextState = 4;
                  var13 = false;
               } else if (hasActiveCall) {
                  if (hasHeldCall) {
                     this._phone.addCallToConference();
                     nextState = 3;
                     var13 = false;
                  } else {
                     var13 = false;
                  }
               } else {
                  var13 = false;
               }
               break;
            case 4:
               if (this._networkType != 3 && this._networkType != 7) {
                  nextState = 4;
                  var13 = false;
               } else if (hasActiveCall && hasHeldCall) {
                  this._phone.transferCall();
                  nextState = 3;
                  var13 = false;
               } else {
                  nextState = 4;
                  var13 = false;
               }
         }
      } finally {
         if (var13) {
            this.log(542262616);
            int nextStatex = 4;
            return nextStatex;
         }
      }

      return nextState;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean doNextHoldAction(boolean success) {
      int heldCall = this.getCallID(2);
      int incomingCall = this.getCallID(48);
      boolean rc = false;
      boolean forceCallHeldUpdate = false;
      if (this._nextHoldState != 0) {
         if (!success) {
            this.sendError();
            this._nextHoldState = 0;
         }

         boolean var8 = false /* VF: Semaphore variable */;

         label48:
         try {
            var8 = true;
            switch (this._nextHoldState) {
               case 0:
               case 4:
                  var8 = false;
                  break;
               case 1:
                  if (incomingCall != 0) {
                     this._phone.answerCall(incomingCall);
                     this._nextHoldState = 3;
                     var8 = false;
                  } else {
                     this.sendOK();
                     this._nextHoldState = 0;
                     var8 = false;
                  }
                  break;
               case 2:
                  if (heldCall != 0) {
                     this._phone.resumeCall();
                     this._nextHoldState = 3;
                     var8 = false;
                  } else {
                     this.sendOK();
                     this._nextHoldState = 0;
                     var8 = false;
                  }
                  break;
               case 5:
               default:
                  forceCallHeldUpdate = true;
               case 3:
                  this.sendOK();
                  this._nextHoldState = 0;
                  var8 = false;
            }
         } finally {
            if (var8) {
               this.log(542262616);
               this._nextHoldState = 0;
               break label48;
            }
         }

         rc = true;
      }

      if (this._nextHoldState == 0) {
         this.updateCallIndicator();
         this.updateCallSetupIndicator();
         this.updateCallHeldIndicator(forceCallHeldUpdate);
      }

      return rc;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean doNextAnswerAction() {
      if (this._nextAnswerState == 1) {
         boolean var3 = false /* VF: Semaphore variable */;

         label24:
         try {
            var3 = true;
            int ex = this.getCallID(48);
            if (ex != 0) {
               this._phone.answerCall(ex);
               var3 = false;
            } else {
               var3 = false;
            }
         } finally {
            if (var3) {
               this.log(542262616);
               break label24;
            }
         }

         this._nextAnswerState = 0;
         this.updateCallIndicator();
         this.updateCallSetupIndicator();
         this.updateCallHeldIndicator(false);
         return true;
      } else {
         return false;
      }
   }

   private final int getNumberType(String number) {
      return number != null && number.length() != 0 && number.charAt(0) == 43 ? 145 : 129;
   }

   private final byte[] getNumberTypeAsBytes(String number) {
      switch (this.getNumberType(number)) {
         case 145:
            return BYTE_145;
         default:
            return BYTE_129;
      }
   }

   @Override
   public final synchronized void handsfreeIncomingConnection(byte[] address) {
      BluetoothDevice device = super._btManager.getPairedDevice(address);
      if (device != null && super._device == null && HandsfreeGateway.isEnabled()) {
         device.addSupportedProfile(1);
         this._deviceToReconnect = null;
         this._isHandsfree = true;
         this.updateState(1, device);
         this._remoteVolumeControl = true;
         this._eventReporting = true;
         this._nrecDisabledByHF = false;
         this._hasVoiceRecognition = false;
      } else {
         HandsfreeGateway.disconnect();
      }
   }

   @Override
   public final void handsfreeConnected(int result) {
      if (HandsfreeGateway.isEnabled() && super._device != null) {
         this.connected(result);
      } else {
         HandsfreeGateway.disconnect();
      }
   }

   @Override
   public final void handsfreeDisconnected() {
      this.disconnected();
   }

   @Override
   public final void handsfreeAudioConnected(int result) {
      this.scoConnected(result);
   }

   @Override
   public final void handsfreeAudioDisconnected() {
      this.scoDisconnected();
   }

   @Override
   public final void handsfreeAnswerCall() {
      this.answer();
   }

   @Override
   public final void handsfreeHangupCall() {
      this.hangup();
   }

   @Override
   public final void handsfreeHoldCall(int holdAction, int index) {
      this._nextHoldState = this.doHoldAction(holdAction, index);
      switch (this._nextHoldState) {
         case 0:
            this.sendOK();
            break;
         case 4:
            this.sendError();
            break;
         default:
            return;
      }

      this.updateCallIndicator();
      this.updateCallSetupIndicator();
      this.updateCallHeldIndicator(false);
   }

   @Override
   public final void handsfreeSpeakerVolumeChange(int volume) {
      this._remoteVolume = volume;
      if (this._audioRouter.getSink() == 2) {
         this._audioRouter.setMasterVolume(this.convertVolumeToPercentage(volume), true);
      }
   }

   @Override
   public final void handsfreeDialNumber(String number) {
      if (this.startCall(number)) {
         this.sendOK();
      } else {
         this.sendError();
      }
   }

   @Override
   public final void handsfreeDialMemory(String memory) {
      if (memory.length() != 0) {
         char c = memory.charAt(0);
         if (!InternalServices.isReducedFormFactor() && Character.isDigit(c)) {
            SLKeyLayout layout = Keypad.getLayout();
            if (layout != null) {
               int keycode = layout.getOriginalKeyCode(c, SLKeyLayout.convertStatusToModifiers(1));
               StringBuffer sb = layout.getKeyChars(keycode, 0);
               char newChar = Character.toUpperCase(sb.charAt(0));
               if (newChar >= 'A' && newChar <= 'Z') {
                  c = newChar;
               }
            }
         }

         Object number = VoiceServices.getVoiceApplication().getSpeedDialInfo(c, true);
         if (number != null && this.startCall(number)) {
            this.sendOK();
            return;
         }
      }

      this.sendError();
   }

   @Override
   public final void handsfreeRedial() {
      if (this.redial()) {
         this.sendOK();
      } else {
         this.sendError();
      }
   }

   @Override
   public final void handsfreeEnableEventReporting(boolean enable) {
      this._eventReporting = enable;
   }

   @Override
   public final void handsfreeEnableCallWaitingReporting(boolean enable) {
      this._callWaitingReporting = enable;
   }

   @Override
   public final void handsfreeEnableCallerIDReporting(boolean enable) {
      this._callerIDReporting = enable;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void handsfreeSendDTMF(int dtmf) {
      int callID = this.getCallID(13);
      if (callID == 0) {
         callID = this.getCallID(2);
      }

      if (callID == 0) {
         this.sendError();
      } else {
         boolean var33 = false /* VF: Semaphore variable */;

         int audioTone;
         try {
            var33 = true;
            audioTone = AudioInternal.mapDTMFToneToAudioTone((byte)dtmf);
            var33 = false;
         } finally {
            if (var33) {
               this.sendError();
               return;
            }
         }

         int i;
         for (i = 0; i < 5; i++) {
            boolean var27 = false /* VF: Semaphore variable */;

            try {
               var27 = true;
               this._phone.startDTMF(callID, (byte)dtmf);
               var27 = false;
               break;
            } finally {
               if (var27) {
                  try {
                     Thread.currentThread();
                     Thread.sleep(300);
                     continue;
                  } finally {
                     continue;
                  }
               }
            }
         }

         if (i == 5) {
            this.sendError();
            this.log(542262616);
         } else {
            AudioInternal.startTone(audioTone);

            label204:
            try {
               Thread.currentThread();
               Thread.sleep(300);
            } finally {
               break label204;
            }

            label201:
            try {
               this._phone.stopDTMF(callID);
            } finally {
               break label201;
            }

            AudioInternal.stopTone();
            this.sendOK();
         }
      }
   }

   @Override
   public final void handsfreeFeatures(int features) {
      this._callWaitingReporting = (features & 2) != 0;
      this._callerIDReporting = (features & 4) != 0;
      this._remoteVolumeControl = (features & 16) != 0;
      this._hasVoiceRecognition = (features & 8) != 0;
      if ((features & 1) == 0) {
         this.log(1313817923);
      }
   }

   @Override
   public final void handsfreeSendIndicators() {
      int service = RadioInfo.getNetworkService();
      int bstat = DeviceInfo.getBatteryStatus();
      this._lastService = (service & 2) != 0;
      this._lastCall = this.haveCallStatus(3);
      this._lastCallSetup = 0;
      if (this.haveCallStatus(48)) {
         this._lastCallSetup = 1;
      } else if (this.haveCallStatus(4)) {
         this._lastCallSetup = 2;
      } else if (this.haveCallStatus(8)) {
         this._lastCallSetup = 3;
      }

      this._lastCallHeld = 0;
      if (this._hasCallHeldIndicator && this.haveCallStatus(2)) {
         if (this.haveCallStatus(49)) {
            this._lastCallHeld = 1;
         } else {
            this._lastCallHeld = 2;
         }
      }

      this._lastBatteryLevel = DeviceInfo.getBatteryLevel() / 20;
      this._lastRSSI = this.getRSSI(RadioInfo.getSignalLevel());
      this._lastLowBattery = (bstat & -1878999040) != 0;
      this._lastCharging = (bstat & 1) != 0;
      this._lastRoaming = (service & 8) != 0;
      StringBuffer sb = new StringBuffer();
      sb.append("+CIND: ");
      sb.append(this._lastService ? 1 : 0);
      sb.append(',');
      sb.append(this._lastCall ? 1 : 0);
      sb.append(',');
      sb.append(this._lastCallSetup);
      sb.append(',');
      sb.append(this._lastBatteryLevel);
      sb.append(',');
      sb.append(this._lastRSSI);
      sb.append(',');
      sb.append(this._lastLowBattery ? 1 : 0);
      sb.append(',');
      sb.append(this._lastCharging ? 1 : 0);
      sb.append(',');
      sb.append(this._lastCallHeld);
      sb.append(',');
      sb.append(this._lastRoaming ? 1 : 0);
      this.sendRawData(sb);
      this.sendOK();
   }

   @Override
   public final void handsfreeSendHoldInfo() {
      String holdInfo;
      if (this._networkType != 3 && this._networkType != 7) {
         holdInfo = "(0,1,2)";
      } else {
         holdInfo = "(0,1,2,3,4)";
      }

      HandsfreeGateway.sendHoldInfo(holdInfo);
   }

   @Override
   public final void handsfreeVoiceRecog(boolean enabled) {
      if (enabled) {
         if (!super._btManager.allowOutgoingCalls()) {
            this.sendError();
            return;
         }

         if (this._scoState == 2) {
            VADUserEvents.sendEvent(0, 2);
         } else {
            VADUserEvents.sendEvent(0, 1);
            this._activateVADAfterSCOConnect = true;
         }
      } else {
         VADUserEvents.sendEvent(2, 3);
      }

      this._vadEnabledOnHF = enabled;
      this.sendOK();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void handsfreeUnknownATData(String data) {
      if (!data.startsWith("AT+")) {
         this.sendError(4);
      } else {
         data = data.substring(3);
         StringBuffer sb = new StringBuffer();
         if (data.startsWith("CPBS=?")) {
            this.sendRawData(RESPONSE_CPBS);
            this.sendOK();
         } else if (data.startsWith("CPBS=")) {
            String book = this.getQuotedString(data);
            if (book != null) {
               if (book.equals("ME")) {
                  this._currentPhonebook = 0;
                  this.sendOK();
               } else if (book.equals("SM")) {
                  this._currentPhonebook = 1;
                  this.sendOK();
               } else if (book.equals("DC")) {
                  this._currentPhonebook = 2;
                  this.sendOK();
               } else if (book.equals("MC")) {
                  this._currentPhonebook = 3;
                  this.sendOK();
               } else if (book.equals("RC")) {
                  this._currentPhonebook = 4;
                  this.sendOK();
               } else {
                  this.sendError(4);
               }

               this._addressBookCards = null;
               this._addressBookNumbers = null;
            } else {
               this.sendError(4);
            }
         } else if (data.startsWith("CPBS?")) {
            String pb = null;
            switch (this._currentPhonebook) {
               case -1:
                  break;
               case 0:
               default:
                  pb = "ME";
                  break;
               case 1:
                  pb = "SM";
                  break;
               case 2:
                  pb = "DC";
                  break;
               case 3:
                  pb = "MC";
                  break;
               case 4:
                  pb = "RC";
            }

            sb.append("+CPBS: \"");
            sb.append(pb);
            sb.append('"');
            this.sendRawData(sb);
            this.sendOK();
         } else if (data.startsWith("CPBR=?")) {
            if (this._addressBookCards == null) {
               this.snapshotAddressBook();
            }

            if (this._addressBookCards == null) {
               this.sendError(3);
            } else {
               int length = this._addressBookCards.length;
               sb.append("+CPBR: (");
               sb.append(length == 0 ? 0 : 1);
               sb.append('-');
               sb.append(length);
               sb.append("),");
               sb.append(40);
               sb.append(',');
               sb.append(24);
               this.sendRawData(sb);
               this.sendOK();
            }
         } else {
            if (!data.startsWith("CPBR=")) {
               if (data.startsWith("CSCS=?")) {
                  this.sendRawData(RESPONSE_CSCS);
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CSCS=")) {
                  String encoding = this.getQuotedString(data);
                  if (encoding == null) {
                     this.sendError(25);
                     return;
                  }

                  if (!encoding.equals("ASCII") && !encoding.equals("8859-1")) {
                     if (encoding.equals("GSM")) {
                        this._addressBookEncoding = 1;
                        this.sendOK();
                        return;
                     }

                     if (encoding.equals("UTF-8")) {
                        this._addressBookEncoding = 2;
                        this.sendOK();
                        return;
                     }

                     if (encoding.equals("UCS-2")) {
                        this._addressBookEncoding = 3;
                        this.sendOK();
                        return;
                     }

                     this.sendError(4);
                     return;
                  }

                  this._addressBookEncoding = 0;
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CSCS?")) {
                  String s;
                  switch (this._addressBookEncoding) {
                     case 0:
                        s = "ASCII";
                        break;
                     case 1:
                     default:
                        s = "GSM";
                        break;
                     case 2:
                        s = "UTF-8";
                        break;
                     case 3:
                        s = "UCS-2";
                  }

                  sb.append("+CSCS: \"");
                  sb.append(s);
                  sb.append('"');
                  this.sendRawData(sb);
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CGMI=?")) {
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CGMI")) {
                  sb.append("Research In Motion");
                  this.sendRawData(sb);
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CGMM=?")) {
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CGMM")) {
                  sb.append("BlackBerry ");
                  sb.append(DeviceInfo.getDeviceName());
                  this.sendRawData(sb);
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CGSN=?")) {
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CGSN")) {
                  if (this._networkType != 3 && this._networkType != 7) {
                     this.sendError(4);
                     return;
                  }

                  sb.append(GPRSInfo.imeiToString(GPRSInfo.getIMEI(), false));
                  this.sendRawData(sb);
                  this.sendOK();
                  return;
               }

               if (data.startsWith("COPS=3,0")) {
                  this.sendOK();
                  return;
               }

               if (data.startsWith("COPS?")) {
                  String name = null;
                  RibbonNetworkInfo ni = RibbonNetworkInfo.getInstance();
                  if (ni != null) {
                     name = ni.getOperatorName();
                  }

                  if (name == null || name.length() == 0) {
                     name = RadioInfo.getCurrentNetworkName();
                  }

                  sb.append("+COPS: 0");
                  if (name != null) {
                     sb.append(",0,\"");
                     sb.append(name);
                     sb.append('"');
                  }

                  this.sendRawData(sb);
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CNUM")) {
                  String phoneNumber = null;

                  label505:
                  try {
                     phoneNumber = this._phone.getNumber(0);
                  } finally {
                     break label505;
                  }

                  if (phoneNumber != null) {
                     sb.append("+CNUM: ,\"");
                     sb.append(phoneNumber);
                     sb.append("\",");
                     sb.append(this.getNumberType(phoneNumber));
                     sb.append(",,4");
                     this.sendRawData(sb);
                  }

                  this.sendOK();
                  return;
               }

               if (data.startsWith("CLCC")) {
                  this.writeCLCC();
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CMEE=0")) {
                  this._extendedErrors = false;
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CMEE=1")) {
                  this._extendedErrors = true;
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CIND=?")) {
                  this._hasCallHeldIndicator = true;
                  this.sendRawData(RESPONSE_CIND);
                  this.sendOK();
                  return;
               }

               if (data.startsWith("CIND?")) {
                  this.handsfreeSendIndicators();
                  return;
               }

               if (data.startsWith("CHLD=")) {
                  char action = data.charAt(5);
                  int index = 0;
                  switch (action) {
                     case '1':
                     case '2':
                        if (data.length() > 6) {
                           char c = data.charAt(6);
                           if (c == 0) {
                              this.sendError(21);
                              return;
                           }

                           if (c >= '1' && c <= '9') {
                              if (this._networkType == 4) {
                                 this.sendError(4);
                                 return;
                              }

                              index = c - '0';
                              if (this.getCallInfoByIndex(index) == null) {
                                 this.sendError(21);
                                 return;
                              }
                           }
                        }
                     case '0':
                     case '3':
                     case '4':
                        this.handsfreeHoldCall(action - '0', index);
                        return;
                     case '?':
                        this.handsfreeSendHoldInfo();
                        return;
                     default:
                        this.sendError(4);
                        return;
                  }
               }

               System.out.println("Unknown AT data: " + data);
               this.sendError(4);
            } else {
               if (this._addressBookCards == null) {
                  this.snapshotAddressBook();
               }

               if (this._addressBookCards == null) {
                  this.sendError(3);
                  return;
               }

               int comma = data.indexOf(44);
               int end = data.indexOf(13);
               boolean var10 = false /* VF: Semaphore variable */;

               try {
                  var10 = true;
                  int startIndex;
                  int endIndex;
                  if (comma == -1) {
                     startIndex = NumberUtilities.parseInt(data, 5, end, 10);
                     endIndex = startIndex;
                  } else {
                     startIndex = NumberUtilities.parseInt(data, 5, comma, 10);
                     endIndex = NumberUtilities.parseInt(data, comma + 1, end, 10);
                  }

                  startIndex--;
                  if (--endIndex < 0) {
                     endIndex = startIndex;
                  }

                  if (startIndex < 0
                     || endIndex < 0
                     || startIndex > endIndex
                     || startIndex >= this._addressBookCards.length
                     || endIndex >= this._addressBookCards.length) {
                     this.sendError(21);
                     return;
                  }

                  this.dumpAddressBookSnapshot(startIndex, endIndex);
                  this.sendOK();
                  var10 = false;
               } finally {
                  if (var10) {
                     this.sendError(21);
                     return;
                  }
               }
            }
         }
      }
   }

   private final synchronized void writeCLCC() {
      int numCalls = this._callInfoArray.length;

      for (int i = 0; i < numCalls; i++) {
         CallInfo info = this._callInfoArray[i];
         if (info != null) {
            String number = info._number;
            if (number == null) {
               label104:
               try {
                  number = this._phone.getCallPhoneNumber(info._callID);
               } finally {
                  break label104;
               }
            }

            int mpty = 0;

            try {
               switch (this._phone.getCallState(info._callID)) {
                  case -1:
                     continue;
                  case 0:
                  default:
                     this._callInfoArray[i] = null;
                     continue;
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                     break;
                  case 5:
                  case 6:
                     mpty = 1;
               }
            } finally {
               continue;
            }

            StringBuffer sb = new StringBuffer();
            sb.append("+CLCC: ");
            sb.append(i + 1);
            sb.append(',');
            sb.append(info._outgoing ? 0 : 1);
            sb.append(',');
            sb.append(info.getCLCCStatus());
            sb.append(",0,");
            sb.append(mpty);
            if (number != null) {
               sb.append(",\"");
               sb.append(number);
               sb.append("\",");
               sb.append(this.getNumberType(number));
            }

            this.sendRawData(sb);
         }
      }
   }

   private final String getQuotedString(String s) {
      int start = s.indexOf(34);
      int end = s.lastIndexOf(34);
      return start != -1 && end != -1 ? s.substring(start + 1, end) : null;
   }

   @Override
   public final void handsfreeDisableNREC() {
      this.log(1314014531);
      this._nrecDisabledByHF = true;
      if (HandsfreeGateway.isNRECSupported()) {
         this.updateSinkProperties(true);
         this.sendOK();
      } else {
         this.sendError(4);
      }
   }

   private final void snapshotAddressBook() {
      long startTime = System.currentTimeMillis();
      if (this._currentPhonebook == 0) {
         this._addressBookCards = super._btManager.getAddressCards(true);
         this._addressBookNumbers = null;
      } else {
         this.snapshotCallHistory();
      }

      System.out.println("snap: " + (System.currentTimeMillis() - startTime));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void snapshotCallHistory() {
      this._addressBookCards = new AddressCardModel[10];
      this._addressBookNumbers = new PhoneNumberModel[10];
      if (super._btManager.getAddressBookTransferMode() != 0) {
         if (this._currentPhonebook != 1) {
            SimpleFolder phoneFolder;
            if (this._currentPhonebook == 3) {
               phoneFolder = PhoneFolders.getMissedCallFolder();
            } else {
               phoneFolder = PhoneFolders.getDefaultFolder();
            }

            ReadableList collection = (ReadableList)phoneFolder.getContainedItems();
            int index = 0;
            int items = collection.size();

            for (int i = items - 1; i >= 0; i--) {
               boolean var13 = false /* VF: Semaphore variable */;

               try {
                  var13 = true;
                  PhoneCallModelImpl call = (PhoneCallModelImpl)collection.getAt(i);
                  if (call.isIncoming()) {
                     if (this._currentPhonebook == 2) {
                        var13 = false;
                        continue;
                     }
                  } else if (this._currentPhonebook == 4) {
                     var13 = false;
                     continue;
                  }

                  CallerIDInfo clid = call.getCallerIDInfo();
                  Object o = clid.getAddress();
                  if (!clid.isPrivateNumber()) {
                     if (clid.isUnknownNumber()) {
                        var13 = false;
                     } else {
                        PersistableRIMModel number = clid.getNumber();
                        if (number != null) {
                           if (!(number instanceof PhoneNumberModel)) {
                              var13 = false;
                           } else {
                              PhoneNumberModel phoneNumber = (PhoneNumberModel)number;
                              AddressCardModel card;
                              if (!(o instanceof AddressCardModel)) {
                                 card = this.createAddressCard();
                                 card.add(phoneNumber);
                              } else {
                                 card = (AddressCardModel)o;
                              }

                              this._addressBookCards[index] = card;
                              this._addressBookNumbers[index] = phoneNumber;
                              if (++index == 10) {
                                 return;
                              }

                              var13 = false;
                           }
                        } else {
                           var13 = false;
                        }
                     }
                  } else {
                     var13 = false;
                  }
               } finally {
                  if (var13) {
                     return;
                  }
               }
            }
         }
      }
   }

   private final AddressCardModel createAddressCard() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Factory addressCardModelFactory = (Factory)ar.get(-3124646573404667739L);
      return (AddressCardModel)addressCardModelFactory.createInstance(null);
   }

   private final void dumpAddressBookSnapshot(int start, int end) {
      this._sniffModeDesired = false;
      super._device.updateSniffMode();
      DataBuffer entry = new DataBuffer();
      DataBuffer name = new DataBuffer();
      byte[] firstNameData = null;
      byte[] lastNameData = null;
      int numEntries = this._addressBookCards.length;
      AddressCardModel card = null;
      int phoneNumberIndex = 0;
      Object[] phoneNumberModels = null;
      boolean lastNameFirst = false;
      AddressBook ab = AddressBookServices.getAddressBook(true);
      if (ab != null) {
         AddressBookOptions abo = ab.getAddressBookOptions();
         if (abo.getSortOrder() == -227891759293611117L) {
            lastNameFirst = true;
         }
      }

      ContextObject context = new ContextObject(117);
      ContextObject.setPrivateFlag(context, 4936088360624690805L, 68);

      int i;
      for (i = start; i <= end; i++) {
         if (this._addressBookCards[i] != null) {
            if (this._addressBookCards[i] == card) {
               phoneNumberIndex++;
            } else {
               card = this._addressBookCards[i];
               firstNameData = null;
               lastNameData = null;
               PersonNameModel person = card.getName();
               if (person == null) {
                  CompanyInfoModel company = card.getCompanyInfo();
                  if (company != null) {
                     String companyName = company.getCompanyName();
                     if (companyName != null) {
                        firstNameData = this.convertString(companyName);
                     }
                  }
               } else {
                  String firstName = person.getFirstName();
                  if (firstName != null) {
                     firstNameData = this.convertString(firstName);
                  }

                  String lastName = person.getLastName();
                  if (lastName != null) {
                     lastNameData = this.convertString(lastName);
                  }
               }

               if (this._addressBookNumbers == null) {
                  phoneNumberModels = card.getPhoneNumberModels();
                  phoneNumberIndex = 0;

                  for (int index = i - 1; index >= 0 && this._addressBookCards[index] == card; index--) {
                     phoneNumberIndex++;
                  }
               }
            }

            PhoneNumberModel pnm;
            if (this._addressBookNumbers != null) {
               pnm = this._addressBookNumbers[i];
            } else {
               if (phoneNumberModels == null || phoneNumberIndex >= phoneNumberModels.length) {
                  continue;
               }

               pnm = (PhoneNumberModel)phoneNumberModels[phoneNumberIndex];
               switch (pnm.getType()) {
                  case -1:
                  case 7:
                     continue;
                  case 0:
                  case 1:
                  case 2:
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                  case 8:
               }
            }

            entry.write(BYTE_CPBR);
            entry.write(String.valueOf(i + 1).getBytes());
            entry.write(44);
            entry.write(34);
            String number = PhoneNumberConverter.convertForBluetooth(pnm);
            byte[] numberData = number.getBytes();
            int length = Math.min(numberData.length, 24);
            entry.write(numberData, 0, length);
            entry.write(34);
            entry.write(44);
            entry.write(this.getNumberTypeAsBytes(number));
            entry.write(44);
            entry.write(34);
            if (firstNameData == null && lastNameData == null) {
               name.write(numberData);
            } else {
               if (lastNameFirst) {
                  if (lastNameData != null) {
                     name.write(lastNameData);
                     if (firstNameData != null) {
                        name.write(44);
                        name.write(32);
                     }
                  }

                  if (firstNameData != null) {
                     name.write(firstNameData);
                  }
               } else {
                  if (firstNameData != null) {
                     name.write(firstNameData);
                     if (lastNameData != null) {
                        name.write(32);
                     }
                  }

                  if (lastNameData != null) {
                     name.write(lastNameData);
                  }
               }

               if (i > 0 && this._addressBookCards[i - 1] == card || i + 1 < numEntries && this._addressBookCards[i + 1] == card) {
                  byte[] type = this.convertString(pnm.getTypeString());
                  if (type != null) {
                     name.write(32);
                     name.write(45);
                     name.write(32);
                     name.write(type);
                  }
               }
            }

            name.rewind();
            length = Math.min(name.getLength(), 40);
            entry.write(name, length);
            entry.write(34);
            entry.trim(true);
            this.sendRawData(entry.getArray());
            entry.setLength(0);
            name.setLength(0);
         }
      }

      if (i == this._addressBookCards.length) {
         this._addressBookCards = null;
         this._addressBookNumbers = null;
         this._sniffModeDesired = true;
         super._device.updateSniffMode();
      }
   }

   private final byte[] convertString(String str) {
      try {
         switch (this._addressBookEncoding) {
            case 0:
               return str.getBytes();
            case 1:
            default:
               return str.getBytes("SMS");
            case 2:
               return str.getBytes("UTF-8");
            case 3:
               return str.getBytes("UnicodeBigUnmarked");
         }
      } finally {
         ;
      }
   }

   private final void sendOK() {
      this.sendRawData(RESPONSE_OK);
   }

   private final void sendError() {
      this.sendError(0);
   }

   private final void sendError(int cmeError) {
      if (this._extendedErrors) {
         StringBuffer sb = new StringBuffer();
         sb.append("+CME ERROR: ");
         sb.append(cmeError);
         this.sendRawData(sb);
      } else {
         this.sendRawData(RESPONSE_ERROR);
      }
   }

   private final void sendRawData(StringBuffer sb) {
      sb.insert(0, "\r\n");
      sb.append("\r\n");
      this.sendRawData(sb.toString());
   }

   private final void sendRawData(String data) {
      if (StringUtilities.getCharacterSize(data) == 2) {
         throw new IllegalArgumentException();
      }

      for (int i = 0; i < 10; i++) {
         if (HandsfreeGateway.sendRawData(data) == 0) {
            return;
         }

         try {
            Thread.currentThread();
            Thread.sleep(100);
         } finally {
            continue;
         }
      }

      System.out.println("sendRawData timeout");
   }

   private final void sendRawData(byte[] data) {
      for (int i = 0; i < 10; i++) {
         if (HandsfreeGateway.sendRawData(data) == 0) {
            return;
         }

         try {
            Thread.currentThread();
            Thread.sleep(100);
         } finally {
            continue;
         }
      }

      System.out.println("sendRawData timeout");
   }

   @Override
   public final synchronized void headsetIncomingConnection(byte[] address) {
      BluetoothDevice device = super._btManager.getPairedDevice(address);
      if (device != null && super._device == null && HeadsetGateway.isEnabled()) {
         device.addSupportedProfile(2);
         this._deviceToReconnect = null;
         this._isHandsfree = false;
         this.updateState(1, device);
         this._remoteVolumeControl = true;
      } else {
         HeadsetGateway.disconnect();
      }
   }

   @Override
   public final void headsetConnected(int result) {
      if (!HeadsetGateway.isEnabled()) {
         HeadsetGateway.disconnect();
      } else {
         this.connected(result);
      }
   }

   @Override
   public final void headsetDisconnected() {
      this.disconnected();
   }

   @Override
   public final void headsetAudioConnected(int result) {
      this.scoConnected(result);
   }

   @Override
   public final void headsetAudioDisconnected() {
      this.scoDisconnected();
   }

   @Override
   public final void headsetButtonPressed() {
      if (!this.answer()) {
         if ((this.isCallInProgress() || this.isDCCallInProgress()) && this._scoState == 0) {
            this.connectSCO();
            return;
         }

         if (!this.hangup()) {
            this.redial();
         }
      }
   }

   @Override
   public final void headsetSpeakerVolumeChange(int volume) {
      this._remoteVolume = volume;
      if (this._audioRouter.getSink() == 2) {
         this._audioRouter.setMasterVolume(this.convertVolumeToPercentage(volume), true);
      }
   }

   @Override
   public final void headsetUnknownATData(String data) {
      System.out.println("Unknown AT data: " + data);
      HeadsetGateway.sendError();
   }

   @Override
   public final void callIncoming(int callId) {
      this.updateCallInfo(callId, 16, false);
      if (super._state == 2) {
         this._sniffModeDesired = false;
         super._device.updateSniffMode();
         this.updateCallSetupIndicator();
         super._btManager.invokeLater(this._ringRunnable);
      }
   }

   @Override
   public final void callDisplayUpdated(int callId) {
   }

   @Override
   public final void callWaiting(int callId) {
      this.updateCallInfo(callId, 32);
      if (super._device != null && this._isHandsfree) {
         try {
            if (this._callWaitingReporting) {
               String id = this._phone.getCallPhoneNumber(callId);
               if (id == null) {
                  id = "";
               }

               HandsfreeGateway.sendCallWaiting(id, this.getNumberType(id));
            }

            this.updateCallSetupIndicator();
         } finally {
            this.log(542262616);
            return;
         }
      }
   }

   @Override
   public final void callInitiated(int callId) {
      this.updateCallInfo(callId, 4, true);
      this.updateCallSetupIndicator();
      this.updateCallHeldIndicator(false);
   }

   @Override
   public final void callConnected(int callId) {
      this.updateCallInfo(callId, 1);
      this.doNextHoldAction(true);
   }

   @Override
   public final void callFailed(int callId, int error) {
      label19:
      try {
         int state = this._phone.getCallState(callId);
         if (state == 3) {
            return;
         }
      } finally {
         break label19;
      }

      this.removeCallInfo(callId);
      this.doNextHoldAction(false);
   }

   @Override
   public final void callDelivered(int callId) {
      this.updateCallInfo(callId, 8);
      this.updateCallSetupIndicator();
   }

   @Override
   public final void callManipulateFailed(int callId, int error) {
      this.doNextHoldAction(false);
   }

   @Override
   public final void callDisconnected(int callId) {
      this.removeCallInfo(callId);
      if (super._device != null && this._isHandsfree) {
         boolean rc = this.doNextAnswerAction();
         if (!rc) {
            rc = this.doNextHoldAction(true);
            if (!rc) {
               super._btManager.invokeLater(this._ringRunnable);
               return;
            }
         }
      } else if (this._deviceToReconnect != null) {
         this.connect(this._deviceToReconnect);
      }
   }

   @Override
   public final void callHeld(int callId) {
      this.updateCallInfo(callId, 2);
      if (super._device != null && this._isHandsfree) {
         if (this._delayedDialingNumber != null) {
            if (!this.startCall(this._delayedDialingNumber)) {
               this.updateCallHeldIndicator(false);
            }

            this._delayedDialingNumber = null;
            return;
         }

         this.doNextHoldAction(true);
         this.updateCallHeldIndicator(false);
      }
   }

   @Override
   public final void callResumed(int callId) {
      this.updateCallInfo(callId, 1);
      this.doNextHoldAction(true);
   }

   @Override
   public final void callAdded(int callId) {
      this.updateCallInfo(callId, 1);
      this.doNextHoldAction(true);
   }

   @Override
   public final void callRemoved(int callId) {
      this.doNextHoldAction(true);
   }

   @Override
   public final void callTransferred(int status, int reason) {
      this.doNextHoldAction(status == 0);
   }

   @Override
   public final void callTransferStateUpdated(int callId, int state) {
   }

   @Override
   public final void callVoicePrivacyUpdated(int callId, boolean on) {
   }

   @Override
   public final void callOTAStatusUpdated(int callId, int status) {
   }

   @Override
   public final void dtmfData(int dtmf) {
   }

   @Override
   public final void signalLevel(int level) {
      if (this.canUpdateIndicators()) {
         int rssi = this.getRSSI(level);
         if (this._lastRSSI != rssi) {
            this._lastRSSI = rssi;
            HandsfreeGateway.sendIndicatorUpdate(5, rssi);
            this.updateServiceIndicator(RadioInfo.getNetworkService());
         }
      }
   }

   @Override
   public final void networkStarted(int networkId, int service) {
      this.updateServiceIndicator(service);
   }

   @Override
   public final void baseStationChange() {
   }

   @Override
   public final void radioTurnedOff() {
      this.updateServiceIndicator(0);
   }

   @Override
   public final void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public final void networkStateChange(int state) {
   }

   @Override
   public final void networkScanComplete(boolean success) {
   }

   @Override
   public final void networkServiceChange(int networkId, int service) {
      this.updateServiceIndicator(service);
   }

   @Override
   public final synchronized void audioVolumeChanged(boolean remote) {
      if (!remote && this._audioRouter.getSink() == 2 && this._scoState == 2) {
         int volume = this.convertPercentageToVolume(this._audioRouter.getMasterVolume());
         this._remoteVolume = volume;
         if (this._isHandsfree && this._remoteVolumeControl) {
            HandsfreeGateway.sendSpeakerVolume(volume);
            return;
         }

         HeadsetGateway.sendSpeakerVolume(volume);
      }
   }

   @Override
   public final synchronized void audioSinkChanged() {
      if (this._audioRouter.getSink() == 2) {
         if (super._state != 2) {
            this._audioRouter.resetSink();
            return;
         }

         if (this._scoState == 0) {
            this.connectSCO();
            return;
         }
      } else if (super._state == 2 && this._scoState == 2) {
         this.disconnectSCO();
      }
   }

   @Override
   public final void audioSourceChanged() {
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 5606159505026478103L) {
         synchronized (this) {
            CallInfo info1 = null;
            CallInfo info2 = null;
            if (this._callInfoArray.length >= 1) {
               info1 = this._callInfoArray[0];
            }

            if (this._callInfoArray.length >= 2) {
               info2 = this._callInfoArray[1];
            }

            if (info1 != null) {
               if (info1._status == 1) {
                  info1._status = 2;
               } else {
                  info1._status = 1;
               }
            }

            if (info2 != null) {
               if (info2._status == 1) {
                  info2._status = 2;
               } else {
                  info2._status = 1;
               }
            } else if (object0 != null && info1 != null) {
               int callID = info1._callID;
               CallInfo info = new CallInfo(callID);
               info._outgoing = true;
               info._status = 1;
               if (object0 instanceof String) {
                  info._number = (String)object0;
               }

               if (this._callInfoArray.length == 1) {
                  Arrays.add(this._callInfoArray, info);
               } else if (this._callInfoArray.length == 2 && this._callInfoArray[1] == null) {
                  this._callInfoArray[1] = info;
               } else {
                  System.out.println("CDMA CLCC state error");
               }

               info1._status = 2;
            }

            this.updateCallHeldIndicator(false);
         }
      }
   }

   @Override
   public final synchronized void vadEvent(int event, int context) {
      switch (event) {
         case 0:
            if (super._device != null && context == 0) {
               if (this._isHandsfree && this._hasVoiceRecognition) {
                  this.sendRawData("\r\n+BVRA: 1\r\n");
                  this._vadEnabledOnHF = true;
               }

               if (this._scoState == 2) {
                  VADUserEvents.sendEvent(0, 2);
                  return;
               }

               this._activateVADAfterSCOConnect = true;
               return;
            }
            break;
         case 3:
            if (super._device != null && this._vadEnabledOnHF && this._hasVoiceRecognition) {
               this.sendRawData("\r\n+BVRA: 0\r\n");
            }

            this._vadEnabledOnHF = false;
      }
   }

   @Override
   public final synchronized Verb[] getVerbs(Object context) {
      Verb[] verbs = new Verb[0];
      if (BluetoothME.isPowerOn() && context instanceof AudioPathControl) {
         AudioPathControl control = (AudioPathControl)context;
         if (control.canSwitchToPath(2)) {
            if (super._state == 2) {
               if (this._scoState == 0) {
                  Arrays.add(verbs, new BluetoothPhoneManager$ConnectVerb(this, super._device, control));
                  return verbs;
               }
            } else if (super._state == 0) {
               Vector v = super._btManager.getPairedDevices();
               int length = v.size();

               for (int i = 0; i < length; i++) {
                  BluetoothDevice device = (BluetoothDevice)v.elementAt(i);
                  if (device.hasHandsfree() && HandsfreeGateway.isEnabled()) {
                     Arrays.add(verbs, new BluetoothPhoneManager$ConnectVerb(this, device, control));
                  } else if (device.hasHeadset() && HeadsetGateway.isEnabled()) {
                     Arrays.add(verbs, new BluetoothPhoneManager$ConnectVerb(this, device, control));
                  }
               }
            }
         }
      }

      return verbs;
   }
}
