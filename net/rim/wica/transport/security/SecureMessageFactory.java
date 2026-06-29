package net.rim.wica.transport.security;

import net.rim.wica.transport.UnsupportedVersionException;
import net.rim.wica.transport.VersionProvider;
import net.rim.wica.transport.internal.security.SecureMessageV1_0;
import net.rim.wica.transport.internal.security.SecureMessageV1_1;
import net.rim.wica.transport.message.TransportMessageV1;

public class SecureMessageFactory {
   private int _minVersion;
   private int _maxVersion;
   private VersionProvider _versionProvider;
   private SecurityProvider _securityProvider;
   private SequenceProvider _sequenceProvider;
   private KeyProvider _keyProvider;
   public static int MIN_VERSION = 1;
   public static int MAX_VERSION = 2;

   public SecureMessageFactory(VersionProvider versionProvider, KeyProvider keyProvider, SecurityProvider securityProvider, SequenceProvider sequenceProvider) {
      this(MIN_VERSION, MAX_VERSION, versionProvider, keyProvider, securityProvider, sequenceProvider);
   }

   public SecureMessageFactory(
      int minVersion,
      int maxVersion,
      VersionProvider versionProvider,
      KeyProvider keyProvider,
      SecurityProvider securityProvider,
      SequenceProvider sequenceProvider
   ) {
      if (versionProvider != null && keyProvider != null && securityProvider != null && sequenceProvider != null) {
         this._minVersion = Math.max(MIN_VERSION, minVersion);
         this._maxVersion = Math.min(MAX_VERSION, maxVersion);
         this._versionProvider = versionProvider;
         this._keyProvider = keyProvider;
         this._securityProvider = securityProvider;
         this._sequenceProvider = sequenceProvider;
      } else {
         throw new Object();
      }
   }

   public void setVersionRange(int minVersion, int maxVersion) {
      this._minVersion = minVersion;
      this._maxVersion = maxVersion;
   }

   public void handleSecureMessage(byte[] buffer, SecureMessageHandler handler) throws UnsupportedVersionException, SecureMessageException {
      if (buffer != null && handler != null) {
         int securityVersion = buffer[0] & 255;
         if (!this.isSupported(securityVersion)) {
            throw new UnsupportedVersionException();
         }

         switch (securityVersion) {
            case 0:
               throw new SecureMessageException(((StringBuffer)(new Object("Internal error, invalid security version "))).append(securityVersion).toString());
            case 1:
            default:
               handler.handleMessage(new SecureMessageV1_0(buffer, this._versionProvider, this._keyProvider, this._securityProvider));
               return;
            case 2:
               handler.handleMessage(new SecureMessageV1_1(buffer, this._keyProvider, this._securityProvider, this._sequenceProvider, this._versionProvider));
         }
      } else {
         throw new Object();
      }
   }

   public void handleUnsecureMessage(byte[] buffer, long senderId, long receiverId, SecureMessageHandler handler) throws UnsupportedVersionException, SecureMessageException {
      if (buffer != null && handler != null) {
         int securityVersion = this._versionProvider.getSecurityVersion(receiverId);
         if (!this.isSupported(securityVersion)) {
            throw new UnsupportedVersionException();
         }

         switch (securityVersion) {
            case 0:
               throw new SecureMessageException(((StringBuffer)(new Object("Internal error, invalid security version "))).append(securityVersion).toString());
            case 1:
            default:
               handler.handleMessage(new SecureMessageV1_0(receiverId, buffer, this._keyProvider, this._securityProvider));
               return;
            case 2:
               handler.handleMessage(new SecureMessageV1_1(buffer, senderId, receiverId, this._keyProvider, this._securityProvider, this._sequenceProvider));
         }
      } else {
         throw new Object();
      }
   }

   public void handleUnsecureMessage(TransportMessageV1 tm, long receiverId, SecureMessageHandler handler) {
      if (tm != null && handler != null) {
         handler.handleMessage(new SecureMessageV1_0(tm, receiverId, this._keyProvider, this._securityProvider));
      } else {
         throw new Object();
      }
   }

   private boolean isSupported(int securityVersion) {
      return this._minVersion <= securityVersion && securityVersion <= this._maxVersion;
   }
}
