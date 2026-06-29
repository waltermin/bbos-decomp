package net.rim.device.apps.internal.api.crypto.certificate;

class CertificateAttachmentField$AttachedCertificateListField extends CertificateListField {
   private final CertificateAttachmentField this$0;

   CertificateAttachmentField$AttachedCertificateListField(CertificateAttachmentField _1, int numRows) {
      super(numRows, 51539607552L);
      this.this$0 = _1;
   }

   @Override
   protected int getCheckState(int index) {
      return this.this$0._certificateAttachmentModel.getCheckState(index);
   }

   @Override
   protected String getText(int index) {
      return this.this$0._certificateAttachmentModel.getText(index);
   }
}
