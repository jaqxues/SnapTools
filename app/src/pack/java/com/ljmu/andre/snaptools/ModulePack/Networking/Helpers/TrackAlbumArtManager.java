package com.ljmu.andre.snaptools.ModulePack.Networking.Helpers;

import android.graphics.Bitmap;

import com.ljmu.andre.snaptools.ModulePack.Networking.Packets.ItunesTrackDataPacket;
import com.ljmu.andre.snaptools.ModulePack.Networking.Packets.SpotifyTrackDataPacket;
import com.ljmu.andre.snaptools.ModulePack.Utils.TrackMetaData;
import com.ljmu.andre.snaptools.Networking.VolleyHandler;
import com.ljmu.andre.snaptools.Networking.WebRequest;
import com.ljmu.andre.snaptools.Networking.WebRequest.Builder;
import com.ljmu.andre.snaptools.Networking.WebRequest.RequestType;
import com.ljmu.andre.snaptools.Networking.WebRequest.WebResponseListener;
import com.ljmu.andre.snaptools.Networking.WebResponse;
import com.ljmu.andre.snaptools.Networking.WebResponse.ObjectResultListener;

import timber.log.Timber;

/**
 * This class was created by Andre R M (SID: 701439)
 * It and its contents are free to use by all
 */

public class TrackAlbumArtManager {
    private static final String ART_REQUEST_TAG = "art_request";
    private static AlbumArtRequest activeRequest;

    public static void getAlbumArt(TrackMetaData trackMetaData, ObjectResultListener<Bitmap> resultListener) {
        if (activeRequest != null) {
            if (activeRequest.getTrackMetaData() != null
                    && activeRequest.getTrackMetaData().equals(trackMetaData)) {
                Timber.w("Already getting album art for: " + trackMetaData);
                return;
            }

            activeRequest.cancel();
        }

        activeRequest = new AlbumArtRequest(trackMetaData, resultListener);
        activeRequest.start();
    }

    private static void getArtImageUrl(TrackMetaData trackMetaData, ObjectResultListener<String> urlResultListener) {
        new Builder()
                .setUrl(trackMetaData.getArtUrl())
                .setType(RequestType.PACKET)
                .setTag(ART_REQUEST_TAG)
                .setPacketClass(trackMetaData.isFromSpotify() ? SpotifyTrackDataPacket.class : ItunesTrackDataPacket.class)
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) throws Throwable {
                        Timber.d("Response: " + webResponse);
                        Timber.d("Result: " + (webResponse.getResult() == null ? "NULL" : webResponse.getResult()));

                        if (trackMetaData.isFromSpotify()) {
                            SpotifyTrackDataPacket spotifyTrackDataPacket = webResponse.getResult();
                            urlResultListener.success(null, spotifyTrackDataPacket.thumbnailUrl);
                        } else {
                            ItunesTrackDataPacket itunesTrackDataPacket = webResponse.getResult();
                            String artUrl = itunesTrackDataPacket.getArtworkUrl();

                            Timber.d("Itunes art URL: " + artUrl);

                            if (artUrl == null) {
                                urlResultListener.error("Couldn't find Art Url", null, -1);
                                return;
                            }

                            urlResultListener.success(null, artUrl);
                        }
                    }

                    @Override
                    public void error(WebResponse webResponse) {
                        urlResultListener.error(
                                webResponse.getMessage(),
                                webResponse.getException(),
                                webResponse.getResponseCode()
                        );
                    }
                })
                .performRequest();
    }

    private static void getArtFromUrl(String url, AlbumArtRequest artRequest, ObjectResultListener<Bitmap> bitmapResultListener) {
        new WebRequest.Builder()
                .setUrl(url)
                .setType(RequestType.BITMAP)
                .setTag(ART_REQUEST_TAG)
                .setCallback(new WebResponseListener() {
                    @Override
                    public void success(WebResponse webResponse) throws Throwable {
                        if (artRequest.isCancelled)
                            return;

                        if (webResponse.getResult() == null) {
                            bitmapResultListener.error("No Album Art Retrieved", null, -1);
                            return;
                        }

                        if (!(webResponse.getResult() instanceof Bitmap)) {
                            bitmapResultListener.error("Art download returned something other than an image: " + webResponse.getResult(), null, -1);
                            return;
                        }

                        bitmapResultListener.success(null, webResponse.getResult());
                    }

                    @Override
                    public void error(WebResponse webResponse) {
                        if (artRequest.isCancelled)
                            return;

                        bitmapResultListener.error(
                                webResponse.getMessage(),
                                webResponse.getException(),
                                webResponse.getResponseCode()
                        );
                    }
                })
                .performRequest();
    }

    public static class AlbumArtRequest {
        private final TrackMetaData trackMetaData;
        private final ObjectResultListener<Bitmap> artResultListener;
        private boolean isCancelled;

        AlbumArtRequest(TrackMetaData trackMetaData, ObjectResultListener<Bitmap> artResultListener) {
            this.trackMetaData = trackMetaData;
            this.artResultListener = artResultListener;
        }

        public void start() {
            Timber.d("Starting art request for: " + trackMetaData);
            isCancelled = false;

            getArtImageUrl(
                    trackMetaData,
                    new ObjectResultListener<String>() {
                        @Override
                        public void success(String message, String object) {
                            if (isCancelled)
                                return;

                            if (object == null) {
                                error("Null AlbumArtUrl", null, -1);
                                return;
                            }

                            getArtFromUrl(object, AlbumArtRequest.this, artResultListener);
                        }

                        @Override
                        public void error(String message, Throwable t, int errorCode) {
                            if (isCancelled)
                                return;

                            artResultListener.error(message, t, errorCode);
                        }
                    }
            );
        }


        public void cancel() {
            Timber.d("Cancelling art request for: " + trackMetaData);
            isCancelled = true;
            VolleyHandler.getInstance().cancelPendingRequests(ART_REQUEST_TAG);
        }

        TrackMetaData getTrackMetaData() {
            return trackMetaData;
        }
    }
}
