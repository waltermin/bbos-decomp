package net.rim.device.apps.internal.mms.model;

import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.internal.io.file.FileUtilities;

public final class FileAttachment implements MMSAttachment {
   private String _filename;
   private int _filesize;
   private String _name;
   private int _type;
   private byte[] _data;
   private String _charset;

   public FileAttachment(String name, int type, String filename, int filesize, String charset) {
      this._name = name;
      this._type = type;
      this._filename = filename;
      this._filesize = filesize;
      this._charset = charset;
   }

   @Override
   public final String getName() {
      return this._name;
   }

   @Override
   public final int getType() {
      return this._type;
   }

   @Override
   public final byte[] getData() {
      if (this._data == null) {
         this._data = FileUtilities.getData(this._filename);
      }

      return this._data;
   }

   @Override
   public final String getCharset() {
      return this._charset;
   }

   @Override
   public final int getDataSize() {
      return this._filesize;
   }
}
