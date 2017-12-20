package com.tasks.notes.ui.infrastructure;

import com.tasks.notes.data.model.Note;

public interface OnItemStateChangedListener {
    void onItemChanged(Note note, int position);
    void onItemRemoved(int position);
}
