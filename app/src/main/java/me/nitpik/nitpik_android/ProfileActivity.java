package me.nitpik.nitpik_android;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import me.nitpik.nitpik_android.api.APIService;
import me.nitpik.nitpik_android.models.Friendship;
import me.nitpik.nitpik_android.models.Nit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maazali on 2016-09-17.
 */
public class ProfileActivity extends AppCompatActivity {

    private Friendship friendship;

    private AppCompatButton postNitBtn;
    private EditText nitContentEditView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        int position = getIntent().getIntExtra("item_index", 0);
        NitpikApplication app = (NitpikApplication) getApplication();

        friendship = (Friendship) app.getFriendships().get(position);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the back button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        if (appBarLayout != null) {
            appBarLayout.setTitle(friendship.getFriend().getName());
        }

        ImageView backdrop = (ImageView) findViewById(R.id.friend_image_backdrop);
        CircleImageView mainImage = (CircleImageView) findViewById(R.id.friendship_page_main_img);
        CircleImageView authorImg = (CircleImageView) findViewById(R.id.nit_author_image);
        nitContentEditView = (EditText) findViewById(R.id.nit_content_edit_view);

        Picasso.with(this)
                .load(friendship.getFriend().getAvatarUrl("normal"))
                .transform(new BlurTransformation(this, 25))
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(backdrop);

        Picasso.with(ProfileActivity.this)
                .load(friendship.getFriend().getAvatarUrl("normal"))
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(mainImage);

        Picasso.with(ProfileActivity.this)
                .load(friendship.getUser().getAvatarUrl("normal"))
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(authorImg);

        setupNitPostBtn();
    }

    private void setupNitPostBtn() {
        postNitBtn = (AppCompatButton) findViewById(R.id.post_nit_btn);

        postNitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isAnonymous = true;
                String nitContent = nitContentEditView.getText().toString();

                if (nitContent.equals("")) {
                    Toast.makeText(getApplicationContext(), "Content of nit cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if (friendship != null) {
                    Nit newNit = new Nit(friendship.getUser(), friendship.getFriend(), nitContent, isAnonymous);

                    Toast.makeText(getApplicationContext(), "Posting nit...", Toast.LENGTH_LONG).show();

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
//                    if (response.raw().code() == 401) {
//                        LoginManager.getInstance().logOut();
//
//                        Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
//                        ProfileActivity.this.startActivity(loginIntent);
//                        ProfileActivity.this.finish();
//                    } else {
//                        Snackbar.make(layoutContainer, "Something went wrong", Snackbar.LENGTH_LONG).show();
//                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nit was posted", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Nit> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Something went wrong, nit not created", Toast.LENGTH_LONG).show();
            }
        });
    }

}
