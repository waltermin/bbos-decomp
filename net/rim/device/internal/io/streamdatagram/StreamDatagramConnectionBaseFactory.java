package net.rim.device.internal.io.streamdatagram;

public class StreamDatagramConnectionBaseFactory {
   private String _className;
   private static final int STREAM_DEBUG_LEVEL = 1000000;

   public StreamDatagramConnectionBaseFactory() {
   }

   public StreamDatagramConnectionBaseFactory(StreamDatagramTransportBase streamDatagramTransport) {
      String protocolName = streamDatagramTransport.getClass().getName();
      int index = protocolName.lastIndexOf(46);
      if (index == -1) {
         throw new Object("Unable to find underlying Protocol class");
      }

      String packageName = protocolName.substring(0, index + 1);
      index = packageName.indexOf("net.rim.device.cldc.io");
      if (index == -1) {
         throw new Object("Unable to find underlying Protocol class");
      }

      this._className = ((StringBuffer)(new Object())).append(packageName).append("Protocol").toString();
   }

   public StreamDatagramConnectionBase createInstance() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/io/streamdatagram/StreamDatagramConnectionBaseFactory._className Ljava/lang/String;
      // 04: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 07: astore 1
      // 08: aload 1
      // 09: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0c: checkcast net/rim/device/internal/io/streamdatagram/StreamDatagramConnectionBase
      // 0f: astore 2
      // 10: aload 2
      // 11: areturn
      // 12: astore 1
      // 13: new java/lang/Object
      // 16: dup
      // 17: aload 1
      // 18: invokevirtual java/lang/InstantiationException.toString ()Ljava/lang/String;
      // 1b: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 1e: athrow
      // 1f: astore 1
      // 20: new java/lang/Object
      // 23: dup
      // 24: aload 1
      // 25: invokevirtual java/lang/IllegalAccessException.toString ()Ljava/lang/String;
      // 28: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 2b: athrow
      // 2c: astore 1
      // 2d: new java/lang/Object
      // 30: dup
      // 31: aload 1
      // 32: invokevirtual java/lang/ClassCastException.toString ()Ljava/lang/String;
      // 35: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 38: athrow
      // 39: astore 1
      // 3a: new java/lang/Object
      // 3d: dup
      // 3e: ldc_w "Requested protocol does not exist: x.toString()"
      // 41: invokespecial javax/microedition/io/ConnectionNotFoundException.<init> (Ljava/lang/String;)V
      // 44: athrow
      // try (0 -> 9): 10 null
      // try (0 -> 9): 17 null
      // try (0 -> 9): 24 null
      // try (0 -> 9): 31 null
   }
}
