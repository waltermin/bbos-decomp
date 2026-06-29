package javax.microedition.lcdui;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;

public class Form extends Screen {
   MIDPLayoutManager _container;
   Form$FormChangeListener _formChangeListener;

   public Form(String title) {
      this(title, null);
   }

   public Form(String title, Item[] items) {
      super(new MIDPScreen());
      synchronized (Application.getEventLock()) {
         this.getPeer().setDisplayable(this);
         this._container = new MIDPLayoutManager();
         this.getPeer().add(this._container);
         this._formChangeListener = new Form$FormChangeListener();
         if (items != null) {
            for (int i = 0; i < items.length; i++) {
               Field field = items[i].addToForm(this._formChangeListener);
               this._container.add(field);
               items[i].setOwner(this);
            }
         }

         this._container.setVerticalScroll(0);
         this.setTitle(title);
      }
   }

   public int append(Item item) {
      synchronized (Application.getEventLock()) {
         Field field = item.addToForm(this._formChangeListener);
         this._container.add(field);
         this._container.setVerticalScroll(0);
         item.setOwner(this);
         return this._container.getFieldCount() - 1;
      }
   }

   public int append(String str) {
      synchronized (Application.getEventLock()) {
         str.length();
         return this.append(new StringItem(null, str));
      }
   }

   public int append(Image img) {
      img.isMutable();
      synchronized (Application.getEventLock()) {
         return this.append(new ImageItem(null, img, 0, null));
      }
   }

   public void insert(int itemNum, Item item) {
      synchronized (Application.getEventLock()) {
         Field field = item.addToForm(this._formChangeListener);
         this._container.insert(field, itemNum);
         this._container.setVerticalScroll(0);
         item.setOwner(this);
      }
   }

   public void delete(int itemNum) {
      synchronized (Application.getEventLock()) {
         Field field = this._container.getField(itemNum);
         this._container.delete(field);
         this._container.setVerticalScroll(0);
         ((Item)field.getCookie()).setOwner(null);
      }
   }

   public void deleteAll() {
      synchronized (Application.getEventLock()) {
         int numItems = this.size();
         if (numItems != 0) {
            for (int i = 0; i < numItems; i++) {
               this.delete(0);
            }
         }
      }
   }

   public void set(int itemNum, Item item) {
      synchronized (Application.getEventLock()) {
         if (itemNum == this._container.getFieldCount()) {
            throw new IndexOutOfBoundsException();
         }

         this.insert(itemNum, item);
         this.delete(itemNum + 1);
      }
   }

   public Item get(int itemNum) {
      synchronized (Application.getEventLock()) {
         return (Item)this._container.getField(itemNum).getCookie();
      }
   }

   public void setItemStateListener(ItemStateListener iListener) {
      synchronized (Application.getEventLock()) {
         this._formChangeListener._itemStateListener = iListener;
      }
   }

   ItemStateListener getItemStateListener() {
      synchronized (Application.getEventLock()) {
         return this._formChangeListener._itemStateListener;
      }
   }

   public int size() {
      synchronized (Application.getEventLock()) {
         return this._container.getFieldCount();
      }
   }

   @Override
   public int getWidth() {
      synchronized (Application.getEventLock()) {
         MIDPScreen peer = this.getPeer();
         if (peer.isValidLayout()) {
            return peer.getWidth();
         }

         peer.getGraphics();
         return net.rim.device.api.ui.Graphics.getScreenWidth();
      }
   }

   @Override
   public int getHeight() {
      synchronized (Application.getEventLock()) {
         MIDPScreen peer = this.getPeer();
         if (peer.isValidLayout()) {
            return peer.getHeight();
         }

         peer.getGraphics();
         return net.rim.device.api.ui.Graphics.getScreenHeight();
      }
   }
}
