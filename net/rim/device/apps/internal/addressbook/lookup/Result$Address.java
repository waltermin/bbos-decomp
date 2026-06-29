package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;

final class Result$Address {
   private byte[] _firstName;
   private byte[] _lastName;
   private SyncBuffer _buff = (SyncBuffer)(new Object((DataBuffer)(new Object(false)), 0, 0));
   private byte _areNamesEncoded = 0;

   private static final String cleanseValue(byte[] v, boolean isEncoded) {
      int i = isEncoded ? 1 : 0;

      for (int n = v.length - i; i < n; i++) {
         if (v[i] != 32) {
            try {
               return (String)CMIMEUtilities.getTextObject(v, isEncoded);
            } finally {
               ;
            }
         }
      }

      return null;
   }

   private final void appendItem(int type, byte[] v, boolean isEncoded) {
      if (v != null) {
         String s = cleanseValue(v, isEncoded);
         if (s != null) {
            this._buff.addField(isEncoded ? type | -128 : type, s);
         }
      }
   }

   final void setField(int type, byte[] v) {
      boolean isEncoded = false;
      if (((byte)type & -128) == -128) {
         switch ((byte)type & 127) {
            case 33:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 41:
            case 42:
            case 55:
            case 56:
            case 57:
            case 61:
            case 62:
            case 63:
            case 69:
            case 70:
            case 72:
               type &= 127;
               isEncoded = true;
         }
      }

      switch (type) {
         case 35:
         case 61:
            boolean work = type == 35;
            String line1Str = (String)(new Object(v));
            int line2Pos = line1Str.indexOf(13);
            if (line2Pos == -1) {
               this.appendItem(type, line1Str.getBytes(), isEncoded);
               return;
            }

            String line1 = line1Str.substring(0, line2Pos);
            this.appendItem(work ? 35 : 61, line1.getBytes(), isEncoded);
            StringBuffer line2Buffer = (StringBuffer)(new Object());
            int line1Length = line1Str.length();
            boolean spaceAdded = false;

            while (line2Pos < line1Length) {
               char c = line1Str.charAt(line2Pos);
               if (c != '\n' && c != '\r') {
                  spaceAdded = false;
                  line2Buffer.append(c);
               } else if (!spaceAdded) {
                  line2Buffer.append(' ');
                  spaceAdded = true;
               }

               line2Pos++;
            }

            String line2 = line2Buffer.toString().trim();
            if (line2.length() > 0) {
               this.appendItem(work ? 36 : 62, line2.getBytes(), isEncoded);
               return;
            }
            break;
         case 56:
            this._firstName = v;
            if (isEncoded) {
               this._areNamesEncoded = (byte)(this._areNamesEncoded | 1);
               return;
            }
            break;
         case 57:
            this._lastName = v;
            if (isEncoded) {
               this._areNamesEncoded = (byte)(this._areNamesEncoded | 2);
               return;
            }
            break;
         default:
            this.appendItem(type, v, isEncoded);
      }
   }

   final RIMModel convertToModel() {
      this.appendItem(32, this._firstName, (this._areNamesEncoded & 1) == 1);
      this.appendItem(32, this._lastName, (this._areNamesEncoded & 2) == 2);
      ContextObject context = (ContextObject)(new Object(18, 19));
      context.put(255, this._buff);
      this._buff.setPosition(0);
      return (RIMModel)FactoryUtil.createInstance(-3124646573404667739L, context);
   }
}
