package ru.practicum.main.compilation.service;

import org.springframework.stereotype.Service;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.repository.CompilationsRepository;

import java.util.Collection;

@Service
public class CompilationsService {
    private final CompilationsRepository compilationsRepository;

    public CompilationsService(CompilationsRepository compilationsRepository) {
        this.compilationsRepository = compilationsRepository;
    }

    public Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        return null;
    }

    public CompilationDto getCompilation(Long compId) {
        return null;
    }
}
