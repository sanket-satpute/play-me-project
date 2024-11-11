package com.sanket_satpute_20.playme.project.notification;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.NOTIFICATION_CHANNEL_ID;
import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.NOTIFICATION_ID;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sanket_satpute_20.playme.R;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.util.Objects;

public class MusicNotificationManager {

    protected PlayerNotificationManager notificationManager;
    protected MediaControllerCompat mediaController;
    Context context;

    public MusicNotificationManager(Context context, MediaSessionCompat.Token token,
                                    PlayerNotificationManager.NotificationListener listener) {
        this.context = context;
        mediaController = new MediaControllerCompat(context, token);
        notificationManager = new PlayerNotificationManager.Builder(
                context,
                NOTIFICATION_ID,
                NOTIFICATION_CHANNEL_ID)
                .setChannelNameResourceId(R.string.notification_channel_name)
                .setChannelDescriptionResourceId(R.string.notification_channel_description)
                .setMediaDescriptionAdapter(new DescriptionAdapter(mediaController, context))
                .setNotificationListener(listener)
                .build();

    }

    public void showNotification(Player player) {
        notificationManager.setPlayer(player);
    }

    private static class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter{

        MediaControllerCompat mediaController;
        Context context;

        public DescriptionAdapter(MediaControllerCompat mediaControllerCompat, Context context) {
            mediaController = mediaControllerCompat;
            this.context = context;
        }

        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            return Objects.requireNonNull(mediaController.getMetadata().getDescription().getTitle()).toString();
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            return mediaController.getSessionActivity();
        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            return Objects.requireNonNull(mediaController.getMetadata().getDescription().getSubtitle()).toString();
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            Glide.with(context).asBitmap().
                    load(mediaController.getMetadata().getDescription().getIconUri())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            callback.onBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
            return null;
        }
    }
}
