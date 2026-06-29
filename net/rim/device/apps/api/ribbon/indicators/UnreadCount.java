package net.rim.device.apps.api.ribbon.indicators;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.ribbon.Action;
import net.rim.device.apps.api.utility.props.StringProps;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.ImageBitmap;
import net.rim.device.internal.ui.ImageOverlay;
import net.rim.device.internal.ui.ScaleBitmap;

public class UnreadCount implements Indicator, CountIndicator {
   private int _unreadCount;
   private IconCollection _icons = IconCollection.get("net_rim_Ribbon_UnreadCount", 9);
   private int _iconIndex;
   private int _typeIndex;
   private int _priority;
   private StringBuffer _workBuffer = (StringBuffer)(new Object());
   private boolean _visible = true;
   StringProps _action;
   boolean _mergeWithText;
   private static final int ICON_MESSAGE;
   private static final int ICON_MESSAGE_FILED;
   private static final int ICON_VOICE;
   private static final int ICON_BROWSER;
   private static final int ICON_CALENDAR;
   private static final int ICON_IM;
   private static final int ICON_SMS;
   private static final int ICON_ANONYMOUS;
   private static final int ICON_TASK;
   private static final int ICON_COUNT;
   public static final int TYPE_MESSAGE;
   public static final int TYPE_MESSAGE_FILED;
   public static final int TYPE_VOICE;
   public static final int TYPE_BROWSER;
   public static final int TYPE_CALENDAR;
   public static final int TYPE_IM;
   public static final int TYPE_SMS;
   public static final int TYPE_ANONYMOUS;
   public static final int TYPE_TASK;
   public static final int TYPE_SMSMMS;
   private static final int TYPE_COUNT;
   private static final String[] _typeName = new String[]{
      "message", "messagefiled", "voice", "browser", "calendar", "im", "sms", "anonymous", "task", "smsmms"
   };
   private static final int[] _typeIconIndex = new int[]{
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      6,
      -805044213,
      775162112,
      774909491,
      3420721,
      -805044199,
      1699878656,
      1918985587,
      1226860643,
      1867325550,
      1852795252,
      1685343264,
      46,
      -805044223,
      2,
      -805044208,
      1718183726,
      1852845578,
      1663961703,
      174486626,
      -804651006,
      51,
      4342354,
      -805044163,
      944130375,
      1007929,
      8388616,
      0,
      570425343,
      66809,
      738197760
   };
   public static final String TYPE_NEW_MESSAGE;
   public static final String TYPE_UNREAD_MESSAGE;

   public boolean hasNewStatus() {
      String appState = null;
      if (this._action != null) {
         appState = this._action.get(2, (String)((Object)null));
      }

      return appState != null && "new".equals(appState);
   }

   protected boolean getVisible() {
      return this._visible;
   }

   public void setVisible(boolean visible) {
      this._visible = visible;
   }

   protected boolean displayIcon() {
      return this._unreadCount > 0 && this._visible;
   }

   protected boolean drawCount() {
      return this._unreadCount > 0;
   }

   protected int setUnreadCount(int unreadCount, boolean updateUI) {
      if (this._unreadCount != unreadCount) {
         this._unreadCount = unreadCount;
         if (this._unreadCount < 0) {
            label29:
            try {
               throw new Object(((StringBuffer)(new Object("Unread count is invalid: "))).append(this._unreadCount).toString());
            } finally {
               this._unreadCount = 0;
               break label29;
            }
         }

         if (updateUI && this._action instanceof Action) {
            Action a = (Action)this._action;
            a.update();
         }
      }

      return this._unreadCount;
   }

   public int getUnreadCount() {
      return this._unreadCount;
   }

   public synchronized int modifyUnreadCount(int byHowMany) {
      this.setUnreadCount(this._unreadCount + byHowMany, true);
      if (this._action != null) {
         this._action.set(2, byHowMany > 0 ? "new" : null);
      }

      return this._unreadCount;
   }

   @Override
   public String getTypeName() {
      return this._typeIndex > -1 && this._typeIndex < 10 ? _typeName[this._typeIndex] : "";
   }

   @Override
   public int getPriority() {
      return this._priority;
   }

   @Override
   public int getHeight(Graphics graphics) {
      return graphics.getFont().getHeight();
   }

   @Override
   public int getWidth(Graphics graphics) {
      if (!this.displayIcon()) {
         return 0;
      }

      synchronized (this._workBuffer) {
         this._workBuffer.setLength(0);
         this._workBuffer.append(this.getCount());
      }

      Font curFont = graphics.getFont();
      return curFont.getBounds(this._workBuffer, 0, this._workBuffer.length()) + this.getGap(curFont) + this._icons.getWidth(curFont);
   }

   @Override
   public int getCount() {
      return this._unreadCount;
   }

   @Override
   public int draw(Graphics graphics, int width, int height, int flags) {
      synchronized (this._workBuffer) {
         int drawWidth = this.getWidth(graphics);
         this._workBuffer.setLength(0);
         if (this.drawCount()) {
            this._workBuffer.append(this.getCount());
         }

         int xpos = 0;
         if ((flags & 1) != 0) {
            xpos = width - drawWidth;
         }

         if ((flags & 1) != 0) {
            xpos += this.drawIcon(graphics, xpos);
            xpos += this.getGap(graphics.getFont());
         }

         if ((flags & 4) == 0) {
            xpos += graphics.drawText(this._workBuffer, 0, this._workBuffer.length(), xpos, 0, flags, width);
         }

         if ((flags & 2) != 0) {
            if ((flags & 4) == 0) {
               xpos += this.getGap(graphics.getFont());
            }

            xpos += this.drawIcon(graphics, xpos);
         }

         return drawWidth;
      }
   }

   private int drawIcon(Graphics graphics, int xpos) {
      if (!this.hasNewStatus()) {
         return this._icons.paint(graphics, xpos, 0, this._iconIndex);
      }

      Image image = this._icons.getImage(this._iconIndex);
      Font font = graphics.getFont();
      int iconWidth = this._icons.getWidth(font);
      int iconHeight = this._icons.getHeight(font);
      EncodedImage overlay = ThemeManager.getActiveTheme().getImage("new_overlay_banner", true);
      if (overlay != null) {
         Bitmap bitmap = overlay.getBitmap();
         if (bitmap.getWidth() != iconWidth) {
            bitmap = ScaleBitmap.scaleBitmap(bitmap, iconWidth, bitmap.getHeight() * iconWidth / bitmap.getWidth());
         }

         image = ImageOverlay.create(image, ImageBitmap.create(bitmap));
      }

      image.paint(graphics, xpos, 0, iconWidth, iconHeight);
      return iconWidth;
   }

   public UnreadCount(int typeIndex, int priority) {
      this._typeIndex = typeIndex;
      this._iconIndex = _typeIconIndex[this._typeIndex];
      this._priority = priority;
   }

   private int getGap(Font font) {
      return font.getFontFamily().getName().equals("System") ? 0 : font.getHeight() / 8;
   }

   public static String getTypeName(int typeIndex) {
      return _typeName[typeIndex];
   }
}
