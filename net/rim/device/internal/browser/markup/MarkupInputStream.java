package net.rim.device.internal.browser.markup;

import com.sun.cldc.i18n.Helper;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.browser.util.Pipe;

public class MarkupInputStream extends InputStream {
   private InputStream _primaryInput;
   private int _type;
   private MarkupContext _context;
   private Pipe _pipe = new Pipe();
   private int _currentPacketNo;
   private String _encoding;
   private Reader _reader;
   private Object _unconsumedData;
   private boolean _setReader = true;
   private InputStream _pipeInput = this._pipe.getInputStream();
   private static final int READ_BUFFER_SIZE;
   private static final int MARKUP_BEFORE;
   private static final int MARKUP_AFTER;
   public static final int CONFIG_DATA_JAVASCRIPT_ENABLED_INDEX;
   public static final int CONFIG_DATA_CSS_ENABLED_INDEX;
   public static final int CONFIG_DATA_SIZE;
   private static final int UNKNOWN;
   private static final int HTML;
   private static final int TEXT;
   private static final int WML_1x_TEXT;
   private static final int WBXML;
   static final int SINGLE_UNIT;
   static final int UTF8;

   public static MarkupInputStream getConvertedInputStream(
      String type,
      String encoding,
      String defaultEncoding,
      InputStream bytes,
      int[] actionStateTable,
      byte[] configurationData,
      String[] tags,
      int[] tagIds,
      int[] tagIndicies,
      String[] entities,
      int[] entityIds,
      int[] entityIndicies,
      String[] attributes,
      int[] attributeIds,
      int[] attributeIndicies,
      String[] attributeValues,
      int[] attributeValueIds,
      int[] attributeValueIndicies
   ) {
      int intType = 0;
      if (StringUtilities.strEqualIgnoreCase(type, "text/html", 1701707776)
         || StringUtilities.strEqualIgnoreCase(type, "application/vnd.wap.xhtml+xml", 1701707776)
         || StringUtilities.strEqualIgnoreCase(type, "application/xhtml+xml", 1701707776)
         || StringUtilities.strEqualIgnoreCase(type, "application/vnd.wap.wml+xml", 1701707776)) {
         intType = 1;
      } else if (StringUtilities.strEqualIgnoreCase(type, "text/plain", 1701707776)) {
         intType = 2;
      } else if (StringUtilities.strEqualIgnoreCase(type, "text/vnd.wap.wml", 1701707776)) {
         intType = 3;
      } else if (StringUtilities.strEqualIgnoreCase(type, "text/vnd.wap.si", 1701707776)
         || StringUtilities.strEqualIgnoreCase(type, "text/vnd.wap.sl", 1701707776)
         || StringUtilities.strEqualIgnoreCase(type, "text/vnd.wap.co", 1701707776)) {
         intType = 4;
      }

      boolean guessEncoding = false;
      int intEncoding = 1;
      Reader reader = null;
      if (encoding != null) {
         if (StringUtilities.compareToIgnoreCase(encoding, "iso-8859-1", 1701707776) == 0
            || StringUtilities.compareToIgnoreCase(encoding, "us-ascii", 1701707776) == 0) {
            intEncoding = 1;
         } else if (StringUtilities.compareToIgnoreCase(encoding, "utf-8", 1701707776) == 0) {
            intEncoding = 2;
         } else {
            try {
               reader = Helper.getStreamReader(bytes, encoding);
            } catch (UnsupportedEncodingException e) {
               guessEncoding = true;
            }
         }
      } else {
         guessEncoding = true;
         if (defaultEncoding != null) {
            if (StringUtilities.compareToIgnoreCase(defaultEncoding, "iso-8859-1", 1701707776) == 0
               || StringUtilities.compareToIgnoreCase(defaultEncoding, "us-ascii", 1701707776) == 0) {
               intEncoding = 1;
            } else if (StringUtilities.compareToIgnoreCase(defaultEncoding, "utf-8", 1701707776) == 0) {
               intEncoding = 2;
            } else {
               try {
                  reader = Helper.getStreamReader(bytes, defaultEncoding);
               } catch (UnsupportedEncodingException var23) {
               }
            }
         }
      }

      if (!bytes.markSupported()) {
         guessEncoding = false;
      } else {
         bytes.mark(Integer.MAX_VALUE);
      }

      MarkupContext context = new MarkupContext();
      context._actionStateTable = actionStateTable;
      context._attributeIds = attributeIds;
      context._attributeIndicies = attributeIndicies;
      context._attributes = attributes;
      context._attributeValueIds = attributeValueIds;
      context._attributeValueIndicies = attributeValueIndicies;
      context._attributeValues = attributeValues;
      context._configurationData = configurationData;
      context._entities = entities;
      context._entityIds = entityIds;
      context._entityIndicies = entityIndicies;
      context._tagIds = tagIds;
      context._tagIndicies = tagIndicies;
      context._tags = tags;
      context._inputEncoding = intEncoding;
      context._guessEncoding = guessEncoding;
      return new MarkupInputStream(intType, bytes, context, reader, encoding);
   }

   public MarkupInputStream(int type, InputStream bytes, MarkupContext context, Reader reader, String encoding) {
      this._type = type;
      this._primaryInput = bytes;
      this._context = context;
      this._reader = reader;
      this._encoding = encoding;
   }

   public Pipe getPipe() {
      return this._pipe;
   }

   public InputStream getPrimaryInput() {
      return this._primaryInput;
   }

   @Override
   public int read() {
      return this._pipeInput.available() <= 0 && !this.readNextChunk() ? -1 : this._pipeInput.read();
   }

   @Override
   public int read(byte[] b, int offset, int length) {
      if (length == 0) {
         return 0;
      }

      int bytesRead = 0;
      int available = 0;

      while (length > 0) {
         available = this._pipeInput.available();
         if (available <= 0 && !this.readNextChunk()) {
            if (bytesRead > 0) {
               return bytesRead;
            }

            return -1;
         }

         int numRead = Math.min(available, length);
         numRead = this._pipeInput.read(b, offset, numRead);
         offset += numRead;
         length -= numRead;
         bytesRead += numRead;
      }

      return bytesRead > 0 ? bytesRead : -1;
   }

   @Override
   public int available() {
      return this._primaryInput.available() + this._pipeInput.available();
   }

   @Override
   public boolean markSupported() {
      return this._pipeInput.markSupported();
   }

   @Override
   public void mark(int limit) {
      this._pipeInput.mark(limit);
   }

   @Override
   public void reset() {
      this._pipeInput.reset();
   }

   @Override
   public void close() {
      this._pipe.closeWrite();
      this._primaryInput.close();
   }

   private boolean readNextChunk() {
      while (true) {
         byte[] currentChunk = new byte[0];
         int numToRead = 1024;
         int bytesOffset = 0;
         Object bytes;
         int originalNumRead;
         if (this._reader != null) {
            bytes = new char[1024];
            if (this._unconsumedData != null) {
               System.arraycopy((char[])this._unconsumedData, 0, (char[])bytes, 0, ((char[])this._unconsumedData).length);
               numToRead -= ((char[])this._unconsumedData).length;
               bytesOffset += ((char[])this._unconsumedData).length;
            }

            originalNumRead = this._reader.read((char[])bytes, bytesOffset, numToRead);
         } else {
            bytes = new byte[1024];
            if (this._unconsumedData != null) {
               System.arraycopy((byte[])this._unconsumedData, 0, (byte[])bytes, 0, ((byte[])this._unconsumedData).length);
               numToRead -= ((byte[])this._unconsumedData).length;
               bytesOffset += ((byte[])this._unconsumedData).length;
            }

            originalNumRead = this._primaryInput.read((byte[])bytes, bytesOffset, numToRead);
         }

         this._unconsumedData = null;
         this._context._currentEndReadPosition = 0;
         if (originalNumRead == -1) {
            this._pipe.closeWrite();
            return false;
         }

         if (this._primaryInput.markSupported()
            && this._context._firstBlock
            && this._context._guessEncoding
            && bytes instanceof byte[]
            && ((byte[])bytes).length > 6) {
            byte[] encodingTypeFound = null;
            byte[] data = (byte[])bytes;
            if (data[0] == 60 && data[1] == 63 && data[2] == 120 && data[3] == 109 && data[4] == 108 && data[5] == 32) {
               int size = data.length;
               int state = 1;

               for (int i = 5; i < size; i++) {
                  switch (data[i]) {
                     case 32:
                        if (state != 9 && state != 10) {
                           state = 1;
                        }
                        break;
                     case 34:
                     case 39:
                        if (state == 10) {
                           byte searchChar = data[i];
                           int beginIndex = ++i;

                           while (i < size) {
                              if (data[i] == searchChar) {
                                 encodingTypeFound = Arrays.copy(data, beginIndex, i - beginIndex);
                                 i = size;
                              }

                              i++;
                           }
                           break;
                        }

                        state = 0;
                        break;
                     case 61:
                        if (state == 9) {
                           state = 10;
                        } else {
                           state = 0;
                        }
                        break;
                     case 99:
                        if (state == 3) {
                           state = 4;
                        } else {
                           state = 0;
                        }
                        break;
                     case 100:
                        if (state == 5) {
                           state = 6;
                        } else {
                           state = 0;
                        }
                        break;
                     case 101:
                        if (state == 1) {
                           state = 2;
                        } else {
                           state = 0;
                        }
                        break;
                     case 103:
                        if (state == 8) {
                           state = 9;
                        } else {
                           state = 0;
                        }
                        break;
                     case 105:
                        if (state == 6) {
                           state = 7;
                        } else {
                           state = 0;
                        }
                        break;
                     case 110:
                        if (state == 2) {
                           state = 3;
                        } else if (state == 7) {
                           state = 8;
                        } else {
                           state = 0;
                        }
                        break;
                     case 111:
                        if (state == 4) {
                           state = 5;
                        } else {
                           state = 0;
                        }
                        break;
                     default:
                        state = 0;
                  }
               }
            }

            if (encodingTypeFound != null) {
               this.setNewEncoding(encodingTypeFound);
               continue;
            }
         }

         originalNumRead += bytesOffset;
         if (originalNumRead < 1024) {
            this._context._lastBlock = true;
         }

         EventLogger.logEvent(1907089860548946979L, 1114465378, 5);

         int numRead;
         try {
            numRead = markupDataChunk(this._type, bytes, originalNumRead, currentChunk, this._context);
         } catch (IllegalArgumentException e) {
            if (this._type == 3) {
               throw new MarkupWrongMIMEType("text/html");
            }

            throw e;
         }

         EventLogger.logEvent(1907089860548946979L, 1114465377, 5);
         if (this._primaryInput.markSupported() && this._setReader && this._context._newEncodingType != null) {
            this.setNewEncoding(this._context._newEncodingType);
         } else {
            this._context._firstBlock = false;
            if (this._context._currentEndReadPosition < originalNumRead) {
               if (originalNumRead - this._context._currentEndReadPosition > 4) {
                  throw new IOException();
               }

               if (this._reader != null) {
                  this._unconsumedData = new char[originalNumRead - this._context._currentEndReadPosition];
                  System.arraycopy((char[])bytes, this._context._currentEndReadPosition, (char[])this._unconsumedData, 0, ((char[])this._unconsumedData).length);
               } else {
                  this._unconsumedData = new byte[originalNumRead - this._context._currentEndReadPosition];
                  System.arraycopy((byte[])bytes, this._context._currentEndReadPosition, (byte[])this._unconsumedData, 0, ((byte[])this._unconsumedData).length);
               }
            }

            if (numRead == -1) {
               this._pipe.closeWrite();
               return false;
            }

            if (numRead > 0) {
               this._pipe.write(currentChunk, 0, currentChunk.length, this._currentPacketNo++);
               return true;
            }
         }
      }
   }

   private void setNewEncoding(byte[] encoding) {
      this._primaryInput.reset();
      this._encoding = new String(encoding);
      this._context.reset();
      this._context._firstBlock = true;
      this._context._lastBlock = false;
      this._context._guessEncoding = false;
      this._context._newEncodingType = encoding;
      this._unconsumedData = null;
      this._setReader = false;
      if (!StringUtilities.strEqualIgnoreCase(this._encoding, "iso-8859-1", 1701707776)) {
         if (!StringUtilities.strEqualIgnoreCase(this._encoding, "us-ascii", 1701707776)) {
            if (StringUtilities.strEqualIgnoreCase(this._encoding, "utf-8", 1701707776)) {
               this._context._inputEncoding = 2;
            } else {
               try {
                  this._reader = Helper.getStreamReader(this._primaryInput, this._encoding);
               } catch (UnsupportedEncodingException var3) {
               }
            }
         }
      }
   }

   private static native int markupDataChunk(int var0, Object var1, int var2, byte[] var3, MarkupContext var4);
}
