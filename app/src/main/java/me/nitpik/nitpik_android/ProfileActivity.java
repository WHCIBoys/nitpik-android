package me.nitpik.nitpik_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import me.nitpik.nitpik_android.api.APIService;
import me.nitpik.nitpik_android.models.Nit;
import me.nitpik.nitpik_android.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maazali on 2016-09-17.
 */
public class ProfileActivity extends AppCompatActivity {

    private User profileUser;
    private boolean isUserOnOwnProfile;

    private AppCompatButton postNitBtn;
    private EditText nitContentEditView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        int position = getIntent().getIntExtra("item_index", 0);
        isUserOnOwnProfile = getIntent().getBooleanExtra("isUserOnOwnProfile", false);
        NitpikApplication app = (NitpikApplication) getApplication();
        LinearLayout nitCreateLayout = (LinearLayout) findViewById(R.id.nit_create_layout);
        RecyclerView nitList = (RecyclerView) findViewById(R.id.nit_list);
        assert nitCreateLayout != null;
        assert nitList != null;

        if (isUserOnOwnProfile) {
            // We get the currently logged in user
            // TODO: store the currently logged in user in the application
            profileUser = app.getFriendships().get(0).getUser();
            nitCreateLayout.setVisibility(View.INVISIBLE);
            nitList.setVisibility(View.VISIBLE);
        } else {
            profileUser = app.getFriendships().get(position).getFriend();
            nitCreateLayout.setVisibility(View.VISIBLE);
            nitList.setVisibility(View.INVISIBLE);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the back button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CoordinatorLayout profileViewLayout = (CoordinatorLayout) findViewById(R.id.profile_view_layout);

        assert toolbarLayout != null;
        assert appBarLayout != null;

        toolbarLayout.setTitle(profileUser.getName());

        ImageView backdrop = (ImageView) findViewById(R.id.friend_image_backdrop);
        CircleImageView mainImage = (CircleImageView) findViewById(R.id.friendship_page_main_img);
        CircleImageView authorImg = (CircleImageView) findViewById(R.id.nit_author_image);
        nitContentEditView = (EditText) findViewById(R.id.nit_content_edit_view);

        Picasso.with(this)
                .load(profileUser.getAvatarUrl("normal"))
                .transform(new BlurTransformation(this, 25))
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(backdrop);

        Picasso.with(ProfileActivity.this)
                .load(profileUser.getAvatarUrl("normal"))
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(mainImage);

        Picasso.with(ProfileActivity.this)
                .load(app.getCurrentUser().getAvatarUrl("normal"))
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(authorImg);

        appBarLayout.setExpanded(false);
        setupNitPostBtn();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isUserOnOwnProfile) {
            makeNitRequest();
        }
    }

    private void makeNitRequest() {
        final NitpikApplication app = (NitpikApplication) getApplication();
        APIService service = app.getAPIService();

        Call<List<Nit>> nitsReq = service.getNits(app.getCurrentUser().getId(), null);


        nitsReq.enqueue(new Callback<List<Nit>>() {
            @Override
            public void onResponse(Call<List<Nit>> call, Response<List<Nit>> response) {

                if (response.body() == null) {
                    Toast.makeText(getApplicationContext(), "Response body was null", Toast.LENGTH_LONG).show();
                } else {
                    View recyclerView = findViewById(R.id.nit_list);
                    assert recyclerView != null;

                    ((RecyclerView)recyclerView).addItemDecoration(
                            new HorizontalDividerItemDecoration.Builder(ProfileActivity.this)
                                    .build());

                    setupRecyclerView((RecyclerView) recyclerView, response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Nit>> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();

            }
        });

    }

    private void setupNitPostBtn() {
        postNitBtn = (AppCompatButton) findViewById(R.id.post_nit_btn);

        postNitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserOnOwnProfile) {
                    Toast.makeText(getApplicationContext(), "Can't create a nit against yourself!", Toast.LENGTH_LONG).show();
                    return;
                }

                NitpikApplication app = ((NitpikApplication)getApplication());

                Boolean isAnonymous = true;
                String nitContent = nitContentEditView.getText().toString();

                if (nitContent.equals("")) {
                    Toast.makeText(getApplicationContext(), "Content of nit cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if (profileUser != null) {
                    Nit newNit = new Nit(app.getCurrentUser(), profileUser, nitContent, isAnonymous);

                    Toast.makeText(getApplicationContext(), "Posting nit...", Toast.LENGTH_SHORT).show();

                    makeCreateNitRequest(newNit);

                }
            }
        });
    }

    private void makeCreateNitRequest(Nit newNit) {
        APIService service = ((NitpikApplication) getApplication()).getAPIService();

        Call<Nit> createNitReq = service.createNit(newNit);

        createNitReq.enqueue(new Callback<Nit>() {
            @Override
            public void onResponse(Call<Nit> call, Response<Nit> response) {

                if (response.body() == null) {
                    Toast.makeText(getApplicationContext(), "Nit was not created", Toast.LENGTH_LONG).show();
                    if (response.raw().code() == 401) {
                        LoginManager.getInstance().logOut();

                        Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                        ProfileActivity.this.startActivity(loginIntent);
                        ProfileActivity.this.finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nit successfully posted", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Nit> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong, nit not created", Toast.LENGTH_LONG).show();
            }
        });
    }





    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Nit> nits) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(nits));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Nit> nits;

        public SimpleItemRecyclerViewAdapter(List<Nit> nits) {
            this.nits = nits;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.nit_item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = this.nits.get(position);
            String content = holder.mItem.getContent();
//            boolean isAnonymous = holder.mItem.getIsAnonymous();

            holder.content.setText(content);
        }

        @Override
        public int getItemCount() {
            return nits.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final LinearLayout cardView;
            public final TextView content;

            public Nit mItem;

            public ViewHolder(View view) {
                super(view);
                cardView = (LinearLayout) view.findViewById(R.id.nit_card);
                content = (TextView) view.findViewById(R.id.nit_content);
            }
        }
    }


}
