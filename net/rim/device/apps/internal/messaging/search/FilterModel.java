package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.LongKeyProviderAdaptor;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.ReadableListUtil;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.addressbook.AddressCardElement;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.EncryptableProvider;
import net.rim.device.apps.api.framework.model.KeyProvider;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.FolderHierarchies;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.messaging.messagelist.MessageListUI;
import net.rim.device.apps.api.messaging.messagelist.ShowMessageApp;
import net.rim.device.apps.api.messaging.search.MessageSearch;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.commonmodels.title.TitleModelImpl;
import net.rim.device.apps.internal.messaging.search.criteria.BodySearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.FolderModel;
import net.rim.device.apps.internal.messaging.search.criteria.FolderModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.NameSearchModel;
import net.rim.device.apps.internal.messaging.search.criteria.NameSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.ShowSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.TypeSearchModelFactory;
import net.rim.vm.Array;

public final class FilterModel
   implements PersistableRIMModel,
   ReadableList,
   SyncObject,
   WritableSet,
   PaintProvider,
   KeyProvider,
   ConversionProvider,
   EncryptableProvider {
   private int _uid;
   protected ShortCutKeyModel _shortCutKey;
   protected TitleModelImpl _titleModel;
   protected Object[] _criteria = new Object[0];
   private static final long CONTROLLER_ID;
   private static ContextObjectWR _filterSyncContextWR = (ContextObjectWR)(new Object(22, 19));
   static final byte[] _filterIdData = new byte[]{102};

   @Override
   public final int paint(Graphics g, int x, int y, int width, int height, Object context) {
      int xoffset = 0;
      RIMModel title = this._titleModel;
      if (title instanceof Object) {
         PaintProvider paintProvider = (PaintProvider)title;
         xoffset = paintProvider.paint(g, x, y, width, height, context);
      }

      RIMModel shortCut = this._shortCutKey;
      if (shortCut instanceof Object) {
         PaintProvider paintProvider = (PaintProvider)shortCut;
         xoffset += paintProvider.paint(g, x + xoffset, y, width - xoffset, height, context);
      }

      return xoffset;
   }

   public final Object performSearch(MessageSearchImpl set, boolean firstMatchOnly, boolean fromRibbon, Object context) {
      Object ticket = PersistentContent.getTicket();
      if (ticket != null) {
         ticket.hashCode();
      }

      if (set != null) {
         set.setLastFilter(this);
      }

      ReadableList filterModel = this;
      if (ObjectGroup.isInGroup(filterModel)) {
         filterModel = (ReadableList)ObjectGroup.expandGroup(filterModel);
      }

      Collection source = null;
      Recognizer typeRecognizer = TypeSearchModelFactory.getInstance();
      Recognizer showRecognizer = ShowSearchModelFactory.getInstance();
      Recognizer nameRecognizer = NameSearchModelFactory.getInstance();
      Recognizer folderRecognizer = FolderModelFactory.getInstance();
      SearchCriterion[] criteria = new Object[0];
      int numSubmembers = filterModel.size();
      long mergeToSearch = 7509894771240321003L;
      boolean sourceIsFiltered = false;
      int i = 0;
      int j = 0;

      while (i < numSubmembers) {
         label139: {
            boolean in_front = false;
            SearchCriterion thisCriterion = (SearchCriterion)filterModel.getAt(i);
            if (showRecognizer.recognize(thisCriterion) || typeRecognizer.recognize(thisCriterion)) {
               Object showValue = thisCriterion.getValue();
               if (showValue == null) {
                  break label139;
               }

               in_front = true;
               if (showRecognizer.recognize(thisCriterion)) {
                  int showSearchType = thisCriterion.getType();
                  if (showSearchType == 11) {
                     mergeToSearch = 6368823655991217730L;
                  }
               }
            } else if (nameRecognizer.recognize(thisCriterion)) {
               NameSearchModel nsm = (NameSearchModel)thisCriterion;
               nsm.establishValue();
            }

            if (folderRecognizer.recognize(thisCriterion)) {
               FolderModel fm = (FolderModel)thisCriterion;
               fm.establishValue();
               long folderLUID = thisCriterion.getValue();
               Folder f = null;
               if (folderLUID != 0) {
                  f = FolderHierarchies.getFolder(folderLUID);
               }

               if (f == null) {
                  break label139;
               }

               source = f.getContainedItems();
               if (source != null) {
                  break label139;
               }

               in_front = true;
            }

            if (thisCriterion.getValue() != null) {
               sourceIsFiltered = true;
               int idxLast = j++;
               Array.resize(criteria, j);
               if (in_front && idxLast != 0) {
                  System.arraycopy(criteria, 0, criteria, 1, idxLast);
                  idxLast = 0;
               }

               criteria[idxLast] = thisCriterion;
            }
         }

         i++;
      }

      Recognizer bodyRecognizer = BodySearchModelFactory.getInstance();
      j = criteria.length;
      int currLast = j - 1;
      int ix = 0;

      while (ix < currLast) {
         SearchCriterion sc = criteria[ix];
         if (bodyRecognizer.recognize(sc)) {
            System.arraycopy(criteria, ix + 1, criteria, ix, currLast - ix);
            criteria[currLast] = sc;
            currLast--;
         } else {
            ix++;
         }
      }

      if (source == null) {
         source = FolderMerge.getMergeCollection(mergeToSearch);
      }

      Object messageListSource;
      if (sourceIsFiltered) {
         SearchResultCollection collection = (SearchResultCollection)(new Object(
            criteria, (Comparator)(new Object((LongKeyProviderAdaptor)(new Object()))), true, true
         ));
         collection.loadFrom(source);
         messageListSource = collection;
         MessageSearchImpl s = (MessageSearchImpl)MessageSearch.getInstance();
         s.newSearchInProgress(collection);
      } else {
         messageListSource = source;
      }

      if (firstMatchOnly) {
         ReadableList records = (ReadableList)messageListSource;

         for (int ixx = 0; ixx < records.size(); ixx++) {
            RIMModel m = (RIMModel)records.getAt(ixx);
            if (m instanceof Object) {
               MatchProvider mp = (MatchProvider)m;
               if (mp.match(criteria) == 1) {
                  return m;
               }
            }
         }

         return null;
      } else {
         TitleModelImpl tm = ((FilterModel)filterModel)._titleModel;
         String title = null;
         if (tm != null) {
            title = tm.toString();
         }

         if (title == null) {
            title = CommonResources.getString(9143);
         }

         String in_progress = CommonResources.getString(9139);
         MessageListUI listUI = (MessageListUI)(new Object(title, in_progress, fromRibbon ? 4 : 3));
         listUI.loadFrom(messageListSource);
         if (ContextObject.getFlag(context, 11)) {
            Object obj = ContextObject.get(context, 252);
            if (obj instanceof Object) {
               AddressCardElement addressCard = (AddressCardElement)obj;
               listUI.setAddressCardUID(addressCard.getUID());
            }
         }

         ShowMessageApp.showMessageApp(-1244342860, listUI);
         return null;
      }
   }

   protected final char getShortCutKey() {
      ShortCutKeyModel keyModel = this._shortCutKey;
      return keyModel != null ? keyModel.getShortCutKey() : '\u0000';
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   protected final void addShortCutKey(char shortCutKey) {
      RIMModelFactory keyFactory = ShortCutKeyModelFactory.getInstance();
      Character key = (Character)(new Object(Character.toLowerCase(shortCutKey)));
      ShortCutKeyModel keyModel = (ShortCutKeyModel)keyFactory.createInstance(key);
      this._shortCutKey = keyModel;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         syncBuffer.addBytes(9, _filterIdData);
         ContextObject filterSyncContext = _filterSyncContextWR.getContextObject();
         if (this._titleModel != null) {
            syncBuffer.addModel(this._titleModel, filterSyncContext);
         }

         if (this._shortCutKey != null) {
            syncBuffer.addModel(this._shortCutKey, filterSyncContext);
         }

         return syncBuffer.addSubmembers(this, filterSyncContext);
      } else {
         return false;
      }
   }

   protected final void addTitle(Object title) {
      TitleModelImpl titleModel = (TitleModelImpl)FactoryUtil.createInstance(-4904857078378172834L, title);
      this._titleModel = titleModel;
   }

   public final boolean validate(Field field, Object context) {
      return true;
   }

   public final boolean grabDataFromField(Field field, Object context) {
      throw new Object("Shouldn't have been called");
   }

   final void setUID(int uid) {
      this._uid = uid;
   }

   @Override
   public final int size() {
      return this._criteria.length;
   }

   @Override
   public final void removeAll() {
      this._criteria = new Object[0];
   }

   @Override
   public final Object getAt(int index) {
      return this._criteria[index];
   }

   @Override
   public final int getKeys(Object context, Object[] keyArray, int index, long keyRequested) {
      int keyCount = 0;
      if (ContextObject.getFlag(context, 56)) {
         RIMModel title = this._titleModel;
         if (title instanceof Object) {
            KeyProvider keyProvider = (KeyProvider)title;
            return keyProvider.getKeys(context, keyArray, index + keyCount, keyRequested);
         }
      }

      return 0;
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
   public final void remove(Object member) {
      int size = this._criteria.length;

      for (int ix = 0; ix < size; ix++) {
         if (this._criteria[ix] == member) {
            System.arraycopy(this._criteria, ix + 1, this._criteria, ix, size - ix - 1);
            Array.resize(this._criteria, size - 1);
            return;
         }
      }
   }

   @Override
   public final void add(Object member) {
      if (ObjectGroup.isInGroup(member)) {
         throw new Object();
      }

      if (!(member instanceof Object)) {
         if (!(member instanceof ShortCutKeyModel)) {
            int var4 = this._criteria.length;
            Array.resize(this._criteria, var4 + 1);
            this._criteria[var4] = member;
         } else {
            ShortCutKeyModel m = (ShortCutKeyModel)member;
            this._shortCutKey = m;
         }
      } else {
         TitleModelImpl m = (TitleModelImpl)member;
         this._titleModel = m;
      }
   }

   @Override
   public final int getIndex(Object element) {
      return ReadableListUtil.getIndex(element, this);
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      return ReadableListUtil.getAt(index, count, elements, destIndex, this);
   }

   @Override
   public final boolean contains(Object element) {
      int n = this._criteria.length;

      for (int i = 0; i < n; i++) {
         if (this._criteria[i] == element) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final boolean checkCrypt(boolean compress, boolean encrypt) {
      if (this._titleModel != null && !this._titleModel.checkCrypt(compress, encrypt)) {
         return false;
      }

      for (int i = 0; i < this._criteria.length; i++) {
         Object obj = this._criteria[i];
         if (obj instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)obj;
            if (!encryptable.checkCrypt(compress, encrypt)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public final Object reCrypt(boolean compress, boolean encrypt) {
      FilterModel newModel = (FilterModel)expandGroup(this);
      if (newModel._titleModel != null) {
         newModel._titleModel.reCrypt(compress, encrypt);
      }

      for (int i = 0; i < this._criteria.length; i++) {
         Object obj = newModel._criteria[i];
         if (obj instanceof Object) {
            EncryptableProvider encryptable = (EncryptableProvider)obj;
            obj = encryptable.reCrypt(compress, encrypt);
            if (obj != null) {
               newModel._criteria[i] = obj;
            }
         }
      }

      return createGroup(newModel);
   }

   static final FilterModel createGroup(FilterModel fm) {
      if (!ObjectGroup.isInGroup(fm)) {
         ObjectGroup.createGroupIgnoreTooBig(fm);
      }

      return fm;
   }

   static final RIMModel expandGroup(RIMModel fm) {
      if (ObjectGroup.isInGroup(fm)) {
         fm = (RIMModel)ObjectGroup.expandGroup(fm);
      }

      return fm;
   }

   FilterModel() {
      this._shortCutKey = null;
      this._titleModel = null;
      this._uid = UIDGenerator.getUID();
   }
}
