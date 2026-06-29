package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.internal.ui.IconCollection;

final class PeerResources implements PeerResource {
   static final long BUG_REPORT_ITEM;
   protected static ResourceBundle _rb = ResourceBundle.getBundle(251742186781095532L, "net.rim.device.apps.internal.qm.peer.Peer");
   public static final int ICON_UNAVAILABLE;
   public static final int ICON_AVAILABLE;
   public static final int ICON_UNREAD_UNAVAILABLE;
   public static final int ICON_UNREAD_AVAILABLE;
   public static final int ICON_MULTICHAT;
   public static final int ICON_UNREAD_MULTICHAT;
   public static final int ICON_BUSY;
   public static final int ICON_DND;
   public static final int ICON_UNREACHABLE;
   public static final int ICON_NEW_CONTACT;
   public static final int ICON_ALERT;
   public static final int ICON_UNREAD;
   public static final int ICON_TYPING;
   public static final int ICON_COLLAPSED;
   public static final int ICON_EXPANDED;
   public static final int NUMBER_OF_ICONS;
   private static IconCollection _icons = IconCollection.get("net_rim_Messenger", 16);
   public static final int MESSAGE_STATE_PENDING;
   public static final int MESSAGE_STATE_SENDING;
   public static final int MESSAGE_STATE_SENT;
   public static final int MESSAGE_STATE_DELIVERED;
   public static final int MESSAGE_STATE_READ;

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
