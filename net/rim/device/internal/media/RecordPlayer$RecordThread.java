package net.rim.device.internal.media;

class RecordPlayer$RecordThread extends Thread {
   boolean done;
   private boolean _resuming;
   private final RecordPlayer this$0;

   public RecordPlayer$RecordThread(RecordPlayer _1) {
      this(_1, false);
   }

   public RecordPlayer$RecordThread(RecordPlayer _1, boolean resuming) {
      this.this$0 = _1;
      this._resuming = resuming;
   }

   @Override
   public void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/media/RecordPlayer$RecordThread._resuming Z
      // 04: ifeq 29
      // 07: aload 0
      // 08: getfield net/rim/device/internal/media/RecordPlayer$RecordThread.this$0 Lnet/rim/device/internal/media/RecordPlayer;
      // 0b: getfield net/rim/device/internal/media/RecordPlayer._codec I
      // 0e: bipush 7
      // 10: if_icmpne 42
      // 13: aload 0
      // 14: getfield net/rim/device/internal/media/RecordPlayer$RecordThread.this$0 Lnet/rim/device/internal/media/RecordPlayer;
      // 17: getfield net/rim/device/internal/media/RecordPlayer._ringBuffer Lnet/rim/device/internal/util/RingBuffer;
      // 1a: aload 0
      // 1b: getfield net/rim/device/internal/media/RecordPlayer$RecordThread.this$0 Lnet/rim/device/internal/media/RecordPlayer;
      // 1e: getfield net/rim/device/internal/media/RecordPlayer._stream Ljava/io/OutputStream;
      // 21: bipush 6
      // 23: invokevirtual net/rim/device/internal/util/RingBuffer.readEntirely (Ljava/io/OutputStream;I)V
      // 26: goto 42
      // 29: aload 0
      // 2a: getfield net/rim/device/internal/media/RecordPlayer$RecordThread.this$0 Lnet/rim/device/internal/media/RecordPlayer;
      // 2d: getfield net/rim/device/internal/media/RecordPlayer._ringBuffer Lnet/rim/device/internal/util/RingBuffer;
      // 30: aload 0
      // 31: getfield net/rim/device/internal/media/RecordPlayer$RecordThread.this$0 Lnet/rim/device/internal/media/RecordPlayer;
      // 34: getfield net/rim/device/internal/media/RecordPlayer._stream Ljava/io/OutputStream;
      // 37: invokevirtual net/rim/device/internal/util/RingBuffer.readEntirely (Ljava/io/OutputStream;)V
      // 3a: goto 42
      // 3d: astore 1
      // 3e: goto 42
      // 41: astore 1
      // 42: aload 0
      // 43: getfield net/rim/device/internal/media/RecordPlayer$RecordThread.this$0 Lnet/rim/device/internal/media/RecordPlayer;
      // 46: getfield net/rim/device/internal/media/RecordPlayer._lock Ljava/lang/Object;
      // 49: dup
      // 4a: astore 1
      // 4b: monitorenter
      // 4c: aload 0
      // 4d: getfield net/rim/device/internal/media/RecordPlayer$RecordThread.this$0 Lnet/rim/device/internal/media/RecordPlayer;
      // 50: getfield net/rim/device/internal/media/RecordPlayer._recordState I
      // 53: bipush 3
      // 55: if_icmpne 5f
      // 58: aload 0
      // 59: getfield net/rim/device/internal/media/RecordPlayer$RecordThread.this$0 Lnet/rim/device/internal/media/RecordPlayer;
      // 5c: invokevirtual net/rim/device/internal/media/RecordPlayer.commit ()V
      // 5f: aload 1
      // 60: monitorexit
      // 61: goto 69
      // 64: astore 2
      // 65: aload 1
      // 66: monitorexit
      // 67: aload 2
      // 68: athrow
      // 69: aload 0
      // 6a: bipush 1
      // 6b: putfield net/rim/device/internal/media/RecordPlayer$RecordThread.done Z
      // 6e: return
      // 6f: astore 1
      // 70: return
      // try (0 -> 24): 25 null
      // try (0 -> 24): 27 null
      // try (34 -> 44): 45 null
      // try (45 -> 48): 45 null
      // try (0 -> 53): 54 null
   }
}
