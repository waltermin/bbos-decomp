package net.rim.tid.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.rms.RecordStore;
import net.rim.tid.io.ContinuousInputStream;
import net.rim.tid.io.RMSOutputStream;

public class ResourceSpec implements IResourceSpec {
   private static final String protocol = "file:";

   protected int findID(RecordStore rs, byte[] header, String name, boolean createIfNeed) {
      ByteArrayInputStream bais = (ByteArrayInputStream)(new Object(header));
      DataInputStream da = (DataInputStream)(new Object(bais));
      int recordsCount = da.readChar();
      StringBuffer sb = (StringBuffer)(new Object());

      for (int i = 0; i < recordsCount; i++) {
         sb.setLength(0);
         int lengthOfName = da.readChar();

         for (int j = 0; j < lengthOfName; j++) {
            sb.append(da.readChar());
         }

         int id = da.readInt();
         if (sb.toString().equals(name)) {
            return id;
         }
      }

      int id = rs.addRecord(new byte[0], 0, 0);
      if (!createIfNeed) {
         return -1;
      }

      ByteArrayOutputStream baos = (ByteArrayOutputStream)(new Object());
      DataOutputStream dos = (DataOutputStream)(new Object(baos));
      dos.writeShort(recordsCount + 1);
      dos.write(header, 2, header.length - 2);
      dos.writeShort(name.length());
      dos.writeChars(name);
      dos.writeInt(id);
      header = baos.toByteArray();
      rs.setRecord(1, header, 0, header.length);
      return id;
   }

   protected int getInRecordId(RecordStore rs, String name) {
      if (rs.getNumRecords() == 0) {
         return -1;
      }

      byte[] header = rs.getRecord(1);
      return this.findID(rs, header, name, false);
   }

   String deleteProtocol(String location) {
      String result = location;
      if (location.length() > 5 && location.substring(0, 5).equals("file:")) {
         result = location.substring(5);
      }

      return result;
   }

   public ContinuousInputStream getInputStream(String location, Class toLoad) {
      location = this.deleteProtocol(location);
      int extIndex = location.lastIndexOf(46);
      return extIndex != -1 && location.substring(extIndex + 1).equals("lwrd") ? this.getLearningInputStream(location) : this.getInputStream0(location, toLoad);
   }

   protected ContinuousInputStream getInputStream0(String location, Class toLoad) {
      return null;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected ContinuousInputStream getLearningInputStream(String location) {
      try {
         RecordStore rs = RecordStore.openRecordStore("LearningStore", true);
         int id = this.getInRecordId(rs, location);
         if (id == -1) {
            return null;
         }

         byte[] data = rs.getRecord(id);
         return new ContinuousInputStream(data);
      } catch (Throwable var7) {
         System.err.println(((StringBuffer)(new Object("ContinuousInputStream getLearningInputStream::"))).append(e).toString());
         return null;
      }
   }

   protected int getOutRecordId(RecordStore rs, String name) {
      if (rs.getNumRecords() == 0) {
         rs.addRecord(new byte[]{0, 0}, 0, 2);
      }

      byte[] header = rs.getRecord(1);
      return this.findID(rs, header, name, true);
   }

   protected void resetLearning() {
      try {
         RecordStore rs = RecordStore.openRecordStore("LearningStore", true);
         rs.closeRecordStore();
         RecordStore.deleteRecordStore("LearningStore");
      } finally {
         return;
      }
   }

   @Override
   public OutputStream getOutputStream(String location) {
      RecordStore rs = null;

      try {
         rs = RecordStore.openRecordStore("LearningStore", true);
         int id = this.getOutRecordId(rs, location);
         return new RMSOutputStream(rs, id);
      } finally {
         this.resetLearning();
         return null;
      }
   }

   @Override
   public String getProtocol() {
      return "file:";
   }

   @Override
   public ContinuousInputStream getInputStream(String location, Class toLoad, boolean needCache) {
      return this.getInputStream(location, toLoad);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private byte[] loadData(InputStream is) {
      ByteArrayOutputStream bos = (ByteArrayOutputStream)(new Object());
      int blockSize = is.available();
      if (blockSize == 0) {
         blockSize = 5000;
      }

      byte[] buf = null;

      do {
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            buf = new byte[blockSize];
            var7 = false;
         } finally {
            if (var7) {
               blockSize /= 2;
               continue;
            }
         }
      } while (buf == null && blockSize > 0);

      if (blockSize == 0) {
         return null;
      }

      while (true) {
         int read = is.read(buf);
         if (read <= 0) {
            return bos.toByteArray();
         }

         bos.write(buf, 0, read);
      }
   }
}
