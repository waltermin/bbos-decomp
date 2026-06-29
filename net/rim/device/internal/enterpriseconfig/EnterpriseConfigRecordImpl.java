package net.rim.device.internal.enterpriseconfig;

import net.rim.device.api.enterpriseconfig.EnterpriseConfigRecord;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.EncryptableProvider;

public class EnterpriseConfigRecordImpl implements EnterpriseConfigRecord, EncryptableProvider {
   private Object _dataEncoding = null;
   private int _uid;

   public EnterpriseConfigRecord createEnterpriseConfigRecord(int uid, byte[] data) {
      return new EnterpriseConfigRecordImpl(uid, data);
   }

   @Override
   public DataBuffer getData() {
      DataBuffer dataBuffer = new DataBuffer(false);

      try {
         byte[] data = (byte[])PersistentContent.decode(this._dataEncoding, false);
         if (data != null) {
            dataBuffer.write(data);
            dataBuffer.rewind();
            return dataBuffer;
         }
      } finally {
         return dataBuffer;
      }

      return dataBuffer;
   }

   @Override
   public byte getTableId() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: bipush 1
      // 01: newarray 8
      // 03: dup
      // 04: bipush 0
      // 05: bipush 0
      // 06: bastore
      // 07: astore 1
      // 08: aload 0
      // 09: invokevirtual net/rim/device/internal/enterpriseconfig/EnterpriseConfigRecordImpl.getData ()Lnet/rim/device/api/util/DataBuffer;
      // 0c: astore 2
      // 0d: aload 2
      // 0e: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 11: istore 3
      // 12: aload 2
      // 13: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 16: aload 2
      // 17: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 1a: ifle 47
      // 1d: aload 2
      // 1e: bipush 127
      // 20: invokestatic net/rim/device/api/synchronization/ConverterUtilities.findType (Lnet/rim/device/api/util/DataBuffer;I)Z
      // 23: ifeq 2b
      // 26: aload 2
      // 27: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 2a: astore 1
      // 2b: aload 2
      // 2c: iload 3
      // 2d: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 30: goto 47
      // 33: astore 4
      // 35: aload 2
      // 36: iload 3
      // 37: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 3a: goto 47
      // 3d: astore 5
      // 3f: aload 2
      // 40: iload 3
      // 41: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 44: aload 5
      // 46: athrow
      // 47: aload 1
      // 48: bipush 0
      // 49: baload
      // 4a: ireturn
      // try (18 -> 25): 29 null
      // try (18 -> 25): 34 null
      // try (29 -> 30): 34 null
      // try (34 -> 35): 34 null
   }

   @Override
   public int getUID() {
      return this._uid;
   }

   @Override
   public boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._dataEncoding, compress, encrypt);
   }

   @Override
   public Object reCrypt(boolean compress, boolean encrypt) {
      this._dataEncoding = PersistentContent.reEncode(this._dataEncoding, compress, encrypt);
      return null;
   }

   public EnterpriseConfigRecordImpl(int uid, DataBuffer buffer) {
      this(uid);
      this._dataEncoding = PersistentContent.encode(buffer.toArray(), true, true);
   }

   public EnterpriseConfigRecordImpl(int uid, byte[] buffer) {
      this(uid);
      this._dataEncoding = PersistentContent.encode(buffer, true, true);
   }

   public EnterpriseConfigRecordImpl(int uid) {
      if (uid == 0) {
         this._uid = UIDGenerator.getUID();
      } else {
         this._uid = uid;
      }
   }
}
