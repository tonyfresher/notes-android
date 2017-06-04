package com.tasks.notes.infrastructure;

import com.tasks.notes.domain.Note;

public interface OnItemStateChangedListener {
    void onItemChanged(Note note, int position);
    void onItemRemoved(int position);
}
