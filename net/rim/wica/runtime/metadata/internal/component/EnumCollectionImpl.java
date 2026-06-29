package net.rim.wica.runtime.metadata.internal.component;

import net.rim.wica.common.metadata.component.EnumCollection;
import net.rim.wica.runtime.metadata.internal.def.DataDefAccess;

public class EnumCollectionImpl implements EnumCollection {
   EnumCollection _standardEnums;
   DataDefAccess _metadataEnums;

   public EnumCollectionImpl(DataDefAccess metadataEnums, EnumCollection standardEnums) {
      this._metadataEnums = metadataEnums;
      this._standardEnums = standardEnums;
   }

   @Override
   public String[] getEnum(int defId) {
      return this._metadataEnums.hasDefinition(defId) ? this._metadataEnums.getEnum(defId) : this._standardEnums.getEnum(defId);
   }

   @Override
   public boolean isValidEnumValue(int defId, String value) {
      return this._metadataEnums.hasDefinition(defId) ? this._metadataEnums.isValidEnumValue(defId, value) : this._standardEnums.isValidEnumValue(defId, value);
   }

   @Override
   public int getEnumValueAsInt(int defId, String value) {
      return this._metadataEnums.hasDefinition(defId)
         ? this._metadataEnums.getEnumValueAsInt(defId, value)
         : this._standardEnums.getEnumValueAsInt(defId, value);
   }
}
