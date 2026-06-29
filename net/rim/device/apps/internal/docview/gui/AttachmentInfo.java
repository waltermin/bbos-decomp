package net.rim.device.apps.internal.docview.gui;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.internal.compress.CompressUtilities;
import net.rim.vm.Array;
import net.rim.vm.Persistable;

final class AttachmentInfo implements Persistable, BackupProvider, RestoreProvider {
   IntHashtable _attachmentHash = (IntHashtable)(new Object());
   private Hashtable _blockCountHash = (Hashtable)(new Object());
   private Hashtable _attachmentDataChunks = (Hashtable)(new Object());
   private String _archiveIndicator;
   private byte _type = -1;
   private byte _subtype = -1;
   private static final int MAXDATACHUNKS_BACKUP;
   private static final String TYPESUBTYPE_DOMID;
   static final int RESET_ERROR;
   static final int RESET_EMBEDDED_STARTCHUNK;
   static final int RESET_EMBEDDED_NONSTARTCHUNK;
   static final int RESET_MAINDOC;

   final int getAttachmentDataSize() {
      int size = 0;
      Enumeration maps = this._attachmentDataChunks.elements();

      while (maps.hasMoreElements()) {
         Enumeration chunks = ((IntHashtable)maps.nextElement()).elements();

         while (chunks.hasMoreElements()) {
            size += this.getObjectLength(chunks.nextElement());
         }
      }

      return size;
   }

   final int resetData() {
      Enumeration domIds1 = this._attachmentDataChunks.keys();

      while (domIds1.hasMoreElements()) {
         String domID = (String)domIds1.nextElement();
         if (domID.length() > 0 && !this.isRenderedDomID(domID) && this.useDomIDForDeletion(domID)) {
            int embBlockCount = this.getTotalBlockCount(domID);
            if (embBlockCount > 0) {
               IntHashtable chunkMap = this.getChunkMap(domID, false);
               if (chunkMap != null && chunkMap.size() > 0) {
                  for (int i = embBlockCount - 1; i > 0; i--) {
                     if (this.clearData(chunkMap, i)) {
                        return -3;
                     }
                  }
               }
            }
         }
      }

      Enumeration domIds2 = this._attachmentDataChunks.keys();

      while (domIds2.hasMoreElements()) {
         String domID = (String)domIds2.nextElement();
         if (domID.length() > 0 && !this.isRenderedDomID(domID) && this.useDomIDForDeletion(domID)) {
            IntHashtable chunkMap = this.getChunkMap(domID, false);
            if (chunkMap != null && chunkMap.size() > 0 && this.clearData(chunkMap, 0)) {
               return -2;
            }
         }
      }

      int blockCount = this.getTotalBlockCount(null);
      if (blockCount > 0) {
         if (AttachmentViewerFactory.isTypeRequestAllChunks(this._type)) {
            if (this.deleteAllBlocks(null)) {
               return -4;
            }
         } else {
            IntHashtable chunkMap = this.getChunkMap(null, false);
            if (chunkMap != null && chunkMap.size() > 0) {
               for (int i = blockCount - 1; i >= 0; i--) {
                  if (this.clearData(chunkMap, i)) {
                     return -4;
                  }
               }
            }
         }
      }

      Enumeration domIds3 = this._attachmentDataChunks.keys();

      while (domIds3.hasMoreElements()) {
         String domID = (String)domIds3.nextElement();
         if (this.isRenderedDomID(domID) && this.deleteAllBlocks(domID)) {
            return -2;
         }
      }

      return -1;
   }

   final void setAttachmentType(byte type) {
      if (this._type == -1 && type != -1) {
         this._type = type;
      }
   }

   final void setAttachmentSubtype(byte subtype) {
      if (this._subtype == -1 && subtype != -1) {
         this._subtype = subtype;
      }
   }

   final byte getAttachmentType() {
      return this._type;
   }

   final byte getAttachmentSubtype() {
      return this._subtype;
   }

   final void setTotalBlockCount(String domID, int blockCount, boolean overwrite) {
      if (overwrite || this.getTotalBlockCount(domID) <= 0) {
         this._blockCountHash.put(domID == null ? "" : domID, new Object(blockCount));
      }
   }

   final int getTotalBlockCount(String domID) {
      try {
         return this._blockCountHash.get(domID == null ? "" : domID);
      } finally {
         ;
      }
   }

   final boolean matchArchiveString(String archiveString) {
      if (this._archiveIndicator == null) {
         return archiveString == null;
      } else {
         return archiveString == null ? false : this._archiveIndicator.endsWith(((StringBuffer)(new Object())).append('|').append(archiveString).toString());
      }
   }

   final int getAttachmentRetrievedBlockCount(String domID) {
      try {
         return this.getChunkMap(domID, false).size();
      } finally {
         ;
      }
   }

   final String getArchiveName() {
      return this._archiveIndicator.substring(0, this._archiveIndicator.indexOf(124));
   }

   final String[] getMatchingAvailableEmbeddedDomIDs(String[] atoms) {
      String[] matchArray = null;
      Enumeration domIds = this._attachmentDataChunks.keys();

      while (domIds.hasMoreElements()) {
         String domID = (String)domIds.nextElement();
         if (domID.length() > 0) {
            boolean addDomID = true;
            if (atoms != null && atoms.length > 0) {
               for (int i = atoms.length - 1; i >= 0; i--) {
                  String crtAtom = atoms[i];
                  if (crtAtom != null && crtAtom.length() > 0 && domID.indexOf(crtAtom) == -1) {
                     addDomID = false;
                     break;
                  }
               }
            }

            if (addDomID) {
               if (matchArray == null) {
                  matchArray = new Object[0];
               }

               Arrays.add(matchArray, domID);
            }
         }
      }

      return matchArray;
   }

   final void setArchiveIndicator(String archiveIndicator) {
      this._archiveIndicator = archiveIndicator;
   }

   final String getArchiveIndicator() {
      try {
         return this._archiveIndicator.substring(this._archiveIndicator.indexOf("|") + 1);
      } finally {
         ;
      }
   }

   final int setUCSData(byte[] array, String domID, int blockIndex, boolean overwrite) {
      if (array != null && array.length > 0 && blockIndex >= 0) {
         IntHashtable attChunks = this.getChunkMap(domID, true);
         if (overwrite || !attChunks.containsKey(blockIndex)) {
            Object compressedData = compressData(array);
            attChunks.put(blockIndex, compressedData);
            return this.getObjectLength(compressedData);
         }
      }

      return 0;
   }

   final boolean containsUCSBlock(String domID, int blockIndex) {
      try {
         return this.getChunkMap(domID, false).containsKey(blockIndex);
      } finally {
         ;
      }
   }

   final byte[] getUCSData(String domID, int blockIndex) {
      IntHashtable attChunks = this.getChunkMap(domID, false);
      return attChunks != null && attChunks.containsKey(blockIndex) ? decompressData((byte[])attChunks.get(blockIndex)) : null;
   }

   @Override
   public final void retrieveData(DataBuffer buffer, int versionID) {
      if (ConverterUtilities.getType(buffer) == 2) {
         this._blockCountHash.put("", new Object(ConverterUtilities.readInt(buffer)));
      } else if (ConverterUtilities.getType(buffer, true) == 17) {
         while (ConverterUtilities.getType(buffer, true) == 17) {
            String domId = ConverterUtilities.readString(buffer);
            if (ConverterUtilities.getType(buffer) != 2) {
               throw new Object();
            }

            this._blockCountHash.put(domId, new Object(ConverterUtilities.readInt(buffer)));
         }
      }

      if (ConverterUtilities.getType(buffer, true) == 15) {
         this._archiveIndicator = ConverterUtilities.readString(buffer);
      }

      if (ConverterUtilities.getType(buffer, true) == 16) {
         while (ConverterUtilities.getType(buffer, true) == 16) {
            String domID = ConverterUtilities.readString(buffer);
            if ("TypeSubtype".compareTo(domID) == 0) {
               if (ConverterUtilities.getType(buffer) == 3) {
                  int chunkIndex = ConverterUtilities.readInt(buffer);
                  if (ConverterUtilities.getType(buffer) != 4) {
                     throw new Object();
                  }

                  byte[] tsArray = ConverterUtilities.readByteArray(buffer);
                  if (tsArray != null && tsArray.length >= 2) {
                     this._type = tsArray[0];
                     this._subtype = tsArray[1];
                  }
               }
            } else {
               this.readChunkData(buffer, this.getChunkMap(domID, true));
            }
         }
      } else if (ConverterUtilities.getType(buffer) == 3) {
         this.readChunkData(buffer, this.getChunkMap(null, true));
      }

      while (ConverterUtilities.getType(buffer) == 5) {
         int partID = ConverterUtilities.readInt(buffer);
         PartInfo part = new PartInfo();
         part.retrieveData(buffer, versionID);
         part._partId = partID;
         part._archiveIndicator = this.getArchiveIndicator();
         this._attachmentHash.put(partID, part);
      }
   }

   @Override
   public final void serializeData(DataBuffer buffer, int param) {
      Enumeration domIdEnumeration = this._blockCountHash.keys();

      while (domIdEnumeration.hasMoreElements()) {
         String domID = (String)domIdEnumeration.nextElement();
         ConverterUtilities.writeStringSmart(buffer, 17, domID);
         ConverterUtilities.writeInt(buffer, 2, this._blockCountHash.get(domID));
      }

      if (this._archiveIndicator != null) {
         ConverterUtilities.writeStringSmart(buffer, 15, this._archiveIndicator);
      }

      if (this._type != -1 || this._subtype != -1) {
         byte[] tsArray = new byte[]{this._type, this._subtype};
         ConverterUtilities.writeStringSmart(buffer, 16, "TypeSubtype");
         ConverterUtilities.writeInt(buffer, 3, 0);
         ConverterUtilities.writeByteArray(buffer, 4, tsArray);
      }

      int totalAvailableChunks = 100;
      Hashtable chunkIndexHash = (Hashtable)(new Object());
      if (totalAvailableChunks > 0) {
         if (this._attachmentDataChunks.containsKey("")) {
            IntHashtable chunkData = (IntHashtable)this._attachmentDataChunks.get("");
            if (chunkData != null) {
               int chunksNo = Math.min(chunkData.size(), totalAvailableChunks);
               if (chunksNo > 0) {
                  ConverterUtilities.writeStringSmart(buffer, 16, "");
                  IntVector chkIndexVector = (IntVector)(new Object());
                  chunkIndexHash.put("", chkIndexVector);

                  for (IntEnumeration chunkEnumeration = chunkData.keys(); chunkEnumeration.hasMoreElements() && chunksNo > 0; totalAvailableChunks--) {
                     int chunkIndex = chunkEnumeration.nextElement();
                     ConverterUtilities.writeInt(buffer, 3, chunkIndex);
                     chkIndexVector.addElement(chunkIndex);
                     ConverterUtilities.writeByteArray(buffer, 4, (byte[])chunkData.get(chunkIndex));
                     chunksNo--;
                  }
               }
            }
         }

         Enumeration dataEnumeration = this._attachmentDataChunks.keys();

         while (dataEnumeration.hasMoreElements() && totalAvailableChunks > 0) {
            String domID = (String)dataEnumeration.nextElement();
            if (domID.length() > 0) {
               IntHashtable chunkData = (IntHashtable)this._attachmentDataChunks.get(domID);
               if (chunkData != null) {
                  int chunksNo = Math.min(chunkData.size(), totalAvailableChunks);
                  if (chunksNo > 0) {
                     ConverterUtilities.writeStringSmart(buffer, 16, domID);
                     IntVector chkIndexVector = (IntVector)(new Object());
                     chunkIndexHash.put(domID, chkIndexVector);

                     for (IntEnumeration chunkEnumeration = chunkData.keys(); chunkEnumeration.hasMoreElements() && chunksNo > 0; totalAvailableChunks--) {
                        int chunkIndex = chunkEnumeration.nextElement();
                        ConverterUtilities.writeInt(buffer, 3, chunkIndex);
                        chkIndexVector.addElement(chunkIndex);
                        ConverterUtilities.writeByteArray(buffer, 4, (byte[])chunkData.get(chunkIndex));
                        chunksNo--;
                     }
                  }
               }
            }
         }
      }

      IntEnumeration parts = this._attachmentHash.keys();

      while (parts.hasMoreElements()) {
         int partID = parts.nextElement();
         PartInfo part = (PartInfo)this._attachmentHash.get(partID);
         ConverterUtilities.writeInt(buffer, 5, partID);
         boolean partBlockPresentInBackup = true;
         if (part._state == 2) {
            label136:
            try {
               partBlockPresentInBackup = ((IntVector)chunkIndexHash.get(partID == 1000 ? "DocInfoDomID" : "")).contains(part._startBlockIndex);
            } finally {
               break label136;
            }
         }

         part.serializeData(buffer, partBlockPresentInBackup ? 1 : 0);
      }

      chunkIndexHash = null;
   }

   private static final byte[] decompressData(byte[] compressedData) {
      Object decompressBuffer = new byte[0];
      int count = CompressUtilities.decompress(compressedData, 0, compressedData.length, decompressBuffer, 0, false, false);
      if (count > 0) {
         Array.resize(decompressBuffer, count);
      }

      return (byte[])decompressBuffer;
   }

   private static final byte[] compressData(byte[] decompressedData) {
      byte[] compressedData = new byte[0];
      int compressedLength = CompressUtilities.compress(decompressedData, 0, -1, compressedData, 0);
      if (compressedLength > 0) {
         Array.resize(compressedData, compressedLength);
      }

      return compressedData;
   }

   private final void readChunkData(DataBuffer buffer, IntHashtable chunkMap) {
      while (ConverterUtilities.getType(buffer) == 3) {
         int chunkIndex = ConverterUtilities.readInt(buffer);
         if (ConverterUtilities.getType(buffer) != 4) {
            throw new Object();
         }

         chunkMap.put(chunkIndex, ConverterUtilities.readByteArray(buffer));
      }
   }

   private final boolean isRenderedDomID(String domID) {
      return domID.indexOf("RenderDomID") >= 0;
   }

   private final int getObjectLength(Object obj) {
      return !(obj instanceof byte[]) ? 0 : ((byte[])obj).length;
   }

   private final boolean clearData(IntHashtable chunkData, int blockIndex) {
      if (chunkData != null && chunkData.containsKey(blockIndex)) {
         Object data = chunkData.remove(blockIndex);
         if (data != null) {
            LowMemoryManager.markAsRecoverable(data);
            return true;
         }
      }

      return false;
   }

   private final boolean deleteAllBlocks(String domID) {
      boolean retValue = false;
      IntHashtable chunkData = this.getChunkMap(domID, false);
      if (chunkData != null) {
         IntEnumeration chunkIdxEnum = chunkData.keys();

         while (chunkIdxEnum.hasMoreElements()) {
            int chunkIdx = chunkIdxEnum.nextElement();
            boolean cleared = this.clearData(chunkData, chunkIdx);
            if (!retValue && cleared) {
               retValue = true;
            }
         }
      }

      return retValue;
   }

   private final boolean useDomIDForDeletion(String domID) {
      return domID == null
         || domID.indexOf("DomIDLimits") == -1 && domID.indexOf("FirstSArbDomID") == -1 && domID.indexOf("RPageDomID") == -1 && domID.indexOf("XChSize") == -1;
   }

   private final IntHashtable getChunkMap(String domID, boolean createHash) {
      String realDomID = domID == null ? "" : domID;
      IntHashtable hashTbl = (IntHashtable)this._attachmentDataChunks.get(realDomID);
      if (createHash && hashTbl == null) {
         hashTbl = (IntHashtable)(new Object());
         this._attachmentDataChunks.put(realDomID, hashTbl);
      }

      return hashTbl;
   }
}
