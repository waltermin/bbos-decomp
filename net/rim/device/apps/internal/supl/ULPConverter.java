package net.rim.device.apps.internal.supl;

import java.io.DataInput;
import net.rim.device.apps.api.utility.serialization.BaseConverter;
import net.rim.device.apps.api.utility.serialization.SerializationException;

final class ULPConverter extends BaseConverter {
   private static final byte ASCII_LF = 10;
   private static final byte ASCII_CR = 13;
   private static final int MAX_PDU_LEN = 1024;

   @Override
   public final boolean canConvert(Object parameters) {
      return true;
   }

   @Override
   public final Object convert(DataInput param1, Object param2) throws SerializationException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: sipush 1024
      // 03: newarray 8
      // 05: astore 3
      // 06: bipush 0
      // 07: istore 4
      // 09: bipush 0
      // 0a: istore 5
      // 0c: bipush 0
      // 0d: istore 6
      // 0f: bipush 0
      // 10: istore 7
      // 12: aload 2
      // 13: instanceof net/rim/device/api/io/http/HttpHeaders
      // 16: ifeq 85
      // 19: aload 1
      // 1a: instanceof net/rim/device/api/io/http/PushInputStream
      // 1d: ifeq 85
      // 20: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 23: ldc_w "Parsing PDU"
      // 26: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 29: aload 1
      // 2a: invokeinterface java/io/DataInput.readByte ()B 1
      // 2f: istore 4
      // 31: iload 4
      // 33: ifle 7c
      // 36: iload 4
      // 38: bipush 13
      // 3a: if_icmpeq 7c
      // 3d: iload 4
      // 3f: bipush 10
      // 41: if_icmpne 47
      // 44: goto 7c
      // 47: iload 6
      // 49: bipush 2
      // 4b: irem
      // 4c: istore 7
      // 4e: aload 3
      // 4f: iload 5
      // 51: dup2
      // 52: baload
      // 53: iload 4
      // 55: i2c
      // 56: invokestatic java/lang/Character.toUpperCase (C)C
      // 59: bipush 16
      // 5b: invokestatic java/lang/Character.digit (CI)I
      // 5e: bipush 4
      // 60: iload 7
      // 62: ifne 69
      // 65: bipush 1
      // 66: goto 6a
      // 69: bipush 0
      // 6a: imul
      // 6b: ishl
      // 6c: ior
      // 6d: i2b
      // 6e: bastore
      // 6f: iload 5
      // 71: iload 7
      // 73: iadd
      // 74: istore 5
      // 76: iinc 6 1
      // 79: goto 29
      // 7c: aload 3
      // 7d: areturn
      // 7e: astore 8
      // 80: goto 85
      // 83: astore 8
      // 85: new net/rim/device/apps/api/utility/serialization/SerializationException
      // 88: dup
      // 89: invokespecial net/rim/device/apps/api/utility/serialization/SerializationException.<init> ()V
      // 8c: athrow
      // try (17 -> 63): 64 null
      // try (17 -> 63): 66 null
   }
}
