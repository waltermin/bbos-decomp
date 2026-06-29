package net.rim.device.apps.internal.addressbook.addresscard;

import java.io.ByteArrayOutputStream;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.apps.api.addressbook.DisplayPictureModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;
import net.rim.device.internal.ui.ScaleBitmap;
import net.rim.vm.Array;

public final class DisplayPictureModelImpl implements DisplayPictureModel, FieldProvider, VerbProvider, ConversionProvider, EncryptableProvider {
   private Object _displayPictureEncoding;

   @Override
   public final void setDisplayPicture(byte[] imageData) {
      this._displayPictureEncoding = PersistentContent.encode(imageData, true, false);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      }

      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 0)) {
         VerticalFieldManager field = (VerticalFieldManager)ContextObject.get(context, 9045827404276417370L);
         AddressBitmapField abf = (AddressBitmapField)field.getLeafFieldWithFocus();
         if (!abf.isDefault()) {
            Array.resize(verbs, 3);
            verbs[0] = new DisplayPictureVerb(5, 16879616);
            verbs[1] = new DisplayPictureVerb(1, 16864592);
            verbs[2] = new DisplayPictureVerb(2, 16864597);
            return verbs[1];
         }
      }

      return null;
   }

   @Override
   public final Bitmap getDisplayBitmap() {
      byte[] imageData = this.getDisplayPicture();
      return imageData != null ? EncodedImage.createEncodedImage(imageData, 0, imageData.length).getBitmap() : null;
   }

   @Override
   public final Bitmap getDisplayIcon() {
      return null;
   }

   @Override
   public final Field getField(Object context) {
      VerticalFieldManager vField = new VerticalFieldManager();
      long flags = ContextObject.getFlag(context, 0) ? 4503599627370496L : 9007199254740992L;
      int scale = Fixed32.toFP(1);
      if (flags == 4503599627370496L) {
         Field field = new LabelField(AddressBookResources.getString(107), 9007199254740992L);
         vField.add(field);
         scale = Fixed32.toFP(2);
      }

      flags |= 18014398509481985L;
      AddressBitmapField bitmapField = new AddressBitmapField(flags);
      bitmapField.setSpace(1, 1);
      byte[] imageData = this.getDisplayPicture();
      if (imageData != null) {
         EncodedImage image = EncodedImage.createEncodedImage(imageData, 0, imageData.length);
         image = image.scaleImage32(scale, scale);
         bitmapField.setImage(image);
      }

      vField.add(bitmapField);
      vField.setCookie(this);
      return vField;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      byte[] displayPicture = this.getDisplayPicture();
      if (ContextObject.getFlag(context, 11) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         if (displayPicture != null) {
            syncBuffer.addBytes(77, displayPicture);
         }

         return true;
      } else if (ContextObject.getFlag(context, 11)
         && ContextObject.getFlag(context, 43)
         && ContextObject.getFlag(context, 54)
         && target instanceof StringBuffer) {
         try {
            StringBuffer output = (StringBuffer)target;
            output.append("\rPhoto:");
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            Base64OutputStream base64Out = new Base64OutputStream(byteOut);
            base64Out.write(displayPicture);
            base64Out.flush();
            byte[] data = byteOut.toByteArray();

            for (int i = 0; i < data.length; i++) {
               output.append((char)data[i]);
            }

            return true;
         } finally {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      if (field instanceof VerticalFieldManager) {
         VerticalFieldManager vfm = (VerticalFieldManager)field;
         AddressBitmapField abf = (AddressBitmapField)vfm.getField(1);
         EncodedImage image = abf.getImage();
         if (image != null) {
            this.setDisplayPicture(image.getData());
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      boolean editable = ContextObject.getFlag(context, 0);
      return editable ? 1900 : 2500;
   }

   @Override
   public final byte[] getDisplayPicture() {
      try {
         return PersistentContent.decodeByteArray(this._displayPictureEncoding);
      } finally {
         return new byte[0];
      }
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._displayPictureEncoding, compress, encrypt);
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      this._displayPictureEncoding = PersistentContent.reEncode(this._displayPictureEncoding, compress, encrypt);
      return null;
   }

   public DisplayPictureModelImpl(Object initialData) {
      byte[] imageData = null;
      if (!(initialData instanceof byte[])) {
         if (initialData instanceof ContextObject) {
            imageData = (byte[])ContextObject.get(initialData, 8849067667159082262L);
         }
      } else {
         imageData = (byte[])initialData;
      }

      if (imageData != null) {
         this.setDisplayPicture(imageData);
      }
   }

   public static final byte[] enforceSizeConstraints(byte[] imageData) {
      EncodedImage image = EncodedImage.createEncodedImage(imageData, 0, imageData.length);
      Bitmap bitmap = null;
      boolean modified = false;
      int height = image.getHeight();
      int width = image.getWidth();
      int qualityFactor = 75;
      if (height > 100 || width > 100) {
         Bitmap var8 = image.getBitmap();
         height = Fixed32.toFP(height);
         width = Fixed32.toFP(width);
         int scale = Math.max(height / 100, width / 100);
         bitmap = ScaleBitmap.scaleBitmap(var8, Fixed32.toInt(Fixed32.div(width, scale)), Fixed32.toInt(Fixed32.div(height, scale)));
         image = JPEGEncodedImage.encode(bitmap, 75);
         modified = true;
      }

      if (image.getLength() > 32768) {
         if (bitmap == null) {
            bitmap = image.getBitmap();
         } else {
            qualityFactor -= 5;
         }

         do {
            image = JPEGEncodedImage.encode(bitmap, qualityFactor);
            qualityFactor -= 5;
         } while (image.getLength() > 32768 && qualityFactor >= 0);

         if (qualityFactor < 0) {
            throw new IllegalArgumentException("Image size could not be reduced to less than 32768 bytes.");
         }

         modified = true;
      }

      return modified ? image.getData() : imageData;
   }
}
