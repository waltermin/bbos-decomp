package net.rim.wica.runtime.comm.internal;

class OutgoingRequestProcessor$DeferredQueueDequeueingStrategy implements Runnable {
   private final OutgoingRequestProcessor this$0;

   private OutgoingRequestProcessor$DeferredQueueDequeueingStrategy(OutgoingRequestProcessor this$0) {
      this.this$0 = this$0;
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
      // 000: new java/util/Vector
      // 003: dup
      // 004: invokespecial java/util/Vector.<init> ()V
      // 007: astore 1
      // 008: aload 0
      // 009: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 00c: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor._deferredQueues Ljava/util/Hashtable;
      // 00f: invokevirtual java/util/Hashtable.keys ()Ljava/util/Enumeration;
      // 012: astore 2
      // 013: aload 2
      // 014: invokeinterface java/util/Enumeration.hasMoreElements ()Z 1
      // 019: ifne 01f
      // 01c: goto 1c8
      // 01f: aload 2
      // 020: invokeinterface java/util/Enumeration.nextElement ()Ljava/lang/Object; 1
      // 025: checkcast java/lang/String
      // 028: astore 3
      // 029: aload 0
      // 02a: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 02d: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor._deferredQueues Ljava/util/Hashtable;
      // 030: aload 3
      // 031: invokevirtual java/util/Hashtable.get (Ljava/lang/Object;)Ljava/lang/Object;
      // 034: checkcast net/rim/wica/runtime/util/LinkedQueue
      // 037: astore 4
      // 039: aload 4
      // 03b: invokevirtual net/rim/wica/runtime/util/LinkedQueue.iterator ()Lnet/rim/wica/runtime/util/LinkedQueue$Iterator;
      // 03e: astore 5
      // 040: aconst_null
      // 041: astore 6
      // 043: aconst_null
      // 044: astore 7
      // 046: bipush 1
      // 047: istore 8
      // 049: iload 8
      // 04b: ifne 051
      // 04e: goto 19b
      // 051: aload 5
      // 053: invokevirtual net/rim/wica/runtime/util/LinkedQueue$Iterator.hasNext ()Z
      // 056: ifne 05c
      // 059: goto 19b
      // 05c: aload 0
      // 05d: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 060: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor._commService Lnet/rim/wica/runtime/comm/internal/CommunicationServiceImpl;
      // 063: invokevirtual net/rim/wica/runtime/comm/internal/CommunicationServiceImpl.isInCoverage ()Z
      // 066: ifne 06c
      // 069: goto 19b
      // 06c: aload 5
      // 06e: invokevirtual net/rim/wica/runtime/util/LinkedQueue$Iterator.next ()Ljava/lang/Object;
      // 071: checkcast net/rim/wica/runtime/comm/internal/OutgoingRequestImpl
      // 074: astore 6
      // 076: aload 0
      // 077: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 07a: aload 6
      // 07c: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processRequest (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;)Lnet/rim/wica/runtime/comm/internal/ResponseImpl;
      // 07f: astore 7
      // 081: bipush 1
      // 082: istore 9
      // 084: aload 7
      // 086: invokevirtual net/rim/wica/runtime/comm/internal/ResponseImpl.retry ()Z
      // 089: ifeq 0a6
      // 08c: bipush 0
      // 08d: istore 8
      // 08f: aload 6
      // 091: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.hasExpired ()Z
      // 094: ifne 09f
      // 097: aload 6
      // 099: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.isCanceled ()Z
      // 09c: ifeq 0a3
      // 09f: bipush 1
      // 0a0: goto 0a4
      // 0a3: bipush 0
      // 0a4: istore 9
      // 0a6: iload 9
      // 0a8: ifeq 049
      // 0ab: aload 5
      // 0ad: invokevirtual net/rim/wica/runtime/util/LinkedQueue$Iterator.remove ()V
      // 0b0: aload 0
      // 0b1: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 0b4: aload 6
      // 0b6: aload 7
      // 0b8: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processResponse (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 0bb: goto 049
      // 0be: astore 10
      // 0c0: aload 0
      // 0c1: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 0c4: aload 6
      // 0c6: aload 10
      // 0c8: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processError (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Ljava/lang/Throwable;)Lnet/rim/wica/runtime/comm/internal/ResponseImpl;
      // 0cb: astore 7
      // 0cd: bipush 1
      // 0ce: istore 9
      // 0d0: aload 7
      // 0d2: invokevirtual net/rim/wica/runtime/comm/internal/ResponseImpl.retry ()Z
      // 0d5: ifeq 0f2
      // 0d8: bipush 0
      // 0d9: istore 8
      // 0db: aload 6
      // 0dd: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.hasExpired ()Z
      // 0e0: ifne 0eb
      // 0e3: aload 6
      // 0e5: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.isCanceled ()Z
      // 0e8: ifeq 0ef
      // 0eb: bipush 1
      // 0ec: goto 0f0
      // 0ef: bipush 0
      // 0f0: istore 9
      // 0f2: iload 9
      // 0f4: ifne 0fa
      // 0f7: goto 049
      // 0fa: aload 5
      // 0fc: invokevirtual net/rim/wica/runtime/util/LinkedQueue$Iterator.remove ()V
      // 0ff: aload 0
      // 100: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 103: aload 6
      // 105: aload 7
      // 107: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processResponse (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 10a: goto 049
      // 10d: astore 10
      // 10f: aload 0
      // 110: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 113: aload 6
      // 115: aload 10
      // 117: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processError (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Ljava/lang/Throwable;)Lnet/rim/wica/runtime/comm/internal/ResponseImpl;
      // 11a: astore 7
      // 11c: bipush 1
      // 11d: istore 9
      // 11f: aload 7
      // 121: invokevirtual net/rim/wica/runtime/comm/internal/ResponseImpl.retry ()Z
      // 124: ifeq 141
      // 127: bipush 0
      // 128: istore 8
      // 12a: aload 6
      // 12c: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.hasExpired ()Z
      // 12f: ifne 13a
      // 132: aload 6
      // 134: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.isCanceled ()Z
      // 137: ifeq 13e
      // 13a: bipush 1
      // 13b: goto 13f
      // 13e: bipush 0
      // 13f: istore 9
      // 141: iload 9
      // 143: ifne 149
      // 146: goto 049
      // 149: aload 5
      // 14b: invokevirtual net/rim/wica/runtime/util/LinkedQueue$Iterator.remove ()V
      // 14e: aload 0
      // 14f: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 152: aload 6
      // 154: aload 7
      // 156: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processResponse (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 159: goto 049
      // 15c: astore 11
      // 15e: bipush 1
      // 15f: istore 9
      // 161: aload 7
      // 163: invokevirtual net/rim/wica/runtime/comm/internal/ResponseImpl.retry ()Z
      // 166: ifeq 183
      // 169: bipush 0
      // 16a: istore 8
      // 16c: aload 6
      // 16e: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.hasExpired ()Z
      // 171: ifne 17c
      // 174: aload 6
      // 176: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.isCanceled ()Z
      // 179: ifeq 180
      // 17c: bipush 1
      // 17d: goto 181
      // 180: bipush 0
      // 181: istore 9
      // 183: iload 9
      // 185: ifeq 198
      // 188: aload 5
      // 18a: invokevirtual net/rim/wica/runtime/util/LinkedQueue$Iterator.remove ()V
      // 18d: aload 0
      // 18e: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 191: aload 6
      // 193: aload 7
      // 195: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processResponse (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 198: aload 11
      // 19a: athrow
      // 19b: iload 8
      // 19d: ifeq 1b5
      // 1a0: aload 6
      // 1a2: ifnull 1b5
      // 1a5: aload 0
      // 1a6: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 1a9: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor._commService Lnet/rim/wica/runtime/comm/internal/CommunicationServiceImpl;
      // 1ac: aload 6
      // 1ae: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.getURL ()Lnet/rim/device/cldc/io/utility/URL;
      // 1b1: bipush 0
      // 1b2: invokevirtual net/rim/wica/runtime/comm/internal/CommunicationServiceImpl.fireServerStatusChangeEvent (Lnet/rim/device/cldc/io/utility/URL;Z)V
      // 1b5: aload 4
      // 1b7: invokevirtual net/rim/wica/runtime/util/LinkedQueue.isEmpty ()Z
      // 1ba: ifne 1c0
      // 1bd: goto 013
      // 1c0: aload 1
      // 1c1: aload 3
      // 1c2: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 1c5: goto 013
      // 1c8: bipush 0
      // 1c9: istore 6
      // 1cb: iload 6
      // 1cd: aload 1
      // 1ce: invokevirtual java/util/Vector.size ()I
      // 1d1: if_icmpge 1f0
      // 1d4: aload 1
      // 1d5: iload 6
      // 1d7: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 1da: checkcast java/lang/String
      // 1dd: astore 3
      // 1de: aload 0
      // 1df: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor$DeferredQueueDequeueingStrategy.this$0 Lnet/rim/wica/runtime/comm/internal/OutgoingRequestProcessor;
      // 1e2: getfield net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor._deferredQueues Ljava/util/Hashtable;
      // 1e5: aload 3
      // 1e6: invokevirtual java/util/Hashtable.remove (Ljava/lang/Object;)Ljava/lang/Object;
      // 1e9: pop
      // 1ea: iinc 6 1
      // 1ed: goto 1cb
      // 1f0: return
      // try (50 -> 55): 82 null
      // try (50 -> 55): 117 null
      // try (50 -> 55): 152 null
      // try (82 -> 89): 152 null
      // try (117 -> 124): 152 null
      // try (152 -> 153): 152 null
   }

   OutgoingRequestProcessor$DeferredQueueDequeueingStrategy(OutgoingRequestProcessor x0, OutgoingRequestProcessor$1 x1) {
      this(x0);
   }
}
