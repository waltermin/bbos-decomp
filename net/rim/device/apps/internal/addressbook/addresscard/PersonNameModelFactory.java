package net.rim.device.apps.internal.addressbook.addresscard;

import java.util.Vector;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.RIMModelFactory;
import net.rim.device.apps.api.framework.verb.Verb;

final class PersonNameModelFactory extends RIMModelFactory {
   private Verb[] _verbs;

   @Override
   public final boolean recognize(Object object) {
      if (object instanceof Object) {
         return true;
      } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(object, 255);
         return syncBuffer != null && syncBuffer.getFieldType(true) == 32;
      } else if (ContextObject.getFlag(object, 11) && ContextObject.getFlag(object, 43) && ContextObject.getFlag(object, 54)) {
         Vector data = (Vector)ContextObject.get(object, 249);
         return data != null && data.size() > 0 && (data.elementAt(0).equals("Name") || data.elementAt(0).equals("Salutation"));
      } else {
         return false;
      }
   }

   @Override
   public final Object createInstance(Object initialData) {
      PersonNameModel model = null;
      if (initialData == null) {
         return new PersonNameModelImpl();
      }

      if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)ContextObject.get(initialData, 255);
         if (syncBuffer == null) {
            return null;
         }

         try {
            int position = syncBuffer.getPosition();
            String firstName = syncBuffer.getString(32, true);
            String lastName = syncBuffer.getString(32, true);
            String salutation = syncBuffer.getString(position, 55, true);
            String firstNameYOMI = syncBuffer.getString(position, 79, true);
            String lastNameYOMI = syncBuffer.getString(position, 80, true);
            if (firstName != null || lastName != null) {
               model = new PersonNameModelImpl(salutation, firstName, lastName);
               model.setFullNameYOMI(firstNameYOMI, lastNameYOMI);
               return model;
            }
         } finally {
            ;
         }
      } else {
         if (ContextObject.getFlag(initialData, 11) && ContextObject.getFlag(initialData, 43) && ContextObject.getFlag(initialData, 54)) {
            Vector data = (Vector)ContextObject.get(initialData, 249);
            String firstName = null;
            String lastName = null;
            String salutation = null;
            boolean lastPersonNameItemFound = false;

            for (int i = 0; i < data.size(); i += 2) {
               boolean remove = false;
               if (data.elementAt(i).equals("Name")) {
                  if (firstName == null) {
                     firstName = (String)data.elementAt(i + 1);
                  } else {
                     lastPersonNameItemFound = true;
                     lastName = (String)data.elementAt(i + 1);
                  }

                  remove = true;
               } else if (data.elementAt(i).equals("Salutation")) {
                  salutation = (String)data.elementAt(i + 1);
                  remove = true;
               }

               if (remove) {
                  data.removeElementAt(i);
                  data.removeElementAt(i);
                  i -= 2;
               }

               if (lastPersonNameItemFound) {
                  break;
               }
            }

            return new PersonNameModelImpl(salutation, firstName, lastName);
         }

         if (!ContextObject.getFlag(initialData, 10)) {
            model = new PersonNameModelImpl(initialData);
         } else {
            String name = (String)ContextObject.get(initialData, -4886909117188079897L);
            if (name == null) {
               RIMModel modelData = (RIMModel)ContextObject.get(initialData, 254);
               if (modelData instanceof Object) {
                  ConversionProvider converter = (ConversionProvider)modelData;
                  String[] names = new Object[2];
                  if (converter.convert(new Object(10), names)) {
                     name = names[1];
                  }
               }
            }

            String firstName = name;
            String lastName = null;
            if (firstName != null) {
               firstName = firstName.trim();
               if (firstName.length() > 0) {
                  boolean flip = false;
                  int index = firstName.indexOf(44);
                  if (index != -1) {
                     flip = true;
                  } else {
                     index = firstName.indexOf(32);
                  }

                  if (index != -1) {
                     if (index != firstName.length() - 1) {
                        lastName = firstName.substring(index + 1).trim();
                        lastName = ((StringBuffer)(new Object())).append(Character.toUpperCase(lastName.charAt(0))).append(lastName.substring(1)).toString();
                     }

                     firstName = ((StringBuffer)(new Object()))
                        .append(Character.toUpperCase(firstName.charAt(0)))
                        .append(firstName.substring(1, index))
                        .toString();
                     if (flip) {
                        String temp = firstName;
                        firstName = lastName;
                        lastName = temp;
                     }
                  }

                  return new PersonNameModelImpl(null, firstName, lastName);
               }
            }
         }
      }

      return model;
   }

   @Override
   public final int getMinimumCount(Object context) {
      return 1;
   }

   @Override
   public final int getMaximumCount(Object context) {
      return 1;
   }

   @Override
   public final Verb[] getVerbs(Object context) {
      if (this._verbs == null) {
         this._verbs = new Object[]{new Object(this, 16864512, 5390928610432442684L, "net.rim.device.apps.internal.resource.AddressBook", 501)};
      }

      return this._verbs;
   }
}
