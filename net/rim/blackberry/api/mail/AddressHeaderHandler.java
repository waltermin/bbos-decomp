package net.rim.blackberry.api.mail;

import java.util.Vector;
import net.rim.device.apps.api.addressbook.EmailAddressModel;
import net.rim.device.apps.api.addressbook.PINAddressModel;
import net.rim.device.apps.internal.blackberryemail.header.EmailHeaderModel;

class AddressHeaderHandler implements HeaderHandler {
   public void setHeader(String match, int type, String header, String value, Message m) {
      this.removeHeader(match, type, header, m);
      this.addHeader(match, type, header, value, m);
   }

   public void addHeader(String match, int type, String header, String value, Message m) {
      if (match.toLowerCase().equals(header.toLowerCase())) {
         Address[] a = new Address[1];

         try {
            a[0] = new Address(value, "");
         } catch (AddressException e) {
            throw new IllegalArgumentException(e.toString());
         }

         try {
            m.addRecipients(type, a);
            return;
         } catch (MessagingException e) {
            System.out.println(e.getMessage());
         }
      }
   }

   public void removeHeader(String match, int type, String header, Message m) {
      if (match.toLowerCase().equals(header.toLowerCase())) {
         EmailHeaderModel[] set = m.getHeader(type);

         for (int i = set.length - 1; i >= 0; i--) {
            m.remove(set[i]);
         }
      }
   }

   public String[] getHeader(String match, int type, String header, Message m) {
      if (match.toLowerCase().equals(header.toLowerCase())) {
         EmailHeaderModel[] set = m.getHeader(type);
         int length = set.length;
         String[] data = new String[length];

         for (int i = length - 1; i >= 0; i--) {
            Object o = set[i].getInsideModel();
            if (!(o instanceof EmailAddressModel)) {
               if (o instanceof PINAddressModel) {
                  PINAddressModel pam = (PINAddressModel)o;
                  data[i] = pam.getAddress();
               }
            } else {
               EmailAddressModel eam = (EmailAddressModel)o;
               data[i] = eam.getAddress();
            }
         }

         return data;
      } else {
         return null;
      }
   }

   public Vector getHeaderObjects(Vector v, String header, int type, Message m) {
      String[] s = this.getHeader(header, type, header, m);

      for (int i = 0; i < s.length; i++) {
         v.addElement(new Header(header, s[i]));
      }

      return v;
   }

   @Override
   public Vector getHeaderObjects(Vector _1, Message _2) {
      throw null;
   }

   @Override
   public String[] getHeader(String _1, Message _2) {
      throw null;
   }

   @Override
   public void removeHeader(String _1, Message _2) {
      throw null;
   }

   @Override
   public void setHeader(String _1, String _2, Message _3) {
      throw null;
   }

   @Override
   public void addHeader(String _1, String _2, Message _3) {
      throw null;
   }
}
