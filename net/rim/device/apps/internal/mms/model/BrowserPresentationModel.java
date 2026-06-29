package net.rim.device.apps.internal.mms.model;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.ui.MMSPresentationField;

final class BrowserPresentationModel implements MMSPresentationModel, FieldProvider {
   private MMSAttachment _attachment;

   BrowserPresentationModel(MMSAttachment attachment) {
      this._attachment = attachment;
   }

   @Override
   public final int getType() {
      return this._attachment.getType();
   }

   @Override
   public final String getName() {
      return this._attachment.getName();
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

   @Override
   public final void addPresentationElement(String filename, int type, boolean isEditable) {
      throw new Object("Not implemented yet");
   }

   @Override
   public final void addPresentationElement(String filename, int type, boolean isEditable, boolean isForwardLocked) {
      throw new Object("Not implemented yet");
   }

   @Override
   public final void addPresentationElement(MMSAttachment attachment, boolean isEditable) {
      throw new Object("Not implemented yet");
   }

   @Override
   public final void addPresentationElement(MMSAttachment attachment, boolean isEditable, boolean isForwardLocked) {
      throw new Object("Not implemented yet");
   }

   @Override
   public final void addSlideBreak(int duration, boolean isEditable) {
      throw new Object("Not implemented yet");
   }

   @Override
   public final boolean canAddPresentationElement(int attachmentType, long attachmentSize) {
      throw new Object("Not implemented yet");
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      target.addPresentationElement(this._attachment, false);
   }

   @Override
   public final Field getField(Object context) {
      return new MMSPresentationField(this, context);
   }

   @Override
   public final int getOrder(Object context) {
      return 5700;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }
}
