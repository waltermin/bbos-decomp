package net.rim.wica.runtime.comm.internal;

import java.io.InputStream;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.cldc.io.utility.URL;
import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.comm.Requestor;
import net.rim.wica.runtime.comm.ResponseListener;
import net.rim.wica.runtime.util.NullProgressMonitor;
import net.rim.wica.runtime.util.ProgressMonitor;

public final class OutgoingRequestImpl extends AbstractRequestResponse implements OutgoingRequest, Runnable {
   private int _maxAttempts;
   private int _attemptCount;
   private String _requestMethod;
   private ResponseListener _responseListener;
   private URL _requestUri;
   private OutgoingRequestProcessor _outgoingRequestProcessor;
   private ProgressMonitor _progressMonitor;
   private HttpConnection _connection;
   private boolean _canceled;
   private Object _customData;
   private Requestor _requestor;
   private static ResponseListener NO_RESPONSE_LISTENER = new OutgoingRequestImpl$NoResponseListener(null);
   private static ProgressMonitor NULL_MONITOR = new NullProgressMonitor();

   final void finalize() {
      if (this._requestor != null) {
         this._requestor.finalize(this);
      }
   }

   final boolean isCanceled() {
      return this._canceled;
   }

   final URL getURL() {
      return this._requestUri;
   }

   final void setConnection(HttpConnection connection) {
      this._connection = connection;
   }

   final void setProgressMonitor(ProgressMonitor progressMonitor) {
      this._progressMonitor = progressMonitor;
   }

   final ProgressMonitor getProgressMonitor() {
      return this._progressMonitor;
   }

   final void incrementAttemptCount() {
      this._attemptCount++;
   }

   final ResponseListener getResponseListener() {
      return this._responseListener;
   }

   final String getRequestMethod() {
      return this._requestMethod;
   }

   final int getMaxAttempts() {
      return this._maxAttempts;
   }

   final void setOutgoingRequestProcessor(OutgoingRequestProcessor orp) {
      this._outgoingRequestProcessor = orp;
   }

   final void copyHeadersTo(HttpConnection connection) {
      HttpHeaders headers = this.getHeaders();
      int size = headers.size();

      for (int i = 0; i < size; i++) {
         connection.setRequestProperty(headers.getPropertyKey(i), headers.getPropertyValue(i));
      }
   }

   final Requestor getRequestor() {
      return this._requestor;
   }

   @Override
   public final void setResponseListener(ResponseListener rListener) {
      if (rListener == null) {
         this._responseListener = NO_RESPONSE_LISTENER;
      } else {
         this._responseListener = rListener;
      }
   }

   @Override
   public final void setRequestMethod(String method) {
      if (method != null && (method.equals("GET") || method.equals("POST"))) {
         this._requestMethod = method;
      } else {
         throw new IllegalArgumentException("Invalid request method " + method);
      }
   }

   @Override
   public final void setMaxAttempts(int maxAttempts) {
      this._maxAttempts = maxAttempts;
   }

   @Override
   public final int getAttemptCount() {
      return this._attemptCount;
   }

   @Override
   public final boolean hasExpired() {
      return this.getAttemptCount() >= this.getMaxAttempts();
   }

   @Override
   public final void setRequestor(Requestor requestor) {
      this._requestor = requestor;
   }

   @Override
   public final void setCustomData(Object data) {
      this._customData = data;
   }

   @Override
   public final Object getCustomData() {
      return this._customData;
   }

   @Override
   public final String getUrl() {
      return this._requestUri.toString();
   }

   @Override
   public final void run() {
      if (!this.isCanceled()) {
         this._outgoingRequestProcessor.attemptSending(this);
      }
   }

   @Override
   public final void cancel() {
      if (!this._canceled) {
         if (this._connection != null) {
            label47:
            try {
               InputStream in = this._connection.openInputStream();
               if (in != null) {
                  label44:
                  try {
                     in.close();
                  } finally {
                     break label44;
                  }
               }
            } finally {
               break label47;
            }
         }

         this._canceled = true;
      }
   }

   public OutgoingRequestImpl(URL url) {
      this._requestUri = url;
      this._requestMethod = "GET";
      this._responseListener = NO_RESPONSE_LISTENER;
      this._progressMonitor = NULL_MONITOR;
      this._maxAttempts = 1;
   }
}
