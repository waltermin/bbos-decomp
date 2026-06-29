package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.profiles.SourceObjectWrapper;

final class ConfigurationEditScreen extends InputHandlingScreen {
   public ConfigurationEditScreen(Profile profile, Object source) {
      super(new ContextObject(69));
      this.setSaveVerb(profile);
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      String[] pairOfStrings = new String[]{resources.getString(203), profile.getName()};
      ContextObject context = ContextObject.clone(super._context);
      context.put(251, pairOfStrings);
      context.put(-4054673099568009991L, new Integer(profile.getIdentifier()));
      this.setTitle((RIMModel)source, context);
      super._originalSource = source;
      if (source instanceof SourceObjectWrapper) {
         super._originalSource = ((SourceObjectWrapper)source).getObject();
      }

      long sourceId = NotificationsManager.getSourceId(super._originalSource);
      Object configuration = profile.getConfiguration(-2870941457036655797L, sourceId);
      if (configuration instanceof FieldProvider) {
         FieldProvider fp = (FieldProvider)configuration;
         Field field = null;
         if (sourceId == 3975384895524745189L) {
            context.setFlag(48);
         }

         context.put(250, super._originalSource);
         field = fp.getField(context);
         if (field != null) {
            this.add(field);
         }

         context.setFlag(67);
         field = fp.getField(context);
         if (field != null) {
            this.add(new SeparatorField());
            this.add(field);
         }

         context.clearFlag(67);
         context.setFlag(89);
         field = fp.getField(context);
         if (field != null) {
            this.add(new SeparatorField());
            this.add(field);
         }

         context.clearFlag(89);
      }
   }

   @Override
   public final boolean onClose() {
      super._closeVerb.invoke(super._context);
      return true;
   }
}
