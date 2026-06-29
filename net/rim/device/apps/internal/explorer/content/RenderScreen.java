package net.rim.device.apps.internal.explorer.content;

import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.browser.field.RenderingSession;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.internal.media.MediaPlayerStateInstance;
import net.rim.device.internal.media.MediaPlayerStateProvider;

public final class RenderScreen extends AppsMainScreen {
   private RenderingApplication _renderingApp;
   private BrowserContent _content;
   private Field _field;
   private Field _banner;
   private MediaContentHandlerApplication _app;
   private boolean _contentLoaded;
   private MediaPlayerStateInstance _mediaPlayerState;
   private static final Tag EXPLORER_BANNER_TAG = Tag.create("explorer-banner");

   public RenderScreen(MediaContentHandlerApplication app, InputConnection connection, String type, String url) {
      super(3459327463773962240L);
      this._app = app;
      this.init(connection, type, url);
   }

   private final void init(InputConnection connection, String type, String url) {
      this._renderingApp = new RenderApp(this, url);
      int mediaType = MIMETypeAssociations.getMediaTypeFromMIMEType(type);
      switch (mediaType) {
         case 2:
         case 3:
         case 7:
            this._banner = RibbonBanner.getInstance().getStatusBanner("", 3);
            this._banner.setTag(EXPLORER_BANNER_TAG);
            this.getDelegate().add(this._banner);
         default:
            this._field = this.createFieldForItem(connection);
            if (this._field == null) {
               this._app.finish(6);
               this._field = this.createRenderErrorField();
            }

            if (this._field instanceof MediaPlayerStateProvider) {
               this._mediaPlayerState = ((MediaPlayerStateProvider)this._field).getMediaPlayerState();
            }

            this.getDelegate().add(this._field);
      }
   }

   private final Field createFieldForItem(InputConnection input) {
      this._contentLoaded = false;

      try {
         if (input == null) {
            return null;
         }

         RenderingSession session = RenderingSession.getNewInstance();
         this._content = session.getBrowserContent(input, null, this._renderingApp, 0);
         if (this._content != null) {
            return this._content.getDisplayableContent();
         }
      } finally {
         return null;
      }

      return null;
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
      // 01: getfield net/rim/device/apps/internal/explorer/content/RenderScreen._contentLoaded Z
      // 04: ifne 3d
      // 07: aload 0
      // 08: getfield net/rim/device/apps/internal/explorer/content/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 0b: ifnull 3d
      // 0e: bipush 0
      // 0f: istore 1
      // 10: aload 0
      // 11: getfield net/rim/device/apps/internal/explorer/content/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
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
      // 26: ifeq 38
      // 29: aload 0
      // 2a: getfield net/rim/device/apps/internal/explorer/content/RenderScreen._app Lnet/rim/device/apps/internal/explorer/content/MediaContentHandlerApplication;
      // 2d: new net/rim/device/apps/internal/explorer/content/RenderScreen$1
      // 30: dup
      // 31: aload 0
      // 32: invokespecial net/rim/device/apps/internal/explorer/content/RenderScreen$1.<init> (Lnet/rim/device/apps/internal/explorer/content/RenderScreen;)V
      // 35: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 38: aload 0
      // 39: bipush 1
      // 3a: putfield net/rim/device/apps/internal/explorer/content/RenderScreen._contentLoaded Z
      // 3d: return
      // try (8 -> 11): 12 null
      // try (8 -> 11): 16 null
   }

   private final Field createRenderErrorField() {
      return new RichTextField("Unable to play content.", 36028797018963968L);
   }

   public final void finishLoadingFile() {
      Thread t = new RenderScreen$2(this);
      t.start();
   }

   @Override
   public final void close() {
      this._app.finish(5, false);
      super.close();
   }

   @Override
   protected final void sublayout(int width, int height) {
      this.setPosition(0, 0);
      this.setExtent(width, height);
      this.setPositionDelegate(0, 0);
      this.layoutDelegate(width, height);
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

   @Override
   protected final boolean keyChar(char c, int status, int time) {
      if (super.keyChar(c, status, time)) {
         return true;
      }

      c = CharacterUtilities.toLowerCase(c);
      if (c == 27) {
         if (this._mediaPlayerState != null) {
            if (!this._mediaPlayerState.isPlayerPlaying() && !this._mediaPlayerState.isPlayerPaused()) {
               this.close();
               return true;
            } else {
               this._app.requestBackground();
               return true;
            }
         } else {
            this.close();
            return true;
         }
      } else {
         return false;
      }
   }
}
