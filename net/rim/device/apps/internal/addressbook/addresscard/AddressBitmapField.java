package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.component.BitmapField;

final class AddressBitmapField extends BitmapField {
   private boolean _default = true;
   private EncodedImage _defaultImage = EncodedImage.getEncodedImageResource("ImagePlaceholder.png");

   AddressBitmapField(long style) {
      super(null, style);
      super.setImage(this._defaultImage);
   }

   @Override
   protected final EncodedImage getImage() {
      return this._default ? null : super.getImage();
   }

   @Override
   public final void setImage(EncodedImage image) {
      this._default = image == null;
      super.setImage(this._default ? this._defaultImage : image);
   }

   public final boolean isDefault() {
      return this._default;
   }
}
