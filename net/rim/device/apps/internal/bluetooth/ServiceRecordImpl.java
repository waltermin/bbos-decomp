package net.rim.device.apps.internal.bluetooth;

import java.io.IOException;
import java.util.Enumeration;
import javax.bluetooth.DataElement;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.bluetooth.BluetoothEvents;
import net.rim.vm.Persistable;

class ServiceRecordImpl implements ServiceRecord, Persistable {
   protected int[] _attributeIDs = new int[0];
   protected DataElement[] _attributeValues = new DataElement[0];
   protected byte[] _address;
   static UUID L2CAP_UUID = new UUID(256);
   static UUID RFCOMM_UUID = new UUID(3);
   static UUID OBEX_UUID = new UUID(8);
   static UUID HANDSFREE_UUID = new UUID(4382);
   static UUID HEADSET_UUID = new UUID(4360);
   static UUID GENERIC_AUDIO_UUID = new UUID(4611);
   static UUID SERIAL_PORT_UUID = new UUID(4353);
   static UUID OBJECT_PUSH_UUID = new UUID(4357);
   static UUID AVRCP_TARGET_UUID = new UUID(4364);
   static UUID AVRCP_CONTROLLER_UUID = new UUID(4366);
   static UUID A2DP_SINK_UUID = new UUID(4363);

   public String getServiceName() {
      DataElement element = null;

      try {
         element = this.getAttributeValue(this.getLanguageBaseID() | 0);
      } finally {
         ;
      }

      String name = null;
      if (element != null && element.getDataType() == 32) {
         name = (String)element.getValue();
      }

      return name;
   }

   public boolean containsClasses(UUID[] classes) {
      try {
         DataElement element = this.getAttributeValue(1);
         if (element == null) {
            throw new IOException();
         }

         if (element.getDataType() != 48) {
            throw new IOException();
         }

         Enumeration e = (Enumeration)element.getValue();
         int index = 0;

         while (e.hasMoreElements()) {
            element = (DataElement)e.nextElement();
            if (element.getDataType() != 24) {
               throw new IOException();
            }

            if (classes[index].equals(element.getValue())) {
               index++;
               if (index == classes.length || classes[index] == null) {
                  return true;
               }
            }
         }
      } finally {
         ;
      }

      return false;
   }

   public int getRFCOMMServerChannel() {
      Enumeration e = this.getDETD_SEQ(4, RFCOMM_UUID, 1230001228);
      if (e == null) {
         return 0;
      } else if (!e.hasMoreElements()) {
         BluetoothEvents.log(1230001228);
         return 0;
      } else {
         DataElement element = (DataElement)e.nextElement();
         if (element.getDataType() != 8) {
            BluetoothEvents.log(1230001228);
            return 0;
         } else {
            return (int)element.getLong();
         }
      }
   }

   private Enumeration getDETD_SEQ(int aid, UUID uuid, int errorLogId) {
      try {
         DataElement element = this.getAttributeValue(aid);
         if (element == null) {
            throw new IOException();
         }

         if (element.getDataType() != 48) {
            throw new IOException();
         }

         Enumeration e = (Enumeration)element.getValue();

         while (e.hasMoreElements()) {
            element = (DataElement)e.nextElement();
            if (element.getDataType() != 48) {
               throw new IOException();
            }

            Enumeration e1 = (Enumeration)element.getValue();
            if (!e1.hasMoreElements()) {
               throw new IOException();
            }

            element = (DataElement)e1.nextElement();
            if (element.getDataType() != 24) {
               throw new IOException();
            }

            if (uuid.equals(element.getValue())) {
               return e1;
            }
         }
      } finally {
         ;
      }

      return null;
   }

   private int getLanguageBaseID() {
      return 256;
   }

   public synchronized byte[] serialize() {
      DataBuffer buf = new DataBuffer();
      int length = this._attributeIDs.length;

      for (int i = 0; i < length; i++) {
         buf.write(9);
         buf.writeShort(this._attributeIDs[i]);
         DataElementUtilities.write(this._attributeValues[i], buf);
      }

      return buf.toArray();
   }

   public synchronized byte[][] getAttributeValues() {
      DataBuffer buf = new DataBuffer();
      int length = this._attributeIDs.length;
      byte[][] values = new byte[length][];

      for (int i = 0; i < length; i++) {
         DataElementUtilities.write(this._attributeValues[i], buf);
         values[i] = buf.toArray();
         buf.reset();
      }

      return values;
   }

   @Override
   public synchronized DataElement getAttributeValue(int attrID) {
      if (attrID >= 0 && attrID <= 65535) {
         for (int i = this._attributeIDs.length - 1; i >= 0; i--) {
            if (this._attributeIDs[i] == attrID) {
               return this._attributeValues[i];
            }
         }

         return null;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public int[] getAttributeIDs() {
      return this._attributeIDs;
   }

   @Override
   public String getConnectionURL(int param1, boolean param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 3
      // 002: bipush 0
      // 003: istore 4
      // 005: new java/lang/StringBuffer
      // 008: dup
      // 009: invokespecial java/lang/StringBuffer.<init> ()V
      // 00c: astore 5
      // 00e: iload 1
      // 00f: tableswitch 29 -1 2 37 45 32 29
      // 02c: bipush 1
      // 02d: istore 4
      // 02f: bipush 1
      // 030: istore 3
      // 031: goto 03c
      // 034: new java/lang/IllegalArgumentException
      // 037: dup
      // 038: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // 03b: athrow
      // 03c: aload 0
      // 03d: bipush 4
      // 03f: invokevirtual net/rim/device/apps/internal/bluetooth/ServiceRecordImpl.getAttributeValue (I)Ljavax/bluetooth/DataElement;
      // 042: astore 6
      // 044: aload 6
      // 046: ifnonnull 04b
      // 049: aconst_null
      // 04a: areturn
      // 04b: aload 6
      // 04d: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 050: checkcast java/util/Enumeration
      // 053: astore 7
      // 055: aconst_null
      // 056: astore 8
      // 058: aconst_null
      // 059: astore 9
      // 05b: aconst_null
      // 05c: astore 10
      // 05e: aconst_null
      // 05f: astore 11
      // 061: aload 7
      // 063: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 068: ifeq 097
      // 06b: aload 7
      // 06d: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 072: checkcast javax/bluetooth/DataElement
      // 075: astore 9
      // 077: aload 9
      // 079: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 07c: checkcast java/util/Enumeration
      // 07f: astore 11
      // 081: aload 11
      // 083: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 088: checkcast javax/bluetooth/DataElement
      // 08b: astore 10
      // 08d: aload 10
      // 08f: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 092: checkcast javax/bluetooth/UUID
      // 095: astore 8
      // 097: aload 8
      // 099: ifnull 0a7
      // 09c: aload 8
      // 09e: getstatic net/rim/device/apps/internal/bluetooth/ServiceRecordImpl.L2CAP_UUID Ljavax/bluetooth/UUID;
      // 0a1: invokevirtual javax/bluetooth/UUID.equals (Ljava/lang/Object;)Z
      // 0a4: ifne 0a9
      // 0a7: aconst_null
      // 0a8: areturn
      // 0a9: aconst_null
      // 0aa: astore 8
      // 0ac: aload 7
      // 0ae: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 0b3: ifeq 0e2
      // 0b6: aload 7
      // 0b8: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0bd: checkcast javax/bluetooth/DataElement
      // 0c0: astore 9
      // 0c2: aload 9
      // 0c4: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 0c7: checkcast java/util/Enumeration
      // 0ca: astore 11
      // 0cc: aload 11
      // 0ce: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 0d3: checkcast javax/bluetooth/DataElement
      // 0d6: astore 10
      // 0d8: aload 10
      // 0da: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 0dd: checkcast javax/bluetooth/UUID
      // 0e0: astore 8
      // 0e2: aload 8
      // 0e4: ifnull 0f2
      // 0e7: aload 8
      // 0e9: getstatic net/rim/device/apps/internal/bluetooth/ServiceRecordImpl.RFCOMM_UUID Ljavax/bluetooth/UUID;
      // 0ec: invokevirtual javax/bluetooth/UUID.equals (Ljava/lang/Object;)Z
      // 0ef: ifne 132
      // 0f2: aload 5
      // 0f4: ldc_w "btl2cap://"
      // 0f7: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0fa: pop
      // 0fb: aload 5
      // 0fd: aload 0
      // 0fe: getfield net/rim/device/apps/internal/bluetooth/ServiceRecordImpl._address [B
      // 101: invokestatic net/rim/device/internal/bluetooth/BluetoothME.deviceAddressToString ([B)Ljava/lang/String;
      // 104: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 107: pop
      // 108: aload 5
      // 10a: bipush 58
      // 10c: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 10f: pop
      // 110: aload 11
      // 112: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 117: checkcast javax/bluetooth/DataElement
      // 11a: astore 10
      // 11c: aload 10
      // 11e: invokevirtual javax/bluetooth/DataElement.getLong ()J
      // 121: l2i
      // 122: istore 12
      // 124: aload 5
      // 126: iload 12
      // 128: invokestatic java/lang/Integer.toHexString (I)Ljava/lang/String;
      // 12b: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 12e: pop
      // 12f: goto 1de
      // 132: aload 11
      // 134: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 139: checkcast javax/bluetooth/DataElement
      // 13c: astore 10
      // 13e: aload 10
      // 140: invokevirtual javax/bluetooth/DataElement.getLong ()J
      // 143: l2i
      // 144: istore 12
      // 146: aconst_null
      // 147: astore 8
      // 149: aload 7
      // 14b: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 150: ifeq 17f
      // 153: aload 7
      // 155: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 15a: checkcast javax/bluetooth/DataElement
      // 15d: astore 9
      // 15f: aload 9
      // 161: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 164: checkcast java/util/Enumeration
      // 167: astore 11
      // 169: aload 11
      // 16b: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 170: checkcast javax/bluetooth/DataElement
      // 173: astore 10
      // 175: aload 10
      // 177: invokevirtual javax/bluetooth/DataElement.getValue ()Ljava/lang/Object;
      // 17a: checkcast javax/bluetooth/UUID
      // 17d: astore 8
      // 17f: aload 8
      // 181: ifnull 18f
      // 184: aload 8
      // 186: getstatic net/rim/device/apps/internal/bluetooth/ServiceRecordImpl.OBEX_UUID Ljavax/bluetooth/UUID;
      // 189: invokevirtual javax/bluetooth/UUID.equals (Ljava/lang/Object;)Z
      // 18c: ifne 1b8
      // 18f: aload 5
      // 191: ldc_w "btspp://"
      // 194: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 197: pop
      // 198: aload 5
      // 19a: aload 0
      // 19b: getfield net/rim/device/apps/internal/bluetooth/ServiceRecordImpl._address [B
      // 19e: invokestatic net/rim/device/internal/bluetooth/BluetoothME.deviceAddressToString ([B)Ljava/lang/String;
      // 1a1: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1a4: pop
      // 1a5: aload 5
      // 1a7: bipush 58
      // 1a9: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 1ac: pop
      // 1ad: aload 5
      // 1af: iload 12
      // 1b1: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 1b4: pop
      // 1b5: goto 1de
      // 1b8: aload 5
      // 1ba: ldc_w "btgoep://"
      // 1bd: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1c0: pop
      // 1c1: aload 5
      // 1c3: aload 0
      // 1c4: getfield net/rim/device/apps/internal/bluetooth/ServiceRecordImpl._address [B
      // 1c7: invokestatic net/rim/device/internal/bluetooth/BluetoothME.deviceAddressToString ([B)Ljava/lang/String;
      // 1ca: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1cd: pop
      // 1ce: aload 5
      // 1d0: bipush 58
      // 1d2: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 1d5: pop
      // 1d6: aload 5
      // 1d8: iload 12
      // 1da: invokevirtual java/lang/StringBuffer.append (I)Ljava/lang/StringBuffer;
      // 1dd: pop
      // 1de: aload 5
      // 1e0: ldc_w ";authenticate="
      // 1e3: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1e6: pop
      // 1e7: aload 5
      // 1e9: iload 3
      // 1ea: invokevirtual java/lang/StringBuffer.append (Z)Ljava/lang/StringBuffer;
      // 1ed: pop
      // 1ee: aload 5
      // 1f0: ldc_w ";encrypt="
      // 1f3: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 1f6: pop
      // 1f7: aload 5
      // 1f9: iload 4
      // 1fb: invokevirtual java/lang/StringBuffer.append (Z)Ljava/lang/StringBuffer;
      // 1fe: pop
      // 1ff: aload 5
      // 201: ldc_w ";master="
      // 204: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 207: pop
      // 208: aload 5
      // 20a: iload 2
      // 20b: invokevirtual java/lang/StringBuffer.append (Z)Ljava/lang/StringBuffer;
      // 20e: pop
      // 20f: aload 5
      // 211: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 214: areturn
      // 215: astore 7
      // 217: aconst_null
      // 218: areturn
      // 219: astore 7
      // 21b: aconst_null
      // 21c: areturn
      // try (27 -> 65): 220 null
      // try (66 -> 219): 220 null
      // try (27 -> 65): 223 null
      // try (66 -> 219): 223 null
   }

   @Override
   public boolean setAttributeValue(int _1, DataElement _2) {
      throw null;
   }

   @Override
   public void setDeviceServiceClasses(int _1) {
      throw null;
   }

   @Override
   public boolean populateRecord(int[] _1) {
      throw null;
   }

   @Override
   public RemoteDevice getHostDevice() {
      throw null;
   }
}
