package com.literacyall.app.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.literacyall.app.R;
import com.literacyall.app.activities.MainActivity;
import com.literacyall.app.adapter.CustomListAdapter;
import com.literacyall.app.adapter.SequenceManageAdapter;
import com.literacyall.app.listener.RecyclerViewClickListener;
import com.literacyall.app.utilities.CustomList;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SequenceManageDialog extends Dialog implements CustomListAdapter.Listener {

    public Context context;
    private SequenceManageAdapter allCorrectItemAdapter;
    private SequenceManageAdapter sequenceManageAdapter;
    RecyclerView rvSequence;
    private ArrayList<CustomList> sequenceList;
    ArrayList<CustomList> allCorrectItemList;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.recyclerPendingList)
    RecyclerView recyclerPendingList;

    @Bind(R.id.textEmptyList)
    TextView textEmptyList;

    @Bind(R.id.ibtnSeqDone)
    ImageButton ibtnSeqDone;

    public SequenceManageDialog(){
        super(null);
    }

    public SequenceManageDialog(Context context, ArrayList<CustomList> sequenceList, ArrayList<CustomList> allCorrectItemList) {
        super(context, R.style.CustomAlertDialog);
        this.context = context;
        this.sequenceList = sequenceList;
        this.allCorrectItemList = allCorrectItemList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_sequence_manage_);
        ButterKnife.bind(this);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerPendingList.setLayoutManager(layoutManager);
        //onSuccess();

        loadAllCorrectItems();
        loadSequenceList();

        ibtnSeqDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.setSequence(generateString(sequenceList));
                dismiss();
                //CustomToast.t(context, generateString(sequenceList));
            }
        });

    }

    private void onSuccess() {
        try {
            //Top One
            CustomListAdapter topCustomListAdapter = new CustomListAdapter(context, sequenceList, this);
            recyclerView.setAdapter(topCustomListAdapter);

            topCustomListAdapter.setSendSequenceListListener(new CustomListAdapter.SendSequenceList() {
                @Override
                public void onDrop(List<CustomList> customListTarget) {
                    generateString(customListTarget);
                }
            });

            //Bottom One
            CustomListAdapter bottomCustomListAdapter = new CustomListAdapter(context, new ArrayList<CustomList>(), this);
            recyclerPendingList.setAdapter(bottomCustomListAdapter);
            textEmptyList.setOnDragListener(bottomCustomListAdapter.getDragInstance());

            bottomCustomListAdapter.setSendSequenceListListener(new CustomListAdapter.SendSequenceList() {
                @Override
                public void onDrop(List<CustomList> customListTarget) {
                    generateString(customListTarget);
                }
            });
            // textEmptyList.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setEmptyList(boolean visibility) {
        textEmptyList.setVisibility(visibility ? View.VISIBLE : View.GONE);
        recyclerPendingList.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }


    // This Method will call when item drop from one to another
    private String generateString(List<CustomList> customListTarget) {
        StringBuilder rString = new StringBuilder();
        String sep = ", ";
        for (int i = 0; i < customListTarget.size(); i++) {
            if (i == 0) {
                rString.append(customListTarget.get(i).key);
            } else {
                rString.append(sep).append(customListTarget.get(i).key);
            }
        }
        return rString.toString();
    }


    private void loadAllCorrectItems() {
        allCorrectItemAdapter = new SequenceManageAdapter(context, allCorrectItemList, new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                boolean flag = false;
                for (CustomList customList : sequenceList) {
                    if (customList.key == allCorrectItemList.get(position).key) {
                        flag = true;
                    }
                }
                if (flag == false) {
                    sequenceList.add(allCorrectItemList.get(position));
                    loadSequenceList();
                }

            }
        });
        recyclerView.setAdapter(allCorrectItemAdapter);
    }

    private void loadSequenceList() {
        sequenceManageAdapter = new SequenceManageAdapter(context, sequenceList, new RecyclerViewClickListener() {
            @Override
            public void recyclerViewListClicked(View v, int position) {
                sequenceList.remove(position);
                loadSequenceList();
            }
        });
        recyclerPendingList.setAdapter(sequenceManageAdapter);
    }
}
