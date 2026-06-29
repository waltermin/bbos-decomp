package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.collection.util.KeywordPrefixManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;

public final class CategoryModel implements PersistableRIMModel, SyncObject, ConversionProvider, EncryptableProvider {
   private int _uid;
   private int _id;
   private Object _nameEncoding;
   private String _key;
   static final int CATEGORY_NAME_TAG = 1;

   public final String getName() {
      try {
         return PersistentContent.decodeString(this._nameEncoding);
      } finally {
         ;
      }
   }

   final int getId() {
      return this._id;
   }

   public final String getKey() {
      return this._key;
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      return PersistentContent.checkEncoding(this._nameEncoding, false, encrypt);
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 105) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addField(1, this.getName());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      CategoryModel newModel = (CategoryModel)ObjectGroup.expandGroup(this);
      newModel._nameEncoding = PersistentContent.reEncode(this._nameEncoding, false, encrypt);
      ObjectGroup.createGroupIgnoreTooBig(newModel);
      return newModel;
   }

   static final int generateUID(String name) {
      return StringUtilities.toLowerCase(name, 1701707776).hashCode();
   }

   CategoryModel(String name) {
      this.establishNameAndUid(name);
      this._id = CategoryList.getInstance().generateId(name);
      this._key = generateKey(this._id);
   }

   private final void establishNameAndUid(String name) {
      if (name == null) {
         throw new IllegalArgumentException();
      }

      name = name.trim();
      if (name.length() <= 0) {
         throw new IllegalArgumentException();
      }

      this._nameEncoding = PersistentContent.encode(name, false, true);
      this._uid = generateUID(name);
   }

   CategoryModel(String name, int id) {
      this.establishNameAndUid(name);
      this._id = id;
      this._key = generateKey(this._id);
   }

   static final String generateKey(int id) {
      int numExtraChars;
      if (id <= 255) {
         numExtraChars = 0;
      } else if (id <= 8191) {
         numExtraChars = 1;
      } else if (id <= 262143) {
         numExtraChars = 2;
      } else if (id <= 8388607) {
         numExtraChars = 3;
      } else {
         numExtraChars = 4;
      }

      StringBuffer sb = new StringBuffer();
      sb.append('\u0001');
      int prefixCode = numExtraChars << 3 | (id & 224) >>> 5;
      sb.append(KeywordPrefixManager.getPrefixChar(prefixCode));
      prefixCode = id & 31;
      sb.append(KeywordPrefixManager.getPrefixChar(prefixCode));
      id >>>= 8;

      for (int i = 0; i < numExtraChars; i++) {
         prefixCode = id & 31;
         id >>>= 5;
         sb.append(KeywordPrefixManager.getPrefixChar(prefixCode));
      }

      return sb.toString();
   }
}
