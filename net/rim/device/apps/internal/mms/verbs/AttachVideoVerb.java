package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public final class AttachVideoVerb extends Verb {
   private MMSPresentationModel _presentation;
   private static final String MMS_VIDEO_FOLDER;

   public AttachVideoVerb(MMSPresentationModel presentation) {
      super(16864064);
      this._presentation = presentation;
   }

   @Override
   public final String toString() {
      return MMSResources.getString(122);
   }

   @Override
   public final Object invoke(Object context) {
      MMSAttachment attachment = this.promptForVideo();
      if (attachment != null) {
         this._presentation.addPresentationElement(attachment, true);
         if (this._presentation instanceof Object) {
            Manager mgr = (Manager)this._presentation;
            mgr.setDirty(true);
         }
      }

      return null;
   }

   private final MMSAttachment promptForVideo() {
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
      // 00: new java/lang/Object
      // 03: dup
      // 04: ldc_w "file:///store/samples/mms/videos/"
      // 07: bipush 3
      // 09: invokespecial net/rim/device/apps/api/framework/file/FileSelector.<init> (Ljava/lang/String;I)V
      // 0c: astore 1
      // 0d: aload 1
      // 0e: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.onlySelectForwardUnlocked ()V
      // 11: aload 1
      // 12: ldc_w "file:///store/samples/mms/videos/"
      // 15: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.setSampleFolder (Ljava/lang/String;)V
      // 18: aload 1
      // 19: ldc_w "file:///store/samples/mms/videos/"
      // 1c: invokevirtual net/rim/device/apps/api/framework/file/FileSelector.selectFile (Ljava/lang/String;)Ljava/lang/String;
      // 1f: astore 2
      // 20: aload 2
      // 21: ifnonnull 27
      // 24: goto c6
      // 27: aconst_null
      // 28: astore 3
      // 29: aload 2
      // 2a: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 2d: astore 2
      // 2e: aload 2
      // 2f: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 32: checkcast java/lang/Object
      // 35: astore 3
      // 36: aload 3
      // 37: ifnull 90
      // 3a: aload 3
      // 3b: invokeinterface javax/microedition/io/file/FileConnection.canRead ()Z 1
      // 40: ifeq 90
      // 43: new java/lang/Object
      // 46: dup
      // 47: aload 2
      // 48: invokevirtual java/lang/String.getBytes ()[B
      // 4b: invokespecial java/lang/String.<init> ([B)V
      // 4e: astore 2
      // 4f: aload 2
      // 50: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 53: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.getMIMEType (Ljava/lang/String;)I
      // 56: istore 4
      // 58: aload 3
      // 59: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 5e: lstore 5
      // 60: lload 5
      // 62: ldc_w 2147483647
      // 65: i2l
      // 66: lcmp
      // 67: ifge 90
      // 6a: new net/rim/device/apps/internal/mms/model/FileAttachment
      // 6d: dup
      // 6e: aload 2
      // 6f: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 72: iload 4
      // 74: aload 2
      // 75: lload 5
      // 77: l2i
      // 78: aconst_null
      // 79: invokespecial net/rim/device/apps/internal/mms/model/FileAttachment.<init> (Ljava/lang/String;ILjava/lang/String;ILjava/lang/String;)V
      // 7c: astore 7
      // 7e: aload 3
      // 7f: ifnull 8d
      // 82: aload 3
      // 83: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 88: goto 8d
      // 8b: astore 8
      // 8d: aload 7
      // 8f: areturn
      // 90: aload 3
      // 91: ifnull c6
      // 94: aload 3
      // 95: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 9a: aconst_null
      // 9b: areturn
      // 9c: astore 4
      // 9e: aconst_null
      // 9f: areturn
      // a0: astore 4
      // a2: aload 3
      // a3: ifnull c6
      // a6: aload 3
      // a7: invokeinterface javax/microedition/io/Connection.close ()V 1
      // ac: aconst_null
      // ad: areturn
      // ae: astore 4
      // b0: aconst_null
      // b1: areturn
      // b2: astore 9
      // b4: aload 3
      // b5: ifnull c3
      // b8: aload 3
      // b9: invokeinterface javax/microedition/io/Connection.close ()V 1
      // be: goto c3
      // c1: astore 10
      // c3: aload 9
      // c5: athrow
      // c6: aconst_null
      // c7: areturn
      // try (63 -> 65): 66 null
      // try (71 -> 73): 75 null
      // try (20 -> 61): 78 null
      // try (81 -> 83): 85 null
      // try (20 -> 61): 88 null
      // try (78 -> 79): 88 null
      // try (91 -> 93): 94 null
      // try (88 -> 89): 88 null
   }
}
