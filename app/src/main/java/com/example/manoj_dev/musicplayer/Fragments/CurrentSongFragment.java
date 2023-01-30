package com.example.manoj_dev.musicplayer.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.manoj_dev.musicplayer.Adapter.SongAdapter;
import com.example.manoj_dev.musicplayer.Model.SongsList;
import com.example.manoj_dev.musicplayer.R;

import java.util.ArrayList;

public class CurrentSongFragment extends ListFragment {

    public ArrayList<SongsList> songsList = new ArrayList<>();

    private ListView listView;

    private createDataParsed createDataParsed;

    public static Fragment getInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("pos", position);
        CurrentSongFragment tabFragment = new CurrentSongFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createDataParsed = (createDataParsed) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.list_playlist);
        //songsList = new ArrayList<>();
        setContent();
    }

    /**
     * Setting the content in the listView and sending the data to the Activity
     */
    public void setContent() {
        if (createDataParsed.getSong() != null)
            songsList.add(createDataParsed.getSong());

        SongAdapter adapter = new SongAdapter(getContext(), songsList);

        if (songsList.size() > 1)
            if (createDataParsed.getPlaylistFlag()) {
                songsList.clear();
            }

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "You clicked :\n" + songsList.get(position), Toast.LENGTH_SHORT).show();
                createDataParsed.onDataPass(songsList.get(position).getTitle(), songsList.get(position).getPath());
                createDataParsed.fullSongList(songsList, position);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return true;
            }
        });
    }

    public interface createDataParsed {
        public void onDataPass(String name, String path);

        public void fullSongList(ArrayList<SongsList> songList, int position);

        public SongsList getSong();

        public boolean getPlaylistFlag();
    }


}
