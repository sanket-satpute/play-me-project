package com.sanket_satpute_20.playme.project.account.recycler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.sanket_satpute_20.playme.R;
import com.sanket_satpute_20.playme.project.account.data.documentation.AppDocs;
import com.sanket_satpute_20.playme.project.account.data.documentation.AppDocsComments;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DocumentationParentRecycler extends RecyclerView.Adapter<DocumentationParentRecycler.DocumentationParentHolder> {

    Activity context;
    ArrayList<AppDocs> documentationItems;
    int currentViewPagerPos;

    int time_delay_to_change_image = 30000;

    public DocumentationParentRecycler(Activity context, ArrayList<AppDocs> documentationItems) {
        this.context = context;
        this.documentationItems = documentationItems;
    }

    @NonNull
    @Override
    public DocumentationParentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_documentation_view_parent_layout, parent, false);
        return new DocumentationParentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentationParentHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(documentationItems.get(position).getTitle());
        String helper = documentationItems.get(position).getHelper_str();
        if (helper != null) {
            holder.helper_txt.setVisibility(View.VISIBLE);
            holder.helper_txt.setText(helper);
        } else {
            holder.helper_txt.setVisibility(View.GONE);
        }
        holder.image_viewpager.setVisibility(View.VISIBLE);
        holder.image_viewpager.setAdapter(new DocImagePagerAdapter(context, documentationItems.get(position).getPictures()));
        holder.image_viewpager.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

        holder.image_viewpager.setClipToPadding(false);
        holder.image_viewpager.setClipChildren(false);
        holder.image_viewpager.setOffscreenPageLimit(3);
        holder.image_viewpager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        holder.image_viewpager.setPageTransformer(compositePageTransformer);

        holder.previous_fab.setOnClickListener(view -> {
            currentViewPagerPos = holder.image_viewpager.getCurrentItem();
            holder.image_viewpager.setCurrentItem((currentViewPagerPos == 0) ? documentationItems.get(position).getPictures().size() :  --currentViewPagerPos);
            currentViewPagerPos = holder.image_viewpager.getCurrentItem();
        });

        holder.next_fab.setOnClickListener(view -> {
            currentViewPagerPos = holder.image_viewpager.getCurrentItem();
            holder.image_viewpager.setCurrentItem((currentViewPagerPos == (documentationItems.get(position).getPictures().size() - 1)) ? 0 :  ++currentViewPagerPos);
            currentViewPagerPos = holder.image_viewpager.getCurrentItem();
        });


        for (int i = 0; i < documentationItems.get(position).getPictures().size(); i++) {
            holder.indicators_layout.addTab(holder.indicators_layout.newTab(), i);
        }

        if (holder.indicators_layout.getTabAt(0) != null)
            Objects.requireNonNull(holder.indicators_layout.getTabAt(0)).select();

        if (documentationItems.get(position).getPictures().size() <= 1) {
            setDescriptionWithViewPagerImages(holder, documentationItems.get(position).getSideByPictureThatInfo().get(0), position, 0);
        } else {
            holder.image_viewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int positionVP, float positionOffset, int positionOffsetPixels) {
                    super.onPageScrolled(positionVP, positionOffset, positionOffsetPixels);
                    if (holder.indicators_layout.getTabAt(positionVP) != null)
                        Objects.requireNonNull(holder.indicators_layout.getTabAt(positionVP)).select();
                    setDescriptionWithViewPagerImages(holder, documentationItems.get(position).getSideByPictureThatInfo().get(positionVP), position, positionVP);
                }
            });

            holder.indicators_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int tab_position = holder.image_viewpager.getCurrentItem();
                    if (holder.indicators_layout.getTabAt(tab_position) != null)
                        Objects.requireNonNull(holder.indicators_layout.getTabAt(tab_position)).select();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

            Handler handler = new Handler();
            Runnable taskUpdate = () -> {
                currentViewPagerPos++;
                if (holder.image_viewpager.getCurrentItem() == (documentationItems.get(position).getPictures().size() - 1))
                    currentViewPagerPos = 0;
                holder.image_viewpager.setCurrentItem(currentViewPagerPos, true);
                Log.d("item_is", currentViewPagerPos + "");
            };
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(taskUpdate);
                }
            }, 0, time_delay_to_change_image);
        }

    }

    @Override
    public int getItemCount() {
        return documentationItems.size();
    }

    public static class DocumentationParentHolder extends RecyclerView.ViewHolder {

        TextView title, helper_txt;
        ViewPager2 image_viewpager;
        RelativeLayout documentation_relative;
        FloatingActionButton previous_fab, next_fab;
        TabLayout indicators_layout;

        public DocumentationParentHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.main_title_txt);
            image_viewpager = itemView.findViewById(R.id.images_view_pager);
            helper_txt = itemView.findViewById(R.id.helper_txt);
            documentation_relative = itemView.findViewById(R.id.documentation_child_relative);
            previous_fab = itemView.findViewById(R.id.previous_image_fab);
            next_fab = itemView.findViewById(R.id.next_image_fab);
            indicators_layout = itemView.findViewById(R.id.image_slider_indicators_layout);
        }
    }

    private void setDescriptionWithViewPagerImages(DocumentationParentHolder holder, ArrayList<AppDocsComments> strDocsList, int position, int positionVP) {
        holder.documentation_relative.removeAllViews();
        for (int i = 0; (i < strDocsList.size()); i++) {
            AppDocsComments appDocs = strDocsList.get(i);
            int txt_id = Integer.parseInt((position + "" + positionVP + "" + i));
            String main_topic_str = appDocs.getMain_topic();
            String comment = appDocs.getDot_char() + " " + main_topic_str +
                    " : " + appDocs.getDetail_of_topic();
            SpannableString spannableString = new SpannableString(comment);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, (main_topic_str.length() + 3), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int alpha = 128;
            ColorStateList colorStateList = ColorStateList.valueOf(holder.title.getCurrentTextColor());
            colorStateList = ColorStateList.valueOf(colorStateList.getDefaultColor() & 0x00ffffff | (alpha << 24));
            ForegroundColorSpan fore_color_span = new ForegroundColorSpan(colorStateList.getDefaultColor());
            spannableString.setSpan(fore_color_span, (main_topic_str.length() + 3), comment.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            RelativeLayout.LayoutParams rl_layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            TextView txt_1 = new TextView(context);
            if (i > 0) {
                rl_layout.addRule(RelativeLayout.BELOW, Integer.parseInt(position+""+positionVP+""+(i-1)));
            }
            txt_1.setId(txt_id);
            txt_1.setText(spannableString);
            ViewGroup.MarginLayoutParams txt_params = (ViewGroup.MarginLayoutParams) txt_1.getLayoutParams();
            if (txt_params != null) {
                int marginInDp = 16; // Set the margin in dp
                int marginInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginInDp, context.getResources().getDisplayMetrics());

                txt_params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);
                txt_1.setLayoutParams(txt_params);
            }
            holder.documentation_relative.addView(txt_1, rl_layout);
        }
    }

}
