package net.rim.device.apps.internal.browser.util;

import java.util.Vector;
import net.rim.device.api.util.StringUtilities;
import org.w3c.dom.html2.HTMLAreaElement;

public final class ImageMap {
   private String _name;
   private Vector _areas;
   private int _scaled;
   private int _newWidth;
   private int _newHeight;
   private int _originalWidth;
   private int _originalHeight;
   public static final int RECT;
   public static final int CIRCLE;
   public static final int POLY;
   public static final int DEFAULT;
   private static final int NOT_INITIALIZED;
   private static final int ORIG_IMAGE_SIZE_SET;
   private static final int AREAS_SCALED;

   public ImageMap(String name) {
      this._name = name;
      this._areas = (Vector)(new Object(0, 1));
   }

   public final String getName() {
      return this._name;
   }

   public final int getNumAreas() {
      return this._areas.size();
   }

   public final ImageMap$ImageArea getArea(int i) {
      return (ImageMap$ImageArea)this._areas.elementAt(i);
   }

   public final boolean isScaled() {
      return this._scaled == 2;
   }

   public final void addArea(HTMLAreaElement areaElement) {
      if (areaElement != null) {
         String coords = areaElement.getCoords();
         if (coords != null && coords.length() != 0) {
            String href = areaElement.getHref();
            if (href != null && href.length() != 0) {
               String shape = areaElement.getShape();
               if (shape != null && shape.length() != 0) {
                  shape = StringUtilities.toLowerCase(shape, 1701707776);
               } else {
                  shape = "rect";
               }

               int intShape = -1;
               if (shape.length() == 4) {
                  if (shape.equals("rect")) {
                     intShape = 0;
                  } else if (shape.equals("poly")) {
                     intShape = 2;
                  }
               } else if (shape.length() == 6 && shape.equals("circle")) {
                  intShape = 1;
               } else if (shape.length() == 7) {
                  if (shape.equals("default")) {
                     intShape = 3;
                  } else if (shape.equals("polygon")) {
                     intShape = 2;
                  }
               }

               if (intShape >= 0 && intShape <= 3) {
                  ImageMap$ImageArea area = new ImageMap$ImageArea(intShape);
                  if (area.setValues(areaElement)) {
                     this._areas.addElement(area);
                  }
               }
            }
         }
      }
   }

   public final void setOriginalSize(int originalWidth, int originalHeight) {
      this._originalWidth = originalWidth;
      this._originalHeight = originalHeight;
      this._scaled = 1;
   }

   public final void setLayoutSize(int newWidth, int newHeight) {
      if (this._scaled != 2 || this._newWidth != newWidth || this._newHeight != newHeight) {
         this._newWidth = newWidth;
         this._newHeight = newHeight;
         this.scale();
      }
   }

   public final void scale() {
      if (this._newWidth > 0 && this._newHeight > 0) {
         int count = this._areas.size();
         if (this._scaled >= 1 && this._originalWidth > 0 && this._originalHeight > 0 && count != 0) {
            for (int i = 0; i < count; i++) {
               ImageMap$ImageArea item = (ImageMap$ImageArea)this._areas.elementAt(i);
               item.scale(this._newWidth, this._newHeight, this._originalWidth, this._originalHeight);
            }

            this._scaled = 2;
         }
      }
   }
}
