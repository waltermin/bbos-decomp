package net.rim.plazmic.internal.mediaengine.io;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;
import net.rim.plazmic.internal.mediaengine.MediaFactory;
import net.rim.plazmic.internal.mediaengine.MediaTypes;
import net.rim.plazmic.internal.mediaengine.ResourceContext;
import net.rim.plazmic.internal.mediaengine.ResourceProvider;
import net.rim.plazmic.mediaengine.MediaException;

public class BundledMediaReader implements ResourceProvider {
   protected byte[] _bundleData;
   private int _bundleOffset;
   private int _major1Version;
   private int _major2Version;
   private int _minor1Version;
   protected String _mediaMimeType;
   protected int _mediaLength;
   protected int _numberOfResources;
   protected int[] _resourceLengths;
   protected String[] _resourceContentType;
   protected int[] _resourceOffsets;
   private int _currentResource;
   protected byte[] _mediaData;
   protected int _checksum;
   Hashtable _resourceHashTable = new Hashtable();
   protected ResourceProvider _resourceProvider;
   private static final String UNKNOWN_RESOURCE = "content/unknown";
   static final int SUPPORTED_MAJOR1_VERSION = 0;
   static final int SUPPORTED_MAJOR2_VERSION = 1;
   static final int SUPPORTED_MINOR1_VERSION = 6;
   static final int BUNDLE_START_HEADER = -548385470;
   protected static Hashtable _mediaReaders = new Hashtable();

   private Object read(Object m, byte[] data, int offset, ResourceContext context) {
      this._bundleData = data;
      this._bundleOffset = offset;
      this._checksum = 0;
      this.readHeader();
      this._mediaData = new byte[this._mediaLength];
      this.readMediaContent();
      this.validateChecksum();
      this._currentResource = 0;
      this._resourceHashTable.clear();
      return this.readMedia(this._mediaData, this._mediaMimeType, context);
   }

   private void readHeader() {
      this.readHeaderTag();
      this.readVersionInfo();
      this._mediaMimeType = this.readMimeType();
      this._mediaLength = this.read3ByteData();
      this._numberOfResources = this.readUnsignedByte();
      this._resourceLengths = new int[this._numberOfResources];
      this._resourceOffsets = new int[this._numberOfResources];
      this._resourceContentType = new String[this._numberOfResources];
      boolean hasContentTypes = true;
      if (this._major1Version == 0 && this._major2Version == 1 && this._minor1Version == 5) {
         hasContentTypes = false;
      }

      for (int i = 0; i < this._numberOfResources; i++) {
         if (hasContentTypes) {
            int contentTypeLength = this.readUnsignedByte() << 8;
            contentTypeLength |= this.readUnsignedByte();
            if (contentTypeLength == 0) {
               this._resourceContentType[i] = null;
            } else {
               this._resourceContentType[i] = this.readString(contentTypeLength);
            }
         }

         this._resourceLengths[i] = this.read3ByteData();
      }

      this.validateChecksum();
      this.readHeaderTerminator();
   }

   private void readHeaderTag() throws MediaException {
      if (this.readInt() != -548385470) {
         throw new MediaException(3);
      }
   }

   private void readVersionInfo() throws MediaException {
      this._major1Version = this.readUnsignedByte();
      this._major2Version = this.readUnsignedByte();
      this._minor1Version = this.readUnsignedByte();
      this.readUnsignedByte();
      if (this._major1Version > 0 || this._major2Version > 1 || this._minor1Version > 6) {
         throw new MediaException(1, this._major1Version + "." + this._major2Version + "." + this._minor1Version);
      } else if (this._major1Version < 0 || this._major2Version < 1) {
         throw new MediaException(2, this._major1Version + "." + this._major2Version + "." + this._minor1Version);
      }
   }

   private String readMimeType() {
      int b1 = this.readUnsignedByte();
      int b2 = this.readUnsignedByte();
      int mimeStringLength = (b1 << 8) + b2;
      return this.readString(mimeStringLength);
   }

   private void readHeaderTerminator() throws MediaException {
      if (this.readUnsignedByte() != 13 || this.readUnsignedByte() != 10 || this.readUnsignedByte() != 32 || this.readUnsignedByte() != 10) {
         throw new MediaException();
      }
   }

   private void readMediaContent() {
      this.readByteArray(this._mediaData);
   }

   private Object getResource(String url, String mediaType, ResourceContext context) throws MediaException {
      boolean hasContentType = true;
      if (this._major1Version == 0 && this._major2Version == 1 && this._minor1Version == 5) {
         hasContentType = false;
      }

      Object resource;
      if (!this._resourceHashTable.containsKey(url)) {
         if (this._resourceContentType[this._currentResource].equals("content/unknown")) {
            hasContentType = false;
         }

         if (hasContentType && MediaTypes.getTypeCategory(mediaType) != MediaTypes.getTypeCategory(this._resourceContentType[this._currentResource])) {
            throw new MediaException(8);
         }

         this._resourceHashTable.put(url, new Integer(this._currentResource));
         this._resourceOffsets[this._currentResource] = this._bundleOffset;
         resource = this.readResource(this._resourceLengths[this._currentResource], this._resourceContentType[this._currentResource], context);
         this._currentResource++;
         this.validateChecksum();
      } else {
         Integer resourceNumber = (Integer)this._resourceHashTable.get(url);
         int savedCurResource = this._currentResource;
         int savedOffset = this._bundleOffset;
         int savedChecksum = this._checksum;
         this._currentResource = resourceNumber;
         this._bundleOffset = this._resourceOffsets[this._currentResource];
         if (this._resourceContentType[this._currentResource].equals("content/unknown")) {
            hasContentType = false;
         }

         if (hasContentType && MediaTypes.getTypeCategory(mediaType) != MediaTypes.getTypeCategory(this._resourceContentType[this._currentResource])) {
            throw new MediaException(8);
         }

         resource = this.readResource(this._resourceLengths[this._currentResource], mediaType, context);
         this._bundleOffset = savedOffset;
         this._checksum = savedChecksum;
         this._currentResource = savedCurResource;
      }

      return resource;
   }

   private Object readResource(int length, String mimeType, ResourceContext context) throws MediaException {
      if (this._currentResource > this._numberOfResources) {
         throw new MediaException(4);
      }

      if (this._resourceLengths[this._currentResource] == 0) {
         int resourceErrorCode = this.readByte();
         return null;
      }

      byte[] resourceArray = new byte[this._resourceLengths[this._currentResource]];
      this.readByteArray(resourceArray);
      Object resource = null;

      try {
         ByteArrayInputStream bais = new ByteArrayInputStream(resourceArray);
         resource = this._resourceProvider.createResource(mimeType, bais, context, null);
      } finally {
         return resource;
      }

      return resource;
   }

   private void validateChecksum() throws MediaException {
      int readerChecksum = this._checksum;
      int b1 = this.readUnsignedByte();
      int b2 = this.readUnsignedByte();
      int fileChecksum = (b1 << 8) + b2;
      readerChecksum &= 65535;
      if (readerChecksum != fileChecksum) {
         throw new MediaException(10);
      }
   }

   private void readByteArray(byte[] byteArray) {
      int length = byteArray.length;
      System.arraycopy(this._bundleData, this._bundleOffset, byteArray, 0, length);
      this._bundleOffset += length;

      for (int i = 0; i < length; i++) {
         this._checksum = this._checksum + byteArray[i];
      }
   }

   private String readString(int length) {
      int oldOffset = this._bundleOffset;
      this._bundleOffset += length;

      for (int i = oldOffset; i < oldOffset + length; i++) {
         this._checksum = this._checksum + this._bundleData[i];
      }

      return new String(this._bundleData, oldOffset, length);
   }

   private int readInt() {
      int ch1 = this.readUnsignedByte();
      int ch2 = this.readUnsignedByte();
      int ch3 = this.readUnsignedByte();
      int ch4 = this.readUnsignedByte();
      return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
   }

   private int read3ByteData() {
      int b1 = this.readUnsignedByte();
      int b2 = this.readUnsignedByte();
      int b3 = this.readUnsignedByte();
      return (b1 << 16) + (b2 << 8) + b3;
   }

   private int readUnsignedByte() {
      return this.readByte() & 0xFF;
   }

   private byte readByte() throws MediaException {
      if (this._bundleOffset >= this._bundleData.length) {
         throw new MediaException(4);
      }

      byte value = this._bundleData[this._bundleOffset++];
      this._checksum += value;
      return value;
   }

   protected Object readMedia(byte[] mediaData, String contentType, ResourceContext context) throws MediaException {
      if (contentType == null) {
         contentType = "application/x-vnd.rim.pme";
      }

      ResourceProvider reader = MediaFactory.createResourceProvider(contentType, mediaData);
      if (reader == null) {
         throw new MediaException(3);
      } else {
         return reader.createResource(contentType, mediaData, context, this);
      }
   }

   @Override
   public synchronized Object createResource(String type, Object data, ResourceContext context, Object referrer) {
      if (referrer instanceof ResourceProvider) {
         this._resourceProvider = (ResourceProvider)referrer;
      }

      if ("application/x-vnd.rim.pme.b".equals(type)) {
         return this.read(null, (byte[])data, 0, context);
      } else {
         return this._resourceProvider != referrer && this._resourceProvider != null ? this._resourceProvider.createResource(type, data, context, null) : null;
      }
   }

   @Override
   public synchronized Object createResourceFromURI(String uri, String suggestedType, ResourceContext context, Object referrer) {
      Object object = this.getResource(uri, suggestedType, context);
      if (object == null && referrer != this._resourceProvider && this._resourceProvider != null) {
         object = this._resourceProvider.createResourceFromURI(uri, suggestedType, context, null);
      }

      return object;
   }
}
