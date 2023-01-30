package com.example.manoj_dev.musicplayer.Fragments;


import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.manoj_dev.musicplayer.Adapter.SongAdapter;
import com.example.manoj_dev.musicplayer.Model.SongsList;
import com.example.manoj_dev.musicplayer.R;

import java.io.File;
import java.util.ArrayList;

public class AllSongFragment extends ListFragment {


    private static ContentResolver contentResolver1;
    private SongAdapter adapter;

    public ArrayList<SongsList> songsList;
    public ArrayList<SongsList> newList;

    private ListView listView;

    private createDataParse createDataParse;
    private ContentResolver contentResolver;

    public static Fragment getInstance(int position, ContentResolver mcontentResolver) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        AllSongFragment tabFragment = new AllSongFragment();
        tabFragment.setArguments(bundle);
        contentResolver1 = mcontentResolver;
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParse = (createDataParse) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        contentResolver = contentResolver1;
        setContent();
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    public void setContent() {
        boolean searchedList = false;
        songsList = new ArrayList<>();
        newList = new ArrayList<>();
        getMusic();
        adapter = new SongAdapter(getContext(), songsList);
        if (!createDataParse.queryText().equals("")) {
            adapter = onQueryTextChange();
            adapter.notifyDataSetChanged();
            searchedList = true;
        } else {
            searchedList = false;
        }
        createDataParse.getLength(songsList.size());
        listView.setAdapter(adapter);

        final boolean finalSearchedList = searchedList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                if (!finalSearchedList) {
                    createDataParse.onDataPass(songsList.get(position).getTitle(), songsList.get(position).getPath());
                    createDataParse.fullSongList(songsList, position);
                } else {
                    createDataParse.onDataPass(newList.get(position).getTitle(), newList.get(position).getPath());
                    createDataParse.fullSongList(songsList, position);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Are you sure you want to delete this?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeItem(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });
    }
    public void removeItem(int position) {
        String uri = songsList.get(position).getPath();
        Log.e("TAG==>", uri);
        File file = new File(uri);
        if (file.exists()) {
            if (file.delete()) {
                songsList.remove(position);
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getMusic() {
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                songsList.add(new SongsList(songCursor.getString(songTitle), songCursor.getString(songArtist), songCursor.getString(songPath)));
            } while (songCursor.moveToNext());
            songCursor.close();
        }
    }

    public SongAdapter onQueryTextChange() {
        String text = createDataParse.queryText();
        for (SongsList songs : songsList) {
            String title = songs.getTitle().toLowerCase();
            if (title.contains(text)) {
                newList.add(songs);
            }
        }
        return new SongAdapter(getContext(), newList);

    }

    private void showDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(getString(R.string.play_next))
                .setCancelable(true)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createDataParse.currentSong(songsList.get(position));
                        setContent();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public interface createDataParse {
        public void onDataPass(String name, String path);

        public void fullSongList(ArrayList<SongsList> songList, int position);

        public String queryText();

        public void currentSong(SongsList songsList);

        public void getLength(int length);
    }

}
