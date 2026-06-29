package net.rim.device.api.io;

import java.io.OutputStream;
import java.util.Vector;

public class SharedOutputStream {
   private int _currentIndex;
   private OutputStream _out;
   private Vector _sharedStreams;

   public SharedOutputStream(OutputStream out) {
      this._out = out;
      this._currentIndex = 0;
      this._sharedStreams = new Vector();
   }

   public synchronized OutputStream getOutputStream() {
      SharedOutputStreamPart element = new SharedOutputStreamPart(this, this._sharedStreams.size());
      this._sharedStreams.addElement(element);
      return element;
   }

   synchronized void write(int index, byte[] buffer, int offset, int length) {
      if (index > this._currentIndex) {
         SharedOutputStreamPart stream = (SharedOutputStreamPart)this._sharedStreams.elementAt(index);
         if (stream.isWritable()) {
            stream.getStream().write(buffer, offset, length);
         } else {
            throw new IllegalStateException();
         }
      } else if (index == this._currentIndex) {
         this._out.write(buffer, offset, length);
      } else {
         throw new IllegalStateException();
      }
   }

   synchronized void flush(int index) {
      if (index > this._currentIndex) {
         SharedOutputStreamPart stream = (SharedOutputStreamPart)this._sharedStreams.elementAt(index);
         if (stream.isWritable()) {
            stream.getStream().flush();
         } else {
            throw new IllegalStateException();
         }
      } else if (index == this._currentIndex) {
         this._out.flush();
      } else {
         throw new IllegalStateException();
      }
   }

   synchronized void close(int index) {
      if (index > this._currentIndex) {
         SharedOutputStreamPart stream = (SharedOutputStreamPart)this._sharedStreams.elementAt(index);
         stream.setIsWritable(false);
      } else if (index == this._currentIndex) {
         this._out.flush();
         this._currentIndex++;
         this.writeSeparator();

         while (this._currentIndex < this._sharedStreams.size()) {
            SharedOutputStreamPart stream = (SharedOutputStreamPart)this._sharedStreams.elementAt(this._currentIndex);
            NoCopyByteArrayOutputStream outputStream = stream.getStream();
            outputStream.flush();
            this._out.write(outputStream.getByteArray(), 0, outputStream.size());
            outputStream.close();
            NoCopyByteArrayOutputStream var5 = null;
            stream.setStream(null);
            if (stream.isWritable()) {
               stream.setIsWritable(false);
               return;
            }

            this.writeSeparator();
            this._currentIndex++;
         }
      } else {
         throw new IllegalStateException();
      }
   }

   public synchronized void close() {
      this._out.close();
   }

   public void writeSeparator() {
   }
}
