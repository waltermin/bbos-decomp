package net.rim.device.api.ui.theme;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Manager;
import net.rim.device.internal.ui.Border;
import net.rim.device.internal.ui.Border3d;
import net.rim.device.internal.ui.BorderBitmap;
import net.rim.device.internal.ui.BorderRounded;
import net.rim.device.internal.ui.BorderSimple;

public final class Theme$Writer {
   private ResourceFetcher _resourceFetcher;
   private final Theme this$0;

   public Theme$Writer(Theme _1) {
      this.this$0 = _1;
   }

   public final void setResourceBundle(ResourceBundle resourceBundle) {
      this.this$0.setResourceBundle(resourceBundle);
   }

   public final void addResources() {
      this.this$0.addResources(this._resourceFetcher, false);
   }

   public final void setOption(String id, String value) {
      if (!this.this$0._options.containsKey(id)) {
         this.this$0._options.put(id, value);
      }
   }

   public final void loadScrollbar(
      String name,
      String sliderVertical,
      String sliderHorizontal,
      String trackVertical,
      String trackHorizontal,
      String upArrow,
      String downArrow,
      String leftArrow,
      String rightArrow,
      String corner,
      String sliderVerticalInactive,
      String sliderHorizontalInactive,
      String trackVerticalInactive,
      String trackHorizontalInactive,
      String upArrowInactive,
      String downArrowInactive,
      String leftArrowInactive,
      String rightArrowInactive,
      String cornerInactive
   ) {
      if (this.this$0.getScrollbar(name) == null) {
         Bitmap[] images = new Bitmap[18];
         EncodedImage image = this.this$0.getImage(sliderVertical, true);
         images[0] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(sliderHorizontal, true);
         images[1] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(trackVertical, true);
         images[2] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(trackHorizontal, true);
         images[3] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(upArrow, true);
         images[4] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(downArrow, true);
         images[5] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(leftArrow, true);
         images[6] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(rightArrow, true);
         images[7] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(corner, true);
         images[8] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(sliderVerticalInactive, true);
         images[9] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(sliderHorizontalInactive, true);
         images[10] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(trackVerticalInactive, true);
         images[11] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(trackHorizontalInactive, true);
         images[12] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(upArrowInactive, true);
         images[13] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(downArrowInactive, true);
         images[14] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(leftArrowInactive, true);
         images[15] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(rightArrowInactive, true);
         images[16] = image == null ? null : image.getBitmap();
         image = this.this$0.getImage(cornerInactive, true);
         images[17] = image == null ? null : image.getBitmap();
         this.this$0._scrollbars.put(name, images);
      }
   }

   public final void createBorder(String name, int top, int right, int bottom, int left) {
      this.createBorderBitmap(name, top, right, bottom, left);
   }

   public final void createBorderBitmap(String name, int top, int right, int bottom, int left) {
      this.createBorderBitmap(name, top, right, bottom, left, top, right, bottom, left);
   }

   public final void createBorderBitmap(
      String name, int top, int right, int bottom, int left, int topCorners, int rightCorners, int bottomCorners, int leftCorners
   ) {
      if (this.getBorder(name) == null) {
         EncodedImage image = this.this$0.getImage("border_" + name);
         Bitmap bitmap = image.getBitmap();
         Border border = new BorderBitmap(top, right, bottom, left, bitmap, topCorners, rightCorners, bottomCorners, leftCorners);
         this.putBorder(name, border);
      }
   }

   public final void createBorderRounded(String name, int top, int right, int bottom, int left) {
      if (this.getBorder(name) == null) {
         Border border = new BorderRounded(top, right, bottom, left, 2);
         this.putBorder(name, border);
      }
   }

   public final void createBorderSimple(String name, int top, int right, int bottom, int left) {
      if (this.getBorder(name) == null) {
         Border border = new BorderSimple(top, right, bottom, left);
         this.putBorder(name, border);
      }
   }

   public final void createBorder3d(
      String name,
      int top,
      int right,
      int bottom,
      int left,
      int colorTop0,
      int colorTop1,
      int colorRight0,
      int colorRight1,
      int colorBottom0,
      int colorBottom1,
      int colorLeft0,
      int colorLeft1
   ) {
      if (this.getBorder(name) == null) {
         Border border = new Border3d(
            top, right, bottom, left, colorTop0, colorTop1, colorRight0, colorRight1, colorBottom0, colorBottom1, colorLeft0, colorLeft1
         );
         this.putBorder(name, border);
      }
   }

   public final ThemeAttributeSet$Writer createThemeAttributeSetWriter(String element) {
      return this.createThemeAttributeSetWriter(element, this._resourceFetcher);
   }

   public final ThemeAttributeSet$Writer createThemeAttributeSetWriter(String element, ResourceFetcher resourceFetcher) {
      ThemeAttributeSet tas = new ThemeAttributeSet(element);
      return tas.getWriterInternal(resourceFetcher);
   }

   public final Manager getLayout(String name, Object context) {
      return null;
   }

   public final Border getBorder(String name) {
      return (Border)this.this$0._borders.get(name);
   }

   public final int getBorderStyle() {
      return this.this$0._borderStyle;
   }

   public final void setBorderStyle(int style) {
      if (style >= 0 && style < 3) {
         this.this$0._borderStyle = style;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private final int getStateForName(String statename) {
      int state = 0;
      if (statename == null || statename.equals("")) {
         return 0;
      } else if (statename.equals("first-child")) {
         return 1;
      } else if (statename.equals("link")) {
         return 2;
      } else if (statename.equals("visited")) {
         return 3;
      } else if (statename.equals("active")) {
         return 4;
      } else if (statename.equals("hover")) {
         return 5;
      } else if (statename.equals("focus")) {
         return 6;
      } else if (statename.equals("disabled")) {
         return 7;
      } else if (statename.equals("disabled-focus")) {
         return 8;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final void put(ThemeAttributeSet$Writer attributesWriter) {
      this.put(attributesWriter, false);
   }

   public final void put(ThemeAttributeSet$Writer attributesWriter, boolean force) {
      ThemeAttributeSet attributes = attributesWriter.getThemeAttributeSet();
      String key = attributes.getElement();
      String tagname = key;
      String idname = null;
      String statename = null;
      int idstart = key.indexOf(35);
      int statestart = key.indexOf(58);
      if (statestart != -1 && statestart < idstart) {
         throw new IllegalArgumentException();
      }

      if (statestart != -1) {
         statename = key.substring(statestart + 1);
         tagname = key.substring(0, statestart);
      }

      if (idstart != -1) {
         idname = key.substring(idstart + 1, statestart != -1 ? statestart : key.length());
         tagname = key.substring(0, idstart);
      }

      Tag tag = Tag.create(tagname);
      int state = this.getStateForName(statename);
      long lkey = this.this$0.getKey(tag.hashCode(), idname, state);
      if (force || this.this$0._attributeSets.get(lkey) == null) {
         this.this$0._attributeSets.put(lkey, attributes);
      }
   }

   public final void putBorder(String name, Border border) {
      if (this.this$0._borders.get(name) == null) {
         this.this$0._borders.put(name, border);
      }
   }

   public final void putPaletteEntry(String name, int color) {
      if (name.charAt(0) == '[' && name.charAt(name.length() - 1) == ']') {
         if (!this.this$0._palette.containsKey(name)) {
            this.this$0._palette.put(name, color);
         }
      } else {
         throw new IllegalArgumentException("Bad palette entry name: " + name);
      }
   }

   public final void setApplicationIconSize(int width, int height) {
      if (!this.this$0._ribbonIconSizeSet) {
         this.this$0._ribbonIconSizeSet = true;
         this.this$0._ribbonIconWidth = width;
         this.this$0._ribbonIconHeight = height;
      }
   }

   public final void setIdleScreenName(String idleScreenName) {
      if (!this.this$0._idleScreenNameSet) {
         this.this$0._idleScreenNameSet = true;
         this.this$0._idleScreenName = idleScreenName;
         if (this.this$0._idleScreenName != null) {
            this.this$0._idleScreenName = this._resourceFetcher.getBaseURL() + this.this$0._idleScreenName;
         }
      }
   }

   public final void setThumbnailName(String thumbnailName) {
      if (!this.this$0._thumbnailNameSet) {
         this.this$0._thumbnailNameSet = true;
         this.this$0._thumbnailName = thumbnailName;
      }
   }

   public final void setLabelOnOwnLine(boolean isLabelOnOwnLine) {
      this.this$0._isLabelOnOwnLine = isLabelOnOwnLine;
   }

   public final void setLayoutFactory(Theme$LayoutFactory layoutFactory) {
      this.this$0._layoutFactory = layoutFactory;
   }

   public final void setResourceFetcher(ResourceFetcher resourceFetcher) {
      this._resourceFetcher = resourceFetcher;
   }

   public final void addAlias(String name, String aka) {
      this.this$0._aliasList.put(name, aka);
   }
}
