package net.rim.device.api.io;

import java.io.EOFException;
import java.io.IOException;

public final class ScanLine extends LineReader {
   private byte[] _boundary = new byte[74];

   public ScanLine(SharedInputStream stream) {
      super(stream);
   }

   public final int searchForBoundary(byte[] boundary) throws EOFException, IOException {
      if (boundary != null && boundary.length <= 70) {
         boolean needLF = false;
         int lineLength = 0;
         int boundaryOffset = 0;
         int boundaryLength = 0;
         this._boundary[boundaryLength++] = 45;
         this._boundary[boundaryLength++] = 45;
         System.arraycopy(boundary, 0, this._boundary, boundaryLength, boundary.length);
         boundaryLength += boundary.length;
         int midBoundaryLength = boundaryLength;
         this._boundary[boundaryLength++] = 45;
         this._boundary[boundaryLength++] = 45;

         boolean crlfFound;
         do {
            if (super._bufferOffset == super._bufferLength) {
               super._bufferOffset = 0;
               super._bufferLength = super._bufferLength < 1024 ? 0 : super._stream.read(super._buffer, 0, 1024);
               if (super._bufferLength <= 0) {
                  super._bufferLength = 0;
                  if (needLF) {
                     throw new IOException();
                  }

                  if (boundaryOffset > boundaryLength || boundaryOffset < midBoundaryLength) {
                     throw new EOFException();
                  }
                  break;
               }
            }

            super._bufferOffset = searchForBoundary(
               this._boundary, boundaryOffset, boundaryLength, midBoundaryLength, super._buffer, super._bufferOffset, super._bufferLength, needLF, lineLength
            );
            needLF = super._bufferOffset < 0;
            boundaryOffset = super._bufferOffset >> 24 & 127;
            lineLength = super._bufferOffset >> 14 & 1023;
            crlfFound = (super._bufferOffset & 8192) != 0;
            super._bufferOffset &= 8191;
         } while (!crlfFound || boundaryOffset > boundaryLength || boundaryOffset < midBoundaryLength);

         return boundaryOffset == boundaryLength
            ? lineLength + super._bufferLength - super._bufferOffset << 1 | 1
            : lineLength + super._bufferLength - super._bufferOffset << 1 | 0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static final native int searchForBoundary(byte[] var0, int var1, int var2, int var3, byte[] var4, int var5, int var6, boolean var7, int var8);
}
