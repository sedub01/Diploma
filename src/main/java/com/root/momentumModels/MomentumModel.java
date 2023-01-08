package com.root.momentumModels;

import com.root.abstractEntities.IModel;

abstract public class MomentumModel extends IModel {
    MomentumModel(){
        mIsGridNeeded = true;
        mIsGridVisible = false;
        mIsReplayNeeded = true;
    }
}
