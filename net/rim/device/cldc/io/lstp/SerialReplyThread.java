package net.rim.device.cldc.io.lstp;

import net.rim.device.api.util.CyclicQueue;
import net.rim.device.cldc.io.daemon.ProtocolDaemon;

final class SerialReplyThread extends Thread {
   private NativeLayer _nativeLayer;
   private CyclicQueue _packets;

   public SerialReplyThread(NativeLayer nativeLayer) {
      this._nativeLayer = nativeLayer;
      this._packets = (CyclicQueue)(new Object());
      ProtocolDaemon.getInstance().startThread(this);
   }

   public final void addRequest(byte[] buffer) {
      synchronized (this._packets) {
         this._packets.enqueue(buffer);
         this._packets.notify();
      }
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
      // 00: aload 0
      // 01: getfield net/rim/device/cldc/io/lstp/SerialReplyThread._packets Lnet/rim/device/api/util/CyclicQueue;
      // 04: dup
      // 05: astore 2
      // 06: monitorenter
      // 07: aload 0
      // 08: getfield net/rim/device/cldc/io/lstp/SerialReplyThread._packets Lnet/rim/device/api/util/CyclicQueue;
      // 0b: invokevirtual net/rim/device/api/util/CyclicQueue.isEmpty ()Z
      // 0e: ifeq 18
      // 11: aload 0
      // 12: getfield net/rim/device/cldc/io/lstp/SerialReplyThread._packets Lnet/rim/device/api/util/CyclicQueue;
      // 15: invokevirtual java/lang/Object.wait ()V
      // 18: aload 0
      // 19: getfield net/rim/device/cldc/io/lstp/SerialReplyThread._packets Lnet/rim/device/api/util/CyclicQueue;
      // 1c: invokevirtual net/rim/device/api/util/CyclicQueue.dequeue ()Ljava/lang/Object;
      // 1f: checkcast [B
      // 22: astore 1
      // 23: aload 2
      // 24: monitorexit
      // 25: goto 2d
      // 28: astore 3
      // 29: aload 2
      // 2a: monitorexit
      // 2b: aload 3
      // 2c: athrow
      // 2d: aload 0
      // 2e: getfield net/rim/device/cldc/io/lstp/SerialReplyThread._nativeLayer Lnet/rim/device/cldc/io/lstp/NativeLayer;
      // 31: aload 1
      // 32: bipush 0
      // 33: aload 1
      // 34: arraylength
      // 35: invokevirtual net/rim/device/cldc/io/lstp/NativeLayer.sendPacket ([BII)V
      // 38: aconst_null
      // 39: astore 1
      // 3a: goto 00
      // 3d: astore 2
      // 3e: aconst_null
      // 3f: astore 1
      // 40: goto 00
      // 43: astore 2
      // 44: aconst_null
      // 45: astore 1
      // 46: goto 00
      // 49: astore 4
      // 4b: aconst_null
      // 4c: astore 1
      // 4d: aload 4
      // 4f: athrow
      // try (5 -> 19): 20 null
      // try (20 -> 23): 20 null
      // try (0 -> 32): 35 null
      // try (0 -> 32): 39 null
      // try (0 -> 32): 43 null
      // try (35 -> 36): 43 null
      // try (39 -> 40): 43 null
      // try (43 -> 44): 43 null
   }
}
