package net.rim.device.apps.api.messaging.ui;

import net.rim.device.api.util.BitSet;
import net.rim.tid.im.conv.repository.IDataSearchRepository;
import net.rim.vm.Array;

final class FolderList$DataSearchRepositoryImpl implements IDataSearchRepository {
   private final FolderList this$0;

   private FolderList$DataSearchRepositoryImpl(FolderList _1) {
      this.this$0 = _1;
   }

   @Override
   public final BitSet searchPrefixes(String[] words) {
      if (words == null) {
         this.this$0.resetQuery();
      } else {
         Array.resize(this.this$0._query, words.length);
         System.arraycopy(words, 0, this.this$0._query, 0, words.length);
      }

      this.this$0._engine.startSearch();
      return this.this$0._engine._foundWordsSet;
   }

   FolderList$DataSearchRepositoryImpl(FolderList x0, FolderList$1 x1) {
      this(x0);
   }
}
