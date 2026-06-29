package net.rim.blackberry.api.pdap;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.UnsupportedFieldException;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.apps.internal.commonmodels.categories.CategoryList;
import net.rim.device.apps.internal.commonmodels.categories.CategoryModel;

class PIMListImpl implements BlackBerryPIMList {
   boolean _closed;
   int _mode;
   private static String LIST_CLOSED_MESSAGE = "PIM List is closed.";
   private static String WRITEONLY_MESSAGE = "PIM List is write-only.";
   protected static String NOT_FOUND_MESSAGE = "PIMItem not found.";

   protected boolean verifyField(int _1) {
      throw null;
   }

   protected Enumeration getItemsInCategory(String _1) {
      throw null;
   }

   protected void removePIMItem(PIMItem _1) {
      throw null;
   }

   protected Hashtable getActualListeners() {
      throw null;
   }

   protected CollectionEventSource getCollectionEventSource() {
      throw null;
   }

   protected PIMItem getPIMItemFor(Object _1) {
      throw null;
   }

   void addListenerInternal(PIMListListener listener, boolean listenForGroupAddressCards) {
      CollectionEventSource ces = this.getCollectionEventSource();
      Hashtable actualListeners = this.getActualListeners();
      synchronized (actualListeners) {
         if (!actualListeners.containsKey(listener)) {
            PIMListImpl$WrapperListener actualListener = new PIMListImpl$WrapperListener(this, listener, listenForGroupAddressCards);
            ces.addCollectionListener(actualListener);
            actualListeners.put(listener, actualListener);
         }
      }
   }

   public int getFieldDateType(int field) {
      throw new Object();
   }

   @Override
   public void deleteCategory(String category, boolean deleteUnassignedItems) throws PIMException {
      if (category == null) {
         throw new Object();
      }

      if (this._closed) {
         throw new PIMException();
      }

      if (this._mode == 1) {
         throw new Object();
      }

      if (deleteUnassignedItems) {
         Enumeration e = this.getItemsInCategory(category);

         while (e.hasMoreElements()) {
            PIMItem pi = (PIMItem)e.nextElement();
            if (pi.getCategories().length == 1) {
               this.removePIMItem(pi);
            }
         }
      }

      CategoryList categoryList = CategoryList.getInstance();
      categoryList.removeCategory(categoryList.getCategoryId(category));
   }

   @Override
   public int maxCategories() {
      return -1;
   }

   @Override
   public void renameCategory(String currentCategory, String newCategory) throws PIMException {
      if (currentCategory != null && newCategory != null) {
         if (newCategory.length() <= 0 || CategoryList.getInstance().getCategoryId(currentCategory) == -1 || this._closed) {
            throw new PIMException();
         }

         if (this._mode == 1) {
            throw new Object();
         }

         if (!currentCategory.equalsIgnoreCase(newCategory)) {
            CategoryList categoryList = CategoryList.getInstance();
            categoryList.addCategoryIfNecessary(newCategory);
            Enumeration e = this.getItemsInCategory(currentCategory);

            while (e.hasMoreElements()) {
               PIMItem nextElement = (PIMItem)e.nextElement();
               nextElement.addToCategory(newCategory);
               nextElement.commit();
            }

            categoryList.removeCategory(categoryList.getCategoryId(currentCategory));
         }
      } else {
         throw new Object();
      }
   }

   @Override
   public Enumeration itemsByCategory(String category) throws PIMException {
      if (this._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      } else if (this._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      } else {
         return (Enumeration)(category != null && CategoryList.getInstance().getCategoryId(category) == -1 ? new Object() : this.getItemsInCategory(category));
      }
   }

   @Override
   public int[] getSupportedArrayElements(int stringArrayField) {
      if (this.getFieldDateType(stringArrayField) == 5) {
         throw new UnsupportedFieldException();
      } else {
         throw new Object();
      }
   }

   @Override
   public void addCategory(String category) throws PIMException {
      if (category == null) {
         throw new Object();
      }

      if (category.length() <= 0 || this._closed) {
         throw new PIMException();
      }

      if (this._mode == 1) {
         throw new Object();
      }

      CategoryList.getInstance().addCategoryIfNecessary(category);
   }

   @Override
   public boolean isSupportedArrayElement(int stringArrayField, int arrayElement) {
      return false;
   }

   @Override
   public int[] getSupportedAttributes(int field) {
      if (!this.verifyField(field)) {
         throw new UnsupportedFieldException("", field);
      } else {
         return new int[0];
      }
   }

   @Override
   public boolean isSupportedAttribute(int field, int attribute) {
      return false;
   }

   @Override
   public int maxValues(int field) {
      return this.verifyField(field) ? 1 : 0;
   }

   @Override
   public boolean isCategory(String category) throws PIMException {
      if (category == null) {
         throw new Object();
      } else if (this._closed) {
         throw new PIMException();
      } else {
         return CategoryList.getInstance().getCategoryId(category) != -1;
      }
   }

   @Override
   public void addListener(PIMListListener listener) {
      this.addListenerInternal(listener, false);
   }

   @Override
   public void removeListener(PIMListListener listener) {
      CollectionEventSource ces = this.getCollectionEventSource();
      Hashtable actualListeners = this.getActualListeners();
      synchronized (actualListeners) {
         if (actualListeners.containsKey(listener)) {
            PIMListImpl$WrapperListener actualListener = (PIMListImpl$WrapperListener)actualListeners.get(listener);
            ces.removeCollectionListener(actualListener);
            actualListeners.remove(listener);
         }
      }
   }

   @Override
   public String[] getCategories() throws PIMException {
      if (this._closed) {
         throw new PIMException();
      }

      CategoryList categoryList = CategoryList.getInstance();
      int numCategories = categoryList.size();
      String[] results = new Object[numCategories];

      for (int i = numCategories - 1; i >= 0; i--) {
         CategoryModel category = (CategoryModel)categoryList.getAt(i);
         results[i] = category.getName();
      }

      return results;
   }

   @Override
   public String getArrayElementLabel(int stringArrayField, int arrayElement) {
      throw new Object();
   }

   @Override
   public String getAttributeLabel(int attribute) {
      if (attribute == 0) {
         return "";
      } else {
         throw new Object();
      }
   }

   @Override
   public void setFieldLabel(int field, String value) {
   }

   @Override
   public boolean isFieldLabelSettable(int field) {
      return false;
   }

   @Override
   public int stringArraySize(int stringArrayField) {
      throw new Object();
   }

   @Override
   public int getFieldDataType(int _1) {
      throw null;
   }

   @Override
   public String getFieldLabel(int _1) {
      throw null;
   }

   @Override
   public int[] getSupportedFields() {
      throw null;
   }

   @Override
   public boolean isSupportedField(int _1) {
      throw null;
   }

   @Override
   public Enumeration items(String _1) {
      throw null;
   }

   @Override
   public Enumeration items(PIMItem _1) {
      throw null;
   }

   @Override
   public Enumeration items() {
      throw null;
   }

   @Override
   public void close() {
      throw null;
   }

   @Override
   public String getName() {
      throw null;
   }
}
