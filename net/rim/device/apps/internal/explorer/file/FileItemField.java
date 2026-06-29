package net.rim.device.apps.internal.explorer.file;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.io.FileInfo;
import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.DrawTextParam;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.icons.FileIcon;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerIcons;
import net.rim.device.apps.internal.explorer.file.util.BitmapCache;
import net.rim.device.internal.io.file.FileIndexService;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.ui.Image;
import net.rim.vm.Array;

public class FileItemField implements FileConnectionHolder, VerbProvider {
   private String _path;
   private String _filename;
   private String _displayName;
   private int _attribs;
   private String _sizeString;
   private long _lastModified;

   void setParent() {
      this._attribs |= 1024;
   }

   @Override
   public Verb getVerbs(Object context, Verb[] verbs) {
      Verb defaultVerb = null;
      if (!this.isDirectory()) {
         VerbRepository verbRepository = VerbRepository.getVerbRepository(-2843135760572915788L);
         Verb[] fileVerbs = verbRepository.getVerbs(this.getObjectType());
         if (fileVerbs != null && fileVerbs.length > 0 && !ContextObject.getFlag(context, 45)) {
            defaultVerb = fileVerbs[0];
         }

         int insertIndex = verbs.length;
         Array.resize(verbs, verbs.length + fileVerbs.length);

         for (int i = 0; i < fileVerbs.length; i++) {
            verbs[insertIndex++] = fileVerbs[i];
         }
      }

      return defaultVerb;
   }

   void refreshFileAttributes() {
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
      // 03: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 06: astore 1
      // 07: aload 1
      // 08: ifnull 23
      // 0b: aload 1
      // 0c: invokeinterface javax/microedition/io/file/FileConnection.canWrite ()Z 1
      // 11: ifeq 23
      // 14: aload 0
      // 15: aload 0
      // 16: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 19: ldc_w 262144
      // 1c: ior
      // 1d: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 20: goto 2f
      // 23: aload 0
      // 24: aload 0
      // 25: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 28: ldc_w -262145
      // 2b: iand
      // 2c: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 2f: aload 1
      // 30: ifnull 5b
      // 33: aload 1
      // 34: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 39: return
      // 3a: astore 2
      // 3b: return
      // 3c: astore 2
      // 3d: aload 1
      // 3e: ifnull 5b
      // 41: aload 1
      // 42: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 47: return
      // 48: astore 2
      // 49: return
      // 4a: astore 3
      // 4b: aload 1
      // 4c: ifnull 59
      // 4f: aload 1
      // 50: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 55: aload 3
      // 56: athrow
      // 57: astore 4
      // 59: aload 3
      // 5a: athrow
      // 5b: return
      // try (25 -> 27): 28 null
      // try (2 -> 23): 30 null
      // try (33 -> 35): 36 null
      // try (2 -> 23): 38 null
      // try (30 -> 31): 38 null
      // try (41 -> 43): 45 null
      // try (38 -> 39): 38 null
   }

   void refreshDetailInfo() {
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
      // 03: ldc_w ""
      // 06: putfield net/rim/device/apps/internal/explorer/file/FileItemField._sizeString Ljava/lang/String;
      // 09: aload 0
      // 0a: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 0d: astore 1
      // 0e: aload 1
      // 0f: ifnull 29
      // 12: aload 1
      // 13: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 18: lstore 2
      // 19: lload 2
      // 1a: bipush 0
      // 1b: i2l
      // 1c: lcmp
      // 1d: iflt 29
      // 20: aload 0
      // 21: lload 2
      // 22: bipush 0
      // 23: invokestatic net/rim/device/internal/io/file/FileUtilities.sizeToString (JI)Ljava/lang/String;
      // 26: putfield net/rim/device/apps/internal/explorer/file/FileItemField._sizeString Ljava/lang/String;
      // 29: aload 1
      // 2a: ifnull 58
      // 2d: aload 1
      // 2e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 33: return
      // 34: astore 2
      // 35: return
      // 36: astore 2
      // 37: aload 1
      // 38: ifnull 58
      // 3b: aload 1
      // 3c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 41: return
      // 42: astore 2
      // 43: return
      // 44: astore 4
      // 46: aload 1
      // 47: ifnull 55
      // 4a: aload 1
      // 4b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 50: goto 55
      // 53: astore 5
      // 55: aload 4
      // 57: athrow
      // 58: return
      // try (25 -> 27): 28 null
      // try (5 -> 23): 30 null
      // try (33 -> 35): 36 null
      // try (5 -> 23): 38 null
      // try (30 -> 31): 38 null
      // try (41 -> 43): 44 null
      // try (38 -> 39): 38 null
   }

   public void paint(
      Graphics graphics,
      EncodedImage thumb,
      int width,
      int height,
      int thumbnailWidth,
      int thumbnailHeight,
      String title,
      Long duration,
      int mode,
      BitmapCache bitmapCache,
      int rightColumnWidth
   ) {
      Image icon = this.getImage(thumbnailHeight, graphics);
      if (mode == 0 || mode == 3) {
         boolean thumbDrawn = false;

         label121:
         try {
            if (thumb != null) {
               int reductionFactorY = Fixed32.div(Fixed32.toFP(thumb.getHeight()), Fixed32.toFP(thumbnailHeight));
               int reductionFactorX = Fixed32.div(Fixed32.toFP(thumb.getWidth()), Fixed32.toFP(thumbnailWidth));
               if (reductionFactorX > reductionFactorY) {
                  thumb.setScaleX32(reductionFactorX);
                  thumb.setScaleY32(reductionFactorX);
               } else {
                  thumb.setScaleX32(reductionFactorY);
                  thumb.setScaleY32(reductionFactorY);
               }

               int borderWidth = width - thumb.getScaledWidth() >> 1;
               int borderHeight = height - thumb.getScaledHeight() >> 1;
               bitmapCache.paint(graphics, thumb, borderWidth, borderHeight, thumb.getScaledWidth(), thumb.getScaledHeight());
               thumbDrawn = true;
               if (mode == 0) {
                  if (this.isDRMForwardLocked()) {
                     Image glyph = ExplorerIcons.getGlyphs().getImage(0);
                     int glyphWidth = glyph.getWidth(16, 16);
                     int glyphHeight = glyph.getHeight(16, 16);
                     int xPos = width - borderWidth + Math.min(1, borderWidth - glyphWidth);
                     glyph.paint(graphics, xPos, Math.max(0, borderHeight - glyphHeight), glyphWidth, glyphHeight);
                  }

                  if (this.isRemovable()) {
                     Image glyph = ExplorerIcons.getGlyphs().getImage(1);
                     int glyphWidth = glyph.getWidth(16, 16);
                     int glyphHeight = glyph.getHeight(16, 16);
                     int xPos = width - borderWidth + Math.min(1, borderWidth - glyphWidth);
                     glyph.paint(graphics, xPos, thumbnailHeight - borderHeight - glyphHeight, glyphWidth, glyphHeight);
                  }
               }
            }
         } finally {
            break label121;
         }

         if (!thumbDrawn) {
            int iconWidth = icon.getWidth(width, thumbnailHeight);
            int iconHeight = icon.getHeight(width, thumbnailHeight);
            icon.paint(graphics, width - iconWidth >> 1, thumbnailHeight - iconHeight >> 1, iconWidth, iconHeight);
            return;
         }
      } else if (mode == 1 || mode == 2) {
         TextMetrics metrics = Ui.getTmpTextMetrics();
         DrawTextParam drawParam = Ui.getTmpDrawTextParam();
         int endPos = -1;
         String displayName = this.getDisplayName();
         if (this.isDirectory()) {
            endPos = displayName.lastIndexOf(47);
         }

         if (endPos == -1) {
            endPos = displayName.length();
         }

         int iconWidth = icon.getWidth(thumbnailWidth, thumbnailHeight);
         int iconHeight = icon.getHeight(thumbnailWidth, thumbnailHeight);
         String lengthString = this._sizeString;
         if (mode == 2) {
            if (title != null) {
               displayName = title;
               endPos = displayName.length();
            }

            if (duration != null) {
               int dur = (int)(duration / 1000000);
               lengthString = Integer.toString(dur / 60) + ':' + NumberUtilities.toString(dur % 60, 10, 2);
            } else {
               lengthString = "";
            }
         }

         boolean drawLength = !this.isDirectory();
         if (drawLength) {
            drawParam.iMaxAdvance = width - rightColumnWidth - iconWidth - 10;
         } else {
            drawParam.iMaxAdvance = width - iconWidth - 5;
         }

         drawParam.iAlignment = 48;
         drawParam.iTruncateWithEllipsis = 2;
         icon.paint(graphics, 0, 0, iconWidth, iconHeight);
         int xPos = iconWidth + 5;
         xPos += graphics.drawText(displayName, 0, endPos, xPos, 0, drawParam, metrics);
         if (drawLength) {
            drawParam.iMaxAdvance = width - xPos - 5;
            drawParam.iAlignment = 5;
            xPos += 5;
            graphics.drawText(lengthString, 0, lengthString.length(), xPos, 0, drawParam, metrics);
         }

         if (metrics.iCharacters < endPos) {
            this._attribs |= 16384;
         } else {
            this._attribs &= -16385;
         }

         Ui.returnTmpDrawTextParam(drawParam);
         Ui.returnTmpTextMetrics(metrics);
      }
   }

   public boolean isAlias() {
      return (this._attribs & 512) != 0;
   }

   public boolean isExecutableAlias() {
      return this.isAlias() && this.isExecutable();
   }

   boolean isParent() {
      return (this._attribs & 1024) != 0;
   }

   boolean hasThumbnailAvailabe() {
      return (this._attribs & 32768) != 0;
   }

   boolean isDRMForwardLocked() {
      return (this.getExtendedAttributes() & 2048) != 0;
   }

   boolean isBuiltIn() {
      return (this.getExtendedAttributes() & 524288) != 0;
   }

   boolean isRemovable() {
      return (this._attribs & 4096) != 0;
   }

   boolean isHidden() {
      return (this._attribs & 65536) != 0;
   }

   public boolean canWrite() {
      return (this._attribs & 262144) != 0;
   }

   protected boolean isExecutable() {
      return false;
   }

   protected Image getImage(int height, Graphics graphics) {
      int attribs = this._attribs;
      int iconRow = (attribs & 6144) >> 11;
      int iconCol = attribs & 0xFF;
      return this.isDirectory() ? ExplorerIcons.getFolderIcon().getImage(iconRow << 16 | 0) : FileIcon.getFileIconImage(iconRow << 16 | iconCol);
   }

   public void setRoot() {
      this._attribs |= 2097152;
   }

   public void setName(String filename) {
      this._filename = filename;
   }

   public String getDisplayName() {
      return this._displayName;
   }

   public boolean isRoot() {
      return (this._attribs & 2097152) != 0;
   }

   public void setPath(String path) {
      this._path = path;
   }

   public long getTimestamp() {
      return this._lastModified;
   }

   public String getFullPath() {
      if (this.isAlias()) {
         return this.getPath();
      }

      StringBuffer sbuf = new StringBuffer(this.getPath());
      sbuf.append(this.getName());
      return sbuf.toString();
   }

   public boolean exists() {
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
      // 03: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 06: astore 1
      // 07: aload 1
      // 08: ifnull 25
      // 0b: aload 1
      // 0c: invokeinterface javax/microedition/io/file/FileConnection.exists ()Z 1
      // 11: ifeq 25
      // 14: bipush 1
      // 15: istore 2
      // 16: aload 1
      // 17: ifnull 23
      // 1a: aload 1
      // 1b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 20: iload 2
      // 21: ireturn
      // 22: astore 3
      // 23: iload 2
      // 24: ireturn
      // 25: aload 1
      // 26: ifnull 58
      // 29: aload 1
      // 2a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 2f: bipush 0
      // 30: ireturn
      // 31: astore 2
      // 32: bipush 0
      // 33: ireturn
      // 34: astore 2
      // 35: aload 1
      // 36: ifnull 58
      // 39: aload 1
      // 3a: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 3f: bipush 0
      // 40: ireturn
      // 41: astore 2
      // 42: bipush 0
      // 43: ireturn
      // 44: astore 4
      // 46: aload 1
      // 47: ifnull 55
      // 4a: aload 1
      // 4b: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 50: goto 55
      // 53: astore 5
      // 55: aload 4
      // 57: athrow
      // 58: bipush 0
      // 59: ireturn
      // try (14 -> 16): 18 null
      // try (23 -> 25): 27 null
      // try (2 -> 12): 30 null
      // try (33 -> 35): 37 null
      // try (2 -> 12): 40 null
      // try (30 -> 31): 40 null
      // try (43 -> 45): 46 null
      // try (40 -> 41): 40 null
   }

   public String getSizeString() {
      return this._sizeString;
   }

   public boolean isDirectory() {
      return (this._attribs & 256) == 256;
   }

   public boolean canView() {
      switch (this.getMediaType()) {
         case 1:
         case 2:
         case 3:
         case 7:
            return true;
         default:
            return false;
      }
   }

   public int getMediaType() {
      return this._attribs & 0xFF;
   }

   @Override
   public String getURL() {
      StringBuffer sbuf = new StringBuffer("file://");
      sbuf.append(this.getPath());
      if (!this.isAlias()) {
         sbuf.append(this.getName());
      }

      return sbuf.toString();
   }

   public boolean canDelete() {
      boolean preCreatedFolder = false;
      if (this.isDirectory()) {
         preCreatedFolder = FileIndexService.isPrecreatedMediaFolder(this.getURL());
      }

      return !preCreatedFolder && (this.getExtendedAttributes() & 655360) == 131072;
   }

   public FileConnection getFileConnection() {
      return (FileConnection)Connector.open(this.getURL());
   }

   @Override
   public String getPath() {
      return this._path;
   }

   @Override
   public String getName() {
      return this._filename;
   }

   public FileItemField(String path, String filename, boolean isLocalizedDirectory) {
      this(path, filename, isLocalizedDirectory, null, null);
   }

   FileItemField(String path, String filename, boolean isLocalizedDirectory, FileInfo fileInfo, FileItemField parent) {
      this(
         path,
         filename,
         path.length() == 1 ? FileUtilities.getDisplayName('/' + filename).substring(1) : FileUtilities.getDisplayName(filename),
         isLocalizedDirectory,
         fileInfo,
         parent
      );
   }

   private int getExtendedAttributes() {
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
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 04: istore 1
      // 05: iload 1
      // 06: ldc_w 1048576
      // 09: iand
      // 0a: ifne 8b
      // 0d: aload 0
      // 0e: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isDirectory ()Z
      // 11: ifne 80
      // 14: aconst_null
      // 15: astore 2
      // 16: aload 0
      // 17: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 1a: astore 2
      // 1b: aload 2
      // 1c: dup
      // 1d: instanceof net/rim/device/api/io/file/ExtendedFileConnection
      // 20: ifne 27
      // 23: pop
      // 24: goto 49
      // 27: checkcast net/rim/device/api/io/file/ExtendedFileConnection
      // 2a: astore 3
      // 2b: aload 3
      // 2c: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentDRMForwardLocked ()Z 1
      // 31: ifeq 3a
      // 34: iload 1
      // 35: sipush 2048
      // 38: ior
      // 39: istore 1
      // 3a: aload 3
      // 3b: invokeinterface net/rim/device/api/io/file/ExtendedFileConnection.isContentBuiltIn ()Z 1
      // 40: ifeq 49
      // 43: iload 1
      // 44: ldc_w 524288
      // 47: ior
      // 48: istore 1
      // 49: aload 2
      // 4a: ifnull 80
      // 4d: aload 2
      // 4e: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 53: goto 80
      // 56: astore 3
      // 57: goto 80
      // 5a: astore 3
      // 5b: aload 2
      // 5c: ifnull 80
      // 5f: aload 2
      // 60: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 65: goto 80
      // 68: astore 3
      // 69: goto 80
      // 6c: astore 4
      // 6e: aload 2
      // 6f: ifnull 7d
      // 72: aload 2
      // 73: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 78: goto 7d
      // 7b: astore 5
      // 7d: aload 4
      // 7f: athrow
      // 80: iload 1
      // 81: ldc_w 1048576
      // 84: ior
      // 85: istore 1
      // 86: aload 0
      // 87: iload 1
      // 88: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 8b: iload 1
      // 8c: ireturn
      // try (39 -> 41): 42 null
      // try (12 -> 37): 44 null
      // try (47 -> 49): 50 null
      // try (12 -> 37): 52 null
      // try (44 -> 45): 52 null
      // try (55 -> 57): 58 null
      // try (52 -> 53): 52 null
   }

   @Override
   public String toString() {
      return this.getFullPath();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (!(obj instanceof FileItemField)) {
         return false;
      }

      FileItemField other = (FileItemField)obj;
      return FileUtilities.filenamesMatch(this.getURL(), other.getURL());
   }

   public FileItemField(String pathAndFilename) {
      this(FileUtilities.getPath(pathAndFilename), FileUtilities.getName(pathAndFilename), false);
   }

   private long getObjectType() {
      long objectType = 0;
      switch (this.getMediaType()) {
         case 1:
            return -753816125826020042L;
         case 2:
         case 7:
            return 8830038681865959882L;
         case 3:
            objectType = -7287235942111338224L;
         default:
            return objectType;
      }
   }

   FileItemField(String param1, String param2, String param3, boolean param4, FileInfo param5, FileItemField param6) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: invokespecial java/lang/Object.<init> ()V
      // 004: aload 0
      // 005: aload 1
      // 006: putfield net/rim/device/apps/internal/explorer/file/FileItemField._path Ljava/lang/String;
      // 009: aload 0
      // 00a: aload 2
      // 00b: putfield net/rim/device/apps/internal/explorer/file/FileItemField._filename Ljava/lang/String;
      // 00e: aload 0
      // 00f: aload 3
      // 010: putfield net/rim/device/apps/internal/explorer/file/FileItemField._displayName Ljava/lang/String;
      // 013: aload 0
      // 014: aload 0
      // 015: getfield net/rim/device/apps/internal/explorer/file/FileItemField._filename Ljava/lang/String;
      // 018: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMediaType (Ljava/lang/String;)I
      // 01b: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 01e: aload 0
      // 01f: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 022: tableswitch 50 -1 7 90 65 50 50 104 104 104 90 104
      // 054: aload 0
      // 055: aload 0
      // 056: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 059: ldc_w 32768
      // 05c: ior
      // 05d: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 060: goto 08a
      // 063: aload 2
      // 064: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMIMEType (Ljava/lang/String;)Ljava/lang/String;
      // 067: invokestatic net/rim/device/internal/io/file/MetaDataProvider.getProvider (Ljava/lang/String;)Lnet/rim/device/internal/io/file/MetaDataProvider;
      // 06a: ifnull 08a
      // 06d: aload 0
      // 06e: aload 0
      // 06f: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 072: ldc_w 32768
      // 075: ior
      // 076: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 079: goto 08a
      // 07c: aload 0
      // 07d: aload 0
      // 07e: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 081: sipush -256
      // 084: iand
      // 085: bipush 0
      // 086: ior
      // 087: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 08a: aload 2
      // 08b: ldc_w "/"
      // 08e: invokevirtual java/lang/String.endsWith (Ljava/lang/String;)Z
      // 091: ifeq 0a0
      // 094: aload 0
      // 095: aload 0
      // 096: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 099: sipush 256
      // 09c: ior
      // 09d: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 0a0: iload 4
      // 0a2: ifeq 0bd
      // 0a5: aload 0
      // 0a6: aload 0
      // 0a7: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 0aa: sipush 768
      // 0ad: ior
      // 0ae: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 0b1: aload 0
      // 0b2: aload 0
      // 0b3: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 0b6: ldc_w -32769
      // 0b9: iand
      // 0ba: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 0bd: aload 1
      // 0be: ifnonnull 0c4
      // 0c1: goto 255
      // 0c4: aload 1
      // 0c5: ldc_w "/SDCard"
      // 0c8: invokevirtual java/lang/String.startsWith (Ljava/lang/String;)Z
      // 0cb: ifne 0e2
      // 0ce: aload 1
      // 0cf: ldc_w "/"
      // 0d2: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0d5: ifeq 0ee
      // 0d8: aload 2
      // 0d9: ldc_w "SDCard/"
      // 0dc: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0df: ifeq 0ee
      // 0e2: aload 0
      // 0e3: aload 0
      // 0e4: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 0e7: sipush 4096
      // 0ea: ior
      // 0eb: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 0ee: aload 0
      // 0ef: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.isExecutable ()Z
      // 0f2: ifeq 0f8
      // 0f5: goto 255
      // 0f8: aconst_null
      // 0f9: astore 7
      // 0fb: bipush -1
      // 0fd: i2l
      // 0fe: lstore 8
      // 100: aload 5
      // 102: ifnull 14f
      // 105: aload 0
      // 106: aload 5
      // 108: invokevirtual net/rim/device/api/io/FileInfo.getLastModified ()J
      // 10b: putfield net/rim/device/apps/internal/explorer/file/FileItemField._lastModified J
      // 10e: aload 5
      // 110: invokevirtual net/rim/device/api/io/FileInfo.getAttributes ()I
      // 113: istore 10
      // 115: iload 10
      // 117: bipush 16
      // 119: iand
      // 11a: ifne 124
      // 11d: aload 5
      // 11f: invokevirtual net/rim/device/api/io/FileInfo.getFileSize ()J
      // 122: lstore 8
      // 124: iload 10
      // 126: bipush 8
      // 128: iand
      // 129: ifeq 138
      // 12c: aload 0
      // 12d: aload 0
      // 12e: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 131: ldc_w 65536
      // 134: ior
      // 135: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 138: iload 10
      // 13a: bipush 2
      // 13c: iand
      // 13d: ifne 1a4
      // 140: aload 0
      // 141: aload 0
      // 142: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 145: ldc_w 262144
      // 148: ior
      // 149: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 14c: goto 1a4
      // 14f: aload 0
      // 150: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 153: astore 7
      // 155: aload 7
      // 157: ifnull 1a4
      // 15a: aload 0
      // 15b: aload 7
      // 15d: invokeinterface javax/microedition/io/file/FileConnection.lastModified ()J 1
      // 162: putfield net/rim/device/apps/internal/explorer/file/FileItemField._lastModified J
      // 165: aload 7
      // 167: invokeinterface javax/microedition/io/file/FileConnection.isDirectory ()Z 1
      // 16c: ifne 178
      // 16f: aload 7
      // 171: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 176: lstore 8
      // 178: aload 7
      // 17a: invokeinterface javax/microedition/io/file/FileConnection.isHidden ()Z 1
      // 17f: ifeq 18e
      // 182: aload 0
      // 183: aload 0
      // 184: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 187: ldc_w 65536
      // 18a: ior
      // 18b: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 18e: aload 7
      // 190: invokeinterface javax/microedition/io/file/FileConnection.canWrite ()Z 1
      // 195: ifeq 1a4
      // 198: aload 0
      // 199: aload 0
      // 19a: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 19d: ldc_w 262144
      // 1a0: ior
      // 1a1: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 1a4: lload 8
      // 1a6: bipush 0
      // 1a7: i2l
      // 1a8: lcmp
      // 1a9: iflt 1b6
      // 1ac: aload 0
      // 1ad: lload 8
      // 1af: bipush 0
      // 1b0: invokestatic net/rim/device/internal/io/file/FileUtilities.sizeToString (JI)Ljava/lang/String;
      // 1b3: putfield net/rim/device/apps/internal/explorer/file/FileItemField._sizeString Ljava/lang/String;
      // 1b6: bipush 0
      // 1b7: istore 10
      // 1b9: aload 6
      // 1bb: ifnonnull 1e7
      // 1be: aload 7
      // 1c0: ifnull 1ca
      // 1c3: aload 7
      // 1c5: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 1ca: aload 1
      // 1cb: invokestatic net/rim/device/internal/io/file/FileUtilities.makeFileURL (Ljava/lang/String;)Ljava/lang/String;
      // 1ce: invokestatic javax/microedition/io/Connector.open (Ljava/lang/String;)Ljavax/microedition/io/Connection;
      // 1d1: checkcast javax/microedition/io/file/FileConnection
      // 1d4: astore 7
      // 1d6: aload 7
      // 1d8: ifnull 1ee
      // 1db: aload 7
      // 1dd: invokeinterface javax/microedition/io/file/FileConnection.canWrite ()Z 1
      // 1e2: istore 10
      // 1e4: goto 1ee
      // 1e7: aload 6
      // 1e9: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.canWrite ()Z
      // 1ec: istore 10
      // 1ee: iload 10
      // 1f0: ifeq 1ff
      // 1f3: aload 0
      // 1f4: aload 0
      // 1f5: getfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 1f8: ldc_w 131072
      // 1fb: ior
      // 1fc: putfield net/rim/device/apps/internal/explorer/file/FileItemField._attribs I
      // 1ff: aload 7
      // 201: ifnull 255
      // 204: aload 7
      // 206: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 20b: goto 255
      // 20e: astore 8
      // 210: goto 255
      // 213: astore 8
      // 215: aload 7
      // 217: ifnull 255
      // 21a: aload 7
      // 21c: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 221: goto 255
      // 224: astore 8
      // 226: goto 255
      // 229: astore 8
      // 22b: aload 7
      // 22d: ifnull 255
      // 230: aload 7
      // 232: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 237: goto 255
      // 23a: astore 8
      // 23c: goto 255
      // 23f: astore 11
      // 241: aload 7
      // 243: ifnull 252
      // 246: aload 7
      // 248: invokeinterface javax/microedition/io/Connection.close ()V 1
      // 24d: goto 252
      // 250: astore 12
      // 252: aload 11
      // 254: athrow
      // 255: aload 0
      // 256: getfield net/rim/device/apps/internal/explorer/file/FileItemField._sizeString Ljava/lang/String;
      // 259: ifnonnull 263
      // 25c: aload 0
      // 25d: ldc_w ""
      // 260: putfield net/rim/device/apps/internal/explorer/file/FileItemField._sizeString Ljava/lang/String;
      // 263: return
      // try (211 -> 213): 214 null
      // try (96 -> 209): 216 null
      // try (219 -> 221): 222 null
      // try (96 -> 209): 224 null
      // try (227 -> 229): 230 null
      // try (96 -> 209): 232 null
      // try (216 -> 217): 232 null
      // try (224 -> 225): 232 null
      // try (235 -> 237): 238 null
      // try (232 -> 233): 232 null
   }
}
