package net.rim.blackberry.api.pdap;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.EmptyEnumeration;
import net.rim.device.api.util.ObjectEnumerator;
import net.rim.device.apps.api.memo.MemoCollection;
import net.rim.device.apps.api.memo.MemoCollectionHolder;
import net.rim.device.apps.api.memo.MemoModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoryList;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;

public final class MemoListImpl extends PIMListImpl implements InternalBlackBerryMemoList {
   private static final long APP_REGISTRY_KEY = 5549232429708694218L;
   private static Hashtable _actualListeners;
   private static ResourceBundle _resources;
   private static MemoCollection _memoCollection;
   private static String LIST_CLOSED_MESSAGE = "Memo list is closed.";
   private static String WRITE_ONLY_MESSAGE = "Memo list is write-only.";

   MemoListImpl() {
      super._closed = false;
   }

   MemoListImpl(int mode) {
      this();
      this.initialize(mode);
   }

   final void commitMemo(MemoModel memoModel) {
      MemoModel oldMemoModel = this.findMemoModelByUID(memoModel.getUID());
      if (oldMemoModel == null) {
         _memoCollection.add(memoModel);
      } else {
         _memoCollection.update(oldMemoModel, memoModel);
      }
   }

   private final MemoModel findMemoModelByUID(int uid) {
      for (int i = _memoCollection.size() - 1; i >= 0; i--) {
         MemoModel memoModel = (MemoModel)_memoCollection.getAt(i);
         if (memoModel.getUID() == uid) {
            return memoModel;
         }
      }

      return null;
   }

   private final boolean stringMatch(String toMatch, String toSearch) {
      if (toMatch == null && toSearch == null) {
         return true;
      }

      if (toMatch != null && toSearch != null) {
         toMatch = toMatch.toLowerCase();
         toSearch = toSearch.toLowerCase();
         int index = 0;

         while (index != -1) {
            if (toSearch.startsWith(toMatch, index)) {
               return true;
            }

            index = toSearch.indexOf(32, index);
            if (index >= 0) {
               index++;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public final BlackBerryMemo createMemo() {
      return new MemoImpl(this);
   }

   @Override
   public final BlackBerryMemo importMemo(BlackBerryMemo element) {
      if (element == null) {
         throw new NullPointerException();
      } else {
         return new MemoImpl(element, this);
      }
   }

   @Override
   public final void removeMemo(BlackBerryMemo element) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 1) {
         throw new SecurityException();
      }

      if (element == null) {
         throw new NullPointerException();
      }

      if (!(element instanceof MemoImpl)) {
         throw new PIMException();
      }

      MemoImpl memoImpl = (MemoImpl)element;
      int uid = memoImpl.getMemoModel().getUID();
      MemoModel memoModel = this.findMemoModelByUID(uid);
      if (memoModel != null) {
         _memoCollection.remove(memoModel);
         memoImpl.removeFromList();
      } else {
         throw new PIMException();
      }
   }

   @Override
   protected final Hashtable getActualListeners() {
      return _actualListeners;
   }

   @Override
   protected final CollectionEventSource getCollectionEventSource() {
      return _memoCollection;
   }

   @Override
   protected final Enumeration getItemsInCategory(String category) {
      int categoryId = -1;
      if (category != null) {
         categoryId = CategoryList.getInstance().getCategoryId(category);
         if (categoryId == -1) {
            return new EmptyEnumeration();
         }
      }

      int listSize = _memoCollection.size();
      Object[] matchingItems = new Object[listSize];
      int[] categoryIds = new int[0];

      for (int i = listSize - 1; i >= 0; i--) {
         boolean memoMatches = false;
         MemoModel memoModel = (MemoModel)_memoCollection.getAt(i);
         CategoriesModel categoriesModel = memoModel.getCategoriesModel();
         if (category == null) {
            if (categoriesModel == null) {
               memoMatches = true;
            } else {
               categoriesModel.getCategoryIds(categoryIds);
               if (categoryIds.length == 0) {
                  memoMatches = true;
               }
            }
         } else if (categoriesModel != null) {
            categoriesModel.getCategoryIds(categoryIds);

            for (int j = categoryIds.length - 1; j >= 0; j--) {
               if (categoryIds[j] == categoryId) {
                  memoMatches = true;
                  break;
               }
            }
         }

         if (memoMatches) {
            matchingItems[i] = this.createMemo(memoModel);
         }
      }

      return new ObjectEnumerator(matchingItems);
   }

   @Override
   protected final PIMItem getPIMItemFor(Object element) {
      if (!(element instanceof MemoModel)) {
         throw new IllegalArgumentException();
      }

      MemoModel memoModel = (MemoModel)element;
      return this.createMemo(memoModel);
   }

   @Override
   protected final void removePIMItem(PIMItem pi) {
      if (pi instanceof BlackBerryMemo) {
         BlackBerryMemo memo = (BlackBerryMemo)pi;

         try {
            this.removeMemo(memo);
            return;
         } catch (PIMException var4) {
         }
      }
   }

   @Override
   protected final boolean verifyField(int field) {
      switch (field) {
         case 99:
            throw new IllegalArgumentException();
         case 100:
         case 101:
         case 102:
         default:
            return true;
      }
   }

   @Override
   public final void close() throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      super._closed = true;
   }

   @Override
   public final int getFieldDataType(int field) {
      switch (field) {
         case 99:
            throw new IllegalArgumentException();
         case 100:
         case 101:
         case 102:
         default:
            return 4;
      }
   }

   @Override
   public final String getFieldLabel(int field) {
      switch (field) {
         case 99:
            throw new IllegalArgumentException();
         case 100:
         default:
            return _resources.getString(50);
         case 101:
            return _resources.getString(51);
         case 102:
            return _resources.getString(52);
      }
   }

   @Override
   public final String getName() {
      return "Memo List";
   }

   @Override
   public final int[] getSupportedFields() {
      return new int[]{100, 101, 102, -804650997, 100, 102, 103, 104, 106, 107, 108, 20000927};
   }

   @Override
   public final boolean isSupportedField(int field) {
      try {
         return this.verifyField(field);
      } finally {
         ;
      }
   }

   @Override
   public final Enumeration items() throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITE_ONLY_MESSAGE);
      }

      int listSize = _memoCollection.size();
      Object[] items = new Object[listSize];

      for (int i = listSize - 1; i >= 0; i--) {
         items[i] = this.createMemo((MemoModel)_memoCollection.getAt(i));
      }

      return new ObjectEnumerator(items);
   }

   @Override
   public final Enumeration items(PIMItem matching) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITE_ONLY_MESSAGE);
      }

      if (!(matching instanceof BlackBerryMemo)) {
         if (matching == null) {
            throw new NullPointerException();
         } else {
            throw new IllegalArgumentException();
         }
      } else {
         int listSize = _memoCollection.size();
         Object[] matchingItems = new Object[listSize];

         for (int i = listSize - 1; i >= 0; i--) {
            MemoModel memoModel = (MemoModel)_memoCollection.getAt(i);
            if (matching.countValues(100) > 0) {
               BodyModel notesModel = memoModel.getNotesModel();
               String notes = notesModel == null ? null : notesModel.getText();
               if (!this.stringMatch(matching.getString(100, 0), notes)) {
                  continue;
               }
            }

            if (matching.countValues(101) > 0) {
               TitleModel titleModel = memoModel.getTitleModel();
               String title = titleModel == null ? null : titleModel.getTitle();
               if (!this.stringMatch(matching.getString(101, 0), title)) {
                  continue;
               }
            }

            if (matching.countValues(102) > 0) {
               String uid = String.valueOf(memoModel.getUID());
               if (!this.stringMatch(matching.getString(102, 0), uid)) {
                  continue;
               }
            }

            matchingItems[i] = this.createMemo(memoModel);
         }

         return new ObjectEnumerator(matchingItems);
      }
   }

   @Override
   public final Enumeration items(String matching) throws PIMException {
      if (super._closed) {
         throw new PIMException(LIST_CLOSED_MESSAGE, 2);
      }

      if (super._mode == 2) {
         throw new SecurityException(WRITE_ONLY_MESSAGE);
      }

      if (matching == null) {
         throw new NullPointerException();
      }

      int listSize = _memoCollection.size();
      Object[] matchingItems = new Object[listSize];

      for (int i = listSize - 1; i >= 0; i--) {
         MemoModel memoModel = (MemoModel)_memoCollection.getAt(i);
         BodyModel notesModel = memoModel.getNotesModel();
         TitleModel titleModel = memoModel.getTitleModel();
         String notes = notesModel == null ? null : notesModel.getText();
         String title = titleModel == null ? null : titleModel.getTitle();
         String uid = String.valueOf(memoModel.getUID());
         if (this.stringMatch(matching, notes) || this.stringMatch(matching, title) || this.stringMatch(matching, uid)) {
            matchingItems[i] = this.createMemo(memoModel);
         }
      }

      return new ObjectEnumerator(matchingItems);
   }

   @Override
   public final void initialize(int mode) {
      super._mode = mode;
   }

   @Override
   public final BlackBerryMemo createMemo(Object input) {
      return this.isInternalMemoModel(input) ? new MemoImpl((MemoModel)input, this) : null;
   }

   @Override
   public final boolean isInternalMemoModel(Object input) {
      return input instanceof MemoModel;
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _actualListeners = (Hashtable)ar.getOrWaitFor(5549232429708694218L);
      if (_actualListeners == null) {
         _actualListeners = new Hashtable();
         ar.put(5549232429708694218L, _actualListeners);
      }

      _resources = ResourceBundle.getBundle(6683049446475877841L, "net.rim.blackberry.api.pim.resource.PIMRes");
      _memoCollection = MemoCollectionHolder.getMemoCollection();
   }
}
