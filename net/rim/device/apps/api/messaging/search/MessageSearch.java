package net.rim.device.apps.api.messaging.search;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.apps.api.messaging.Folder;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.api.utility.framework.SimplePersistentEncryptedSyncCollection;

public class MessageSearch {
   protected static final long SEARCH_SINGLETON = 7241276700323413934L;
   public static final long FOLDER_SELECT_VERB_KEY = 3746287353255662597L;
   private static MessageSearch _messageSearch;

   public EditorUsingRIMModelFactory getSearchEditScreen(Object _1, boolean _2) {
      throw null;
   }

   public EditorUsingRIMModelFactory getSearchEditScreen(boolean _1) {
      throw null;
   }

   public void runSearch(String _1, boolean _2) {
      throw null;
   }

   public Object nameSearch(String _1, boolean _2, Object _3, boolean _4) {
      throw null;
   }

   public Object subjectSearch(String _1, boolean _2, Object _3) {
      throw null;
   }

   public void folderSearch(Folder _1) {
      throw null;
   }

   public SimplePersistentEncryptedSyncCollection getCollection() {
      throw null;
   }

   public static MessageSearch getInstance() {
      if (_messageSearch == null) {
         _messageSearch = (MessageSearch)ApplicationRegistry.getApplicationRegistry().get(7241276700323413934L);
      }

      return _messageSearch;
   }
}
