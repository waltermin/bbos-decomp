package net.rim.device.apps.api.framework.file;

import java.io.InputStream;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

public class SaveInputStreamVerb extends Verb {
   ContextObject _ctx;
   boolean _confirmName;
   boolean _overwrite;

   public SaveInputStreamVerb(String defaultFilename, InputStream inputStream, int mediaType, boolean confirmName, boolean overwrite) {
      this(null, confirmName, overwrite);
      if (inputStream != null) {
         this._ctx.put(5473606008898265655L, inputStream);
      }

      if (defaultFilename != null) {
         this._ctx.put(2765042845091913199L, defaultFilename);
      }
   }

   public SaveInputStreamVerb(ContextObject context, boolean confirmName, boolean overwrite) {
      super(332288, ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer"), 39);
      this._ctx = ContextObject.clone(context);
      this._confirmName = confirmName;
      this._overwrite = overwrite;
   }

   @Override
   public Object invoke(Object parameter) {
      if (parameter instanceof InputStream) {
         this._ctx.put(5473606008898265655L, parameter);
      } else if (parameter instanceof ContextObject) {
         this._ctx = (ContextObject)parameter;
      }

      String defaultFilename = (String)this._ctx.get(2765042845091913199L);
      if (defaultFilename == null) {
         ResourceBundle rb = ResourceBundle.getBundle(349501092522026426L, "net.rim.device.apps.internal.resource.Explorer");
         defaultFilename = rb.getString(42);
      }

      InputStream inputStream = (InputStream)this._ctx.get(5473606008898265655L);
      int mediaType = this._ctx.getIntegerData(MIMETypeAssociations.getMediaType(defaultFilename));
      return ExplorerServices.saveInputStream(defaultFilename, inputStream, mediaType, this._confirmName, this._overwrite);
   }
}
