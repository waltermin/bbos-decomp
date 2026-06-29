package net.rim.device.internal.bluetooth;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.internal.system.EventDispatchManager;
import net.rim.device.internal.system.InternalServices;

public final class BluetoothME {
   public static final int DEVICE_CLASS_LIMITED_DISCOVERABLE_MODE;
   public static final int DEVICE_CLASS_POSITIONING;
   public static final int DEVICE_CLASS_NETWORKING;
   public static final int DEVICE_CLASS_RENDERING;
   public static final int DEVICE_CLASS_CAPTURING;
   public static final int DEVICE_CLASS_OBJECT_TRANSFER;
   public static final int DEVICE_CLASS_AUDIO;
   public static final int DEVICE_CLASS_TELEPHONY;
   public static final int DEVICE_CLASS_INFORMATION;
   public static final int DEVICE_CLASS_MAJOR_MISCELLANEOUS;
   public static final int DEVICE_CLASS_MAJOR_COMPUTER;
   public static final int DEVICE_CLASS_MAJOR_PHONE;
   public static final int DEVICE_CLASS_MAJOR_LAN_ACCESS_POINT;
   public static final int DEVICE_CLASS_MAJOR_AUDIO;
   public static final int DEVICE_CLASS_MAJOR_PERIPHERAL;
   public static final int DEVICE_CLASS_MAJOR_IMAGING;
   public static final int DEVICE_CLASS_MAJOR_UNCLASSIFIED;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_UNCLASSIFIED;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_DESKTOP;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_SERVER;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_LAPTOP;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_HANDHELD;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_PALM;
   public static final int DEVICE_CLASS_MINOR_COMPUTER_WEARABLE;
   public static final int DEVICE_CLASS_MINOR_PHONE_UNCLASSIFIED;
   public static final int DEVICE_CLASS_MINOR_PHONE_CELLULAR;
   public static final int DEVICE_CLASS_MINOR_PHONE_CORDLESS;
   public static final int DEVICE_CLASS_MINOR_PHONE_SMART;
   public static final int DEVICE_CLASS_MINOR_PHONE_MODEM;
   public static final int DEVICE_CLASS_MINOR_PHONE_ISDN;
   public static final int DEVICE_CLASS_MINOR_LAN_0;
   public static final int DEVICE_CLASS_MINOR_LAN_17;
   public static final int DEVICE_CLASS_MINOR_LAN_33;
   public static final int DEVICE_CLASS_MINOR_LAN_50;
   public static final int DEVICE_CLASS_MINOR_LAN_67;
   public static final int DEVICE_CLASS_MINOR_LAN_83;
   public static final int DEVICE_CLASS_MINOR_LAN_99;
   public static final int DEVICE_CLASS_MINOR_LAN_NO_SERVICE;
   public static final int DEVICE_CLASS_MINOR_AUDIO_UNCLASSIFIED;
   public static final int DEVICE_CLASS_MINOR_AUDIO_HEADSET;
   public static final int DEVICE_CLASS_MINOR_AUDIO_HANDSFREE;
   public static final int DEVICE_CLASS_MINOR_AUDIO_MICROPHONE;
   public static final int DEVICE_CLASS_MINOR_AUDIO_LOUDSPEAKER;
   public static final int DEVICE_CLASS_MINOR_AUDIO_HEADPHONES;
   public static final int DEVICE_CLASS_MINOR_AUDIO_PORTABLEAUDIO;
   public static final int DEVICE_CLASS_MINOR_AUDIO_CARAUDIO;
   public static final int DEVICE_CLASS_MINOR_AUDIO_SETTOPBOX;
   public static final int DEVICE_CLASS_MINOR_AUDIO_HIFIAUDIO;
   public static final int DEVICE_CLASS_MINOR_AUDIO_VCR;
   public static final int DEVICE_CLASS_MINOR_AUDIO_VIDEOCAMERA;
   public static final int DEVICE_CLASS_MINOR_AUDIO_CAMCORDER;
   public static final int DEVICE_CLASS_MINOR_AUDIO_VIDEOMONITOR;
   public static final int DEVICE_CLASS_MINOR_AUDIO_VIDEOSPEAKER;
   public static final int DEVICE_CLASS_MINOR_AUDIO_CONFERENCING;
   public static final int DEVICE_CLASS_MINOR_AUDIO_GAMING;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_KEYBOARD;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_POINTING;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_COMBOKEY;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_UNCLASSIFIED;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_JOYSTICK;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_GAMEPAD;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_REMOTECONTROL;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_SENSING;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_DIGITIZER;
   public static final int DEVICE_CLASS_MINOR_PERIPHERAL_CARD_READER;
   public static final int DEVICE_CLASS_MINOR_IMAGE_UNCLASSIFIED;
   public static final int DEVICE_CLASS_MINOR_IMAGE_DISPLAY;
   public static final int DEVICE_CLASS_MINOR_IMAGE_CAMERA;
   public static final int DEVICE_CLASS_MINOR_IMAGE_SCANNER;
   public static final int DEVICE_CLASS_MINOR_IMAGE_PRINTER;
   public static final int DEVICE_CLASS_MASK_SERVICE;
   public static final int DEVICE_CLASS_MASK_MAJOR;
   public static final int DEVICE_CLASS_MASK_MINOR;
   public static final int DEVICE_CLASS_MASK_DISC;
   public static final int DEVICE_STATUS_IN_RANGE;
   public static final int DEVICE_STATUS_PAIRED;
   public static final int DEVICE_STATUS_TRUSTED;
   public static final int BT_STATUS_SUCCESS;
   public static final int BT_STATUS_FAILED;
   public static final int BT_STATUS_PENDING;
   public static final int BT_STATUS_BUSY;
   public static final int BT_STATUS_NO_RESOURCES;
   public static final int BT_STATUS_NOT_FOUND;
   public static final int BT_STATUS_DEVICE_NOT_FOUND;
   public static final int BT_STATUS_CONNECTION_FAILED;
   public static final int BT_STATUS_TIMEOUT;
   public static final int BT_STATUS_NO_CONNECTION;
   public static final int BT_STATUS_INVALID_PARM;
   public static final int BT_STATUS_IN_PROGRESS;
   public static final int BT_STATUS_RESTRICTED;
   public static final int BT_STATUS_INVALID_TYPE;
   public static final int BT_STATUS_HCI_INIT_ERR;
   public static final int BT_STATUS_NOT_SUPPORTED;
   public static final int BT_STATUS_IN_USE;
   public static final int BT_STATUS_SDP_CONT_STATE;
   public static final int BT_STATUS_CANCELLED;
   public static final int BT_LINK_MODE_ACTIVE;
   public static final int BT_LINK_MODE_HOLD;
   public static final int BT_LINK_MODE_SNIFF;
   public static final int BT_LINK_MODE_PARK;
   public static final int BT_CONNECTION_ROLE_MASTER;
   public static final int BT_CONNECTION_ROLE_SLAVE;
   public static final int BT_CONNECTION_ROLE_ANY;
   public static final int BT_CONNECTION_ROLE_UNKNOWN;
   public static final int BT_CONNECTION_ROLE_PSLAVE;
   public static final int BT_CONNECTION_ROLE_PMASTER;

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
