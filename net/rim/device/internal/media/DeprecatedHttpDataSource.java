package net.rim.device.internal.media;

import java.io.IOException;
import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.RIMConnector;
import net.rim.vm.TraceBack;

public class DeprecatedHttpDataSource extends DataSourceImpl {
   private HttpConnection _connection;

   public DeprecatedHttpDataSource(String locator) {
      super(locator);
   }

   @Override
   public void connect() throws IOException {
      if (!super._connected) {
         if (super._locator == null) {
            throw new IOException("Locator is null");
         }

         String protocol = "";
         int index = super._locator.indexOf(58);
         if (index == -1) {
            throw new IOException("Invalid URL");
         }

         protocol = super._locator.substring(0, index);
         if (!StringUtilities.strEqualIgnoreCase(protocol, "http", 1701707776)) {
            throw new IOException("Not an HTTP locator");
         }

         try {
            int callingModule = TraceBack.getCallingModule(2);
            this._connection = (HttpConnection)RIMConnector.open(callingModule, super._locator);
            int responseCode = this._connection.getResponseCode();
            if (responseCode != 200) {
               this._connection.close();
               throw new IOException("HTTP response code: " + responseCode);
            }

            super._contentType = this._connection.getType();
            if (super._contentType == null) {
               this._connection.close();
               throw new IOException("Cannot determine content type");
            }

            super._contentType = StringUtilities.toLowerCase(MIMETypeAssociations.getNormalizedType(super._contentType), 1701707776);
            super._contentLength = this._connection.getLength();
            super._seekType = 1;
         } catch (SecurityException e) {
            throw e;
         } catch (Exception e) {
            throw new IOException(e.getMessage());
         }

         super._connected = true;
      }
   }

   @Override
   public void disconnect() {
      if (super._connected) {
         if (super._started) {
            try {
               this.stop();
            } catch (IOException var3) {
            }
         }

         try {
            this._connection.close();
         } catch (IOException var2) {
         }

         super._connected = false;
      }
   }

   @Override
   public void start() throws IOException {
      if (super._connected) {
         if (!super._started) {
            if (this._connection != null) {
               super._is = this._connection.openInputStream();
               super._started = true;
            } else {
               throw new IOException("Connection is null");
            }
         }
      } else {
         throw new IllegalStateException("Not connected");
      }
   }

   @Override
   public long seek(long where) throws IOException {
      if (where < 0) {
         where = 0;
      }

      if (where != 0) {
         return super._position;
      }

      this.disconnect();

      try {
         this.connect();
         this.start();
      } catch (IOException ioe) {
         this.disconnect();
         throw ioe;
      }

      super._position = 0;
      return super._position;
   }
}
