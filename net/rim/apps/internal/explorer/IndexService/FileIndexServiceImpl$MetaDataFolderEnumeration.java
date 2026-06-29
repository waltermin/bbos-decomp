package net.rim.apps.internal.explorer.IndexService;

import java.util.Enumeration;

final class FileIndexServiceImpl$MetaDataFolderEnumeration implements Enumeration {
   Enumeration _elements;
   int _mediaType;
   String _lookAhead;
   private final FileIndexServiceImpl this$0;

   FileIndexServiceImpl$MetaDataFolderEnumeration(FileIndexServiceImpl _1, Enumeration elements, int mediaType) {
      this.this$0 = _1;
      this._elements = elements;
      this._mediaType = mediaType;
      this.lookAhead();
   }

   private final void lookAhead() {
      while (this._elements.hasMoreElements()) {
         String lookAhead = (String)this._elements.nextElement();
         Object o = this.this$0._mediaFolderList.get(lookAhead);
         if (o instanceof int[]) {
            if (this._mediaType == 0) {
               this._lookAhead = lookAhead;
               return;
            }

            int[] mediaCounts = (int[])o;
            if (mediaCounts.length > this._mediaType && mediaCounts[this._mediaType] != 0) {
               this._lookAhead = lookAhead;
               return;
            }
         }
      }

      this._lookAhead = null;
   }

   @Override
   public final boolean hasMoreElements() {
      return this._lookAhead != null;
   }

   @Override
   public final Object nextElement() {
      String folderURL = this._lookAhead;
      this.lookAhead();
      return folderURL;
   }
}
