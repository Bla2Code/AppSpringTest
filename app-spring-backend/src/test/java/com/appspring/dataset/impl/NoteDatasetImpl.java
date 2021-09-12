package com.appspring.dataset.impl;

import com.appspring.dataset.NoteDataset;
import com.appspring.entity.Note;
import com.appspring.entity.User;
import com.appspring.repository.NoteRepository;
import com.appspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NoteDatasetImpl implements NoteDataset {

    private final UserRepository userRepository;
    private final NoteRepository noteRepository;

    @Override
    @Transactional
    public void createData() {
        User admin1 = userRepository.findByLogin("admin1")
                .orElseThrow();
        Instant now1 = Instant.now();
        Note note1 = Note.builder()
                .name("Название заметки 1")
                .description("Текст заметки 1")
                .user(admin1)
                .created(now1)
                .updated(now1)
                .build();

        User user1 = userRepository.findByLogin("user1")
                .orElseThrow();
        Instant now2 = Instant.now();
        Note note2 = Note.builder()
                .name("Название заметки 2")
                .description("Текст заметки 2")
                .user(user1)
                .created(now2)
                .updated(now2)
                .build();

        User user2 = userRepository.findByLogin("user2")
                .orElseThrow();
        Instant now3 = Instant.now();
        Note note3 = Note.builder()
                .name("Название заметки 3")
                .description("Текст заметки 3")
                .user(user2)
                .created(now3)
                .updated(now3)
                .build();

        noteRepository.saveAll(List.of(note1, note2, note3));
    }

    @Override
    @Transactional
    public void removeData() {
        noteRepository.deleteAllInBatch();
    }

}
