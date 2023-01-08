package com.root.gravityModels;

import com.root.abstractEntities.IModel;

abstract public class GravityModel extends IModel {
    GravityModel(){
        mIsReplayNeeded = true;
        mIsGridNeeded = false;
    }
}
