package net.rim.device.apps.internal.vad;

import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

final class VADAudioFile extends VADFile {
   private FileConnection _file;
   private OutputStream _out;

   @Override
   final boolean open(String name) {
      try {
         if (this._file != null) {
            this._file.close();
         }

         this._file = (FileConnection)Connector.open(((StringBuffer)(new Object("file:///store/home/user/vad/"))).append(name).toString());
         if (this._file.exists()) {
            this._file.delete();
         }

         this._file.create();
         this._out = this._file.openOutputStream();
         return true;
      } finally {
         ;
      }
   }

   @Override
   final void close() {
      try {
         if (this._file != null) {
            this._out.close();
            this._file.close();
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   final void write(byte[] data, int offset) {
      try {
         this._out.write(data);
      } finally {
         return;
      }
   }

   @Override
   final void read(int location, int offset, int count) {
      throw new Object("write-only file");
   }

   @Override
   final void copy(VADFile file) {
      throw new Object("write-only file");
   }

   @Override
   final void delete() {
      throw new Object("write-only file");
   }

   @Override
   final int size() {
      throw new Object("write-only file");
   }

   @Override
   final boolean exists() {
      return true;
   }
}
