package net.rim.device.internal.EScreens;

import net.rim.vm.Array;

public final class EScreenModel {
   private EScreenInfo _screenInfo = new EScreenInfo();
   private EScreenItem _item = new EScreenItem();
   private EScreenItemInfo _itemInfo = new EScreenItemInfo();
   private int[] _curItemsFlags = new int[0];
   private int[] _curItemsIds = new int[0];
   private int[] _curItemsIdCookies = new int[0];
   public static final int SID_NULL;
   public static final int SID_CONTENTS;
   public static final int SID_SESSION;
   public static final int SID_EVENTLOG;
   public static final int SID_VM_STATS;
   public static final int SID_GRAPHICS;
   public static final int SID_QUINCY;
   public static final int ACTION_NULL;
   public static final int ACTION_TOGGLE_RADIO;
   public static final int ACTION_POWER_DOWN;
   public static final int ACTION_PING_SELF;
   public static final int ITEM_FLAG_MUTABLE;
   public static final int ITEM_FLAG_TEXT_BOX;
   public static final int ITEM_FLAG_IP_TEXT_BOX;
   public static final int ITEM_FLAG_NUMERIC_TEXT_BOX;
   public static final int ITEM_FLAG_HEX_TEXT_BOX;
   public static final int ITEM_FLAG_HORIZONTAL_LINE;
   public static final int ITEM_FLAG_HAS_DETAILS;
   public static final int ITEM_FLAG_ACTION;
   public static final int ITEM_FLAG_USER_DATA_REQUIRED;
   public static final int ITEM_FLAG_READ_ONLY;
   public static final int ITEM_FLAG_HAS_DATA;
   public static final int ITEM_FLAG_PHONE_TEXT_BOX;
   public static final int ITEM_FLAG_CHOICE;
   public static final int ITEM_FLAG_BOLD;
   public static final int ITEM_FLAG_POP_ON_SUCCESS;
   public static final int SCREEN_FLAG_NO_AUTO_REFRESH;
   public static final int RET_OK;
   public static final int RET_ERROR;
   public static final int RET_BAD_SID;
   public static final int RET_BAD_IID;
   public static final int RET_BAD_ACTIONID;
   public static final int RET_NULL_POINTER;
   public static final int RET_BAD_USER_DATA;
   public static final int ACCESS_LEVEL_NONE;
   public static final int ACCESS_LEVEL_FULL;
   public static final int ACCESS_LEVEL_TEMPORARY;
   public static final int ACCESS_LEVEL_CRIPPLED;

   public EScreenModel(int accessLevel) {
      this._screenInfo.accessLevel = accessLevel;
      this._item.accessLevel = accessLevel;
   }

   public final synchronized void setScreen(int id, int idCookie) {
      this.loadScreenInfo(id, idCookie, false);
   }

   public final synchronized void refresh() {
      this.loadScreenInfo(this._screenInfo.screenId, this._screenInfo.idCookie, true);
   }

   private final synchronized void loadScreenInfo(int screenId, int idCookie, boolean refresh) {
      this._screenInfo.screenId = screenId;
      this._screenInfo.idCookie = idCookie;
      EScreen.getScreenInfo(this._screenInfo, refresh);
      int numItems = this._screenInfo.numItems + this._screenInfo.numUserItems;
      if (numItems != this._curItemsFlags.length) {
         Array.resize(this._curItemsFlags, numItems);
         Array.resize(this._curItemsIds, numItems);
         Array.resize(this._curItemsIdCookies, numItems);
      }

      this._item.screenId = screenId;
      this._item.screenIdCookie = idCookie;
      if (!refresh) {
         this._item.mode = 0;
      }

      for (int i = refresh ? this._screenInfo.numUserItems : 0; i < numItems; i++) {
         this._item.itemId = i;
         EScreen.getItemInfo(this._item, this._itemInfo);
         this._curItemsFlags[i] = this._itemInfo.flags;
         this._curItemsIds[i] = this._itemInfo.id;
         this._curItemsIdCookies[i] = this._itemInfo.idCookie;
      }
   }

   public final int getItemFlag(int index) {
      return index >= 0 && index < this._curItemsFlags.length ? this._curItemsFlags[index] : -1;
   }

   public final int getItemId(int index) {
      return this._curItemsIds[index];
   }

   public final int getItemIdCookie(int index) {
      return this._curItemsIdCookies[index];
   }

   public final int getNumModes() {
      return this._screenInfo.numModes;
   }

   public final int getMode() {
      return this._item.mode;
   }

   public final void setMode(int mode) {
      this._item.mode = mode;
   }

   public final boolean nextMode() {
      int oldMode = this._item.mode;
      if (this._screenInfo.numModes != 0) {
         this._item.mode = (this._item.mode + 1) % this._screenInfo.numModes;
      }

      return this._item.mode != oldMode;
   }

   public final int getData(int id, byte[] buffer) {
      this._item.itemId = id;
      return EScreen.getItemData(this._item, buffer);
   }

   public final String getTitle() {
      return this._screenInfo.title;
   }

   public final int getNumItems() {
      return this._screenInfo.numItems;
   }

   public final int getNumUserItems() {
      return this._screenInfo.numUserItems;
   }

   public final void getMenuInfo(int itemId, EScreenMenuInfo info) {
      this._item.itemId = itemId;
      EScreen.getMenuInfo(this._item, info);
   }

   public final void keyPressed(int id, int last4Keys) {
      this._item.itemId = id;
      EScreen.keyPressed(this._item, last4Keys);
   }

   public final int getScreenId() {
      return this._item.screenId;
   }

   public final int getScreenIdCookie() {
      return this._item.screenIdCookie;
   }

   public final int getScreenFlags() {
      return this._screenInfo.flags;
   }

   public final int getAccessLevel() {
      return this._screenInfo.accessLevel;
   }
}
