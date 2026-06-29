package net.rim.device.apps.internal.iota;

import java.io.DataInputStream;
import java.io.InputStream;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.Array;

public final class MimeMultipart {
   private BodyPart[] _bodyParts;
   private int _rootBodyPartIndex;
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

   public MimeMultipart(InputStream is, String contentType) {
      String boundary = null;
      String start = null;
      StringTokenizer tokenizer = (StringTokenizer)(new Object(contentType, ';'));
      if (tokenizer.hasMoreTokens()) {
         tokenizer.nextToken();
         String boundaryParameterName = "boundary";
         String startParameterName = "start";

         while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            int equals = token.indexOf(61);
            if (equals > 0) {
               String parameterName = token.substring(0, equals);
               if (StringUtilities.strEqualIgnoreCase(boundaryParameterName, parameterName, 1701707776)) {
                  boundary = unquote(token.substring(equals + 1));
               } else if (StringUtilities.strEqualIgnoreCase(startParameterName, parameterName, 1701707776)) {
                  start = unquote(token.substring(equals + 1));
               }
            }
         }
      }

      if (boundary == null) {
         throw new Object("Content-Type is missing boundary");
      }

      int boundaryLength = boundary.length();
      int boundaryIndex = 0;
      int currentByte = -1;
      boolean readNewByte = true;
      BodyPart bodyPart = null;
      int boundaryState = 2;

      while (true) {
         if (readNewByte) {
            currentByte = is.read();
            if (currentByte == -1) {
               throw new Object();
            }
         }

         readNewByte = true;
         switch (boundaryState) {
            case -1:
               throw new Object();
            case 0:
            default:
               if (currentByte == 13) {
                  boundaryState = 1;
               } else if (bodyPart != null) {
                  bodyPart.append((byte)currentByte);
               }
               break;
            case 1:
               if (currentByte == 10) {
                  boundaryState = 2;
               } else {
                  if (bodyPart != null) {
                     bodyPart.append((byte)13);
                  }

                  boundaryState = 0;
                  readNewByte = false;
               }
               break;
            case 2:
               if (currentByte == 45) {
                  boundaryState = 3;
               } else {
                  if (bodyPart != null) {
                     bodyPart.append((byte)13);
                     bodyPart.append((byte)10);
                  }

                  boundaryState = 0;
                  readNewByte = false;
               }
               break;
            case 3:
               if (currentByte == 45) {
                  boundaryState = 4;
                  boundaryIndex = 0;
               } else {
                  if (bodyPart != null) {
                     bodyPart.append((byte)13);
                     bodyPart.append((byte)10);
                     bodyPart.append((byte)45);
                  }

                  boundaryState = 0;
                  readNewByte = false;
               }
               break;
            case 4:
               if (boundaryIndex < boundaryLength && boundary.charAt(boundaryIndex) == currentByte) {
                  if (++boundaryIndex >= boundaryLength) {
                     boundaryState = 5;
                  }
                  break;
               }

               if (bodyPart != null && boundaryIndex > 0) {
                  bodyPart.append(boundary.substring(0, boundaryIndex).getBytes());
               }

               boundaryState = 0;
               readNewByte = false;
               break;
            case 5:
               if (currentByte == 45) {
                  boundaryState = 6;
               } else {
                  boundaryState = 7;
                  readNewByte = false;
               }
               break;
            case 6:
               if (currentByte == 45) {
                  return;
               }

               boundaryState = 7;
               readNewByte = false;
               break;
            case 7:
               if (currentByte == 10) {
                  HttpHeaders headers = (HttpHeaders)(new Object());
                  headers.readFromStream((DataInputStream)(new Object(is)));
                  bodyPart = new BodyPart(headers);
                  if (this._bodyParts == null) {
                     this._bodyParts = new BodyPart[1];
                  } else {
                     Array.resize(this._bodyParts, this._bodyParts.length + 1);
                  }

                  this._bodyParts[this._bodyParts.length - 1] = bodyPart;
                  if (start != null && start.equals(headers.getPropertyValue("Content-Id"))) {
                     this._rootBodyPartIndex = this._bodyParts.length - 1;
                     start = null;
                  }

                  boundaryState = 0;
               }
         }
      }
   }

   private static final String unquote(String value) {
      if (value == null) {
         return null;
      } else {
         return value.length() >= 2 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"' ? value.substring(1, value.length() - 1) : value;
      }
   }

   public final BodyPart getBodyPart(String contentID) {
      if (this._bodyParts != null) {
         for (int i = this._bodyParts.length - 1; i >= 0; i--) {
            BodyPart bodyPart = this._bodyParts[i];
            if (contentID.equals(bodyPart.getContentID())) {
               return bodyPart;
            }
         }
      }

      return null;
   }

   public final BodyPart getRootBodyPart() {
      return this._bodyParts != null ? this._bodyParts[this._rootBodyPartIndex] : null;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final String cidURLtoContentID(String cidURL) {
      StringBuffer buffer = (StringBuffer)(new Object(cidURL.length()));
      buffer.append('<');
      int urlLength = cidURL.length();

      for (int i = 4; i < urlLength; i++) {
         char ch = cidURL.charAt(i);
         if (ch == '%' && i + 2 < urlLength) {
            char digit1 = cidURL.charAt(++i);
            char digit2 = cidURL.charAt(++i);
            boolean var10 = false /* VF: Semaphore variable */;

            try {
               var10 = true;
               int nfe = NumberUtilities.hexDigitToInt(digit1);
               int h0 = NumberUtilities.hexDigitToInt(digit2);
               buffer.append((char)(nfe << 4 | h0));
               var10 = false;
            } finally {
               if (var10) {
                  buffer.append(ch);
                  buffer.append(digit1);
                  buffer.append(digit2);
                  continue;
               }
            }
         } else {
            buffer.append(ch);
         }
      }

      buffer.append('>');
      return buffer.toString();
   }
}
