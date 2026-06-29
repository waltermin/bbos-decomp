package net.rim.device.cldc.io.http;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.io.http.HttpProtocolConstants;
import net.rim.device.api.io.http.HttpServerConnection;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.cldc.io.devicehttp.ChunkedInputStream;
import net.rim.device.cldc.io.devicehttp.LengthControlledInputStream;
import net.rim.device.cldc.io.utility.URL;

public class HttpServerProtocolBase implements HttpServerConnection, HttpProtocolConstants {
   private HttpServerSocketConnectionBase _serverAcceptConnection;
   protected StreamConnection _subConnection;
   private RequestLine _requestLine;
   private HttpHeaders _headers;
   private boolean _headersRead;
   private boolean _closed;
   private DataInputStream _dataInputStream;
   private InputStream _inputStream;
   private ByteArrayOutputStream _outputStream;
   private DataOutputStream _dataOutputStream;
   private HttpHeaders _responseHeaders;
   private StatusLine _statusLine;
   private boolean _closeSubConnectionOnCloseRequest;
   private DataInputStream _subIn;
   private DataOutputStream _subOut;
   private static final String MSG_Url_is_Null = "Url is Null";
   private static final String MSG_SubConnection_is_Null = "SubConnection is Null";
   private static final String MSG_Connection_is_closed = "Connection is closed";
   private static final String MSG_Not_Supported = "Not Supported";
   private static final String MSG_RequestLine_is_Null = "RequestLine is Null";

   @Override
   public final void close() {
      if (!this._closed) {
         if (this._headersRead) {
            label249:
            try {
               DataOutputStream out = this._subOut;
               if (out != null && this._statusLine != null) {
                  byte[] bytes = null;
                  if (this._outputStream != null) {
                     bytes = this._outputStream.toByteArray();
                  }

                  if (bytes != null) {
                     this._responseHeaders.setProperty("Content-Length", Integer.toString(bytes.length));
                  }

                  if (this._closeSubConnectionOnCloseRequest) {
                     this._responseHeaders.setProperty("Connection", "close");
                  }

                  this._statusLine.writeToStream(out);
                  this._responseHeaders.writeToStream(out);
                  if (bytes != null) {
                     out.write(bytes);
                  }
               }

               if (out != null && this._closeSubConnectionOnCloseRequest) {
                  out.close();
               }
            } finally {
               break label249;
            }
         }

         if (this._closeSubConnectionOnCloseRequest
            || !this._headersRead
            || this._inputStream instanceof LengthControlledInputStream && ((LengthControlledInputStream)this._inputStream).getLength() > 0
            || this._inputStream instanceof ChunkedInputStream && !((ChunkedInputStream)this._inputStream).eof()
            || this._inputStream instanceof Object && this._inputStream.available() > 0) {
            label227:
            try {
               if (this._subIn != null) {
                  this._subIn.close();
               }
            } finally {
               break label227;
            }

            label224:
            try {
               if (this._subOut != null) {
                  this._subOut.close();
               }
            } finally {
               break label224;
            }

            this._subConnection.close();
         } else if (this._serverAcceptConnection != null) {
            this._serverAcceptConnection.addAcceptCandidate(this._subConnection, this._subIn, this._subOut);
         }

         this._closed = true;
         this.clear();
      }
   }

   public String getAddress() {
      return !(this._subConnection instanceof Object) ? null : ((SocketConnection)this._subConnection).getAddress();
   }

   public String getResponseProperty(int n) {
      return this._responseHeaders.getPropertyValue(n);
   }

   public String getResponsePropertyKey(int n) {
      return this._responseHeaders.getPropertyKey(n);
   }

   public final HttpHeaders getHeaders() {
      if (this._closed) {
         throw new Object();
      }

      if (!this._headersRead) {
         this._inputStream = this.parseIncomingHeaders(this._subIn);
      }

      return this._headers;
   }

   @Override
   public final String getType() {
      try {
         return ((HttpHeaders)this.getHeaders()).getPropertyValue("Content-Type");
      } finally {
         ;
      }
   }

   @Override
   public final String getEncoding() {
      try {
         return ((HttpHeaders)this.getHeaders()).getPropertyValue("Content-Encoding");
      } finally {
         ;
      }
   }

   @Override
   public final long getLength() {
      try {
         String value = ((HttpHeaders)this.getHeaders()).getPropertyValue("Content-Length");
         return value == null ? -1 : Integer.parseInt(value);
      } finally {
         return -1;
      }
   }

   @Override
   public final String getRequestMethod() {
      RequestLine requestLine = this.getRequestLine();
      if (requestLine != null) {
         return requestLine.getMethod();
      } else {
         throw new Object("RequestLine is Null");
      }
   }

   @Override
   public final String getVersion() {
      return this.getRequestLine().getVersion();
   }

   @Override
   public final String getRequestURI() {
      return this.getRequestLine().getRequestURI();
   }

   @Override
   public final String getHeaderField(int n) {
      return ((HttpHeaders)this.getHeaders()).getPropertyValue(n);
   }

   @Override
   public final String getHeaderField(String name) {
      return ((HttpHeaders)this.getHeaders()).getPropertyValue(name);
   }

   @Override
   public final int getHeaderFieldInt(String name, int def) {
      if (name == null) {
         return def;
      }

      String value = ((HttpHeaders)this.getHeaders()).getPropertyValue(name);
      return value != null ? Integer.parseInt(value) : def;
   }

   @Override
   public final String getHeaderFieldKey(int n) {
      return ((HttpHeaders)this.getHeaders()).getPropertyKey(n);
   }

   @Override
   public final OutputStream openOutputStream() {
      if (this._outputStream == null) {
         this._outputStream = (ByteArrayOutputStream)(new Object());
      }

      return this._outputStream;
   }

   @Override
   public final DataInputStream openDataInputStream() {
      if (this._dataInputStream == null) {
         InputStream in = this.openInputStream();
         if (in instanceof Object) {
            this._dataInputStream = (DataInputStream)in;
         } else {
            this._dataInputStream = (DataInputStream)(new Object(in));
         }
      }

      return this._dataInputStream;
   }

   @Override
   public final InputStream openInputStream() {
      if (this._closed) {
         throw new Object();
      }

      if (!this._headersRead) {
         this._inputStream = this.parseIncomingHeaders(this._subIn);
      }

      return this._inputStream;
   }

   @Override
   public String getResponseProperty(String key) {
      return this._responseHeaders.getPropertyValue(key);
   }

   @Override
   public void setResponseProperty(String key, String value) {
      if (this._closed) {
         throw new Object();
      }

      this._responseHeaders.setProperty(key, value);
   }

   @Override
   public void setResponseCode(int code) {
      if (this._closed) {
         throw new Object();
      }

      if (this._statusLine == null) {
         this._statusLine = new StatusLine();
      }

      this._statusLine.setStatus(code);
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      if (this._dataOutputStream == null) {
         this._dataOutputStream = (DataOutputStream)(new Object(this.openOutputStream()));
      }

      return this._dataOutputStream;
   }

   private boolean containsToken(String value, String token) {
      if (value != null && token != null) {
         StringTokenizer tokenizer = (StringTokenizer)(new Object(value, ','));

         while (tokenizer.hasMoreTokens()) {
            if (StringUtilities.strEqualIgnoreCase(tokenizer.nextToken().trim(), token, 1701707776)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private InputStream parseIncomingHeaders(DataInputStream dins) throws IOException {
      try {
         this._requestLine = new RequestLine(dins);
         this._headers = new HttpHeaders(dins);
         this._headersRead = true;
         this._closeSubConnectionOnCloseRequest = true;
         String connection = this._headers.getPropertyValue("Connection");
         if (StringUtilities.strEqual(this._requestLine.getVersion(), "HTTP/1.1")) {
            this._closeSubConnectionOnCloseRequest = false;
         }

         if (this.containsToken(connection, "close")) {
            this._closeSubConnectionOnCloseRequest = true;
         }

         String transferEncoding = this._headers.getPropertyValue("Transfer-Encoding");
         String contentLength = this._headers.getPropertyValue("Content-Length");
         int contentLen = 0;
         if (StringUtilities.strEqualIgnoreCase(transferEncoding, "chunked", 1701707776)) {
            return new ChunkedInputStream(dins, this._closeSubConnectionOnCloseRequest);
         } else if (contentLength != null) {
            contentLen = Integer.parseInt(contentLength);
            return new LengthControlledInputStream(dins, contentLen, true, this._closeSubConnectionOnCloseRequest);
         } else {
            return dins;
         }
      } finally {
         this.close();
      }
   }

   private RequestLine getRequestLine() {
      this.getHeaders();
      return this._requestLine;
   }

   private void clear() {
      this._subConnection = null;
      this._subIn = null;
      this._subOut = null;
      this._inputStream = null;
   }

   public HttpServerProtocolBase(
      URL url, HttpServerSocketConnectionBase serverAcceptConnection, StreamConnection subConnection, DataInputStream subIn, DataOutputStream subOut
   ) {
      if (url == null) {
         throw new Object("Url is Null");
      }

      if (subConnection == null) {
         throw new Object("SubConnection is Null");
      }

      this._serverAcceptConnection = serverAcceptConnection;
      this._subConnection = subConnection;
      this._subIn = subIn;
      this._subOut = subOut;
      this._responseHeaders = new HttpHeaders();
   }
}
