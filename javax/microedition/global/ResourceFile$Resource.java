package javax.microedition.global;

class ResourceFile$Resource {
   int id;
   int type;
   int offset;
   int length;
   Object data;

   public ResourceFile$Resource(byte[] header, int start) {
      this.id = (header[start] & 255) << 24 | (header[start + 1] & 255) << 16 | (header[start + 2] & 255) << 8 | header[start + 3] & 255;
      this.type = header[start + 4];
      this.offset = (header[start + 5] & 255) << 16 | (header[start + 6] & 255) << 8 | header[start + 7] & 255;
      this.length = 0;
      this.data = null;
   }
}
