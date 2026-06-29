package net.rim.blackberry.api.mail;

import java.io.InputStream;
import java.io.OutputStream;

class AbstractContactAttachmentPart extends BodyPart {
   protected Object _data;
   protected byte[] _vcardForm;

   protected AbstractContactAttachmentPart(Multipart parent) {
      super(parent);
   }

   @Override
   public InputStream getInputStream() {
      byte[] data = this.getVCardForm();
      return (InputStream)(new Object(data));
   }

   protected byte[] getVCardForm() {
      throw null;
   }

   @Override
   public void setContent(Object _1) {
      throw null;
   }

   @Override
   public Object getContent() {
      return this._data;
   }

   @Override
   public int getSize() {
      byte[] data = this.getVCardForm();
      return data.length;
   }

   @Override
   public void writeTo(OutputStream out) {
      super.writeTo(out);
      out.write(BodyPart.CRLF);
      out.write(BodyPart.CRLF);
      out.write(this.getVCardForm());
   }
}
