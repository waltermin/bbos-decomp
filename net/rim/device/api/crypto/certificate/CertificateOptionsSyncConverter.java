package net.rim.device.api.crypto.certificate;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.DataBuffer;

public final class CertificateOptionsSyncConverter implements SyncConverter {
   public static final int FRIENDLY_NAME;
   public static final int BASE_QUERY;
   public static final int PORT;
   public static final int AUTH_TYPE;
   public static final int CONN_TYPE;

   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      if (!(object instanceof CertificateServerInfo)) {
         return false;
      }

      CertificateServerInfo serverInfo = (CertificateServerInfo)object;
      String friendlyName = serverInfo.getFriendlyName();
      if (friendlyName != null) {
         ConverterUtilities.writeStringIntellisync(buffer, 4, friendlyName);
      }

      String server = serverInfo.getServer();
      if (server != null) {
         ConverterUtilities.writeStringIntellisync(buffer, serverInfo.getType(), server);
      }

      int id = serverInfo.getType();
      if (id == 1) {
         String baseQuery = serverInfo.getBaseQuery();
         if (baseQuery != null) {
            ConverterUtilities.writeStringIntellisync(buffer, 5, baseQuery);
         }

         ConverterUtilities.convertInt(buffer, 6, serverInfo.getPort(), 4);
         ConverterUtilities.convertInt(buffer, 7, serverInfo.getAuthType(), 4);
         ConverterUtilities.convertInt(buffer, 8, serverInfo.getConnectionType(), 4);
      }

      return true;
   }

   @Override
   public final SyncObject convert(DataBuffer buffer, int version, int uid) {
      String server = null;
      String friendlyName = null;
      int port = 0;
      int authType = 0;
      int connType = 0;
      String baseQuery = null;

      try {
         buffer.rewind();
         if (ConverterUtilities.findType(buffer, 4)) {
            friendlyName = ConverterUtilities.readString(buffer);
         }

         buffer.rewind();
         if (ConverterUtilities.findType(buffer, 1)) {
            server = ConverterUtilities.readString(buffer);
            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 5)) {
               baseQuery = ConverterUtilities.readString(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 6)) {
               port = ConverterUtilities.readInt(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 7)) {
               authType = ConverterUtilities.readInt(buffer);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 8)) {
               connType = ConverterUtilities.readInt(buffer);
            }

            return new CertificateServerInfo(server, 1, authType, friendlyName, baseQuery, port, connType);
         } else {
            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 2)) {
               return new CertificateServerInfo(ConverterUtilities.readString(buffer), 2, friendlyName);
            }

            buffer.rewind();
            if (ConverterUtilities.findType(buffer, 3)) {
               return new CertificateServerInfo(ConverterUtilities.readString(buffer), 3, friendlyName);
            }

            buffer.rewind();
            return ConverterUtilities.findType(buffer, 4) ? new CertificateServerInfo(ConverterUtilities.readString(buffer), 4, friendlyName) : null;
         }
      } finally {
         ;
      }
   }
}
