package net.rim.device.apps.api.memo;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.model.PersistableRIMModel;
import net.rim.device.apps.internal.commonmodels.body.BodyModel;
import net.rim.device.apps.internal.commonmodels.categories.CategoriesModel;
import net.rim.device.apps.internal.commonmodels.title.TitleModel;

public interface MemoModel extends ReadableList, WritableSet, PersistableRIMModel {
   TitleModel getTitleModel();

   BodyModel getNotesModel();

   CategoriesModel getCategoriesModel();

   int getUID();
}
