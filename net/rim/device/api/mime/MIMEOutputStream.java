package net.rim.device.api.mime;

import java.io.OutputStream;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.io.SharedOutputStream;
import net.rim.device.api.util.StringUtilities;

public class MIMEOutputStream extends OutputStream {
   private String _boundary;
   private String _topBoundary;
   private boolean _isMultiPart;
   private boolean _writeVersion;
   private boolean _canonicalizeLineEndings;
   private boolean _foundCR;
   private int _counter;
   private byte[] _buffer;
   private SharedOutputStream _sharedOutputStream;
   private OutputStream _out;
   private NoCopyByteArrayOutputStream _bufferOut;
   private MIMEHeader _mimeHeader;
   private boolean _closed;
   private static final byte CR = 13;
   private static final byte LF = 10;
   public static final String ENCODING_7BIT = "7bit";
   public static final String ENCODING_8BIT = "8bit";
   public static final String ENCODING_BINARY = "binary";
   public static final String ENCODING_BASE64 = "base64";

   public MIMEOutputStream(OutputStream out, boolean isMultiPart, String encoding) {
      this(out, isMultiPart, encoding, true, 0, null);
   }

   MIMEOutputStream(OutputStream out, boolean isMultiPart, String encoding, boolean writeVersion, int counter, String boundary) {
      this._counter = counter;
      this._isMultiPart = isMultiPart;
      this._topBoundary = boundary;
      this._writeVersion = writeVersion;
      if (encoding == null) {
         encoding = "7bit";
      }

      if (!StringUtilities.strEqualIgnoreCase(encoding, "binary", 1701707776)) {
         this._canonicalizeLineEndings = true;
      }

      this._mimeHeader = new MIMEHeader(encoding);
      if (this._isMultiPart) {
         this._sharedOutputStream = (SharedOutputStream)(new Object(out));
         this._out = this._sharedOutputStream.getOutputStream();
         long now = System.currentTimeMillis();
         String b = ((StringBuffer)(new Object("\"----RIM_"))).append(this._counter).append('_').append(now).append("_1978_08_20\"").toString();
         this._mimeHeader.setContentType("multipart/mixed");
         this._mimeHeader.addContentTypeParameter("boundary", b);
         this._boundary = b.substring(1, b.length() - 1);
      } else {
         this._out = out;
         this._mimeHeader.setContentType("text/plain");
         this._bufferOut = (NoCopyByteArrayOutputStream)(new Object());
      }
   }

   public void setContentType(String contentType) {
      if (this._isMultiPart && !StringUtilities.startsWithIgnoreCase(contentType, "multipart", 1701707776)) {
         throw new Object();
      }

      if (contentType != null) {
         this._mimeHeader.setContentType(contentType);
      }
   }

   public void setContentDescription(String contentDescription) {
      this._mimeHeader.setContentDescription(contentDescription);
   }

   public void addContentTypeParameter(String attribute, String value) {
      this._mimeHeader.addContentTypeParameter(attribute, value);
   }

   public void setContentID(String contentID) {
      this._mimeHeader.setContentID(contentID);
   }

   public void addHeaderField(String headerField) {
      this._mimeHeader.addHeaderField(headerField);
   }

   public MIMEOutputStream getPartOutputStream(boolean isMultiPart, String encoding) {
      if (this._closed) {
         throw new Object();
      } else if (this._isMultiPart) {
         return new MIMEOutputStream(this._sharedOutputStream.getOutputStream(), isMultiPart, encoding, false, this._counter + 1, this._boundary);
      } else {
         throw new Object();
      }
   }

   void writeBoundary(String boundary) {
      this._out.write(MIMEHeader.CRLF);
      this._out.write("--".getBytes());
      this._out.write(boundary.getBytes());
      this._out.write(MIMEHeader.CRLF);
   }

   void writeEndBoundary(OutputStream out) {
      out.write(MIMEHeader.CRLF);
      out.write("--".getBytes());
      out.write(this._boundary.getBytes());
      out.write("--".getBytes());
      out.write(MIMEHeader.CRLF);
   }

   @Override
   public void write(int data) {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      this._buffer[0] = (byte)data;
      this.write(this._buffer, 0, 1);
   }

   @Override
   public void write(byte[] data) {
      this.write(data, 0, data.length);
   }

   @Override
   public void write(byte[] buffer, int offset, int length) {
      if (this._closed) {
         throw new Object();
      }

      if (this._isMultiPart) {
         throw new Object();
      }

      if (offset >= 0 && length >= 0 && buffer.length - length >= offset) {
         int start = offset;
         if (this._canonicalizeLineEndings) {
            for (int i = offset; i < offset + length; i++) {
               if (this._foundCR) {
                  if (buffer[i] != 10) {
                     this._bufferOut.write(buffer, start, i - start);
                     this._bufferOut.write(10);
                     start = i;
                  }

                  this._foundCR = false;
               } else if (buffer[i] == 10) {
                  this._bufferOut.write(buffer, start, i - start);
                  this._bufferOut.write(13);
                  start = i;
               }

               if (buffer[i] == 13) {
                  this._foundCR = true;
               }
            }
         }

         if (start < offset + length) {
            this._bufferOut.write(buffer, start, offset + length - start);
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public void flush() {
      if (this._closed) {
         throw new Object();
      }

      this._out.flush();
   }

   @Override
   public void close() {
      if (this._closed) {
         throw new Object();
      }

      this._closed = true;
      if (this._isMultiPart) {
         if (this._topBoundary != null) {
            this.writeBoundary(this._topBoundary);
         }

         this._mimeHeader.writeHeader(this._out, this._writeVersion);
         OutputStream tempOut = this._sharedOutputStream.getOutputStream();
         this.writeEndBoundary(tempOut);
         tempOut.close();
      } else {
         if (this._topBoundary != null) {
            this.writeBoundary(this._topBoundary);
         }

         this._mimeHeader.writeHeader(this._out, this._writeVersion);
         this._out.write(this._bufferOut.getByteArray(), 0, this._bufferOut.size());
      }

      if (this._foundCR) {
         this._out.write(10);
      }

      this._out.close();
      if (this._sharedOutputStream != null) {
         this._sharedOutputStream.close();
      }
   }
}
