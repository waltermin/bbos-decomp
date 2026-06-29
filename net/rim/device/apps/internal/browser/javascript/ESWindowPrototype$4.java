package net.rim.device.apps.internal.browser.javascript;

class ESWindowPrototype$4 extends JavaScriptHostFunction {
   private final ESWindowPrototype this$0;

   ESWindowPrototype$4(ESWindowPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokevirtual net/rim/ecmascript/runtime/HostFunction.getNumParms ()I
      // 04: istore 1
      // 05: iload 1
      // 06: bipush 1
      // 07: if_icmpne 42
      // 0a: new java/lang/Object
      // 0d: dup
      // 0e: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 11: astore 2
      // 12: new java/lang/Object
      // 15: dup
      // 16: aload 2
      // 17: invokespecial net/rim/device/api/io/Base64OutputStream.<init> (Ljava/io/OutputStream;)V
      // 1a: astore 3
      // 1b: aload 3
      // 1c: aload 0
      // 1d: bipush 0
      // 1e: invokevirtual net/rim/ecmascript/runtime/HostFunction.getParm (I)J
      // 21: invokestatic net/rim/ecmascript/runtime/Convert.toString (J)Ljava/lang/String;
      // 24: invokevirtual java/lang/String.getBytes ()[B
      // 27: invokevirtual net/rim/device/api/io/Base64OutputStream.write ([B)V
      // 2a: aload 3
      // 2b: invokevirtual net/rim/device/api/io/Base64OutputStream.close ()V
      // 2e: new java/lang/Object
      // 31: dup
      // 32: aload 2
      // 33: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 36: invokespecial java/lang/String.<init> ([B)V
      // 39: invokestatic net/rim/ecmascript/runtime/Value.makeStringValue (Ljava/lang/String;)J
      // 3c: lreturn
      // 3d: astore 2
      // 3e: goto 42
      // 41: astore 2
      // 42: getstatic net/rim/ecmascript/runtime/Value.NULL J
      // 45: lreturn
      // try (6 -> 30): 31 null
      // try (6 -> 30): 33 null
   }
}
