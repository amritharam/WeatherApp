package com.example.amritha.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    private final int VIEW_TYPE_TODAY =0;
    private final int VIEW_TYPE_FUTURE_DAY=1;
    private boolean mUseTodayLayout;

    public static class ViewHolder {
                public final ImageView iconView;
                public final TextView dateView;
                public final TextView descriptionView;
                public final TextView highTempView;
                public final TextView lowTempView;

                        public ViewHolder(View view) {
                        iconView = (ImageView) view.findViewById(R.id.list_item_icon);
                        dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
                        descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
                        highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
                        lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
                    }
            }

                public ForecastAdapter(Context context, Cursor c, int flags) {
                super(context, c, flags);
            }
    /**
     * Prepare the weather high/lows for presentation.
     */


    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }


    @Override
    public int getItemViewType(int position) {
        return (position==0 && mUseTodayLayout) ? VIEW_TYPE_TODAY:VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" + Utility.formatTemperature(mContext, low, isMetric);
        return highLowStr;
    }
    /*
            This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
            string.
         */


    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));
        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }
    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
        int viewType = getItemViewType(cursor.getPosition());
        int layoutid=-1;
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutid = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutid = R.layout.list_item_forecast;
                break;
            }
        }
        View view=LayoutInflater.from(context).inflate(layoutid,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }
    /*
            This is where we fill-in the views with the contents of the cursor.
         */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        // Read weather icon ID from cursor
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.iconView.setImageResource(R.mipmap.ic_launcher);

        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                // Get weather icon
                viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(
                        cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                // Get weather icon
                viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(
                        cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
                break;
            }
        }
        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context,dateInMillis));
        String desc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(desc);
        viewHolder.iconView.setContentDescription(desc);
        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context,high,isMetric));
        viewHolder.lowTempView.setText(Utility.formatTemperature(context,low,isMetric));
    }
}