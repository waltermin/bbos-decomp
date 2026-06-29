package net.rim.device.apps.internal.ribbon;

import java.util.Enumeration;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.ribbon.launcher.FolderEntryPointDescriptor;
import net.rim.device.internal.ui.ScaleBitmap;

final class FilmstripField extends ButtonField {
   private String _url;
   private int _currentImageIndex;
   private int _lastImageIndex;
   private Bitmap[] _images;
   private String[] _names;
   private Bitmap _currentBitmap;
   private Bitmap _previousBitmap;
   private Bitmap _nextBitmap;
   private int _width;
   private int _iconHeight;
   private int _iconWidth;
   private boolean _active;

   FilmstripField(long style, String url) {
      super(style);
      this._url = url;
   }

   @Override
   public final void layout(int width, int height) {
      this._iconWidth = ThemeManager.getActiveTheme().getRibbonIconWidth();
      this._iconHeight = ThemeManager.getActiveTheme().getRibbonIconHeight();
      this._width = this._iconWidth * 2 - 10;
      this.setExtent(this._width, this._iconHeight);
   }

   @Override
   protected final void paint(Graphics g) {
      if (this._active) {
         g.setGlobalAlpha(40);
         g.fillRoundRect(0, 0, this._width, this._iconHeight, 5, 5);
         g.setGlobalAlpha(255);
      }

      int naviX = this._width / 2 - this._iconWidth / 2 - 5;
      int currentX = this._width / 2 - this._currentBitmap.getWidth() / 2;
      int currentY = this._iconHeight / 2 - this._currentBitmap.getHeight() / 2;
      g.drawBitmap(currentX, currentY, this._currentBitmap.getWidth(), this._currentBitmap.getHeight(), this._currentBitmap, 0, 0);
      if (this._active) {
         if (this._currentImageIndex > 0) {
            int previousX = naviX - this._previousBitmap.getWidth();
            int previousY = this._iconHeight / 2 - this._previousBitmap.getHeight() / 2;
            g.drawBitmap(previousX, previousY, this._previousBitmap.getWidth(), this._previousBitmap.getHeight(), this._previousBitmap, 0, 0);
         }

         if (this._currentImageIndex < this._images.length - 1) {
            int nextX = naviX + this._iconWidth + 10;
            int nextY = this._iconHeight / 2 - this._nextBitmap.getHeight() / 2;
            g.drawBitmap(nextX, nextY, this._nextBitmap.getWidth(), this._nextBitmap.getHeight(), this._nextBitmap, 0, 0);
         }
      }
   }

   @Override
   protected final boolean trackwheelRoll(int dy, int status, int time) {
      if (this._active) {
         this._currentImageIndex += dy;
         if (this._currentImageIndex < 0) {
            this._currentImageIndex = 0;
         }

         if (this._currentImageIndex >= this._images.length) {
            this._currentImageIndex = this._images.length - 1;
         }

         this.updateImages();
         this.invalidate();
         return true;
      } else {
         return super.trackwheelRoll(dy, status, time);
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      if (this._active) {
         this._currentImageIndex += dx + dy;
         if (this._currentImageIndex < 0) {
            this._currentImageIndex = 0;
         }

         if (this._currentImageIndex >= this._images.length) {
            this._currentImageIndex = this._images.length - 1;
         }

         this.updateImages();
         this.invalidate();
         return true;
      } else {
         return super.navigationMovement(dx, dy, status, time);
      }
   }

   @Override
   protected final boolean navigationClick(int status, int time) {
      this._active = !this._active;
      if (this._active) {
         this._lastImageIndex = this._currentImageIndex;
         this.invalidate();
         return true;
      } else {
         this.invalidate();
         return super.navigationClick(status, time);
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == 27 && this._active) {
         this._currentImageIndex = this._lastImageIndex;
         this._active = false;
         this.updateImages();
         this.invalidate();
         return true;
      } else {
         return super.keyChar(key, status, time);
      }
   }

   public final String getFolderImageName() {
      return this._currentImageIndex == 0 ? null : this._names[this._currentImageIndex];
   }

   public final void setCurrentImage(String name) {
      this._currentImageIndex = 0;
      if (name != null) {
         for (int i = 0; i < this._names.length; i++) {
            if (this._names[i].equals(name.trim())) {
               this._currentImageIndex = i;
               break;
            }
         }
      }

      this.updateImages();
   }

   public final void loadImages() {
      Bitmap temp = null;
      this._images = new Object[0];
      this._names = new Object[0];
      Theme theme = ThemeManager.getActiveTheme();
      this._iconWidth = theme.getRibbonIconWidth();
      this._iconHeight = theme.getRibbonIconHeight();
      EncodedImage defaultImage = theme.getImage("folder", true);
      if (defaultImage != null) {
         temp = defaultImage.getBitmap();
         Arrays.add(this._images, temp);
         Arrays.add(this._names, "folder");
      }

      FileConnection conn = null;

      label45:
      try {
         conn = (FileConnection)Connector.open(this._url);
         if (conn.isDirectory()) {
            Enumeration names = conn.list();

            while (names.hasMoreElements()) {
               String name = (String)names.nextElement();
               temp = FolderEntryPointDescriptor.getScaledImage(((StringBuffer)(new Object())).append(this._url).append(name).toString());
               Arrays.add(this._images, temp);
               Arrays.add(this._names, name);
            }
         }

         conn.close();
      } finally {
         break label45;
      }

      this._lastImageIndex = this._currentImageIndex;
      this.updateImages();
      this._active = false;
   }

   private final void updateImages() {
      this._currentBitmap = this.getThemedBitmap(this._images[this._currentImageIndex]);
      Theme theme = ThemeManager.getActiveTheme();
      this._previousBitmap = theme.getBitmap("navigation_left_arrow");
      this._nextBitmap = theme.getBitmap("navigation_right_arrow");
   }

   private final Bitmap getThemedBitmap(Bitmap originalBitmap) {
      Theme theme = ThemeManager.getActiveTheme();
      int customBitmapWidth = originalBitmap.getWidth();
      int customBitmapHeight = originalBitmap.getHeight();
      if (customBitmapWidth == theme.getRibbonIconWidth() && customBitmapHeight == theme.getRibbonIconHeight()) {
         return originalBitmap;
      }

      customBitmapWidth = theme.getRibbonIconWidth();
      customBitmapHeight = theme.getRibbonIconHeight();
      int origW = originalBitmap.getWidth();
      int origH = originalBitmap.getHeight();
      Bitmap scaledCustomBitmap;
      if (origW > origH) {
         scaledCustomBitmap = ScaleBitmap.scaleBitmap(1, originalBitmap, customBitmapWidth, customBitmapWidth * origH / origW);
      } else {
         scaledCustomBitmap = ScaleBitmap.scaleBitmap(1, originalBitmap, customBitmapHeight * origW / origH, customBitmapHeight);
      }

      return scaledCustomBitmap;
   }
}
