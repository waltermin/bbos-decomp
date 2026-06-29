package net.rim.device.api.crypto;

import java.io.OutputStream;

final class CryptoDummyOutputStream extends OutputStream {
   public CryptoDummyOutputStream() {
   }

   @Override
   public final void close() {
   }

   @Override
   public final void flush() {
   }

   @Override
   public final void write(int b) {
   }

   @Override
   public final void write(byte[] b, int off, int len) {
   }

   @Override
   public final void write(byte[] b) {
   }
}
