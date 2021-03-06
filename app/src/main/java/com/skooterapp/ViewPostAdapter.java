package com.skooterapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.skooterapp.data.Comment;
import com.skooterapp.data.Post;

import java.util.ArrayList;
import java.util.List;

public class ViewPostAdapter extends ArrayAdapter {

    protected Context mContext;
    protected int mLayoutResourceId;
    protected List<Object> data = new ArrayList<>();
    protected boolean mFlaggable;
    protected LinearLayout mDeleteView;
    protected LinearLayout mFlagView;
    protected TextView mTypeIdView;
    protected TextView mTypeView;
    protected boolean canPerformActivity = true;

    public ViewPostAdapter(Context context, int resource, List<Object> objects) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
        this.mFlaggable = false;
    }

    public ViewPostAdapter(Context context, int resource, List<Object> objects, boolean canPerformActivity) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
        this.canPerformActivity = canPerformActivity;
    }

    public ViewPostAdapter(Context context, int resource, List<Object> objects, boolean flaggable, LinearLayout flagView, LinearLayout deleteView, TextView typeIdView, TextView typeView, boolean canPerformActivity) {
        super(context, resource, objects);
        mContext = context;
        mLayoutResourceId = resource;
        this.data = objects;
        this.mFlaggable = flaggable;
        this.mFlagView = flagView;
        this.mDeleteView = deleteView;
        this.mTypeIdView = typeIdView;
        this.mTypeView = typeView;
        this.canPerformActivity = canPerformActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (data.get(position).getClass().equals(Post.class)) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_view_post_row, parent, false);
            }

            final Post post = (Post) data.get(position);


            final View is_user_post_view = convertView.findViewById(R.id.is_user_skoot);
            final TextView postContent = (TextView) convertView.findViewById(R.id.postText);
            final TextView handleContent = (TextView) convertView.findViewById(R.id.handleText);
            final TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            final TextView voteCount = (TextView) convertView.findViewById(R.id.voteCount);
            final TextView commentsCount = (TextView) convertView.findViewById(R.id.commentsCount);
            final TextView favoritesCount = (TextView) convertView.findViewById(R.id.favoritesCount);
            final FeedImageView postImage = (FeedImageView) convertView.findViewById(R.id.post_image);
            final ImageView commentImage = (ImageView) convertView.findViewById(R.id.commentImage);
            final Button flagButton = (Button) convertView.findViewById(R.id.flagButton);
            final Button favoriteBtn = (Button) convertView.findViewById(R.id.favorite);
            final Button upvoteBtn = (Button) convertView.findViewById(R.id.upvote);
            final Button downvoteBtn = (Button) convertView.findViewById(R.id.downvote);

            if (post.isUserSkoot()) {
                is_user_post_view.setAlpha(1.0f);
                is_user_post_view.setVisibility(View.VISIBLE);
            } else {
                is_user_post_view.setAlpha(0.0f);
                is_user_post_view.setVisibility(View.GONE);
            }

            postContent.setText(post.getContent());

            handleContent.setText(post.getChannel());
            handleContent.setVisibility(View.VISIBLE);
            if (post.getChannel().equals("")) {
                handleContent.setVisibility(View.GONE);
            }
            if (mContext.getClass() != ChannelActivity.class) {
                handleContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence channel = handleContent.getText();
                        Intent intent = new Intent(mContext, ChannelActivity.class);
                        intent.putExtra("CHANNEL_NAME", channel.toString());
                        mContext.startActivity(intent);
                    }
                });
            }

            timestamp.setText(post.getTimestamp());
            voteCount.setText(Integer.toString(post.getVoteCount()));
            commentsCount.setText(Integer.toString(post.getCommentsCount()));
            favoritesCount.setText(Integer.toString(post.getFavoriteCount()));

            if (mFlaggable) {
                flagButton.setVisibility(View.VISIBLE);
                if (post.isUserSkoot()) {
                    flagButton.setBackground(mContext.getResources().getDrawable(R.drawable.delete));
                } else {
                    flagButton.setBackground(mContext.getResources().getDrawable(R.drawable.flag_inactive));
                }
                flagButton.setTag(post);

                flagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Post post = (Post) flagButton.getTag();

                        if (post.isUserSkoot()) {
                            //Deletable post
                            mDeleteView.setVisibility(View.VISIBLE);
                            mTypeIdView.setText(Integer.toString(post.getId()));
                            mTypeView.setText("post");
                        } else {
                            //Flaggable post
                            mFlagView.setVisibility(View.VISIBLE);
                            mTypeIdView.setText(Integer.toString(post.getId()));
                            mTypeView.setText("post");
                        }
                    }
                });
            } else {
                flagButton.setVisibility(View.GONE);
            }
            if (post.isImagePresent()) {
                postImage.setVisibility(View.VISIBLE);
                postImage.setImageUrl(post.getSmallImageUrl(), AppController.getInstance().getImageLoader());

                postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Open an activity with the full image
                        Intent intent = new Intent(mContext, ViewImage.class);
                        intent.putExtra("IMAGE_URL", post.getLargeImageUrl());
                        mContext.startActivity(intent);
                    }
                });
            } else {
                postImage.setVisibility(View.GONE);
            }

            favoriteBtn.setTag(post);
            upvoteBtn.setTag(post);
            downvoteBtn.setTag(post);

            //Favorited
            if (post.isUserFavorited()) {
                favoriteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.favorite_icon_active));
            } else {
                favoriteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.favorite_icon_inactive));
            }

            favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = (Post) favoriteBtn.getTag();

                    if (!post.isUserFavorited()) {
                        favoriteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.favorite_icon_active));
                        post.favoritePost();
                    } else {
                        favoriteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.favorite_icon_inactive));
                        post.unFavoritePost();
                    }
                    favoritesCount.setText(Integer.toString(post.getFavoriteCount()));
                }
            });

            //Commented
            if (post.isUserCommented()) {
                commentImage.setImageResource(R.drawable.comment_active);
            } else {
                commentImage.setImageResource(R.drawable.comment_inactive);
            }

            if (canPerformActivity) {
                upvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        Button upvoteBtn = (Button) rl.findViewById(R.id.upvote);
                        Button downvoteBtn = (Button) rl.findViewById(R.id.downvote);
                        Post post = (Post) upvoteBtn.getTag();

                        //Call the upvote method
                        post.upvotePost();
                        voteCount.setText(Integer.toString(post.getVoteCount()));

                        upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_active));
                        downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
                    }
                });

                downvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        Button upvoteBtn = (Button) rl.findViewById(R.id.upvote);
                        Button downvoteBtn = (Button) rl.findViewById(R.id.downvote);

                        Post post = (Post) downvoteBtn.getTag();

                        //Call the downvote method
                        post.downvotePost();
                        voteCount.setText(Integer.toString(post.getVoteCount()));
                        upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
                        downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                    }
                });
            } else {
                upvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("You can't do any activity outside 3 kms");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

                downvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("You can't do any activity outside 3 kms");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }

            if (post.isIfUserVoted()) {
                if (post.isUserVote()) {
                    upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_active));
                    downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
                } else {
                    upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
                    downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                }
            } else {
                upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
                downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
            }
            convertView.setBackgroundColor(Color.parseColor("#1A01EDFF"));
        } else if (data.get(position).getClass().equals(Comment.class)) {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(R.layout.list_view_comment_post_row, parent, false);
            }

            Comment comment = (Comment) data.get(position);

            View is_user_comment = convertView.findViewById(R.id.is_user_comment);
            final TextView commentContent = (TextView) convertView.findViewById(R.id.postText);
            final TextView timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            final TextView voteCount = (TextView) convertView.findViewById(R.id.voteCount);
            final Button upvoteBtn = (Button) convertView.findViewById(R.id.upvote);
            final Button downvoteBtn = (Button) convertView.findViewById(R.id.downvote);
            final Button flagButton = (Button) convertView.findViewById(R.id.flagButton);

            commentContent.setText(comment.getContent());
            timestamp.setText(comment.getTimestamp());
            voteCount.setText(Integer.toString(comment.getVoteCount()));

            upvoteBtn.setTag(comment);
            downvoteBtn.setTag(comment);

            if (comment.isUserComment()) {
                is_user_comment.setVisibility(View.VISIBLE);
                is_user_comment.setAlpha(1.0f);

                flagButton.setBackground(mContext.getResources().getDrawable(R.drawable.delete));
            } else {
                is_user_comment.setVisibility(View.GONE);
                is_user_comment.setAlpha(0.0f);

                flagButton.setBackground(mContext.getResources().getDrawable(R.drawable.flag_inactive));
            }

            if (mFlaggable) {
                flagButton.setTag(comment);

                flagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Comment comment = (Comment) flagButton.getTag();

                        if (comment.isUserComment()) {
                            //Deletable post
                            mDeleteView.setVisibility(View.VISIBLE);
                            mTypeIdView.setText(Integer.toString(comment.getId()));
                            mTypeView.setText("comment");
                        } else {
                            //Flaggeble post
                            mFlagView.setVisibility(View.VISIBLE);
                            mTypeIdView.setText(Integer.toString(comment.getId()));
                            mTypeView.setText("comment");
                        }
                    }
                });
            } else {
                flagButton.setVisibility(View.GONE);
            }
            if (canPerformActivity) {
                upvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        Button upvoteBtn = (Button) rl.findViewById(R.id.upvote);
                        Button downvoteBtn = (Button) rl.findViewById(R.id.downvote);
                        Comment comment = (Comment) upvoteBtn.getTag();

                        //Call the upvote method
                        comment.upvoteComment();
                        voteCount.setText(Integer.toString(comment.getVoteCount()));

                        upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_active));
                        downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
                    }
                });

                downvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout) v.getParent();
                        Button upvoteBtn = (Button) rl.findViewById(R.id.upvote);
                        Button downvoteBtn = (Button) rl.findViewById(R.id.downvote);

                        Comment comment = (Comment) downvoteBtn.getTag();

                        //Call the upvote method
                        comment.downvoteComment();
                        voteCount.setText(Integer.toString(comment.getVoteCount()));

                        upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
                        downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                    }
                });
            } else {
                upvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("You can't do any activity outside 3 kms");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

                downvoteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("You can't do any activity outside 3 kms");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }

            if (comment.isIfUserVoted()) {
                if (comment.isUserVote()) {
                    upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_active));
                    downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
                } else {
                    upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
                    downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_active));
                }
            } else {
                upvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_up_inactive));
                downvoteBtn.setBackground(mContext.getResources().getDrawable(R.drawable.vote_down_inactive));
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return (null != data ? data.size() : 0);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}