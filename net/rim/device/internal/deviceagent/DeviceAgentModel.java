package net.rim.device.internal.deviceagent;

import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Persistable;

public final class DeviceAgentModel implements Persistable, SyncObject {
   private int _uid;
   private DataBuffer _data = (DataBuffer)(new Object(false));

   public final byte getTableId() {
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
      // 09: invokevirtual net/rim/device/internal/deviceagent/DeviceAgentModel.getData ()Lnet/rim/device/api/util/DataBuffer;
      // 0c: astore 2
      // 0d: aload 2
      // 0e: invokevirtual net/rim/device/api/util/DataBuffer.getPosition ()I
      // 11: istore 3
      // 12: aload 2
      // 13: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 16: aload 2
      // 17: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 1a: ifle 43
      // 1d: aload 2
      // 1e: invokestatic net/rim/device/api/synchronization/ConverterUtilities.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 21: pop
      // 22: aload 2
      // 23: invokestatic net/rim/device/api/synchronization/ConverterUtilities.readByteArray (Lnet/rim/device/api/util/DataBuffer;)[B
      // 26: astore 1
      // 27: aload 2
      // 28: iload 3
      // 29: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 2c: goto 43
      // 2f: astore 4
      // 31: aload 2
      // 32: iload 3
      // 33: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 36: goto 43
      // 39: astore 5
      // 3b: aload 2
      // 3c: iload 3
      // 3d: invokevirtual net/rim/device/api/util/DataBuffer.setPosition (I)V
      // 40: aload 5
      // 42: athrow
      // 43: aload 1
      // 44: bipush 0
      // 45: baload
      // 46: ireturn
      // try (18 -> 24): 28 null
      // try (18 -> 24): 33 null
      // try (28 -> 29): 33 null
      // try (33 -> 34): 33 null
   }

   public final DataBuffer getData() {
      return this._data;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public DeviceAgentModel(int uid, DataBuffer buffer) {
      this(uid);
      byte[] temp = new byte[buffer.getLength()];

      try {
         int pos = buffer.getPosition();
         buffer.rewind();
         buffer.readFully(temp);
         buffer.setPosition(pos);
         this._data.write(temp);
      } finally {
         return;
      }
   }

   public DeviceAgentModel(int uid, byte[] buffer) {
      this(uid);
      this._data.write(buffer);
   }

   public DeviceAgentModel(int uid) {
      this._uid = uid;
   }
}
