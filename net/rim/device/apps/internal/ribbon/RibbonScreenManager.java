package net.rim.device.apps.internal.ribbon;

import java.util.Hashtable;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.ribbon.launcher.ApplicationLauncherField;
import net.rim.device.internal.ui.Background;
import net.rim.vm.WeakReference;

final class RibbonScreenManager extends Manager {
   private VerticalFieldManager _titleAreaField;
   private VerticalFieldManager _topAreaField;
   private Field _iconAreaField;
   private VerticalFieldManager _bottomAreaField;
   private boolean _refreshRequired;
   private Bitmap _initialBackgroundBitmap;
   private Bitmap _backgroundBitmap;
   private int _chooserOffset = -1;
   private int _chooserHeight = -1;
   private int _backgroundColour = 0;
   private WeakReference _refBackgroundBitmap = new WeakReference(null);
   private boolean _compressedBanners;
   private Field[] _fieldInfo;
   private Hashtable[] _layoutArgs;
   boolean _noLayouts = true;
   private static Tag TAG = Tag.create("homescreen");
   private static Tag MASK_TAG = Tag.create("homescreen_mask");

   RibbonScreenManager(boolean compressedBanners) {
      super(3458764513820540928L);
      this._compressedBanners = compressedBanners;
   }

   final Bitmap getBackgroundImage() {
      return this._backgroundBitmap;
   }

   final void setHomeScreenContents(Hashtable[] layout, Field[] fields) {
      this._noLayouts = true;

      for (int i = 0; i < fields.length; i++) {
         Manager manager = fields[i].getManager();
         if (manager != null) {
            manager.delete(fields[i]);
         }
      }

      this.deleteAll();
      this._layoutArgs = layout;
      this._fieldInfo = fields;
      this._titleAreaField = new VerticalFieldManager();
      this._topAreaField = new VerticalFieldManager();
      this._iconAreaField = null;
      this._bottomAreaField = new VerticalFieldManager();

      for (int i = 0; i < layout.length; i++) {
         Hashtable args = this._layoutArgs[i];
         Field field = this._fieldInfo[i];
         String value = (String)args.get("align");
         if (StringUtilities.strEqual(value, "title")) {
            this._titleAreaField.add(field);
         } else if (StringUtilities.strEqual(value, "top")) {
            this._topAreaField.add(field);
         } else if (StringUtilities.strEqual(value, "bottom")) {
            this._bottomAreaField.add(field);
         }

         value = (String)args.get("tag");
         if (value != null) {
            field.setTag(Tag.create(value));
         }

         value = (String)args.get("id");
         if (value != null) {
            field.setId(value);
         }

         value = (String)args.get("chooser");
         if (value != null) {
            this._iconAreaField = field;
         }
      }

      this.add(this._titleAreaField);
      this.add(this._topAreaField);
      this.add(this._iconAreaField);
      this.add(this._bottomAreaField);
      if (this._iconAreaField != null) {
         ((ApplicationLauncherField)this._iconAreaField).setBackgroundBitmap(this._backgroundBitmap);
      }

      this._noLayouts = false;
      if (this.isValidLayout()) {
         this.layout(this.getWidth(), this.getHeight());
      }
   }

   final void setBackgroundImage(Bitmap bitmap) {
      this._initialBackgroundBitmap = bitmap;
      if (bitmap != null) {
         int chooserOffset = 0;
         int chooserHeight = 0;
         if (this._iconAreaField != null) {
            chooserOffset = this._iconAreaField.getExtent().y;
            chooserHeight = this._iconAreaField.getHeight();
         }

         this._refreshRequired = chooserHeight == 0;
         if (this._refreshRequired) {
            return;
         }

         this._initialBackgroundBitmap = null;
         int screenWidth = Display.getWidth();
         int screenHeight = Display.getHeight();
         int width = bitmap.getWidth();
         int height = bitmap.getHeight();
         Bitmap newBitmap = this._backgroundBitmap;
         if (newBitmap == null || newBitmap.getWidth() != screenWidth || newBitmap.getHeight() != screenHeight) {
            newBitmap = new Bitmap(screenWidth, screenHeight);
         }

         Theme theme = ThemeManager.getActiveTheme();
         ThemeAttributeSet baseAttributes = theme.getAttributeSet(TAG);
         int backgroundColour = baseAttributes.getColor(0);
         if (newBitmap != this._backgroundBitmap
            || bitmap != this._refBackgroundBitmap.get()
            || chooserOffset != this._chooserOffset
            || chooserHeight != this._chooserHeight
            || backgroundColour != this._backgroundColour) {
            Graphics graphics = new Graphics(newBitmap);
            if (baseAttributes != null) {
               graphics.setColor(baseAttributes.getColor(0));
               graphics.fillRect(0, 0, screenWidth, screenHeight);
            }

            int srcY;
            int destY;
            if (height <= chooserHeight) {
               destY = chooserOffset + (chooserHeight - height >> 1);
               srcY = 0;
            } else if (height <= screenHeight) {
               destY = screenHeight - height;
               srcY = 0;
            } else {
               destY = 0;
               srcY = height - screenHeight;
            }

            int xOffset = screenWidth - width >> 1;
            graphics.drawBitmap(xOffset < 0 ? 0 : xOffset, destY, width, height, bitmap, xOffset < 0 ? -xOffset : 0, srcY);
            ThemeAttributeSet mask = theme.getAttributeSet(MASK_TAG);
            if (mask != null) {
               Background background = mask.getBackground();
               if (background != null) {
                  background.draw(graphics, this.getContentRect());
               }
            }

            this._chooserOffset = chooserOffset;
            this._chooserHeight = chooserHeight;
            this._backgroundColour = backgroundColour;
            this._refBackgroundBitmap = new WeakReference(bitmap);
         }

         bitmap = newBitmap;
      }

      this._backgroundBitmap = bitmap;
      if (this._iconAreaField != null) {
         ((ApplicationLauncherField)this._iconAreaField).setBackgroundBitmap(this._backgroundBitmap);
      }

      this.invalidate();
   }

   @Override
   protected final void subpaint(Graphics graphics) {
      if (this._backgroundBitmap != null) {
         int width = this.getWidth();
         int height = this.getHeight();
         if (width >= 0 && height >= 0) {
            graphics.drawBitmap(0, 0, width, height, this._backgroundBitmap, Display.getWidth() - width, Display.getHeight() - height);
         }
      }

      super.subpaint(graphics);
   }

   @Override
   public final int getPreferredWidth() {
      return Display.getWidth();
   }

   @Override
   public final int getPreferredHeight() {
      return Display.getHeight();
   }

   @Override
   protected final void sublayout(int width, int height) {
      if (!this._noLayouts) {
         this.setPositionChild(this._titleAreaField, 0, 0);
         this.layoutChild(this._titleAreaField, width, height);
         int verticalOffset = 0;
         if (this._compressedBanners) {
            verticalOffset = 1;
         }

         this.layoutChild(this._bottomAreaField, width, height);
         int h = this._bottomAreaField.getHeight();
         this.setPositionChild(this._bottomAreaField, 0, height - h + verticalOffset);
         this.layoutChild(this._bottomAreaField, width, h);
         int topHeight = this._titleAreaField.getHeight();
         this.setPositionChild(this._topAreaField, 0, topHeight);
         this.layoutChild(this._topAreaField, width, height);
         topHeight += this._topAreaField.getHeight();
         h += topHeight;
         if (this._iconAreaField.getManager() == this) {
            this.layoutChild(this._iconAreaField, width, height - h + verticalOffset);
            this.setPositionChild(this._iconAreaField, 0, (height - h + verticalOffset - this._iconAreaField.getHeight() >> 1) + topHeight);
         }

         this.setExtent(width, height);
         if (this._refreshRequired) {
            this.setBackgroundImage(this._initialBackgroundBitmap);
         }
      }
   }
}
