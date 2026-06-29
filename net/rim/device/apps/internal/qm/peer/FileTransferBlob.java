package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.JPEGEncodedImage;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.transmission.rim.CMIMEUtilities;
import net.rim.device.apps.api.transmission.rim.RIMMessagingOutgoingMessage;

final class FileTransferBlob extends PeerDataBlob implements PersistableRIMModel, ConversionProvider, CloneProvider {
   String _contentType;
   String _filename;
   byte[] _data;
   int _size;
   int _integer;
   String _url;
   String _application;
   int _id;
   int _sessionId;
   private static final int CONTENT_TYPE = 1;
   private static final int FILENAME = 2;
   private static final int DATA = 3;
   private static final int SIZE = 4;
   private static final int INT = 5;
   private static final int URL = 6;
   private static final int APP = 7;
   private static final int ID = 8;
   private static final int SESSION = 9;

   @Override
   public final Object clone(Object context) {
      return this;
   }

   public final String getFilename() {
      return this._filename;
   }

   public final byte[] getData() {
      return this._data;
   }

   public final int getInteger() {
      return this._integer;
   }

   public final String getUrl() {
      return this._url;
   }

   public final int getSize() {
      return this._size;
   }

   public final String getApplication() {
      return this._application;
   }

   public final int getSessionId() {
      return this._sessionId;
   }

   public final void setSessionId(int id) {
      this._sessionId = id;
   }

   public final String getContentType() {
      return this._contentType;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (!(target instanceof RIMMessagingOutgoingMessage)) {
         return false;
      }

      RIMMessagingOutgoingMessage outgoingMessage = (RIMMessagingOutgoingMessage)target;
      Parameters parameters = CMIMEUtilities.createContentDispositionParameters(outgoingMessage, this._filename);
      outgoingMessage.addAttachment(this._data, parameters, this._contentType);
      return true;
   }

   @Override
   public final int getId() {
      return this._id;
   }

   @Override
   public final int getType() {
      return 22;
   }

   @Override
   public final void pickle(DataBuffer db) {
      DataBuffer db2 = new DataBuffer();
      if (this._size > 15360) {
         EncodedImage image = EncodedImage.createEncodedImage(this._data, 0, this._data.length);
         image.setScaleX32(65536 * image.getWidth() / 320);
         image.setScaleY32(65536 * image.getHeight() / 240);
         Bitmap bitmap = image.getBitmap();
         JPEGEncodedImage encodedImg = new JPEGEncodedImage(bitmap, 45);
         this._data = encodedImg.getData();

         for (int quality = 40; this._data.length >= 15360 && quality >= 0; quality -= 5) {
            encodedImg = new JPEGEncodedImage(bitmap, quality);
            this._data = encodedImg.getData();
         }

         this._size = this._data.length;
         this._contentType = encodedImg.getMIMEType();
      }

      this.addStringToDataBuffer(db2, 1, this._contentType);
      if (this._filename != null) {
         this.addStringToDataBuffer(db2, 2, this._filename);
      }

      ConverterUtilities.writeInt(db2, 4, this._size);
      if (this._data != null) {
         ConverterUtilities.writeByteArray(db2, 3, this._data);
      }

      ConverterUtilities.writeInt(db2, 5, this._integer);
      if (this._url != null && this._url.length() > 0) {
         this.addStringToDataBuffer(db2, 6, this._url);
      }

      if (this._application != null && this._application.length() > 0) {
         this.addStringToDataBuffer(db2, 7, this._application);
      }

      ConverterUtilities.writeInt(db2, 8, this._id);
      ConverterUtilities.writeInt(db2, 9, this._sessionId);
      ConverterUtilities.writeEmptyField(db2, 0);
      db2.trim();
      this.appendDataBuffer(db, db2);
   }

   @Override
   public final void unPickle(DataBuffer db, int length) {
      int type;
      while (db.available() > 2 && (type = ConverterUtilities.getType(db, true)) != 0) {
         switch (type) {
            case 0:
               ConverterUtilities.skipField(db);
               break;
            case 1:
            default:
               this._contentType = ConverterUtilities.readString(db);
               break;
            case 2:
               this._filename = ConverterUtilities.readString(db);
               break;
            case 3:
               this._data = ConverterUtilities.readByteArray(db);
               break;
            case 4:
               this._size = ConverterUtilities.readInt(db);
               break;
            case 5:
               this._integer = ConverterUtilities.readInt(db);
               break;
            case 6:
               this._url = ConverterUtilities.readString(db);
               break;
            case 7:
               this._application = ConverterUtilities.readString(db);
               break;
            case 8:
               this._id = ConverterUtilities.readInt(db);
               break;
            case 9:
               this._sessionId = ConverterUtilities.readInt(db);
         }
      }

      ConverterUtilities.skipField(db);
   }

   public FileTransferBlob() {
   }

   public FileTransferBlob(String contentType, String filename, byte[] data, int integer, String url, String application) {
      this._contentType = contentType;
      this._filename = filename;
      this._data = data;
      this._size = data == null ? 0 : data.length;
      this._integer = integer;
      this._url = url;
      this._application = application;
      this._id = this.hashCode();
      this._sessionId = 0;
   }
}
