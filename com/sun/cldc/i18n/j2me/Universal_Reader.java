package com.sun.cldc.i18n.j2me;

import com.sun.cldc.i18n.StreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import net.rim.device.api.util.StringUtilities;

public final class Universal_Reader extends StreamReader {
   private int _enc = -1;
   private int _pos;
   private int _size;
   private byte[] _buf;
   private int[] _bytesUsed = new int[2];
   private char[] _char = new char[1];
   private boolean _tryToReadMore;
   private byte[] _conversionData;
   private int[] _conversionDataOffset = new int[1];
   private long _streamPos;
   private long _markPos;
   private boolean _noConversionData;
   private static final int BUF_SIZE = 1024;

   @Override
   public final Reader open(InputStream in, String enc) {
      if (enc == null) {
         return null;
      }

      if (!StringUtilities.strEqualIgnoreCase(enc, TranscodingGateway.ISO8859_1, 1701707776)
         && !StringUtilities.strEqualIgnoreCase(enc, TranscodingGateway.ASCII, 1701707776)) {
         TextProcessingRegistry txtRg = TextProcessingRegistry.getInstance();
         this._enc = txtRg.getTextProcessingDataID(enc, 0);
         if (this._enc == -1) {
            throw new UnsupportedEncodingException("Encoding " + enc + " is not suported!");
         }

         byte[][] bData = txtRg.getTextProcessingData(this._enc, 0, this._conversionDataOffset);
         if (bData != null && bData.length > 0) {
            this._conversionData = bData[0];
         }
      } else {
         this._noConversionData = true;
      }

      super.in = in;
      int len = this._noConversionData ? 1 : 1024;
      if (this._buf == null || this._buf.length < len) {
         this._buf = new byte[len];
      }

      this._bytesUsed[0] = 1048574;
      return this;
   }

   @Override
   public final int read() {
      return this.read(this._char, 0, 1) > 0 ? this._char[0] : -1;
   }

   @Override
   public final int read(char[] cbuf, int offset, int length) {
      if (offset >= 0 && length >= 0 && offset + length <= cbuf.length) {
         int count = 0;
         if (this._noConversionData) {
            while (count < length) {
               int ch = super.in.read();
               if (ch == -1) {
                  if (count == 0) {
                     return -1;
                  }

                  return count;
               }

               cbuf[offset++] = (char)ch;
               count++;
               this._streamPos += 1;
            }

            return length;
         } else {
            return this.read(super.in, 0, Integer.MAX_VALUE, cbuf, offset, length);
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public final boolean ready() {
      if (super.in == null) {
         return false;
      }

      try {
         return super.in.available() <= 0 ? this._size - this._pos > 0 : true;
      } catch (IOException x) {
         return this._size - this._pos > 0;
      }
   }

   @Override
   public final void reset() {
      if (super.in == null) {
         throw new IOException("Stream closed");
      }

      super.in.reset();
      if (super.in.skip(this._markPos) < this._markPos) {
         throw new IOException();
      }

      this._bytesUsed[0] = 1048574;
      this._pos = this._size = 0;
      this._streamPos = this._markPos;
      this._tryToReadMore = false;
   }

   @Override
   public final boolean markSupported() {
      return super.in != null ? super.in.markSupported() : false;
   }

   @Override
   public final void mark(int readAheadLimit) {
      if (!this.markSupported()) {
         throw new IOException("mark() not supported");
      }

      this._markPos = this._streamPos;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void close() {
      this._bytesUsed[0] = 1048574;
      this._pos = this._size = this._conversionDataOffset[0] = 0;
      this._tryToReadMore = this._noConversionData = false;
      this._enc = -1;
      this._conversionData = null;
      boolean var5 = false /* VF: Semaphore variable */;

      label38: {
         try {
            var5 = true;
            if (super.in != null) {
               if (this._streamPos > 0) {
                  super.in.reset();
                  super.in.skip(this._streamPos);
                  var5 = false;
               } else {
                  var5 = false;
               }
            } else {
               var5 = false;
            }
            break label38;
         } catch (IOException var6) {
            var5 = false;
         } finally {
            if (var5) {
               this._markPos = this._streamPos = 0;
               super.close();
            }
         }

         this._markPos = this._streamPos = 0;
         super.close();
         return;
      }

      this._markPos = this._streamPos = 0;
      super.close();
   }

   @Override
   public final int sizeOf(byte[] cbuf, int offset, int length) {
      if (offset < 0 || length < 0 || offset + length > cbuf.length) {
         throw new IndexOutOfBoundsException();
      }

      if (this._noConversionData) {
         return length;
      }

      int ret = TranscodingGateway.sizeOf(this._enc, length, Integer.MAX_VALUE, true);
      if (ret < 0) {
         int pos = this._pos;
         int size = this._size;
         boolean tryToReadMore = this._tryToReadMore;
         int read = this._bytesUsed[0];
         this._bytesUsed[0] = 1048574;
         long streamPos = this._streamPos;

         try {
            ret = this.read(cbuf, offset, length, null, 0, Integer.MAX_VALUE);
         } catch (IOException ioe) {
            return -1;
         }

         this._pos = pos;
         this._size = size;
         this._tryToReadMore = tryToReadMore;
         this._bytesUsed[0] = read;
         this._streamPos = streamPos;

         try {
            if (super.in != null && this._streamPos > 0) {
               super.in.reset();
               this._streamPos = super.in.skip(this._streamPos);
               this._bytesUsed[0] = 1048574;
               this._pos = this._size = 0;
               this._tryToReadMore = false;
            }
         } catch (IOException var12) {
         }
      }

      return ret;
   }

   private final int read(Object in, int inOffset, int inLength, Object cbuf, int offset, int length) {
      int count = 0;
      int converted = 0;
      length += offset;
      boolean isIO = in instanceof InputStream;
      if (this._bytesUsed[0] == 1048574) {
         this._pos = 0;
         this._tryToReadMore = false;
         if (isIO) {
            this._size = ((InputStream)in).read(this._buf);
         } else {
            this._size = Math.min(this._buf.length, inLength);
            inLength -= this._size;
            System.arraycopy(in, inOffset, this._buf, this._pos, this._size);
            inOffset += this._size;
         }
      }

      while (offset < length) {
         if (this._pos < this._size && !this._tryToReadMore) {
            int skipChar = 0;

            try {
               skipChar = this._size == this._buf.length && inLength > 0 && this._buf[this._size - 1] == 13 ? 1 : 0;
            } catch (Exception e) {
               skipChar = 0;
            }

            converted = TranscodingGateway.L2U(
               this._enc,
               this._buf,
               this._pos,
               this._size - skipChar - this._pos,
               cbuf,
               offset,
               length - offset,
               this._bytesUsed,
               this._conversionData,
               this._conversionDataOffset[0]
            );
            if (converted > 0) {
               this._pos = this._pos + this._bytesUsed[0];
               this._streamPos = this._streamPos + this._bytesUsed[0];
               offset += converted;
               count += converted;
            } else {
               this._tryToReadMore = true;
            }
         } else {
            int delta = this._size - this._pos;
            if (delta < 0) {
               delta = 0;
            }

            for (int i = 0; i < delta; i++) {
               this._buf[i] = this._buf[this._pos++];
            }

            this._size = delta;
            this._pos = 0;
            int rc = 0;
            if (isIO) {
               rc = ((InputStream)in).read(this._buf, delta, 1024 - delta);
            } else if (inLength > 0) {
               rc = Math.min(1024 - delta, inLength);
               inLength -= rc;
               System.arraycopy(in, inOffset, this._buf, delta, rc);
               inOffset += rc;
            }

            if (rc <= 0) {
               if (converted <= 0 && this._size - this._pos > 0) {
                  this._pos = this._size;
               }
               break;
            }

            this._tryToReadMore = false;
            this._size += rc;
         }
      }

      if (count == 0) {
         count = -1;
      }

      return count;
   }

   public final Object byteToCharArray(int encId, byte[] buffer, int offset, int length, String enc) {
      if (encId != 0
         && encId != 1
         && (
            encId != -1
               || !StringUtilities.strEqualIgnoreCase(enc, TranscodingGateway.ISO8859_1, 1701707776)
                  && !StringUtilities.strEqualIgnoreCase(enc, TranscodingGateway.ASCII, 1701707776)
         )) {
         TextProcessingRegistry txtRg = TextProcessingRegistry.getInstance();
         if (encId == -1) {
            encId = txtRg.getTextProcessingDataID(enc, 0);
            if (encId == -1) {
               throw new UnsupportedEncodingException("Encoding " + enc + " is not suported!");
            }
         }

         this._size = this._pos = 0;
         this._tryToReadMore = false;
         this._bytesUsed[0] = 1048574;
         this._bytesUsed[1] = 0;
         if (this._buf == null) {
            this._buf = new byte[1024];
         }

         if (encId != this._enc) {
            this._enc = encId;
            byte[][] bData = txtRg.getTextProcessingData(this._enc, 0, this._conversionDataOffset);
            if (bData != null && bData.length > 0) {
               this._conversionData = bData[0];
            } else {
               this._conversionData = null;
               this._conversionDataOffset[0] = 0;
            }
         }

         try {
            Object outbuf = null;
            int outputSize = this.sizeOf(buffer, offset, length);
            if (outputSize >= 0) {
               if (this._bytesUsed[1] == 1) {
                  outbuf = new char[outputSize];
               } else {
                  outbuf = new byte[outputSize];
               }

               this.read(buffer, offset, length, outbuf, 0, outputSize);
               return outbuf;
            } else {
               throw new UnsupportedEncodingException("Data does not match the expected encoding");
            }
         } catch (Exception e) {
            throw new UnsupportedEncodingException("IOException in UniversalReader#byteToCharArray()! " + e.getMessage());
         }
      } else {
         byte[] outbuf = new byte[length];
         System.arraycopy(buffer, offset, outbuf, 0, length);
         return outbuf;
      }
   }
}
