package net.rim.device.internal.browser.util;

import java.io.DataInputStream;
import java.io.InputStream;

public final class PipeInputDataInputStream extends DataInputStream implements PipeInput {
   private PipeInput _pipeIn;

   public PipeInputDataInputStream(PipeInput in) {
      super((InputStream)in);
      this._pipeIn = in;
   }

   @Override
   public final int readByteArray(PipePtr ptr, int length) {
      return this._pipeIn.readByteArray(ptr, length);
   }

   @Override
   public final int readCompressedInt() {
      return this._pipeIn.readCompressedInt();
   }

   @Override
   public final String readInlineString(String encoding) {
      return this._pipeIn.readInlineString(encoding);
   }

   @Override
   public final void skipInlineString() {
      this._pipeIn.skipInlineString();
   }

   @Override
   public final Pipe getPipe() {
      return this._pipeIn.getPipe();
   }

   @Override
   public final PipeContext getPosition() {
      return this._pipeIn.getPosition();
   }
}
