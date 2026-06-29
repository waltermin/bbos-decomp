package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;
import net.rim.device.apps.internal.mms.resources.MMSResources;

public final class AttachAudioVerb extends Verb {
   private MMSPresentationModel _presentation;
   private static final String MMS_AUDIO_FOLDER = "file:///store/samples/mms/tunes/";

   public AttachAudioVerb(MMSPresentationModel presentation) {
      super(16864048);
      this._presentation = presentation;
   }

   @Override
   public final String toString() {
      return MMSResources.getString(67);
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject ctx = ContextObject.castOrCreate(context);
      ctx.setFlag(39);
      MMSAttachment attachment = this.promptForAudio(ctx);
      if (attachment != null) {
         this._presentation.addPresentationElement(attachment, true);
         if (this._presentation instanceof Object) {
            Manager mgr = (Manager)this._presentation;
            mgr.setDirty(true);
         }
      }

      return null;
   }

   private final MMSAttachment promptForAudio(Object param1) {
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
      // 04: ldc_w "file:///store/samples/mms/tunes/"
      // 07: bipush 2
      // 09: invokespecial net/rim/device/apps/api/framework/file/FileSelector.<init> (Ljava/lang/String;I)V
      // 0c: astore 2
      // 0d: aload 2
      // 0e: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.onlySelectForwardUnlocked ()V
      // 11: aload 2
      // 12: ldc_w "file:///store/samples/mms/tunes/"
      // 15: invokevirtual net/rim/device/apps/api/framework/file/FileSelectionFilter.setSampleFolder (Ljava/lang/String;)V
      // 18: aload 2
      // 19: ldc_w "file:///store/samples/mms/tunes/"
      // 1c: invokevirtual net/rim/device/apps/api/framework/file/FileSelector.selectFile (Ljava/lang/String;)Ljava/lang/String;
      // 1f: astore 3
      // 20: aload 3
      // 21: ifnonnull 27
      // 24: goto ce
      // 27: aconst_null
      // 28: astore 4
      // 2a: aload 3
      // 2b: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 2e: astore 3
      // 2f: aload 3
      // 30: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 33: checkcast java/lang/Object
      // 36: astore 4
      // 38: aload 4
      // 3a: invokeinterface javax/microedition/io/file/FileConnection.canRead ()Z 1
      // 3f: ifeq 92
      // 42: new java/lang/Object
      // 45: dup
      // 46: aload 3
      // 47: invokevirtual java/lang/String.getBytes ()[B
      // 4a: invokespecial java/lang/String.<init> ([B)V
      // 4d: astore 3
      // 4e: aload 3
      // 4f: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 52: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.getMIMEType (Ljava/lang/String;)I
      // 55: istore 5
      // 57: aload 4
      // 59: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 5e: lstore 6
      // 60: lload 6
      // 62: ldc_w 2147483647
      // 65: i2l
      // 66: lcmp
      // 67: ifge 92
      // 6a: new net/rim/device/apps/internal/mms/model/FileAttachment
      // 6d: dup
      // 6e: aload 3
      // 6f: invokestatic net/rim/device/internal/io/file/FileUtilities.getName (Ljava/lang/String;)Ljava/lang/String;
      // 72: iload 5
      // 74: aload 3
      // 75: lload 6
      // 77: l2i
      // 78: aconst_null
      // 79: invokespecial net/rim/device/apps/internal/mms/model/FileAttachment.<init> (Ljava/lang/String;ILjava/lang/String;ILjava/lang/String;)V
      // 7c: astore 8
      // 7e: aload 4
      // 80: ifnull 8f
      // 83: aload 4
      // 85: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 8a: goto 8f
      // 8d: astore 9
      // 8f: aload 8
      // 91: areturn
      // 92: aload 4
      // 94: ifnull ce
      // 97: aload 4
      // 99: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 9e: aconst_null
      // 9f: areturn
      // a0: astore 5
      // a2: aconst_null
      // a3: areturn
      // a4: astore 5
      // a6: aload 4
      // a8: ifnull ce
      // ab: aload 4
      // ad: invokeinterface javax/microedition/io/Connection.close ()V 1
      // b2: aconst_null
      // b3: areturn
      // b4: astore 5
      // b6: aconst_null
      // b7: areturn
      // b8: astore 10
      // ba: aload 4
      // bc: ifnull cb
      // bf: aload 4
      // c1: invokeinterface javax/microedition/io/Connection.close ()V 1
      // c6: goto cb
      // c9: astore 11
      // cb: aload 10
      // cd: athrow
      // ce: aconst_null
      // cf: areturn
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
