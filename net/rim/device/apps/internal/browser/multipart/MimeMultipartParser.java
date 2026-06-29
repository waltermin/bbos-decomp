package net.rim.device.apps.internal.browser.multipart;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import net.rim.device.api.io.Base64InputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.ByteVector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;

public final class MimeMultipartParser {
   private InputStream _in;
   private String _boundary;
   private ByteVector _bodyPartBuffer;
   private boolean _endOfPart;
   private boolean _closeDelimiterSeen;
   private HttpHeaders _currentPartHeaders;
   private boolean _base64decode;
   private int _boundaryState = 2;
   private boolean _delimiterHadCR = true;
   private static final int BEFORE_CRLF_DASH_BOUNDARY = 0;
   private static final int BEFORE_LF_DASH_BOUNDARY = 1;
   private static final int BEFORE_DASH_BOUNDARY = 2;
   private static final int BEFORE_HYPHEN_BOUNDARY = 3;
   private static final int READING_BOUNDARY = 4;
   private static final int AFTER_BOUNDARY = 5;
   private static final int AFTER_BOUNDARY_HYPHEN = 6;
   private static final int READING_TO_END_OF_BOUNDARY_LINE = 7;
   private static final byte CR = 13;
   private static final byte LF = 10;
   private static final byte HYPHEN = 45;

   public MimeMultipartParser(InputStream inputStream, String contentType) {
      if (contentType != null) {
         StringTokenizer tokenizer = new StringTokenizer(contentType, ';');
         if (tokenizer.hasMoreTokens()) {
            tokenizer.nextToken();
            String boundaryParameterName = "boundary";

            while (tokenizer.hasMoreTokens()) {
               String token = tokenizer.nextToken().trim();
               int equals = token.indexOf(61);
               if (equals > 0) {
                  String parameterName = token.substring(0, equals);
                  if (StringUtilities.strEqualIgnoreCase(boundaryParameterName, parameterName, 1701707776)) {
                     this._boundary = unquote(token.substring(equals + 1));
                  }
               }
            }
         }
      }

      this._in = inputStream;
   }

   public final HttpHeaders getPartHeaders() {
      if (this._currentPartHeaders == null) {
         HttpHeaders headers = new HttpHeaders();
         headers.readFromStream(new DataInputStream(new MimeMultipartParser$PartInputStream(this)));
         if (headers.getPropertyValue("Content-Type") == null) {
            headers.addProperty("Content-Type", "text/plain");
         }

         String contentTransferEncoding = headers.getPropertyValue("Content-Transfer-Encoding");
         if (contentTransferEncoding != null && StringUtilities.strEqualIgnoreCase(contentTransferEncoding, "base64", 1701707776)) {
            this._base64decode = true;
            headers.removeProperties("Content-Transfer-Encoding");
         }

         this._currentPartHeaders = headers;
      }

      return this._currentPartHeaders;
   }

   public final InputStream getPartContent() throws EOFException {
      if (this._closeDelimiterSeen) {
         throw new EOFException();
      }

      this.getPartHeaders();
      InputStream partInputStream = new MimeMultipartParser$PartInputStream(this);
      return this._base64decode ? new Base64InputStream(partInputStream, false) : partInputStream;
   }

   public final boolean nextPart() {
      if (!this._endOfPart) {
         while (this.read() != -1) {
         }
      }

      if (this._closeDelimiterSeen) {
         return false;
      }

      this._endOfPart = false;
      this._currentPartHeaders = null;
      this._base64decode = false;
      this._bodyPartBuffer.removeAllElements();
      return true;
   }

   final int read() throws EOFException {
      if (!this._endOfPart && (this._bodyPartBuffer == null || this._bodyPartBuffer.size() == 0)) {
         int boundaryIndex = 0;
         boolean readNewByte = true;
         int currentByte = -1;

         label176:
         while (!readNewByte || this._bodyPartBuffer == null || this._bodyPartBuffer.size() == 0) {
            if (readNewByte) {
               currentByte = this._in.read();
               if (currentByte == -1) {
                  throw new EOFException();
               }
            }

            readNewByte = true;
            switch (this._boundaryState) {
               case -1:
                  throw new IllegalStateException();
               case 0:
               default:
                  if (currentByte == 13) {
                     this._delimiterHadCR = true;
                     this._boundaryState = 1;
                  } else if (currentByte == 10) {
                     this._delimiterHadCR = false;
                     this._boundaryState = 2;
                  } else if (this._bodyPartBuffer != null) {
                     this._bodyPartBuffer.addElement((byte)currentByte);
                  }
                  break;
               case 1:
                  if (currentByte == 10) {
                     this._boundaryState = 2;
                  } else {
                     if (this._bodyPartBuffer != null && this._delimiterHadCR) {
                        this._bodyPartBuffer.addElement((byte)13);
                     }

                     this._boundaryState = 0;
                     readNewByte = false;
                  }
                  break;
               case 2:
                  if (currentByte == 45) {
                     this._boundaryState = 3;
                  } else {
                     if (this._bodyPartBuffer != null) {
                        if (this._delimiterHadCR) {
                           this._bodyPartBuffer.addElement((byte)13);
                        }

                        this._bodyPartBuffer.addElement((byte)10);
                     }

                     this._boundaryState = 0;
                     readNewByte = false;
                  }
                  break;
               case 3:
                  if (currentByte == 45) {
                     this._boundaryState = 4;
                     boundaryIndex = 0;
                  } else {
                     if (this._bodyPartBuffer != null) {
                        if (this._delimiterHadCR) {
                           this._bodyPartBuffer.addElement((byte)13);
                        }

                        this._bodyPartBuffer.addElement((byte)10);
                        this._bodyPartBuffer.addElement((byte)45);
                     }

                     this._boundaryState = 0;
                     readNewByte = false;
                  }
                  break;
               case 4:
                  if (this._boundary != null) {
                     if (boundaryIndex < this._boundary.length() && this._boundary.charAt(boundaryIndex) == currentByte) {
                        if (++boundaryIndex >= this._boundary.length()) {
                           this._boundaryState = 5;
                        }
                     } else {
                        if (this._bodyPartBuffer != null) {
                           if (this._delimiterHadCR) {
                              this._bodyPartBuffer.addElement((byte)13);
                           }

                           this._bodyPartBuffer.addElement((byte)10);
                           this._bodyPartBuffer.addElement((byte)45);
                           this._bodyPartBuffer.addElement((byte)45);
                           if (boundaryIndex > 0) {
                              int boundaryLength = this._boundary.length();

                              for (int i = 0; i < boundaryIndex && i < boundaryLength; i++) {
                                 this._bodyPartBuffer.addElement((byte)this._boundary.charAt(i));
                              }
                           }
                        }

                        this._boundaryState = 0;
                        readNewByte = false;
                     }
                     break;
                  } else {
                     StringBuffer boundaryBuffer = new StringBuffer();

                     while (currentByte != 13) {
                        if (currentByte == 10) {
                           readNewByte = false;
                           break;
                        }

                        boundaryBuffer.append((char)currentByte);
                        currentByte = this._in.read();
                        if (currentByte == -1) {
                           throw new EOFException();
                        }
                     }

                     int i = boundaryBuffer.length() - 1;

                     while (true) {
                        if (i > 0) {
                           char ch = boundaryBuffer.charAt(i);
                           if (ch == ' ' || ch == '\t' || ch == '\f') {
                              boundaryBuffer.setLength(i);
                              i--;
                              continue;
                           }
                        }

                        this._boundary = boundaryBuffer.toString();
                        this._boundaryState = 7;
                        continue label176;
                     }
                  }
               case 5:
                  if (currentByte == 45) {
                     this._boundaryState = 6;
                  } else {
                     this._boundaryState = 7;
                     readNewByte = false;
                  }
                  break;
               case 6:
                  if (currentByte == 45) {
                     this._endOfPart = true;
                     this._closeDelimiterSeen = true;
                     break label176;
                  }

                  this._boundaryState = 7;
                  readNewByte = false;
                  break;
               case 7:
                  if (currentByte == 10) {
                     this._boundaryState = 0;
                     if (this._bodyPartBuffer != null) {
                        this._endOfPart = true;
                        break label176;
                     }

                     this._bodyPartBuffer = new ByteVector();
                  }
            }
         }
      }

      if (this._bodyPartBuffer != null && this._bodyPartBuffer.size() > 0) {
         int nextByte = this._bodyPartBuffer.firstElement() & 255;
         this._bodyPartBuffer.removeElementAt(0);
         return nextByte;
      } else {
         return -1;
      }
   }

   private static final String unquote(String value) {
      if (value == null) {
         return null;
      } else {
         return value.length() >= 2 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"' ? value.substring(1, value.length() - 1) : value;
      }
   }
}
