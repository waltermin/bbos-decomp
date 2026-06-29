package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.collection.util.PrefixKeywordFilterList;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModelOrderHelper;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.apps.api.ui.KeywordFilteredScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.NetworkInfo;
import net.rim.device.internal.system.RadioInternal;

public final class PrefNetworkSelectOption extends KeywordFilteredScreen implements ListFieldCallback {
   private SortedReadableList _listItems;
   private PrefNetworkSelectOption$PrefNetworkInfo[] _netInfos;
   private PrefNetworkList _netList;
   private int _priority;
   private int _type;
   private CollectionListField _listField;
   static final int TYPE_AVAIL_NETWORKS = 1;
   static final int TYPE_KNOWN_NETWORKS = 2;

   protected final boolean selectNetwork() {
      NetworkInfo netInfo = (NetworkInfo)this._listField.getSelectedElement();
      if (netInfo == null) {
         return false;
      }

      if (this._netList.isItemInList(netInfo) != -1) {
         Status.show(OptionsResources.getString(1894), Bitmap.getPredefinedBitmap(2), 2000);
         return false;
      }

      if (NetworkOptionsUtils.is3GSupported()) {
         PrefNetworkSelectOption$RATSelectionPopup popup = new PrefNetworkSelectOption$RATSelectionPopup(netInfo);
         popup.show();
         if (popup.getCloseReason() == -1) {
            return false;
         }

         int clearFlag = 80;
         if (this._netList.isItemInList(netInfo) != -1) {
            netInfo.setCategory(NetworkOptionsUtils.clearFlag(netInfo.getCategory(), clearFlag));
            Status.show(OptionsResources.getString(1894), Bitmap.getPredefinedBitmap(2), 2000);
            return false;
         }
      }

      this._netList.add(this._priority, netInfo);
      return true;
   }

   @Override
   public final Object get(ListField listField, int index) {
      CollectionListField clf = (CollectionListField)listField;
      return (NetworkInfo)clf.getElementAt(index);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return listField.getPreferredWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      NetworkInfo netInfo = (NetworkInfo)this.get(listField, index);
      if (netInfo == null) {
         graphics.drawText(OptionsResources.getString(907), 0, y, 4, width);
      } else {
         String idStr = NetworkOptionsUtils.buildNetIdString(netInfo);
         String name = netInfo.getName();
         if (name == null || name.length() == 0) {
            name = OptionsResources.getString(1885);
         }

         int widthDrawn = graphics.drawText(name, 0, y, 70, width);
         int valueWidth = listField.getFont().getBounds(idStr);
         int drawStyle = 5;
         if (valueWidth > width - widthDrawn) {
            drawStyle = 70;
         }

         graphics.drawText(idStr, widthDrawn, y, drawStyle, width - widthDrawn);
      }
   }

   @Override
   public final boolean onClose() {
      this.close();
      return true;
   }

   private final void refresh() {
      this._listItems.loadFrom(this._netInfos);
      if (this._type != 1) {
         this._listItems.sort();
      }

      this._listField.setSize(this._listItems.size());
      if (!this._listField.isEmpty()) {
         this._listField.setFocus();
      }
   }

   private final void buildList() {
      NetworkInfo[] netInfos;
      if (this._type == 2) {
         netInfos = NetworkOptionsUtils.getPredefinedNetworks();
      } else {
         netInfos = NetworkOptionsUtils.getAvailableNetworks();
         int currentNetworkSelMode = RadioInternal.getNetworkSelectionMode();
         if (currentNetworkSelMode != 3) {
            RadioInternal.setNetworkSelectionModeOS(3);
         }

         if (NetworkOptionsUtils.scanForNetworks() == 1) {
            netInfos = NetworkOptionsUtils.getAvailableNetworks();
         }

         if (currentNetworkSelMode != 3) {
            RadioInternal.setNetworkSelectionModeOS(currentNetworkSelMode);
         }
      }

      this._netInfos = new PrefNetworkSelectOption$PrefNetworkInfo[netInfos.length];

      for (int i = 0; i < netInfos.length; i++) {
         this._netInfos[i] = new PrefNetworkSelectOption$PrefNetworkInfo(netInfos[i].getName(), netInfos[i].getNetworkId());
      }
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         NetworkInfo netInfo = (NetworkInfo)this._listField.getSelectedElement();
         if (netInfo != null) {
            Verb prefNetworkSelectVerb = new PrefNetworkSelectOption$PrefNetworkSelectVerb(this);
            prefNetworkSelectVerb.invoke(null);
            return true;
         }
      } else if (key == 27) {
         String pattern = this.getSearchPattern();
         if (pattern != null && pattern.length() > 0) {
            this.setSearchPattern(null);
            return true;
         }

         Verb exitVerb = ExitVerb.createCloseVerb(0, null);
         exitVerb.invoke(null);
         return true;
      }

      return super.keyChar(key, status, time);
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      NetworkInfo netInfo = (NetworkInfo)this._listField.getSelectedElement();
      if (netInfo != null) {
         Verb defaultVerb = new PrefNetworkSelectOption$PrefNetworkSelectVerb(this);
         menu.add(defaultVerb);
         menu.setDefault(defaultVerb);
      }
   }

   public PrefNetworkSelectOption(int type, PrefNetworkList netList, int priority) {
      super(null, null, null);
      this._type = type;
      this._netList = netList;
      this._priority = priority;
      this._listField = this.getListField();
      this._listItems = new SortedReadableList(new PrefNetworkSelectOption$PrefNetworkListItemComparator());
      if (this._netList == null) {
         this._netList = new PrefNetworkList(null);
      }

      if (this._priority >= 0 && this._netList.getListSize() > 0) {
         this._priority++;
      } else {
         this._priority = 0;
      }

      this.buildList();
      this.refresh();
      KeywordFilterList keywordFilterList = new PrefixKeywordFilterList(
         this._listItems, new RIMModelOrderHelper(new PrefNetworkSelectOption$PrefNetworkListItemComparator(), new ContextObject()), false
      );
      this.setListCallback(this);
      this.setList(keywordFilterList);
      if (!this.getListField().isEmpty()) {
         this.getListField().setFocus();
      }

      this._listField.setSize(this._listItems.size());
   }
}
