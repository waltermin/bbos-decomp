package net.rim.device.apps.internal.explorer.file.render;

import javax.microedition.io.ContentConnection;
import javax.microedition.io.InputConnection;
import net.rim.device.api.browser.field.BrowserContent;
import net.rim.device.api.browser.field.RenderingApplication;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.theme.Tag;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.internal.explorer.Media.MediaLauncher;
import net.rim.device.apps.internal.explorer.file.ExploreHotkeys;
import net.rim.device.apps.internal.explorer.file.ExploreManager;
import net.rim.device.apps.internal.explorer.file.FileItemField;
import net.rim.device.apps.internal.explorer.file.menu.DeleteMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.NextMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.PauseMenuItem;
import net.rim.device.apps.internal.explorer.file.menu.PrevMenuItem;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;
import net.rim.device.internal.io.file.FileUtilities;

public final class RenderScreen extends AppsMainScreen {
   private RenderingApplication _renderingApp;
   private ExploreManager _explorer;
   private FileItemField _item;
   private BrowserContent _content;
   private Field _field;
   private Field _banner;
   private boolean _showProgress;
   private SlideShowThread _slideShowThread;
   private boolean _isSlideShow;
   private boolean _contentValid;
   private boolean _contentLoaded;
   private boolean _select;
   private static final Tag PICTURE_TAG = Tag.create("picture-window");
   private static final Tag EXPLORER_BANNER_TAG = Tag.create("explorer-banner");
   private static final Tag MEDIAPLAYER_BANNER_TAG = Tag.create("mediaplayer-banner");

   private RenderScreen(ExploreManager mgr) {
      super(3459045988797251584L);
      this._explorer = mgr;
      this.setHelp(32247);
   }

   public RenderScreen(ExploreManager mgr, FileItemField item, boolean slideShow) {
      this(mgr);
      String url = item.getURL();
      this._item = item;
      if (!slideShow) {
         this.init(url, null);
      } else {
         this._isSlideShow = true;
         this._renderingApp = new RenderApp(this, url);
         ReadableList list = this._explorer.getFileView().getList();
         int advance = 0;
         int maxAdvance = list.size();
         int curIndex = list.getIndex(this._item);

         while (true) {
            if (this._item.getMediaType() == 1) {
               this._field = this.createFieldForItem(null);
               if (this._field != null) {
                  this._field.setTag(PICTURE_TAG);
                  break;
               }
            }

            this._item = this.getNextViewableItem(this._item, true, true);
            if (this._item == null) {
               this._item = item;
               break;
            }

            int nextIndex = list.getIndex(this._item);
            if (nextIndex > curIndex) {
               advance += nextIndex - curIndex;
            } else {
               advance += maxAdvance - curIndex + nextIndex;
            }

            if (advance >= maxAdvance) {
               break;
            }

            curIndex = nextIndex;
         }

         if (this._field == null) {
            this._field = this.createRenderErrorField();
         }

         this.getDelegate().add(this._field);
      }
   }

   public RenderScreen(ExploreManager mgr, String url, InputConnection inputConnection) {
      this(mgr);
      this.init(url, inputConnection);
   }

   private final void init(String url, InputConnection input) {
      this._renderingApp = new RenderApp(this, url);
      int mediaType = 0;
      if (this._item != null) {
         mediaType = this._item.getMediaType();
      } else if (input instanceof Object) {
         mediaType = MIMETypeAssociations.getMediaTypeFromMIMEType(((ContentConnection)input).getType());
      }

      boolean isErrorField = false;
      this._field = this.createFieldForItem(input);
      if (this._field == null) {
         this._field = this.createRenderErrorField();
         isErrorField = true;
      }

      switch (mediaType) {
         case 1:
            if (!isErrorField) {
               this._field.setTag(PICTURE_TAG);
            }
            break;
         case 2:
         case 3:
         case 7:
            if (!isErrorField) {
               MediaLauncher.stop();
            }

            this._banner = RibbonBanner.getInstance().getStatusBanner("", 3);
            if (MEDIAPLAYER_BANNER_TAG == null) {
               this._banner.setTag(EXPLORER_BANNER_TAG);
            } else {
               this._banner.setTag(MEDIAPLAYER_BANNER_TAG);
            }

            this.getDelegate().add(this._banner);
      }

      this.getDelegate().add(this._field);
   }

   private final Field createFieldForItem(InputConnection param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: bipush 0
      // 02: putfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._contentValid Z
      // 05: aload 0
      // 06: bipush 0
      // 07: putfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._contentLoaded Z
      // 0a: aload 1
      // 0b: ifnonnull 1c
      // 0e: aload 0
      // 0f: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._item Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 12: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getFileConnection ()Ljavax/microedition/io/file/FileConnection;
      // 15: astore 1
      // 16: aload 1
      // 17: ifnonnull 1c
      // 1a: aconst_null
      // 1b: areturn
      // 1c: aload 0
      // 1d: bipush 0
      // 1e: putfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._showProgress Z
      // 21: aload 0
      // 22: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._slideShowThread Lnet/rim/device/apps/internal/explorer/file/render/SlideShowThread;
      // 25: ifnonnull 57
      // 28: aload 0
      // 29: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._item Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 2c: ifnull 57
      // 2f: aload 1
      // 30: instanceof java/lang/Object
      // 33: ifeq 57
      // 36: aload 0
      // 37: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._item Lnet/rim/device/apps/internal/explorer/file/FileItemField;
      // 3a: invokevirtual net/rim/device/apps/internal/explorer/file/FileItemField.getMediaType ()I
      // 3d: bipush 1
      // 3e: if_icmpne 57
      // 41: aload 1
      // 42: checkcast java/lang/Object
      // 45: invokeinterface javax/microedition/io/file/FileConnection.fileSize ()J 1
      // 4a: ldc_w 51200
      // 4d: i2l
      // 4e: lcmp
      // 4f: ifle 57
      // 52: aload 0
      // 53: bipush 1
      // 54: putfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._showProgress Z
      // 57: invokestatic net/rim/device/api/browser/field/RenderingSession.getNewInstance ()Lnet/rim/device/api/browser/field/RenderingSession;
      // 5a: astore 2
      // 5b: aload 2
      // 5c: ifnonnull 62
      // 5f: goto e1
      // 62: aload 2
      // 63: invokevirtual net/rim/device/api/browser/field/RenderingSession.getRenderingOptions ()Lnet/rim/device/api/browser/field/RenderingOptions;
      // 66: astore 3
      // 67: aload 3
      // 68: ldc2_w 4550690918222697397
      // 6b: bipush 26
      // 6d: bipush 0
      // 6e: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 71: aload 3
      // 72: ldc2_w 4550690918222697397
      // 75: bipush 40
      // 77: bipush 0
      // 78: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 7b: aload 3
      // 7c: ldc2_w -2413443615265356506
      // 7f: bipush 1
      // 80: aload 0
      // 81: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._isSlideShow Z
      // 84: ifne 8b
      // 87: bipush 1
      // 88: goto 8c
      // 8b: bipush 0
      // 8c: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // 8f: bipush 1
      // 90: istore 4
      // 92: aload 0
      // 93: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._isSlideShow Z
      // 96: ifeq af
      // 99: aload 0
      // 9a: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._slideShowThread Lnet/rim/device/apps/internal/explorer/file/render/SlideShowThread;
      // 9d: ifnonnull a6
      // a0: bipush 0
      // a1: istore 4
      // a3: goto af
      // a6: aload 0
      // a7: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._slideShowThread Lnet/rim/device/apps/internal/explorer/file/render/SlideShowThread;
      // aa: invokevirtual net/rim/device/apps/internal/explorer/file/render/SlideShowThread.isPaused ()Z
      // ad: istore 4
      // af: aload 3
      // b0: ldc2_w 4550690918222697397
      // b3: bipush 41
      // b5: iload 4
      // b7: invokevirtual net/rim/device/api/browser/field/RenderingOptions.setProperty (JIZ)V
      // ba: aload 0
      // bb: aload 2
      // bc: aload 1
      // bd: aconst_null
      // be: aload 0
      // bf: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._renderingApp Lnet/rim/device/api/browser/field/RenderingApplication;
      // c2: bipush 0
      // c3: invokevirtual net/rim/device/api/browser/field/RenderingSession.getBrowserContent (Ljavax/microedition/io/InputConnection;Ljava/lang/String;Lnet/rim/device/api/browser/field/RenderingApplication;I)Lnet/rim/device/api/browser/field/BrowserContent;
      // c6: putfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // c9: aload 0
      // ca: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // cd: ifnull e1
      // d0: aload 0
      // d1: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // d4: invokeinterface net/rim/device/api/browser/field/BrowserContent.getDisplayableContent ()Lnet/rim/device/api/ui/Field; 1
      // d9: areturn
      // da: astore 2
      // db: aconst_null
      // dc: areturn
      // dd: astore 2
      // de: aconst_null
      // df: areturn
      // e0: astore 2
      // e1: aconst_null
      // e2: areturn
      // try (6 -> 15): 107 null
      // try (16 -> 106): 107 null
      // try (6 -> 15): 110 null
      // try (16 -> 106): 110 null
      // try (6 -> 15): 113 null
      // try (16 -> 106): 113 null
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
      // 01: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._showProgress Z
      // 04: ifeq 21
      // 07: new net/rim/device/apps/internal/explorer/file/render/RenderScreen$ProgressDialog
      // 0a: dup
      // 0b: invokespecial net/rim/device/apps/internal/explorer/file/render/RenderScreen$ProgressDialog.<init> ()V
      // 0e: astore 1
      // 0f: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 12: new net/rim/device/apps/internal/explorer/file/render/RenderScreen$1
      // 15: dup
      // 16: aload 0
      // 17: aload 1
      // 18: invokespecial net/rim/device/apps/internal/explorer/file/render/RenderScreen$1.<init> (Lnet/rim/device/apps/internal/explorer/file/render/RenderScreen;Lnet/rim/device/apps/internal/explorer/file/render/RenderScreen$ProgressDialog;)V
      // 1b: invokevirtual net/rim/device/api/system/Application.invokeAndWait (Ljava/lang/Runnable;)V
      // 1e: goto 23
      // 21: aconst_null
      // 22: astore 1
      // 23: aload 0
      // 24: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._contentLoaded Z
      // 27: ifne 64
      // 2a: aload 0
      // 2b: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 2e: ifnull 64
      // 31: bipush 0
      // 32: istore 2
      // 33: aload 0
      // 34: getfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._content Lnet/rim/device/api/browser/field/BrowserContent;
      // 37: invokeinterface net/rim/device/api/browser/field/BrowserContent.finishLoading ()V 1
      // 3c: aload 0
      // 3d: bipush 1
      // 3e: putfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._contentValid Z
      // 41: goto 4d
      // 44: astore 3
      // 45: bipush 1
      // 46: istore 2
      // 47: goto 4d
      // 4a: astore 3
      // 4b: bipush 1
      // 4c: istore 2
      // 4d: iload 2
      // 4e: ifeq 5f
      // 51: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 54: new net/rim/device/apps/internal/explorer/file/render/RenderScreen$2
      // 57: dup
      // 58: aload 0
      // 59: invokespecial net/rim/device/apps/internal/explorer/file/render/RenderScreen$2.<init> (Lnet/rim/device/apps/internal/explorer/file/render/RenderScreen;)V
      // 5c: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 5f: aload 0
      // 60: bipush 1
      // 61: putfield net/rim/device/apps/internal/explorer/file/render/RenderScreen._contentLoaded Z
      // 64: aload 1
      // 65: ifnull 77
      // 68: invokestatic net/rim/device/api/system/Application.getApplication ()Lnet/rim/device/api/system/Application;
      // 6b: new net/rim/device/apps/internal/explorer/file/render/RenderScreen$3
      // 6e: dup
      // 6f: aload 0
      // 70: aload 1
      // 71: invokespecial net/rim/device/apps/internal/explorer/file/render/RenderScreen$3.<init> (Lnet/rim/device/apps/internal/explorer/file/render/RenderScreen;Lnet/rim/device/apps/internal/explorer/file/render/RenderScreen$ProgressDialog;)V
      // 74: invokevirtual net/rim/device/api/system/Application.invokeLater (Ljava/lang/Runnable;)V
      // 77: return
      // try (25 -> 31): 32 null
      // try (25 -> 31): 36 null
   }

   private final Field createRenderErrorField() {
      return (Field)(new Object(
         ((StringBuffer)(new Object()))
            .append(ExplorerResources.getString(86))
            .append('\n')
            .append(this._item != null ? FileUtilities.getDisplayName(this._item.getFullPath()) : "")
            .toString(),
         36028797018963968L
      ));
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
      if (this._isSlideShow) {
         this._slideShowThread = new SlideShowThread(this, this._item);
         this._slideShowThread.start();
      } else {
         Thread t = new RenderScreen$4(this);
         t.start();
      }
   }

   public final boolean isSlide() {
      return this._slideShowThread != null;
   }

   public final boolean isSelectedSet() {
      return this._select;
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
      if (c == ' ') {
         if (this._slideShowThread != null) {
            this.pause(!this._slideShowThread.isPaused());
         }

         return true;
      } else {
         if (c != 127 && c != '\b') {
            return super.keyChar(c, status, time);
         }

         if (this._item != null && this._item.canDelete() && !this._item.isAlias()) {
            new DeleteMenuItem(this._item, this).run();
         }

         return true;
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int k = ExploreHotkeys.map(keycode);
      switch (k) {
         case 5:
            return super.keyDown(keycode, time);
         case 6:
         default:
            MenuItem next = this.getNextMenuItem(true);
            if (next != null && Application.getApplication().getMessageQueueSize() < 5) {
               next.run();
               return true;
            }

            FileItemField nextItem = this.getNextViewableItem(false, true);
            if (nextItem != null) {
               this._item = nextItem;
            }

            return true;
         case 7:
            MenuItem prev = this.getPrevMenuItem(true);
            if (prev != null && Application.getApplication().getMessageQueueSize() < 5) {
               prev.run();
               return true;
            } else {
               FileItemField previousItem = this._explorer.getFileView().getPreviousViewableFile(this._item, true);
               if (previousItem != null) {
                  this._item = previousItem;
               }

               return true;
            }
      }
   }

   public final ExploreManager getExploreManager() {
      return this._explorer;
   }

   public final FileItemField getNextViewableItem(boolean wrap, boolean select) {
      return this.getNextViewableItem(null, wrap, select);
   }

   public final FileItemField getNextViewableItem(FileItemField curItem, boolean wrap, boolean select) {
      if (curItem == null) {
         curItem = this._item;
      }

      return curItem == null ? null : this._explorer.getFileView().getNextViewableFile(curItem, wrap, select);
   }

   private final MenuItem getPrevMenuItem(boolean select) {
      if (this._item == null) {
         return null;
      } else {
         FileItemField item = this._explorer.getFileView().getPreviousViewableFile(this._item, select);
         if (item != null) {
            this._select = select;
            return new PrevMenuItem(this._explorer, this, item);
         } else {
            return null;
         }
      }
   }

   private final MenuItem getNextMenuItem(boolean select) {
      FileItemField item = this.getNextViewableItem(false, select);
      if (item != null) {
         this._select = select;
         return new NextMenuItem(this._explorer, this, item);
      } else {
         return null;
      }
   }

   @Override
   protected final void makeMenu(Menu contextMenu, int instance) {
      super.makeMenu(contextMenu, instance);
      this._explorer.addFileActionMenuItems(contextMenu, this._item, this._field, this, instance);
      this._explorer.addThirdPartyMenuItems(contextMenu, 3504265587951702900L, this._item);
      this._explorer.addThirdPartyMenuItems(contextMenu, -2166984963208053554L, this._item);
      if (this._item != null) {
         MenuItem nextMenuItem = this.getNextMenuItem(false);
         if (nextMenuItem != null) {
            contextMenu.add(nextMenuItem);
         }

         MenuItem prevMenuItem = this.getPrevMenuItem(false);
         if (prevMenuItem != null) {
            contextMenu.add(prevMenuItem);
         }

         if (this.isSlide()) {
            contextMenu.add(new PauseMenuItem(this, !this._slideShowThread.isPaused()));
         }
      }
   }

   final boolean setItem(FileItemField item) {
      FileItemField oldItem = this._item;
      this._item = item;
      Field field = this.createFieldForItem(null);
      if (field == null) {
         this._item = oldItem;
         return false;
      }

      if (item.getMediaType() == 1) {
         field.setTag(PICTURE_TAG);
      }

      this.loadContent();
      synchronized (Application.getEventLock()) {
         this.getDelegate().replace(this._field, field);
      }

      this._field = field;
      return true;
   }

   public final void pause(boolean enable) {
      if (this._slideShowThread != null) {
         this._slideShowThread.pause(enable);
         if (this._contentValid) {
            this._field.getLeafFieldWithFocus().setEditable(enable);
         }

         if (this._slideShowThread.isAlive()) {
            this._slideShowThread.interrupt();
         }
      }
   }

   public final void gotoSlide(FileItemField item) {
      if (this._slideShowThread != null) {
         this._slideShowThread.setNextSlide(item);
         if (this._slideShowThread.isAlive()) {
            this._slideShowThread.interrupt();
         }
      }
   }

   @Override
   public final boolean onClose() {
      boolean rc = super.onClose();
      this.pause(false);
      return rc;
   }

   @Override
   protected final void onObscured() {
      if (this._slideShowThread != null) {
         this._slideShowThread.setObscured(true);
      }
   }

   @Override
   protected final void onExposed() {
      if (this._slideShowThread != null) {
         this._slideShowThread.setObscured(false);
      }
   }

   @Override
   protected final void paint(Graphics graphics) {
      super.paint(graphics);
      if (this._slideShowThread != null) {
         this._slideShowThread.setSlideStartTime(System.currentTimeMillis());
      }
   }
}
