package net.rim.blackberry.api.pim;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.collection.CollectionEventSource;

class PIMListImpl implements BlackBerryPIMList {
   boolean _closed;
   int _mode;
   private Vector _listeners = (Vector)(new Object());
   private static String NO_CATEGORIES_MESSAGE = "Categories are not supported.";
   private static String LIST_CLOSED_MESSAGE = "PIM List is closed.";
   private static String WRITEONLY_MESSAGE = "PIM List is write-only.";
   protected static String NOT_FOUND_MESSAGE = "PIMItem not found.";
   private static Hashtable _actualListeners = (Hashtable)(new Object());

   protected boolean verifyField(int _1) {
      throw null;
   }

   protected CollectionEventSource getCollectionEventSource() {
      throw null;
   }

   protected PIMItem getPIMItemFor(Object _1) {
      throw null;
   }

   @Override
   public String[] getCategories() {
      return new Object[0];
   }

   @Override
   public boolean isCategory(String category) {
      return false;
   }

   @Override
   public void addCategory(String category) throws PIMException {
      throw new PIMException(NO_CATEGORIES_MESSAGE, 0);
   }

   @Override
   public void deleteCategory(String category, boolean deleteUnassignedItems) throws PIMException {
      throw new PIMException(NO_CATEGORIES_MESSAGE, 0);
   }

   @Override
   public int maxCategories() {
      return 0;
   }

   @Override
   public void renameCategory(String currentCategory, String newCategory) throws PIMException {
      throw new PIMException(NO_CATEGORIES_MESSAGE, 0);
   }

   @Override
   public Enumeration itemsByCategory(String category) throws PIMException {
      if (category == PIMList.UNCATEGORIZED) {
         return this.items();
      } else if (this._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      } else if (this._mode == 2) {
         throw new Object(WRITEONLY_MESSAGE);
      } else {
         return (Enumeration)(new Object());
      }
   }

   @Override
   public int[] getSupportedArrayElements(int stringArrayField) {
      if (!this.verifyField(stringArrayField)) {
         throw new UnsupportedFieldException(stringArrayField);
      } else {
         return new int[0];
      }
   }

   @Override
   public boolean isSupportedArrayElement(int stringArrayField, int arrayElement) {
      return false;
   }

   @Override
   public int[] getSupportedAttributes(int field) {
      if (!this.verifyField(field)) {
         throw new UnsupportedFieldException(field);
      } else {
         return new int[0];
      }
   }

   @Override
   public boolean isSupportedAttribute(int field, int attribute) {
      this.verifyField(field);
      return attribute == 0;
   }

   @Override
   public int maxValues(int field) {
      return this.verifyField(field) ? 1 : 0;
   }

   @Override
   public void addListener(PIMListListener listener) {
      CollectionEventSource ces = this.getCollectionEventSource();
      PIMListImpl$WrapperListener actualListener = new PIMListImpl$WrapperListener(this, listener);
      synchronized (this._listeners) {
         ces.addCollectionListener(actualListener);
         this._listeners.addElement(listener);
         _actualListeners.put(listener, actualListener);
      }
   }

   @Override
   public void removeListener(PIMListListener listener) {
      CollectionEventSource ces = this.getCollectionEventSource();
      synchronized (this._listeners) {
         ces.removeCollectionListener(_actualListeners.get(listener));
         this._listeners.removeElement(listener);
         _actualListeners.remove(listener);
      }
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
