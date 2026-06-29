package com.sun.cldc.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class StreamReader extends Reader {
   public InputStream in;

   public Reader open(InputStream in, String enc) {
      this.in = in;
      return this;
   }

   @Override
   public boolean ready() {
      try {
         return this.in.available() > 0;
      } catch (IOException x) {
         return false;
      }
   }

   @Override
   public boolean markSupported() {
      return this.in.markSupported();
   }

   @Override
   public void mark(int readAheadLimit) {
      if (this.in.markSupported()) {
         this.in.mark(readAheadLimit);
      } else {
         throw new IOException("mark() not supported");
      }
   }

   @Override
   public void reset() {
      this.in.reset();
   }

   @Override
   public void close() {
      this.in.close();
      this.in = null;
   }

   public int sizeOf(byte[] _1, int _2, int _3) {
      throw null;
   }
}
