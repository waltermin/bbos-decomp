package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class TypeLists extends CodfileItem {
   private TypeList _markerTypeList;
   private TypeList _nullTypeList;
   private TypeList _emptyTypeList;
   private CodfileVectorHash _typeLists = new CodfileVectorHash(33);

   public TypeLists() {
      this._markerTypeList = new TypeList(-1);
      this._nullTypeList = new TypeList(0);
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      this._typeLists.write(out);
      this.setExtent(out);
   }

   public final TypeList getNullTypeList() {
      return this._nullTypeList;
   }

   public final TypeList getEmptyTypeList() {
      if (this._emptyTypeList == null) {
         TypeList typeList = new TypeList(0);
         this._emptyTypeList = (TypeList)this._typeLists.get(typeList);
         if (this._emptyTypeList == null) {
            this._emptyTypeList = typeList;
            this._typeLists.put(this._emptyTypeList, this._emptyTypeList);
         }
      }

      return this._emptyTypeList;
   }

   public final TypeList getTypeList(TypeList typeList, DataSection dataSection, boolean compressable) {
      if (typeList == this._markerTypeList) {
         return typeList;
      }

      if (typeList == this._nullTypeList) {
         return typeList;
      }

      TypeList existing = (TypeList)this._typeLists.get(typeList);
      if (existing == null) {
         typeList.makeSymbolic(dataSection);
         this._typeLists.put(typeList, typeList);
         existing = typeList;
      }

      existing.setCompressable(compressable);
      return existing;
   }
}
