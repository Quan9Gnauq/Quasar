package com.example.quasar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CardAdapter extends BaseAdapter {

    Context context;
    Cursor cursor;

    public CardAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        cursor.moveToPosition(position);
        return cursor;
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(cursor.getColumnIndexOrThrow("id"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_card, parent, false);
        }

        cursor.moveToPosition(position);

        ImageView img = convertView.findViewById(R.id.imgCard);
        TextView name = convertView.findViewById(R.id.txtName);
        TextView rarity = convertView.findViewById(R.id.txtRarity);

        String cardName = cursor.getString(
                cursor.getColumnIndexOrThrow("name"));
        String imageName = cursor.getString(
                cursor.getColumnIndexOrThrow("image"));
        String cardRarity = cursor.getString(
                cursor.getColumnIndexOrThrow("rarity"));

        int resId = context.getResources().getIdentifier(
                imageName, "drawable", context.getPackageName());

        if (resId != 0) {
            img.setImageResource(resId);
        }

        name.setText(cardName);
        rarity.setText(cardRarity);

        return convertView;
    }
}
