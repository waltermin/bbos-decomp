package net.rim.device.apps.internal.browser.stack;

import java.io.DataInputStream;
import java.io.InputStream;
import net.rim.device.internal.browser.util.Pipe;
import net.rim.device.internal.browser.util.PipeContext;
import net.rim.device.internal.browser.util.PipeInput;
import net.rim.device.internal.browser.util.PipePtr;

public final class WAPInputStream extends DataInputStream {
   private PipeInput _pipeInStream;
   private PipePtr _pipePtr = (PipePtr)(new Object());

   public WAPInputStream(byte[] buf) {
      this((InputStream)(new Object(buf)));
   }

   public WAPInputStream(InputStream inStream) {
      super(getInputStream(inStream));
      this._pipeInStream = (PipeInput)super.in;
   }

   public WAPInputStream() {
      this(new byte[0]);
   }

   public final int readMBInt() {
      return this.readCompressedInt();
   }

   public final void save() {
      if (super.in instanceof MarkableInputStream) {
         ((MarkableInputStream)super.in).save();
      }
   }

   public final InputStream getSaved() {
      return this._pipeInStream.getPipe().getInputStream();
   }

   public final void skipInlineString() {
      this._pipeInStream.skipInlineString();
   }

   public final String readInlineString(String encoding) {
      return this._pipeInStream.readInlineString(encoding);
   }

   public final PipePtr readByteArrayRef() {
      return this.readByteArrayRef(this.readCompressedInt());
   }

   public final PipePtr readByteArrayRef(int size) {
      int readSize = this._pipeInStream.readByteArray(this._pipePtr, size);
      if (readSize != size) {
         throw new Object();
      } else {
         return this._pipePtr;
      }
   }

   public final byte[] readByteArray() {
      int size = this.readCompressedInt();
      byte[] data = new byte[size];
      int readSize = this.read(data, 0, size);
      if (readSize != size) {
         throw new Object();
      } else {
         return data;
      }
   }

   public final void skipByteArray() {
      this.skipBytes(this.readCompressedInt());
   }

   public final int readCompressedInt() {
      return this._pipeInStream.readCompressedInt();
   }

   private static final InputStream getInputStream(InputStream inStream) {
      return inStream instanceof Object ? inStream : new MarkableInputStream(inStream);
   }

   public final PipeContext getPosition() {
      return this._pipeInStream.getPosition();
   }

   public final Pipe getPipe() {
      return this._pipeInStream.getPipe();
   }
}
