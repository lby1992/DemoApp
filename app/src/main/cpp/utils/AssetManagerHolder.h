#pragma once

#include <android/asset_manager.h>

class AssetManagerHolder {
public:
    static void init(AAssetManager *manager);

    static AAssetManager *manager();

private:
    static AAssetManager *assetsManager;
};