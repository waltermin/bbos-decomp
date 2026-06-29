package net.rim.blackberry.api.mail;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.apps.internal.blackberryemail.unknown.UnknownMimePartModel;

public final class UnsupportedAttachmentPart extends BodyPart {
   private UnknownMimePartModel _data;

   UnsupportedAttachmentPart(Multipart parent) {
      super(parent);
      this.setContentType("application/octet-stream");
   }

   @Override
   public final InputStream getInputStream() {
      return null;
   }

   @Override
   public final void setContent(Object content) {
      this._data = (UnknownMimePartModel)content;
   }

   @Override
   public final Object getContent() {
      return null;
   }

   @Override
   public final int getSize() {
      return 0;
   }

   @Override
   public final void writeTo(OutputStream out) {
      super.writeTo(out);
      out.write(BodyPart.CRLF);
      out.write(BodyPart.CRLF);
      out.write(this._data.getNameBytes());
   }

   public final String getName() {
      return new String(this._data.getNameBytes());
   }
}
