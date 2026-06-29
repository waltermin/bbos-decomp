package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.notification.Consequence;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PaintProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.vm.Array;

public final class Profile implements PersistableRIMModel, SyncObject, VerbProvider, FieldProvider, PaintProvider {
   private int _uid;
   private String _name;
   private byte _identifier;
   private LongHashtable _configurations;

   @Override
   public final int paint(Graphics graphics, int x, int y, int width, int height, Object context) {
      StringBuffer buffer = (StringBuffer)(new Object(this.getName()));
      boolean on = Profiles.getInstance().getEnabled() == this;
      Font f;
      if (on) {
         f = Font.getDefault().derive(1 | Font.getDefault().getStyle());
         buffer.append(
            ((StringBuffer)(new Object(" ")))
               .append(ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles").getString(248))
               .toString()
         );
      } else {
         f = Font.getDefault();
      }

      graphics.setFont(f);
      return graphics.drawText(buffer, 0, buffer.length(), x, y, 64, width);
   }

   @Override
   public final Verb getVerbs(Object contextObject, Verb[] verbArray) {
      Profiles profiles = Profiles.getInstance();
      Verb defaultVerb = null;
      char key = (char)ContextObject.getIntegerData(contextObject, 0);
      if (key != 0) {
         if (key == '\n' && this.isEditable()) {
            defaultVerb = new EditProfileVerb(this);
         } else if (key == ' ' && this != profiles.getEnabled()) {
            defaultVerb = new EnableProfileVerb(this);
         } else if ((key == 127 || Keypad.getAltedChar(key) == 127) && this.isRemovable()) {
            defaultVerb = new DeleteProfileVerb(this);
         }
      } else {
         Array.resize(verbArray, 0);
         Arrays.add(verbArray, new EditProfileVerb(null));
         if (this.isEditable()) {
            defaultVerb = new EditProfileVerb(this);
            Arrays.add(verbArray, defaultVerb);
         }

         if (this.isRemovable()) {
            Arrays.add(verbArray, new DeleteProfileVerb(this));
         }

         if (profiles.getEnabled() != this) {
            defaultVerb = new EnableProfileVerb(this);
            Arrays.add(verbArray, defaultVerb);
         }
      }

      return defaultVerb;
   }

   final String getSyncName() {
      switch (this._identifier) {
         case -1:
            return this._name;
         case 0:
         default:
            return "_loud_profile_";
         case 1:
            return "_discreet_profile_";
         case 2:
            return "_quiet_profile_";
         case 3:
            return "_default_profile_";
         case 4:
            return "_paging_profile_";
         case 5:
            return "_phone_only_profile_";
         case 6:
            return "_legacy_profile_";
         case 7:
            return "_off_profile_";
      }
   }

   @Override
   public final int getUID() {
      return this._uid;
   }

   public final boolean isRemovable() {
      return this._identifier == -1 || this._identifier == 6;
   }

   public final boolean isEditable() {
      return this._identifier != 7;
   }

   public final byte getIdentifier() {
      return this._identifier;
   }

   public final void setIdentifier(byte identifierByte) {
      this._identifier = identifierByte;
   }

   public final Object getConfiguration(long consequenceIdLong, long sourceIdLong) {
      Object configuration = null;
      synchronized (this._configurations) {
         LongHashtable configurationsPerSource = (LongHashtable)this._configurations.get(sourceIdLong);
         if (configurationsPerSource == null) {
            configurationsPerSource = (LongHashtable)(new Object());
            this._configurations.put(sourceIdLong, configurationsPerSource);
            PersistentObject.commit(this._configurations);
         }

         if ((configuration = configurationsPerSource.get(consequenceIdLong)) == null) {
            int level = NotificationsManager.getSourceLevel(sourceIdLong);
            Consequence consequence = NotificationsManager.getConsequence(consequenceIdLong);
            long parentSourceID = NotificationsManager.getParentSourceID(sourceIdLong);
            if (parentSourceID != -1) {
               Object parentConfiguration = configurationsPerSource.get(parentSourceID);
               if (parentConfiguration instanceof Object) {
                  Copyable cp = (Copyable)parentConfiguration;
                  configuration = cp.copy();
               }
            }

            if (configuration == null) {
               configuration = consequence.newConfiguration(consequenceIdLong, sourceIdLong, this._identifier, level, null);
            }

            configurationsPerSource.put(consequenceIdLong, configuration);
            PersistentObject.commit(configurationsPerSource);
         }

         return configuration;
      }
   }

   public final void setConfiguration(long consequenceIdLong, long sourceIdLong, Object configurationObject) {
      synchronized (this._configurations) {
         LongHashtable configurationsPerSource = (LongHashtable)this._configurations.get(sourceIdLong);
         if (configurationsPerSource == null) {
            configurationsPerSource = (LongHashtable)(new Object());
            this._configurations.put(sourceIdLong, configurationsPerSource);
            PersistentObject.commit(this._configurations);
         }

         if (configurationObject == null) {
            configurationsPerSource.remove(consequenceIdLong);
         } else {
            configurationsPerSource.put(consequenceIdLong, configurationObject);
            long[] relatedSourceIDs = NotificationsManager.getRelatedSourceIds(sourceIdLong, true);

            for (int i = 0; i < relatedSourceIDs.length; i++) {
               LongHashtable relatedSourceConfigurations = (LongHashtable)this._configurations.get(relatedSourceIDs[i]);
               if (relatedSourceConfigurations == null) {
                  relatedSourceConfigurations = (LongHashtable)(new Object());
                  this._configurations.put(relatedSourceIDs[i], relatedSourceConfigurations);
                  PersistentObject.commit(this._configurations);
               }

               if (!relatedSourceConfigurations.containsKey(consequenceIdLong)) {
                  relatedSourceConfigurations.put(consequenceIdLong, configurationObject);
               }

               PersistentObject.commit(relatedSourceConfigurations);
            }
         }

         PersistentObject.commit(configurationsPerSource);
         Object o = NotificationsManager.getSource(sourceIdLong);
         if (o == null) {
            NotificationsManager.registerSource(sourceIdLong, "ORPHANED_SOURCE", 2);
            Profiles.getInstance().hideSource(sourceIdLong);
         }
      }
   }

   final void checkForOrphanedSources(Profiles ps) {
      synchronized (this._configurations) {
         LongEnumeration keys = this._configurations.keys();

         while (keys.hasMoreElements()) {
            long sourceID = keys.nextElement();
            Object o = NotificationsManager.getSource(sourceID);
            if (o == null) {
               NotificationsManager.registerSource(sourceID, "ORPHANED_SOURCE", 2);
               ps.hideSource(sourceID);
            }
         }
      }
   }

   public final void moveSource(long srcSourceID, long destSourceID) {
      synchronized (this._configurations) {
         LongEnumeration keys = this._configurations.keys();

         while (keys.hasMoreElements()) {
            long sourceID = keys.nextElement();
            if (srcSourceID == sourceID) {
               Object o = this._configurations.get(srcSourceID);
               this._configurations.remove(srcSourceID);
               this._configurations.put(destSourceID, o);
            }
         }
      }
   }

   public final LongHashtable getConfigurations() {
      return this._configurations;
   }

   public final String getName() {
      int profileNameId;
      switch (this._identifier) {
         case -1:
            return this._name;
         case 0:
         default:
            profileNameId = 221;
            break;
         case 1:
            profileNameId = 222;
            break;
         case 2:
            profileNameId = 223;
            break;
         case 3:
            profileNameId = 224;
            break;
         case 4:
            profileNameId = 239;
            break;
         case 5:
            profileNameId = 227;
            break;
         case 6:
            profileNameId = 225;
            break;
         case 7:
            profileNameId = 324;
      }

      return ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles").getString(profileNameId);
   }

   final String getIconBaseName() {
      switch (this._identifier) {
         case -2:
         case 3:
         case 4:
         case 6:
            return "default";
         case -1:
            return "Profiles";
         case 0:
         default:
            return "loud";
         case 1:
            return "discreet";
         case 2:
            return "quiet";
         case 5:
            return "phoneonly";
         case 7:
            return "off";
      }
   }

   public final void setName(String nameString) {
      this._name = nameString;
   }

   @Override
   public final boolean grabDataFromField(Field aField, Object contextObject) {
      boolean result = false;
      AutoTextEditField nameField = (AutoTextEditField)aField;
      String name = nameField.getText().trim();
      ContextObject context = ContextObject.castOrCreate(contextObject);
      if (name.length() > 0) {
         Profiles profiles = Profiles.getInstance();
         String oldName = this.getName();
         this.setName(name);
         if (ContextObject.getFlag(context, 6)) {
            result = profiles.add(this, false, true);
         } else {
            result = profiles.commitChanges(this, true);
         }

         if (!result) {
            ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
            Status.show(resources.getString(322));
            this.setName(oldName);
            return result;
         }
      } else {
         ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         Status.show(resources.getString(201));
      }

      return result;
   }

   @Override
   public final boolean validate(Field aField, Object contextObject) {
      return true;
   }

   @Override
   public final int getOrder(Object contextObject) {
      return 0;
   }

   @Override
   public final Field getField(Object contextObject) {
      if (this.isRemovable()) {
         Field field = (Field)(new Object(
            ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles").getString(200),
            this.getName(),
            Integer.MAX_VALUE,
            4503601774854144L
         ));
         field.setCookie(this);
         return field;
      } else {
         return (Field)(new Object(this.getName(), 64));
      }
   }

   Profile(String nameString, byte identifierByte, int uid) {
      this._name = nameString;
      this._identifier = identifierByte;
      this._uid = uid;
      this._configurations = (LongHashtable)(new Object());
   }

   @Override
   public final String toString() {
      return this.getName();
   }
}
