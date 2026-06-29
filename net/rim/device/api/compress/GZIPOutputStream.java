package net.rim.device.api.compress;

import java.io.IOException;
import java.io.OutputStream;
import net.rim.device.internal.compress.Deflater;

public class GZIPOutputStream extends OutputStream {
   private OutputStream _outputStream;
   private Deflater _deflater;
   private byte[] _buffer;
   private int _bufferOffset;
   public static final int MIN_LOG2_WINDOW_LENGTH = 8;
   public static final int MAX_LOG2_WINDOW_LENGTH = 15;
   public static final int COMPRESSION_NONE = 0;
   public static final int COMPRESSION_BEST = 9;
   private static final int BUFFER_LENGTH = 1024;

   public GZIPOutputStream(OutputStream outputStream) {
      this(outputStream, 0);
   }

   public GZIPOutputStream(OutputStream outputStream, int compressionValue) {
      this(outputStream, compressionValue, 8);
   }

   public GZIPOutputStream(OutputStream outputStream, int compressionValue, int windowLength) {
      if (outputStream != null && compressionValue >= 0 && compressionValue <= 9 && windowLength >= 8 && windowLength <= 15) {
         this._outputStream = outputStream;
         this._buffer = new byte[1024];
         this._deflater = new Deflater(compressionValue, 0, windowLength + 16);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public synchronized void write(int data) throws IOException {
      if (this._outputStream == null) {
         throw new IOException("Stream closed");
      }

      if (this._bufferOffset == 1024) {
         this._outputStream.write(this._deflater.compress(this._buffer, 0, 1024, 2));
         this._bufferOffset = 0;
      }

      this._buffer[this._bufferOffset++] = (byte)data;
   }

   @Override
   public synchronized void write(byte[] data, int dataOffset, int dataLength) throws IOException {
      if (data == null || dataOffset < 0 || dataLength < 0 || dataOffset + dataLength > data.length) {
         throw new IllegalArgumentException();
      }

      if (this._outputStream == null) {
         throw new IOException("Stream closed");
      }

      if (this._bufferOffset + dataLength < this._buffer.length) {
         System.arraycopy(data, dataOffset, this._buffer, this._bufferOffset, dataLength);
         this._bufferOffset += dataLength;
      } else {
         if (this._bufferOffset > 0) {
            this._outputStream.write(this._deflater.compress(this._buffer, 0, this._bufferOffset, 2));
            this._bufferOffset = 0;
         }

         if (dataLength > 0) {
            this._outputStream.write(this._deflater.compress(data, dataOffset, dataLength, 2));
         }
      }
   }

   @Override
   public synchronized void flush() throws IOException {
      if (this._outputStream == null) {
         throw new IOException("Stream closed");
      }

      this._outputStream.write(this._deflater.compress(this._buffer, 0, this._bufferOffset, 3));
      this._bufferOffset = 0;
      this._outputStream.flush();
   }

   @Override
   public void close() throws IOException {
      if (this._outputStream == null) {
         throw new IOException();
      }

      this._outputStream.write(this._deflater.compress(this._buffer, 0, this._bufferOffset, 4));
      this._bufferOffset = 0;
      this._outputStream.close();
      this._outputStream = null;
   }
}
