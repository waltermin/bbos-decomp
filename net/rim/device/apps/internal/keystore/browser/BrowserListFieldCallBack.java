package net.rim.device.apps.internal.keystore.browser;

import java.util.Vector;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.internal.resource.crypto.CryptoIndicatorImages;

final class BrowserListFieldCallBack implements ListFieldCallback {
   private Vector _keyStoreItems;
   private int[] _displayedIndices;
   private boolean _havePrivateKeys;
   private Object _keyStoreItemsSyncObject;

   BrowserListFieldCallBack(Vector keyStoreItems, int[] displayedIndices, Object keyStoreItemsSyncObject) {
      this._keyStoreItems = keyStoreItems;
      this._displayedIndices = displayedIndices;
      this._keyStoreItemsSyncObject = keyStoreItemsSyncObject;
   }

   public final void setHavePrivateKeys(boolean havePrivateKeys) {
      this._havePrivateKeys = havePrivateKeys;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      synchronized (this._keyStoreItemsSyncObject) {
         if (index >= 0 && index < this._keyStoreItems.size()) {
            int size = this._displayedIndices.length;

            int i;
            for (i = index; i < size; i++) {
               if (this._displayedIndices[i] == index) {
                  index = i;
                  break;
               }
            }

            if (index == i) {
               KeyStoreBrowserData data = (KeyStoreBrowserData)this._keyStoreItems.elementAt(index);
               int offset = 0;
               int indicatorWidth = CryptoIndicatorImages.getImageWidth();
               int imageHeight = CryptoIndicatorImages.getImageHeight();
               int listFieldHeight = listField.getRowHeight();
               int indicatorY = y + listFieldHeight / 2 - imageHeight / 2;
               if (this._havePrivateKeys) {
                  if (data.isPrivateKeySet()) {
                     CryptoIndicatorImages.drawIcon(graphics, offset, indicatorY, 4);
                  }

                  offset += indicatorWidth;
               }

               int imageIndex = data.getStatusImageIndex();
               if (imageIndex != -1) {
                  CryptoIndicatorImages.drawIcon(graphics, offset, indicatorY, imageIndex);
               }

               offset += indicatorWidth;
               int listItemHeight = listField.getRowHeight();
               int fontHeight = Font.getDefault().getHeight();
               int labelOffset = (listItemHeight - fontHeight) / 2;
               String label = data.getLabel();
               graphics.drawText(label, 0, label.length(), offset, y + labelOffset, 64, width - offset);
            }
         }
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      synchronized (this._keyStoreItemsSyncObject) {
         return index >= 0 && index < this._keyStoreItems.size() ? ((KeyStoreBrowserData)this._keyStoreItems.elementAt(index)).getKeyStoreData() : null;
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      synchronized (this._keyStoreItemsSyncObject) {
         if (start < 0) {
            return -1;
         }

         int size = this._displayedIndices.length;

         int i;
         for (i = start; i < size; i++) {
            if (this._displayedIndices[i] == start) {
               start = i;
               break;
            }
         }

         if (start != i) {
            return -1;
         }

         for (int var10 = start; var10 < size; var10++) {
            if (this._displayedIndices[var10] != -1) {
               KeyStoreBrowserData data = (KeyStoreBrowserData)this._keyStoreItems.elementAt(var10);
               if (data.match(prefix) == 1) {
                  return this._displayedIndices[var10];
               }
            }
         }

         return -1;
      }
   }
}
