package net.rim.device.internal.bluetooth;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.InternalServices;

public final class BluetoothME {
   public static final int DEVICE_CLASS_LIMITED_DISCOVERABLE_MODE = 8192;
   public static final int DEVICE_CLASS_POSITIONING = 65536;
   public static final int DEVICE_CLASS_NETWORKING = 131072;
   public static final int DEVICE_CLASS_RENDERING = 262144;
   public static final int DEVICE_CLASS_CAPTURING = 524288;
   public static final int DEVICE_CLASS_OBJECT_TRANSFER = 1048576;
   public static final int DEVICE_CLASS_AUDIO = 2097152;
   public static final int DEVICE_CLASS_TELEPHONY = 4194304;
   public static final int DEVICE_CLASS_INFORMATION = 8388608;
   public static final int DEVICE_CLASS_MAJOR_MISCELLANEOUS = 0;
   public static final int DEVICE_CLASS_MAJOR_COMPUTER = 256;
   public static final int DEVICE_CLASS_MAJOR_PHONE = 512;
   public static final int DEVICE_CLASS_MAJOR_LAN_ACCESS_POINT = 768;
   public static final int DEVICE_CLASS_MAJOR_AUDIO = 1024;
   public static final int DEVICE_CLASS_MAJOR_PERIPHERAL = 1280;
   public static final int DEVICE_CLASS_MAJOR_IMAGING = 1536;
   public static final int DEVICE_CLASS_MAJOR_UNCLASSIFIED = 7936;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_UNCLASSIFIED = 0;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_DESKTOP = 4;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_SERVER = 8;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_LAPTOP = 12;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_HANDHELD = 16;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_PALM = 20;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_WEARABLE = 24;
   public static final int DEVICE_CLASS_MINOR_PHONE_UNCLASSIFIED = 0;
   public static final int DEVICE_CLASS_MINOR_PHONE_CELLULAR = 4;
   public static final int DEVICE_CLASS_MINOR_PHONE_CORDLESS = 8;
   public static final int DEVICE_CLASS_MINOR_PHONE_SMART = 12;
   public static final int DEVICE_CLASS_MINOR_PHONE_MODEM = 16;
   public static final int DEVICE_CLASS_MINOR_PHONE_ISDN = 20;
   public static final int DEVICE_CLASS_MINOR_LAN_0 = 0;
   public static final int DEVICE_CLASS_MINOR_LAN_17 = 32;
   public static final int DEVICE_CLASS_MINOR_LAN_33 = 64;
   public static final int DEVICE_CLASS_MINOR_LAN_50 = 96;
   public static final int DEVICE_CLASS_MINOR_LAN_67 = 128;
   public static final int DEVICE_CLASS_MINOR_LAN_83 = 160;
   public static final int DEVICE_CLASS_MINOR_LAN_99 = 192;
   public static final int DEVICE_CLASS_MINOR_LAN_NO_SERVICE = 224;
   public static final int DEVICE_CLASS_MINOR_AUDIO_UNCLASSIFIED = 0;
   public static final int DEVICE_CLASS_MINOR_AUDIO_HEADSET = 4;
   public static final int DEVICE_CLASS_MINOR_AUDIO_HANDSFREE = 8;
   public static final int DEVICE_CLASS_MINOR_AUDIO_MICROPHONE = 16;
   public static final int DEVICE_CLASS_MINOR_AUDIO_LOUDSPEAKER = 20;
   public static final int DEVICE_CLASS_MINOR_AUDIO_HEADPHONES = 24;
   public static final int DEVICE_CLASS_MINOR_AUDIO_PORTABLEAUDIO = 28;
   public static final int DEVICE_CLASS_MINOR_AUDIO_CARAUDIO = 32;
   public static final int DEVICE_CLASS_MINOR_AUDIO_SETTOPBOX = 36;
   public static final int DEVICE_CLASS_MINOR_AUDIO_HIFIAUDIO = 40;
   public static final int DEVICE_CLASS_MINOR_AUDIO_VCR = 44;
   public static final int DEVICE_CLASS_MINOR_AUDIO_VIDEOCAMERA = 48;
   public static final int DEVICE_CLASS_MINOR_AUDIO_CAMCORDER = 52;
   public static final int DEVICE_CLASS_MINOR_AUDIO_VIDEOMONITOR = 56;
   public static final int DEVICE_CLASS_MINOR_AUDIO_VIDEOSPEAKER = 60;
   public static final int DEVICE_CLASS_MINOR_AUDIO_CONFERENCING = 64;
   public static final int DEVICE_CLASS_MINOR_AUDIO_GAMING = 72;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_KEYBOARD = 64;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_POINTING = 128;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_COMBOKEY = 192;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_UNCLASSIFIED = 0;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_JOYSTICK = 4;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_GAMEPAD = 8;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_REMOTECONTROL = 12;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_SENSING = 16;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_DIGITIZER = 20;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_CARD_READER = 24;
   public static final int DEVICE_CLASS_MINOR_IMAGE_UNCLASSIFIED = 0;
   public static final int DEVICE_CLASS_MINOR_IMAGE_DISPLAY = 16;
   public static final int DEVICE_CLASS_MINOR_IMAGE_CAMERA = 32;
   public static final int DEVICE_CLASS_MINOR_IMAGE_SCANNER = 64;
   public static final int DEVICE_CLASS_MINOR_IMAGE_PRINTER = 128;
   public static final int DEVICE_CLASS_MASK_SERVICE = 16760832;
   public static final int DEVICE_CLASS_MASK_MAJOR = 7936;
   public static final int DEVICE_CLASS_MASK_MINOR = 252;
   public static final int DEVICE_CLASS_MASK_DISC = 8192;
   public static final int DEVICE_STATUS_IN_RANGE = 1;
   public static final int DEVICE_STATUS_PAIRED = 2;
   public static final int DEVICE_STATUS_TRUSTED = 4;
   public static final int BT_STATUS_SUCCESS = 0;
   public static final int BT_STATUS_FAILED = 1;
   public static final int BT_STATUS_PENDING = 2;
   public static final int BT_STATUS_BUSY = 11;
   public static final int BT_STATUS_NO_RESOURCES = 12;
   public static final int BT_STATUS_NOT_FOUND = 13;
   public static final int BT_STATUS_DEVICE_NOT_FOUND = 14;
   public static final int BT_STATUS_CONNECTION_FAILED = 15;
   public static final int BT_STATUS_TIMEOUT = 16;
   public static final int BT_STATUS_NO_CONNECTION = 17;
   public static final int BT_STATUS_INVALID_PARM = 18;
   public static final int BT_STATUS_IN_PROGRESS = 19;
   public static final int BT_STATUS_RESTRICTED = 20;
   public static final int BT_STATUS_INVALID_TYPE = 21;
   public static final int BT_STATUS_HCI_INIT_ERR = 22;
   public static final int BT_STATUS_NOT_SUPPORTED = 23;
   public static final int BT_STATUS_IN_USE = 5;
   public static final int BT_STATUS_SDP_CONT_STATE = 24;
   public static final int BT_STATUS_CANCELLED = 25;
   public static final int BT_LINK_MODE_ACTIVE = 0;
   public static final int BT_LINK_MODE_HOLD = 1;
   public static final int BT_LINK_MODE_SNIFF = 2;
   public static final int BT_LINK_MODE_PARK = 3;
   public static final int BT_CONNECTION_ROLE_MASTER = 0;
   public static final int BT_CONNECTION_ROLE_SLAVE = 1;
   public static final int BT_CONNECTION_ROLE_ANY = 2;
   public static final int BT_CONNECTION_ROLE_UNKNOWN = 3;
   public static final int BT_CONNECTION_ROLE_PSLAVE = 4;
   public static final int BT_CONNECTION_ROLE_PMASTER = 5;

   public static final boolean isSupported() {
      return InternalServices.isDeviceCapable(8);
   }

   public static final int requestPowerOn() {
      return ITPolicy.getBoolean(34, 1, false) ? 20 : requestPowerOn0();
   }

   private static final native int requestPowerOn0();

   public static final native int requestPowerOff();

   public static final native boolean isPowerOn();

   public static final native int startInquiry(int var0);

   public static final native int stopInquiry();

   public static final native int sendPIN(byte[] var0, byte[] var1);

   public static final native int authorizeDevice(byte[] var0, boolean var1);

   public static final native int connectDevice(byte[] var0, int var1, int[] var2);

   public static final native int disconnectDevice(int var0);

   public static final int disconnectDevice(byte[] address) {
      return forceDisconnectDevice(address);
   }

   public static final native int forceDisconnectDevice(byte[] var0);

   public static final native int authenticateConnection(byte[] var0);

   public static final native int encryptConnection(byte[] var0, boolean var1);

   public static final native boolean isConnectionEncrypted(byte[] var0);

   public static final native int retrieveDeviceName(byte[] var0, int var1);

   public static final native int cancelRetrieveDeviceName(byte[] var0);

   public static final native boolean isDeviceConnected(byte[] var0);

   public static final native boolean isAnyDeviceConnected();

   public static final native byte[] getLocalDeviceAddress();

   public static final native int setLocalDeviceName(byte[] var0);

   public static final native int getDeviceState(byte[] var0);

   public static final int setDiscoverable(boolean on) {
      return setAccessibleMode(on ? 3 : 2);
   }

   public static final native int setAccessibleMode(int var0);

   public static final native int startServiceDiscovery(byte[] var0, int var1, byte[] var2, byte[] var3);

   public static final native int stopServiceDiscovery(byte[] var0);

   public static final native int startSniff(byte[] var0, int var1, int var2, int var3, int var4);

   public static final native int stopSniff(byte[] var0);

   public static final native int getLinkMode(byte[] var0);

   public static final native int setClassOfDevice(int var0);

   public static final String deviceAddressToString(byte[] address) {
      return deviceAddressToString(address, false);
   }

   public static final String deviceAddressToString(byte[] address, boolean addColons) {
      StringBuffer sb = new StringBuffer();

      for (int i = address.length - 1; i >= 0; i--) {
         String s = Integer.toHexString(address[i] & 255);
         if (s.length() == 1) {
            sb.append('0');
         }

         sb.append(s);
         if (i > 0 && addColons) {
            sb.append(':');
         }
      }

      return sb.toString().toUpperCase();
   }

   public static final byte[] stringToDeviceAddress(String string) {
      if (string.length() != 12) {
         throw new IllegalArgumentException();
      }

      byte[] address = new byte[6];

      for (int i = 0; i < 6; i++) {
         int stringIndex = i * 2;
         address[5 - i] = (byte)(NumberUtilities.parseInt(string, stringIndex, stringIndex + 2, 16) & 0xFF);
      }

      return address;
   }

   public static final native int restoreLinkKey(byte[] var0, byte[] var1, int var2);

   public static final native int deleteLinkKey(byte[] var0);

   public static final native byte[] getLinkKey(byte[] var0);

   public static final native int getLinkKeyType(byte[] var0);

   public static final native int enableSecurityMode3(boolean var0, boolean var1);

   public static final native int getMaxPower();

   public static final native boolean setMaxPower(int var0);

   public static final native int setConnectionRole(int var0);

   public static final native int getCurrentRole(byte[] var0);

   public static final native int switchRole(byte[] var0);

   public static final native int changeConnectionLinkKey(byte[] var0);

   public static final void addListener(Application app, BluetoothMEListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(39) == null) {
            dispatchManager.setDispatcher(39, new BluetoothMEEventDispatcher());
         }
      }

      app.addListener(39, listener);
   }

   public static final void removeListener(Application app, BluetoothMEListener listener) {
      app.removeListener(39, listener);
   }
}
