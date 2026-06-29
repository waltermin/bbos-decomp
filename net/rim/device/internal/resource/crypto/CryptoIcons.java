package net.rim.device.internal.resource.crypto;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;

public final class CryptoIcons {
   public static final int ICON_CERTIFICATE;
   public static final int ICON_CERTIFICATES;
   public static final int ICON_CERTIFICATE_CHAIN_ERROR;
   public static final int ICON_CERTIFICATE_CHAIN_EXPIRED;
   public static final int ICON_CERTIFICATE_CHAIN_REVOKED;
   public static final int ICON_CERTIFICATE_CHAIN_NOT_TRUSTED;
   public static final int ICON_CERTIFICATE_CHAIN_TRUSTED;
   public static final int ICON_CERTIFICATE_TRUSTED;
   public static final int ICON_SIGNATURE_VERIFIED;
   public static final int ICON_SIGNATURE_FAILED;
   public static final int ICON_SIGNATURE_NEED_MORE;
   public static final int ICON_ENCRYPTED_STRONG;
   public static final int ICON_ENCRYPTED_WEAK;
   public static final int ICON_PLEASE_WAIT;
   public static final int ICON_SIGNED_RECEIPT;
   public static final int ICON_RADIO_PENDING;
   public static final int ICON_RADIO_SENDING;
   public static final int ICON_RADIO_SENT;
   public static final int ICON_RADIO_NO_COVERAGE;
   public static final int ICON_UNKNOWN_ATTACHMENT;
   public static final int ICON_SIGNATURE_VERIFIED_ON_BES;
   public static final int ICON_SIGNATURE_FAILED_ON_BES;
   public static final int ICON_SERVER_ATTACHMENT;
   private static final int ICON_COUNT;
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
