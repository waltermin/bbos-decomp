package net.rim.device.apps.internal.docview.gui;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.vm.Persistable;

final class DocViewAttachmentPersist$DocViewAttachmentPersistData implements Persistable, SyncObject, RestoreProvider {
   private IntHashtable _msgMap = (IntHashtable)(new Object(128));
   private Vector _lruQueue = (Vector)(new Object());
   private static final long MESSAGE_EXPIRED = 86400000L;
   private static final long PENDING_EXPIRED = 3600000L;
   private static final int BACKUP_VERSIONID = 3;

   final AttachmentElementInfo getElementData(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      synchronized (this._msgMap) {
         AttachmentInfo attachInfo = this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator);
         return this.getElement((PartInfo)attachInfo._attachmentHash.get(partID), partID);
      }
   }

   final int getSyncObjectsCount() {
      int syncObjectsCount = 0;
      synchronized (this._msgMap) {
         Enumeration messages = this._msgMap.elements();

         while (messages.hasMoreElements()) {
            IntHashtable msgHashtable = (IntHashtable)messages.nextElement();
            if (msgHashtable != null) {
               Enumeration attachmentsVector = msgHashtable.elements();

               while (attachmentsVector.hasMoreElements()) {
                  syncObjectsCount += ((Vector)attachmentsVector.nextElement()).size();
               }
            }
         }

         return syncObjectsCount;
      }
   }

   final SyncObject[] getSyncObjects() {
      SyncObject[] objArray = new Object[0];
      synchronized (this._msgMap) {
         IntEnumeration messages = this._msgMap.keys();

         while (messages.hasMoreElements()) {
            int msgID = messages.nextElement();
            IntHashtable msgHashtable = (IntHashtable)this._msgMap.get(msgID);
            if (msgHashtable != null) {
               IntEnumeration attachments = msgHashtable.keys();

               while (attachments.hasMoreElements()) {
                  int morePartID = attachments.nextElement();
                  Vector attachVector = (Vector)msgHashtable.get(morePartID);

                  for (int i = 0; i < attachVector.size(); i++) {
                     Arrays.add(
                        objArray,
                        new DocViewAttachmentPersist$DocViewAttachmentPersistData$DocViewSyncObject(
                           this, (AttachmentInfo)attachVector.elementAt(i), msgID, morePartID
                        )
                     );
                  }
               }
            }
         }

         return objArray;
      }
   }

   @Override
   public final int getUID() {
      return 3;
   }

   public final int getCurrentTotalAttachmentSize() {
      int size = 0;
      synchronized (this._msgMap) {
         Enumeration messages = this._msgMap.elements();

         while (messages.hasMoreElements()) {
            IntHashtable msgHashtable = (IntHashtable)messages.nextElement();
            if (msgHashtable != null) {
               Enumeration attachmentsVector = msgHashtable.elements();

               while (attachmentsVector.hasMoreElements()) {
                  Vector attachVector = (Vector)attachmentsVector.nextElement();

                  for (int i = 0; i < attachVector.size(); i++) {
                     size += ((AttachmentInfo)attachVector.elementAt(i)).getAttachmentDataSize();
                  }
               }
            }
         }

         return size;
      }
   }

   final void clear() {
      synchronized (this._msgMap) {
         synchronized (this._lruQueue) {
            this._lruQueue.removeAllElements();
            this._msgMap.clear();
         }
      }

      this.dataChanged(null);
   }

   public final void resetAttachmentTimestamps(int messageId) {
      synchronized (this._msgMap) {
         synchronized (this._lruQueue) {
            IntHashtable msgHashtable = (IntHashtable)this._msgMap.get(messageId);
            if (msgHashtable != null) {
               Enumeration attachments = msgHashtable.elements();

               while (attachments.hasMoreElements()) {
                  Vector attachVector = (Vector)attachments.nextElement();

                  for (int i = 0; i < attachVector.size(); i++) {
                     AttachmentInfo ai = (AttachmentInfo)attachVector.elementAt(i);
                     Enumeration parts = ai._attachmentHash.elements();
                     long now = System.currentTimeMillis();

                     while (parts.hasMoreElements()) {
                        PartInfo part = (PartInfo)parts.nextElement();
                        part._accessTimestamp = now;
                     }
                  }
               }
            }
         }
      }
   }

   public final boolean freeStaleObject(int priority, IntVector msgIDVector, IntVector msgEmbeddedStartChunksAndLinksVector) {
      boolean retval = false;
      boolean continuePurge = true;
      boolean checkIntegrity = false;
      msgIDVector.removeAllElements();
      msgEmbeddedStartChunksAndLinksVector.removeAllElements();
      boolean tryRefreshFwdScreen = false;
      synchronized (this._msgMap) {
         synchronized (this._lruQueue) {
            for (int i = this._lruQueue.size() - 1; i >= 0 && continuePurge; i--) {
               PartInfo part = (PartInfo)this._lruQueue.elementAt(i);
               if (canDeleteFromMessage(part._messageId)) {
                  if (priority != 1 && priority != 2 && System.currentTimeMillis() - part._accessTimestamp < 86400000) {
                     continuePurge = false;
                  } else {
                     try {
                        AttachmentInfo attachInfo = this.findAttachmentInfo(part._messageId, part._attachmentIndex, part._archiveIndicator);
                        int resetValue = attachInfo.resetData();
                        if (resetValue != -1) {
                           if (resetValue == -4 && !msgIDVector.contains(part._messageId)) {
                              msgIDVector.addElement(part._messageId);
                           }

                           if (!msgEmbeddedStartChunksAndLinksVector.contains(part._messageId)) {
                              msgEmbeddedStartChunksAndLinksVector.addElement(part._messageId);
                           }

                           retval = true;
                           checkIntegrity = true;
                           continuePurge = false;
                        }
                     } finally {
                        continue;
                     }
                  }
               }
            }

            if (!retval && priority != 0) {
               continuePurge = true;
               IntEnumeration messagesIds = this._msgMap.keys();

               while (continuePurge && messagesIds.hasMoreElements()) {
                  int msgId = messagesIds.nextElement();
                  if (canDeleteFromMessage(msgId)) {
                     IntHashtable msgHashtable = (IntHashtable)this._msgMap.get(msgId);
                     if (msgHashtable != null) {
                        IntEnumeration attachmentsIds = msgHashtable.keys();

                        while (continuePurge && attachmentsIds.hasMoreElements()) {
                           int attachId = attachmentsIds.nextElement();
                           Vector attachVector = (Vector)msgHashtable.get(attachId);

                           for (int i = 0; i < attachVector.size(); i++) {
                              AttachmentInfo attachInfo = (AttachmentInfo)attachVector.elementAt(i);
                              int resetValue = attachInfo.resetData();
                              if (resetValue != -1) {
                                 if (resetValue == -4 && !msgIDVector.contains(msgId)) {
                                    msgIDVector.addElement(msgId);
                                 }

                                 if (!msgEmbeddedStartChunksAndLinksVector.contains(msgId)) {
                                    msgEmbeddedStartChunksAndLinksVector.addElement(msgId);
                                 }

                                 retval = true;
                                 checkIntegrity = true;
                                 continuePurge = false;
                              }

                              if (!continuePurge) {
                                 break;
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (!retval && priority != 0) {
               continuePurge = true;

               for (int i = this._lruQueue.size() - 1; i >= 0 && continuePurge; i--) {
                  PartInfo part = (PartInfo)this._lruQueue.elementAt(i);
                  if (part._state != 1 && canDeleteFromMessage(part._messageId)) {
                     IntHashtable msgHashtable = (IntHashtable)this._msgMap.remove(part._messageId);
                     if (msgHashtable != null) {
                        LowMemoryManager.markAsRecoverable(msgHashtable);
                        if (!msgIDVector.contains(part._messageId)) {
                           msgIDVector.addElement(part._messageId);
                        }

                        tryRefreshFwdScreen = true;

                        for (int j = this._lruQueue.size() - 1; j >= 0; j--) {
                           PartInfo newPart = (PartInfo)this._lruQueue.elementAt(j);
                           if (newPart._messageId == part._messageId) {
                              this._lruQueue.removeElement(newPart);
                           }
                        }

                        retval = true;
                        continuePurge = false;
                     }
                  }
               }

               if (!retval) {
                  IntVector messageKeys = (IntVector)(new Object());
                  IntEnumeration messagesIds = this._msgMap.keys();

                  while (messagesIds.hasMoreElements()) {
                     int msgId = messagesIds.nextElement();
                     if (canDeleteFromMessage(msgId)) {
                        messageKeys.addElement(msgId);
                     }
                  }

                  IntEnumeration var43 = null;
                  continuePurge = true;

                  for (int idx = messageKeys.size() - 1; idx >= 0 && continuePurge; idx--) {
                     int msgId = messageKeys.elementAt(idx);
                     IntHashtable msgHashtable = (IntHashtable)this._msgMap.remove(msgId);
                     if (msgHashtable != null) {
                        LowMemoryManager.markAsRecoverable(msgHashtable);
                        if (!msgIDVector.contains(msgId)) {
                           msgIDVector.addElement(msgId);
                        }

                        tryRefreshFwdScreen = true;

                        for (int i = this._lruQueue.size() - 1; i >= 0; i--) {
                           PartInfo part = (PartInfo)this._lruQueue.elementAt(i);
                           if (part._messageId == msgId) {
                              this._lruQueue.removeElement(part);
                           }
                        }

                        retval = true;
                        continuePurge = false;
                     }
                  }

                  messageKeys = null;
               }
            }
         }
      }

      if (checkIntegrity) {
         int msgAffected = msgIDVector.size();
         if (msgAffected > 0) {
            synchronized (this._msgMap) {
               for (int i = 0; i < msgAffected; i++) {
                  IntHashtable msgHashtable = (IntHashtable)this._msgMap.get(msgIDVector.elementAt(i));
                  if (msgHashtable != null) {
                     Enumeration attachmentsVectors = msgHashtable.elements();

                     while (attachmentsVectors.hasMoreElements()) {
                        Vector attachments = (Vector)attachmentsVectors.nextElement();

                        for (int j = 0; j < attachments.size(); j++) {
                           AttachmentInfo ai = (AttachmentInfo)attachments.elementAt(j);
                           Enumeration parts = ai._attachmentHash.elements();

                           while (parts.hasMoreElements()) {
                              PartInfo part = (PartInfo)parts.nextElement();
                              if (part._state == 2 && !ai.containsUCSBlock(part._partId == 1000 ? part._domID : null, part._startBlockIndex)) {
                                 PartInfo.resetPart(part);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      if (tryRefreshFwdScreen) {
         this.dataChanged(msgIDVector);
      }

      return retval;
   }

   final boolean checkPendingState(IntVector msgIDVector) {
      boolean retVal = false;
      synchronized (this._msgMap) {
         IntEnumeration messagesIds = this._msgMap.keys();

         while (messagesIds.hasMoreElements()) {
            int msgId = messagesIds.nextElement();
            IntHashtable msgHashtable = (IntHashtable)this._msgMap.get(msgId);
            if (msgHashtable != null) {
               long now = System.currentTimeMillis();
               Enumeration attachmentsVector = msgHashtable.elements();

               while (attachmentsVector.hasMoreElements()) {
                  Vector attachVector = (Vector)attachmentsVector.nextElement();
                  int size = attachVector.size();

                  for (int i = 0; i < size; i++) {
                     AttachmentInfo ai = (AttachmentInfo)attachVector.elementAt(i);
                     Enumeration parts = ai._attachmentHash.elements();

                     while (parts.hasMoreElements()) {
                        PartInfo part = (PartInfo)parts.nextElement();
                        if (now - part._pendingTimestamp >= 3600000 && part._state == 1) {
                           part._state = 3;
                           part._error = 250;
                           if (!msgIDVector.contains(msgId)) {
                              msgIDVector.addElement(msgId);
                           }

                           retVal = true;
                        }
                     }
                  }
               }
            }
         }

         return retVal;
      }
   }

   final void addArchiveInformation(int messageID, int attachmentIndex, IntHashtable archiveContents) {
      int entriesCount = archiveContents.size();
      if (entriesCount != 0) {
         synchronized (this._msgMap) {
            IntHashtable msgHashtable = (IntHashtable)this._msgMap.get(messageID);
            if (msgHashtable == null) {
               msgHashtable = (IntHashtable)(new Object());
               this._msgMap.put(messageID, msgHashtable);
            } else if (msgHashtable.containsKey(attachmentIndex)) {
               return;
            }

            Vector attachInfoVector = (Vector)(new Object());
            IntEnumeration keys = archiveContents.keys();

            while (keys.hasMoreElements()) {
               int nextKey = keys.nextElement();
               AttachmentInfo ai = new AttachmentInfo();
               ai.setArchiveIndicator(
                  ((StringBuffer)(new Object())).append((String)archiveContents.get(nextKey)).append('|').append(String.valueOf(nextKey)).toString()
               );
               attachInfoVector.addElement(ai);
            }

            msgHashtable.put(attachmentIndex, attachInfoVector);
         }
      }
   }

   final boolean isArchiveInformationAdded(int messageID, int attachmentIndex) {
      synchronized (this._msgMap) {
         return ((IntHashtable)this._msgMap.get(messageID)).containsKey(attachmentIndex);
      }
   }

   final void addAttachmentPart(
      int messageID, int attachmentIndex, String archiveIndicator, String name, String domID, int partID, byte state, short error, boolean dynamicPart
   ) {
      synchronized (this._msgMap) {
         IntHashtable msgHashtable = (IntHashtable)this._msgMap.get(messageID);
         if (msgHashtable == null) {
            msgHashtable = (IntHashtable)(new Object());
            this._msgMap.put(messageID, msgHashtable);
         }

         Vector attachInfoVector = (Vector)msgHashtable.get(attachmentIndex);
         AttachmentInfo attachInfo = null;
         if (attachInfoVector == null) {
            attachInfoVector = (Vector)(new Object());
            msgHashtable.put(attachmentIndex, attachInfoVector);
         } else {
            attachInfo = this.findAttachmentInfoInVector(attachInfoVector, archiveIndicator);
         }

         if (attachInfo == null) {
            attachInfo = new AttachmentInfo();
            attachInfo.setArchiveIndicator(archiveIndicator);
            attachInfoVector.addElement(attachInfo);
         }

         PartInfo newInfo = (PartInfo)attachInfo._attachmentHash.get(partID);
         if (newInfo == null) {
            newInfo = new PartInfo();
            newInfo._archiveIndicator = archiveIndicator;
            if (dynamicPart) {
               newInfo._name = name;
               newInfo._domID = domID;
            } else if (partID == 999) {
               newInfo._chunkHint = 0;
            }

            attachInfo._attachmentHash.put(partID, newInfo);
         }

         newInfo._state = state;
         newInfo._error = error;
         if (state == 1) {
            newInfo._pendingTimestamp = System.currentTimeMillis();
         }

         this.addToLMMQueue(newInfo, messageID, attachmentIndex, partID);
      }
   }

   final boolean isArchive(int messageID, int attachmentIndex) {
      synchronized (this._msgMap) {
         Vector attachmentVector = (Vector)((IntHashtable)this._msgMap.get(messageID)).get(attachmentIndex);
         if (attachmentVector.size() > 1) {
            return true;
         }

         AttachmentInfo aInfo = (AttachmentInfo)attachmentVector.elementAt(0);
         return aInfo.getArchiveIndicator() != null;
      }
   }

   final String getAttachmentNameByIndex(int iMessageID, int attachmentIndex, int archiveIndex) {
      synchronized (this._msgMap) {
         return ((AttachmentInfo)((Vector)((IntHashtable)this._msgMap.get(iMessageID)).get(attachmentIndex)).elementAt(archiveIndex)).getArchiveName();
      }
   }

   final String[] getMatchingAvailableEmbeddedDomIDs(int messageID, int attachmentIndex, String archiveIndicator, String[] atoms) {
      synchronized (this._msgMap) {
         return this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator).getMatchingAvailableEmbeddedDomIDs(atoms);
      }
   }

   final String getArchiveIndicator(int iMessageID, int attachmentIndex, int archiveIndex) {
      synchronized (this._msgMap) {
         return ((AttachmentInfo)((Vector)((IntHashtable)this._msgMap.get(iMessageID)).get(attachmentIndex)).elementAt(archiveIndex)).getArchiveIndicator();
      }
   }

   final String getFirstArbDomID(int messageID, int attachmentIndex, String archiveIndicator, String embeddedDomID) {
      synchronized (this._msgMap) {
         return this.getFirstArbDomID(this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator), embeddedDomID);
      }
   }

   final SimpleSortingVector getAttachmentsPart(int messageID, int attachmentIndex, String archiveIndicator) {
      synchronized (this._msgMap) {
         return this.fillPartsInfo(this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator));
      }
   }

   final Vector getAttachmentsPart(int messageID, int attachmentIndex) {
      Vector mainVector = null;
      synchronized (this._msgMap) {
         Vector bodyVector = (Vector)((IntHashtable)this._msgMap.get(messageID)).get(attachmentIndex);

         for (int i = 0; i < bodyVector.size(); i++) {
            SimpleSortingVector atomVector = this.fillPartsInfo((AttachmentInfo)bodyVector.elementAt(i));
            if (mainVector == null) {
               mainVector = (Vector)(new Object());
            }

            mainVector.addElement(atomVector);
         }

         return mainVector;
      }
   }

   final boolean onMessageDeleted(int messageID) {
      synchronized (this._msgMap) {
         Object msgHashtable = this._msgMap.remove(messageID);
         if (msgHashtable == null) {
            return false;
         }

         msgHashtable = null;
         synchronized (this._lruQueue) {
            for (int i = this._lruQueue.size() - 1; i >= 0; i--) {
               PartInfo part = (PartInfo)this._lruQueue.elementAt(i);
               if (part._messageId == messageID) {
                  this._lruQueue.removeElement(part);
               }
            }
         }

         return true;
      }
   }

   final void onPartAccess(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      PartInfo partInfo = this.findPartInfo(messageID, attachmentIndex, archiveIndicator, partID);
      this.addToLMMQueue(partInfo, messageID, attachmentIndex, partID);
   }

   final boolean onMessageError(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      PartInfo partInfo = this.findPartInfo(messageID, attachmentIndex, archiveIndicator, partID);
      if (partInfo._state == 1) {
         partInfo._state = 3;
         partInfo._error = 253;
         return true;
      } else {
         return false;
      }
   }

   final void setAttachmentTypeSubtype(int messageID, int attachmentIndex, String archiveIndicator, byte type, byte subtype) {
      if (type != -1 || subtype != -1) {
         synchronized (this._msgMap) {
            AttachmentInfo attachInfo = this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator);
            attachInfo.setAttachmentType(type);
            attachInfo.setAttachmentSubtype(subtype);
         }
      }
   }

   final short addChunkData(
      int messageID,
      int attachmentIndex,
      String archiveIndicator,
      int blockIndex,
      short errorTag,
      DocViewAtomicID atomicID,
      String[] arbDOMIDList,
      byte[] ucsData,
      boolean overwrite
   ) {
      AttachmentInfo attachInfo;
      synchronized (this._msgMap) {
         attachInfo = this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator);
         if (atomicID._partIndex == -1 && atomicID._domID == null) {
            return this.addTOCChunkData(messageID, attachmentIndex, archiveIndicator, attachInfo, errorTag, ucsData);
         }
      }

      if (errorTag == 0 && atomicID._domID == null && blockIndex == 0 && attachInfo._attachmentHash.get(999) == null) {
         this.addAttachmentPart(messageID, attachmentIndex, archiveIndicator, null, null, 999, (byte)0, errorTag, false);
      }

      DocViewAttachmentPersist persistInstance = DocViewAttachmentPersist.getInstance();
      if (errorTag == 0) {
         while (persistInstance.getCurrentAttachmentDataSize() > DocViewOptions.getOptions().getMaxCacheSize() && persistInstance.freeStaleObject(1)) {
         }
      }

      synchronized (this._msgMap) {
         attachInfo = this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator);
         if (atomicID._totalBlocks != -1) {
            attachInfo.setTotalBlockCount(atomicID._domID, atomicID._totalBlocks, overwrite);
         }

         if (errorTag == 0) {
            int dataSize = attachInfo.setUCSData(ucsData, atomicID._domID, blockIndex, overwrite);
            persistInstance.changeAttachmentDataSize(dataSize);
         }

         if (atomicID._partIndex >= 0 && atomicID._partIndex <= 999) {
            if (atomicID._domID == null) {
               boolean canMatchAllPartsOnSuccess = attachInfo.getTotalBlockCount(null) == attachInfo.getAttachmentRetrievedBlockCount(null);
               boolean isTypeReqAll = AttachmentViewerFactory.isTypeRequestAllChunks(atomicID._docType);
               IntEnumeration parts = attachInfo._attachmentHash.keys();

               while (parts.hasMoreElements()) {
                  int nextPartID = parts.nextElement();
                  if (nextPartID != 1000) {
                     PartInfo info = (PartInfo)attachInfo._attachmentHash.get(nextPartID);
                     if (info._chunkHint == -1 && blockIndex != -1 && info._domID != null) {
                        if (blockIndex == atomicID._arbDOMIDStartBlock
                           && atomicID._arbDOMID != null
                           && info._domID != null
                           && atomicID._arbDOMID.compareTo(info._domID) == 0) {
                           info._chunkHint = blockIndex;
                        }

                        if (info._domID != null && info._chunkHint == -1 && arbDOMIDList != null) {
                           for (int i = arbDOMIDList.length - 1; i >= 0; i--) {
                              if (info._domID.compareTo(arbDOMIDList[i]) == 0) {
                                 info._chunkHint = blockIndex;
                              }
                           }
                        }
                     }

                     if (info._state != 2) {
                        boolean canMarkSuccess = canMatchAllPartsOnSuccess;
                        if (!canMarkSuccess && errorTag == 0) {
                           if (isTypeReqAll) {
                              canMarkSuccess = this.isArbDomIDFullyRetrieved(
                                 attachInfo, null, info._partId == 999 ? this.getFirstArbDomID(attachInfo, null) : info._domID
                              );
                           } else {
                              canMarkSuccess = info._chunkHint == blockIndex;
                           }
                        }

                        if (!this.modifyPartInfo(info, atomicID._partIndex, errorTag, blockIndex, canMarkSuccess, canMatchAllPartsOnSuccess)) {
                        }
                     }
                  }
               }
            }
         } else if (atomicID._partIndex == 1000) {
            PartInfo info = (PartInfo)attachInfo._attachmentHash.get(1000);
            if (errorTag == 0) {
               info._startBlockIndex = blockIndex;
               info._state = 2;
            } else {
               info._state = 3;
            }

            info._error = errorTag;
         }

         return errorTag;
      }
   }

   final byte[] getUCSData(int messageID, int attachmentIndex, String archiveIndicator, String domID, int blockIndex) {
      synchronized (this._msgMap) {
         return this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator).getUCSData(domID, blockIndex);
      }
   }

   final short getErrorTag(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      return this.findPartInfo(messageID, attachmentIndex, archiveIndicator, partID)._error;
   }

   final int getStartBlockIndex(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      return this.findPartInfo(messageID, attachmentIndex, archiveIndicator, partID)._startBlockIndex;
   }

   final int getChunkHint(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      return this.findPartInfo(messageID, attachmentIndex, archiveIndicator, partID)._chunkHint;
   }

   final byte getPartState(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      return this.findPartInfo(messageID, attachmentIndex, archiveIndicator, partID)._state;
   }

   final int getAttachmentBlockCount(int messageID, int attachmentIndex, String archiveIndicator, String embeddedDomID) {
      synchronized (this._msgMap) {
         return this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator).getTotalBlockCount(embeddedDomID);
      }
   }

   final byte getAttachmentType(int messageID, int attachmentIndex, String archiveIndicator) {
      synchronized (this._msgMap) {
         return this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator).getAttachmentType();
      }
   }

   final byte getAttachmentSubtype(int messageID, int attachmentIndex, String archiveIndicator) {
      synchronized (this._msgMap) {
         return this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator).getAttachmentSubtype();
      }
   }

   final boolean isObjectChunkAvailable(int messageID, int attachmentIndex, String archiveIndicator, String domID, int chunkIndex) {
      synchronized (this._msgMap) {
         return this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator).containsUCSBlock(domID, chunkIndex);
      }
   }

   final int getAttachmentRetrievedBlockCount(int messageID, int attachmentIndex, String archiveIndicator, String domID) {
      synchronized (this._msgMap) {
         return this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator).getAttachmentRetrievedBlockCount(domID);
      }
   }

   @Override
   public final void retrieveData(DataBuffer buffer, int versionID) {
      try {
         synchronized (this._msgMap) {
            while (ConverterUtilities.getType(buffer) == 0) {
               int messageID = ConverterUtilities.readInt(buffer);
               IntHashtable msgHashtable;
               if (this._msgMap.containsKey(messageID)) {
                  msgHashtable = (IntHashtable)this._msgMap.get(messageID);
               } else {
                  msgHashtable = (IntHashtable)(new Object());
                  this._msgMap.put(messageID, msgHashtable);
               }

               try {
                  while (ConverterUtilities.getType(buffer) == 1) {
                     int attachMoreID = ConverterUtilities.readInt(buffer);
                     Vector aInfoVector;
                     if (msgHashtable.containsKey(attachMoreID)) {
                        aInfoVector = (Vector)msgHashtable.get(attachMoreID);
                     } else {
                        aInfoVector = (Vector)(new Object());
                        msgHashtable.put(attachMoreID, aInfoVector);
                     }

                     if (versionID == 1) {
                        AttachmentInfo aInfo = this.retrieveAttachmentData1(buffer, messageID, attachMoreID, versionID);
                        aInfoVector.removeAllElements();
                        aInfoVector.addElement(aInfo);
                     } else {
                        Vector localVector = this.retrieveAttachmentData2(buffer, messageID, attachMoreID, versionID);
                        int size = localVector.size();

                        for (int i = 0; i < size; i++) {
                           AttachmentInfo aInfo = (AttachmentInfo)localVector.elementAt(i);
                           boolean added = false;
                           int existingSize = aInfoVector.size();

                           for (int j = 0; j < existingSize; j++) {
                              AttachmentInfo checkInfo = (AttachmentInfo)aInfoVector.elementAt(j);
                              if (checkInfo.matchArchiveString(aInfo.getArchiveIndicator())) {
                                 aInfoVector.insertElementAt(aInfo, j);
                                 aInfoVector.removeElement(checkInfo);
                                 added = true;
                                 break;
                              }
                           }

                           if (!added) {
                              aInfoVector.addElement(aInfo);
                           }
                        }
                     }
                  }
               } finally {
                  continue;
               }
            }

            throw new Object();
         }
      } finally {
         this.dataChanged(null);
         return;
      }
   }

   private static final boolean canDeleteFromMessage(int messageID) {
      Object obj = MessageLookups.get(-4420850319371185992L, messageID);
      if (obj instanceof Object && ((EmailMessageModel)obj).flagsSet(16)) {
         return false;
      }

      ForwardScreen[] fwdScreenInstances = DocViewDisplayScreenInstance.getForwardScreenInstances();
      if (fwdScreenInstances != null && fwdScreenInstances.length > 0) {
         for (int i = 0; i < fwdScreenInstances.length; i++) {
            if (fwdScreenInstances[i]._messageID == messageID) {
               return false;
            }
         }
      }

      return true;
   }

   private final SimpleSortingVector fillPartsInfo(AttachmentInfo attachInfo) {
      SimpleSortingVector retVector = null;
      IntEnumeration e = attachInfo._attachmentHash.keys();

      while (e.hasMoreElements()) {
         int nextElement = e.nextElement();
         PartInfo part = (PartInfo)attachInfo._attachmentHash.get(nextElement);
         if (part._partId != 1000) {
            if (retVector == null) {
               retVector = (SimpleSortingVector)(new Object());
               retVector.setSortComparator(new DocViewAttachmentPersist$ElementComparator(null));
               retVector.setSort(true);
            }

            retVector.addElement(this.getElement(part, nextElement));
         }
      }

      return retVector;
   }

   private final void addToLMMQueue(PartInfo newInfo, int messageID, int attachmentIndex, int partID) {
      long accessTime = System.currentTimeMillis();
      newInfo.lowMemoryManagement(messageID, attachmentIndex, partID, accessTime);
      synchronized (this._lruQueue) {
         PartInfo duplicateInfo = null;

         for (int i = this._lruQueue.size() - 1; i >= 0; i--) {
            PartInfo info = (PartInfo)this._lruQueue.elementAt(i);
            boolean match = info == duplicateInfo;
            if (!match
               && info._messageId == messageID
               && info._attachmentIndex == attachmentIndex
               && (
                  info._archiveIndicator == null && newInfo._archiveIndicator == null
                     || info._archiveIndicator != null && newInfo._archiveIndicator != null && info._archiveIndicator.compareTo(newInfo._archiveIndicator) == 0
               )) {
               match = true;
            }

            if (match) {
               duplicateInfo = info;
               break;
            }
         }

         if (duplicateInfo != null) {
            this._lruQueue.removeElement(duplicateInfo);
         }

         this._lruQueue.insertElementAt(newInfo, 0);
      }
   }

   private final AttachmentInfo retrieveAttachmentData1(DataBuffer buffer, int messageID, int attachMoreID, int versionID) {
      AttachmentInfo aInfo = new AttachmentInfo();

      label35:
      try {
         aInfo.retrieveData(buffer, versionID);
      } finally {
         break label35;
      }

      Enumeration parts = aInfo._attachmentHash.elements();

      while (parts.hasMoreElements()) {
         PartInfo part = (PartInfo)parts.nextElement();
         part._messageId = messageID;
         part._attachmentIndex = attachMoreID;
         this.addToLMMQueue(part, messageID, attachMoreID, part._partId);
      }

      return aInfo;
   }

   private final Vector retrieveAttachmentData2(DataBuffer buffer, int messageID, int attachMoreID, int versionID) {
      Vector retValue = (Vector)(new Object());

      try {
         while (true) {
            int nextType = ConverterUtilities.getType(buffer, true);
            if (nextType != 2 && nextType != 17 && nextType != 5 && nextType != 15 && nextType != 16) {
               return retValue;
            }

            retValue.addElement(this.retrieveAttachmentData1(buffer, messageID, attachMoreID, versionID));
         }
      } finally {
         return retValue;
      }
   }

   private final boolean modifyPartInfo(
      PartInfo partInfo, int partID, short errorTag, int blockIndex, boolean canMarkSuccess, boolean canMatchAllPartsOnSuccess
   ) {
      boolean matchPart = canMatchAllPartsOnSuccess || canMarkSuccess;
      if (!matchPart && errorTag == 0) {
         matchPart = partInfo._chunkHint == blockIndex || partInfo._partId == 999 && blockIndex == 0;
      }

      if (!matchPart && partInfo._partId == partID) {
         matchPart = true;
      }

      if (matchPart) {
         if (errorTag == 0) {
            if (partInfo._partId != -1) {
               if (partInfo._startBlockIndex == -1 && partInfo._chunkHint == blockIndex) {
                  partInfo._startBlockIndex = partInfo._chunkHint;
               }

               if (canMarkSuccess && partInfo._startBlockIndex >= 0) {
                  partInfo._state = 2;
               }
            }
         } else {
            partInfo._state = 3;
         }

         partInfo._error = errorTag;
      }

      return matchPart;
   }

   private final String getFirstArbDomID(AttachmentInfo attachInfo, String embeddedDomID) {
      byte[] info = attachInfo.getUCSData(AttachmentViewerFactory.constructCustomDomIDString(embeddedDomID, "FirstSArbDomID"), 0);
      return info != null ? DocViewUtilities.readString(info, 0, false) : null;
   }

   private final boolean isArbDomIDFullyRetrieved(AttachmentInfo attachInfo, String embeddedDomID, String arbDomID) {
      if (arbDomID != null) {
         String limits = AttachmentViewerFactory.constructCustomDomIDStringEx(embeddedDomID, arbDomID, "DomIDLimits");
         byte[] limitData = attachInfo.getUCSData(limits, 0);
         int startBlockIndex = AttachmentViewerFactory.readInt(limitData, 0);
         int endBlockIndex = AttachmentViewerFactory.readInt(limitData, 4);
         if (endBlockIndex == -2) {
            endBlockIndex = attachInfo.getTotalBlockCount(embeddedDomID) - 1;
         }

         if (startBlockIndex >= 0 && endBlockIndex >= 0) {
            for (int i = startBlockIndex; i <= endBlockIndex; i++) {
               if (!attachInfo.containsUCSBlock(embeddedDomID, i)) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   private final short addTOCChunkData(int messageID, int attachmentIndex, String archiveIndicator, AttachmentInfo attachInfo, short errorTag, byte[] ucsData) {
      PartInfo partInfo = (PartInfo)attachInfo._attachmentHash.get(-1);
      attachInfo._attachmentHash.remove(-1);
      if (partInfo != null && partInfo._state != 2) {
         short errTag = errorTag;
         if (errTag == 0) {
            errTag = 252;
            DocViewParser coreData = new DocViewParser(false);
            coreData.parseDocument(ucsData, true, 0, false, false);
            if (coreData.getLastParsingStatus() == 0) {
               StringBuffer strSummaryBuf = coreData.getParsingData().getTextContent(true);
               if (strSummaryBuf != null) {
                  String strSummary = strSummaryBuf.toString();
                  int nStringSize = strSummary.length();
                  String[] domIDVector = coreData.getParsingData().getSummaryDOMIDVector();
                  int nDOMCount = domIDVector.length;
                  if (nDOMCount > 0 && nStringSize > 0) {
                     StringTokenizer tokenizer = (StringTokenizer)(new Object(strSummary, '\n'));
                     String[] sectionNameArray = new Object[0];

                     while (tokenizer.hasMoreTokens()) {
                        Arrays.add(sectionNameArray, tokenizer.nextToken());
                     }

                     int size = sectionNameArray.length;
                     if (size == nDOMCount) {
                        IntHashtable attachHash = attachInfo._attachmentHash;
                        IntVector chunkHintVector = coreData.getParsingData().getChunkHintVector();
                        boolean hasChunkHints = chunkHintVector.size() == nDOMCount;

                        for (int i = 0; i < size; i++) {
                           PartInfo newInfo = new PartInfo();
                           newInfo._name = sectionNameArray[i];
                           newInfo._domID = domIDVector[i];
                           newInfo._partId = i;
                           newInfo._archiveIndicator = archiveIndicator;
                           if (hasChunkHints) {
                              newInfo._chunkHint = chunkHintVector.elementAt(i);
                           } else {
                              newInfo._chunkHint = AttachmentViewerFactory.readInt(
                                 attachInfo.getUCSData(AttachmentViewerFactory.constructCustomDomIDStringEx(null, newInfo._domID, "DomIDLimits"), 0), 0
                              );
                           }

                           if (newInfo._chunkHint != -1 && attachInfo.containsUCSBlock(null, newInfo._chunkHint)) {
                              newInfo._startBlockIndex = newInfo._chunkHint;
                              if (attachInfo.getTotalBlockCount(null) == attachInfo.getAttachmentRetrievedBlockCount(null)) {
                                 newInfo._state = 2;
                              } else if (!AttachmentViewerFactory.isTypeRequestAllChunks(attachInfo.getAttachmentType())
                                 || this.isArbDomIDFullyRetrieved(attachInfo, null, newInfo._domID)) {
                                 newInfo._state = 2;
                              }
                           }

                           attachHash.put(i, newInfo);
                        }

                        errTag = 0;
                        partInfo._state = 2;
                     }
                  }
               }
            }

            Object var23 = null;
         }

         if (errTag != 0) {
            attachInfo._attachmentHash.put(-1, partInfo);
            partInfo._state = 3;
            partInfo._error = errTag;
         }

         return errTag;
      } else {
         return errorTag;
      }
   }

   private DocViewAttachmentPersist$DocViewAttachmentPersistData() {
   }

   private final AttachmentElementInfo getElement(PartInfo partInfo, int partID) {
      AttachmentElementInfo elemInfo = new AttachmentElementInfo(partID);
      elemInfo.setElementName(partInfo._name);
      elemInfo.setElementState(partInfo._state);
      elemInfo.setElementDOMId(partInfo._domID);
      elemInfo.setElementArchiveIndicator(partInfo._archiveIndicator);
      elemInfo.setServerResponseTag(partInfo._error);
      return elemInfo;
   }

   private final PartInfo findPartInfo(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      synchronized (this._msgMap) {
         AttachmentInfo attachInfo = this.findAttachmentInfo(messageID, attachmentIndex, archiveIndicator);
         return (PartInfo)attachInfo._attachmentHash.get(partID);
      }
   }

   private final AttachmentInfo findAttachmentInfo(int messageID, int attachmentIndex, String archiveIndicator) {
      Vector bodyVector = (Vector)((IntHashtable)this._msgMap.get(messageID)).get(attachmentIndex);
      if (archiveIndicator == null) {
         if (bodyVector.size() == 1) {
            AttachmentInfo aInfo = (AttachmentInfo)bodyVector.elementAt(0);
            if (aInfo.getArchiveIndicator() == null) {
               return aInfo;
            }
         }

         return null;
      } else {
         return this.findAttachmentInfoInVector(bodyVector, archiveIndicator);
      }
   }

   private final AttachmentInfo findAttachmentInfoInVector(Vector attachInfoVector, String archiveIndicator) {
      for (int i = 0; i < attachInfoVector.size(); i++) {
         AttachmentInfo ai = (AttachmentInfo)attachInfoVector.elementAt(i);
         if (ai.matchArchiveString(archiveIndicator)) {
            return ai;
         }
      }

      return null;
   }

   private final void dataChanged(IntVector msgIDVector) {
      ForwardScreen[] fwdScreenInstances = DocViewDisplayScreenInstance.getForwardScreenInstances();
      ActiveDisplayedPart[] displayParts = DocViewDisplayScreenInstance.getActivePartInstances();
      if (msgIDVector == null) {
         if (fwdScreenInstances != null) {
            for (int i = 0; i < fwdScreenInstances.length; i++) {
               fwdScreenInstances[i].refreshTreeData();
            }
         }

         if (displayParts != null) {
            for (int i = 0; i < displayParts.length; i++) {
               if (displayParts[i]._screen != null) {
                  displayParts[i]._screen.stateChanged();
               }
            }
         }
      } else if (msgIDVector.size() > 0) {
         if (fwdScreenInstances != null) {
            for (int i = 0; i < fwdScreenInstances.length; i++) {
               if (msgIDVector.contains(fwdScreenInstances[i]._messageID)) {
                  fwdScreenInstances[i].refreshTreeData();
               }
            }
         }

         if (displayParts != null) {
            for (int i = 0; i < displayParts.length; i++) {
               if (displayParts[i]._screen != null && msgIDVector.contains(displayParts[i]._screen._messageID)) {
                  displayParts[i]._screen.stateChanged();
               }
            }
         }
      }
   }

   DocViewAttachmentPersist$DocViewAttachmentPersistData(DocViewAttachmentPersist$1 x0) {
      this();
   }
}
