package net.rim.device.api.mime;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.io.ScanLine;
import net.rim.device.api.io.SharedInputStream;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public class MIMEInputStream extends InputStream {
   private InputStream _input;
   private SharedInputStream _entirePart;
   private byte[] _buffer;
   private byte[] _boundary;
   private byte[] _contentEncoding;
   private byte[] _contentType;
   private Hashtable _headers;
   private MIMEInputStream[] _parts;
   private Hashtable _parameters;
   private int _startPosition;
   private int _endPosition;
   private boolean _readFinalBoundary;
   private boolean _readBoundary;
   private boolean _closed;
   private int _entirePartPresent;
   private static String MIME_VERSION = "MIME-Version";
   private static byte[] MIME_VERSION_BYTES = new byte[]{109, 105, 109, 101, 45, 118, 101, 114, 115, 105, 111, 110};
   private static byte[] CONTENT_BYTES = new byte[]{99, 111, 110, 116, 101, 110, 116, 45};
   private static String CONTENT_TYPE = "Content-Type";
   private static byte[] CONTENT_TYPE_BYTES = new byte[]{99, 111, 110, 116, 101, 110, 116, 45, 116, 121, 112, 101};
   private static String CONTENT_ENCODING = "Content-Transfer-Encoding";
   private static byte[] CONTENT_ENCODING_BYTES = new byte[]{
      99, 111, 110, 116, 101, 110, 116, 45, 116, 114, 97, 110, 115, 102, 101, 114, 45, 101, 110, 99, 111, 100, 105, 110, 103
   };
   private static String CONTENT_ID = "Content-ID";
   private static byte[] CONTENT_ID_BYTES = new byte[]{99, 111, 110, 116, 101, 110, 116, 45, 105, 100};
   private static String CONTENT_DESCRIPTION = "Content-Description";
   private static byte[] CONTENT_DESCRIPTION_BYTES = new byte[]{99, 111, 110, 116, 101, 110, 116, 45, 100, 101, 115, 99, 114, 105, 112, 116, 105, 111, 110};
   private static byte[] MULTIPART_BYTES = new byte[]{109, 117, 108, 116, 105, 112, 97, 114, 116};
   private static String BOUNDARY = "boundary";
   private static String BASE64 = "base64";
   private static byte[] BASE64_BYTES = new byte[]{98, 97, 115, 101, 54, 52};
   private static String QUOTED_PRINTABLE = "quoted-printable";
   private static byte[] QUOTED_PRINTABLE_BYTES = new byte[]{113, 117, 111, 116, 101, 100, 45, 112, 114, 105, 110, 116, 97, 98, 108, 101};
   private static final byte CR = 13;
   private static final byte LF = 10;

   public MIMEInputStream(InputStream input) {
      this(input, null, false, null);
   }

   private void saveHeader(byte[] headerLine) {
      String t = (String)(new Object(headerLine));
      int index = t.indexOf(58);
      if (index == -1) {
         this._headers.put(t, "");
      } else {
         String start = t.substring(0, index);
         index++;
         int length = t.length();

         while (index < length && t.charAt(index) == ' ') {
            index++;
         }

         this._headers.put(start, t.substring(index));
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   MIMEInputStream(InputStream input, byte[] boundary, boolean useStream, ScanLine scanLine) {
      if (input == null) {
         throw new Object();
      }

      try {
         byte[] line = null;
         this._headers = (Hashtable)(new Object());
         if (useStream) {
            if (!(input instanceof Object)) {
               throw new MIMEParsingException();
            }

            SharedInputStream stream = (SharedInputStream)input;
            this._input = stream;
         } else {
            this._input = SharedInputStream.getSharedInputStream(input);
         }

         this._parameters = (Hashtable)(new Object());
         this._boundary = null;
         this._contentEncoding = null;
         this._contentType = null;
         this._parts = null;
         SharedInputStream stream = (SharedInputStream)this._input;
         ScanLine _scanLine = null;
         if (scanLine == null) {
            _scanLine = (ScanLine)(new Object((SharedInputStream)this._input));
         } else {
            _scanLine = scanLine;
         }

         this._startPosition = stream.getCurrentPosition() - _scanLine.lengthUnreadData();
         this._endPosition = -1;
         line = _scanLine.readLine();
         byte[] headerLine = null;

         while (line.length > 0) {
            line = this.parseComments(line);
            if (this.startsWithIgnoreCase(line, MIME_VERSION_BYTES)) {
               int index = this.indexOf(line, 58, 0) + 1;
               if (index == 0 || index == line.length) {
                  throw new MIMEParsingException();
               }

               while (line[index] == 32) {
                  index++;
               }

               byte[] temp = this.subArray(line, index, line.length);
               if (temp.length >= 3 && (temp[0] != 49 || temp[1] != 46 || temp[2] != 48)) {
                  throw new MIMEParsingException();
               }

               this._headers.put(MIME_VERSION, new Object(temp));
            } else if (this.startsWithIgnoreCase(line, CONTENT_TYPE_BYTES)) {
               this.readParameters(line, _scanLine);
            } else if (this.startsWithIgnoreCase(line, CONTENT_ENCODING_BYTES)) {
               int index = this.indexOf(line, 58, 0) + 1;
               if (index == 0 || index == line.length) {
                  throw new MIMEParsingException();
               }

               while (line[index] == 32) {
                  index++;
               }

               this._contentEncoding = this.toLowerCase(this.trim(this.subArray(line, index, line.length)));
               this._headers.put(CONTENT_ENCODING, new Object(this._contentEncoding));
            } else if (this.startsWithIgnoreCase(line, CONTENT_ID_BYTES)) {
               int index = this.indexOf(line, 58, 0) + 1;
               if (index == 0 || index == line.length) {
                  throw new MIMEParsingException();
               }

               while (line[index] == 32) {
                  index++;
               }

               this._headers.put(CONTENT_ID, new Object(this.toLowerCase(this.trim(this.subArray(line, index, line.length)))));
            } else if (this.startsWithIgnoreCase(line, CONTENT_DESCRIPTION_BYTES)) {
               int index = this.indexOf(line, 58, 0) + 1;
               if (index == 0 || index == line.length) {
                  throw new MIMEParsingException();
               }

               while (line[index] == 32) {
                  index++;
               }

               this._headers.put(CONTENT_DESCRIPTION, new Object(this.toLowerCase(this.trim(this.subArray(line, index, line.length)))));
            } else if (this.startsWithIgnoreCase(line, CONTENT_BYTES)) {
               if (headerLine != null) {
                  this.saveHeader(headerLine);
                  byte[] var38 = null;
               }

               headerLine = new byte[line.length];
               System.arraycopy(line, 0, headerLine, 0, line.length);
            } else if (headerLine != null) {
               int oldsize = headerLine.length;
               Array.resize(headerLine, oldsize + 2 + line.length);
               headerLine[oldsize] = MIMEHeader.CRLF[0];
               headerLine[oldsize + 1] = MIMEHeader.CRLF[1];
               System.arraycopy(line, 0, headerLine, oldsize + 2, line.length);
            }

            line = _scanLine.readLine();
         }

         if (headerLine != null) {
            this.saveHeader(headerLine);
            byte[] var39 = null;
         }

         if (this._contentType != null && this.startsWithIgnoreCase(this._contentType, MULTIPART_BYTES)) {
            String boundaryString = (String)this._parameters.get(BOUNDARY);
            if (boundaryString == null) {
               throw new MIMEParsingException();
            }

            this._boundary = boundaryString.getBytes();
            this._parts = new MIMEInputStream[0];
            boolean finalBoundary = false;

            label563:
            try {
               try {
                  int jumpback = _scanLine.searchForBoundary(this._boundary);
                  finalBoundary = (jumpback & 1) != 0;
                  jumpback >>= 1;
                  int i = 0;

                  while (true) {
                     if (finalBoundary) {
                        this._entirePartPresent = 1;
                        break;
                     }

                     MIMEInputStream nextInnerPart = new MIMEInputStream(this._input, this._boundary, true, _scanLine);
                     Array.resize(this._parts, i + 1);
                     this._parts[i] = nextInnerPart;
                     if (nextInnerPart._readFinalBoundary) {
                        this._entirePartPresent = 1;
                        break;
                     }

                     if (!nextInnerPart._readBoundary) {
                        jumpback = _scanLine.searchForBoundary(this._boundary);
                        finalBoundary = (jumpback & 1) != 0;
                        jumpback >>= 1;
                     }

                     i++;
                  }
               } catch (MIMEParsingException var28) {
               }
            } finally {
               break label563;
            }

            stream = (SharedInputStream)this._input;
            int curPosition = stream.getCurrentPosition();
            this._endPosition = curPosition - _scanLine.lengthUnreadData();
            stream.setCurrentPosition(this._startPosition);
            this._entirePart = SharedInputStream.getSharedInputStream(stream);
            this._entirePart.setLength(this._endPosition - this._startPosition);
            stream.setCurrentPosition(curPosition);
         } else if (boundary == null) {
            this._entirePartPresent = -1;
            stream = (SharedInputStream)this._input;
            int currentPosition = stream.getCurrentPosition() - _scanLine.lengthUnreadData();
            stream.setCurrentPosition(this._startPosition);
            this._entirePart = SharedInputStream.getSharedInputStream(stream);
            stream.setCurrentPosition(currentPosition);
            if (this._contentEncoding != null) {
               if (Arrays.equals(this._contentEncoding, BASE64_BYTES)) {
                  this._input = (InputStream)(new Object(stream, true));
               } else if (Arrays.equals(this._contentEncoding, QUOTED_PRINTABLE_BYTES)) {
                  this._input = new QuotedPrintableInputStream(stream);
                  return;
               }
            }
         } else {
            if (!(this._input instanceof Object)) {
               throw new Object();
            }

            stream = (SharedInputStream)this._input;
            int currentPosition = stream.getCurrentPosition() - _scanLine.lengthUnreadData();
            SharedInputStream temp = stream.readInputStream();
            temp.setCurrentPosition(currentPosition);
            boolean var22 = false /* VF: Semaphore variable */;

            int curPosition;
            label548:
            try {
               var22 = true;
               int jumpback = _scanLine.searchForBoundary(boundary);
               this._readFinalBoundary = (jumpback & 1) != 0;
               this._readBoundary = true;
               jumpback >>= 1;
               curPosition = stream.getCurrentPosition();
               this._endPosition = curPosition - jumpback - 2;
               this._entirePartPresent = 1;
               var22 = false;
            } finally {
               if (var22) {
                  this._readFinalBoundary = false;
                  this._readBoundary = false;
                  curPosition = stream.getCurrentPosition();
                  this._endPosition = curPosition;
                  break label548;
               }
            }

            stream.setCurrentPosition(this._startPosition);
            this._entirePart = SharedInputStream.getSharedInputStream(stream);
            this._entirePart.setLength(this._endPosition - this._startPosition);
            stream.setCurrentPosition(curPosition);
            SharedInputStream tempInput = temp.readInputStream();
            tempInput.setLength(this._endPosition - currentPosition);
            if (this._contentEncoding != null) {
               if (Arrays.equals(this._contentEncoding, BASE64_BYTES)) {
                  this._input = (InputStream)(new Object(tempInput, true));
               } else if (Arrays.equals(this._contentEncoding, QUOTED_PRINTABLE_BYTES)) {
                  this._input = new QuotedPrintableInputStream(tempInput);
               } else {
                  this._input = tempInput;
               }
            } else {
               this._input = tempInput;
            }
         }
      } finally {
         return;
      }
   }

   public Enumeration getHeaders() {
      Vector temp = (Vector)(new Object());
      Enumeration keys = this._headers.keys();

      while (keys.hasMoreElements()) {
         String key = (String)keys.nextElement();
         String value = (String)this._headers.get(key);
         temp.addElement(((StringBuffer)(new Object())).append(key).append(": ").append(value).toString());
      }

      return temp.elements();
   }

   public String getHeader(String headerFieldName) {
      return (String)this._headers.get(headerFieldName);
   }

   public SharedInputStream getRawMIMEInputStream() {
      return this._entirePart;
   }

   public boolean isMultiPart() {
      return this._parts != null;
   }

   public int isPartComplete() {
      return this._entirePartPresent;
   }

   public MIMEInputStream[] getParts() {
      return this._parts;
   }

   public String getContentEncoding() {
      String contentEncoding = this.getHeader(CONTENT_ENCODING);
      if (contentEncoding == null) {
         return null;
      } else {
         return !contentEncoding.equals(BASE64) && !contentEncoding.equals("7bit") && !contentEncoding.equals(QUOTED_PRINTABLE) ? contentEncoding : null;
      }
   }

   public String getContentType() {
      String contentType = this.getHeader(CONTENT_TYPE);
      if (contentType == null) {
         return "text/plain";
      }

      int index = contentType.indexOf(59);
      return index == -1 ? contentType : contentType.substring(0, index);
   }

   public String getContentTypeParameter(String attribute) {
      if (attribute == null) {
         return null;
      }

      String lookup = attribute;
      if (lookup.charAt(0) != '"') {
         lookup = StringUtilities.toLowerCase(lookup, 1701707776);
      }

      return (String)this._parameters.get(lookup);
   }

   public String getContentID() {
      return this.getHeader(CONTENT_ID);
   }

   public String getContentDescription() {
      return this.getHeader(CONTENT_DESCRIPTION);
   }

   @Override
   public int read(byte[] buffer, int offset, int length) {
      if (this._closed) {
         throw new Object();
      } else if (offset < 0 || length < 0 || buffer.length - length < offset) {
         throw new Object();
      } else {
         return this.isMultiPart() && this._parts.length > 0 ? this._parts[0].read(buffer, offset, length) : this._input.read(buffer, offset, length);
      }
   }

   @Override
   public int read() {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      return this.read(this._buffer, 0, 1) < 0 ? -1 : this._buffer[0] & 0xFF;
   }

   @Override
   public int read(byte[] buffer) {
      return this.read(buffer, 0, buffer.length);
   }

   @Override
   public long skip(long n) {
      if (this._closed) {
         throw new Object();
      } else {
         return this.isMultiPart() && this._parts.length > 0 ? this._parts[0].skip(n) : this._input.skip(n);
      }
   }

   @Override
   public int available() {
      if (this._closed) {
         throw new Object();
      } else {
         return this.isMultiPart() && this._parts.length > 0 ? this._parts[0].available() : this._input.available();
      }
   }

   @Override
   public void close() {
      if (this._closed) {
         throw new Object();
      }

      this._closed = true;
      if (this.isMultiPart() && this._parts.length > 0) {
         this._parts[0].close();
      } else {
         this._input.close();
      }
   }

   private byte[] parseComments(byte[] line) {
      byte[] buffer = new byte[line.length];
      int i = 0;
      int dataLength = 0;
      boolean inComment = false;

      while (i < buffer.length) {
         if (inComment) {
            if (line[i] == 34) {
               int start = i;
               int index = this.indexOf(line, 34, i + 1);
               if (index == -1) {
                  throw new MIMEParsingException();
               }

               while (line[index - 1] == 92) {
                  index = this.indexOf(line, 34, index + 1);
               }

               i = Math.min(index + 1, line.length);
               System.arraycopy(line, start, buffer, dataLength, i - start);
               dataLength += i - start;
            } else if (line[i] == 41) {
               i++;
               inComment = false;
            } else {
               buffer[dataLength++] = line[i++];
            }
         } else if (line[i] != 34) {
            if (line[i] == 40) {
               inComment = true;
               i++;
            } else {
               buffer[dataLength++] = line[i++];
            }
         } else {
            int start = i;
            int index = this.indexOf(line, 34, i + 1);
            if (index == -1) {
               throw new MIMEParsingException();
            }

            while (line[index - 1] == 92) {
               index = this.indexOf(line, 34, index + 1);
            }

            i = Math.min(index + 1, line.length);
            System.arraycopy(line, start, buffer, dataLength, i - start);
            dataLength += i - start;
         }
      }

      return buffer;
   }

   private void readParameters(byte[] line, ScanLine scanLine) {
      boolean parametersLeft = false;
      byte[] parseLine = null;
      byte[] completeline = new byte[line.length];
      System.arraycopy(line, 0, completeline, 0, line.length);
      int index = this.indexOf(line, 58, 0) + 1;
      if (index != 0 && index != line.length) {
         parseLine = this.trim(this.subArray(line, index, line.length));
         index = this.indexOf(parseLine, 59, 0);
         if (index == -1) {
            this._contentType = this.toLowerCase(this.trim(this.subArray(parseLine, 0, parseLine.length)));
         } else {
            this._contentType = this.toLowerCase(this.trim(this.subArray(parseLine, 0, index)));
            parametersLeft = true;
         }

         byte[] attribute = null;
         byte[] value = null;

         while (parametersLeft) {
            parseLine = this.trim(this.subArray(parseLine, ++index, parseLine.length));
            if (parseLine == null || parseLine.length == 0) {
               parseLine = this.trim(this.parseComments(scanLine.readLine()));
               int oldsize = completeline.length;
               Array.resize(completeline, oldsize + 2 + parseLine.length);
               completeline[oldsize] = MIMEHeader.CRLF[0];
               completeline[oldsize + 1] = MIMEHeader.CRLF[1];
               System.arraycopy(parseLine, 0, completeline, oldsize + 2, parseLine.length);
            }

            index = this.indexOf(parseLine, 61, 0);
            if (index == -1) {
               throw new MIMEParsingException();
            }

            attribute = this.trim(this.subArray(parseLine, 0, index));
            if (attribute[0] != 34) {
               attribute = this.toLowerCase(attribute);
            } else {
               if (attribute[attribute.length - 1] != 34) {
                  throw new MIMEParsingException();
               }

               attribute = this.subArray(attribute, 1, attribute.length - 1);
            }

            parseLine = this.subArray(parseLine, index + 1, parseLine.length);
            index = this.indexOf(parseLine, 59, 0);
            if (index == -1) {
               value = this.trim(parseLine);
               parametersLeft = false;
            } else {
               value = this.trim(this.subArray(parseLine, 0, index));
            }

            if (value[0] != 34) {
               value = value;
            } else {
               if (value[value.length - 1] != 34) {
                  throw new MIMEParsingException();
               }

               value = this.subArray(value, 1, value.length - 1);
            }

            this._parameters.put(new Object(attribute), new Object(value));
         }

         index = this.indexOf(completeline, 58, 0) + 1;
         if (index != 0 && index != line.length) {
            this._headers.put(CONTENT_TYPE, new Object(this.toLowerCase(this.trim(this.subArray(completeline, index, completeline.length)))));
         } else {
            throw new MIMEParsingException();
         }
      } else {
         throw new MIMEParsingException();
      }
   }

   private boolean startsWithIgnoreCase(byte[] line, byte[] header) {
      if (line.length < header.length) {
         return false;
      }

      for (int i = 0; i < header.length; i++) {
         byte compare1 = line[i];
         byte compare2 = header[i];
         if (compare1 != compare2) {
            if (compare1 >= 65 && compare1 <= 90) {
               compare1 = (byte)(compare1 + 32);
            }

            if (compare1 != compare2) {
               return false;
            }
         }
      }

      return true;
   }

   private byte[] toLowerCase(byte[] data) {
      byte[] buffer = new byte[data.length];

      for (int i = 0; i < data.length; i++) {
         byte element = data[i];
         if (element >= 65 && element <= 90) {
            buffer[i] = (byte)(element - 65 + 97);
         } else {
            buffer[i] = element;
         }
      }

      return buffer;
   }

   private int indexOf(byte[] line, int element, int startPos) {
      if (startPos >= 0 && startPos < line.length) {
         for (int i = startPos; i < line.length; i++) {
            if (line[i] == element) {
               return i;
            }
         }

         return -1;
      } else {
         throw new Object();
      }
   }

   private byte[] trim(byte[] line) {
      int start = 0;
      int end = line.length;

      for (int i = 0; i < line.length && line[i] <= 32; i++) {
         start++;
      }

      if (start == line.length) {
         return null;
      }

      for (int i = line.length - 1; i >= 0 && line[i] <= 32; i--) {
         end--;
      }

      return Arrays.copy(line, start, end - start);
   }

   private byte[] subArray(byte[] line, int startIndex, int endIndex) {
      if (startIndex <= endIndex && startIndex >= 0 && endIndex <= line.length) {
         return Arrays.copy(line, startIndex, endIndex - startIndex);
      } else {
         throw new Object();
      }
   }
}
