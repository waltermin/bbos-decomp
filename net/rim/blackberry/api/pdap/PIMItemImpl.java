package net.rim.blackberry.api.pdap;

import javax.microedition.pim.FieldFullException;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.PIMList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoryList;
import net.rim.vm.Array;

class PIMItemImpl implements PIMItem {
   protected CategoriesModel _categoriesModel;
   private static Factory _categoriesFactory;

   protected void checkIndex(int field, int index) {
      if (index < 0 || this.countValues(field) <= index) {
         throw new Object();
      }
   }

   protected void checkFieldNotFull(int field) {
      if (this.countValues(field) > 0) {
         throw new FieldFullException();
      }
   }

   protected boolean removeCategoryFromModel(WritableSet internalModel, String category) {
      if (category == null) {
         throw new Object();
      }

      if (this._categoriesModel != null) {
         int[] ids = new int[0];
         this._categoriesModel.getCategoryIds(ids);
         int removeId = CategoryList.getInstance().getCategoryId(category);
         if (removeId != -1) {
            for (int i = ids.length - 1; i >= 0; i--) {
               if (ids[i] == removeId) {
                  ids[i] = -1;
                  String categoryString = CategoryList.getInstance().getCategoryNames(ids);
                  internalModel.remove(this._categoriesModel);
                  this._categoriesModel = (CategoriesModel)_categoriesFactory.createInstance(categoryString);
                  if (this._categoriesModel != null) {
                     internalModel.add(this._categoriesModel);
                  }

                  return true;
               }
            }
         }
      }

      return false;
   }

   protected boolean addCategoryToModel(WritableSet internalModel, String category) {
      if (category == null) {
         throw new Object();
      }

      CategoryList categoryList = CategoryList.getInstance();
      int categoryId = categoryList.getCategoryId(category);
      if (categoryId == -1) {
         throw new PIMException();
      }

      if (this._categoriesModel != null) {
         int[] ids = new int[0];
         this._categoriesModel.getCategoryIds(ids);
         if (Arrays.getIndex(ids, categoryId) != -1) {
            return false;
         }

         StringBuffer categoryBuffer = (StringBuffer)(new Object(category));
         if (ids.length > 0) {
            categoryBuffer.append(',');
            categoryBuffer.append(categoryList.getCategoryNames(ids));
         }

         internalModel.remove(this._categoriesModel);
         category = categoryBuffer.toString();
      }

      this._categoriesModel = (CategoriesModel)_categoriesFactory.createInstance(category);
      internalModel.add(this._categoriesModel);
      return true;
   }

   @Override
   public void setBinary(int field, int index, int attributes, byte[] value, int offset, int length) {
      throw new Object();
   }

   @Override
   public void addBoolean(int field, int attributes, boolean value) {
      throw new Object();
   }

   @Override
   public boolean getBoolean(int field, int index) {
      throw new Object();
   }

   @Override
   public void setBoolean(int field, int index, int attributes, boolean value) {
      throw new Object();
   }

   @Override
   public void addStringArray(int field, int attributes, String[] value) {
      throw new Object();
   }

   @Override
   public String[] getStringArray(int field, int index) {
      throw new Object();
   }

   @Override
   public void setStringArray(int field, int index, int attributes, String[] value) {
      throw new Object();
   }

   @Override
   public int getAttributes(int field, int index) {
      if (index >= 0 && this.countValues(field) > index) {
         return 0;
      } else {
         throw new Object();
      }
   }

   @Override
   public byte[] getBinary(int field, int index) {
      throw new Object();
   }

   @Override
   public void addBinary(int field, int attributes, byte[] value, int offset, int length) {
      throw new Object();
   }

   @Override
   public String[] getCategories() {
      if (this._categoriesModel == null) {
         return new Object[0];
      }

      int[] ids = new int[0];
      this._categoriesModel.getCategoryIds(ids);
      String[] results = new Object[ids.length];
      int numResults = 0;
      CategoryList categoryList = CategoryList.getInstance();

      for (int i = ids.length - 1; i >= 0; i--) {
         String category = categoryList.getCategoryName(ids[i]);
         if (category != null) {
            results[numResults++] = category;
         }
      }

      Array.resize(results, numResults);
      return results;
   }

   @Override
   public int maxCategories() {
      return -1;
   }

   @Override
   public void setString(int _1, int _2, int _3, String _4) {
      throw null;
   }

   @Override
   public void setInt(int _1, int _2, int _3, int _4) {
      throw null;
   }

   @Override
   public void setDate(int _1, int _2, int _3, long _4) {
      throw null;
   }

   @Override
   public void removeFromCategory(String _1) {
      throw null;
   }

   @Override
   public void addToCategory(String _1) {
      throw null;
   }

   @Override
   public void removeValue(int _1, int _2) {
      throw null;
   }

   @Override
   public int countValues(int _1) {
      throw null;
   }

   @Override
   public void addString(int _1, int _2, String _3) {
      throw null;
   }

   @Override
   public String getString(int _1, int _2) {
      throw null;
   }

   @Override
   public void addInt(int _1, int _2, int _3) {
      throw null;
   }

   @Override
   public int getInt(int _1, int _2) {
      throw null;
   }

   @Override
   public void addDate(int _1, int _2, long _3) {
      throw null;
   }

   @Override
   public long getDate(int _1, int _2) {
      throw null;
   }

   @Override
   public int[] getFields() {
      throw null;
   }

   @Override
   public boolean isModified() {
      throw null;
   }

   @Override
   public void commit() {
      throw null;
   }

   @Override
   public PIMList getPIMList() {
      throw null;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _categoriesFactory = (Factory)ar.waitFor(-537018776823173138L);
   }
}
