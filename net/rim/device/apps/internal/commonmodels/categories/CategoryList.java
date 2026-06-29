package net.rim.device.apps.internal.commonmodels.categories;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.SimplePersistentEncryptedSyncCollection;
import net.rim.device.apps.api.utility.framework.SimplePersistentSyncCollection$SimpleData;
import net.rim.vm.Array;
import net.rim.vm.PersistentInteger;
import net.rim.vm.WeakReference;

public final class CategoryList extends SimplePersistentEncryptedSyncCollection implements OTASyncCapable {
   private SimplePersistentSyncCollection$SimpleData _categories;
   private IntIntHashtable _oldCategoryIds;
   private PersistentObject _oldCategoryIdsPersistentObject = RIMPersistentStore.getPersistentObject(9169138517289043186L);
   private IntHashtable _categoryHashtable;
   private SyncCollectionSchema _schema = (SyncCollectionSchema)(new Object());
   private static final long PERSISTED_CATEGORIES = -253933379052014540L;
   private static final long PERSISTED_OLD_CATEGORY_IDS = 9169138517289043186L;
   private static final long CATEGORY_LIST = 7441508056667167330L;
   private static final long CATEGORY_ID = 7353985417580865631L;
   private static final int CATEGORIES_INITIAL_SIZE = 16;
   private static int _persistentNextCategoryId = PersistentInteger.getId(7353985417580865631L, 1);
   private static SyncConverter _syncConverter;
   private static WeakReference _sbWR = (WeakReference)(new Object(null));
   private static CategoryList _instance;
   private static final int[] KEY_FIELD_IDS = new int[]{1, -804651007, 51, -805044223};
   private static final int DEFAULT_RECORD_TYPE = 1;

   private CategoryList() {
      super(new CategoryList$CategoryComparator(), -253933379052014540L);
      this._schema.setDefaultRecordType(1);
      this._schema.setKeyFieldIds(1, KEY_FIELD_IDS);
      this.initialize(false, false);
      this.commonCtorEpilogue();
   }

   public static final CategoryList getInstance() {
      if (_instance == null) {
         _instance = (CategoryList)ApplicationRegistry.getApplicationRegistry().waitFor(7441508056667167330L);
      }

      return _instance;
   }

   static final CategoryList createAndRegisterInstance() {
      CategoryList categoryList = new CategoryList();
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(7441508056667167330L, categoryList);
      return categoryList;
   }

   private final void initialize(boolean forceNewCategories, boolean forcePopulate) {
      this._categories = (SimplePersistentSyncCollection$SimpleData)super._persistentObject.getContents();
      boolean populateCategoryList = forcePopulate || this._categories == null;
      this.initializeOldCategoryIds(forceNewCategories);
      if (forceNewCategories || this._categories == null) {
         this._categories = (SimplePersistentSyncCollection$SimpleData)(new Object(16));
         super._persistentObject.setContents(this._categories, 51);
         this.commit();
      }

      this.initList(this._categories._items, 1);
      this._categoryHashtable = (IntHashtable)(new Object());
      int numCategories = this.size();

      for (int i = 0; i < numCategories; i++) {
         CategoryModel category = (CategoryModel)this.getAt(i);
         this._categoryHashtable.put(category.getId(), category);
      }

      if (populateCategoryList) {
         this.populate();
      }
   }

   private final void initializeOldCategoryIds(boolean addCurrentCategoryIds) {
      this._oldCategoryIds = (IntIntHashtable)this._oldCategoryIdsPersistentObject.getContents();
      if (this._oldCategoryIds == null) {
         this._oldCategoryIds = (IntIntHashtable)(new Object());
         this._oldCategoryIdsPersistentObject.setContents(this._oldCategoryIds, 51);
         this._oldCategoryIdsPersistentObject.commit();
      }

      if (addCurrentCategoryIds) {
         int numCategories = this.size();

         for (int i = 0; i < numCategories; i++) {
            CategoryModel category = (CategoryModel)this.getAt(i);
            this._oldCategoryIds.put(category.getUID(), category.getId());
         }

         if (numCategories > 0) {
            this._oldCategoryIdsPersistentObject.commit();
         }
      }
   }

   @Override
   protected final String getContentProtectionEnabledMessage() {
      return "Category List";
   }

   public final synchronized void reset() {
      this.initialize(true, true);
   }

   private final void updateAuxilliaryCategoryData(CategoryModel category, boolean add) {
      int id = category.getId();
      int uid = category.getUID();
      if (add) {
         this._categoryHashtable.put(id, category);
         this._oldCategoryIds.remove(uid);
      } else {
         this._categoryHashtable.remove(id);
         this._oldCategoryIds.put(uid, id);
      }

      this._oldCategoryIdsPersistentObject.commit();
   }

   @Override
   public final void replaceElementAt(Object oldElement, Object newElement, int index, Object cookie) {
      super.replaceElementAt(oldElement, newElement, index, cookie);
      CategoryModel category = (CategoryModel)newElement;
      this._categoryHashtable.put(category.getId(), category);
   }

   private final void populate() {
      String[] categoryNames = CommonResources.getStringArray(9110);
      if (categoryNames != null) {
         for (int i = 0; i < categoryNames.length; i++) {
            String categoryName = categoryNames[i];
            if (categoryName != null) {
               categoryName = categoryName.trim();
               if (categoryName.length() > 0) {
                  this.addCategoryIfNecessary(categoryName, true);
               }
            }
         }
      }
   }

   final synchronized int generateId(String name) {
      int id = this._oldCategoryIds.get(CategoryModel.generateUID(name));
      if (id != -1) {
         return id;
      }

      id = PersistentInteger.get(_persistentNextCategoryId);
      PersistentInteger.set(_persistentNextCategoryId, id + 1);
      return id;
   }

   public final synchronized int addCategoryIfNecessary(String name) {
      return this.addCategoryIfNecessary(name, false);
   }

   private final int addCategoryIfNecessary(String name, boolean generateId) {
      if (name != null && name.trim().length() > 0) {
         int categoryId = this.getCategoryId(name);
         if (categoryId != -1) {
            return categoryId;
         }

         CategoryModel category;
         if (generateId) {
            category = new CategoryModel(name, this.generateId(name));
         } else {
            category = new CategoryModel(name);
         }

         this.add(category, false);
         return category.getId();
      } else {
         throw new Object();
      }
   }

   private final synchronized void add(CategoryModel category, boolean checkForDuplicates) {
      if (!checkForDuplicates || this.getCategory(category.getId()) == null) {
         this.updateAuxilliaryCategoryData(category, true);
         if (!ObjectGroup.isInGroup(category)) {
            ObjectGroup.createGroupIgnoreTooBig(category);
         }

         super.add(category);
      }
   }

   public final synchronized boolean removeCategory(int id) {
      CategoryModel category = this.getCategory(id);
      if (category == null) {
         return false;
      }

      this.remove(category);
      return true;
   }

   public final synchronized CategoryModel getCategory(int id) {
      CategoryModel category = (CategoryModel)this._categoryHashtable.get(id);
      return category != null ? category : null;
   }

   public final synchronized CategoryModel getCategory(String name) {
      return this.getCategory(this.getCategoryId(name));
   }

   public final synchronized int getCategoryId(String name) {
      int index = this._categories._items.binarySearch(this.getComparator(), name);
      return index >= 0 ? ((CategoryModel)this.getAt(index)).getId() : -1;
   }

   public final synchronized int getCategoryIds(String categoryNames, int[] categoryIds, boolean addMissingCategories) {
      if (categoryNames != null && categoryIds != null) {
         StringTokenizer tokenizer = (StringTokenizer)(new Object(categoryNames, ','));
         Array.resize(categoryIds, tokenizer.countTokens());
         int numCategories = 0;

         while (tokenizer.hasMoreElements()) {
            String categoryName = tokenizer.nextToken().trim();
            if (categoryName.length() != 0) {
               int categoryId;
               if (addMissingCategories) {
                  categoryId = this.addCategoryIfNecessary(categoryName);
               } else {
                  categoryId = this.getCategoryId(categoryName);
               }

               if (categoryId >= 0) {
                  categoryIds[numCategories++] = categoryId;
               }
            }
         }

         if (numCategories < categoryIds.length) {
            Array.resize(categoryIds, numCategories);
         }

         return numCategories;
      } else {
         throw new Object();
      }
   }

   public final synchronized String getCategoryName(int id) {
      CategoryModel category = this.getCategory(id);
      return category == null ? null : category.getName();
   }

   public final synchronized String getCategoryNames(int[] categoryIds) {
      return this.getCategoryNames(categoryIds, false);
   }

   final synchronized String getCategoryNames(int[] categoryIds, boolean forSync) {
      if (categoryIds == null) {
         throw new Object();
      }

      String categoryNames = null;
      if (categoryIds.length > 0) {
         StringBuffer _sb = WeakReferenceUtilities.getStringBuffer(_sbWR);
         _sb.setLength(0);

         for (int i = 0; i < categoryIds.length; i++) {
            String categoryName = this.getCategoryName(categoryIds[i]);
            if (categoryName != null) {
               if (_sb.length() > 0) {
                  _sb.append(',');
                  if (!forSync) {
                     _sb.append(' ');
                  }
               }

               _sb.append(categoryName);
            }
         }

         if (_sb.length() > 0) {
            categoryNames = _sb.toString();
         }
      }

      return categoryNames;
   }

   public final synchronized String getCategoryKey(int id) {
      StringBuffer _sb = WeakReferenceUtilities.getStringBuffer(_sbWR);
      _sb.setLength(0);
      this.appendCategoryKey(_sb, id);
      return _sb.length() > 0 ? _sb.toString() : null;
   }

   public final synchronized String getCategoryKeys(int[] categoryIds) {
      if (categoryIds == null) {
         throw new Object();
      }

      StringBuffer _sb = WeakReferenceUtilities.getStringBuffer(_sbWR);
      _sb.setLength(0);

      for (int i = 0; i < categoryIds.length; i++) {
         this.appendCategoryKey(_sb, categoryIds[i]);
      }

      return _sb.length() > 0 ? _sb.toString() : null;
   }

   private final void appendCategoryKey(StringBuffer sb, int id) {
      CategoryModel category = this.getCategory(id);
      String key;
      if (category != null) {
         key = category.getKey();
      } else {
         key = CategoryModel.generateKey(id);
      }

      if (sb.length() > 0) {
         sb.append(' ');
      }

      sb.append(key);
   }

   @Override
   public final synchronized void add(Object o) {
      this.add((CategoryModel)o, true);
   }

   @Override
   public final synchronized void remove(Object o) {
      this.updateAuxilliaryCategoryData((CategoryModel)o, false);
      super.remove(o);
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final String getSyncName() {
      return "Categories";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final SyncConverter getSyncConverter() {
      if (_syncConverter == null) {
         _syncConverter = new CategorySyncConverter();
      }

      return _syncConverter;
   }

   @Override
   protected final void clearPersistentData() {
      this.initialize(true, false);
   }

   @Override
   protected final void syncTransactionStopped() {
      if (super._syncRemoveAllDone) {
         int size = this.size();
         if (size == 0) {
            this.populate();
         } else {
            for (int i = 0; i < size; i++) {
               super._listenerManager.fireElementAdded(this, this.getAt(i));
            }
         }
      }

      super.syncTransactionStopped();
   }

   @Override
   public final SyncCollectionSchema getSchema() {
      return this._schema;
   }
}
