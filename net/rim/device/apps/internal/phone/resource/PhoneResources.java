package net.rim.device.apps.internal.phone.resource;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;

public class PhoneResources {
   private static ResourceBundleFamily _resources = ResourceBundle.getBundle(2699923441625099942L, "net.rim.device.apps.internal.resource.Phone");
   private static final int NUM_PHONE_CALL_TYPES = 8;
   private static final int[] CALL_TYPE_RES_IDS = new int[]{
      120,
      121,
      122,
      122,
      113,
      6019,
      6019,
      6021,
      -805040882,
      1196314761,
      169478669,
      218103808,
      1380206665,
      234881024,
      234881024,
      1544,
      759701248,
      209,
      1497919497,
      184549491,
      184549395,
      -1711275757,
      6300,
      1130974986,
      1750093891,
      1936684143,
      544239464,
      541279049,
      1718579824,
      6646889,
      -1646626816,
      1398040403
   };
   public static final int ICON_CALL_LOG_INCOMING = 0;
   public static final int ICON_CALL_LOG_OUTGOING = 1;
   public static final int ICON_MISSED_CALL_UNOPENED = 2;
   public static final int ICON_MISSED_CALL_OPENED = 3;
   public static final int ICON_CONFERENCE_CALL_LOG = 4;
   public static final int ICON_ALERT_UNHANDLED = 5;
   public static final int ICON_ALERT_HANDLED = 6;
   public static final int ICON_CALL_LOG_DC = 7;
   public static final int ICON_DTMF_PAUSE = 8;
   public static final int ICON_DTMF_WAIT = 9;
   public static final int ICON_PADLOCK = 10;
   public static final int ICON_CALL_ACTIVE = 11;
   public static final int ICON_CALL_MUTED = 12;
   public static final int ICON_CALL_FORWARDING = 13;
   public static final int ICON_CALL_NOTES = 14;
   public static final int ICON_SEND_KEY = 15;
   public static final int ICON_END_KEY = 16;
   public static final int ICON_COUNT = 17;
   public static final String BITMAP_ANSWERIGNORE240 = "AnswerIgnoreBanner240.png";
   public static final String BITMAP_ANSWERIGNORE320 = "AnswerIgnoreBanner320.png";
   public static final String BITMAP_ANSWERICON = "net_rim_Phone-Send.png";
   public static final String BITMAP_ANSWERBTICON = "net_rim_Phone-SendBT.png";
   public static final String BITMAP_IGNOREICON = "net_rim_Phone-End.png";
   public static final String BITMAP_MOREICON = "net_rim_Phone-More.png";
   private static IconCollection _icons = IconCollection.get("net_rim_Phone", 17);

   public static ResourceBundleFamily getResourceBundle() {
      return _resources;
   }

   public static IconCollection getECAIcons() {
      return IconCollection.get("net_rim_Phone_ECA", 5);
   }

   public static String getCallTypeString(int callType) {
      return callType >= 0 && callType < 8 ? getString(CALL_TYPE_RES_IDS[callType]) : "";
   }

   public static IconCollection getIcons() {
      return _icons;
   }

   public static int getIconWidth(Font font, int index) {
      return _icons.getWidth(font);
   }

   public static Image getImage(int index) {
      return _icons.getImage(index);
   }

   public static int drawIcon(Graphics g, int x, int y, int index) {
      return _icons.paint(g, x, y, index);
   }

   public static int drawIcon(Graphics g, int x, int y, int availableHeight, int index) {
      return _icons.paint(g, x, y, availableHeight, index);
   }

   public static String getString(int id) {
      return _resources.getString(id);
   }

   public static String getString(int id, int index) {
      return _resources.getStringArray(id)[index];
   }

   public static Bitmap getBitmap(String name) {
      return Bitmap.getBitmapResource(name);
   }

   public static Object getObject(int id) {
      return _resources.getObject(id);
   }
}
