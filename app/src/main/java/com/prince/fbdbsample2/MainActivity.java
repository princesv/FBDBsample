package com.prince.fbdbsample2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ARTIST_NAME="artistname";
    public static final String ARTIST_ID="artistid";
    EditText editTextName;
    Button buttonAdd;
    Spinner spinnerGenere;
    DatabaseReference databaseArtists;
    ListView listViewArtists;
    List<Artist> artistList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseArtists= FirebaseDatabase.getInstance().getReference("artist");
        editTextName=findViewById(R.id.edittextname);
        buttonAdd=findViewById(R.id.button);
        spinnerGenere=findViewById(R.id.spinner);
        listViewArtists=findViewById(R.id.listViewArtists);
        artistList=new ArrayList<>();
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
             Artist artist=artistList.get(i);
                Intent intent=new Intent(getApplicationContext(),AddTrackActivity.class);
                intent.putExtra(ARTIST_ID,artist.getArtistId());
                intent.putExtra(ARTIST_NAME,artist.getArtistName());
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                artistList.clear();
                for(DataSnapshot artistSnapshot:dataSnapshot.getChildren()){

                    Artist artist=artistSnapshot.getValue(Artist.class);
                    artistList.add(artist);

                }
                ArtistList adapter=new ArtistList(MainActivity.this,artistList);
                listViewArtists.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addArtist(){
        String name=editTextName.getText().toString().trim();
        String genere=spinnerGenere.getSelectedItem().toString();
        if(!TextUtils.isEmpty(name)){

          String id=databaseArtists.push().getKey();
          Artist artist=new Artist(id,name,genere);
          databaseArtists.child(id).setValue(artist);
          editTextName.setText("");
            Toast.makeText(this, "Artist added", Toast.LENGTH_SHORT).show();


        }else{
            Toast.makeText(this, "Name is not provided", Toast.LENGTH_SHORT).show();
        }
    }
}
