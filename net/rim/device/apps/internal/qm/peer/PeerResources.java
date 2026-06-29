package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.internal.ui.IconCollection;

final class PeerResources implements PeerResource {
   static final long BUG_REPORT_ITEM = -1390867101630436713L;
   protected static ResourceBundle _rb = ResourceBundle.getBundle(251742186781095532L, "net.rim.device.apps.internal.qm.peer.Peer");
   public static final int ICON_UNAVAILABLE = 0;
   public static final int ICON_AVAILABLE = 1;
   public static final int ICON_UNREAD_UNAVAILABLE = 2;
   public static final int ICON_UNREAD_AVAILABLE = 3;
   public static final int ICON_MULTICHAT = 4;
   public static final int ICON_UNREAD_MULTICHAT = 5;
   public static final int ICON_BUSY = 6;
   public static final int ICON_DND = 7;
   public static final int ICON_UNREACHABLE = 8;
   public static final int ICON_NEW_CONTACT = 9;
   public static final int ICON_ALERT = 10;
   public static final int ICON_UNREAD = 11;
   public static final int ICON_TYPING = 12;
   public static final int ICON_COLLAPSED = 14;
   public static final int ICON_EXPANDED = 15;
   public static final int NUMBER_OF_ICONS = 16;
   private static IconCollection _icons = IconCollection.get("net_rim_Messenger", 16);
   public static final int MESSAGE_STATE_PENDING = 7;
   public static final int MESSAGE_STATE_SENDING = 8;
   public static final int MESSAGE_STATE_SENT = 9;
   public static final int MESSAGE_STATE_DELIVERED = 10;
   public static final int MESSAGE_STATE_READ = 11;

   static final String getString(int id) {
      try {
         return _rb.getString(id);
      } finally {
         ;
      }
   }

   public static final String format(int id, String arg1) {
      String format = getString(id);
      return MessageFormat.format(format, new Object[]{arg1});
   }

   public static final String format(int id, String arg1, String arg2) {
      String format = getString(id);
      return MessageFormat.format(format, new Object[]{arg1, arg2});
   }

   static final int getIconHeight(Font font) {
      return _icons.getHeight(font);
   }

   static final int iconIndent(Font font) {
      return getIconHeight(font) < 15 ? 8 : 9;
   }

   static final int drawIcon(Graphics g, int x, int y, int id) {
      return _icons.paint(g, x, y, getIconHeight(g.getFont()), id);
   }

   public static final int drawMessageIcon(Graphics g, int x, int y, int id) {
      int height = g.getFont().getHeight();
      MessageIcons.paint(g, x, y, height, height, id);
      return height;
   }

   public static final void onFontChanged() {
   }
}
