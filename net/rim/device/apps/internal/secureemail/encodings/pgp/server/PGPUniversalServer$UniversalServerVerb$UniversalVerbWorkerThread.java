package net.rim.device.apps.internal.secureemail.encodings.pgp.server;

import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread extends PleaseWaitWorkerThread {
   private final PGPUniversalServer$UniversalServerVerb this$1;

   private PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread(PGPUniversalServer$UniversalServerVerb _1) {
      this.this$1 = _1;
   }

   @Override
   protected void doWork() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/internal/ui/component/PleaseWaitWorkerThread._pleaseWaitDialog Lnet/rim/device/internal/ui/component/PleaseWaitDialog;
      // 04: checkcast java/lang/Object
      // 07: astore 1
      // 08: bipush -1
      // 0a: istore 2
      // 0b: aload 0
      // 0c: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread.this$1 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb;
      // 0f: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb._verbType I
      // 12: tableswitch 34 -1 3 179 78 34 122 166
      // 34: sipush 8083
      // 37: istore 2
      // 38: aload 0
      // 39: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread.this$1 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb;
      // 3c: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer;
      // 3f: aload 0
      // 40: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread.this$1 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb;
      // 43: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb._authenticationCookie Ljava/lang/String;
      // 46: aload 1
      // 47: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.authenticate (Ljava/lang/String;Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)I
      // 4a: istore 3
      // 4b: iload 3
      // 4c: bipush 2
      // 4e: if_icmpne 54
      // 51: goto 60
      // 54: iload 3
      // 55: bipush 1
      // 56: if_icmpne d6
      // 59: sipush 8076
      // 5c: istore 2
      // 5d: goto d6
      // 60: sipush 8080
      // 63: istore 2
      // 64: aload 0
      // 65: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread.this$1 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb;
      // 68: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer;
      // 6b: aload 1
      // 6c: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.enroll (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)I
      // 6f: istore 4
      // 71: iload 4
      // 73: bipush 1
      // 74: if_icmpne 7e
      // 77: sipush 8105
      // 7a: istore 2
      // 7b: goto d6
      // 7e: iload 4
      // 80: bipush 2
      // 82: if_icmpne d6
      // 85: sipush 8081
      // 88: istore 2
      // 89: goto d6
      // 8c: sipush 8085
      // 8f: istore 2
      // 90: aload 0
      // 91: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread.this$1 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb;
      // 94: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer;
      // 97: aload 1
      // 98: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.downloadKeys (Lnet/rim/device/apps/internal/secureemail/server/SecureEmailServerOperationListener;)I
      // 9b: istore 5
      // 9d: iload 5
      // 9f: bipush 1
      // a0: if_icmpne aa
      // a3: sipush 8086
      // a6: istore 2
      // a7: goto d6
      // aa: iload 5
      // ac: bipush 2
      // ae: if_icmpne d6
      // b1: sipush 8112
      // b4: istore 2
      // b5: goto d6
      // b8: aload 0
      // b9: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread.this$1 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb;
      // bc: getfield net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer$UniversalServerVerb.this$0 Lnet/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer;
      // bf: invokespecial net/rim/device/apps/internal/secureemail/encodings/pgp/server/PGPUniversalServer.clearCache ()V
      // c2: goto d6
      // c5: new java/lang/Object
      // c8: dup
      // c9: invokespecial java/lang/IllegalArgumentException.<init> ()V
      // cc: athrow
      // cd: astore 3
      // ce: sipush 8110
      // d1: istore 2
      // d2: goto d6
      // d5: astore 3
      // d6: iload 2
      // d7: bipush -1
      // d9: if_icmpeq e3
      // dc: iload 2
      // dd: invokestatic net/rim/device/apps/internal/secureemail/encodings/pgp/PGPResources.getString (I)Ljava/lang/String;
      // e0: invokestatic net/rim/device/internal/ui/component/BackgroundDialog.showMessage (Ljava/lang/String;)V
      // e3: return
      // try (6 -> 80): 80 null
      // try (6 -> 80): 84 null
   }

   PGPUniversalServer$UniversalServerVerb$UniversalVerbWorkerThread(PGPUniversalServer$UniversalServerVerb x0, PGPUniversalServer$1 x1) {
      this(x0);
   }
}
