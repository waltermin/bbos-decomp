package net.rim.device.internal.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.vm.TraceBack;

public class IconCollection {
   private int _columns;
   private int _rows;
   private String _moduleName;
   private EncodedImage[] _defaultImages = new EncodedImage[0];
   private int[] _defaultHeights = new int[0];
   private int[] _defaultWidths = new int[0];
   private EncodedImage[] _images = new EncodedImage[0];
   private int[] _heights = new int[0];
   private int[] _widths = new int[0];
   private boolean _hasThemedImages;
   private Bitmap _cachedBitmap;
   private int _cachedIndex;
   private int _iconWidth;
   private Font _currentFont;

   public IconCollection(int columns, int rows) {
      this(columns, rows, null);
   }

   public IconCollection(int columns, int rows, String moduleName) {
      if (columns > 0 && rows > 0) {
         this._columns = columns;
         this._rows = rows;
         this._moduleName = moduleName;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public synchronized void addImage(EncodedImage image, int width, int height, boolean isDefault) {
      if (image.getWidth() % width == 0 && image.getHeight() % height == 0) {
         int decodeMode = image.getDecodeMode();
         image.setDecodeMode(decodeMode | 8);
         if (isDefault) {
            int columns = image.getWidth() / width;
            int rows = image.getHeight() / height;
            if ((columns != this._columns || rows != this._rows) && this._defaultImages.length <= 0) {
               this._columns = columns;
               this._rows = rows;
            }

            if (width * this._columns == image.getWidth() && height * this._rows != image.getHeight()) {
            }

            int index = Arrays.binarySearch(this._defaultHeights, height);
            if (index < 0) {
               index = -index - 1;
               Arrays.insertAt(this._defaultWidths, width, index);
               Arrays.insertAt(this._defaultHeights, height, index);
               Arrays.insertAt(this._defaultImages, image, index);
            }

            if (this._hasThemedImages) {
               return;
            }
         } else if (!this._hasThemedImages) {
            this._hasThemedImages = true;
            this._heights = new int[0];
            this._widths = new int[0];
            this._images = new EncodedImage[0];
            this._cachedIndex = -1;
         }

         int index = Arrays.binarySearch(this._heights, height);
         if (index >= 0) {
            if (isDefault) {
               return;
            }
         } else {
            index = -index - 1;
            Arrays.insertAt(this._widths, width, index);
            Arrays.insertAt(this._heights, height, index);
            Arrays.insertAt(this._images, image, index);
            if (index <= this._cachedIndex) {
               this._cachedIndex++;
            }
         }

         this._widths[index] = width;
         this._heights[index] = height;
         this._images[index] = image;
         if (index == this._cachedIndex) {
            this._cachedIndex = -1;
            this._cachedBitmap = null;
         }
      } else {
         throw new IllegalArgumentException("Invalid width or height");
      }
   }

   void addImage(byte[] imageBytes, boolean isDefault) {
   }

   public synchronized void clear() {
      this._heights = Arrays.copy(this._defaultHeights);
      this._widths = Arrays.copy(this._defaultWidths);
      this._images = new EncodedImage[this._defaultImages.length];

      for (int lv = this._defaultImages.length - 1; lv >= 0; lv--) {
         this._images[lv] = this._defaultImages[lv];
      }

      this._currentFont = null;
      this._hasThemedImages = false;
      this._cachedIndex = -1;
   }

   public static synchronized IconCollection get(String name, int columns) {
      String moduleName = TraceBack.getCallingModuleName(0);
      return ThemeManager.getIconCollection(name, columns, 1, moduleName);
   }

   public static synchronized IconCollection get(String name, int columns, int rows) {
      String moduleName = TraceBack.getCallingModuleName(0);
      return ThemeManager.getIconCollection(name, columns, rows, moduleName);
   }

   public int getHeight(int width, int height) {
      return this._heights[this.getIndexForSize(height, false)];
   }

   public Image getImage(int index) {
      return new IconCollection$ProxiedImage(this, index, null);
   }

   private int getIndexForSize(int size, boolean defaultIcon) {
      int[] heights = defaultIcon ? this._defaultHeights : this._heights;
      int index = Arrays.binarySearch(heights, size);
      if (index < 0) {
         index = -index - 2;
      }

      return MathUtilities.clamp(0, index, heights.length - 1);
   }

   public int getWidth(int width, int height) {
      return this._widths[this.getIndexForSize(height, false)];
   }

   public int getWidth(Font f) {
      if (this._currentFont == null || !this._currentFont.equals(f)) {
         this._currentFont = f;
         int fontHeight = f.getHeight();
         this._iconWidth = this.getWidth(fontHeight, fontHeight);
      }

      return this._iconWidth;
   }

   public int getHeight(Font f) {
      int fontHeight = f.getHeight();
      return this.getHeight(fontHeight, fontHeight);
   }

   public boolean isDefaultSet() {
      return this._defaultImages.length != 0;
   }

   public int paint(Graphics graphics, int x, int y, int availableHeight, int index) {
      Font font = graphics.getFont();
      int iconWidth = this.getWidth(font);
      return this.paint(graphics, x, y, iconWidth, availableHeight, index);
   }

   public int paint(Graphics graphics, int x, int y, int index) {
      Font font = graphics.getFont();
      int iconWidth = this.getWidth(font);
      return this.paint(graphics, x, y, iconWidth, font.getHeight(), index);
   }

   public int paint(Graphics graphics, int x, int y, int width, int height, int index) {
      return this.paint(graphics, x, y, width, height, index & 65535, index >> 16);
   }

   public int paint(Graphics graphics, int x, int y, int width, int height, int column, int row) {
      int index = this.getIndexForSize(height, false);
      int iconWidth = this._widths[index];
      int iconHeight = this._heights[index];
      if (index != this._cachedIndex) {
         this._cachedBitmap = this._images[index].getBitmap();
         this._cachedIndex = index;
      }

      Bitmap bitmap = this._cachedBitmap;
      int left = column * iconWidth;
      int top = row * iconHeight;
      if (left >= bitmap.getWidth() || top >= bitmap.getHeight()) {
         index = this.getIndexForSize(height, true);
         bitmap = this._defaultImages[index].getBitmap();
         iconWidth = this._defaultWidths[index];
         iconHeight = this._defaultHeights[index];
         left = column * iconWidth;
         top = row * iconHeight;
      }

      x += Math.max(0, width - iconWidth >> 1);
      y += Math.max(0, height - iconHeight >> 1);
      width = Math.min(width, iconWidth);
      height = Math.min(height, iconHeight);
      if (Graphics.isColor() && bitmap.getType() == 129 && bitmap.hasAlpha()) {
         graphics.rop(-96, x, y, width, height, bitmap, left, top);
      } else {
         graphics.drawBitmap(x, y, width, height, bitmap, left, top);
      }

      return iconWidth;
   }

   public boolean containsIcon(int height, int column) {
      int index = this.getIndexForSize(height, false);
      int iconWidth = this._widths[index];
      Bitmap bitmap = this._images[index].getBitmap();
      int left = column * iconWidth;
      return left < bitmap.getWidth();
   }

   private void render(int width, int height) {
   }

   public void verifyModule(String moduleName) {
      if (!this._moduleName.equals(moduleName)) {
         throw new IllegalStateException("IconCollection accessed from wrong module.");
      }
   }
}
