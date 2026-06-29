package net.rim.device.apps.internal.browser.javascript;

class ESWindowPrototype$2 extends JavaScriptHostFunction {
   private final ESWindowPrototype this$0;

   ESWindowPrototype$2(ESWindowPrototype _1, String x0, String x1, int x2) {
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
      // 07: if_icmpne 59
      // 0a: new java/lang/Object
      // 0d: dup
      // 0e: invokespecial java/io/ByteArrayOutputStream.<init> ()V
      // 11: astore 2
      // 12: new java/lang/Object
      // 15: dup
      // 16: new java/lang/Object
      // 19: dup
      // 1a: aload 0
      // 1b: bipush 0
      // 1c: invokevirtual net/rim/ecmascript/runtime/HostFunction.getParm (I)J
      // 1f: invokestatic net/rim/ecmascript/runtime/Convert.toString (J)Ljava/lang/String;
      // 22: invokevirtual java/lang/String.getBytes ()[B
      // 25: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 28: invokespecial net/rim/device/api/io/Base64InputStream.<init> (Ljava/io/InputStream;)V
      // 2b: astore 3
      // 2c: aload 3
      // 2d: invokevirtual net/rim/device/api/io/Base64InputStream.read ()I
      // 30: dup
      // 31: istore 4
      // 33: bipush -1
      // 35: if_icmpeq 41
      // 38: aload 2
      // 39: iload 4
      // 3b: invokevirtual java/io/ByteArrayOutputStream.write (I)V
      // 3e: goto 2c
      // 41: aload 2
      // 42: invokevirtual java/io/ByteArrayOutputStream.close ()V
      // 45: new java/lang/Object
      // 48: dup
      // 49: aload 2
      // 4a: invokevirtual java/io/ByteArrayOutputStream.toByteArray ()[B
      // 4d: invokespecial java/lang/String.<init> ([B)V
      // 50: invokestatic net/rim/ecmascript/runtime/Value.makeStringValue (Ljava/lang/String;)J
      // 53: lreturn
      // 54: astore 2
      // 55: goto 59
      // 58: astore 2
      // 59: getstatic net/rim/ecmascript/runtime/Value.NULL J
      // 5c: lreturn
      // try (6 -> 40): 41 null
      // try (6 -> 40): 43 null
   }
}
