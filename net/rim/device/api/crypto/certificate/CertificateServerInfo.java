package net.rim.device.api.crypto.certificate;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.ui.component.BackgroundDialog;

public final class CertificateServerInfo implements SyncObject, Persistable {
   private int _uid;
   private int _type;
   private String _server;
   private String _friendlyName;
   private String _baseQuery;
   private int _port;
   private int _authType;
   private int _connectionType;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(
      -3843310740975580338L, "net.rim.device.internal.resource.crypto.CertificateServersOptions"
   );

   public final String getServer() {
      return this._server;
   }

   public final String getFriendlyName() {
      return this._friendlyName;
   }

   public final int getType() {
      return this._type;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public final String getBaseQuery() {
      return this._baseQuery;
   }

   public final int getPort() {
      return this._port;
   }

   public final int getAuthType() {
      return this._authType;
   }

   public final int getConnectionType() {
      return this._connectionType;
   }

   public final CertificateServerInfo clone() {
      return new CertificateServerInfo(this._server, this._type, this._authType, this._friendlyName, this._baseQuery, this._port, this._connectionType);
   }

   final void markDeleted() {
      this._type = 4;
   }

   public CertificateServerInfo(String server, int type, String friendlyName) {
      this.initializer(server, type, friendlyName, null, 0, 0, 0);
   }

   public CertificateServerInfo(String server, int type, int authType, String friendlyName, String baseQuery, int port) {
      this.initializer(server, type, friendlyName, baseQuery, port, authType, 0);
   }

   public CertificateServerInfo(String server, int type, int authType, String friendlyName, String baseQuery, int port, int connectionType) {
      this.initializer(server, type, friendlyName, baseQuery, port, authType, connectionType);
   }

   private final void initializer(String server, int type, String friendlyName, String baseQuery, int port, int authType, int connectionType) {
      if (server != null && friendlyName != null && (type != 1 || baseQuery != null) && (type != 1 || connectionType == 0 || connectionType == 1)) {
         this._server = server;
         this._type = type;
         this._friendlyName = friendlyName;
         this._baseQuery = baseQuery;
         this._port = port;
         this._authType = authType;
         this._connectionType = connectionType;
         this._uid = type;
         this._uid = CRC32.update(this._uid, friendlyName.getBytes());
         this._uid = CRC32.update(this._uid, server.getBytes());
         if (type == 1) {
            this._uid = CRC32.update(this._uid, baseQuery.getBytes());
            this._uid ^= port;
            this._uid ^= authType;
         }
      } else {
         throw new Object();
      }
   }

   public CertificateServerInfo(String server, int type, String friendlyName, int uid) {
      this.initializer(server, type, friendlyName, null, 0, 0, 0);
   }

   public static final void display(CertificateServerInfo serverInfo) {
      BackgroundDialog.show(new ServerDialog(serverInfo, 134217728));
   }

   @Override
   public final int hashCode() {
      return this._uid;
   }

   @Override
   public final String toString() {
      StringBuffer buffer = (StringBuffer)(new Object());
      switch (this._type) {
         case 0:
            break;
         case 1:
            buffer.append(_rb.getString(111));
            break;
         case 2:
            buffer.append(_rb.getString(110));
            break;
         case 3:
         default:
            buffer.append(_rb.getString(109));
      }

      this.addEndOfLine(buffer);
      if (this._friendlyName.length() > 0) {
         buffer.append(_rb.getString(4)).append(' ').append(this._friendlyName);
         this.addEndOfLine(buffer);
      }

      if (this._server.length() > 0) {
         if (this._type == 1) {
            buffer.append(_rb.getString(2));
         } else {
            buffer.append(_rb.getString(3));
         }

         buffer.append(' ').append(this._server);
         this.addEndOfLine(buffer);
      }

      if (this._type == 1) {
         if (this._baseQuery != null && this._baseQuery.length() > 0) {
            buffer.append(_rb.getString(5)).append(' ').append(this._baseQuery);
            this.addEndOfLine(buffer);
         }

         buffer.append(_rb.getString(6)).append(' ').append(this._port);
         this.addEndOfLine(buffer);
         buffer.append(_rb.getString(118)).append(' ');
         String[] authTypes = _rb.getStringArray(119);
         buffer.append(authTypes[this._authType]);
         this.addEndOfLine(buffer);
         buffer.append(_rb.getString(120)).append(' ');
         String[] connectionTypes = _rb.getStringArray(121);
         buffer.append(connectionTypes[this._connectionType]);
         this.addEndOfLine(buffer);
      }

      return buffer.toString();
   }

   public CertificateServerInfo(String server, int type, String friendlyName, String baseQuery, int port) {
      this.initializer(server, type, friendlyName, baseQuery, port, 0, 0);
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof CertificateServerInfo)) {
         return false;
      }

      CertificateServerInfo info = (CertificateServerInfo)obj;
      return StringUtilities.strEqual(info.getServer(), this._server)
         && StringUtilities.strEqual(info.getFriendlyName(), this._friendlyName)
         && StringUtilities.strEqual(info.getBaseQuery(), this._baseQuery)
         && info.getPort() == this._port
         && info.getUID() == this._uid
         && info.getAuthType() == this._authType
         && info.getConnectionType() == this._connectionType
         && info.getType() == this._type;
   }

   public CertificateServerInfo(String server, int type, String friendlyName, String baseQuery, int port, int uid) {
      this.initializer(server, type, friendlyName, baseQuery, port, 0, 0);
   }

   private final void addEndOfLine(StringBuffer buffer) {
      buffer.append('\n');
   }
}
