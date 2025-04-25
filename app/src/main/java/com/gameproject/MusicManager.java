package com.gameproject;

import android.content.Context;
import android.media.MediaPlayer;

public final class MusicManager {
    private static MediaPlayer player;
    private static boolean isMuted = false;

    private static void ensurePlayer(Context ctx) {
        if (player == null) {
            player = MediaPlayer.create(ctx.getApplicationContext(), R.raw.music_q);
            player.setLooping(true);
        }
    }
    public static void play(Context ctx) {
        ensurePlayer(ctx);
        if (!isMuted && !player.isPlaying()) {
            player.start();
        }
    }
    public static void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }
    public static void setMuted(boolean muted, Context ctx) {
        isMuted = muted;
        ensurePlayer(ctx);
        if (isMuted) {
            pause();
        } else {
            play(ctx);
        }
    }
}
