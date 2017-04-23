package com.home.aleksandrnazarenko.yandextranslator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.home.aleksandrnazarenko.yandextranslator.FavoriteTab.MemoryWords;
import com.home.aleksandrnazarenko.yandextranslator.data.DbHelper;
import com.home.aleksandrnazarenko.yandextranslator.data.DbMethods;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {


    private List<MemoryWords> items;
    private List<MemoryWords> mCleanCopyDataset;
    private static DbHelper mDbHelper;

    static ClipData myClip;
    static ClipboardManager clipboard;

    // класс view holder-а с помощью которого мы получаем ссылку на каждый элемент
    // отдельного пункта списка
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView SourceTV;
        TextView TranslateTV;
        TextView DirectionTV;
        ImageButton FvrtBtn;

        public ViewHolder(View v) {
            super(v);
             SourceTV = (TextView) v.findViewById(R.id.textView_source_txt);
             TranslateTV = (TextView) v.findViewById(R.id.textView_translate_txt);
             DirectionTV = (TextView) v.findViewById(R.id.textView_direction);
             FvrtBtn = (ImageButton) v.findViewById(R.id.TabFavoriteBtn);
             v.setOnClickListener(this);
             mDbHelper = new DbHelper(v.getContext());
        }

        @Override
        public void onClick(View v) {

            myClip = ClipData.newPlainText("text", TranslateTV.getText());
            clipboard= (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(myClip);
            Toast toast = Toast.makeText(v.getContext(), "Слово: "+TranslateTV.getText()+" скопировано!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    // Конструктор
    public RecyclerAdapter(List<MemoryWords> FavoriteWords,int itemLayout) {
        this.items = FavoriteWords;
        this.mCleanCopyDataset = this.items;
    }




    // Создает новые views (вызывается layout manager-ом)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
       final MemoryWords str =  items.get(position);
        holder.SourceTV.setText(str.getSource());
        holder.TranslateTV.setText(str.getTranslate());
        holder.DirectionTV.setText(str.getDirection());
        if (str.getFavorite()==0) {
            holder.FvrtBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }else
        {
            holder.FvrtBtn.setImageResource(R.drawable.ic_favorite_black_24dp);
        }

        //вешаем обработку нажатий на итем в целом, чтобы оно скопировалось в буфер
        //вешаем обработку нажатий на кнопку избраного
        holder.FvrtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (str.getFavorite()==1) {
                        //убираем из базы
                        DbMethods.deleteFavorite(str.getSource(), str.getTranslate(), str.getDirection(), mDbHelper);
                        EventBus.getDefault().post(new FragmentDataSync("FavoriteDelete",position,str.getSource(),str.getTranslate(),str.getDirection(),0));
                    }
                    else
                    {
                        //добавялем в базу и отправялем сообщение о синхронизации
                        DbMethods.insertFavorite(str.getSource(), str.getTranslate(), str.getDirection(), mDbHelper);
                        EventBus.getDefault().post(new FragmentDataSync("FavoriteAdd",position,str.getSource(),str.getTranslate(),str.getDirection(),1));
                    }
                } catch(IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }
            }
        });


    }

    // Возвращает размер данных (вызывается layout manager-ом)
    @Override
    public int getItemCount() {
        return items.size();
    }

    //метод поиска
    public void filter(String charText) {
        items = new ArrayList<MemoryWords>();
        charText = charText.toLowerCase();
        if (charText.length() == 0) {
            items.addAll(mCleanCopyDataset);
        } else {
            for(int i=0; i < mCleanCopyDataset.size(); i++) {
                if (mCleanCopyDataset.get(i).getSource().toLowerCase().contains(charText)
                        || mCleanCopyDataset.get(i).getTranslate().toLowerCase().contains(charText)) {
                    items.add(new MemoryWords(mCleanCopyDataset.get(i).getSource(), mCleanCopyDataset.get(i).getTranslate(), mCleanCopyDataset.get(i).getDirection(),mCleanCopyDataset.get(i).getFavorite()));
                }

            }
        }
        notifyDataSetChanged();
    }

    public void RefreshData()
    {
        //Костыль, иначе после поиска не при изменение избранноого, нет синхронизации с экранами
        items = new ArrayList<MemoryWords>();
        items.addAll(mCleanCopyDataset);
        notifyDataSetChanged();
    }


}
