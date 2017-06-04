package com.tasks.notes.storage;

import com.tasks.notes.domain.Note;

import java.util.List;

public interface StorageProvider {
    void save(Note note);
    void saveMany(List<Note> notes);
    void refreshViewedDate(long id, String visited);
    void replace(long id, Note note);
    void delete(long id);
    void deleteAll();

    List<Note> getAll();
    List<Note> searchBySubstring(String substring);
}
