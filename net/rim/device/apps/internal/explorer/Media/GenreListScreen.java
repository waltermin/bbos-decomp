package net.rim.device.apps.internal.explorer.Media;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.KeywordFilterCollectionListField;
import net.rim.device.apps.internal.explorer.Media.verbs.SelectMediaVerb;
import net.rim.device.apps.internal.explorer.MediaLibrary.ContextInfo;
import net.rim.device.apps.internal.explorer.MediaLibrary.Genre;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaInfoCollection;
import net.rim.device.apps.internal.explorer.MediaLibrary.MediaLibrary;
import net.rim.device.apps.internal.explorer.file.SelectionListener;
import net.rim.device.apps.internal.explorer.file.resource.ExplorerResources;

public final class GenreListScreen extends MediaListScreen {
   public GenreListScreen(Object context) {
      super(context);
   }

   @Override
   protected final MediaInfoCollection getCollection() {
      return MediaLibrary.getInstance().getGenreCollection();
   }

   @Override
   protected final String getTitle() {
      return ExplorerResources.getString(142);
   }

   @Override
   protected final String getEmptyString() {
      return ExplorerResources.getString(160);
   }

   @Override
   protected final void buildSubset() {
   }

   @Override
   protected final void checkToAddAll(KeywordFilterCollectionListField list) {
   }

   @Override
   protected final void checkToAddNew(KeywordFilterCollectionListField list) {
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
      Genre genre = (Genre)this.getSelectedObject();
      if (genre != null) {
         ContextInfo contextInfo = ContextInfo.createCopy(super._contextInfo);
         contextInfo.setType(9);
         contextInfo.setData(8, genre);
         TrackListScreen tls = new TrackListScreen(contextInfo);
         tls.perform(actionId, genre);
         tls.cleanUp();
      }
   }

   @Override
   protected final void invoke() {
      Field field = this.getLeafFieldWithFocus();
      if (field instanceof Object) {
         Genre genre = (Genre)this.getSelectedObject();
         SelectionListener listener = this.getSelectionListener();
         if (listener != null) {
            if (genre != null) {
               SelectMediaVerb smv = null;
               Object selectedItem = genre.toString();
               if (listener instanceof SelectMediaVerb) {
                  smv = (SelectMediaVerb)listener;
                  if (!smv.isFlagSet(4)) {
                     selectedItem = genre;
                  }
               }

               listener.selected(selectedItem);
            }

            this.close();
         } else if (genre != null) {
            ContextInfo contextInfo = ContextInfo.createCopy(super._contextInfo);
            contextInfo.setType(10);
            contextInfo.setData(8, genre);
            UiApplication.getUiApplication().pushScreen(new ArtistListScreen(contextInfo));
         }
      }
   }
}
