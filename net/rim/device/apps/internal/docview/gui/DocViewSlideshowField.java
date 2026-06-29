package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;

public final class DocViewSlideshowField extends VerticalFieldManager implements DocViewRequestMore {
   private final DocViewParser _docData;
   private final boolean _isPresentation;
   private boolean _duringPausedAutoMore;
   private final UiApplication _uiApplication = UiApplication.getUiApplication();
   private DocViewNotify _notifyObject;
   private final DocViewSlideshowField$SlideshowImageField _imgFld;
   private IntHashtable _renderedImages = new IntHashtable();
   private int _currentIndex = -1;
   private int _imageCount;
   private final DocViewTextDisplayField$DummyBitmapField _dummyFld = new DocViewTextDisplayField$DummyBitmapField();
   private final MenuItem _prevItem = new DocViewSlideshowField$1(this, null, 196624, 0);
   private final MenuItem _nextItem = new DocViewSlideshowField$2(this, null, 196624, 0);
   private final MenuItem _goToItem = new DocViewSlideshowField$3(this, null, 131088, 0);
   private static final ResourceBundleFamily _resources = ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView");

   final void setNotifyObject(DocViewNotify newNotifyObj) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final synchronized void more() {
      if (this._docData.getParsingData().getStopFlag() == 1) {
         if (this._duringPausedAutoMore) {
            return;
         }

         this._duringPausedAutoMore = true;
         DocViewSlideshowField thisObject = this;
         new DocViewSlideshowField$4(this, thisObject).start();
      }
   }

   public DocViewSlideshowField(DocViewParser docData, boolean isPresentation) {
      super(3461579263587647488L);
      if (docData == null) {
         throw new IllegalArgumentException("Null DocViewParser object.");
      }

      this._docData = docData;
      this._isPresentation = isPresentation;
      this._imgFld = new DocViewSlideshowField$SlideshowImageField(
         this, DocViewGUIInternalConstants.SCREEN_WIDTH < DocViewGUIInternalConstants.SCREEN_HEIGHT && isPresentation
      );
      this.calculateImageCount();
      if (isPresentation) {
         this._prevItem.setText(_resources.getString(111));
         this._nextItem.setText(_resources.getString(112));
         this._goToItem.setText(_resources.getString(121));
      } else {
         this._prevItem.setText(_resources.getString(114));
         this._nextItem.setText(_resources.getString(115));
         this._goToItem.setText(_resources.getString(120));
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      boolean retValue = super.navigationMovement(dx, dy, status, time);
      return !retValue && (status & 2) == 0 ? this.navigateImpl(dy, status) : retValue;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (!InternalServices.isReducedFormFactor()) {
         switch (UiInternal.map(keycode)) {
            case '\n':
            case ' ':
               this.advance(true, 1);
               return true;
            case 'G':
            case 'g':
               this._goToItem.run();
               return true;
            case 'N':
            case 'n':
               this.advance(true, 1);
               return true;
            case 'P':
            case 'p':
               this.advance(false, 1);
               return true;
         }
      } else {
         switch (UiInternal.map(keycode)) {
            case '\n':
            case ' ':
            case 'G':
            case 'g':
               this.advance(true, 1);
               return true;
            case 'A':
            case 'a':
               this._goToItem.run();
               return true;
            case 'D':
            case 'd':
               this.advance(false, 1);
               return true;
            case 'J':
            case 'j':
               this.advance(true, 1);
               return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      if (instance != 0) {
         int size = menu.getSize();

         for (int i = size - 1; i >= 0; i--) {
            MenuItem item = menu.getItem(i);
            if (item != null && item.getId() != 10125) {
               if (item.getOrdinal() == 131072) {
                  if (item.getId() != 110) {
                     menu.deleteItem(i);
                  }
               } else {
                  menu.deleteItem(i);
               }
            }
         }
      }

      if (this._currentIndex >= 0 && this._currentIndex < this._imageCount) {
         if (this._currentIndex > 0) {
            menu.add(this._prevItem);
            menu.setDefaultIgnoreContextMenuDefault(this._prevItem);
         }

         if (this._currentIndex < this._imageCount - 1) {
            menu.add(this._nextItem);
            menu.setDefaultIgnoreContextMenuDefault(this._nextItem);
         }

         if (instance == 0 && this._imageCount > 1) {
            menu.add(this._goToItem);
         }
      }

      if (instance == 0 && !this._duringPausedAutoMore && this._docData.getParsingData().getStopFlag() == 1) {
         menu.add(new DocViewSlideshowField$5(this, EmailResources.getResourceBundle(), 80, 344064, 0));
      }
   }

   private final synchronized void calculateImageCount() {
      DocViewImageData[] images = this._docData.getParsingData().getImages();
      if (images != null) {
         if (this._docData.getParsingData().getStopFlag() == 1) {
            this._imageCount = Math.max(images.length - 1, 0);
         } else {
            this._imageCount = images.length;
         }
      }

      if (this._imageCount > 0) {
         if (this._currentIndex < 0 || this._currentIndex >= this._imageCount) {
            this._currentIndex = 0;
            this.setDisplayField();
            return;
         }

         if (this._dummyFld.getIndex() != -1) {
            this.setDisplayField();
         }
      }
   }

   private final boolean navigateImpl(int amount, int status) {
      if (amount == 0 || (status & 65536) != 0 || !this.canScrollFromCurrentSlide()) {
         return false;
      } else if ((status & 1) != 0) {
         this.advance(amount > 0);
         return true;
      } else if (Trackball.isSupported()) {
         this.advance(amount > 0, 1);
         return true;
      } else {
         this.advance(amount > 0, Math.abs(amount));
         return true;
      }
   }

   private final boolean canScrollFromCurrentSlide() {
      return this._imgFld != null
         ? this._imgFld.getBitmapWidth() <= DocViewGUIInternalConstants.SCREEN_WIDTH
            && this._imgFld.getBitmapHeight() <= DocViewGUIInternalConstants.SCREEN_HEIGHT
            && !this._imgFld.isZooming()
         : true;
   }

   private final void advance(boolean endOfSlides) {
      if (this._currentIndex >= 0 && this._imageCount > 0) {
         if (endOfSlides) {
            this.advance(true, this._imageCount - this._currentIndex + 1);
            return;
         }

         this.advance(false, this._currentIndex);
      }
   }

   private final void advance(boolean forward, int steps) {
      if (this._currentIndex >= 0 && this._imageCount > 0 && steps > 0) {
         if (forward) {
            if (this._currentIndex < this._imageCount - 1) {
               this._currentIndex = Math.min(this._imageCount - 1, this._currentIndex + steps);
               this.setDisplayField();
               return;
            }
         } else if (this._currentIndex > 0) {
            this._currentIndex = Math.max(0, this._currentIndex - steps);
            this.setDisplayField();
            return;
         }
      }
   }

   private final void goToImpl() {
      if (this._currentIndex >= 0 && this._currentIndex < this._imageCount && this._imageCount > 1) {
         String prompt = this._isPresentation ? _resources.getString(76) : _resources.getString(119);
         prompt = prompt + " (1 - " + this._imageCount + ")" + ':';
         DocViewTextDisplayField$GoToDlg dlg = new DocViewTextDisplayField$GoToDlg(prompt, this._imageCount);
         dlg.show();
         if (dlg.getCloseReason() == 0) {
            int newIndex = dlg.getPositiveNumberText();
            if (newIndex > 0 && newIndex <= this._imageCount) {
               this._currentIndex = newIndex - 1;
               this.setDisplayField();
            }
         }
      }
   }

   private final synchronized void setDisplayField() {
      if (this._currentIndex >= 0 && this._imageCount > 0 && this._currentIndex < this._imageCount) {
         EncodedImage img = this.getDisplayField(this._currentIndex);
         if (img != null) {
            this._uiApplication.invokeAndWait(new DocViewSlideshowField$6(this, img));
            return;
         }

         if (this._dummyFld.getIndex() == -1) {
            this._uiApplication.invokeAndWait(new DocViewSlideshowField$7(this));
         }
      }
   }

   private final synchronized EncodedImage getDisplayField(int param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: iload 1
      // 01: iflt 89
      // 04: iload 1
      // 05: aload 0
      // 06: getfield net/rim/device/apps/internal/docview/gui/DocViewSlideshowField._imageCount I
      // 09: if_icmpge 89
      // 0c: aload 0
      // 0d: getfield net/rim/device/apps/internal/docview/gui/DocViewSlideshowField._renderedImages Lnet/rim/device/api/util/IntHashtable;
      // 10: iload 1
      // 11: invokevirtual net/rim/device/api/util/IntHashtable.get (I)Ljava/lang/Object;
      // 14: astore 2
      // 15: aload 2
      // 16: dup
      // 17: instanceof net/rim/device/api/system/EncodedImage
      // 1a: ifne 21
      // 1d: pop
      // 1e: goto 40
      // 21: checkcast net/rim/device/api/system/EncodedImage
      // 24: astore 3
      // 25: aload 3
      // 26: invokevirtual net/rim/device/api/system/EncodedImage.getScaleX32 ()I
      // 29: ldc_w 65536
      // 2c: if_icmpne 39
      // 2f: aload 3
      // 30: invokevirtual net/rim/device/api/system/EncodedImage.getScaleY32 ()I
      // 33: ldc_w 65536
      // 36: if_icmpeq 3e
      // 39: aload 3
      // 3a: bipush 1
      // 3b: invokevirtual net/rim/device/api/system/EncodedImage.setScale (I)V
      // 3e: aload 3
      // 3f: areturn
      // 40: aload 0
      // 41: getfield net/rim/device/apps/internal/docview/gui/DocViewSlideshowField._docData Lnet/rim/device/apps/internal/docview/gui/DocViewParser;
      // 44: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getParsingData ()Lnet/rim/device/apps/internal/docview/gui/DocViewParsingData;
      // 47: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.getImages ()[Lnet/rim/device/apps/internal/docview/gui/DocViewImageData;
      // 4a: astore 3
      // 4b: aload 3
      // 4c: ifnull 89
      // 4f: iload 1
      // 50: aload 3
      // 51: arraylength
      // 52: if_icmpge 89
      // 55: aload 3
      // 56: iload 1
      // 57: aaload
      // 58: astore 4
      // 5a: aload 4
      // 5c: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewImageData.getImageContents ()[B
      // 5f: astore 5
      // 61: aload 5
      // 63: bipush 0
      // 64: aload 5
      // 66: arraylength
      // 67: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage ([BII)Lnet/rim/device/api/system/EncodedImage;
      // 6a: astore 6
      // 6c: aload 6
      // 6e: ifnull 89
      // 71: aload 0
      // 72: getfield net/rim/device/apps/internal/docview/gui/DocViewSlideshowField._renderedImages Lnet/rim/device/api/util/IntHashtable;
      // 75: iload 1
      // 76: aload 6
      // 78: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // 7b: pop
      // 7c: aload 6
      // 7e: areturn
      // 7f: astore 4
      // 81: aconst_null
      // 82: areturn
      // 83: astore 4
      // 85: aconst_null
      // 86: areturn
      // 87: astore 4
      // 89: aconst_null
      // 8a: areturn
      // try (43 -> 65): 66 null
      // try (43 -> 65): 69 null
      // try (43 -> 65): 72 null
   }
}
