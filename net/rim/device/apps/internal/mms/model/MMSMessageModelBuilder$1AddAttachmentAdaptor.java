package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

class MMSMessageModelBuilder$1AddAttachmentAdaptor implements MMSPresentationModel {
   AttachmentDataProvider _source;
   private final MMSMessageModelBuilder this$0;

   MMSMessageModelBuilder$1AddAttachmentAdaptor(MMSMessageModelBuilder _1, AttachmentDataProvider source) {
      this.this$0 = _1;
      this._source = source;
   }

   private void addAttachment(String filename) {
      if (!this.this$0._payload.hasAttachment(filename)) {
         this.this$0._payload.addAttachment(this._source.getAttachment(filename));
      }
   }

   @Override
   public boolean canAddPresentationElement(int attachmentType, long attachmentSize) {
      return true;
   }

   @Override
   public void addPresentationElement(String filename, int type, boolean isEditable, boolean isForwardLocked) {
      this.addAttachment(filename);
   }

   @Override
   public void addPresentationElement(String filename, int type, boolean isEditable) {
      this.addAttachment(filename);
   }

   @Override
   public void addPresentationElement(MMSAttachment attachment, boolean isEditable) {
      this.addAttachment(attachment.getName());
   }

   @Override
   public void addPresentationElement(MMSAttachment attachment, boolean isEditable, boolean isForwardLocked) {
      this.addAttachment(attachment.getName());
   }

   @Override
   public void addSlideBreak(int duration, boolean isEditable) {
   }

   @Override
   public void copyTo(MMSPresentationModel target) {
   }

   @Override
   public String getName() {
      return null;
   }

   @Override
   public int getType() {
      return 65536;
   }

   @Override
   public byte[] getData() {
      return null;
   }

   @Override
   public String getCharset() {
      return null;
   }

   @Override
   public int getDataSize() {
      return 0;
   }
}
