package net.rim.device.cldc.io.btgoep;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import net.rim.device.api.util.DataBuffer;

class OBEXSession$OperationImpl implements Operation {
   protected HeaderSetImpl _receivedHeaders;
   protected HeaderSetImpl _headersToSend;
   protected OBEXSession$OperationImpl$OperationInputStream _in;
   protected OBEXSession$OperationImpl$OperationOutputStream _out;
   protected DataBuffer _receiveBuffer;
   protected DataBuffer _sendBuffer;
   protected boolean _sendEndOfBody;
   protected boolean _isPut;
   protected boolean _isClosed;
   private final OBEXSession this$0;

   @Override
   public void close() {
      synchronized (this.this$0) {
         this._isClosed = true;
         if (this._in != null) {
            this._in.close();
         }

         if (this._out != null) {
            this._out.close();
         }

         this.this$0._operationInProgress = false;
      }
   }

   protected void writeToReceiveBuffer(byte[] data) {
      synchronized (this.this$0) {
         this._receiveBuffer.trimHead(false);
         this._receiveBuffer.setPosition(this._receiveBuffer.getLength());
         this._receiveBuffer.write(data);
         this._receiveBuffer.setPosition(0);
      }
   }

   protected void doSendReceive(boolean _1, boolean _2) {
      throw null;
   }

   @Override
   public void sendHeaders(HeaderSet headers) {
      synchronized (this.this$0) {
         if (headers == null) {
            throw new Object();
         }

         if (!this._isClosed && (this._out == null || !this._out.isClosed())) {
            if (headers instanceof HeaderSetImpl) {
               this._headersToSend = (HeaderSetImpl)headers;
            } else {
               throw new Object();
            }
         } else {
            throw new Object("Operation is closed");
         }
      }
   }

   @Override
   public int getResponseCode() {
      throw null;
   }

   @Override
   public String getType() {
      try {
         if (this._receivedHeaders == null) {
            this.doSendReceive(false, false);
            if (this._receivedHeaders == null) {
               return null;
            }
         }

         return (String)this._receivedHeaders.getHeader(66);
      } finally {
         ;
      }
   }

   @Override
   public String getEncoding() {
      return null;
   }

   @Override
   public long getLength() {
      try {
         if (this._receivedHeaders == null) {
            this.doSendReceive(false, false);
            if (this._receivedHeaders == null) {
               return -1;
            }
         }

         Long l = (Long)this._receivedHeaders.getHeader(195);
         if (l != null) {
            return l;
         }
      } finally {
         return -1;
      }

      return -1;
   }

   @Override
   public InputStream openInputStream() {
      synchronized (this.this$0) {
         if (this._isClosed) {
            throw new Object("Operation is closed");
         } else if (this._in == null) {
            this._in = new OBEXSession$OperationImpl$OperationInputStream(this);
            return this._in;
         } else {
            throw new Object("Already open");
         }
      }
   }

   @Override
   public DataInputStream openDataInputStream() {
      return (DataInputStream)(new Object(this.openInputStream()));
   }

   @Override
   public OutputStream openOutputStream() {
      synchronized (this.this$0) {
         if (this._isClosed) {
            throw new Object("Operation is closed");
         } else if (this._out == null) {
            this._sendEndOfBody = true;
            this._out = new OBEXSession$OperationImpl$OperationOutputStream(this);
            return this._out;
         } else {
            throw new Object("Already open");
         }
      }
   }

   @Override
   public DataOutputStream openDataOutputStream() {
      return (DataOutputStream)(new Object(this.openOutputStream()));
   }

   @Override
   public HeaderSet getReceivedHeaders() {
      synchronized (this.this$0) {
         if (this._isClosed) {
            throw new Object("Operation is closed");
         }

         if (this._receivedHeaders == null) {
            this.doSendReceive(false, false);
         }

         return this._receivedHeaders;
      }
   }

   @Override
   public void abort() {
      throw null;
   }

   OBEXSession$OperationImpl(OBEXSession _1) {
      this.this$0 = _1;
      _1._operationInProgress = true;
   }
}
