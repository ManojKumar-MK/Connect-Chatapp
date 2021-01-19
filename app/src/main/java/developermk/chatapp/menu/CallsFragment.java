package developermk.chatapp.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.developermk.chatapp.R;

import java.util.ArrayList;
import java.util.List;

import developermk.chatapp.model.CallList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallsFragment extends Fragment {

    public CallsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.activity_profile, container, false);

            //RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
            //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            List<CallList> lists = new ArrayList<>();
            //recyclerView.setAdapter(new CallListAdapter(lists,getContext()));
        return view;
    }
}
