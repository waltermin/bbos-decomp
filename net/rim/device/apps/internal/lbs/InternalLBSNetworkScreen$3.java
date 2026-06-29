package net.rim.device.apps.internal.lbs;

final class InternalLBSNetworkScreen$3 implements Runnable {
   private final InternalLBSNetworkScreen this$0;

   InternalLBSNetworkScreen$3(InternalLBSNetworkScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: invokestatic java/lang/System.currentTimeMillis ()J
      // 003: lstore 1
      // 004: aconst_null
      // 005: astore 3
      // 006: aconst_null
      // 007: astore 4
      // 009: aconst_null
      // 00a: astore 5
      // 00c: aload 0
      // 00d: getfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen$3.this$0 Lnet/rim/device/apps/internal/lbs/InternalLBSNetworkScreen;
      // 010: getfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen._LBSServer Lnet/rim/device/api/ui/component/BasicEditField;
      // 013: invokevirtual net/rim/device/api/ui/component/BasicEditField.getText ()Ljava/lang/String;
      // 016: astore 6
      // 018: invokestatic net/rim/device/api/system/DeviceInfo.isSimulator ()Z
      // 01b: ifeq 038
      // 01e: new java/lang/Object
      // 021: dup
      // 022: invokespecial java/lang/StringBuffer.<init> ()V
      // 025: aload 6
      // 027: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 02a: ldc_w ";deviceside=true"
      // 02d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 030: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 033: astore 6
      // 035: goto 057
      // 038: invokestatic net/rim/device/api/system/RadioInfo.getNetworkType ()I
      // 03b: bipush 5
      // 03d: if_icmpne 057
      // 040: new java/lang/Object
      // 043: dup
      // 044: invokespecial java/lang/StringBuffer.<init> ()V
      // 047: aload 6
      // 049: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 04c: ldc_w ";deviceside=false"
      // 04f: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 052: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 055: astore 6
      // 057: aload 6
      // 059: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 05c: checkcast java/lang/Object
      // 05f: astore 3
      // 060: goto 06e
      // 063: astore 7
      // 065: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 068: ldc_w "please help us figure out why this happens"
      // 06b: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 06e: aload 3
      // 06f: ifnull 087
      // 072: aload 3
      // 073: ldc_w "POST"
      // 076: invokeinterface javax/microedition/io/HttpConnection.setRequestMethod (Ljava/lang/String;)V 2
      // 07b: aload 3
      // 07c: ldc_w "Content-Length"
      // 07f: ldc_w "1"
      // 082: invokeinterface javax/microedition/io/HttpConnection.setRequestProperty (Ljava/lang/String;Ljava/lang/String;)V 3
      // 087: aload 3
      // 088: invokeinterface javax/microedition/io/OutputConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 08d: astore 4
      // 08f: aload 4
      // 091: bipush 1
      // 092: newarray 8
      // 094: dup
      // 095: bipush 0
      // 096: bipush 0
      // 097: bastore
      // 098: invokevirtual java/io/OutputStream.write ([B)V
      // 09b: aload 4
      // 09d: invokevirtual java/io/OutputStream.close ()V
      // 0a0: aload 3
      // 0a1: invokeinterface javax/microedition/io/InputConnection.openDataInputStream ()Ljava/io/DataInputStream; 1
      // 0a6: astore 5
      // 0a8: aload 5
      // 0aa: invokevirtual java/io/InputStream.read ()I
      // 0ad: pop
      // 0ae: aload 4
      // 0b0: ifnull 0bd
      // 0b3: aload 4
      // 0b5: invokevirtual java/io/OutputStream.close ()V
      // 0b8: goto 0bd
      // 0bb: astore 10
      // 0bd: aload 5
      // 0bf: ifnull 0cc
      // 0c2: aload 5
      // 0c4: invokevirtual java/io/InputStream.close ()V
      // 0c7: goto 0cc
      // 0ca: astore 10
      // 0cc: aload 3
      // 0cd: ifnonnull 0d3
      // 0d0: goto 1be
      // 0d3: aload 3
      // 0d4: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0d9: goto 1be
      // 0dc: astore 10
      // 0de: goto 1be
      // 0e1: astore 7
      // 0e3: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 0e6: ldc_w "please help us figure out why this happens"
      // 0e9: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 0ec: aload 4
      // 0ee: ifnull 0fb
      // 0f1: aload 4
      // 0f3: invokevirtual java/io/OutputStream.close ()V
      // 0f6: goto 0fb
      // 0f9: astore 10
      // 0fb: aload 5
      // 0fd: ifnull 10a
      // 100: aload 5
      // 102: invokevirtual java/io/InputStream.close ()V
      // 105: goto 10a
      // 108: astore 10
      // 10a: aload 3
      // 10b: ifnonnull 111
      // 10e: goto 1be
      // 111: aload 3
      // 112: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 117: goto 1be
      // 11a: astore 10
      // 11c: goto 1be
      // 11f: astore 7
      // 121: aload 4
      // 123: ifnull 130
      // 126: aload 4
      // 128: invokevirtual java/io/OutputStream.close ()V
      // 12b: goto 130
      // 12e: astore 10
      // 130: aload 5
      // 132: ifnull 13f
      // 135: aload 5
      // 137: invokevirtual java/io/InputStream.close ()V
      // 13a: goto 13f
      // 13d: astore 10
      // 13f: aload 3
      // 140: ifnull 1be
      // 143: aload 3
      // 144: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 149: goto 1be
      // 14c: astore 10
      // 14e: goto 1be
      // 151: astore 7
      // 153: getstatic java/lang/System.out Ljava/io/PrintStream;
      // 156: ldc_w "please help us figure out why this happens"
      // 159: invokevirtual java/io/PrintStream.println (Ljava/lang/String;)V
      // 15c: aload 4
      // 15e: ifnull 16b
      // 161: aload 4
      // 163: invokevirtual java/io/OutputStream.close ()V
      // 166: goto 16b
      // 169: astore 10
      // 16b: aload 5
      // 16d: ifnull 17a
      // 170: aload 5
      // 172: invokevirtual java/io/InputStream.close ()V
      // 175: goto 17a
      // 178: astore 10
      // 17a: aload 3
      // 17b: ifnull 1be
      // 17e: aload 3
      // 17f: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 184: goto 1be
      // 187: astore 10
      // 189: goto 1be
      // 18c: astore 8
      // 18e: aload 4
      // 190: ifnull 19d
      // 193: aload 4
      // 195: invokevirtual java/io/OutputStream.close ()V
      // 198: goto 19d
      // 19b: astore 10
      // 19d: aload 5
      // 19f: ifnull 1ac
      // 1a2: aload 5
      // 1a4: invokevirtual java/io/InputStream.close ()V
      // 1a7: goto 1ac
      // 1aa: astore 10
      // 1ac: aload 3
      // 1ad: ifnull 1bb
      // 1b0: aload 3
      // 1b1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1b6: goto 1bb
      // 1b9: astore 10
      // 1bb: aload 8
      // 1bd: athrow
      // 1be: invokestatic java/lang/System.currentTimeMillis ()J
      // 1c1: lload 1
      // 1c2: lsub
      // 1c3: lstore 7
      // 1c5: aload 0
      // 1c6: getfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen$3.this$0 Lnet/rim/device/apps/internal/lbs/InternalLBSNetworkScreen;
      // 1c9: getfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen._pingAvg J
      // 1cc: bipush 0
      // 1cd: i2l
      // 1ce: lcmp
      // 1cf: ifne 1de
      // 1d2: aload 0
      // 1d3: getfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen$3.this$0 Lnet/rim/device/apps/internal/lbs/InternalLBSNetworkScreen;
      // 1d6: lload 7
      // 1d8: putfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen._pingAvg J
      // 1db: goto 1f1
      // 1de: aload 0
      // 1df: getfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen$3.this$0 Lnet/rim/device/apps/internal/lbs/InternalLBSNetworkScreen;
      // 1e2: aload 0
      // 1e3: getfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen$3.this$0 Lnet/rim/device/apps/internal/lbs/InternalLBSNetworkScreen;
      // 1e6: getfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen._pingAvg J
      // 1e9: lload 7
      // 1eb: ladd
      // 1ec: bipush 1
      // 1ed: lshr
      // 1ee: putfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen._pingAvg J
      // 1f1: aload 0
      // 1f2: getfield net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen$3.this$0 Lnet/rim/device/apps/internal/lbs/InternalLBSNetworkScreen;
      // 1f5: lload 7
      // 1f7: invokespecial net/rim/device/apps/internal/lbs/InternalLBSNetworkScreen.donePing (J)V
      // 1fa: return
      // try (37 -> 41): 42 null
      // try (13 -> 74): 94 null
      // try (13 -> 74): 118 null
      // try (13 -> 74): 138 null
      // try (13 -> 74): 161 null
      // try (94 -> 98): 161 null
      // try (118 -> 119): 161 null
      // try (138 -> 142): 161 null
      // try (161 -> 162): 161 null
      // try (164 -> 166): 167 null
      // try (144 -> 146): 147 null
      // try (121 -> 123): 124 null
      // try (100 -> 102): 103 null
      // try (76 -> 78): 79 null
      // try (170 -> 172): 173 null
      // try (150 -> 152): 153 null
      // try (127 -> 129): 130 null
      // try (106 -> 108): 109 null
      // try (82 -> 84): 85 null
      // try (176 -> 178): 179 null
      // try (156 -> 158): 159 null
      // try (133 -> 135): 136 null
      // try (113 -> 115): 116 null
      // try (89 -> 91): 92 null
   }
}
