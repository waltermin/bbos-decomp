package net.rim.device.apps.internal.profiles;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;

final class ProfilesScreen$ProfileListField extends CollectionListField implements ListFieldCallback {
   private final ProfilesScreen this$0;

   ProfilesScreen$ProfileListField(ProfilesScreen _1) {
      super(_1._profiles, null);
      this.this$0 = _1;
      this.setCallback(this);
      _1._profiles.addCollectionListener(this);
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            Profile profile = (Profile)this.this$0._listField.getSelectedElement();
            if (profile instanceof VerbProvider) {
               VerbProvider verbProvider = profile;
               Verb[] providedVerbs = new Verb[0];
               Verb defaultVerb = verbProvider.getVerbs(null, providedVerbs);
               if (defaultVerb != null) {
                  Object result = defaultVerb.invoke(null);
                  if (ContextObject.getFlag(result, 39)) {
                     this.this$0.close();
                  }

                  return true;
               }
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   protected final boolean keyChar(char keyChar, int statusInt, int timeInt) {
      if (keyChar == 127) {
         Profile selectedProfile = (Profile)this.getSelectedElement();
         if (selectedProfile != null && selectedProfile.isRemovable()) {
            DeleteProfileVerb verb = new DeleteProfileVerb(selectedProfile);
            verb.invoke(null);
         }

         return true;
      } else {
         Profile profile = (Profile)this.getSelectedElement();
         if (profile instanceof VerbProvider) {
            VerbProvider verbProvider = profile;
            ContextObject context = new ContextObject();
            context.putIntegerData(keyChar);
            Verb[] providedVerbs = new Verb[0];
            Verb defaultVerb = verbProvider.getVerbs(context, providedVerbs);
            if (defaultVerb != null) {
               Object result = defaultVerb.invoke(context);
               if (ContextObject.getFlag(result, 39)) {
                  this.this$0.close();
               }

               return true;
            }
         }

         return super.keyChar(keyChar, statusInt, timeInt);
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return 10;
   }

   @Override
   public final Object get(ListField listField, int index) {
      return null;
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final void drawListRow(ListField aListField, Graphics aGraphics, int indexInt, int yInt, int widthInt) {
      if (indexInt < this.this$0._profiles.size()) {
         Profile profile = (Profile)this.this$0._profiles.getAt(indexInt);
         profile.paint(aGraphics, 0, yInt, widthInt, aListField.getRowHeight(), null);
      }
   }
}
