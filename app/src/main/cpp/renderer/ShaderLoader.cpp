#include "ShaderLoader.h"
#include "utils/AssetManagerHolder.h"

#include <android/asset_manager.h>

std::string ShaderLoader::load(const char *path) {
    AAsset *asset = AAssetManager_open(
            AssetManagerHolder::manager(),
            path,
            AASSET_MODE_BUFFER
    );

    if (asset == nullptr) {
        return "";
    }

    size_t length = AAsset_getLength(asset);

    std::string result;
    result.resize(length);

    AAsset_read(
            asset,
            result.data(),
            length
    );

    AAsset_close(asset);

    return result;
}
