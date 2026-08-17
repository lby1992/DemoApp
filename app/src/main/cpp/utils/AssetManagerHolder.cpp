#include "AssetManagerHolder.h"

AAssetManager* AssetManagerHolder::assetsManager = nullptr;

void AssetManagerHolder::init(AAssetManager *manager) {
    assetsManager = manager;
}

AAssetManager *AssetManagerHolder::manager() {
    return assetsManager;
}
