package net.rim.device.apps.internal.browser.stack;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.vm.Memory;
import net.rim.vm.Persistable;

final class CacheResult$EncryptedPipe implements Persistable {
   private Object[] _data;

   public CacheResult$EncryptedPipe(byte[][][] data) {
      if (data != null) {
         int dataLength = data.length;
         this._data = new Object[dataLength];

         for (int i = 0; i < dataLength; i++) {
            this._data[i] = PersistentContent.encode((byte[])data[i], true, true);
            Memory.createGroup(this._data[i]);
         }

         Memory.createGroup(this._data);
      }
   }

   public final Pipe getDecryptedPipe() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: Constructor net/rim/device/internal/browser/util/Pipe.<init>([[BZ)V not found
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExprUtil.getSyntheticParametersMask(ExprUtil.java:49)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:982)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:462)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExitExprent.toJava(ExitExprent.java:85)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:238)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1351)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/browser/stack/CacheResult$EncryptedPipe._data [Ljava/lang/Object;
      // 04: ifnull 0f
      // 07: aload 0
      // 08: getfield net/rim/device/apps/internal/browser/stack/CacheResult$EncryptedPipe._data [Ljava/lang/Object;
      // 0b: arraylength
      // 0c: ifne 17
      // 0f: new java/lang/Object
      // 12: dup
      // 13: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ()V
      // 16: areturn
      // 17: aload 0
      // 18: getfield net/rim/device/apps/internal/browser/stack/CacheResult$EncryptedPipe._data [Ljava/lang/Object;
      // 1b: arraylength
      // 1c: istore 1
      // 1d: iload 1
      // 1e: bipush 1
      // 1f: if_icmpne 38
      // 22: aload 0
      // 23: getfield net/rim/device/apps/internal/browser/stack/CacheResult$EncryptedPipe._data [Ljava/lang/Object;
      // 26: bipush 0
      // 27: aaload
      // 28: invokestatic net/rim/device/api/system/PersistentContent.decodeByteArray (Ljava/lang/Object;)[B
      // 2b: astore 2
      // 2c: new java/lang/Object
      // 2f: dup
      // 30: aload 2
      // 31: aload 2
      // 32: arraylength
      // 33: bipush 0
      // 34: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([BIZ)V
      // 37: areturn
      // 38: iload 1
      // 39: multianewarray 121 1
      // 3d: astore 2
      // 3e: bipush 0
      // 3f: istore 3
      // 40: iload 3
      // 41: iload 1
      // 42: if_icmpge 57
      // 45: aload 2
      // 46: iload 3
      // 47: aload 0
      // 48: getfield net/rim/device/apps/internal/browser/stack/CacheResult$EncryptedPipe._data [Ljava/lang/Object;
      // 4b: iload 3
      // 4c: aaload
      // 4d: invokestatic net/rim/device/api/system/PersistentContent.decodeByteArray (Ljava/lang/Object;)[B
      // 50: aastore
      // 51: iinc 3 1
      // 54: goto 40
      // 57: new java/lang/Object
      // 5a: dup
      // 5b: aload 2
      // 5c: bipush 0
      // 5d: invokespecial net/rim/device/internal/browser/util/Pipe.<init> ([[BZ)V
      // 60: areturn
   }

   public final int getLength() {
      if (this._data != null && this._data.length != 0) {
         int dataLength = this._data.length;
         if (dataLength == 1) {
            return PersistentContent.getLength(this._data[0]);
         }

         int totalLength = 0;

         for (int i = 0; i < dataLength; i++) {
            totalLength += PersistentContent.getLength(this._data[i]);
         }

         return totalLength;
      } else {
         return 0;
      }
   }
}
