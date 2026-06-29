package net.rim.device.apps.internal.docview.gui;

import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.theme.ThemeAttributeSet;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.ObjectUtilities;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.MessageIcons;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.RichText;
import net.rim.device.internal.ui.UiInternal;
import net.rim.device.internal.ui.component.HorizontalSpacerField;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.device.internal.ui.component.SimpleInputDialog;

final class ForwardScreen extends AppsMainScreen implements TreeFieldCallback, MoreNotify, StateChangeNotify, GlobalEventListener {
   final int _applicationID = ((Random)(new Object())).nextInt();
   private Vector _attachmentVector;
   int _messageID;
   private Object _syncObject = new Object();
   private ImageField _iconField = (ImageField)(new Object(65536));
   private HorizontalFieldManager _titleManager = (HorizontalFieldManager)(new Object());
   private HorizontalSpacerField _spacerField;
   private int _iconIndex = -1;
   private IntHashtable _pwdInfoMap = (IntHashtable)(new Object());
   private MenuItem _retrieveItem;
   private MenuItem _viewItem;
   private MenuItem _playItem;
   private MenuItem _passwordItem;
   private boolean _displayInProgress;
   private int _idleRunnableID = -1;
   private ForwardScreen$FwdScreenTree _attachmentsTree;
   private int _pendingParts;
   private UiApplication _appInstance;
   private int _selectedAttachment = -1;
   private boolean _executeAction;
   private static final int TREEINDENT_WIDTH;
   private static final int IDLE_PERIOD;
   private static final ResourceBundle _resources = ResourceBundle.getBundle(-4603212010799374808L, "net.rim.device.apps.internal.resource.DocView");

   final int loadTreeElementData(int iAttachmentsCount) {
      int iCurrentSibling = -1;
      DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();
      int treeCount = 0;

      for (int i = 0; i < iAttachmentsCount; i++) {
         DocViewAttachmentViewerModel model = (DocViewAttachmentViewerModel)this._attachmentVector.elementAt(i);
         Vector attachmentContainer = persistInstance.getAttachmentPartsByIndex(this._messageID, model.getMorePartID());
         if (attachmentContainer != null && !attachmentContainer.isEmpty() || !model.isArchive()) {
            treeCount++;
            if (model.isArchive()) {
               AttachmentElementInfo elemInfo = new AttachmentElementInfo(model.getFilename());
               elemInfo._morePartID = model.getMorePartID();
               elemInfo._attachmentIndex = i;
               if (iCurrentSibling == -1) {
                  iCurrentSibling = this._attachmentsTree.addChildNode(0, elemInfo);
               } else {
                  iCurrentSibling = this._attachmentsTree.addSiblingNode(iCurrentSibling, elemInfo);
               }
            }

            int count = attachmentContainer == null ? 0 : attachmentContainer.size() - 1;
            int rootSibling = iCurrentSibling;

            for (int j = 0; j <= count; j++) {
               iCurrentSibling = this.addFullContentElement(
                  (SimpleSortingVector)(attachmentContainer == null ? null : attachmentContainer.elementAt(j)),
                  i,
                  model.getMorePartID(),
                  model.isArchive() ? persistInstance.getArchiveIndicator(this._messageID, model.getMorePartID(), j) : null,
                  iCurrentSibling == -1 ? 0 : iCurrentSibling,
                  model.isArchive() ? persistInstance.getAttachmentNameByIndex(this._messageID, model.getMorePartID(), j) : model.getFilename(),
                  iCurrentSibling == -1 || model.isArchive() && j == 0
               );
            }

            if (model.isArchive()) {
               if (count > 0 && i != this._selectedAttachment) {
                  this._attachmentsTree.setExpanded(rootSibling, false);
               }

               iCurrentSibling = rootSibling;
            }

            if (i == this._selectedAttachment) {
               this._attachmentsTree.setCurrentNode(iCurrentSibling);
            }
         }
      }

      if (this._selectedAttachment == -1) {
         int firstRoot = this._attachmentsTree.getFirstRoot();
         if (firstRoot != -1) {
            this._attachmentsTree.setCurrentNode(firstRoot);
            if (treeCount == 1 && this._attachmentsTree.getFirstChild(firstRoot) != -1 && !this._attachmentsTree.getExpanded(firstRoot)) {
               this._attachmentsTree.setExpanded(firstRoot, true);
            }
         }
      }

      return treeCount;
   }

   final String getValidPassword(int morePartID, String archiveIndicator) {
      ForwardScreen$PasswordInfo pwdInfo = this.getPwdInfo(morePartID, archiveIndicator);
      return pwdInfo != null && pwdInfo._password != null ? pwdInfo._password : null;
   }

   final void refreshTreeData() {
      this.checkZipEntries(this._attachmentVector.size());
      this.stateChanged();
   }

   final void onDocDisplayEnd() {
      synchronized (this._syncObject) {
         this._displayInProgress = false;
      }
   }

   final void scheduleExecuteAction(int morePartID, String archiveIndicator, int partID, boolean displayTrackChanges) {
      int node = this._attachmentsTree.findNode(morePartID, archiveIndicator, partID);
      if (node != -1) {
         AttachmentElementInfo info = (AttachmentElementInfo)this._attachmentsTree.getCookie(node);
         if (info != null && info.getElementPartID() == 999 && (info.getElementState() == 0 || info.getElementState() == 2)) {
            this._attachmentsTree.setCurrentNode(node);
            this._executeAction = true;
         }
      }
   }

   final void displayElement(int morePartID, String archiveIndicator, int partID, boolean displayTrackChanges) {
      int node = this._attachmentsTree.findNode(morePartID, archiveIndicator, partID);
      if (node != -1) {
         this._attachmentsTree.setCurrentNode(node);
         this.displayElement(node, displayTrackChanges);
      }
   }

   protected final void createMenuItems() {
      this._retrieveItem = new ForwardScreen$9(this, _resources, 8, 65552, 0);
      this._viewItem = new ForwardScreen$10(this, _resources, 9, 65552, 0);
      this._playItem = new ForwardScreen$11(this, _resources, 108, 65552, 0);
      this._passwordItem = new ForwardScreen$12(this, _resources, 7, 131088, 0);
   }

   @Override
   public final void notifyMoreRequestFailed(LatestRequestInfo reqInfo) {
      if (reqInfo != null && reqInfo._messageID == this._messageID) {
         int node = this._attachmentsTree.findNode(reqInfo._attachmentMoreID, reqInfo._archiveIndicatorString, reqInfo._attachmentPartID);
         if (node != -1) {
            this.singleNodeUpdate(node, reqInfo._attachmentMoreID, reqInfo._archiveIndicatorString, reqInfo._attachmentPartID);
         }

         this._appInstance.invokeLater(new ForwardScreen$7(this));
      }
   }

   @Override
   public final void stateChanged() {
      int node = this._attachmentsTree.nextNode(0, 0, true);
      boolean refreshScreen = false;

      while (node != -1) {
         if (this.notifyStateChanged(node, false)) {
            refreshScreen = true;
         }

         node = this._attachmentsTree.nextNode(node, 0, true);
      }

      if (refreshScreen) {
         this._appInstance.invokeLater(new ForwardScreen$8(this));
      }
   }

   @Override
   public final void drawTreeItem(TreeField treeField, Graphics graphics, int node, int y, int width, int indent) {
      AttachmentElementInfo crtElement = (AttachmentElementInfo)treeField.getCookie(node);
      int nodeDepth = this._attachmentsTree.getNodeDepth(node);
      int crtIndent = nodeDepth == 0 && crtElement.getElementPartID() != -2 ? 1 : indent;
      Font f = graphics.getFont();
      int iconHeight = f.getHeight();
      int lineNumber = treeField.getLineNumberForNode(node);
      int lineHeight = this._attachmentsTree.getRowHeight(lineNumber);
      int yPosAdjustment = treeField.getAdjustedY(this.getFont(), crtElement.toString(), y) - y;
      yPosAdjustment = Math.max(lineHeight - iconHeight >> 1, yPosAdjustment);
      if (crtElement.getElementPartID() != 999) {
         if (nodeDepth == 0 && this.isArchive(crtElement)) {
            label74:
            try {
               Image icon = this._attachmentsTree.getExpanded(node) ? DocViewIcons.getIcons().getImage(1) : DocViewIcons.getIcons().getImage(0);
               graphics.clear(0, y, crtIndent, iconHeight);
               int iconWidth = DocViewIcons.getIcons().getWidth(f);
               icon.paint(graphics, Math.max(1, indent - 8 - iconWidth), y + yPosAdjustment, iconWidth, iconHeight);
            } finally {
               break label74;
            }

            String text = crtElement.toString();
            RichText.drawTextWithEllipses(graphics, text, crtIndent + 2, y + yPosAdjustment, width - 2, RichText.getLineDirection(text), 64);
         }
      } else {
         int iconIndex = -1;
         switch (crtElement.getElementState()) {
            case 0:
               break;
            case 1:
            default:
               iconIndex = 7;
               break;
            case 2:
               iconIndex = 9;
               break;
            case 3:
               iconIndex = 12;
         }

         int iconWidth = iconIndex != -1 ? MessageIcons.getIcons().getWidth(f) : 0;
         int totalWidth = nodeDepth == 0 ? treeField.getWidth() : width;
         String text = crtElement.toString();
         RichText.drawTextWithEllipses(graphics, text, crtIndent, y + yPosAdjustment, totalWidth - iconWidth - 2, RichText.getLineDirection(text), 64);
         if (iconIndex != -1) {
            MessageIcons.paint(graphics, crtIndent + totalWidth - iconWidth - 1, y + yPosAdjustment, iconWidth, iconHeight, iconIndex);
            return;
         }
      }
   }

   @Override
   public final void notifyMoreRequestCompleted(int messageID, int morePartID, ServerResponse response, byte[] ucsData, int totalRetrievedBlocks) {
      if (messageID == this._messageID && response._docID._partIndex == 999) {
         new ForwardScreen$MoreUpdateThread(this, response, morePartID).start();
      }

      this.checkTitleInformation(this._attachmentsTree.getCurrentNode(), true);
      this._appInstance.invokeLater(new ForwardScreen$6(this), 1100, false);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == -7131874474196788121L && PersistentContent.isEncryptionEnabled()) {
         this.closeForwardScreen();
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      switch (UiInternal.map(keycode)) {
         case 'P':
         case 'p':
            try {
               AttachmentElementInfo info = (AttachmentElementInfo)this._attachmentsTree.getCookie(this._attachmentsTree.getCurrentNode());
               if (info.getElementPartID() == 999) {
                  byte docType = DocViewAttachmentPersist.getInstance().getAttachmentType(this._messageID, info._morePartID, info.getElementArchiveIndicator());
                  byte docSubtype = DocViewAttachmentPersist.getInstance()
                     .getAttachmentSubtype(this._messageID, info._morePartID, info.getElementArchiveIndicator());
                  boolean readPwd = docType == 0 && docSubtype == 2;
                  if (!readPwd) {
                     readPwd = docType == -1 && docSubtype == -1 && StringUtilities.toLowerCase(info.toString(), 1701707776).indexOf(".pdf") != -1;
                  }

                  if (readPwd) {
                     this.readPassword(info._morePartID, info.getElementArchiveIndicator());
                     return true;
                  }
               }
            } finally {
               return super.keyDown(keycode, time);
            }
         default:
            return super.keyDown(keycode, time);
      }
   }

   private final void displayElement(int crtNode, boolean displayTrackChanges) {
      if (crtNode != -1) {
         AttachmentElementInfo info = (AttachmentElementInfo)this._attachmentsTree.getCookie(crtNode);
         if (info != null && info.getElementState() == 2) {
            DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();
            byte type = persistInstance.getAttachmentType(this._messageID, info._morePartID, info.getElementArchiveIndicator());
            if (type == 5 && !AttachmentViewerFactory.isAudioStreamingSupported()) {
               Dialog.alert(_resources.getString(75));
               return;
            }

            synchronized (this._syncObject) {
               if (this._displayInProgress) {
                  return;
               }

               this._displayInProgress = true;
            }

            ContextObject cloneContext = ContextObject.castOrCreate(this.getContext()).clone();
            String domID = info.getElementDOMId();
            String nextDOMID = null;
            int partID = info.getElementPartID();
            int startBlockIndex = persistInstance.getStartBlockIndex(this._messageID, info._morePartID, info.getElementArchiveIndicator(), partID);
            int endBlockIndex = AttachmentViewerFactory.getEndBlockIndexWithArbDomID(
               this._messageID,
               info._morePartID,
               info.getElementArchiveIndicator(),
               null,
               partID == 999 ? persistInstance.getFirstArbDomID(this._messageID, info._morePartID, info.getElementArchiveIndicator(), null) : domID,
               persistInstance.getAttachmentBlockCount(this._messageID, info._morePartID, info.getElementArchiveIndicator(), null)
            );
            if (info._bDynamicPart) {
               int nextSibling = this._attachmentsTree.getNextSibling(crtNode);
               if (nextSibling != -1) {
                  nextDOMID = ((AttachmentElementInfo)this._attachmentsTree.getCookie(nextSibling)).getElementDOMId();
               }
            }

            int chunkSize = -1;
            if (type != 2) {
               byte[] xcsData = persistInstance.getUCSData(this._messageID, info._morePartID, info.getElementArchiveIndicator(), "XChSize", 0);
               if (xcsData != null) {
                  chunkSize = DocViewUtilities.readInt(xcsData, 0, false);
               }
            } else {
               chunkSize = AttachmentViewerFactory.getReqChunkSize(this.getAssocMessageModel(), type);
            }

            persistInstance.onPartAccess(this._messageID, info._morePartID, info.getElementArchiveIndicator(), partID);
            ContextObject.put(
               cloneContext,
               -7432523643332070209L,
               new ClientRequest(
                  type == 5 ? "AUDIO" : "NEXT",
                  partID,
                  domID,
                  nextDOMID,
                  null,
                  -1,
                  this.getPassword(info._morePartID, info.getElementArchiveIndicator()),
                  startBlockIndex,
                  (byte)-1,
                  info.getElementArchiveIndicator(),
                  null,
                  null,
                  false,
                  true,
                  chunkSize,
                  false,
                  type == 2,
                  null,
                  DocViewGUIInternalConstants.SCREEN_WIDTH,
                  DocViewGUIInternalConstants.SCREEN_HEIGHT,
                  -1,
                  -1
               )
            );
            ActiveDisplayedPart part = DocViewDisplayScreenInstance.getActivePartInstance(this._messageID, this._applicationID);
            if (part != null && part.compare(this._messageID, info)) {
               part._screen.reInitialize(cloneContext);
               part._screen.go(true);
               this.onDocDisplayEnd();
               return;
            }

            byte subtype = persistInstance.getAttachmentSubtype(this._messageID, info._morePartID, info.getElementArchiveIndicator());
            AttachmentViewerFactory.constructAndShowNewDisplayScreen(
               this._messageID,
               info._morePartID,
               info.getElementArchiveIndicator(),
               null,
               AttachmentViewerFactory.isTypeRequestAllChunks(
                  persistInstance.getAttachmentType(this._messageID, info._morePartID, info.getElementArchiveIndicator())
               ),
               startBlockIndex,
               endBlockIndex,
               info.toString(),
               cloneContext,
               ThemeAttributeSet.getColor(this, 0),
               ThemeAttributeSet.getColor(this, 1),
               displayTrackChanges,
               this._applicationID,
               false,
               AttachmentViewerFactory.getPresentationValue(type, subtype)
            );
         }
      }
   }

   private final EmailMessageModel getAssocMessageModel() {
      return (EmailMessageModel)ContextObject.get(this.getContext(), 246);
   }

   private final boolean isFwdScreenTop() {
      if (this._appInstance.isForeground()) {
         try {
            Screen activeScreen = this.getUiEngine().getActiveScreen();

            while (activeScreen instanceof Object) {
               activeScreen = activeScreen.getScreenBelow();
            }

            if (ObjectUtilities.objEqual(this, activeScreen)) {
               return true;
            }
         } finally {
            return false;
         }
      }

      return false;
   }

   private final void doAutoView(int node) {
      this._appInstance.invokeLater(new ForwardScreen$4(this, node), 1250, false);
   }

   private final void singleNodeUpdate(int node, int morePartID, String archiveIndicator, int attachmentPartId) {
      AttachmentElementInfo elemInfo = (AttachmentElementInfo)this._attachmentsTree.getCookie(node);
      AttachmentElementInfo newInfo = DocViewAttachmentPersist.getInstance().getElementData(this._messageID, morePartID, archiveIndicator, attachmentPartId);
      if (newInfo != null && newInfo.getElementState() != elemInfo.getElementState()) {
         boolean wasPending = elemInfo.getElementState() == 1;
         elemInfo.setElementState(newInfo.getElementState());
         elemInfo.setServerResponseTag(newInfo.getLastServerResponse());
         if (wasPending) {
            synchronized (this._syncObject) {
               if (this._pendingParts > 0) {
                  this._pendingParts--;
               }
            }
         }

         this._appInstance.invokeLater(new ForwardScreen$5(this, node));
      }
   }

   private final int addFullContentElement(
      SimpleSortingVector attachmentInfoVector, int index, int morePartID, String archiveIndicator, int iCurrentSibling, String name, boolean addChild
   ) {
      AttachmentElementInfo elemInfo;
      if (attachmentInfoVector != null
         && !attachmentInfoVector.isEmpty()
         && ((AttachmentElementInfo)attachmentInfoVector.lastElement()).getElementPartID() == 999) {
         elemInfo = (AttachmentElementInfo)attachmentInfoVector.lastElement();
         elemInfo.setElementName(name);
         if (elemInfo.getElementState() == 1) {
            this._pendingParts++;
         }
      } else {
         elemInfo = new AttachmentElementInfo(name, 999);
      }

      elemInfo._attachmentIndex = index;
      elemInfo._morePartID = morePartID;
      elemInfo.setElementArchiveIndicator(archiveIndicator);
      return addChild ? this._attachmentsTree.addChildNode(iCurrentSibling, elemInfo) : this._attachmentsTree.addSiblingNode(iCurrentSibling, elemInfo);
   }

   ForwardScreen(Object context, Vector attachmentVector, int iSelectedAttachment) {
      super(299067162755072L);
      this.setContext(ContextObject.castOrCreate(context));
      if (context instanceof Object) {
         this.setContext((ContextObject)context);
      } else {
         this.setHelp("messages_index");
      }

      this._attachmentVector = attachmentVector;
      this._selectedAttachment = iSelectedAttachment;
      this._appInstance = UiApplication.getUiApplication();
      this._messageID = this.getAssocMessageModel().getUID();
      this.clearCacheScreen();
      DocViewDisplayScreenInstance.removeActivePartInstance(this._messageID, this._applicationID);
      DocViewDisplayScreenInstance.putForwardScreenInstance(this);
      int iAttachmentsCount = attachmentVector.size();
      this._attachmentsTree = new ForwardScreen$FwdScreenTree(this, this, 0);
      this._attachmentsTree.setIndentWidth(8);
      iAttachmentsCount = this.loadTreeElementData(iAttachmentsCount);
      this.add(this._attachmentsTree);
      String strAttachments = ((StringBuffer)(new Object())).append(_resources.getString(0)).append(": ").append(String.valueOf(iAttachmentsCount)).toString();
      this._titleManager.add(new DocViewFittingLabelFieldEllipsis(strAttachments));
      this.setTitle(this._titleManager);
      this.createMenuItems();
      this._idleRunnableID = this._appInstance.invokeLater(new ForwardScreen$1(this), 900000, true);
   }

   private final synchronized void closeForwardScreen() {
      Screen activeScreen = UiApplication.getUiApplication().getActiveScreen();
      if (activeScreen instanceof Object) {
         UiApplication.getUiApplication().popScreen(activeScreen);
         activeScreen = UiApplication.getUiApplication().getActiveScreen();
      }

      if (activeScreen == this) {
         this.onClose();
      }
   }

   private final boolean notifyStateChanged(int node, boolean isSynchronized) {
      boolean updated = false;

      try {
         AttachmentElementInfo infoNode = (AttachmentElementInfo)this._attachmentsTree.getCookie(node);
         if (infoNode.getElementPartID() != -2) {
            AttachmentElementInfo savedInfo = DocViewAttachmentPersist.getInstance()
               .getElementData(this._messageID, infoNode._morePartID, infoNode.getElementArchiveIndicator(), infoNode.getElementPartID());
            byte oldState = infoNode.getElementState();
            boolean decreasePendingCount = false;
            if (savedInfo != null) {
               byte newState = savedInfo.getElementState();
               if (newState != oldState) {
                  infoNode.setElementState(newState);
                  decreasePendingCount = oldState == 1;
                  updated = true;
               }

               infoNode.setServerResponseTag(savedInfo.getLastServerResponse());
            } else if (oldState != 0) {
               infoNode.setElementState((byte)0);
               infoNode.setServerResponseTag((short)0);
               decreasePendingCount = oldState == 1;
               updated = true;
            }

            if (decreasePendingCount) {
               if (!isSynchronized) {
                  synchronized (this._syncObject) {
                     if (this._pendingParts > 0) {
                        this._pendingParts--;
                     }
                  }
               } else if (this._pendingParts > 0) {
                  this._pendingParts--;
                  return updated;
               }
            }
         }
      } finally {
         return updated;
      }

      return updated;
   }

   @Override
   public final boolean onClose() {
      DocViewDisplayScreenInstance.removeForwardScreenInstance(this._messageID, this._applicationID);
      this.clearCacheScreen();
      DocViewAttachmentPersist.commitChanges();
      if (this._idleRunnableID != -1) {
         this._appInstance.cancelInvokeLater(this._idleRunnableID);
         this._idleRunnableID = -1;
      }

      DocViewPreviewFieldManager.getInstance().removeCachedObjects(this._messageID);
      return super.onClose();
   }

   private final void checkTitleInformation(int node, boolean synchronize) {
      if (node != -1) {
         AttachmentElementInfo crtElement = (AttachmentElementInfo)this._attachmentsTree.getCookie(node);
         DocViewAttachmentPersist docInstance = DocViewAttachmentPersist.getInstance();
         int iconIndex = AttachmentViewerFactory.getAttachmentIconIndex(
            docInstance.getAttachmentType(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator()),
            docInstance.getAttachmentSubtype(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator())
         );
         if (iconIndex != this._iconIndex) {
            if (iconIndex != -1) {
               Image icon = null;

               label81:
               try {
                  icon = DocViewIcons.getIcons().getImage(iconIndex);
               } finally {
                  break label81;
               }

               if (icon != null) {
                  if (this._spacerField == null) {
                     this._spacerField = new DocViewHorizontalSpacerField(this.getFont().getHeight() >> 2);
                  }

                  if (this._iconField == null) {
                     this._iconField = (ImageField)(new Object(65536));
                  }

                  if (synchronize) {
                     Image laterIcon = icon;
                     this._appInstance.invokeLater(new ForwardScreen$2(this, laterIcon));
                  } else {
                     this._iconField.setImage(icon);
                     if (this._spacerField.getIndex() == -1) {
                        this._titleManager.add(this._spacerField);
                     }

                     if (this._iconField.getIndex() == -1) {
                        this._titleManager.add(this._iconField);
                     }
                  }
               } else {
                  iconIndex = -1;
               }
            }

            if (iconIndex == -1 && this._iconField != null && this._iconField.getIndex() != -1) {
               this._appInstance.invokeLater(new ForwardScreen$3(this));
            }

            this._iconIndex = iconIndex;
         }
      }
   }

   private final void checkZipEntries(int iAttachmentsCount) {
      DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();

      for (int i = 0; i < iAttachmentsCount; i++) {
         DocViewAttachmentViewerModel model = (DocViewAttachmentViewerModel)this._attachmentVector.elementAt(i);
         if (model.isArchive() && !persistInstance.isArchiveInformationAdded(this._messageID, model.getMorePartID())) {
            IntHashtable archiveHash = CommandHandler.decodeArchiveContents(model.getData());
            if (archiveHash.size() > 0) {
               persistInstance.addArchiveInformation(this._messageID, model.getMorePartID(), archiveHash);
            }
         }
      }
   }

   @Override
   protected final void makeMenu(Menu menu, int instance) {
      int nCurrentNode = this._attachmentsTree.getCurrentNode();
      if (nCurrentNode != -1) {
         AttachmentElementInfo crtElement = (AttachmentElementInfo)this._attachmentsTree.getCookie(nCurrentNode);
         if (crtElement.getElementPartID() == 999) {
            if (crtElement.getElementState() == 2) {
               if (DocViewAttachmentPersist.getInstance().getAttachmentType(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator())
                  != 5) {
                  menu.add(this._viewItem);
                  menu.setDefault(this._viewItem);
               } else {
                  menu.add(this._playItem);
                  menu.setDefault(this._playItem);
               }
            } else {
               menu.add(this._retrieveItem);
               menu.setDefault(this._retrieveItem);
            }

            byte docType = DocViewAttachmentPersist.getInstance()
               .getAttachmentType(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator());
            byte docSubtype = DocViewAttachmentPersist.getInstance()
               .getAttachmentSubtype(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator());
            boolean addPwdMenuItem = docType == 0 && docSubtype == 2;
            if (!addPwdMenuItem) {
               addPwdMenuItem = docType == -1 && docSubtype == -1 && StringUtilities.toLowerCase(crtElement.toString(), 1701707776).indexOf(".pdf") != -1;
            }

            if (addPwdMenuItem) {
               menu.add(this._passwordItem);
            }
         }
      }

      super.makeMenu(menu, instance);
   }

   @Override
   protected final void onUiEngineAttached(boolean attached) {
      if (attached) {
         Application app = Application.getApplication();
         app.addGlobalEventListener(this);
         super.onUiEngineAttached(attached);
         if (this._executeAction) {
            this._executeAction = false;
            this.doActionOnSelectedNode(true);
            return;
         }
      } else {
         Application app = Application.getApplication();
         app.removeGlobalEventListener(this);
         super.onUiEngineAttached(attached);
      }
   }

   private final boolean isArchive(AttachmentElementInfo crtElement) {
      return DocViewAttachmentPersist.getInstance().isArchive(this._messageID, crtElement._morePartID);
   }

   private final void saveLatestMoreRequest(AttachmentElementInfo info) {
      boolean bgEmailFailed = true;

      label34:
      try {
         bgEmailFailed = this.getAssocMessageModel().getTransmissionError() != 0;
      } finally {
         break label34;
      }

      Object obj = ApplicationRegistry.getApplicationRegistry().get(-5725347163685773030L);
      if (!(obj instanceof LatestRequestInfo)) {
         ApplicationRegistry.getApplicationRegistry()
            .replace(
               -5725347163685773030L,
               new LatestRequestInfo(
                  this._messageID, info._morePartID, info.getElementArchiveIndicator(), info.getElementPartID(), bgEmailFailed, this._applicationID
               )
            );
      } else {
         LatestRequestInfo request = (LatestRequestInfo)obj;
         request._attachmentMoreID = info._morePartID;
         request._attachmentPartID = info.getElementPartID();
         request._archiveIndicatorString = info.getElementArchiveIndicator();
         request._messageAlreadyFailed = bgEmailFailed;
         request._applicationID = this._applicationID;
         request._messageID = this._messageID;
      }
   }

   private final boolean isPwdRequired(int node) {
      AttachmentElementInfo element = (AttachmentElementInfo)this._attachmentsTree.getCookie(node);
      ForwardScreen$PasswordInfo pwdInfo = this.getPwdInfo(element._morePartID, element.getElementArchiveIndicator());
      if (pwdInfo != null) {
         return pwdInfo._requireDisplayPwd;
      }

      short lastErrorCode = element.getLastServerResponse();
      return lastErrorCode == 3 || lastErrorCode == 4;
   }

   private final void doActionOnSelectedNode(boolean isAutoMore) {
      int nCurrentNode = this._attachmentsTree.getCurrentNode();
      if (nCurrentNode != -1) {
         AttachmentElementInfo crtElement = (AttachmentElementInfo)this._attachmentsTree.getCookie(nCurrentNode);
         if (crtElement.getElementPartID() != -2) {
            byte action = 2;
            byte docType = DocViewAttachmentPersist.getInstance()
               .getAttachmentType(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator());
            int audioServerCodec = -1;
            if (docType == 5 && crtElement.getElementPartID() >= 0 && crtElement.getElementPartID() <= 999) {
               audioServerCodec = AttachmentViewerFactory.getAttachmentServerCodecValue();
               if (audioServerCodec == -1) {
                  Dialog.alert(_resources.getString(75));
                  return;
               }
            }

            switch (crtElement.getElementState()) {
               case -1:
                  break;
               case 0:
               default:
                  action = 1;
                  break;
               case 1:
                  String strMessage = _resources.getString(4);
                  strMessage = ((StringBuffer)(new Object())).append(strMessage).append(' ').append(_resources.getString(5)).toString();
                  if (SimpleChoiceDialog.askYesNoQuestion(strMessage)) {
                     action = 0;
                  }
                  break;
               case 2:
                  this.displayElement(nCurrentNode, false);
                  break;
               case 3:
                  if (SimpleChoiceDialog.askYesNoQuestion(
                     ((StringBuffer)(new Object()))
                        .append(AttachmentViewerFactory.getErrorString(crtElement.getLastServerResponse()))
                        .append(' ')
                        .append(_resources.getString(5))
                        .toString()
                  )) {
                     action = 1;
                  }
            }

            if (action != 2 && crtElement.getElementPartID() != -2) {
               if (this.isPwdRequired(nCurrentNode) && !this.readPassword(crtElement._morePartID, crtElement.getElementArchiveIndicator())) {
                  return;
               }

               if (action == 1) {
                  crtElement.setElementState((byte)1);
                  this._attachmentsTree.invalidateNode(nCurrentNode);
                  DocViewAttachmentPersist.getInstance()
                     .addAttachmentPart(
                        this._messageID,
                        crtElement._morePartID,
                        crtElement.getElementArchiveIndicator(),
                        crtElement.toString(),
                        crtElement.getElementDOMId(),
                        crtElement.getElementPartID(),
                        crtElement.getElementState(),
                        crtElement.getLastServerResponse(),
                        crtElement._bDynamicPart
                     );
                  synchronized (this._syncObject) {
                     this._pendingParts++;
                  }
               } else if (action == 0) {
                  DocViewAttachmentPersist.getInstance()
                     .onPartAccess(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator(), crtElement.getElementPartID());
               }

               this.saveLatestMoreRequest(crtElement);
               DocViewAttachmentViewerModel model = (DocViewAttachmentViewerModel)this._attachmentVector.elementAt(crtElement._attachmentIndex);
               ContextObject cloneContext = ContextObject.castOrCreate(this.getContext()).clone();
               byte[] contentType = null;
               if (model._conversionsAvailable != null) {
                  contentType = model._conversionsAvailable[model.getPreferredConversion()];
               }

               byte moreType = (byte)(isAutoMore ? 3 : 1);
               boolean isMoreAllSupported = AttachmentViewerFactory.isMoreAllSupported(this.getAssocMessageModel());
               if (isMoreAllSupported) {
                  moreType = (byte)(isAutoMore ? 4 : 2);
               }

               DocViewMoreVerb retrieveMoreVerb = new DocViewMoreVerb(null, this._messageID, model, contentType, true, moreType);
               int chunkHint = DocViewAttachmentPersist.getInstance()
                  .getChunkHint(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator(), crtElement.getElementPartID());
               String domID = crtElement.getElementDOMId();
               String arbDomID = null;
               if (chunkHint == -1 && domID != null) {
                  arbDomID = domID;
                  domID = null;
               }

               int chunkSize = AttachmentViewerFactory.getReqChunkSize(this.getAssocMessageModel(), docType);
               if (DocViewAttachmentPersist.getInstance()
                        .getAttachmentRetrievedBlockCount(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator(), null)
                     > 0
                  && docType != 2) {
                  byte[] xcsData = DocViewAttachmentPersist.getInstance()
                     .getUCSData(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator(), "XChSize", 0);
                  if (xcsData != null) {
                     chunkSize = DocViewUtilities.readInt(xcsData, 0, false);
                  } else {
                     chunkSize = -1;
                  }
               }

               String commandCode = docType == 5 ? "AUDIO" : "NEXT";
               ContextObject.put(
                  cloneContext,
                  -7432523643332070209L,
                  new ClientRequest(
                     commandCode,
                     crtElement.getElementPartID(),
                     domID,
                     null,
                     arbDomID,
                     chunkHint,
                     this.getPassword(crtElement._morePartID, crtElement.getElementArchiveIndicator()),
                     DocViewAttachmentPersist.getInstance()
                        .getStartBlockIndex(this._messageID, crtElement._morePartID, crtElement.getElementArchiveIndicator(), crtElement.getElementPartID()),
                     (byte)-1,
                     crtElement.getElementArchiveIndicator(),
                     null,
                     null,
                     false,
                     true,
                     chunkSize,
                     docType == -1,
                     docType == 2 || docType == -1,
                     null,
                     DocViewGUIInternalConstants.SCREEN_WIDTH,
                     DocViewGUIInternalConstants.SCREEN_HEIGHT,
                     docType == -1 ? (isMoreAllSupported ? 64000 : -1) : -1,
                     audioServerCodec
                  )
               );
               this._appInstance.invokeLater(new ForwardScreen$13(this, retrieveMoreVerb, cloneContext));
               Status.show(
                  ((StringBuffer)(new Object())).append(_resources.getString(118)).append(' ').append(crtElement.toString()).append('…').toString(), 2500
               );
            }
         }
      }
   }

   private final ForwardScreen$PasswordInfo getPwdInfo(int morePartID, String archiveIndicator) {
      Object obj1 = this._pwdInfoMap.get(morePartID);
      if (obj1 instanceof Object) {
         Hashtable hash = (Hashtable)obj1;
         Object obj2 = hash.get(archiveIndicator == null ? "" : archiveIndicator);
         if (obj2 instanceof ForwardScreen$PasswordInfo) {
            return (ForwardScreen$PasswordInfo)obj2;
         }
      }

      return null;
   }

   private final String getPassword(int morePartID, String archiveIndicator) {
      ForwardScreen$PasswordInfo pwdInfo = this.getPwdInfo(morePartID, archiveIndicator);
      return pwdInfo != null ? pwdInfo._password : null;
   }

   private final boolean readPassword(int morePartID, String archiveIndicator) {
      SimpleInputDialog pwdDlg = (SimpleInputDialog)(new Object(5, CommonResources.getString(2012)));
      ForwardScreen$PasswordInfo infoPwd = this.getPwdInfo(morePartID, archiveIndicator);
      if (infoPwd != null && infoPwd._password != null && infoPwd._password.length() > 0) {
         pwdDlg.setText(infoPwd._password);
      }

      pwdDlg.show();
      if (pwdDlg.getCloseReason() == 0) {
         if (infoPwd != null) {
            infoPwd._password = pwdDlg.getText();
            infoPwd._requireDisplayPwd = false;
            return true;
         }

         Hashtable pwdHash = (Hashtable)this._pwdInfoMap.get(morePartID);
         if (pwdHash == null) {
            pwdHash = (Hashtable)(new Object());
            this._pwdInfoMap.put(morePartID, pwdHash);
         }

         pwdHash.put(archiveIndicator == null ? "" : archiveIndicator, new ForwardScreen$PasswordInfo(pwdDlg.getText()));
         return true;
      } else {
         return false;
      }
   }

   private final void clearCacheScreen() {
      try {
         ExitScreen activeDisplay = DocViewDisplayScreenInstance.getActivePartInstance(this._messageID, this._applicationID)._screen;
         if (!ObjectUtilities.objEqual(this.getUiEngine().getActiveScreen(), activeDisplay)) {
            activeDisplay.releaseRefs();
            return;
         }
      } finally {
         return;
      }
   }
}
