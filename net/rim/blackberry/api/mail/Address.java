package net.rim.blackberry.api.mail;

import net.rim.device.api.ui.text.EmailAddressTextFilter;
import net.rim.device.api.util.AbstractStringWrapper;

public class Address {
   protected String _type;
   protected String _addr;
   protected String _name;
   public static String EMAIL_ADDR = "mailto";
   public static String HTTP_ADDR = "http";
   public static String WAP_ADDR = "wap";
   public static String FTP_ADDR = "ftp";
   private static EmailAddressTextFilter _filter = (EmailAddressTextFilter)(new Object());

   public Address(String addr, String name) throws AddressException {
      AbstractStringWrapper asw = AbstractStringWrapper.createInstance(addr);
      if (!_filter.validate(asw)) {
         throw new AddressException(((StringBuffer)(new Object())).append(addr).append(" is an invalid email address").toString());
      }

      this._type = EMAIL_ADDR;
      this._addr = addr;
      this._name = name;
   }

   Address() {
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (o instanceof Address) {
         Address address = (Address)o;
         if (address._type.equals(this._type) && address._addr.equals(this._addr)) {
            if (address._name != null) {
               if (address._name.equals(this._name)) {
                  return true;
               }
            } else if (this._name == null) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public int hashCode() {
      int result = 17;
      result = 37 * result + this._type.hashCode();
      result = 37 * result + this._addr.hashCode();
      if (this._name != null) {
         result = 37 * result + this._name.hashCode();
      }

      return result;
   }

   public String get_type() {
      return this._type;
   }

   public String getName() {
      return this._name;
   }

   public String getAddr() {
      return this._addr;
   }

   @Override
   public String toString() {
      return ((StringBuffer)(new Object())).append(this._type).append(':').append(this._addr).toString();
   }
}
