package net.rim.device.cldc.io.ippp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.util.IntHashtable;

public final class SBApplicationData {
   private IntHashtable _applicationDataTable = new IntHashtable();
   private int _serviceRecordID = -1;
   public static final int TYPE_IPPP_DATA_SIZE = 1;
   public static final int TYPE_IPPP_TIMEOUT = 2;
   public static final int TYPE_MAXIMUM_CACHE_SIZE = 3;
   public static final int TYPE_MAXIMUM_QUEUE_SIZE = 4;
   public static final int TYPE_MAXIMUM_PENDING_SIZE = 5;
   public static final int TYPE_IPPP_TYPE = 6;
   public static final int TYPE_IPPP_CAPABILITIES = 7;

   public SBApplicationData(ServiceRecord rec) {
      this.readData(rec);
   }

   private final synchronized void readData(ServiceRecord param1) throws IOException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: ifnull 6a
      // 04: aload 0
      // 05: aload 1
      // 06: invokevirtual net/rim/device/api/servicebook/ServiceRecord.getId ()I
      // 09: putfield net/rim/device/cldc/io/ippp/SBApplicationData._serviceRecordID I
      // 0c: aload 1
      // 0d: invokevirtual net/rim/device/api/servicebook/ServiceRecord.getApplicationData ()[B
      // 10: astore 2
      // 11: aload 2
      // 12: ifnull 75
      // 15: new java/io/ByteArrayInputStream
      // 18: dup
      // 19: aload 2
      // 1a: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 1d: astore 3
      // 1e: new java/io/DataInputStream
      // 21: dup
      // 22: aload 3
      // 23: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 26: astore 4
      // 28: bipush 0
      // 29: istore 5
      // 2b: iload 5
      // 2d: ifne 75
      // 30: aload 4
      // 32: invokevirtual java/io/DataInputStream.readShort ()S
      // 35: istore 6
      // 37: aload 4
      // 39: invokevirtual java/io/DataInputStream.readByte ()B
      // 3c: istore 7
      // 3e: iload 6
      // 40: newarray 8
      // 42: astore 8
      // 44: aload 4
      // 46: aload 8
      // 48: invokevirtual java/io/DataInputStream.readFully ([B)V
      // 4b: aload 0
      // 4c: getfield net/rim/device/cldc/io/ippp/SBApplicationData._applicationDataTable Lnet/rim/device/api/util/IntHashtable;
      // 4f: iload 7
      // 51: aload 8
      // 53: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 56: pop
      // 57: goto 2b
      // 5a: astore 6
      // 5c: bipush 1
      // 5d: istore 5
      // 5f: goto 2b
      // 62: astore 6
      // 64: bipush 1
      // 65: istore 5
      // 67: goto 2b
      // 6a: new java/io/IOException
      // 6d: dup
      // 6e: ldc_w "Service book error"
      // 71: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 74: athrow
      // 75: return
      // try (25 -> 43): 44 null
      // try (25 -> 43): 48 null
   }

   private static final byte[] getIntAsBytes(int value) {
      int topBit = 31;

      while (topBit >= 0 && (value & 1 << topBit) == 0) {
         topBit--;
      }

      int numBytes = (topBit + 8) / 8;
      byte[] data = new byte[numBytes];

      for (int i = numBytes - 1; i >= 0; i--) {
         data[numBytes - 1 - i] = (byte)(value >> i * 8 & 0xFF);
      }

      return data;
   }

   public static final byte[] createAppData(int dataSize, int timeout, int cacheSize, int queueSize, int pendingSize, int type) {
      try {
         ByteArrayOutputStream bouts = new ByteArrayOutputStream();
         DataOutputStream douts = new DataOutputStream(bouts);
         if (dataSize >= 0) {
            byte[] data = getIntAsBytes(dataSize);
            douts.writeShort(data.length);
            douts.writeByte(1);
            douts.write(data);
         }

         if (timeout >= 0) {
            byte[] data = getIntAsBytes(timeout);
            douts.writeShort(data.length);
            douts.writeByte(2);
            douts.write(data);
         }

         if (cacheSize >= 0) {
            byte[] data = getIntAsBytes(dataSize);
            douts.writeShort(data.length);
            douts.writeByte(3);
            douts.write(data);
         }

         if (queueSize >= 0) {
            byte[] data = getIntAsBytes(queueSize);
            douts.writeShort(data.length);
            douts.writeByte(4);
            douts.write(data);
         }

         if (pendingSize >= 0) {
            byte[] data = getIntAsBytes(pendingSize);
            douts.writeShort(data.length);
            douts.writeByte(5);
            douts.write(data);
         }

         if (type >= 0) {
            byte[] data = getIntAsBytes(type);
            douts.writeShort(data.length);
            douts.writeByte(6);
            douts.write(data);
         }

         return bouts.toByteArray();
      } finally {
         ;
      }
   }

   public final void update() {
      this._applicationDataTable.clear();
      ServiceRecord sr = ServiceBook.getSB().getRecordById(this._serviceRecordID);
      this.readData(sr);
   }

   public final int getServiceRecordId() {
      return this._serviceRecordID;
   }

   public final byte[] getValue(int type) {
      return (byte[])this._applicationDataTable.get(type);
   }

   public final String getValueAsString(int type) {
      byte[] value = this.getValue(type);
      return value != null ? new String(value, 0, value.length - 1) : null;
   }

   public final int getValueAsInt(int type) {
      byte[] value = this.getValue(type);
      if (value == null) {
         return 0;
      }

      int someInt = 0;

      for (int i = 0; i < value.length; i++) {
         someInt <<= 8;
         someInt |= value[i] & 255;
      }

      return someInt;
   }

   public final byte getValueAsByte(int type) {
      return (byte)this.getValueAsInt(type);
   }

   public final short getValueAsShort(int type) {
      return (short)this.getValueAsInt(type);
   }
}
