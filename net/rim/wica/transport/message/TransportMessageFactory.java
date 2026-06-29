package net.rim.wica.transport.message;

import net.rim.wica.transport.UnsupportedVersionException;
import net.rim.wica.transport.VersionProvider;
import net.rim.wica.transport.internal.message.TransportMessageV1_0;
import net.rim.wica.transport.internal.message.TransportMessageV2_0;

public class TransportMessageFactory {
   private int _minVersion;
   private int _maxVersion;
   private VersionProvider _versionProvider;
   public static int MIN_VERSION = 1;
   public static int MAX_VERSION = 2;

   public TransportMessageFactory(VersionProvider versionProvider) {
      this(MIN_VERSION, MAX_VERSION, versionProvider);
   }

   public TransportMessageFactory(int minVersion, int maxVersion, VersionProvider versionProvider) {
      if (versionProvider == null) {
         throw new IllegalArgumentException();
      }

      if (minVersion >= MIN_VERSION && maxVersion <= MAX_VERSION) {
         this._minVersion = minVersion;
         this._maxVersion = maxVersion;
         this._versionProvider = versionProvider;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void handleMessage(long receiverId, TransportMessageHandler handler) {
      this.handleMessage(receiverId, -1, handler);
   }

   public void handleMessage(long receiverId, int capacity, TransportMessageHandler handler) throws UnsupportedVersionException {
      if (handler == null) {
         throw new IllegalArgumentException();
      }

      int version = this._versionProvider.getTransportMessageVersion(receiverId);
      if (!this.isSupported(version)) {
         throw new UnsupportedVersionException();
      }

      switch (version) {
         case 0:
            throw new RuntimeException();
         case 1:
         default:
            handler.handleMessage(new TransportMessageV1_0(capacity));
            return;
         case 2:
            handler.handleMessage(new TransportMessageV2_0(capacity));
      }
   }

   public void handleMessage(byte[] buffer, TransportMessageHandler handler) throws UnsupportedVersionException {
      if (buffer != null && handler != null) {
         int version = buffer[0] & 255;
         if (!this.isSupported(version)) {
            throw new UnsupportedVersionException();
         }

         switch (version) {
            case 0:
               throw new RuntimeException();
            case 1:
            default:
               handler.handleMessage(new TransportMessageV1_0(buffer));
               return;
            case 2:
               handler.handleMessage(new TransportMessageV2_0(buffer));
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private boolean isSupported(int version) {
      return this._minVersion <= version && version <= this._maxVersion;
   }
}
