package com.example.clouddatabaseapp;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import static com.example.clouddatabaseapp.UserStaticInfo.POSITION;
import static com.example.clouddatabaseapp.UserStaticInfo.users;

public class MainActivity extends AppCompatActivity {

    Context context;
    LayoutInflater layoutInflater;
    static UserListAdapter userListAdapter;
    FrameLayout UserPanel;
    static TextView NameTextView, StateTextView, AgeTextView;
    int positionActiveUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new UserStaticInfo();
        Init();
    }

    private void Init() {
        context = this;
        layoutInflater = LayoutInflater.from(context);
        ListView listView = findViewById(R.id.listView);
        userListAdapter = new UserListAdapter();
        listView.setAdapter(userListAdapter);

        UserPanel = findViewById(R.id.userPanel);
        NameTextView = findViewById(R.id.NameTextView);
        StateTextView = findViewById(R.id.StateTextView);
        AgeTextView = findViewById(R.id.AgeTextView);
    }

    public void GoToUserProfile(int position) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra(POSITION, position);
        startActivity(intent);
    }

    public void EditUser(View view) {
        GoToUserProfile(positionActiveUser);
    }

    public void BackToList(View view) {
        UserVisibility(false);
    }

    private void UserVisibility(boolean visible) {
        if (UserPanel != null) {
            UserPanel.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public static void UpdateListAndUserPanel(User user) {
        userListAdapter.notifyDataSetChanged();
        InitPanel(user);
    }

    private static void InitPanel(User item) {
        if (NameTextView != null) NameTextView.setText(item.getName());
        if (StateTextView != null) StateTextView.setText(item.getState());
        if (AgeTextView != null) AgeTextView.setText(String.valueOf(item.getAge()));
    }

    private class UserListAdapter extends BaseAdapter {
        @Override
        public int getCount() { return users.size(); }

        @Override
        public User getItem(int position) { return users.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View currentView, ViewGroup parent) {
            User currentUser = getItem(position);
            currentView = layoutInflater.inflate(R.layout.item_user, parent, false);

            TextView nameView = currentView.findViewById(R.id.NameTextView);
            TextView stateView = currentView.findViewById(R.id.StateTextView);
            FrameLayout StateRound = currentView.findViewById(R.id.StateRound);

            nameView.setText(currentUser.getName());
            stateView.setText(currentUser.getState());

            switch (currentUser.getStateSignal()) {
                case 0: StateRound.setBackgroundResource(R.drawable.back_offline); break;
                case 1: StateRound.setBackgroundResource(R.drawable.back_online); break;
                case 2: StateRound.setBackgroundResource(R.drawable.back_departed); break;
            }

            currentView.setOnClickListener(view -> {
                positionActiveUser = position;
                InitPanel(getItem(position));
                UserVisibility(true);
            });

            return currentView;
        }
    }
}
