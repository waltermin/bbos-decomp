package net.rim.blackberry.api.pim;

class PIMItemImpl implements PIMItem {
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

   @Override
   public void addBinary(int field, int attributes, byte[] value, int offset, int length) {
      throw new Object();
   }

   @Override
   public byte[] getBinary(int field, int index) {
      throw new Object();
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
   public void addToCategory(String category) {
      throw new PIMException("Categories not supported.", 0);
   }

   @Override
   public void removeFromCategory(String category) {
      if (category == null) {
         throw new Object();
      }
   }

   @Override
   public String[] getCategories() {
      return new Object[0];
   }

   @Override
   public int maxCategories() {
      return 0;
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
}
