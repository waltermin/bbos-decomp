package net.rim.device.internal.io.store;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.io.FilenameValidator;
import net.rim.device.internal.io.file.FileSystem;
import net.rim.device.internal.io.file.FileSystemOptions;
import net.rim.device.internal.io.file.FileUtilities;
import net.rim.device.internal.system.DRMServices;
import net.rim.vm.Array;
import net.rim.vm.Memory;

class FileImpl implements FSDescriptor, Persistable, SyncObject {
   private int _uid;
   private int _folder;
   private String _name;
   private String _bindName;
   private String _originalUrl;
   private int _attributes;
   private int _drm;
   private long _timeCreate;
   private long _timeModify;
   private long _timeExpiry = Long.MAX_VALUE;
   private Hashtable _eattributes;
   private Object _contentLock = new Object();
   private byte[] _content;
   private int _contentLength;
   private CodeSigningKey _codeSigningKey;
   public static final int DRM_UNPROTECTED;
   public static final int DRM_PROTECTED;
   public static final int DRM_NO_COPY;
   public static final int DRM_NO_MODIFY;
   public static final byte HEADER_VERSION;
   public static final int TYPE_BYTEARRAY;
   public static final int TYPE_INT;
   public static final int TYPE_LONG;
   public static final int TYPE_STRING;
   private static final int MAX_CONTIGUOUS_ARRAY_SIZE;
   public static String ATTRIB_NAME = "name";
   public static String ATTRIB_FOLDER = "folder";
   public static String ATTRIB_ORIGINAL_URL = "x-rim-original-url";
   public static String ATTRIB_CONTENT_TYPE = "content-type";
   public static String ATTRIB_ATTRIB = "x-rim-attributes";
   public static String ATTRIB_DRM = "x-rim-drm";
   public static String ATTRIB_MEDIA = "x-rim-media";
   public static String ATTRIB_SYMLINK = "symlink";
   public static String ATTRIB_TIME_CREATE = "date";
   public static String ATTRIB_TIME_MODIFY = "last-modified";
   public static String ATTRIB_TIME_EXPIRY = "expires";
   private static boolean _onSync;
   private static final int ARCHIVE_ATTR_ON;
   private static final int ARCHIVE_ATTR_OFF;

   void commit() {
      ContentStoreDatabase.getInstance().commit(this, false);
   }

   void convertToBytes(byte[] output, int offset, int length) {
   }

   public void setCodeSigningKey(CodeSigningKey key) {
      if (this._codeSigningKey == null) {
         this._codeSigningKey = key;
      }
   }

   @Override
   public int getUID() {
      return this._uid;
   }

   public CodeSigningKey getCodeSigningKey() {
      return this._codeSigningKey;
   }

   public int getDrmAttributes() {
      return this._drm;
   }

   void writeHeader(DataOutputStream out) {
      out.write(1);
      this.writeHeader("x-rim-original-url", this._originalUrl, out);
      this.writeHeader("x-rim-attributes", this._attributes, out);
      this.writeHeader("x-rim-drm", this._drm, out);
      this.writeHeader("date", this._timeCreate, out);
      this.writeHeader("last-modified", this._timeModify, out);
      this.writeHeader("expires", this._timeExpiry, out);
      if (this._eattributes != null) {
         Enumeration enumeration = this._eattributes.keys();

         while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            Object value = this._eattributes.get(key);
            if (value instanceof Object) {
               this.writeHeader(key, (String)value, out);
            } else if (value instanceof byte[]) {
               this.writeHeader(key, (byte[])value, out);
            } else if (value instanceof Object) {
               this.writeHeader(key, value, out);
            } else {
               if (!(value instanceof Object)) {
                  throw new Object();
               }

               this.writeHeader(key, value, out);
            }
         }
      }
   }

   public int getFolderId() {
      return this._folder;
   }

   public long getLength() {
      return this._contentLength;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   void readHeader(DataInputStream in) {
      int version = in.read();
      if (version == 1) {
         while (true) {
            boolean var9 = false /* VF: Semaphore variable */;

            String key;
            try {
               var9 = true;
               key = in.readUTF();
               var9 = false;
            } finally {
               if (var9) {
                  return;
               }
            }

            int type = in.read();
            int length = in.readInt();
            Object value = null;
            switch (type) {
               case -1:
                  break;
               case 0:
               default:
                  byte[] bytes = new byte[length];
                  in.readFully(bytes);
                  value = bytes;
                  break;
               case 1:
                  if (length != 4) {
                     throw new Object("Invalid length");
                  }

                  value = new Object(in.readInt());
                  break;
               case 2:
                  if (length != 8) {
                     throw new Object("Invalid length");
                  }

                  value = new Object(in.readLong());
                  break;
               case 3:
                  value = in.readUTF();
            }

            this.setAttributeInternal(key, value, false);
         }
      }
   }

   public long getTimeCreate() {
      return this._timeCreate;
   }

   public long getTimeExpiry() {
      return this._timeExpiry;
   }

   public long getTimeModify() {
      return this._timeModify;
   }

   public void setTimeModify(long timeModify) {
      this._timeModify = timeModify;
      this.commit();
   }

   public void setTimeCreate(long timeCreate) {
      this._timeCreate = timeCreate;
      this.commit();
   }

   public boolean isArchivable() {
      return (this._attributes & 32) == 32 && (this._attributes & 2052) == 0;
   }

   public InputStream openInputStream() {
      return (InputStream)(this.hasContent() ? new FileImpl$FileImplInputStream(this) : new Object(new byte[0]));
   }

   public InputStream openRawInputStream() {
      if (!this.hasContent()) {
         InputStream data = (InputStream)(new Object(new byte[0]));
         return data;
      } else if ((this.getDrmAttributes() & 1) != 0) {
         InputStream data = (InputStream)(new Object(ContentStoreEncryption.encrypt(this.openInputStream())));
         return data;
      } else {
         return this.openInputStream();
      }
   }

   public long getRawLength() {
      if (!this.hasContent()) {
         return 0;
      } else {
         return (this.getDrmAttributes() & 1) != 0 ? ContentStoreEncryption.getEncryptedLength((int)this.getLength()) : this.getLength();
      }
   }

   public OutputStream openOutputStream(long offset) {
      if (offset < 0) {
         throw new Object();
      }

      if (offset > this._contentLength) {
         offset = this._contentLength;
      }

      FileImpl$FileImplOutputStream stream = new FileImpl$FileImplOutputStream(this, (int)offset);
      return new AutoCloseOutputStream(stream);
   }

   void purge() {
   }

   void setName(String name, boolean notify) {
      synchronized (ContentStoreImpl.getInstance().getMonitor()) {
         int error = validateName(name);
         if (error != 0) {
            throw new Object(error);
         }

         String oldPath = this.getJSRPath();
         this._name = name;
         this._bindName = IOUtilities.getBindName(name);
         if (notify) {
            this.commit();
            FileSystem.addFileJournalEntry(this.getJSRPath(), oldPath, 3);
         }
      }
   }

   public void setLastModified(long time) {
   }

   void setFolder(FolderImpl folder, boolean notify, boolean isSystem) {
      if (!isSystem) {
         FolderTable folderTable = ContentStoreDatabase.getInstance().getFolderTable();
         folderTable.assertOperation(folder, 0);
         if (!notify) {
            this._attributes = folder.getAttributes();
            this._attributes &= 134217700;
            this._attributes |= 268435459;
         }
      }

      String oldPath = this.getJSRPath();
      this._folder = folder.getId();
      if (notify) {
         this.commit();
         FileSystem.addFileJournalEntry(this.getJSRPath(), oldPath, 3);
      }
   }

   public synchronized void setAttribute(String key, String value) {
      synchronized (ContentStoreImpl.getInstance().getMonitor()) {
         this.setAttributeInternal(key, value, true);
      }
   }

   public void setDrmAttributes(int on, int off) {
      if ((on & off) != 0) {
         throw new Object();
      }

      if ((off & 1) != 0 && (this._drm & 1) != 0 && this.hasContent()) {
         throw new Object("Access denied to remove DRM.");
      }

      int drm = (this._drm | on) & ~off;
      this._drm = drm;
      this.commit();
   }

   public void truncate(long bytes) {
      synchronized (ContentStoreImpl.getInstance().getMonitor()) {
         if (bytes < this._contentLength) {
            if (bytes > Integer.MAX_VALUE) {
               throw new Object("offset too large");
            }

            if (this._content == null) {
               throw new Object();
            }

            this.setContentInternal(Arrays.copy(this._content, 0, (int)bytes));
            FileSystem.addFileJournalEntry(this.getJSRPath(), 2);
         }
      }
   }

   void setContentSync(Object object) {
      _onSync = true;
      if (object instanceof byte[]) {
         this.setBytes((byte[])object, true);
      } else {
         this.setContent(object);
      }

      _onSync = false;
   }

   public void setContent(Object object) {
      this.setContent(object, true);
   }

   void setContent(Object object, boolean notify) {
      byte[] bytes;
      if (!(object instanceof byte[])) {
         if (!(object instanceof Object)) {
            if (!(object instanceof Object)) {
               if (object != null) {
                  throw new Object("Unrecognized content.");
               }

               bytes = null;
            } else {
               String string = (String)object;
               bytes = new byte[2 * string.length() + 2];
               int length = StringUtilities.codeBOM(string, 0, string.length(), bytes, 0);
               Array.resize(bytes, length);
            }
         } else {
            bytes = IOUtilities.streamToBytes((InputStream)object);
         }
      } else {
         bytes = Arrays.copy((byte[])object);
      }

      this.setBytes(bytes, notify);
   }

   byte[] getSimKey() {
      return DRMServices.getSubscriberKey();
   }

   void setDrmAttributes(int drm) {
      if ((drm & 1) == 0 && (this._drm & 1) != 0 && this.hasContent()) {
         throw new Object("Access denied to remove DRM.");
      }

      this._drm = drm;
      this.commit();
   }

   @Override
   public void setAttributes(int on, int off) {
      if ((on & off) != 0) {
         throw new Object();
      }

      this._attributes = (this._attributes | on) & ~off | 268435456;
      this.commit();
   }

   @Override
   public void resurrect() {
      synchronized (ContentStoreImpl.getInstance().getMonitor()) {
         if (this.isAlive()) {
            throw new Object();
         }

         FolderImpl folder = this.getFolder();
         if (!folder.isAlive()) {
            folder.resurrect();
         }

         this._attributes |= 32;
         this.commit();
         this.notifyTable(0);
         ContentStoreImpl.getInstance().getSyncCollection().fileAdded(this);
         FileSystem.addFileJournalEntry(this.getJSRPath(), 0);
      }
   }

   @Override
   public String getJSRPath() {
      FolderImpl folder = this.getFolder();
      if (folder != null) {
         try {
            return FileUtilities.encodeString(((StringBuffer)(new Object("/store"))).append(folder.getPath()).append(this.getName()).toString());
         } finally {
            return "";
         }
      } else {
         return "";
      }
   }

   @Override
   public void setName(String name) {
      this.setName(name, true);
   }

   @Override
   public void remove() {
      synchronized (ContentStoreImpl.getInstance().getMonitor()) {
         if (!this.isAlive()) {
            throw new Object();
         }

         this._attributes &= -33;
         this.commit();
         this.notifyTable(1);
         ContentStoreImpl.getInstance().getSyncCollection().fileRemoved(this);
         if (!(this instanceof SymbolicLinkImpl)) {
            ContentStoreImpl.getInstance().updateQuotaUsed(this, -this.getLength());
         }

         FileSystem.addFileJournalEntry(this.getJSRPath(), 1);
      }
   }

   @Override
   public boolean isAlive() {
      return (this._attributes & 32) != 0;
   }

   @Override
   public String getAttribute(String name) {
      if (name.equals("name")) {
         return this.getName();
      } else {
         return (String)(this._eattributes != null ? this._eattributes.get(name) : null);
      }
   }

   @Override
   public String getName() {
      return this._name;
   }

   @Override
   public FolderImpl getFolder() {
      return ContentStoreDatabase.getInstance().getFolderTable().getFolderForId(this._folder);
   }

   @Override
   public String getBindName() {
      return this._bindName;
   }

   @Override
   public int getAttributes() {
      return this._attributes;
   }

   protected FileImpl(int uid) {
      this._attributes = 268435488;
      if (uid != 0) {
         this._uid = uid;
      } else {
         this._uid = UIDGenerator.getUID();
      }

      this._timeCreate = this._timeModify = System.currentTimeMillis();
   }

   private void setBytes(byte[] bytes, boolean notify) {
      if (bytes == null) {
         this.setContentInternal(null);
      } else {
         synchronized (ContentStoreImpl.getInstance().getMonitor()) {
            if ((this.getDrmAttributes() & 1) != 0) {
               if (bytes.length > 2 && bytes[0] == 68 && bytes[1] == 82 && bytes[2] == 77) {
                  this.setContentInternal(ContentStoreEncryption.decrypt(bytes));
               } else {
                  this.setContentInternal(bytes);
               }
            } else {
               this.setContentInternal(bytes);
            }

            this.setTimeModify();
            if (notify) {
               this.commit();
            }
         }
      }
   }

   private void notifyTable(int action) {
      FileTable table = ContentStoreImpl.getInstance().getDatabase().getFileTable();
      table.notifyTable(action, this);
   }

   private long setAttributeInternal_GetDate(Object value, long def) {
      if (value == null) {
         return def;
      } else {
         return !(value instanceof Object) ? Long.parseLong((String)value) : value;
      }
   }

   private synchronized void setAttributeInternal(String key, Object value, boolean notify) {
      if (key.equals(ATTRIB_NAME)) {
         this.setName((String)value);
      } else if (key.equals(ATTRIB_FOLDER)) {
         if (value instanceof Object) {
            this._folder = value;
         } else {
            this._folder = ContentStoreImpl.getInstance().getFolder(key).getId();
         }
      } else if (key.equals(ATTRIB_ORIGINAL_URL)) {
         this._originalUrl = (String)value;
      } else if (key.equals(ATTRIB_ATTRIB)) {
         this._attributes = this.setAttributeInternal_GetInt(value, 268435488) | 268435456;
      } else if (key.equals(ATTRIB_DRM)) {
         this._drm = this.setAttributeInternal_GetInt(value, 0);
      } else if (key.equals(ATTRIB_TIME_CREATE)) {
         this._timeCreate = this.setAttributeInternal_GetDate(value, System.currentTimeMillis());
      } else if (key.equals(ATTRIB_TIME_MODIFY)) {
         this._timeModify = this.setAttributeInternal_GetDate(value, System.currentTimeMillis());
      } else if (key.equals(ATTRIB_TIME_EXPIRY)) {
         this._timeExpiry = this.setAttributeInternal_GetDate(value, Long.MAX_VALUE);
      } else if (value != null || this._eattributes != null) {
         if (this._eattributes == null) {
            this._eattributes = (Hashtable)(new Object());
         }

         if (value != null) {
            this._eattributes.put(key, value);
         } else {
            this._eattributes.remove(key);
         }

         if (this._eattributes.size() == 0) {
            this._eattributes = null;
         }
      }

      if (notify) {
         this.commit();
      }
   }

   private void setTimeModify() {
      this.setTimeModify(System.currentTimeMillis());
   }

   public static boolean validateNameChar(char character) {
      return FilenameValidator.validateChar(character);
   }

   public static int validateName(String name) {
      if (name != null && name.length() != 0 && name.length() <= 255) {
         for (int lv = name.length() - 1; lv >= 0; lv--) {
            if (!validateNameChar(name.charAt(lv))) {
               return 1004;
            }
         }

         return 0;
      } else {
         return 1004;
      }
   }

   private void setContentInternal(byte[] bytes, int length, boolean allowGroup) {
      try {
         synchronized (this._contentLock) {
            int oldContentLength = this._contentLength;
            this._contentLength = verifyLength(length);
            if (this._contentLength != 0) {
               if (this._contentLength <= 4096) {
                  if (Array.getSectionSize(bytes) < 4096) {
                     Array.setSectionSize(bytes, bytes.length);
                  }
               } else if (allowGroup && this._contentLength <= Memory.getMaxGroupSize()) {
                  Memory.createGroup(bytes);
               } else if (Array.getSectionSize(bytes) != 4096) {
                  Array.setSectionSize(bytes, 4096);
               }
            }

            if (bytes != null && !_onSync && length >= oldContentLength && !ContentStoreImpl.getInstance().isQuotaAvailable(this, length - oldContentLength)) {
               this._contentLength = oldContentLength;
               throw new Object(9);
            }

            if (this._content == null) {
               ContentStoreImpl.getInstance().updateQuotaUsed(this, this.getLength());
            } else if (bytes != null && !this.isAlive()) {
               ContentStoreImpl.getInstance().updateQuotaUsed(this, oldContentLength + (length - oldContentLength));
            } else if (bytes != null) {
               ContentStoreImpl.getInstance().updateQuotaUsed(this, length - oldContentLength);
            }

            this._content = bytes;
         }

         ContentStoreDatabase.getInstance().commit(this._content, true);
      } finally {
         throw new Object(9);
      }
   }

   private int setAttributeInternal_GetInt(Object value, int def) {
      if (value == null) {
         return def;
      } else {
         return !(value instanceof Object) ? Integer.parseInt((String)value) : value;
      }
   }

   private void writeHeader(String key, byte[] value, DataOutputStream out) {
      if (value != null) {
         out.writeUTF(key);
         out.write(0);
         out.writeInt(value.length);
         out.write(value);
      }
   }

   private void writeHeader(String key, int value, DataOutputStream out) {
      out.writeUTF(key);
      out.write(1);
      out.writeInt(4);
      out.writeInt(value);
   }

   private void writeHeader(String key, long value, DataOutputStream out) {
      out.writeUTF(key);
      out.write(2);
      out.writeInt(8);
      out.writeLong(value);
   }

   private void writeHeader(String key, String value, DataOutputStream out) {
      if (value != null) {
         out.writeUTF(key);
         out.write(3);
         out.writeInt(value.length());
         out.writeUTF(value);
      }
   }

   private void setContentInternal(byte[] bytes) {
      this.setContentInternal(bytes, bytes == null ? 0 : bytes.length, true);
   }

   private boolean hasContent() {
      return this.getLength() > 0;
   }

   static int verifyLength(long length) {
      if (length > FileSystemOptions.getContentStoreMaxFileSize()) {
         throw new Object(1008);
      } else {
         return (int)length;
      }
   }
}
