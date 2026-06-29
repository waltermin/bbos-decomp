package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.internal.explorer.Media.verbs.SelectMediaVerb;
import net.rim.device.apps.internal.explorer.MediaLibrary.Artist;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.Genre;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfoCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaLibrary;
import net.rim.device.apps.internal.explorer.file.SelectionListener;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class ArtistListScreen extends MediaListScreen {
   public ArtistListScreen(Object context) {
      super(context);
   }

   @Override
   protected final MediaInfoCollection getCollection() {
      return MediaLibrary.getInstance().getArtistCollection();
   }

   @Override
   protected final String getTitle() {
      return ExplorerResources.getString(140);
   }

   @Override
   protected final String getEmptyString() {
      return ExplorerResources.getString(159);
   }

   @Override
   protected final void checkToAddAll(KeywordFilterCollectionListField list) {
      if (super._contextInfo.getType() == 10) {
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
      if (type != 2) {
         Genre genre = (Genre)super._contextInfo.getData(8);
         if (genre != null) {
            String[] keywords = new String[0];
            switch (type) {
               case 10:
                  Arrays.append(keywords, genre.getPrefixedKeywords());
               default:
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
      }
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
      Artist artist = (Artist)this.getSelectedObject();
      if (artist != null) {
         ContextInfo contextInfo = ContextInfo.createCopy(super._contextInfo);
         contextInfo.setType(3);
         contextInfo.setData(2, artist);
         TrackListScreen tls = new TrackListScreen(contextInfo);
         tls.perform(actionId, artist);
         tls.cleanUp();
      }
   }

   @Override
   protected final void invoke() {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof ListField) {
         Artist artist = (Artist)this.getSelectedObject();
         Genre genre = (Genre)super._contextInfo.getData(8);
         SelectionListener listener = this.getSelectionListener();
         if (listener != null) {
            if (artist != null) {
               SelectMediaVerb smv = null;
               Object selectedItem = artist.toString();
               if (listener instanceof SelectMediaVerb) {
                  smv = (SelectMediaVerb)listener;
                  if (!smv.isFlagSet(4)) {
                     selectedItem = artist;
                  }
               }

               listener.selected(selectedItem);
            }

            this.close();
         } else {
            ContextInfo contextInfo = ContextInfo.createCopy(super._contextInfo);
            if (super._allAdded && artist == null && this.getSelectedIndex() == 0) {
               contextInfo.setType(12);
               contextInfo.setData(8, genre);
            } else if (artist != null && genre != null) {
               contextInfo.setType(14);
               contextInfo.setData(8, genre);
               contextInfo.setData(2, artist);
            } else {
               if (artist == null) {
                  return;
               }

               contextInfo.setType(6);
               contextInfo.setData(2, artist);
            }

            UiApplication.getUiApplication().pushScreen(new AlbumListScreen(contextInfo));
         }
      }
   }
}
