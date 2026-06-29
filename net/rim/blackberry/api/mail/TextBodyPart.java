package net.rim.blackberry.api.mail;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;

public class TextBodyPart extends BodyPart {
   private String _body;
   public static String TEXT_PLAIN = CMIMEUtilities.getTextContentType();

   public static String getTextPlainContentType() {
      return CMIMEUtilities.getTextContentType();
   }

   public TextBodyPart(Multipart multipart) {
      super(multipart);
   }

   public TextBodyPart(Multipart mp, String text) {
      super(mp);
      this._body = text;
      this.setContentType(CMIMEUtilities.getTextContentType());
   }

   @Override
   public InputStream getInputStream() {
      return (InputStream)(this._body != null ? new Object(this._body.getBytes()) : null);
   }

   @Override
   public void setContent(Object content) {
      this._body = (String)content;
   }

   @Override
   public Object getContent() {
      return this._body;
   }

   @Override
   public int getSize() {
      return this._body.length();
   }

   @Override
   public void writeTo(OutputStream out) {
      super.writeTo(out);
      out.write(BodyPart.CRLF);
      out.write(BodyPart.CRLF);
      out.write(this._body.getBytes());
   }
}
