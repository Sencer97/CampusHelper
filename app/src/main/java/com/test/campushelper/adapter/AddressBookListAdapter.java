package com.test.campushelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.test.campushelper.R;
import com.test.campushelper.model.Fellow;

import java.util.List;

/**
 * 通讯录列表适配器
 */
public class AddressBookListAdapter extends BaseAdapter{
    private Context context;
    private List<Fellow> fellowList;
    private ColorGenerator generator = ColorGenerator.MATERIAL;      //颜色生成器

    public AddressBookListAdapter(){}
    public AddressBookListAdapter(Context context, List<Fellow> fellowList) {
        this.context = context;
        this.fellowList = fellowList;
    }

    @Override
    public int getCount() {
        return fellowList.size();
    }

    @Override
    public Object getItem(int position) {
        return fellowList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        Fellow fellow = fellowList.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_address_book, parent,false);
            viewHolder.name =  convertView.findViewById(R.id.tv_abk_fellow);
            viewHolder.catalog = convertView.findViewById(R.id.tv_abk_catalog);
            viewHolder.thumb =  convertView.findViewById(R.id.iv_abk_thumb);
            viewHolder.mobile = convertView.findViewById(R.id.tv_abk_mobile);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position获取首字母作为目录catalog
        String catalog = fellowList.get(position).getFirstLetter();

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(catalog)){
            viewHolder.catalog.setVisibility(View.VISIBLE);
            viewHolder.catalog.setText(fellow.getFirstLetter().toUpperCase());
        }else{
            viewHolder.catalog.setVisibility(View.GONE);
        }
        viewHolder.name.setText(fellowList.get(position).getName());
        viewHolder.mobile.setText(fellowList.get(position).getMobile());
        int color = generator.getRandomColor();
        TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig().endConfig().round();
        TextDrawable icon = builder.build(viewHolder.name.getText().toString().substring(0,1),color);
        viewHolder.thumb.setImageDrawable(icon);
        return convertView;
    }

    /**
     * 获取catalog首次出现位置
     */
    public int getPositionForSection(String catalog) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = fellowList.get(i).getFirstLetter();
            if (catalog.equalsIgnoreCase(sortStr)) {
                return i;
            }
        }
        return -1;
    }
    class ViewHolder {
        ImageView thumb;
        TextView catalog;
        TextView name;
        TextView mobile;
    }
}
