package javax.microedition.global;

import java.io.ByteArrayOutputStream;
import net.rim.device.resources.Resource;

class ResourceFile {
   ResourceFile$Resource[] _resources;
   byte[] _filedata;
   public static final int RESOURCE_TYPE_STRING = 1;
   public static final int RESOURCE_TYPE_BINARY = 16;
   public static final int RESOURCE_TYPE_END = 0;
   private static String EMPTY = "";

   private ResourceFile(byte[] filedata, int headerlength) {
      int numresources = (headerlength >> 3) - 1;
      this._resources = new ResourceFile$Resource[numresources];

      for (int i = 0; i < numresources; i++) {
         this._resources[i] = new ResourceFile$Resource(filedata, (i + 1) * 8);
         if (i > 0) {
            this._resources[i - 1].length = this._resources[i].offset - this._resources[i - 1].offset;
         }
      }

      this._resources[numresources - 1].length = filedata.length - this._resources[numresources - 1].offset;
      this._filedata = filedata;
   }

   public static ResourceFile getResourceFile(String filename) {
      byte[] filedata = Resource.getResourceClass().getResource(filename);
      if (filedata == null) {
         throw new ResourceException(3, filename);
      } else if (filedata.length < 8) {
         throw new ResourceException(5, filename);
      } else if (filedata[0] != 238 && filedata[1] != 77 && filedata[2] != 73 && filedata[3] != 16) {
         throw new ResourceException(5, filename);
      } else {
         int headerlength = (filedata[4] & 255) << 24 | (filedata[5] & 255) << 16 | (filedata[6] & 255) << 8 | filedata[7] & 255;
         if (filedata.length < headerlength + 8) {
            throw new ResourceException(5, filename);
         } else {
            return new ResourceFile(filedata, headerlength);
         }
      }
   }

   private int getIndexForId(int id) {
      for (int i = this._resources.length - 1; i >= 0; i--) {
         if (this._resources[i].id == id) {
            return i;
         }

         if (this._resources[i].id < id) {
            return -1;
         }
      }

      return -1;
   }

   private ResourceFile$Resource getResource(int id) {
      int index = this.getIndexForId(id);
      if (index == -1) {
         throw new ResourceException(1, EMPTY);
      } else {
         return this._resources[index];
      }
   }

   public boolean isValidId(int id) {
      return this.getIndexForId(id) != -1;
   }

   public int getType(int id) {
      return this.getResource(id).type;
   }

   public Object getData(int id) {
      ResourceFile$Resource res = this.getResource(id);
      int length = res.length;
      ByteArrayOutputStream baos = new ByteArrayOutputStream(length);

      for (int i = 0; i < length; i++) {
         baos.write(this._filedata[i + res.offset]);
      }

      switch (res.type) {
         case 1:
            return baos.toString();
         case 16:
            return baos.toByteArray();
         default:
            throw new ResourceException(6, EMPTY);
      }
   }
}
