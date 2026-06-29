package net.rim.wica.runtime.comm.internal;

import net.rim.wica.runtime.comm.IncomingRequest;

public final class IncomingRequestImpl extends AbstractRequestResponse implements IncomingRequest {
   public IncomingRequestImpl(byte[] data) {
      super(data);
   }

   @Override
   public final String toString() {
      StringBuffer buf = new StringBuffer(64);
      buf.append("IncomingRequest[");
      if (this.hasData()) {
         buf.append("dataSize=").append(this.getData().length);
      } else {
         buf.append("dataSize=0");
      }

      buf.append(']');
      return buf.toString();
   }
}
