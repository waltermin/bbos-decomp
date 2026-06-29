package net.rim.device.apps.api.framework.file;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;

public class FileBrowser {
   private Verb _fileBrowserVerb;
   private ContextObject _ctx = new ContextObject();
   public static final int VIEW_MODE_THUMBNAIL = 0;
   public static final int VIEW_MODE_LIST = 1;
   public static final int VIEW_MODE_DETAILS = 2;
   public static final long KEY = -7165719342027358947L;
   public static final long INTEGER_MENU_ITEM_INVOKE = -8073278814961745892L;

   public FileBrowser(String defaultPath, int mediaType, VerbProvider verbProvider) {
      this(defaultPath, mediaType, 0, verbProvider);
   }

   public FileBrowser(String defaultPath, int mediaType, int view, VerbProvider verbProvider) {
      this._fileBrowserVerb = ExplorerServices.getBrowseVerb(defaultPath, mediaType, verbProvider);
      this.setView(view);
      this.setMediaType(mediaType);
   }

   public void setRecognizer(Recognizer filter) {
      this._ctx.put(-409744358660961448L, filter);
   }

   public void setMediaType(int mediaType) {
      this._ctx.putIntegerData(mediaType);
   }

   public void setVerbProvider(VerbProvider verbProvider) {
      this._ctx.put(424670468422402792L, verbProvider);
   }

   public void setView(int view) {
      this._ctx.setPrivateFlag(-7165719342027358947L, view);
   }

   public void show() {
      this.show(null);
   }

   public void show(String directoryPath) {
      if (directoryPath != null) {
         this._ctx.put(2765042845091913199L, directoryPath);
      }

      this._fileBrowserVerb.invoke(this._ctx);
   }
}
