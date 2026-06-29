package net.rim.device.apps.internal.messaging.search;

import java.util.Hashtable;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.MMS;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.FactoryUtil;
import net.rim.device.apps.api.framework.hotkeys.HotKeys;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.messaging.search.MessageSearch;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.api.utility.framework.PopulateModels;
import net.rim.device.apps.api.utility.framework.SimplePersistentEncryptedSyncCollection;
import net.rim.device.apps.internal.blackberryemail.folder.EmailHierarchy;
import net.rim.device.apps.internal.messaging.search.criteria.FolderModel;
import net.rim.device.apps.internal.messaging.search.criteria.FolderModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.NameSearchModel;
import net.rim.device.apps.internal.messaging.search.criteria.NameSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.ServiceSearchModel;
import net.rim.device.apps.internal.messaging.search.criteria.ServiceSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.ShowSearchModel;
import net.rim.device.apps.internal.messaging.search.criteria.ShowSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.SubjectSearchModel;
import net.rim.device.apps.internal.messaging.search.criteria.SubjectSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.criteria.TypeSearchModel;
import net.rim.device.apps.internal.messaging.search.criteria.TypeSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;
import net.rim.device.internal.system.InternalServices;
import net.rim.tid.im.layout.SLKeyLayout;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public final class MessageSearchImpl extends MessageSearch {
   private SearchCollection _searchCollection;
   private KeywordFilterList _keyList;
   private WeakReference _currentSearch;
   Object[] _keyChoices;
   static final long FILTER_NAME = 7820085525428081380L;

   final void newSearchInProgress(SearchResultCollection src) {
      WeakReference wr = this._currentSearch;
      if (wr != null) {
         SearchResultCollection oldSearch = (SearchResultCollection)wr.get();
         if (oldSearch != null) {
            oldSearch.haltFiltering(true);
         }
      }

      this._currentSearch = (WeakReference)(new Object(src));
   }

   public final long getCollectionId() {
      return 7820085525428081380L;
   }

   @Override
   public final SimplePersistentEncryptedSyncCollection getCollection() {
      return this._searchCollection;
   }

   final KeywordFilterList getKeywordList() {
      if (this._keyList == null) {
         this._keyList = (KeywordFilterList)(new Object(
            this.getCollection(), (KeywordIndexerHelper)(new Object(new FilterComparator(), (ContextObject)(new Object(56))))
         ));
      }

      return this._keyList;
   }

   @Override
   public final EditorUsingRIMModelFactory getSearchEditScreen(boolean fromRibbon) {
      SearchEditScreen searchEditScreen = new SearchEditScreen(this, fromRibbon);
      if (fromRibbon) {
         searchEditScreen.setModel(SearchEditScreen.getDefaultSearch());
         return searchEditScreen;
      } else {
         searchEditScreen.setModel(this._searchCollection.getLastFilter());
         return searchEditScreen;
      }
   }

   @Override
   public final EditorUsingRIMModelFactory getSearchEditScreen(Object filter, boolean fromRibbon) {
      try {
         FilterModel f = (FilterModel)filter;
         SearchEditScreen searchEditScreen = new SearchEditScreen(this, fromRibbon);
         searchEditScreen.setModel(f);
         return searchEditScreen;
      } finally {
         ;
      }
   }

   public final SearchSaveScreen getSearchSaveScreen(Object filter, boolean fromRibbon) {
      try {
         FilterModel f = (FilterModel)FilterModel.expandGroup((FilterModel)filter);
         SearchSaveScreen searchSaveScreen = new SearchSaveScreen(this, fromRibbon, filter);
         searchSaveScreen.setModel(f);
         return searchSaveScreen;
      } finally {
         ;
      }
   }

   @Override
   public final void runSearch(String name, boolean fromRibbon) {
      FilterModel m = this._searchCollection.find(name);
      if (m != null) {
         m.performSearch(this, false, fromRibbon, null);
      }
   }

   private static final void addNameSearch(WritableSet s, String name, int kind) {
      NameSearchModelFactory nsmf = NameSearchModelFactory.getInstance();
      NameSearchModel nsm = (NameSearchModel)nsmf.createInstance(null);
      nsm.setNameString(name);
      nsm.setType(kind);
      s.add(nsm);
   }

   private static final void addShowSearch(WritableSet s, int kind) {
      ShowSearchModelFactory ssmf = ShowSearchModelFactory.getInstance();
      ShowSearchModel ssm = (ShowSearchModel)ssmf.createInstance(null);
      ssm.setIndex(kind);
      s.add(ssm);
   }

   private static final void addTypeSearch(WritableSet s, int kind) {
      TypeSearchModelFactory tsmf = TypeSearchModelFactory.getInstance();
      TypeSearchModel tsm = (TypeSearchModel)tsmf.createInstance(null);
      tsm.setIndex(kind);
      s.add(tsm);
   }

   private static final void addFolderSearch(WritableSet s, Folder f) {
      FolderModelFactory fmf = FolderModelFactory.getInstance();
      FolderModel fm = (FolderModel)fmf.createInstance(f);
      s.add(fm);
   }

   private static final void addTitleAndHotKey(FilterModel fm, int title, int hotKey) {
      fm.addTitle(new BundleString(title));
      fm.addShortCutKey(getHotKeyFromResource(hotKey, InternalServices.isReducedFormFactor()));
   }

   private static final void addSubjectSearch(WritableSet s, String subject) {
      SubjectSearchModelFactory ssmf = SubjectSearchModelFactory.getInstance();
      SubjectSearchModel ssm = (SubjectSearchModel)ssmf.createInstance(null);
      ssm.setValue(subject);
      s.add(ssm);
   }

   private static final void addServiceSearchIfNecessary(FilterModel fm, Object context) {
      Long emailHierarchyLuid = (Long)ContextObject.get(context, -953487338188658393L);
      if (emailHierarchyLuid != null) {
         EmailHierarchy hierarchy = EmailHierarchy.getEmailHierarchy(emailHierarchyLuid);
         if (hierarchy != null) {
            ServiceSearchModelFactory ssmf = ServiceSearchModelFactory.getInstance();
            ServiceSearchModel ssm = (ServiceSearchModel)ssmf.createInstance(null);
            ssm.setService(hierarchy.getServiceUserId(), hierarchy.getServiceUidHash());
            fm.add(ssm);
         }
      }
   }

   private static final void addAsDefaultSearch(SearchCollection collection, FilterModel fm) {
      PopulateModels.populateModels(fm, 7820085525428081380L, null);
      FilterModel.createGroup(fm);
      collection.add(fm);
   }

   @Override
   public final Object nameSearch(String name, boolean firstMatch, Object context, boolean fromRibbon) {
      FilterModel fm = new FilterModel();
      addNameSearch(fm, name, 5);
      addServiceSearchIfNecessary(fm, context);
      return fm.performSearch(this, firstMatch, fromRibbon, context);
   }

   @Override
   public final Object subjectSearch(String subject, boolean firstMatch, Object context) {
      FilterModel fm = new FilterModel();
      addSubjectSearch(fm, subject);
      addServiceSearchIfNecessary(fm, context);
      return fm.performSearch(this, firstMatch, false, context);
   }

   @Override
   public final void folderSearch(Folder folder) {
      FilterModel fm = new FilterModel();
      String title = folder.getFriendlyName();
      if (title != null) {
         fm.add(FactoryUtil.createInstance(-4904857078378172834L, title));
      }

      addFolderSearch(fm, folder);
      fm.performSearch(this, false, false, null);
   }

   final void establishPrecannedSearches(SearchCollection collection) {
      establishInboxSearch(collection);
      if (!InternalServices.isReducedFormFactor()) {
         establishOutboxSearch(collection);
      }

      if (Phone.isSupported()) {
         if (SMSPacketHeader.isSendSupported()) {
            establishSMSSearch(collection);
         }

         establishPhoneSearch(collection);
         establishVoicemailSearch(collection);
      }

      if (DirectConnect.isSupported()) {
         establishDirectConnectSearch(collection);
      }
   }

   private static final void establishInboxSearch(SearchCollection collection) {
      FilterModel fm = new FilterModel();
      addTitleAndHotKey(fm, 33, 34);
      addShowSearch(fm, 9);
      addAsDefaultSearch(collection, fm);
   }

   private static final void establishOutboxSearch(SearchCollection collection) {
      FilterModel fm = new FilterModel();
      addTitleAndHotKey(fm, 35, 36);
      addShowSearch(fm, 10);
      addAsDefaultSearch(collection, fm);
   }

   private static final void establishSMSSearch(SearchCollection collection) {
      FilterModel fm = new FilterModel();
      addTitleAndHotKey(fm, 51, 52);
      addTypeSearch(fm, 13);
      addAsDefaultSearch(collection, fm);
   }

   private static final boolean establishMMSSearch(SearchCollection collection) {
      FilterModel fm = new FilterModel();
      addTitleAndHotKey(fm, 64, 10);
      addTypeSearch(fm, 20);
      if (collection.find(fm) == null) {
         addAsDefaultSearch(collection, fm);
         return true;
      } else {
         return false;
      }
   }

   private static final void establishPhoneSearch(SearchCollection collection) {
      FilterModel fm = new FilterModel();
      addTitleAndHotKey(fm, 53, 54);
      addTypeSearch(fm, 14);
      addAsDefaultSearch(collection, fm);
   }

   private static final void establishVoicemailSearch(SearchCollection collection) {
      FilterModel fm = new FilterModel();
      addTitleAndHotKey(fm, 60, 61);
      addTypeSearch(fm, 18);
      addAsDefaultSearch(collection, fm);
   }

   private static final void establishDirectConnectSearch(SearchCollection collection) {
      FilterModel fm = new FilterModel();
      addTitleAndHotKey(fm, 55, 56);
      addTypeSearch(fm, 16);
      addAsDefaultSearch(collection, fm);
   }

   MessageSearchImpl() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ar.put(7241276700323413934L, this);
      this._searchCollection = new SearchCollection(this);
      this.establishKeyChoices(false);
      this.refreshHotKeysFromCollection();
      MMS.onEnabled(new MessageSearchImpl$1(this));
   }

   final void repairPrecannedSearches() {
      boolean isReducedFormFactor = InternalServices.isReducedFormFactor();
      Hashtable resourceTable = (Hashtable)(new Object());
      int[][] resources = new int[][]{
         {33, 34, -804651006, 35, 36, -804650998, 46, 47},
         {51, 52, -804651006, 53, 54, -804519934, -1466262987, 980431202},
         {64, 10, -805044223, 102, -805044223, 131, 0, -804519930},
         {60, 61, -804651006, 64, 10, -805044223, 102, -805044223},
         {35, 36, -804650998, 46, 47, 48, 49, 57},
         {53, 54, -804519934, -1466262987, 980431202, -1205878232, 1883249540, -804651006}
      };

      for (int i = 0; i < resources.length; i++) {
         resourceTable.put(SearchResources.getString(resources[i][0]), resources[i]);
      }

      synchronized (this._searchCollection) {
         for (int i = this._searchCollection.size() - 1; i >= 0; i--) {
            FilterModel fm = (FilterModel)this._searchCollection.getAt(i);
            String title = fm._titleModel.getTitle();
            char hotKey = fm.getShortCutKey();
            int[] resourceIDs = (int[])resourceTable.get(title);
            if (resourceIDs != null) {
               int titleID = resourceIDs[0];
               int hotKeyID = resourceIDs[1];
               if (hotKey == 0 || hotKey == getHotKeyFromResource(hotKeyID, !isReducedFormFactor)) {
                  if (isReducedFormFactor && titleID == 53) {
                     FilterModel outgoing = this._searchCollection.find(SearchResources.getString(35));
                     if (outgoing != null && outgoing.getShortCutKey() == 'o') {
                        this.updateHotKeyInCollection(outgoing, '\u0000');
                     }
                  }

                  this.updateHotKeyInCollection(fm, getHotKeyFromResource(hotKeyID, isReducedFormFactor));
               }
            }
         }
      }
   }

   private static final char getHotKeyFromResource(int key, boolean isReducedFormFactor) {
      char hotKey = Character.toLowerCase(SearchResources.getString(key).charAt(0));
      if (isReducedFormFactor) {
         switch (key) {
            case 10:
               return 'm';
            case 34:
               return 'u';
            case 36:
               hotKey = 0;
               break;
            case 52:
               return 'a';
            case 54:
               return 'o';
            case 61:
               return 'c';
         }
      }

      return hotKey;
   }

   private final void updateHotKeyInCollection(FilterModel model, char c) {
      if (c != model.getShortCutKey() && (c == 0 || this.findHotKey(c) >= 0)) {
         FilterModel newModel = (FilterModel)FilterModel.expandGroup(model);
         this.returnHotKey(model.getShortCutKey(), true);
         if (c == 0) {
            newModel._shortCutKey = null;
         } else {
            newModel.addShortCutKey(c);
            this.takeHotKey(newModel.getShortCutKey(), new SearchVerb(newModel), false);
         }

         this._searchCollection.update(model, newModel);
      }
   }

   final void refreshHotKeysFromCollection() {
      for (int i = 0; i < this._searchCollection.size(); i++) {
         FilterModel fm = (FilterModel)this._searchCollection.getAt(i);
         this.takeHotKey(fm.getShortCutKey(), new SearchVerb(fm), false);
      }
   }

   protected final void setLastFilter(FilterModel m) {
      this._searchCollection.setLastFilter(m);
   }

   private static final void hotKeyProblem(char c, int found) {
      try {
         throw new Object(((StringBuffer)(new Object("Search hot key: '"))).append(c).append("' (").append(found).append(')').toString());
      } finally {
         return;
      }
   }

   private final int findHotKey(char c) {
      int n = this._keyChoices.length;

      for (int i = 1; i < n; i++) {
         Character ch = (Character)this._keyChoices[i];
         char key = ch;
         if (c == key) {
            return i;
         }

         if (c < key) {
            return -(i + 1);
         }
      }

      return -(n + 1);
   }

   final void takeHotKey(char c, Verb v, boolean logErrors) {
      if (c != 0) {
         int found = this.findHotKey(c);
         if (found >= 0) {
            int amtToCopy = this._keyChoices.length - found - 1;
            if (amtToCopy > 0) {
               System.arraycopy(this._keyChoices, found + 1, this._keyChoices, found, amtToCopy);
            }

            Array.resize(this._keyChoices, this._keyChoices.length - 1);
            if (!HotKeys.registerHotKey(7, Character.toUpperCase(c), v) && logErrors) {
               hotKeyProblem(c, 1001);
               return;
            }
         } else if (logErrors) {
            hotKeyProblem(c, found);
         }
      }
   }

   final void returnHotKey(char c, boolean logErrors) {
      if (c != 0) {
         int found = this.findHotKey(c);
         if (found < 0) {
            found = -found - 1;
            Array.resize(this._keyChoices, this._keyChoices.length + 1);
            int amtToCopy = this._keyChoices.length - found - 1;
            if (amtToCopy > 0) {
               System.arraycopy(this._keyChoices, found, this._keyChoices, found + 1, amtToCopy);
            }

            this._keyChoices[found] = new Object(c);
            if (!HotKeys.unregisterHotKey(7, Character.toUpperCase(c)) && logErrors) {
               hotKeyProblem(c, 1000);
               return;
            }
         } else if (logErrors) {
            hotKeyProblem(c, found);
         }
      }
   }

   final Object[] getUnusedHotKeys() {
      this._keyChoices[0] = SearchResources.getString(3);
      return this._keyChoices;
   }

   final void establishKeyChoices(boolean force) {
      if (this._keyChoices == null || force) {
         this._keyChoices = new Object[1];
         this._keyChoices[0] = SearchResources.getString(3);
         SLKeyLayout layout = Keypad.getLayout();
         if (layout != null) {
            for (int i = 0; i <= 25; i++) {
               char c = (char)(97 + i);
               StringBuffer complementaryChars = layout.getComplementaryChars(c, 0);
               if (complementaryChars == null || complementaryChars.charAt(0) == c) {
                  Arrays.add(this._keyChoices, new Object(c));
               }
            }
         }
      }
   }
}
