package com.example.songathon

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {


    private val clientId ="02386d77c3a843468c2ecd788a64e4a2"
    private val redirectUri = "https://com.example.songathon/callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    private var isAdminOfSession = false

    val firebase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var userKey: String? = null

    data class User (
            var isAdmin: Boolean
    )

    data class SongState(
        var pause: Boolean = false,
        var skipPressed: Boolean = false,
        var skipPrevious: Boolean = false,
        var setRepeat: Boolean = false
    )

    data class SongInfo(
        var isPaused: Boolean = true,
        var trackName: String? = null,
        var trackArtist: String? = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        // Set the connection parameters
        Log.d("MainActivity", "Line 31")
        super.onStart()
        val user = User(false)
        val newRef = firebase.child("Users").push()
        this.userKey = newRef.key
        newRef.setValue(user)
        firebase.child("Song State").setValue(SongState())

        authenticate.setOnClickListener { view ->
            Snackbar.make(view, "Authenticate Clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            authenticateUser()
        }

        pause.setOnClickListener { view ->
            Snackbar.make(view, "Pausing", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            pause()
        }

        skipNext.setOnClickListener { view ->
            Snackbar.make(view, "Next-ing", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            skippedPressed()
        }
        skipPrevious.setOnClickListener { view ->
            Snackbar.make(view, "Previous-ing", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            skipPrevious()
        }
        repeat.setOnClickListener { view ->
            Snackbar.make(view, "Repeat-ing", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            setRepeat()
        }
    }

    private fun pause() = firebase.setSongState(SongState(pause = true))

    private fun skippedPressed() = firebase.setSongState(SongState(skipPressed = true))

    private fun skipPrevious() = firebase.setSongState(SongState(skipPrevious = true))

    private fun setRepeat() = firebase.setSongState(SongState(setRepeat = true))

    private fun DatabaseReference.setSongState(state: SongState) =
        this.child("Song State").setValue(state)

    private fun DatabaseReference.setSongInfo(state: SongInfo) =
        this.child("Song Info").setValue(state)

    private fun authenticateUser() {
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()
        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                isAdminOfSession = true
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
            var songInfo = SongInfo()
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                songInfo.isPaused = it.isPaused
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
                songInfo.trackName = track.name
                songInfo.trackArtist = track.artist.name
                firebase.setSongInfo(songInfo)
            }
        }

    }

    fun getUsers() = firebase.child("Users").limitToFirst(10)

    override fun onPause() {
        super.onPause()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
        userKey?.let {
            firebase.child("Users").child(it).removeValue()
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
        userKey?.let {
            firebase.child("Users").child(it).removeValue()
        }
    }
}
