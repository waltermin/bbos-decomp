package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.apps.api.framework.model.CloneProvider;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.vm.Array;

public final class CategoriesModel implements PersistableRIMModel, FieldProvider, KeyProvider, ConversionProvider, VerbProvider, CloneProvider {
   private int[] _categoryIds = new int[0];

   public final String getCategoryNames(boolean forSync) {
      return getCategoryNames(this._categoryIds, forSync);
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      if (ContextObject.getFlag(context, 87)) {
         return null;
      } else {
         Field uiField = (Field)ContextObject.get(context, 9045827404276417370L);
         Array.resize(verbs, 0);
         if ((uiField instanceof CategoriesField || uiField instanceof Object) && uiField.getCookie() instanceof CategoriesModel) {
            Verb verb = new DisplayCategoriesForFieldVerb(uiField);
            Array.resize(verbs, 1);
            verbs[0] = verb;
            return verb;
         } else {
            return null;
         }
      }
   }

   public final String getCategoryKeys() {
      CategoryList categoryList = CategoryList.getInstance();
      return categoryList.getCategoryKeys(this._categoryIds);
   }

   public final void getCategoryIds(int[] categoryIds) {
      Array.resize(categoryIds, this._categoryIds.length);
      System.arraycopy(this._categoryIds, 0, categoryIds, 0, categoryIds.length);
   }

   @Override
   public final int getOrder(Object context) {
      if (ContextObject.getFlag(context, 11)) {
         return ContextObject.getFlag(context, 0) ? 4500 : 5500;
      } else {
         return ContextObject.getFlag(context, 28) ? 13400 : 0;
      }
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 19)) {
         int fieldId = -1;
         if (ContextObject.getFlag(context, 11)) {
            fieldId = 59;
         } else if (ContextObject.getFlag(context, 28)) {
            fieldId = 17;
         } else if (ContextObject.getFlag(context, 8)) {
            fieldId = 4;
         }

         if (fieldId != -1) {
            ((SyncBuffer)target).addField(fieldId, getCategoryNames(this._categoryIds, true));
            return true;
         }
      }

      return false;
   }

   @Override
   public final Object clone(Object context) {
      ContextObject co = ContextObject.castOrCreate(context);
      ContextObject.put(co, 254, this);
      return new CategoriesModel(co);
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      int numCategories = this._categoryIds.length;
      if (index + numCategories > keyArray.length) {
         Array.resize(keyArray, index + numCategories);
      }

      CategoryList categoryList = CategoryList.getInstance();
      int numKeysAdded = 0;

      for (int i = 0; i < numCategories; i++) {
         String key = categoryList.getCategoryKey(this._categoryIds[i]);
         if (key != null) {
            keyArray[index++] = key;
            numKeysAdded++;
         }
      }

      if (numKeysAdded != numCategories) {
         Array.resize(keyArray, index);
      }

      return numKeysAdded;
   }

   @Override
   public final int getKeys(Object context, int[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final int getKeys(Object context, long[] keyArray, int index, long keyRequested) {
      return 0;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      String categoryNames = null;
      if (!(field instanceof CategoriesField)) {
         if (field instanceof Object) {
            categoryNames = ((BasicEditField)field).getText().trim();
         }
      } else {
         categoryNames = ((CategoriesField)field).getCategoryNames();
      }

      return categoryNames != null ? this.populateCategoryIds(categoryNames) > 0 : false;
   }

   @Override
   public final Field getField(Object context) {
      String label = null;
      if (!ContextObject.getFlag(context, 1)) {
         label = (String)ContextObject.get(context, 3986845832244503196L);
         if (label == null) {
            label = CommonResources.getString(9102);
         }
      }

      String categoryNames = getCategoryNames(this._categoryIds, false);
      boolean editable = ContextObject.getFlag(context, 0);
      if (!editable && categoryNames == null) {
         return null;
      }

      CategoriesField fieldToReturn = new CategoriesField(label, categoryNames);
      fieldToReturn.setCookie(this);
      fieldToReturn.setEditable(editable);
      if (ContextObject.getFlag(context, 8)) {
         fieldToReturn.setVisible(false);
      }

      return fieldToReturn;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   private final int populateCategoryIds(String categoryNames) {
      return CategoryList.getInstance().getCategoryIds(categoryNames, this._categoryIds, true);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final byte[] compressCategories(byte[] data, int offset, int length, String encoding) {
      byte[] compressedData = null;
      String categoryNames = null;
      boolean var11 = false /* VF: Semaphore variable */;

      label49:
      try {
         var11 = true;
         categoryNames = (String)(encoding != null ? new Object(data, offset, length - 1, encoding) : new Object(data, offset, length - 1));
         var11 = false;
      } finally {
         if (var11) {
            categoryNames = (String)(new Object(data, offset, length - 1));
            break label49;
         }
      }

      int[] categoryIds = new int[0];
      int numCategories = CategoryList.getInstance().getCategoryIds(categoryNames, categoryIds, true);
      if (numCategories > 0) {
         DataBuffer db = (DataBuffer)(new Object(true));

         for (int i = 0; i < numCategories; i++) {
            db.writeCompressedInt(categoryIds[i]);
         }

         compressedData = db.toArray();
      }

      return compressedData;
   }

   public CategoriesModel() {
   }

   private static final String getCategoryNames(int[] categoryIds, boolean forSync) {
      if (categoryIds == null) {
         return null;
      }

      String categoryNames = CategoryList.getInstance().getCategoryNames(categoryIds, forSync);
      if (forSync && categoryNames == null) {
         categoryNames = "";
      }

      return categoryNames;
   }

   public static final byte[] compressCategories(byte[] data, int offset, int length) {
      return compressCategories(data, offset, length, null);
   }

   public static final byte[] uncompressCategories(byte[] data, int offset, int length) {
      return uncompressCategories(data, offset, length, null);
   }

   public CategoriesModel(Object initialData) {
      if (initialData instanceof Object) {
         this.populateCategoryIds((String)initialData);
      } else {
         ContextObject contextObject = ContextObject.verifyNonNull(initialData);
         Object data = contextObject.get(254);
         if (!(data instanceof CategoriesModel)) {
            data = contextObject.get(253);
            if (data != null) {
               this.populateCategoryIds((String)data);
            }
         } else {
            ((CategoriesModel)data).getCategoryIds(this._categoryIds);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final byte[] uncompressCategories(byte[] data, int offset, int length, String encoding) {
      DataBuffer db = (DataBuffer)(new Object(data, offset, length, true));
      int[] categoryIds = new int[0];
      int numCategoryIds = 0;

      label76:
      try {
         while (!db.eof()) {
            Array.resize(categoryIds, numCategoryIds + 1);
            categoryIds[numCategoryIds++] = db.readCompressedInt();
         }
      } finally {
         break label76;
      }

      if (numCategoryIds > 0) {
         byte[] categories = null;
         boolean var11 = false /* VF: Semaphore variable */;

         try {
            var11 = true;
            categories = encoding != null ? getCategoryNames(categoryIds, true).getBytes(encoding) : getCategoryNames(categoryIds, true).getBytes();
            var11 = false;
         } finally {
            if (var11) {
               return getCategoryNames(categoryIds, true).getBytes();
            }
         }

         return categories;
      } else {
         return null;
      }
   }
}
