package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.ui.MIMEContentAnimatedBitmapField;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.browser.verbs.SaveImageVerb;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.apps.internal.mms.verbs.DeleteFieldVerb;

final class BitmapPresentationElementField extends MIMEContentAnimatedBitmapField implements PresentationElement, MMSAttachment {
   private MMSAttachment _attachment;
   private EncodedImage _image;
   private boolean _moveMode;
   private static final int MAX_ITERATIONS = 5;

   private BitmapPresentationElementField(EncodedImage image, boolean isProtected, MMSAttachment attachment, boolean isEditable) {
      super(image, 5, getFlags(isEditable));
      this._attachment = attachment;
      this._image = image;
      this.setProtected(isProtected);
   }

   private BitmapPresentationElementField(Bitmap bitmap, EncodedImage image, boolean isProtected, MMSAttachment attachment, boolean isEditable) {
      super(bitmap, getFlags(isEditable));
      this._attachment = attachment;
      this._image = image;
      this.setProtected(isProtected);
   }

   static final BitmapPresentationElementField createInstance(MMSAttachment attachment, boolean isEditable) {
      return createInstance(attachment, isEditable, false);
   }

   static final BitmapPresentationElementField createInstance(MMSAttachment attachment, boolean isEditable, boolean isForwardLocked) {
      String mimeType = MMSUtilities.getMIMETypeString(attachment.getType());
      byte[] data = MMSUtilities.decrypt(attachment.getData());
      if (data != null) {
         EncodedImage image = getEncodedImage(data, mimeType);
         if (image != null) {
            boolean isProtected = data != attachment.getData() || isForwardLocked;
            boolean isStandardContent = MMSUtilities.isImageType(attachment.getType(), false);
            if (isEditable && !isStandardContent) {
               switch (MMSClientServiceBook.getRestrictedSendMode()) {
                  case 0:
                     break;
                  case 1:
                  default:
                     Dialog.alert(MMSResources.getString(108));
                     return null;
                  case 2:
                     if (Dialog.ask(3, MMSResources.getString(109)) == -1) {
                        return null;
                     }
               }
            }

            boolean isTooLarge = image.getWidth() > MMSClientServiceBook.getMaxImageWidth() || image.getHeight() > MMSClientServiceBook.getMaxImageHeight();
            if (isEditable && isTooLarge) {
               switch (MMSClientServiceBook.getRestrictedSendMode()) {
                  case 0:
                     break;
                  case 1:
                  default:
                     if (!MMSClientServiceBook.allowImageReductionBeforeSend()) {
                        Dialog.alert(MMSResources.getString(105));
                        return null;
                     }
                     break;
                  case 2:
                     if (Dialog.ask(3, MMSResources.getString(106)) == -1) {
                        return null;
                     }
               }
            }

            if (image.getFrameCount() == 1) {
               int reductionFactor = image.getScale();
               if (reductionFactor > 1) {
                  Bitmap bitmap = getScaledBitmap(image);
                  return new BitmapPresentationElementField(bitmap, image, isProtected, attachment, isEditable);
               }
            }

            return new BitmapPresentationElementField(image, isProtected, attachment, isEditable);
         }
      }

      return new BitmapPresentationElementField(Bitmap.getBitmapResource("BrokenImage.gif"), null, true, attachment, isEditable);
   }

   private static final long getFlags(boolean isEditable) {
      return isEditable ? 22517998136852480L : 27021597764222976L;
   }

   private static final EncodedImage getEncodedImage(byte[] byteData, String mimeType) {
      try {
         EncodedImage image = EncodedImage.createEncodedImage(byteData, 0, byteData.length);
         int width = image.getWidth();
         int maxWidth = Display.getWidth() * 11 / 10;
         if (width > maxWidth) {
            int reductionFactor = (width + maxWidth - 1) / maxWidth;
            image.setScale(reductionFactor);
         }

         return image;
      } finally {
         ;
      }
   }

   private static final Bitmap getScaledBitmap(EncodedImage image) {
      int scale = image.getScale();
      int scaledWidth = image.getWidth() / scale;
      int scaledHeight = image.getHeight() / scale;
      Bitmap scaledBitmap = new Bitmap(scaledWidth, scaledHeight);
      Graphics scaledGraphics = new Graphics(scaledBitmap);
      scaledGraphics.drawImage(0, 0, scaledWidth, scaledHeight, image, 0, 0, 0);
      return scaledBitmap;
   }

   final void reduce(int maxSize, int maxWidth, int maxHeight) {
      this._attachment = MMSUtilities.reduceImage(this._attachment, maxSize, maxWidth, maxHeight);
   }

   @Override
   protected final void makeContextMenu(ContextMenu contextMenu) {
      super.makeContextMenu(contextMenu);
      EncodedImage image = this._image;
      if (image != null) {
         SaveImageVerb verb = new SaveImageVerb(this._attachment.getName(), image, this.isProtected());
         VerbMenuItem verbMenuItem = new VerbMenuItem(null, verb.getOrdering(), 500, verb, null);
         contextMenu.addItem(verbMenuItem);
         contextMenu.setDefaultItem(verbMenuItem);
      }

      if (this.isEditable()) {
         contextMenu.addItem(new VerbMenuItem(new DeleteFieldVerb(this), 1));
      }
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      if (this._moveMode) {
         MMSPresentationField.drawMoveFocus(graphics, this);
      } else if (on) {
         graphics.setStipple(-1);
         graphics.setColor(15461355);
         graphics.drawRect(0, 0, this.getBitmapWidth(), this.getBitmapHeight());
         graphics.setColor(1052688);
         graphics.setStipple(-252645136);
         graphics.drawRect(0, 0, this.getBitmapWidth(), this.getBitmapHeight());
      } else {
         EncodedImage image = this._image;
         if (image != null && image.hasTransparency()) {
            this.invalidate();
         } else {
            graphics.clear();
            this.paint(graphics);
         }
      }
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      target.addPresentationElement(this.getName(), this.getType(), this.isEditable());
   }

   @Override
   public final boolean canMove() {
      return this.isEditable();
   }

   @Override
   public final void move(boolean mode) {
      if (this._moveMode != mode) {
         this._moveMode = mode;
         this.invalidate();
      }
   }

   @Override
   public final String getName() {
      return this._attachment.getName();
   }

   @Override
   public final int getType() {
      return this._attachment.getType();
   }

   @Override
   public final byte[] getData() {
      return this._attachment.getData();
   }

   @Override
   public final String getCharset() {
      return this._attachment.getCharset();
   }

   @Override
   public final int getDataSize() {
      return this._attachment.getDataSize();
   }
}
