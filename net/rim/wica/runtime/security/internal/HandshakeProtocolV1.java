package net.rim.wica.runtime.security.internal;

import net.rim.wica.runtime.comm.CommunicationService;
import net.rim.wica.runtime.comm.OutgoingRequest;
import net.rim.wica.runtime.comm.Response;
import net.rim.wica.runtime.comm.ResponseListener;
import net.rim.wica.runtime.security.HandshakeException;
import net.rim.wica.runtime.security.HandshakeInfo;
import net.rim.wica.runtime.security.SecurityService;
import net.rim.wica.runtime.service.ServiceProvider;
import net.rim.wica.runtime.util.internal.RuntimeUtilities;
import net.rim.wica.transport.security.Key;
import net.rim.wica.transport.security.KeyType;
import net.rim.wica.transport.security.SecurityProvider;

final class HandshakeProtocolV1 implements HandshakeProtocol, ResponseListener {
   private SecurityProvider _securityProvider;
   private HandshakeInfo _handshakeInfo;
   private CommunicationService _commService;
   private HandshakeHandler _handshakeHandler;
   private Key _rePublicKey;
   static Class class$net$rim$wica$runtime$security$SecurityService;
   static Class class$net$rim$wica$runtime$comm$CommunicationService;

   HandshakeProtocolV1(HandshakeHandler h, HandshakeInfo info, Key rePublicKey, ServiceProvider sp) {
      this._handshakeInfo = info;
      this._handshakeHandler = h;
      this._rePublicKey = rePublicKey;
      SecurityService security = (SecurityService)sp.getService(
         class$net$rim$wica$runtime$security$SecurityService == null
            ? (class$net$rim$wica$runtime$security$SecurityService = class$("net.rim.wica.runtime.security.SecurityService"))
            : class$net$rim$wica$runtime$security$SecurityService
      );
      this._securityProvider = security.getSecurityProvider();
      this._commService = (CommunicationService)sp.getService(
         class$net$rim$wica$runtime$comm$CommunicationService == null
            ? (class$net$rim$wica$runtime$comm$CommunicationService = class$("net.rim.wica.runtime.comm.CommunicationService"))
            : class$net$rim$wica$runtime$comm$CommunicationService
      );
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void register() {
      try {
         OutgoingRequest outReq = this._commService.createOutgoingRequestInstance(this._handshakeInfo.getAGURL());
         outReq.setRequestMethod("POST");
         outReq.setHeader("device_pin", Integer.toHexString(this._handshakeInfo.getDevicePIN()));
         outReq.setHeader("device_security_version", "1");
         outReq.setHeader("re_version", RuntimeUtilities.getRuntimeVersion(true));
         byte[] rePublicKey = this._securityProvider.encodeKey(this._rePublicKey);
         outReq.setData(rePublicKey);
         outReq.setResponseListener(this);
         this._commService.sendRequest(outReq);
      } catch (Throwable var4) {
         throw new HandshakeException(e, this._handshakeInfo);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void unregister() {
      try {
         OutgoingRequest request = this._commService.createOutgoingRequestInstance(this._handshakeInfo.getAGURL());
         request.setRequestMethod("POST");
         request.setHeader("device_pin", Integer.toHexString(this._handshakeInfo.getDevicePIN()));
         request.setHeader("device_un_registration", "1");
         this._commService.sendRequest(request);
      } catch (Throwable var3) {
         throw new HandshakeException("Exception during unregister operation", e, this._handshakeInfo);
      }

      this._handshakeHandler.unregistrationCompleted(this._handshakeInfo);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void processResponse(Response response, OutgoingRequest request) {
      int responseCode = response.getResponseCode();
      if (200 == responseCode) {
         try {
            byte[] agPublicKeyEncoded = response.getData();
            Key agPublicKey = this._securityProvider.decodeKey(KeyType.RSA, agPublicKeyEncoded);
            long agId = Long.parseLong(response.getHeader("server_id"));
            this._handshakeInfo.setAGId(agId);
            this._handshakeInfo.setDeviceId(Long.parseLong(response.getHeader("device_id")));
            this._handshakeInfo.setSecurityVersion(1);
            this._handshakeHandler.registrationCompleted(this._handshakeInfo, new Key[]{agPublicKey});
         } catch (Throwable var9) {
            HandshakeException he = !(e instanceof HandshakeException) ? new HandshakeException(e, this._handshakeInfo) : (HandshakeException)e;
            this._handshakeHandler.registrationFailed(he);
            return;
         }
      } else {
         this._handshakeHandler
            .registrationFailed(
               new HandshakeException((Throwable)(new Object(((StringBuffer)(new Object("HTTP Error"))).append(responseCode).toString())), this._handshakeInfo)
            );
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static final Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
