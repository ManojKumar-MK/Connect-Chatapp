package developermk.chatapp.interfaces;

import java.util.List;

import developermk.chatapp.model.chat.Chats;

public interface OnReadChatCallBack {
    void onReadSuccess(List<Chats> list);
    void onReadFailed();
}
