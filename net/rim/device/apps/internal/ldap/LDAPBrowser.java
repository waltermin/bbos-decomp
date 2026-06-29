package net.rim.device.apps.internal.ldap;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.crypto.certificate.Certificate;
import net.rim.device.api.crypto.certificate.CertificateServerInfo;
import net.rim.device.api.crypto.certificate.CertificateServers;
import net.rim.device.api.crypto.certificate.CertificateServersOptionsScreen;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.status.CertificateStatusProvider;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;
import net.rim.device.api.crypto.certificate.status.QueryStatusDialog;
import net.rim.device.api.crypto.keystore.KeyStoreOptions;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ldap.LDAPEntry;
import net.rim.device.api.ldap.LDAPListener;
import net.rim.device.api.ldap.LDAPQuery;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.UiEngine;
import net.rim.device.api.ui.component.AutoTextEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EmailAddressEditField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.SimpleSortingVector;
import net.rim.device.api.util.StringTokenizer;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.api.framework.model.MatchProvider;
import net.rim.device.apps.internal.keystore.browser.LaunchKeyStoreBrowserVerb;
import net.rim.device.internal.ui.component.BackgroundDialog;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;
import net.rim.vm.Array;

public final class LDAPBrowser implements FieldChangeListener, LDAPListener, Comparator, CollectionListener {
   private String _contextString;
   private LDAPBrowserContext _context;
   private LDAPBrowser$LDAPBrowserVerb _searchVerb;
   private LDAPBrowser$LDAPBrowserVerb _certViewVerb;
   private LDAPBrowser$LDAPBrowserVerb _certAddVerb;
   private LDAPBrowser$LDAPBrowserVerb _fetchRootVerb;
   private LDAPBrowser$LDAPBrowserVerb _newServerVerb;
   private LDAPBrowser$LDAPBrowserVerb _viewServerVerb;
   private LDAPBrowser$LDAPBrowserVerb _certStatusVerb;
   private LDAPBrowser$LDAPBrowserVerb _crossCertVerb;
   private LDAPBrowser$LDAPBrowserVerb _optionVerb;
   private LDAPBrowser$LDAPBrowserVerb _closeVerb;
   private LaunchKeyStoreBrowserVerb _launchKeyStoreBrowserVerb;
   private UiApplication _app;
   private UiEngine _UiEngine;
   private ObjectChoiceField _serverChoice;
   private AutoTextEditField _editFirstName;
   private AutoTextEditField _editLastName;
   private EmailAddressEditField _editEmail;
   private LDAPBrowser$LDAPCertificateListField _list;
   private MainScreen _screen;
   private RichTextField _throttledField;
   private SeparatorField _separatorField;
   private CertificateServers _certOptions;
   private CertificateServerInfo[] _servers;
   private String[] _serverList;
   private QueryStatusDialog _dialog;
   private LDAPQuery _query;
   private SimpleSortingVector _containers;
   private int _resultSize;
   private int _state;
   private MatchProvider _mp;
   private boolean _certsAdded;
   private Font _font = Font.getDefault();
   private Font _boldFont = this._font.derive(this._font.getStyle() | 1);
   private static final int FIRST_STAGE = 1;
   private static final int SECOND_STAGE = 2;
   private static ResourceBundleFamily _rb = ResourceBundle.getBundle(-4732399874182263183L, "net.rim.device.apps.internal.ldap.resource.LDAP");

   public LDAPBrowser(String context) {
      this(context, null, null, null, null);
   }

   public LDAPBrowser(String context, MatchProvider mp) {
      this(context, mp, null, null, null);
   }

   public LDAPBrowser(String contextString, MatchProvider mp, String firstName, String lastName, String email) {
      this._contextString = contextString;
      this._context = LDAPBrowserContextFactory.getContext(contextString);
      if (this._context == null) {
         throw new Object();
      }

      this._mp = mp;
      this._UiEngine = Ui.getUiEngine();
      this.resetResults();
      this.addFields(firstName, lastName, email);
   }

   private final void addFields(String firstName, String lastName, String email) {
      this._editFirstName = (AutoTextEditField)(new Object(getString(30), firstName, 1024, 4503601774854144L));
      this._editLastName = (AutoTextEditField)(new Object(getString(31), lastName, 1024, 4503601774854144L));
      this._editEmail = (EmailAddressEditField)(new Object(getString(32), email, 1024));
      this._editFirstName.setDirty(true);
      this._editLastName.setDirty(true);
      this._editEmail.setDirty(true);
      this._list = new LDAPBrowser$LDAPCertificateListField(this, 0, 2);
      this._list.setEmptyString(this._context.getEmptyString(), 4);
      this._certOptions = CertificateServers.getInstance();
      this._certOptions.addCollectionListener(this);
      this.updateServers();
      this._screen = new LDAPBrowser$LDAPBrowserScreen(this);
      this._screen.setTitle((Field)(new Object(this._context.getScreenTitle())));
      this._screen.add(this._serverChoice);
      this._screen.add(this._editFirstName);
      this._screen.add(this._editLastName);
      this._screen.add(this._editEmail);
      this._screen.add((Field)(new Object()));
      this._screen.add(this._list);
   }

   private final void updateServers() {
      CertificateServerInfo initialServer;
      if (this._serverChoice == null) {
         initialServer = this._context.getLastServer();
      } else {
         initialServer = this._servers[this._serverChoice.getSelectedIndex()];
      }

      int size = this._certOptions.getServerSize(1);
      this._servers = new Object[size + 1];
      this._serverList = new Object[size + 1];
      this._serverList[0] = getString(34);

      for (int i = 1; i <= size; i++) {
         this._servers[i] = this._certOptions.elementAt(1, i - 1);
         this._serverList[i] = this._servers[i].getFriendlyName();
      }

      Arrays.sort(this._serverList, 1, this._serverList.length, this._servers, new LDAPBrowser$StringComparator(null));
      int initialIndex = 0;

      for (int i = 1; i <= size; i++) {
         if (this._servers[i] == initialServer) {
            initialIndex = i;
            break;
         }
      }

      if (this._serverChoice == null) {
         this._serverChoice = (ObjectChoiceField)(new Object(
            ((StringBuffer)(new Object())).append(getString(33)).append(": ").toString(), this._serverList, initialIndex
         ));
         this._serverChoice.setChangeListener(this);
      } else {
         this._serverChoice.setChoices(this._serverList);
         this._serverChoice.setSelectedIndex(initialIndex);
      }
   }

   public final void setServerIndex(String indexName) {
      int size = this._serverList.length;

      for (int i = 0; i < size; i++) {
         if (this._serverList[i] == indexName) {
            this._serverChoice.setSelectedIndex(i);
            return;
         }
      }
   }

   public final boolean open(boolean autoFetch) {
      this._editFirstName.setFocus();
      this._app = UiApplication.getUiApplication();
      if (autoFetch && this.isValidSearchCriteria()) {
         this.prepareSearch();
         this._app.invokeLater(new LDAPBrowser$1(this));
      }

      if (this._app.isEventThread()) {
         synchronized (this._app.getAppEventLock()) {
            this._UiEngine.pushModalScreen(this._screen);
         }
      } else {
         synchronized (this._app.getAppEventLock()) {
            this._UiEngine.pushScreen(this._screen);
         }

         synchronized (this) {
            try {
               this.wait();
            } finally {
               return this._certsAdded;
            }
         }
      }

      return this._certsAdded;
   }

   private final void doClose() {
      this._certOptions.removeCollectionListener(this);
      this._UiEngine.popScreen(this._screen);
      synchronized (this) {
         this.notifyAll();
      }
   }

   private final void doSearch() {
      if (this.prepareSearch()) {
         this.invokeSearch();
      }
   }

   private final boolean prepareSearch() {
      if (!this.dirtyFields() && !SimpleChoiceDialog.askYesNoQuestionOnBackground(getString(79))) {
         return false;
      }

      if (this._query != null) {
         this._query.abort();
      }

      this.resetResults();
      if (!this.isValidSearchCriteria()) {
         BackgroundDialog.showMessage(getString(55));
         return false;
      }

      String[] firstNames = this.extractStrings(this._editFirstName.getText());
      String[] lastNames = this.extractStrings(this._editLastName.getText());
      String[] emailAddresses = this.extractStrings(this._editEmail.getText());
      CertificateUtilities.canonicalizeEmailAddresses(emailAddresses);

      try {
         this.setHostandAuthandConnType();

         try {
            this._context.addFirstStageFilter(firstNames, lastNames, emailAddresses, this._query);
            this._context.addFirstStageAttributes(this._query);
            if (this._mp != null) {
               this._context.addSecondStageAttributes(this._query);
            }
         } catch (LDAPBrowserException e) {
            this.cancelQuery();
            return false;
         }

         this._query.setScope(2);
         synchronized (this._app.getAppEventLock()) {
            this._list.setSize(0);
         }

         this._dialog = (QueryStatusDialog)(new Object(this._query, getString(71)));
         return true;
      } finally {
         this.cancelQuery();
         return false;
      }
   }

   private final String[] extractStrings(String fullString) {
      String[] extractedStrings = new Object[0];
      StringTokenizer tokenizer = (StringTokenizer)(new Object(fullString, ';'));

      while (tokenizer.hasMoreTokens()) {
         Arrays.add(extractedStrings, tokenizer.nextToken());
      }

      return extractedStrings;
   }

   private final void invokeSearch() {
      this._dialog.show();
   }

   private final void doCertView() {
      Field focus = this._UiEngine.getActiveScreen().getLeafFieldWithFocus();
      if (focus == this._list) {
         int selectedIndex = this._list.getSelectedIndex();
         if (selectedIndex < 0) {
            return;
         }

         this.fetchCertificate(new int[]{selectedIndex}, true);
         Certificate currentCertificate = ((LDAPBrowserContainer)this._containers.elementAt(selectedIndex)).getCertificate();
         if (currentCertificate == null) {
            Dialog.inform(getString(73));
            return;
         }

         CertificateUtilities.displayCertificateDetails(currentCertificate, this._context.getKeyStore(), true, null);
      }
   }

   private final void doCertAdd() {
      int[] indices = this.getIndices();
      if (indices != null) {
         this.fetchCertificate(indices, true);
         int indicesLength = indices.length;
         LDAPEntry[] entries = new Object[indicesLength];
         Certificate[] certs = new Object[indicesLength];
         int position = 0;

         try {
            for (int i = 0; i < indicesLength; i++) {
               LDAPBrowserContainer container = (LDAPBrowserContainer)this._containers.elementAt(indices[i]);
               Certificate certificate = container.getCertificate();
               if (!this._context.isCertificateInKeyStore(certificate)) {
                  entries[position] = container.getEntry();
                  certs[position++] = certificate;
               }
            }

            Array.resize(entries, position);
            Array.resize(certs, position);
            this._context.addToKeyStore(entries, certs);
            if (this._context.isRootCertificatesLoadNeeded(certs)) {
               Dialog.inform(getString(110));
            }

            this._certsAdded = true;
         } catch (LDAPBrowserException e) {
            Dialog.inform(getString(74));
         }

         synchronized (Application.getEventLock()) {
            this._list.invalidate();
         }
      }
   }

   private final void doCertStatus() {
      int[] indices = this.getIndices();
      if (indices != null) {
         this.fetchCertificate(indices, true);
         int indicesLength = indices.length;

         for (int i = 0; i < indicesLength; i++) {
            LDAPBrowserContainer container = (LDAPBrowserContainer)this._containers.elementAt(indices[i]);
            Certificate certificate = container.getCertificate();
            if (certificate != null) {
               CertificateStatusRequest request = (CertificateStatusRequest)(new Object(
                  new Object[]{certificate}, false, this._context.getKeyStore(), null, certificate
               ));
               CertificateStatusProvider.requestCertificateStatus(request, null, true, false);
            }
         }
      }
   }

   private final void doFetchRoot() {
      this.resetResults();

      try {
         this.setHostandAuthandConnType();

         try {
            this._context.addRootCertFilter(this._query);
            this._context.addRootCertAttributes(this._query);
         } catch (LDAPBrowserException e) {
            this.cancelQuery();
            return;
         }

         this._query.setScope(2);
         synchronized (this._app.getAppEventLock()) {
            this._list.setSize(0);
            this._editFirstName.setText("");
            this._editLastName.setText("");
            this._editEmail.setText("");
         }

         this._dialog = (QueryStatusDialog)(new Object(this._query, getString(76)));
         this._dialog.show();
      } finally {
         this.cancelQuery();
         return;
      }
   }

   private final void doNewServer() {
      CertificateServersOptionsScreen screen = (CertificateServersOptionsScreen)(new Object(null, 1));
      this._app.pushScreen(screen);
   }

   private final void doViewServer() {
      int serverChoice = this._serverChoice.getSelectedIndex();
      if (serverChoice > 0) {
         CertificateServersOptionsScreen screen = (CertificateServersOptionsScreen)(new Object(this._servers[serverChoice], 1));
         this._app.pushScreen(screen);
      }
   }

   private final void doShowOptions() {
      LDAPBrowserOptionScreen optionScreen = new LDAPBrowserOptionScreen(this._context.getScreenTitle(), this._contextString);
      synchronized (this._app.getAppEventLock()) {
         this._UiEngine.pushScreen(optionScreen);
      }
   }

   private final void doCrossCerts() {
      int[] indices = this.getIndices();
      if (indices != null) {
         int indicesLength = indices.length;
         LDAPEntry[] entries = new Object[indicesLength];
         int position = 0;

         try {
            for (int i = 0; i < indicesLength; i++) {
               LDAPBrowserContainer container = (LDAPBrowserContainer)this._containers.elementAt(indices[i]);
               if (this._context.isCrossCertificateAvailable(container.getEntry())) {
                  entries[position++] = container.getEntry();
               }
            }

            Array.resize(entries, position);
            this._context.addCrossCertificatesToKeyStore(entries);
            this._certsAdded = true;
         } catch (LDAPBrowserException e) {
            Dialog.inform(getString(103));
         }
      }
   }

   private final void cancelQuery() {
      this._query.abort();
      Dialog.alert(getString(72));
   }

   private final void resetResults() {
      this._query = (LDAPQuery)(new Object(null, KeyStoreOptions.getCertificateServiceUID(), this));
      this._containers = (SimpleSortingVector)(new Object());
      this._containers.setSortComparator(this);
      this._resultSize = 0;
      this._state = 1;
      if (this._list != null && this._list.getSize() > 0) {
         synchronized (Application.getEventLock()) {
            this._list.setSize(0);
            this._list.invalidate();
         }
      }

      if (this._throttledField != null) {
         this._screen.delete(this._throttledField);
         this._throttledField = null;
      }

      if (this._separatorField != null) {
         this._screen.delete(this._separatorField);
         this._separatorField = null;
      }

      if (this._serverChoice != null) {
         int lastServerIndex = this._serverChoice.getSelectedIndex();
         if (lastServerIndex > 0) {
            this._context.setLastServer(this._servers[lastServerIndex]);
         }
      }
   }

   private final void fetchCertificate(int[] indices, boolean showDialog) {
      if (indices != null && indices.length >= 0) {
         int indicesLength = indices.length;
         LDAPEntry[] entries = new Object[indicesLength];
         int position = 0;

         for (int i = 0; i < indicesLength; i++) {
            LDAPBrowserContainer container = (LDAPBrowserContainer)this._containers.elementAt(indices[i]);
            if (container.getCertificate() == null) {
               entries[position++] = container.getEntry();
            }
         }

         if (position != 0) {
            Array.resize(entries, position);

            try {
               this._state = 2;
               this._query = (LDAPQuery)(new Object(null, KeyStoreOptions.getCertificateServiceUID(), this));
               this.setHostandAuthandConnType();

               try {
                  this._context.addSecondStageFilter(entries, this._query);
                  this._context.addSecondStageAttributes(this._query);
               } catch (LDAPBrowserException e) {
                  this.cancelQuery();
                  return;
               }

               this._query.setScope(2);
               if (showDialog) {
                  this._dialog = (QueryStatusDialog)(new Object(this._query, this._context.getFetchingString()));
                  this._dialog.show();
               } else {
                  this._dialog.setTitle(this._context.getFetchingString());
                  this._dialog.setQuery(this._query);
                  this._query.start();
               }
            } finally {
               this.cancelQuery();
               return;
            }
         }
      }
   }

   private final boolean buildList() {
      this._containers.reSort();
      this._resultSize = this._containers.size();
      this.cleanFields();
      boolean matches = false;
      LDAPEntry firstEntry = null;
      Certificate firstCertificate = null;
      if (this._resultSize > 0) {
         LDAPBrowserContainer firstContainer = (LDAPBrowserContainer)this._containers.elementAt(0);
         if (firstContainer != null) {
            firstEntry = firstContainer.getEntry();
            firstCertificate = firstContainer.getCertificate();
            matches = true;
         }
      }

      int length = this._containers.size();

      for (int i = 0; i < length; i++) {
         LDAPBrowserContainer container = (LDAPBrowserContainer)this._containers.elementAt(i);
         LDAPEntry entry = container.getEntry();
         if (matches && !this.equals(firstEntry, firstCertificate, entry, container.getCertificate())) {
            matches = false;
         }
      }

      this._containers.reSort();
      return matches;
   }

   private final boolean equals(LDAPEntry entry1, Certificate certificate1, LDAPEntry entry2, Certificate certificate2) {
      boolean cnNotAvailable = false;
      boolean mailNotAvailable = false;

      try {
         String friendlyName1 = this._context.getFriendlyName(entry1, certificate1);
         String friendlyName2 = this._context.getFriendlyName(entry2, certificate2);
         if (!friendlyName1.equals(friendlyName2)) {
            return false;
         }
      } catch (LDAPBrowserException e) {
         cnNotAvailable = true;
      }

      try {
         String mail1 = this._context.getEmail(entry1);
         String mail2 = this._context.getEmail(entry2);
         if (!mail1.equals(mail2)) {
            return false;
         }
      } catch (LDAPBrowserException e) {
         mailNotAvailable = true;
      }

      return !cnNotAvailable || !mailNotAvailable;
   }

   private final boolean isMatched(int index) {
      if (this._mp != null) {
         LDAPBrowserContainer container = (LDAPBrowserContainer)this._containers.elementAt(index);
         if (container.getCertificate() != null && this._mp.match(container.getCertificate()) == 1) {
            return true;
         }
      }

      return false;
   }

   private final void showErrorMessage(int errorCode) {
      String output = null;
      switch (errorCode) {
         case -1:
         case 129:
         case 130:
         case 131:
         case 133:
            return;
         case 1:
         case 101:
         case 102:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 113:
         case 114:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 125:
         case 126:
         case 127:
            output = getString(95);
            break;
         case 2:
            output = getString(108);
            break;
         case 4:
            output = getString(107);
            break;
         case 103:
            output = getString(96);
            break;
         case 104:
            output = getString(97);
            break;
         case 105:
            output = getString(98);
            break;
         case 112:
            output = getString(99);
            break;
         case 115:
            output = getString(111);
            break;
         case 124:
            output = getString(100);
            break;
         case 132:
            output = getString(109);
            break;
         case 134:
            output = getString(113);
            break;
         default:
            output = getString(94);
      }

      String tempOutput = output;
      new LDAPBrowser$2(this, tempOutput).start();
      this._editFirstName.setDirty(true);
   }

   private final boolean dirtyFields() {
      return this._editFirstName.isDirty() || this._editLastName.isDirty() || this._editEmail.isDirty() || this._serverChoice.isDirty();
   }

   private final void cleanFields() {
      this._editFirstName.setDirty(false);
      this._editLastName.setDirty(false);
      this._editEmail.setDirty(false);
      this._serverChoice.setDirty(false);
   }

   @Override
   public final void statusUpdate(LDAPQuery query, int status) {
      if (query == this._query) {
         switch (status) {
            case 6:
               this.handleQueryComplete(false);
               return;
            case 7:
            case 10:
               this.showErrorMessage(this._query.getErrorCode());
            case 5:
            case 9:
               return;
            case 8:
            default:
               this.handleQueryTimeoutUpdate();
               return;
            case 11:
               this.handleQueryComplete(true);
         }
      }
   }

   @Override
   public final void entryReady(LDAPQuery query, int index) {
      LDAPEntry entry = query.getEntry(index);
      if (entry != null) {
         if (this._state == 1) {
            try {
               byte[][] certificateIDs = new byte[0][];
               this._context.getCertificateIDs(entry, certificateIDs);

               for (byte[] currentCertificateID : certificateIDs) {
                  LDAPBrowserContainer existingContainer = this.findContainer(currentCertificateID);
                  if (existingContainer == null) {
                     Certificate currentCertificate = this._context.getCertificateFromKeyStore(currentCertificateID);
                     this._containers.addElement(new LDAPBrowserContainer(entry, currentCertificate, currentCertificateID, this._context));
                  }
               }
            } catch (LDAPBrowserException var12) {
            }
         }

         try {
            Certificate[] certificates = new Object[0];
            byte[][] certificateIDs = new byte[0][];
            this._context.getCertificates(entry, certificates, certificateIDs);
            int numCertificates = certificates.length;
            if (numCertificates != certificateIDs.length) {
               return;
            }

            for (int i = 0; i < numCertificates; i++) {
               Certificate currentCertificate = certificates[i];
               byte[] currentCertificateID = certificateIDs[i];
               LDAPBrowserContainer existingContainer = this.findContainer(currentCertificateID);
               if (existingContainer != null) {
                  existingContainer.setCertificate(currentCertificate);
               } else {
                  this._containers.addElement(new LDAPBrowserContainer(entry, currentCertificate, currentCertificateID, this._context));
               }
            }
         } catch (LDAPBrowserException var11) {
         }
      }
   }

   private final LDAPBrowserContainer findContainer(byte[] certificateID) {
      int numContainers = this._containers.size();

      for (int i = 0; i < numContainers; i++) {
         LDAPBrowserContainer currentContainer = (LDAPBrowserContainer)this._containers.elementAt(i);
         if (Arrays.equals(certificateID, currentContainer.getCertificateID())) {
            return currentContainer;
         }
      }

      return null;
   }

   private final void handleQueryTimeoutUpdate() {
      if (this._state == 1) {
         this.buildList();
      } else {
         this._containers.reSort();
      }

      this.invalidateList();
   }

   private final void handleQueryComplete(boolean resultsTruncated) {
      if (this._state == 1) {
         boolean matches = this.buildList();
         if (matches && !this.isCertAvailable(0)) {
            this.fetchCertificate(new int[]{0, -805044213, 775162112, 774909491}, false);
         } else {
            this.invalidateList();
         }

         if (resultsTruncated) {
            this._separatorField = (SeparatorField)(new Object());
            this._throttledField = (RichTextField)(new Object(getString(63), 36028797019226112L));
            synchronized (Application.getEventLock()) {
               this._screen.add(this._separatorField);
               this._screen.add(this._throttledField);
            }
         }
      } else {
         if (this._state != 2) {
            throw new Object();
         }

         this._containers.reSort();
         this.invalidateList();
      }

      if (this._query.getErrorCode() != -1 && this._containers.size() == 0) {
         this.showErrorMessage(this._query.getErrorCode());
      }
   }

   private final boolean isCertAvailable(int index) {
      if (index >= 0 && index <= this._containers.size()) {
         LDAPBrowserContainer container = (LDAPBrowserContainer)this._containers.elementAt(index);
         return container.getCertificate() != null;
      } else {
         return false;
      }
   }

   private final void invalidateList() {
      int index = this._list.getSelectedIndex();
      synchronized (this._app.getAppEventLock()) {
         this._resultSize = this._containers.size();
         this._list.setSize(this._resultSize);
         this._list.invalidate();
         this._list.setSelectedIndex(index);
      }
   }

   private final void setHostandAuthandConnType() {
      int selectedIndex = this._serverChoice.getSelectedIndex();
      if (selectedIndex > 0) {
         CertificateServerInfo selectedServer = this._servers[selectedIndex];
         this._query.setHost(selectedServer.getServer(), selectedServer.getPort(), selectedServer.getBaseQuery());
         this._query.setAuthType(selectedServer.getAuthType());
         this._query.setConnectionType(selectedServer.getConnectionType());
      }
   }

   private final int[] getIndices() {
      Field focus = this._UiEngine.getActiveScreen().getLeafFieldWithFocus();
      return focus != null && focus == this._list && this._list.getSelectedIndex() >= 0 ? this._list.getSelection() : null;
   }

   private final boolean isValidSearchCriteria() {
      return this._editFirstName.getText().length() > 0 || this._editLastName.getText().length() > 0 || this._editEmail.getText().length() > 0;
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._serverChoice) {
         this.resetResults();
      }
   }

   @Override
   public final int compare(Object o1, Object o2) {
      return StringUtilities.compareObjectToStringIgnoreCase(((LDAPBrowserContainer)o1).getLabel(), ((LDAPBrowserContainer)o2).getLabel(), 1701707776);
   }

   @Override
   public final boolean equals(Object obj) {
      return obj == this;
   }

   public static final String getString(int id) {
      return _rb.getString(id);
   }

   public static final String[] getStringArray(int id) {
      return _rb.getStringArray(id);
   }

   public static final ResourceBundleFamily getBundle() {
      return _rb;
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.updateServers();
      CertificateServerInfo server = (CertificateServerInfo)element;
      this.setServerIndex(server.getFriendlyName());
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.updateServers();
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.updateServers();
   }

   @Override
   public final void reset(Collection collection) {
      this.updateServers();
   }
}
