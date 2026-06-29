package net.rim.device.apps.internal.lbs;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontRegistry;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

public final class GPSMode extends AbstractMode {
   private int _msgType = 3;
   private int _satCount = 0;
   private String _bodyMsg = null;
   private Font _headerFont;
   private int _speed;
   private int _bearing;
   private Font _dashboardStreetFont = null;
   private static final String[] DIRECTION = new Object[]{
      LBSResources.getString(186),
      LBSResources.getString(187),
      LBSResources.getString(188),
      LBSResources.getString(189),
      LBSResources.getString(190),
      LBSResources.getString(191),
      LBSResources.getString(192),
      LBSResources.getString(193),
      LBSResources.getString(186)
   };
   public static final int TYPE_GPS;
   public static final int TYPE_STREET_ON;
   public static final int TYPE_STREET_NEXT;
   public static final int TYPE_STREET_INVALID;
   private static final Bitmap SATELLITE = Bitmap.getBitmapResource("images/satellite.png");
   private static final Bitmap SATELLITE_NO_GPS = Bitmap.getBitmapResource("images/satellite_no_GPS.png");

   public GPSMode() {
      FontRegistry.loadFont("MediaMedium.cbtf", "net_rim_bb_lbs", "MediaMedium");
      this._headerFont = FontRegistry.get("MediaMedium").getFont(0, 14, 0, 2, 0);
      this._dashboardStreetFont = FontRegistry.get("BBClarity").getFont(1, 20, 0, 2, 0);
      int height = this._dashboardStreetFont.getHeight();
      if (Display.getWidth() < 320) {
         height = -4;
      }

      this._dashboardStreetFont = this._dashboardStreetFont.derive(1, height, 0, 2, 2560, 65536, 0, 0, 65536, 0, 0, 197379, 16053492);
   }

   public final void setSatelliteCount(int count) {
      this._satCount = count;
   }

   public final void setMessage(String message, int type) {
      this._bodyMsg = message;
      this._msgType = type;
   }

   public final void setSpeed(float speed) {
      this._speed = (int)(speed + 1056964608);
   }

   public final void setBearing(int bearing) {
      this._bearing = bearing;
   }

   public final int getMessageType() {
      return this._msgType;
   }

   @Override
   public final void paintHeader(Graphics g, int w, int h) {
      String speed = null;
      int baseline = this._headerFont.getBaseline();
      TextMetrics metrics = (TextMetrics)(new Object());
      int above = 0;
      int below = 0;
      int height = 0;
      g.setColor(16777215);
      switch (this._msgType) {
         case 1:
         case 2:
         default:
            if (this._bodyMsg != null && this._bodyMsg.equals("")) {
            }
         case 3:
            if (LBSOptions.getInt(-6817208986109478597L, 2) == 1) {
               speed = ((StringBuffer)(new Object(""))).append(this._speed).toString();
            } else {
               speed = ((StringBuffer)(new Object(""))).append((int)(this._speed * 62 / 1120403456)).toString();
            }

            speed = ((StringBuffer)(new Object()))
               .append(speed)
               .append(" ")
               .append(LBSResources.getResourceBundle().getStringArray(136)[LBSOptions.getInt(-6817208986109478597L, 2) - 1])
               .toString();
         case 0:
            g.setColor(16777215);
            Bitmap img = null;
            if (this._satCount > 0) {
               img = SATELLITE;
            } else {
               img = SATELLITE_NO_GPS;
            }

            g.setFont(this._headerFont);
            g.setGlobalAlpha(255);
            if (speed != null) {
               g.drawText(speed, 0, speed.length(), 0, h >> 1, 36, 75);
            }

            int x = w / 2;
            String lbl = ((StringBuffer)(new Object(" "))).append(this._satCount).toString();
            int lblWidth = img.getWidth() + this._headerFont.getAdvance(lbl);
            x -= lblWidth / 2;
            g.drawBitmap(x, 0, img.getWidth(), img.getHeight(), img, 0, 0);
            x += img.getWidth();
            g.drawText(lbl, 0, lbl.length(), x, h >> 1, 32, 30);
            if (this._speed > 0) {
               lbl = DIRECTION[(int)Math.floor((this._bearing + 4627026404658118656L) * 4620693217682128896L / 4645040803167600640L)];
               synchronized (metrics) {
                  this._headerFont.measureText(lbl, 0, lbl.length(), null, metrics);
                  above = Math.max(-metrics.iBoundsTlY, baseline);
                  below = Math.max(metrics.iBoundsBrY, this._headerFont.getDescent());
                  height = Math.max(this._headerFont.getHeight(), above + below);
               }

               if (height > 20) {
                  g.setFont(FontRegistry.get("BBClarity").getFont(1, 16, 0, 2, 0).derive(1, 11, 0, 2, 2560, 65536, 0, 0, 65536, 0, 0, 197379, 16053492));
               }

               g.drawText(lbl, 0, lbl.length(), w - 75, h >> 1, 36, 75);
            }
         case -1:
      }
   }

   @Override
   public final void paintBody(Graphics g, int w, int h) {
      int yContent = 1;
      int labelColour = 16777215;
      String label = this._bodyMsg;
      if (label != null && !label.equals("")) {
         int y = yContent + h / 2 - this._dashboardStreetFont.getHeight() / 2;
         g.setFont(this._dashboardStreetFont);
         g.setColor(labelColour);
         g.drawText(label, 0, label.length(), 0, y, 4, w);
      }
   }

   @Override
   public final boolean isScreen() {
      return false;
   }

   @Override
   protected final boolean screenViewSupported() {
      return false;
   }

   @Override
   protected final Screen getScreen() {
      return null;
   }

   @Override
   protected final boolean hasHeaderToPaint() {
      switch (this._msgType) {
         case -1:
            return false;
         case 0:
         default:
            return false;
         case 1:
            return true;
         case 2:
            return true;
         case 3:
            return true;
      }
   }
}
