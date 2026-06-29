package net.rim.device.apps.internal.blackberryemail.email.filters;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CollectionListField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.options.MainScreenOptionsListItem;
import net.rim.device.apps.api.utility.framework.VerbToMenu;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.email.emailsetting.EmailSettingCollectionImpl;
import net.rim.device.apps.internal.blackberryemail.email.emailsetting.EmailSettingModelImpl;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;
import net.rim.device.internal.ui.IconCollection;
import net.rim.device.internal.ui.SystemIcon;
import net.rim.vm.PersistentInteger;
import net.rim.vm.WeakReference;

public final class EmailFilterList extends MainScreenOptionsListItem implements ListFieldCallback, FieldChangeListener, CollectionListener {
   private int _id;
   private ObjectChoiceField _service;
   private MainScreen _mainScreen;
   private String _userId;
   private WeakReference _weakReference = (WeakReference)(new Object(this));
   private EmailFilterList$EmailFilterSaveVerb _saveVerb;
   private static final long ID;

   protected final EmailFilterCollectionListField getListField() {
      EmailFilterList$ServiceInfo info = (EmailFilterList$ServiceInfo)this._service.getChoice(this._service.getSelectedIndex());
      return info != null ? info._listField : null;
   }

   protected final void verbInvoked(Verb verb, Object context, Object result) {
      if (verb instanceof EmailFilterNewVerb && result != null) {
         EmailFilterCollectionListField listField = this.getListField();
         if (listField.isEmpty()) {
            listField.setFocus();
         }

         listField.setElementWithFocus(result);
      }
   }

   public final Object run(EmailMessageModel m, int type) {
      this.perform(6099736323056465049L, m);
      if (m != null && m.getServiceRecordForMessage() != null) {
         Object filter = EmailFilterModelFactory.getInstance().createInstance(m, type);
         Verb newVerb = EmailFilterNewVerb.getInstance(this.getListField(), this._userId);
         Object result = newVerb.invoke(filter);
         this.verbInvoked(newVerb, filter, result);
      }

      return null;
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.exitLater();
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.exitLater();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      if (field == this._service) {
         this.populateConfiguration();
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.exitLater();
   }

   @Override
   public final void reset(Collection collection) {
      this.exitLater();
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      CollectionListField clf = (CollectionListField)listField;
      EmailFilterModelImpl m = (EmailFilterModelImpl)clf.getElementAt(index);
      if (m == null) {
         graphics.drawText(EmailResources.getString(183), 0, y, 4, width);
      } else {
         IconCollection icon = SystemIcon.COLLECTION;
         int box = m.getStatus() ? 1 : 0;
         int boxWidth = icon.getWidth(graphics.getFont());
         icon.paint(graphics, 0, y, box);
         width -= boxWidth;
         graphics.drawText(m.getName(), boxWidth + 3, y);
      }
   }

   @Override
   public final Object get(ListField listField, int index) {
      CollectionListField clf = (CollectionListField)listField;
      EmailFilterModelImpl m = (EmailFilterModelImpl)clf.getElementAt(index);
      return m == null ? m : m.getName();
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   protected final Verb getSaveVerb() {
      this._saveVerb.setCloseScreen(true);
      return this._saveVerb;
   }

   @Override
   protected final boolean save() {
      super.save();
      this._saveVerb.setCloseScreen(false);
      this._saveVerb.invoke(null);
      return true;
   }

   private final void exitLater() {
      EmailFilterCollectionListField listField = this.getListField();
      if (listField.isAlerted() && this._mainScreen.isDisplayed()) {
         listField.setAlerted(false);
         this._mainScreen.getApplication().invokeLater(new EmailFilterList$1(this));
      }
   }

   private final void populateConfiguration() {
      int choice = this._service.getSelectedIndex();
      PersistentInteger.set(this._id, choice);
      EmailFilterList$ServiceInfo info = (EmailFilterList$ServiceInfo)this._service.getChoice(choice);
      this._userId = String.valueOf(info.getSR().getUserId());
      info._userId = this._userId;
      this._mainScreen.deleteAll();
      if (this._service.getSize() > 1) {
         this._mainScreen.add(this._service);
      }

      if (info._listField != null) {
         if (info._forward != null) {
            this._mainScreen.add(info._forward);
            this._mainScreen.add((Field)(new Object()));
         }
      } else {
         info._listField = new EmailFilterCollectionListField(
            this._mainScreen, this._userId, this.cloneSortedReadableList(EmailFilter.getCollection(this._userId)), this
         );
         if (this._userId != null) {
            String[] noYesOptions = new Object[]{EmailResources.getString(190), EmailResources.getString(191)};
            EmailSettingModelImpl emailSetting = (EmailSettingModelImpl)EmailSettingCollectionImpl.getInstance(this._userId).getAt(0);
            if (emailSetting != null) {
               int filterDefaultAction = MathUtilities.clamp(0, emailSetting.getFilterDefaultAction(), noYesOptions.length - 1);
               info._forward = (ObjectChoiceField)(new Object(EmailResources.getString(189), noYesOptions, filterDefaultAction));
               info._forward.setCookie(emailSetting);
               this._mainScreen.add(info._forward);
               this._mainScreen.add((Field)(new Object()));
               EmailFilterCollectionImpl.getInstance(this._userId).addCollectionListener(this._weakReference);
            }
         }
      }

      this._mainScreen.add(info._listField);
      info._listField.setCallback(this);
      info._listField.setFocus();
   }

   public static final EmailFilterList getInstance() {
      ApplicationRegistry applicationRegistry = ApplicationRegistry.getApplicationRegistry();
      EmailFilterList instance = (EmailFilterList)applicationRegistry.get(-4706366031686710983L);
      if (instance == null) {
         instance = new EmailFilterList();
         applicationRegistry.put(-4706366031686710983L, instance);
      }

      return instance;
   }

   static final EmailFilterList getInstance(String userId) {
      return new EmailFilterList(userId);
   }

   protected EmailFilterList() {
      super(EmailResources.getResourceBundle(), 160, null);
   }

   protected EmailFilterList(String userId) {
      this();
      this._userId = userId;
   }

   @Override
   protected final void addScreenVerbs(VerbToMenu verbToMenu, int instance) {
      super.addScreenVerbs(verbToMenu, instance);
      if (this._service.getSize() != 0) {
         EmailFilterCollectionListField listField = this.getListField();
         Verb verb = EmailFilterNewVerb.getInstance(listField, this._userId);
         verbToMenu.addVerb(verb);
         Object mm = null;
         if (this._mainScreen.getFieldWithFocus() == listField) {
            verbToMenu.setDefaultVerb(verb);
            mm = listField.getSelectedElement();
         }

         if (mm instanceof EmailFilterModelImplClone) {
            EmailFilterModelImplClone model = (EmailFilterModelImplClone)mm;
            VerbProvider verbProvider = (VerbProvider)mm;
            Verb[] itemVerbs = new Object[5];
            verbProvider.getVerbs(null, itemVerbs);
            verbToMenu.addVerbs(itemVerbs);
            verbToMenu.addVerb(EmailFilterEditVerb.getInstance(model, this._userId, listField));
            verbToMenu.addVerb(EmailFilterDeleteVerb.getInstance(listField, this._userId));
            if (this._mainScreen.getFieldWithFocus() == listField) {
               verbToMenu.addVerb(new EmailFilterMoveVerb(listField));
            }
         }
      }
   }

   private final void forceExit() {
      for (int i = this._service.getSize() - 1; i >= 0; i--) {
         EmailFilterList$ServiceInfo info = (EmailFilterList$ServiceInfo)this._service.getChoice(i);
         if (info != null) {
            EmailFilterCollectionImpl.getInstance(info._userId).removeCollectionListener(this._weakReference);
         }
      }

      Dialog.alert(EmailResources.getString(1005));
      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
   }

   @Override
   protected final void populateMainScreen(MainScreen screen) {
      this._mainScreen = screen;
      this._mainScreen.setTitle(EmailResources.getString(160));
      this._id = PersistentInteger.getId(-4706366031686710983L, 0);
      this._service = (ObjectChoiceField)(new Object(EmailResources.getString(161), null));
      this._saveVerb = new EmailFilterList$EmailFilterSaveVerb(this);
      this.populateService();
      if (this._service.getSize() != 0) {
         if (this._service.getSize() > 1) {
            this._service.setChangeListener(this);
         }

         this.populateConfiguration();
      }
   }

   private final void populateService() {
      ServiceBook serviceBook = ServiceBook.getSB();
      ServiceRecord[] srSYNC = serviceBook.findRecordsByCid("SYNC");
      Object[] choices = new Object[srSYNC.length];
      int index = 0;

      for (int i = 0; i < srSYNC.length; i++) {
         choices[index++] = new EmailFilterList$ServiceInfo(srSYNC[i]);
      }

      int id = PersistentInteger.get(this._id);
      if (id < 0 || id >= choices.length) {
         id = 0;
      }

      if (choices.length > 0) {
         this._service.setChoices(choices);
         this._service.setSelectedIndex(id);
      }
   }

   private final SortedReadableList cloneSortedReadableList(EmailFilterCollectionImpl collection) {
      SortedReadableList list = (SortedReadableList)(new Object(new EmailFilterComparator()));
      int size = collection.size();

      for (int i = 0; i < size; i++) {
         list.elementAdded(null, EmailFilter.clone((EmailFilterModelImpl)collection.getAt(i)));
      }

      return list;
   }
}
