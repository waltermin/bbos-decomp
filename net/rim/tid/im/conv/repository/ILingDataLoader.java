package net.rim.tid.im.conv.repository;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.resources.Resource;
import net.rim.device.resources.Resource$Internal;
import net.rim.tid.itie.LingDataRegistry;
import net.rim.tid.itie.LinguisticData;

public class ILingDataLoader {
   protected String[][][] _resNames;
   protected Locale[] _locales;
   protected int[] _types;
   protected int[] _versions;

   protected void registerData() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: Constructor net/rim/tid/itie/LinguisticData.<init>(Ljava/lang/String;II[[BLjava/lang/String;Ljava/lang/String;)V not found
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExprUtil.getSyntheticParametersMask(ExprUtil.java:49)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:982)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.toJava(NewExprent.java:462)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:238)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.DoStatement.toJava(DoStatement.java:142)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:860)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1351)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/api/system/ApplicationDescriptor.currentApplicationDescriptor ()Lnet/rim/device/api/system/ApplicationDescriptor;
      // 03: invokevirtual net/rim/device/api/system/ApplicationDescriptor.getModuleName ()Ljava/lang/String;
      // 06: astore 1
      // 07: new java/lang/Object
      // 0a: dup
      // 0b: invokespecial java/lang/StringBuffer.<init> ()V
      // 0e: astore 2
      // 0f: bipush 0
      // 10: istore 3
      // 11: iload 3
      // 12: aload 0
      // 13: getfield net/rim/tid/im/conv/repository/ILingDataLoader._resNames [[[Ljava/lang/String;
      // 16: arraylength
      // 17: if_icmpge 71
      // 1a: aload 0
      // 1b: aload 1
      // 1c: aload 0
      // 1d: getfield net/rim/tid/im/conv/repository/ILingDataLoader._resNames [[[Ljava/lang/String;
      // 20: iload 3
      // 21: aaload
      // 22: invokespecial net/rim/tid/im/conv/repository/ILingDataLoader.loadRes (Ljava/lang/String;[Ljava/lang/String;)[[[B
      // 25: astore 4
      // 27: aload 4
      // 29: ifnull 6b
      // 2c: aload 2
      // 2d: bipush 0
      // 2e: invokevirtual java/lang/StringBuffer.setLength (I)V
      // 31: aload 0
      // 32: aload 2
      // 33: aload 1
      // 34: aload 0
      // 35: getfield net/rim/tid/im/conv/repository/ILingDataLoader._resNames [[[Ljava/lang/String;
      // 38: iload 3
      // 39: aaload
      // 3a: aload 0
      // 3b: getfield net/rim/tid/im/conv/repository/ILingDataLoader._locales [Lnet/rim/device/api/i18n/Locale;
      // 3e: iload 3
      // 3f: aaload
      // 40: invokespecial net/rim/tid/im/conv/repository/ILingDataLoader.composeDiagnosticMessage (Ljava/lang/StringBuffer;Ljava/lang/String;[Ljava/lang/String;Lnet/rim/device/api/i18n/Locale;)V
      // 43: aload 0
      // 44: getfield net/rim/tid/im/conv/repository/ILingDataLoader._locales [Lnet/rim/device/api/i18n/Locale;
      // 47: iload 3
      // 48: aaload
      // 49: invokevirtual net/rim/device/api/i18n/Locale.getCode ()I
      // 4c: new java/lang/Object
      // 4f: dup
      // 50: aload 1
      // 51: aload 0
      // 52: getfield net/rim/tid/im/conv/repository/ILingDataLoader._types [I
      // 55: iload 3
      // 56: iaload
      // 57: aload 0
      // 58: getfield net/rim/tid/im/conv/repository/ILingDataLoader._versions [I
      // 5b: iload 3
      // 5c: iaload
      // 5d: aload 4
      // 5f: aload 2
      // 60: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 63: aload 1
      // 64: invokespecial net/rim/tid/itie/LinguisticData.<init> (Ljava/lang/String;II[[BLjava/lang/String;Ljava/lang/String;)V
      // 67: invokestatic net/rim/tid/itie/LingDataRegistry.registerLingData (ILnet/rim/tid/itie/LinguisticData;)I
      // 6a: pop
      // 6b: iinc 3 1
      // 6e: goto 11
      // 71: return
   }

   private void composeDiagnosticMessage(StringBuffer result, String module, String[] chunks, Locale locale) {
      result.append(" Ling data from ");
      result.append(module);
      result.append('[');

      for (int i = 0; i < chunks.length; i++) {
         result.append(chunks[i]);
         result.append(" ");
      }

      result.append(']');
      result.append(" was rejected for locale ");
      result.append(locale.toString());
   }

   private byte[][][] loadRes(String module, String[] names) {
      Resource resource = Resource$Internal.getResourceClass(module);
      if (resource == null) {
         return (byte[][][])((byte[][])null);
      }

      byte[][][] result = new byte[names.length][][];

      for (int i = 0; i < names.length; i++) {
         result[i] = (byte[][])resource.getResource(names[i]);
         if (result[i] == null) {
            System.out.println(((StringBuffer)(new Object("WARNING: can't load "))).append(names[i]).append(" from ").append(module).toString());
            return (byte[][][])((byte[][])null);
         }
      }

      return result;
   }
}
