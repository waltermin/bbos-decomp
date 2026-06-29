package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.quickcontact.QuickContactItem;
import net.rim.device.apps.api.quickcontact.QuickContactItemFactory;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.api.quickcontact.QuickContactUtil;

final class SpeedDialItemFactory extends RIMModelFactory implements QuickContactItemFactory {
   SpeedDialItemFactory() {
      this.addVoiceMailItem();
   }

   @Override
   public final Object createInstance(Object param1) {
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
      // 001: dup
      // 002: instanceof net/rim/device/apps/api/quickcontact/QuickContactData
      // 005: ifne 00c
      // 008: pop
      // 009: goto 086
      // 00c: checkcast net/rim/device/apps/api/quickcontact/QuickContactData
      // 00f: astore 2
      // 010: aload 2
      // 011: invokevirtual net/rim/device/apps/api/quickcontact/QuickContactData.getKey ()C
      // 014: istore 3
      // 015: aload 2
      // 016: invokevirtual net/rim/device/apps/api/quickcontact/QuickContactData.getData ()Ljava/lang/Object;
      // 019: astore 4
      // 01b: aload 4
      // 01d: dup
      // 01e: instanceof net/rim/device/apps/api/framework/model/ContextObject
      // 021: ifne 028
      // 024: pop
      // 025: goto 06d
      // 028: checkcast net/rim/device/apps/api/framework/model/ContextObject
      // 02b: astore 5
      // 02d: aload 5
      // 02f: sipush 247
      // 032: i2l
      // 033: invokevirtual net/rim/device/api/util/LongHashtable.get (J)Ljava/lang/Object;
      // 036: astore 6
      // 038: aload 6
      // 03a: instanceof net/rim/device/apps/internal/phone/model/PhoneNumberModel
      // 03d: ifeq 06b
      // 040: aload 5
      // 042: sipush 252
      // 045: i2l
      // 046: invokevirtual net/rim/device/api/util/LongHashtable.get (J)Ljava/lang/Object;
      // 049: astore 7
      // 04b: new net/rim/device/apps/internal/phone/data/CallerIDInfo
      // 04e: dup
      // 04f: aload 6
      // 051: checkcast net/rim/device/apps/api/framework/model/PersistableRIMModel
      // 054: aload 7
      // 056: checkcast net/rim/device/apps/api/framework/model/PersistableRIMModel
      // 059: bipush 0
      // 05a: bipush 1
      // 05b: invokespecial net/rim/device/apps/internal/phone/data/CallerIDInfo.<init> (Lnet/rim/device/apps/api/framework/model/PersistableRIMModel;Lnet/rim/device/apps/api/framework/model/PersistableRIMModel;ZZ)V
      // 05e: astore 8
      // 060: new net/rim/device/apps/internal/phone/data/SpeedDialItem
      // 063: dup
      // 064: iload 3
      // 065: aload 8
      // 067: invokespecial net/rim/device/apps/internal/phone/data/SpeedDialItem.<init> (CLnet/rim/device/apps/internal/phone/data/CallerIDInfo;)V
      // 06a: areturn
      // 06b: aconst_null
      // 06c: areturn
      // 06d: aload 4
      // 06f: instanceof net/rim/device/apps/internal/phone/data/CallerIDInfo
      // 072: ifne 078
      // 075: goto 118
      // 078: new net/rim/device/apps/internal/phone/data/SpeedDialItem
      // 07b: dup
      // 07c: iload 3
      // 07d: aload 4
      // 07f: checkcast net/rim/device/apps/internal/phone/data/CallerIDInfo
      // 082: invokespecial net/rim/device/apps/internal/phone/data/SpeedDialItem.<init> (CLnet/rim/device/apps/internal/phone/data/CallerIDInfo;)V
      // 085: areturn
      // 086: aload 1
      // 087: bipush 79
      // 089: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getFlag (Ljava/lang/Object;I)Z
      // 08c: ifne 092
      // 08f: goto 118
      // 092: aload 1
      // 093: bipush 19
      // 095: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getFlag (Ljava/lang/Object;I)Z
      // 098: ifne 09e
      // 09b: goto 118
      // 09e: aload 1
      // 09f: sipush 255
      // 0a2: i2l
      // 0a3: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 0a6: checkcast net/rim/device/apps/api/framework/model/SyncBuffer
      // 0a9: astore 2
      // 0aa: aload 2
      // 0ab: ifnonnull 0b0
      // 0ae: aconst_null
      // 0af: areturn
      // 0b0: aconst_null
      // 0b1: astore 3
      // 0b2: aload 1
      // 0b3: bipush 20
      // 0b5: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getFlag (Ljava/lang/Object;I)Z
      // 0b8: istore 4
      // 0ba: iload 4
      // 0bc: ifne 0c5
      // 0bf: aload 1
      // 0c0: bipush 20
      // 0c2: invokestatic net/rim/device/apps/api/framework/model/ContextObject.setFlag (Ljava/lang/Object;I)V
      // 0c5: ldc2_w -7443913519806541253
      // 0c8: aload 2
      // 0c9: invokestatic net/rim/device/apps/api/quickcontact/QuickContactItem.getKey (JLnet/rim/device/apps/api/framework/model/SyncBuffer;)C
      // 0cc: istore 5
      // 0ce: ldc2_w 2629643229137268956
      // 0d1: aload 1
      // 0d2: invokestatic net/rim/device/api/util/FactoryUtil.createInstance (JLjava/lang/Object;)Ljava/lang/Object;
      // 0d5: checkcast net/rim/device/apps/internal/phone/data/CallerIDInfo
      // 0d8: astore 6
      // 0da: new net/rim/device/apps/internal/phone/data/SpeedDialItem
      // 0dd: dup
      // 0de: aload 2
      // 0df: invokevirtual net/rim/device/apps/api/framework/model/SyncBuffer.getUID ()I
      // 0e2: iload 5
      // 0e4: aload 6
      // 0e6: invokespecial net/rim/device/apps/internal/phone/data/SpeedDialItem.<init> (ICLnet/rim/device/apps/internal/phone/data/CallerIDInfo;)V
      // 0e9: astore 3
      // 0ea: iload 4
      // 0ec: ifne 116
      // 0ef: aload 1
      // 0f0: bipush 20
      // 0f2: invokestatic net/rim/device/apps/api/framework/model/ContextObject.clearFlag (Ljava/lang/Object;I)V
      // 0f5: aload 3
      // 0f6: areturn
      // 0f7: astore 5
      // 0f9: iload 4
      // 0fb: ifne 116
      // 0fe: aload 1
      // 0ff: bipush 20
      // 101: invokestatic net/rim/device/apps/api/framework/model/ContextObject.clearFlag (Ljava/lang/Object;I)V
      // 104: aload 3
      // 105: areturn
      // 106: astore 9
      // 108: iload 4
      // 10a: ifne 113
      // 10d: aload 1
      // 10e: bipush 20
      // 110: invokestatic net/rim/device/apps/api/framework/model/ContextObject.clearFlag (Ljava/lang/Object;I)V
      // 113: aload 9
      // 115: athrow
      // 116: aload 3
      // 117: areturn
      // 118: aconst_null
      // 119: areturn
      // try (95 -> 112): 119 null
      // try (95 -> 112): 127 null
      // try (119 -> 120): 127 null
      // try (127 -> 128): 127 null
   }

   @Override
   public final boolean recognize(Object object) {
      if (object instanceof SpeedDialItem) {
         return true;
      } else {
         return ContextObject.getFlag(object, 79) && ContextObject.getFlag(object, 19)
            ? QuickContactItem.recognize(-7443913519806541253L, (SyncBuffer)ContextObject.get(object, 255))
            : false;
      }
   }

   @Override
   public final int getMinimumCount(Object context) {
      return 1;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      return null;
   }

   @Override
   public final void syncFinished(QuickContactItem[] rejectedQCIs) {
      for (int i = rejectedQCIs.length - 1; i >= 0; i--) {
         QuickContactItem var10000 = rejectedQCIs[i];
         if (rejectedQCIs[i] instanceof SpeedDialItem) {
            SpeedDialItem sdi = (SpeedDialItem)var10000;
            if (sdi.isVoiceMail()) {
               this.addVoiceMailItem();
               return;
            }
         }
      }
   }

   private final void addVoiceMailItem() {
      QuickContactList qcl = QuickContactList.getInstance();
      char oneKey = '1';
      char targetKey;
      if (QuickContactUtil.fullQWERTYSupport()) {
         targetKey = CharacterUtilities.toUpperCase(Keypad.getUnaltedChar(oneKey));
      } else {
         targetKey = oneKey;
      }

      Object sdItem = qcl.getQuickContactItem(targetKey);
      if (sdItem == null) {
         CallerIDInfo voicemailCallerID = new CallerIDInfo(null, new SpecialAddressCard(-7117173429217454741L), false, false);
         SpeedDialItem sdi = new SpeedDialItem(targetKey, voicemailCallerID);
         qcl.add(sdi);
      }
   }
}
