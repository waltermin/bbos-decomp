package net.rim.device.apps.internal.passwordkeeper;

import net.rim.device.api.collection.List;
import net.rim.device.api.collection.util.KeywordIndexerHelper;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

public final class PasswordKeeperList implements List, ListFieldCallback, KeywordIndexerHelper {
   private PasswordKeeperVector _list;
   private PasswordKeeperComparator _comparator;
   private PersistentObject _persist = RIMPersistentStore.getPersistentObject(5233466584661269113L);
   private static final long LIST = 5233466584661269113L;

   public PasswordKeeperList() {
      if (this._persist.getContents() == null) {
         this._persist.setContents(new PasswordKeeperVector(), 51);
         this._persist.commit();
      }

      this._list = (PasswordKeeperVector)this._persist.getContents();
      this._comparator = new PasswordKeeperComparator();
   }

   public final void sort() {
      this._list.sort();
   }

   @Override
   public final Object getAt(int index) {
      return this._list.elementAt(index);
   }

   @Override
   public final int getAt(int index, int count, Object[] elements, int destIndex) {
      int i = index;

      for (int j = 0; i < index + count; j++) {
         elements[destIndex + j] = this._list.elementAt(i);
         i++;
      }

      return count;
   }

   @Override
   public final int getIndex(Object element) {
      return this._list.indexOf(element);
   }

   @Override
   public final int size() {
      return this._list.size();
   }

   @Override
   public final void add(Object element) {
      if (!ObjectGroup.isInGroup(element)) {
         ObjectGroup.createGroup(element);
      }

      int size = this._list.size();

      for (int i = 0; i < size; i++) {
         PasswordKeeperElement currentElement = (PasswordKeeperElement)this._list.elementAt(i);
         int compare = this._comparator.compare(element, currentElement);
         if (compare < 0) {
            this._list.insertElementAt(element, i);
            this._persist.commit();
            return;
         }
      }

      this._list.addElement(element);
      this._persist.commit();
   }

   @Override
   public final void insertAt(int index, Object element) {
      if (!ObjectGroup.isInGroup(element)) {
         ObjectGroup.createGroup(element);
      }

      this._list.insertElementAt(element, index);
      this._persist.commit();
   }

   @Override
   public final void remove(Object element) {
      this._list.removeElement(element);
      this._persist.commit();
   }

   @Override
   public final void removeAll() {
      this._list.removeAllElements();
      this._persist.commit();
   }

   @Override
   public final void removeAt(int index) {
      this._list.removeElementAt(index);
      this._persist.commit();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField collectionListField = (CollectionListField)listField;
      PasswordKeeperElement element = (PasswordKeeperElement)collectionListField.getElementAt(index);
      if (element == null) {
         graphics.drawText(PasswordKeeper.getString(1000), 0, y, 116, width);
      } else {
         try {
            PasswordKeeperElement passwordElement = element;
            graphics.drawText(passwordElement.getTitle(), 0, y, 118, width);
            return;
         } catch (DecryptionException var9) {
         } catch (PasswordKeeperLockedException var10) {
         }

         graphics.drawText(PasswordKeeper.getString(3006), 0, y, 118, width);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      throw new Object();
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      throw new Object();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      throw new Object();
   }

   @Override
   public final boolean checkForMatch(Object element, String[] words) {
      return !(element instanceof PasswordKeeperElement) ? false : ((PasswordKeeperElement)element).checkForMatch(words);
   }

   @Override
   public final int getKeywords(Object element, String[] keywords) {
      return !(element instanceof PasswordKeeperElement) ? 0 : ((PasswordKeeperElement)element).getKeywords(keywords);
   }
}
