package net.rim.device.apps.internal.browser.stack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.microedition.io.file.FileConnection;

public final class CachedFileConnection implements FileConnection {
   private CacheResult _cacheResult;
   private InputStream _inputStream;
   private FileConnection _connection;
   private boolean _closed;

   @Override
   public final void close() {
      if (this._inputStream != null) {
         this._inputStream.close();
         this._inputStream = null;
      }

      this._closed = true;
      if (this._connection != null) {
         this._connection.close();
         this._connection = null;
      }
   }

   public final CacheResult getCacheResult() {
      return this._cacheResult;
   }

   @Override
   public final InputStream openInputStream() {
      return this._inputStream;
   }

   @Override
   public final DataInputStream openDataInputStream() {
      return (DataInputStream)(new Object(this._inputStream));
   }

   @Override
   public final OutputStream openOutputStream() {
      throw new Object();
   }

   @Override
   public final DataOutputStream openDataOutputStream() {
      throw new Object();
   }

   @Override
   public final OutputStream openOutputStream(long byteOffset) {
      throw new Object();
   }

   @Override
   public final long totalSize() {
      return this._connection.totalSize();
   }

   @Override
   public final long availableSize() {
      return this._connection.availableSize();
   }

   @Override
   public final long usedSize() {
      return this._connection.usedSize();
   }

   @Override
   public final long directorySize(boolean includeSubDirs) {
      return this._connection.directorySize(includeSubDirs);
   }

   @Override
   public final long fileSize() {
      return this._connection.fileSize();
   }

   @Override
   public final boolean canRead() {
      return this._connection.canRead();
   }

   @Override
   public final boolean canWrite() {
      return this._connection.canWrite();
   }

   @Override
   public final boolean isHidden() {
      return this._connection.isHidden();
   }

   @Override
   public final void setReadable(boolean readable) {
      this._connection.setReadable(readable);
   }

   @Override
   public final void setWritable(boolean writable) {
      this._connection.setWritable(writable);
   }

   @Override
   public final void setHidden(boolean hidden) {
      this._connection.setHidden(hidden);
   }

   @Override
   public final Enumeration list() {
      return this._connection.list();
   }

   @Override
   public final Enumeration list(String filter, boolean includeHidden) {
      return this._connection.list(filter, includeHidden);
   }

   @Override
   public final void create() {
      this._connection.create();
   }

   @Override
   public final void mkdir() {
      this._connection.mkdir();
   }

   @Override
   public final boolean exists() {
      return this._connection.exists();
   }

   @Override
   public final boolean isDirectory() {
      return this._connection.isDirectory();
   }

   @Override
   public final void delete() {
      this._connection.delete();
   }

   @Override
   public final void rename(String newName) {
      this._connection.rename(newName);
   }

   @Override
   public final void truncate(long byteOffset) {
      this._connection.truncate(byteOffset);
   }

   @Override
   public final void setFileConnection(String fileName) {
      this._connection.setFileConnection(fileName);
   }

   @Override
   public final String getName() {
      return this._connection.getName();
   }

   @Override
   public final String getPath() {
      return this._connection.getPath();
   }

   @Override
   public final String getURL() {
      return this._connection.getURL();
   }

   @Override
   public final long lastModified() {
      return this._connection.lastModified();
   }

   @Override
   public final boolean isOpen() {
      return !this._closed;
   }

   public CachedFileConnection(FileConnection connection, InputStream inputStream, CacheResult cacheResult) {
      this._cacheResult = cacheResult;
      this._connection = connection;
      this._inputStream = inputStream;
   }
}
