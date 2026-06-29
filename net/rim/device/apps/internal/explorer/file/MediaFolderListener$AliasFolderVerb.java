package net.rim.device.apps.internal.explorer.file;

import net.rim.device.apps.api.framework.verb.Verb;

final class MediaFolderListener$AliasFolderVerb extends Verb {
   String _path;
   private final MediaFolderListener this$0;

   public MediaFolderListener$AliasFolderVerb(MediaFolderListener _1, String path) {
      super(0);
      this.this$0 = _1;
      this._path = path;
   }

   @Override
   public final Object invoke(Object context) {
      ExploreManager manager = this.this$0.getManager();
      if (manager != null) {
         manager.pushPath(new FileItemField(this._path));
      }

      return null;
   }
}
