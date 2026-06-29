package net.rim.device.apps.internal.bluetooth;

import javax.bluetooth.DataElement;
import javax.bluetooth.UUID;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.bluetooth.BluetoothEvents;
import net.rim.device.internal.bluetooth.UUIDUtilities;

final class DataElementUtilities {
   public static final void skip(DataBuffer buf) {
      read(buf, false);
   }

   public static final DataElement read(DataBuffer buf) {
      return read(buf, true);
   }

   private static final DataElement read(DataBuffer buf, boolean create) {
      int dataType = buf.readUnsignedByte();
      int length;
      switch (dataType & 7) {
         case -1:
            throw new Object("Bad DESD");
         case 0:
         default:
            length = dataType == 0 ? 0 : 1;
            break;
         case 1:
            length = 2;
            break;
         case 2:
            length = 4;
            break;
         case 3:
            length = 8;
            break;
         case 4:
            length = 16;
            break;
         case 5:
            length = buf.readUnsignedByte();
            break;
         case 6:
            length = buf.readUnsignedShort();
            break;
         case 7:
            length = buf.readInt();
      }

      if (create) {
         return createDataElement(dataType, buf, length);
      }

      buf.skipBytes(length);
      return null;
   }

   private static final DataElement createDataElement(int dataType, DataBuffer buf, int length) {
      switch (dataType) {
         case 0:
            return new DataElement(0);
         case 8:
            return new DataElement(dataType, buf.readUnsignedByte());
         case 9:
            return new DataElement(dataType, buf.readUnsignedShort());
         case 10:
            return new DataElement(dataType, buf.readInt() & 4294967295L);
         case 11:
         case 12:
         case 20:
            byte[] data = new byte[length];
            buf.read(data, 0, length);
            return new DataElement(dataType, data);
         case 16:
            return new DataElement(dataType, buf.readByte());
         case 17:
            return new DataElement(dataType, buf.readShort());
         case 18:
            return new DataElement(dataType, buf.readInt());
         case 19:
            return new DataElement(dataType, buf.readLong());
         case 40:
            return new DataElement(buf.readUnsignedByte() != 0);
         default:
            dataType &= 248;
            switch (dataType) {
               case 24:
                  byte[] var15 = new byte[length];
                  buf.read(var15, 0, length);
                  UUID uuid = new UUID(UUIDUtilities.toString(var15), var15.length != 16);
                  return new DataElement(dataType, uuid);
               case 32:
               case 64:
                  byte[] var14 = new byte[length];
                  buf.read(var14, 0, length);

                  try {
                     if (length != 0 && var14[length - 1] == 0) {
                        length--;
                     }

                     return new DataElement(dataType, new Object(var14, 0, length, "UTF8"));
                  } finally {
                     ;
                  }
               case 48:
               case 56:
                  DataElement dataElement = new DataElement(dataType);
                  byte[] datax = new byte[length];
                  buf.read(datax, 0, length);

                  try {
                     DataBuffer buf1 = (DataBuffer)(new Object(datax, 0, length, true));

                     while (!buf1.eof()) {
                        dataElement.addElement(read(buf1));
                     }

                     return dataElement;
                  } finally {
                     BluetoothEvents.log(1111770444);
                     return null;
                  }
               default:
                  throw new Object();
            }
      }
   }

   public static final int readDET_SEQ(DataBuffer buf) {
      switch (buf.readUnsignedByte()) {
         case 52:
            throw new Object();
         case 53:
         default:
            return buf.readUnsignedByte();
         case 54:
            return buf.readUnsignedShort();
         case 55:
            return buf.readInt();
      }
   }

   public static final void write(DataElement param0, DataBuffer param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokevirtual javax/bluetooth/DataElement.getDataType ()I
      // 004: istore 2
      // 005: iload 2
      // 006: lookupswitch 643 17 0 146 8 174 9 194 10 214 11 292 12 292 16 233 17 248 18 263 19 278 20 292 24 317 32 344 40 152 48 569 56 569 64 344
      // 098: aload 1
      // 099: iload 2
      // 09a: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 09d: return
      // 09e: aload 1
      // 09f: iload 2
      // 0a0: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0a3: aload 1
      // 0a4: aload 0
      // 0a5: invokevirtual javax/bluetooth/DataElement.getBoolean ()Z
      // 0a8: ifeq 0af
      // 0ab: bipush 1
      // 0ac: goto 0b0
      // 0af: bipush 0
      // 0b0: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0b3: return
      // 0b4: aload 1
      // 0b5: iload 2
      // 0b6: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0b9: aload 1
      // 0ba: aload 0
      // 0bb: invokevirtual javax/bluetooth/DataElement.getLong ()J
      // 0be: sipush 255
      // 0c1: i2l
      // 0c2: land
      // 0c3: l2i
      // 0c4: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0c7: return
      // 0c8: aload 1
      // 0c9: iload 2
      // 0ca: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0cd: aload 1
      // 0ce: aload 0
      // 0cf: invokevirtual javax/bluetooth/DataElement.getLong ()J
      // 0d2: ldc_w 65535
      // 0d5: i2l
      // 0d6: land
      // 0d7: l2i
      // 0d8: invokevirtual net/rim/device/api/util/DataBuffer.writeShort (I)V
      // 0db: return
      // 0dc: aload 1
      // 0dd: iload 2
      // 0de: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0e1: aload 1
      // 0e2: aload 0
      // 0e3: invokevirtual javax/bluetooth/DataElement.getLong ()J
      // 0e6: ldc2_w 4294967295
      // 0e9: land
      // 0ea: l2i
      // 0eb: invokevirtual net/rim/device/api/util/DataBuffer.writeInt (I)V
      // 0ee: return
      // 0ef: aload 1
      // 0f0: iload 2
      // 0f1: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0f4: aload 1
      // 0f5: aload 0
      // 0f6: invokevirtual javax/bluetooth/DataElement.getLong ()J
      // 0f9: l2i
      // 0fa: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 0fd: return
      // 0fe: aload 1
      // 0ff: iload 2
      // 100: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 103: aload 1
      // 104: aload 0
      // 105: invokevirtual javax/bluetooth/DataElement.getLong ()J
      // 108: l2i
      // 109: invokevirtual net/rim/device/api/util/DataBuffer.writeShort (I)V
      // 10c: return
      // 10d: aload 1
      // 10e: iload 2
      // 10f: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 112: aload 1
      // 113: aload 0
      // 114: invokevirtual javax/bluetooth/DataElement.getLong ()J
      // 117: l2i
      // 118: invokevirtual net/rim/device/api/util/DataBuffer.writeInt (I)V
      // 11b: return
      // 11c: aload 1
      // 11d: iload 2
      // 11e: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 121: aload 1
      // 122: aload 0
      // 123: invokevirtual javax/bluetooth/DataElement.getLong ()J
      // 126: invokevirtual net/rim/device/api/util/DataBuffer.writeLong (J)V
      // 129: return
      // 12a: aload 1
      // 12b: iload 2
      // 12c: invokevirtual net/rim/device/api/util/DataBuffer.writeByte (I)V
      // 12f: aload 0
      // 130: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 133: checkcast [B
      // 136: astore 5
      // 138: aload 1
      // 139: aload 5
      // 13b: bipush 0
      // 13c: aload 5
      // 13e: arraylength
      // 13f: invokevirtual net/rim/device/api/util/DataBuffer.write ([BII)V
      // 142: return
      // 143: aload 0
      // 144: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 147: checkcast javax/bluetooth/UUID
      // 14a: astore 6
      // 14c: aload 6
      // 14e: invokevirtual javax/bluetooth/UUID.toString ()Ljava/lang/String;
      // 151: invokestatic net/rim/device/internal/bluetooth/UUIDUtilities.serialize (Ljava/lang/String;)[B
      // 154: astore 3
      // 155: aload 1
      // 156: aload 3
      // 157: bipush 0
      // 158: aload 3
      // 159: arraylength
      // 15a: invokevirtual net/rim/device/api/util/DataBuffer.write ([BII)V
      // 15d: return
      // 15e: aload 0
      // 15f: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 162: checkcast java/lang/Object
      // 165: astore 7
      // 167: ldc_w "ENCODING:"
      // 16a: astore 8
      // 16c: aload 7
      // 16e: aload 8
      // 170: invokevirtual java/lang/String.indexOf (Ljava/lang/String;)I
      // 173: istore 9
      // 175: ldc_w "UTF8"
      // 178: astore 10
      // 17a: iload 9
      // 17c: ifne 1f1
      // 17f: aload 7
      // 181: invokevirtual java/lang/String.length ()I
      // 184: aload 8
      // 186: invokevirtual java/lang/String.length ()I
      // 189: bipush 1
      // 18a: iadd
      // 18b: if_icmple 1f1
      // 18e: aload 7
      // 190: aload 8
      // 192: invokevirtual java/lang/String.length ()I
      // 195: invokevirtual java/lang/String.charAt (I)C
      // 198: bipush 91
      // 19a: if_icmpne 1f1
      // 19d: aload 8
      // 19f: invokevirtual java/lang/String.length ()I
      // 1a2: bipush 1
      // 1a3: iadd
      // 1a4: istore 11
      // 1a6: iload 11
      // 1a8: istore 12
      // 1aa: iload 12
      // 1ac: aload 7
      // 1ae: invokevirtual java/lang/String.length ()I
      // 1b1: if_icmpge 1f1
      // 1b4: aload 7
      // 1b6: iload 12
      // 1b8: invokevirtual java/lang/String.charAt (I)C
      // 1bb: bipush 93
      // 1bd: if_icmpne 1eb
      // 1c0: iload 11
      // 1c2: iload 12
      // 1c4: if_icmpne 1cd
      // 1c7: aconst_null
      // 1c8: astore 10
      // 1ca: goto 1d8
      // 1cd: aload 7
      // 1cf: iload 11
      // 1d1: iload 12
      // 1d3: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 1d6: astore 10
      // 1d8: aload 7
      // 1da: iload 12
      // 1dc: bipush 1
      // 1dd: iadd
      // 1de: aload 7
      // 1e0: invokevirtual java/lang/String.length ()I
      // 1e3: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 1e6: astore 7
      // 1e8: goto 1f1
      // 1eb: iinc 12 1
      // 1ee: goto 1aa
      // 1f1: aload 10
      // 1f3: ifnonnull 1fe
      // 1f6: aload 7
      // 1f8: invokevirtual java/lang/String.getBytes ()[B
      // 1fb: goto 205
      // 1fe: aload 7
      // 200: aload 10
      // 202: invokevirtual java/lang/String.getBytes (Ljava/lang/String;)[B
      // 205: astore 11
      // 207: aload 7
      // 209: invokevirtual java/lang/String.length ()I
      // 20c: aload 10
      // 20e: ifnonnull 215
      // 211: bipush 0
      // 212: goto 216
      // 215: bipush 1
      // 216: iadd
      // 217: istore 4
      // 219: iload 2
      // 21a: iload 4
      // 21c: aload 1
      // 21d: invokestatic net/rim/device/apps/internal/bluetooth/DataElementUtilities.writeDataType (IILnet/rim/device/api/util/DataBuffer;)V
      // 220: aload 11
      // 222: astore 3
      // 223: aload 1
      // 224: aload 3
      // 225: bipush 0
      // 226: aload 3
      // 227: arraylength
      // 228: invokevirtual net/rim/device/api/util/DataBuffer.write ([BII)V
      // 22b: aload 10
      // 22d: ifnull 291
      // 230: aload 1
      // 231: bipush 0
      // 232: invokevirtual net/rim/device/api/util/DataBuffer.write (I)V
      // 235: return
      // 236: astore 8
      // 238: return
      // 239: astore 8
      // 23b: return
      // 23c: astore 8
      // 23e: return
      // 23f: aload 0
      // 240: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 243: checkcast java/lang/Object
      // 246: astore 8
      // 248: new java/lang/Object
      // 24b: dup
      // 24c: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 24f: astore 9
      // 251: aload 8
      // 253: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 258: ifeq 26d
      // 25b: aload 8
      // 25d: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 262: checkcast javax/bluetooth/DataElement
      // 265: aload 9
      // 267: invokestatic net/rim/device/apps/internal/bluetooth/DataElementUtilities.write (Ljavax/bluetooth/DataElement;Lnet/rim/device/api/util/DataBuffer;)V
      // 26a: goto 251
      // 26d: aload 9
      // 26f: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 272: aload 9
      // 274: invokevirtual net/rim/device/api/util/DataBuffer.getLength ()I
      // 277: istore 4
      // 279: iload 2
      // 27a: iload 4
      // 27c: aload 1
      // 27d: invokestatic net/rim/device/apps/internal/bluetooth/DataElementUtilities.writeDataType (IILnet/rim/device/api/util/DataBuffer;)V
      // 280: aload 1
      // 281: aload 9
      // 283: iload 4
      // 285: invokevirtual net/rim/device/api/util/DataBuffer.write (Lnet/rim/device/api/util/DataBuffer;I)V
      // 288: return
      // 289: new java/lang/Object
      // 28c: dup
      // 28d: invokespecial java/lang/ClassCastException.<init> ()V
      // 290: athrow
      // 291: return
      // try (124 -> 220): 221 null
      // try (124 -> 220): 223 null
      // try (124 -> 220): 225 null
   }

   private static final void writeDataType(int dataType, int length, DataBuffer buf) {
      if (length <= 256) {
         buf.write(dataType | 5);
         buf.writeByte(length);
      } else if (length <= 65536) {
         buf.write(dataType | 6);
         buf.writeShort(length);
      } else {
         buf.write(dataType | 7);
         buf.writeInt(length);
      }
   }
}
