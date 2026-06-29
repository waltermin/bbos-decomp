package net.rim.device.api.crypto.certificate;

import java.util.Enumeration;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;

final class CertificateServersOptionsItem extends MainScreenOptionsListItem implements ListFieldCallback, CollectionListener, Comparator {
   private CertificateServers _certificateOptions = CertificateServers.getInstance();
   private Object _eventLock;
   private ListField _ldapListField;
   private ListField _ocspListField;
   private ListField _crlListField;
   private CertificateServersOptionsItem$ServerVerb _addVerb;
   private CertificateServersOptionsItem$ServerVerb _editVerb;
   private CertificateServersOptionsItem$ServerVerb _deleteVerb;
   private static final ResourceBundle _rb = ResourceBundle.getBundle(
      -3843310740975580338L, "net.rim.device.internal.resource.crypto.CertificateServersOptions"
   );

   public final String getDescription(int type) {
      switch (type) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            return _rb.getString(0);
         case 1:
            return CommonResources.getString(3011);
         case 2:
            return CommonResources.getString(1000);
      }
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return -1;
   }

   @Override
   public final Object get(ListField listField, int index) {
      CertificateServerInfo serverInfo = this.getCertificateServerInfo(listField, index);
      return serverInfo != null ? serverInfo.getFriendlyName() : null;
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CertificateServerInfo serverInfo = this.getCertificateServerInfo(listField, index);
      if (serverInfo != null) {
         String label = serverInfo.getFriendlyName();
         graphics.drawText(label, 5, y, 0, width);
      }
   }

   @Override
   public final void reset(Collection collection) {
      this.refreshServers(super._mainScreen);
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.refreshServers(super._mainScreen);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.refreshServers(super._mainScreen);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.refreshServers(super._mainScreen);
   }

   @Override
   public final int compare(Object o1, Object o2) {
      if (o1 instanceof CertificateServerInfo && o2 instanceof CertificateServerInfo) {
         String friendlyName1 = ((CertificateServerInfo)o1).getFriendlyName();
         String friendlyName2 = ((CertificateServerInfo)o2).getFriendlyName();
         return friendlyName1.compareTo(friendlyName2);
      } else {
         throw new ClassCastException();
      }
   }

   private final ListField addServerGroup(int serverGroupId, MainScreen mainScreen, Font boldFont) {
      LabelField labelField = new LabelField(_rb.getString(serverGroupId));
      labelField.setFont(boldFont);
      mainScreen.add(labelField);
      ListField listField = new ListField();
      listField.setCallback(this);
      listField.setEmptyString(_rb.getString(103), 4);
      mainScreen.add(listField);
      return listField;
   }

   private final void refreshServers(MainScreen mainScreen) {
      this.refreshServerGroup(mainScreen, 1, this._ldapListField);
      this.refreshServerGroup(mainScreen, 2, this._ocspListField);
      this.refreshServerGroup(mainScreen, 3, this._crlListField);
      synchronized (this._eventLock) {
         mainScreen.invalidate();
      }
   }

   @Override
   protected final void open() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      char key = Keypad.map(keycode);
      CertificateServerInfo info = this.getSelectedCertificateServerInfo();
      if (info != null) {
         if (key == '\n') {
            if (this._editVerb == null) {
               this._editVerb = new CertificateServersOptionsItem$ServerVerb(this, 1, this.getDescription(1), getOrdering(1));
            }

            this._editVerb.setInfo(info);
            this._editVerb.invoke(null);
            return true;
         }

         if (key == 127 || key == '\b') {
            if (this._deleteVerb == null) {
               this._deleteVerb = new CertificateServersOptionsItem$ServerVerb(this, 2, this.getDescription(2), getOrdering(2));
            }

            this._deleteVerb.setInfo(info);
            this._deleteVerb.invoke(null);
            return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   protected final boolean invokeOptionsAction(int action) {
      switch (action) {
         case 1:
            this.keyDown(Keypad.keycode('\n', 32768), 0);
            return true;
         default:
            return false;
      }
   }

   @Override
   public final boolean confirm(Verb verb, Object context) {
      boolean result = super.confirm(verb, context);
      if (result) {
         this._certificateOptions.removeCollectionListener(this);
      }

      return result;
   }

   private final int getServerTypeOfFieldWithFocus() {
      Field field = super._mainScreen.getFieldWithFocus();
      if (field == this._ocspListField) {
         return 2;
      } else {
         return field == this._crlListField ? 3 : 1;
      }
   }

   public CertificateServersOptionsItem() {
      super(_rb.getString(401), new ContextObject(0, 2), 5294015899860238835L);
      ContextObject.put(super._context, 244, "net_rim_bb_secureemail_help/certificate_servers");
   }

   private static final int getOrdering(int type) {
      switch (type) {
         case -1:
            throw new IllegalArgumentException();
         case 0:
         default:
            return 589904;
         case 1:
            return 611152;
         case 2:
            return 611472;
      }
   }

   @Override
   protected final Verb addCurrentItemVerbs(VerbToMenu verbToMenu, int instance) {
      CertificateServerInfo info = this.getSelectedCertificateServerInfo();
      if (this._addVerb == null) {
         this._addVerb = new CertificateServersOptionsItem$ServerVerb(this, 0, this.getDescription(0), getOrdering(0));
      }

      this._addVerb.setInfo(info);
      verbToMenu.addVerb(this._addVerb);
      if (info == null) {
         return this._addVerb;
      }

      if (this._editVerb == null) {
         this._editVerb = new CertificateServersOptionsItem$ServerVerb(this, 1, this.getDescription(1), getOrdering(1));
      }

      this._editVerb.setInfo(info);
      verbToMenu.addVerb(this._editVerb);
      if (this._deleteVerb == null) {
         this._deleteVerb = new CertificateServersOptionsItem$ServerVerb(this, 2, this.getDescription(2), getOrdering(2));
      }

      this._deleteVerb.setInfo(info);
      verbToMenu.addVerb(this._deleteVerb);
      VerbRepository verbRepository = VerbRepository.getVerbRepository(1363053212162519223L);
      Verb[] verbs = verbRepository.getVerbs(4738722199580714034L);
      if (verbs != null) {
         for (int i = 0; i < verbs.length; i++) {
            Verb var10000 = verbs[i];
            if (verbs[i] instanceof SetParameter) {
               ((SetParameter)var10000).setParameter(info);
            }

            if (verbs[i] instanceof Copyable) {
               verbToMenu.addVerb((Verb)((Copyable)verbs[i]).copy());
            } else {
               verbToMenu.addVerb(verbs[i]);
            }
         }
      }

      return this._editVerb;
   }

   private final CertificateServerInfo getCertificateServerInfo(ListField listField, int index) {
      SimpleSortingVector vector = (SimpleSortingVector)listField.getCookie();
      return (CertificateServerInfo)vector.elementAt(index);
   }

   private final CertificateServerInfo getSelectedCertificateServerInfo() {
      Field field = super._mainScreen.getFieldWithFocus();
      if (field instanceof ListField) {
         ListField list = (ListField)field;
         int index = list.getSelectedIndex();
         if (index != -1) {
            return this.getCertificateServerInfo(list, index);
         }
      }

      return null;
   }

   private final void refreshServerGroup(MainScreen mainScreen, int type, ListField listField) {
      SimpleSortingVector servers = new SimpleSortingVector();
      servers.setSortComparator(this);
      Enumeration enumeration = this._certificateOptions.getServers(type);

      while (enumeration.hasMoreElements()) {
         servers.addElement(enumeration.nextElement());
      }

      servers.reSort();
      listField.setCookie(servers);
      synchronized (this._eventLock) {
         listField.setSize(servers.size());
      }
   }

   @Override
   protected final void populateMainScreen(MainScreen mainScreen) {
      this._certificateOptions.addCollectionListener(this);
      this._eventLock = Application.getEventLock();
      Font font = Font.getDefault();
      Font boldFont = font.derive(font.getStyle() | 1);
      this._ldapListField = this.addServerGroup(102, mainScreen, boldFont);
      mainScreen.add(new SeparatorField());
      this._ocspListField = this.addServerGroup(104, mainScreen, boldFont);
      mainScreen.add(new SeparatorField());
      this._crlListField = this.addServerGroup(105, mainScreen, boldFont);
      this.refreshServers(mainScreen);
   }

   @Override
   public final boolean equals(Object obj) {
      return obj == this;
   }
}
