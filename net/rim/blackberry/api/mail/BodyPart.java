package net.rim.blackberry.api.mail;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.MorePartModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;

public class BodyPart implements Part {
   protected Multipart _parent;
   private String _contentType;
   private EmailMessageModel _internalModel;
   private int _morePartId;
   public static String CONTENT_TYPE = "Content-Type";
   protected static final char SEPARATOR = ':';
   protected static final byte[] CRLF = new byte[]{13, 10};
   protected static final byte[] EMPTY = new byte[]{0};

   public Multipart getParent() {
      return this._parent;
   }

   MorePartModel getMorePartModel() {
      return EmailMoreVerb.findMorePartByIdentifier(this._internalModel, this._morePartId);
   }

   void setInternalModel(EmailMessageModel model, int morePartId) {
      this._internalModel = model;
      this._morePartId = morePartId;
   }

   public boolean moreRequestSent() {
      MorePartModel mpm = this.getMorePartModel();
      return mpm != null && mpm.getMoreRequestSent();
   }

   public void setContentType(String value) {
      this._contentType = value;
   }

   public boolean hasMore() {
      MorePartModel mpm = this.getMorePartModel();
      return mpm != null && mpm.isMoreAvailable();
   }

   @Override
   public boolean isMimeType(String mimeType) {
      return mimeType.equals(this._contentType);
   }

   @Override
   public void removeHeader(String header) {
      if (header.equals(CONTENT_TYPE)) {
         this.setContentType(null);
      }
   }

   @Override
   public void setHeader(String header, String value) {
      if (header.equals(CONTENT_TYPE)) {
         this.setContentType(value);
      }
   }

   @Override
   public void writeTo(OutputStream os) {
      os.write(CRLF);
      os.write(CONTENT_TYPE.getBytes());
      os.write(58);
      String ctype = this.getContentType();
      os.write(ctype != null ? ctype.getBytes() : EMPTY);
   }

   @Override
   public String[] getHeader(String header) {
      return header.equals(CONTENT_TYPE) ? new Object[]{this.getContentType()} : new Object[0];
   }

   @Override
   public String getContentType() {
      return this._contentType;
   }

   @Override
   public Enumeration getAllHeaders() {
      Vector v = (Vector)(new Object());
      v.addElement(this.getContentType());
      return v.elements();
   }

   @Override
   public void addHeader(String name, String value) {
      this.setHeader(name, value);
   }

   @Override
   public int getSize() {
      throw null;
   }

   @Override
   public InputStream getInputStream() {
      throw null;
   }

   @Override
   public Object getContent() {
      throw null;
   }

   @Override
   public void setContent(Object _1) {
      throw null;
   }

   protected BodyPart(Multipart parent) {
      this._parent = parent;
   }
}
