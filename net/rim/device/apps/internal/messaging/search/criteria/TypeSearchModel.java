package net.rim.device.apps.internal.messaging.search.criteria;

import java.util.Enumeration;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.MMS;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ConversionProvider;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.model.SyncBuffer;
import net.rim.device.apps.api.framework.registration.DeviceFeature;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.utility.editor.EditorUsingRIMModelFactory;
import net.rim.device.apps.internal.blackberryemail.email.PagingSupport;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;

public final class TypeSearchModel implements PersistableRIMModel, SearchCriterion, FieldProvider, ConversionProvider {
   public int _index = 0;
   private static long[] _possibleTypes = new long[]{
      -6822293833372928884L,
      -7381165762800557185L,
      6099112494809837535L,
      4846413703361859244L,
      532879436795165891L,
      -1033518664251583668L,
      -804651006L,
      1830151402379280383L,
      8370014414044201069L,
      -7176394455708466944L,
      60983603964281088L,
      30406372590514945L,
      8503078132182382081L,
      7741536031146863471L,
      8503077693663953253L,
      8458154160303729519L,
      4284129912720028780L,
      8018770648639078656L,
      7308057372952423028L,
      7741536031140942156L,
      -4200449976296581531L,
      457966387533709512L,
      432456625304240320L,
      30406372594352748L,
      2006725356421128L,
      7813541361589823496L,
      4685995911166820582L,
      -1842975799356328861L,
      576594438529968456L,
      892648847517705025L,
      7330017838163820563L,
      7800524827373824084L,
      8088755202912953204L,
      3315170596277616L,
      7467182369790771464L,
      576588574959349150L,
      7295658126394815553L,
      7251480679412662867L,
      35576495120842867L,
      64858469827691016L,
      8379267070541185800L,
      3137527086500049723L,
      7809911830047671156L,
      -8449805099198558941L,
      8325673857230895959L,
      8436172115966887936L,
      -4881048773110658218L,
      4902169033696235479L
   };
   public static final int TYPE_INVALID = -1;
   public static final int TYPE_ALL = 0;
   public static final int TYPE_EMAIL = 1;
   public static final int TYPE_SMS = 2;
   public static final int TYPE_PHONE = 3;
   public static final int TYPE_DIRECT_CONNECT = 4;
   public static final int TYPE_VOICEMAIL = 5;
   public static final int TYPE_PAGE = 6;
   public static final int TYPE_MMS = 7;
   public static final int TYPE_PIN = 8;
   public static final int TYPE_EMAIL_WITH_ATTACHMENTS = 9;
   static final int NUM_SEARCH_TYPES = 10;
   static String _removedSubjectText;
   static FieldChangeListener _oldChangeListener;
   private static int[] _indexToResourceIdMap = new int[]{
      46,
      47,
      48,
      49,
      57,
      59,
      62,
      63,
      65,
      66,
      -804651007,
      51,
      -804651006,
      51,
      52,
      -804651006,
      53,
      54,
      -804519934,
      -1466262987,
      980431202,
      -1205878232,
      1883249540,
      -804651006,
      60,
      61,
      -804651006,
      64,
      10,
      -805044223,
      102,
      -805044223,
      131,
      0,
      -804519930,
      -1804077940,
      -1588439065,
      -2013305985,
      -1718561576,
      -498503713
   };
   private static int[] _choiceFieldIndexToSearchTypeMap = new int[10];
   private static final long TYPE_SEARCH_FIELD_CHANGE_LISTENER_SINGLETON = 5410879324383661698L;

   public final void setIndex(long objectType) {
      if (objectType == -6822293833372928884L) {
         this._index = 1;
      } else if (objectType == -7381165762800557185L) {
         this._index = 2;
      } else if (objectType == 6099112494809837535L) {
         this._index = 7;
      } else if (objectType == 4846413703361859244L) {
         this._index = 3;
      } else if (objectType == 532879436795165891L) {
         this._index = 4;
      } else if (objectType == -1033518664251583668L) {
         this._index = 5;
      } else {
         this._index = 0;
      }
   }

   public final void setIndex(int index) {
      switch (index) {
         case 11:
         case 15:
         case 17:
         case 21:
         case 22:
         case 23:
         case 24:
            this._index = 0;
            return;
         case 12:
         default:
            this._index = 1;
            return;
         case 13:
            this._index = 2;
            return;
         case 14:
            this._index = 3;
            return;
         case 16:
            this._index = 4;
            return;
         case 18:
            this._index = 5;
            return;
         case 19:
            this._index = 6;
            return;
         case 20:
            this._index = 7;
            return;
         case 25:
            this._index = 8;
         case 26:
            this._index = 9;
      }
   }

   public final Verb getDefaultVerb(Verb[] verbs, Object context) {
      return null;
   }

   public final Verb[] getVerbs(Object context) {
      return null;
   }

   @Override
   public final int getOrder(Object context) {
      return 12500;
   }

   @Override
   public final boolean convert(Object context, Object target) {
      if (ContextObject.getFlag(context, 22) && ContextObject.getFlag(context, 19)) {
         SyncBuffer syncBuffer = (SyncBuffer)target;
         int value = this._index;
         if (value != 0) {
            syncBuffer.addInt(10, value, 4);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      ObjectChoiceField ocf = (ObjectChoiceField)field;
      this._index = mapChoiceFieldSelectedIndexToType(ocf.getSelectedIndex());
      return true;
   }

   @Override
   public final int getType() {
      switch (this._index) {
         case 1:
            return 12;
         case 2:
         default:
            return 13;
         case 3:
            return 14;
         case 4:
            return 16;
         case 5:
            return 18;
         case 6:
            return 19;
         case 7:
            return 20;
         case 8:
            return 25;
         case 9:
            return 26;
      }
   }

   @Override
   public final Object getValue() {
      return this._index > 0 ? this : null;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final Field getField(Object context) {
      Field f = (Field)(new Object(SearchResources.getString(45), this.makeChoices(), SearchResources.getString(_indexToResourceIdMap[this._index])));
      _oldChangeListener = f.getChangeListener();
      switch (this._index) {
         case 2:
         case 3:
         case 4:
         case 6:
         case 7:
         default:
            _removedSubjectText = "";
         case 1:
         case 5:
            ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
            TypeSearchFieldChangeListener listener = (TypeSearchFieldChangeListener)ar.getOrWaitFor(5410879324383661698L);
            if (listener == null) {
               listener = new TypeSearchFieldChangeListener();
               ar.put(5410879324383661698L, listener);
            }

            f.setChangeListener(listener);
            return f;
      }
   }

   static final int mapChoiceFieldSelectedIndexToType(int selectedIndex) {
      return selectedIndex >= 0 && selectedIndex < _choiceFieldIndexToSearchTypeMap.length ? _choiceFieldIndexToSearchTypeMap[selectedIndex] : -1;
   }

   TypeSearchModel() {
   }

   public static final long[] getPossibleTypes() {
      return _possibleTypes;
   }

   private final Object[] makeChoices() {
      Object[] choices = new Object[0];
      boolean smsSupported = this._index == 2;
      boolean mmsSupported = this._index == 7;
      boolean voicemailSupported = this._index == 5;
      boolean phoneSupported = this._index == 3;
      boolean dcSupported = this._index == 4;
      boolean pagingSupported = this._index == 6;
      if (Phone.isSupported()) {
         if (SMSPacketHeader.isSendSupported()) {
            smsSupported = true;
         }

         voicemailSupported = true;
         if (MMS.isEnabled()) {
            mmsSupported = true;
         }

         if (DeviceFeature.isPhoneEnabled()) {
            phoneSupported = true;
            if (DirectConnect.isSupported()) {
               dcSupported = true;
            }
         }
      }

      if (PagingSupport.isPagingEnabled()) {
         pagingSupported = true;
      }

      addChoice(choices, 46, 0, true);
      addChoice(choices, 47, 1, true);
      addChoice(choices, 66, 9, true);
      addChoice(choices, 65, 8, true);
      addChoice(choices, 48, 2, smsSupported);
      addChoice(choices, 63, 7, mmsSupported);
      addChoice(choices, 62, 6, pagingSupported);
      addChoice(choices, 49, 3, phoneSupported);
      addChoice(choices, 57, 4, dcSupported);
      addChoice(choices, 59, 5, voicemailSupported);
      return choices;
   }

   private static final void addChoice(Object[] choices, int resourceId, int typeId, boolean supported) {
      if (supported) {
         int numChoices = choices.length;
         Arrays.add(choices, SearchResources.getString(resourceId));
         _choiceFieldIndexToSearchTypeMap[numChoices] = typeId;
      }
   }

   static final RIMModel getFirstModel(EditorUsingRIMModelFactory editor, Recognizer recognizer) {
      Enumeration e = editor.getFieldsFromEdit();

      while (e.hasMoreElements()) {
         Field f = (Field)e.nextElement();
         Object o = f.getCookie();
         if (recognizer.recognize(o)) {
            return (RIMModel)o;
         }
      }

      return null;
   }

   static final Field getFieldFromModel(EditorUsingRIMModelFactory editor, RIMModel m) {
      Enumeration e = editor.getFieldsFromEdit();

      while (e.hasMoreElements()) {
         Field modelField = (Field)e.nextElement();
         if (modelField.getCookie() == m) {
            return modelField;
         }
      }

      return null;
   }
}
