package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;
import net.rim.device.internal.io.file.FileUtilities;

public final class AttachVoiceNoteVerb extends Verb {
   private MMSPresentationModel _presentation;
   private static final String MMS_VOICENOTE_FOLDER_DEVICEMEMORY;
   private static final String MMS_VOICENOTE_FOLDER_MEDICARD;
   private static final String AMR_MIME_TYPE;

   public AttachVoiceNoteVerb(MMSPresentationModel presentation) {
      super(16864080);
      this._presentation = presentation;
   }

   @Override
   public final String toString() {
      return MMSResources.getString(123);
   }

   @Override
   public final Object invoke(Object context) {
      MMSAttachment attachment = this.promptForVoiceNote();
      if (attachment != null) {
         this._presentation.addPresentationElement(attachment, true);
         if (this._presentation instanceof Object) {
            Manager mgr = (Manager)this._presentation;
            mgr.setDirty(true);
         }
      }

      return null;
   }

   private final boolean recordingToMediaCard() {
      String location = FileUtilities.getDefaultPathForMIMEType("audio/amr");
      return location.toLowerCase().startsWith("file:///sdcard");
   }

   private final MMSAttachment promptForVoiceNote() {
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
      // 000: new java/lang/Object
      // 003: dup
      // 004: invokespecial net/rim/device/apps/api/framework/model/ContextObject.<init> ()V
      // 007: astore 1
      // 008: aload 1
      // 009: bipush 39
      // 00b: invokevirtual net/rim/device/apps/api/framework/model/ContextObject.setFlag (I)V
      // 00e: aload 1
      // 00f: ldc2_w 3941043584844673548
      // 012: new java/lang/Object
      // 015: dup
      // 016: bipush 5
      // 018: invokespecial java/lang/Integer.<init> (I)V
      // 01b: invokestatic net/rim/device/apps/api/framework/model/ContextObject.put (Ljava/lang/Object;JLjava/lang/Object;)Ljava/lang/Object;
      // 01e: pop
      // 01f: aload 0
      // 020: invokespecial net/rim/device/apps/internal/mms/verbs/AttachVoiceNoteVerb.recordingToMediaCard ()Z
      // 023: ifeq 02d
      // 026: ldc_w "file:///SDCard/BlackBerry/voicenotes/"
      // 029: astore 2
      // 02a: goto 031
      // 02d: ldc_w "file:///store/home/user/voicenotes/"
      // 030: astore 2
      // 031: new java/lang/Object
      // 034: dup
      // 035: aload 2
      // 036: aload 1
      // 037: bipush 2
      // 039: aconst_null
      // 03a: invokespecial net/rim/device/apps/api/framework/file/FileSelector.<init> (Ljava/lang/String;Ljava/lang/Object;ILnet/rim/device/apps/api/framework/model/Recognizer;)V
      // 03d: astore 3
      // 03e: aload 3
      // 03f: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.onlySelectForwardUnlocked ()V
      // 042: aload 3
      // 043: aload 2
      // 044: invokevirtual net/rim/device/apps/api/framework/file/FileSelector.selectFile (Ljava/lang/String;)Ljava/lang/String;
      // 047: astore 4
      // 049: aload 4
      // 04b: ifnonnull 051
      // 04e: goto 100
      // 051: aconst_null
      // 052: astore 5
      // 054: aload 4
      // 056: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 059: astore 4
      // 05b: aload 4
      // 05d: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 060: checkcast java/lang/Object
      // 063: astore 5
      // 065: aload 5
      // 067: invokeinterface javax/microedition/io/file/FileConnection.canRead ()Z 1
      // 06c: ifeq 0c4
      // 06f: new java/lang/Object
      // 072: dup
      // 073: aload 4
      // 075: invokevirtual java/lang/String.getBytes ()[B
      // 078: invokespecial java/lang/String.<init> ([B)V
      // 07b: astore 4
      // 07d: aload 4
      // 07f: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 082: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.getMIMEType (Ljava/lang/String;)I
      // 085: istore 6
      // 087: aload 5
      // 089: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 08e: lstore 7
      // 090: lload 7
      // 092: ldc_w 2147483647
      // 095: i2l
      // 096: lcmp
      // 097: ifge 0c4
      // 09a: new net/rim/device/apps/internal/mms/model/FileAttachment
      // 09d: dup
      // 09e: aload 4
      // 0a0: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 0a3: iload 6
      // 0a5: aload 4
      // 0a7: lload 7
      // 0a9: l2i
      // 0aa: aconst_null
      // 0ab: invokespecial net/rim/device/apps/internal/mms/model/FileAttachment.<init> (Ljava/lang/String;ILjava/lang/String;ILjava/lang/String;)V
      // 0ae: astore 9
      // 0b0: aload 5
      // 0b2: ifnull 0c1
      // 0b5: aload 5
      // 0b7: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0bc: goto 0c1
      // 0bf: astore 10
      // 0c1: aload 9
      // 0c3: areturn
      // 0c4: aload 5
      // 0c6: ifnull 100
      // 0c9: aload 5
      // 0cb: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0d0: aconst_null
      // 0d1: areturn
      // 0d2: astore 6
      // 0d4: aconst_null
      // 0d5: areturn
      // 0d6: astore 6
      // 0d8: aload 5
      // 0da: ifnull 100
      // 0dd: aload 5
      // 0df: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0e4: aconst_null
      // 0e5: areturn
      // 0e6: astore 6
      // 0e8: aconst_null
      // 0e9: areturn
      // 0ea: astore 11
      // 0ec: aload 5
      // 0ee: ifnull 0fd
      // 0f1: aload 5
      // 0f3: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 0f8: goto 0fd
      // 0fb: astore 12
      // 0fd: aload 11
      // 0ff: athrow
      // 100: aconst_null
      // 101: areturn
      // try (83 -> 85): 86 null
      // try (91 -> 93): 95 null
      // try (42 -> 81): 98 null
      // try (101 -> 103): 105 null
      // try (42 -> 81): 108 null
      // try (98 -> 99): 108 null
      // try (111 -> 113): 114 null
      // try (108 -> 109): 108 null
   }
}
