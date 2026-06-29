package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public final class AddImageVerb extends Verb {
   private MMSPresentationModel _presentation;
   private static final String MMS_IMAGE_FOLDER = "file:///store/samples/mms/pictures/";

   public AddImageVerb(MMSPresentationModel presentation) {
      super(16864032);
      this._presentation = presentation;
   }

   @Override
   public final String toString() {
      return MMSResources.getString(66);
   }

   @Override
   public final Object invoke(Object context) {
      MMSAttachment attachment = this.promptForImage();
      if (attachment != null) {
         this._presentation.addPresentationElement(attachment, true);
         if (this._presentation instanceof Manager) {
            Manager mgr = (Manager)this._presentation;
            mgr.setDirty(true);
         }
      }

      return null;
   }

   private final MMSAttachment promptForImage() {
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
      // 00: new net/rim/device/apps/api/framework/file/FileSelector
      // 03: dup
      // 04: ldc_w "file:///store/samples/mms/pictures/"
      // 07: bipush 1
      // 08: invokespecial net/rim/device/apps/api/framework/file/FileSelector.<init> (Ljava/lang/String;I)V
      // 0b: astore 1
      // 0c: aload 1
      // 0d: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.onlySelectForwardUnlocked ()V
      // 10: aload 1
      // 11: ldc_w "file:///store/samples/mms/pictures/"
      // 14: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.setSampleFolder (Ljava/lang/String;)V
      // 17: aload 1
      // 18: ldc_w "file:///store/samples/mms/pictures/"
      // 1b: invokevirtual net/rim/device/apps/api/framework/file/FileSelector.selectFile (Ljava/lang/String;)Ljava/lang/String;
      // 1e: astore 2
      // 1f: aload 2
      // 20: ifnonnull 26
      // 23: goto c1
      // 26: aconst_null
      // 27: astore 3
      // 28: aload 2
      // 29: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 2c: astore 2
      // 2d: aload 2
      // 2e: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 31: checkcast javax/microedition/io/file/FileConnection
      // 34: astore 3
      // 35: aload 3
      // 36: invokeinterface javax/microedition/io/file/FileConnection.canRead ()Z 1
      // 3b: ifeq 8b
      // 3e: new java/lang/String
      // 41: dup
      // 42: aload 2
      // 43: invokevirtual java/lang/String.getBytes ()[B
      // 46: invokespecial java/lang/String.<init> ([B)V
      // 49: astore 2
      // 4a: aload 2
      // 4b: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 4e: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.getMIMEType (Ljava/lang/String;)I
      // 51: istore 4
      // 53: aload 3
      // 54: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 59: lstore 5
      // 5b: lload 5
      // 5d: ldc_w 2147483647
      // 60: i2l
      // 61: lcmp
      // 62: ifge 8b
      // 65: new net/rim/device/apps/internal/mms/model/FileAttachment
      // 68: dup
      // 69: aload 2
      // 6a: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 6d: iload 4
      // 6f: aload 2
      // 70: lload 5
      // 72: l2i
      // 73: aconst_null
      // 74: invokespecial net/rim/device/apps/internal/mms/model/FileAttachment.<init> (Ljava/lang/String;ILjava/lang/String;ILjava/lang/String;)V
      // 77: astore 7
      // 79: aload 3
      // 7a: ifnull 88
      // 7d: aload 3
      // 7e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 83: goto 88
      // 86: astore 8
      // 88: aload 7
      // 8a: areturn
      // 8b: aload 3
      // 8c: ifnull c1
      // 8f: aload 3
      // 90: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 95: aconst_null
      // 96: areturn
      // 97: astore 4
      // 99: aconst_null
      // 9a: areturn
      // 9b: astore 4
      // 9d: aload 3
      // 9e: ifnull c1
      // a1: aload 3
      // a2: invokeinterface javax/microedition/io/Connection.close ()V 1
      // a7: aconst_null
      // a8: areturn
      // a9: astore 4
      // ab: aconst_null
      // ac: areturn
      // ad: astore 9
      // af: aload 3
      // b0: ifnull be
      // b3: aload 3
      // b4: invokeinterface javax/microedition/io/Connection.close ()V 1
      // b9: goto be
      // bc: astore 10
      // be: aload 9
      // c0: athrow
      // c1: aconst_null
      // c2: areturn
      // try (61 -> 63): 64 null
      // try (69 -> 71): 73 null
      // try (20 -> 59): 76 null
      // try (79 -> 81): 83 null
      // try (20 -> 59): 86 null
      // try (76 -> 77): 86 null
      // try (89 -> 91): 92 null
      // try (86 -> 87): 86 null
   }
}
