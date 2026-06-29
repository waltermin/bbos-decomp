package net.rim.device.cldc.io.mdp;

final class MDPMaxDelayTimeoutThread extends Thread {
   private int _maxTimeout;
   private boolean _stopped = true;
   private boolean _dieNow;
   private boolean _flag = true;
   private WirelessTransportProfile _wtProfile;

   MDPMaxDelayTimeoutThread(WirelessTransportProfile wtProfile) {
      this._wtProfile = wtProfile;
   }

   final void setTimeout(int maxTimeout) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   final synchronized void stopTimer(boolean killThread) {
      this._stopped = true;
      this._dieNow = killThread;
      this.notify();
   }

   final synchronized void startTimer() {
      if (!this._dieNow) {
         this._flag = false;
         this._stopped = false;
         this.notify();
      }
   }

   final boolean isTimerRunning() {
      return !this._stopped && !this._dieNow;
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
      // 00: aload 0
      // 01: aload 0
      // 02: astore 1
      // 03: monitorenter
      // 04: aload 0
      // 05: getfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._dieNow Z
      // 08: ifeq 0e
      // 0b: aload 1
      // 0c: monitorexit
      // 0d: return
      // 0e: aload 0
      // 0f: getfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._flag Z
      // 12: ifeq 1d
      // 15: aload 0
      // 16: invokevirtual java/lang/Object.wait ()V
      // 19: goto 1d
      // 1c: astore 2
      // 1d: aload 1
      // 1e: monitorexit
      // 1f: goto 27
      // 22: astore 3
      // 23: aload 1
      // 24: monitorexit
      // 25: aload 3
      // 26: athrow
      // 27: aload 0
      // 28: aload 0
      // 29: astore 1
      // 2a: monitorenter
      // 2b: aload 0
      // 2c: getfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._stopped Z
      // 2f: ifne 56
      // 32: aload 0
      // 33: aload 0
      // 34: getfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._maxTimeout I
      // 37: i2l
      // 38: invokevirtual java/lang/Object.wait (J)V
      // 3b: aload 0
      // 3c: bipush 1
      // 3d: putfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._flag Z
      // 40: goto 56
      // 43: astore 2
      // 44: aload 0
      // 45: bipush 1
      // 46: putfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._flag Z
      // 49: goto 56
      // 4c: astore 4
      // 4e: aload 0
      // 4f: bipush 1
      // 50: putfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._flag Z
      // 53: aload 4
      // 55: athrow
      // 56: aload 1
      // 57: monitorexit
      // 58: goto 62
      // 5b: astore 5
      // 5d: aload 1
      // 5e: monitorexit
      // 5f: aload 5
      // 61: athrow
      // 62: aload 0
      // 63: getfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._stopped Z
      // 66: ifne 00
      // 69: invokestatic net/rim/device/cldc/io/mdp/MdpMTHUtil.getTransport ()Lnet/rim/device/cldc/io/mdp/Transport;
      // 6c: invokevirtual net/rim/device/cldc/io/mdp/Transport.getTransportLock ()Ljava/lang/Object;
      // 6f: dup
      // 70: astore 1
      // 71: monitorenter
      // 72: aload 0
      // 73: getfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._wtProfile Lnet/rim/device/cldc/io/mdp/WirelessTransportProfile;
      // 76: ifnull 98
      // 79: aload 0
      // 7a: getfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._wtProfile Lnet/rim/device/cldc/io/mdp/WirelessTransportProfile;
      // 7d: getfield net/rim/device/cldc/io/mdp/WirelessTransportProfile._legacyMode Z
      // 80: ifne 98
      // 83: aload 0
      // 84: getfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._wtProfile Lnet/rim/device/cldc/io/mdp/WirelessTransportProfile;
      // 87: getfield net/rim/device/cldc/io/mdp/WirelessTransportProfile._windowCount I
      // 8a: ifle 98
      // 8d: invokestatic net/rim/device/cldc/io/mdp/MdpMTHUtil.getTransport ()Lnet/rim/device/cldc/io/mdp/Transport;
      // 90: aload 0
      // 91: getfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._wtProfile Lnet/rim/device/cldc/io/mdp/WirelessTransportProfile;
      // 94: bipush 1
      // 95: invokevirtual net/rim/device/cldc/io/mdp/Transport.sendReceipts (Lnet/rim/device/cldc/io/mdp/WirelessTransportProfile;Z)V
      // 98: aload 1
      // 99: monitorexit
      // 9a: goto a4
      // 9d: astore 6
      // 9f: aload 1
      // a0: monitorexit
      // a1: aload 6
      // a3: athrow
      // a4: aload 0
      // a5: aload 0
      // a6: astore 1
      // a7: monitorenter
      // a8: aload 0
      // a9: bipush 1
      // aa: putfield net/rim/device/cldc/io/mdp/MDPMaxDelayTimeoutThread._stopped Z
      // ad: aload 1
      // ae: monitorexit
      // af: goto 00
      // b2: astore 7
      // b4: aload 1
      // b5: monitorexit
      // b6: aload 7
      // b8: athrow
      // b9: astore 1
      // ba: goto 00
      // try (13 -> 15): 16 null
      // try (4 -> 9): 20 null
      // try (10 -> 19): 20 null
      // try (20 -> 23): 20 null
      // try (32 -> 37): 41 null
      // try (32 -> 37): 46 null
      // try (41 -> 42): 46 null
      // try (46 -> 47): 46 null
      // try (29 -> 54): 55 null
      // try (55 -> 58): 55 null
      // try (68 -> 86): 87 null
      // try (87 -> 90): 87 null
      // try (96 -> 101): 102 null
      // try (102 -> 105): 102 null
      // try (0 -> 9): 107 null
      // try (10 -> 107): 107 null
   }
}
