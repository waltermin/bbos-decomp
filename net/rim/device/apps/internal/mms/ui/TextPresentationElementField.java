package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ActiveAutoTextEditField;
import net.rim.device.apps.internal.mms.MMSUtilities;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.MMSAttachmentImpl;
import net.rim.device.apps.internal.mms.options.MMSClientServiceBook;

final class TextPresentationElementField extends ActiveAutoTextEditField implements PresentationElement, MMSAttachment {
   private MMSAttachment _attachment;
   private boolean _isEditable;
   private boolean _moveMode;

   public TextPresentationElementField(String str, boolean isEditable) {
      this(
         new MMSAttachmentImpl(((StringBuffer)(new Object(""))).append(System.currentTimeMillis()).append(".txt").toString(), 3, str.getBytes(), "utf-8"),
         isEditable
      );
   }

   public TextPresentationElementField(MMSAttachment attachment, boolean isEditable) {
      super(null, null, getMaxChars(), getFlags(isEditable));
      this._attachment = attachment;
      this._isEditable = isEditable;
      this.setAdjustAlignments(!isEditable);
      byte[] data = MMSUtilities.decrypt(attachment.getData());
      if (data != null) {
         this.setText(MMSUtilities.byteArrayToString(data, attachment.getCharset()));
      }
   }

   private static final int getMaxChars() {
      return Math.min(1000000, MMSClientServiceBook.getMaxComposeTextLength());
   }

   private static final long getFlags(boolean isEditable) {
      return isEditable ? 4503599627370496L : 9007199254740992L;
   }

   @Override
   protected final void drawFocus(Graphics graphics, boolean on) {
      if (this._moveMode) {
         MMSPresentationField.drawMoveFocus(graphics, this);
      } else {
         super.drawFocus(graphics, on);
      }
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      target.addPresentationElement(this.getName(), this.getType(), this.isEditable());
   }

   @Override
   public final boolean canMove() {
      return this._isEditable;
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
      if (!this._isEditable) {
         return this._attachment.getData();
      }

      String encoding = this.getCharset();
      if (encoding != null) {
         try {
            return this.getText().getBytes(encoding);
         } finally {
            return this.getText().getBytes();
         }
      } else {
         return this.getText().getBytes();
      }
   }

   @Override
   public final String getCharset() {
      return this._attachment.getCharset();
   }

   @Override
   public final int getDataSize() {
      if (this._isEditable) {
         byte[] data = this.getData();
         return data.length;
      } else {
         return this._attachment.getDataSize();
      }
   }
}
