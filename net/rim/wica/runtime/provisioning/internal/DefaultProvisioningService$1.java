package net.rim.wica.runtime.provisioning.internal;

class DefaultProvisioningService$1 extends Thread {
   private final DefaultProvisioningService this$0;

   DefaultProvisioningService$1(DefaultProvisioningService this$0, String x0) {
      super(x0);
      this.this$0 = this$0;
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
      // 00: bipush 1
      // 01: istore 1
      // 02: iload 1
      // 03: ifeq a2
      // 06: aload 0
      // 07: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 0a: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._provQueue Lnet/rim/wica/runtime/util/BoundedLinkedQueue;
      // 0d: invokevirtual net/rim/wica/runtime/util/BoundedLinkedQueue.take ()Ljava/lang/Object;
      // 10: checkcast net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask
      // 13: astore 2
      // 14: aload 2
      // 15: aload 0
      // 16: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 19: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.STOP_THREAD_TASK Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask;
      // 1c: if_acmpne 24
      // 1f: bipush 0
      // 20: istore 1
      // 21: goto 02
      // 24: new net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTaskListener
      // 27: dup
      // 28: aload 0
      // 29: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 2c: aconst_null
      // 2d: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTaskListener.<init> (Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1;)V
      // 30: astore 3
      // 31: aload 0
      // 32: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 35: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._eventService Lnet/rim/wica/runtime/event/EventService;
      // 38: sipush 500
      // 3b: aload 3
      // 3c: invokeinterface net/rim/wica/runtime/event/EventService.addListener (ILnet/rim/wica/runtime/event/EventListener;)V 3
      // 41: aload 0
      // 42: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 45: aload 2
      // 46: putfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._currentProvisioningTask Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask;
      // 49: aload 2
      // 4a: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.run ()V
      // 4d: aload 0
      // 4e: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 51: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._eventService Lnet/rim/wica/runtime/event/EventService;
      // 54: sipush 500
      // 57: aload 3
      // 58: invokeinterface net/rim/wica/runtime/event/EventService.removeListener (ILnet/rim/wica/runtime/event/EventListener;)V 3
      // 5d: aload 0
      // 5e: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 61: aconst_null
      // 62: putfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._currentProvisioningTask Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask;
      // 65: goto 02
      // 68: astore 4
      // 6a: aload 0
      // 6b: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 6e: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._eventService Lnet/rim/wica/runtime/event/EventService;
      // 71: sipush 500
      // 74: aload 3
      // 75: invokeinterface net/rim/wica/runtime/event/EventService.removeListener (ILnet/rim/wica/runtime/event/EventListener;)V 3
      // 7a: aload 0
      // 7b: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 7e: aconst_null
      // 7f: putfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._currentProvisioningTask Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask;
      // 82: goto 02
      // 85: astore 5
      // 87: aload 0
      // 88: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 8b: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._eventService Lnet/rim/wica/runtime/event/EventService;
      // 8e: sipush 500
      // 91: aload 3
      // 92: invokeinterface net/rim/wica/runtime/event/EventService.removeListener (ILnet/rim/wica/runtime/event/EventListener;)V 3
      // 97: aload 0
      // 98: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$1.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 9b: aconst_null
      // 9c: putfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService._currentProvisioningTask Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask;
      // 9f: aload 5
      // a1: athrow
      // a2: return
      // try (35 -> 37): 48 null
      // try (35 -> 37): 60 null
      // try (48 -> 49): 60 null
      // try (60 -> 61): 60 null
   }
}
