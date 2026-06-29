package net.rim.wica.runtime.messaging.internal.outbound;

import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.comm.Requestor;
import net.rim.wica.runtime.messaging.internal.MessageImpl;
import net.rim.wica.runtime.messaging.internal.MessageV1Impl;
import net.rim.wica.runtime.messaging.internal.MessageV2Impl;
import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.Provider;
import net.rim.wica.runtime.util.internal.BackgroundWorker;
import net.rim.wica.transport.security.SecureMessageFactory;
import net.rim.wica.transport.security.SecureMessageHandler;
import net.rim.wica.transport.security.SecureMessageV1;

public class OutboundSecureMsgHandler extends SecureMessageHandler implements Requestor {
   private SecureMessageFactory _factory;
   private OutgoingRequest _request;
   private Throwable _error;
   static Class class$net$rim$wica$transport$security$SecureMessageFactory;

   OutboundSecureMsgHandler(Provider provider) {
      this._factory = (BackgroundWorker)provider.getService(
         class$net$rim$wica$transport$security$SecureMessageFactory == null
            ? (class$net$rim$wica$transport$security$SecureMessageFactory = class$("net.rim.wica.transport.security.SecureMessageFactory"))
            : class$net$rim$wica$transport$security$SecureMessageFactory
      );
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void finalize(OutgoingRequest request) {
      this._request = request;
      MessageImpl message = (MessageImpl)request.getCustomData();

      label30:
      try {
         if (message.getVersion() == 1) {
            this._factory.handleUnsecureMessage(((MessageV1Impl)message).getTransportMessage(), message.getAGID(), this);
         } else {
            this._factory.handleUnsecureMessage(((MessageV2Impl)message).getTransportMessage().serialize(), message.getDeviceId(), message.getAGID(), this);
         }
      } catch (Throwable var5) {
         this._error = t;
         break label30;
      }

      if (this._error != null) {
         this._request.cancel();
         InternalLogger.logWarning(this, "Request canceled", this._error, message);
      }

      this._request = null;
      this._error = null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleMessage(SecureMessageV1 message) {
      try {
         message.secure(((MessageImpl)this._request.getCustomData()).getSecurityMode());
         this._request.setData(message.getPayload());
      } catch (Throwable var4) {
         this._error = t;
         return;
      }
   }

   @Override
   public String toString() {
      return "MDS Runtime Messaging.OutboundProcessor";
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new NoClassDefFoundError(x1.getMessage());
      }
   }
}
