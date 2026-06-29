package net.rim.device.apps.internal.blackberryemail.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionStatusListener;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingLargeAttachmentChunkOutgoingTransmission;

class NativeAttachmentRequestProcessor$Worker implements Runnable, TransmissionStatusListener, EmailEventLoggerEvents {
   private boolean _running;
   private final NativeAttachmentRequestProcessor this$0;

   @Override
   public synchronized void updateTransmissionStatus(TransmissionService service, int tag, int code, Object context) {
      for (int i = 0; i < this.this$0._processedRequests.size(); i++) {
         NativeAttachmentRequest request = (NativeAttachmentRequest)this.this$0._processedRequests.elementAt(i);
         if (request.updateStatus(tag, code)) {
            break;
         }
      }

      this.notifyAll();
   }

   protected void processException(NativeAttachmentRequest request, Exception exception) {
      if (request != null) {
         this.this$0._processedRequests.removeElement(request);
         this.this$0.cancelChunks(request);
         EmailMessageModel message = request.getMessage();
         if (message != null) {
            NativeAttachmentRequestProcessor$Helper.updateMessageStatus(message, 8191);
            String errorMsg = exception.getMessage();
            EventLogger.logEvent(-1237457833540244999L, errorMsg.getBytes(), 2);
            byte errorCode = 1;
            NativeAttachmentRequestProcessor$Helper.transmitMessageMoreCancel(
               this.this$0._transmissionService, message.getCMIMEReferenceIdentifier(), request.getServiceRecord(), errorCode, errorMsg.getBytes()
            );
         }
      }
   }

   public boolean isRunning() {
      return this._running;
   }

   @Override
   public void run() {
      this._running = true;
      RIMMessagingLargeAttachmentChunkOutgoingTransmission transmission = new RIMMessagingLargeAttachmentChunkOutgoingTransmission();
      this.this$0._currentRequest = null;

      while (!this.this$0._requests.isEmpty() || !this.this$0._processedRequests.isEmpty()) {
         while (!this.this$0._requests.isEmpty()) {
            synchronized (this.this$0._requests) {
               if (!this.this$0._requests.isEmpty()) {
                  this.this$0._currentRequest = (NativeAttachmentRequest)this.this$0._requests.elementAt(0);
                  this.this$0._requests.removeElementAt(0);
               }
            }

            if (this.this$0._currentRequest != null) {
               this.this$0._processedRequests.addElement(this.this$0._currentRequest);
               this.perform(this.this$0._currentRequest, transmission);
               this.this$0._currentRequest = null;
            }
         }

         label165:
         try {
            Thread.sleep(1000);
         } finally {
            break label165;
         }

         synchronized (this) {
            for (int i = this.this$0._processedRequests.size() - 1; i >= 0; i--) {
               this.this$0._currentRequest = (NativeAttachmentRequest)this.this$0._processedRequests.elementAt(i);
               if (this.this$0._currentRequest.hasFailed()) {
                  this.this$0.failRequest(this.this$0._currentRequest);
               } else if (this.this$0._currentRequest.isCompleted() || this.this$0._currentRequest.hasExpired()) {
                  this.this$0._processedRequests.removeElementAt(i);
               } else if (this.this$0._currentRequest.shouldResend()) {
                  this.resendChunks(this.this$0._currentRequest, transmission);
               }
            }

            this.this$0._currentRequest = null;
            if (!this.this$0._processedRequests.isEmpty() && this.this$0._requests.isEmpty()) {
               try {
                  this.wait(1800000);
               } finally {
                  continue;
               }
            }
         }
      }

      this._running = false;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private synchronized void resendChunks(NativeAttachmentRequest request, RIMMessagingLargeAttachmentChunkOutgoingTransmission transmission) {
      InputStream inputStream = null;
      boolean var18 = false /* VF: Semaphore variable */;

      label103: {
         try {
            label101:
            try {
               var18 = true;
               int e = this.getAttachmentSizeForRequest(request);
               if (e == -1) {
                  throw new Exception("Could not determine the total attachment size for " + request.getCmimeRefId() + ":" + request.getContentPartId());
               }

               EventLogger.logEvent(-1237457833540244999L, 11, 5);
               inputStream = this.getInputStream(request);
               if (inputStream == null) {
                  throw new IOException("Could not obtain the input stream.");
               }

               IntHashtable chunks = request.getChunks();
               IntEnumeration ex = chunks.keys();

               while (ex.hasMoreElements()) {
                  int key = ex.nextElement();
                  NativeAttachmentRequest$NativeAttachmentChunk chunk = (NativeAttachmentRequest$NativeAttachmentChunk)chunks.get(key);
                  if (chunk.shouldResend()) {
                     byte[] chunkData = new byte[chunk.getLength()];
                     int offset = chunk.getOffset();
                     inputStream.reset();
                     long skipped = inputStream.skip(offset);
                     if (skipped != offset) {
                        throw new IOException("Could not skip to the given offset.");
                     }

                     int bytesRead = inputStream.read(chunkData);
                     if (bytesRead < 0) {
                        byte[] var23 = null;
                        throw new IOException("Could not obtain the input stream.");
                     }

                     this.this$0._transmissionService.cancelTransmitObject(chunk.getID(), null);
                     int chunkID = UIDGenerator.getUID();
                     chunk.setID(chunkID);
                     chunk.clearStatus();
                     chunk.incrementResendCount();
                     this.transmitDataChunk(request, chunkData, bytesRead, chunk.getOffset(), transmission, chunkID);
                  }
               }

               request.setShouldResend(false);
               EventLogger.logEvent(-1237457833540244999L, 12, 5);
               var18 = false;
               break label103;
            } catch (Throwable var21) {
               this.processException(request, e);
               var18 = false;
               break label101;
            }
         } finally {
            if (var18) {
               this.closeInputStream(inputStream);
            }
         }

         this.closeInputStream(inputStream);
         return;
      }

      this.closeInputStream(inputStream);
   }

   private NativeAttachmentRequestProcessor$Worker(NativeAttachmentRequestProcessor _1) {
      this.this$0 = _1;
   }

   private InputStream getInputStream(NativeAttachmentRequest request) {
      EmailMessageModel messageModel = request.getMessage();
      if (messageModel == null) {
         return null;
      }

      LargeAttachmentModel attachmentModel = this.getLargeAttachmentModelFromEmailMessageModelBasedOnContentPartId(messageModel, request.getContentPartId());
      if (attachmentModel == null) {
         return null;
      }

      InputStream inputStream = null;
      if (!(attachmentModel instanceof LargeAttachmentModel$LargeCachedAttachmentModel)) {
         String fileUrl = attachmentModel.getFile();
         FileConnection fc = (FileConnection)Connector.open(fileUrl);
         if (!fc.exists() || !fc.canRead()) {
            return null;
         }

         inputStream = fc.openInputStream();
      } else {
         LargeAttachmentModel$LargeCachedAttachmentModel model = (LargeAttachmentModel$LargeCachedAttachmentModel)attachmentModel;
         inputStream = new ByteArrayInputStream(model.getData());
      }

      return inputStream;
   }

   private int getAttachmentSizeForRequest(NativeAttachmentRequest request) {
      EmailMessageModel messageModel = request.getMessage();
      if (messageModel == null) {
         return -1;
      }

      LargeAttachmentModel attachmentModel = this.getLargeAttachmentModelFromEmailMessageModelBasedOnContentPartId(messageModel, request.getContentPartId());
      return attachmentModel == null ? -1 : (int)attachmentModel.getFileSize();
   }

   private LargeAttachmentModel getLargeAttachmentModelFromEmailMessageModelBasedOnContentPartId(EmailMessageModel emailMessageModel, int contentPartId) {
      if (emailMessageModel == null) {
         return null;
      }

      int length = emailMessageModel.size();
      LargeAttachmentModel model = null;

      for (int i = 0; i < length; i++) {
         Object object = emailMessageModel.getAt(i);
         if (object instanceof LargeAttachmentModel) {
            model = (LargeAttachmentModel)object;
            if (model.getContentPartId() == (short)contentPartId) {
               return model;
            }
         }
      }

      return null;
   }

   private int getChunkSize(ServiceRecord serviceRecord) {
      int chunkSizeFromService = (int)CMIMEUtilities.getNativeAttachmentChunkSizeFromServiceRecord(serviceRecord);
      int threshhold = 51200;
      return Math.min(chunkSizeFromService, threshhold);
   }

   private synchronized void transmitDataChunk(
      NativeAttachmentRequest request,
      byte[] data,
      int dataSize,
      int chunkOffset,
      RIMMessagingLargeAttachmentChunkOutgoingTransmission transmission,
      int transmissionId
   ) {
      byte[] dataToTx = null;
      if (data.length == dataSize) {
         dataToTx = data;
      } else {
         dataToTx = new byte[dataSize];
         System.arraycopy(data, 0, dataToTx, 0, dataSize);
      }

      transmission.init(dataToTx, request.getCmimeRefId(), request.getContentPartId(), chunkOffset, request.getComponentId());
      this.this$0._transmissionContextObject.clear();
      this.this$0._transmissionContextObject.put(-6095803566992128485L, request.getServiceRecord());
      this.this$0._transmissionService.transmitObject(null, transmission, this, transmissionId, this.this$0._transmissionContextObject);
   }

   private void closeInputStream(InputStream inputStream) {
      if (inputStream != null) {
         try {
            inputStream.close();
         } finally {
            return;
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private synchronized void perform(NativeAttachmentRequest request, RIMMessagingLargeAttachmentChunkOutgoingTransmission transmission) {
      InputStream inputStream = null;
      boolean var15 = false /* VF: Semaphore variable */;

      label135: {
         label134: {
            try {
               label132:
               try {
                  var15 = true;
                  int e = this.getAttachmentSizeForRequest(request);
                  if (e == -1) {
                     throw new Exception("Could not determine the total attachment size for " + request.getCmimeRefId() + ":" + request.getContentPartId());
                  }

                  int numberOfBytesToRead = request.getLength();
                  int numberOfBytesAvailableToBeRead = e - request.getOffset();
                  if (numberOfBytesToRead > numberOfBytesAvailableToBeRead) {
                     numberOfBytesToRead = numberOfBytesAvailableToBeRead;
                  }

                  int chunkOffset = request.getOffset();
                  EventLogger.logEvent(-1237457833540244999L, 7, 5);
                  request.setTransmissionId(UIDGenerator.getUID());
                  request.updateTimestamp();
                  inputStream = this.getInputStream(request);
                  if (inputStream == null) {
                     throw new IOException("Could not obtain the input stream.");
                  }

                  int chunkSize = this.getChunkSize(request.getServiceRecord());
                  byte[] chunkData = new byte[numberOfBytesToRead > chunkSize ? chunkSize : numberOfBytesToRead];
                  if (chunkOffset > 0) {
                     inputStream.skip(chunkOffset);
                  }

                  int bytesRead = 0;

                  while (numberOfBytesToRead > 0) {
                     if (this.this$0._cancelCurrentRequest) {
                        this.this$0._cancelCurrentRequest = false;
                        var15 = false;
                        break label135;
                     }

                     bytesRead = inputStream.read(chunkData);
                     if (bytesRead <= 0) {
                        byte[] var20 = null;
                        break;
                     }

                     int chunkID = UIDGenerator.getUID();
                     request.addChunk(chunkOffset, bytesRead, chunkID);
                     this.transmitDataChunk(request, chunkData, bytesRead, chunkOffset, transmission, chunkID);
                     numberOfBytesToRead -= bytesRead;
                     chunkOffset += bytesRead;
                  }

                  EventLogger.logEvent(-1237457833540244999L, 8, 5);
                  var15 = false;
                  break label134;
               } catch (Throwable var18) {
                  this.processException(request, e);
                  var15 = false;
                  break label132;
               }
            } finally {
               if (var15) {
                  this.closeInputStream(inputStream);
               }
            }

            this.closeInputStream(inputStream);
            return;
         }

         this.closeInputStream(inputStream);
         return;
      }

      this.closeInputStream(inputStream);
   }

   NativeAttachmentRequestProcessor$Worker(NativeAttachmentRequestProcessor x0, NativeAttachmentRequestProcessor$1 x1) {
      this(x0);
   }
}
