package sg.ntu.cz2002.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import sg.ntu.cz2002.R;
import sg.ntu.cz2002.entity.Location;

/**
 * Created by Moistyburger on 24/10/15.
 */
public class CategoryAdapter extends ArrayAdapter {
    private Context mContext;
    private ArrayList<Location.Category> categories;
    private Location.Category category;
    private ArrayList<Location.Category> selectedCategories;

    public CategoryAdapter(Context context, int resource, ArrayList<Location.Category> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.categories = objects;
    }


    public ArrayList<Location.Category> getSelectedCategories(){
        return  this.selectedCategories;
    }
    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Location.Category getItem(int position) {
        return categories.get(position);
    }


    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        convertView = mInflater.inflate(R.layout.categroylist, null);
        holder = new ViewHolder();
        convertView.setTag(holder);

        holder.title = (TextView) convertView.findViewById(R.id.categoryTitle);
        holder.checkBox = (CheckBox) convertView.findViewById(R.id.categoryCheckbox);
        holder.layout = (LinearLayout) convertView.findViewById(R.id.categorylistlayout);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.checkBox.isChecked()){
                    holder.checkBox.setChecked(false);
                    selectedCategories.remove(categories.get(i));
                }
                else{
                    holder.checkBox.setChecked(true);
                    if(selectedCategories==null)
                        selectedCategories = new ArrayList<Location.Category>();
                    selectedCategories.add(categories.get(i));
                }
            }
        });
        category = categories.get(i);

        holder.title.setText(category.toString());
        return convertView;
    }

    class ViewHolder {
        public TextView title;
        public CheckBox checkBox;
        public LinearLayout layout;
    }
}

