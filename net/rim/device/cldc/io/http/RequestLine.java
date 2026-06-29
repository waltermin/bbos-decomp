package net.rim.device.cldc.io.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.rim.device.api.io.http.HttpProtocolConstants;

public final class RequestLine implements HttpProtocolConstants {
   private String _method;
   private String _requestURI;
   private String _version;

   public RequestLine() {
   }

   public RequestLine(DataInputStream dins) {
      this.readFromStream(dins);
   }

   public final void setMethod(String method) throws IOException {
      if (!method.equals("OPTIONS")
         && !method.equals("GET")
         && !method.equals("POST")
         && !method.equals("HEAD")
         && !method.equals("PUT")
         && !method.equals("TRACE")
         && !method.equals("DELETE")
         && !method.equals("CONNECT")) {
         throw new IOException("The specified HTTP method is not supported.");
      }

      this._method = method;
   }

   public final void setRequestURI(String requestURI) {
      this._requestURI = requestURI;
   }

   public final void setVersion(String version) {
      this._version = version;
   }

   public final String getMethod() {
      return this._method;
   }

   public final String getRequestURI() {
      return this._requestURI;
   }

   public final String getVersion() {
      return this._version;
   }

   public final void readFromStream(DataInputStream dins) throws IOException {
      String lineString = Utilities.receiveLine(dins, true);
      String[] line = Utilities.processTransmissionLine(lineString);
      if (line[0] != null && line[1] != null && line[0].length() != 0 && line[1].length() != 0) {
         this.setMethod(line[0]);
         if (line[2] != null && line[2].length() != 0) {
            this.setRequestURI(line[1]);
            this.setVersion(line[2]);
         } else {
            this.setRequestURI("/");
            this.setVersion(line[1]);
         }
      } else {
         throw new IOException("Request line is invalid [" + lineString + ']');
      }
   }

   public final void writeToStream(DataOutputStream douts) {
      douts.write(this.toString().getBytes());
      douts.write("\r\n".getBytes());
   }

   @Override
   public final String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(this.getMethod());
      buffer.append(" ");
      if (this.getRequestURI() != null && this.getRequestURI().length() != 0) {
         buffer.append(this.getRequestURI());
      } else {
         buffer.append('/');
      }

      buffer.append(" ");
      buffer.append(this.getVersion());
      return buffer.toString();
   }
}
