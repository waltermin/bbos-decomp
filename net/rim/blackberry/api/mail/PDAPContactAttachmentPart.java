package net.rim.blackberry.api.mail;

import java.io.ByteArrayOutputStream;
import javax.microedition.pim.Contact;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import net.rim.blackberry.api.pdap.ContactImpl;
import net.rim.blackberry.api.pdap.ContactListImpl;

public class PDAPContactAttachmentPart extends AbstractContactAttachmentPart {
   protected static ContactListImpl _contactList;

   public PDAPContactAttachmentPart(Multipart parent) {
      super(parent);
      this.setContentType("application/x-rimdeviceaddress book");
   }

   public PDAPContactAttachmentPart(Multipart parent, Contact contact) {
      this(parent);
      super._data = contact;
   }

   @Override
   protected byte[] getVCardForm() {
      if (super._vcardForm == null) {
         ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object());

         try {
            String[] dataFormats = PIM.getInstance().supportedSerialFormats(1);
            PIM.getInstance().toSerialFormat((Contact)super._data, baos, "UTF8", dataFormats[0]);
            super._vcardForm = baos.toByteArray();
            return super._vcardForm;
         } catch (PIMException var5) {
         } finally {
            ;
         }

         return BodyPart.EMPTY;
      } else {
         return super._vcardForm;
      }
   }

   @Override
   public void setContent(Object content) {
      if (content instanceof Object) {
         super._data = new ContactImpl(content, _contactList);
      } else {
         if (content instanceof Contact) {
            super._data = content;
         }
      }
   }

   static {
      try {
         _contactList = (ContactListImpl)PIM.getInstance().openPIMList(1, 3);
      } catch (PIMException e) {
         throw new Object(e.toString());
      }
   }
}
