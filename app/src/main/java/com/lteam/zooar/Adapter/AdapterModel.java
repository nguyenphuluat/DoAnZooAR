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

import com.lteam.zooar.Model.Animal;
import com.lteam.zooar.R;
import com.lteam.zooar.Server.APIUtils;
import com.lteam.zooar.Server.DataClient;
import com.lteam.zooar.Ultils.UltilsLogin;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterModel extends RecyclerView.Adapter<AdapterModel.RecyclerViewHolder> {

    ArrayList<Animal> animals;
    Context context;

    public AdapterModel(ArrayList<Animal> animals, Context context) {
        this.animals = animals;
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
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        final Animal animal = animals.get(position);
        Picasso.get().load(APIUtils.base_url + animal.getUrlimage()).error(R.drawable.shark).into(holder.avatar);
        holder.id.setText("id: " + animal.getId());
        holder.displayname.setText(animal.getName());

        holder.role.setVisibility(View.INVISIBLE);
        if (animal.getStatus().equals("2")) {
            holder.status.setText("Trạng thái: Ngừng hoạt động");
            holder.statusAccountNot.setText("Cancel The Ban");
            holder.account.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bo_goc_video_xam));

        } else {
            holder.status.setText("Trạng thái: Đang hoạt động");
            holder.statusAccountNot.setText("Ban");
            holder.account.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bo_goc_video));
        }
        holder.btnBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(UltilsLogin.getUser(context).getLevel()) > Integer.parseInt(animal.getLevel())) {
                    if (holder.status.getText().toString().equals("Trạng thái: Ngừng hoạt động")) {
                        holder.statusAccountNot.setText("Ban");
                        holder.status.setText("Trạng thái: Đang hoạt động");
                        holder.account.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bo_goc_video));
                        ban(true, animal.getId());
                    } else {
                        holder.statusAccountNot.setText("Cancel The Ban");
                        holder.status.setText("Trạng thái: Ngừng hoạt động");
                        holder.account.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bo_goc_video_xam));
                        ban(false, animal.getId());
                    }
                } else {
                    Toast.makeText(context, "Bạn không đủ quyền với tài khoản này", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ban(boolean b, String idModel) {
        int status = 1;
        if (b == false) {
            status = 2;
        }

        DataClient dataClient = APIUtils.getData();
        Call<String> call = dataClient.banModel(idModel, status);
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
        return animals.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView id, displayname, numbervideo, role, status, statusAccountNot;
        LinearLayout btnBan, account;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgAvatarManagerAccount);
            id = itemView.findViewById(R.id.txtIdUserManagerAccount);
            displayname = itemView.findViewById(R.id.txtDisplaynameManagerAccount);
            role = itemView.findViewById(R.id.txtRoleUserManagerAccount);
            status = itemView.findViewById(R.id.txtStatusUserManagerAccount);
            btnBan = itemView.findViewById(R.id.LLBtnBan);
            statusAccountNot = itemView.findViewById(R.id.txtStatusAccountNotManagerAccount);
            account = itemView.findViewById(R.id.AccountLayoutManagerAccount);
        }
    }
}
