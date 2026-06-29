package net.rim.device.apps.api.framework.file;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.verb.Verb;

public class FileSelector extends FileSelectionFilter {
   private Verb _fileSelectVerb;
   private ContextObject _ctx;

   public FileSelector(String initialPath, Object context, int mediaType, Recognizer filter) {
      this(initialPath, context, mediaType, false, filter);
   }

   public FileSelector(String initialPath, Object context, int mediaType, boolean writeable, Recognizer filter) {
      super(mediaType, writeable, filter);
      this.initialize(initialPath, context);
   }

   public FileSelector(String initialPath, Object context, int mediaType, int selectAttribs, Recognizer filter) {
      super(mediaType, selectAttribs, filter);
      this.initialize(initialPath, context);
   }

   private void initialize(String initialPath, Object context) {
      this._ctx = ContextObject.clone(context);
      this._ctx.setFlag(5);
      this._ctx.put(-1002650280265073678L, this);
      if (initialPath != null) {
         this._ctx.put(2765042845091913199L, initialPath);
      }

      this._fileSelectVerb = ExplorerServices.getSelectVerb(initialPath, this._ctx, this.getMediaType(), this.getRecognizer());
   }

   public FileSelector(String initialPath) {
      this(initialPath, null, 0, null);
   }

   public FileSelector(String initialPath, int mediaType) {
      this(initialPath, null, mediaType, null);
   }

   public FileSelector(String initialPath, int mediaType, Recognizer filter) {
      this(initialPath, null, mediaType, filter);
   }

   public void setPath(String path) {
      if (path != null) {
         this._ctx.put(2765042845091913199L, path);
      }
   }

   public String getPath() {
      return (String)ContextObject.get(this._ctx, 2765042845091913199L);
   }

   public String selectFile(String path) {
      if (path == null) {
         path = this.getPath();
      }

      if (path != null) {
         this._ctx.put(2765042845091913199L, path);
      }

      return (String)(this._fileSelectVerb != null ? this._fileSelectVerb.invoke(this._ctx) : null);
   }
}
