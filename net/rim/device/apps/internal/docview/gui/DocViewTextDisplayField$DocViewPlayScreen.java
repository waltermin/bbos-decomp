package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.math.Fixed32;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.api.ui.ZoomBitmapField;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.UiInternal;

public final class DocViewTextDisplayField$DocViewPlayScreen extends AppsMainScreen {
   private final boolean _isInitialRotated;
   private final DocViewTextDisplayField$DummyBitmapField _dummyFld;
   private final SlideshowBitmapField _imgFld;
   private final MenuItem _prevItem;
   private final MenuItem _nextItem;
   private final MenuItem _retrieveItem;
   private final MenuItem _vtItem;
   private final MenuItem _vbItem;
   private final MenuItem _vdocinfoItem;
   private final MenuItem _rdocinfoItem;
   private final MenuItem _goToItem;
   private String[] _renderedDomIDs;
   private int _currentIndex;
   private boolean _gotCurrentSlide;
   private int _lastRequestedSlideIndex;
   private IntHashtable _usedDisplayScreens;
   private IntHashtable _fullScaleImages;
   private IntHashtable _pageAccessedImgs;
   private boolean _checkSlidesInProgress;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$DocViewPlayScreen(DocViewTextDisplayField _1, boolean useRotatedBitmaps) {
      super(2814751914590208L);
      this.this$0 = _1;
      this._dummyFld = new DocViewTextDisplayField$DummyBitmapField();
      this._prevItem = new DocViewTextDisplayField$DocViewPlayScreen$1(
         this, this.this$0._isPresentation ? DocViewDisplayField._resources.getString(111) : DocViewDisplayField._resources.getString(114), 196624, 0
      );
      this._nextItem = new DocViewTextDisplayField$DocViewPlayScreen$2(
         this, this.this$0._isPresentation ? DocViewDisplayField._resources.getString(112) : DocViewDisplayField._resources.getString(115), 196624, 0
      );
      this._retrieveItem = new DocViewTextDisplayField$DocViewPlayScreen$3(this, DocViewDisplayField._resources.getString(8), 65552, 0);
      this._vtItem = new DocViewTextDisplayField$DocViewPlayScreen$4(this, DocViewDisplayField._resources.getString(26), 65552, 0);
      this._vbItem = new DocViewTextDisplayField$DocViewPlayScreen$5(this, DocViewDisplayField._resources.getString(50), 65552, 0);
      this._vdocinfoItem = new DocViewTextDisplayField$DocViewPlayScreen$6(this, DocViewDisplayField._resources.getString(60), 65552, 0);
      this._rdocinfoItem = new DocViewTextDisplayField$DocViewPlayScreen$7(this, DocViewDisplayField._resources.getString(59), 65552, 0);
      this._goToItem = new DocViewTextDisplayField$DocViewPlayScreen$8(
         this, DocViewDisplayField._resources.getString(this.this$0._isPresentation ? 121 : 120), 131088, 0
      );
      this._currentIndex = -1;
      this._lastRequestedSlideIndex = -1;
      this._usedDisplayScreens = new IntHashtable(1);
      this._fullScaleImages = new IntHashtable(16);
      this._pageAccessedImgs = new IntHashtable(16);
      this.setHelp("messages_index");
      this._imgFld = new SlideshowBitmapField(useRotatedBitmaps);
      this._isInitialRotated = useRotatedBitmaps;
      if (_1._strRenderDomIDs != null && _1._strRenderDomIDs.length() > 0) {
         StringTokenizer tokens = new StringTokenizer(_1._strRenderDomIDs, ',');
         int tokensCount = tokens.countTokens();
         if (tokensCount > 0) {
            this._renderedDomIDs = new String[tokensCount];
            int idx = 0;

            while (tokens.hasMoreTokens()) {
               this._renderedDomIDs[idx++] = tokens.nextToken();
            }

            this._currentIndex = 0;
         }
      }

      this.initialize();
   }

   final void initialize() {
      this.checkSlides();
      this.setDisplayField(false);
   }

   @Override
   public final boolean onClose() {
      if (this._gotCurrentSlide && this._imgFld.getIndex() == -1 && !this.showMainSlide(true, this._currentIndex)) {
         return false;
      }

      boolean retValue = super.onClose();
      this.this$0.closeMainScreen();
      return retValue;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      if (!this.showMainSlide(false, this._currentIndex)) {
         return super.keyDown(keycode, time);
      }

      if (!InternalServices.isReducedFormFactor()) {
         switch (UiInternal.map(keycode)) {
            case '\n':
            case ' ':
               this.advance(true, 1, false);
               return true;
            case 'G':
            case 'g':
               this._goToItem.run();
               return true;
            case 'M':
            case 'm':
               if (this.this$0._hasPageBreaks) {
                  this._vbItem.run();
                  return true;
               }

               this._vtItem.run();
               return true;
            case 'N':
            case 'n':
               this.advance(true, 1, false);
               return true;
            case 'P':
            case 'p':
               this.advance(false, 1, false);
               return true;
         }
      } else {
         switch (UiInternal.map(keycode)) {
            case '\n':
            case ' ':
            case 'G':
            case 'g':
               this.advance(true, 1, false);
               return true;
            case 'A':
            case 'a':
               this._goToItem.run();
               return true;
            case 'D':
            case 'd':
               this.advance(false, 1, false);
               return true;
            case 'J':
            case 'j':
               this.advance(true, 1, false);
               return true;
            case 'Z':
            case 'z':
               if (this.this$0._hasPageBreaks) {
                  this._vbItem.run();
                  return true;
               }

               this._vtItem.run();
               return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   public final boolean onMenu(int instance) {
      if (this._imgFld.getIndex() != -1) {
         this.setDisplayFieldDetailed();
      }

      return super.onMenu(instance);
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      boolean isMainSlide = this.showMainSlide(false, this._currentIndex);
      if (isMainSlide && instance != 0) {
         MenuItem newFullImage = null;
         int size = menu.getSize();

         for (int i = size - 1; i >= 0; i--) {
            MenuItem item = menu.getItem(i);
            if (item != null) {
               if (item instanceof VerbMenuItem && ((VerbMenuItem)item).getVerb() instanceof DocViewGuiVerb) {
                  int behaviour = ((DocViewGuiVerb)((VerbMenuItem)item).getVerb()).getBehaviour();
                  menu.deleteItem(i);
                  if (behaviour == 20 || behaviour == 19) {
                     item.setOrdinal(131088);
                     newFullImage = item;
                  }
               } else if (item.getId() != 10125) {
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

         if (newFullImage != null) {
            menu.add(newFullImage);
            Object var9 = null;
         }
      }

      if (isMainSlide) {
         if (this._currentIndex >= 0 && this._renderedDomIDs != null && this._currentIndex < this._renderedDomIDs.length) {
            if (this._currentIndex > 0) {
               menu.add(this._prevItem);
               menu.setDefaultIgnoreContextMenuDefault(this._prevItem);
            }

            if (this._currentIndex < this._renderedDomIDs.length - 1) {
               menu.add(this._nextItem);
               menu.setDefaultIgnoreContextMenuDefault(this._nextItem);
            }

            if (!this._gotCurrentSlide) {
               menu.add(this._retrieveItem);
               menu.setDefaultIgnoreContextMenuDefault(this._retrieveItem);
            }

            if (instance == 0 && this._renderedDomIDs.length > 1) {
               menu.add(this._goToItem);
            }
         }

         if (instance == 0) {
            menu.add(this._vtItem);
            if (this.this$0._hasPageBreaks) {
               menu.add(this._vbItem);
            }
         }
      }

      if (instance == 0 && this.this$0.allowDocInfo()) {
         menu.add(this.this$0.hasDocInfoData() ? this._vdocinfoItem : this._rdocinfoItem);
      }
   }

   @Override
   protected final boolean navigationMovement(int dx, int dy, int status, int time) {
      boolean retValue = super.navigationMovement(dx, dy, status, time);
      return !retValue && (status & 2) == 0 ? this.navigateImpl(dy, status) : retValue;
   }

   protected final synchronized void processPageDisplay(String[] arbitraryDomIDs, byte[] ucsData) {
      if (this._renderedDomIDs != null && this._currentIndex >= 0 && this._currentIndex < this._renderedDomIDs.length) {
         if (!this._gotCurrentSlide) {
            int idx = Arrays.getIndex(arbitraryDomIDs, this._renderedDomIDs[this._currentIndex]);
            if (idx != -1
               && this.parseAndProcess(
                     this.this$0.parseCustomData((byte)0, ucsData != null ? ucsData : this._renderedDomIDs[this._currentIndex]), this._currentIndex
                  )
                  != null) {
               int currentIdx = this._currentIndex;
               this.this$0._application.invokeLater(new DocViewTextDisplayField$DocViewPlayScreen$9(this, currentIdx));
            }
         }

         if (this.isDisplayed()) {
            this.checkSlides();
         }
      }
   }

   private final void advance(boolean endOfSlides) {
      if (this._currentIndex >= 0 && this._renderedDomIDs != null) {
         if (endOfSlides) {
            this.advance(true, this._renderedDomIDs.length - this._currentIndex + 1, false);
            return;
         }

         this.advance(false, this._currentIndex, false);
      }
   }

   private final void advance(boolean forward, int steps, boolean quitable) {
      if (this._currentIndex >= 0 && this._renderedDomIDs != null && steps > 0) {
         if (forward) {
            if (this._currentIndex < this._renderedDomIDs.length - 1) {
               this._currentIndex = Math.min(this._renderedDomIDs.length - 1, this._currentIndex + steps);
               this.setDisplayField(false);
               return;
            }
         } else if (this._currentIndex > 0) {
            this._currentIndex = Math.max(0, this._currentIndex - steps);
            this.setDisplayField(false);
            return;
         }
      }

      if (quitable) {
         this.onClose();
      }
   }

   private final boolean showMainSlide(boolean executeClose, int index) {
      if (this._renderedDomIDs != null && index >= 0 && index < this._renderedDomIDs.length) {
         Object info = this._usedDisplayScreens.get(index);
         if (info instanceof DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo) {
            DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo scrInfo = (DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo)info;
            if (scrInfo._imageField instanceof DocViewDisplayField) {
               return ((DocViewDisplayField)scrInfo._imageField).canClose(executeClose);
            }
         }
      }

      return true;
   }

   private final void processCachedScreensOnExit() {
      IntEnumeration e = this._usedDisplayScreens.keys();

      while (e.hasMoreElements()) {
         int index = e.nextElement();
         DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo esi = (DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo)this._usedDisplayScreens
            .get(index);
         if (esi._imageField.getIndex() != -1) {
            esi._imageField.getManager().delete(esi._imageField);
         }

         esi._screen.insert(esi._imageField, 0);
         if (esi._imageField instanceof DocViewDisplayField) {
            this.showMainSlide(true, index);
            if (((DocViewDisplayField)esi._imageField).getFieldCount() > 0 && ((DocViewDisplayField)esi._imageField).getField(0) instanceof ZoomBitmapField) {
               ZoomBitmapField zbf = (ZoomBitmapField)((DocViewDisplayField)esi._imageField).getField(0);
               zbf.setScale(esi._origScale, esi._origTopX, esi._origTopY, esi._origRotationValue);
            }
         }

         esi._hasBeenInit = false;
      }

      this._usedDisplayScreens.clear();
   }

   private final void setDisplayModeImpl(byte displayMode) {
      this.processCachedScreensOnExit();
      super.onClose();
      this.this$0._inSlideshowMode = false;
      IntEnumeration e = this._pageAccessedImgs.keys();

      while (e.hasMoreElements()) {
         EncodedImage img = (EncodedImage)this._pageAccessedImgs.get(e.nextElement());
         int scale2FP = Fixed32.toFP(2);
         if (img.getScaleX32() != scale2FP || img.getScaleY32() != scale2FP) {
            img.setScale(2);
         }
      }

      if (this.this$0._displayMode != displayMode) {
         this.this$0.setDisplayMode(displayMode, true);
      }

      if (displayMode == 0) {
         this.this$0.mainUpdateLayout();
      }
   }

   private final void goToImpl() {
      if (this._currentIndex >= 0 && this._renderedDomIDs != null && this._currentIndex < this._renderedDomIDs.length && this._renderedDomIDs.length > 1) {
         String prompt = this.this$0._isPresentation ? DocViewDisplayField._resources.getString(76) : DocViewDisplayField._resources.getString(119);
         prompt = prompt + " (1 - " + this._renderedDomIDs.length + ")" + ':';
         DocViewTextDisplayField$GoToDlg dlg = new DocViewTextDisplayField$GoToDlg(prompt, this._renderedDomIDs.length);
         dlg.show();
         if (dlg.getCloseReason() == 0) {
            int newIndex = dlg.getPositiveNumberText();
            if (newIndex > 0 && newIndex <= this._renderedDomIDs.length) {
               this._currentIndex = newIndex - 1;
               this.setDisplayField(true);
            }
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
         this.advance(amount > 0, 1, false);
         return true;
      } else {
         this.advance(amount > 0, Math.abs(amount), false);
         return true;
      }
   }

   private final boolean canScrollFromCurrentSlide() {
      if (this._renderedDomIDs != null && this._currentIndex >= 0 && this._currentIndex < this._renderedDomIDs.length && this._gotCurrentSlide) {
         if (!this.showMainSlide(false, this._currentIndex)) {
            return false;
         }

         ZoomBitmapField zbf = null;
         if (this._imgFld.getIndex() != -1) {
            zbf = this._imgFld;
         } else {
            Object obj = this._usedDisplayScreens.get(this._currentIndex);
            if (obj instanceof DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo) {
               Field imgFld = ((DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo)obj)._imageField;
               if (imgFld instanceof DocViewDisplayField
                  && ((DocViewDisplayField)imgFld).getFieldCount() > 0
                  && ((DocViewDisplayField)imgFld).getField(0) instanceof ZoomBitmapField) {
                  zbf = (ZoomBitmapField)((DocViewDisplayField)imgFld).getField(0);
               }
            }
         }

         if (zbf != null) {
            if (zbf.getBitmapWidth() <= DocViewGUIInternalConstants.SCREEN_WIDTH && zbf.getBitmapHeight() <= DocViewGUIInternalConstants.SCREEN_HEIGHT) {
               return true;
            }

            return false;
         }
      }

      return true;
   }

   private final synchronized DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo getDisplayInfo(int index) {
      if (this._renderedDomIDs != null && index >= 0 && index < this._renderedDomIDs.length) {
         DocViewTextDisplayField$PageBreakStatusField pgBrk = this.this$0.getPageStatusFieldWithDomID(this._renderedDomIDs[index]);
         if (pgBrk != null && pgBrk._pageDisplay != null && !pgBrk._isDummy) {
            DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo info = new DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo(this, null);
            info._arbDomID = pgBrk._domID;
            info._image = ((CustomBitmapField)pgBrk._pageDisplay).getEncodedImage();
            info._imageDomID = pgBrk._imageDomID;
            info._originalHeight = pgBrk._originalDisplayHeight;
            info._originalWidth = pgBrk._originalDisplayWidth;
            info._title = pgBrk._renderedName;
            return info;
         }

         Object cachedInfo = this._fullScaleImages.get(index);
         if (cachedInfo instanceof DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo) {
            return (DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo)cachedInfo;
         }
      }

      return null;
   }

   private final void initEmbeddedScreenInfo(DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo scrInfo, ZoomBitmapField zbf) {
      if (scrInfo._imageField.getIndex() != -1) {
         scrInfo._origRotationValue = zbf.getRotationValue();
         scrInfo._origScale = zbf.getScale();
         scrInfo._origTopX = zbf.getImageX();
         scrInfo._origTopY = zbf.getImageY();
      } else {
         scrInfo._origRotationValue = 0;
         scrInfo._origScale = 65536;
         scrInfo._origTopX = 0;
         scrInfo._origTopY = 0;
      }

      scrInfo._hasBeenInit = true;
   }

   private final void setDisplayFieldDetailed() {
      if (this._currentIndex >= 0 && this._renderedDomIDs != null && this._currentIndex < this._renderedDomIDs.length) {
         if (this._usedDisplayScreens.containsKey(this._currentIndex)) {
            DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo scrInfo = (DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo)this._usedDisplayScreens
               .get(this._currentIndex);
            Field imgFld = scrInfo._imageField;
            if (imgFld instanceof DocViewDisplayField
               && ((DocViewDisplayField)imgFld).getFieldCount() > 0
               && ((DocViewDisplayField)imgFld).getField(0) instanceof ZoomBitmapField) {
               this.showMainSlide(true, this._currentIndex);
               ZoomBitmapField zbf = (ZoomBitmapField)((DocViewDisplayField)imgFld).getField(0);
               if (!scrInfo._hasBeenInit) {
                  this.initEmbeddedScreenInfo(scrInfo, zbf);
               }

               zbf.setScale(65536, 0, 0, this._isInitialRotated ? 90 : 0);
            }

            this.addImageFieldToScreen(imgFld);
            return;
         }

         DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo info = this.getDisplayInfo(this._currentIndex);
         if (info != null) {
            Screen renderScreen = this.this$0
               .getRenderedScreen(info._arbDomID, info._title, info._image, info._originalWidth, info._originalHeight, info._imageDomID);
            if (renderScreen != null) {
               if (!(renderScreen instanceof DocViewDisplayScreen)) {
                  if (renderScreen.getFieldCount() > 0) {
                     Field displayField = renderScreen.getField(0);
                     this.addRenderScreenInfo(renderScreen, displayField, this._currentIndex, this._imgFld.getIndex() != -1 ? this._imgFld : null);
                     this.addImageFieldToScreen(displayField);
                  }
               } else {
                  DocViewDisplayField imageFld = (DocViewDisplayField)((DocViewDisplayScreen)renderScreen)._displayField;
                  if (imageFld != null) {
                     this.addRenderScreenInfo(renderScreen, imageFld, this._currentIndex, this._imgFld.getIndex() != -1 ? this._imgFld : null);
                     this.addImageFieldToScreen(imageFld);
                     return;
                  }
               }
            }
         }
      }
   }

   private final void addImageFieldToScreen(Field imageFld) {
      if (!ObjectUtilities.objEqual(imageFld, this._imgFld) || imageFld.getIndex() == -1) {
         if (imageFld.getIndex() != -1) {
            imageFld.getManager().delete(imageFld);
         }

         if (this.getFieldCount() > 0) {
            this.deleteAll();
         }

         this.add(imageFld);
      }
   }

   private final void addDummyFieldToScreen() {
      if (this._dummyFld.getIndex() == -1) {
         this.deleteAll();
         this.add(this._dummyFld);
      }
   }

   private final synchronized EncodedImage getDisplayField(int index) {
      EncodedImage img = null;
      if (this._renderedDomIDs != null && index >= 0 && index < this._renderedDomIDs.length) {
         Object obj = this._fullScaleImages.get(index);
         if (obj instanceof DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo) {
            img = ((DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo)obj)._image;
         }

         if (img == null) {
            obj = this._pageAccessedImgs.get(index);
            if (obj instanceof EncodedImage) {
               img = (EncodedImage)obj;
            }
         }

         if (img == null) {
            DocViewTextDisplayField$PageBreakStatusField pgBrk = this.this$0.getPageStatusFieldWithDomID(this._renderedDomIDs[index]);
            if (pgBrk != null && pgBrk._pageDisplay != null && !pgBrk._isDummy) {
               img = ((CustomBitmapField)pgBrk._pageDisplay).getEncodedImage();
               if (img != null) {
                  this._pageAccessedImgs.put(index, img);
               }
            }
         }

         if (img == null) {
            return this.parseAndProcess(this.this$0.parseCustomData((byte)0, this._renderedDomIDs[index]), index);
         }

         if (img.getScaleX32() != 65536 || img.getScaleY32() != 65536) {
            img.setScale(1);
         }
      }

      return img;
   }

   private final synchronized EncodedImage parseAndProcess(Object param1, int param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 3
      // 02: aload 1
      // 03: instanceof net/rim/device/apps/internal/docview/gui/DocViewParsingData
      // 06: ifne 0c
      // 09: goto f3
      // 0c: aload 0
      // 0d: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen._renderedDomIDs [Ljava/lang/String;
      // 10: ifnonnull 16
      // 13: goto f3
      // 16: iload 2
      // 17: ifge 1d
      // 1a: goto f3
      // 1d: iload 2
      // 1e: aload 0
      // 1f: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen._renderedDomIDs [Ljava/lang/String;
      // 22: arraylength
      // 23: if_icmplt 29
      // 26: goto f3
      // 29: aload 1
      // 2a: checkcast net/rim/device/apps/internal/docview/gui/DocViewParsingData
      // 2d: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.getImages ()[Lnet/rim/device/apps/internal/docview/gui/DocViewImageData;
      // 30: arraylength
      // 31: bipush 1
      // 32: if_icmple 39
      // 35: bipush 1
      // 36: goto 3a
      // 39: bipush 0
      // 3a: istore 4
      // 3c: aconst_null
      // 3d: astore 5
      // 3f: iload 4
      // 41: ifeq 5e
      // 44: aload 1
      // 45: checkcast net/rim/device/apps/internal/docview/gui/DocViewParsingData
      // 48: aload 0
      // 49: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen._renderedDomIDs [Ljava/lang/String;
      // 4c: iload 2
      // 4d: aaload
      // 4e: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.getObjectWithDOMID (Ljava/lang/String;)Ljava/lang/Object;
      // 51: checkcast net/rim/device/apps/internal/docview/gui/DocViewImageData
      // 54: astore 5
      // 56: goto 6e
      // 59: astore 6
      // 5b: goto 6e
      // 5e: aload 1
      // 5f: checkcast net/rim/device/apps/internal/docview/gui/DocViewParsingData
      // 62: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.getImages ()[Lnet/rim/device/apps/internal/docview/gui/DocViewImageData;
      // 65: bipush 0
      // 66: aaload
      // 67: astore 5
      // 69: goto 6e
      // 6c: astore 6
      // 6e: aload 5
      // 70: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewImageData.getImageContents ()[B
      // 73: astore 6
      // 75: aload 6
      // 77: bipush 0
      // 78: aload 6
      // 7a: arraylength
      // 7b: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage ([BII)Lnet/rim/device/api/system/EncodedImage;
      // 7e: astore 3
      // 7f: aload 3
      // 80: ifnull f3
      // 83: new net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo
      // 86: dup
      // 87: aload 0
      // 88: aconst_null
      // 89: invokespecial net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo.<init> (Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen;Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$1;)V
      // 8c: astore 7
      // 8e: aload 7
      // 90: aload 0
      // 91: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen._renderedDomIDs [Ljava/lang/String;
      // 94: iload 2
      // 95: aaload
      // 96: putfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo._arbDomID Ljava/lang/String;
      // 99: aload 7
      // 9b: aload 3
      // 9c: putfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo._image Lnet/rim/device/api/system/EncodedImage;
      // 9f: aload 7
      // a1: iload 4
      // a3: ifeq af
      // a6: aload 0
      // a7: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen._renderedDomIDs [Ljava/lang/String;
      // aa: iload 2
      // ab: aaload
      // ac: goto b6
      // af: aload 1
      // b0: checkcast net/rim/device/apps/internal/docview/gui/DocViewParsingData
      // b3: invokestatic net/rim/device/apps/internal/docview/gui/DocViewDisplayField.getFirstArbitraryDomID (Lnet/rim/device/apps/internal/docview/gui/DocViewParsingData;)Ljava/lang/String;
      // b6: putfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo._imageDomID Ljava/lang/String;
      // b9: aload 7
      // bb: aload 5
      // bd: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewImageData.getOriginalHeight ()I
      // c0: putfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo._originalHeight I
      // c3: aload 7
      // c5: aload 5
      // c7: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewImageData.getOriginalWidth ()I
      // ca: putfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo._originalWidth I
      // cd: aload 7
      // cf: aload 0
      // d0: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen.this$0 Lnet/rim/device/apps/internal/docview/gui/DocViewTextDisplayField;
      // d3: iload 2
      // d4: aload 0
      // d5: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen._renderedDomIDs [Ljava/lang/String;
      // d8: arraylength
      // d9: bipush 0
      // da: invokespecial net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField.getPageBreakDescription (IIZ)Ljava/lang/String;
      // dd: putfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen$SlideDisplayInfo._title Ljava/lang/String;
      // e0: aload 0
      // e1: getfield net/rim/device/apps/internal/docview/gui/DocViewTextDisplayField$DocViewPlayScreen._fullScaleImages Lnet/rim/device/api/util/IntHashtable;
      // e4: iload 2
      // e5: aload 7
      // e7: invokevirtual net/rim/device/api/util/IntHashtable.put (ILjava/lang/Object;)Ljava/lang/Object;
      // ea: pop
      // eb: aload 3
      // ec: areturn
      // ed: astore 6
      // ef: aload 3
      // f0: areturn
      // f1: astore 6
      // f3: aload 3
      // f4: areturn
      // try (33 -> 42): 43 null
      // try (45 -> 51): 52 null
      // try (53 -> 115): 117 null
      // try (53 -> 115): 120 null
   }

   private final synchronized void setDisplayField(boolean forceDownload) {
      this._gotCurrentSlide = false;
      if (this._currentIndex >= 0 && this._renderedDomIDs != null && this._currentIndex < this._renderedDomIDs.length) {
         if (this._usedDisplayScreens.containsKey(this._currentIndex)) {
            this.setDisplayFieldDetailed();
            this._gotCurrentSlide = true;
            return;
         }

         EncodedImage img = this.getDisplayField(this._currentIndex);
         if (img != null) {
            this._imgFld.setImage(img);
            this.addImageFieldToScreen(this._imgFld);
            this._gotCurrentSlide = true;
            return;
         }

         this.addDummyFieldToScreen();
         this.this$0._application.invokeLater(new DocViewTextDisplayField$DocViewPlayScreen$10(this, forceDownload));
      }
   }

   private final void addRenderScreenInfo(Screen renderScreen, Field imgFld, int index, ZoomBitmapField existingImage) {
      if (renderScreen != null
         && imgFld != null
         && !(this._usedDisplayScreens.get(index) instanceof DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo)) {
         DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo newInfo = new DocViewTextDisplayField$DocViewPlayScreen$EmbeddedScreenInfo(
            this, renderScreen, imgFld
         );
         if (imgFld instanceof DocViewDisplayField
            && ((DocViewDisplayField)imgFld).getFieldCount() > 0
            && ((DocViewDisplayField)imgFld).getField(0) instanceof ZoomBitmapField) {
            ZoomBitmapField zbf = (ZoomBitmapField)((DocViewDisplayField)imgFld).getField(0);
            this.initEmbeddedScreenInfo(newInfo, zbf);
            if (existingImage != null) {
               zbf.setScale(existingImage.getScale(), existingImage.getImageX(), existingImage.getImageY(), existingImage.getRotationValue());
            } else {
               zbf.setScale(65536, 0, 0, this._isInitialRotated ? 90 : 0);
            }
         }

         this._usedDisplayScreens.clear();
         this._usedDisplayScreens.put(index, newInfo);
      }
   }

   private final void checkSlidesImpl() {
      if (this._renderedDomIDs != null) {
         for (int i = Math.max(this._lastRequestedSlideIndex, 0); i < this._renderedDomIDs.length; i++) {
            if (this._fullScaleImages.get(i) == null && this._pageAccessedImgs.get(i) == null) {
               DocViewTextDisplayField$PageBreakStatusField pgBrk = this.this$0.getPageStatusFieldWithDomID(this._renderedDomIDs[i]);
               if (pgBrk != null && pgBrk._pageDisplay != null && !pgBrk._isDummy) {
                  EncodedImage img = ((CustomBitmapField)pgBrk._pageDisplay).getEncodedImage();
                  if (img != null) {
                     continue;
                  }
               }

               if (!this.this$0.hasRenderedData(this._renderedDomIDs[i])) {
                  if (i > this._lastRequestedSlideIndex) {
                     this.this$0.retrieveSlide(this._renderedDomIDs[i], true, true);
                     this._lastRequestedSlideIndex = i;
                  }

                  synchronized (this) {
                     this._checkSlidesInProgress = false;
                     return;
                  }
               }
            }
         }

         this._lastRequestedSlideIndex = this._renderedDomIDs.length;
      }

      synchronized (this) {
         this._checkSlidesInProgress = false;
      }
   }

   private final synchronized void checkSlides() {
      if (!this._checkSlidesInProgress && this._renderedDomIDs != null && this._lastRequestedSlideIndex < this._renderedDomIDs.length) {
         this._checkSlidesInProgress = true;
         new DocViewTextDisplayField$DocViewPlayScreen$11(this).start();
      }
   }
}
