package net.rim.wica.transport.handshake;

import net.rim.wica.transport.security.SecurityProvider;

public class HandshakeMessageFactory {
   private SecurityProvider _sp;
   private int _minSupportedVersion;
   private int _maxSupportedVersion;
   private static final int _minImplementedVersion = 2;
   private static final int _maxImplementedVersion = 2;

   public HandshakeMessageFactory(SecurityProvider sp, int minSupportedVersion, int maxSupportedVersion) {
      this._sp = sp;
      this._minSupportedVersion = minSupportedVersion;
      this._maxSupportedVersion = maxSupportedVersion;
   }

   public void handleMessage(byte[] m, HandshakeMessageHandler h) {
      if (h != null) {
         HandshakeMessageBuffer hmb = new HandshakeMessageBuffer(m);
         int messageType = hmb.readByte();
         int messageVersion = hmb.readByte();
         if (messageType == 1) {
            if (messageVersion == 2 && this.isSupportedVersion(2)) {
               h.handleMessage(new ClientHelloV1_1(hmb));
            } else {
               h.handleMessage(new DefaultClientHello(hmb, messageVersion));
            }
         } else if (messageType == 2) {
            if (messageVersion == 2 && this.isSupportedVersion(2)) {
               h.handleMessage(new ServerHelloV1_1(hmb));
            } else {
               h.handleMessage(new DefaultServerHello(hmb, messageVersion));
            }
         } else if (messageVersion == 2 && this.isSupportedVersion(2)) {
            byte command = hmb.readByte();
            if (command == RegisterV1_1.getCommand()) {
               h.handleMessage(new RegisterV1_1(hmb, this._sp));
            } else if (command == OkV1_1.getCommand()) {
               h.handleMessage(new OkV1_1(hmb, this._sp));
            } else if (command == FailureV1_1.getCommand()) {
               h.handleMessage(new FailureV1_1(hmb, this._sp));
            } else if (command == UnregisterV1_1.getCommand()) {
               h.handleMessage(new UnregisterV1_1(hmb, this._sp));
            } else {
               throw new HandshakeMessageException();
            }
         } else {
            throw new VersionNotSupportedException();
         }
      }
   }

   public ClientHelloV1 createClientHelloV1(int version) {
      ClientHelloV1 message = null;
      if (version == 2) {
         message = new ClientHelloV1_1();
      }

      return message;
   }

   public ClientHello createClientHello() {
      return new ClientHelloV1_1();
   }

   public ServerHello createServerHello() {
      return new ServerHelloV1_1();
   }

   public ServerHelloV1 createServerHelloV1(int version) {
      ServerHelloV1 message = null;
      if (version == 2) {
         message = new ServerHelloV1_1();
      }

      return message;
   }

   public RegisterV1 createRegisterV1(int version) {
      RegisterV1 message = null;
      if (version == 2) {
         message = new RegisterV1_1(this._sp);
      }

      return message;
   }

   public OkV1 createOkV1(int version) {
      OkV1 message = null;
      if (version == 2) {
         message = new OkV1_1(this._sp);
      }

      return message;
   }

   public FailureV1 createFailureV1(int version) {
      FailureV1 message = null;
      if (version == 2) {
         message = new FailureV1_1(this._sp);
      }

      return message;
   }

   private boolean isSupportedVersion(int version) {
      return this._minSupportedVersion <= version && version <= this._maxSupportedVersion;
   }

   public int getServerVersion(int deviceVersion) {
      if (deviceVersion > this._maxSupportedVersion) {
         int serverSecurityVersion = this._maxSupportedVersion;
      }

      int var3;
      if (deviceVersion <= 2) {
         var3 = deviceVersion;
      } else {
         var3 = 2;
      }

      if (var3 < this._minSupportedVersion || var3 < 2) {
         var3 = -1;
      }

      return var3;
   }
}
