package com.gameproject;

import android.media.Image;
import android.widget.ImageView;

import java.util.Map;

public class Skins {
    public Map<String, ImageView> BirdSkinImages;

    public Map<String, ImageView> getBirdSkinImages() {
        return BirdSkinImages;
    }

    public ImageView getSkinImage(String birdSkin) {
        return BirdSkinImages.get(birdSkin);
    }
}
