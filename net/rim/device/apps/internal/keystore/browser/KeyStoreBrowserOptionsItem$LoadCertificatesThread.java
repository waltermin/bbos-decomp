package net.rim.device.apps.internal.keystore.browser;

import java.util.Enumeration;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.keystore.KeyStoreData;
import net.rim.device.apps.api.ui.ProgressIndicator;
import net.rim.device.apps.api.ui.ProgressRunnable;
import net.rim.vm.Array;

class KeyStoreBrowserOptionsItem$LoadCertificatesThread extends Thread implements ProgressRunnable {
   private ProgressIndicator _progressIndicator;
   private int _reloadType;
   private int _myEpoch;
   private final KeyStoreBrowserOptionsItem this$0;

   public KeyStoreBrowserOptionsItem$LoadCertificatesThread(KeyStoreBrowserOptionsItem _1, int reloadType, int epoch) {
      this.this$0 = _1;
      this._reloadType = reloadType;
      this._myEpoch = epoch;
   }

   @Override
   public void setProgressIndicator(ProgressIndicator progressIndicator) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public void stop() {
   }

   private void loadKeyStore() {
      synchronized (this.this$0._keyStoreItemsSyncObject) {
         if (this._myEpoch == this.this$0._epoch) {
            this.this$0._keyStoreItems.removeAllElements();
            Array.resize(this.this$0._displayedIndices, 0);
            int numEntries = this.this$0._keyStore.size();
            if (numEntries > 0) {
               int certIndex = 0;
               if (this._progressIndicator != null) {
                  this._progressIndicator.initialize(KeyStoreBrowserResources.getString(6014), null, 0, numEntries, 0);
               }

               Enumeration enumeration = this.this$0._keyStore.elements();

               while (enumeration.hasMoreElements()) {
                  if (this._myEpoch != this.this$0._epoch) {
                     if (this._progressIndicator != null) {
                        this._progressIndicator.dismiss();
                     }

                     return;
                  }

                  if (this._progressIndicator != null) {
                     if (++certIndex <= numEntries) {
                        this._progressIndicator.setProgress(certIndex);
                     }
                  }

                  KeyStoreData keyStoreData = (KeyStoreData)enumeration.nextElement();
                  Certificate certificate = keyStoreData.getCertificate();
                  if (this.this$0._browserContext.isCertificateTypeSupported(certificate)) {
                     KeyStoreBrowserData data = new KeyStoreBrowserData(
                        keyStoreData,
                        this.this$0._keyStore,
                        this.this$0._trustedKeyStore,
                        this.this$0._cryptoSystemProperties,
                        this.this$0._certificateStatusManager
                     );
                     this.this$0._keyStoreItems.addElement(data);
                  }
               }

               numEntries = this.this$0._keyStoreItems.size();
               Array.resize(this.this$0._displayedIndices, numEntries);
               if (this._progressIndicator != null) {
                  this._progressIndicator.setStatusString(KeyStoreBrowserResources.getString(6027), false);
               }

               if (this._myEpoch != this.this$0._epoch) {
                  if (this._progressIndicator != null) {
                     this._progressIndicator.dismiss();
                  }
               } else {
                  this.this$0.sort(this.this$0._keyStoreItems);
                  if (this._progressIndicator != null) {
                     this._progressIndicator.dismiss();
                  }
               }
            }
         }
      }
   }

   private void loadCertificatesOnScreen() {
      int size;
      synchronized (this.this$0._keyStoreItemsSyncObject) {
         if (this._myEpoch != this.this$0._epoch) {
            return;
         }

         size = this.this$0._displayedIndices.length;
         if (size <= 0) {
            return;
         }

         this.this$0._titleField.setNumberOfCerts(size);
      }

      for (int i = 0; i < size; i++) {
         int selectedIndex;
         KeyStoreBrowserData selectedData;
         synchronized (this.this$0._keyStoreItemsSyncObject) {
            if (this._myEpoch != this.this$0._epoch) {
               return;
            }

            selectedIndex = this.this$0._listField.getSelectedIndex();
            selectedData = null;
            if (selectedIndex >= 0 && selectedIndex < this.this$0._keyStoreItems.size()) {
               int j = selectedIndex;

               while (j < size && this.this$0._displayedIndices[j] != selectedIndex) {
                  j++;
               }

               if (j < size) {
                  selectedData = (KeyStoreBrowserData)this.this$0._keyStoreItems.elementAt(j);
               }
            }
         }

         boolean dataLoaded = false;
         if (selectedData != null) {
            dataLoaded = selectedData.loadDataIfNeeded();
         }

         int displayedIndex;
         KeyStoreBrowserData dataToLoad;
         synchronized (this.this$0._keyStoreItemsSyncObject) {
            if (this._myEpoch != this.this$0._epoch) {
               return;
            }

            if (dataLoaded) {
               this.this$0._listField.invalidate(selectedIndex);
            }

            displayedIndex = this.this$0._displayedIndices[i];
            if (displayedIndex == -1) {
               continue;
            }

            dataToLoad = (KeyStoreBrowserData)this.this$0._keyStoreItems.elementAt(i);
         }

         dataLoaded = dataToLoad.loadDataIfNeeded();
         synchronized (this.this$0._keyStoreItemsSyncObject) {
            if (this._myEpoch != this.this$0._epoch) {
               return;
            }

            if (dataLoaded) {
               this.this$0._listField.invalidate(displayedIndex);
            }

            this.this$0._titleField.incrementProgress();
         }
      }
   }

   private void invalidateLoadedCertificates() {
      synchronized (this.this$0._keyStoreItemsSyncObject) {
         if (this._myEpoch == this.this$0._epoch) {
            int numDisplayedItems = this.this$0._keyStoreItems.size();

            for (int i = 0; i < numDisplayedItems; i++) {
               KeyStoreBrowserData data = (KeyStoreBrowserData)this.this$0._keyStoreItems.elementAt(i);
               data.invalidate();
            }

            this.this$0._listField.invalidate();
         }
      }
   }

   @Override
   public void run() {
      if (this._reloadType == 2) {
         this.invalidateLoadedCertificates();
      } else if (this._reloadType == 1) {
         this.loadKeyStore();
         if (this._myEpoch == this.this$0._epoch) {
            this.this$0.configureDisplayFilter();
         }

         synchronized (this.this$0._epochSyncObject) {
            this.this$0._epochType = 3;
         }
      } else if (this._reloadType == 3) {
      }

      this.setPriority(1);
      this.loadCertificatesOnScreen();
      synchronized (this.this$0._keyStoreItemsSyncObject) {
         if (this._myEpoch == this.this$0._epoch) {
            synchronized (this.this$0._epochSyncObject) {
               this.this$0._epochType = -1;
            }

            this.this$0.setTitleFilterType();
         }
      }
   }
}
