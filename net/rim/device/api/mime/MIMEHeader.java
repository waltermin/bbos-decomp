package net.rim.device.api.mime;

import java.io.OutputStream;
import java.util.Vector;
import net.rim.device.api.util.StringUtilities;

final class MIMEHeader {
   private String _contentType;
   private String _contentDescription;
   private String _contentID;
   private Vector _parameters;
   private String _encoding;
   private Vector _extensions;
   public static final String Version;
   public static final String ContentTypeHeader;
   public static final String ContentEncodingHeader;
   public static final String ContentIDHeader;
   public static final String ContentDescriptionHeader;
   public static final byte[] CRLF = new byte[]{13, 10};

   public MIMEHeader(String encoding) {
      if (encoding == null) {
         throw new Object();
      }

      this._parameters = (Vector)(new Object());
      this._encoding = StringUtilities.toLowerCase(encoding, 1701707776);
      this._extensions = (Vector)(new Object());
   }

   public final void setContentType(String contentType) {
      this._contentType = contentType;
   }

   public final void setContentDescription(String contentDescription) {
      this._contentDescription = contentDescription;
   }

   public final void addContentTypeParameter(String attribute, String value) {
      this._parameters.addElement(new Parameter(attribute, value));
   }

   public final void setContentID(String contentID) {
      this._contentID = contentID;
   }

   public final void addHeaderField(String headerField) {
      if (headerField != null) {
         if (StringUtilities.startsWithIgnoreCase(headerField, "Content-", 1701707776)) {
            this._extensions.addElement(headerField);
         }
      }
   }

   public final void writeHeader(OutputStream out, boolean writeVersion) {
      if (writeVersion) {
         out.write("MIME-Version: 1.0".getBytes());
         out.write(CRLF);
      }

      if (this._contentType != null) {
         out.write("Content-Type".getBytes());
         out.write(58);
         out.write(32);
         out.write(this._contentType.getBytes());
         int size = this._parameters.size();

         for (int i = 0; i < size; i++) {
            out.write(59);
            out.write(CRLF);
            out.write("    ".getBytes());
            Parameter param = (Parameter)this._parameters.elementAt(i);
            out.write(param.getAttribute().getBytes());
            out.write(61);
            out.write(param.getValue().getBytes());
         }

         out.write(CRLF);
      }

      out.write("Content-Transfer-Encoding".getBytes());
      out.write(58);
      out.write(32);
      out.write(this._encoding.getBytes());
      out.write(CRLF);
      if (this._contentID != null) {
         out.write("Content-ID".getBytes());
         out.write(58);
         out.write(32);
         out.write(this._contentID.getBytes());
         out.write(CRLF);
      }

      if (this._contentDescription != null) {
         out.write("Content-Description".getBytes());
         out.write(58);
         out.write(32);
         out.write(this._contentDescription.getBytes());
         out.write(CRLF);
      }

      int size = this._extensions.size();

      for (int i = 0; i < size; i++) {
         out.write(((String)this._extensions.elementAt(i)).getBytes());
         out.write(CRLF);
      }

      out.write(CRLF);
   }
}
