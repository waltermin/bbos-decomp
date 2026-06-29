package net.rim.device.internal.resource.crypto;

import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

public final class CryptoIndicatorImages {
   public static final int ICON_HOUR_GLASS;
   public static final int ICON_VALID;
   public static final int ICON_UNKNOWN;
   public static final int ICON_INVALID;
   public static final int ICON_PRIVATE_KEY;
   private static final int ICON_COUNT;
   private static IconCollection _icons = IconCollection.get("net_rim_crypto_indicator", 5);

   public static final IconCollection getIcons() {
      return _icons;
   }

   public static final Image getImage(int index) {
      return _icons.getImage(index);
   }

   public static final ImageField getImageField(int index) {
      ImageField imageField = (ImageField)(new Object());
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
