package net.rim.device.internal.resource.crypto;

import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

public final class CryptoIndicatorImages {
   public static final int ICON_HOUR_GLASS = 0;
   public static final int ICON_VALID = 1;
   public static final int ICON_UNKNOWN = 2;
   public static final int ICON_INVALID = 3;
   public static final int ICON_PRIVATE_KEY = 4;
   private static final int ICON_COUNT = 5;
   private static IconCollection _icons = IconCollection.get("net_rim_crypto_indicator", 5);

   public static final IconCollection getIcons() {
      return _icons;
   }

   public static final Image getImage(int index) {
      return _icons.getImage(index);
   }

   public static final ImageField getImageField(int index) {
      ImageField imageField = new ImageField();
      imageField.setImage(getImage(index));
      return imageField;
   }

   public static final int getImageHeight() {
      return _icons.getHeight(14, 12);
   }

   public static final int getImageWidth() {
      return _icons.getWidth(14, 12);
   }

   public static final int drawIcon(Graphics g, int x, int y, int index) {
      return _icons.paint(g, x, y, getImageWidth(), getImageHeight(), index);
   }
}
