package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.apps.api.ui.VerbMenuItem;
import net.rim.device.apps.internal.messaging.MessageHotkeys;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.tid.util.Utils;

class DocViewDisplayField extends VerticalFieldManager implements BaseMenuModel, DocViewRequestMore {
   protected boolean _fullDocState = true;
   protected DocViewTOCManager _docViewTocManager;
   protected FontFactory _fontFactory = new FontFactory();
   private final DocViewParser _docData;
   protected final DocViewParsingData _parsingData;
   protected final UiApplication _application = UiApplication.getUiApplication();
   protected String _strFindString = "";
   private DocViewDataProvider _dataProvider;
   private DocViewGUIProvider _guiProvider;
   private DocViewNotify _notifyObject;
   private VerbMenuItem _findVerb;
   private VerbMenuItem _findNextVerb;
   private MenuItem _changeLanguageItem;
   protected MoreDescriptor _descriptor = new MoreDescriptor();
   private short _menuFlags = 2;
   private boolean _caseSensitiveSearch;
   protected int _themeBgColor = -1;
   protected int _themeForeColor = -1;
   protected String _currentItemDomID;
   protected String _startSummaryDomID;
   protected String _endSummaryDomID;
   protected DocViewDisplayField$ItemInfo[] _previewInfo;
   private final Object _syncObject = new Object();
   private boolean _duringPausedAutoMore;
   private boolean _initialDisplay;
   protected static final short FLAG_ADDFIND;
   protected static final short FLAG_ADDMULTIPAGE;
   private static final int COLOR_IDENTICAL_RANGE;
   protected static ResourceBundleFamily _resources = ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView");

   protected void onFinalRelease() {
   }

   protected boolean init() {
      if (this.hasMultipleItems()) {
         this._previewInfo = (DocViewDisplayField$ItemInfo[])this.parseCustomData((byte)1, null);
      }

      this._initialDisplay = true;
      return true;
   }

   protected void callOnceOnDisplay() {
      if (this.autoRequestOnDisplay() && AttachmentViewerFactory.isFastAttachmentEnabled()) {
         this._application.invokeLater(new DocViewDisplayField$1(this));
      }
   }

   void setDataProvider(DocViewDataProvider newProvider) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   void setGUIProvider(DocViewGUIProvider newProvider) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected boolean autoRequestOnDisplay() {
      return true;
   }

   void setNotifyObject(DocViewNotify newNotifyObj) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   protected final void closeMainScreen() {
      this._application.suspendPainting(true);
      this._application.invokeLater(new DocViewDisplayField$2(this));
   }

   protected final void removeFindRelatedMenuItems(Menu menuInstance) {
      if (menuInstance != null && (this._findVerb != null || this._findNextVerb != null || this._changeLanguageItem != null)) {
         int count = menuInstance.getSize();

         for (int i = count - 1; i >= 0; i--) {
            MenuItem item = menuInstance.getItem(i);
            if (ObjectUtilities.objEqual(item, this._findVerb)
               || ObjectUtilities.objEqual(item, this._findNextVerb)
               || ObjectUtilities.objEqual(item, this._changeLanguageItem)) {
               menuInstance.deleteItem(i);
            }
         }
      }
   }

   protected final boolean isMoreAvailable() {
      if (this._parsingData.getStopFlag() == 1) {
         return true;
      } else {
         return this._dataProvider != null ? this._dataProvider.isMoreAvailable() : false;
      }
   }

   protected void embeddedObjectInitialChunkArrived(DocViewParser docData, String domID) {
   }

   protected final boolean executeMore(MoreDescriptor request, boolean autoMore, boolean allowDuplicateRequest) {
      if (request == null) {
         if (this._parsingData.getStopFlag() == 1) {
            synchronized (this._syncObject) {
               if (this._duringPausedAutoMore) {
                  return false;
               }

               this._duringPausedAutoMore = true;
            }

            new DocViewDisplayField$3(this).start();
            return true;
         }

         if (!this.isMoreAvailable()) {
            return false;
         }
      }

      return this._dataProvider != null ? this._dataProvider.executeMore(request, autoMore, allowDuplicateRequest) : false;
   }

   protected final boolean isMoreRequestSent() {
      return this._dataProvider != null ? this._dataProvider.isMoreRequestSent() : false;
   }

   protected final boolean hasRenderedData(String domID) {
      return this._dataProvider != null ? this._dataProvider.hasRenderedData(domID) : false;
   }

   protected final void setAudioCodec(int serverCodec) {
      if (this._dataProvider != null) {
         this._dataProvider.setAudioCodec(serverCodec);
      }
   }

   protected final String getFileName() {
      return this._guiProvider != null ? this._guiProvider.getFileName() : "";
   }

   protected final int getMoreAvailableBytes() {
      if (this._parsingData.getStopFlag() == 1) {
         return this._docData.getMoreAvailableBytes();
      } else {
         return this._dataProvider != null ? this._dataProvider.getMoreAvailableBytes(this._dataProvider.getMoreAvailableBlocks()) : 0;
      }
   }

   protected final int getMoreAvailableBytes(int blocks) {
      return this._dataProvider != null ? this._dataProvider.getMoreAvailableBytes(blocks) : 0;
   }

   protected final String getListOfNotRetrievedChunks() {
      return this._dataProvider != null ? this._dataProvider.getListOfNotRetrievedChunks() : null;
   }

   protected final boolean allowServerFind() {
      return this._dataProvider != null ? this._dataProvider.allowServerFind() : false;
   }

   protected final boolean allowDocInfo() {
      return this._dataProvider != null ? this._dataProvider.allowDocInfo() : false;
   }

   protected final short getDocInfoStatus() {
      return this._dataProvider != null ? this._dataProvider.getDocInfoStatus() : -1;
   }

   protected final boolean hasDocInfoData() {
      return this._dataProvider != null ? this._dataProvider.hasDocInfoData() : false;
   }

   protected final boolean hasMultipleBlocks() {
      return this._dataProvider != null ? this._dataProvider.hasMultipleBlocks() : false;
   }

   protected final Object parseCustomData(byte dataID, Object param) {
      return this._dataProvider != null ? this._dataProvider.parseCustomData(dataID, param) : null;
   }

   protected final int adjustForeColor(int foreColor, int bgColor) {
      if ((bgColor != -1 || foreColor != -1)
         && !areColorsDifferent(foreColor == -1 ? this._themeForeColor : foreColor, bgColor == -1 ? this._themeBgColor : bgColor)) {
         return bgColor == -1 ? 16777215 - this._themeBgColor : 16777215 - bgColor;
      } else {
         return foreColor;
      }
   }

   protected final boolean isEmbObjectAvailable(String embObjectDOMID) {
      return this._dataProvider != null ? this._dataProvider.isEmbeddedObjectAvailable(embObjectDOMID) : false;
   }

   protected final boolean isMoreSupported() {
      return this._dataProvider != null;
   }

   protected final byte getTargetBlockStatus(int blockIndex) {
      return this._dataProvider != null ? this._dataProvider.getTargetBlockStatus(blockIndex) : 2;
   }

   protected final boolean loadTargetBlock(int blockIndex) {
      return this._dataProvider != null ? this._dataProvider.parseTargetBlock(blockIndex) : false;
   }

   protected boolean doRunDefaultVerb(boolean defaultToSpace, int time) {
      boolean retValue = false;
      if (!DeviceInfo.isInHolster()) {
         Menu menu = (Menu)(new Object(65536));
         menu.setInstance(0);
         Manager mgr = this._fullDocState ? this : this._docViewTocManager;
         Field field = mgr.getLeafFieldWithFocus();
         if (field != null) {
            ContextMenu cmenu = field.getContextMenu();
            menu.add(cmenu, true);
         }

         this.makeMenu(menu, 0);
         MenuItem menuVerb = menu.getDefault();
         ContextMenu.getInstance().setTarget(null);
         menu = null;
         if (menuVerb != null) {
            if (!ObjectUtilities.objEqual(menuVerb, this._findVerb)) {
               menuVerb.run();
               return true;
            }

            if (defaultToSpace) {
               Ui.getUiEngine().getActiveScreen().dispatchKeyEvent(32768, ' ', Keypad.keycode(' ', 0), time);
               retValue = true;
            }
         }
      }

      return retValue;
   }

   protected final boolean allowMultipleItems() {
      return this._dataProvider != null ? this._dataProvider.allowMultipleItems() : true;
   }

   protected boolean allowDefaultAction() {
      return true;
   }

   protected boolean hasMultipleItems() {
      if (this._dataProvider != null) {
         if (!this._dataProvider.allowMultipleItems()) {
            return false;
         }

         if (this._dataProvider.isMoreAvailable()) {
            return true;
         }
      }

      int itemCount = 0;
      if (this._previewInfo != null) {
         itemCount = this._previewInfo.length;
      } else {
         Object[] items = getVectorOfItems(this._parsingData);
         if (items == null) {
            itemCount = 1;
         } else {
            itemCount = items.length;
         }
      }

      return itemCount > 1;
   }

   protected final int getStartBlockIndexToRetrieve(String arbDOMID) {
      int startBlockIndex = this.getStartBlockIndexForArbDomID(arbDOMID);
      if (startBlockIndex != -1 && AttachmentViewerFactory.isTypeRequestAllChunks(this._parsingData.getDocumentType())) {
         int endBlockIndex = this.getEndBlockIndexForArbDomID(arbDOMID);
         if (endBlockIndex != -1) {
            int i = -1;

            for (i = startBlockIndex; i <= endBlockIndex; i++) {
               if (this.getTargetBlockStatus(i) == 2) {
                  startBlockIndex = i;
                  break;
               }
            }

            if (i > endBlockIndex) {
               return -2;
            }

            return startBlockIndex - 1;
         }

         startBlockIndex = -1;
      }

      return startBlockIndex;
   }

   protected final byte isDomIDRetrieved(String itemDomID, int chunkHint) {
      if (itemDomID == null) {
         return 2;
      }

      if (this._currentItemDomID != null && itemDomID.compareTo(this._currentItemDomID) == 0) {
         return 0;
      }

      int startChIndex = chunkHint;
      if (startChIndex == -1) {
         startChIndex = this.getStartBlockIndexForArbDomID(itemDomID);
      }

      if (startChIndex >= 0) {
         if (AttachmentViewerFactory.isTypeRequestAllChunks(this._parsingData.getDocumentType())) {
            int endChIndex = this.getEndBlockIndexForArbDomID(itemDomID);
            if (endChIndex != -1) {
               boolean loaded = true;

               for (int i = startChIndex; i <= endChIndex; i++) {
                  if (this.getTargetBlockStatus(i) != 0) {
                     loaded = false;
                     break;
                  }
               }

               if (loaded) {
                  return 0;
               }

               if (this.getMoreAvailableBytes() == 0) {
                  return 1;
               }

               for (int i = startChIndex; i <= endChIndex; i++) {
                  byte status = this.getTargetBlockStatus(i);
                  if (status != 0 && status != 1) {
                     return 2;
                  }
               }

               return 1;
            }

            if (this.getMoreAvailableBytes() == 0) {
               return 1;
            }
         } else {
            byte status = this.getTargetBlockStatus(startChIndex);
            if (status == 0 || status == 1) {
               return status;
            }
         }
      } else if (this._previewInfo == null
         && (
            !this.isMoreAvailable() && this.getMoreAvailableBytes() == 0
               || !AttachmentViewerFactory.isTypeRequestAllChunks(this._parsingData.getDocumentType())
         )
         && this._parsingData.getObjectWithDOMID(itemDomID) != null) {
         return 0;
      }

      return 2;
   }

   protected final int getSelectedItemIndex(DocViewDisplayField$ItemInfo[] itemsArray) {
      if (this._currentItemDomID != null) {
         for (int i = itemsArray.length - 1; i >= 0; i--) {
            if (itemsArray[i]._arbDOMID.compareTo(this._currentItemDomID) == 0) {
               return i;
            }
         }
      }

      return -1;
   }

   protected boolean docViewHandleCharRegular(char ch, int altStatus, int time) {
      switch (ch) {
         case '\n':
            if (altStatus == 0) {
               boolean handled = false;

               try {
                  handled = this.doRunDefaultVerb(true, time);
               } finally {
                  return handled;
               }

               return handled;
            }
            break;
         case 'F':
         case 'f':
            if ((this._menuFlags & 1) != 0 && (this._fullDocState || this._parsingData.getDocumentType() != 1)) {
               this.searchString(false, true);
               return true;
            }
            break;
         case 'J':
         case 'j':
            this.jump();
            return true;
         case 'N':
         case 'n':
            if ((this._menuFlags & 2) != 0 && this._fullDocState && this.hasMultipleItems()) {
               this.nextItem();
               return true;
            }
            break;
         case 'P':
         case 'p':
            if ((this._menuFlags & 2) != 0 && this._fullDocState && this.hasMultipleItems()) {
               this.previousItem();
               return true;
            }
            break;
         case 'V':
         case 'v':
            if ((this._menuFlags & 2) != 0) {
               if (this.hasMultipleItems()) {
                  this.displayItems(this.getItems(), true);
                  return true;
               }

               if (this.hasTOC()) {
                  this.toggleDisplayMode();
                  return true;
               }
            }
      }

      return false;
   }

   protected boolean canClose(boolean executeClose) {
      if (!this._fullDocState) {
         if (executeClose) {
            this.toggleDisplayMode();
         }

         return false;
      } else {
         return true;
      }
   }

   protected final void checkTrackChanges() {
      if (this._parsingData.getTrackChangesOnStatus()) {
         Object obj = this.getCurrentTrackChangeDescription();
         this.setElementDescription(obj == null && !this._fullDocState ? _resources.getString(15) : obj);
      }
   }

   protected boolean selectItem(String itemDomID) {
      return false;
   }

   protected boolean isCurrentDisplayItemComplete() {
      return false;
   }

   protected final void docInfo() {
      if (this.allowDocInfo()) {
         if (this.hasDocInfoData()) {
            IconHandler hIcon = new IconHandler();
            Object docInfoData = this.parseCustomData((byte)5, hIcon);
            if (docInfoData instanceof DocViewParsingData) {
               this.showDocInfo((DocViewParsingData)docInfoData, hIcon._icon);
            }

            hIcon = null;
            return;
         }

         short docInfoTag = this.getDocInfoStatus();
         if (docInfoTag != -1
            && docInfoTag != 0
            && !SimpleChoiceDialog.askYesNoQuestion(
               ((StringBuffer)(new Object())).append(AttachmentViewerFactory.getErrorString(docInfoTag)).append(' ').append(_resources.getString(5)).toString()
            )) {
            return;
         }

         this._descriptor.reset();
         this._descriptor._commandCode = "DI";
         this._descriptor._partIndex = 1000;
         this.executeMore(this._descriptor, false, true);
      }
   }

   protected final void showDocInfo(DocViewParsingData parsingData, Image icon) {
      IntHashtable docInfoHash = parsingData.getDocInfoHash();
      if (docInfoHash != null) {
         IntEnumeration e = docInfoHash.keys();
         StringBuffer info = (StringBuffer)(new Object());

         while (e.hasMoreElements()) {
            int elemID = e.nextElement();
            info.append(
               ((StringBuffer)(new Object()))
                  .append(AttachmentViewerFactory.getDocInfoDescription(_resources, elemID))
                  .append((String)docInfoHash.get(elemID))
                  .toString()
            );
            if (e.hasMoreElements()) {
               info.append('\n');
            }
         }

         ModalDisplayDlg docInfoDlg = new ModalDisplayDlg(
            (Manager)(new Object(281474976710656L)), this.getFileName(), info.toString(), icon, null, null, null, null, null, -1, (byte)17
         );
         info = null;
         docInfoDlg.setModal(true);
         docInfoDlg.show();
         Object var9 = null;
      }
   }

   protected final boolean doSelectItem(DocViewDisplayField$ItemInfo item) {
      if (item != null && item._arbDOMID != null && item._available != 2) {
         if (item._arbDOMID.compareTo(this._currentItemDomID) == 0) {
            return true;
         }

         if (this._dataProvider != null) {
            this._dataProvider.preSelectItem();
         }

         if (item._available != 0) {
            this.loadTargetChunkForDomID(item._arbDOMID, item._chunkHint);
         }

         if (this._parsingData.getStopFlag() == 1) {
            this._docData.resumeParsing();
            this._parsingData.waitForData();
            this.notifyDataParsed(0);
         }

         if (this.selectItem(item._arbDOMID)) {
            String prevItemID = this._currentItemDomID;
            this._currentItemDomID = item._arbDOMID;
            if (this._dataProvider != null) {
               this._dataProvider.selectItem(getItemInfo(this.getItems(), prevItemID), item);
            }

            return true;
         }
      }

      return false;
   }

   protected int getCustomStringID(int menuStringID) {
      return -1;
   }

   protected final void setElementDescription(Object description) {
      if (this._guiProvider != null) {
         this._guiProvider.currentElementDescriptionChanged(description);
      }
   }

   protected final DocViewDisplayField$ItemInfo[] parsePreviewData(byte[] previewData) {
      DocViewDisplayField$ItemInfo[] items = null;
      DocViewParser parser = new DocViewParser(false);
      parser.parseDocument(previewData, true, 0, false, true);
      if (parser.getLastParsingStatus() == 0) {
         items = this.getItems(parser.getParsingData());
      }

      DocViewParser var4 = null;
      return items;
   }

   protected final void updatePreviewDataState(boolean dataAdded) {
      if (this._previewInfo != null) {
         byte checkStatus = (byte)(dataAdded ? 2 : 1);

         for (int i = this._previewInfo.length - 1; i >= 0; i--) {
            if (this._previewInfo[i]._available == checkStatus) {
               this._previewInfo[i]._available = this.isDomIDRetrieved(this._previewInfo[i]._arbDOMID, this._previewInfo[i]._chunkHint);
            }
         }
      }
   }

   protected final DocViewDisplayField$ItemInfo[] getItems() {
      return this._previewInfo != null ? this._previewInfo : this.getItems(this._parsingData);
   }

   protected Object getCurrentTrackChangeDescription() {
      return null;
   }

   protected boolean hasTOC() {
      return false;
   }

   protected final void checkArbItems() {
      if (this._docViewTocManager != null) {
         this.displayItems(this.getItems(), false);
      }
   }

   protected final DocViewDisplayField$ItemInfo getPrevNextItem(String arbDomID, boolean next) {
      if (arbDomID != null) {
         DocViewDisplayField$ItemInfo[] items = this._previewInfo == null ? this.getItems(this._parsingData) : this._previewInfo;
         if (items != null) {
            int size = items.length;
            if (size > 1) {
               for (int i = 0; i < size; i++) {
                  DocViewDisplayField$ItemInfo info = items[i];
                  if (info._arbDOMID.compareTo(arbDomID) == 0) {
                     if (next) {
                        if (i < size - 1) {
                           return items[i + 1];
                        }
                     } else if (i > 0) {
                        return items[i - 1];
                     }
                     break;
                  }
               }
            }
         }
      }

      return null;
   }

   protected boolean toggleDisplayMode() {
      if (this._docViewTocManager == null) {
         return false;
      }

      Manager fldManager = this._fullDocState ? this.getManager() : this._docViewTocManager.getManager();
      if (fldManager == null) {
         return false;
      }

      synchronized (this._application.getAppEventLock()) {
         if (this._fullDocState) {
            this._fullDocState = false;
            fldManager.insert(this._docViewTocManager, this.getIndex());
            fldManager.delete(this);
            this.setElementDescription(_resources.getString(15));
            this._docViewTocManager.init();
            this._docViewTocManager.setFocus();
         } else {
            this._fullDocState = true;
            fldManager.insert(this, this._docViewTocManager.getIndex());
            fldManager.delete(this._docViewTocManager);
            this.setElementDescription(null);
            this.setFocus();
         }

         return true;
      }
   }

   protected final void createTOCManager(byte style) {
      if (this._docViewTocManager == null) {
         Font ft = null;
         if (this._parsingData.getDocumentType() == 1) {
            ft = this._fontFactory.getFont(0, DocViewOptions.getOptions().getSheetFontSize());
         }

         this._docViewTocManager = new DocViewTOCManager(this, style, ft);
      }
   }

   protected boolean docViewHandleCharReducedKeyboard(char ch, int altStatus, int time) {
      if (altStatus == 0) {
         switch (ch) {
            case '\n':
            case 'G':
            case 'g':
               boolean handled = false;

               try {
                  handled = this.doRunDefaultVerb(true, time);
               } finally {
                  return handled;
               }

               return handled;
            case 'O':
            case 'o':
               if (this.hasMultipleItems()) {
                  this.displayItems(this.getItems(), true);
                  return true;
               }

               if (this.hasTOC()) {
                  this.toggleDisplayMode();
               }

               return true;
            case 'Q':
            case 'q':
               if ((this._menuFlags & 1) != 0 && (this._fullDocState || this._parsingData.getDocumentType() != 1)) {
                  this.searchString(false, true);
                  return true;
               }
         }
      }

      return false;
   }

   protected boolean doProcessMoreData() {
      return true;
   }

   protected void optionsChanged() {
   }

   protected void setExtremePosition(boolean top) {
   }

   protected void addCustomMenuVerbs(Menu menu, int instance) {
      if ((this._menuFlags & 2) != 0) {
         if (instance == 0 && (this.hasTOC() || this.hasMultipleItems())) {
            menu.add((MenuItem)(new Object(new DocViewGuiVerb(2, 65552, _resources, this._fullDocState ? 35 : 33, this), 0)));
         }

         if (this.hasMultipleItems() && this._fullDocState) {
            DocViewDisplayField$ItemInfo tempItem = this.getPrevNextItem(this._currentItemDomID, false);
            if (tempItem != null && tempItem._available != 2) {
               menu.add((MenuItem)(new Object(new DocViewGuiVerb(4, 65552, _resources, this.getCustomStringID(4), this), 0)));
            }

            tempItem = this.getPrevNextItem(this._currentItemDomID, true);
            if (tempItem != null && tempItem._available != 2) {
               menu.add((MenuItem)(new Object(new DocViewGuiVerb(3, 65552, _resources, this.getCustomStringID(3), this), 0)));
            }
         }
      }

      if ((this._menuFlags & 1) != 0) {
         menu.add(this._findVerb);
         menu.setDefault(this._findVerb);
         if (this._strFindString.length() > 0) {
            menu.add(this._findNextVerb);
         }

         if (instance == 0 && Utils.getAvailableInputLocales(false).length > 1) {
            menu.add(this._changeLanguageItem);
         }
      }

      if (instance == 0 && this.allowDocInfo()) {
         menu.add((MenuItem)(new Object(new DocViewGuiVerb(33, 65552, _resources, this.hasDocInfoData() ? 60 : 59, this), 0)));
      }
   }

   protected final boolean displayEmbeddedObject(String domID) {
      if (this._guiProvider != null) {
         this._guiProvider.displayEmbeddedObject(domID);
         return true;
      } else {
         return false;
      }
   }

   protected final boolean displayEmbeddedLikeObject(
      DocViewParser docParser, String title, int foreColor, int bgColor, byte presentationValue, boolean isSpecificBgDisplay, boolean forceAllowDocInfo
   ) {
      if (this._guiProvider != null) {
         this._guiProvider.displayEmbeddedLikeObject(docParser, title, foreColor, bgColor, presentationValue, isSpecificBgDisplay, forceAllowDocInfo);
         return true;
      } else {
         return false;
      }
   }

   protected final Screen getRenderedScreen(String arbDomID, String title, EncodedImage image, int originalWidth, int originalHeight, String imageDomID) {
      return this._guiProvider != null ? this._guiProvider.getRenderedScreen(arbDomID, title, image, originalWidth, originalHeight, imageDomID) : null;
   }

   protected final void processMenuFlag(boolean add, short flag) {
      if (add) {
         this._menuFlags |= flag;
         if ((flag & 1) != 0) {
            this.createFindMenuItems();
            return;
         }
      } else {
         this._menuFlags = (short)(this._menuFlags & ~flag);
      }
   }

   protected final short getMenuFlags() {
      return this._menuFlags;
   }

   protected final void setMenuFlags(short newFlags) {
      this._menuFlags = newFlags;
      if ((newFlags & 1) != 0) {
         this.createFindMenuItems();
      }
   }

   protected void embeddedInitialChunkDataModified(String targetDOMID) {
   }

   protected void jump() {
   }

   protected final DocViewGuiVerb getVerbForEmbeddedObject(String domID, int objectType) {
      DocViewGuiVerb jumpVerb = null;
      if (this.isEmbObjectAvailable(domID)) {
         return new DocViewGuiVerb(10, 131088, _resources, 9, this);
      }

      if (this.isMoreSupported()) {
         jumpVerb = new DocViewGuiVerb(objectType == 6 ? 24 : 25, 131088, _resources, 8, this);
         jumpVerb.setCookie(domID);
      }

      return jumpVerb;
   }

   protected boolean searchString(boolean bDisplayDlg, boolean bSearchNext) {
      boolean bSameString = true;
      if (bDisplayDlg || this._strFindString.length() == 0) {
         FindStringDlg findDlg = new FindStringDlg();
         findDlg.setText(this._strFindString);
         findDlg.setCaseSensitiveSearch(this._caseSensitiveSearch);
         findDlg.show();
         if (findDlg.getCloseReason() != 0) {
            return false;
         }

         String strToFind = findDlg.getText();
         if (strToFind.length() == 0) {
            return false;
         }

         this._caseSensitiveSearch = findDlg.isCaseSensitiveSearch();
         if (!this._strFindString.equals(strToFind)) {
            bSameString = false;
            this._strFindString = strToFind;
         }
      }

      return this.findString(bSearchNext, bSameString, this._caseSensitiveSearch);
   }

   protected boolean findString(boolean bSearchNext, boolean bSameString, boolean bCaseSensitive) {
      return false;
   }

   protected final void notifyDataParsed(int currentBlockIndex) {
      try {
         this.processNewData(currentBlockIndex);
      } finally {
         return;
      }
   }

   protected void processNewData(int currentBlockIndex) {
      if (this._currentItemDomID == null && this.hasMultipleItems()) {
         DocViewDisplayField$ItemInfo[] items = this.getItems(this._parsingData);
         if (items != null && items.length > 0) {
            this._currentItemDomID = items[0]._arbDOMID;
            items = null;
         }
      }

      if (this._notifyObject != null) {
         this._notifyObject.moreDataParsed();
      }
   }

   @Override
   public void perform(int menuCode, Object cookie) {
      switch (menuCode) {
         case 0:
            this.searchString(true, true);
            return;
         case 1:
            this.findString(true, true, this._caseSensitiveSearch);
            return;
         case 2:
            if (this.hasMultipleItems()) {
               this.displayItems(this.getItems(), true);
               return;
            }

            this.toggleDisplayMode();
            return;
         case 3:
            this.nextItem();
            return;
         case 4:
            this.previousItem();
            return;
         case 9:
            this.executeMore(null, false, true);
            return;
         case 10:
            this.jump();
            return;
         case 21:
            this._descriptor.reset();
            this._descriptor._commandCode = "PREVIEW";
            this._descriptor._partIndex = 1004;
            this._descriptor._chunkSize = 64000;
            this.executeMore(this._descriptor, false, true);
            return;
         case 23:
            if (cookie instanceof Object) {
               this._descriptor.reset();
               this._descriptor._targetBlockIndex = cookie - 1;
               this.executeMore(this._descriptor, false, true);
               return;
            }

            if (cookie instanceof DocViewDisplayField$JumpDescriptor) {
               this._descriptor.reset();
               DocViewDisplayField$JumpDescriptor jmpObj = (DocViewDisplayField$JumpDescriptor)cookie;
               this._descriptor._targetBlockIndex = jmpObj._targetBlockIndex - 1;
               this._descriptor._latestBookmarkRequestByRemoteLink = jmpObj._latestBookmarkRequestByRemoteLink;
               this.executeMore(this._descriptor, false, true);
               return;
            }
            break;
         case 24:
         case 25:
            if (cookie instanceof Object) {
               this._descriptor.reset();
               if (menuCode == 24) {
                  this._descriptor._chunkSize = 64000;
                  this._descriptor._isImageRequest = true;
               }

               this._descriptor._embeddedomID = (String)cookie;
               this.executeMore(this._descriptor, false, true);
               return;
            }
            break;
         case 33:
            this.docInfo();
      }
   }

   @Override
   public final void more() {
      if (this.isMoreAvailable()) {
         this.executeMore(null, true, true);
      }
   }

   private int getEndBlockIndexForArbDomID(String arbDomID) {
      return this._dataProvider != null ? this._dataProvider.getEndBlockIndexForArbDomID(arbDomID) : -1;
   }

   @Override
   public void setVerticalScroll(int position) {
      if (Application.getApplication() != this._application) {
         synchronized (this._application.getAppEventLock()) {
            super.setVerticalScroll(position);
         }
      } else {
         super.setVerticalScroll(position);
      }
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      switch (MessageHotkeys.map(keycode)) {
         case 140:
            char ch = UiInternal.map(keycode);
            int altStatus = Keypad.status(keycode) & 1;
            if (InternalServices.isReducedFormFactor()) {
               if (this.docViewHandleCharReducedKeyboard(ch, altStatus, time)) {
                  return true;
               }
            } else if (this.docViewHandleCharRegular(ch, altStatus, time)) {
               return true;
            }

            if (this._fullDocState) {
               return super.keyDown(keycode, time);
            }

            return false;
         case 141:
         default:
            this.setExtremePosition(true);
            return true;
         case 142:
            this.setExtremePosition(false);
            return true;
      }
   }

   private static boolean areColorsDifferent(int foreColor, int bgColor) {
      int red1 = foreColor >> 16 & 0xFF;
      int green1 = foreColor >> 8 & 0xFF;
      int blue1 = foreColor & 0xFF;
      int red2 = bgColor >> 16 & 0xFF;
      int green2 = bgColor >> 8 & 0xFF;
      int blue2 = bgColor & 0xFF;
      return Math.abs(red1 - red2) + Math.abs(green1 - green2) + Math.abs(blue1 - blue2) > 255;
   }

   private void createFindMenuItems() {
      if (this._findVerb == null) {
         this._findVerb = (VerbMenuItem)(new Object(new DocViewGuiVerb(0, this), 0));
      }

      if (this._findNextVerb == null) {
         this._findNextVerb = (VerbMenuItem)(new Object(new DocViewGuiVerb(1, this), 0));
      }

      if (this._changeLanguageItem == null) {
         this._changeLanguageItem = new DocViewDisplayField$4(this, CommonResource.getBundle(), 10089, 50680656, Integer.MAX_VALUE);
      }
   }

   private void displayItems(DocViewDisplayField$ItemInfo[] itemsArray, boolean toggleState) {
      if (itemsArray != null) {
         this.createTOCManager((byte)0);
         this._docViewTocManager
            .setItems(itemsArray, this.getSelectedItemIndex(itemsArray), this._previewInfo == null && this.getMoreAvailableBytes() > 0, !toggleState);
         if (toggleState) {
            this.toggleDisplayMode();
         }
      }
   }

   protected static int adjustForeColorDefault(int foreColor, int bgColor) {
      return bgColor != -1 && !areColorsDifferent(foreColor, bgColor) ? 16777215 - bgColor : foreColor;
   }

   protected static final void addChunk(StringBuffer buffer, int start, int count) {
      if (buffer.length() > 0) {
         buffer.append(',');
      }

      buffer.append(String.valueOf(start));
      if (count > 1) {
         buffer.append('-');
         buffer.append(String.valueOf(start + count - 1));
      }
   }

   protected static final DocViewDisplayField$ItemInfo getItemInfo(DocViewDisplayField$ItemInfo[] items, String arbDomID) {
      for (int i = items.length - 1; i >= 0; i--) {
         if (items[i]._arbDOMID.compareTo(arbDomID) == 0) {
            return items[i];
         }
      }

      return null;
   }

   @Override
   protected void onUndisplay() {
      if (this._fullDocState && this._parsingData.getStopFlag() == 1) {
         this._docData.stopParsing();
      }

      super.onUndisplay();
   }

   DocViewDisplayField(
      DocViewDataProvider dataProvider,
      DocViewGUIProvider guiProvider,
      DocViewNotify notifyObject,
      DocViewParser docData,
      String currentDomID,
      String startSummaryDomID,
      String endSummaryDomID,
      int themeBgColor,
      int themeForeColor,
      boolean addFind
   ) {
      super(281474976710656L);
      this.setDataProvider(dataProvider);
      this.setGUIProvider(guiProvider);
      this.setNotifyObject(notifyObject);
      this._docData = docData;
      this._themeBgColor = themeBgColor;
      this._themeForeColor = themeForeColor;
      this._currentItemDomID = currentDomID;
      this._startSummaryDomID = startSummaryDomID;
      this._endSummaryDomID = endSummaryDomID;
      this._parsingData = docData.getParsingData();
      if (addFind) {
         this.createFindMenuItems();
         this._menuFlags = (short)(this._menuFlags | 1);
      }
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      this.addCustomMenuVerbs(menu, instance);
      super.makeMenu(menu, instance);
   }

   @Override
   protected boolean invokeAction(int action) {
      boolean handled = super.invokeAction(action);
      if (!handled) {
         switch (action) {
            case 1:
               if (this.allowDefaultAction()) {
                  handled = this.doRunDefaultVerb(false, 0);
               }
         }
      }

      return handled;
   }

   private boolean loadTargetChunkForDomID(String itemDomID, int chunkHint) {
      int startChIndex = chunkHint;
      if (startChIndex == -1) {
         startChIndex = this.getStartBlockIndexForArbDomID(itemDomID);
      }

      if (startChIndex == -1) {
         return false;
      }

      byte type = this._parsingData.getDocumentType();
      if (AttachmentViewerFactory.isTypeRequestAllChunks(type)) {
         int endChIndex = this.getEndBlockIndexForArbDomID(itemDomID);
         if (endChIndex != -1) {
            for (int i = startChIndex; i <= endChIndex; i++) {
               if (!this.loadTargetBlock(i)) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return this.loadTargetBlock(startChIndex);
      }
   }

   private DocViewDisplayField$ItemInfo[] getItems(DocViewParsingData parsingData) {
      Object[] items = getVectorOfItems(parsingData);
      if (items != null) {
         int size = items.length;
         if (size > 0) {
            DocViewDisplayField$ItemInfo[] retValue = new DocViewDisplayField$ItemInfo[size];

            for (int i = 0; i < size; i++) {
               Object item = items[i];
               String domID = parsingData.getDomIDForAssocObject(item);
               retValue[i] = new DocViewDisplayField$ItemInfo(
                  item.toString(), domID, this.isDomIDRetrieved(domID, -1), this.getStartBlockIndexForArbDomID(domID)
               );
            }

            return retValue;
         }
      }

      return null;
   }

   private boolean nextItem() {
      return this.doSelectItem(this.getPrevNextItem(this._currentItemDomID, true));
   }

   protected static Object[] getVectorOfItems(DocViewParsingData parsingData) {
      switch (parsingData.getDocumentType()) {
         case 1:
            return parsingData.getSpreadsheets();
         case 2:
            return parsingData.getImages();
         case 5:
            return parsingData.getAudio();
         default:
            return null;
      }
   }

   protected static final String getFirstArbitraryDomID(DocViewParsingData parsingData) {
      Object[] items = getVectorOfItems(parsingData);
      return items != null && items.length > 0 ? parsingData.getDomIDForAssocObject(items[0]) : null;
   }

   private boolean previousItem() {
      return this.doSelectItem(this.getPrevNextItem(this._currentItemDomID, false));
   }

   private int getStartBlockIndexForArbDomID(String arbDomID) {
      return this._dataProvider != null ? this._dataProvider.getStartBlockIndexForArbDomID(arbDomID) : -1;
   }

   @Override
   protected void onDisplay() {
      if (this._initialDisplay) {
         this._initialDisplay = false;
         this.callOnceOnDisplay();
      }

      super.onDisplay();
   }
}
