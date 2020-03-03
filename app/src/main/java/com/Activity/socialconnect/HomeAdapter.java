package com.Activity.socialconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeAdapterViewHolder> {

    private List<ModelBlog> hList;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

   public HomeAdapter(List<ModelBlog> hList){

       this.hList = hList;

   }



    @NonNull
    @Override
    public HomeAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_card_view,parent,false);
       context = parent.getContext();
       return new HomeAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeAdapterViewHolder holder, int position) {

       String bindDescription = hList.get(position).getBlogDescription();
       holder.sethDescription(bindDescription);


       String blogImageUri = hList.get(position).getBlogImage();
       holder.setBlogPostImage(blogImageUri);

       String userUid = hList.get(position).getBlogUserId();

       if(userUid != null){
            firebaseFirestore = FirebaseFirestore.getInstance();
           firebaseFirestore.collection("User").document(userUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
               @Override
               public void onSuccess(DocumentSnapshot documentSnapshot) {

                   if(documentSnapshot.exists()){

                       String userName = documentSnapshot.getString("userName");
                       String userThumb = documentSnapshot.getString("imageUri");

                       holder.setHomeUserName(userName);
                       holder.setHomeUserThumb(userThumb);
                   }
               }
           });

       }
   }

    @Override
    public int getItemCount() {
        return hList.size();
    }

    public class HomeAdapterViewHolder extends RecyclerView.ViewHolder{

        View item;
        TextView homeDescription;
        ImageView blogPost;
        TextView homeUserName;
        ImageView homeUserThumb;




        public HomeAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

             this.item = itemView;

            }


             void sethDescription(String viewDescription){

              homeDescription = item.findViewById(R.id.home_frag_user_desc);
              homeDescription.setText(viewDescription);
        }

             void setBlogPostImage(String imageUri){

             blogPost = item.findViewById(R.id.home_frag_blog_image);
              Glide.with(context)
                      .load(imageUri)
                      .placeholder(R.mipmap.blog_display)
                      .into(blogPost);
        }

             void setHomeUserName(String userViewName){

                 homeUserName = item.findViewById(R.id.home_frag_user_name);
                 homeUserName.setText(userViewName);
             }

             void setHomeUserThumb(String viewThumb){

                homeUserThumb = item.findViewById(R.id.home_frag_user_image_thumb);
                Glide.with(context)
                        .load(viewThumb)
                        .placeholder(R.mipmap.user_thumb)
                        .into(homeUserThumb);


             }

    }
}



