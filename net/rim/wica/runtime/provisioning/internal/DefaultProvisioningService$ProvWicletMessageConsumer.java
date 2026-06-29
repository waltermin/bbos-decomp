package net.rim.wica.runtime.provisioning.internal;

import net.rim.wica.runtime.messaging.Message;
import net.rim.wica.runtime.messaging.MessageConsumer;

final class DefaultProvisioningService$ProvWicletMessageConsumer implements MessageConsumer {
   private final DefaultProvisioningService this$0;

   private DefaultProvisioningService$ProvWicletMessageConsumer(DefaultProvisioningService this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final Message processMessage(Message param1) {
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
      // 000: aload 1
      // 001: invokeinterface net/rim/wica/runtime/messaging/Message.getMessageName ()Ljava/lang/String; 1
      // 006: astore 2
      // 007: aload 2
      // 008: getstatic net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.START_PROVISIONING_MSG_NAME Ljava/lang/String;
      // 00b: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 00e: ifne 014
      // 011: goto 182
      // 014: aload 1
      // 015: invokeinterface net/rim/wica/runtime/messaging/Message.openReadableDataStream ()Lnet/rim/wica/runtime/messaging/ReadableDataStream; 1
      // 01a: astore 3
      // 01b: aconst_null
      // 01c: astore 4
      // 01e: aconst_null
      // 01f: astore 5
      // 021: bipush -1
      // 023: i2l
      // 024: lstore 6
      // 026: bipush 0
      // 027: istore 8
      // 029: aload 3
      // 02a: invokevirtual net/rim/wica/runtime/messaging/ReadableDataStream.startComponentRead ()Z
      // 02d: istore 8
      // 02f: iload 8
      // 031: ifeq 055
      // 034: aload 3
      // 035: invokevirtual net/rim/wica/runtime/messaging/ReadableDataStream.readString ()Ljava/lang/String;
      // 038: astore 5
      // 03a: aload 3
      // 03b: invokevirtual net/rim/wica/runtime/messaging/ReadableDataStream.readString ()Ljava/lang/String;
      // 03e: astore 9
      // 040: aload 9
      // 042: invokestatic java/lang/Long.parseLong (Ljava/lang/String;)J
      // 045: lstore 6
      // 047: goto 04f
      // 04a: astore 10
      // 04c: bipush 0
      // 04d: istore 8
      // 04f: aload 3
      // 050: invokestatic net/rim/wica/runtime/provisioning/DeploymentDescriptor.readFromStream (Lnet/rim/wica/runtime/messaging/ReadableDataStream;)Lnet/rim/wica/runtime/provisioning/DeploymentDescriptor;
      // 053: astore 4
      // 055: iload 8
      // 057: ifeq 06f
      // 05a: aload 0
      // 05b: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 05e: aload 4
      // 060: aload 5
      // 062: lload 6
      // 064: bipush 0
      // 065: bipush 1
      // 066: bipush 0
      // 067: bipush 0
      // 068: aconst_null
      // 069: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.installApplication (Lnet/rim/wica/runtime/provisioning/DeploymentDescriptor;Ljava/lang/String;JIZZZ[Lnet/rim/wica/runtime/persistence/CollectionSyncModel;)V
      // 06c: goto 1bd
      // 06f: new java/lang/StringBuffer
      // 072: dup
      // 073: ldc_w "Could not deserialize "
      // 076: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 079: getstatic net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.START_PROVISIONING_MSG_NAME Ljava/lang/String;
      // 07c: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 07f: ldc_w " message sent from provisioning app. Invalid message format or DD."
      // 082: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 085: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 088: astore 13
      // 08a: new net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask
      // 08d: dup
      // 08e: aload 0
      // 08f: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 092: aload 4
      // 094: aload 5
      // 096: lload 6
      // 098: bipush 0
      // 099: bipush 1
      // 09a: bipush 0
      // 09b: bipush 0
      // 09c: bipush 0
      // 09d: aconst_null
      // 09e: aconst_null
      // 09f: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.<init> (Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;Lnet/rim/wica/runtime/provisioning/DeploymentDescriptor;Ljava/lang/String;JIZZIZ[B[Lnet/rim/wica/runtime/persistence/CollectionSyncModel;)V
      // 0a2: astore 14
      // 0a4: aload 0
      // 0a5: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 0a8: aload 14
      // 0aa: bipush 3
      // 0ac: sipush 906
      // 0af: aload 13
      // 0b1: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;)V
      // 0b4: goto 1bd
      // 0b7: astore 9
      // 0b9: bipush 0
      // 0ba: istore 8
      // 0bc: iload 8
      // 0be: ifeq 0d6
      // 0c1: aload 0
      // 0c2: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 0c5: aload 4
      // 0c7: aload 5
      // 0c9: lload 6
      // 0cb: bipush 0
      // 0cc: bipush 1
      // 0cd: bipush 0
      // 0ce: bipush 0
      // 0cf: aconst_null
      // 0d0: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.installApplication (Lnet/rim/wica/runtime/provisioning/DeploymentDescriptor;Ljava/lang/String;JIZZZ[Lnet/rim/wica/runtime/persistence/CollectionSyncModel;)V
      // 0d3: goto 1bd
      // 0d6: new java/lang/StringBuffer
      // 0d9: dup
      // 0da: ldc_w "Could not deserialize "
      // 0dd: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 0e0: getstatic net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.START_PROVISIONING_MSG_NAME Ljava/lang/String;
      // 0e3: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0e6: ldc_w " message sent from provisioning app. Invalid message format or DD."
      // 0e9: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0ec: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0ef: astore 13
      // 0f1: new net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask
      // 0f4: dup
      // 0f5: aload 0
      // 0f6: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 0f9: aload 4
      // 0fb: aload 5
      // 0fd: lload 6
      // 0ff: bipush 0
      // 100: bipush 1
      // 101: bipush 0
      // 102: bipush 0
      // 103: bipush 0
      // 104: aconst_null
      // 105: aconst_null
      // 106: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.<init> (Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;Lnet/rim/wica/runtime/provisioning/DeploymentDescriptor;Ljava/lang/String;JIZZIZ[B[Lnet/rim/wica/runtime/persistence/CollectionSyncModel;)V
      // 109: astore 14
      // 10b: aload 0
      // 10c: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 10f: aload 14
      // 111: bipush 3
      // 113: sipush 906
      // 116: aload 13
      // 118: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;)V
      // 11b: goto 1bd
      // 11e: astore 11
      // 120: iload 8
      // 122: ifeq 13a
      // 125: aload 0
      // 126: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 129: aload 4
      // 12b: aload 5
      // 12d: lload 6
      // 12f: bipush 0
      // 130: bipush 1
      // 131: bipush 0
      // 132: bipush 0
      // 133: aconst_null
      // 134: invokevirtual net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.installApplication (Lnet/rim/wica/runtime/provisioning/DeploymentDescriptor;Ljava/lang/String;JIZZZ[Lnet/rim/wica/runtime/persistence/CollectionSyncModel;)V
      // 137: goto 17f
      // 13a: new java/lang/StringBuffer
      // 13d: dup
      // 13e: ldc_w "Could not deserialize "
      // 141: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 144: getstatic net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.START_PROVISIONING_MSG_NAME Ljava/lang/String;
      // 147: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 14a: ldc_w " message sent from provisioning app. Invalid message format or DD."
      // 14d: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 150: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 153: astore 13
      // 155: new net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask
      // 158: dup
      // 159: aload 0
      // 15a: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 15d: aload 4
      // 15f: aload 5
      // 161: lload 6
      // 163: bipush 0
      // 164: bipush 1
      // 165: bipush 0
      // 166: bipush 0
      // 167: bipush 0
      // 168: aconst_null
      // 169: aconst_null
      // 16a: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvisioningTask.<init> (Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;Lnet/rim/wica/runtime/provisioning/DeploymentDescriptor;Ljava/lang/String;JIZZIZ[B[Lnet/rim/wica/runtime/persistence/CollectionSyncModel;)V
      // 16d: astore 14
      // 16f: aload 0
      // 170: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 173: aload 14
      // 175: bipush 3
      // 177: sipush 906
      // 17a: aload 13
      // 17c: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.fireProvisioningEvent (Lnet/rim/wica/runtime/provisioning/ProvisioningTaskInfo;IILjava/lang/String;)V
      // 17f: aload 11
      // 181: athrow
      // 182: aload 2
      // 183: getstatic net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.CANCEL_PROVISIONING_MSG_NAME Ljava/lang/String;
      // 186: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 189: ifeq 1bd
      // 18c: aconst_null
      // 18d: astore 3
      // 18e: aload 1
      // 18f: invokeinterface net/rim/wica/runtime/messaging/Message.openReadableDataStream ()Lnet/rim/wica/runtime/messaging/ReadableDataStream; 1
      // 194: invokevirtual net/rim/wica/runtime/messaging/ReadableDataStream.readString ()Ljava/lang/String;
      // 197: astore 3
      // 198: aload 0
      // 199: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 19c: aload 3
      // 19d: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.cancelProvisioning (Ljava/lang/String;)V
      // 1a0: goto 1bd
      // 1a3: astore 4
      // 1a5: aload 0
      // 1a6: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 1a9: aload 3
      // 1aa: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.cancelProvisioning (Ljava/lang/String;)V
      // 1ad: goto 1bd
      // 1b0: astore 15
      // 1b2: aload 0
      // 1b3: getfield net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService$ProvWicletMessageConsumer.this$0 Lnet/rim/wica/runtime/provisioning/internal/DefaultProvisioningService;
      // 1b6: aload 3
      // 1b7: invokespecial net/rim/wica/runtime/provisioning/internal/DefaultProvisioningService.cancelProvisioning (Ljava/lang/String;)V
      // 1ba: aload 15
      // 1bc: athrow
      // 1bd: aconst_null
      // 1be: areturn
      // try (31 -> 34): 35 null
      // try (20 -> 41): 89 net/rim/wica/runtime/messaging/MessageException
      // try (20 -> 41): 140 null
      // try (89 -> 92): 140 null
      // try (140 -> 141): 140 null
      // try (196 -> 200): 205 null
      // try (196 -> 200): 211 null
      // try (205 -> 206): 211 null
      // try (211 -> 212): 211 null
   }

   DefaultProvisioningService$ProvWicletMessageConsumer(DefaultProvisioningService x0, DefaultProvisioningService$1 x1) {
      this(x0);
   }
}
