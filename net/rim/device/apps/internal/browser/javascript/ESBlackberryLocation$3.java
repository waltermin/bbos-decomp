package net.rim.device.apps.internal.browser.javascript;

class ESBlackberryLocation$3 extends JavaScriptHostFunction {
   private final ESBlackberryLocation this$0;

   ESBlackberryLocation$3(ESBlackberryLocation _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation$3.this$0 Lnet/rim/device/apps/internal/browser/javascript/ESBlackberryLocation;
      // 04: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation._gpsSupported Z
      // 07: ifne 0d
      // 0a: goto b4
      // 0d: aload 0
      // 0e: invokevirtual net/rim/ecmascript/runtime/HostFunction.getNumParms ()I
      // 11: bipush 1
      // 12: if_icmpeq 18
      // 15: goto b4
      // 18: aload 0
      // 19: bipush 0
      // 1a: invokevirtual net/rim/ecmascript/runtime/HostFunction.getParm (I)J
      // 1d: invokestatic net/rim/ecmascript/runtime/Convert.toInt32 (J)I
      // 20: tableswitch 32 -1 2 81 32 48 65
      // 40: new java/lang/Object
      // 43: dup
      // 44: invokespecial javax/microedition/location/Criteria.<init> ()V
      // 47: astore 1
      // 48: aload 1
      // 49: bipush 1
      // 4a: invokevirtual javax/microedition/location/Criteria.setPreferredPowerConsumption (I)V
      // 4d: goto 73
      // 50: new java/lang/Object
      // 53: dup
      // 54: invokespecial javax/microedition/location/Criteria.<init> ()V
      // 57: astore 1
      // 58: aload 1
      // 59: bipush 2
      // 5b: invokevirtual javax/microedition/location/Criteria.setPreferredPowerConsumption (I)V
      // 5e: goto 73
      // 61: new java/lang/Object
      // 64: dup
      // 65: invokespecial javax/microedition/location/Criteria.<init> ()V
      // 68: astore 1
      // 69: aload 1
      // 6a: bipush 0
      // 6b: invokevirtual javax/microedition/location/Criteria.setCostAllowed (Z)V
      // 6e: goto 73
      // 71: aconst_null
      // 72: astore 1
      // 73: aload 1
      // 74: ifnull b4
      // 77: aload 0
      // 78: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation$3.this$0 Lnet/rim/device/apps/internal/browser/javascript/ESBlackberryLocation;
      // 7b: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation._locationProvider Ljavax/microedition/location/LocationProvider;
      // 7e: ifnull a4
      // 81: aload 0
      // 82: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation$3.this$0 Lnet/rim/device/apps/internal/browser/javascript/ESBlackberryLocation;
      // 85: bipush 0
      // 86: putfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation._listenerRegistered Z
      // 89: aload 0
      // 8a: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation$3.this$0 Lnet/rim/device/apps/internal/browser/javascript/ESBlackberryLocation;
      // 8d: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation._locationProvider Ljavax/microedition/location/LocationProvider;
      // 90: aconst_null
      // 91: bipush -1
      // 93: bipush -1
      // 95: bipush -1
      // 97: invokevirtual javax/microedition/location/LocationProvider.setLocationListener (Ljavax/microedition/location/LocationListener;III)V
      // 9a: aload 0
      // 9b: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation$3.this$0 Lnet/rim/device/apps/internal/browser/javascript/ESBlackberryLocation;
      // 9e: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation._locationProvider Ljavax/microedition/location/LocationProvider;
      // a1: invokevirtual javax/microedition/location/LocationProvider.reset ()V
      // a4: aload 0
      // a5: getfield net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation$3.this$0 Lnet/rim/device/apps/internal/browser/javascript/ESBlackberryLocation;
      // a8: aload 1
      // a9: invokespecial net/rim/device/apps/internal/browser/javascript/ESBlackberryLocation.setCriteria (Ljavax/microedition/location/Criteria;)V
      // ac: goto b4
      // af: astore 1
      // b0: goto b4
      // b3: astore 1
      // b4: getstatic net/rim/ecmascript/runtime/Value.NULL J
      // b7: lreturn
      // try (10 -> 67): 68 null
      // try (10 -> 67): 70 null
   }
}
