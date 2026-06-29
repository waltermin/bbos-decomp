package net.rim.device.apps.internal.explorer.player;

import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.internal.media.MediaPlayerState;

public final class RenderScreen extends AppsMainScreen {
   private UiApplication _app;
   private BrowserContent _content;
   private Field _field;
   private Field _banner;
   private boolean _contentLoaded;
   private static final Tag EXPLORER_BANNER_TAG = Tag.create("explorer-banner");
   private static final Tag MEDIAPLAYER_BANNER_TAG = Tag.create("mediaplayer-banner");

   public RenderScreen(UiApplication app, Object context, InputConnection inputConnection) {
      super(3459327463773962240L);
      this._app = app;
      this.init(context, inputConnection);
   }

   private final void init(Object param1, InputConnection param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: bipush 0
      // 002: putfield net/rim/device/apps/internal/explorer/player/RenderScreen._contentLoaded Z
      // 005: aload 2
      // 006: ifnonnull 00c
      // 009: goto 153
      // 00c: aconst_null
      // 00d: astore 3
      // 00e: invokestatic net/rim/device/api/browser/field/RenderingSession.getNewInstance ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 011: astore 4
      // 013: aload 4
      // 015: ifnonnull 01b
      // 018: goto 153
      // 01b: aload 4
      // 01d: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 020: astore 5
      // 022: aload 5
      // 024: ldc2_w 4550690918222697397
      // 027: bipush 40
      // 029: bipush 0
      // 02a: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 02d: bipush 0
      // 02e: istore 6
      // 030: aload 2
      // 031: dup
      // 032: instanceof javax/microedition/io/HttpConnection
      // 035: ifne 03c
      // 038: pop
      // 039: goto 04c
      // 03c: checkcast javax/microedition/io/HttpConnection
      // 03f: invokeinterface javax/microedition/io/ContentConnection.getType ()Ljava/lang/String; 1
      // 044: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMediaTypeFromMIMEType (Ljava/lang/String;)I
      // 047: istore 6
      // 049: goto 081
      // 04c: aload 2
      // 04d: dup
      // 04e: instanceof javax/microedition/io/file/FileConnection
      // 051: ifne 058
      // 054: pop
      // 055: goto 081
      // 058: checkcast javax/microedition/io/file/FileConnection
      // 05b: invokeinterface javax/microedition/io/file/FileConnection.getURL ()Ljava/lang/String; 1
      // 060: astore 3
      // 061: aload 3
      // 062: invokestatic net/rim/device/api/io/MIMETypeAssociations.getMediaType (Ljava/lang/String;)I
      // 065: istore 6
      // 067: aload 1
      // 068: ldc2_w -4656037551219504382
      // 06b: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 06e: astore 7
      // 070: aload 7
      // 072: ifnull 081
      // 075: aload 5
      // 077: ldc2_w 9094571315961484757
      // 07a: bipush 2
      // 07c: aload 7
      // 07e: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JILjava/lang/Object;)V
      // 081: iload 6
      // 083: lookupswitch 159 3 2 33 3 33 7 33
      // 0a4: aload 1
      // 0a5: ldc2_w -4054673099568009991
      // 0a8: invokestatic net/rim/device/apps/api/framework/model/ContextObject.get (Ljava/lang/Object;J)Ljava/lang/Object;
      // 0ab: checkcast java/lang/Integer
      // 0ae: astore 7
      // 0b0: aload 7
      // 0b2: ifnonnull 0b9
      // 0b5: bipush 0
      // 0b6: goto 0be
      // 0b9: aload 7
      // 0bb: invokevirtual java/lang/Integer.intValue ()I
      // 0be: istore 8
      // 0c0: aload 5
      // 0c2: ldc2_w 9094571315961484757
      // 0c5: bipush 3
      // 0c7: iload 8
      // 0c9: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JII)V
      // 0cc: aload 1
      // 0cd: bipush 39
      // 0cf: invokestatic net/rim/device/apps/api/framework/model/ContextObject.getFlag (Ljava/lang/Object;I)Z
      // 0d2: istore 9
      // 0d4: aload 5
      // 0d6: ldc2_w 9094571315961484757
      // 0d9: bipush 4
      // 0db: iload 9
      // 0dd: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 0e0: aload 5
      // 0e2: ldc2_w 9094571315961484757
      // 0e5: bipush 5
      // 0e7: bipush 1
      // 0e8: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 0eb: aload 0
      // 0ec: invokestatic net/rim/device/apps/api/ribbon/RibbonBanner.getInstance ()Lnet/rim/device/apps/api/ribbon/RibbonBanner;
      // 0ef: ldc_w ""
      // 0f2: bipush 3
      // 0f4: invokevirtual net/rim/device/apps/api/ribbon/RibbonBanner.getStatusBanner (Ljava/lang/String;I)Lnet/rim/device/api/ui/Field;
      // 0f7: putfield net/rim/device/apps/internal/explorer/player/RenderScreen._banner Lnet/rim/device/api/ui/Field;
      // 0fa: getstatic net/rim/device/apps/internal/explorer/player/RenderScreen.MEDIAPLAYER_BANNER_TAG Lnet/rim/device/api/ui/theme/Tag;
      // 0fd: ifnonnull 10d
      // 100: aload 0
      // 101: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._banner Lnet/rim/device/api/ui/Field;
      // 104: getstatic net/rim/device/apps/internal/explorer/player/RenderScreen.EXPLORER_BANNER_TAG Lnet/rim/device/api/ui/theme/Tag;
      // 107: invokevirtual net/rim/device/api/ui/Field.setTag (Lnet/rim/device/api/ui/theme/Tag;)V
      // 10a: goto 117
      // 10d: aload 0
      // 10e: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._banner Lnet/rim/device/api/ui/Field;
      // 111: getstatic net/rim/device/apps/internal/explorer/player/RenderScreen.MEDIAPLAYER_BANNER_TAG Lnet/rim/device/api/ui/theme/Tag;
      // 114: invokevirtual net/rim/device/api/ui/Field.setTag (Lnet/rim/device/api/ui/theme/Tag;)V
      // 117: aload 0
      // 118: invokevirtual net/rim/device/api/ui/Screen.getDelegate ()Lnet/rim/device/api/ui/Manager;
      // 11b: aload 0
      // 11c: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._banner Lnet/rim/device/api/ui/Field;
      // 11f: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 122: aload 0
      // 123: aload 4
      // 125: aload 2
      // 126: aconst_null
      // 127: new net/rim/device/apps/internal/explorer/player/RenderApp
      // 12a: dup
      // 12b: aload 0
      // 12c: aload 3
      // 12d: invokespecial net/rim/device/apps/internal/explorer/player/RenderApp.<init> (Lnet/rim/device/apps/internal/explorer/player/RenderScreen;Ljava/lang/String;)V
      // 130: bipush 0
      // 131: invokevirtual net/rim/device/api/browser/field/RenderingSession.getBrowserContent (Ljavax/microedition/io/InputConnection;Ljava/lang/String;Lnet/rim/device/api/browser/field/RenderingApplication;I)Lnet/rim/device/api/browser/field/BrowserContent;
      // 134: putfield net/rim/device/apps/internal/explorer/player/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 137: aload 0
      // 138: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 13b: ifnull 153
      // 13e: aload 0
      // 13f: aload 0
      // 140: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 143: invokeinterface net/rim/device/api/browser/field/BrowserContent.getDisplayableContent ()Lnet/rim/device/api/ui/Field; 1
      // 148: putfield net/rim/device/apps/internal/explorer/player/RenderScreen._field Lnet/rim/device/api/ui/Field;
      // 14b: goto 153
      // 14e: astore 3
      // 14f: goto 153
      // 152: astore 3
      // 153: aload 0
      // 154: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._field Lnet/rim/device/api/ui/Field;
      // 157: ifnonnull 162
      // 15a: aload 0
      // 15b: aload 0
      // 15c: invokespecial net/rim/device/apps/internal/explorer/player/RenderScreen.createRenderErrorField ()Lnet/rim/device/api/ui/Field;
      // 15f: putfield net/rim/device/apps/internal/explorer/player/RenderScreen._field Lnet/rim/device/api/ui/Field;
      // 162: aload 0
      // 163: invokevirtual net/rim/device/api/ui/Screen.getDelegate ()Lnet/rim/device/api/ui/Manager;
      // 166: aload 0
      // 167: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._field Lnet/rim/device/api/ui/Field;
      // 16a: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 16d: return
      // try (6 -> 132): 133 null
      // try (6 -> 132): 135 null
   }

   final void loadContent() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._contentLoaded Z
      // 04: ifne 3c
      // 07: aload 0
      // 08: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 0b: ifnull 3c
      // 0e: bipush 0
      // 0f: istore 1
      // 10: aload 0
      // 11: getfield net/rim/device/apps/internal/explorer/player/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 14: invokeinterface net/rim/device/api/browser/field/BrowserContent.finishLoading ()V 1
      // 19: goto 25
      // 1c: astore 2
      // 1d: bipush 1
      // 1e: istore 1
      // 1f: goto 25
      // 22: astore 2
      // 23: bipush 1
      // 24: istore 1
      // 25: iload 1
      // 26: ifeq 37
      // 29: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 2c: new net/rim/device/apps/internal/explorer/player/RenderScreen$1
      // 2f: dup
      // 30: aload 0
      // 31: invokespecial net/rim/device/apps/internal/explorer/player/RenderScreen$1.<init> (Lnet/rim/device/apps/internal/explorer/player/RenderScreen;)V
      // 34: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 37: aload 0
      // 38: bipush 1
      // 39: putfield net/rim/device/apps/internal/explorer/player/RenderScreen._contentLoaded Z
      // 3c: return
      // try (8 -> 11): 12 null
      // try (8 -> 11): 16 null
   }

   private final Field createRenderErrorField() {
      return new RichTextField("Unable to play content.", 36028797018963968L);
   }

   public final int getContentWindowWidth() {
      return Display.getWidth();
   }

   public final int getContentWindowHeight() {
      int height = Display.getHeight();
      if (this._banner != null) {
         height -= this._banner.getPreferredHeight();
      }

      return height;
   }

   public final void finishLoadingFile() {
      Thread t = new RenderScreen$2(this);
      t.start();
   }

   @Override
   public final void close() {
      super.close();
      System.exit(0);
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(width, height);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
   }

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time)) {
         return true;
      }

      c = CharacterUtilities.toLowerCase(c);
      if (c == 27) {
         if (!MediaPlayerState.isMediaPlayerPlaying() && !MediaPlayerState.isMediaPlayerPaused()) {
            this.close();
            return true;
         } else {
            this._app.requestBackground();
            return true;
         }
      } else {
         return false;
      }
   }
}
