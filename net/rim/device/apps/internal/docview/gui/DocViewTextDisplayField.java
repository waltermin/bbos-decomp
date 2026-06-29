package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.theme.Theme;
import net.rim.device.api.ui.theme.Theme$Writer;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.ui.theme.ThemeAttributeSet$Writer;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

final class DocViewTextDisplayField extends DocViewDisplayField {
   private boolean _inSlideshowMode;
   private final int AUTOMORE_TRIGGER;
   private DocViewTextDisplayField$TextControlInfo[] _FullDocFieldVector;
   private DocViewTextDisplayField$TextControlInfo[] _TOCFieldVector;
   private byte _displayMode;
   private IntHashtable _bookmarkInfoMap;
   private DocViewTextDisplayField$TextControlInfo _createdNewTextFieldToFullScreen;
   private int _lastBlockFullContentSize;
   protected String _crtStringNotFoundInChunks;
   private int _lastFindSelectedCtrlIndex;
   private int _lastFindSelectedOffset;
   private int _crtFontSize;
   private int _crtFontStyle;
   private String _crtFontName;
   private boolean _useOriginalFont;
   private StringBuffer _hashCodeBuffer;
   private StringBuffer _currentTrackChangeDescription;
   private DocViewTrackChange _crtTrackChange;
   private StringBuffer _findStringBuffer;
   private DocViewDisplayField$CenterStatusField _moreStatusField;
   private SimpleSortingVector _skippedDataFields;
   private SimpleSortingVector _embeddedStatusFields;
   private byte _latestLinkType;
   private int _latestTextLinkBookmarkTarget;
   private int _latestTextLinkChunkHint;
   private String _latestEmbLinkInfo;
   private boolean _useLatestLink;
   private Object _lastSelectedCtrl;
   private int _lastSelectedCtrlPos;
   private boolean _hasPageBreaks;
   private final String _strRenderDomIDs;
   private boolean _duringLayoutActivity;
   private boolean _incLayoutRequested;
   private DocViewTextDisplayField$DocViewPlayScreen _playScreen;
   DocViewTextDisplayField$CacheTextOffsetInfo _cacheOffsetInfo;
   private final boolean _isPresentation;
   private final boolean _isSpecificBgDisplay;
   private static final int MAXCONTROLCHARS = 2000;
   private static final int RIGHTTHRESHOLD = 500;
   private static final int LEFTTHRESHOLD = 50;
   private static final byte DISPLAYMODE_BOTH = 0;
   private static final byte DISPLAYMODE_TEXT = 1;

   DocViewTextDisplayField(
      DocViewDataProvider dataProvider,
      DocViewGUIProvider guiProvider,
      DocViewNotify notifyObject,
      DocViewParser docData,
      String startSummaryDomID,
      String endSummaryDomID,
      int themeBgColor,
      int themeForeColor,
      boolean isPresentation,
      boolean isSpecificBgDisplay
   ) {
   }

   @Override
   protected final boolean init() {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   @Override
   protected final void callOnceOnDisplay() {
      if (this._hasPageBreaks) {
         DocViewTextDisplayField$PageBreakStatusField pgBrk = this.getNextPageStatusField(this.getFieldWithFocusIndex());
         if (pgBrk != null) {
            this.checkSlideRetrieveOnMoveImpl(pgBrk);
         }
      }

      if (super._fullDocState && this.allowMultipleItems() && this.hasRenderedSlides()) {
         this._inSlideshowMode = true;
      }

      super.callOnceOnDisplay();
   }

   @Override
   protected final void onDisplay() {
      super.onDisplay();
      if (this._inSlideshowMode && !this.play()) {
         this._inSlideshowMode = false;
      }
   }

   @Override
   protected final void applyTheme() {
      super.applyTheme();
      if (this._isSpecificBgDisplay && super._themeBgColor != -1 && super._themeForeColor != -1) {
         Theme t = ThemeManager.getActiveTheme();
         if (t != null) {
            Theme$Writer themeWriter = ThemeManager.getActiveTheme().getWriterInternalDeprecated();
            ThemeAttributeSet$Writer writer = themeWriter.createThemeAttributeSetWriter(null);
            writer.setColor(0, super._themeBgColor);
            writer.setColor(1, super._themeForeColor);
            ThemeAttributeSet attr = writer.getThemeAttributeSet();
            this.setThemeAttributesSpecial(attr, null);
         }
      }
   }

   protected final DocViewTextDisplayField$CacheTextOffsetInfo getCurrentTextOffset() {
      Field fieldWithFocus = this.getFieldWithFocus();
      RichTextField textField = null;
      if (!(fieldWithFocus instanceof RichTextField)) {
         if (fieldWithFocus != null && fieldWithFocus.getIndex() > 0) {
            for (int i = fieldWithFocus.getIndex() - 1; i >= 0; i--) {
               Field fld = this.getField(i);
               if (fld instanceof RichTextField && ((RichTextField)fld).getTextLength() > 0) {
                  textField = (RichTextField)fld;
                  break;
               }
            }
         }
      } else {
         textField = (RichTextField)fieldWithFocus;
      }

      if (textField != null) {
         int iFields = this._FullDocFieldVector.length;
         if (iFields > 0) {
            for (int i = 0; i < iFields; i++) {
               DocViewTextDisplayField$TextControlInfo crtControl = this._FullDocFieldVector[i];
               if (ObjectUtilities.objEqual(crtControl._richTextField, textField)) {
                  return crtControl.getTextPosInfo(
                     ObjectUtilities.objEqual(fieldWithFocus, textField) ? textField.getCursorPosition() : textField.getTextLength() - 1
                  );
               }
            }
         }
      }

      return null;
   }

   private final void createSkippedContentVector() {
      if (this._skippedDataFields == null) {
         this._skippedDataFields = new SimpleSortingVector();
         this._skippedDataFields.setSortComparator(new DocViewTextDisplayField$2(this));
         this._skippedDataFields.setSort(true);
      }
   }

   private final void createEmbeddedContentVector() {
      if (this._embeddedStatusFields == null) {
         this._embeddedStatusFields = new SimpleSortingVector();
         this._embeddedStatusFields.setSortComparator(new DocViewTextDisplayField$3(this));
         this._embeddedStatusFields.setSort(true);
      }
   }

   @Override
   protected final void processNewData(int currentBlockIndex) {
      super.processNewData(currentBlockIndex);
      if (this._duringLayoutActivity) {
         super._application.invokeAndWait(new DocViewTextDisplayField$4(this, currentBlockIndex));
      } else {
         this.processNewDataImpl(currentBlockIndex);
      }
   }

   private final void processNewDataImpl(int currentBlockIndex) {
      Runnable[] updates = new Runnable[0];
      this.doProcessNewData(currentBlockIndex, updates);
      int size = updates.length;
      if (size > 0) {
         Runnable[] appUpdates = updates;
         super._application.invokeAndWait(new DocViewTextDisplayField$5(this, appUpdates));
      }

      Runnable[] var5 = null;
   }

   private final void executeUpdates(Runnable[] updates) {
      int size = updates.length;
      int appendIndex = -1;

      for (int i = 0; i < size; i++) {
         if (updates[i] instanceof DocViewTextDisplayField$DocViewInsertFieldRunnable) {
            updates[i].run();
            DocViewTextDisplayField$DocViewInsertFieldRunnable runn = (DocViewTextDisplayField$DocViewInsertFieldRunnable)updates[i];
            if (runn._manager == this) {
               appendIndex = runn._field.getIndex() + 1;
            }

            updates[i] = null;
         }
      }

      if (appendIndex >= 0) {
         for (int i = 0; i < size; i++) {
            Runnable var10000 = updates[i];
            if (updates[i] instanceof DocViewTextDisplayField$DocViewAppendFieldRunnable) {
               ((DocViewTextDisplayField$DocViewAppendFieldRunnable)var10000).setIndex(appendIndex);
               updates[i].run();
               updates[i] = null;
               appendIndex++;
            }
         }
      }

      for (int i = 0; i < size; i++) {
         if (updates[i] instanceof DocViewTextDisplayField$DocViewDeleteFieldRunnable) {
            updates[i].run();
            updates[i] = null;
         }
      }

      for (int i = 0; i < size; i++) {
         if (updates[i] != null) {
            updates[i].run();
            updates[i] = null;
         }
      }

      if (this._hasPageBreaks && !this.allowMultipleItems()) {
         boolean lastFieldEncountered = false;
         int fldCount = this.getFieldCount();

         for (int i = fldCount - 1; i >= 0; i--) {
            Field crtFld = this.getField(i);
            if (crtFld instanceof DocViewTextDisplayField$PageBreakStatusField) {
               if (!lastFieldEncountered) {
                  lastFieldEncountered = true;
               } else {
                  ((DocViewTextDisplayField$PageBreakStatusField)crtFld)._invisible = false;
               }
            }
         }
      }
   }

   @Override
   protected final boolean incrementalLayout(int index, int added, int deleted) {
      if (this._duringLayoutActivity) {
         this._incLayoutRequested = true;
         return true;
      } else {
         return super.incrementalLayout(index, added, deleted);
      }
   }

   private final void doProcessNewData(int currentBlockIndex, Runnable[] updates) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   protected final void previewBitmapFieldIsInvalid(String domID) {
      if (domID != null && this._embeddedStatusFields != null && this._embeddedStatusFields.size() > 0) {
         for (int i = this._embeddedStatusFields.size() - 1; i >= 0; i--) {
            DocViewTextDisplayField$EmbeddedStatusField fld = (DocViewTextDisplayField$EmbeddedStatusField)this._embeddedStatusFields.elementAt(i);
            if (domID.compareTo(fld._domID) == 0) {
               fld._isDummy = true;
               if (fld._previewField instanceof BitmapField) {
                  ((BitmapField)fld._previewField).setBitmap(Bitmap.getPredefinedBitmap(1));
                  return;
               }
               break;
            }
         }
      }
   }

   private final void addUpdateAction(Runnable[] updates, Runnable action) {
      Arrays.add(updates, action);
   }

   @Override
   protected final Object getCurrentTrackChangeDescription() {
      Object retValue = null;
      DocViewTextDisplayField$TextControlInfo crtControl = this.getCurrentActiveTextControl(super._fullDocState);
      if (crtControl == null) {
         if (this._crtTrackChange != null) {
            this._crtTrackChange = null;
         }
      } else if (crtControl._richTextField != null) {
         Object crtRegion = crtControl._richTextField.getObjRegionCookie(crtControl._richTextField.getRegion(crtControl._richTextField.getCursorPosition()));
         if (crtRegion instanceof DocViewTrackChange || crtRegion instanceof DocViewComplexRegionInfo) {
            DocViewTrackChange trackChange = !(crtRegion instanceof DocViewTrackChange)
               ? ((DocViewComplexRegionInfo)crtRegion)._trackChange
               : (DocViewTrackChange)crtRegion;
            if (!trackChange.identical(this._crtTrackChange)) {
               if (this._currentTrackChangeDescription == null) {
                  this._currentTrackChangeDescription = new StringBuffer(32);
               }

               this._currentTrackChangeDescription.setLength(0);
               if (trackChange._trackChangeAuthor != null) {
                  this._currentTrackChangeDescription.append(trackChange._trackChangeAuthor);
               } else {
                  this._currentTrackChangeDescription.append(DocViewDisplayField._resources.getString(86));
               }

               if (trackChange._trackChangeDateTime != null) {
                  this._currentTrackChangeDescription.append(',');
                  this._currentTrackChangeDescription.append(' ');
                  this._currentTrackChangeDescription.append(trackChange._trackChangeDateTime);
               }

               this._currentTrackChangeDescription.append(':');
               this._currentTrackChangeDescription.append(' ');
               this._currentTrackChangeDescription
                  .append(trackChange._trackChangeType == 2 ? DocViewDisplayField._resources.getString(84) : DocViewDisplayField._resources.getString(85));
               this._crtTrackChange = trackChange;
            }

            return this._currentTrackChangeDescription;
         }

         if (this._crtTrackChange != null) {
            this._crtTrackChange = null;
            return retValue;
         }
      }

      return retValue;
   }

   @Override
   protected final boolean hasMultipleItems() {
      return false;
   }

   private final String getMoreStatusFieldText() {
      if (!this.isMoreAvailable()) {
         return "";
      }

      StringBuffer retValue = new StringBuffer(" ");
      retValue.append(EmailResources.getString(82));
      int bVal = this.getMoreAvailableBytes();
      if (bVal > 0) {
         int val = Math.max(bVal * 10 / 1024, 1);
         retValue.append(String.valueOf(val / 10));
         val %= 10;
         if (val != 0) {
            retValue.append('.');
            retValue.append(String.valueOf(val));
         }

         retValue.append(' ');
         retValue.append(DocViewDisplayField._resources.getString(49));
         retValue.append(' ');
      }

      return retValue.toString();
   }

   private final boolean isTextLinkActive(DocViewTextHyperlinkInfo hyperlink) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final boolean isRetrievedHyperlink(DocViewHyperlinkInfo hyperlink) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final boolean hasRenderedSlides() {
      return this._strRenderDomIDs != null && this._strRenderDomIDs.length() > 0;
   }

   private final boolean canJumpAtCurrentIndex() {
      this._useLatestLink = false;

      try {
         DocViewTextDisplayField$TextControlInfo crtControl = this.getCurrentActiveTextControl(super._fullDocState);
         DocViewHyperlinkInfo hyperInfo = (DocViewHyperlinkInfo)crtControl._richTextField
            .getHyperlinkInfo(crtControl._richTextField.getRegion(crtControl._richTextField.getCursorPosition()));
         if (hyperInfo == null) {
            hyperInfo = crtControl.getHyperlink(crtControl._richTextField.getCursorPosition());
         }

         if (hyperInfo._linkType != 0 || this.isTextLinkActive((DocViewTextHyperlinkInfo)hyperInfo)) {
            this._latestLinkType = hyperInfo._linkType;
            if (hyperInfo._linkType == 0) {
               this._latestTextLinkChunkHint = ((DocViewTextHyperlinkInfo)hyperInfo)._iLinkTargetChunkHint;
               this._latestTextLinkBookmarkTarget = ((DocViewTextHyperlinkInfo)hyperInfo)._iLinkTargetBookmark;
            } else {
               this._latestEmbLinkInfo = ((DocViewEmbHyperlinkInfo)hyperInfo)._linkTargetString;
            }

            this._useLatestLink = true;
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }

   private final void notifyChangeFont(DocViewTextDisplayField$TextControlInfo[] textInfoVector) {
      int iFields = textInfoVector.length;
      if (iFields > 0) {
         for (int i = 0; i < iFields; i++) {
            textInfoVector[i].notifyChangeFonts();
         }
      }
   }

   @Override
   protected final void optionsChanged() {
      DocViewOptions options = DocViewOptions.getOptions();
      int newPixelSize = options.getDocFontSize();
      String newFontName = options.getDocFontName();
      int newStyle = options.getDocFontStyle();
      boolean useDefaultFontChanged = options.getUseOriginalDocFont() != this._useOriginalFont;
      boolean fontSizeChanged = newPixelSize != this._crtFontSize;
      boolean fontNameChanged = !newFontName.equals(this._crtFontName);
      boolean fontStyleChanged = newStyle != this._crtFontStyle;
      if (fontSizeChanged || fontNameChanged || fontStyleChanged || useDefaultFontChanged) {
         if (fontNameChanged) {
            label85:
            try {
               super._fontFactory.setFontFamilyName(newFontName);
            } finally {
               break label85;
            }

            this._crtFontName = newFontName;
         }

         if (fontSizeChanged) {
            this._crtFontSize = newPixelSize;
         }

         if (useDefaultFontChanged) {
            this._useOriginalFont = options.getUseOriginalDocFont();
         }

         if (fontStyleChanged) {
            this._crtFontStyle = newStyle;
         }

         super._application.invokeAndWait(new DocViewTextDisplayField$6(this));
      }
   }

   protected final boolean jumpToBookmark(int bookmark) {
      DocViewTextDisplayField$TextControlInfo targetControl = (DocViewTextDisplayField$TextControlInfo)this._bookmarkInfoMap.get(bookmark);
      if (targetControl != null) {
         int targetIdx = targetControl._bookmarkHash.get(bookmark);
         if (targetIdx != -1) {
            super._application.suspendPainting(true);

            label32:
            try {
               if (!super._fullDocState) {
                  this.toggleDisplayMode();
               }

               this.putCursorPosition(targetControl, targetIdx, true, true);
            } finally {
               break label32;
            }

            super._application.suspendPainting(false);
            this.invalidate();
            super._application.doPainting();
            return true;
         }
      }

      return false;
   }

   @Override
   protected final void jump() {
      if (super._fullDocState) {
         Field fld = this.getFieldWithFocus();
         if (fld instanceof DocViewTextDisplayField$EmbeddedStatusField) {
            this.displayEmbeddedObject(((DocViewTextDisplayField$EmbeddedStatusField)fld)._domID);
            return;
         }

         if (fld instanceof CustomBitmapField) {
            if (((CustomBitmapField)fld)._domID != null) {
               this.displayEmbeddedObject(((CustomBitmapField)fld)._domID);
               return;
            }

            if (this._hasPageBreaks) {
               DocViewTextDisplayField$PageBreakStatusField pgBrk = this.getNextPageStatusField(fld.getIndex());
               if (pgBrk != null && ObjectUtilities.objEqual(pgBrk._pageDisplay, fld)) {
                  Screen renderedScreen = this.getRenderedScreen(
                     pgBrk._domID,
                     pgBrk._renderedName,
                     ((CustomBitmapField)fld).getEncodedImage(),
                     pgBrk._originalDisplayWidth,
                     pgBrk._originalDisplayHeight,
                     pgBrk._imageDomID
                  );
                  if (renderedScreen != null) {
                     if (renderedScreen instanceof ModelScreen) {
                        ((ModelScreen)renderedScreen).go(true);
                        return;
                     }

                     super._application.pushModalScreen(renderedScreen);
                  }
               }
            }

            return;
         }
      }

      if (this._useLatestLink || this.canJumpAtCurrentIndex()) {
         if (this._useLatestLink) {
            switch (this._latestLinkType) {
               case 0:
               default:
                  try {
                     if (!this._bookmarkInfoMap.containsKey(this._latestTextLinkBookmarkTarget)) {
                        byte status = this.getTargetBlockStatus(this._latestTextLinkChunkHint);
                        if (status == 3) {
                           return;
                        }

                        if (status == 2) {
                           return;
                        }

                        if (status == 1 && !this.loadTargetBlock(this._latestTextLinkChunkHint)) {
                           return;
                        }
                     }

                     this.jumpToBookmark(this._latestTextLinkBookmarkTarget);
                     return;
                  } finally {
                     return;
                  }
               case 1:
               case 3:
               case 4:
               case 5:
               case 6:
                  this.displayEmbeddedObject(this._latestEmbLinkInfo);
               case -1:
               case 2:
            }
         }
      }
   }

   private final boolean revert() {
      if (!super._fullDocState) {
         return false;
      }

      if (this._cacheOffsetInfo != null && this._cacheOffsetInfo._blockIndex >= 0 && this._cacheOffsetInfo._charOffset >= 0) {
         byte status = this.getTargetBlockStatus(this._cacheOffsetInfo._blockIndex);
         if (status != 3 && status != 2 && (status != 1 || this.loadTargetBlock(this._cacheOffsetInfo._blockIndex))) {
            int controls = this._FullDocFieldVector.length;
            if (controls > 0) {
               for (int i = 0; i < controls; i++) {
                  DocViewTextDisplayField$TextControlInfo crtControl = this._FullDocFieldVector[i];
                  int absOffset = crtControl.getAbsOffset(this._cacheOffsetInfo);
                  if (absOffset >= 0) {
                     this.putCursorPosition(crtControl, absOffset, false, true);
                     return true;
                  }
               }
            }
         }
      }

      this.setExtremePosition(true);
      return true;
   }

   @Override
   protected final boolean toggleDisplayMode() {
      Object lastSelectedCtrl = this._lastSelectedCtrl;
      int lastSelectedPos = this._lastSelectedCtrlPos;
      this._lastSelectedCtrl = this.getCurrentActiveTextControl(super._fullDocState);
      if (this._lastSelectedCtrl == null) {
         this._lastSelectedCtrl = super._fullDocState ? this.getFieldWithFocus() : ((Manager)super._docViewTocManager).getFieldWithFocus();
      } else {
         this._lastSelectedCtrlPos = ((DocViewTextDisplayField$TextControlInfo)this._lastSelectedCtrl)._richTextField.getCursorPosition();
      }

      if (super.toggleDisplayMode()) {
         if (lastSelectedCtrl == null) {
            DocViewTextDisplayField$TextControlInfo[] activeVector = super._fullDocState ? this._FullDocFieldVector : this._TOCFieldVector;
            if (activeVector.length > 0) {
               lastSelectedCtrl = activeVector[0];
               lastSelectedPos = 0;
            }
         }

         if (lastSelectedCtrl instanceof DocViewTextDisplayField$TextControlInfo) {
            this.putCursorPosition((DocViewTextDisplayField$TextControlInfo)lastSelectedCtrl, lastSelectedPos, true, false);
            return true;
         }

         if (lastSelectedCtrl instanceof Field && ((Field)lastSelectedCtrl).getManager() != null) {
            ((Field)lastSelectedCtrl).setFocus();
         }

         return true;
      } else {
         this._lastSelectedCtrl = lastSelectedCtrl;
         this._lastSelectedCtrlPos = lastSelectedPos;
         return false;
      }
   }

   private final void toggleImgDisplayMode() {
      if (super._fullDocState && this.hasRenderedSlides()) {
         switch (this._displayMode) {
            case -1:
               break;
            case 0:
            default:
               this.setDisplayMode((byte)1, true);
               return;
            case 1:
               if (this.allowMultipleItems()) {
                  this.play();
                  return;
               }

               if (this._hasPageBreaks) {
                  this.setDisplayMode((byte)0, true);
               }
         }
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      switch (key) {
         case ' ':
            if ((status & 1) == 0) {
               int direction = (status & 2) == 0 ? 512 : 256;
               Field oldFldWithFocus = this.getFieldWithFocus();
               this.getScreen().scroll(direction);
               Field newFldWithFocus = this.getFieldWithFocus();
               boolean moved = true;
               if (oldFldWithFocus != null && newFldWithFocus != null && oldFldWithFocus == newFldWithFocus && oldFldWithFocus instanceof CustomBitmapField) {
                  moved = false;
                  int idx = this.getFieldWithFocusIndex();
                  int fldCount = this.getFieldCount();
                  if (idx >= 0 && idx < fldCount) {
                     if (direction == 512) {
                        for (int i = idx + 1; i < fldCount; i++) {
                           Field fld = this.getField(i);
                           if (fld != null && fld.isFocusable()) {
                              fld.setFocus();
                              moved = true;
                              break;
                           }
                        }
                     } else {
                        for (int i = idx - 1; i >= 0; i--) {
                           Field fld = this.getField(i);
                           if (fld != null && fld.isFocusable()) {
                              fld.setFocus();
                              moved = true;
                              break;
                           }
                        }
                     }
                  }
               }

               if (moved) {
                  this.checkSlideRetrieveOnMove(direction == 512 ? 1 : -1);
               }

               return true;
            }
         default:
            return super.keyChar(key, status, time);
      }
   }

   @Override
   protected final boolean docViewHandleCharRegular(char ch, int altStatus, int time) {
      switch (ch) {
         case 'G':
         case 'g':
            return this.revert();
         case 'J':
         case 'j':
            this._useLatestLink = false;
            return super.docViewHandleCharRegular(ch, altStatus, time);
         case 'M':
         case 'm':
            this.toggleImgDisplayMode();
            return true;
         case 'S':
         case 's':
            this.play();
            return true;
         default:
            return super.docViewHandleCharRegular(ch, altStatus, time);
      }
   }

   @Override
   protected final boolean docViewHandleCharReducedKeyboard(char ch, int altStatus, int time) {
      if (altStatus == 0) {
         Screen activeScreen = null;

         label42:
         try {
            activeScreen = Ui.getUiEngine().getActiveScreen();
         } finally {
            break label42;
         }

         if (activeScreen != null) {
            switch (ch) {
               case 'A':
               case 'a':
                  this.play();
                  return true;
               case 'B':
               case 'b':
                  return activeScreen.dispatchTrackwheelEvent(519, 1, 0, time);
               case 'D':
               case 'd':
                  return activeScreen.dispatchTrackwheelEvent(519, -1, 1, time);
               case 'G':
               case 'g':
                  return this.revert();
               case 'J':
               case 'j':
                  return activeScreen.dispatchTrackwheelEvent(519, 1, 1, time);
               case 'M':
               case 'm':
                  activeScreen.scroll(512);
                  return true;
               case 'T':
               case 't':
                  return activeScreen.dispatchTrackwheelEvent(519, -1, 0, time);
               case 'U':
               case 'u':
                  activeScreen.scroll(256);
                  return true;
               case 'Z':
               case 'z':
                  this.toggleImgDisplayMode();
                  return true;
            }
         }
      }

      return super.docViewHandleCharReducedKeyboard(ch, altStatus, time);
   }

   private final void markControlsDirty(boolean fullDocState) {
      DocViewTextDisplayField$TextControlInfo[] activeVector = fullDocState ? this._FullDocFieldVector : this._TOCFieldVector;

      for (int i = activeVector.length - 1; i >= 0; i--) {
         activeVector[i]._linksDirty = true;
      }
   }

   @Override
   protected final void embeddedObjectInitialChunkArrived(DocViewParser docData, String domID) {
      this.embeddedInitialChunkDataModified(domID);
      super.embeddedObjectInitialChunkArrived(docData, domID);
   }

   @Override
   protected final void embeddedInitialChunkDataModified(String targetDOMID) {
      super._application.invokeLater(new DocViewTextDisplayField$7(this, targetDOMID));
   }

   private final void refreshCrtPosition(boolean fullDocMode) {
      try {
         DocViewTextDisplayField$TextControlInfo crtControl = this.getCurrentActiveTextControl(fullDocMode);
         this.putCursorPosition(crtControl, crtControl._richTextField.getCursorPosition(), true, false);
      } finally {
         return;
      }
   }

   @Override
   protected final boolean hasTOC() {
      return this._TOCFieldVector.length > 0;
   }

   private final void checkSlideRetrieveOnMoveImpl(DocViewTextDisplayField$PageBreakStatusField pgBrk) {
      if (pgBrk._isDummy
         && pgBrk._domID != null
         && !pgBrk._inProcess
         && pgBrk._pageDisplay != null
         && pgBrk._pageDisplay.getIndex() >= 0
         && !this.isMoreRequestSent()) {
         pgBrk._inProcess = true;
         super._application.invokeLater(new DocViewTextDisplayField$8(this, pgBrk), 250, false);
      }
   }

   private final void checkSlideRetrieveOnMove(int amount) {
      if (this._hasPageBreaks && amount != 0) {
         DocViewTextDisplayField$PageBreakStatusField pgBrk = this.getNextPageStatusField(this.getFieldWithFocusIndex());
         if (pgBrk != null) {
            this.checkSlideRetrieveOnMoveImpl(pgBrk);
         }
      }
   }

   @Override
   protected final int moveFocus(int amount, int status, int time) {
      int retValue = super.moveFocus(amount, status, time);
      this.checkSlideRetrieveOnMove(amount);
      return retValue;
   }

   @Override
   public final void perform(int menuCode, Object cookie) {
      switch (menuCode) {
         case 22:
            if (cookie instanceof DocViewTextDisplayField$PageBreakStatusField) {
               DocViewTextDisplayField$PageBreakStatusField pgBrk = (DocViewTextDisplayField$PageBreakStatusField)cookie;
               if (pgBrk._isDummy) {
                  this.privateProcessPageDisplay(pgBrk, null, false);
                  if (pgBrk._isDummy) {
                     this.retrieveSlide(pgBrk._domID, false, true);
                  }
               }
            }

            return;
         case 29:
            this.setDisplayMode((byte)1, true);
            return;
         case 30:
            this.setDisplayMode((byte)0, true);
            return;
         case 31:
            this.play();
            return;
         default:
            super.perform(menuCode, cookie);
      }
   }

   private final void retrieveSlide(String domID, boolean automore, boolean allowDuplicateRequest) {
      if (domID != null && (allowDuplicateRequest || !this.isMoreRequestSent())) {
         super._descriptor.reset();
         super._descriptor._commandCode = "RENDER";
         super._descriptor._partIndex = this.allowMultipleItems() ? 1006 : 1005;
         super._descriptor._chunkSize = 64000;
         super._descriptor._arbDomID = domID;
         super._descriptor._isImageRequest = true;
         super._descriptor._reqImageWidth = AttachmentViewerFactory.getDesiredRenderedWidth(this._isPresentation);
         super._descriptor._reqImageHeight = AttachmentViewerFactory.getDesiredRenderedHeight(this._isPresentation);
         this.executeMore(super._descriptor, automore, allowDuplicateRequest);
      }
   }

   private final boolean play() {
      if (super._fullDocState && this.allowMultipleItems() && this.hasRenderedSlides()) {
         if (this._playScreen == null) {
            this._playScreen = new DocViewTextDisplayField$DocViewPlayScreen(
               this, DocViewGUIInternalConstants.SCREEN_WIDTH < DocViewGUIInternalConstants.SCREEN_HEIGHT && this._isPresentation
            );
         } else {
            this._playScreen.initialize();
         }

         this._inSlideshowMode = true;
         super._application.pushModalScreen(this._playScreen);
         return true;
      } else {
         return false;
      }
   }

   private final void mainUpdateLayout() {
      this.updateLayout();
   }

   @Override
   protected final void addCustomMenuVerbs(Menu menu, int instance) {
      super.addCustomMenuVerbs(menu, instance);
      boolean skippedFieldSelected = false;
      boolean embFieldSelected = false;
      int nextSkippedBlockToRetrieve = -1;
      Field field = null;
      VerbMenuItem moreVerb = null;
      if (super._fullDocState) {
         field = this.getFieldWithFocus();
         label146:
         if (!(field instanceof DocViewTextDisplayField$SkippedStatusField)) {
            if (!(field instanceof DocViewTextDisplayField$EmbeddedStatusField)) {
               if (!(field instanceof CustomBitmapField)) {
                  break label146;
               }

               if (((CustomBitmapField)field)._domID == null) {
                  break label146;
               }
            }

            embFieldSelected = true;
         } else {
            nextSkippedBlockToRetrieve = ((DocViewTextDisplayField$SkippedStatusField)field)._nextBlockIndex;
            skippedFieldSelected = true;
         }

         if (!skippedFieldSelected && this.isMoreAvailable()) {
            moreVerb = new VerbMenuItem(new DocViewGuiVerb(9, 344064, EmailResources.getResourceBundle(), 80, this), 0);
            menu.add(moreVerb);
         }

         if (this._hasPageBreaks) {
            DocViewTextDisplayField$PageBreakStatusField pgNext = this.getNextPageStatusField(field.getIndex());
            DocViewTextDisplayField$PageBreakStatusField pgPrev = null;
            if (pgNext == null) {
               pgPrev = this.getPrevPageStatusField(field.getIndex());
            }

            if (pgNext != null && pgNext._pageDisplay != null || pgPrev != null && pgPrev._pageDisplay != null) {
               switch (this._displayMode) {
                  case -1:
                     break;
                  case 0:
                  default:
                     menu.add(new VerbMenuItem(new DocViewGuiVerb(29, 65552, DocViewDisplayField._resources, 26, this), 0));
                     break;
                  case 1:
                     menu.add(new VerbMenuItem(new DocViewGuiVerb(30, 65552, DocViewDisplayField._resources, 50, this), 0));
               }

               if (pgNext != null && ObjectUtilities.objEqual(pgNext._pageDisplay, field)) {
                  VerbMenuItem addon = null;
                  if (pgNext._isDummy) {
                     DocViewGuiVerb guiVerb = new DocViewGuiVerb(22, 131088, DocViewDisplayField._resources, 8, this);
                     guiVerb.setCookie(pgNext);
                     addon = new VerbMenuItem(guiVerb, 0);
                  } else {
                     addon = new VerbMenuItem(new DocViewGuiVerb(10, 131088, DocViewDisplayField._resources, 9, this), 0);
                  }

                  if (addon != null) {
                     menu.add(addon);
                     menu.setDefault(addon);
                  }
               }
            }
         }

         if (this.allowMultipleItems() && this.hasRenderedSlides()) {
            menu.add(new VerbMenuItem(new DocViewGuiVerb(31, 65552, DocViewDisplayField._resources, this._isPresentation ? 23 : 113, this), 0));
         }
      }

      if (!skippedFieldSelected && !embFieldSelected || !super._fullDocState) {
         this.canJumpAtCurrentIndex();
         if (!this._useLatestLink) {
            if (this.isMoreAvailable() && !AttachmentViewerFactory.isAutoMoreEnabled() && this.isLastCharSelected(super._fullDocState) && moreVerb != null) {
               menu.setDefault(moreVerb);
            }
         } else {
            VerbMenuItem jumpVerb = null;
            switch (this._latestLinkType) {
               case 0:
                  if (this._bookmarkInfoMap.containsKey(this._latestTextLinkBookmarkTarget)) {
                     jumpVerb = new VerbMenuItem(new DocViewGuiVerb(10, 131088, DocViewDisplayField._resources, 34, this), 0);
                  } else {
                     byte status = this.getTargetBlockStatus(this._latestTextLinkChunkHint);
                     if (status != 3) {
                        if (status != 2) {
                           jumpVerb = new VerbMenuItem(new DocViewGuiVerb(10, 131088, DocViewDisplayField._resources, 34, this), 0);
                        } else if (this.isMoreSupported()) {
                           DocViewGuiVerb guiVerb = new DocViewGuiVerb(23, 131088, DocViewDisplayField._resources, 8, this);
                           guiVerb.setCookie(new DocViewDisplayField$JumpDescriptor(this._latestTextLinkChunkHint, this._latestTextLinkBookmarkTarget));
                           jumpVerb = new VerbMenuItem(guiVerb, 0);
                        }
                     }
                  }
               case 2:
                  break;
               default:
                  DocViewGuiVerb vb = (DocViewGuiVerb)this.getVerbForEmbeddedObject(this._latestEmbLinkInfo, this._latestLinkType);
                  if (vb != null) {
                     jumpVerb = new VerbMenuItem(vb, 0);
                  }
            }

            if (jumpVerb != null) {
               menu.add(jumpVerb);
               menu.setDefault(jumpVerb);
            }
         }
      }

      if (nextSkippedBlockToRetrieve != -1 && this.isMoreSupported()) {
         DocViewGuiVerb guiVerb = new DocViewGuiVerb(23, 344064, EmailResources.getResourceBundle(), 80, this);
         guiVerb.setCookie(new Integer(nextSkippedBlockToRetrieve + 1));
         VerbMenuItem skippedMoreVerb = new VerbMenuItem(guiVerb, 0);
         menu.add(skippedMoreVerb);
         menu.setDefault(skippedMoreVerb);
      }

      if (embFieldSelected) {
         VerbMenuItem jumpVerb = null;
         if (field instanceof CustomBitmapField) {
            DocViewGuiVerb vb = (DocViewGuiVerb)this.getVerbForEmbeddedObject(((CustomBitmapField)field)._domID, 6);
            if (vb != null) {
               jumpVerb = new VerbMenuItem(vb, 0);
            }
         } else {
            DocViewTextDisplayField$EmbeddedStatusField embField = (DocViewTextDisplayField$EmbeddedStatusField)field;
            DocViewGuiVerb vb = (DocViewGuiVerb)this.getVerbForEmbeddedObject(embField._domID, embField._type);
            if (vb != null) {
               jumpVerb = new VerbMenuItem(vb, 0);
            }
         }

         if (jumpVerb != null) {
            menu.add(jumpVerb);
            menu.setDefault(jumpVerb);
         }
      }
   }

   private final void setDisplayMode(byte mode, boolean updateLayout) {
      if (super._fullDocState && mode != this._displayMode && this.hasRenderedSlides()) {
         if (mode == 0 && !this._hasPageBreaks) {
            return;
         }

         this._duringLayoutActivity = true;
         DocViewTextDisplayField$PageBreakStatusField pgBrk = null;

         while (true) {
            if (pgBrk == null) {
               pgBrk = this.getNextPageStatusField(-1);
            } else {
               pgBrk = this.getNextPageStatusField(pgBrk.getIndex());
            }

            if (pgBrk == null) {
               this._displayMode = mode;
               this._duringLayoutActivity = false;
               if (this._incLayoutRequested) {
                  if (updateLayout) {
                     this.updateLayout();
                  }

                  this._incLayoutRequested = false;
               }
               break;
            }

            if (pgBrk._pageDisplay != null) {
               if (mode == 1) {
                  if (pgBrk._pageDisplay.getIndex() != -1) {
                     this.delete(pgBrk._pageDisplay);
                  }
               } else if (pgBrk._pageDisplay.getIndex() == -1) {
                  DocViewTextDisplayField$PageBreakStatusField pgPrev = this.getPrevPageStatusField(pgBrk.getIndex());
                  int startIndex = pgPrev != null ? pgPrev.getIndex() + 1 : 0;
                  int insertIndex = startIndex;
                  if (pgPrev != null && pgPrev._pageIndex < pgBrk._pageIndex - 1) {
                     int endIndex = pgBrk.getIndex();

                     for (int j = endIndex - 1; j >= startIndex; j--) {
                        if (this.getField(j) instanceof DocViewTextDisplayField$SkippedStatusField) {
                           insertIndex = j + 1;
                           break;
                        }
                     }
                  }

                  this.insert(pgBrk._pageDisplay, insertIndex);
               }
            }
         }
      }
   }

   private final boolean findStringInControls(
      String strFind,
      int crtActiveTextControlIndex,
      boolean fullDocState,
      boolean bSameString,
      boolean bCaseSensitive,
      boolean needToSynchronize,
      int offsetToStartInCrtControl
   ) {
      boolean bSearchNext = true;
      String strToFind = strFind;
      if (!bCaseSensitive) {
         strToFind = strToFind.toLowerCase();
      }

      DocViewTextDisplayField$TextControlInfo[] activeVector = fullDocState ? this._FullDocFieldVector : this._TOCFieldVector;
      int size = activeVector.length;

      for (int i = crtActiveTextControlIndex; i < size; i++) {
         DocViewTextDisplayField$TextControlInfo crtControl = activeVector[i];
         if (crtControl._richTextField != null) {
            this._findStringBuffer.setLength(0);
            this._findStringBuffer.append(crtControl._richTextField.getText());
            if (i < size - 1) {
               DocViewTextDisplayField$TextControlInfo nextCtrl = activeVector[i + 1];
               if (crtControl._richTextField.getIndex() + 1 == nextCtrl._richTextField.getIndex()) {
                  String strNextText = nextCtrl._richTextField.getText();
                  int charsFromNextControl = Math.min(strNextText.length(), Math.max(0, strToFind.length() - 1));

                  for (int j = 0; j < charsFromNextControl; j++) {
                     this._findStringBuffer.append(strNextText.charAt(j));
                  }
               }
            }

            String contentString = this._findStringBuffer.toString();
            if (!bCaseSensitive) {
               contentString = contentString.toLowerCase();
            }

            int nPos = 0;
            if (i == crtActiveTextControlIndex) {
               nPos = offsetToStartInCrtControl;
               if (nPos == -1) {
                  nPos = crtControl._richTextField.getCursorPosition();
               }

               nPos = bSearchNext ? (bSameString ? nPos + 1 : nPos) : (bSameString ? nPos - 1 : nPos);
            }

            int nFoundIndex = this.findTextString(contentString, strToFind, nPos, bSearchNext);
            if (nFoundIndex != -1) {
               if (needToSynchronize) {
                  super._application.invokeAndWait(new DocViewTextDisplayField$9(this, crtControl, nFoundIndex));
                  return true;
               }

               this.putCursorPosition(crtControl, nFoundIndex, true, true);
               return true;
            }
         }
      }

      return false;
   }

   protected final void serverFindResponseSucceed(String findPattern, int findIncomplete, boolean caseSensitive) {
      if (this._lastFindSelectedCtrlIndex != -1 && this._lastFindSelectedOffset != -1) {
         boolean regularSearch = findIncomplete <= 0 || this._createdNewTextFieldToFullScreen == null;
         if (regularSearch) {
            this.findStringInControls(
               findPattern,
               this._lastFindSelectedCtrlIndex,
               true,
               super._strFindString.compareTo(findPattern) == 0,
               caseSensitive,
               true,
               this._lastFindSelectedOffset
            );
         } else {
            label62:
            try {
               DocViewTextDisplayField$TextControlInfo crtControl = this._createdNewTextFieldToFullScreen;
               int index = Arrays.getIndex(this._FullDocFieldVector, crtControl);

               int textIndex;
               for (textIndex = this._lastBlockFullContentSize - findIncomplete;
                  textIndex >= crtControl._richTextField.getTextLength();
                  crtControl = this._FullDocFieldVector[++index]
               ) {
                  textIndex -= crtControl._richTextField.getTextLength();
               }

               DocViewTextDisplayField$TextControlInfo crtControlFinal = crtControl;
               int textIndexFinal = textIndex;
               super._application.invokeAndWait(new DocViewTextDisplayField$10(this, crtControlFinal, textIndexFinal));
            } finally {
               break label62;
            }
         }

         this._lastFindSelectedCtrlIndex = -1;
         this._lastFindSelectedOffset = -1;
      }
   }

   private final String adjustChunksSearchString(String inputString) {
      return inputString != null && this._crtStringNotFoundInChunks != null && this._crtStringNotFoundInChunks.indexOf(inputString) != -1 ? null : inputString;
   }

   private final String getServerChunkList(boolean fromCrtOffset) {
      if (super._fullDocState && this.getMoreAvailableBytes() > 0) {
         try {
            StringBuffer retValue = new StringBuffer();
            int startIndex = fromCrtOffset ? this.getFieldWithFocus().getIndex() : 0;
            if (this._skippedDataFields != null) {
               int size = this._skippedDataFields.size();

               for (int i = 0; i < size; i++) {
                  DocViewTextDisplayField$SkippedStatusField skipField = (DocViewTextDisplayField$SkippedStatusField)this._skippedDataFields.elementAt(i);
                  if (skipField.getIndex() >= startIndex) {
                     DocViewDisplayField.addChunk(retValue, skipField._nextBlockIndex + 1, skipField._skippedRegions);
                  }
               }
            }

            String serverList = this.getListOfNotRetrievedChunks();
            if (serverList != null && serverList.length() > 0) {
               if (retValue.length() > 0) {
                  retValue.append(',');
                  retValue.append(serverList);
               } else {
                  retValue.append(serverList);
               }
            }

            if (retValue.length() > 0) {
               return retValue.toString();
            }
         } finally {
            return null;
         }
      }

      return null;
   }

   @Override
   protected final boolean findString(boolean bSearchNext, boolean bSameString, boolean bCaseSensitive) {
      if (!bSearchNext) {
         super._strFindString = "";
         return false;
      }

      if (!bSameString) {
         this._crtStringNotFoundInChunks = null;
      }

      int index = this.getCurrentActiveTextControlIndex(super._fullDocState);
      if (index == -1) {
         Field field = this.getFieldWithFocus();
         if (field != null) {
            int fieldsCount = this.getFieldCount();

            label87:
            for (int iIndex = field.getIndex() + 1; iIndex < fieldsCount; iIndex++) {
               if (this.getField(iIndex) instanceof RichTextField) {
                  for (int i = this._FullDocFieldVector.length - 1; i >= 0; i--) {
                     DocViewTextDisplayField$TextControlInfo cInfo = this._FullDocFieldVector[i];
                     if (cInfo._richTextField != null && cInfo._richTextField.getIndex() == iIndex) {
                        index = i;
                        break label87;
                     }
                  }
                  break;
               }
            }
         }
      }

      if (index != -1 && this.findStringInControls(super._strFindString, index, super._fullDocState, bSameString, bCaseSensitive, false, -1)) {
         return true;
      }

      if (this.isMoreSupported() && this.allowServerFind()) {
         String remainingServerChunks = null;
         int findAction = -1;
         remainingServerChunks = this.getServerChunkList(true);
         if (remainingServerChunks == null) {
            if (this.getMoreAvailableBytes() > 0) {
               remainingServerChunks = this.adjustChunksSearchString(this.getServerChunkList(false));
               if (remainingServerChunks != null) {
                  findAction = 2;
               }
            }
         } else {
            remainingServerChunks = this.adjustChunksSearchString(remainingServerChunks);
            if (remainingServerChunks != null) {
               findAction = 1;
            }
         }

         if (findAction != -1) {
            if (SimpleChoiceDialog.askYesNoQuestion(
               DocViewDisplayField._resources.getString(46)
                  + ' '
                  + (findAction == 1 ? DocViewDisplayField._resources.getString(69) : DocViewDisplayField._resources.getString(72))
            )) {
               super._descriptor.reset();
               super._descriptor._commandCode = "FIND";
               super._descriptor._partIndex = 1001;
               super._descriptor._findPattern = super._strFindString;
               super._descriptor._findBlockList = remainingServerChunks;
               super._descriptor._findCaseSensitive = bCaseSensitive;
               super._descriptor._findNext = bSearchNext;
               if (this.executeMore(super._descriptor, false, true)) {
                  this._crtStringNotFoundInChunks = remainingServerChunks;
                  this._lastFindSelectedCtrlIndex = findAction == 1 ? index : 0;
                  DocViewTextDisplayField$TextControlInfo cInfo = this._FullDocFieldVector[index];
                  if (cInfo._richTextField != null) {
                     this._lastFindSelectedOffset = findAction == 1 ? cInfo._richTextField.getCursorPosition() : 0;
                     return false;
                  }
               }
            } else {
               super._strFindString = "";
            }

            return false;
         }
      }

      super._strFindString = "";
      Status.show(DocViewDisplayField._resources.getString(46));
      return false;
   }

   private final boolean isLastCharSelected(boolean fullDocState) {
      try {
         DocViewTextDisplayField$TextControlInfo lastCtrl = this.getExtremeTextControl(fullDocState, false);
         if (ObjectUtilities.objEqual(this.getCurrentActiveTextControl(fullDocState), lastCtrl)) {
            int offset = lastCtrl._richTextField.getCursorPosition();
            if (offset == lastCtrl._richTextField.getTextLength()) {
               return true;
            }
         }
      } finally {
         return false;
      }

      return false;
   }

   private final int findTextString(String mainString, String searchString, int nStartPos, boolean bFindNext) {
      return bFindNext ? mainString.indexOf(searchString, nStartPos) : -1;
   }

   private final boolean preprocessTOCText(DocViewTextHint sectionBegin, DocViewTextHint sectionEnd) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final boolean isAllowedControl(Field fieldAtIndex) {
      if (!(fieldAtIndex instanceof DocViewTextDisplayField$PageBreakStatusField) && !(fieldAtIndex instanceof DocViewTextDisplayField$EmbeddedStatusField)) {
         if (this._embeddedStatusFields != null) {
            for (int i = this._embeddedStatusFields.size() - 1; i >= 0; i--) {
               DocViewTextDisplayField$EmbeddedStatusField statusField = (DocViewTextDisplayField$EmbeddedStatusField)this._embeddedStatusFields.elementAt(i);
               if (ObjectUtilities.objEqual(statusField._previewField, fieldAtIndex)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static final DocViewTextDisplayField$DocViewInsertFieldRunnable getUpdateWithIndex(Runnable[] updates, int index) {
      int size = updates.length;

      for (int i = size - 1; i >= 0; i--) {
         Runnable var10000 = updates[i];
         if (updates[i] instanceof DocViewTextDisplayField$DocViewInsertFieldRunnable) {
            DocViewTextDisplayField$DocViewInsertFieldRunnable run = (DocViewTextDisplayField$DocViewInsertFieldRunnable)var10000;
            if (run._insertIndex == index) {
               return run;
            }
         }
      }

      return null;
   }

   private final DocViewTextDisplayField$TextControlInfo addAndGetTextControlToUpdate(
      int currentBlockIndex, boolean fullDocState, boolean hasMoreField, Runnable[] updates
   ) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final String getPageBreakDescription(int pageIndex, int totalPages, boolean addExtraOne) {
      StringBuffer retValue = new StringBuffer();
      retValue.append(' ');
      retValue.append(DocViewDisplayField._resources.getString(this._isPresentation ? 90 : 44));
      retValue.append(' ');
      if (pageIndex != -1) {
         retValue.append(String.valueOf(pageIndex + 1 + (addExtraOne ? 1 : 0)));
         if (totalPages > pageIndex) {
            retValue.append(' ');
            retValue.append(DocViewDisplayField._resources.getString(58));
            retValue.append(' ');
            retValue.append(String.valueOf(totalPages));
         }
      } else {
         retValue.append(DocViewDisplayField._resources.getString(73));
      }

      retValue.append(' ');
      return retValue.toString();
   }

   protected final void processPageDisplay(String arbitraryDomID, String[] arbitraryDomIDs, byte[] ucsData) {
      if (arbitraryDomID != null) {
         String[] actualDomIDs = arbitraryDomIDs;
         if (actualDomIDs == null) {
            actualDomIDs = new String[]{arbitraryDomID};
         }

         if (this._playScreen != null) {
            this._playScreen.processPageDisplay(actualDomIDs, ucsData);
         }

         synchronized (super._application.getAppEventLock()) {
            this._duringLayoutActivity = true;

            for (int i = 0; i < actualDomIDs.length; i++) {
               this.privateProcessPageDisplay(this.getPageStatusFieldWithDomID(actualDomIDs[i]), ucsData, true);
            }

            this._duringLayoutActivity = false;
            if (this._incLayoutRequested) {
               this.updateLayout();
               this._incLayoutRequested = false;
            }
         }
      }
   }

   private final void setSlidePreview(DocViewTextDisplayField$PageBreakStatusField pgBrk, Field fld) {
      int idx = pgBrk._pageDisplay.getIndex();
      if (idx >= 0) {
         boolean hasFocus = pgBrk._pageDisplay.isFocus();
         this.delete(pgBrk._pageDisplay);
         this.insert(fld, idx);
         if (hasFocus) {
            fld.setFocus();
         }

         this.invalidate();
      }

      pgBrk._pageDisplay = fld;
      pgBrk._isDummy = false;
   }

   private final void privateProcessPageDisplay(DocViewTextDisplayField$PageBreakStatusField pgBrk, byte[] ucsData, boolean addAllOtherDomIDs) {
      if (pgBrk != null && pgBrk._domID != null && pgBrk._pageDisplay != null && pgBrk._isDummy) {
         Object previewData = null;
         if (ucsData != null) {
            previewData = this.parseCustomData((byte)0, ucsData);
         } else {
            previewData = this.parseCustomData((byte)0, pgBrk._domID);
         }

         if (previewData instanceof DocViewParsingData) {
            boolean multipleRenderedImages = ((DocViewParsingData)previewData).getImages().length > 1;
            DocViewImageData imgData = null;
            if (!multipleRenderedImages) {
               try {
                  imgData = ((DocViewParsingData)previewData).getImages()[0];
                  if (imgData != null) {
                     this.setRenderPreviewField(pgBrk, imgData, DocViewDisplayField.getFirstArbitraryDomID((DocViewParsingData)previewData));
                     return;
                  }
               } finally {
                  return;
               }
            } else {
               label191:
               try {
                  imgData = (DocViewImageData)((DocViewParsingData)previewData).getObjectWithDOMID(pgBrk._domID);
                  if (imgData != null) {
                     this.setRenderPreviewField(pgBrk, imgData, pgBrk._domID);
                  }
               } finally {
                  break label191;
               }

               if (addAllOtherDomIDs) {
                  int images = ((DocViewParsingData)previewData).getImages().length;

                  for (int i = 0; i < images; i++) {
                     try {
                        imgData = ((DocViewParsingData)previewData).getImages()[i];
                        if (imgData != null) {
                           String renderDomID = ((DocViewParsingData)previewData).getDomIDForAssocObject(imgData);
                           if (renderDomID != null && renderDomID.compareTo(pgBrk._domID) != 0) {
                              DocViewTextDisplayField$PageBreakStatusField pgBrkCustom = this.getPageStatusFieldWithDomID(renderDomID);
                              if (pgBrkCustom != null && pgBrkCustom._domID != null && pgBrkCustom._pageDisplay != null && pgBrkCustom._isDummy) {
                                 this.setRenderPreviewField(pgBrkCustom, imgData, pgBrkCustom._domID);
                              }
                           }
                        }
                     } finally {
                        continue;
                     }
                  }
               }
            }
         }
      }
   }

   private final void setRenderPreviewField(DocViewTextDisplayField$PageBreakStatusField pgBrk, DocViewImageData imgData, String imageDomID) {
      Field fld = AttachmentViewerFactory.getPreviewField(imgData, null, super._fontFactory, 2, null, 0, false);
      if (fld != null) {
         pgBrk._originalDisplayWidth = imgData.getOriginalWidth();
         pgBrk._originalDisplayHeight = imgData.getOriginalHeight();
         pgBrk._imageDomID = imageDomID;
         if (pgBrk._pageDisplay.getIndex() >= 0) {
            this.setSlidePreview(pgBrk, fld);
            return;
         }

         pgBrk._pageDisplay = fld;
         pgBrk._isDummy = false;
      }
   }

   private final int addBreakObject(Manager activeManager, BreakObj breakObj, int lastInsertedIndex, Runnable[] updates) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final DocViewTextDisplayField$PageBreakStatusField getPageStatusFieldWithDomID(String arbDomID) {
      if (this._hasPageBreaks && arbDomID != null) {
         for (int i = this.getFieldCount() - 1; i >= 0; i--) {
            Field fld = this.getField(i);
            if (fld instanceof DocViewTextDisplayField$PageBreakStatusField
               && ((DocViewTextDisplayField$PageBreakStatusField)fld)._domID != null
               && ((DocViewTextDisplayField$PageBreakStatusField)fld)._domID.compareTo(arbDomID) == 0) {
               return (DocViewTextDisplayField$PageBreakStatusField)fld;
            }
         }
      }

      return null;
   }

   private final DocViewTextDisplayField$PageBreakStatusField getNextPageStatusField(int startIndex) {
      if (this._hasPageBreaks) {
         int fldCount = this.getFieldCount();

         for (int i = startIndex + 1; i < fldCount; i++) {
            Field crtFld = this.getField(i);
            if (crtFld instanceof DocViewTextDisplayField$PageBreakStatusField) {
               return (DocViewTextDisplayField$PageBreakStatusField)crtFld;
            }
         }
      }

      return null;
   }

   private final DocViewTextDisplayField$PageBreakStatusField getPrevPageStatusField(int startIndex) {
      if (this._hasPageBreaks) {
         for (int i = startIndex - 1; i >= 0; i--) {
            Field crtFld = this.getField(i);
            if (crtFld instanceof DocViewTextDisplayField$PageBreakStatusField) {
               return (DocViewTextDisplayField$PageBreakStatusField)crtFld;
            }
         }
      }

      return null;
   }

   private final void updateWithText(
      boolean bFullDocumentState, int currentBlockIndex, DocViewTextHint sectionBegin, DocViewTextHint sectionEnd, Runnable[] updates
   ) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private final int createTextControls(
      String strText,
      int nCrtLength,
      DocViewTextDisplayField$TextControlInfo textControl,
      DocViewRichTextInfo[] formatCnt,
      DocViewHyperlinkInfo[] links,
      DocViewTrackChange[] trackChanges,
      IntIntHashtable bookmarkHash,
      boolean bFullDocumentState,
      int sectionBegin,
      int activeVectorInd,
      DocViewTextDisplayField$TextControlInfo[] activeVector,
      Manager activeManager,
      int lastInsertedInd,
      int currentBlockIndex,
      Runnable[] updates
   ) {
      int nTextLength = strText.length();
      int screenElementsAdded = 0;
      if (nTextLength > 0) {
         int activeVectorIndex = activeVectorInd;
         int lastInsertedIndex = lastInsertedInd;
         int nRemainingLength = nCrtLength;
         DocViewTextDisplayField$TextControlInfo latestCtrlInfo = textControl;
         if (latestCtrlInfo == null) {
            latestCtrlInfo = new DocViewTextDisplayField$TextControlInfo(this, currentBlockIndex, ++lastInsertedIndex, bFullDocumentState);
            Arrays.insertAt(activeVector, latestCtrlInfo, ++activeVectorIndex);
            screenElementsAdded++;
         }

         boolean newControlNeeded = false;

         while (nRemainingLength > 0) {
            int nAvailableLength = 2000 - (latestCtrlInfo._richTextField != null ? latestCtrlInfo._richTextField.getTextLength() : 0);
            if (nRemainingLength <= nAvailableLength && !newControlNeeded) {
               String text = nTextLength - nRemainingLength == strText.length() ? strText : strText.substring(nTextLength - nRemainingLength);
               nRemainingLength = latestCtrlInfo.calculateCorrectArrays(
                  formatCnt, links, trackChanges, bookmarkHash, text, sectionBegin + nTextLength - nRemainingLength, updates, currentBlockIndex
               );
               newControlNeeded = true;
            } else {
               if (!newControlNeeded && nAvailableLength > 0) {
                  int iSplitIndex = findSplitIndex(strText, nTextLength - nRemainingLength + nAvailableLength);
                  int stringLengthRemaining = latestCtrlInfo.calculateCorrectArrays(
                     formatCnt,
                     links,
                     trackChanges,
                     bookmarkHash,
                     strText.substring(nTextLength - nRemainingLength, iSplitIndex + 1),
                     sectionBegin + nTextLength - nRemainingLength,
                     updates,
                     currentBlockIndex
                  );
                  nRemainingLength = nTextLength - iSplitIndex - 1 + stringLengthRemaining;
               }

               if (nRemainingLength > 0) {
                  DocViewTextDisplayField$TextControlInfo newCtrl = new DocViewTextDisplayField$TextControlInfo(
                     this, currentBlockIndex, ++lastInsertedIndex, bFullDocumentState
                  );
                  latestCtrlInfo = newCtrl;
                  Arrays.insertAt(activeVector, latestCtrlInfo, ++activeVectorIndex);
                  screenElementsAdded++;
               }

               newControlNeeded = false;
            }
         }
      }

      return screenElementsAdded;
   }

   private final String getRealSectionText(boolean fullDocState, DocViewTextHint sectionBeginHint, DocViewTextHint sectionEndHint) {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }

   private static final int findSplitIndex(String strText, int iStartIndex) {
      int length = strText.length();
      if (iStartIndex >= length) {
         return length - 1;
      }

      int iNextLF = StringUtilities.indexOf(strText, '\n', iStartIndex, iStartIndex + 500 - 1);
      if (iNextLF != -1) {
         return iNextLF;
      }

      int iNextSpace = StringUtilities.indexOf(strText, ' ', iStartIndex, iStartIndex + 500 - 1);
      if (iNextSpace != -1) {
         return iNextSpace;
      }

      for (int i = iStartIndex; i >= 0 && i > iStartIndex - 50; i--) {
         if (strText.charAt(i) == '\n') {
            return i;
         }
      }

      for (int i = iStartIndex; i >= 0 && i > iStartIndex - 50; i--) {
         if (strText.charAt(i) == ' ') {
            return i;
         }
      }

      return iStartIndex;
   }

   private final void putCursorPosition(DocViewTextDisplayField$TextControlInfo targetControl, int iOffset, boolean processLinks, boolean moveToTopOfScreen) {
      label42:
      try {
         int offsetToSet = Math.max(iOffset, 0);
         int lastOffset = targetControl._richTextField.getTextLength() - 1;
         if (offsetToSet > lastOffset) {
            offsetToSet = lastOffset;
         }

         if (processLinks) {
            targetControl.doProcessLinks();
         }

         targetControl._richTextField.setFocus();
         targetControl._richTextField.setCursorPosition(offsetToSet);
         if (moveToTopOfScreen) {
            XYRect focusRect = new XYRect();
            targetControl._richTextField.getFocusRect(focusRect);
            int top = 0;
            Manager mgr = this.getActiveManager(super._fullDocState);
            if (mgr != null && mgr.getManager() != null) {
               top = mgr.getManager().getTop();
            }

            this.getScreen()
               .ensureRegionVisible(
                  targetControl._richTextField,
                  targetControl._richTextField.getLeft(),
                  focusRect.y,
                  targetControl._richTextField.getWidth(),
                  Math.min(
                     DocViewGUIInternalConstants.SCREEN_HEIGHT - top,
                     targetControl._richTextField.getManager().getHeight() - targetControl._richTextField.getTop() - focusRect.y
                  )
               );
            Object var12 = null;
         }
      } finally {
         break label42;
      }

      this.checkTrackChanges();
   }

   private final DocViewTextDisplayField$TextControlInfo getCurrentActiveTextControl(boolean fullDocMode) {
      int index = this.getCurrentActiveTextControlIndex(fullDocMode);
      if (index != -1) {
         DocViewTextDisplayField$TextControlInfo[] activeVector = fullDocMode ? this._FullDocFieldVector : this._TOCFieldVector;
         return activeVector[index];
      } else {
         return null;
      }
   }

   private final int getCurrentActiveTextControlIndex(boolean fullDocMode) {
      Manager activeManager = (Manager)(fullDocMode ? this : super._docViewTocManager);
      Field fieldWithFocus = activeManager.getFieldWithFocus();
      if (fieldWithFocus instanceof RichTextField) {
         DocViewTextDisplayField$TextControlInfo[] activeVector = fullDocMode ? this._FullDocFieldVector : this._TOCFieldVector;
         int iFields = activeVector.length;
         if (iFields > 0) {
            for (int i = 0; i < iFields; i++) {
               DocViewTextDisplayField$TextControlInfo crtControl = activeVector[i];
               if (ObjectUtilities.objEqual(crtControl._richTextField, fieldWithFocus)) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   @Override
   protected final void setExtremePosition(boolean top) {
      boolean moved = false;
      Manager mgr = (Manager)(super._fullDocState ? this : super._docViewTocManager);
      int fieldCount = mgr.getFieldCount();
      if (fieldCount > 0) {
         if (top) {
            for (int i = 0; i < fieldCount; i++) {
               Field fld = mgr.getField(i);
               if (fld != null && fld.isFocusable()) {
                  if (fld instanceof DocViewTextDisplayField$TextControlInfo$TextScreenField) {
                     DocViewTextDisplayField$TextControlInfo ctrl = this.getExtremeTextControl(super._fullDocState, true);
                     if (ctrl != null && ctrl._richTextField == fld) {
                        this.putCursorPosition(ctrl, 0, true, false);
                        moved = true;
                     }
                  } else {
                     fld.setFocus();
                     moved = true;
                  }
                  break;
               }
            }
         } else {
            for (int i = fieldCount - 1; i >= 0; i--) {
               Field fld = mgr.getField(i);
               if (fld != null && fld.isFocusable()) {
                  if (fld instanceof DocViewTextDisplayField$TextControlInfo$TextScreenField) {
                     DocViewTextDisplayField$TextControlInfo ctrl = this.getExtremeTextControl(super._fullDocState, false);
                     if (ctrl != null && ctrl._richTextField == fld) {
                        this.putCursorPosition(ctrl, ctrl._richTextField.getTextLength() - 1, true, false);
                        moved = true;
                     }
                  } else {
                     fld.setFocus();
                     moved = true;
                  }
                  break;
               }
            }
         }
      }

      if (super._fullDocState && moved && !top && this.isMoreAvailable() && AttachmentViewerFactory.isAutoMoreEnabled() && !this.isMoreRequestSent()) {
         this.executeMore(null, true, false);
      }
   }

   private final DocViewTextDisplayField$TextControlInfo getExtremeTextControl(boolean fullDocMode, boolean top) {
      DocViewTextDisplayField$TextControlInfo[] activeVector = fullDocMode ? this._FullDocFieldVector : this._TOCFieldVector;

      try {
         return top ? activeVector[0] : activeVector[activeVector.length - 1];
      } finally {
         ;
      }
   }

   private final int getHashCode(int fontStyle, int fontSize, int foreColor, int bgColor) {
      this._hashCodeBuffer.setLength(0);
      this._hashCodeBuffer.append(fontStyle).append(fontSize).append(foreColor).append(bgColor);
      return StringUtilities.computeHashCode(this._hashCodeBuffer);
   }

   private final Manager getActiveManager(boolean fullDocState) {
      return (Manager)(fullDocState ? this : super._docViewTocManager);
   }

   static final void access$500(DocViewTextDisplayField x0) {
      x0.updateLayout();
   }

   static final void access$1700(DocViewTextDisplayField x0) {
      x0.updateLayout();
   }

   static final void access$4100(DocViewTextDisplayField x0) {
      x0.updateLayout();
   }
}
