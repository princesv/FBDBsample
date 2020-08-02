package com.prince.fbdbsample2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Artist artist=artistList.get(position);
                showUpdateDialog(artist.getArtistId(),artist.getArtistName());
                return false;
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

    private void showUpdateDialog(final String artistId, String artistName){
        AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater=getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.update_dialog,null);

        dialogBuilder.setView(dialogView);
        final EditText editTextName=dialogView.findViewById(R.id.dialogArtist);
        final Spinner spinnerGenere=dialogView.findViewById(R.id.dialogSpinner);
        final Button updateButton=dialogView.findViewById(R.id.dialogButton);
        final Button deleteButton=dialogView.findViewById(R.id.deleteButton);
        dialogBuilder.setTitle("Update Artist "+artistName);
        final AlertDialog alertDialog=dialogBuilder.create();
        alertDialog.show();

        final String iD=artistId;

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference drArtist=FirebaseDatabase.getInstance().getReference("artist").child(artistId);
                DatabaseReference drtracks=FirebaseDatabase.getInstance().getReference("tracks").child(artistId);
                drArtist.removeValue();
                drtracks.removeValue();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=editTextName.getText().toString().trim();
                String genere=spinnerGenere.getSelectedItem().toString().trim();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(MainActivity.this, "name not entered", Toast.LENGTH_SHORT).show();
                }
                else {
                    Artist artist = new Artist(iD, name, genere);
                    DatabaseReference dbrEdit = FirebaseDatabase.getInstance().getReference("artist").child(iD);

                    dbrEdit.setValue(artist);
                    Toast.makeText(MainActivity.this, "Update Successful", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
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
