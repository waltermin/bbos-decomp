package net.rim.device.cldc.io.gme;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.TLEFieldController;

final class ITPolicyParser implements TLEFieldController {
   private DataBuffer _previousKey;
   private DataBuffer _currentKey;
   private DataBuffer _buffer;
   private static final int SET_PEER_TO_PEER_KEY_MAJOR_VERSION = 16;
   private static final int SET_PEER_TO_PEER_KEY_MINOR_VERSION = 0;
   private static final int SET_PEER_TO_PEER_KEY_VERSION = 16;

   public ITPolicyParser(byte[] data, int offset, int length) {
      this._buffer = new DataBuffer(data, offset, length, true);
   }

   public final boolean parse() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/cldc/io/gme/ITPolicyParser._buffer Lnet/rim/device/api/util/DataBuffer;
      // 04: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 07: sipush 240
      // 0a: iand
      // 0b: bipush 16
      // 0d: if_icmpne 6a
      // 10: aload 0
      // 11: getfield net/rim/device/cldc/io/gme/ITPolicyParser._buffer Lnet/rim/device/api/util/DataBuffer;
      // 14: aload 0
      // 15: aload 0
      // 16: getfield net/rim/device/cldc/io/gme/ITPolicyParser._buffer Lnet/rim/device/api/util/DataBuffer;
      // 19: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 1c: invokestatic net/rim/device/api/util/TLEUtilities.parseField (Lnet/rim/device/api/util/DataBuffer;Lnet/rim/device/api/util/TLEFieldController;I)V
      // 1f: aload 0
      // 20: getfield net/rim/device/cldc/io/gme/ITPolicyParser._previousKey Lnet/rim/device/api/util/DataBuffer;
      // 23: ifnull 43
      // 26: aload 0
      // 27: getfield net/rim/device/cldc/io/gme/ITPolicyParser._currentKey Lnet/rim/device/api/util/DataBuffer;
      // 2a: ifnull 43
      // 2d: getstatic net/rim/device/internal/crypto/CryptoBlock.CURRENT_BES_SCRAMBLE_KEY Ljava/lang/String;
      // 30: aload 0
      // 31: getfield net/rim/device/cldc/io/gme/ITPolicyParser._currentKey Lnet/rim/device/api/util/DataBuffer;
      // 34: invokestatic net/rim/device/internal/crypto/CryptoBlock.addSymmetricKey (Ljava/lang/String;Ljava/io/DataInput;)V
      // 37: getstatic net/rim/device/internal/crypto/CryptoBlock.PREVIOUS_BES_SCRAMBLE_KEY Ljava/lang/String;
      // 3a: aload 0
      // 3b: getfield net/rim/device/cldc/io/gme/ITPolicyParser._previousKey Lnet/rim/device/api/util/DataBuffer;
      // 3e: invokestatic net/rim/device/internal/crypto/CryptoBlock.addSymmetricKey (Ljava/lang/String;Ljava/io/DataInput;)V
      // 41: bipush 1
      // 42: ireturn
      // 43: aload 0
      // 44: getfield net/rim/device/cldc/io/gme/ITPolicyParser._previousKey Lnet/rim/device/api/util/DataBuffer;
      // 47: ifnonnull 6a
      // 4a: aload 0
      // 4b: getfield net/rim/device/cldc/io/gme/ITPolicyParser._currentKey Lnet/rim/device/api/util/DataBuffer;
      // 4e: ifnonnull 6a
      // 51: bipush 5
      // 53: istore 1
      // 54: getstatic net/rim/device/internal/crypto/CryptoBlock.CURRENT_BES_SCRAMBLE_KEY Ljava/lang/String;
      // 57: iload 1
      // 58: invokestatic net/rim/device/internal/crypto/CryptoBlock.removeSymmetricKey (Ljava/lang/String;B)Z
      // 5b: pop
      // 5c: getstatic net/rim/device/internal/crypto/CryptoBlock.PREVIOUS_BES_SCRAMBLE_KEY Ljava/lang/String;
      // 5f: iload 1
      // 60: invokestatic net/rim/device/internal/crypto/CryptoBlock.removeSymmetricKey (Ljava/lang/String;B)Z
      // 63: pop
      // 64: bipush 0
      // 65: ireturn
      // 66: astore 1
      // 67: bipush 0
      // 68: ireturn
      // 69: astore 1
      // 6a: bipush 0
      // 6b: ireturn
      // try (0 -> 29): 48 null
      // try (30 -> 46): 48 null
      // try (0 -> 29): 51 null
      // try (30 -> 46): 51 null
   }

   @Override
   public final boolean processField(int type, int length, DataBuffer db) {
      switch (type) {
         case 0:
            return false;
         case 1:
         default:
            this._previousKey = length == 0 ? null : new DataBuffer(db, length);
            return true;
         case 2:
            this._currentKey = length == 0 ? null : new DataBuffer(db, length);
            return true;
      }
   }

   @Override
   public final void dumpField(int type, DataBuffer db) {
   }
}
