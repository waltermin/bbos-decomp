package net.rim.device.api.crypto.tls;

import net.rim.device.api.crypto.InitializationVector;
import net.rim.device.api.crypto.SymmetricKey;

public final class KeyMaterial {
   private SymmetricKey _clientCipher;
   private SymmetricKey _serverCipher;
   private SymmetricKey _clientMAC;
   private SymmetricKey _serverMAC;
   private InitializationVector _clientIV;
   private InitializationVector _serverIV;

   public KeyMaterial(SymmetricKey clientCipher, SymmetricKey serverCipher, SymmetricKey clientMAC, SymmetricKey serverMAC) {
      this(clientCipher, serverCipher, clientMAC, serverMAC, null, null);
   }

   public KeyMaterial(
      SymmetricKey clientCipher,
      SymmetricKey serverCipher,
      SymmetricKey clientMAC,
      SymmetricKey serverMAC,
      InitializationVector clientIV,
      InitializationVector serverIV
   ) {
      this._clientCipher = clientCipher;
      this._serverCipher = serverCipher;
      this._clientMAC = clientMAC;
      this._serverMAC = serverMAC;
      this._clientIV = clientIV;
      this._serverIV = serverIV;
   }

   public final SymmetricKey getClientCipher() {
      return this._clientCipher;
   }

   public final SymmetricKey getServerCipher() {
      return this._serverCipher;
   }

   public final SymmetricKey getClientMAC() {
      return this._clientMAC;
   }

   public final SymmetricKey getServerMAC() {
      return this._serverMAC;
   }

   public final InitializationVector getClientIV() {
      return this._clientIV;
   }

   public final InitializationVector getServerIV() {
      return this._serverIV;
   }
}
