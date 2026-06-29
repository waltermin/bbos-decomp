package net.rim.device.apps.api.messaging;

import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.IconCollection;

public final class MessageIcons {
   public static final int SPACE;
   public static final int RIM_RX_UNREAD;
   public static final int RIM_RX_READ;
   public static final int RIM_RX_ERROR;
   public static final int RIM_TX_COMPOSING;
   public static final int RIM_TX_COMPRESSING;
   public static final int RIM_TX_ENCRYPTING;
   public static final int RIM_TX_PENDING;
   public static final int RIM_TX_SENDING;
   public static final int RIM_TX_SENT;
   public static final int RIM_TX_DELIVERED;
   public static final int RIM_TX_READ_ACKED;
   public static final int RIM_TX_ERROR;
   public static final int RIM_UNREAD_FOLDER;
   public static final int RIM_FOLDER;
   public static final int RIM_RX_RINGTONE_UNREAD;
   public static final int RIM_RX_RINGTONE_READ;
   public static final int RIM_RX_PAGE_UNOPENED;
   public static final int RIM_RX_PAGE_OPENED;
   public static final int RIM_RX_MSGATTACH_UNREAD;
   public static final int RIM_RX_MSGATTACH_READ;
   private static final int RIM_SMS;
   public static final int SMS_SPACE;
   public static final int RIM_SMS_RX_UNREAD;
   public static final int RIM_SMS_RX_READ;
   public static final int RIM_SMS_RX_ERROR;
   public static final int RIM_SMS_TX_COMPOSING;
   public static final int RIM_SMS_TX_COMPRESSING;
   public static final int RIM_SMS_TX_ENCRYPTING;
   public static final int RIM_SMS_TX_PENDING;
   public static final int RIM_SMS_TX_SENDING;
   public static final int RIM_SMS_TX_SENT;
   public static final int RIM_SMS_TX_DELIVERED;
   public static final int RIM_SMS_TX_READ_ACKED;
   public static final int RIM_SMS_TX_ERROR;
   public static final int RIM_SMS_UNREAD_FOLDER;
   public static final int RIM_SMS_FOLDER;
   public static final int RIM_SMS_RX_RINGTONE_UNREAD;
   public static final int RIM_SMS_RX_RINGTONE_READ;
   private static final int RIM_SECURITY;
   public static final int RIM_SECURITY_RX_UNREAD;
   public static final int RIM_SECURITY_RX_READ;
   public static final int RIM_SECURITY_RX_RECEIPT_UNREAD;
   public static final int RIM_SECURITY_RX_RECEIPT_READ;
   private static final int RIM_MMS;
   public static final int MMS_SPACE;
   public static final int RIM_MMS_RX_UNREAD;
   public static final int RIM_MMS_RX_READ;
   public static final int RIM_MMS_RX_ERROR;
   public static final int RIM_MMS_TX_COMPOSING;
   public static final int RIM_MMS_TX_COMPRESSING;
   public static final int RIM_MMS_TX_ENCRYPTING;
   public static final int RIM_MMS_TX_PENDING;
   public static final int RIM_MMS_TX_SENDING;
   public static final int RIM_MMS_TX_SENT;
   public static final int RIM_MMS_TX_DELIVERED;
   public static final int RIM_MMS_TX_READ_ACKED;
   public static final int RIM_MMS_TX_ERROR;
   public static final int RIM_MMS_UNREAD_FOLDER;
   public static final int RIM_MMS_FOLDER;
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
