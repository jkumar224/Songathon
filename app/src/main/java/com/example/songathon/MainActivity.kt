package com.example.songathon

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {


    private val clientId ="02386d77c3a843468c2ecd788a64e4a2"
    private val redirectUri = "https://com.example.songathon/callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        // Set the connection parameters
        Log.d("MainActivity", "Line 31")
        super.onStart()

        val progress = 0

        button.setOnClickListener { view ->
            Snackbar.make(view, "Button Clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

        }

        button2.setOnClickListener { view ->
            Snackbar.make(view, "Button 2 Clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        button4.setOnClickListener { view ->
            Snackbar.make(view, "Button 4 Clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        button5.setOnClickListener { view ->
            Snackbar.make(view, "Button 5 Clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // Set the connection parameters
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }


    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }
}
