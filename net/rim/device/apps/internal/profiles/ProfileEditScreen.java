package net.rim.device.apps.internal.profiles;

import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.notification.NotificationsManager$SourceListener;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.profiles.SourceObjectWrapper;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.SystemEnabledMenu;

final class ProfileEditScreen extends InputHandlingScreen implements ListFieldCallback, NotificationsManager$SourceListener {
   private Profile _profile;
   private SourceObjectWrapper[] _sources;
   private ListField _listField;

   public ProfileEditScreen(Profile profile, boolean newProfile) {
      super((ContextObject)(new Object(newProfile ? 6 : 0)));
      NotificationsManager.addSourceChangedListener(this);
      this.getSortedSources();
      if (profile.getIdentifier() == -1) {
         this.setSaveVerb(profile);
      }

      this.setTitle(profile, null);
      this._profile = profile;
      this._listField = (ListField)(new Object(this._sources.length));
      this._listField.setCallback(this);
      this.add(this._listField);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void getSortedSources() {
      this._sources = new Object[0];
      long[] registeredSourceIds = NotificationsManager.enumerateSourceIds();

      for (int i = 0; i < registeredSourceIds.length; i++) {
         long sourceId = registeredSourceIds[i];
         if (!NotificationsManager.isHidden(sourceId)) {
            Object o = NotificationsManager.getSource(sourceId);
            if (o != null) {
               Arrays.add(this._sources, new Object(o, sourceId));
            }
         }
      }

      try {
         Arrays.sort(this._sources, new ProfileEditScreen$SortComparator());
      } catch (Throwable var10) {
         int length = this._sources.length;
         String error = "\n";
         if (this._profile != null) {
            error = ((StringBuffer)(new Object())).append(error).append("Profile - ").append(this._profile.getName()).append("\n").toString();
         }

         error = ((StringBuffer)(new Object())).append(error).append("Error sorting the following sources.\n").toString();

         for (int index = 0; index < length; index++) {
            SourceObjectWrapper source = this._sources[index];
            if (source != null) {
               error = ((StringBuffer)(new Object()))
                  .append(error)
                  .append(source.getString())
                  .append(" : ")
                  .append(NotificationsManager.getSourceId(source.getObject()))
                  .append("\n")
                  .toString();
            }
         }

         error = ((StringBuffer)(new Object())).append(error).append(e.toString()).toString();
         EventLogger.logEvent(6982943375119825480L, error.getBytes(), 2);
         return;
      }
   }

   @Override
   public final boolean onClose() {
      super._closeVerb.invoke(super._context);
      if (!this.isDisplayed()) {
         NotificationsManager.removeSourceChangedListener(this);
      }

      return true;
   }

   @Override
   protected final boolean keyChar(char keyChar, int status, int time) {
      if (keyChar == '\n' && this.getFieldWithFocus() == this._listField) {
         int selectedIndex = this._listField.getSelectedIndex();
         Object source = selectedIndex != -1 ? this._sources[selectedIndex] : null;
         if (source != null) {
            Verb verb = new ProfileEditScreen$EditConfigurationVerb(this, source);
            verb.invoke(super._context);
            return true;
         }
      }

      return keyChar == 27 ? this.onClose() : super.keyChar(keyChar, status, time);
   }

   private final Verb getEditVerb() {
      Field field = this.getFieldWithFocus();
      if (field == this._listField) {
         int selectedIndex = this._listField.getSelectedIndex();
         Object source = selectedIndex != -1 ? this._sources[selectedIndex] : null;
         if (source != null) {
            return new ProfileEditScreen$EditConfigurationVerb(this, source);
         }
      }

      return null;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      super.makeMenu(menu, instance);
      Verb editVerb = this.getEditVerb();
      if (editVerb != null) {
         menu.add(editVerb);
         menu.setDefault(editVerb);
      }
   }

   @Override
   protected final boolean invokeAction(int action) {
      switch (action) {
         case 1:
            Verb editVerb = this.getEditVerb();
            if (editVerb != null) {
               editVerb.invoke(null);
               return true;
            }
         default:
            return super.invokeAction(action);
      }
   }

   @Override
   public final void drawListRow(ListField aListField, Graphics aGraphics, int indexInt, int yInt, int widthInt) {
      if (indexInt < this._sources.length) {
         int rowHeight = aListField.getRowHeight();
         Object source = this._sources[indexInt];
         Object context = super._context;
         if (source instanceof Object) {
            PaintProvider paintProvider = (PaintProvider)source;
            paintProvider.paint(aGraphics, 0, yInt, widthInt, rowHeight, context);
         }
      }
   }

   @Override
   public final int indexOfList(ListField aListField, String prefixString, int startInt) {
      Object source = null;
      int listFieldSize = this._listField.getSize();

      for (int i = startInt; i < listFieldSize; i++) {
         source = this._sources[i];
         if (source instanceof Object && ((MatchProvider)source).match(prefixString) == 1) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public final int getPreferredWidth(ListField aListField) {
      return this._listField.getPreferredWidth();
   }

   @Override
   public final Object get(ListField aListField, int indexInt) {
      return this._sources[indexInt];
   }

   @Override
   public final void sourceUpdated() {
      this.getSortedSources();
      this.invalidate();
   }
}
