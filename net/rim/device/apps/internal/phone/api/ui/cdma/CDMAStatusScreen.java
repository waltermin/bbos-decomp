package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.apps.api.utility.general.SerialNumber;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.component.IPEditField;

public final class CDMAStatusScreen extends MainScreen {
   private EditField _phoneNumber = new EditField("Phone Number: ", "", 40, 9007199254740992L);
   private EditField _deviceESNhex = new EditField("Device ESNhex: ", "", 40, 9007199254740992L);
   private EditField _deviceESNdec = new EditField("Device ESNdec: ", "", 40, 9007199254740992L);
   private EditField _BBPIN = new EditField("Device BBPIN: ", "", 40, 9007199254740992L);
   private ObjectListField _languageList = new ObjectListField();
   private EditField _deviceSWVersion = new EditField("Device Software Version: ", "", 40, 9007199254740992L);
   private EditField _model = new EditField("Model: ", "", 40, 9007199254740992L);
   private EditField _HWVersion = new EditField("H/W Version: ", "", 40, 9007199254740992L);
   private EditField _PRLVersion = new EditField("PRL Version: ", "", 40, 9007199254740992L);
   private EditField _currentSID = new EditField("Current SID: ", "", 40, 9007199254740992L);
   private EditField _homeSID = new EditField("HomeSID/HomeNID: ", "", 40, 9007199254740992L);
   private EditField _blackBerryHomeSID = new EditField("BlackBerry HomeSID: ", "", 40, 9007199254740992L);
   private EditField _network = new EditField("Network Scan Mode: ", "", 40, 9007199254740992L);
   private EditField _bandClass = new EditField("Band Class: ", "", 40, 9007199254740992L);
   private EditField _CDMAChannel = new EditField("CDMA Channel: ", "", 40, 9007199254740992L);
   private EditField _IPAddress = new EditField("IP Address: ", "", 40, 9007199254740992L);
   private EditField _browserVersion = new EditField("Browser Version: ", "", 40, 9007199254740992L);
   private EditField _battery = new EditField("Battery: ", "", 40, 9007199254740992L);
   private RichTextField _language = new RichTextField("Language List: ");
   private RichTextField _technology = new RichTextField("Technology: dual-band CDMA 1x");
   private RichTextField _enhancedRoamingIndicator = new RichTextField("ERI Version: not supported");
   private RichTextField _deviceCapability = new RichTextField("Device Capabilities: " + this.getDeviceCapabilities());
   private RichTextField _BREW = new RichTextField("BREW: not supported");

   public CDMAStatusScreen() {
      this.init();
   }

   protected final void init() {
      this.setTitle(new LabelField("Status"));
      this.add(this._phoneNumber);
      this.add(this._deviceESNhex);
      this.add(this._deviceESNdec);
      this.add(this._BBPIN);
      this.add(this._technology);
      this.add(this._language);
      this.add(this._languageList);
      this.add(this._deviceSWVersion);
      this.add(this._model);
      this.add(this._HWVersion);
      this.add(this._PRLVersion);
      this.add(this._enhancedRoamingIndicator);
      this.add(this._currentSID);
      this.add(this._homeSID);
      this.add(this._blackBerryHomeSID);
      this.add(this._network);
      this.add(this._bandClass);
      this.add(this._CDMAChannel);
      this.add(this._IPAddress);
      this.add(this._deviceCapability);
      this.add(this._browserVersion);
      this.add(this._BREW);
      this.add(this._battery);
      this._phoneNumber.setText(this.getPhoneNumber());
      this._deviceESNhex.setText(this.getESNHexValue());
      this._deviceESNdec.setText(this.getESNDecValue());
      this._BBPIN.setText(this.getPIN());
      Locale[] locale = Locale.getAvailableLocales();
      int size = locale.length;
      String[] languageName = new String[size - 1];

      for (int i = 1; i < size; i++) {
         languageName[i - 1] = "     " + locale[i].getDisplayName();
      }

      this._languageList.set(languageName);
      this._deviceSWVersion.setText(this.getAppVersion());
      this._model.setText(this.getModelNumber());
      this._bandClass.setText(this.getBandClass());
      this._HWVersion.setText(this.getHardwareVersion());
      this._PRLVersion.setText(this.getPRLVersion());
      this._currentSID.setText(this.getCurrentSID());
      this._homeSID.setText(this.getHomeSID());
      this._blackBerryHomeSID.setText(this.getBlackBerryHomeSID());
      this._network.setText(this.getNetwork());
      this._CDMAChannel.setText(this.getCDMAChannel());
      this._IPAddress.setText(this.getDeviceIP());
      this._browserVersion.setText(this.getAppVersion());
      this._battery.setText(this.getBateryStatus());
   }

   public final String getDeviceCapabilities() {
      String ret = "SMS, Packet DATA, VOICE capable";
      if (GPS.isSupported()) {
         ret = ret + ", GPS enabled";
      }

      return ret;
   }

   public final String getPhoneNumber() {
      return PhoneUtilities.getDevicePhoneNumber();
   }

   public final String getESNHexValue() {
      return SerialNumber.getSerialNumber();
   }

   public final String getESNDecValue() {
      return SerialNumber.getDecimalSerialNumber();
   }

   public final String getPIN() {
      return Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();
   }

   public final String getAppVersion() {
      return ApplicationDescriptor.currentApplicationDescriptor().getVersion();
   }

   public final String getModelNumber() {
      return DeviceInfo.getDeviceName();
   }

   public final String getHardwareVersion() {
      return Integer.toString(CDMAInfo.getHardwareVersion());
   }

   public final String getPRLVersion() {
      return Integer.toString(CDMAInfo.getPRLVersion());
   }

   public final String getCurrentSID() {
      return Integer.toString(CDMAInfo.getCurrentSID() & 32767);
   }

   public final String getHomeSID() {
      short[] sid = CDMAInfo.getHomeSystemSIDs();
      short[] nid = CDMAInfo.getHomeSystemNIDs();
      return Integer.toString(sid[0] & 32767) + ", " + Integer.toString(nid[0] & 65535);
   }

   public final String getBlackBerryHomeSID() {
      return Integer.toString(CDMAInfo.getHomeSID() & 32767);
   }

   public final String getBandClass() {
      return Integer.toString(CDMAInfo.getBandClass());
   }

   public final String getNetwork() {
      int mode = RadioInternal.getNetworkSelectionMode();
      switch (mode) {
         case -1:
         case 3:
         case 4:
            RadioInternal.setNetworkSelectionMode(0);
            return "Automatic";
         case 0:
         default:
            return "Automatic";
         case 1:
            return "Automatic A";
         case 2:
            return "Automatic B";
         case 5:
            return "Home Only";
      }
   }

   public final String getCDMAChannel() {
      return Integer.toString(CDMAInfo.getChannelNumber());
   }

   public final String getDeviceIP() {
      int apn;
      try {
         apn = RadioInfo.getAccessPointNumber("");
      } finally {
         ;
      }

      byte[] ip;
      if (apn < 0) {
         ip = null;
      } else {
         ip = RadioInfo.getIPAddress(apn);
      }

      StringBuffer sb = new StringBuffer();
      IPEditField.appendIpAddr(sb, ip);
      return sb.toString();
   }

   public final String getBateryStatus() {
      int level = DeviceInfo.getBatteryLevel();
      level = 5 * ((level + 2) / 5);
      if (level > 100) {
         level = 100;
      } else if (level < 0) {
         level = 0;
      }

      return String.valueOf(level) + '%';
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case '\u001b':
            UiApplication.getUiApplication().popScreen(this);
            return true;
         default:
            return super.keyChar(key, status, time);
      }
   }
}
