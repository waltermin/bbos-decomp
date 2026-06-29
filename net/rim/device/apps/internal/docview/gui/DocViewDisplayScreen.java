package net.rim.device.apps.internal.docview.gui;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.OptionsChangeListener;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.api.utility.framework.ModelScreen;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.docview.core.EmbeddedHint;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.SimpleInputDialog;

class DocViewDisplayScreen
   extends ModelScreen
   implements BaseMenuModel,
   MoreNotify,
   ExitScreen,
   OptionsChangeListener,
   StateChangeNotify,
   MoreDataProcessor,
   DocViewDataProvider,
   DocViewGUIProvider {
   protected boolean _isEmbScreen;
   int _applicationID;
   protected DocViewDisplayScreen$CustomLayoutHorizontalFieldManager _titleField = new DocViewDisplayScreen$CustomLayoutHorizontalFieldManager(this);
   protected DocViewFittingLabelFieldEllipsis _textField;
   private LabelField _pageInfoLabel;
   protected String _name;
   private LabelField _percentLabel;
   private Image _icon;
   protected String _domID;
   protected String _nextDOMID;
   protected String _startArbDomID;
   protected byte _docType = -1;
   protected byte _docSubtype = -1;
   protected EmailMessageModel _parentMessage;
   protected int _messageID;
   protected int _morePartID;
   protected String _archiveIndicator;
   private int _partIndex = -2;
   private int _totalBlocks;
   private int _totalNumberOfRetrievedBlocks;
   protected int _startBlockIndex;
   protected int _maxAllowedBlockIndex = -1;
   protected int _currentMaxBlockIndex;
   private int _currentAutoLoadTargetBlockIndex;
   private DocViewMoreVerb _mainMoreVerb;
   private DocViewMoreVerb _autoMoreVerb;
   protected Verb _optionsVerb;
   protected ClientRequest _clientRequest;
   private boolean _pendingServerSearch;
   protected IntIntHashtable _retrievedChunks = (IntIntHashtable)(new Object());
   private boolean _displayingData;
   private Object _parseSyncObject = new Object();
   private boolean _idleSyncInProgress;
   protected Hashtable _embeddedObjHash = (Hashtable)(new Object());
   private int _idleRunnableID = -1;
   protected short _lastErrorCode = 0;
   private boolean _stopAutoLoad;
   protected int _themeScreenBgColor = -1;
   protected int _themeScreenForeColor = -1;
   protected DocViewDisplayScreen$ExecuteObj _executeObj;
   protected DocViewParser _docData;
   protected DocViewDisplayField _displayField;
   private DocViewGenericMoreAction _moreHandler;
   private int _savedCrtMaxBlockIndex = -1;
   private boolean _justChangingTitleName;
   boolean _forceAllowDocInfo;
   protected static final int IDLE_PERIOD;
   protected static final int TIMEOUT_IDLE;
   protected static final String FAKEEMBEDDED_DOMID;
   protected static final ResourceBundleFamily _resources = ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView");

   final String getValidPassword() {
      if (this._clientRequest != null && this._clientRequest._password != null && this._lastErrorCode != 3 && this._lastErrorCode != 4) {
         return this._clientRequest._password;
      }

      ActiveDisplayedPart activePart = DocViewDisplayScreenInstance.getActivePartInstance(this._messageID, this._applicationID);
      if (activePart != null && activePart._screen != null) {
         if (activePart._screen._clientRequest != null
            && activePart._screen._clientRequest._password != null
            && activePart._screen._lastErrorCode != 3
            && activePart._screen._lastErrorCode != 4) {
            return activePart._screen._clientRequest._password;
         }

         Enumeration e = activePart._screen._embeddedObjHash.elements();

         while (e.hasMoreElements()) {
            DocViewDisplayScreen$EmbeddedObjectInfo embeddedObject = (DocViewDisplayScreen$EmbeddedObjectInfo)e.nextElement();
            if (embeddedObject._displayScreen != null
               && embeddedObject._displayScreen._clientRequest != null
               && embeddedObject._displayScreen._clientRequest._password != null
               && embeddedObject._displayScreen._lastErrorCode != 3
               && embeddedObject._displayScreen._lastErrorCode != 4) {
               return embeddedObject._displayScreen._clientRequest._password;
            }
         }
      }

      return null;
   }

   protected void newDataParsed(int currentBlockIndex) {
      if (this._nextDOMID != null
         && this._docData.getParsingData().getObjectWithDOMID(this._nextDOMID) != null
         && this._maxAllowedBlockIndex != currentBlockIndex) {
         this._maxAllowedBlockIndex = currentBlockIndex;
      }

      if (this._displayField != null) {
         this._displayField.notifyDataParsed(currentBlockIndex);
      }
   }

   protected boolean reloadNullEmbeddedObject(boolean showTrackChanges) {
      return false;
   }

   protected void serverFindResponse(short errorCode, String pattern, int findIncomplete, boolean findCaseSensitive) {
      this._pendingServerSearch = false;
   }

   protected void gotMoreData(ServerResponse response, byte[] ucsData, int totalRetrievedBlocks) {
      if (response._docID._partIndex == 1000) {
         this.gotDocInfo(ucsData);
      } else {
         new DocViewDisplayScreen$MoreParsingThread(this, response, ucsData, totalRetrievedBlocks, this).start();
      }
   }

   protected boolean hasAutoLoad() {
      return true;
   }

   protected final boolean loadTargetChunk(int reqBlockIndex) {
      byte[] blockData = this.getSavedUCSData(null, reqBlockIndex);
      if (blockData != null) {
         byte retValue = this.moreDataProcessed(blockData, reqBlockIndex, this._totalNumberOfRetrievedBlocks, true);
         byte[] var4 = null;
         return retValue == 0;
      } else {
         return false;
      }
   }

   protected final byte[] getSavedUCSData(String embDomID, int blockIndex) {
      return this._parentMessage != null
         ? DocViewAttachmentPersist.getInstance().getUCSData(this._messageID, this._morePartID, this._archiveIndicator, embDomID, blockIndex)
         : null;
   }

   protected final int getRetrievedBlockCount(String embDomID) {
      return this._parentMessage != null
         ? DocViewAttachmentPersist.getInstance().getAttachmentRetrievedBlockCount(this._messageID, this._morePartID, this._archiveIndicator, embDomID)
         : 0;
   }

   protected final String[] getMatchingAvailableEmbeddedDomIDs(String[] atoms) {
      return this._parentMessage != null
         ? DocViewAttachmentPersist.getInstance().getMatchingAvailableEmbeddedDomIDs(this._messageID, this._morePartID, this._archiveIndicator, atoms)
         : null;
   }

   protected final int getBlockCount(String embDomID) {
      return this._parentMessage != null
         ? DocViewAttachmentPersist.getInstance().getAttachmentBlockCount(this._messageID, this._morePartID, this._archiveIndicator, embDomID)
         : -1;
   }

   protected boolean selectItem(String itemDomID) {
      return false;
   }

   final void closeScreen() {
      this.leaveScreen();
      this.releaseRefs();
   }

   protected final void displayRetrievedPercentage() {
      try {
         boolean removePercentage = false;
         if (this._retrievedChunks.size() == this._maxAllowedBlockIndex - this._startBlockIndex + 1) {
            removePercentage = true;
         }

         if (!removePercentage) {
            int controlWidth = this._titleField.getWidth();
            if (controlWidth > 0) {
               int percent = this._totalNumberOfRetrievedBlocks * 100 / this._totalBlocks;
               if (percent == 0) {
                  percent = 1;
               }

               if (percent < 100) {
                  String percentString = ((StringBuffer)(new Object(" ["))).append(String.valueOf(percent)).append("%]").toString();
                  if (this._percentLabel == null) {
                     this._percentLabel = (LabelField)(new Object(percentString));
                  } else {
                     this._percentLabel.setText(percentString);
                  }

                  if (this._percentLabel.getIndex() == -1) {
                     this._titleField.add(this._percentLabel);
                  }
               } else {
                  removePercentage = true;
               }
            }
         }

         if (removePercentage && this._percentLabel != null && this._percentLabel.getIndex() != -1) {
            this._titleField.delete(this._percentLabel);
            return;
         }
      } finally {
         return;
      }
   }

   public final EmailMessageModel getEmailMessageModel() {
      return this._parentMessage;
   }

   public final boolean preMoreRequest(Object context) {
      return this._lastErrorCode != 3 && this._lastErrorCode != 4 ? true : this.readPwd(context);
   }

   @Override
   public final int getStartBlockIndexForArbDomID(String arbDomID) {
      return AttachmentViewerFactory.getStartBlockIndexWithArbDomID(
         this._messageID, this._morePartID, this._archiveIndicator, this._isEmbScreen ? this._domID : null, arbDomID
      );
   }

   @Override
   public final int getEndBlockIndexForArbDomID(String arbDomID) {
      return AttachmentViewerFactory.getEndBlockIndexWithArbDomID(
         this._messageID, this._morePartID, this._archiveIndicator, this._isEmbScreen ? this._domID : null, arbDomID, this._totalBlocks
      );
   }

   @Override
   public boolean hasRenderedData(String domID) {
      String renderDomID = AttachmentViewerFactory.constructCustomDomIDStringEx(this._isEmbScreen ? this._domID : null, domID, "RenderDomID");
      int blockCount = this.getBlockCount(renderDomID);
      if (blockCount <= 0) {
         String[] atoms = new Object[0];
         if (this._isEmbScreen) {
            Arrays.add(atoms, ((StringBuffer)(new Object())).append(this._domID).append("/").toString());
         }

         Arrays.add(atoms, "/RenderDomID");
         Arrays.add(atoms, ((StringBuffer)(new Object())).append(domID).append(',').toString());
         String[] matchDomIDs = this.getMatchingAvailableEmbeddedDomIDs(atoms);
         if (matchDomIDs != null) {
            for (int i = 0; i < matchDomIDs.length; i++) {
               blockCount = this.getBlockCount(matchDomIDs[i]);
               if (blockCount > 0) {
                  renderDomID = matchDomIDs[i];
                  break;
               }
            }
         }

         String[] var8 = null;
         matchDomIDs = null;
      }

      return blockCount > 0;
   }

   @Override
   public final short getDocInfoStatus() {
      return DocViewAttachmentPersist.getInstance().getErrorTag(this._messageID, this._morePartID, this._archiveIndicator, 1000);
   }

   @Override
   public final boolean hasDocInfoData() {
      return DocViewAttachmentPersist.getInstance().isObjectChunkAvailable(this._messageID, this._morePartID, this._archiveIndicator, "DocInfoDomID", 0);
   }

   @Override
   public Object parseCustomData(byte dataID, Object param) {
      switch (dataID) {
         case 1:
            String previewDomID = ((StringBuffer)(new Object()))
               .append(this._isEmbScreen ? ((StringBuffer)(new Object())).append(this._domID).append("/").toString() : "")
               .append("PreviewlDomID")
               .toString();
            DocViewDisplayField$ItemInfo[] items = null;
            byte[] previewData = this.getSavedUCSData(previewDomID, 0);
            if (previewData != null) {
               items = this._displayField.parsePreviewData(previewData);
            }

            if (!this._isEmbScreen) {
               boolean previewDataExisting = items != null;
               DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();
               SimpleSortingVector tocParts = persistInstance.getAttachmentPartsByIndex(this._messageID, this._morePartID, this._archiveIndicator);
               if (tocParts != null) {
                  int size = tocParts.size();
                  if (size > 1) {
                     for (int i = 0; i < size; i++) {
                        AttachmentElementInfo info = (AttachmentElementInfo)tocParts.elementAt(i);
                        if (info.getElementPartID() >= 0 && info.getElementPartID() < 999) {
                           String domID = info.getElementDOMId();
                           int chunkHint = persistInstance.getChunkHint(this._messageID, this._morePartID, this._archiveIndicator, info.getElementPartID());
                           if (previewDataExisting) {
                              if (chunkHint != -1) {
                                 DocViewDisplayField$ItemInfo item = DocViewDisplayField.getItemInfo(items, domID);
                                 if (item != null) {
                                    item._chunkHint = chunkHint;
                                 }
                              }
                           } else {
                              if (items == null) {
                                 items = new DocViewDisplayField$ItemInfo[0];
                              }

                              Arrays.add(
                                 items,
                                 new DocViewDisplayField$ItemInfo(info.toString(), domID, this._displayField.isDomIDRetrieved(domID, chunkHint), chunkHint)
                              );
                           }
                        }
                     }
                  }

                  tocParts.removeAllElements();
                  Object var21 = null;
               }
            }

            return items;
         case 5:
            if (this.allowDocInfo()) {
               byte[] ucsData = DocViewAttachmentPersist.getInstance().getUCSData(this._messageID, this._morePartID, this._archiveIndicator, "DocInfoDomID", 0);
               if (ucsData != null) {
                  DocViewParser docData = new DocViewParser(false);
                  docData.parseDocument(ucsData, true, 0, false, true);
                  DocViewParsingData parsingData = null;
                  if (docData.getLastParsingStatus() == 0) {
                     parsingData = docData.getParsingData();
                     if (param instanceof IconHandler) {
                        int iconIndex = AttachmentViewerFactory.getAttachmentIconIndex(this._docType, this._docSubtype);
                        if (iconIndex != -1) {
                           label99:
                           try {
                              ((IconHandler)param)._icon = DocViewIcons.getIcons().getImage(iconIndex);
                           } finally {
                              break label99;
                           }
                        }
                     }
                  }

                  DocViewParser var18 = null;
                  return parsingData;
               }
            }
         default:
            return null;
      }
   }

   @Override
   public final boolean allowMultipleItems() {
      return !this._isEmbScreen && (this._isEmbScreen || this._domID == null);
   }

   @Override
   public final void displayEmbeddedObject(String domID) {
      this.displayEmbeddedObject(null, domID, this._docData.getParsingData().getTrackChangesOnStatus());
   }

   @Override
   public final void displayEmbeddedLikeObject(
      DocViewParser docParser, String title, int foreColor, int bgColor, byte presentationValue, boolean isSpecificBgDisplay, boolean forceAllowDocInfo
   ) {
      byte type = docParser.getParsingData().getDocumentType();
      ContextObject cloneContext = null;
      if (super._context != null && this._clientRequest != null) {
         cloneContext = this.cloneContextObject("NEXT", null, null, type == 2, (byte)-1);
      }

      DocViewDisplayScreen scr = AttachmentViewerFactory.getDisplayScreen(
         docParser, title, cloneContext, 0, 1, this._morePartID, true, type, bgColor, foreColor, 0, this._applicationID, presentationValue, isSpecificBgDisplay
      );
      if (scr != null) {
         if (forceAllowDocInfo) {
            scr._forceAllowDocInfo = true;
         }

         scr.moreDataProcessed(null, 0, 1, true);
         DocViewDisplayField fld = scr._displayField;
         if (fld != null && fld.init()) {
            synchronized (this._embeddedObjHash) {
               Object item = this._embeddedObjHash.get("CustomEmbeddedDomID");
               if (item instanceof DocViewDisplayScreen$EmbeddedObjectInfo) {
                  DocViewDisplayScreen$EmbeddedObjectInfo embeddedObject = (DocViewDisplayScreen$EmbeddedObjectInfo)item;
                  if (embeddedObject._displayScreen != null) {
                     embeddedObject._displayScreen.releaseRefs();
                  }

                  this._embeddedObjHash.remove("CustomEmbeddedDomID");
               }

               item = null;
            }

            DocViewDisplayScreen$EmbeddedObjectInfo embInfo = new DocViewDisplayScreen$EmbeddedObjectInfo(1, 1);
            embInfo._displayScreen = scr;
            this._embeddedObjHash.put("CustomEmbeddedDomID", embInfo);
            scr.go(true);
         }
      }
   }

   @Override
   public final Screen getRenderedScreen(String arbDomID, String title, EncodedImage image, int originalWidth, int originalHeight, String imageDomID) {
      if (arbDomID != null) {
         String embDomID = AttachmentViewerFactory.constructCustomDomIDStringEx(this._isEmbScreen ? this._domID : null, arbDomID, "RenderDomID");
         DocViewDisplayScreen$EmbeddedObjectInfo embInfo = (DocViewDisplayScreen$EmbeddedObjectInfo)this._embeddedObjHash.get(embDomID);
         ContextObject cloneContext = this.cloneContextObject("RENDER", null, arbDomID, true, this._clientRequest._srcType);
         if (embInfo != null && embInfo._displayScreen != null) {
            embInfo._displayScreen.reInitialize(cloneContext);
            return embInfo._displayScreen;
         }

         DocViewParser embeddedCoreData = null;
         int totalBlockCnt = 1;
         int retrievedBlockCnt = 1;
         if (image != null && imageDomID != null && originalWidth > 0 && originalHeight > 0) {
            embeddedCoreData = AttachmentViewerFactory.simulateParsingDataForImage(image, originalWidth, originalHeight, imageDomID);
         } else {
            totalBlockCnt = this.getBlockCount(embDomID);
            retrievedBlockCnt = this.getRetrievedBlockCount(embDomID);
            if (totalBlockCnt <= 0 || totalBlockCnt != retrievedBlockCnt) {
               return null;
            }

            Object data = AttachmentViewerFactory.getDocData(this._messageID, this._morePartID, this._archiveIndicator, embDomID, true, 0, -1);
            if (data != null) {
               embeddedCoreData = this.parseInitialChunkData(data, this._docData.getParsingData().getTrackChangesOnStatus(), totalBlockCnt);
            }
         }

         if (embeddedCoreData != null) {
            synchronized (this._embeddedObjHash) {
               Enumeration e = this._embeddedObjHash.keys();

               while (e.hasMoreElements()) {
                  Object key = e.nextElement();
                  if (key instanceof Object && ((String)key).indexOf("RenderDomID") != -1) {
                     DocViewDisplayScreen$EmbeddedObjectInfo embeddedObject = (DocViewDisplayScreen$EmbeddedObjectInfo)this._embeddedObjHash.get(key);
                     if (embeddedObject._displayScreen != null && !embeddedObject._displayScreen.isDisplayed()) {
                        embeddedObject._displayScreen.releaseRefs();
                        embeddedObject._displayScreen = null;
                     }
                  }
               }
            }

            if (embInfo == null) {
               embInfo = new DocViewDisplayScreen$EmbeddedObjectInfo(totalBlockCnt, retrievedBlockCnt);
               this._embeddedObjHash.put(embDomID, embInfo);
            }

            embInfo._displayScreen = AttachmentViewerFactory.getDisplayScreen(
               embeddedCoreData,
               title,
               cloneContext,
               0,
               embInfo._totalBlockCount,
               this._morePartID,
               true,
               (byte)2,
               this._themeScreenBgColor,
               this._themeScreenForeColor,
               0,
               this._applicationID,
               AttachmentViewerFactory.getPresentationValue(this._docType, this._docSubtype),
               false
            );
            if (embInfo._displayScreen != null) {
               embInfo._displayScreen.moreDataProcessed(null, 0, embInfo._retrievedBlockCount, true);
               DocViewDisplayField fld = embInfo._displayScreen._displayField;
               if (fld != null && fld.init()) {
                  return embInfo._displayScreen;
               }
            }
         }
      }

      return null;
   }

   @Override
   public final String getFileName() {
      return this._name;
   }

   @Override
   public final boolean parseTargetBlock(int blockIndex) {
      if (!this._retrievedChunks.containsKey(blockIndex)) {
         byte[] blockData = this.getSavedUCSData(this._isEmbScreen ? this._domID : null, blockIndex);
         if (blockData != null) {
            byte retValue = this.moreDataProcessed(blockData, blockIndex, this._totalNumberOfRetrievedBlocks, true);
            byte[] var4 = null;
            return retValue == 0;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   @Override
   public final void currentElementDescriptionChanged(Object newDescription) {
      String crtText = this._textField.getText();
      if (crtText != null) {
         if (newDescription != null) {
            if (crtText.compareTo(newDescription.toString()) != 0) {
               this._justChangingTitleName = true;
               this._textField.setText(newDescription);
               this._justChangingTitleName = false;
               return;
            }
         } else if (crtText.compareTo(this._name) != 0) {
            this._justChangingTitleName = true;
            this._textField.setText(this._name);
            this._justChangingTitleName = false;
            return;
         }
      } else {
         if (newDescription != null) {
            this._justChangingTitleName = true;
            this._textField.setText(newDescription);
            this._justChangingTitleName = false;
            return;
         }

         this._justChangingTitleName = true;
         this._textField.setText(this._name);
         this._justChangingTitleName = false;
      }
   }

   @Override
   public final void selectItem(DocViewDisplayField$ItemInfo crtItem, DocViewDisplayField$ItemInfo nextItem) {
      if (crtItem != null && nextItem != null) {
         this.cancelIdleTimer();
         crtItem._currentItemMaxBlockIndex = this._savedCrtMaxBlockIndex;
         crtItem._currentItemAutoLoadMaxBlockIndex = this._currentAutoLoadTargetBlockIndex;
         if (nextItem._currentItemMaxBlockIndex == -1) {
            nextItem._chunkHint = nextItem._currentItemAutoLoadMaxBlockIndex = nextItem._currentItemMaxBlockIndex = this.getStartBlockIndexForArbDomID(
               nextItem._arbDOMID
            );
            if (nextItem._currentItemMaxBlockIndex == -1) {
               nextItem._chunkHint = nextItem._currentItemAutoLoadMaxBlockIndex = nextItem._currentItemMaxBlockIndex = this._currentMaxBlockIndex;
            }
         }

         this._currentMaxBlockIndex = nextItem._currentItemMaxBlockIndex;
         if (this._clientRequest != null) {
            this._clientRequest._blockIndex = nextItem._currentItemMaxBlockIndex;
         }

         this._currentAutoLoadTargetBlockIndex = nextItem._currentItemAutoLoadMaxBlockIndex;
         this._stopAutoLoad = this._displayField.isCurrentDisplayItemComplete();
         this.initializeAutoLoad();
         this.checkTitleField();
      }
   }

   @Override
   public final void saveLatestRequest(int partIndex, String embeddedDomID) {
      boolean bgEmailFailed = this._parentMessage != null && this._parentMessage.getTransmissionError() != 0;
      Object obj = ApplicationRegistry.getApplicationRegistry().get(-1519928498572343430L);
      if (!(obj instanceof LatestRequestInfo)) {
         LatestRequestInfo request = new LatestRequestInfo(
            this._messageID, this._morePartID, this._archiveIndicator, partIndex, bgEmailFailed, this._applicationID
         );
         request._embeddedDomID = embeddedDomID;
         ApplicationRegistry.getApplicationRegistry().replace(-1519928498572343430L, request);
      } else {
         LatestRequestInfo request = (LatestRequestInfo)obj;
         request._attachmentMoreID = this._morePartID;
         request._attachmentPartID = partIndex;
         request._archiveIndicatorString = this._archiveIndicator;
         request._messageAlreadyFailed = bgEmailFailed;
         request._applicationID = this._applicationID;
         request._embeddedDomID = embeddedDomID;
         request._messageID = this._messageID;
      }
   }

   @Override
   public final void preSelectItem() {
      this._savedCrtMaxBlockIndex = this._currentMaxBlockIndex;
   }

   @Override
   public final boolean hasMultipleBlocks() {
      return this._maxAllowedBlockIndex > this._startBlockIndex;
   }

   @Override
   public boolean canExitScreen() {
      if (this._displayField != null && !this._displayField.canClose(true)) {
         return false;
      }

      this.cancelIdleTimer();
      if (!this._isEmbScreen) {
         try {
            DocViewDisplayScreenInstance.getActivePartInstance(this._messageID, this._applicationID)._active = false;
            return true;
         } finally {
            return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public final byte getTargetBlockStatus(int blockIndex) {
      if (blockIndex >= this._startBlockIndex && blockIndex <= this._maxAllowedBlockIndex) {
         if (this._retrievedChunks.containsKey(blockIndex)) {
            return 0;
         } else {
            return (byte)(this._parentMessage != null
                  && DocViewAttachmentPersist.getInstance()
                     .isObjectChunkAvailable(this._messageID, this._morePartID, this._archiveIndicator, this._isEmbScreen ? this._domID : null, blockIndex)
               ? 1
               : 2);
         }
      } else {
         return 3;
      }
   }

   @Override
   public final boolean isEmbeddedObjectAvailable(String embObjectDOMID) {
      boolean ret = false;
      if (embObjectDOMID != null) {
         boolean existDisplayScreen = false;
         synchronized (this._embeddedObjHash) {
            Object item = this._embeddedObjHash.get(embObjectDOMID);
            if (item instanceof DocViewDisplayScreen$EmbeddedObjectInfo) {
               existDisplayScreen = ((DocViewDisplayScreen$EmbeddedObjectInfo)item)._displayScreen != null;
            }

            item = null;
         }

         if (existDisplayScreen) {
            return true;
         }

         EmbeddedHint embHint = (EmbeddedHint)this._docData.getParsingData().getObjectWithDOMID(embObjectDOMID);
         if (embHint != null) {
            if (embHint._type != 4) {
               ret = DocViewAttachmentPersist.getInstance()
                  .isObjectChunkAvailable(this._messageID, this._morePartID, this._archiveIndicator, embObjectDOMID, 0);
            } else {
               ret = this.getBlockCount(embObjectDOMID) > 0 && this.getBlockCount(embObjectDOMID) == this.getRetrievedBlockCount(embObjectDOMID);
            }

            if (!ret) {
               this._embeddedObjHash.remove(embObjectDOMID);
            }
         }
      }

      return ret;
   }

   @Override
   public boolean executeMore(MoreDescriptor request, boolean autoMore, boolean allowDuplicateRequest) {
      if (allowDuplicateRequest || !this.isMoreRequestSent()) {
         if (request == null) {
            DocViewMoreVerb targetVerb = autoMore ? this._autoMoreVerb : this._mainMoreVerb;
            if (targetVerb != null) {
               boolean idleProcessorRequest = ContextObject.getPrivateFlag(super._context, 2945628545186852484L, 0);
               if (!idleProcessorRequest && !this.preMoreRequest(super._context)) {
                  return false;
               }

               boolean crtDuplicateValue = targetVerb._duplicateMore;
               boolean changeBack = false;
               if (crtDuplicateValue != allowDuplicateRequest) {
                  targetVerb._duplicateMore = allowDuplicateRequest;
                  changeBack = true;
               }

               targetVerb.invoke(super._context);
               if (changeBack) {
                  targetVerb._duplicateMore = crtDuplicateValue;
               }

               return true;
            }
         } else {
            if (request._partIndex != 1000 && request._chunkSize == -1 && this._clientRequest != null) {
               request._chunkSize = this._clientRequest._chunkSize;
            }

            if (this._moreHandler != null && this._moreHandler.invoke(request, autoMore, allowDuplicateRequest)) {
               if (request._partIndex == 1000) {
                  DocViewAttachmentPersist.getInstance()
                     .addAttachmentPart(this._messageID, this._morePartID, this._archiveIndicator, null, "DocInfoDomID", 1000, (byte)1, (short)0, true);
               }

               if (request._embeddedomID != null) {
                  DocViewDisplayScreen$ExecuteObj obj = new DocViewDisplayScreen$ExecuteObj((byte)0);
                  obj._domID = request._embeddedomID;
                  this.setExecuteObj(obj);
               }

               if (request._latestBookmarkRequestByRemoteLink >= 0) {
                  DocViewDisplayScreen$ExecuteObj obj = new DocViewDisplayScreen$ExecuteObj((byte)1);
                  obj._latestBookmarkRequestByRemoteLink = request._latestBookmarkRequestByRemoteLink;
                  obj._latestChunkRequestByRemoteLink = request._targetBlockIndex + 1;
                  this.setExecuteObj(obj);
               }

               if (request._arbDomID != null) {
                  DocViewDisplayScreen$ExecuteObj obj = new DocViewDisplayScreen$ExecuteObj(
                     (byte)(request._partIndex != 1005 && request._partIndex != 1006 ? 2 : 3)
                  );
                  obj._domID = request._arbDomID;
                  this.setExecuteObj(obj);
               }

               if (request._partIndex == 1001) {
                  this._pendingServerSearch = true;
               }

               if (request._partIndex == 1000) {
                  DocViewDisplayScreen$ExecuteObj obj = new DocViewDisplayScreen$ExecuteObj((byte)4);
                  this.setExecuteObj(obj);
               }

               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final void setAudioCodec(int serverCodec) {
      if (this._clientRequest != null) {
         this._clientRequest._serverAudioCodec = serverCodec;
      }
   }

   @Override
   public final boolean isMoreRequestSent() {
      try {
         return EmailMoreVerb.findMorePartByIdentifier(this._parentMessage, this._morePartID).getMoreRequestSent();
      } finally {
         ;
      }
   }

   @Override
   public final boolean allowDocInfo() {
      return this._forceAllowDocInfo || !this._isEmbScreen;
   }

   @Override
   public final boolean allowServerFind() {
      return !this._pendingServerSearch && (this._domID == null || this._isEmbScreen);
   }

   @Override
   public final String getListOfNotRetrievedChunks() {
      StringBuffer retValue = (StringBuffer)(new Object());
      if (this._currentMaxBlockIndex < this._maxAllowedBlockIndex) {
         int startRegionBlock = this._currentMaxBlockIndex + 1;

         for (int i = this._currentMaxBlockIndex + 1; i <= this._maxAllowedBlockIndex; i++) {
            if (this.getTargetBlockStatus(i) != 2) {
               if (i > startRegionBlock) {
                  DocViewDisplayField.addChunk(retValue, startRegionBlock, i - startRegionBlock);
               }

               startRegionBlock = i + 1;
            }
         }

         if (this._maxAllowedBlockIndex >= startRegionBlock) {
            DocViewDisplayField.addChunk(retValue, startRegionBlock, this._maxAllowedBlockIndex - startRegionBlock + 1);
         }
      }

      return retValue.toString();
   }

   @Override
   public final int getMoreAvailableBlocks() {
      return this._totalBlocks - this._totalNumberOfRetrievedBlocks;
   }

   @Override
   public final int getMoreAvailableBytes(int blocks) {
      return this._clientRequest != null && this._clientRequest._chunkSize != -1
         ? (int)((long)(blocks * 17 * this._clientRequest._chunkSize) * 1024 / 30000)
         : (int)((long)(blocks * 17) * 1024 / 10);
   }

   @Override
   public void processMoreResponse(ServerResponse response, byte[] ucsData, int totalRetrievedBlocks) {
      if ((this._lastErrorCode == 3 || this._lastErrorCode == 4) && response._errorCode != 3 && response._errorCode != 4) {
         ActiveDisplayedPart activePart = DocViewDisplayScreenInstance.getActivePartInstance(this._messageID, this._applicationID);
         if (activePart != null && activePart._screen != null) {
            if (activePart._screen._lastErrorCode == 3 || activePart._screen._lastErrorCode == 4) {
               activePart._screen._lastErrorCode = response._errorCode;
            }

            Enumeration e = activePart._screen._embeddedObjHash.elements();

            while (e.hasMoreElements()) {
               DocViewDisplayScreen$EmbeddedObjectInfo embeddedObject = (DocViewDisplayScreen$EmbeddedObjectInfo)e.nextElement();
               if (embeddedObject._displayScreen != null
                  && (embeddedObject._displayScreen._lastErrorCode == 3 || embeddedObject._displayScreen._lastErrorCode == 4)) {
                  embeddedObject._displayScreen._lastErrorCode = response._errorCode;
               }
            }
         }
      }

      this._lastErrorCode = response._errorCode;
      if (response._errorCode == 0) {
         if (ucsData.length > 0 && response._docID._partIndex != -1) {
            if (response._docID._partIndex == 1004) {
               if (this._displayField._previewInfo == null) {
                  this._displayField._previewInfo = this._displayField.parsePreviewData(ucsData);
                  if (this._displayField._previewInfo != null) {
                     super._application.invokeLater(new DocViewDisplayScreen$7(this));
                  }
               }
            } else {
               this.gotMoreData(response, ucsData, totalRetrievedBlocks);
            }
         }
      } else if (response._docID._partIndex == 1001) {
         this.serverFindResponse(response._errorCode, response._findPattern, response._findIncomplete, response._findCaseSensitive);
      } else {
         Screen myScreen = this;
         super._application.invokeLater(new DocViewDisplayScreen$8(this, myScreen, response), 700, false);
      }

      super._application.invokeLater(new DocViewDisplayScreen$9(this), 1100, false);
   }

   @Override
   public void perform(int menuCode, Object cookie) {
      switch (menuCode) {
         case 10:
            return;
         case 11:
            this.reloadScreen(!this._docData.getParsingData().getTrackChangesOnStatus());
            return;
         case 12:
         default:
            byte optionsType = -1;

            label38:
            try {
               switch (this._docData.getParsingData().getDocumentType()) {
                  case -1:
                     break;
                  case 0:
                  default:
                     optionsType = 1;
                     break;
                  case 1:
                     optionsType = 2;
                     break;
                  case 2:
                     optionsType = 3;
               }
            } finally {
               break label38;
            }

            DocViewOptions.editOptions(optionsType);
      }
   }

   @Override
   public final MoreDataProcessor getEmbeddedDisplayScreen(String embeddedDomID) {
      if (this._embeddedObjHash.containsKey(embeddedDomID)) {
         return ((DocViewDisplayScreen$EmbeddedObjectInfo)this._embeddedObjHash.get(embeddedDomID))._displayScreen;
      }

      Enumeration e = this._embeddedObjHash.elements();

      while (e.hasMoreElements()) {
         DocViewDisplayScreen$EmbeddedObjectInfo embeddedObject = (DocViewDisplayScreen$EmbeddedObjectInfo)e.nextElement();
         if (embeddedObject._displayScreen != null) {
            MoreDataProcessor processor = (MoreDataProcessor)embeddedObject._displayScreen.getEmbeddedDisplayScreen(embeddedDomID);
            if (processor != null) {
               return processor;
            }
         }
      }

      return null;
   }

   @Override
   public void notifyMoreRequestCompleted(int param1, int param2, ServerResponse param3, byte[] param4, int param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 3
      // 01: getfield net/rim/device/apps/internal/docview/gui/ServerResponse._docID Lnet/rim/device/apps/internal/docview/gui/DocViewAtomicID;
      // 04: getfield net/rim/device/apps/internal/docview/gui/DocViewAtomicID._srcType B
      // 07: bipush 1
      // 08: if_icmpne 35
      // 0b: aload 3
      // 0c: getfield net/rim/device/apps/internal/docview/gui/ServerResponse._docID Lnet/rim/device/apps/internal/docview/gui/DocViewAtomicID;
      // 0f: getfield net/rim/device/apps/internal/docview/gui/DocViewAtomicID._domID Ljava/lang/String;
      // 12: ifnull 34
      // 15: aload 0
      // 16: aload 3
      // 17: getfield net/rim/device/apps/internal/docview/gui/ServerResponse._docID Lnet/rim/device/apps/internal/docview/gui/DocViewAtomicID;
      // 1a: getfield net/rim/device/apps/internal/docview/gui/DocViewAtomicID._domID Ljava/lang/String;
      // 1d: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen.getEmbeddedDisplayScreen (Ljava/lang/String;)Ljava/lang/Object;
      // 20: astore 6
      // 22: aload 6
      // 24: ifnull c3
      // 27: aload 6
      // 29: aload 3
      // 2a: aload 4
      // 2c: iload 5
      // 2e: invokeinterface net/rim/device/apps/internal/docview/gui/MoreDataProcessor.processMoreResponse (Lnet/rim/device/apps/internal/docview/gui/ServerResponse;[BI)V 4
      // 33: return
      // 34: return
      // 35: aload 3
      // 36: getfield net/rim/device/apps/internal/docview/gui/ServerResponse._errorCode S
      // 39: ifeq c3
      // 3c: invokestatic net/rim/device/api/system/ApplicationRegistry.getApplicationRegistry ()Lnet/rim/device/api/system/ApplicationRegistry;
      // 3f: ldc2_w -1519928498572343430
      // 42: invokevirtual net/rim/device/api/system/ApplicationRegistry.get (J)Ljava/lang/Object;
      // 45: checkcast net/rim/device/apps/internal/docview/gui/LatestRequestInfo
      // 48: astore 6
      // 4a: aload 6
      // 4c: getfield net/rim/device/apps/internal/docview/gui/LatestRequestInfo._messageID I
      // 4f: aload 0
      // 50: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._messageID I
      // 53: if_icmpne c3
      // 56: aload 6
      // 58: getfield net/rim/device/apps/internal/docview/gui/LatestRequestInfo._attachmentMoreID I
      // 5b: aload 0
      // 5c: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._morePartID I
      // 5f: if_icmpne c3
      // 62: aload 6
      // 64: getfield net/rim/device/apps/internal/docview/gui/LatestRequestInfo._applicationID I
      // 67: aload 0
      // 68: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._applicationID I
      // 6b: if_icmpne c3
      // 6e: aload 6
      // 70: getfield net/rim/device/apps/internal/docview/gui/LatestRequestInfo._archiveIndicatorString Ljava/lang/String;
      // 73: aload 0
      // 74: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._archiveIndicator Ljava/lang/String;
      // 77: invokestatic net/rim/device/api/util/ObjectUtilities.objEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 7a: ifeq c3
      // 7d: aload 6
      // 7f: getfield net/rim/device/apps/internal/docview/gui/LatestRequestInfo._embeddedDomID Ljava/lang/String;
      // 82: ifnull c3
      // 85: aload 0
      // 86: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._isEmbScreen Z
      // 89: ifeq 93
      // 8c: aload 0
      // 8d: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._domID Ljava/lang/String;
      // 90: goto 94
      // 93: aconst_null
      // 94: aload 6
      // 96: getfield net/rim/device/apps/internal/docview/gui/LatestRequestInfo._embeddedDomID Ljava/lang/String;
      // 99: invokestatic net/rim/device/api/util/ObjectUtilities.objEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 9c: ifne c3
      // 9f: aload 0
      // a0: aload 6
      // a2: getfield net/rim/device/apps/internal/docview/gui/LatestRequestInfo._embeddedDomID Ljava/lang/String;
      // a5: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen.getEmbeddedDisplayScreen (Ljava/lang/String;)Ljava/lang/Object;
      // a8: astore 7
      // aa: aload 7
      // ac: ifnull c3
      // af: aload 7
      // b1: aload 3
      // b2: aload 4
      // b4: iload 5
      // b6: invokeinterface net/rim/device/apps/internal/docview/gui/MoreDataProcessor.processMoreResponse (Lnet/rim/device/apps/internal/docview/gui/ServerResponse;[BI)V 4
      // bb: return
      // bc: astore 6
      // be: goto c3
      // c1: astore 6
      // c3: aload 0
      // c4: aload 3
      // c5: aload 4
      // c7: iload 5
      // c9: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen.processMoreResponse (Ljava/lang/Object;[BI)V
      // cc: return
      // try (27 -> 79): 80 null
      // try (27 -> 79): 82 null
   }

   @Override
   public void notifyMoreRequestFailed(LatestRequestInfo reqInfo) {
      if (reqInfo != null
         && reqInfo._messageID == this._messageID
         && reqInfo._attachmentMoreID == this._morePartID
         && reqInfo._applicationID == this._applicationID
         && ObjectUtilities.objEqual(reqInfo._archiveIndicatorString, this._archiveIndicator)) {
         if (reqInfo._embeddedDomID != null && !ObjectUtilities.objEqual(this._isEmbScreen ? this._domID : null, reqInfo._embeddedDomID)) {
            Object embScreen = this.getEmbeddedDisplayScreen(reqInfo._embeddedDomID);
            if (embScreen instanceof DocViewDisplayScreen) {
               ((DocViewDisplayScreen)embScreen).notifyMoreRequestFailed(reqInfo);
               return;
            }
         }

         Screen myScreen = this;
         super._application.invokeLater(new DocViewDisplayScreen$11(this, myScreen));
      }
   }

   @Override
   public final void stateChanged() {
      this.embeddedInitialChunkDataModified(null);
      Enumeration e = this._embeddedObjHash.elements();

      while (e.hasMoreElements()) {
         DocViewDisplayScreen$EmbeddedObjectInfo embeddedObject = (DocViewDisplayScreen$EmbeddedObjectInfo)e.nextElement();
         if (embeddedObject._displayScreen != null) {
            embeddedObject._displayScreen.embeddedInitialChunkDataModified(null);
         }
      }

      if (this._displayField != null) {
         this._displayField.updatePreviewDataState(false);
      }
   }

   @Override
   public void processEmbeddedInitialChunk(Object data, int totalBlocks, String embeddedDomID, int totalRetrievedBlocks) {
      if (totalBlocks != -1) {
         DocViewDisplayScreen screen = this.getScreenWithRequest(embeddedDomID);
         if (screen != null && !screen._embeddedObjHash.containsKey(embeddedDomID)) {
            DocViewParser docData = this.parseInitialChunkData(data, this._docData.getParsingData().getTrackChangesOnStatus(), totalBlocks);
            if (docData != null) {
               screen._embeddedObjHash.put(embeddedDomID, new DocViewDisplayScreen$EmbeddedObjectInfo(totalBlocks, totalRetrievedBlocks));
               screen.embeddedObjectInitialChunkArrived(docData, embeddedDomID);
            }
         }
      }
   }

   @Override
   public final boolean isMoreAvailable() {
      return this._currentMaxBlockIndex < this._maxAllowedBlockIndex;
   }

   @Override
   public byte moreDataProcessed(byte[] ucsData, int currentBlockIndex, int totalNumberOfBlocksRetrieved, boolean forceLoad) {
      if (totalNumberOfBlocksRetrieved >= 0) {
         this._totalNumberOfRetrievedBlocks = totalNumberOfBlocksRetrieved;
      }

      synchronized (this) {
         if (this._displayingData) {
            return 2;
         }

         this._displayingData = true;
      }

      if (forceLoad || (this.isLoadSkippingChunks() || currentBlockIndex == this._currentMaxBlockIndex + 1) && this._displayField.doProcessMoreData()) {
         if (ContextObject.getPrivateFlag(super._context, 2945628545186852484L, 0)) {
            if (currentBlockIndex <= this._maxAllowedBlockIndex) {
               this._currentAutoLoadTargetBlockIndex = currentBlockIndex;
            } else {
               this.cancelIdleTimer();
            }
         }

         if (forceLoad || currentBlockIndex >= this._startBlockIndex && currentBlockIndex <= this._maxAllowedBlockIndex) {
            if (!this._retrievedChunks.containsKey(currentBlockIndex)) {
               byte parsingStatus = 0;
               if (ucsData != null) {
                  this._docData.parseDocument(ucsData, false, currentBlockIndex, false, currentBlockIndex == this._maxAllowedBlockIndex);
                  parsingStatus = this._docData.getLastParsingStatus();
               }

               if (parsingStatus == 0) {
                  this._retrievedChunks.put(currentBlockIndex, 1);
                  this._currentMaxBlockIndex = Math.max(currentBlockIndex, this._currentMaxBlockIndex);
                  if (this._clientRequest != null) {
                     this._clientRequest._blockIndex = this._currentMaxBlockIndex;
                  }

                  this.newDataParsed(currentBlockIndex);
                  synchronized (this) {
                     this._displayingData = false;
                     this.notifyAll();
                  }

                  super._application.invokeLater(new DocViewDisplayScreen$14(this));
                  return 0;
               } else {
                  synchronized (this) {
                     this._displayingData = false;
                     this.notifyAll();
                     return 1;
                  }
               }
            } else {
               synchronized (this) {
                  this._displayingData = false;
                  this.notifyAll();
                  return 0;
               }
            }
         } else {
            synchronized (this) {
               this._displayingData = false;
               this.notifyAll();
               return 1;
            }
         }
      } else {
         synchronized (this) {
            this._displayingData = false;
            this.notifyAll();
            return 1;
         }
      }
   }

   @Override
   public void reInitialize(Object context) {
      super._context = context;
      this.setModel(new Object());
      if (this._clientRequest != null) {
         ContextObject.put(super._context, -7432523643332070209L, this._clientRequest);
      }
   }

   @Override
   public void optionsChanged(int changedOptions) {
      try {
         this._displayField.optionsChanged();
      } finally {
         return;
      }
   }

   @Override
   public void releaseRefs() {
      this._docData = null;

      label32:
      try {
         this.deleteAll();
      } finally {
         break label32;
      }

      if (this._displayField != null) {
         this._displayField.onFinalRelease();
         this._displayField = null;
      }

      this.removeListeners();
      this._parseSyncObject = null;
      this._autoMoreVerb = this._mainMoreVerb = null;
      this._optionsVerb = null;
      this._retrievedChunks.clear();
      this._retrievedChunks = null;
      this._domID = this._nextDOMID = this._startArbDomID = null;
      this._textField = null;
      this._titleField = null;
      this._percentLabel = null;
      this._pageInfoLabel = null;
      this._icon = null;
      this._name = null;
      this._executeObj = null;
      this._moreHandler = null;
      this._embeddedObjHash.clear();
      this._embeddedObjHash = null;
      this.setLeaveScreenVerb(null);
      this._parentMessage = null;
      this._clientRequest = null;
      if (!this._isEmbScreen) {
         DocViewDisplayScreenInstance.removeActivePartInstance(this._messageID, this._applicationID);
      }
   }

   private void executeIdleTimeMore() {
      if (this._displayField._fullDocState && ObjectUtilities.objEqual(this.getUiEngine().getActiveScreen(), this)) {
         if (super._context != null) {
            while (
               this._currentAutoLoadTargetBlockIndex < this._currentMaxBlockIndex
                  && this._retrievedChunks.containsKey(this._currentAutoLoadTargetBlockIndex + 1)
            ) {
               this._currentAutoLoadTargetBlockIndex++;
            }

            boolean skipChunks = this.isLoadSkippingChunks();
            if (this._currentAutoLoadTargetBlockIndex < this._maxAllowedBlockIndex) {
               if (skipChunks || this._currentAutoLoadTargetBlockIndex == this._currentMaxBlockIndex) {
                  this._clientRequest._blockIndex = this._currentAutoLoadTargetBlockIndex;
                  ContextObject.setPrivateFlag(super._context, 2945628545186852484L, 0);
                  if (!skipChunks) {
                     ContextObject.setPrivateFlag(super._context, 2945628545186852484L, 1);
                  }

                  Object retValue = this._autoMoreVerb.invoke(super._context);
                  this._stopAutoLoad = retValue instanceof StopAutoLoadObject;
                  if (!skipChunks) {
                     ContextObject.clearPrivateFlag(super._context, 2945628545186852484L, 1);
                  }

                  ContextObject.clearPrivateFlag(super._context, 2945628545186852484L, 0);
                  if (this._stopAutoLoad) {
                     this.cancelIdleTimer();
                     return;
                  }
               }
            } else {
               this.cancelIdleTimer();
            }
         }
      }
   }

   private void postMoreRequestProcessed(int partIndex, Screen thisScreen) {
      throw new RuntimeException("cod2jar: field: unresolved slot");
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      label45:
      try {
         super.makeMenu(menu, instance);
      } finally {
         break label45;
      }

      if (this._optionsVerb != null && instance == 0) {
         menu.add(this._optionsVerb);
      }

      if (this._displayField != null && this._displayField._fullDocState && this._docData.getParsingData().hasTrackChanges()) {
         if (this._docData.getParsingData().getTrackChangesOnStatus()) {
            menu.add(new DocViewGuiVerb(11, 65552, _resources, 93, this));
            return;
         }

         menu.add(new DocViewGuiVerb(11, 65552, _resources, 92, this));
      }
   }

   private void gotDocInfo(byte[] ucsData) {
      synchronized (this) {
         if (this._executeObj != null && this._executeObj._type != 4) {
            return;
         }
      }

      DocViewParser docData = new DocViewParser(false);
      docData.parseDocument(ucsData, true, 0, false, true);
      if (docData.getLastParsingStatus() == 0) {
         Image icon = null;
         int iconIndex = AttachmentViewerFactory.getAttachmentIconIndex(this._docType, this._docSubtype);
         if (iconIndex != -1) {
            label46:
            try {
               icon = DocViewIcons.getIcons().getImage(iconIndex);
            } finally {
               break label46;
            }
         }

         Image iconFinal = icon;
         Screen screen = this;
         super._application.invokeLater(new DocViewDisplayScreen$6(this, screen, docData, iconFinal), 1000, false);
      }
   }

   @Override
   public Object go(boolean modal) {
      if (!this._isEmbScreen) {
         DocViewDisplayScreenInstance.putActivePartInstance(
            new ActiveDisplayedPart(this, this._morePartID, this._partIndex, this._messageID, this._archiveIndicator)
         );
      }

      if (this._displayField != null && this._displayField.getIndex() == -1) {
         this.add(this._displayField);
      }

      this.checkTitleField();
      super._application.invokeLater(new DocViewDisplayScreen$2(this), 50, false);
      return super.go(modal);
   }

   private final synchronized void setExecuteObj(DocViewDisplayScreen$ExecuteObj obj) {
      this._executeObj = obj;
   }

   private void cancelIdleTimer() {
      if (this._idleRunnableID != -1) {
         super._application.cancelInvokeLater(this._idleRunnableID);
         this._idleRunnableID = -1;
      }
   }

   private DocViewDisplayScreen getScreenWithRequest(String embeddedDomID) {
      if (this._docData.getParsingData().getDocumentType() == 0 && this._docData.getParsingData().getObjectWithDOMID(embeddedDomID) != null) {
         return this;
      }

      Enumeration e = this._embeddedObjHash.elements();

      while (e.hasMoreElements()) {
         DocViewDisplayScreen screen = ((DocViewDisplayScreen$EmbeddedObjectInfo)e.nextElement())._displayScreen;
         if (screen != null) {
            DocViewDisplayScreen retValue = screen.getScreenWithRequest(embeddedDomID);
            if (retValue != null) {
               return retValue;
            }
         }
      }

      return null;
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (!InternalServices.isReducedFormFactor()) {
         switch (UiInternal.map(keycode)) {
            case 'H':
            case 'h':
               if (this._displayField != null && this._displayField._fullDocState && this._docData.getParsingData().hasTrackChanges()) {
                  this.reloadScreen(!this._docData.getParsingData().getTrackChangesOnStatus());
                  return true;
               }
         }
      }

      return super.keyDown(keycode, time);
   }

   private DocViewDisplayScreen getMasterScreenWithEmbeddedScreen(Screen embeddedScreen) {
      if (embeddedScreen == null) {
         return null;
      }

      Enumeration e = this._embeddedObjHash.elements();

      while (e.hasMoreElements()) {
         DocViewDisplayScreen enumScreen = ((DocViewDisplayScreen$EmbeddedObjectInfo)e.nextElement())._displayScreen;
         if (enumScreen != null) {
            if (ObjectUtilities.objEqual(embeddedScreen, enumScreen)) {
               return this;
            }

            DocViewDisplayScreen retValueScreen = enumScreen.getMasterScreenWithEmbeddedScreen(embeddedScreen);
            if (retValueScreen != null) {
               return retValueScreen;
            }
         }
      }

      return null;
   }

   private void reloadScreen(boolean showTrackChanges) {
      if (this._isEmbScreen) {
         ActiveDisplayedPart activePart = DocViewDisplayScreenInstance.getActivePartInstance(this._messageID, this._applicationID);
         DocViewDisplayScreen masterScreen = null;
         if (activePart != null) {
            masterScreen = activePart._screen;
         }

         if (masterScreen != null) {
            DocViewDisplayScreen prevLevelScreen = masterScreen.getMasterScreenWithEmbeddedScreen(this);
            if (prevLevelScreen != null) {
               if (this._domID != null) {
                  if (DocViewAttachmentPersist.getInstance().isObjectChunkAvailable(this._messageID, this._morePartID, this._archiveIndicator, this._domID, 0)) {
                     String savedDomID = this._domID;
                     this.closeScreen();
                     prevLevelScreen._embeddedObjHash.remove(savedDomID);
                     prevLevelScreen.displayEmbeddedObject(null, savedDomID, showTrackChanges);
                     return;
                  }
               } else if (prevLevelScreen.reloadNullEmbeddedObject(showTrackChanges)) {
                  return;
               }
            }
         }

         Dialog.alert(_resources.getString(95));
      } else {
         ForwardScreen fwdScreen = DocViewDisplayScreenInstance.getForwardScreenInstance(this._messageID, this._applicationID);
         if (fwdScreen == null) {
            Dialog.alert(_resources.getString(95));
         } else if (!this.isTargetBlockInStore(this._startBlockIndex)) {
            Dialog.alert(_resources.getString(95));
         } else {
            if (!this._displayField._fullDocState) {
               this._displayField.toggleDisplayMode();
            }

            this.closeScreen();
            ForwardScreen fScreen = fwdScreen;
            super._application.invokeLater(new DocViewDisplayScreen$10(this, fScreen, showTrackChanges));
         }
      }
   }

   private void removeListeners() {
      Application app = Application.getApplication();
      DocViewOptions options = DocViewOptions.getOptions();
      app.removeGlobalEventListener(this);
      options.removeOptionsChangeListener(this);
      Enumeration e = this._embeddedObjHash.elements();

      while (e.hasMoreElements()) {
         DocViewDisplayScreen$EmbeddedObjectInfo embeddedObject = (DocViewDisplayScreen$EmbeddedObjectInfo)e.nextElement();
         if (embeddedObject._displayScreen != null) {
            embeddedObject._displayScreen.releaseRefs();
         }
      }
   }

   private boolean isTargetBlockInStore(int targetBlock) {
      if (this._parentMessage != null) {
         String domID = this._isEmbScreen ? this._domID : null;
         return DocViewAttachmentPersist.getInstance().isObjectChunkAvailable(this._messageID, this._morePartID, this._archiveIndicator, domID, targetBlock);
      } else {
         return false;
      }
   }

   private boolean readPwd(Object context) {
      ClientRequest request = (ClientRequest)ContextObject.get(context, -7432523643332070209L);
      SimpleInputDialog pwdDlg = (SimpleInputDialog)(new Object(5, CommonResources.getString(2012)));
      super._application.invokeAndWait(new DocViewDisplayScreen$1(this, pwdDlg));
      if (pwdDlg.getCloseReason() == 0) {
         String pwd = pwdDlg.getText();
         request._password = pwd;
         if (this._clientRequest != null) {
            this._clientRequest._password = pwd;
         }

         ActiveDisplayedPart activePart = DocViewDisplayScreenInstance.getActivePartInstance(this._messageID, this._applicationID);
         if (activePart != null && activePart._screen != null) {
            if (activePart._screen._clientRequest != null) {
               activePart._screen._clientRequest._password = pwd;
            }

            Enumeration e = activePart._screen._embeddedObjHash.elements();

            while (e.hasMoreElements()) {
               DocViewDisplayScreen$EmbeddedObjectInfo embeddedObject = (DocViewDisplayScreen$EmbeddedObjectInfo)e.nextElement();
               if (embeddedObject._displayScreen != null && embeddedObject._displayScreen._clientRequest != null) {
                  embeddedObject._displayScreen._clientRequest._password = pwd;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private void embeddedInitialChunkDataModified(String targetDOMID) {
      this._displayField.embeddedInitialChunkDataModified(targetDOMID);
      if (this._docData.getParsingData().getDocumentType() != 2) {
         this._totalNumberOfRetrievedBlocks = this.getRetrievedBlockCount(this._isEmbScreen ? this._domID : null);
         super._application.invokeLater(new DocViewDisplayScreen$12(this));
      }
   }

   private void displayEmbeddedObject(DocViewParser docData, String domID, boolean displayTrackChanges) {
      try {
         EmbeddedHint embHint = (EmbeddedHint)this._docData.getParsingData().getObjectWithDOMID(domID);
         DocViewDisplayScreen$EmbeddedObjectInfo embInfo = (DocViewDisplayScreen$EmbeddedObjectInfo)this._embeddedObjHash.get(domID);
         DocViewParser embeddedCoreData = docData;
         if (embInfo == null || embeddedCoreData == null && embInfo._displayScreen == null) {
            int totalBlockCnt = this.getBlockCount(domID);
            int retrievedBlockCnt = this.getRetrievedBlockCount(domID);
            if (embeddedCoreData == null) {
               if (embHint._type == 4 && (totalBlockCnt < 0 || totalBlockCnt != retrievedBlockCnt)) {
                  return;
               }

               embeddedCoreData = this.parseInitialChunkData(
                  AttachmentViewerFactory.getDocData(this._messageID, this._morePartID, this._archiveIndicator, domID, embHint._type == 4, 0, -1),
                  displayTrackChanges,
                  totalBlockCnt
               );
            }

            if (embInfo == null) {
               embInfo = new DocViewDisplayScreen$EmbeddedObjectInfo(totalBlockCnt, retrievedBlockCnt);
               this._embeddedObjHash.put(domID, embInfo);
            }
         }

         ContextObject cloneContext = this.cloneContextObject(
            this._clientRequest._commandCode, domID, this._clientRequest._arbDOMID, embHint._type == 4, (byte)1
         );
         if (embInfo._displayScreen != null) {
            embInfo._displayScreen.reInitialize(cloneContext);
            embInfo._displayScreen.go(true);
         } else if (embeddedCoreData != null) {
            if (embHint._type == 0) {
               String sheetName = null;

               label193:
               try {
                  sheetName = embeddedCoreData.getParsingData().getSpreadsheets()[0].toString();
               } finally {
                  break label193;
               }

               if (sheetName == null) {
                  sheetName = _resources.getString(45);
                  if (embHint._index != -1) {
                     sheetName = ((StringBuffer)(new Object())).append(sheetName).append(':').append(String.valueOf(embHint._index + 1)).toString();
                  }

                  embeddedCoreData.getParsingData().getSpreadsheets()[0].setSheetName(sheetName);
               }

               embInfo._displayScreen = AttachmentViewerFactory.getDisplayScreen(
                  embeddedCoreData,
                  sheetName,
                  cloneContext,
                  0,
                  embInfo._totalBlockCount,
                  this._morePartID,
                  true,
                  (byte)1,
                  this._themeScreenBgColor,
                  this._themeScreenForeColor,
                  embHint._flipImage,
                  this._applicationID,
                  (byte)0,
                  false
               );
            } else if (embHint._type == 3) {
               String name = _resources.getString(87);
               if (embHint._index != -1) {
                  name = ((StringBuffer)(new Object())).append(name).append(':').append(String.valueOf(embHint._index + 1)).toString();
               }

               embInfo._displayScreen = AttachmentViewerFactory.getDisplayScreen(
                  embeddedCoreData,
                  name,
                  cloneContext,
                  0,
                  embInfo._totalBlockCount,
                  this._morePartID,
                  true,
                  (byte)0,
                  this._themeScreenBgColor,
                  this._themeScreenForeColor,
                  embHint._flipImage,
                  this._applicationID,
                  (byte)0,
                  false
               );
            } else if (embHint._type == 1) {
               embInfo._displayScreen = AttachmentViewerFactory.getDisplayScreen(
                  embeddedCoreData,
                  ((StringBuffer)(new Object()))
                     .append(_resources.getString(37))
                     .append(':')
                     .append(' ')
                     .append(this._docData.getParsingData().getEmbeddedObjectName(domID))
                     .toString(),
                  cloneContext,
                  0,
                  embInfo._totalBlockCount,
                  this._morePartID,
                  true,
                  (byte)0,
                  this._themeScreenBgColor,
                  this._themeScreenForeColor,
                  embHint._flipImage,
                  this._applicationID,
                  (byte)0,
                  false
               );
            } else if (embHint._type == 2) {
               embInfo._displayScreen = AttachmentViewerFactory.getDisplayScreen(
                  embeddedCoreData,
                  this._docData.getParsingData().getEmbeddedObjectOwner(domID),
                  cloneContext,
                  0,
                  embInfo._totalBlockCount,
                  this._morePartID,
                  true,
                  (byte)0,
                  this._themeScreenBgColor,
                  this._themeScreenForeColor,
                  embHint._flipImage,
                  this._applicationID,
                  (byte)0,
                  false
               );
            } else if (embHint._type == 4) {
               String name = _resources.getString(96);
               if (embHint._index != -1) {
                  name = ((StringBuffer)(new Object())).append(name).append(':').append(String.valueOf(embHint._index + 1)).toString();
               }

               embInfo._displayScreen = AttachmentViewerFactory.getDisplayScreen(
                  embeddedCoreData,
                  name,
                  cloneContext,
                  0,
                  embInfo._totalBlockCount,
                  this._morePartID,
                  true,
                  (byte)2,
                  this._themeScreenBgColor,
                  this._themeScreenForeColor,
                  embHint._flipImage,
                  this._applicationID,
                  (byte)0,
                  false
               );
            }

            if (embInfo._displayScreen != null) {
               embInfo._displayScreen.moreDataProcessed(null, 0, embInfo._retrievedBlockCount, true);
               DocViewDisplayField fld = embInfo._displayScreen._displayField;
               if (fld != null && fld.init()) {
                  embInfo._displayScreen.go(true);
                  return;
               }
            }
         }
      } finally {
         return;
      }
   }

   private boolean isLoadSkippingChunks() {
      return this._docData.getParsingData().getDocumentType() == 0;
   }

   @Override
   protected void onUiEngineAttached(boolean attached) {
      if (attached) {
         this.initializeAutoLoad();
         super.onUiEngineAttached(attached);
      } else {
         super.onUiEngineAttached(attached);
         Application.getApplication().addGlobalEventListener(this);
      }
   }

   private DocViewParser parseInitialChunkData(Object data, boolean displayTrackChanges, int totalBlockCount) {
      synchronized (this._parseSyncObject) {
         DocViewParser docData = new DocViewParser(displayTrackChanges);
         if (data instanceof byte[]) {
            docData.parseDocument((byte[])data, true, 0, false, totalBlockCount == 1);
         } else if (data instanceof Object) {
            boolean continueParse = true;
            Vector ucsDataVector = (Vector)data;
            int size = ucsDataVector.size();

            for (int i = 0; continueParse && i < size; i++) {
               docData.parseDocument((byte[])ucsDataVector.elementAt(i), i == 0, i, false, i == totalBlockCount - 1);
               if (docData.getLastParsingStatus() != 0) {
                  break;
               }
            }
         }

         byte parseStatus = docData.getLastParsingStatus();
         if (parseStatus != 0) {
            docData = null;
            int errorID = AttachmentViewerFactory.getParsingErrorID(parseStatus);
            if (errorID != -1) {
               super._application.invokeLater(new ErrorDlg(_resources.getString(errorID)));
            }
         }

         return docData;
      }
   }

   private void embeddedObjectInitialChunkArrived(DocViewParser docData, String domID) {
      this._displayField.embeddedObjectInitialChunkArrived(docData, domID);
      synchronized (this) {
         if (this._executeObj == null || this._executeObj._type != 0 || domID.compareTo(this._executeObj._domID) != 0) {
            return;
         }

         this._executeObj = null;
      }

      Screen screen = this;
      super._application.invokeLater(new DocViewDisplayScreen$13(this, screen, docData, domID), 500, false);
   }

   private void onIdleTime() {
      if (DeviceInfo.getIdleTime() >= 1) {
         synchronized (this) {
            if (this._idleSyncInProgress || this._displayingData) {
               return;
            }
         }

         if (this._displayField == null || this._autoMoreVerb == null || this._clientRequest == null) {
            this.cancelIdleTimer();
            return;
         }

         if (!this._displayField._fullDocState) {
            return;
         }

         Object scr = this;
         new DocViewDisplayScreen$4(this, scr).start();
      }
   }

   private void checkTitleField() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._displayField Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 004: ifnonnull 00a
      // 007: goto 136
      // 00a: aload 0
      // 00b: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._displayField Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 00e: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField.hasMultipleItems ()Z
      // 011: ifne 017
      // 014: goto 136
      // 017: aload 0
      // 018: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._docData Lnet/rim/device/apps/internal/docview/gui/DocViewParser;
      // 01b: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getParsingData ()Lnet/rim/device/apps/internal/docview/gui/DocViewParsingData;
      // 01e: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.getDocumentType ()B
      // 021: bipush 2
      // 023: if_icmpeq 029
      // 026: goto 136
      // 029: aload 0
      // 02a: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._displayField Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 02d: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayField._fullDocState Z
      // 030: ifeq 04f
      // 033: aload 0
      // 034: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._textField Lnet/rim/device/apps/internal/docview/gui/DocViewFittingLabelFieldEllipsis;
      // 037: invokevirtual net/rim/device/api/ui/component/LabelField.getText ()Ljava/lang/String;
      // 03a: aload 0
      // 03b: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._name Ljava/lang/String;
      // 03e: invokestatic net/rim/device/api/util/ObjectUtilities.objEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
      // 041: ifne 04f
      // 044: aload 0
      // 045: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._textField Lnet/rim/device/apps/internal/docview/gui/DocViewFittingLabelFieldEllipsis;
      // 048: aload 0
      // 049: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._name Ljava/lang/String;
      // 04c: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewFittingLabelFieldEllipsis.setText (Ljava/lang/Object;)V
      // 04f: aload 0
      // 050: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._displayField Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 053: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField.getItems ()[Ljava/lang/Object;
      // 056: astore 1
      // 057: aload 0
      // 058: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._displayField Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 05b: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayField._previewInfo [Ljava/lang/Object;
      // 05e: ifnull 06c
      // 061: aload 0
      // 062: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._displayField Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 065: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayField._previewInfo [Ljava/lang/Object;
      // 068: arraylength
      // 069: goto 06e
      // 06c: bipush -1
      // 06e: istore 2
      // 06f: iload 2
      // 070: bipush -1
      // 072: if_icmpne 083
      // 075: aload 0
      // 076: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._totalNumberOfRetrievedBlocks I
      // 079: aload 0
      // 07a: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._totalBlocks I
      // 07d: if_icmpne 083
      // 080: aload 1
      // 081: arraylength
      // 082: istore 2
      // 083: new java/lang/Object
      // 086: dup
      // 087: invokespecial java/lang/StringBuffer.<init> ()V
      // 08a: aload 1
      // 08b: aload 0
      // 08c: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._displayField Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayField;
      // 08f: aload 1
      // 090: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField.getSelectedItemIndex ([Ljava/lang/Object;)I
      // 093: aaload
      // 094: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewDisplayField$ItemInfo.toString ()Ljava/lang/String;
      // 097: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 09a: iload 2
      // 09b: bipush 1
      // 09c: if_icmple 0c8
      // 09f: new java/lang/Object
      // 0a2: dup
      // 0a3: invokespecial java/lang/StringBuffer.<init> ()V
      // 0a6: bipush 32
      // 0a8: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0ab: getstatic net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._resources Lnet/rim/device/api/i18n/ResourceBundleFamily;
      // 0ae: bipush 58
      // 0b0: invokevirtual net/rim/device/api/i18n/ResourceBundle.getString (I)Ljava/lang/String;
      // 0b3: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0b6: bipush 32
      // 0b8: invokevirtual java/lang/StringBuffer.append (C)Ljava/lang/StringBuffer;
      // 0bb: iload 2
      // 0bc: invokestatic java/lang/String.valueOf (I)Ljava/lang/String;
      // 0bf: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0c2: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0c5: goto 0cb
      // 0c8: ldc_w ""
      // 0cb: invokevirtual java/lang/StringBuffer.append (Ljava/lang/String;)Ljava/lang/StringBuffer;
      // 0ce: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0d1: astore 3
      // 0d2: aload 0
      // 0d3: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._pageInfoLabel Lnet/rim/device/api/ui/component/LabelField;
      // 0d6: ifnonnull 0e8
      // 0d9: aload 0
      // 0da: new java/lang/Object
      // 0dd: dup
      // 0de: aload 3
      // 0df: invokespecial net/rim/device/api/ui/component/LabelField.<init> (Ljava/lang/Object;)V
      // 0e2: putfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._pageInfoLabel Lnet/rim/device/api/ui/component/LabelField;
      // 0e5: goto 0f0
      // 0e8: aload 0
      // 0e9: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._pageInfoLabel Lnet/rim/device/api/ui/component/LabelField;
      // 0ec: aload 3
      // 0ed: invokevirtual net/rim/device/api/ui/component/LabelField.setText (Ljava/lang/Object;)V
      // 0f0: aload 0
      // 0f1: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._pageInfoLabel Lnet/rim/device/api/ui/component/LabelField;
      // 0f4: invokevirtual net/rim/device/api/ui/Field.getIndex ()I
      // 0f7: bipush -1
      // 0f9: if_icmpne 136
      // 0fc: aload 0
      // 0fd: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._textField Lnet/rim/device/apps/internal/docview/gui/DocViewFittingLabelFieldEllipsis;
      // 100: invokevirtual net/rim/device/api/ui/Field.getIndex ()I
      // 103: istore 4
      // 105: aload 0
      // 106: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._titleField Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayScreen$CustomLayoutHorizontalFieldManager;
      // 109: new net/rim/device/apps/internal/docview/gui/DocViewHorizontalSpacerField
      // 10c: dup
      // 10d: aload 0
      // 10e: invokevirtual net/rim/device/api/ui/Field.getFont ()Lnet/rim/device/api/ui/Font;
      // 111: invokevirtual net/rim/device/api/ui/Font.getHeight ()I
      // 114: bipush 2
      // 116: ishr
      // 117: invokespecial net/rim/device/apps/internal/docview/gui/DocViewHorizontalSpacerField.<init> (I)V
      // 11a: iinc 4 1
      // 11d: iload 4
      // 11f: invokevirtual net/rim/device/api/ui/Manager.insert (Lnet/rim/device/api/ui/Field;I)V
      // 122: aload 0
      // 123: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._titleField Lnet/rim/device/apps/internal/docview/gui/DocViewDisplayScreen$CustomLayoutHorizontalFieldManager;
      // 126: aload 0
      // 127: getfield net/rim/device/apps/internal/docview/gui/DocViewDisplayScreen._pageInfoLabel Lnet/rim/device/api/ui/component/LabelField;
      // 12a: iinc 4 1
      // 12d: iload 4
      // 12f: invokevirtual net/rim/device/api/ui/Manager.insert (Lnet/rim/device/api/ui/Field;I)V
      // 132: return
      // 133: astore 1
      // 134: return
      // 135: astore 1
      // 136: return
      // try (16 -> 135): 136 null
      // try (16 -> 135): 138 null
   }

   private ContextObject cloneContextObject(String commandCode, String domID, String arbDomID, boolean isImageRequest, byte embeddedObject) {
      ContextObject cloneContext = ContextObject.castOrCreate(super._context).clone();
      ClientRequest request = new ClientRequest(this._clientRequest);
      request._commandCode = commandCode;
      request._domID = domID;
      request._arbDOMID = arbDomID;
      request._nextDOMID = null;
      request._srcType = embeddedObject;
      request._blockIndex = 0;
      request._partIndex = 999;
      request._imageRequest = isImageRequest;
      if (request._imageRequest) {
         request._chunkSize = AttachmentViewerFactory.isMoreAllSupported(this._parentMessage) ? 64000 : -1;
      } else {
         request._chunkSize = AttachmentViewerFactory.getDefaultChunkSize(this._parentMessage);
         byte[] xcsData = DocViewAttachmentPersist.getInstance().getUCSData(this._messageID, this._morePartID, this._archiveIndicator, "XChSize", 0);
         if (xcsData != null) {
            int chunkSize = AttachmentViewerFactory.readInt(xcsData, 0);
            if (chunkSize != -1) {
               request._chunkSize = chunkSize;
            } else {
               request._chunkSize = -1;
            }
         } else {
            request._chunkSize = -1;
         }
      }

      ContextObject.put(cloneContext, -7432523643332070209L, request);
      return cloneContext;
   }

   private void initializeAutoLoad() {
      if (this.hasAutoLoad()
         && !this._stopAutoLoad
         && this._retrievedChunks.size() < this._totalBlocks
         && this._currentAutoLoadTargetBlockIndex < this._maxAllowedBlockIndex
         && this._idleRunnableID == -1) {
         this._idleRunnableID = super._application.invokeLater(new DocViewDisplayScreen$3(this), 1000, true);
      }
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -2473353045158446860L) {
         this.optionsChanged(-1);
      } else if (guid == -7131874474196788121L && PersistentContent.isEncryptionEnabled()) {
         this.closeAttachment();
      } else {
         super.eventOccurred(guid, data0, data1, object0, object1);
      }
   }

   private synchronized void closeAttachment() {
      Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
      if (activeScreen instanceof Object || activeScreen instanceof InPlaceContextMenu) {
         UiApplication.getUiApplication().popScreen(activeScreen);
         activeScreen = UiApplication.getUiApplication().getActiveScreen();
      }

      if (activeScreen == this) {
         this.canExitScreen();
         UiApplication.getUiApplication().popScreen(this);
      }
   }

   DocViewDisplayScreen(IntHashtable paramsHash) {
      super(paramsHash.get(1), (String)paramsHash.get(2), paramsHash.get(4));
      this.setDefaultClose(false);
      this._docData = (DocViewParser)paramsHash.get(3);

      label633:
      try {
         this._themeScreenBgColor = paramsHash.get(9);
      } finally {
         break label633;
      }

      label630:
      try {
         this._themeScreenForeColor = paramsHash.get(11);
      } finally {
         break label630;
      }

      this._name = (String)paramsHash.get(2);
      this._parentMessage = (EmailMessageModel)ContextObject.get(super._context, 246);
      this.setLeaveScreenVerb(new DocViewExitVerb(this));

      label626:
      try {
         this._currentMaxBlockIndex = this._startBlockIndex = this._currentAutoLoadTargetBlockIndex = paramsHash.get(5);
      } finally {
         break label626;
      }

      label623:
      try {
         this._totalBlocks = paramsHash.get(6);
      } finally {
         break label623;
      }

      label620:
      try {
         this._isEmbScreen = paramsHash.get(0);
      } finally {
         break label620;
      }

      label617:
      try {
         this._morePartID = paramsHash.get(7);
      } finally {
         break label617;
      }

      label614:
      try {
         this._applicationID = paramsHash.get(12);
      } finally {
         break label614;
      }

      this._clientRequest = (ClientRequest)ContextObject.get(super._context, -7432523643332070209L);
      if (this._clientRequest != null) {
         if (!this._isEmbScreen) {
            this._startArbDomID = this._clientRequest._domID;
         }

         this._domID = this._clientRequest._domID;
         this._nextDOMID = this._clientRequest._nextDOMID;
         this._archiveIndicator = this._clientRequest._archiveIndicator;
         this._partIndex = this._clientRequest._partIndex;
      }

      label609:
      try {
         this._messageID = this._parentMessage.getUID();
         if (!this._isEmbScreen && this._domID != null && this._nextDOMID != null) {
            this._maxAllowedBlockIndex = this.getEndBlockIndexForArbDomID(this._domID);
            if (this._maxAllowedBlockIndex == -1) {
               this._maxAllowedBlockIndex = this.getStartBlockIndexForArbDomID(this._nextDOMID);
            }

            if (this._maxAllowedBlockIndex == -1) {
               this._maxAllowedBlockIndex = DocViewAttachmentPersist.getInstance()
                  .getChunkHint(this._messageID, this._morePartID, this._archiveIndicator, this._partIndex + 1);
            }
         }

         DocViewAttachmentViewerModel partModel = (DocViewAttachmentViewerModel)EmailMoreVerb.findMorePartByIdentifier(this._parentMessage, this._morePartID);
         if (this._clientRequest != null) {
            boolean isMoreAllSupported = AttachmentViewerFactory.isMoreAllSupported(this._parentMessage);
            byte[] contentType = partModel._conversionsAvailable[partModel.getPreferredConversion()];
            if (this._clientRequest._chunkSize != -1 && isMoreAllSupported) {
               this._autoMoreVerb = new DocViewMoreVerb(this, this._messageID, partModel, contentType, false, (byte)4);
               this._mainMoreVerb = new DocViewMoreVerb(this, this._messageID, partModel, contentType, true, (byte)2);
            } else {
               this._autoMoreVerb = new DocViewMoreVerb(this, this._messageID, partModel, contentType, false, (byte)3);
               this._mainMoreVerb = new DocViewMoreVerb(this, this._messageID, partModel, contentType, true, (byte)1);
            }

            if (isMoreAllSupported) {
               this._moreHandler = new DocViewGenericMoreAction(
                  super._context,
                  this._clientRequest,
                  new DocViewMoreVerb(this, this._messageID, partModel, contentType, true, (byte)1),
                  new DocViewMoreVerb(this, this._messageID, partModel, contentType, true, (byte)2),
                  new DocViewMoreVerb(this, this._messageID, partModel, contentType, false, (byte)3),
                  new DocViewMoreVerb(this, this._messageID, partModel, contentType, false, (byte)4)
               );
            } else {
               this._moreHandler = new DocViewGenericMoreAction(
                  super._context,
                  this._clientRequest,
                  new DocViewMoreVerb(this, this._messageID, partModel, contentType, true, (byte)1),
                  null,
                  new DocViewMoreVerb(this, this._messageID, partModel, contentType, true, (byte)3),
                  null
               );
            }
         }
      } finally {
         break label609;
      }

      if (this._maxAllowedBlockIndex == -1) {
         this._maxAllowedBlockIndex = this._totalBlocks - 1;
      }

      if (!this._isEmbScreen) {
         this._docType = DocViewAttachmentPersist.getInstance().getAttachmentType(this._messageID, this._morePartID, this._archiveIndicator);
         this._docSubtype = DocViewAttachmentPersist.getInstance().getAttachmentSubtype(this._messageID, this._morePartID, this._archiveIndicator);
         int iconIndex = AttachmentViewerFactory.getAttachmentIconIndex(this._docType, this._docSubtype);
         if (iconIndex != -1) {
            label595:
            try {
               this._icon = DocViewIcons.getIcons().getImage(iconIndex);
            } finally {
               break label595;
            }

            if (this._icon != null) {
               ImageField iconField = (ImageField)(new Object(65536));
               iconField.setImage(this._icon);
               this._titleField.add(iconField);
               this._titleField.add(new DocViewHorizontalSpacerField(this.getFont().getHeight() >> 2));
            }
         }
      }

      DocViewOptions.getOptions().addOptionsChangeListener(this);
      this._textField = new DocViewFittingLabelFieldEllipsis(this._name);
      this._titleField.add(this._textField);
      this.setTitle(this._titleField);
      this.setModel(new Object());
   }

   static Application access$500(DocViewDisplayScreen x0) {
      return x0._application;
   }

   static Application access$700(DocViewDisplayScreen x0) {
      return x0._application;
   }
}
