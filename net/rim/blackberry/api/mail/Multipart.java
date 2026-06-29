package net.rim.blackberry.api.mail;

import java.io.OutputStream;
import java.util.Vector;
import net.rim.blackberry.api.mail.rim.MailConverter;
import net.rim.blackberry.api.mail.rim.MailConverterManager;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

public class Multipart {
   protected String _contentType;
   protected Part _parent;
   protected Vector _parts;
   private static MailConverterManager _mailConverterManager = MailConverterManager.getInstance();

   public Multipart() {
      this("multipart/mixed");
   }

   public Multipart(String type) {
      this._contentType = type;
      this._parts = (Vector)(new Object());
   }

   protected Multipart(String type, Part parent) {
      this(type);
      this._parent = parent;
      this.initialize(parent);
   }

   protected Multipart(Part parent) {
      this("multipart/mixed", parent);
   }

   private void initialize(Part parent) {
      Message m = (Message)parent;
      EmailMessageModel emm = m.getEmailMessageModel();
      ReadableList l = emm;
      int size = l.size();

      for (int i = 0; i < size; i++) {
         Object element = l.getAt(i);
         MailConverter mc = _mailConverterManager.getConverter(element);
         if (mc != null) {
            this.addBodyPart((BodyPart)mc.convert(element, this));
         }
      }
   }

   public void writeTo(OutputStream os) {
      os.write(BodyPart.CONTENT_TYPE.getBytes());
      os.write(58);
      os.write(this.getContentType().getBytes());
      os.write(BodyPart.CRLF);
      os.write(BodyPart.CRLF);
      int count = this.getCount();

      for (int i = 0; i < count; i++) {
         BodyPart bp = this.getBodyPart(i);
         bp.writeTo(os);
      }
   }

   public void addBodyPart(BodyPart part) {
      if (part != null) {
         this._parts.addElement(part);
      }
   }

   public void addBodyPart(BodyPart part, int index) {
      if (part != null) {
         this._parts.insertElementAt(part, index);
      }
   }

   public BodyPart getBodyPart(int index) {
      return (BodyPart)this._parts.elementAt(index);
   }

   public String getContentType() {
      return this._contentType;
   }

   public int getCount() {
      return this._parts.size();
   }

   public Part getParent() {
      return this._parent;
   }

   public boolean removeBodyPart(BodyPart part) {
      return this._parts.removeElement(part);
   }

   public void removeBodyPart(int index) {
      this._parts.removeElementAt(index);
   }

   public void setParent(Part parent) {
      this._parent = parent;
   }
}
