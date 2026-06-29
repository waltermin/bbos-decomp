package net.rim.blackberry.api.mail;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.rim.blackberry.api.pim.Contact;
import net.rim.blackberry.api.pim.ContactImpl;
import net.rim.blackberry.api.pim.ContactListImpl;
import net.rim.blackberry.api.pim.PIM;
import net.rim.blackberry.api.pim.PIMException;

public class ContactAttachmentPart extends BodyPart {
   private Contact _data;
   private byte[] _vcardForm;
   private static ContactListImpl _contactList;

   public ContactAttachmentPart(Multipart parent) {
      super(parent);
      this.setContentType("application/x-rimdeviceaddress book");
   }

   public ContactAttachmentPart(Multipart parent, Contact contact) {
      this(parent);
      this._data = contact;
   }

   @Override
   public InputStream getInputStream() {
      byte[] data = this.getVCardForm();
      return (InputStream)(new Object(data));
   }

   private byte[] getVCardForm() {
      if (this._vcardForm == null) {
         ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object());

         try {
            String[] dataFormats = PIM.getInstance().supportedSerialFormats(1);
            PIM.getInstance().toSerialFormat(this._data, baos, "UTF8", dataFormats[0]);
            this._vcardForm = baos.toByteArray();
            return this._vcardForm;
         } catch (PIMException var5) {
         } finally {
            ;
         }

         return BodyPart.EMPTY;
      } else {
         return this._vcardForm;
      }
   }

   @Override
   public void setContent(Object content) {
      if (content instanceof Object) {
         this._data = new ContactImpl(content, _contactList);
      } else {
         if (content instanceof Contact) {
            this._data = (Contact)content;
         }
      }
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

   static {
      try {
         _contactList = (ContactListImpl)PIM.getInstance().openPIMList(1, 3);
      } catch (PIMException e) {
         throw new Object(e.toString());
      }
   }
}
