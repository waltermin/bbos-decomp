package net.rim.device.apps.internal.blackberryemail.email;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.transmission.TransmissionService;
import net.rim.device.apps.api.transmission.TransmissionServiceManager;

public class NativeAttachmentRequestProcessor {
   private NativeAttachmentRequestProcessor$Worker _worker = new NativeAttachmentRequestProcessor$Worker(this, null);
   private Vector _requests = (Vector)(new Object());
   private Vector _processedRequests = (Vector)(new Object());
   private NativeAttachmentRequest _currentRequest;
   private TransmissionService _transmissionService = TransmissionServiceManager.get(8399767144006445082L);
   private ContextObject _transmissionContextObject = ContextObject.castOrCreate(null);
   private boolean _cancelCurrentRequest;
   private static final long ID = -285957192141787993L;
   private static NativeAttachmentRequestProcessor _self = null;

   public boolean addRequest(NativeAttachmentRequest request) {
      if (request == null) {
         EventLogger.logEvent(-1237457833540244999L, 1, 4);
         return false;
      } else if (!this._requests.contains(request)) {
         this._requests.addElement(request);
         EventLogger.logEvent(-1237457833540244999L, 2, 4);
         NativeAttachmentRequestProcessor$Helper.updateMessageStatus(request.getMessage(), 67108863);
         this.process();
         return true;
      } else {
         return false;
      }
   }

   public boolean cancelRequest(int messageRefId, int contentPartId, byte errorCode, byte[] errorDescription, boolean cancelAttachmentOnly, boolean retry) {
      return this.cancelRequest(messageRefId, contentPartId, errorCode, errorDescription, cancelAttachmentOnly, retry, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean cancelRequest(
      int messageRefId, int contentPartId, byte errorCode, byte[] errorDescription, boolean cancelAttachmentOnly, boolean retry, boolean sendCancelToServer
   ) {
      synchronized (this._worker) {
         boolean var14 = false /* VF: Semaphore variable */;

         boolean var18;
         label138: {
            label139: {
               try {
                  var14 = true;
                  if (this._currentRequest != null) {
                     if (!cancelAttachmentOnly) {
                        this._cancelCurrentRequest = this._currentRequest.getCmimeRefId() == messageRefId;
                     } else {
                        this._cancelCurrentRequest = this._currentRequest.getCmimeRefId() == messageRefId
                           && this._currentRequest.getContentPartId() == contentPartId;
                     }
                  } else {
                     this._cancelCurrentRequest = false;
                  }

                  if (cancelAttachmentOnly && this._cancelCurrentRequest) {
                     var18 = this.cancelCurrentAttachment(retry);
                     var14 = false;
                     break label138;
                  }

                  if (cancelAttachmentOnly) {
                     var18 = this.cancelQueuedAttachment(messageRefId, contentPartId, retry);
                     var14 = false;
                     break label139;
                  }

                  var18 = this.cancelMessage(messageRefId, sendCancelToServer, errorCode, errorDescription);
                  var14 = false;
               } finally {
                  if (var14) {
                     if (this._cancelCurrentRequest) {
                        this._cancelCurrentRequest = false;
                     }

                     this._worker.notifyAll();
                  }
               }

               if (this._cancelCurrentRequest) {
                  this._cancelCurrentRequest = false;
               }

               this._worker.notifyAll();
               return var18;
            }

            if (this._cancelCurrentRequest) {
               this._cancelCurrentRequest = false;
            }

            this._worker.notifyAll();
            return var18;
         }

         if (this._cancelCurrentRequest) {
            this._cancelCurrentRequest = false;
         }

         this._worker.notifyAll();
         return var18;
      }
   }

   private boolean cancelCurrentAttachment(boolean retry) {
      EventLogger.logEvent(-1237457833540244999L, 3, 5);
      if (retry && this._currentRequest != null) {
         synchronized (this._worker) {
            this._processedRequests.removeElement(this._currentRequest);
            this.cancelChunks(this._currentRequest);
            getInstance().addRequest(this._currentRequest);
            return true;
         }
      } else {
         return true;
      }
   }

   private boolean cancelQueuedAttachment(int messageRefId, int contentPartId, boolean retry) {
      NativeAttachmentRequest requestToCancel = null;
      Enumeration e = this._requests.elements();

      while (e.hasMoreElements()) {
         NativeAttachmentRequest request = (NativeAttachmentRequest)e.nextElement();
         if (request.getCmimeRefId() == messageRefId && request.getContentPartId() == contentPartId) {
            requestToCancel = request;
            break;
         }
      }

      if (requestToCancel != null) {
         if (retry) {
            EventLogger.logEvent(-1237457833540244999L, 10, 5);
            return true;
         }

         EventLogger.logEvent(-1237457833540244999L, 9, 5);
         this._requests.removeElement(requestToCancel);
      }

      return true;
   }

   private boolean cancelMessage(int messageRefId, boolean sendCancelToServer, byte errorCode, byte[] errorDescription) {
      EventLogger.logEvent(-1237457833540244999L, 4, 5);
      ServiceRecord serviceRecord = null;
      ServiceRecord r1 = this.removeRequests(this._requests, messageRefId);
      ServiceRecord r2 = this.removeRequests(this._processedRequests, messageRefId);
      serviceRecord = r1 != null ? r1 : r2;
      if (sendCancelToServer && serviceRecord != null) {
         NativeAttachmentRequestProcessor$Helper.transmitMessageMoreCancel(this._transmissionService, messageRefId, serviceRecord, errorCode, errorDescription);
      }

      return true;
   }

   private void failRequest(NativeAttachmentRequest request) {
      this.cancelChunks(request);
      this._processedRequests.removeElement(request);
      EventLogger.logEvent(-1237457833540244999L, 13, 2);
      NativeAttachmentRequestProcessor$Helper.updateMessageStatus(request.getMessage(), 8191);
   }

   private ServiceRecord removeRequests(Vector requests, int messageRefId) {
      ServiceRecord serviceRecord = null;

      for (int i = requests.size() - 1; i >= 0; i--) {
         NativeAttachmentRequest request = (NativeAttachmentRequest)requests.elementAt(i);
         if (request.getCmimeRefId() == messageRefId) {
            serviceRecord = request.getServiceRecord();
            this.cancelChunks(request);
            requests.removeElementAt(i);
         }
      }

      return serviceRecord;
   }

   private ServiceRecord removeRequests(Vector requests, int messageRefId, int contentPartId) {
      ServiceRecord serviceRecord = null;

      for (int i = requests.size() - 1; i >= 0; i--) {
         NativeAttachmentRequest request = (NativeAttachmentRequest)requests.elementAt(i);
         if (request.getCmimeRefId() == messageRefId && request.getContentPartId() == contentPartId) {
            serviceRecord = request.getServiceRecord();
            this.cancelChunks(request);
            requests.removeElementAt(i);
         }
      }

      return serviceRecord;
   }

   private void cancelChunks(NativeAttachmentRequest request) {
      IntHashtable chunks = request.getChunks();
      IntEnumeration e = chunks.keys();

      while (e.hasMoreElements()) {
         int key = e.nextElement();
         this._transmissionService.cancelTransmitObject(key, null);
      }

      request.removeAllChunks();
   }

   public boolean moreCompleted(int messageRefId, int contentPartId, boolean markMessageComplete, Object transmissionContext) {
      EmailMessageModel message = NativeAttachmentRequestProcessor$Helper.getMessageFromParameters(messageRefId, transmissionContext);
      if (message == null) {
         return false;
      }

      if (markMessageComplete) {
         EventLogger.logEvent(-1237457833540244999L, 5, 4);
         NativeAttachmentRequestProcessor$Helper.updateMessageStatus(message, 33554431);
         synchronized (this._worker) {
            this.removeRequests(this._processedRequests, messageRefId);
            this._worker.notifyAll();
            return true;
         }
      } else {
         return true;
      }
   }

   public static NativeAttachmentRequestProcessor getInstance() {
      if (_self == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _self = (NativeAttachmentRequestProcessor)applicationRegistry.getOrWaitFor(-285957192141787993L);
         if (_self == null) {
            _self = new NativeAttachmentRequestProcessor();
            applicationRegistry.put(-285957192141787993L, _self);
         }
      }

      return _self;
   }

   private NativeAttachmentRequestProcessor() {
      this._currentRequest = null;
   }

   public void process() {
      if (this._requests.isEmpty()) {
         EventLogger.logEvent(-1237457833540244999L, 6, 4);
      } else {
         synchronized (this._worker) {
            if (!this._worker.isRunning()) {
               Thread thread = (Thread)(new Object(this._worker));
               thread.start();
            } else {
               this._worker.notifyAll();
            }
         }
      }
   }
}
