package net.rim.device.apps.api.framework.file;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.FileUtilities;

public class FileDialog extends Dialog {
   private FilenameEditField _filenameEditField;
   private ButtonField _buttonOk;
   private ButtonField _buttonCancel;
   private boolean _promptForOverwrite;
   public static final long SAVE = 134217728L;

   public FileDialog(String path, String filename, int mediaType, String msg) {
      this(path, filename, mediaType, msg, 0);
   }

   public FileDialog(String path, String filename, int mediaType, String msg, long editStyle) {
      this(path, filename, mediaType, msg, CommonResource.getString(10070), editStyle);
   }

   public FileDialog(String path, String filename, int mediaType, String msg, String okButtonLabel) {
      this(path, filename, mediaType, msg, okButtonLabel, 0);
   }

   public FileDialog(String path, String filename, int mediaType, String msg, String okButtonLabel, long editStyle) {
      super(msg, null, null, 0, null);
      this.setIcon(ThemeManager.getThemeAwareImage("dialog_information"));
      boolean allowPathChange = true;
      if (filename == null) {
         filename = "";
      }

      if (FileUtilities.isDirectory(filename)) {
         filename = filename.substring(0, filename.length() - 1);
         allowPathChange = false;
      }

      this._filenameEditField = new FilenameEditField(path, filename, mediaType, editStyle, allowPathChange);
      this.setEscapeEnabled(false);
      Manager fm = new HorizontalFieldManager();
      this.add(fm);
      this.add(this._filenameEditField);
      this.add(new SeparatorField(65536));
      this._buttonOk = new ButtonField(okButtonLabel);
      this._buttonOk.setChangeListener(this);
      this._buttonCancel = new ButtonField(CommonResource.getString(10044));
      this._buttonCancel.setChangeListener(this);
      fm = new HorizontalFieldManager(12884901888L);
      this.add(fm);
      fm.add(this._buttonOk);
      fm.add(this._buttonCancel);
   }

   public String getFilename() {
      return this._filenameEditField.getFilename();
   }

   public void setPromptForOverwrite(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public String getPath() {
      return this._filenameEditField.getPath();
   }

   public FileSelector getFileSelector() {
      return (FileSelector)this._filenameEditField.getFileSelector();
   }

   @Override
   public int doModal() {
      int retVal = -1;

      do {
         retVal = super.doModal();
         if (-1 == retVal) {
            return retVal;
         }
      } while (this._promptForOverwrite && !this.overwriteIfExists());

      return retVal;
   }

   private boolean overwriteIfExists() {
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
      // 00: aconst_null
      // 01: astore 1
      // 02: aload 0
      // 03: invokevirtual net/rim/device/apps/api/framework/file/FileDialog.getURL ()Ljava/lang/String;
      // 06: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 09: checkcast javax/microedition/io/file/FileConnection
      // 0c: astore 1
      // 0d: aload 1
      // 0e: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 13: ifne 27
      // 16: bipush 1
      // 17: istore 2
      // 18: aload 1
      // 19: ifnull 25
      // 1c: aload 1
      // 1d: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 22: iload 2
      // 23: ireturn
      // 24: astore 3
      // 25: iload 2
      // 26: ireturn
      // 27: aload 1
      // 28: ifnull 5e
      // 2b: aload 1
      // 2c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 31: goto 5e
      // 34: astore 2
      // 35: goto 5e
      // 38: astore 2
      // 39: aload 1
      // 3a: ifnull 5e
      // 3d: aload 1
      // 3e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 43: goto 5e
      // 46: astore 2
      // 47: goto 5e
      // 4a: astore 4
      // 4c: aload 1
      // 4d: ifnull 5b
      // 50: aload 1
      // 51: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 56: goto 5b
      // 59: astore 5
      // 5b: aload 4
      // 5d: athrow
      // 5e: new net/rim/device/api/ui/component/Dialog
      // 61: dup
      // 62: bipush 3
      // 64: sipush 10082
      // 67: aload 0
      // 68: invokevirtual net/rim/device/apps/api/framework/file/FileDialog.getFilename ()Ljava/lang/String;
      // 6b: invokestatic net/rim/device/internal/i18n/CommonResource.format (ILjava/lang/String;)Ljava/lang/String;
      // 6e: bipush 1
      // 6f: aconst_null
      // 70: bipush 0
      // 71: i2l
      // 72: invokespecial net/rim/device/api/ui/component/Dialog.<init> (ILjava/lang/String;ILnet/rim/device/api/system/Bitmap;J)V
      // 75: astore 2
      // 76: aload 2
      // 77: ldc_w "dialog_information"
      // 7a: invokestatic net/rim/device/api/ui/theme/ThemeManager.getThemeAwareImage (Ljava/lang/String;)Lnet/rim/device/internal/ui/Image;
      // 7d: invokevirtual net/rim/device/api/ui/component/Dialog.setIcon (Lnet/rim/device/internal/ui/Image;)V
      // 80: bipush 4
      // 82: aload 2
      // 83: invokevirtual net/rim/device/api/ui/component/Dialog.doModal ()I
      // 86: if_icmpne 8b
      // 89: bipush 1
      // 8a: ireturn
      // 8b: bipush 0
      // 8c: ireturn
      // try (14 -> 16): 18 null
      // try (23 -> 25): 26 null
      // try (2 -> 12): 28 null
      // try (31 -> 33): 34 null
      // try (2 -> 12): 36 null
      // try (28 -> 29): 36 null
      // try (39 -> 41): 42 null
      // try (36 -> 37): 36 null
   }

   public String getURL() {
      String path = this._filenameEditField.getPath();
      StringBuffer sbuf = new StringBuffer();
      if (!path.startsWith("file://")) {
         sbuf.append("file://");
      }

      sbuf.append(path);
      String filename = this.getFilename();
      if (filename != null) {
         sbuf.append(FileUtilities.encodeString(filename));
      }

      return sbuf.toString();
   }

   @Override
   public void fieldChanged(Field field, int context) {
      if (field == this._buttonCancel) {
         this.cancel();
      } else {
         if (field == this._buttonOk) {
            String errorStr = this._filenameEditField.validate();
            if (errorStr == null) {
               this.close();
               return;
            }

            Dialog.inform(errorStr);
         }
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled) {
         switch (key) {
            case '\n':
               String errorStr = this._filenameEditField.validate();
               if (errorStr == null) {
                  this.close();
               } else {
                  Dialog.inform(errorStr);
               }
               break;
            case '\u001b':
               this.cancel();
               break;
            default:
               return false;
         }

         handled = true;
      }

      return handled;
   }
}
