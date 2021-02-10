package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.mapper.NoteMapper;
import com.udacity.jwdnd.course1.cloudstorage.model.Note;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    private final NoteMapper noteMapper;

    public NoteService(NoteMapper noteMapper) {
        this.noteMapper = noteMapper;
    }

    public Integer save(Note note){return noteMapper.insert(note);}

    public List<Note> getAllNotes(int userId){
        List<Note> allNotes = noteMapper.getNotes(userId);
        for (Note n : allNotes){
            n.setNoteDescriptionForJS(n.getNoteDescription().replaceAll("\r\n","\\\\n"));
        }

        return allNotes;
    }

    public String delete(int noteId){
        // check the existence of note
        if(noteMapper.getNoteById(noteId)!=null){
            noteMapper.delete(noteId);
            return "Successfully deleted the note.";
        } else {
            return "note does not exist";
        }
    }

    public void update(Note note){noteMapper.update(note);}
}
