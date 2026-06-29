package net.rim.device.apps.api.utility.options;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.framework.ModelUser;

public class OptionsEditorVerb extends Verb {
   protected long _rimModelFactoryId;
   protected int _separatorInterval;
   private ResourceBundleFamily _etResourceBundle;
   private int _etKey;

   public OptionsEditorVerb(
      int menuOrdering,
      ResourceBundleFamily resourceBundle,
      int rbKey,
      ResourceBundleFamily etResourceBundle,
      int etKey,
      long rimModelFactoryId,
      int separatorInterval
   ) {
      super(menuOrdering, resourceBundle, rbKey);
      this._etResourceBundle = etResourceBundle;
      this._etKey = etKey;
      this._rimModelFactoryId = rimModelFactoryId;
      this._separatorInterval = separatorInterval;
   }

   @Override
   public Object invoke(Object parameter) {
      ModelUser composeScreen = (ModelUser)ContextObject.get(parameter, -6581931217101110672L);
      OptionsEditorScreen optionsEditorScreen = new OptionsEditorScreen(
         parameter, this._etResourceBundle.getString(this._etKey), this._rimModelFactoryId, this._separatorInterval
      );
      optionsEditorScreen.setModel(composeScreen.getModel(false));
      return optionsEditorScreen.run();
   }
}
