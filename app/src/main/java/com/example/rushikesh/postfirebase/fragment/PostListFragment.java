package com.example.rushikesh.postfirebase.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rushikesh.postfirebase.PostDetailActivity;
import com.example.rushikesh.postfirebase.R;
import com.example.rushikesh.postfirebase.models.Post;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StreamDownloadTask;
import com.squareup.picasso.Picasso;

public abstract class PostListFragment extends Fragment {

    private static final String TAG = "PostListFragment";

    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]
    public View dia_view;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    public Context context;
    private ImageView imageView;
    AlertDialog alert;
    private View v;
    private static int FLAG=0;

    @Override
    public Context getContext() {

        return getActivity();
    }



    public PostListFragment() {}

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_posts, container, false);
         v = inflater.inflate(R.layout.dialog,container,false);
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
      //  mDatabase.keepSynced(true);
        // [END create_database_reference]
//
//        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
//        dia_view = layoutInflater.inflate(R.layout.dialog,null);
//        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
//
//        dialog.setView(dia_view);
//        AlertDialog alert = dialog.create();


        mRecycler = (RecyclerView) rootView.findViewById(R.id.messages_list);
        mRecycler.setHasFixedSize(true);


        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mLayoutManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(Post.class, R.layout.item_post,
                PostViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                mDatabase.child("posts").child(postKey).child("uid").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Toast.makeText(getActivity(),dataSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
                        mDatabase.child("users").child(dataSnapshot.getValue().toString()).child("Image").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try
                                {
//                                    ImageView img = (ImageView)dia_view.findViewById(R.id.image_post_dia);
                                    Picasso.with(getActivity()).load(dataSnapshot.getValue().toString()).into(viewHolder.image_prof);
//                                    Picasso.with(getActivity()).load(dataSnapshot.getValue().toString()).into(img);
                                }
                                catch (Exception e)
                                {
//                                    ImageView img = (ImageView)dia_view.findViewById(R.id.image_post_dia);
                                    Picasso.with(getActivity()).load("https://firebasestorage.googleapis.com/v0/b/firstfirebase-1ec42.appspot.com/o/Default%2Fprofile_1x.png?alt=media&token=206f7802-9f01-4773-a3e1-7d47b70a8998").into(viewHolder.image_prof);
//                                    Picasso.with(getActivity()).load("https://firebasestorage.googleapis.com/v0/b/firstfirebase-1ec42.appspot.com/o/Default%2Fprofile_1x.png?alt=media&token=206f7802-9f01-4773-a3e1-7d47b70a8998").into(img);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
//
                        mDatabase.child("users").child(dataSnapshot.getValue().toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                            try
                            {
//                                if(dataSnapshot.child("Name").getValue().toString()==null)
//                                {
                                    viewHolder.user_name.setText(dataSnapshot.child("username").getValue().toString());
//                                }
//                                else
//                                {
//                                    viewHolder.user_name.setText(dataSnapshot.child("Name").getValue().toString());
//                                }


                            }
                            catch (Exception e)
                            {
//                                TextView txt = (TextView)dia_view.findViewById(R.id.status_dia);
//                                txt.setText("No Status");
                            }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//
                viewHolder.image_prof.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mDatabase.child("posts").child(postKey).child("uid").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
//                        Toast.makeText(getActivity(),dataSnapshot.getValue().toString(),Toast.LENGTH_SHORT).show();
                                mDatabase.child("users").child(dataSnapshot.getValue().toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        try
                                        {
//                                    ImageView img = (ImageView)dia_view.findViewById(R.id.image_post_dia);
                                            show_dia(dataSnapshot.child("Image").getValue().toString(),dataSnapshot.child("Status").getValue().toString());
                                            //Picasso.with(getActivity()).load(dataSnapshot.getValue().toString()).into();
//                                    Picasso.with(getActivity()).load(dataSnapshot.getValue().toString()).into(img);
                                        }
                                        catch (Exception e)
                                        {
                                            show_dia("https://firebasestorage.googleapis.com/v0/b/firstfirebase-1ec42.appspot.com/o/Default%2Fprofile_1x.png?alt=media&token=206f7802-9f01-4773-a3e1-7d47b70a8998","No Status");
//                                    ImageView img = (ImageView)dia_view.findViewById(R.id.image_post_dia);
                                           // Picasso.with(getActivity()).load("https://firebasestorage.googleapis.com/v0/b/firstfirebase-1ec42.appspot.com/o/Default%2Fprofile_1x.png?alt=media&token=206f7802-9f01-4773-a3e1-7d47b70a8998").into(viewHolder.image_prof);
//                                    Picasso.with(getActivity()).load("https://firebasestorage.googleapis.com/v0/b/firstfirebase-1ec42.appspot.com/o/Default%2Fprofile_1x.png?alt=media&token=206f7802-9f01-4773-a3e1-7d47b70a8998").into(img);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
                Picasso.with(getActivity()).load(model.image).into(viewHolder.imageView);
//                imageView  = (ImageView).findViewById(R.id.image_post);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                        intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });

                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View starView) {
                        // Need to write to both places the post is stored
                        DatabaseReference globalPostRef = mDatabase.child("posts").child(postRef.getKey());
                        DatabaseReference userPostRef = mDatabase.child("user-posts").child(model.uid).child(postRef.getKey());

                        // Run two transactions
                        onStarClicked(globalPostRef);
                        onStarClicked(userPostRef);
                    }
                });
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    private void show_dia(String img, String st) {

        final NiftyDialogBuilder dialogBuilder=NiftyDialogBuilder.getInstance(getActivity());
        v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        ImageView imageView =(ImageView)v.findViewById(R.id.image_post_dia);

        Picasso.with(getActivity()).load(img).into(imageView);
        dialogBuilder
                .setCustomView(v,getActivity())
                .withTitle("Profile")
                .withDividerColor("#FFFFFF")
                .withEffect(Effectstype.Fall)
                .withDialogColor("#039BE5")
                .withMessageColor("#FFFFFF")
                .withMessage(st)
                .show();

    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public TextView authorView;
        public ImageView starView;
        public TextView numStarsView;
        public TextView bodyView;
        public ImageView imageView;
        public ImageView image_prof;
        public TextView user_name;



        public PostViewHolder(View itemView) {
            super(itemView);

//        titleView = (TextView) itemView.findViewById(R.id.post_title);
//        authorView = (TextView) itemView.findViewById(R.id.post_author);
            starView = (ImageView) itemView.findViewById(R.id.star);
            numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
            imageView = (ImageView)itemView.findViewById(R.id.image_post);
            image_prof = (ImageView)itemView.findViewById(R.id.post_author_photo);
            user_name = (TextView)itemView.findViewById(R.id.post_author);
//        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        }

        public void bindToPost(Post post, View.OnClickListener starClickListener) {
//        titleView.setText(post.title);
//        authorView.setText(post.author);
            numStarsView.setText(String.valueOf(post.starCount));
//        bodyView.setText(post.body);
            starView.setOnClickListener(starClickListener);
//            imageView.setOnClickListener(starClickListener);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
