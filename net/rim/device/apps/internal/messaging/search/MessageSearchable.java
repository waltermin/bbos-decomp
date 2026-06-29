package net.rim.device.apps.internal.messaging.search;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.MMS;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.framework.registration.DeviceFeature;
import net.rim.device.apps.api.messaging.FolderMerge;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.search.SearchCriterion;
import net.rim.device.apps.api.search.SearchResultCollection;
import net.rim.device.apps.api.search.Searchable;
import net.rim.device.apps.api.search.criteria.OrSearchCriterion;
import net.rim.device.apps.internal.messaging.search.criteria.TypeSearchModel;
import net.rim.device.apps.internal.messaging.search.criteria.TypeSearchModelFactory;
import net.rim.device.apps.internal.messaging.search.resources.SearchResources;
import net.rim.vm.Array;

public class MessageSearchable implements Searchable {
   private MessageSearchableSubItem[] _messageSearchableSubItems;
   private MessageSearchResultCollection _results;
   static final long ID;
   static final long EMAIL;
   static final long PIN;
   static final long SMS;
   static final long PHONE;
   static final long VOICEMAIL;
   static final long MMS;
   static final long DIRECT_CONNECT;
   private static final long APPREG_ID;
   private static MessageSearchable _instance;

   public synchronized void addSubItem(MessageSearchableSubItem subItem) {
      if (this._messageSearchableSubItems == null) {
         this._messageSearchableSubItems = new MessageSearchableSubItem[]{subItem};
      } else {
         Arrays.add(this._messageSearchableSubItems, subItem);
      }
   }

   @Override
   public String getName(long id) {
      int resourceId = 0;
      if (id == 4210919951396674101L) {
         resourceId = 47;
      } else if (id == 8088495187596132904L) {
         resourceId = 65;
      } else if (id == 7776309681094264201L) {
         resourceId = 48;
      } else if (id == 5783489986899586136L) {
         resourceId = 49;
      } else if (id == -2814112899726160614L) {
         resourceId = 59;
      } else if (id == 8374061216902157526L) {
         resourceId = 63;
      } else if (id == -6816112214670730480L) {
         resourceId = 55;
      } else {
         int numMessageSearchableSubItems = this._messageSearchableSubItems == null ? 0 : this._messageSearchableSubItems.length;

         for (int i = 0; i < numMessageSearchableSubItems; i++) {
            String name = this._messageSearchableSubItems[i].getName(id);
            if (name != null) {
               return name;
            }
         }
      }

      if (resourceId != 0) {
         return SearchResources.getString(resourceId);
      }

      ResourceBundle messageResources = ResourceBundle.getBundle(1758158344049992104L, "net.rim.device.apps.internal.resource.Message");
      return ApplicationEntryPoint.removeHotkeyFromDescription(messageResources.getString(0));
   }

   @Override
   public int getPriority(long id, Object context) {
      return 0;
   }

   @Override
   public EncodedImage getIcon(long id) {
      EncodedImage tempImage = ThemeManager.getActiveTheme().getImage("net_rim_bb_messaging_app.Messages", true);
      return tempImage != null ? EncodedImage.createEncodedImage(tempImage.getData(), 0, -1) : null;
   }

   @Override
   public boolean isInitiallyEnabled(long id) {
      return true;
   }

   @Override
   public SearchResultCollection search(long[] ids, Object searchCriteria) {
      SearchCriterion[] originalSearchCriteria = (Object[])searchCriteria;
      SearchCriterion[] finalSearchCriteria;
      if (ids != null) {
         int numOriginalCriteria = originalSearchCriteria.length;
         finalSearchCriteria = new Object[numOriginalCriteria];
         System.arraycopy(originalSearchCriteria, 0, finalSearchCriteria, 0, numOriginalCriteria);
         this.processIds(ids, finalSearchCriteria);
      } else {
         finalSearchCriteria = originalSearchCriteria;
      }

      this._results = new MessageSearchResultCollection(finalSearchCriteria);
      this._results.loadFrom(FolderMerge.getMergeCollection(7509894771240321003L));
      return this._results;
   }

   @Override
   public long[] getSearchableIds(boolean advancedMode) {
      long[] ids = null;
      if (advancedMode) {
         ids = new long[]{
            4210919951396674101L,
            8088495187596132904L,
            261188354050L,
            -3455949755463499715L,
            42949673024L,
            441576587265L,
            566130638849L,
            -3455386788330209280L,
            -6822293833372928884L,
            -7381165762800557185L,
            6099112494809837535L,
            4846413703361859244L,
            532879436795165891L,
            -1033518664251583668L,
            -804651006L,
            1830151402379280383L
         };
         if (Phone.isSupported()) {
            if (SMSPacketHeader.isSendSupported()) {
               Arrays.add(ids, 7776309681094264201L);
            }

            if (net.rim.device.api.system.MMS.isEnabled()) {
               Arrays.add(ids, 8374061216902157526L);
            }

            if (DeviceFeature.isPhoneEnabled()) {
               Arrays.add(ids, 5783489986899586136L);
               if (DirectConnect.isSupported()) {
                  Arrays.add(ids, -6816112214670730480L);
               }
            }

            Arrays.add(ids, -2814112899726160614L);
         }
      }

      int numMessageSearchableSubItems = this._messageSearchableSubItems == null ? 0 : this._messageSearchableSubItems.length;

      for (int i = 0; i < numMessageSearchableSubItems; i++) {
         long[] subItemIds = this._messageSearchableSubItems[i].getSubItemIds(advancedMode);
         if (subItemIds != null) {
            if (ids == null) {
               ids = subItemIds;
            } else {
               int numIds = ids.length;
               int numSubItemIds = subItemIds.length;
               Array.resize(ids, numIds + numSubItemIds);
               System.arraycopy(subItemIds, 0, ids, numIds, numSubItemIds);
            }
         }
      }

      return ids;
   }

   public static MessageSearchable getInstance() {
      if (_instance == null) {
         ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (MessageSearchable)applicationRegistry.getOrWaitFor(-2407626174746969601L);
         if (_instance == null) {
            _instance = new MessageSearchable();
            applicationRegistry.put(-2407626174746969601L, _instance);
         }
      }

      return _instance;
   }

   private void processIds(long[] ids, SearchCriterion[] searchCriteria) {
      TypeSearchModelFactory tsmf = TypeSearchModelFactory.getInstance();
      SearchCriterion[] typeCriteriaArray = new Object[0];

      for (int i = ids.length - 1; i >= 0; i--) {
         long id = ids[i];
         if (id == 7776309681094264201L) {
            this.addTypeCriterion(tsmf, typeCriteriaArray, 13);
         } else if (id == -2814112899726160614L) {
            this.addTypeCriterion(tsmf, typeCriteriaArray, 18);
         } else if (id == 8374061216902157526L) {
            this.addTypeCriterion(tsmf, typeCriteriaArray, 20);
         } else if (id == 5783489986899586136L) {
            this.addTypeCriterion(tsmf, typeCriteriaArray, 14);
         } else if (id == -6816112214670730480L) {
            this.addTypeCriterion(tsmf, typeCriteriaArray, 16);
         } else if (id == 8088495187596132904L) {
            this.addTypeCriterion(tsmf, typeCriteriaArray, 25);
         } else if (id == 4210919951396674101L) {
            this.addTypeCriterion(tsmf, typeCriteriaArray, 12);
         } else {
            int numMessageSearchableSubItems = this._messageSearchableSubItems == null ? 0 : this._messageSearchableSubItems.length;

            for (int j = 0; j < numMessageSearchableSubItems; j++) {
               this._messageSearchableSubItems[j].modifySearchCriteria(id, searchCriteria);
            }
         }
      }

      if (typeCriteriaArray.length > 0) {
         OrSearchCriterion typeCriteria = (OrSearchCriterion)(new Object());
         typeCriteria.setValue(typeCriteriaArray);
         int numExistingCriteria = searchCriteria.length;
         Array.resize(searchCriteria, numExistingCriteria + 1);
         System.arraycopy(searchCriteria, 0, searchCriteria, 1, numExistingCriteria);
         searchCriteria[0] = typeCriteria;
      }
   }

   private void addTypeCriterion(TypeSearchModelFactory tsmf, SearchCriterion[] typeCriteriaArray, int typeIndex) {
      TypeSearchModel tsm = (TypeSearchModel)tsmf.createInstance(null);
      tsm.setIndex(typeIndex);
      Arrays.add(typeCriteriaArray, tsm);
   }

   private MessageSearchable() {
   }
}
