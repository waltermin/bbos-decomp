package net.rim.device.api.ui.component;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionLock;
import net.rim.device.api.collection.FilterStatusListener;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.util.KeywordFilterList;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.ui.Ui;
import net.rim.tid.awt.Event;
import net.rim.tid.awt.im.InputMethodRequests;
import net.rim.vm.TraceBack;

public class KeywordFilterCollectionListField extends CollectionListField implements FilterStatusListener {
   protected KeywordFilterList _list;
   private int _searchesInProgress;
   private KeywordFilteredListFinder _inputProcessor;

   public void setSize(int count, int index, boolean selectFirstItem) {
      if (selectFirstItem && this.getListSize() == 0) {
         index = 0;
      }

      super.setSize(count, index);
   }

   public void setKeywordFilterList(KeywordFilterList newList) {
      if (newList != this._list) {
         this._list = newList;
         super.setList(newList);
      }
   }

   public KeywordFilterList getKeywordFilterList() {
      return this._list;
   }

   void initiateSearch(String pattern) {
      if (this._list != null) {
         this._searchesInProgress++;
         synchronized (CollectionLock.getGlobalLock()) {
            synchronized (this._list) {
               this._list.setCriteria(pattern, this);
            }
         }
      }
   }

   void setInputProcessor(KeywordFilteredListFinder processor) {
      this._inputProcessor = processor;
   }

   @Override
   public void filterStarted() {
   }

   @Override
   public void filterDone(boolean interrupted) {
      if (this._searchesInProgress > 0) {
         this._searchesInProgress--;
         if (this._searchesInProgress == 0) {
            this.updateList();
         }
      }
   }

   @Override
   public void dispatchEvent(Event rEvent) {
      if (this._inputProcessor == null) {
         super.dispatchEvent(rEvent);
      } else if (rEvent.getID() == 1004) {
         rEvent.setSource(this._inputProcessor);
         this._inputProcessor.dispatchEvent(rEvent);
      } else {
         this._inputProcessor.dispatchEvent(rEvent);
      }
   }

   @Override
   protected void doUpdateList() {
      if (this._searchesInProgress == 0) {
         super.doUpdateList();
      }

      if (Ui.isTTSEnabled()) {
         super.accessibleEventOccurred(6, new Integer(1), new Integer(2), this);
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      return this._inputProcessor != null ? this._inputProcessor.keyChar(key, status, time) : super.keyChar(key, status, time);
   }

   @Override
   protected boolean keyControl(char key, int status, int time) {
      return this._inputProcessor != null ? this._inputProcessor.keyControl(key, status, time) : super.keyControl(key, status, time);
   }

   @Override
   public int processKeyEvent(int event, char key, int keycode, int time) {
      return this._inputProcessor != null ? this._inputProcessor.processKeyEvent(event, key, keycode, time) : super.processKeyEvent(event, key, keycode, time);
   }

   @Override
   public boolean processNavigationEvent(int event, int dx, int dy, int status, int time) {
      return this._inputProcessor != null
         ? this._inputProcessor.processNavigationEvent(event, dx, dy, status, time)
         : super.processNavigationEvent(event, dx, dy, status, time);
   }

   @Override
   public void reset(Collection collection) {
      if (this._searchesInProgress == 0) {
         super.reset(collection);
      }
   }

   @Override
   public InputMethodRequests getInputMethodRequests() {
      return this._inputProcessor == null ? super.getInputMethodRequests() : this._inputProcessor.getInputMethodRequests();
   }

   public KeywordFilterCollectionListField(ReadableList list, ListFieldCallback listCallback, long style) {
      super(list, listCallback, style);
      this._list = (KeywordFilterList)list;
   }

   @Override
   public void setSize(int count, int index) {
      this.setSize(count, index, true);
   }

   public KeywordFilterCollectionListField(ReadableList list, ListFieldCallback listCallback) {
      this(list, listCallback, 0);
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      this._list = (KeywordFilterList)list;
   }
}
