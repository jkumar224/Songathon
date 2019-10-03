package com.example.songathon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

class MainActivity : AppCompatActivity() {


    private val clientId ="02386d77c3a843468c2ecd788a64e4a2";
    private val redirectUri = "https://com.spotify.android.songathon/callback"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {

    }

    private fun connected() {

    }

    override fun onStop() {
        super.onStop()
    }
}
