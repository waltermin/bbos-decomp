package net.rim.blackberry.api.mail;

import java.io.InputStream;
import java.io.OutputStream;

public class SupportedAttachmentPart extends BodyPart {
   MailApiAttachmentViewerModel _data;

   public SupportedAttachmentPart(Multipart parent) {
      super(parent);
      this._data = new MailApiAttachmentViewerModel(null);
   }

   SupportedAttachmentPart(Multipart parent, MailApiAttachmentViewerModel data) {
      super(parent);
      this._data = data;
   }

   public SupportedAttachmentPart(Multipart parent, String contentType, String filename, byte[] data) {
      this(parent);
      this.setContent(data);
      this.setContentType(contentType);
      this.setFilename(filename);
   }

   @Override
   public InputStream getInputStream() {
      return null;
   }

   @Override
   public String getContentType() {
      return this._data.getContentType();
   }

   @Override
   public void setContentType(String value) {
      this._data.setContentType(value);
   }

   public String getFilename() {
      return this._data.getFilename();
   }

   public void setFilename(String filename) {
      this._data.setFilename(filename);
   }

   @Override
   public void setContent(Object content) {
      if (!(content instanceof MailApiAttachmentViewerModel)) {
         if (content instanceof byte[]) {
            byte[] data = (byte[])content;
            this._data.setData(data);
            this._data.setTrueLength(data.length);
         }
      } else {
         MailApiAttachmentViewerModel m = (MailApiAttachmentViewerModel)content;
         this._data = m;
      }
   }

   @Override
   public Object getContent() {
      return this._data.getData();
   }

   @Override
   public int getSize() {
      return this._data.getTrueLength();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void writeTo(OutputStream out) {
      super.writeTo(out);
      out.write(BodyPart.CRLF);
      out.write(BodyPart.CRLF);
      boolean var4 = false /* VF: Semaphore variable */;

      try {
         var4 = true;
         out.write(this._data.getFilename().getBytes());
         var4 = false;
      } finally {
         if (var4) {
            throw new Object();
         }
      }

      out.write(BodyPart.CRLF);
      out.write(this._data.getData());
   }

   public String getName() {
      return (String)(new Object(this._data.getNameBytes()));
   }
}
