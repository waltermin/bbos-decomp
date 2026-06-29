package net.rim.device.cldc.io.http;

import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.http.HttpProtocolConstants;

public final class StatusLine implements HttpProtocolConstants {
   private String _version;
   private String _status;
   private int _statusAsInt;
   private String _statusPhrase;
   private ResourceBundle _resources = ResourceBundle.getBundle(-6246750274064102835L, "net.rim.device.internal.resource.HTTP");

   public final String getVersion() {
      return this._version;
   }

   public final String getStatus() {
      return this._status;
   }

   public final int getStatusAsInt() {
      return this._statusAsInt;
   }

   public final String getStatusPhrase() {
      return this._statusPhrase;
   }

   public final void reset() {
      this._version = null;
      this._status = null;
      this._statusAsInt = 0;
      this._statusPhrase = null;
   }

   public final void setStatus(int code) {
      if (this._version == null) {
         this._version = "HTTP/1.1";
      }

      this._statusAsInt = code;
      this._status = Integer.toString(code);

      try {
         this._statusPhrase = this._resources.getString(this._statusAsInt);
      } finally {
         this._statusPhrase = "";
         return;
      }
   }

   public final void readFromStream(InputStream ins) {
      this.parseFromString(Utilities.receiveLine(ins, true));
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void parseFromString(String lineString) {
      String[] line = Utilities.processTransmissionLine(lineString);
      this._version = line[0];
      this._status = line[1];
      boolean var8 = false /* VF: Semaphore variable */;

      label58:
      try {
         var8 = true;
         this._statusAsInt = Integer.parseInt(this._status);
         var8 = false;
      } finally {
         if (var8) {
            this._statusAsInt = 0;
            break label58;
         }
      }

      this._statusPhrase = line[2];
      if (this._statusPhrase == null || this._statusPhrase.length() == 0) {
         try {
            this._statusPhrase = this._resources.getString(this._statusAsInt);
         } finally {
            this._statusPhrase = "";
            return;
         }
      }
   }

   public final void writeToStream(OutputStream outs) {
      outs.write(this._version.getBytes());
      outs.write(" ".getBytes());
      outs.write(this._status.getBytes());
      outs.write(" ".getBytes());
      outs.write(this._statusPhrase.getBytes());
      outs.write("\r\n".getBytes());
   }
}
