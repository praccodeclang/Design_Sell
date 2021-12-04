package com.taewon.shoppingmall.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.taewon.shoppingmall.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class DownloadPictureRecyclerAdapter extends RecyclerView.Adapter<DownloadPictureRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<StorageReference> refs;
    public DownloadPictureRecyclerAdapter(Context context, ArrayList<StorageReference> refs){
        this.context = context;
        this.refs = refs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_img_item, parent, false);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.board_slide_in);
        view.setAnimation(animation);
        return new DownloadPictureRecyclerAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StorageReference ref = refs.get(position);
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                .load(uri)
                .error(R.drawable.ic_warning)
                .into(holder.iv_download_img);
            }
        });

        holder.iv_download_img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("이미지 다운로드")
                        .setMessage("다운로드 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //휴대폰 로컬 영역에 저장하기
                                String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/DesignSell/"+ref.getParent().getName();
                                try {
                                    File fileDir = new File(savePath);
                                    if(!fileDir.exists()){
                                        fileDir.mkdirs();
                                    }
                                    File downloadFile = new File(fileDir, ref.getName() +".png");
                                    ref.getFile(downloadFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Toast.makeText(context, "이미지를 저장 했습니다." + downloadFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "파일 저장 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                catch (Exception e) {
                                    Toast.makeText(context, "예외가 발생했다 씨!!!!", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                return  false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return refs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_download_img;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_download_img = itemView.findViewById(R.id.iv_download_img);
        }
    }
}
