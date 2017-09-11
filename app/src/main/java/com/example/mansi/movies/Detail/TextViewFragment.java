package com.example.mansi.movies.Detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mansi.movies.R;

public class TextViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.fragment_replace_textview);
        String strText = getArguments().getString(getString(R.string.textview_key));
        textView.setText(strText);
        return rootView;
    }
}

