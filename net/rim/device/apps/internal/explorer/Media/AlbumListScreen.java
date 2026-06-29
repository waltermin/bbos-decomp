package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.explorer.Media.verbs.SelectMediaVerb;
import net.rim.device.apps.internal.explorer.MediaLibrary.Album;
import net.rim.device.apps.internal.explorer.MediaLibrary.Artist;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.Genre;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfoCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaLibrary;
import net.rim.device.apps.internal.explorer.file.SelectionListener;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class AlbumListScreen extends MediaListScreen {
   public AlbumListScreen(Object context) {
      super(context);
   }

   @Override
   protected final MediaInfoCollection getCollection() {
      return MediaLibrary.getInstance().getAlbumCollection();
   }

   @Override
   protected final String getTitle() {
      return ExplorerResources.getString(141);
   }

   @Override
   protected final String getEmptyString() {
      return ExplorerResources.getString(158);
   }

   @Override
   protected final void checkToAddAll(KeywordFilterCollectionListField list) {
      if (super._contextInfo.getType() == 6 || super._contextInfo.getType() == 14 || super._contextInfo.getType() == 12) {
         list.setExtraRowCount(1);
         super._allAdded = true;
      }
   }

   @Override
   protected final void checkToAddNew(KeywordFilterCollectionListField list) {
   }

   @Override
   protected final void buildSubset() {
      int type = super._contextInfo.getType();
      if (type != 4) {
         Artist artist = (Artist)super._contextInfo.getData(2);
         Genre genre = (Genre)super._contextInfo.getData(8);
         String[] keywords = new Object[0];
         switch (type) {
            case 6:
               Arrays.append(keywords, artist.getPrefixedKeywords());
               break;
            case 12:
               Arrays.append(keywords, genre.getPrefixedKeywords());
               break;
            case 14:
               Arrays.append(keywords, genre.getPrefixedKeywords());
               Arrays.append(keywords, artist.getPrefixedKeywords());
         }

         if (this.isExternal()) {
            super._keywordList = super._collection.getKeywordFilterListInstance(null);
         } else {
            super._keywordList = super._collection.getKeywordFilterList();
         }

         if (keywords.length > 0) {
            super._collection.addCriteria(super._keywordList, keywords);
         }
      }
   }

   @Override
   protected final boolean isTwoLine() {
      return false;
   }

   @Override
   public final boolean perform(long actionId, Object context) {
      if (actionId == 3712387911241450002L) {
         this.performAction(actionId);
         return true;
      } else if (actionId == 7051267296949023454L) {
         this.performAction(actionId);
         return true;
      } else {
         return super.perform(actionId, context);
      }
   }

   protected final void performAction(long actionId) {
      Album album = (Album)this.getSelectedObject();
      if (album != null) {
         ContextInfo contextInfo = ContextInfo.createCopy(super._contextInfo);
         contextInfo.setType(5);
         contextInfo.setData(4, album);
         TrackListScreen tls = new TrackListScreen(contextInfo);
         tls.perform(actionId, album);
         tls.cleanUp();
      }
   }

   @Override
   protected final void invoke() {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof Object) {
         Album album = (Album)this.getSelectedObject();
         Artist artist = (Artist)super._contextInfo.getData(2);
         Genre genre = (Genre)super._contextInfo.getData(8);
         SelectionListener listener = this.getSelectionListener();
         if (listener != null) {
            if (album != null) {
               SelectMediaVerb smv = null;
               Object selectedItem = album.toString();
               if (listener instanceof SelectMediaVerb) {
                  smv = (SelectMediaVerb)listener;
                  if (!smv.isFlagSet(4)) {
                     selectedItem = album;
                  }
               }

               listener.selected(selectedItem);
            }

            this.close();
         } else {
            ContextInfo contextInfo = ContextInfo.createCopy(super._contextInfo);
            if (super._allAdded && album == null && this.getSelectedIndex() == 0) {
               switch (super._contextInfo.getType()) {
                  case 6:
                     contextInfo.setType(3);
                     contextInfo.setData(2, artist);
                     break;
                  case 12:
                     contextInfo.setType(9);
                     contextInfo.setData(8, genre);
                     break;
                  case 14:
                     contextInfo.setType(11);
                     contextInfo.setData(8, genre);
                     contextInfo.setData(2, artist);
               }
            } else {
               if (album == null) {
                  return;
               }

               switch (super._contextInfo.getType()) {
                  case 4:
                     contextInfo.setType(5);
                     contextInfo.setData(4, album);
                     break;
                  case 6:
                     contextInfo.setType(7);
                     contextInfo.setData(2, artist);
                     contextInfo.setData(4, album);
                     break;
                  case 12:
                     contextInfo.setType(13);
                     contextInfo.setData(8, genre);
                     contextInfo.setData(4, album);
                     break;
                  case 14:
                     contextInfo.setType(14);
                     contextInfo.setData(2, artist);
                     contextInfo.setData(8, genre);
                     contextInfo.setData(4, album);
               }
            }

            UiApplication.getUiApplication().pushScreen(new TrackListScreen(contextInfo));
         }
      }
   }
}
