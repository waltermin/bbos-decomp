package net.rim.device.cldc.io.mdp;

import net.rim.device.api.io.ConnEvent;
import net.rim.device.api.io.DatagramAddressBase;
import net.rim.device.api.io.DatagramBase;

final class MDPRequestThread implements Runnable, ConnEvent {
   private int _maxNumRetries;
   private final int _backoffMax;
   private int _retries;
   private DatagramBase _datagram;
   private WirelessTransportProfile _wtProfile;

   MDPRequestThread(int backoffMax, WirelessTransportProfile wtProfile) {
      this._backoffMax = backoffMax;
      this._wtProfile = wtProfile;
   }

   final boolean createParamRequest(DatagramAddressBase nativeAddress, boolean pAck) {
      this._datagram = MdpMTHUtil.getTransport().createParamRequest(nativeAddress, pAck);
      return this._datagram != null;
   }

   final synchronized void reset(int maxNumRetries) {
      this.setRetries(maxNumRetries);
      this._retries = 0;
      this.notify();
   }

   final synchronized void setRetries(int maxNumRetries) {
      this._maxNumRetries = maxNumRetries;
   }

   final synchronized boolean isRunning() {
      return this._retries < this._maxNumRetries;
   }

   @Override
   public final void run() {
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
      // 00: bipush 0
      // 01: istore 1
      // 02: aload 0
      // 03: bipush 0
      // 04: putfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // 07: aload 0
      // 08: aload 0
      // 09: astore 2
      // 0a: monitorenter
      // 0b: aload 0
      // 0c: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // 0f: aload 0
      // 10: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._maxNumRetries I
      // 13: if_icmplt 19
      // 16: aload 2
      // 17: monitorexit
      // 18: return
      // 19: aload 2
      // 1a: monitorexit
      // 1b: goto 23
      // 1e: astore 3
      // 1f: aload 2
      // 20: monitorexit
      // 21: aload 3
      // 22: athrow
      // 23: invokestatic net/rim/device/cldc/io/mdp/MdpMTHUtil.getTransport ()Lnet/rim/device/cldc/io/mdp/Transport;
      // 26: invokevirtual net/rim/device/cldc/io/mdp/Transport.getTransportLock ()Ljava/lang/Object;
      // 29: dup
      // 2a: astore 2
      // 2b: monitorenter
      // 2c: aload 0
      // 2d: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._wtProfile Lnet/rim/device/cldc/io/mdp/WirelessTransportProfile;
      // 30: ifnull 66
      // 33: aload 0
      // 34: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._wtProfile Lnet/rim/device/cldc/io/mdp/WirelessTransportProfile;
      // 37: getfield net/rim/device/cldc/io/mdp/WirelessTransportProfile._legacyMode Z
      // 3a: ifne 47
      // 3d: aload 0
      // 3e: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._wtProfile Lnet/rim/device/cldc/io/mdp/WirelessTransportProfile;
      // 41: getfield net/rim/device/cldc/io/mdp/WirelessTransportProfile._requestPeriod I
      // 44: ifle 66
      // 47: aload 0
      // 48: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._datagram Lnet/rim/device/api/io/DatagramBase;
      // 4b: ifnull 66
      // 4e: getstatic net/rim/device/cldc/io/mdp/MdpMTHUtil.GUID J
      // 51: ldc_w 1414557810
      // 54: bipush 0
      // 55: invokestatic net/rim/device/api/system/EventLogger.logEvent (JII)Z
      // 58: pop
      // 59: invokestatic net/rim/device/cldc/io/mdp/MdpMTHUtil.getTransport ()Lnet/rim/device/cldc/io/mdp/Transport;
      // 5c: aload 0
      // 5d: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._datagram Lnet/rim/device/api/io/DatagramBase;
      // 60: invokevirtual net/rim/device/cldc/io/mdp/Transport.addSendRequestInternal (Ljavax/microedition/io/Datagram;)V
      // 63: goto 71
      // 66: aload 0
      // 67: aload 0
      // 68: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._maxNumRetries I
      // 6b: putfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // 6e: aload 2
      // 6f: monitorexit
      // 70: return
      // 71: aload 2
      // 72: monitorexit
      // 73: goto 7d
      // 76: astore 4
      // 78: aload 2
      // 79: monitorexit
      // 7a: aload 4
      // 7c: athrow
      // 7d: aload 0
      // 7e: aload 0
      // 7f: astore 2
      // 80: monitorenter
      // 81: bipush 1
      // 82: aload 0
      // 83: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // 86: bipush 1
      // 87: iadd
      // 88: ishl
      // 89: sipush 5000
      // 8c: imul
      // 8d: istore 1
      // 8e: aload 0
      // 8f: iload 1
      // 90: aload 0
      // 91: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._backoffMax I
      // 94: if_icmpgt 9b
      // 97: iload 1
      // 98: goto 9f
      // 9b: aload 0
      // 9c: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._backoffMax I
      // 9f: i2l
      // a0: invokevirtual java/lang/Object.wait (J)V
      // a3: aload 0
      // a4: aload 0
      // a5: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // a8: bipush 1
      // a9: iadd
      // aa: putfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // ad: goto cd
      // b0: astore 3
      // b1: aload 0
      // b2: aload 0
      // b3: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // b6: bipush 1
      // b7: iadd
      // b8: putfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // bb: goto cd
      // be: astore 5
      // c0: aload 0
      // c1: aload 0
      // c2: getfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // c5: bipush 1
      // c6: iadd
      // c7: putfield net/rim/device/cldc/io/mdp/MDPRequestThread._retries I
      // ca: aload 5
      // cc: athrow
      // cd: aload 2
      // ce: monitorexit
      // cf: goto 07
      // d2: astore 6
      // d4: aload 2
      // d5: monitorexit
      // d6: aload 6
      // d8: athrow
      // d9: astore 2
      // da: goto 07
      // try (9 -> 16): 20 null
      // try (17 -> 19): 20 null
      // try (20 -> 23): 20 null
      // try (30 -> 60): 64 null
      // try (61 -> 63): 64 null
      // try (64 -> 67): 64 null
      // try (73 -> 93): 100 null
      // try (73 -> 93): 108 null
      // try (100 -> 101): 108 null
      // try (108 -> 109): 108 null
      // try (73 -> 119): 120 null
      // try (120 -> 123): 120 null
      // try (5 -> 16): 125 null
      // try (17 -> 60): 125 null
      // try (61 -> 125): 125 null
   }
}
