package com.test.campushelper.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.model.TechProject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectHolder>{
    private List<TechProject> projList;
    private Context context;
    private ProjectsAdapter.OnItemClickListener mItemClickListener;

    public ProjectsAdapter(Context context,List<TechProject> list){
        this.context = context;
        this.projList = list;
    }

    @Override
    public ProjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_techproj,parent,false);
        ProjectHolder holder = new ProjectHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ProjectHolder holder, final int position) {
        TechProject project = projList.get(position);

        //使用Glide加载头像
        Glide.with(context)
                .load(project.getHeadUrl())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade()
                .into(holder.head);
        holder.nickname.setText(project.getName());
        holder.time.setText(project.getTime());
        holder.depart.setText(project.getDepart());
        holder.projName.setText(project.getProjName());
        holder.projGoal.setText(project.getProjGoal());
        holder.projNeed.setText(project.getProjNeed());
        holder.projPeriod.setText(project.getProjPeriod());
        holder.projReplacedCourse.setText(project.getReplacedCourse());
        holder.projReplacedCredit.setText(project.getReplacedCredit());
        holder.projReplacedReason.setText(project.getReplaceReason());

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v,position);
            }
        };
        if (mItemClickListener != null) {
            holder.head.setOnClickListener(clickListener);
            holder.nickname.setOnClickListener(clickListener);
            holder.depart.setOnClickListener(clickListener);
            holder.time.setOnClickListener(clickListener);
            holder.ll_projBody.setOnClickListener(clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return projList.size();
    }

    /**
     * 添加项目item
     * @param i
     * @param proj
     */
    public void addData(int i, TechProject proj) {
        projList.add(i,proj);
        notifyItemInserted(i);
        notifyItemRangeChanged(i,projList.size());
    }

    class ProjectHolder extends RecyclerView.ViewHolder{
        private CircleImageView head;
        private TextView nickname;
        private TextView time;
        private TextView depart;
        private TextView projName;
        private TextView projGoal;
        private TextView projNeed;
        private TextView projPeriod;
        private TextView projReplacedCourse;
        private TextView projReplacedCredit;
        private TextView projReplacedReason;
        private LinearLayout ll_projBody;

        public ProjectHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.civ_tech_head);
            nickname = itemView.findViewById(R.id.tv_tech_name);
            time = itemView.findViewById(R.id.tv_tech_time);
            depart = itemView.findViewById(R.id.tv_tech_department);
            projName = itemView.findViewById(R.id.tv_tech_proj_name);
            projGoal = itemView.findViewById(R.id.tv_tech_proj_goal);
            projNeed = itemView.findViewById(R.id.tv_tech_proj_need);
            projPeriod = itemView.findViewById(R.id.tv_tech_proj_period);
            projReplacedCourse = itemView.findViewById(R.id.tv_tech_proj_replaced_course);
            projReplacedCredit = itemView.findViewById(R.id.tv_tech_proj_replaced_credit);
            projReplacedReason = itemView.findViewById(R.id.tv_tech_proj_replaced_reason);
            ll_projBody = itemView.findViewById(R.id.ll_project_body);
        }
    }
    /**
     * item的回调接口
     */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    /**
     * 定义一个设置点击监听器的方法
     * @param itemClickListener
     */
    public void setOnItemClickListener(ProjectsAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }
}
