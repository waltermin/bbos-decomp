package net.rim.device.internal.resource.crypto;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

public final class CryptoIcons {
   public static final int ICON_CERTIFICATE = 0;
   public static final int ICON_CERTIFICATES = 1;
   public static final int ICON_CERTIFICATE_CHAIN_ERROR = 2;
   public static final int ICON_CERTIFICATE_CHAIN_EXPIRED = 3;
   public static final int ICON_CERTIFICATE_CHAIN_REVOKED = 4;
   public static final int ICON_CERTIFICATE_CHAIN_NOT_TRUSTED = 5;
   public static final int ICON_CERTIFICATE_CHAIN_TRUSTED = 6;
   public static final int ICON_CERTIFICATE_TRUSTED = 7;
   public static final int ICON_SIGNATURE_VERIFIED = 8;
   public static final int ICON_SIGNATURE_FAILED = 9;
   public static final int ICON_SIGNATURE_NEED_MORE = 10;
   public static final int ICON_ENCRYPTED_STRONG = 11;
   public static final int ICON_ENCRYPTED_WEAK = 12;
   public static final int ICON_PLEASE_WAIT = 13;
   public static final int ICON_SIGNED_RECEIPT = 14;
   public static final int ICON_RADIO_PENDING = 15;
   public static final int ICON_RADIO_SENDING = 16;
   public static final int ICON_RADIO_SENT = 17;
   public static final int ICON_RADIO_NO_COVERAGE = 18;
   public static final int ICON_UNKNOWN_ATTACHMENT = 19;
   public static final int ICON_SIGNATURE_VERIFIED_ON_BES = 20;
   public static final int ICON_SIGNATURE_FAILED_ON_BES = 21;
   public static final int ICON_SERVER_ATTACHMENT = 22;
   private static final int ICON_COUNT = 23;
   private static IconCollection _icons = IconCollection.get("net_rim_crypto", 23);

   public static final IconCollection getIcons() {
      return _icons;
   }

   public static final Image getImage(int index) {
      return _icons.getImage(index);
   }

   public static final ImageField getLargeImageField(int index) {
      ImageField imageField = (ImageField)(new Object());
      imageField.setImage(getImage(index));
      imageField.setPreferredSize(26, 20);
      return imageField;
   }

   public static final ImageField getLargeImageField(Image image, long style) {
      ImageField imageField = (ImageField)(new Object(style));
      imageField.setImage(image);
      imageField.setPreferredSize(26, 20);
      return imageField;
   }

   public static final ImageField getSmallImageField(int index) {
      ImageField imageField = (ImageField)(new Object());
      imageField.setImage(getImage(index));
      imageField.setPreferredSize(20, 15);
      return imageField;
   }

   public static final ImageField getSmallImageField(Image image, long style) {
      ImageField imageField = (ImageField)(new Object(style));
      imageField.setImage(image);
      imageField.setPreferredSize(20, 15);
      return imageField;
   }

   public static final int getIconWidth(Font font, int index) {
      return _icons.getWidth(font);
   }

   public static final int drawIcon(Graphics g, int x, int y, int index) {
      return _icons.paint(g, x, y, index);
   }
}
