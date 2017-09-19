package sg.lifecare.cumii.ui.dashboard.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import sg.lifecare.cumii.R;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MemberViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private ArrayList<AssistsedEntityResponse.Data> mMembers = new ArrayList<>();

    private OnItemClickListener mListener;

    public MemberListAdapter(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_member, parent, false);


        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    public void replaceMembers(List<AssistsedEntityResponse.Data> members) {
        mMembers.clear();

        if (members != null) {
            mMembers.addAll(members);
        }

        notifyDataSetChanged();
    }


    class MemberViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.name_text)
        TextView mNameText;

        @BindView(R.id.profile_image)
        CircleImageView mProfileImage;

        MemberViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        void bindView(int position) {
            AssistsedEntityResponse.Data member = mMembers.get(position);

            mNameText.setText(member.getName());

            String avatar = member.getDefaultMediaUrl();

            if (TextUtils.isEmpty(avatar)) {
                mProfileImage.setImageResource(R.drawable.ic_avatar);

            } else {
                Glide.with(itemView.getContext())
                        .load(avatar)
                        .into(mProfileImage);
            }

            if (mListener != null) {
                itemView.setOnClickListener(v -> mListener.onItemClick(position));
            }
        }
    }
}
