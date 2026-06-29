package net.rim.device.api.crypto.pgp;

import java.io.OutputStream;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.device.api.io.SharedOutputStream;
import net.rim.device.api.util.CRC24;

public final class PGPArmorEncoder extends OutputStream {
   private byte[] _tail;
   private byte[] _buffer;
   private int _crc = 13501623;
   private boolean _isClearSigned;
   private SharedOutputStream _sharedOutput;
   private OutputStream _clearPart;
   private OutputStream _mainPart;
   private Base64OutputStream _base64Part;
   private NoCopyByteArrayOutputStream _baseOut;
   private PGPDashEscapedOutputStream _dashEscapeOut;
   public static final byte[] BEGIN = new byte[]{45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32};
   public static final byte[] BEGIN_PGP_DASHES = new byte[]{
      45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32, 80, 71, 80, 32, 77, 69, 83, 83, 65, 71, 69, 45, 45, 45, 45, 45
   };
   public static final byte[] BEGIN_SIGNEDMESSAGE_DASHES = new byte[]{
      45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32, 80, 71, 80, 32, 83, 73, 71, 78, 69, 68, 32, 77, 69, 83, 83, 65, 71, 69, 45, 45, 45, 45, 45
   };
   public static final byte[] BEGIN_SIGNATURE_DASHES = new byte[]{
      45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32, 80, 71, 80, 32, 83, 73, 71, 78, 65, 84, 85, 82, 69, 45, 45, 45, 45, 45
   };
   public static final byte[] BEGIN_PUBLIC_KEY_DASHES = new byte[]{
      45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32, 80, 71, 80, 32, 80, 85, 66, 76, 73, 67, 32, 75, 69, 89, 32, 66, 76, 79, 67, 75, 45, 45, 45, 45, 45
   };
   public static final byte[] BEGIN_PRIVATE_KEY_DASHES = new byte[]{
      45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32, 80, 71, 80, 32, 80, 82, 73, 86, 65, 84, 69, 32, 75, 69, 89, 32, 66, 76, 79, 67, 75, 45, 45, 45, 45, 45
   };
   public static final byte[] END = new byte[]{45, 45, 45, 45, 45, 69, 78, 68, 32};
   public static final byte[] HASH = new byte[]{72, 97, 115, 104, 58, 32};
   public static final byte[] END_SIGNATURE_DASHES = new byte[]{
      45, 45, 45, 45, 45, 69, 78, 68, 32, 80, 71, 80, 32, 83, 73, 71, 78, 65, 84, 85, 82, 69, 45, 45, 45, 45, 45
   };
   public static final byte[] END_PGP_DASHES = new byte[]{45, 45, 45, 45, 45, 69, 78, 68, 32, 80, 71, 80, 32, 77, 69, 83, 83, 65, 71, 69, 45, 45, 45, 45, 45};
   public static final byte[] END_PUBLIC_KEY_DASHES = new byte[]{
      45, 45, 45, 45, 45, 69, 78, 68, 32, 80, 71, 80, 32, 80, 85, 66, 76, 73, 67, 32, 75, 69, 89, 32, 66, 76, 79, 67, 75, 45, 45, 45, 45, 45
   };
   public static final byte[] END_PRIVATE_KEY_DASHES = new byte[]{
      45, 45, 45, 45, 45, 69, 78, 68, 32, 80, 71, 80, 32, 80, 82, 73, 86, 65, 84, 69, 32, 75, 69, 89, 32, 66, 76, 79, 67, 75, 45, 45, 45, 45, 45
   };
   private static final byte[] VERSION = new byte[]{86, 101, 114, 115, 105, 111, 110, 58, 32};
   private static final byte[] COMMENT = new byte[]{67, 111, 109, 109, 101, 110, 116, 58, 32};
   private static final byte[] CHARSET = new byte[]{67, 104, 97, 114, 115, 101, 116, 58, 32};
   private static final byte[] CRLF = new byte[]{13, 10};

   public PGPArmorEncoder(OutputStream out) {
      this(out, null, null, null, false);
   }

   public PGPArmorEncoder(OutputStream out, boolean clearSigned) {
      this(out, null, null, null, clearSigned);
   }

   public PGPArmorEncoder(OutputStream out, String version, String comment, String charSet) {
      this(out, version, comment, charSet, false);
   }

   public PGPArmorEncoder(OutputStream out, String version, String comment, String charSet, boolean clearSigned) {
      this(
         out,
         clearSigned ? BEGIN_SIGNATURE_DASHES : BEGIN_PGP_DASHES,
         clearSigned ? END_SIGNATURE_DASHES : END_PGP_DASHES,
         version,
         comment,
         charSet,
         clearSigned
      );
   }

   public PGPArmorEncoder(OutputStream out, byte[] header, byte[] tail, String version, String comment, String charSet, boolean clearSigned) {
      if (header != null && tail != null) {
         this._tail = tail;
         this._sharedOutput = new SharedOutputStream(out);
         this._isClearSigned = clearSigned;
         if (this._isClearSigned) {
            this._clearPart = this._sharedOutput.getOutputStream();
            this._dashEscapeOut = new PGPDashEscapedOutputStream(this._clearPart);
         }

         this._mainPart = this._sharedOutput.getOutputStream();
         this._mainPart.write(CRLF);
         this._mainPart.write(header);
         this._mainPart.write(CRLF);
         if (version != null) {
            this._mainPart.write(VERSION);
            this._mainPart.write(version.getBytes());
            this._mainPart.write(CRLF);
         }

         if (comment != null) {
            this._mainPart.write(COMMENT);
            this._mainPart.write(comment.getBytes());
            this._mainPart.write(CRLF);
         }

         if (charSet != null) {
            this._mainPart.write(CHARSET);
            this._mainPart.write(charSet.getBytes());
            this._mainPart.write(CRLF);
         }

         this._mainPart.write(CRLF);
         this._baseOut = new NoCopyByteArrayOutputStream();
         this._base64Part = new Base64OutputStream(this._baseOut, true, true);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final boolean isClearSigned() {
      return this._isClearSigned;
   }

   final void writeClearSignedHeader(String[] digests) {
      if (this._clearPart == null) {
         throw new IllegalArgumentException();
      }

      this._clearPart.write(CRLF);
      this._clearPart.write(BEGIN_SIGNEDMESSAGE_DASHES);
      this._clearPart.write(CRLF);
      if (digests != null && digests.length > 0) {
         this._clearPart.write(HASH);

         for (int i = 0; i < digests.length; i++) {
            this._clearPart.write(digests[i].getBytes());
            if (i != digests.length - 1) {
               this._clearPart.write(44);
               this._clearPart.write(32);
            }
         }

         this._clearPart.write(CRLF);
      }

      this._clearPart.write(CRLF);
   }

   final void writeClearSignedData(byte[] buffer, int offset, int length) {
      this._dashEscapeOut.write(buffer, offset, length);
   }

   @Override
   public final void write(byte[] data, int offset, int length) {
      if (data != null && data.length - length >= offset && length >= 0 && offset >= 0) {
         this._crc = CRC24.update(this._crc, data, offset, length);
         this._base64Part.write(data, offset, length);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final void write(int data) {
      if (this._buffer == null) {
         this._buffer = new byte[1];
      }

      this._buffer[0] = (byte)data;
      this.write(this._buffer, 0, 1);
   }

   @Override
   public final void write(byte[] buffer) {
      this.write(buffer, 0, buffer == null ? 0 : buffer.length);
   }

   @Override
   public final void flush() {
      if (this._clearPart != null) {
         this._clearPart.flush();
      }

      this._base64Part.flush();
   }

   @Override
   public final void close() {
      if (this._clearPart != null) {
         this._clearPart.close();
      }

      this._base64Part.close();
      byte[] crc = new byte[]{(byte)this._crc, (byte)(this._crc >> 8), (byte)(this._crc >> 16)};
      NoCopyByteArrayOutputStream temp = new NoCopyByteArrayOutputStream();
      Base64OutputStream base64 = new Base64OutputStream(temp);
      base64.write(crc);
      base64.close();
      this._mainPart.write(this._baseOut.getByteArray(), 0, this._baseOut.size());
      this._mainPart.write(61);
      this._mainPart.write(temp.getByteArray(), 0, temp.size());
      this._mainPart.write(CRLF);
      this._mainPart.write(this._tail);
      this._mainPart.write(CRLF);
      this._mainPart.close();
      this._sharedOutput.close();
   }
}
