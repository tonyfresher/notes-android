package com.tasks.notes.infrastructure;

import com.tasks.notes.domain.Note;

public interface OnItemAddedListener {
    void onItemAdded(Note note);
}
