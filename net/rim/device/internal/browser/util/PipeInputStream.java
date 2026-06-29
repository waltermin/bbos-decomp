package net.rim.device.internal.browser.util;

import java.io.InputStream;

public class PipeInputStream extends InputStream implements PipeInput {
   Pipe _pipe;
   PipeContext _context;
   PipeContext _markedContext;

   public int getNumRead() {
      return 0;
   }

   public Pipe getCacheableData() {
      this._pipe.setCacheStart(this._context._currentPacket, this._context._currentReadPos);
      synchronized (this._context) {
         this._context._currentPacket = 0;
         this._context._currentReadPos = 0;
         this._context._numRead = 0;
      }

      return this._pipe;
   }

   public int getEstimatedSize() {
      return this._pipe.getEstimatedSize();
   }

   @Override
   public int readCompressedInt() {
      return this._pipe.readCompressedInt(this._context);
   }

   @Override
   public Pipe getPipe() {
      return this._pipe;
   }

   @Override
   public int readByteArray(PipePtr ptr, int length) {
      return this._pipe.readByteArray(ptr, length, this._context);
   }

   @Override
   public String readInlineString(String encoding) {
      return this._pipe.readInlineString(this._context, encoding);
   }

   @Override
   public void skipInlineString() {
      this._pipe.skipInlineString(this._context);
   }

   @Override
   public PipeContext getPosition() {
      return this._context;
   }

   @Override
   public boolean markSupported() {
      return true;
   }

   @Override
   public void mark(int readAhead) {
      synchronized (this._context) {
         this._markedContext._currentPacket = this._context._currentPacket;
         this._markedContext._currentReadPos = this._context._currentReadPos;
         this._markedContext._availableBytes = this._context._availableBytes;
      }
   }

   @Override
   public void reset() {
      synchronized (this._context) {
         this._context._currentPacket = this._markedContext._currentPacket;
         this._context._currentReadPos = this._markedContext._currentReadPos;
         this._context._availableBytes = this._markedContext._availableBytes;
      }
   }

   @Override
   public int read() {
      return this._pipe.read(this._context);
   }

   PipeInputStream(Pipe pipe) {
      this._pipe = pipe;
      this._context = new PipeContext();
      this._markedContext = new PipeContext();
   }

   @Override
   public void close() {
      this._pipe.closeRead(this._context);
   }

   PipeInputStream(Pipe pipe, int packet, int offset, int length) {
      this._pipe = pipe;
      this._context = new PipeContext();
      this._context._currentPacket = packet;
      this._context._currentReadPos = offset;
      this._context._availableBytes = length;
      this._markedContext = new PipeContext();
      this._markedContext._currentReadPos = offset;
      this._markedContext._currentPacket = packet;
      this._markedContext._availableBytes = length;
   }

   @Override
   public int available() {
      return this._pipe.available(this._context);
   }

   @Override
   public int read(byte[] b, int off, int len) {
      return this._pipe.read(b, off, len, this._context);
   }

   @Override
   public long skip(long n) {
      return this._pipe.skip(this._context, n);
   }
}
