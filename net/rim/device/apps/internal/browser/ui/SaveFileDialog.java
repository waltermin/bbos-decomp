package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.framework.file.FileDialog;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.ui.RunnableDialog;
import net.rim.device.apps.internal.browser.resources.BrowserResources;
import net.rim.device.apps.internal.browser.util.MediaUtilities;
import net.rim.device.apps.internal.browser.util.RendererControl;
import net.rim.device.cldc.io.utility.URIDecoder;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.io.file.FileUtilities;

public final class SaveFileDialog extends FileDialog {
   private CheckboxField _checkbox;
   private int _mediaType;

   public SaveFileDialog(String defaultFilename, String contentType, int mimeType, int size) {
      super(
         FileUtilities.getDefaultWritablePathForMIMEType(contentType, size),
         addExtension(defaultFilename, contentType),
         mimeType,
         CommonResource.getString(10078),
         134217728
      );
      switch (mimeType) {
         case 0:
            break;
         case 1:
         default:
            if (Graphics.isColor() && ThemeManager.allowUserWallpaper(ThemeManager.getActiveName())) {
               this.addCheckbox(BrowserResources.getString(733));
            }
            break;
         case 2:
            if (TuneManager.isTuneManagerAvailable()) {
               this.addCheckbox(BrowserResources.getString(787));
            }
      }

      this._mediaType = mimeType;
   }

   private static final String addExtension(String filename, String contentType) {
      if (contentType != null && filename != null) {
         String extension = MIMETypeAssociations.getExtensionFromMIMEType(RendererControl.stripContentTypeParameters(contentType));
         if (extension != null && extension.length() > 0) {
            StringBuffer newFilename = new StringBuffer(filename);
            int currentExtensionIndex = filename.indexOf(46);
            if (currentExtensionIndex != -1) {
               newFilename.setLength(currentExtensionIndex + 1);
            } else {
               newFilename.append('.');
            }

            newFilename.append(extension);
            return newFilename.toString();
         }
      }

      return filename;
   }

   private final void addCheckbox(String label) {
      this._checkbox = new CheckboxField(label, false, 1073741824);
      this.add(this._checkbox);
   }

   @Override
   public final int doModal() {
      int retVal = -1;

      do {
         retVal = super.doModal();
         if (-1 == retVal) {
            return retVal;
         }
      } while (!this.overwriteIfExists());

      return retVal;
   }

   private final boolean overwriteIfExists() {
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

   public final String getFullFilename() {
      String url = this.getURL();
      return url != null ? url.substring(7) : url;
   }

   public final void runPostSaveActions() {
      if (this._checkbox != null && this._checkbox.getChecked()) {
         switch (this._mediaType) {
            case 0:
               break;
            case 1:
            default:
               MediaUtilities.setAsHomeScreenBackground(this.getFullFilename());
               return;
            case 2:
               MediaUtilities.setAsRingtone(this.getFullFilename());
         }
      }
   }

   public static final SaveFileDialog promptToSave(String url, String contentType, long contentLength) {
      boolean stripExtension = false;
      String extension = MIMETypeAssociations.getExtensionFromMIMEType(RendererControl.stripContentTypeParameters(contentType));
      if (extension != null && extension.length() > 0) {
         stripExtension = true;
      }

      SaveFileDialog saveDialog = new SaveFileDialog(
         getFilenameFromURL(url, stripExtension), contentType, MIMETypeAssociations.getMediaTypeFromMIMEType(contentType), (int)contentLength
      );
      if (Application.isEventDispatchThread()) {
         if (saveDialog.doModal() == -1) {
            return null;
         }
      } else {
         RunnableDialog runnableDialog = new RunnableDialog(saveDialog);
         Application.getApplication().invokeAndWait(runnableDialog);
         if (runnableDialog.getResult() == -1) {
            return null;
         }
      }

      return saveDialog;
   }

   public static final String save(String url, String contentType, int mediaType, boolean drmForwardLock, byte[] data) {
      return save(url, contentType, mediaType, drmForwardLock, data, 0, data.length);
   }

   public static final String save(String param0, String param1, int param2, boolean param3, byte[] param4, int param5, int param6) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 4
      // 002: ifnonnull 00d
      // 005: new java/lang/NullPointerException
      // 008: dup
      // 009: invokespecial java/lang/NullPointerException.<init> ()V
      // 00c: athrow
      // 00d: new net/rim/device/apps/internal/browser/ui/SaveFileDialog
      // 010: dup
      // 011: aload 0
      // 012: bipush 1
      // 013: invokestatic net/rim/device/apps/internal/browser/ui/SaveFileDialog.getFilenameFromURL (Ljava/lang/String;Z)Ljava/lang/String;
      // 016: aload 1
      // 017: iload 2
      // 018: iload 6
      // 01a: invokespecial net/rim/device/apps/internal/browser/ui/SaveFileDialog.<init> (Ljava/lang/String;Ljava/lang/String;II)V
      // 01d: astore 7
      // 01f: invokestatic net/rim/device/api/system/Application.isEventDispatchThread ()Z
      // 022: ifeq 031
      // 025: aload 7
      // 027: invokevirtual net/rim/device/apps/internal/browser/ui/SaveFileDialog.doModal ()I
      // 02a: bipush -1
      // 02c: if_icmpne 050
      // 02f: aconst_null
      // 030: areturn
      // 031: new net/rim/device/apps/api/ui/RunnableDialog
      // 034: dup
      // 035: aload 7
      // 037: invokespecial net/rim/device/apps/api/ui/RunnableDialog.<init> (Lnet/rim/device/api/ui/component/Dialog;)V
      // 03a: astore 8
      // 03c: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 03f: aload 8
      // 041: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 044: aload 8
      // 046: invokevirtual net/rim/device/apps/api/ui/RunnableDialog.getResult ()I
      // 049: bipush -1
      // 04b: if_icmpne 050
      // 04e: aconst_null
      // 04f: areturn
      // 050: aconst_null
      // 051: astore 8
      // 053: aconst_null
      // 054: astore 9
      // 056: aload 7
      // 058: invokevirtual net/rim/device/apps/api/framework/file/FileDialog.getURL ()Ljava/lang/String;
      // 05b: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 05e: checkcast javax/microedition/io/file/FileConnection
      // 061: astore 8
      // 063: aload 8
      // 065: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 06a: ifeq 074
      // 06d: aload 8
      // 06f: invokeinterface javax/microedition/io/file/FileConnection.delete ()V 1
      // 074: iload 3
      // 075: ifeq 09e
      // 078: aload 8
      // 07a: dup
      // 07b: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 07e: ifne 085
      // 081: pop
      // 082: goto 09e
      // 085: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 088: astore 10
      // 08a: aload 10
      // 08c: bipush 51
      // 08e: invokestatic net/rim/device/api/system/CodeSigningKey.getBuiltInKey (I)Lnet/rim/device/api/system/CodeSigningKey;
      // 091: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.setControlledAccess (Lnet/rim/device/api/system/CodeSigningKey;)Z 2
      // 096: pop
      // 097: aload 10
      // 099: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.enableDRMForwardLock ()V 1
      // 09e: aload 8
      // 0a0: invokeinterface javax/microedition/io/file/FileConnection.create ()V 1
      // 0a5: aload 8
      // 0a7: invokeinterface javax/microedition/io/file/FileConnection.openOutputStream ()Ljava/io/OutputStream; 1
      // 0ac: astore 9
      // 0ae: aload 9
      // 0b0: aload 4
      // 0b2: iload 5
      // 0b4: iload 6
      // 0b6: invokevirtual java/io/OutputStream.write ([BII)V
      // 0b9: aload 9
      // 0bb: invokevirtual java/io/OutputStream.close ()V
      // 0be: aconst_null
      // 0bf: astore 9
      // 0c1: aload 7
      // 0c3: invokevirtual net/rim/device/apps/internal/browser/ui/SaveFileDialog.runPostSaveActions ()V
      // 0c6: sipush 832
      // 0c9: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 0cc: bipush 2
      // 0ce: anewarray 663
      // 0d1: dup
      // 0d2: bipush 0
      // 0d3: aload 8
      // 0d5: invokeinterface javax/microedition/io/file/FileConnection.getName ()Ljava/lang/String; 1
      // 0da: aastore
      // 0db: dup
      // 0dc: bipush 1
      // 0dd: aload 8
      // 0df: invokeinterface javax/microedition/io/file/FileConnection.getPath ()Ljava/lang/String; 1
      // 0e4: invokestatic net/rim/device/internal/io/file/FileUtilities.getDisplayName (Ljava/lang/String;)Ljava/lang/String;
      // 0e7: aastore
      // 0e8: invokestatic net/rim/device/api/i18n/MessageFormat.format (Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
      // 0eb: astore 10
      // 0ed: invokestatic net/rim/device/api/system/Application.isEventDispatchThread ()Z
      // 0f0: ifeq 0fb
      // 0f3: aload 10
      // 0f5: invokestatic net/rim/device/api/ui/component/Status.show (Ljava/lang/String;)V
      // 0f8: goto 10a
      // 0fb: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 0fe: new net/rim/device/apps/internal/browser/ui/SaveFileDialog$1
      // 101: dup
      // 102: aload 10
      // 104: invokespecial net/rim/device/apps/internal/browser/ui/SaveFileDialog$1.<init> (Ljava/lang/String;)V
      // 107: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 10a: aload 9
      // 10c: ifnull 119
      // 10f: aload 9
      // 111: invokevirtual java/io/OutputStream.close ()V
      // 114: goto 119
      // 117: astore 10
      // 119: aload 8
      // 11b: ifnonnull 121
      // 11e: goto 1b6
      // 121: aload 8
      // 123: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 128: goto 1b6
      // 12b: astore 10
      // 12d: goto 1b6
      // 130: astore 10
      // 132: aload 10
      // 134: invokevirtual net/rim/device/api/io/file/FileIOException.getMessage ()Ljava/lang/String;
      // 137: invokestatic net/rim/device/apps/internal/browser/ui/SaveFileDialog.showAlert (Ljava/lang/String;)V
      // 13a: aconst_null
      // 13b: astore 11
      // 13d: aload 9
      // 13f: ifnull 14c
      // 142: aload 9
      // 144: invokevirtual java/io/OutputStream.close ()V
      // 147: goto 14c
      // 14a: astore 12
      // 14c: aload 8
      // 14e: ifnull 15d
      // 151: aload 8
      // 153: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 158: goto 15d
      // 15b: astore 12
      // 15d: aload 11
      // 15f: areturn
      // 160: astore 10
      // 162: sipush 828
      // 165: invokestatic net/rim/device/apps/internal/browser/resources/BrowserResources.getString (I)Ljava/lang/String;
      // 168: invokestatic net/rim/device/apps/internal/browser/ui/SaveFileDialog.showAlert (Ljava/lang/String;)V
      // 16b: aconst_null
      // 16c: astore 11
      // 16e: aload 9
      // 170: ifnull 17d
      // 173: aload 9
      // 175: invokevirtual java/io/OutputStream.close ()V
      // 178: goto 17d
      // 17b: astore 12
      // 17d: aload 8
      // 17f: ifnull 18e
      // 182: aload 8
      // 184: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 189: goto 18e
      // 18c: astore 12
      // 18e: aload 11
      // 190: areturn
      // 191: astore 13
      // 193: aload 9
      // 195: ifnull 1a2
      // 198: aload 9
      // 19a: invokevirtual java/io/OutputStream.close ()V
      // 19d: goto 1a2
      // 1a0: astore 14
      // 1a2: aload 8
      // 1a4: ifnull 1b3
      // 1a7: aload 8
      // 1a9: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1ae: goto 1b3
      // 1b1: astore 14
      // 1b3: aload 13
      // 1b5: athrow
      // 1b6: aload 7
      // 1b8: invokevirtual net/rim/device/apps/api/framework/file/FileDialog.getURL ()Ljava/lang/String;
      // 1bb: areturn
      // try (115 -> 117): 118 null
      // try (122 -> 124): 125 null
      // try (42 -> 113): 127 null
      // try (135 -> 137): 138 null
      // try (141 -> 143): 144 null
      // try (42 -> 113): 147 null
      // try (155 -> 157): 158 null
      // try (161 -> 163): 164 null
      // try (42 -> 113): 167 null
      // try (127 -> 133): 167 null
      // try (147 -> 153): 167 null
      // try (170 -> 172): 173 null
      // try (176 -> 178): 179 null
      // try (167 -> 168): 167 null
   }

   private static final String getFilenameFromURL(String url, boolean stripExtension) {
      if (url != null && url.length() != 0) {
         int startIndex = 0;
         int endIndex = url.length();
         int queryString = url.indexOf(63);
         if (queryString > 0) {
            endIndex = queryString;
         }

         int lastSlash = url.lastIndexOf(47, endIndex - 1);
         if (lastSlash >= 0) {
            startIndex = lastSlash + 1;
         }

         if (stripExtension) {
            int fileExtension = url.indexOf(46, startIndex);
            if (fileExtension >= startIndex) {
               endIndex = fileExtension;
            }
         }

         return endIndex > startIndex ? FileUtilities.makeValidFilename(URIDecoder.decode(url.substring(startIndex, endIndex), "utf-8", false)) : "";
      } else {
         return "";
      }
   }

   private static final void showAlert(String message) {
      if (Application.isEventDispatchThread()) {
         Dialog.alert(message);
      } else {
         Application.getApplication().invokeAndWait(new RunnableDialog(message, 0));
      }
   }
}
