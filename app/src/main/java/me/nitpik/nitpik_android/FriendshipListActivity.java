package me.nitpik.nitpik_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.nitpik.nitpik_android.api.APIService;
import me.nitpik.nitpik_android.models.Friendship;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maazali on 2016-09-17.
 */
public class FriendshipListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NitpikApplication app = (NitpikApplication) getApplication();

        setContentView(R.layout.activity_friendship_list);
        makeFriendshipRequest();
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());
    }

    private void makeFriendshipRequest() {
        final NitpikApplication app = (NitpikApplication) getApplication();
        APIService service = app.getAPIService();

        Call<List<Friendship>> friendshipsReq = service.getFriendships();


        friendshipsReq.enqueue(new Callback<List<Friendship>>() {
            @Override
            public void onResponse(Call<List<Friendship>> call, Response<List<Friendship>> response) {

                if (response.body() == null) {
                    Toast err = Toast.makeText(getApplicationContext(), response.errorBody().toString(), Toast.LENGTH_LONG);
                    err.show();

//                    if (response.raw().code() == 401) {
//                        LoginManager.getInstance().logOut();
//
//                        Intent loginIntent = new Intent(FriendshipListActivity.this, LoginActivity.class);
//                        FriendshipListActivity.this.startActivity(loginIntent);
//                        FriendshipListActivity.this.finish();
//                    }

                } else {
                    View recyclerView = findViewById(R.id.friendship_list);
                    assert recyclerView != null;

                    ((RecyclerView)recyclerView).addItemDecoration(new HorizontalDividerItemDecoration.Builder(FriendshipListActivity.this).build());
                    setupRecyclerView((RecyclerView)recyclerView, response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Friendship>> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Friendship> friendships) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(friendships));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Friendship> friendships;

        public SimpleItemRecyclerViewAdapter(List<Friendship> friendships) {
            this.friendships = friendships;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friendship_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Context context = FriendshipListActivity.this;
            final int index = position;
            holder.mItem = this.friendships.get(position);
            holder.friendName.setText(holder.mItem.getFriend().getName());
//            holder.friendBalance.setTextColor(holder.mItem.getBalanceColor());

            Picasso.with(FriendshipListActivity.this)
                    .load(holder.mItem.getFriend().getAvatarUrl("normal"))
//                    .placeholder(context.getResources().getDrawable(R.drawable.default_profile_picture))
//                    .error(context.getResources().getDrawable(R.drawable.default_profile_picture))
                    .into(holder.friendImage);

//            holder.cardView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Context context = v.getContext();
//                    Intent intent = new Intent(context, FriendshipDetailActivity.class);
//                    intent.putExtra("item_index", index);
//
//                    context.startActivity(intent);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return friendships.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final RelativeLayout cardView;
            public final CircleImageView friendImage;
            public final TextView friendName;
            public final TextView friendBalance;
            public Friendship mItem;

            public ViewHolder(View view) {
                super(view);
                cardView = (RelativeLayout) view.findViewById(R.id.friendship_card);
                friendImage = (CircleImageView) view.findViewById(R.id.friendship_card_image);
                friendName = (TextView) view.findViewById(R.id.friendship_card_name);
                friendBalance = (TextView) view.findViewById(R.id.friendship_card_balance);
            }
        }
    }
}

