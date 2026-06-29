package com.fourthpass.wmls;

import java.io.UnsupportedEncodingException;

final class ConstantPool {
   private int _charSet;
   private Constant[] _constants;

   public ConstantPool(WMLInputStream stream, String charsetString) throws UnsupportedEncodingException {
      int count = stream.readMBInt();
      this._charSet = stream.readMBInt();
      if (this._charSet != 1000 && this._charSet != 106 && this._charSet != 4 && this._charSet != 0) {
         throw new UnsupportedEncodingException();
      }

      if (this._charSet == 0) {
         this._charSet = this.getCharSet(charsetString);
      }

      this._constants = new Constant[count];

      for (int i = 0; i < count; i++) {
         this._constants[i] = this.getConstant(stream);
      }
   }

   private final int getCharSet(String s) throws UnsupportedEncodingException {
      if (s == null) {
         return 106;
      } else if (s.equals("ISO-10646-UCS-2")) {
         return 1000;
      } else if (s.equals("ISO-8859-1")) {
         return 106;
      } else if (s.equals("UTF-8")) {
         return 4;
      } else {
         throw new UnsupportedEncodingException();
      }
   }

   final Constant getConstant(int i) {
      return this._constants[i];
   }

   final Constant getConstant(WMLInputStream stream) throws UnsupportedEncodingException, Exception {
      int type = stream.readUInt8();
      switch (type) {
         case -1:
            throw new Exception("Error Reading a Constant");
         case 0:
         default:
            return new ConstantInteger(stream.readInt8());
         case 1:
            return new ConstantInteger(stream.readInt16());
         case 2:
            return new ConstantInteger(stream.readInt32());
         case 3:
            return new ConstantFloat(stream.readIEEE754());
         case 4:
            return new ConstantString(new String(this.readMBSizeGetBytes(stream), "utf-8"));
         case 5:
            return new ConstantString();
         case 6:
            switch (this._charSet) {
               case 4:
                  return new ConstantString(new String(this.readMBSizeGetBytes(stream)));
               case 106:
                  return new ConstantString(new String(this.readMBSizeGetBytes(stream)));
               case 1000:
                  int size = stream.readMBInt();
                  byte[] bytes = new byte[size];
                  stream.readBytes(bytes);
                  char[] ac = new char[size / 2];

                  for (int i = 0; i < size; i += 2) {
                     ac[i / 2] = (char)(bytes[i] << 8 | bytes[i + 1]);
                  }

                  return new ConstantString(new String(ac));
               default:
                  throw new UnsupportedEncodingException();
            }
      }
   }

   private final byte[] readMBSizeGetBytes(WMLInputStream stream) {
      byte[] bytes = new byte[stream.readMBInt()];
      stream.readBytes(bytes);
      return bytes;
   }
}
