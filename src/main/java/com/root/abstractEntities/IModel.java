package com.root.abstractEntities;

import javafx.scene.Parent;

abstract public class IModel {
    protected String mModelName;
    protected String mModelDescription;
    protected String mModuleFilePath;
    protected Parent mRoot;
    protected boolean mIsGridNeeded;
    protected boolean mIsGridVisible;
    protected boolean mIsReplayNeeded;

    public String getModuleFilePath() {
        return mModuleFilePath;
    }

    public boolean hasScene(){
        return mRoot != null;
    }
    public void setScene(Parent root){
        mRoot = root;
    }
    public Parent getScene(){
        return mRoot;
    }
}
