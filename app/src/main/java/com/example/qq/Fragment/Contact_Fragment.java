package com.example.qq.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.qq.R;
import com.example.qq.activity.AddPeople_Activity;
import com.example.qq.adapter.ConcaterAdapter;
import com.example.qq.dao.ConcaterDao;
import com.example.qq.db.model.Concater;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Contact_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contact_Fragment extends Fragment {
    private static final String TAG= Contact_Fragment.class.getSimpleName();
    private final Activity mContext=getActivity();
    private RecyclerView rec;
    private ConcaterAdapter adapter;
    private ConcaterDao concaterDao;
    private List<Concater> list;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    public Contact_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment qqContact.
     */
    // TODO: Rename and change types and number of parameters
    public static Contact_Fragment newInstance() {
        Contact_Fragment fragment = new Contact_Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        System.out.println(concaterDao.queryAll());
        adapter.setList(concaterDao.queryAll());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_qq_contact, container, false);
        Button btn_add=view.findViewById(R.id.btn_add);
        Button btn_logout=view.findViewById(R.id.btn_logout);
        concaterDao=new ConcaterDao(getActivity());
        list = concaterDao.queryAll();
        adapter=new ConcaterAdapter(mContext,list);
        rec=view.findViewById(R.id.rec);
        rec.setLayoutManager(new LinearLayoutManager(mContext));
        rec.setAdapter(adapter);




        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), AddPeople_Activity.class);
                startActivity(intent);
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.finish();
            }
        });
        return view;
    }
}