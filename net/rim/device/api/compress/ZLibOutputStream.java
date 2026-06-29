package net.rim.device.api.compress;

import java.io.IOException;
import java.io.OutputStream;
import net.rim.device.internal.compress.Deflater;

public class ZLibOutputStream extends OutputStream {
   private OutputStream _outputStream;
   private Deflater _deflater;
   private byte[] _buffer;
   private int _bufferOffset;
   public static final int MIN_LOG2_WINDOW_LENGTH;
   public static final int MAX_LOG2_WINDOW_LENGTH;
   public static final int COMPRESSION_NONE;
   public static final int COMPRESSION_BEST;
   private static final int BUFFER_LENGTH;

   public ZLibOutputStream(OutputStream outputStream) {
      this(outputStream, false);
   }

   public ZLibOutputStream(OutputStream outputStream, boolean noWrap) {
      this(outputStream, noWrap, 8);
   }

   public ZLibOutputStream(OutputStream outputStream, boolean noWrap, int maxLog2WindowLength) {
      this(outputStream, noWrap, maxLog2WindowLength, 0);
   }

   public ZLibOutputStream(OutputStream outputStream, boolean noWrap, int maxLog2WindowLength, int compressionValue) {
      if (outputStream != null && maxLog2WindowLength >= 8 && maxLog2WindowLength <= 15 && compressionValue >= 0 && compressionValue <= 9) {
         this._outputStream = outputStream;
         this._buffer = new byte[1024];
         this._deflater = new Deflater(compressionValue, 0, noWrap ? -maxLog2WindowLength : maxLog2WindowLength);
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public synchronized void write(int data) {
      if (this._outputStream == null) {
         throw new IOException("Stream closed");
      }

      if (this._bufferOffset == 1024) {
         this._outputStream.write(this._deflater.compress(this._buffer, 0, this._bufferOffset, 2));
         this._bufferOffset = 0;
      }

      this._buffer[this._bufferOffset++] = (byte)data;
   }

   @Override
   public synchronized void write(byte[] data, int dataOffset, int dataLength) {
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
   public synchronized void flush() {
      if (this._outputStream == null) {
         throw new IOException("Stream closed");
      }

      this._outputStream.write(this._deflater.compress(this._buffer, 0, this._bufferOffset, 3));
      this._bufferOffset = 0;
      this._outputStream.flush();
   }

   @Override
   public void close() {
      if (this._outputStream == null) {
         throw new IOException();
      }

      this._outputStream.write(this._deflater.compress(this._buffer, 0, this._bufferOffset, 4));
      this._bufferOffset = 0;
      this._outputStream.close();
      this._outputStream = null;
   }
}
