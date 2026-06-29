package net.rim.device.apps.internal.browser.util;

import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringTokenizer;
import org.w3c.dom.html2.HTMLAreaElement;

public final class ImageMap$ImageArea {
   private int _shape;
   private int _minX;
   private int _maxX;
   private int _minY;
   private int _maxY;
   private int[] _xCoords;
   private int[] _yCoords;
   private int[] _scaledXCoords;
   private int[] _scaledYCoords;
   private int _radius;
   private int _scaledRadius;
   private HTMLAreaElement _areaElement;

   public ImageMap$ImageArea(int shape) {
      this._shape = shape;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final boolean setValues(HTMLAreaElement areaElement) {
      if (areaElement == null) {
         return false;
      }

      String coords = areaElement.getCoords();
      if (coords == null) {
         return false;
      }

      this._areaElement = areaElement;
      StringTokenizer tokenizer = new StringTokenizer(coords, ", -'\"");
      int tokenCount = tokenizer.countTokens();
      switch (this._shape) {
         case -1:
            break;
         case 0:
         default:
            if (tokenCount != 4) {
               return false;
            }
            break;
         case 1:
            if (tokenCount != 3) {
               return false;
            }

            tokenCount = 2;
            break;
         case 2:
            if (tokenCount <= 0 || tokenCount % 2 != 0) {
               return false;
            }
      }

      try {
         int halfCount = tokenCount >> 1;
         this._xCoords = new int[halfCount];
         this._yCoords = new int[halfCount];

         for (int i = 0; i < halfCount; i++) {
            this._xCoords[i] = Math.max(Integer.parseInt(tokenizer.nextToken().trim()), 0);
            this._yCoords[i] = Math.max(Integer.parseInt(tokenizer.nextToken().trim()), 0);
         }

         if (this._shape == 1) {
            this._scaledRadius = this._radius = Math.max(Integer.parseInt(tokenizer.nextToken().trim()), 0);
         }

         this._scaledXCoords = Arrays.copy(this._xCoords);
         this._scaledYCoords = Arrays.copy(this._yCoords);
         return true;
      } catch (Throwable var8) {
         RIMGlobalMessagePoster.postGlobalEvent(-2269441167196113981L, 0, 0, e, null);
         return false;
      }
   }

   public final int getShape() {
      return this._shape;
   }

   public final int[] getXCoords() {
      return this._scaledXCoords;
   }

   public final int[] getYCoords() {
      return this._scaledYCoords;
   }

   public final int getRadius() {
      return this._scaledRadius;
   }

   public final HTMLAreaElement getAreaElement() {
      return this._areaElement;
   }

   public final String getHref() {
      return this._areaElement != null ? this._areaElement.getHref() : null;
   }

   public final int getMinX() {
      return this._minX;
   }

   public final int getMaxX() {
      return this._maxX;
   }

   public final int getMinY() {
      return this._minY;
   }

   public final int getMaxY() {
      return this._maxY;
   }

   public final void scale(int newWidth, int newHeight, int originalWidth, int originalHeight) {
      if (this._shape == 1) {
         this._scaledXCoords[0] = this._xCoords[0] * newWidth / originalWidth;
         this._scaledYCoords[0] = this._yCoords[0] * newHeight / originalHeight;
         this._scaledRadius = this._radius * newWidth / originalWidth;
         this._minX = this._scaledXCoords[0] - this._scaledRadius;
         this._maxX = this._scaledXCoords[0] + this._scaledRadius;
         this._minY = this._scaledYCoords[0] - this._scaledRadius;
         this._maxY = this._scaledYCoords[0] + this._scaledRadius;
      } else {
         int count = this._xCoords.length;
         this._minX = Integer.MAX_VALUE;
         this._maxX = Integer.MIN_VALUE;
         this._minY = Integer.MAX_VALUE;
         this._maxY = Integer.MIN_VALUE;

         for (int i = 0; i < count; i++) {
            this._scaledXCoords[i] = this._xCoords[i] * newWidth / originalWidth;
            this._scaledYCoords[i] = this._yCoords[i] * newHeight / originalHeight;
            this._minY = Math.min(this._scaledYCoords[i], this._minY);
            this._maxY = Math.max(this._scaledYCoords[i], this._maxY);
            this._minX = Math.min(this._scaledXCoords[i], this._minX);
            this._maxX = Math.max(this._scaledXCoords[i], this._maxX);
         }
      }
   }
}
