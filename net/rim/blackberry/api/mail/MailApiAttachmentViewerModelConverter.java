package net.rim.blackberry.api.mail;

import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.internal.attachment.AttachmentViewerModelConverter;

final class MailApiAttachmentViewerModelConverter extends AttachmentViewerModelConverter {
   public MailApiAttachmentViewerModelConverter() {
      CMIMEConverterRegistry.addConverter(this, 10);
   }

   @Override
   public final boolean canConvert(Object param1) {
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
      // 002: instanceof java/lang/String
      // 005: ifne 00c
      // 008: pop
      // 009: goto 052
      // 00c: checkcast java/lang/String
      // 00f: astore 2
      // 010: aload 2
      // 011: ldc_w "text/plain"
      // 014: invokestatic net/rim/device/api/util/StringUtilities.startsWithIgnoreCase (Ljava/lang/String;Ljava/lang/String;)Z
      // 017: ifeq 01c
      // 01a: bipush 0
      // 01b: ireturn
      // 01c: invokestatic net/rim/blackberry/api/mail/AttachmentHandlerManager.getInstance ()Lnet/rim/blackberry/api/mail/AttachmentHandlerManager;
      // 01f: invokevirtual net/rim/blackberry/api/mail/AttachmentHandlerManager.getHandlers ()Ljava/util/Vector;
      // 022: astore 3
      // 023: aload 3
      // 024: invokevirtual java/util/Vector.size ()I
      // 027: istore 4
      // 029: iload 4
      // 02b: bipush 1
      // 02c: isub
      // 02d: istore 5
      // 02f: iload 5
      // 031: iflt 052
      // 034: aload 3
      // 035: iload 5
      // 037: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 03a: checkcast net/rim/blackberry/api/mail/AttachmentHandler
      // 03d: astore 6
      // 03f: aload 6
      // 041: aload 2
      // 042: invokeinterface net/rim/blackberry/api/mail/AttachmentHandler.supports (Ljava/lang/String;)Z 2
      // 047: ifeq 04c
      // 04a: bipush 1
      // 04b: ireturn
      // 04c: iinc 5 -1
      // 04f: goto 02f
      // 052: aload 1
      // 053: instanceof net/rim/device/apps/api/transmission/Parameters
      // 056: ifne 05c
      // 059: goto 126
      // 05c: aload 0
      // 05d: aload 1
      // 05e: invokespecial net/rim/device/apps/internal/attachment/AttachmentViewerModelConverter.canConvert (Ljava/lang/Object;)Z
      // 061: pop
      // 062: aload 1
      // 063: checkcast net/rim/device/apps/api/transmission/Parameters
      // 066: astore 2
      // 067: new java/lang/StringBuffer
      // 06a: dup
      // 06b: invokespecial java/lang/StringBuffer.<init> ()V
      // 06e: astore 3
      // 06f: aload 2
      // 070: bipush 1
      // 071: invokevirtual net/rim/device/apps/api/transmission/Parameters.getFirst (B)[B
      // 074: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEContentType.getBaseType ([B)Ljava/lang/String;
      // 077: astore 4
      // 079: aload 4
      // 07b: ifnull 085
      // 07e: aload 3
      // 07f: aload 4
      // 081: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 084: pop
      // 085: aload 2
      // 086: bipush -14
      // 088: invokevirtual net/rim/device/apps/api/transmission/Parameters.getFirst (B)[B
      // 08b: astore 5
      // 08d: bipush 0
      // 08e: istore 6
      // 090: aload 5
      // 092: ifnonnull 0a5
      // 095: aload 2
      // 096: bipush -6
      // 098: invokevirtual net/rim/device/apps/api/transmission/Parameters.getFirst (B)[B
      // 09b: astore 5
      // 09d: aload 5
      // 09f: ifnull 0a5
      // 0a2: bipush 1
      // 0a3: istore 6
      // 0a5: aload 5
      // 0a7: ifnull 11d
      // 0aa: aconst_null
      // 0ab: astore 7
      // 0ad: iload 6
      // 0af: ifeq 0e8
      // 0b2: aload 5
      // 0b4: bipush 1
      // 0b5: invokestatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.getTextObject ([BZ)Ljava/lang/Object;
      // 0b8: checkcast java/lang/String
      // 0bb: astore 7
      // 0bd: aload 7
      // 0bf: ifnonnull 10e
      // 0c2: ldc_w ""
      // 0c5: astore 7
      // 0c7: goto 10e
      // 0ca: astore 8
      // 0cc: aload 7
      // 0ce: ifnonnull 10e
      // 0d1: ldc_w ""
      // 0d4: astore 7
      // 0d6: goto 10e
      // 0d9: astore 9
      // 0db: aload 7
      // 0dd: ifnonnull 0e5
      // 0e0: ldc_w ""
      // 0e3: astore 7
      // 0e5: aload 9
      // 0e7: athrow
      // 0e8: new java/lang/String
      // 0eb: dup
      // 0ec: aload 5
      // 0ee: bipush 0
      // 0ef: aload 5
      // 0f1: arraylength
      // 0f2: getstatic net/rim/device/apps/api/transmission/rim/CMIMEUtilities.CMIME_DEFAULT_EMAIL_ENCODING Ljava/lang/String;
      // 0f5: invokespecial java/lang/String.<init> ([BIILjava/lang/String;)V
      // 0f8: astore 7
      // 0fa: goto 10e
      // 0fd: astore 8
      // 0ff: new java/lang/String
      // 102: dup
      // 103: aload 5
      // 105: bipush 0
      // 106: aload 5
      // 108: arraylength
      // 109: invokespecial java/lang/String.<init> ([BII)V
      // 10c: astore 7
      // 10e: aload 3
      // 10f: ldc_w ";filename="
      // 112: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 115: pop
      // 116: aload 3
      // 117: aload 7
      // 119: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 11c: pop
      // 11d: aload 0
      // 11e: aload 3
      // 11f: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 122: invokevirtual net/rim/blackberry/api/mail/MailApiAttachmentViewerModelConverter.canConvert (Ljava/lang/Object;)Z
      // 125: ireturn
      // 126: bipush 0
      // 127: ireturn
      // try (87 -> 92): 97 null
      // try (87 -> 92): 103 null
      // try (97 -> 98): 103 null
      // try (103 -> 104): 103 null
      // try (110 -> 119): 120 null
   }

   @Override
   public final Object convert(byte[] inputBytes, Object contextObject) {
      ContextObject context = null;
      if (contextObject instanceof Parameters) {
         Parameters parameters = (Parameters)contextObject;
         context = new ContextObject();
         if (inputBytes != null) {
            context.put(8849067667159082262L, inputBytes);
         }

         context.put(-7353832199068708928L, parameters);
      }

      return FactoryUtil.createInstance(7720385540336953835L, context);
   }
}
