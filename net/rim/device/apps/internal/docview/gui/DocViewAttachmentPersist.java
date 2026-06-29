package net.rim.device.apps.internal.docview.gui;

import java.util.Vector;
import net.rim.device.api.lowmemory.LowMemoryListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.SimpleSortingVector;

final class DocViewAttachmentPersist implements LowMemoryListener {
   private DocViewAttachmentPersist$DocViewAttachmentPersistData _data;
   private PersistentObject _persistentObject;
   private int _attachmentDataSize;
   static final long PERSISTENCE_GUID;
   private static final long ID;
   private static DocViewAttachmentPersist _instance;

   final void changeAttachmentDataSize(int numBytes) {
      this._attachmentDataSize += numBytes;
   }

   final int getCurrentAttachmentDataSize() {
      return this._attachmentDataSize;
   }

   final String getAttachmentNameByIndex(int iMessageID, int attachmentIndex, int archiveIndex) {
      try {
         return this._data.getAttachmentNameByIndex(iMessageID, attachmentIndex, archiveIndex);
      } finally {
         ;
      }
   }

   final String[] getMatchingAvailableEmbeddedDomIDs(int iMessageID, int attachmentIndex, String archiveindicator, String[] atoms) {
      try {
         return this._data.getMatchingAvailableEmbeddedDomIDs(iMessageID, attachmentIndex, archiveindicator, atoms);
      } finally {
         ;
      }
   }

   final String getArchiveIndicator(int iMessageID, int attachmentIndex, int archiveIndex) {
      return this._data.getArchiveIndicator(iMessageID, attachmentIndex, archiveIndex);
   }

   final String getFirstArbDomID(int messageID, int attachmentIndex, String archiveIndicator, String embeddedDomID) {
      try {
         return this._data.getFirstArbDomID(messageID, attachmentIndex, archiveIndicator, embeddedDomID);
      } finally {
         ;
      }
   }

   final SimpleSortingVector getAttachmentPartsByIndex(int iMessageID, int attachmentIndex, String archiveIndicator) {
      try {
         return this._data.getAttachmentsPart(iMessageID, attachmentIndex, archiveIndicator);
      } finally {
         ;
      }
   }

   final Vector getAttachmentPartsByIndex(int iMessageID, int attachmentIndex) {
      try {
         return this._data.getAttachmentsPart(iMessageID, attachmentIndex);
      } finally {
         ;
      }
   }

   final int getSyncObjectsCount() {
      return this._data.getSyncObjectsCount();
   }

   final SyncObject[] getSyncObjects() {
      return this._data.getSyncObjects();
   }

   final SyncObject restoreData(DataBuffer buffer, int uid) {
      if (uid <= this._data.getUID()) {
         try {
            this._data.retrieveData(buffer, uid);
            this._attachmentDataSize = this._data.getCurrentTotalAttachmentSize();
            return this._data;
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   final void clear() {
      this._data.clear();
      this._persistentObject.commit();
      this._attachmentDataSize = 0;
   }

   final void addArchiveInformation(int messageID, int attachmentIndex, IntHashtable archiveContents) {
      this._data.addArchiveInformation(messageID, attachmentIndex, archiveContents);
   }

   final boolean isArchiveInformationAdded(int messageID, int attachmentIndex) {
      try {
         return this._data.isArchiveInformationAdded(messageID, attachmentIndex);
      } finally {
         ;
      }
   }

   final void addAttachmentPart(
      int messageID, int attachmentIndex, String archiveIndicator, String name, String domID, int partID, byte state, short error, boolean dynamicPart
   ) {
      this._data.addAttachmentPart(messageID, attachmentIndex, archiveIndicator, name, domID, partID, state, error, dynamicPart);
   }

   final void setAttachmentTypeSubtype(int messageID, int attachmentIndex, String archiveIndicator, byte type, byte subtype) {
      try {
         this._data.setAttachmentTypeSubtype(messageID, attachmentIndex, archiveIndicator, type, subtype);
      } finally {
         return;
      }
   }

   final void onMessageDeleted(int messageID) {
      if (this._data.onMessageDeleted(messageID)) {
         this._attachmentDataSize = this._data.getCurrentTotalAttachmentSize();
      }
   }

   final void onPartAccess(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      try {
         this._data.onPartAccess(messageID, attachmentIndex, archiveIndicator, partID);
      } finally {
         return;
      }
   }

   final boolean onMessageError(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      try {
         return this._data.onMessageError(messageID, attachmentIndex, archiveIndicator, partID);
      } finally {
         ;
      }
   }

   final boolean isArchive(int messageID, int attachmentIndex) {
      try {
         return this._data.isArchive(messageID, attachmentIndex);
      } finally {
         ;
      }
   }

   final short addChunkData(
      int messageID,
      int attachmentIndex,
      String archiveIndicator,
      int blockIndex,
      short errorTag,
      DocViewAtomicID atomicID,
      String[] domIDList,
      byte[] ucsData,
      boolean overwrite
   ) {
      try {
         return this._data.addChunkData(messageID, attachmentIndex, archiveIndicator, blockIndex, errorTag, atomicID, domIDList, ucsData, overwrite);
      } finally {
         ;
      }
   }

   final void checkPendingState() {
      IntVector msgIDVector = (IntVector)(new Object());
      if (this._data.checkPendingState(msgIDVector)) {
         ForwardScreen[] fwdScreens = DocViewDisplayScreenInstance.getForwardScreenInstances();
         if (fwdScreens != null && fwdScreens.length > 0) {
            for (int i = 0; i < fwdScreens.length; i++) {
               if (msgIDVector.contains(fwdScreens[i]._messageID)) {
                  fwdScreens[i].stateChanged();
               }
            }
         }
      }

      msgIDVector = null;
   }

   final int getAttachmentBlockCount(int messageID, int attachmentIndex, String archiveIndicator, String embeddedDomID) {
      try {
         return this._data.getAttachmentBlockCount(messageID, attachmentIndex, archiveIndicator, embeddedDomID);
      } finally {
         ;
      }
   }

   final byte getAttachmentType(int messageID, int attachmentIndex, String archiveIndicator) {
      try {
         return this._data.getAttachmentType(messageID, attachmentIndex, archiveIndicator);
      } finally {
         ;
      }
   }

   final byte getAttachmentSubtype(int messageID, int attachmentIndex, String archiveIndicator) {
      try {
         return this._data.getAttachmentSubtype(messageID, attachmentIndex, archiveIndicator);
      } finally {
         ;
      }
   }

   final boolean isObjectChunkAvailable(int messageID, int attachmentIndex, String archiveIndicator, String domID, int chunkIndex) {
      try {
         return this._data.isObjectChunkAvailable(messageID, attachmentIndex, archiveIndicator, domID, chunkIndex);
      } finally {
         ;
      }
   }

   final int getAttachmentRetrievedBlockCount(int messageID, int attachmentIndex, String archiveIndicator, String domID) {
      try {
         return this._data.getAttachmentRetrievedBlockCount(messageID, attachmentIndex, archiveIndicator, domID);
      } finally {
         ;
      }
   }

   final byte[] getUCSData(int messageID, int attachmentIndex, String archiveIndicator, String domID, int blockIndex) {
      try {
         return this._data.getUCSData(messageID, attachmentIndex, archiveIndicator, domID, blockIndex);
      } finally {
         ;
      }
   }

   final int getStartBlockIndex(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      try {
         return this._data.getStartBlockIndex(messageID, attachmentIndex, archiveIndicator, partID);
      } finally {
         ;
      }
   }

   final short getErrorTag(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      try {
         return this._data.getErrorTag(messageID, attachmentIndex, archiveIndicator, partID);
      } finally {
         ;
      }
   }

   final int getChunkHint(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      try {
         return this._data.getChunkHint(messageID, attachmentIndex, archiveIndicator, partID);
      } finally {
         ;
      }
   }

   final boolean isPartPending(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      try {
         return this._data.getPartState(messageID, attachmentIndex, archiveIndicator, partID) == 1;
      } finally {
         ;
      }
   }

   final AttachmentElementInfo getElementData(int messageID, int attachmentIndex, String archiveIndicator, int partID) {
      try {
         return this._data.getElementData(messageID, attachmentIndex, archiveIndicator, partID);
      } finally {
         ;
      }
   }

   public final void resetAttachmentTimestamps(int messageId) {
      this._data.resetAttachmentTimestamps(messageId);
   }

   @Override
   public final boolean freeStaleObject(int priority) {
      IntVector msgIDVector = (IntVector)(new Object());
      IntVector msgIDForEmbeddedAndLinksVector = (IntVector)(new Object());
      boolean retValue = this._data.freeStaleObject(priority, msgIDVector, msgIDForEmbeddedAndLinksVector);
      if (retValue) {
         this._attachmentDataSize = this._data.getCurrentTotalAttachmentSize();
         ForwardScreen[] fwdScreens = DocViewDisplayScreenInstance.getForwardScreenInstances();
         if (fwdScreens != null && fwdScreens.length > 0) {
            for (int i = 0; i < fwdScreens.length; i++) {
               if (msgIDVector.contains(fwdScreens[i]._messageID)) {
                  fwdScreens[i].stateChanged();
               }
            }
         }

         ActiveDisplayedPart[] displayParts = DocViewDisplayScreenInstance.getActivePartInstances();
         if (displayParts != null && displayParts.length > 0) {
            for (int i = 0; i < displayParts.length; i++) {
               if (displayParts[i]._screen != null && msgIDForEmbeddedAndLinksVector.contains(displayParts[i]._screen._messageID)) {
                  displayParts[i]._screen.stateChanged();
               }
            }
         }
      }

      msgIDVector = null;
      msgIDForEmbeddedAndLinksVector = null;
      return retValue;
   }

   private DocViewAttachmentPersist() {
      LowMemoryManager.addLowMemoryListener(this);
      this._persistentObject = RIMPersistentStore.getPersistentObject(-907586620148940345L);
      synchronized (this._persistentObject) {
         this._data = (DocViewAttachmentPersist$DocViewAttachmentPersistData)this._persistentObject.getContents();
         if (this._data == null) {
            this._data = new DocViewAttachmentPersist$DocViewAttachmentPersistData(null);
            this._persistentObject.setContents(this._data, 51);
            this._persistentObject.commit();
         }

         this._attachmentDataSize = this._data.getCurrentTotalAttachmentSize();
      }
   }

   private static final synchronized void createSync() {
      if (_instance == null) {
         _instance = new DocViewAttachmentPersist();
      }
   }

   public static final void commitChanges() {
      if (_instance != null) {
         _instance._persistentObject.commit();
      }
   }

   public static final DocViewAttachmentPersist getInstance() {
      if (_instance != null) {
         return _instance;
      }

      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         _instance = (DocViewAttachmentPersist)ar.get(-8178559449158512521L);
         if (_instance == null) {
            createSync();
            ar.put(-8178559449158512521L, _instance);
         }

         return _instance;
      }
   }
}
