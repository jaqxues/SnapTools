package com.ljmu.andre.snaptools.ModulePack.Utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TrackMetaData {
    private static final String UNKNOWN_FIELD_TEXT = "Unknown";
    private final String id;
    private final String artist;
    private final String album;
    private final String title;
    @Nullable
    private final String artUrl;
    private final boolean playing;
    private final boolean isFromSpotify;
    private Bitmap artwork;

    TrackMetaData(String id, String artist, String album, String title, @Nullable String artUrl, boolean playing,
                  boolean isFromSpotify) {
        this.id = id;
        this.artist = artist;
        this.album = album;
        this.title = title;
        this.artUrl = artUrl;
        this.playing = playing;
        this.isFromSpotify = isFromSpotify;

        Timber.d("Built new Track: " + toString());
    }

    public boolean isPlaying() {
        return playing;
    }

    public boolean isFromSpotify() {
        return isFromSpotify;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getArtist(), getAlbum(), getTitle(), getArtUrl());
    }

    @Nullable
    public String getId() {
        return id;
    }

    public String getArtist() {
        return artist == null ? UNKNOWN_FIELD_TEXT : artist;
    }

    public String getAlbum() {
        return album == null ? UNKNOWN_FIELD_TEXT : album;
    }

    public String getTitle() {
        return title == null ? UNKNOWN_FIELD_TEXT : title;
    }

    @Nullable
    public String getArtUrl() {
        return artUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrackMetaData)) return false;
        TrackMetaData that = (TrackMetaData) o;
        return Objects.equal(getId(), that.getId()) &&
                Objects.equal(getArtist(), that.getArtist()) &&
                Objects.equal(getAlbum(), that.getAlbum()) &&
                Objects.equal(getTitle(), that.getTitle()) &&
                Objects.equal(getArtUrl(), that.getArtUrl());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("artist", artist)
                .add("album", album)
                .add("title", title)
                .add("playing", playing)
                .add("artUrl", artUrl)
                .toString();
    }

    @Nullable
    public Bitmap getArtwork() {
        return artwork;
    }

    public TrackMetaData setArtwork(Bitmap artwork) {
        if (this.artwork != null)
            this.artwork.recycle();

        this.artwork = artwork;
        return this;
    }

    public static class Builder {
        private static final String SPOTIFY_TRACKS = "https://embed.spotify.com/oembed/?url=spotify:track:";
        private static final String ITUNES_SEARCH = "https://itunes.apple.com/search?limit=1&media=music&term=%s";

        private String id;
        private String artist;
        private String album;
        private String title;
        private boolean playing;
        private boolean fromSpotify;

        @Nullable
        public TrackMetaData build() throws NullPointerException {
            String url;

            if (isFromSpotify() && getId() != null) {
                Preconditions.checkNotNull(getId());

                url = SPOTIFY_TRACKS + getId();
            } else if (title != null) {
                url = String.format(ITUNES_SEARCH, formatSearchArgs(getArtist(), getTitle()));
            } else
                throw new NullPointerException();

            return new TrackMetaData(
                    getId(),
                    getArtist(),
                    getAlbum(),
                    getTitle(),
                    url,
                    isPlaying(),
                    isFromSpotify()
            );
        }

        public boolean isFromSpotify() {
            return fromSpotify;
        }

        public Builder setFromSpotify(boolean fromSpotify) {
            this.fromSpotify = fromSpotify;
            return this;
        }

        public String getId() {
            return id;
        }

        private String formatSearchArgs(String... args) {
            StringBuilder searchTerm = new StringBuilder();

            int index = 0;
            for (String arg : args) {
                index++;
                if (arg == null || arg.isEmpty() || arg.equals(" "))
                    continue;

                searchTerm.append(
                        arg.trim().replaceAll(" ", "+")
                );

                if (index < args.length)
                    searchTerm.append("+");
            }

            Timber.d("SearchTerm: " + searchTerm.toString());
            return searchTerm.toString();
        }

        public String getArtist() {
            return artist;
        }

        public Builder setArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public String getAlbum() {
            return album;
        }

        public Builder setAlbum(String album) {
            this.album = album;
            return this;
        }

        public boolean isPlaying() {
            return playing;
        }

        public Builder setPlaying(boolean playing) {
            this.playing = playing;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setId(String id) throws NullPointerException {
            if (id == null || id.isEmpty())
                return this;

            String[] split = id.split(":");

            if (split.length >= 2)
                this.id = split[2];
            else
                this.id = id;

            return this;
        }

        @NonNull
        public static Builder fromIntent(Intent intent) throws NullPointerException {
            Preconditions.checkNotNull(intent);
            Bundle bundle = Preconditions.checkNotNull(intent.getExtras());
            Object id = bundle.get("id");

            return new Builder()
                    .setId(id != null ? String.valueOf(id) : null)
                    .setAlbum(bundle.getString("album"))
                    .setTitle(bundle.getString("track"))
                    .setArtist(bundle.getString("artist"))
                    .setPlaying(bundle.getBoolean("playing"))
                    .setFromSpotify(
                            intent.getAction() != null
                                    && intent.getAction().equals("com.spotify.music.playbackstatechanged")
                    );
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitNullValues()
                    .add("id", id)
                    .add("artist", artist)
                    .add("album", album)
                    .add("title", title)
                    .add("playing", playing)
                    .add("fromSpotify", fromSpotify)
                    .toString();
        }
    }
}
