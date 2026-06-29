package net.rim.device.api.crypto.certificate.status;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ldap.LDAPListener;
import net.rim.device.api.ldap.LDAPQuery;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioStatusListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.resource.crypto.CryptoIcons;
import net.rim.device.internal.ui.Image;
import net.rim.device.internal.ui.component.ImageField;
import net.rim.device.internal.ui.component.PopupDialog;

public class QueryStatusDialog extends PopupDialog implements FieldChangeListener, QueryProgressListener, RadioStatusListener, LDAPListener {
   private Object _query;
   private int _state = -1;
   private boolean _inCoverage = true;
   private boolean _isClosed;
   private boolean _allowHide;
   private Application _app;
   private VerticalFieldManager _vfm;
   private LabelField _titleField;
   private ImageField _iconField;
   private RichTextField _textMessage;
   private ButtonField _hideButton;
   private ButtonField _cancelButton;
   private static final int STATE_NONE;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(-7644390350925054654L, "net.rim.device.internal.resource.crypto.StatusProviders");

   public void setTitle(String title) {
      if (title == null || title.length() <= 0) {
         throw new Object();
      }

      if (this._titleField == null) {
         this._titleField = (LabelField)(new Object(title, 36028797018964032L));
         Font boldFont = Font.getDefault();
         boldFont = boldFont.derive(boldFont.getStyle() | 1);
         this._titleField.setFont(boldFont);
         VerticalFieldManager vfm = (VerticalFieldManager)this.getDelegate();
         vfm.add(this._titleField);
         vfm.add((Field)(new Object()));
      } else {
         synchronized (this._app.getAppEventLock()) {
            this._titleField.setText(title);
         }
      }
   }

   public void setQuery(LDAPQuery query) {
      if (query != null && this._query instanceof Object) {
         ((LDAPQuery)this._query).removeListener(this);
         this._query = query;
         query.addListener(this);
         this.setRequestState(0);
      } else {
         throw new Object();
      }
   }

   public void mobilityManagementEvent(int eventCode, int cause) {
   }

   @Override
   public void updateProgress(CertificateStatusQuery query, int progress) {
      this.setRequestState(progress);
   }

   @Override
   public void signalLevel(int level) {
      if (level == -256) {
         this.setRadioCoverage(false);
      } else {
         this.setRadioCoverage(true);
      }
   }

   @Override
   public void networkStarted(int networkId, int service) {
   }

   @Override
   public void baseStationChange() {
   }

   @Override
   public void radioTurnedOff() {
   }

   @Override
   public void pdpStateChange(int apn, int state, int cause) {
   }

   @Override
   public void networkStateChange(int state) {
   }

   @Override
   public void networkScanComplete(boolean success) {
   }

   @Override
   public void fieldChanged(Field field, int context) {
      synchronized (this._app.getAppEventLock()) {
         if (field == this._hideButton) {
            switch (this._state) {
               case 0:
                  break;
               case 1:
               case 2:
               case 3:
               case 4:
               case 5:
                  this.close(2);
                  break;
               case 6:
                  this.close(1);
                  break;
               case 7:
               default:
                  this.close(4);
            }

            this._isClosed = true;
         } else if (field == this._cancelButton && this._state > 1 && this._state < 5) {
            if (!(this._query instanceof Object)) {
               if (this._query instanceof CertificateStatusQuery) {
                  ((CertificateStatusQuery)this._query).terminateQuery();
               }
            } else {
               ((LDAPQuery)this._query).abort();
            }

            this.close(2);
            this._isClosed = true;
         }
      }
   }

   @Override
   public void networkServiceChange(int networkId, int service) {
   }

   @Override
   public void statusUpdate(LDAPQuery query, int status) {
      if (this._query == query) {
         this.setRequestState(status);
      }
   }

   @Override
   public void entryReady(LDAPQuery query, int entry) {
   }

   public QueryStatusDialog(CertificateStatusQuery query, boolean allowHide) {
      this(query, allowHide, 0);
   }

   public QueryStatusDialog(CertificateStatusQuery query, boolean allowHide, long style) {
      super((Manager)(new Object()), style);
      if (query == null) {
         throw new Object();
      }

      this._query = query;
      this._allowHide = allowHide;
      this._app = Application.getApplication();
      VerticalFieldManager vfm = (VerticalFieldManager)this.getDelegate();
      this.setTitle(query.getCertChain()[0].getSubjectFriendlyName());
      this._vfm = (VerticalFieldManager)(new Object(299067162755072L));
      this.setIcon();
      this.addButtons();
      vfm.add(this._vfm);
      this.setRequestState(2);
      if (allowHide) {
         this._hideButton.setFocus();
      } else {
         this._cancelButton.setFocus();
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      boolean handled = super.keyChar(key, status, time);
      if (!handled && key == 27) {
         this.fieldChanged(this._cancelButton, Integer.MIN_VALUE);
      }

      return handled;
   }

   @Override
   public void close(int closeReason) {
      Proxy.getInstance().removeRadioListener(this);
      super.close(closeReason);
   }

   private void addButtons() {
      HorizontalFieldManager buttonManager = (HorizontalFieldManager)(new Object(8589934592L));
      if (this._allowHide) {
         this._hideButton = (ButtonField)(new Object(_rb.getString(59)));
         this._hideButton.setChangeListener(this);
         buttonManager.add(this._hideButton);
      }

      this._cancelButton = (ButtonField)(new Object(_rb.getString(60)));
      this._cancelButton.setChangeListener(this);
      buttonManager.add(this._cancelButton);
      this._vfm.add((Field)(new Object(4)));
      this._vfm.add(buttonManager);
   }

   @Override
   protected void onDisplay() {
      super.onDisplay();
      Proxy.getInstance().addRadioListener(this);
      if (!(this._query instanceof CertificateStatusQuery)) {
         if (this._query instanceof Object) {
            try {
               ((LDAPQuery)this._query).start();
               return;
            } finally {
               this.close(4);
               return;
            }
         }
      } else {
         CertificateStatusQuery statusQuery = (CertificateStatusQuery)this._query;
         statusQuery.setProgressListener(this);
         int errorCode = statusQuery.beginQuery();
         if (errorCode != 0) {
            this.close(4);
            return;
         }
      }
   }

   private void setRequestState(int state) {
      this._app.invokeLater(new QueryStatusDialog$RequestStateRunnable(this, state));
   }

   public QueryStatusDialog(LDAPQuery query, String title) {
      this(query, title, 0);
   }

   private void setRadioCoverage(boolean inCoverage) {
      synchronized (this._app.getAppEventLock()) {
         boolean changeBitmap = false;
         if (this._state >= 2 && this._state <= 4) {
            changeBitmap = true;
         }

         if (changeBitmap) {
            if (inCoverage) {
               this.setIcon();
               this._inCoverage = true;
            } else {
               this._iconField.setImage(CryptoIcons.getImage(18));
               this._inCoverage = false;
            }
         }
      }
   }

   private void setIcon() {
      if (this._inCoverage) {
         Image icon = null;
         switch (this._state) {
            case -2:
            case 7:
            case 8:
            case 9:
            case 10:
               icon = CryptoIcons.getImage(4);
               break;
            case -1:
            default:
               icon = CryptoIcons.getImage(13);
               break;
            case 0:
            case 1:
            case 2:
               icon = CryptoIcons.getImage(15);
               break;
            case 3:
               icon = CryptoIcons.getImage(16);
               break;
            case 4:
            case 5:
            case 6:
            case 11:
               icon = CryptoIcons.getImage(17);
         }

         if (this._iconField == null) {
            HorizontalFieldManager manager = (HorizontalFieldManager)(new Object());
            this._iconField = CryptoIcons.getLargeImageField(icon, 51539607552L);
            manager.add(this._iconField);
            manager.add((Field)(new Object(4)));
            this._textMessage = (RichTextField)(new Object(36028848558571520L));
            manager.add(this._textMessage);
            this._vfm.add(manager);
         } else {
            this._iconField.setImage(icon);
         }
      }
   }

   public QueryStatusDialog(LDAPQuery query, String title, long style) {
      super((Manager)(new Object()), style);
      this.setModal(true);
      if (query == null) {
         throw new Object();
      }

      query.addListener(this);
      VerticalFieldManager vfm = (VerticalFieldManager)this.getDelegate();
      this.setTitle(title);
      this._vfm = (VerticalFieldManager)(new Object(299067162755072L));
      this.setIcon();
      this.addButtons();
      vfm.add(this._vfm);
      this._query = query;
      this._app = Application.getApplication();
      this.setRequestState(0);
   }
}
