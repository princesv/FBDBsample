package com.prince.fbdbsample2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTrackActivity extends AppCompatActivity {

    TextView textViewArtistName;
    EditText editTextTrackName;
    SeekBar seekBarRating;
    Button addTrack;
    DatabaseReference databaseTracks;
    List<Track> tracks;
    ListView trackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_add_track);
        textViewArtistName=findViewById(R.id.textViewArtistName);
        editTextTrackName=findViewById(R.id.edittextTrackName);
        seekBarRating=findViewById(R.id.seekBarRating);
        addTrack=findViewById(R.id.buttonAddTrack);
        trackView=findViewById(R.id.listViewTrack);
        tracks=new ArrayList<>();
        Intent intent=getIntent();
        String id=intent.getStringExtra(MainActivity.ARTIST_ID);
        String name=intent.getStringExtra(MainActivity.ARTIST_NAME);
        textViewArtistName.setText(name);
        databaseTracks= FirebaseDatabase.getInstance().getReference("tracks").child(id);
        addTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrack();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseTracks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tracks.clear();
                for(DataSnapshot trackSnapshot:dataSnapshot.getChildren()){

                    Track track=trackSnapshot.getValue(Track.class);
                    tracks.add(track);

                }
                TrackList adapter=new TrackList(AddTrackActivity.this,tracks);
                trackView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveTrack() {

        String trackName = editTextTrackName.getText().toString().trim();
        int rating = seekBarRating.getProgress();

        if (!TextUtils.isEmpty(trackName)) {
            String trackId=databaseTracks.push().getKey();
            Track track=new Track(trackId,trackName,rating);
            databaseTracks.child(trackId).setValue(track);
            Toast.makeText(this, "Track saved successfully", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(this, "Track empty", Toast.LENGTH_SHORT).show();
        }



    }
}
