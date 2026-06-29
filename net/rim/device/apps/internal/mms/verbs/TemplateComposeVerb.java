package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.apps.api.framework.file.AliasFileEntry;
import net.rim.device.apps.api.framework.file.ExplorerRegistry;
import net.rim.device.apps.api.framework.file.FileSelector;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.model.MMSMessageModelBuilder;
import net.rim.device.apps.internal.mms.model.PresentationModelFactory;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public class TemplateComposeVerb extends MMSComposeVerb {
   private MMSAttachment _pdu;
   private FileSelector _fileSelector;
   private static final String MMS_IMAGE_FOLDER = "/store/samples/mms/pictures/";
   private static final String MMS_TEMPLATE_FOLDER = "/store/samples/mms/templates/";
   private static Recognizer _templateRecognizer = new TemplateComposeVerb$1();

   static void registerTemplateAlias() {
      ExplorerRegistry registry = ExplorerRegistry.getInstance();
      AliasFileEntry emptyMMSTemplate = new TemplateComposeVerb$2(null, null, MMSResources.getBlankTemplateImage());
      registry.addAlias("/store/samples/mms/templates/", emptyMMSTemplate);
   }

   public TemplateComposeVerb() {
      this._fileSelector = (FileSelector)(new Object("/store/samples/mms/pictures/", 0, _templateRecognizer));
   }

   @Override
   public Object copy() {
      return new TemplateComposeVerb();
   }

   @Override
   public Object invoke(Object context) {
      return !this.promptForTemplate(context) ? null : super.invoke(context);
   }

   @Override
   protected void addMessageBody(MMSMessageModelBuilder builder, Object context) {
      if (this._pdu != null) {
         MMSPresentationModel newPresentation = PresentationModelFactory.createInstance(65536);
         String textAttachmentName = "net_rim_TextEntry";
         builder.addAttachment(textAttachmentName, 3, "");
         newPresentation.addPresentationElement(textAttachmentName, 3, true);
         builder.addAttachment(this._pdu);
         newPresentation.addPresentationElement(this._pdu, false);
         builder.addAttachment(newPresentation);
      } else {
         super.addMessageBody(builder, context);
      }
   }

   private boolean promptForTemplate(Object param1) {
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
      // 000: aload 0
      // 001: aconst_null
      // 002: putfield net/rim/device/apps/internal/mms/verbs/TemplateComposeVerb._pdu Lnet/rim/device/apps/internal/mms/api/MMSAttachment;
      // 005: aconst_null
      // 006: astore 2
      // 007: ldc_w "/store/samples/mms/templates/"
      // 00a: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 00d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 010: checkcast java/lang/Object
      // 013: astore 2
      // 014: aload 2
      // 015: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 01a: ifne 02f
      // 01d: bipush 1
      // 01e: istore 3
      // 01f: aload 2
      // 020: ifnull 02d
      // 023: aload 2
      // 024: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 029: iload 3
      // 02a: ireturn
      // 02b: astore 4
      // 02d: iload 3
      // 02e: ireturn
      // 02f: aload 2
      // 030: ifnull 06a
      // 033: aload 2
      // 034: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 039: goto 06a
      // 03c: astore 3
      // 03d: goto 06a
      // 040: astore 3
      // 041: bipush 1
      // 042: istore 4
      // 044: aload 2
      // 045: ifnull 053
      // 048: aload 2
      // 049: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 04e: goto 053
      // 051: astore 5
      // 053: iload 4
      // 055: ireturn
      // 056: astore 6
      // 058: aload 2
      // 059: ifnull 067
      // 05c: aload 2
      // 05d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 062: goto 067
      // 065: astore 7
      // 067: aload 6
      // 069: athrow
      // 06a: aload 0
      // 06b: getfield net/rim/device/apps/internal/mms/verbs/TemplateComposeVerb._fileSelector Lnet/rim/device/apps/api/framework/file/FileSelector;
      // 06e: ifnonnull 074
      // 071: goto 19b
      // 074: aload 0
      // 075: getfield net/rim/device/apps/internal/mms/verbs/TemplateComposeVerb._fileSelector Lnet/rim/device/apps/api/framework/file/FileSelector;
      // 078: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.onlySelectFile ()V
      // 07b: aload 0
      // 07c: getfield net/rim/device/apps/internal/mms/verbs/TemplateComposeVerb._fileSelector Lnet/rim/device/apps/api/framework/file/FileSelector;
      // 07f: ldc_w "/store/samples/mms/templates/"
      // 082: invokevirtual net/rim/device/apps/api/framework/file/FileSelector.selectFile (Ljava/lang/String;)Ljava/lang/String;
      // 085: astore 3
      // 086: aload 3
      // 087: ifnonnull 08d
      // 08a: goto 19b
      // 08d: aconst_null
      // 08e: astore 4
      // 090: aconst_null
      // 091: astore 2
      // 092: aload 3
      // 093: new java/lang/Object
      // 096: dup
      // 097: ldc_w "/store/samples/mms/templates/"
      // 09a: invokespecial java/lang/StringBuffer.<init> (Ljava/lang/String;)V
      // 09d: bipush 120
      // 09f: invokestatic net/rim/device/apps/internal/mms/resources/MMSResources.getString (I)Ljava/lang/String;
      // 0a2: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0a5: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0a8: invokevirtual java/lang/String.equalsIgnoreCase (Ljava/lang/String;)Z
      // 0ab: ifeq 0d2
      // 0ae: bipush 1
      // 0af: istore 5
      // 0b1: aload 4
      // 0b3: ifnull 0c0
      // 0b6: aload 4
      // 0b8: invokevirtual java/io/InputStream.close ()V
      // 0bb: goto 0c0
      // 0be: astore 6
      // 0c0: aload 2
      // 0c1: ifnull 0cf
      // 0c4: aload 2
      // 0c5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0ca: goto 0cf
      // 0cd: astore 6
      // 0cf: iload 5
      // 0d1: ireturn
      // 0d2: aload 3
      // 0d3: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 0d6: astore 3
      // 0d7: aload 3
      // 0d8: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 0db: checkcast java/lang/Object
      // 0de: astore 2
      // 0df: aload 2
      // 0e0: invokeinterface javax/microedition/io/file/FileConnection.canRead ()Z 1
      // 0e5: ifeq 136
      // 0e8: new java/lang/Object
      // 0eb: dup
      // 0ec: aload 3
      // 0ed: invokevirtual java/lang/String.getBytes ()[B
      // 0f0: invokespecial java/lang/String.<init> ([B)V
      // 0f3: astore 3
      // 0f4: aload 2
      // 0f5: invokeinterface javax/microedition/io/file/FileConnection.openInputStream ()Ljava/io/InputStream; 1
      // 0fa: astore 4
      // 0fc: aload 0
      // 0fd: new net/rim/device/apps/internal/mms/model/MMSAttachmentImpl
      // 100: dup
      // 101: ldc_w "net_rim_Template"
      // 104: bipush 62
      // 106: aload 4
      // 108: invokestatic net/rim/device/api/io/IOUtilities.streamToBytes (Ljava/io/InputStream;)[B
      // 10b: aconst_null
      // 10c: invokespecial net/rim/device/apps/internal/mms/model/MMSAttachmentImpl.<init> (Ljava/lang/String;I[BLjava/lang/String;)V
      // 10f: putfield net/rim/device/apps/internal/mms/verbs/TemplateComposeVerb._pdu Lnet/rim/device/apps/internal/mms/api/MMSAttachment;
      // 112: bipush 1
      // 113: istore 5
      // 115: aload 4
      // 117: ifnull 124
      // 11a: aload 4
      // 11c: invokevirtual java/io/InputStream.close ()V
      // 11f: goto 124
      // 122: astore 6
      // 124: aload 2
      // 125: ifnull 133
      // 128: aload 2
      // 129: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 12e: goto 133
      // 131: astore 6
      // 133: iload 5
      // 135: ireturn
      // 136: aload 4
      // 138: ifnull 145
      // 13b: aload 4
      // 13d: invokevirtual java/io/InputStream.close ()V
      // 140: goto 145
      // 143: astore 5
      // 145: aload 2
      // 146: ifnull 199
      // 149: aload 2
      // 14a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 14f: bipush 1
      // 150: ireturn
      // 151: astore 5
      // 153: bipush 1
      // 154: ireturn
      // 155: astore 5
      // 157: aload 4
      // 159: ifnull 166
      // 15c: aload 4
      // 15e: invokevirtual java/io/InputStream.close ()V
      // 161: goto 166
      // 164: astore 5
      // 166: aload 2
      // 167: ifnull 199
      // 16a: aload 2
      // 16b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 170: bipush 1
      // 171: ireturn
      // 172: astore 5
      // 174: bipush 1
      // 175: ireturn
      // 176: astore 8
      // 178: aload 4
      // 17a: ifnull 187
      // 17d: aload 4
      // 17f: invokevirtual java/io/InputStream.close ()V
      // 182: goto 187
      // 185: astore 9
      // 187: aload 2
      // 188: ifnull 196
      // 18b: aload 2
      // 18c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 191: goto 196
      // 194: astore 9
      // 196: aload 8
      // 198: athrow
      // 199: bipush 1
      // 19a: ireturn
      // 19b: bipush 0
      // 19c: ireturn
      // try (17 -> 19): 21 null
      // try (26 -> 28): 29 null
      // try (5 -> 15): 31 null
      // try (36 -> 38): 39 null
      // try (5 -> 15): 42 null
      // try (31 -> 34): 42 null
      // try (45 -> 47): 48 null
      // try (42 -> 43): 42 null
      // try (85 -> 87): 88 null
      // try (91 -> 93): 94 null
      // try (130 -> 132): 133 null
      // try (136 -> 138): 139 null
      // try (144 -> 146): 147 null
      // try (150 -> 152): 154 null
      // try (70 -> 83): 157 null
      // try (97 -> 128): 157 null
      // try (160 -> 162): 163 null
      // try (166 -> 168): 170 null
      // try (70 -> 83): 173 null
      // try (97 -> 128): 173 null
      // try (157 -> 158): 173 null
      // try (176 -> 178): 179 null
      // try (182 -> 184): 185 null
      // try (173 -> 174): 173 null
   }
}
