package com.sun.cldc.i18n;

import java.io.OutputStream;
import java.io.Writer;

public class StreamWriter extends Writer {
   public OutputStream out;

   public Writer open(OutputStream out, String enc) {
      this.out = out;
      return this;
   }

   @Override
   public void flush() {
      this.out.flush();
   }

   @Override
   public void close() {
      this.out.close();
   }

   public int sizeOf(char[] _1, int _2, int _3) {
      throw null;
   }
}
