package net.rim.device.apps.internal.blackberryemail.email.filters;

import java.util.Vector;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModelFactory;

public class EmailFilterModelImplClone extends EmailFilterModelImpl {
   boolean _hasChanged;
   EmailFilterModelImpl _org;
   EmailFilterBodyModelFactory _filterBodyFactory = EmailFilterBodyModelFactory.getInstance();
   TitleModelFactory _titleModelFactory = TitleModelFactory.getInstance();

   public EmailFilterModelImplClone(EmailFilterModelImpl m) {
      this._org = m;
      this._hasChanged = false;
      if (ObjectGroup.isInGroup(m)) {
         m = (EmailFilterModelImpl)ObjectGroup.expandGroup(m);
      }

      super._additionalData = m._additionalData;
      super._userId = m._userId;
      super._enabled = m._enabled;
      super._order = m._order;
      super._uid = m._uid;
      synchronized (m._fields) {
         int size = m._fields.size();
         super._fields = (Vector)(new Object(size));

         for (int i = 0; i < size; i++) {
            Object obj = m._fields.elementAt(i);
            if (this._titleModelFactory.recognize(obj)) {
               super._filterName = (TitleModel)this._titleModelFactory.createInstance(obj);
               super._fields.addElement(super._filterName);
            } else if (this._filterBodyFactory.recognize(obj)) {
               super._fields.addElement(this._filterBodyFactory.createInstance(obj));
            }
         }
      }
   }
}
