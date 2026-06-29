package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.util.Factory;

public final class WAPPushModelFactory implements Factory {
   public static final int TYPE = 1;
   public static final int TIME_STAMP = 2;
   public static final int STATUS = 3;
   public static final int URL = 4;
   public static final int ACTION = 5;
   public static final int MESSAGE = 6;
   public static final int MESSAGE_2 = 7;
   public static final int EXPIRY = 8;
   public static final int CREATED = 9;
   public static final int ID = 10;
   public static final int PREFERRED_CONFIG_UID = 11;
   public static final int PREFERRED_TRANSPORT_CID = 12;
   public static final int PREFERRED_CONFIG_TYPE = 13;
   public static final int SI = 1;
   public static final int SL = 2;
   public static final int CO = 3;
   public static final int PROVISIONING = 4;

   @Override
   public final Object createInstance(Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 1
      // 01: bipush 61
      // 03: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getFlag (Ljava/lang/Object;I)Z
      // 06: ifne 0c
      // 09: goto e6
      // 0c: aload 1
      // 0d: bipush 19
      // 0f: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getFlag (Ljava/lang/Object;I)Z
      // 12: ifne 18
      // 15: goto e6
      // 18: aload 1
      // 19: sipush 255
      // 1c: i2l
      // 1d: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 20: checkcast net/rim/device/apps/api/framework/model/SyncBuffer
      // 23: astore 2
      // 24: aload 2
      // 25: ifnull 2f
      // 28: aload 2
      // 29: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.isEmpty ()Z
      // 2c: ifeq 31
      // 2f: aconst_null
      // 30: areturn
      // 31: aload 2
      // 32: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.getPosition ()I
      // 35: istore 3
      // 36: bipush 0
      // 37: istore 4
      // 39: aload 2
      // 3a: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.isEmpty ()Z
      // 3d: ifne 68
      // 40: aload 2
      // 41: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.getPosition ()I
      // 44: istore 5
      // 46: aload 2
      // 47: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.getFieldType ()I
      // 4a: bipush 1
      // 4b: if_icmpne 57
      // 4e: aload 2
      // 4f: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.getInt ()I
      // 52: istore 4
      // 54: goto 68
      // 57: aload 2
      // 58: iload 5
      // 5a: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.setPosition (I)V
      // 5d: aload 2
      // 5e: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.skipField ()V
      // 61: goto 39
      // 64: astore 5
      // 66: aconst_null
      // 67: areturn
      // 68: aload 2
      // 69: iload 3
      // 6a: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.setPosition (I)V
      // 6d: aconst_null
      // 6e: astore 5
      // 70: iload 4
      // 72: tableswitch 34 0 4 97 34 51 97 68
      // 94: new net/rim/device/apps/internal/browser/wappush/SICModel
      // 97: dup
      // 98: aconst_null
      // 99: getstatic net/rim/device/apps/internal/browser/options/BrowserConfigRecord.INVALID_VALUE I
      // 9c: aconst_null
      // 9d: invokespecial net/rim/device/apps/internal/browser/wappush/SICModel.<init> (Ljava/lang/String;ILjava/lang/String;)V
      // a0: astore 5
      // a2: goto d3
      // a5: new net/rim/device/apps/internal/browser/wappush/SLCModel
      // a8: dup
      // a9: aconst_null
      // aa: getstatic net/rim/device/apps/internal/browser/options/BrowserConfigRecord.INVALID_VALUE I
      // ad: aconst_null
      // ae: invokespecial net/rim/device/apps/internal/browser/wappush/SLCModel.<init> (Ljava/lang/String;ILjava/lang/String;)V
      // b1: astore 5
      // b3: goto d3
      // b6: ldc_w "net.rim.device.apps.internal.browser.wapprovisioning.WAPProvisioningModel"
      // b9: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // bc: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // bf: checkcast net/rim/device/apps/internal/browser/wappush/WAPPushModel
      // c2: astore 5
      // c4: goto d3
      // c7: astore 6
      // c9: goto d3
      // cc: astore 6
      // ce: goto d3
      // d1: astore 6
      // d3: aload 5
      // d5: ifnull e3
      // d8: aload 5
      // da: aload 2
      // db: invokevirtual net/rim/device/apps/internal/browser/wappush/WAPPushModel.readWAPPushModel (Lnet/rim/device/apps/api/framework/model/SyncBuffer;)Z
      // de: ifne e3
      // e1: aconst_null
      // e2: areturn
      // e3: aload 5
      // e5: areturn
      // e6: aconst_null
      // e7: areturn
      // try (28 -> 48): 48 null
      // try (74 -> 79): 80 null
      // try (74 -> 79): 82 null
      // try (74 -> 79): 84 null
   }
}
