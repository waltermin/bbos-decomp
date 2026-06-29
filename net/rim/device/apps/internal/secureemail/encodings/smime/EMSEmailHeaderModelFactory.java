package net.rim.device.apps.internal.secureemail.encodings.smime;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;

class EMSEmailHeaderModelFactory extends RIMModelFactory {
   @Override
   public boolean recognize(Object o) {
      SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(o, 255);
      return syncBuffer != null && syncBuffer.getFieldType() == 71;
   }

   @Override
   public Object createInstance(Object param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 1
      // 001: bipush 43
      // 003: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getFlag (Ljava/lang/Object;I)Z
      // 006: ifne 00c
      // 009: goto 10c
      // 00c: aload 1
      // 00d: bipush 19
      // 00f: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getFlag (Ljava/lang/Object;I)Z
      // 012: ifne 018
      // 015: goto 10c
      // 018: aload 1
      // 019: sipush 255
      // 01c: i2l
      // 01d: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 020: checkcast net/rim/device/apps/api/framework/model/SyncBuffer
      // 023: astore 2
      // 024: new net/rim/device/api/util/DataBuffer
      // 027: dup
      // 028: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 02b: astore 3
      // 02c: new net/rim/device/api/util/DataBuffer
      // 02f: dup
      // 030: invokespecial net/rim/device/api/util/DataBuffer.<init> ()V
      // 033: astore 4
      // 035: bipush 2
      // 037: anewarray 81
      // 03a: astore 5
      // 03c: new net/rim/device/apps/api/framework/model/ContextObject
      // 03f: dup
      // 040: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 043: astore 6
      // 045: aload 2
      // 046: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.getBytes ()[B
      // 049: astore 7
      // 04b: aload 3
      // 04c: aload 7
      // 04e: bipush 0
      // 04f: aload 7
      // 051: arraylength
      // 052: invokevirtual net/rim/device/api/util/DataBuffer.setData ([BII)V
      // 055: aload 3
      // 056: bipush 1
      // 057: invokestatic net/rim/device/api/synchronization/ConverterUtilities.findType (Lnet/rim/device/api/util/DataBuffer;I)Z
      // 05a: ifne 05f
      // 05d: aconst_null
      // 05e: areturn
      // 05f: aload 0
      // 060: aload 3
      // 061: aload 4
      // 063: aload 5
      // 065: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/EMSEmailHeaderModelFactory.fillStringPair (Lnet/rim/device/api/util/DataBuffer;Lnet/rim/device/api/util/DataBuffer;[Ljava/lang/String;)V
      // 068: new net/rim/device/apps/internal/secureemail/encodings/smime/EMSEmailHeaderModel
      // 06b: dup
      // 06c: aload 5
      // 06e: aconst_null
      // 06f: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/EMSEmailHeaderModel.<init> ([Ljava/lang/String;Lnet/rim/device/apps/api/framework/model/ContextObject;)V
      // 072: astore 8
      // 074: aload 3
      // 075: invokevirtual net/rim/device/api/util/DataBuffer.rewind ()V
      // 078: aload 3
      // 079: invokevirtual net/rim/device/api/util/DataBuffer.eof ()Z
      // 07c: ifeq 082
      // 07f: goto 103
      // 082: aload 3
      // 083: invokestatic net/rim/device/api/synchronization/ConverterUtilities.getType (Lnet/rim/device/api/util/DataBuffer;)I
      // 086: tableswitch 30 1 4 49 30 36 42
      // 0a4: bipush 0
      // 0a5: istore 9
      // 0a7: goto 0be
      // 0aa: bipush 1
      // 0ab: istore 9
      // 0ad: goto 0be
      // 0b0: bipush 2
      // 0b2: istore 9
      // 0b4: goto 0be
      // 0b7: aload 3
      // 0b8: invokestatic net/rim/device/api/synchronization/ConverterUtilities.skipField (Lnet/rim/device/api/util/DataBuffer;)V
      // 0bb: goto 078
      // 0be: aload 0
      // 0bf: aload 3
      // 0c0: aload 4
      // 0c2: aload 5
      // 0c4: invokespecial net/rim/device/apps/internal/secureemail/encodings/smime/EMSEmailHeaderModelFactory.fillStringPair (Lnet/rim/device/api/util/DataBuffer;Lnet/rim/device/api/util/DataBuffer;[Ljava/lang/String;)V
      // 0c7: aload 6
      // 0c9: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.reset ()V
      // 0cc: aload 6
      // 0ce: ldc2_w -4054673099568009991
      // 0d1: getstatic net/rim/device/apps/internal/blackberryemail/header/HeaderTypes._typesAsInteger [Ljava/lang/Integer;
      // 0d4: iload 9
      // 0d6: aaload
      // 0d7: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 0da: pop
      // 0db: aload 6
      // 0dd: sipush 251
      // 0e0: i2l
      // 0e1: aload 5
      // 0e3: invokevirtual net/rim/device/api/util/LongHashtable.put (JLjava/lang/Object;)Ljava/lang/Object;
      // 0e6: pop
      // 0e7: ldc2_w -8034039608019345282
      // 0ea: aload 6
      // 0ec: invokestatic net/rim/device/api/util/FactoryUtil.createInstance (JLjava/lang/Object;)Ljava/lang/Object;
      // 0ef: checkcast net/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel
      // 0f2: astore 10
      // 0f4: aload 10
      // 0f6: ifnull 078
      // 0f9: aload 8
      // 0fb: aload 10
      // 0fd: invokevirtual net/rim/device/apps/internal/secureemail/encodings/smime/EMSEmailHeaderModel.addDisplayModel (Lnet/rim/device/apps/internal/blackberryemail/header/EmailHeaderModel;)V
      // 100: goto 078
      // 103: aload 8
      // 105: areturn
      // 106: astore 7
      // 108: aconst_null
      // 109: areturn
      // 10a: astore 7
      // 10c: aconst_null
      // 10d: areturn
      // try (31 -> 45): 111 null
      // try (46 -> 110): 111 null
      // try (31 -> 45): 114 null
      // try (46 -> 110): 114 null
   }

   private void fillStringPair(DataBuffer allData, DataBuffer currentAddressData, String[] stringPair) {
      stringPair[0] = null;
      stringPair[1] = null;
      byte[] currentAddressBytes = ConverterUtilities.readByteArray(allData);
      currentAddressData.setData(currentAddressBytes, 0, currentAddressBytes.length);

      while (!currentAddressData.eof()) {
         switch (ConverterUtilities.getType(currentAddressData)) {
            case 4:
               ConverterUtilities.skipField(currentAddressData);
               break;
            case 5:
            default:
               stringPair[0] = ConverterUtilities.readString(currentAddressData);
               break;
            case 6:
               stringPair[1] = ConverterUtilities.readString(currentAddressData);
         }
      }
   }

   @Override
   public int getMinimumCount(Object context) {
      return Integer.MIN_VALUE;
   }
}
