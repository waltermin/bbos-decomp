package net.rim.tools.compiler;

import java.io.InputStream;
import net.rim.tools.compiler.io.StructuredInputStream;

public class ResourceFile {
   protected String _absoluteName;
   protected String _relativeName;
   protected byte[] _data;
   protected boolean _slice;
   public static final int MAX_RESOURCE_SIZE = 61440;

   public ResourceFile() {
      this._relativeName = "";
      this._absoluteName = this._relativeName;
   }

   public ResourceFile(String name, byte[] data, boolean slice) {
      this._relativeName = name;
      this._absoluteName = name;
      this._data = data;
      this._slice = slice;
   }

   public ResourceFile(String name, InputStream in, int size) {
      if (name.startsWith("/")) {
         name = name.substring(1);
      }

      this._relativeName = name;
      this._absoluteName = name;
      this.readData(in, size, name);
   }

   private void readData(InputStream in, int length, String name) {
      this._data = StructuredInputStream.readAll(in, length, name);
   }

   public String getRelativeName() {
      return this._relativeName;
   }

   public boolean isEmpty() {
      return this._data == null;
   }

   public boolean isSlice() {
      return this._slice;
   }

   public void resetData(byte[] data) {
      this._data = data;
   }

   public byte[] getData() {
      return this._data;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }

      if (obj instanceof Object) {
         return this._relativeName != null && this._relativeName.equals(obj) ? true : this._absoluteName.equals(obj);
      }

      if (!(obj instanceof ResourceFile)) {
         return false;
      }

      ResourceFile rf = (ResourceFile)obj;
      return this._relativeName != null && this._relativeName.equals(rf._relativeName) ? true : this._absoluteName.equals(rf._absoluteName);
   }

   @Override
   public int hashCode() {
      return this._relativeName != null ? this._relativeName.hashCode() : this._absoluteName.hashCode();
   }
}
