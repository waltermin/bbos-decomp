package net.rim.device.apps.internal.mms.service;

import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

class SMILAttachmentBuilder$1 implements MMSPresentationModel {
   private final SMILAttachmentBuilder this$0;

   SMILAttachmentBuilder$1(SMILAttachmentBuilder _1) {
      this.this$0 = _1;
   }

   @Override
   public void addPresentationElement(String filename, int type, boolean isEditable) {
      this.this$0.addContent(filename, type);
   }

   @Override
   public void addPresentationElement(String filename, int type, boolean isEditable, boolean isForwardLocked) {
      this.this$0.addContent(filename, type);
   }

   @Override
   public void addPresentationElement(MMSAttachment attachment, boolean isEditable) {
      this.this$0.addContent(attachment.getName(), attachment.getType());
   }

   @Override
   public void addPresentationElement(MMSAttachment attachment, boolean isEditable, boolean isForwardLocked) {
      this.this$0.addContent(attachment.getName(), attachment.getType());
   }

   @Override
   public void addSlideBreak(int duration, boolean isEditable) {
      this.this$0.endPar(duration);
   }

   @Override
   public boolean canAddPresentationElement(int attachmentType, long attachmentSize) {
      return true;
   }

   @Override
   public void copyTo(MMSPresentationModel target) {
      throw new Object();
   }

   @Override
   public String getName() {
      return null;
   }

   @Override
   public byte[] getData() {
      return null;
   }

   @Override
   public int getType() {
      return -1;
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
