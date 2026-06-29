package net.rim.device.apps.internal.docview.gui;

import java.util.Timer;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ActionProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.registration.ModelViewListenerRegistry;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.transmission.rim.CMIMEContentType;
import net.rim.device.apps.api.transmission.rim.RIMMessagingMoreMessage;
import net.rim.device.apps.internal.attachment.AttachmentViewerModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.ProxyModel;

final class DocViewAttachmentViewerModel extends AttachmentViewerModel implements PersistableRIMModel, VerbProvider, ActionProvider {
   final int requestFirstChunk(EmailMessageModel messageModel, ContextObject context, int remainingRequests, int maxArchiveElements) {
      if (remainingRequests <= 0) {
         return 0;
      }

      int retValue = 0;
      byte moreType = 3;
      boolean isMoreAllSupported = AttachmentViewerFactory.isMoreAllSupported(messageModel);
      if (isMoreAllSupported) {
         moreType = 4;
      }

      byte[] contentType = null;
      if (super._conversionsAvailable != null) {
         contentType = super._conversionsAvailable[this.getPreferredConversion()];
      }

      DocViewMoreVerb retrieveMoreVerb = new DocViewMoreVerb(null, messageModel.getUID(), this, contentType, true, moreType);
      if (retrieveMoreVerb != null) {
         if (this.isArchive()) {
            IntHashtable archiveHash = CommandHandler.decodeArchiveContents(this.getData());
            if (archiveHash.size() > 0) {
               DocViewAttachmentPersist.getInstance().addArchiveInformation(messageModel.getUID(), this.getMorePartID(), archiveHash);
               DocViewAttachmentPersist.commitChanges();
               int remainingReq = remainingRequests;
               retValue = 0;

               for (IntEnumeration keys = archiveHash.keys(); keys.hasMoreElements(); retValue++) {
                  int nextKey = keys.nextElement();
                  if (remainingReq <= 0 || retValue >= maxArchiveElements) {
                     break;
                  }

                  this.requestInitialChunk(messageModel, context, retrieveMoreVerb, String.valueOf(nextKey));
                  remainingReq--;
               }
            }
         } else {
            this.requestInitialChunk(messageModel, context, retrieveMoreVerb, null);
            retValue = 1;
         }

         DocViewMoreVerb var14 = null;
      }

      return retValue;
   }

   final boolean isArchive() {
      return this.matchContentType("application/x-rimdevicezipucs");
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      if (actionId == -7793619941406158181L) {
         ProxyModel proxyModel = (ProxyModel)ContextObject.get(context, 5983802064804519487L);
         if (proxyModel != null) {
            DocViewProxyModelStore proxyModelStore = DocViewProxyModelStore.getInstance();
            proxyModelStore.delete(proxyModel.getObjectHandle());
         }

         Object o = ContextObject.get(context, -7450314121582082994L);
         if (o instanceof EmailMessageModel) {
            EmailMessageModel emailMessageModel = (EmailMessageModel)o;
            DocViewAttachmentPersist.getInstance().onMessageDeleted(emailMessageModel.getUID());
            DocViewAttachmentPersist.commitChanges();
            DocViewPreviewFieldManager.getInstance().removeCachedObjects(emailMessageModel.getUID());
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      return null;
   }

   private final String getPassword(int messageID, int partID, String archiveIndicator) {
      ActiveDisplayedPart[] displayParts = DocViewDisplayScreenInstance.getActivePartInstances();
      if (displayParts != null) {
         for (int i = 0; i < displayParts.length; i++) {
            if (displayParts[i]._screen != null && displayParts[i].compare(messageID, partID, archiveIndicator)) {
               String pwd = displayParts[i]._screen.getValidPassword();
               if (pwd != null) {
                  return pwd;
               }
            }
         }
      }

      ForwardScreen[] fwdScreens = DocViewDisplayScreenInstance.getForwardScreenInstances();
      if (fwdScreens != null) {
         for (int i = 0; i < fwdScreens.length; i++) {
            if (fwdScreens[i]._messageID == messageID) {
               String pwd = fwdScreens[i].getValidPassword(partID, archiveIndicator);
               if (pwd != null) {
                  return pwd;
               }
            }
         }
      }

      return null;
   }

   @Override
   public final void receiveMore(Object context, Object moreObject) {
      SerialRunnableManager.post(new DocViewAttachmentViewerModel$ReceiveRunnable(this, context, (RIMMessagingMoreMessage)moreObject, null));
   }

   private final void processReceiveMore(Object context, String descriptor, byte[] moreData, int messageID, int partID) {
      try {
         ServerResponse response = CommandHandler.decodeServerResponse(descriptor);
         this.processReceiveMoreImpl(context, response, moreData, messageID, partID, true);
      } finally {
         return;
      }
   }

   private final void processReceiveMoreImpl(Object context, ServerResponse response, byte[] moreData, int messageID, int partID, boolean delayForRenderedData) {
      try {
         byte type = response._docID._domID != null && response._docID._srcType == 1 ? response._docID._embObjType : response._docID._docType;
         byte subtype = response._docID._domID != null && response._docID._srcType == 1 ? response._docID._embObjSubtype : response._docID._docSubtype;
         String embeddedDomID = null;
         if (response._docID._srcType == 1) {
            embeddedDomID = response._docID._domID;
         }

         DocViewAtomicID dataId = new DocViewAtomicID(response._docID);
         embeddedDomID = customProcessResponse(dataId, response._arbDOMIDStartArray, embeddedDomID, response);
         Object emailMsgModel = MessageLookups.get(-4420850319371185992L, messageID);
         EmailMessageModel messageModel = null;
         if (emailMsgModel instanceof EmailMessageModel) {
            messageModel = (EmailMessageModel)emailMsgModel;
         }

         if (delayForRenderedData && messageModel != null) {
            String pageDomID = this.autoRequestRenderedDomID(
               response, messageModel, super._conversionsAvailable[this.getPreferredConversion()], messageID, partID, embeddedDomID, type, subtype
            );
            if (pageDomID != null) {
               ServerResponse responseFinal = response;
               String embeddedDomIDFinal = embeddedDomID;
               Object contextFinal = context;
               Timer timerFinal = new Timer();
               timerFinal.scheduleAtFixedRate(
                  new DocViewAttachmentViewerModel$1(this, embeddedDomIDFinal, pageDomID, messageID, partID, responseFinal, contextFinal, moreData, timerFinal),
                  2000,
                  1500
               );
               return;
            }
         }

         short realErrorCode = response._errorCode;
         boolean isTypeReqAll = AttachmentViewerFactory.isTypeRequestAllChunks(type)
            || response._docID._partIndex == 1005
            || response._docID._partIndex == 1002
            || response._docID._partIndex == 1003;
         DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();
         boolean isFullContentPending = persistInstance.isPartPending(messageID, partID, response._archiveIndicator, 999);
         if (response._areaToEnlarge == null && response._docID._partIndex != 1005 && response._docID._partIndex != 1006) {
            persistInstance.setAttachmentTypeSubtype(messageID, partID, response._archiveIndicator, response._docID._docType, response._docID._docSubtype);
         }

         int serverAudioCodec = -1;
         if (type == 5) {
            serverAudioCodec = AttachmentViewerFactory.getAttachmentServerCodecValue();
         }

         boolean trySaveData = false;
         if ((response._areaToEnlarge == null || response._docID._partIndex == 1003)
            && (response._docID._partIndex != 1001 || response._errorCode == 0)
            && (!response._ftr || type != 5 || serverAudioCodec == -1)
            && (
               !response._ftr
                  || response._imgInfoPresent
                  || response._docID._partIndex == -1
                  || response._docID._partIndex == 1000
                  || !isTypeReqAll && (type != 5 || serverAudioCodec == -1)
                  || response._docID._totalBlocks == 1
                  || response._docID._partIndex == 1005
            )) {
            dataId._partIndex = response._docID._partIndex != 1001 ? response._docID._partIndex : 999;
            if (dataId._partIndex >= 0 && dataId._partIndex <= 999) {
               int savedTotalBlocks = dataId._totalBlocks;
               byte savedSrcType = dataId._srcType;
               dataId._totalBlocks = 1;
               dataId._srcType = 1;
               if (response._crtBlockIndex == 0 && response._docID._arbDOMID != null) {
                  dataId._domID = AttachmentViewerFactory.constructCustomDomIDString(embeddedDomID, "FirstSArbDomID");
                  byte[] info = writeString(response._docID._arbDOMID);
                  persistInstance.addChunkData(messageID, partID, response._archiveIndicator, 0, (short)0, dataId, null, info, false);
                  Object var45 = null;
               }

               if (response._pageDomIDs != null && response._pageDomIDs.length() > 0) {
                  dataId._domID = AttachmentViewerFactory.constructCustomDomIDString(embeddedDomID, "RPageDomID");
                  byte[] info = writeString(response._pageDomIDs);
                  persistInstance.addChunkData(messageID, partID, response._archiveIndicator, 0, (short)0, dataId, null, info, false);
                  Object var47 = null;
               }

               if (response._xcsChunkSize > 0) {
                  dataId._domID = "XChSize";
                  byte[] info = new byte[4];
                  AttachmentViewerFactory.writeInt(info, 0, response._xcsChunkSize);
                  persistInstance.addChunkData(messageID, partID, response._archiveIndicator, 0, (short)0, dataId, null, info, false);
                  Object var49 = null;
               }

               if (response._docID._arbDOMID != null && (response._docID._arbDOMIDStartBlock != -1 || response._docID._arbDOMIDEndBlock != -1)
                  || response._arbDOMIDStartArray != null && response._crtBlockIndex != -1) {
                  byte[] startEndIndicator = new byte[8];
                  if (response._docID._arbDOMID != null) {
                     dataId._domID = AttachmentViewerFactory.constructCustomDomIDStringEx(embeddedDomID, response._docID._arbDOMID, "DomIDLimits");
                     if (writeStartEndChunkInfo(
                        startEndIndicator,
                        persistInstance.getUCSData(messageID, partID, response._archiveIndicator, dataId._domID, 0),
                        response._docID._arbDOMIDStartBlock,
                        response._docID._arbDOMIDEndBlock
                     )) {
                        persistInstance.addChunkData(messageID, partID, response._archiveIndicator, 0, (short)0, dataId, null, startEndIndicator, true);
                     }
                  }

                  if (response._arbDOMIDStartArray != null) {
                     int count = response._arbDOMIDStartArray.length;

                     for (int i = 0; i < count; i++) {
                        dataId._domID = AttachmentViewerFactory.constructCustomDomIDStringEx(embeddedDomID, response._arbDOMIDStartArray[i], "DomIDLimits");
                        if (writeStartEndChunkInfo(
                           startEndIndicator,
                           persistInstance.getUCSData(messageID, partID, response._archiveIndicator, dataId._domID, 0),
                           response._crtBlockIndex,
                           i != count - 1 ? response._crtBlockIndex : response._lastEndChunkForDomID
                        )) {
                           persistInstance.addChunkData(messageID, partID, response._archiveIndicator, 0, (short)0, dataId, null, startEndIndicator, true);
                        }
                     }
                  }

                  Object var51 = null;
               }

               dataId._totalBlocks = savedTotalBlocks;
               dataId._srcType = savedSrcType;
            }

            dataId._domID = embeddedDomID;
            short errCode = response._errorCode;
            byte[] recvMoreData = moreData;
            String[] recvArbDOMIDStartArray = response._arbDOMIDStartArray;
            if (type == 5 && serverAudioCodec == -1) {
               errCode = 30000;
               recvMoreData = null;
               recvArbDOMIDStartArray = null;
               dataId._totalBlocks = -1;
            }

            realErrorCode = persistInstance.addChunkData(
               messageID, partID, response._archiveIndicator, response._crtBlockIndex, errCode, dataId, recvArbDOMIDStartArray, recvMoreData, false
            );
            byte[] var43 = null;
            String[] var53 = null;
            trySaveData = true;
         }

         DocViewAtomicID var34 = null;
         DocViewAttachmentPersist.commitChanges();
         int totalNumberOfBlocksRetrieved = 1;
         int totalBlocks = response._docID._totalBlocks;
         if (response._areaToEnlarge == null) {
            totalNumberOfBlocksRetrieved = persistInstance.getAttachmentRetrievedBlockCount(messageID, partID, response._archiveIndicator, embeddedDomID);
            totalBlocks = persistInstance.getAttachmentBlockCount(messageID, partID, response._archiveIndicator, embeddedDomID);
         }

         if (response._docID._totalBlocks == -1) {
            response._docID._totalBlocks = totalBlocks;
         }

         label787:
         try {
            ActiveDisplayedPart[] displayParts = DocViewDisplayScreenInstance.getActivePartInstances();
            if (displayParts != null) {
               for (int i = 0; i < displayParts.length; i++) {
                  if (displayParts[i]._screen != null && displayParts[i].compare(messageID, partID, response._archiveIndicator)) {
                     displayParts[i]._screen.notifyMoreRequestCompleted(messageID, partID, response, moreData, totalNumberOfBlocksRetrieved);
                  }
               }
            }
         } finally {
            break label787;
         }

         if (messageModel != null
            && isFullContentPending
            && embeddedDomID == null
            && response._areaToEnlarge == null
            && response._docID._partIndex != 1005
            && response._docID._partIndex != 1006
            && !ModelViewListenerRegistry.isViewerUp(0, messageModel, null)
            && messageModel.flagsSet(65536)) {
            if (response._errorCode == 0) {
               if (response._crtBlockIndex == 0) {
                  messageModel.changeStatus(0, 1, 0, 0, true, true, false, false, context);
               }
            } else if (response._crtBlockIndex == -1) {
               messageModel.changeStatus(0, 1, 0, 0, true, true, false, false, context);
            }
         }

         if (messageModel != null && this.canHaveFollowUpRequest(response)) {
            if (!isTypeReqAll) {
               if (totalNumberOfBlocksRetrieved == 0 && type == 5 && serverAudioCodec != -1) {
                  DocViewMoreVerb retrieveMoreVerb = new DocViewMoreVerb(
                     null,
                     messageID,
                     this,
                     super._conversionsAvailable[this.getPreferredConversion()],
                     true,
                     (byte)(AttachmentViewerFactory.isMoreAllSupported(messageModel) ? 4 : 3)
                  );
                  ContextObject newContext = ContextObject.castOrCreate(null);
                  ContextObject.put(newContext, 254, messageModel);
                  ContextObject.put(
                     newContext,
                     -7432523643332070209L,
                     new ClientRequest(
                        "AUDIO",
                        response._docID._partIndex,
                        response._docID._domID,
                        null,
                        null,
                        -1,
                        this.getPassword(messageID, partID, response._archiveIndicator),
                        -1,
                        response._docID._srcType,
                        response._archiveIndicator,
                        null,
                        null,
                        false,
                        true,
                        retrieveMoreVerb._docViewMoreType != 2 && retrieveMoreVerb._docViewMoreType != 4 ? -1 : 64000,
                        false,
                        false,
                        null,
                        -1,
                        -1,
                        -1,
                        serverAudioCodec
                     )
                  );
                  retrieveMoreVerb.invoke(newContext);
               }
            } else {
               int nextBlockIndex = 0;
               int reqLastBlockIndex = response._docID._totalBlocks - 1;
               if (trySaveData) {
                  nextBlockIndex = response._crtBlockIndex + 1;
                  if (response._areaToEnlarge == null
                     && response._docID._arbDOMID != null
                     && response._docID._partIndex != 1005
                     && response._docID._arbDOMIDEndBlock >= 0) {
                     reqLastBlockIndex = response._docID._arbDOMIDEndBlock;
                  }
               } else if (response._areaToEnlarge != null) {
                  nextBlockIndex = response._crtBlockIndex + 1;
               }

               if (nextBlockIndex <= reqLastBlockIndex) {
                  ContextObject newContext = ContextObject.castOrCreate(null);
                  ContextObject.put(newContext, 254, messageModel);
                  if (response._docID._partIndex == 1003 && response._areaToEnlarge != null) {
                     int maxWidth = Math.min(response._areaToEnlarge.width, DocViewGUIInternalConstants.SCREEN_WIDTH * 8 / 3);
                     int maxHeight = Math.min(response._areaToEnlarge.height, DocViewGUIInternalConstants.SCREEN_HEIGHT * 8 / 3);
                     response._reqScreenWidth = maxWidth;
                     response._reqScreenHeight = maxHeight;
                     if (maxWidth > DocViewGUIInternalConstants.SCREEN_WIDTH && maxHeight > DocViewGUIInternalConstants.SCREEN_HEIGHT) {
                        if (DocViewGUIInternalConstants.SCREEN_WIDTH >= DocViewGUIInternalConstants.SCREEN_HEIGHT) {
                           if (response._areaToEnlarge.width < response._areaToEnlarge.height) {
                              response._reqScreenWidth = maxHeight;
                              response._reqScreenHeight = maxWidth;
                           }
                        } else if (response._areaToEnlarge.width >= response._areaToEnlarge.height) {
                           response._reqScreenWidth = maxHeight;
                           response._reqScreenHeight = maxWidth;
                        }
                     }
                  }

                  DocViewMoreVerb retrieveMoreVerb = new DocViewMoreVerb(
                     null,
                     messageID,
                     this,
                     super._conversionsAvailable[this.getPreferredConversion()],
                     true,
                     (byte)(AttachmentViewerFactory.isMoreAllSupported(messageModel) ? 4 : 3)
                  );
                  int reqWidth = response._reqScreenWidth;
                  int reqHeight = response._reqScreenHeight;
                  if (reqWidth <= 0) {
                     if (response._docID._partIndex != 1005) {
                        reqWidth = DocViewGUIInternalConstants.SCREEN_WIDTH;
                     } else {
                        reqWidth = AttachmentViewerFactory.getDesiredRenderedWidth(type == 0 && subtype == 3);
                     }
                  }

                  if (reqHeight <= 0) {
                     if (response._docID._partIndex != 1005) {
                        reqHeight = DocViewGUIInternalConstants.SCREEN_HEIGHT;
                     } else {
                        reqHeight = AttachmentViewerFactory.getDesiredRenderedHeight(type == 0 && subtype == 3);
                     }
                  }

                  ContextObject.put(
                     newContext,
                     -7432523643332070209L,
                     new ClientRequest(
                        response._docID._partIndex != 1005 && type == 2 ? "NEXT" : "RENDER",
                        response._docID._partIndex,
                        response._docID._domID,
                        null,
                        response._docID._arbDOMID,
                        nextBlockIndex,
                        this.getPassword(messageID, partID, response._archiveIndicator),
                        -1,
                        response._docID._srcType,
                        response._archiveIndicator,
                        null,
                        null,
                        false,
                        true,
                        retrieveMoreVerb._docViewMoreType != 2 && retrieveMoreVerb._docViewMoreType != 4 ? -1 : 64000,
                        false,
                        type == 2 || response._docID._partIndex == 1005 || response._docID._partIndex == 1002 || response._docID._partIndex == 1003,
                        response._areaToEnlarge,
                        reqWidth,
                        reqHeight,
                        -1,
                        -1
                     )
                  );
                  retrieveMoreVerb.invoke(newContext);
               }
            }
         }

         response._errorCode = realErrorCode;
         if (response._areaToEnlarge == null) {
            ForwardScreen[] fwdScreens = DocViewDisplayScreenInstance.getForwardScreenInstances();
            if (fwdScreens != null) {
               for (int i = 0; i < fwdScreens.length; i++) {
                  if (fwdScreens[i]._messageID == messageID) {
                     fwdScreens[i].notifyMoreRequestCompleted(messageID, partID, response, moreData, totalNumberOfBlocksRetrieved);
                  }
               }
            }
         }
      } finally {
         return;
      }
   }

   private final boolean canHaveFollowUpRequest(ServerResponse response) {
      return response._errorCode == 0
         && response._docID._partIndex != 1001
         && response._docID._partIndex != 1000
         && response._docID._partIndex != -1
         && response._docID._partIndex != 1006;
   }

   private final String autoRequestRenderedDomID(
      ServerResponse response, EmailMessageModel messageModel, byte[] contentType, int messageID, int partID, String embeddedDomID, byte type, byte subtype
   ) {
      if (response._pageDomIDs != null
         && (response._docID._partIndex != 999 || response._docID._partIndex == 999 && response._crtBlockIndex == 0)
         && this.canHaveFollowUpRequest(response)) {
         StringTokenizer tokenizer = new StringTokenizer(response._pageDomIDs, ',');
         int count = tokenizer.countTokens();
         if (count > 0) {
            String pageDomID = tokenizer.nextToken();
            if (response._docID._partIndex != 999 && response._docID._partIndex > 0) {
               for (int i = 0; i < response._docID._partIndex && tokenizer.hasMoreTokens(); i++) {
                  pageDomID = tokenizer.nextToken();
               }
            }

            if (pageDomID != null && !this.isPageRenderingRetrieved(embeddedDomID, pageDomID, messageID, partID, response._archiveIndicator, true)) {
               DocViewMoreVerb retrieveMoreVerb = new DocViewMoreVerb(
                  null, messageID, this, contentType, true, (byte)(AttachmentViewerFactory.isMoreAllSupported(messageModel) ? 4 : 3)
               );
               ContextObject newContext = ContextObject.castOrCreate(null);
               ContextObject.put(newContext, 254, messageModel);
               ContextObject.put(
                  newContext,
                  -7432523643332070209L,
                  new ClientRequest(
                     "RENDER",
                     (retrieveMoreVerb._docViewMoreType == 2 || retrieveMoreVerb._docViewMoreType == 4) && response._docID._partIndex == 999 ? 1006 : 1005,
                     response._docID._domID,
                     null,
                     pageDomID,
                     -1,
                     this.getPassword(messageID, partID, response._archiveIndicator),
                     -1,
                     response._docID._srcType,
                     response._archiveIndicator,
                     null,
                     null,
                     false,
                     true,
                     retrieveMoreVerb._docViewMoreType != 2 && retrieveMoreVerb._docViewMoreType != 4 ? -1 : 64000,
                     false,
                     true,
                     null,
                     AttachmentViewerFactory.getDesiredRenderedWidth(type == 0 && subtype == 3),
                     AttachmentViewerFactory.getDesiredRenderedHeight(type == 0 && subtype == 3),
                     -1,
                     -1
                  )
               );
               retrieveMoreVerb.invoke(newContext);
               newContext = null;
               return pageDomID;
            }
         }
      }

      return null;
   }

   private final String[] getMatchingRenderedDomIDs(String embeddedDomID, String pageDomID, int messageID, int partID, String archiveIndicator) {
      String renderDomID = AttachmentViewerFactory.constructCustomDomIDStringEx(embeddedDomID, pageDomID, "RenderDomID");
      DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();
      if (persistInstance.isObjectChunkAvailable(messageID, partID, archiveIndicator, renderDomID, 0)) {
         return new String[]{renderDomID};
      }

      String[] atoms = new String[0];
      if (embeddedDomID != null) {
         Arrays.add(atoms, embeddedDomID + "/");
      }

      Arrays.add(atoms, "/RenderDomID");
      Arrays.add(atoms, pageDomID + ',');
      String[] matchDomIDs = persistInstance.getMatchingAvailableEmbeddedDomIDs(messageID, partID, archiveIndicator, atoms);
      String[] var10 = null;
      return matchDomIDs;
   }

   private final boolean isPageRenderingRetrieved(
      String embeddedDomID, String pageDomID, int messageID, int partID, String archiveIndicator, boolean justFirstChunk
   ) {
      String[] matchDomIDs = this.getMatchingRenderedDomIDs(embeddedDomID, pageDomID, messageID, partID, archiveIndicator);
      if (matchDomIDs != null) {
         DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();

         for (int i = 0; i < matchDomIDs.length; i++) {
            if (justFirstChunk) {
               if (persistInstance.isObjectChunkAvailable(messageID, partID, archiveIndicator, matchDomIDs[i], 0)) {
                  matchDomIDs = null;
                  return true;
               }
            } else {
               int blockCount = persistInstance.getAttachmentBlockCount(messageID, partID, archiveIndicator, matchDomIDs[i]);
               if (blockCount > 0 && blockCount == persistInstance.getAttachmentRetrievedBlockCount(messageID, partID, archiveIndicator, matchDomIDs[i])) {
                  matchDomIDs = null;
                  return true;
               }
            }
         }

         matchDomIDs = null;
      }

      return false;
   }

   private static final boolean writeStartEndChunkInfo(byte[] indicator, byte[] savedIndicator, int startChunk, int endChunk) {
      if (startChunk == -1 && endChunk == -1) {
         return false;
      }

      int startChunkToWrite = startChunk;
      int endChunkToWrite = endChunk;
      if (savedIndicator != null) {
         int startChunkIdx = AttachmentViewerFactory.readInt(savedIndicator, 0);
         int endChunkIdx = AttachmentViewerFactory.readInt(savedIndicator, 4);
         if (startChunkIdx != -1 && endChunkIdx != -1) {
            return false;
         }

         if (startChunkIdx == startChunk && endChunkIdx == endChunk) {
            return false;
         }

         if (startChunkIdx != -1) {
            startChunkToWrite = startChunkIdx;
         }

         if (endChunkIdx != -1) {
            endChunkToWrite = endChunkIdx;
         }
      }

      AttachmentViewerFactory.writeInt(indicator, 0, startChunkToWrite);
      AttachmentViewerFactory.writeInt(indicator, 4, endChunkToWrite);
      return true;
   }

   private static final byte[] writeString(String str) {
      short len = (short)str.length();
      byte[] info = new byte[2 + len * 2];
      DocViewUtilities.writeShort(info, 0, len, false);

      for (int i = 0; i < len; i++) {
         DocViewUtilities.writeShort(info, i * 2 + 2, (short)str.charAt(i), false);
      }

      return info;
   }

   @Override
   public final void setLengthOnDevice(int lengthOnDevice) {
   }

   @Override
   public final int getLengthOnDevice() {
      return 0;
   }

   @Override
   public final boolean isMoreAvailable() {
      return AttachmentViewerModel.getPreferredConversion(super._conversionsAvailable) >= 0;
   }

   private static final String customProcessResponse(DocViewAtomicID docID, String[] arbDomIDStartList, String embeddedDomID, ServerResponse response) {
      throw new RuntimeException("cod2jar: field: no name");
   }

   private final void requestInitialChunk(EmailMessageModel messageModel, ContextObject context, DocViewMoreVerb moreVerb, String archiveIndicator) {
      int messageID = messageModel.getUID();
      int morePartID = this.getMorePartID();
      DocViewAttachmentPersist.getInstance().addAttachmentPart(messageID, morePartID, archiveIndicator, null, null, 999, (byte)1, (short)0, false);
      boolean bgEmailFailed = false;
      Object obj = ApplicationRegistry.getApplicationRegistry().get(-5725347163685773030L);
      if (!(obj instanceof LatestRequestInfo)) {
         ApplicationRegistry.getApplicationRegistry().replace(-5725347163685773030L, new LatestRequestInfo(messageID, morePartID, null, 999, bgEmailFailed, 0));
      } else {
         LatestRequestInfo request = (LatestRequestInfo)obj;
         request._attachmentMoreID = morePartID;
         request._attachmentPartID = 999;
         request._archiveIndicatorString = null;
         request._messageAlreadyFailed = bgEmailFailed;
         request._applicationID = 0;
         request._messageID = messageID;
      }

      ContextObject.put(
         context,
         -7432523643332070209L,
         new ClientRequest(
            "NEXT",
            999,
            null,
            null,
            null,
            -1,
            null,
            -1,
            (byte)-1,
            archiveIndicator,
            null,
            null,
            false,
            true,
            AttachmentViewerFactory.getReqChunkSize(messageModel, (byte)-1),
            true,
            true,
            null,
            DocViewGUIInternalConstants.SCREEN_WIDTH,
            DocViewGUIInternalConstants.SCREEN_HEIGHT,
            moreVerb._docViewMoreType != 2 && moreVerb._docViewMoreType != 4 ? -1 : 64000,
            -1
         )
      );
      moreVerb.invoke(context);
   }

   DocViewAttachmentViewerModel(Object initialData) {
      super(initialData);

      label57:
      try {
         String name = this.getFilename();
         if (StringUtilities.startsWithIgnoreCase(name, "x-rimdeviceapplication: ", 1701707776)) {
            name = name.substring(24);
            this.setFilename(name);
         }
      } finally {
         break label57;
      }

      if (this.isArchive()) {
         int trueLength = -1;

         try {
            trueLength = Integer.parseInt(CommandHandler.readTagValue(new String(this.getData()), "SIZE"));
            if (trueLength > 0) {
               this.setTrueLength(trueLength);
               return;
            }
         } finally {
            return;
         }
      }
   }

   private final boolean matchContentType(String contentType) {
      try {
         for (int i = 0; i < super._conversionsAvailable.length; i++) {
            String fullType = CMIMEContentType.getFullType(super._conversionsAvailable[i]);
            if (fullType != null && StringUtilities.compareToIgnoreCase(fullType, contentType, 1701707776) == 0) {
               return true;
            }
         }
      } finally {
         return false;
      }

      return false;
   }

   private final Field getImageField(int param1, int param2, String param3, int param4, int param5) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 000: iload 1
      // 001: iload 2
      // 002: aconst_null
      // 003: aload 3
      // 004: bipush 1
      // 005: bipush 0
      // 006: iload 4
      // 008: invokestatic net/rim/device/apps/internal/docview/gui/AttachmentViewerFactory.getDocData (IILjava/lang/String;Ljava/lang/String;ZII)Ljava/lang/Object;
      // 00b: astore 6
      // 00d: aload 6
      // 00f: ifnonnull 015
      // 012: goto 16f
      // 015: new net/rim/device/apps/internal/docview/gui/DocViewParser
      // 018: dup
      // 019: bipush 0
      // 01a: invokespecial net/rim/device/apps/internal/docview/gui/DocViewParser.<init> (Z)V
      // 01d: astore 7
      // 01f: aload 6
      // 021: instanceof [B
      // 024: ifeq 042
      // 027: aload 7
      // 029: aload 6
      // 02b: checkcast [B
      // 02e: bipush 1
      // 02f: bipush 0
      // 030: bipush 0
      // 031: iload 5
      // 033: bipush 1
      // 034: if_icmpne 03b
      // 037: bipush 1
      // 038: goto 03c
      // 03b: bipush 0
      // 03c: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.parseDocument ([BZIZZ)V
      // 03f: goto 0a5
      // 042: aload 6
      // 044: instanceof java/util/Vector
      // 047: ifeq 0a5
      // 04a: bipush 1
      // 04b: istore 8
      // 04d: aload 6
      // 04f: checkcast java/util/Vector
      // 052: astore 9
      // 054: aload 9
      // 056: invokevirtual java/util/Vector.size ()I
      // 059: istore 10
      // 05b: bipush 0
      // 05c: istore 11
      // 05e: iload 8
      // 060: ifeq 0a5
      // 063: iload 11
      // 065: iload 10
      // 067: if_icmpge 0a5
      // 06a: aload 7
      // 06c: aload 9
      // 06e: iload 11
      // 070: invokevirtual java/util/Vector.elementAt (I)Ljava/lang/Object;
      // 073: checkcast [B
      // 076: iload 11
      // 078: ifne 07f
      // 07b: bipush 1
      // 07c: goto 080
      // 07f: bipush 0
      // 080: iload 11
      // 082: bipush 0
      // 083: iload 11
      // 085: iload 5
      // 087: bipush 1
      // 088: isub
      // 089: if_icmpne 090
      // 08c: bipush 1
      // 08d: goto 091
      // 090: bipush 0
      // 091: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.parseDocument ([BZIZZ)V
      // 094: aload 7
      // 096: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getLastParsingStatus ()B
      // 099: ifeq 09f
      // 09c: goto 0a5
      // 09f: iinc 11 1
      // 0a2: goto 05e
      // 0a5: aload 7
      // 0a7: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getLastParsingStatus ()B
      // 0aa: ifeq 0b0
      // 0ad: goto 16f
      // 0b0: aload 7
      // 0b2: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getParsingData ()Lnet/rim/device/apps/internal/docview/gui/DocViewParsingData;
      // 0b5: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.getDocumentType ()B
      // 0b8: bipush 2
      // 0ba: if_icmpeq 0c0
      // 0bd: goto 16f
      // 0c0: aload 7
      // 0c2: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParser.getParsingData ()Lnet/rim/device/apps/internal/docview/gui/DocViewParsingData;
      // 0c5: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewParsingData.getImages ()[Lnet/rim/device/apps/internal/docview/gui/DocViewImageData;
      // 0c8: astore 8
      // 0ca: aload 8
      // 0cc: arraylength
      // 0cd: ifgt 0d3
      // 0d0: goto 16f
      // 0d3: aconst_null
      // 0d4: astore 9
      // 0d6: aload 8
      // 0d8: bipush 0
      // 0d9: aaload
      // 0da: invokevirtual net/rim/device/apps/internal/docview/gui/DocViewImageData.getImageContents ()[B
      // 0dd: astore 10
      // 0df: aload 10
      // 0e1: bipush 0
      // 0e2: aload 10
      // 0e4: arraylength
      // 0e5: invokestatic net/rim/device/api/system/EncodedImage.createEncodedImage ([BII)Lnet/rim/device/api/system/EncodedImage;
      // 0e8: astore 9
      // 0ea: goto 0f4
      // 0ed: astore 11
      // 0ef: goto 0f4
      // 0f2: astore 11
      // 0f4: aload 9
      // 0f6: ifnonnull 0fc
      // 0f9: goto 16f
      // 0fc: aload 9
      // 0fe: invokevirtual net/rim/device/api/system/EncodedImage.getWidth ()I
      // 101: istore 11
      // 103: aload 9
      // 105: invokevirtual net/rim/device/api/system/EncodedImage.getHeight ()I
      // 108: istore 12
      // 10a: getstatic net/rim/device/apps/internal/docview/gui/DocViewGUIInternalConstants.SCREEN_WIDTH I
      // 10d: bipush 2
      // 10f: ishr
      // 110: istore 13
      // 112: getstatic net/rim/device/apps/internal/docview/gui/DocViewGUIInternalConstants.SCREEN_HEIGHT I
      // 115: bipush 2
      // 117: ishr
      // 118: istore 14
      // 11a: bipush 1
      // 11b: istore 15
      // 11d: iload 11
      // 11f: iload 13
      // 121: if_icmpgt 12b
      // 124: iload 12
      // 126: iload 14
      // 128: if_icmple 145
      // 12b: iinc 15 1
      // 12e: aload 9
      // 130: invokevirtual net/rim/device/api/system/EncodedImage.getWidth ()I
      // 133: iload 15
      // 135: idiv
      // 136: istore 11
      // 138: aload 9
      // 13a: invokevirtual net/rim/device/api/system/EncodedImage.getHeight ()I
      // 13d: iload 15
      // 13f: idiv
      // 140: istore 12
      // 142: goto 11d
      // 145: iload 15
      // 147: ifle 162
      // 14a: iload 15
      // 14c: bipush 1
      // 14d: if_icmpeq 162
      // 150: iload 15
      // 152: invokestatic net/rim/device/api/math/Fixed32.toFP (I)I
      // 155: istore 16
      // 157: aload 9
      // 159: iload 16
      // 15b: iload 16
      // 15d: invokevirtual net/rim/device/api/system/EncodedImage.scaleImage32 (II)Lnet/rim/device/api/system/EncodedImage;
      // 160: astore 9
      // 162: new net/rim/device/apps/internal/docview/gui/CustomBitmapField
      // 165: dup
      // 166: aload 9
      // 168: ldc2_w 18014398509481988
      // 16b: invokespecial net/rim/device/apps/internal/docview/gui/CustomBitmapField.<init> (Lnet/rim/device/api/system/EncodedImage;J)V
      // 16e: areturn
      // 16f: aconst_null
      // 170: areturn
      // try (104 -> 110): 111 null
      // try (104 -> 110): 113 null
   }

   private final Field getPreviewField(int messageID, int morePartID) {
      Field retField = null;
      DocViewAttachmentPersist instance = DocViewAttachmentPersist.getInstance();
      byte type = instance.getAttachmentType(messageID, morePartID, null);
      if (type != -1) {
         if (type == 2) {
            String domID = instance.getFirstArbDomID(messageID, morePartID, null, null);
            int startBlockIndex = AttachmentViewerFactory.getStartBlockIndexWithArbDomID(messageID, morePartID, null, null, domID);
            int blockCount = instance.getAttachmentBlockCount(messageID, morePartID, null, null);
            int endBlockIndex = AttachmentViewerFactory.getEndBlockIndexWithArbDomID(messageID, morePartID, null, null, domID, blockCount);
            if (startBlockIndex >= 0 && endBlockIndex >= 0 && endBlockIndex - startBlockIndex <= 2) {
               return this.getImageField(messageID, morePartID, null, endBlockIndex, blockCount);
            }
         } else if (type == 0) {
            byte[] rPageDomIDs = instance.getUCSData(messageID, morePartID, null, AttachmentViewerFactory.constructCustomDomIDString(null, "RPageDomID"), 0);
            if (rPageDomIDs != null) {
               String strRenderDomIDs = DocViewUtilities.readString(rPageDomIDs, 0, false);
               if (strRenderDomIDs != null) {
                  StringTokenizer tokenizer = new StringTokenizer(strRenderDomIDs, ',');
                  int count = tokenizer.countTokens();
                  if (count > 0) {
                     String pageDomID = tokenizer.nextToken();
                     if (pageDomID != null) {
                        String[] matchDomIDs = this.getMatchingRenderedDomIDs(null, pageDomID, messageID, morePartID, null);
                        String renderDomID = null;
                        if (matchDomIDs != null) {
                           for (int i = 0; i < matchDomIDs.length; i++) {
                              if (instance.getAttachmentBlockCount(messageID, morePartID, null, matchDomIDs[i]) > 0) {
                                 renderDomID = matchDomIDs[i];
                                 break;
                              }
                           }

                           matchDomIDs = null;
                        }

                        if (renderDomID != null) {
                           int endBlockIndex = instance.getAttachmentBlockCount(messageID, morePartID, null, renderDomID) - 1;
                           if (endBlockIndex >= 0 && endBlockIndex < 2) {
                              retField = this.getImageField(messageID, morePartID, renderDomID, endBlockIndex, endBlockIndex + 1);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return retField;
   }

   @Override
   public final Field getField(Object context) {
      Field attachmentField = super.getField(context);
      if (!this.isArchive() && attachmentField != null && AttachmentViewerFactory.isAttachmentViewingEnabled()) {
         Object selectedItem = ContextObject.get(context, 246);
         if (!(selectedItem instanceof EmailMessageModel)) {
            selectedItem = ContextObject.get(context, 250);
         }

         if (selectedItem instanceof EmailMessageModel) {
            EmailMessageModel emailModel = (EmailMessageModel)selectedItem;
            int messageID = emailModel.getCMIMEReferenceIdentifier();
            int morePartID = this.getMorePartID();
            DocViewPreviewFieldManager mgr = DocViewPreviewFieldManager.getInstance();
            Object cachedObj = mgr.getObject(messageID, morePartID);
            if (cachedObj != null && cachedObj instanceof Manager) {
               Manager fldMgr = (Manager)cachedObj;

               label81:
               try {
                  synchronized (Application.getApplication().getAppEventLock()) {
                     fldMgr.delete(fldMgr.getField(0));
                     fldMgr.insert(attachmentField, 0);
                     if (fldMgr.getIndex() != -1) {
                        Manager parent = fldMgr.getManager();
                        if (parent != null) {
                           parent.delete(fldMgr);
                        }
                     }
                  }

                  return fldMgr;
               } finally {
                  break label81;
               }
            }

            DocViewAttachmentViewerModel$CustomVerticalFieldManager manager = new DocViewAttachmentViewerModel$CustomVerticalFieldManager();
            manager.add(attachmentField);
            Field previewField = this.getPreviewField(messageID, morePartID);
            if (previewField != null) {
               manager.add(previewField);
               mgr.putObject(messageID, morePartID, manager);
            }

            return manager;
         }
      }

      return attachmentField;
   }
}
