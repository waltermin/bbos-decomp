package net.rim.device.apps.internal.diagnostic;

import net.rim.vm.EventLog;

final class EventLogScanner extends Thread {
   String[] targetStr;
   long callingTimeStamp;
   int[] eventHandles;
   boolean hitFlag;
   String targetAppName;
   TestBBReg thread;

   EventLogScanner(Thread _thread, String _targetAppName, String[] _targetStr, long _callingTimeStamp) {
      this.callingTimeStamp = _callingTimeStamp;
      this.targetStr = _targetStr;
      this.targetAppName = _targetAppName;
      this.thread = (TestBBReg)_thread;
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
      // 01: bipush 0
      // 02: putfield net/rim/device/apps/internal/diagnostic/EventLogScanner.hitFlag Z
      // 05: aload 0
      // 06: invokestatic net/rim/vm/EventLog.getSnapshot ()[I
      // 09: putfield net/rim/device/apps/internal/diagnostic/EventLogScanner.eventHandles [I
      // 0c: aload 0
      // 0d: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.eventHandles [I
      // 10: arraylength
      // 11: bipush 1
      // 12: isub
      // 13: istore 1
      // 14: iload 1
      // 15: iflt 5c
      // 18: aload 0
      // 19: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.callingTimeStamp J
      // 1c: aload 0
      // 1d: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.eventHandles [I
      // 20: iload 1
      // 21: iaload
      // 22: invokestatic net/rim/vm/EventLog.getTime (I)J
      // 25: lcmp
      // 26: ifgt 5c
      // 29: aload 0
      // 2a: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.targetAppName Ljava/lang/String;
      // 2d: ldc_w "net.rim.hrtRT"
      // 30: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 33: ifeq 46
      // 36: aload 0
      // 37: iload 1
      // 38: invokespecial net/rim/device/apps/internal/diagnostic/EventLogScanner.isHRTHit (I)Z
      // 3b: ifeq 56
      // 3e: aload 0
      // 3f: bipush 1
      // 40: putfield net/rim/device/apps/internal/diagnostic/EventLogScanner.hitFlag Z
      // 43: goto 5c
      // 46: aload 0
      // 47: iload 1
      // 48: invokespecial net/rim/device/apps/internal/diagnostic/EventLogScanner.isHit (I)Z
      // 4b: ifeq 56
      // 4e: aload 0
      // 4f: bipush 1
      // 50: putfield net/rim/device/apps/internal/diagnostic/EventLogScanner.hitFlag Z
      // 53: goto 5c
      // 56: iinc 1 -1
      // 59: goto 14
      // 5c: aload 0
      // 5d: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.hitFlag Z
      // 60: ifeq 6b
      // 63: aload 0
      // 64: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.thread Lnet/rim/device/apps/internal/diagnostic/TestBBReg;
      // 67: bipush 1
      // 68: putfield net/rim/device/apps/internal/diagnostic/TestBBReg.responseFlag Z
      // 6b: aload 0
      // 6c: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.eventHandles [I
      // 6f: invokestatic net/rim/vm/EventLog.freeSnapshot ([I)V
      // 72: return
      // 73: astore 1
      // 74: aload 0
      // 75: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.eventHandles [I
      // 78: invokestatic net/rim/vm/EventLog.freeSnapshot ([I)V
      // 7b: return
      // 7c: astore 2
      // 7d: aload 0
      // 7e: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.eventHandles [I
      // 81: invokestatic net/rim/vm/EventLog.freeSnapshot ([I)V
      // 84: aload 2
      // 85: athrow
      // try (0 -> 53): 57 null
      // try (0 -> 53): 62 null
      // try (57 -> 58): 62 null
      // try (62 -> 63): 62 null
   }

   private final boolean isHit(int i) {
      if (!EventLog.getRegisteredAppName(EventLog.getGUID(this.eventHandles[i])).equalsIgnoreCase(this.targetAppName)) {
         return false;
      }

      String tmpStr = (String)(new Object(EventLog.getData(this.eventHandles[i])));
      System.out.println(tmpStr);

      for (int j = 0; j < this.targetStr.length; j++) {
         if (tmpStr.equalsIgnoreCase(this.targetStr[j])) {
            return true;
         }
      }

      return false;
   }

   private final boolean isHRTHit(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.eventHandles [I
      // 04: iload 1
      // 05: iaload
      // 06: invokestatic net/rim/vm/EventLog.getGUID (I)J
      // 09: invokestatic net/rim/vm/EventLog.getRegisteredAppName (J)Ljava/lang/String;
      // 0c: aload 0
      // 0d: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.targetAppName Ljava/lang/String;
      // 10: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 13: ifne 18
      // 16: bipush 0
      // 17: ireturn
      // 18: new java/lang/Object
      // 1b: dup
      // 1c: aload 0
      // 1d: getfield net/rim/device/apps/internal/diagnostic/EventLogScanner.eventHandles [I
      // 20: iload 1
      // 21: iaload
      // 22: invokestatic net/rim/vm/EventLog.getData (I)[B
      // 25: invokespecial java/lang/String.<init> ([B)V
      // 28: astore 2
      // 29: aload 2
      // 2a: ldc_w "ERRs"
      // 2d: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 30: ifeq 56
      // 33: aload 2
      // 34: bipush 7
      // 36: aload 2
      // 37: invokevirtual java/lang/String.length ()I
      // 3a: invokevirtual java/lang/String.substring (II)Ljava/lang/String;
      // 3d: bipush 16
      // 3f: invokestatic java/lang/Integer.parseInt (Ljava/lang/String;I)I
      // 42: istore 3
      // 43: iload 3
      // 44: ifle 56
      // 47: iload 3
      // 48: sipush 3000
      // 4b: if_icmpge 56
      // 4e: bipush 1
      // 4f: ireturn
      // 50: astore 3
      // 51: bipush 0
      // 52: ireturn
      // 53: astore 3
      // 54: bipush 0
      // 55: ireturn
      // 56: bipush 0
      // 57: ireturn
      // try (25 -> 39): 40 null
      // try (25 -> 39): 43 null
   }
}
