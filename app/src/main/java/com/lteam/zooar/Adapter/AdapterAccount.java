package com.lteam.zooar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lteam.zooar.Model.User;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;
import com.lteam.zooar.Ultils.UltilsLogin;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterAccount extends RecyclerView.Adapter<AdapterAccount.RecyclerViewHolder> {

    ArrayList<User> users;
    Context context;

    public AdapterAccount(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.account_view, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder recyclerViewHolder, int i) {
        final User user=users.get(i);
        Picasso.get().load(APIUtils.base_url+user.getUrlAvatar()).error(R.drawable.shark).into(recyclerViewHolder.avatar);
        recyclerViewHolder.id.setText("id: "+user.getId());
        recyclerViewHolder.displayname.setText(user.getName());


        String role="user";
        if(user.getLevel().equals("2")){
            role="admin";
        }
        recyclerViewHolder.role.setText(role);
        if(user.getStatus().equals("0")){
            recyclerViewHolder.status.setText("Trạng thái: Ngừng hoạt động");
            recyclerViewHolder.statusAccountNot.setText("Cancel The Ban");
            recyclerViewHolder.account.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bo_goc_video_xam));

        }else{
            recyclerViewHolder.status.setText("Trạng thái: Đang hoạt động");
            recyclerViewHolder.statusAccountNot.setText("Ban");
            recyclerViewHolder.account.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bo_goc_video));
        }
        recyclerViewHolder.btnBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(UltilsLogin.getUser(context).getLevel())>Integer.parseInt(user.getLevel())){
                    if(recyclerViewHolder.status.getText().toString().equals("Trạng thái: Ngừng hoạt động")){
                        recyclerViewHolder.statusAccountNot.setText("Ban");
                        recyclerViewHolder.status.setText("Trạng thái: Đang hoạt động");
                        recyclerViewHolder.account.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bo_goc_video));
                        ban(true,user.getId());
                    }else{
                        recyclerViewHolder.statusAccountNot.setText("Cancel The Ban");
                        recyclerViewHolder.status.setText("Trạng thái: Ngừng hoạt động");
                        recyclerViewHolder.account.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bo_goc_video_xam));
                        ban(false,user.getId());
                    }
                }else{
                    Toast.makeText(context, "Bạn không đủ quyền với tài khoản này", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ban(boolean b,String idUser) {
        int status=1;
        if(b==false){
            status=2;
        }

        DataClient dataClient=APIUtils.getData();
        Call<String> call=dataClient.banUser(idUser,status);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body().equals("0")) Toast.makeText(context, R.string.wrong, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(context, R.string.fail_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView id,displayname,numbervideo,role,status,statusAccountNot;
        LinearLayout btnBan,account;
        public RecyclerViewHolder(View itemView) {
            super(itemView);
            avatar=itemView.findViewById(R.id.imgAvatarManagerAccount);
            id=itemView.findViewById(R.id.txtIdUserManagerAccount);
            displayname=itemView.findViewById(R.id.txtDisplaynameManagerAccount);
            role=itemView.findViewById(R.id.txtRoleUserManagerAccount);
            status=itemView.findViewById(R.id.txtStatusUserManagerAccount);
            btnBan=itemView.findViewById(R.id.LLBtnBan);
            statusAccountNot=itemView.findViewById(R.id.txtStatusAccountNotManagerAccount);
            account=itemView.findViewById(R.id.AccountLayoutManagerAccount);
        }
    }
}
