package com.mottc.coze.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;
import com.mottc.coze.R;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/7
 * Time: 19:56
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<EMMessage> mValues;

    public ChatAdapter(List<EMMessage> values) {
        mValues = values;
    }

    @Override
    public int getItemViewType(int position) {

        EMMessage emMessage = mValues.get(position);
        if (emMessage.direct() == EMMessage.Direct.RECEIVE) {
            if (emMessage.getType().equals(EMMessage.Type.TXT)) {
                return 0;
            } else if (emMessage.getType().equals(EMMessage.Type.IMAGE)) {
                return 1;
            } else {
                return 2;
            }

        } else {
            if (emMessage.getType().equals(EMMessage.Type.TXT)) {
                return 3;
            } else if (emMessage.getType().equals(EMMessage.Type.IMAGE)) {
                return 4;
            } else {
                return 5;
            }
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;

        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.receive_txt, parent, false);
                return new ReceiveTxtHolder(view);
            break;
            case 1:
                view = inflater.inflate(R.layout.receive_image, parent, false);
                return new ReceiveImageHolder(view);
            break;
            case 2:
                view = inflater.inflate(R.layout.receive_voice, parent, false);
                return new ReceiveVoice(view);
            break;

            case 3:
                view = inflater.inflate(R.layout.send_txt, parent, false);
                return new SendTxtHolder(view);
            break;

            case 4:
                view = inflater.inflate(R.layout.send_image, parent, false);
                return new FootHolder(view);
            break;

            case 5:
                view = inflater.inflate(R.layout.item_foot, parent, false);
                return new FootHolder(view);
            break;

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ReceiveTxtHolder extends RecyclerView.ViewHolder {
        public ReceiveTxtHolder(View itemView) {
            super(itemView);

        }
    }

    public class ReceiveImageHolder extends RecyclerView.ViewHolder {
        public ReceiveImageHolder(View itemView) {
            super(itemView);

        }
    }

    public class ReceiveVoice extends RecyclerView.ViewHolder {
        public ReceiveVoice(View itemView) {
            super(itemView);

        }
    }
    public class SendTxtHolder extends RecyclerView.ViewHolder {
        public SendTxtHolder(View itemView) {
            super(itemView);

        }
    }

}
