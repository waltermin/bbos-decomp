package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.resource.PMEPlugginResource;
import net.rim.plazmic.internal.mediaengine.MediaModel;
import net.rim.plazmic.internal.mediaengine.util.MEUtilities;
import net.rim.plazmic.mediaengine.MediaPlayer;

final class InfoVerb extends Verb implements PMEPlugginResource, FieldChangeListener {
   private MediaPlayer _player;

   public InfoVerb(MediaPlayer player) {
      super(341278, 1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin", 7);
      this._player = player;
   }

   @Override
   public final Object invoke(Object context) {
      boolean wasPaused = this._player.getState() != 2;
      ResourceBundle _resources = ResourceBundle.getBundle(1239635442466553906L, "net.rim.device.apps.internal.resource.PMEPluggin");

      try {
         if (!wasPaused) {
            this._player.stop();
         }

         String nullStr = "";
         MediaModel model = MEUtilities.getMediaModel(this._player.getMedia());
         if (model == null) {
            return null;
         }

         Dialog d = new Dialog(_resources.getString(7), null, null, 1, null, 281474976710656L);
         String cr = "\n\n";
         String message = nullStr;
         int numMeta = model.getNumMetaInfo();

         for (int x = 0; x < numMeta; x++) {
            String key = model.getMetaKey(x);
            if (StringUtilities.toLowerCase(key, 1701707776).equals("title")) {
               key = _resources.getString(8);
            } else if (StringUtilities.toLowerCase(key, 1701707776).equals("desc") || StringUtilities.toLowerCase(key, 1701707776).equals("description")) {
               key = _resources.getString(10);
            }

            message = message + key + ": " + model.getMetaValue(x) + cr;
         }

         RichTextField label = new RichTextField(message, 18014398576590848L);
         d.add(label);
         ButtonField button = new ButtonField(CommonResources.getString(117), 12884901888L);
         button.setChangeListener(this);
         d.add(button);
         d.setIcon(ThemeManager.getActiveTheme().getImage("dialog_information"));
         d.doModal();
         if (!wasPaused) {
            this._player.start();
            return null;
         }
      } finally {
         return null;
      }

      return null;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field instanceof ButtonField) {
         field.getScreen().close();
      }
   }
}
