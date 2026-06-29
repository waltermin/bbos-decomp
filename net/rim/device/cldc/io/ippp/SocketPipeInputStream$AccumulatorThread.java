package net.rim.device.cldc.io.ippp;

class SocketPipeInputStream$AccumulatorThread extends Thread {
   private final SocketPipeInputStream this$0;

   public SocketPipeInputStream$AccumulatorThread(SocketPipeInputStream _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
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
      // 000: aload 0
      // 001: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 004: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._socketIn Lnet/rim/device/cldc/io/ippp/SocketInputStream;
      // 007: invokevirtual net/rim/device/cldc/io/ippp/SocketInputStream.getMoreInput ()Lnet/rim/device/api/util/DataBuffer;
      // 00a: astore 1
      // 00b: aload 1
      // 00c: ifnull 000
      // 00f: aload 1
      // 010: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 013: ifle 000
      // 016: aload 1
      // 017: invokevirtual net/rim/device/api/util/DataBuffer.getArray ()[B
      // 01a: astore 2
      // 01b: aload 1
      // 01c: invokevirtual net/rim/device/api/util/DataBuffer.getArrayPosition ()I
      // 01f: istore 3
      // 020: aload 1
      // 021: invokevirtual net/rim/device/api/util/DataBuffer.available ()I
      // 024: istore 4
      // 026: aload 0
      // 027: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 02a: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 02d: ifnull 045
      // 030: aload 0
      // 031: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 034: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 037: aload 2
      // 038: iload 3
      // 039: iload 4
      // 03b: invokevirtual net/rim/device/internal/compress/YKDecode.yk_decode ([BII)[B
      // 03e: astore 2
      // 03f: bipush 0
      // 040: istore 3
      // 041: aload 2
      // 042: arraylength
      // 043: istore 4
      // 045: aload 0
      // 046: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 049: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 04c: aload 2
      // 04d: iload 3
      // 04e: iload 4
      // 050: aload 0
      // 051: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 054: invokestatic net/rim/device/cldc/io/ippp/SocketPipeInputStream.access$208 (Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;)I
      // 057: invokevirtual net/rim/device/internal/browser/util/Pipe.write ([BIII)V
      // 05a: aload 0
      // 05b: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 05e: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._syncObject Ljava/lang/Object;
      // 061: dup
      // 062: astore 5
      // 064: monitorenter
      // 065: aload 0
      // 066: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 069: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._stats Lnet/rim/device/cldc/io/utility/SessionStats;
      // 06c: ifnull 07b
      // 06f: aload 0
      // 070: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 073: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._stats Lnet/rim/device/cldc/io/utility/SessionStats;
      // 076: iload 4
      // 078: invokevirtual net/rim/device/cldc/io/utility/SessionStats.addToReceived (I)V
      // 07b: aload 5
      // 07d: monitorexit
      // 07e: goto 000
      // 081: astore 6
      // 083: aload 5
      // 085: monitorexit
      // 086: aload 6
      // 088: athrow
      // 089: astore 1
      // 08a: aload 0
      // 08b: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 08e: aload 1
      // 08f: putfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._socketBaseException Ljava/io/IOException;
      // 092: aload 0
      // 093: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 096: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 099: ifnull 0ae
      // 09c: aload 0
      // 09d: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0a0: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 0a3: invokevirtual net/rim/device/internal/compress/YKDecode.yk_uninit ()V
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0aa: aconst_null
      // 0ab: putfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 0ae: aload 0
      // 0af: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0b2: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 0b5: invokevirtual net/rim/device/internal/browser/util/Pipe.closeWrite ()V
      // 0b8: return
      // 0b9: astore 1
      // 0ba: aload 0
      // 0bb: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0be: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 0c1: ifnull 0d6
      // 0c4: aload 0
      // 0c5: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0c8: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 0cb: invokevirtual net/rim/device/internal/compress/YKDecode.yk_uninit ()V
      // 0ce: aload 0
      // 0cf: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0d2: aconst_null
      // 0d3: putfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 0d6: aload 0
      // 0d7: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0da: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 0dd: invokevirtual net/rim/device/internal/browser/util/Pipe.closeWrite ()V
      // 0e0: return
      // 0e1: astore 7
      // 0e3: aload 0
      // 0e4: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0e7: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 0ea: ifnull 0ff
      // 0ed: aload 0
      // 0ee: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0f1: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 0f4: invokevirtual net/rim/device/internal/compress/YKDecode.yk_uninit ()V
      // 0f7: aload 0
      // 0f8: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 0fb: aconst_null
      // 0fc: putfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._ykDecoder Lnet/rim/device/internal/compress/YKDecode;
      // 0ff: aload 0
      // 100: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream$AccumulatorThread.this$0 Lnet/rim/device/cldc/io/ippp/SocketPipeInputStream;
      // 103: getfield net/rim/device/cldc/io/ippp/SocketPipeInputStream._pipe Lnet/rim/device/internal/browser/util/Pipe;
      // 106: invokevirtual net/rim/device/internal/browser/util/Pipe.closeWrite ()V
      // 109: aload 7
      // 10b: athrow
      // try (52 -> 63): 64 null
      // try (64 -> 67): 64 null
      // try (0 -> 69): 69 net/rim/device/cldc/io/ippp/SocketBaseIOException
      // try (0 -> 69): 91 null
      // try (0 -> 74): 109 null
      // try (91 -> 92): 109 null
      // try (109 -> 110): 109 null
   }
}
