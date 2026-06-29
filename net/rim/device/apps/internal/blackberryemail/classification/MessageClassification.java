package net.rim.device.apps.internal.blackberryemail.classification;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.StringMatch;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

public class MessageClassification {
   private String _displayString;
   private String _subjectSuffix;
   private int _minimumEncodingActions;
   private static final long INJECTED_MESSAGE_CLASSIFICATIONS = -4543606409829069159L;
   private static final int OUTER_TYPE_MESSAGE_CLASSIFICATION = 1;
   private static final int INNER_TYPE_DISPLAY_STRING = 1;
   private static final int INNER_TYPE_SUBJECT_SUFFIX = 2;
   private static final int INNER_TYPE_MINIMUM_ACTIONS = 3;

   public static MessageClassification[] getMessageClassifications() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: bipush 24
      // 02: bipush 56
      // 04: invokestatic net/rim/device/api/itpolicy/ITPolicy.getByteArray (II)[B
      // 07: astore 0
      // 08: aload 0
      // 09: ifnonnull 1c
      // 0c: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 0f: ldc2_w -4543606409829069159
      // 12: invokevirtual net/rim/device/api/system/ApplicationRegistry.get (J)Ljava/lang/Object;
      // 15: checkcast [Lnet/rim/device/apps/internal/blackberryemail/classification/MessageClassification;
      // 18: checkcast [Lnet/rim/device/apps/internal/blackberryemail/classification/MessageClassification;
      // 1b: areturn
      // 1c: bipush 0
      // 1d: anewarray 59
      // 20: astore 1
      // 21: new java/lang/Object
      // 24: dup
      // 25: aload 0
      // 26: bipush 0
      // 27: aload 0
      // 28: arraylength
      // 29: bipush 1
      // 2a: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 2d: astore 2
      // 2e: aload 2
      // 2f: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 32: istore 3
      // 33: iload 3
      // 34: lookupswitch 47 1 1 20
      // 48: aload 2
      // 49: invokevirtual net/rim/device/api/util/DataBuffer.readByteArray ()[B
      // 4c: astore 4
      // 4e: aload 4
      // 50: invokestatic net/rim/device/apps/internal/blackberryemail/classification/MessageClassification.getMessageClassification ([B)Lnet/rim/device/apps/internal/blackberryemail/classification/MessageClassification;
      // 53: astore 5
      // 55: aload 5
      // 57: ifnull 2e
      // 5a: aload 1
      // 5b: aload 5
      // 5d: invokestatic net/rim/device/api/util/Arrays.add ([Ljava/lang/Object;Ljava/lang/Object;)V
      // 60: goto 2e
      // 63: aload 2
      // 64: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 67: istore 6
      // 69: aload 2
      // 6a: iload 6
      // 6c: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 6f: pop
      // 70: goto 2e
      // 73: astore 3
      // 74: aload 1
      // 75: areturn
      // 76: astore 3
      // 77: aload 1
      // 78: areturn
      // try (24 -> 49): 49 null
      // try (24 -> 49): 52 null
   }

   private static MessageClassification getMessageClassification(byte[] param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new java/lang/Object
      // 03: dup
      // 04: aload 0
      // 05: bipush 0
      // 06: aload 0
      // 07: arraylength
      // 08: bipush 1
      // 09: invokespecial net/rim/device/api/util/DataBuffer.<init> ([BIIZ)V
      // 0c: astore 1
      // 0d: aconst_null
      // 0e: astore 2
      // 0f: aconst_null
      // 10: astore 3
      // 11: bipush 0
      // 12: istore 4
      // 14: aload 1
      // 15: invokevirtual net/rim/device/api/util/DataBuffer.readByte ()B
      // 18: istore 5
      // 1a: iload 5
      // 1c: tableswitch 32 0 3 95 32 51 70
      // 3c: aload 1
      // 3d: invokevirtual net/rim/device/api/util/DataBuffer.readByteArray ()[B
      // 40: astore 6
      // 42: new java/lang/Object
      // 45: dup
      // 46: aload 6
      // 48: invokespecial java/lang/String.<init> ([B)V
      // 4b: astore 2
      // 4c: goto 14
      // 4f: aload 1
      // 50: invokevirtual net/rim/device/api/util/DataBuffer.readByteArray ()[B
      // 53: astore 7
      // 55: new java/lang/Object
      // 58: dup
      // 59: aload 7
      // 5b: invokespecial java/lang/String.<init> ([B)V
      // 5e: astore 3
      // 5f: goto 14
      // 62: aload 1
      // 63: invokevirtual net/rim/device/api/util/DataBuffer.readByteArray ()[B
      // 66: astore 8
      // 68: aload 8
      // 6a: arraylength
      // 6b: ifle 14
      // 6e: aload 8
      // 70: bipush 0
      // 71: baload
      // 72: sipush 255
      // 75: iand
      // 76: istore 4
      // 78: goto 14
      // 7b: aload 1
      // 7c: invokevirtual net/rim/device/api/util/DataBuffer.readCompressedInt ()I
      // 7f: istore 9
      // 81: aload 1
      // 82: iload 9
      // 84: invokevirtual net/rim/device/api/util/DataBuffer.skipBytes (I)I
      // 87: pop
      // 88: goto 14
      // 8b: astore 5
      // 8d: goto 92
      // 90: astore 5
      // 92: aload 2
      // 93: ifnonnull 98
      // 96: aconst_null
      // 97: areturn
      // 98: new net/rim/device/apps/internal/blackberryemail/classification/MessageClassification
      // 9b: dup
      // 9c: aload 2
      // 9d: aload 3
      // 9e: iload 4
      // a0: invokespecial net/rim/device/apps/internal/blackberryemail/classification/MessageClassification.<init> (Ljava/lang/String;Ljava/lang/String;I)V
      // a3: areturn
      // try (15 -> 59): 59 null
      // try (15 -> 59): 61 null
   }

   public MessageClassification(String displayString, String subjectSuffix, int minimumEncodingActions) {
      if (displayString == null) {
         throw new Object();
      }

      this._displayString = displayString;
      this._subjectSuffix = subjectSuffix;
      this._minimumEncodingActions = minimumEncodingActions;
   }

   @Override
   public String toString() {
      return this._displayString;
   }

   public boolean matchesSubject(String subject) {
      if (subject != null && this._subjectSuffix != null) {
         String trimmedSubject = subject.trim();
         StringTokenizer tokenizer = (StringTokenizer)(new Object(this._subjectSuffix, '/'));

         while (tokenizer.hasMoreTokens()) {
            if (trimmedSubject.endsWith(tokenizer.nextToken())) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public String addSubjectIndicator(String originalSubject) {
      if (this._subjectSuffix == null) {
         return originalSubject;
      }

      StringBuffer stringBuffer = (StringBuffer)(new Object());
      if (originalSubject != null) {
         stringBuffer.append(originalSubject.trim());
         stringBuffer.append(' ');
      }

      stringBuffer.append(this._subjectSuffix);
      return stringBuffer.toString();
   }

   public String removeSubjectIndicator(String originalSubject) {
      if (originalSubject != null && this._subjectSuffix != null) {
         String trimmedSubject = originalSubject.trim();
         int currentSubjectLength = trimmedSubject.length();
         StringTokenizer tokenizer = (StringTokenizer)(new Object(this._subjectSuffix, '/'));
         int numSubjectSuffixTokens = tokenizer.countTokens();
         String[] subjectSuffixTokens = new Object[numSubjectSuffixTokens];

         for (int i = 0; i < numSubjectSuffixTokens; i++) {
            subjectSuffixTokens[i] = tokenizer.nextToken();
         }

         label33:
         while (true) {
            for (int i = 0; i < numSubjectSuffixTokens; i++) {
               String currentToken = subjectSuffixTokens[i];
               int currentTokenLength = currentToken.length();

               for (int numTrailingCharsToIgnore = 0; numTrailingCharsToIgnore < 2; numTrailingCharsToIgnore++) {
                  int testTokenStart = currentSubjectLength - (currentTokenLength + numTrailingCharsToIgnore);
                  if (trimmedSubject.regionMatches(false, testTokenStart, currentToken, 0, currentTokenLength)) {
                     currentSubjectLength = testTokenStart;
                     continue label33;
                  }
               }
            }

            String subjectNoSuffix = trimmedSubject.substring(0, currentSubjectLength);
            return subjectNoSuffix.trim();
         }
      } else {
         return originalSubject;
      }
   }

   public boolean matchesBodyPrefix(String bodyPrefix) {
      if (bodyPrefix == null) {
         return false;
      }

      StringTokenizer tokenizer = (StringTokenizer)(new Object(this.toString(), '/'));

      while (tokenizer.hasMoreTokens()) {
         StringMatch stringMatch = (StringMatch)(new Object(tokenizer.nextToken()));
         if (stringMatch.indexOf(bodyPrefix) >= 0) {
            return true;
         }
      }

      return false;
   }

   public String addBodyIndicator(String originalBody) {
      String title = ITPolicy.getString(24, 81);
      if (title == null) {
         title = EmailResources.getString(110);
      }

      StringBuffer stringBuffer = (StringBuffer)(new Object(title));
      stringBuffer.append(this.toString());
      stringBuffer.append('\n');
      if (originalBody != null && originalBody.length() > 0) {
         stringBuffer.append('\n');
         stringBuffer.append(originalBody);
      }

      return stringBuffer.toString();
   }

   public int getMinimumEncodingActions() {
      return this._minimumEncodingActions;
   }

   @Override
   public boolean equals(Object other) {
      return other instanceof MessageClassification ? this._displayString.equals(((MessageClassification)other)._displayString) : false;
   }

   @Override
   public int hashCode() {
      return this._displayString.hashCode();
   }
}
