package com.sun.cldc.i18n.j2me;

import com.sun.cldc.i18n.StreamWriter;

public final class SMS_Writer extends StreamWriter {
   @Override
   public final void write(int c) {
      byte var2;
      switch (c) {
         case 10:
            var2 = 10;
            break;
         case 12:
            super.out.write(27);
            var2 = 10;
            break;
         case 13:
            return;
         case 32:
            var2 = 32;
            break;
         case 33:
            var2 = 33;
            break;
         case 34:
            var2 = 34;
            break;
         case 35:
            var2 = 35;
            break;
         case 36:
            var2 = 2;
            break;
         case 37:
            var2 = 37;
            break;
         case 38:
            var2 = 38;
            break;
         case 39:
            var2 = 39;
            break;
         case 40:
            var2 = 40;
            break;
         case 41:
            var2 = 41;
            break;
         case 42:
            var2 = 42;
            break;
         case 43:
            var2 = 43;
            break;
         case 44:
            var2 = 44;
            break;
         case 45:
            var2 = 45;
            break;
         case 46:
            var2 = 46;
            break;
         case 47:
            var2 = 47;
            break;
         case 48:
            var2 = 48;
            break;
         case 49:
            var2 = 49;
            break;
         case 50:
            var2 = 50;
            break;
         case 51:
            var2 = 51;
            break;
         case 52:
            var2 = 52;
            break;
         case 53:
            var2 = 53;
            break;
         case 54:
            var2 = 54;
            break;
         case 55:
            var2 = 55;
            break;
         case 56:
            var2 = 56;
            break;
         case 57:
            var2 = 57;
            break;
         case 58:
            var2 = 58;
            break;
         case 59:
            var2 = 59;
            break;
         case 60:
            var2 = 60;
            break;
         case 61:
            var2 = 61;
            break;
         case 62:
            var2 = 62;
            break;
         case 63:
            var2 = 63;
            break;
         case 64:
            var2 = 0;
            break;
         case 65:
            var2 = 65;
            break;
         case 66:
            var2 = 66;
            break;
         case 67:
            var2 = 67;
            break;
         case 68:
            var2 = 68;
            break;
         case 69:
            var2 = 69;
            break;
         case 70:
            var2 = 70;
            break;
         case 71:
            var2 = 71;
            break;
         case 72:
            var2 = 72;
            break;
         case 73:
            var2 = 73;
            break;
         case 74:
            var2 = 74;
            break;
         case 75:
            var2 = 75;
            break;
         case 76:
            var2 = 76;
            break;
         case 77:
            var2 = 77;
            break;
         case 78:
            var2 = 78;
            break;
         case 79:
            var2 = 79;
            break;
         case 80:
            var2 = 80;
            break;
         case 81:
            var2 = 81;
            break;
         case 82:
            var2 = 82;
            break;
         case 83:
            var2 = 83;
            break;
         case 84:
            var2 = 84;
            break;
         case 85:
            var2 = 85;
            break;
         case 86:
            var2 = 86;
            break;
         case 87:
            var2 = 87;
            break;
         case 88:
            var2 = 88;
            break;
         case 89:
            var2 = 89;
            break;
         case 90:
            var2 = 90;
            break;
         case 91:
            super.out.write(27);
            var2 = 60;
            break;
         case 92:
            super.out.write(27);
            var2 = 47;
            break;
         case 93:
            super.out.write(27);
            var2 = 62;
            break;
         case 94:
            super.out.write(27);
            var2 = 20;
            break;
         case 95:
            var2 = 17;
            break;
         case 97:
            var2 = 97;
            break;
         case 98:
            var2 = 98;
            break;
         case 99:
            var2 = 99;
            break;
         case 100:
            var2 = 100;
            break;
         case 101:
            var2 = 101;
            break;
         case 102:
            var2 = 102;
            break;
         case 103:
            var2 = 103;
            break;
         case 104:
            var2 = 104;
            break;
         case 105:
            var2 = 105;
            break;
         case 106:
            var2 = 106;
            break;
         case 107:
            var2 = 107;
            break;
         case 108:
            var2 = 108;
            break;
         case 109:
            var2 = 109;
            break;
         case 110:
            var2 = 110;
            break;
         case 111:
            var2 = 111;
            break;
         case 112:
            var2 = 112;
            break;
         case 113:
            var2 = 113;
            break;
         case 114:
            var2 = 114;
            break;
         case 115:
            var2 = 115;
            break;
         case 116:
            var2 = 116;
            break;
         case 117:
            var2 = 117;
            break;
         case 118:
            var2 = 118;
            break;
         case 119:
            var2 = 119;
            break;
         case 120:
            var2 = 120;
            break;
         case 121:
            var2 = 121;
            break;
         case 122:
            var2 = 122;
            break;
         case 123:
            super.out.write(27);
            var2 = 40;
            break;
         case 124:
            super.out.write(27);
            var2 = 64;
            break;
         case 125:
            super.out.write(27);
            var2 = 41;
            break;
         case 126:
            super.out.write(27);
            var2 = 61;
            break;
         case 161:
            var2 = 64;
            break;
         case 163:
            var2 = 1;
            break;
         case 164:
            var2 = 36;
            break;
         case 165:
            var2 = 3;
            break;
         case 167:
            var2 = 95;
            break;
         case 191:
            var2 = 96;
            break;
         case 196:
            var2 = 91;
            break;
         case 197:
            var2 = 14;
            break;
         case 198:
            var2 = 28;
            break;
         case 199:
            var2 = 9;
            break;
         case 201:
            var2 = 31;
            break;
         case 209:
            var2 = 93;
            break;
         case 214:
            var2 = 92;
            break;
         case 216:
            var2 = 11;
            break;
         case 220:
            var2 = 94;
            break;
         case 223:
            var2 = 30;
            break;
         case 224:
            var2 = 127;
            break;
         case 228:
            var2 = 123;
            break;
         case 229:
            var2 = 15;
            break;
         case 230:
            var2 = 29;
            break;
         case 232:
            var2 = 4;
            break;
         case 233:
            var2 = 5;
            break;
         case 236:
            var2 = 7;
            break;
         case 241:
            var2 = 125;
            break;
         case 242:
            var2 = 8;
            break;
         case 246:
            var2 = 124;
            break;
         case 248:
            var2 = 12;
            break;
         case 249:
            var2 = 6;
            break;
         case 252:
            var2 = 126;
            break;
         case 915:
            var2 = 19;
            break;
         case 916:
            var2 = 16;
            break;
         case 920:
            var2 = 25;
            break;
         case 923:
            var2 = 20;
            break;
         case 926:
            var2 = 26;
            break;
         case 928:
            var2 = 22;
            break;
         case 931:
            var2 = 24;
            break;
         case 934:
            var2 = 18;
            break;
         case 936:
            var2 = 23;
            break;
         case 937:
            var2 = 21;
            break;
         case 8364:
            super.out.write(27);
            var2 = 101;
            break;
         default:
            var2 = 63;
      }

      super.out.write(var2);
   }

   @Override
   public final void write(char[] cbuf, int off, int len) {
      while (len-- > 0) {
         this.write(cbuf[off++]);
      }
   }

   @Override
   public final void write(String str, int off, int len) {
      for (int i = 0; i < len; i++) {
         this.write(str.charAt(off + i));
      }
   }

   @Override
   public final int sizeOf(char[] array, int offset, int length) {
      int size = 0;

      while (length-- > 0) {
         int ch = array[offset++];
         switch (ch) {
            case 12:
            case 91:
            case 92:
            case 93:
            case 94:
            case 123:
            case 124:
            case 125:
            case 126:
            case 8364:
               size++;
            default:
               size++;
            case 13:
         }
      }

      return size;
   }
}
