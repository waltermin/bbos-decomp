package net.rim.wica.runtime.metadata.internal.component.ui;

final class ControlMappingResolver$KeyGenerator {
   private StringBuffer _strBuf;
   private int _mappingIndex;
   private int[] _mapping;

   public ControlMappingResolver$KeyGenerator(int[] mapping) {
      this._mapping = mapping;
   }

   public final String getKey(int elementCount) {
      this._strBuf = new StringBuffer();

      for (this._mappingIndex = 0; this._mappingIndex < elementCount; this._mappingIndex++) {
         this._strBuf.append(this._mapping[this._mappingIndex]).append('.');
      }

      return this._strBuf.toString();
   }

   public final String nextKey() {
      if (this._mappingIndex == this._mapping.length) {
         return null;
      }

      this._strBuf.append(this._mapping[this._mappingIndex]).append('.');
      this._mappingIndex++;
      return this._strBuf.toString();
   }
}
