package net.rim.device.apps.internal.blackberryemail.email;

final class HackyMoreData {
   int _bodyContentLength;
   int _bodyContentLengthOnDevice;
   int _bodyTrueContentLength;
   int _morePartID;

   final void populateToMorePartModel(MorePartModel morePartModel) {
      morePartModel.setAvailableLength(this._bodyContentLength);
      morePartModel.setLengthOnDevice(this._bodyContentLengthOnDevice);
      morePartModel.setTrueLength(this._bodyTrueContentLength);
      morePartModel.setMorePartID(this._morePartID);
   }

   final void populateFromMorePartModel(MorePartModel morePartModel) {
      this._bodyContentLength = morePartModel.getAvailableLength();
      this._bodyContentLengthOnDevice = morePartModel.getLengthOnDevice();
      this._bodyTrueContentLength = morePartModel.getTrueLength();
      this._morePartID = morePartModel.getMorePartID();
   }
}
