package net.rim.wica.runtime.comm.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.rim.device.cldc.io.utility.URL;
import net.rim.vm.Array;
import net.rim.wica.runtime.event.EventListener;
import net.rim.wica.runtime.util.BoundedLinkedQueue;
import net.rim.wica.runtime.util.LinkedQueue;
import net.rim.wica.runtime.util.ProgressMonitor;

public final class OutgoingRequestProcessor implements Runnable, EventListener {
   private CommunicationServiceImpl _commService;
   private Hashtable _deferredQueues;
   private Runnable _dequeueingStrategy;
   private BoundedLinkedQueue _mainQueue;
   private int _totalWorkUnits = 100;
   private static String USER_AGENT = "MDSRuntime";
   private static int MAIN_QUEUE_WAIT = 600000;

   final void attemptSending(OutgoingRequestImpl param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestImpl.getURL ()Lnet/rim/device/cldc/io/utility/URL;
      // 05: invokevirtual net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.isDestinationServerPolled (Lnet/rim/device/cldc/io/utility/URL;)Z
      // 08: istore 2
      // 09: iload 2
      // 0a: ifeq 13
      // 0d: aload 0
      // 0e: aload 1
      // 0f: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.handleDestinationPolled (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;)V
      // 12: return
      // 13: aconst_null
      // 14: astore 3
      // 15: aload 0
      // 16: aload 1
      // 17: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processRequest (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;)Lnet/rim/wica/runtime/comm/internal/ResponseImpl;
      // 1a: astore 3
      // 1b: aload 3
      // 1c: invokevirtual net/rim/wica/runtime/comm/internal/ResponseImpl.isSuccessful ()Z
      // 1f: ifeq 29
      // 22: aload 0
      // 23: aload 1
      // 24: aload 3
      // 25: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processResponse (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 28: return
      // 29: aload 0
      // 2a: aload 1
      // 2b: aload 3
      // 2c: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.handleRequestFailed (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 2f: return
      // 30: astore 4
      // 32: aload 0
      // 33: aload 1
      // 34: aload 4
      // 36: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processError (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Ljava/lang/Throwable;)Lnet/rim/wica/runtime/comm/internal/ResponseImpl;
      // 39: astore 3
      // 3a: aload 3
      // 3b: invokevirtual net/rim/wica/runtime/comm/internal/ResponseImpl.isSuccessful ()Z
      // 3e: ifeq 48
      // 41: aload 0
      // 42: aload 1
      // 43: aload 3
      // 44: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processResponse (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 47: return
      // 48: aload 0
      // 49: aload 1
      // 4a: aload 3
      // 4b: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.handleRequestFailed (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 4e: return
      // 4f: astore 4
      // 51: aload 0
      // 52: aload 1
      // 53: aload 4
      // 55: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processError (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Ljava/lang/Throwable;)Lnet/rim/wica/runtime/comm/internal/ResponseImpl;
      // 58: astore 3
      // 59: aload 3
      // 5a: invokevirtual net/rim/wica/runtime/comm/internal/ResponseImpl.isSuccessful ()Z
      // 5d: ifeq 67
      // 60: aload 0
      // 61: aload 1
      // 62: aload 3
      // 63: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processResponse (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 66: return
      // 67: aload 0
      // 68: aload 1
      // 69: aload 3
      // 6a: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.handleRequestFailed (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 6d: return
      // 6e: astore 5
      // 70: aload 3
      // 71: invokevirtual net/rim/wica/runtime/comm/internal/ResponseImpl.isSuccessful ()Z
      // 74: ifeq 80
      // 77: aload 0
      // 78: aload 1
      // 79: aload 3
      // 7a: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.processResponse (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 7d: goto 86
      // 80: aload 0
      // 81: aload 1
      // 82: aload 3
      // 83: invokespecial net/rim/wica/runtime/comm/internal/OutgoingRequestProcessor.handleRequestFailed (Lnet/rim/wica/runtime/comm/internal/OutgoingRequestImpl;Lnet/rim/wica/runtime/comm/internal/ResponseImpl;)V
      // 86: aload 5
      // 88: athrow
      // try (13 -> 17): 30 null
      // try (13 -> 17): 49 null
      // try (13 -> 17): 68 null
      // try (30 -> 36): 68 null
      // try (49 -> 55): 68 null
      // try (68 -> 69): 68 null
   }

   final void stop() {
      this._mainQueue.put(new OutgoingRequestProcessor$2(this));
   }

   final void attemptDeferred() {
      this._mainQueue.put(this._dequeueingStrategy);
   }

   final boolean isDestinationServerPolled(URL url) {
      return url == null ? false : this._deferredQueues.containsKey(url.getHost());
   }

   final void sendRequest(OutgoingRequestImpl request) {
      request.setOutgoingRequestProcessor(this);
      this.enqueueInternal(request);
   }

   @Override
   public final void handleEvent(Object sender, int event, int eventParam, Object data) {
      if (event == 302) {
         this.attemptDeferred();
      } else {
         if (event == 100 && eventParam == 2) {
            this._mainQueue.put(new OutgoingRequestProcessor$1(this));
         }
      }
   }

   @Override
   public final void run() {
      while (this._commService.isRunning()) {
         Runnable request = (Runnable)this._mainQueue.poll(MAIN_QUEUE_WAIT);
         if (request != null) {
            request.run();
         } else {
            this._dequeueingStrategy.run();
         }
      }
   }

   public OutgoingRequestProcessor(CommunicationServiceImpl commService) {
      this._commService = commService;
      this._mainQueue = new BoundedLinkedQueue(Integer.MAX_VALUE);
      this._deferredQueues = new Hashtable();
      this._dequeueingStrategy = new OutgoingRequestProcessor$DeferredQueueDequeueingStrategy(this, null);
   }

   private final void enqueueDeferredInternal(OutgoingRequestImpl request) {
      String host = request.getURL().getHost();
      LinkedQueue deferredQueue = (LinkedQueue)this._deferredQueues.get(host);
      if (deferredQueue == null) {
         deferredQueue = new LinkedQueue();
         this._deferredQueues.put(host, deferredQueue);
      }

      deferredQueue.put(request);
      this._commService.fireServerStatusChangeEvent(request.getURL(), true);
   }

   private final void enqueueInternal(OutgoingRequestImpl request) {
      this._mainQueue.put(request);
   }

   private final void handleDestinationPolled(OutgoingRequestImpl request) {
      if (request.getMaxAttempts() == 1) {
         this.processResponse(request, new ResponseImpl(604));
      } else {
         this.enqueueDeferredInternal(request);
      }
   }

   private final void handleRequestFailed(OutgoingRequestImpl request, ResponseImpl response) {
      if (!request.hasExpired() && !request.isCanceled() && response.retry()) {
         if (request.getAttemptCount() == 1) {
            this.attemptSending(request);
         } else {
            this.enqueueDeferredInternal(request);
         }
      } else {
         this.processResponse(request, response);
      }
   }

   private final ResponseImpl processError(OutgoingRequestImpl request, Throwable error) {
      if (request.isCanceled()) {
         return new ResponseImpl(602);
      } else if (!this._commService.isInCoverage()) {
         this._commService.logException("Sending request to " + request.getURL() + " failed due to out of coverage", error);
         return new ResponseImpl(603);
      } else if (error instanceof IOException) {
         this._commService.logException("Server unreachable, request to " + request.getURL() + " failed", error);
         return new ResponseImpl(604);
      } else if (error instanceof OutOfMemoryError) {
         this._commService.logException("Out of memory, request to " + request.getURL() + " failed", error);
         return new ResponseImpl(603);
      } else {
         this._commService.logException("Sending request to " + request.getURL() + " failed", error);
         return new ResponseImpl(601);
      }
   }

   private final void processResponse(OutgoingRequestImpl request, ResponseImpl response) {
      try {
         request.getResponseListener().processResponse(response, request);
      } finally {
         return;
      }
   }

   private final ResponseImpl receiveResponse(HttpConnection connection, OutgoingRequestImpl request) {
      ProgressMonitor monitor = request.getProgressMonitor();
      byte[] buffer = null;
      ResponseImpl response = null;
      monitor.subTask("Waiting for response...");
      response = new ResponseImpl(connection.getResponseCode(), connection.getURL());
      if (response.isSuccessful()) {
         InputStream in = connection.openInputStream();
         int responseSize = (int)connection.getLength();
         boolean responseSizeKnown = responseSize != -1;
         double appxdownloaded = (double)0L;
         if (responseSizeKnown) {
            buffer = new byte[responseSize];
         } else {
            buffer = new byte[0];
         }

         byte[] readingChunk = new byte[256];
         int bytesRead = 0;
         int offset = 0;

         while ((bytesRead = in.read(readingChunk)) != -1) {
            if (monitor.isCanceled()) {
               monitor.done();
               return new ResponseImpl(602);
            }

            if (!responseSizeKnown) {
               Array.resize(buffer, buffer.length + bytesRead);
               appxdownloaded = offset / 4674736413210574848L;
               if (appxdownloaded > 4606281698874543309L) {
                  appxdownloaded = (double)4606281698874543309L;
               }

               monitor.worked((int)(appxdownloaded * this._totalWorkUnits));
            } else {
               monitor.worked((int)((double)offset / responseSize * this._totalWorkUnits));
            }

            System.arraycopy(readingChunk, 0, buffer, offset, bytesRead);
            offset += bytesRead;
         }

         response.setData(buffer);
         response.setContentType(connection.getType());
         response.copyHeadersFrom(connection);
         monitor.worked(this._totalWorkUnits);
         monitor.done();
      }

      return response;
   }

   private final HttpConnection createConnection(String uri, OutgoingRequestImpl request) {
      uri = uri + ";DeviceSide=false";
      String uid = this._commService.getIPPPUid();
      if (uid != null && (uid.length() > 1 || uid.length() == 1 && uid.charAt(0) != '-')) {
         uri = uri + ";ConnectionUID=" + uid;
      }

      HttpConnection connection = (HttpConnection)Connector.open(uri);
      request.setConnection(connection);
      connection.setRequestMethod(request.getRequestMethod());
      connection.setRequestProperty("User-Agent", USER_AGENT);
      request.finalize();
      if (request.hasHeaders()) {
         request.copyHeadersTo(connection);
      }

      if (request.hasData()) {
         OutputStream stream = connection.openOutputStream();
         stream.write(request.getData());
         stream.flush();
      }

      return connection;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final ResponseImpl processRequest(OutgoingRequestImpl request) {
      request.incrementAttemptCount();
      boolean var9 = false /* VF: Semaphore variable */;

      ResponseImpl var12;
      label75: {
         label74: {
            ResponseImpl var14;
            try {
               var9 = true;
               ProgressMonitor pm = request.getProgressMonitor();
               if (pm.isCanceled()) {
                  var12 = new ResponseImpl(602);
                  var9 = false;
                  break label75;
               }

               pm.beginTask("Sending request to " + request.getUrl(), this._totalWorkUnits);
               this._commService.waitForNetwork();
               if (pm.isCanceled()) {
                  var12 = new ResponseImpl(602);
                  var9 = false;
                  break label74;
               }

               String uri = request.getUrl();
               ResponseImpl response = null;
               int numRedirects = 0;

               while (true) {
                  HttpConnection connection = this.createConnection(uri, request);
                  response = this.receiveResponse(connection, request);
                  if (!response.isRedirect() || numRedirects > 10) {
                     CommUtilities.closeAndIgnoreException(connection);
                     var14 = response;
                     var9 = false;
                     break;
                  }

                  uri = CommUtilities.getRedirectUrl(connection);
                  CommUtilities.closeAndIgnoreException(connection);
                  numRedirects++;
               }
            } finally {
               if (var9) {
                  request.setConnection(null);
               }
            }

            request.setConnection(null);
            return var14;
         }

         request.setConnection(null);
         return var12;
      }

      request.setConnection(null);
      return var12;
   }
}
