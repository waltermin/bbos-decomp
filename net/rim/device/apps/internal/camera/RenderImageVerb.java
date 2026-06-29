package net.rim.device.apps.internal.camera;

import net.rim.device.apps.api.framework.file.ExplorerServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

public final class RenderImageVerb extends Verb {
   String _filename;

   public RenderImageVerb() {
      super(565264, 349501092522026426L, "net.rim.device.apps.internal.resource.Explorer", 8);
   }

   public RenderImageVerb(String filename) {
      this();
      this._filename = filename;
   }

   @Override
   public final Object invoke(Object parameter) {
      String fileURL = null;
      if (parameter instanceof Object) {
         fileURL = (String)ContextObject.get(parameter, 2765042845091913199L);
      } else if (!(parameter instanceof Object)) {
         fileURL = this._filename;
      } else {
         fileURL = (String)parameter;
      }

      if (fileURL != null) {
         ContextObject context = (ContextObject)(new Object(45));
         context.put(2765042845091913199L, fileURL);
         context.putIntegerData(1);
         context.put(-8073278814961745892L, new Object(5));
         Verb browseVerb = ExplorerServices.getBrowseVerb(fileURL, 1, null);
         browseVerb.invoke(context);
      }

      return null;
   }
}
