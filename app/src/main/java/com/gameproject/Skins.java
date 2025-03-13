package com.gameproject;

import android.media.Image;

import java.util.Map;

public class Skins {
    public Map<String, Image> BirdSkinImages;

    public Map<String, Image> getBirdSkinImages() {
        return BirdSkinImages;
    }

    public Image getSkinImage(String birdSkin) {
        return BirdSkinImages.get(birdSkin);
    }
}
