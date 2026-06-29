package net.rim.device.api.io;

public class DatagramAddressBase {
   protected String _address;
   protected int _key;
   public static final int NONE;
   private static final byte[] DIGITS = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

   public DatagramAddressBase() {
   }

   public DatagramAddressBase(DatagramAddressBase addressBase) {
      this.setAddress(addressBase.getAddress());
   }

   public DatagramAddressBase(String address) {
      this.setAddress(address);
   }

   public void setAddress(String address) {
      this._address = address;
   }

   public String getAddress() {
      return this._address;
   }

   public String getSubAddress() {
      return null;
   }

   public DatagramAddressBase getSubAddressBase() {
      return null;
   }

   public int getKey() {
      return this._key;
   }

   public void swap() {
   }

   @Override
   public boolean equals(Object addressBase) {
      if (addressBase == this) {
         return true;
      } else if (!(addressBase instanceof DatagramAddressBase)) {
         return false;
      } else {
         DatagramAddressBase dab = (DatagramAddressBase)addressBase;
         if (this._address == null && dab._address == null) {
            return true;
         } else {
            return this._address == null & dab._address != null ? false : this._address.equals(dab._address);
         }
      }
   }

   @Override
   public int hashCode() {
      return this._address == null ? 7 : this._address.hashCode();
   }

   public static int indexOfNextDelim(String str, int start) {
      int length = str.length();

      while (start < length) {
         switch (str.charAt(start)) {
            case '$':
            case '(':
            case '/':
            case ':':
            case ';':
            case '|':
               return start;
            default:
               start++;
         }
      }

      return start;
   }

   public static short readShort(byte[] buf, int offset) {
      short ret = 0;
      ret = (short)(ret | buf[offset] & 0xFF);
      offset++;
      ret = (short)(ret << 8);
      return (short)(ret | buf[offset] & 0xFF);
   }

   public static int readInt(byte[] buf, int offset) {
      int ret = 0;
      ret |= buf[offset] & 255;
      offset++;
      ret <<= 8;
      ret |= buf[offset] & 255;
      offset++;
      ret <<= 8;
      ret |= buf[offset] & 255;
      offset++;
      ret <<= 8;
      return ret | buf[offset] & 0xFF;
   }

   public static void writeInt(byte[] buf, int offset, int value) {
      offset += 3;
      buf[offset] = (byte)value;
      offset--;
      value >>>= 8;
      buf[offset] = (byte)value;
      offset--;
      value >>>= 8;
      buf[offset] = (byte)value;
      offset--;
      value >>>= 8;
      buf[offset] = (byte)value;
   }

   public static void writeShort(byte[] buf, int offset, int value) {
      buf[++offset] = (byte)value;
      offset--;
      value >>>= 8;
      buf[offset] = (byte)value;
   }

   public static int parseInt(String buf, int start, int end, int radix) {
      int ret = 0;

      for (int i = start; i < end; i++) {
         int digit = Character.digit(buf.charAt(i), radix);
         if (digit < 0) {
            throw new IllegalArgumentException("Invalid digit");
         }

         ret *= radix;
         ret += digit;
      }

      return ret;
   }

   public static int parseInt(byte[] buf, int start, int end, int radix) {
      int ret = 0;

      for (int i = start; i < end; i++) {
         int digit = Character.digit((char)buf[i], radix);
         if (digit < 0) {
            throw new IllegalArgumentException("Invalid digit");
         }

         ret *= radix;
         ret += digit;
      }

      return ret;
   }

   public static long parseLong(String buf, int start, int end, int radix) {
      long ret = 0;

      for (int i = start; i < end; i++) {
         int digit = Character.digit(buf.charAt(i), radix);
         if (digit < 0) {
            throw new IllegalArgumentException("Invalid digit");
         }

         ret *= radix;
         ret += digit;
      }

      return ret;
   }

   public static void appendHex(StringBuffer buf, int offset, int value, int length) {
      int index = offset + 8;
      buf.setLength(index);

      do {
         buf.setCharAt(--index, (char)DIGITS[value & 15]);
         value >>>= 4;
         length--;
      } while (value != 0 || length > 0);

      buf.delete(offset, index);
   }

   public static void appendHex(byte[] buf, int offset, int value, int length) {
      int index = offset + 8;

      do {
         buf[--index] = DIGITS[value & 15];
         value >>>= 4;
         length--;
      } while (value != 0 || length > 0);

      System.arraycopy(buf, index, buf, offset, offset + 8 - index);
   }

   public static int parseIpAddressInt(String address, int offset) {
      int ipAddress = 0;

      for (int i = 0; i < 4; i++) {
         int delim = i < 3 ? address.indexOf(46, offset) : indexOfNextDelim(address, offset);
         if (delim <= offset) {
            throw new IllegalArgumentException("Bad IP_ADDRESS");
         }

         int ret = parseInt(address, offset, delim, 10);
         if (ret < 0 || ret > 255) {
            throw new IllegalArgumentException("Invalid IP_ADDRESS");
         }

         ipAddress <<= 8;
         ipAddress |= ret;
         offset = delim + 1;
      }

      return ipAddress;
   }

   protected static boolean isDomainName(String address, int startIndex, int endIndex) {
      if (endIndex <= startIndex + 1) {
         return false;
      }

      int start = startIndex;
      int end = startIndex;

      do {
         end = address.indexOf(46, end);
         if (end == -1 || end >= endIndex) {
            end = endIndex;
         }

         while (start < end && Character.isDigit(address.charAt(start))) {
            start++;
         }

         if (start < end) {
            return true;
         }

         start = ++end;
      } while (end < endIndex);

      return false;
   }
}
