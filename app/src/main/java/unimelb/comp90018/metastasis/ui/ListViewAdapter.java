package unimelb.comp90018.metastasis.ui;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import unimelb.comp90018.metastasis.gamedata.GameScore;

/**
 * Created by Purathani on 13/10/15.
 */
public class ListViewAdapter extends ArrayAdapter<GameScore> {

    Context context;
    LayoutInflater inflater;
    List<GameScore> gameScoreList;
    private SparseBooleanArray mSelectedItemsIds;

    public ListViewAdapter(Context context, int resourceId,
                           List<GameScore> bioInfoList) {
        super(context, resourceId, bioInfoList);
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;
        this.gameScoreList = bioInfoList;
        inflater = LayoutInflater.from(context);
    }

    private class ViewHolder {
        TextView textScore, textPlayerName, textDisksEaten, textGameState;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.textScore = (TextView) view.findViewById(R.id.textScore);
            holder.textPlayerName = (TextView) view.findViewById(R.id.textPlayerName);
            holder.textDisksEaten = (TextView) view.findViewById(R.id.textDisksEaten);
            holder.textGameState = (TextView) view.findViewById(R.id.textGameState);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Capture position and set to the TextViews

        final GameScore gameScore = gameScoreList.get(position); // you can remove the final modifieer.

        holder.textScore.setText(Double.toString(gameScore.getScore()));
        holder.textPlayerName.setText(gameScore.getPlayer_name());
        holder.textDisksEaten.setText(Double.toString(gameScore.getDisks_eaten()));
        holder.textGameState.setText(gameScore.getGame_state());

        return view;
    }

    @Override
    public void remove(GameScore object) {
        gameScoreList.remove(object);
        notifyDataSetChanged();
    }

    public List<GameScore> getBioInfoList() {
        return gameScoreList;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }
}
