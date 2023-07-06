package ru.practicum.main.compilation.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main.compilation.mapper.CompilationsMapper;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationsRepository;
import ru.practicum.main.error.EntityNotFoundException;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.repository.EventsRepository;
import ru.practicum.main.event.service.EventsService;
import ru.practicum.main.validation.PagingParametersChecker;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CompilationsService {
    private final CompilationsRepository compilationsRepository;
    private final EventsRepository eventsRepository;
    private final EventsService eventsService;

    public CompilationsService(CompilationsRepository compilationsRepository, EventsRepository eventsRepository, EventsService eventsService) {
        this.compilationsRepository = compilationsRepository;
        this.eventsRepository = eventsRepository;
        this.eventsService = eventsService;
    }

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Collection<Event> events = eventsRepository.findAllById(newCompilationDto.getEvents());
        Compilation compilation = compilationsRepository.save(CompilationsMapper.toCompilation(newCompilationDto, events));

        return CompilationsMapper.toCompilationDto(compilation, eventsService.getEventShortDtos(events));
    }

    public void deleteCompilation(Long compId) {
        compilationsRepository.deleteById(compId);
    }

    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationsRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка " + compId + " не найдена."));

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(eventsRepository.findAllById(updateCompilationRequest.getEvents()));
        }

        Compilation updatedCompilation = compilationsRepository.save(compilation);

        return CompilationsMapper.toCompilationDto(updatedCompilation,
                eventsService.getEventShortDtos(updatedCompilation.getEvents()));
    }

    public Collection<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        PagingParametersChecker.check(from, size);
        Pageable pageable = PageRequest.of(from / size, size);

        Collection<Compilation> compilations = compilationsRepository.findAllByPinned(pinned, pageable).toList();
        Collection<CompilationDto> compilationDtos = new ArrayList<>();

        for (Compilation compilation : compilations) {
            CompilationDto compilationDto = CompilationsMapper.toCompilationDto(compilation, eventsService.getEventShortDtos(compilation.getEvents()));
            compilationDtos.add(compilationDto);
        }

        return compilationDtos;
    }

    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationsRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка " + compId + " не найдена."));
        return CompilationsMapper.toCompilationDto(compilation,
                eventsService.getEventShortDtos(compilation.getEvents()));
    }
}
