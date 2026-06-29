package net.rim.device.internal.io.store;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.io.file.FileUtilities;

final class ContentStoreSyncCollection$ContentStoreSyncConverter implements SyncConverter {
   ContentStoreImpl _store;
   ContentStoreDatabase _database;
   private final ContentStoreSyncCollection this$0;
   private static final int FIELD_FILENAME = 1;
   private static final int FIELD_FOLDER = 2;
   private static final int FIELD_ATTRIBUTES = 3;
   private static final int FIELD_CONTENT_TYPE = 5;
   private static final int FIELD_EXT_ATTRIBUTES = 6;
   private static final int FIELD_CONTENT = 7;

   ContentStoreSyncCollection$ContentStoreSyncConverter(ContentStoreSyncCollection _1) {
      this.this$0 = _1;
      this._store = ContentStoreImpl.getInstance();
      this._database = this._store.getDatabase();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final boolean convert(SyncObject object, DataBuffer buffer, int version) {
      try {
         FolderTable folderTable = ContentStoreImpl.getInstance().getDatabase().getFolderTable();
         if (object instanceof FileImpl) {
            FileImpl entry = (FileImpl)object;
            ConverterUtilities.writeStringSmart(buffer, 1, ((FolderImpl)entry.getFolder()).getPath() + entry.getName());
            ByteArrayOutputStream osAttrib = new ByteArrayOutputStream();
            entry.writeHeader(new DataOutputStream(osAttrib));
            byte[] abAttrib = osAttrib.toByteArray();
            ConverterUtilities.writeByteArray(buffer, 6, abAttrib);
            if (object instanceof SymbolicLinkImpl) {
               ConverterUtilities.writeStringSmart(buffer, 5, "symlink");
            } else {
               InputStream stream = entry.openRawInputStream();
               ConverterUtilities.writeByteStream(buffer, 7, stream, stream.available());
               stream.close();
            }

            return true;
         }

         if (object instanceof FolderImpl) {
            FolderImpl entry = (FolderImpl)object;
            int parentId = entry.getFolder().getUID();
            ConverterUtilities.writeStringSmart(buffer, 1, entry.getPath());
            ConverterUtilities.writeInt(buffer, 3, entry.getAttributes());
            ConverterUtilities.writeStringSmart(buffer, 5, "folder");
            return true;
         }
      } catch (Throwable var10) {
         this.this$0.log(e);
         return false;
      }

      return false;
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final SyncObject convert(DataBuffer dataBuffer, int version, int uid) {
      if (version == 1 || version >= 1 && version <= 1) {
         try {
            String filename = null;
            int folderId = 0;
            String contentType = null;
            int attrib = 0;
            byte[] attributes = null;
            boolean hasContent = false;

            label413:
            while (true) {
               int type;
               try {
                  type = ConverterUtilities.getType(dataBuffer, true);
               } finally {
                  break;
               }

               switch (type) {
                  case 0:
                  case 4:
                     ConverterUtilities.skipField(dataBuffer);
                     break label413;
                  case 1:
                  default:
                     filename = ConverterUtilities.readString(dataBuffer, true);
                     break;
                  case 2:
                     folderId = ConverterUtilities.readInt(dataBuffer, true);
                     break;
                  case 3:
                     attrib = ConverterUtilities.readInt(dataBuffer, true);
                     break;
                  case 5:
                     contentType = ConverterUtilities.readString(dataBuffer, true);
                     break;
                  case 6:
                     attributes = ConverterUtilities.readByteArray(dataBuffer, true);
                     break;
                  case 7:
                     hasContent = true;
                     break label413;
               }
            }

            FolderTable folderTable = this._database.getFolderTable();
            if ("folder".equals(contentType)) {
               if (filename.length() == 0) {
                  this.this$0._folderMap = new IntIntHashtable();
                  this.this$0._folderMap.put(folderId, folderTable.getFolder("/").getId());
               }

               if (filename.indexOf(47) != -1) {
                  if (filename.length() == 1) {
                     folderId = uid;
                  } else {
                     String path = filename.substring(0, filename.lastIndexOf(47, filename.length() - 2) + 1);
                     FolderImpl parent = this._store.getFolder(path);
                     if (parent == null) {
                        folderTable.add(path, 4096, false);
                        parent = this._store.getFolder(path);
                     }

                     folderId = parent.getId();
                  }

                  filename = filename.substring(filename.lastIndexOf(47, filename.length() - 2) + 1, filename.length() - 1);
               } else if (this.this$0._folderMap == null) {
                  throw new IllegalStateException();
               }

               return new FolderImpl(uid, folderId, attrib, filename, uid);
            } else {
               FolderImpl folder;
               if (filename != null && filename.indexOf(47) != -1) {
                  String path = filename.substring(0, filename.lastIndexOf(47) + 1);
                  if (folderTable.getFolder(path) == null) {
                     folderTable.add(path, 4096, false);
                  }

                  folder = folderTable.getFolder(path);
                  filename = filename.substring(filename.lastIndexOf(47) + 1, filename.length());
               } else {
                  if (this.this$0._folderMap == null) {
                     throw new IllegalStateException();
                  }

                  folderId = this.this$0._folderMap.get(folderId);
                  folder = folderTable.getFolderForId(folderId);
               }

               String folderPath = folder.getPath();
               byte[] bytes = null;
               if (folderPath.equals(ContentStoreSyncCollection.USER_PICTURES)) {
                  FolderImpl samplePicturesFolder = folderTable.getFolder(ContentStoreSyncCollection.SAMPLE_PICTURES);
                  if (samplePicturesFolder != null) {
                     FileImpl file = samplePicturesFolder.getFile(filename);
                     if (file != null) {
                        if (hasContent) {
                           ByteArrayOutputStream content = new ByteArrayOutputStream();
                           ConverterUtilities.readByteStream(dataBuffer, true, content);
                           bytes = content.toByteArray();
                        }

                        int crc = CRC32.update(-1, bytes);
                        if (filename.equals(this._store.getFilenameFromCRC(crc))) {
                           return file;
                        }
                     }
                  }
               }

               boolean isSymLink = false;
               FileImpl file;
               if ("symlink".equals(contentType)) {
                  isSymLink = true;
                  file = new SymbolicLinkImpl(uid);
               } else {
                  file = new FileImpl(uid);
               }

               if (filename.indexOf(46) == -1) {
                  if (contentType != null) {
                     String ext = MIMETypeAssociations.getExtensionFromMIMEType(contentType);
                     if (ext != null) {
                        filename = filename + ext;
                     }
                  } else if (folderPath.equals(ContentStoreSyncCollection.USER_PICTURES)) {
                     filename = filename + ".jpg";
                  } else if (folderPath.equals(ContentStoreSyncCollection.USER_RINGTONES)) {
                     filename = filename + ".mid";
                  }
               }

               file.setFolder(folder, false, true);
               file.setName(FileUtilities.makeValidFilename(filename), false);
               if (attributes != null) {
                  file.readHeader(new DataInputStream(new ByteArrayInputStream(attributes)));
               }

               if (isSymLink) {
                  try {
                     Object linkName = file.getAttribute(FileImpl.ATTRIB_SYMLINK);
                     if (linkName != null) {
                        ((SymbolicLinkImpl)file).setLink((String)linkName);
                     }
                  } finally {
                     return file;
                  }
               } else {
                  file.truncate(0);
                  ByteArrayOutputStream ostream = new FileImpl$ReservableByteArrayOutputStream();
                  if (bytes == null) {
                     ConverterUtilities.readByteStream(dataBuffer, true, ostream);
                  } else {
                     ostream.write(bytes);
                  }

                  ostream.close();
                  boolean var26 = false /* VF: Semaphore variable */;

                  try {
                     var26 = true;
                     file.setContentSync(ostream.toByteArray());
                     var26 = false;
                  } finally {
                     if (var26) {
                        file.setAttributes(8192, 0);
                        return file;
                     }
                  }
               }

               return file;
            }
         } catch (Throwable var38) {
            this.this$0.log(e);
            return null;
         }
      } else {
         return null;
      }
   }
}
