package com.adsrole.sync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adsrole.sync.chromecast.CastControllerActivity;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaLoadRequestData;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

public class PrerecordEventsActivity extends AppCompatActivity {
    // creating a variable for exoplayerview.
    SimpleExoPlayerView exoPlayerView;

    // creating a variable for exoplayer
    SimpleExoPlayer exoPlayer;

    // url of video which we are loading.
    String videoURL = "https://syncliv.com/app/prerecord/1.mp4";

    private CastContext castContext;
    private Toolbar toolbar;
    private MenuItem mediaRouteMenuItem;
    private SessionManagerListener<CastSession> sessionManagerListener;
    private CastSession castSession;
    private int playbackState=PlaybackState.STATE_NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prerecord_events);
        setupActionBar();
        setupCastListener();
        castContext=CastContext.getSharedInstance(this);
        //getSupportActionBar().hide();
        exoPlayerView = findViewById(R.id.idExoPlayerVIew);
        try {

            // bandwisthmeter is used for
            // getting default bandwidth
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            // track selector is used to navigate between
            // video using a default seekbar.
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            // we are adding our track selector to exoplayer.
            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            // we are parsing a video url
            // and parsing its video uri.
            Uri videouri = Uri.parse(videoURL);

            // we are creating a variable for datasource factory
            // and setting its user agent as 'exoplayer_view'
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");

            // we are creating a variable for extractor factory
            // and setting it to default extractor factory.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            // we are creating a media source with above variables
            // and passing our event handler as null,
            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);

            // inside our exoplayer view
            // we are setting our player
            exoPlayerView.setPlayer(exoPlayer);

            // we are preparing our exoplayer
            // with media source.
            exoPlayer.prepare(mediaSource);

            // we are setting our exoplayer
            // when it is ready.
            exoPlayer.setPlayWhenReady(true);

        } catch (Exception e) {
            // below line is used for
            // handling our errors.
            Log.e("TAG", "Error : " + e.toString());
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        exoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                PrerecordEventsActivity.this.playbackState=playbackState;
                Log.d(this.getClass().getName(),"PlaybackState: "+playbackState);
                if(playbackState==PlaybackState.STATE_PLAYING)
                {
                    if(castSession!=null && castSession.getRemoteMediaClient()!=null)
                    {
                        castSession.getRemoteMediaClient().seek(exoPlayer.getCurrentPosition());
                    }

                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
    }

    private void setupActionBar() {
    toolbar=findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cast_menu, menu);
        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        castContext.getSessionManager().addSessionManagerListener(
                sessionManagerListener, CastSession.class);
        super.onResume();

    }

    @Override
    protected void onPause() {
        castContext.getSessionManager().removeSessionManagerListener(
                sessionManagerListener, CastSession.class);
        super.onPause();
    }

    private void setupCastListener()
    {
       sessionManagerListener= new SessionManagerListener<CastSession>() {
           @Override
           public void onSessionStarting(CastSession castSession) {

               Log.d(this.getClass().getName(),"onSessionStarting()=>"+castSession.getApplicationStatus());
           }

           @Override
           public void onSessionStarted(CastSession castSession, String s) {

               Log.d(this.getClass().getName(),"onSessionStarted()=>"+castSession.getApplicationStatus());
               onApplicationConnected(castSession);
               Toast.makeText(PrerecordEventsActivity.this,"Session Started",Toast.LENGTH_SHORT).show();

           }

           @Override
           public void onSessionStartFailed(CastSession castSession, int i) {

               Log.d(this.getClass().getName(),"onSessionStartFailed()=>"+i);
               onApplicationDisconnected();
               Toast.makeText(PrerecordEventsActivity.this,"Session Failed",Toast.LENGTH_SHORT).show();

           }

           @Override
           public void onSessionEnding(CastSession castSession) {

           }

           @Override
           public void onSessionEnded(CastSession castSession, int i) {

               Log.d(this.getClass().getName(),"onSessionEnded()=>"+castSession.getApplicationStatus());
               onApplicationDisconnected();
               Toast.makeText(PrerecordEventsActivity.this,"Session End",Toast.LENGTH_SHORT).show();
           }

           @Override
           public void onSessionResuming(CastSession castSession, String s) {

           }

           @Override
           public void onSessionResumed(CastSession castSession, boolean b) {

               onApplicationConnected(castSession);
           }

           @Override
           public void onSessionResumeFailed(CastSession castSession, int i) {

               onApplicationDisconnected();
           }

           @Override
           public void onSessionSuspended(CastSession castSession, int i) {

           }
       };
    }

    private void onApplicationConnected(CastSession castSession) {
        this.castSession = castSession;
        if(playbackState==PlaybackState.STATE_PLAYING || playbackState==PlaybackState.STATE_PAUSED)
        {
            if(exoPlayer.getDuration()>0)
            {
                loadRemoteMedia(exoPlayer.getCurrentPosition(), true);
            }
            exoPlayer.stop();
        }
       // updatePlayButton(mPlaybackState);
        supportInvalidateOptionsMenu();
    }

    private void onApplicationDisconnected() {
      //  updatePlaybackLocation(PlaybackLocation.LOCAL);
        playbackState = PlaybackState.STATE_NONE;
       // mLocation = PlaybackLocation.LOCAL;
        //updatePlayButton(mPlaybackState);
        supportInvalidateOptionsMenu();
    }

    private void loadRemoteMedia(long position, boolean autoPlay) {

        if (castSession == null) {
            return;
        }

        final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }

        remoteMediaClient.registerCallback(new RemoteMediaClient.Callback() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(PrerecordEventsActivity.this,
                        CastControllerActivity.class);
                startActivity(intent);
                remoteMediaClient.unregisterCallback(this);
            }
        });

        remoteMediaClient.load(new MediaLoadRequestData.Builder()
                .setMediaInfo(buildMediaInfo())
                .setAutoplay(autoPlay)
                .setCurrentTime(position).build());
    }


    private MediaInfo buildMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, "Testing casting video");
        movieMetadata.putString(MediaMetadata.KEY_TITLE, "Demo Mp4 Video");
        //movieMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.getImage(0))));
        //movieMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.getImage(1))));

        return new MediaInfo.Builder(videoURL)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
                .setMetadata(movieMetadata)
                .setStreamDuration(exoPlayer.getDuration())
                .build();
    }

}