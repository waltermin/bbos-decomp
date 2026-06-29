package net.rim.device.apps.api.messaging;

import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.IconCollection;

public final class MessageIcons {
   public static final int SPACE = 0;
   public static final int RIM_RX_UNREAD = 1;
   public static final int RIM_RX_READ = 2;
   public static final int RIM_RX_ERROR = 3;
   public static final int RIM_TX_COMPOSING = 4;
   public static final int RIM_TX_COMPRESSING = 5;
   public static final int RIM_TX_ENCRYPTING = 6;
   public static final int RIM_TX_PENDING = 7;
   public static final int RIM_TX_SENDING = 8;
   public static final int RIM_TX_SENT = 9;
   public static final int RIM_TX_DELIVERED = 10;
   public static final int RIM_TX_READ_ACKED = 11;
   public static final int RIM_TX_ERROR = 12;
   public static final int RIM_UNREAD_FOLDER = 13;
   public static final int RIM_FOLDER = 14;
   public static final int RIM_RX_RINGTONE_UNREAD = 15;
   public static final int RIM_RX_RINGTONE_READ = 16;
   public static final int RIM_RX_PAGE_UNOPENED = 17;
   public static final int RIM_RX_PAGE_OPENED = 18;
   public static final int RIM_RX_MSGATTACH_UNREAD = 19;
   public static final int RIM_RX_MSGATTACH_READ = 20;
   private static final int RIM_SMS = 65536;
   public static final int SMS_SPACE = 65536;
   public static final int RIM_SMS_RX_UNREAD = 65537;
   public static final int RIM_SMS_RX_READ = 65538;
   public static final int RIM_SMS_RX_ERROR = 65539;
   public static final int RIM_SMS_TX_COMPOSING = 65540;
   public static final int RIM_SMS_TX_COMPRESSING = 65541;
   public static final int RIM_SMS_TX_ENCRYPTING = 65542;
   public static final int RIM_SMS_TX_PENDING = 65543;
   public static final int RIM_SMS_TX_SENDING = 65544;
   public static final int RIM_SMS_TX_SENT = 65545;
   public static final int RIM_SMS_TX_DELIVERED = 65546;
   public static final int RIM_SMS_TX_READ_ACKED = 65547;
   public static final int RIM_SMS_TX_ERROR = 65548;
   public static final int RIM_SMS_UNREAD_FOLDER = 65549;
   public static final int RIM_SMS_FOLDER = 65550;
   public static final int RIM_SMS_RX_RINGTONE_UNREAD = 65551;
   public static final int RIM_SMS_RX_RINGTONE_READ = 65552;
   private static final int RIM_SECURITY = 131072;
   public static final int RIM_SECURITY_RX_UNREAD = 131073;
   public static final int RIM_SECURITY_RX_READ = 131074;
   public static final int RIM_SECURITY_RX_RECEIPT_UNREAD = 131076;
   public static final int RIM_SECURITY_RX_RECEIPT_READ = 131077;
   private static final int RIM_MMS = 196608;
   public static final int MMS_SPACE = 196608;
   public static final int RIM_MMS_RX_UNREAD = 196609;
   public static final int RIM_MMS_RX_READ = 196610;
   public static final int RIM_MMS_RX_ERROR = 196611;
   public static final int RIM_MMS_TX_COMPOSING = 196612;
   public static final int RIM_MMS_TX_COMPRESSING = 196613;
   public static final int RIM_MMS_TX_ENCRYPTING = 196614;
   public static final int RIM_MMS_TX_PENDING = 196615;
   public static final int RIM_MMS_TX_SENDING = 196616;
   public static final int RIM_MMS_TX_SENT = 196617;
   public static final int RIM_MMS_TX_DELIVERED = 196618;
   public static final int RIM_MMS_TX_READ_ACKED = 196619;
   public static final int RIM_MMS_TX_ERROR = 196620;
   public static final int RIM_MMS_UNREAD_FOLDER = 196621;
   public static final int RIM_MMS_FOLDER = 196622;
   private static IconCollection _icons = IconCollection.get("net_rim_Message", 21, 4);

   private MessageIcons() {
   }

   public static final IconCollection getIcons() {
      return _icons;
   }

   public static final void paint(Graphics graphics, int x, int y, int width, int height, int icon) {
      _icons.paint(graphics, x, y, width, height, icon);
   }
}
