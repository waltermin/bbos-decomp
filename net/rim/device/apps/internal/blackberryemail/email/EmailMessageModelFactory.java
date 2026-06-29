package net.rim.device.apps.internal.blackberryemail.email;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.Factory;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModelFactoryCache;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.registration.RIMModelFactoryRepository;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.transmission.rim.CMIMEConverterRegistry;
import net.rim.device.apps.api.transmission.rim.CMIMEParameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.utility.serialization.Converter;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailModifier;
import net.rim.device.apps.internal.blackberryemail.email.api.EmailMoreVerb;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolder;
import net.rim.device.apps.internal.blackberryemail.folder.EmailFolderSync;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

final class EmailMessageModelFactory implements Factory, Recognizer {
   private WeakReference _folderDataBufferWR = new WeakReference(null);
   private WeakReference _addressPairWR = new WeakReference(null);
   private RIMModelFactory[] _factoryCache = RIMModelFactoryCache.allocate();
   private WeakReference _submemberCreationContextWR = new WeakReference(null);
   private EmailPayloadRecognizer _emailPayloadRecognizer = EmailPayloadRecognizer.getInstance();
   private IntHashtable _fixUpList = new IntHashtable();
   private ContextObject _replyWithoutTextStubContext = new ContextObject();

   @Override
   public final boolean recognize(Object o) {
      return o instanceof EmailMessageModel;
   }

   @Override
   public final Object createInstance(Object initialData) {
      ContextObject contextObject = ContextObject.castOrCreate(initialData);
      if (ContextObject.getFlag(contextObject, 43) && ContextObject.getFlag(contextObject, 19)) {
         EmailMessageModelImpl message = null;
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(contextObject, 255);
         if (syncBuffer == null) {
            this.clearFixUpList();
            return null;
         }

         contextObject.clearFlag(94);
         int thisMessageReferenceId = syncBuffer.getUID();
         int fieldType = syncBuffer.getFieldType();
         long folder = 0;
         int originalMessageReferenceId = 0;
         byte encodingId = -1;
         HackyMoreData hackyMoreData = new HackyMoreData();
         if (!syncBuffer.isEmpty() && (fieldType == 0 || fieldType == -16)) {
            int position = syncBuffer.getPosition();
            DataBuffer dataBuffer = syncBuffer.getDataBuffer();
            byte[] dataBufferArray = dataBuffer.getArray();
            int dataBufferPos = dataBuffer.getArrayPosition() + 3;
            message = this.readStatus(dataBufferArray, dataBufferPos, contextObject);
            this.readMoreData(hackyMoreData, message, dataBufferArray, dataBufferPos, contextObject);
            folder = this.readFolderInfo(dataBufferArray, dataBufferPos, contextObject);
            this.readRecipientType(message, dataBufferArray, dataBufferPos);
            if (folder == 0) {
               return null;
            }

            if (message.flagsSet(8)) {
               folder = ((EmailFolder)EmailHierarchy.getEmailHierarchyForFolder(folder).getOrphanedSavedFolder()).getLUID();
            }

            originalMessageReferenceId = this.readOriginalMessageReferenceId(dataBufferArray, dataBufferPos, contextObject);
            encodingId = this.readEncodingId(dataBufferArray, dataBufferPos);
            syncBuffer.setPosition(position);

            try {
               syncBuffer.skipField();
            } finally {
               ;
            }

            boolean bodyRead = false;
            byte isEncoded = 0;
            byte setEncodingId = 0;
            RIMModelFactory[] factories = RIMModelFactoryRepository.getModelFactories(2497613418300956405L);

            while (!syncBuffer.isEmpty()) {
               int positionx = syncBuffer.getPosition();
               fieldType = syncBuffer.getFieldType(false);
               byte var41;
               if (((byte)fieldType & -128) == -128 && (fieldType & 240) != 240) {
                  fieldType &= 127;
                  var41 = 1;
               } else {
                  var41 = 0;
               }

               switch (fieldType) {
                  case 1:
                     this.readAddressField(message, syncBuffer, 0, contextObject, var41);
                     break;
                  case 2:
                     this.readAddressField(message, syncBuffer, 1, contextObject, var41);
                     break;
                  case 3:
                     this.readAddressField(message, syncBuffer, 2, contextObject, var41);
                     break;
                  case 4:
                     this.readAddressField(message, syncBuffer, 4, contextObject, var41);
                     break;
                  case 5:
                     this.readAddressField(message, syncBuffer, 3, contextObject, var41);
                     break;
                  case 6:
                     this.readAddressField(message, syncBuffer, 5, contextObject, var41);
                     break;
                  case 8:
                     this.readGroupAddressField(message, syncBuffer, 0, contextObject, var41);
                     continue;
                  case 9:
                     this.readGroupAddressField(message, syncBuffer, 1, contextObject, var41);
                     continue;
                  case 10:
                     this.readGroupAddressField(message, syncBuffer, 2, contextObject, var41);
                     continue;
                  case 11:
                     this.readSubjectField(message, syncBuffer, contextObject);
                     break;
                  case 12:
                     this.readBody(hackyMoreData, message, syncBuffer, contextObject);
                     bodyRead = true;
                     break;
                  case 18:
                     if (originalMessageReferenceId != 0) {
                        this.addOriginalMessageLink(message, thisMessageReferenceId, originalMessageReferenceId, true, contextObject);
                        originalMessageReferenceId = 0;
                     }
                     break;
                  case 19:
                     this.readErrorMessage(message, syncBuffer, contextObject);
                     break;
                  case 22:
                     if (this.readAttachment(hackyMoreData, message, syncBuffer, false, contextObject, var41)) {
                        message.setAttachmentCount(message.getAttachmentCount() + 1);
                     }
                     break;
                  case 24:
                     this.readAttachment(hackyMoreData, message, syncBuffer, true, contextObject, var41);
                     if (hackyMoreData._bodyContentLengthOnDevice >= 0) {
                        bodyRead = true;
                     }
                     break;
                  case 30:
                     this.readSpecialMessageType(message, syncBuffer, contextObject);
                     break;
                  case 59:
                     this.readTransportError(message, syncBuffer, contextObject);
                     break;
                  case 75:
                     try {
                        thisMessageReferenceId = syncBuffer.getInt();
                        break;
                     } finally {
                        break;
                     }
                  case 100:
                     this.readContentLengthOnDevice(hackyMoreData, message, syncBuffer, contextObject);
                     break;
                  default:
                     Object tmpModel = RIMModelFactoryCache.checkAndCreateSubModel(this._factoryCache, factories, null, syncBuffer, contextObject);
                     if (tmpModel != null) {
                        message.add(tmpModel);
                     }

                     var41 = 0;
               }

               syncBuffer.setPosition(positionx);

               try {
                  syncBuffer.skipField();
               } finally {
                  ;
               }

               setEncodingId |= var41;
            }

            if (originalMessageReferenceId != 0) {
               this.addOriginalMessageLink(message, thisMessageReferenceId, originalMessageReferenceId, false, contextObject);
            }

            if (setEncodingId != 0) {
               message.setEncoding(encodingId);
            }

            if (contextObject.getFlag(94)) {
               message.setFlags(8192);
            }

            message.setFolderId(folder);
            message.setCMIMEReferenceIdentifier(thisMessageReferenceId);
            if (!bodyRead && hackyMoreData._bodyContentLength > 0) {
               PersistableRIMModel emptyBody = (PersistableRIMModel)FactoryUtil.createInstance(5987399499453925075L, null);
               message.add(emptyBody);
               if (emptyBody instanceof MorePartModel) {
                  MorePartModel morePartModel = (MorePartModel)emptyBody;
                  hackyMoreData.populateToMorePartModel(morePartModel);
               }
            }

            EmailModifier.endChanges(message, null, contextObject);
            if (!contextObject.getFlag(133)) {
               PersistentObject.forceCommit(message);
            }

            int[] waitingMessages = (int[])this._fixUpList.get(thisMessageReferenceId);
            if (waitingMessages != null) {
               int numMessages = waitingMessages.length;

               for (int i = 0; i < numMessages; i++) {
                  EmailMessageModelImpl messageToFixup = (EmailMessageModelImpl)MessageLookups.get(-4420850319371185992L, waitingMessages[i]);
                  if (messageToFixup != null) {
                     if (ObjectGroup.isInGroup(messageToFixup._payload)) {
                        messageToFixup._payload = (EmailPayloadModelImpl)ObjectGroup.expandGroup(messageToFixup._payload);
                     }

                     int numSubmembers = messageToFixup._payload.size();

                     for (int j = 0; j < numSubmembers; j++) {
                        if (this._emailPayloadRecognizer.recognize(messageToFixup._payload.getAt(j))) {
                           messageToFixup._payload.removeAt(j);
                           messageToFixup._payload.insertAt(message._payload, j);
                           break;
                        }
                     }

                     EmailModifier.endChanges(messageToFixup, null, contextObject);
                     PersistentObject.forceCommit(messageToFixup);
                  }
               }

               this._fixUpList.remove(thisMessageReferenceId);
            }

            return message;
         } else {
            return null;
         }
      } else {
         return new EmailMessageModelImpl(contextObject);
      }
   }

   private final void clearFixUpList() {
      this._fixUpList = new IntHashtable();
   }

   private final EmailMessageModelImpl readStatus(byte[] dataBuffer, int pos, ContextObject contextObject) {
      EmailMessageModelImpl message = null;
      int flagsToSet = 0;
      if ((dataBuffer[2 + pos] & 1) == 0) {
         contextObject.setFlag(38);
      } else {
         contextObject.clearFlag(38);
      }

      message = new EmailMessageModelImpl(contextObject);
      if (message.inbound()) {
         if ((dataBuffer[3 + pos] & 8) == 0) {
            flagsToSet |= 1;
         }
      } else {
         byte msgType;
         if ((dataBuffer[2 + pos] & 4) != 0) {
            msgType = 1;
         } else if ((dataBuffer[2 + pos] & 8) != 0) {
            msgType = 16;
         } else {
            msgType = 32;
         }

         message.setType(msgType);
      }

      if ((dataBuffer[2 + pos] & 2) == 0) {
         flagsToSet |= 16;
      }

      if ((dataBuffer[2 + pos] & 16) != 0) {
         flagsToSet |= 32;
      }

      if ((dataBuffer[2 + pos] & 128) == 0) {
         flagsToSet |= 8;
      }

      if ((dataBuffer[3 + pos] & 16) != 0) {
         flagsToSet |= 4;
      }

      if ((dataBuffer[3 + pos] & 32) != 0) {
         flagsToSet |= 16777216;
      }

      if ((dataBuffer[3 + pos] & 64) != 0) {
         flagsToSet |= 2;
      }

      message.setNotificationLevel(this.readMessageHeaderBitFieldThatSpansByteBoundaries(dataBuffer, 3, 3 + pos, 7));
      message._payload.setCopyInsteadOfReference((dataBuffer[4 + pos] & 8) != 0);
      if ((dataBuffer[4 + pos] & 64) != 0) {
         flagsToSet |= 33554432;
      }

      if ((dataBuffer[40 + pos] & 2) != 0) {
         flagsToSet |= 2097152;
      }

      if ((dataBuffer[40 + pos] & 4) != 0) {
         flagsToSet |= 64;
      }

      if ((dataBuffer[40 + pos] & 16) != 0) {
         flagsToSet |= 4194304;
      }

      if ((dataBuffer[40 + pos] & 8) != 0) {
         message.setPriority((byte)2);
      }

      if ((dataBuffer[40 + pos] & 32) != 0) {
         message.setPriority((byte)3);
      }

      message.setSensitivity(this.readMessageHeaderBitFieldThatSpansByteBoundaries(dataBuffer, 3, 40 + pos, 6));
      if ((dataBuffer[100 + pos] & 4) != 0) {
         flagsToSet |= 16384;
      }

      if ((dataBuffer[100 + pos] & 8) != 0) {
         flagsToSet |= 32768;
      }

      if ((dataBuffer[100 + pos] & 16) != 0) {
         flagsToSet |= 131072;
      }

      if ((dataBuffer[100 + pos] & 32) != 0) {
         flagsToSet |= 1048576;
      }

      this.readTimestamps(message, dataBuffer, pos, contextObject);
      int status = this.readMessageHeaderInt(dataBuffer, 6 + pos);
      int transmissionError = this.translateTransmissionError(this.readMessageHeaderShort(dataBuffer, 10 + pos));
      if (message.inbound() && status == 0) {
         status = 2047;
      }

      message.changeStatus(flagsToSet, 0, status, transmissionError, false, false, false, false, contextObject);
      return message;
   }

   private final int translateTransmissionError(int errorcode) {
      if ((errorcode & 128) != 0) {
         return errorcode;
      }

      errorcode &= 127;
      switch (errorcode) {
         case 0:
            return 0;
         case 29:
            return 4528;
         case 30:
            return 4544;
         case 31:
            return 4545;
         case 32:
            return 4560;
         case 33:
            return 4576;
         case 34:
            return 4592;
         case 35:
            return 4607;
         default:
            return errorcode >= 0 && errorcode < 64 ? errorcode + 16768 : 128;
      }
   }

   private final void readTimestamps(EmailMessageModelImpl message, byte[] dataBuffer, int pos, ContextObject contextObject) {
      message._payload._creationDate = ConverterUtilities.readNetworkMessageDate(dataBuffer, 28 + pos) / 1000 * 1000;
      long timestamp = ConverterUtilities.readNetworkMessageDate(dataBuffer, 36 + pos) / 1000 * 1000;
      message.setTimestamp(timestamp);
   }

   private final void readMoreData(HackyMoreData hackyMoreData, EmailMessageModelImpl message, byte[] dataBuffer, int pos, ContextObject contextObject) {
      int flags = 0;
      if (0 == (dataBuffer[2 + pos] & 32)) {
         flags |= 256;
      }

      if (0 != (dataBuffer[3 + pos] & 4)) {
         flags |= 4096;
      }

      if (0 != (dataBuffer[4 + pos] & 16)) {
         flags |= 1024;
      }

      message.setFlags(flags);
      hackyMoreData._bodyContentLength = this.readMessageHeaderInt(dataBuffer, 56 + pos);
      hackyMoreData._bodyTrueContentLength = this.readMessageHeaderInt(dataBuffer, 60 + pos);
      message._cursorPosition = this.readMessageHeaderShort(dataBuffer, 78 + pos);
      hackyMoreData._morePartID = this.readMessageHeaderInt(dataBuffer, 52 + pos);
   }

   private final void readRecipientType(EmailMessageModelImpl message, byte[] dataBuffer, int pos) {
      byte recipient = this.readMessageHeaderByte(dataBuffer, 68 + pos);
      message.setRecipientType(recipient);
   }

   private final byte readEncodingId(byte[] dataBuffer, int pos) {
      return this.readMessageHeaderByte(dataBuffer, 103 + pos);
   }

   private final byte readMessageHeaderBitFieldThatSpansByteBoundaries(
      byte[] messageHeader, int numberOfBitsToReadFromSourceByte, int firstSourceByteOffset, int bitIntoFirstSourceByteToStart
   ) {
      int value = (messageHeader[firstSourceByteOffset] & 255) + ((messageHeader[firstSourceByteOffset + 1] & 255) << 8);
      value >>= bitIntoFirstSourceByteToStart;
      value &= (1 << numberOfBitsToReadFromSourceByte) - 1;
      return (byte)value;
   }

   private final byte readMessageHeaderByte(byte[] buffer, int offset) {
      return buffer[offset];
   }

   private final int readMessageHeaderShort(byte[] buffer, int offset) {
      return (buffer[offset] & 0xFF) + ((buffer[offset + 1] & 0xFF) << 8);
   }

   private final int readMessageHeaderInt(byte[] buffer, int offset) {
      return (buffer[offset] & 0xFF) + ((buffer[offset + 1] & 0xFF) << 8) + ((buffer[offset + 2] & 0xFF) << 16) + ((buffer[offset + 3] & 0xFF) << 24);
   }

   private final long readFolderInfo(byte[] dataBuffer, int pos, ContextObject contextObject) {
      DataBuffer folderDataBuffer = WeakReferenceUtilities.getDataBuffer(this._folderDataBufferWR, false);
      folderDataBuffer.setData(dataBuffer, 80 + pos, 12, false);

      try {
         return EmailFolderSync.readFolder(folderDataBuffer);
      } finally {
         return 0;
      }
   }

   private final int readOriginalMessageReferenceId(byte[] dataBuffer, int pos, ContextObject contextObject) {
      return this.readMessageHeaderInt(dataBuffer, 70 + pos);
   }

   private final void addOriginalMessageLink(
      EmailMessageModelImpl message, int thisMessageReferenceId, int originalMessageReferenceId, boolean includeText, ContextObject contextObject
   ) {
      Object originalPayloadModel = null;
      if (includeText) {
         EmailMessageModelImpl originalModel = (EmailMessageModelImpl)MessageLookups.get(-4420850319371185992L, originalMessageReferenceId);
         if (originalModel != null) {
            originalPayloadModel = originalModel._payload;
         }

         if (originalPayloadModel == null) {
            originalPayloadModel = new EmailPayloadModelImpl(null);
            int[] v = (int[])this._fixUpList.get(originalMessageReferenceId);
            if (v == null) {
               v = new int[1];
               this._fixUpList.put(originalMessageReferenceId, v);
            } else {
               Array.resize(v, v.length + 1);
            }

            v[v.length - 1] = thisMessageReferenceId;
         }
      } else {
         this._replyWithoutTextStubContext.putIntegerData(originalMessageReferenceId);
         originalPayloadModel = FactoryUtil.createInstance(7954277133629574293L, this._replyWithoutTextStubContext);
      }

      message.add(originalPayloadModel);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void readAddressField(EmailMessageModelImpl message, SyncBuffer syncBuffer, int headerType, ContextObject contextObject, byte encoded) {
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();
      int fieldLength = 0;
      boolean var33 = false /* VF: Semaphore variable */;

      try {
         var33 = true;
         var38 = dataBuffer.readShort();
         dataBuffer.readByte();
         var33 = false;
      } finally {
         if (var33) {
            return;
         }
      }

      if (var38 != 0) {
         byte[] dataBufferArray = dataBuffer.getArray();
         int pos = dataBuffer.getArrayPosition();
         int addressType = dataBufferArray[pos + 6];
         if (addressType == 10) {
            contextObject.setFlag(94);
         } else if (addressType == 5 || addressType == 4 || addressType == 2 || addressType == 3) {
            contextObject.clearFlag(94);
            message.setFlags(8388608);
         }

         int messageStatus = (dataBufferArray[pos + 3] & 255) << 24
            | (dataBufferArray[pos + 2] & 255) << 16
            | (dataBufferArray[pos + 1] & 255) << 8
            | dataBufferArray[pos] & 255;
         String encoding = null;
         int bytesToSkip = 0;
         int startOfData = pos + 8;
         boolean var29 = false /* VF: Semaphore variable */;

         try {
            var29 = true;
            if (encoded != 0) {
               encoding = CMIMEUtilities.getEncoding(dataBufferArray[startOfData]);
               bytesToSkip = 1;
            }

            if (encoding != null && encoding.length() != 0) {
               var29 = false;
            } else {
               byte var37 = false;
               bytesToSkip = 0;
               encoding = "windows-1252\r";
               var29 = false;
            }
         } finally {
            if (var29) {
               return;
            }
         }

         int endOfName = -1;
         int endPos = var38 + pos;
         startOfData += bytesToSkip;
         int length = endPos - startOfData;
         String wholeAddress = null;
         if (startOfData <= dataBufferArray.length - length && length > 0) {
            boolean var25 = false /* VF: Semaphore variable */;

            label227:
            try {
               var25 = true;
               wholeAddress = new String(dataBufferArray, startOfData, length, encoding);
               var25 = false;
            } finally {
               if (var25) {
                  wholeAddress = new String(dataBufferArray, startOfData, length);
                  break label227;
               }
            }
         }

         if (wholeAddress != null) {
            endOfName = wholeAddress.indexOf(0);
            if (endOfName != -1) {
               String name = null;
               String address = null;
               if (endOfName >= 0) {
                  name = wholeAddress.substring(0, endOfName);
               }

               endPos = wholeAddress.length();
               if (endOfName + 1 <= endPos - 1) {
                  address = wholeAddress.substring(endOfName + 1, endPos - (wholeAddress.charAt(endPos - 1) == 0 ? 1 : 0));
               }

               if (message.flagsSet(8388608)) {
                  StringBuffer addressBuffer = new StringBuffer();
                  switch (addressType) {
                     case 1:
                        break;
                     case 2:
                        addressBuffer.append("PHN:");
                        break;
                     case 3:
                        addressBuffer.append("FAX:");
                        break;
                     case 4:
                        addressBuffer.append("TAP:");
                        break;
                     case 5:
                     default:
                        addressBuffer.append("IAP:");
                  }

                  addressBuffer.append(address);
                  address = addressBuffer.toString();
               }

               String[] addressPair = (String[])this._addressPairWR.get();
               if (addressPair == null) {
                  addressPair = new String[2];
                  this._addressPairWR.set(addressPair);
               }

               addressPair[0] = address;
               addressPair[1] = name;
               EmailBuilder.createEmailHeaderModel(addressPair, headerType, contextObject, message, messageStatus);
            }
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void readGroupAddressField(EmailMessageModelImpl message, SyncBuffer syncBuffer, int headerType, ContextObject contextObject, byte encoded) {
      int fieldPos = syncBuffer.getPosition();
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();
      int fieldLength = 0;
      boolean var37 = false /* VF: Semaphore variable */;

      try {
         var37 = true;
         var43 = dataBuffer.readShort();
         dataBuffer.readByte();
         var37 = false;
      } finally {
         if (var37) {
            return;
         }
      }

      if (var43 != 0) {
         byte[] dataBufferArray = dataBuffer.getArray();
         int pos = dataBuffer.getArrayPosition();
         String encoding = null;
         int bytesToSkip = 0;
         boolean var32 = false /* VF: Semaphore variable */;

         try {
            var32 = true;
            if (encoded != 0) {
               encoding = CMIMEUtilities.getEncoding(dataBufferArray[pos]);
               bytesToSkip = 1;
            }

            if (encoding != null && encoding.length() != 0) {
               var32 = false;
            } else {
               byte var42 = false;
               bytesToSkip = 0;
               encoding = "windows-1252\r";
               var32 = false;
            }
         } finally {
            if (var32) {
               return;
            }
         }

         int endOfName = -1;
         pos += bytesToSkip;
         int length = var43 - bytesToSkip - 8;
         String name = null;
         if (pos <= dataBufferArray.length - length && length > 0) {
            boolean var27 = false /* VF: Semaphore variable */;

            label199:
            try {
               var27 = true;
               name = new String(dataBufferArray, pos, length, encoding);
               var27 = false;
            } finally {
               if (var27) {
                  name = new String(dataBufferArray, pos, length);
                  break label199;
               }
            }
         }

         if (name != null) {
            endOfName = name.indexOf(0);
            if (endOfName != -1) {
               name = name.substring(0, endOfName);
            }

            try {
               dataBuffer.skipBytes(var43 - 8);
               short startOffset = dataBuffer.readShort();
               short endOffset = dataBuffer.readShort();
               int groupUIN = dataBuffer.readInt();
               EmailBuilder.createHeaderForGroupAddress(name, groupUIN, headerType, contextObject, message);
               syncBuffer.setPosition(fieldPos);
               syncBuffer.skipField();

               while (startOffset <= endOffset) {
                  syncBuffer.skipField();
                  startOffset++;
               }
            } finally {
               return;
            }
         }
      }
   }

   private final ContextObject getSubmemberCreationContext() {
      ContextObject context = (ContextObject)this._submemberCreationContextWR.get();
      if (context == null) {
         context = new ContextObject();
         this._submemberCreationContextWR.set(context);
      }

      return context;
   }

   private final void readSubjectField(EmailMessageModelImpl message, SyncBuffer syncBuffer, ContextObject contextObject) {
      String subjectString = null;

      label29:
      try {
         subjectString = ConverterUtilities.readString(syncBuffer.getDataBuffer(), false);
      } finally {
         break label29;
      }

      if (subjectString != null && subjectString.length() != 0) {
         EmailBuilder.createTextObjectForString(subjectString, 3928489455534245796L, this.getSubmemberCreationContext(), message);
      }
   }

   private final void readBody(HackyMoreData hackyMoreData, EmailMessageModelImpl message, SyncBuffer syncBuffer, ContextObject contextObject) {
      try {
         if (hackyMoreData._bodyContentLengthOnDevice == 0) {
            DataBuffer buffer = syncBuffer.getDataBuffer();
            int pos = buffer.getPosition();
            hackyMoreData._bodyContentLengthOnDevice = buffer.readShort();
            buffer.setPosition(pos);
         }

         String bodyString = ConverterUtilities.readString(syncBuffer.getDataBuffer(), false);
         Object body = EmailBuilder.createTextObjectForString(bodyString, 5987399499453925075L, this.getSubmemberCreationContext(), message);
         if (body instanceof MorePartModel) {
            MorePartModel morePartModel = (MorePartModel)body;
            hackyMoreData.populateToMorePartModel(morePartModel);
            return;
         }
      } finally {
         return;
      }
   }

   private final void readContentLengthOnDevice(HackyMoreData hackyMoreData, EmailMessageModelImpl message, SyncBuffer syncBuffer, ContextObject contextObject) {
      try {
         int bodyContentLengthOnDevice = syncBuffer.getInt();
         hackyMoreData._bodyContentLengthOnDevice = bodyContentLengthOnDevice;
         MorePartModel morePartModel = EmailMoreVerb.findBodyMorePartModel(message);
         if (morePartModel != null) {
            morePartModel.setLengthOnDevice(bodyContentLengthOnDevice);
            return;
         }
      } finally {
         return;
      }
   }

   private final void readTransportError(EmailMessageModelImpl message, SyncBuffer syncBuffer, ContextObject contextObject) {
      try {
         message.setStatus(message.getStatus(), syncBuffer.getInt());
      } finally {
         return;
      }
   }

   private final void readErrorMessage(EmailMessageModelImpl message, SyncBuffer syncBuffer, ContextObject contextObject) {
      try {
         String errorMessage = ConverterUtilities.readString(syncBuffer.getDataBuffer(), false);
         message.setTransmissionErrorMessage(errorMessage);
      } finally {
         return;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void readSpecialMessageType(EmailMessageModelImpl message, SyncBuffer syncBuffer, ContextObject contextObject) {
      DataBuffer dataBuffer = syncBuffer.getDataBuffer();
      boolean var9 = false /* VF: Semaphore variable */;

      short fieldLength;
      try {
         var9 = true;
         fieldLength = dataBuffer.readShort();
         dataBuffer.readByte();
         var9 = false;
      } finally {
         if (var9) {
            return;
         }
      }

      if (fieldLength == 1) {
         byte[] dataBufferArray = dataBuffer.getArray();
         int specialMessageType = dataBufferArray[dataBuffer.getArrayPosition()];
         if (specialMessageType == 21) {
            message.setFlags(524288);
            PagingSupport.enablePagingSupport();
            return;
         }

         if (specialMessageType == 127) {
            message.setIsNNE(true);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean readAttachment(
      HackyMoreData hackyMoreData, EmailMessageModelImpl message, SyncBuffer syncBuffer, boolean isHidden, ContextObject contextObject, byte encoded
   ) {
      boolean attachmentAdded = false;

      try {
         DataBuffer dataBuffer = syncBuffer.getDataBuffer();
         int fieldLength = dataBuffer.readShort();
         dataBuffer.readByte();
         int beginningPos = dataBuffer.getArrayPosition();
         dataBuffer.readInt();
         int attachmentLength = dataBuffer.readInt();
         dataBuffer.readByte();
         byte hackyNewAttachmentDataFormatByte = dataBuffer.readByte();
         boolean isEncoded = encoded != 0;
         String filename = this.readNullTerminatedString(dataBuffer, isEncoded);
         String typeString = this.readNullTerminatedString(dataBuffer, false);
         int currentPos = dataBuffer.getArrayPosition();
         int realDataLength = fieldLength - (currentPos - beginningPos);
         if (realDataLength < 0) {
            return false;
         }

         byte[] data;
         if (realDataLength > 0) {
            data = new byte[realDataLength];
            System.arraycopy(dataBuffer.getArray(), currentPos, data, 0, realDataLength);
         } else {
            data = null;
         }

         Converter converter = null;
         CMIMEParameters parameters = null;
         if (hackyNewAttachmentDataFormatByte == 1) {
            int cmimeParameterLength = dataBuffer.readInt();
            DataBuffer cmimeParameterBuffer = new DataBuffer();
            if (cmimeParameterLength > 0) {
               cmimeParameterBuffer.setData(dataBuffer.getArray(), dataBuffer.getArrayPosition(), cmimeParameterLength);
            }

            parameters = new CMIMEParameters(3, 5);
            parameters.read(cmimeParameterBuffer, (byte)0);
            converter = CMIMEConverterRegistry.getDefaultConverter(parameters);

            try {
               data = cmimeParameterBuffer.readByteArray();
            } finally {
               ;
            }
         } else {
            int colonIndex = typeString.indexOf(58);
            String mimeType;
            if (colonIndex != -1) {
               mimeType = typeString.substring(0, colonIndex);
            } else {
               mimeType = typeString;
            }

            converter = CMIMEConverterRegistry.getDefaultConverter(mimeType);
            parameters = new CMIMEParameters(new DataBuffer(), 3, 5);
            if (isEncoded) {
               byte encodingByte = CMIMEUtilities.getEncoding("UTF-8\r");
               byte[] nameBytes = null;
               boolean var57 = false /* VF: Semaphore variable */;

               label440:
               try {
                  var57 = true;
                  nameBytes = filename.getBytes("UTF-8\r");
                  var57 = false;
               } finally {
                  if (var57) {
                     nameBytes = filename.getBytes();
                     isEncoded = false;
                     break label440;
                  }
               }

               if (isEncoded && encodingByte == 0) {
                  parameters.add((byte)-6, encodingByte, nameBytes);
               } else {
                  parameters.add((byte)-14, nameBytes);
               }
            } else {
               boolean var50 = false /* VF: Semaphore variable */;

               label433:
               try {
                  var50 = true;
                  parameters.add((byte)-14, filename.getBytes("windows-1252\r"));
                  var50 = false;
               } finally {
                  if (var50) {
                     parameters.add((byte)-14, filename.getBytes());
                     break label433;
                  }
               }
            }

            parameters.add((byte)1, typeString.getBytes());
         }

         Object attachmentModel = null;
         if (converter != null) {
            label427:
            try {
               attachmentModel = converter.convert(data, parameters);
            } finally {
               break label427;
            }
         }

         if (attachmentModel == null) {
            converter = CMIMEConverterRegistry.getDefaultConverter();

            label421:
            try {
               attachmentModel = converter.convert(data, parameters);
            } finally {
               break label421;
            }
         }

         if (attachmentModel instanceof MorePartModel) {
            MorePartModel morePartModel = (MorePartModel)attachmentModel;
            morePartModel.setLengthOnDevice(attachmentLength);
            if (hackyMoreData._bodyContentLengthOnDevice >= 0 && isHidden) {
               hackyMoreData.populateToMorePartModel(morePartModel);
            }
         }

         if (attachmentModel instanceof PersistableRIMModel) {
            message.add(attachmentModel);
            attachmentAdded = true;
         }
      } finally {
         return attachmentAdded;
      }

      return attachmentAdded;
   }

   private final String readNullTerminatedString(DataBuffer dataBuffer, boolean isEncoded) {
      byte[] array = dataBuffer.getArray();
      int arrayPos = dataBuffer.getArrayPosition();
      int posDelta = arrayPos - dataBuffer.getPosition();
      int i = isEncoded ? arrayPos + 1 : arrayPos;

      while (i < array.length && array[i] != 0) {
         i++;
      }

      if (i != array.length) {
         dataBuffer.setPosition(i - posDelta + 1);
      } else {
         dataBuffer.setPosition(i - posDelta);
      }

      return isEncoded
         ? ConverterUtilities.readStringEncoded(array, arrayPos, i - arrayPos, dataBuffer.isBigEndian())
         : StringUtilities.decodeBOM(array, arrayPos, i - arrayPos, true);
   }
}
