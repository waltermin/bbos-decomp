package net.rim.device.internal.EScreens;

import net.rim.device.api.crypto.Digest;
import net.rim.device.api.crypto.HMAC;
import net.rim.device.api.crypto.HMACKey;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.Memory;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.tid.im.layout.SLKeyLayout;

final class EngScreenSecurity extends MainScreen implements ListFieldCallback {
   private String _helpString;
   private String[] _info;
   private String[] _headers;
   private IntVector _elements;
   private String _uptimeString;
   private int _last4Keys;
   private int[] _accessInput;
   private int _accessInputIndex;
   private byte[][] _accessKeys;
   private RichTextField _help;
   private ListField _list;
   ResourceBundle _rb;
   EngScreenApp _app;
   private static final long ONE_DAY = 86400000L;
   private static final int BD_CRIPPLED_ESCREEN = 2053205356;
   private static final int ACCESS_KEY_1_DAY = 0;
   private static final int ACCESS_KEY_3_DAYS = 1;
   private static final int ACCESS_KEY_7_DAYS = 2;
   private static final int ACCESS_KEY_15_DAYS = 3;
   private static final int ACCESS_KEY_30_DAYS = 4;
   private static final int NUM_ACCESS_KEYS = 5;
   private static final int ACCESS_KEY_LENGTH = 8;
   private static final int VENDOR_ID = 0;
   private static final int OS_VERSION = 1;
   private static final int APP_VERSION = 2;
   private static final int PIN = 3;
   private static final int IMEI = 4;
   private static final int UPTIME = 5;
   private static final int SIGNAL = 6;
   private static final int BATTERY = 7;
   private static final int FILE_FREE = 8;
   private static final int FILE_TOTAL = 9;
   private static final int ESN = 10;
   private static final int MAC = 11;
   private static final int NUM_HEADERS_REQUIRED = 12;
   public static final int MENU_ESCREEN_NORMAL = 1;
   public static final int MENU_ESCREEN_CRIPPLED = 2;

   public EngScreenSecurity(EngScreenApp app) {
      this._app = app;
      this._rb = ResourceBundle.getBundle(3398356469225582779L, "net.rim.device.internal.resource.EScreen");
      this._accessInput = new int[8];
      this._accessKeys = new byte[5][];
      this.setupElements();
      this.setupHelpString();
      this.setupHeaders();
      this.setupInfoStrings();
      this.generateHash();
      this.setTitle((Field)(new Object(this._rb.getString(2))));
      this._help = (RichTextField)(new Object(this._helpString, 36028797018963968L));
      this.add(this._help);
      this.add((Field)(new Object()));
      this._list = (ListField)(new Object(this._elements.size(), 18014398509481984L));
      this._list.setCallback(this);
      this.add(this._list);
   }

   private final int[] createAccessKey(byte[] mac) {
      SLKeyLayout keyLayout = Keypad.getLayout();
      int[] result = new int[8];
      int keyInt = 0;

      for (int i = 0; i < 4; i++) {
         keyInt <<= 8;
         keyInt |= mac[i] & 255;
      }

      if (keyInt == 0 || keyInt == 2053205356) {
         for (int i = 0; i < 4; i++) {
            mac[i] = (byte)(~mac[i]);
         }
      }

      for (int i = 0; i < 8; i++) {
         int nibble = mac[i / 2];
         nibble >>= (i & 1) == 0 ? 4 : 0;
         nibble &= 15;
         char key = this.nibble2Char(nibble);
         if (keyLayout.isReduced() && !keyLayout.contains(key)) {
            StringBuffer strBuf = keyLayout.getKeyChars(keyLayout.getOriginalKeyCode(key, 0), 0);
            key = strBuf.charAt(0);
         }

         result[i] = key;
      }

      return result;
   }

   private final int get(int n) {
      int z = 12;

      for (int i = 0; i < n; i++) {
         z = ++z * 71 % 251;
      }

      return z;
   }

   private final long getAccessLength(int key) {
      switch (key) {
         case 0:
            return 86400000;
         case 1:
         default:
            return 259200000;
         case 2:
            return 604800000;
         case 3:
            return 1296000000;
         case 4:
            return 2592000000L;
      }
   }

   private final String getInfoString(int i) {
      switch (i) {
         case -1:
            throw new Object();
         case 0:
         default:
            return Integer.toString(Branding.getVendorId());
         case 1:
            String var12 = DeviceInfo.getPlatformVersion();
            if (var12 == null || var12.length() == 0) {
               var12 = "0.9";
            }

            return var12;
         case 2:
            String str = ApplicationDescriptor.currentApplicationDescriptor().getVersion();
            if (str == null || str.length() == 0) {
               str = "0.9";
            }

            return ((StringBuffer)(new Object())).append(str).append(" (199)").toString();
         case 3: {
            int value = DeviceInfo.getDeviceId();
            return Integer.toHexString(value);
         }
         case 4:
            if (RadioInfo.areWAFsSupported(8)) {
               return IDENInfo.imeiToString(IDENInfo.getIMEI());
            }

            return GPRSInfo.imeiToString(GPRSInfo.getIMEI());
         case 6:
            if (RadioInfo.getState() == 0) {
               return this._rb.getString(3);
            } else {
               int signalLevel = RadioInfo.getSignalLevel();
               if (signalLevel == -256) {
                  return this._rb.getString(204);
               }

               return ((StringBuffer)(new Object())).append(String.valueOf(signalLevel)).append(this._rb.getString(201)).toString();
            }
         case 7: {
            int value = DeviceInfo.getBatteryLevel();
            value = 5 * ((value + 2) / 5);
            if (value > 100) {
               value = 100;
            } else if (value < 0) {
               value = 0;
            }

            return ((StringBuffer)(new Object())).append(String.valueOf(value)).append(this._rb.getString(202)).toString();
         }
         case 8:
            return ((StringBuffer)(new Object())).append(Memory.getFlashFree()).append(this._rb.getString(203)).toString();
         case 9:
            return ((StringBuffer)(new Object())).append(Memory.getFlashTotal()).append(this._rb.getString(203)).toString();
         case 10:
            return Integer.toHexString(CDMAInfo.getESN());
         case 11:
            try {
               byte[] mac = WLAN.getMACAddress();
               if (mac != null) {
                  StringBuffer sb = (StringBuffer)(new Object(mac.length * 3));

                  for (int j = 0; j < mac.length; j++) {
                     if (sb.length() > 1) {
                        sb.append(':');
                     }

                     NumberUtilities.appendNumber(sb, mac[j], 16, 2);
                  }

                  return sb.toString();
               }
            } finally {
               ;
            }
         case 5:
            this._uptimeString = Long.toString(InternalServices.getUptime() / 1000);
            return ((StringBuffer)(new Object())).append(this._uptimeString).append(this._rb.getString(200)).toString();
      }
   }

   private final void generateHash() {
      try {
         HMAC hmac = (HMAC)(new Object((HMACKey)(new Object("Up the time stream without a TARDIS".getBytes())), (Digest)(new Object())));

         for (int i = 0; i < 5; i++) {
            hmac.reset();
            hmac.update(StringUtilities.toLowerCase(this.getInfoString(3), 1701707776).getBytes());
            hmac.update(StringUtilities.toLowerCase(this.getInfoString(2), 1701707776).getBytes());
            hmac.update(StringUtilities.toLowerCase(this._uptimeString, 1701707776).getBytes());
            switch (i) {
               case 0:
                  break;
               case 1:
               default:
                  hmac.update("Hello my baby, hello my honey, hello my rag time gal".getBytes());
                  break;
               case 2:
                  hmac.update("He was a boy, and she was a girl, can I make it any more obvious?".getBytes());
                  break;
               case 3:
                  hmac.update("So am I, still waiting, for this world to stop hating?".getBytes());
                  break;
               case 4:
                  hmac.update("I love myself today, not like yesterday. I'm cool, I'm calm, I'm gonna be okay".getBytes());
            }

            int[] accessKey = this.createAccessKey(hmac.getMAC());
            Digest digest = (Digest)(new Object());
            digest.update(93 ^ this.get(3));
            digest.update(32 ^ this.get(7));
            digest.update(192 ^ this.get(13));
            digest.update(223 ^ this.get(17));

            for (int ii = 0; ii < 8; ii++) {
               digest.update(accessKey[ii]);
            }

            this._accessKeys[i] = digest.getDigest();
         }
      } finally {
         throw new Object();
      }
   }

   public final void go() {
      this.go(false);
   }

   public final void go(boolean modal) {
      EScreenSecurityData secData = EScreenSecurityData.get(0);
      secData.checkForExpiry();
      UiApplication app = UiApplication.getUiApplication();
      if (modal) {
         app.pushModalScreen(this);
      } else {
         app.pushScreen(this);
      }
   }

   private final char nibble2Char(int nibble) {
      if (nibble >= 0 && nibble <= 9) {
         return (char)(nibble + 48);
      } else if (nibble >= 10 && nibble <= 15) {
         return (char)(nibble - 10 + 97);
      } else {
         throw new Object();
      }
   }

   private final void setupElements() {
      this._elements = (IntVector)(new Object());
      this._elements.addElement(0);
      this._elements.addElement(1);
      this._elements.addElement(2);
      this._elements.addElement(3);
      int wafs = RadioInfo.getSupportedWAFs();
      if ((wafs & 9) != 0) {
         this._elements.addElement(4);
      }

      if ((wafs & 4) != 0) {
         this._elements.addElement(11);
      }

      if ((wafs & 2) != 0) {
         this._elements.addElement(10);
      }

      this._elements.addElement(5);
      this._elements.addElement(6);
      this._elements.addElement(7);
      this._elements.addElement(8);
      this._elements.addElement(9);
   }

   private final void setupHeaders() {
      this._headers = this._rb.getStringArray(1);
      if (this._headers.length != 12) {
         this._headers = null;
      }
   }

   private final void setupHelpString() {
      byte[] data = Branding.getData(8192);
      if (data != null) {
         label19:
         try {
            this._helpString = (String)(new Object(data, "UTF8"));
            return;
         } finally {
            break label19;
         }
      }

      this._helpString = this._rb.getString(0);
   }

   private final void setupInfoStrings() {
      this._info = new Object[this._elements.size()];

      for (int i = 0; i < this._info.length; i++) {
         this._info[i] = this.getInfoString(this._elements.elementAt(i));
      }
   }

   private final void showEScreens(int accessLevel) {
      this._app.popScreen(this);
      this._app.startupEScreens(accessLevel);
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      int x = 0;
      if (this._headers != null) {
         String h = this._headers[this._elements.elementAt(index)];
         int header_length = graphics.getFont().measureText(h, 0, h.length(), null, null);
         int value_length = graphics.getFont().measureText(this._info[index], 0, this._info[index].length(), null, null);
         int calc_width = 0;
         int total_length = header_length + value_length;
         if (total_length > width) {
            calc_width = width * header_length / total_length;
         }

         graphics.drawText(h, 0, y, 70, calc_width > 0 ? calc_width : width);
         if (calc_width > 0) {
            width = width * value_length / total_length;
            x = calc_width;
         }
      }

      graphics.drawText(this._info[index], x, y, 69, width);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      StringBuffer strBuf = (StringBuffer)(new Object(32));
      if (this._headers != null) {
         strBuf.append(this._headers[this._elements.elementAt(index)]);
      }

      strBuf.append(this._info[index]);
      return strBuf;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      int al = EScreenAccess.getAccessLevel();
      if (al == 0 || al == 1) {
         menu.add(new EngScreenSecurity$MyMenuItem(this, 1));
      }

      if (RadioInfo.areWAFsSupported(8) && al == 0) {
         menu.add(new EngScreenSecurity$MyMenuItem(this, 2));
      }

      super.makeMenu(menu, instance);
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      char key = Keypad.map(Keypad.key(keycode), Keypad.status(keycode));
      boolean handled = CharacterUtilities.isLetter(key) || CharacterUtilities.isDigit(key);
      this._accessInput[this._accessInputIndex] = key;
      this._accessInputIndex = this._accessInputIndex + 1 & 7;
      key = Keypad.map(Keypad.key(keycode), Keypad.status(keycode) & -2);
      this._last4Keys <<= 8;
      this._last4Keys |= key;
      Digest digest = (Digest)(new Object());
      digest.update(93 ^ this.get(3));
      digest.update(32 ^ this.get(7));
      digest.update(192 ^ this.get(13));
      digest.update(223 ^ this.get(17));
      int index = this._accessInputIndex;

      for (int i = 0; i < 8; i++) {
         digest.update(this._accessInput[index]);
         if (++index >= 8) {
            index = 0;
         }
      }

      byte[] inputHash = digest.getDigest();

      for (int i = 4; i >= 0; i--) {
         if (Arrays.equals(this._accessKeys[i], inputHash)) {
            EScreenSecurityData.get(0).allowAccess(this.getAccessLength(i));
            this.showEScreens(1);
            return true;
         }
      }

      if (this._last4Keys == 2053205356) {
         if (RadioInfo.areWAFsSupported(8)) {
            this.showEScreens(2);
         }

         return true;
      } else {
         if (!handled) {
            handled = super.keyDown(keycode, time);
         }

         return handled;
      }
   }

   private final int getMenuId(int id) {
      switch (id) {
         case 0:
            return 0;
         case 1:
         default:
            return 101;
         case 2:
            return 102;
      }
   }
}
