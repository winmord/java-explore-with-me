package ru.practicum.main.admin.compilation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.UpdateCompilationRequest;

@RestController
@RequestMapping("/admin/compilations")
public class AdminCompilationsController {
    private final AdminCompilationsService adminCompilationsService;

    public AdminCompilationsController(AdminCompilationsService adminCompilationsService) {
        this.adminCompilationsService = adminCompilationsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        return adminCompilationsService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Integer compId) {
        adminCompilationsService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Integer compId,
                                            @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return adminCompilationsService.updateCompilation(compId, updateCompilationRequest);
    }
}
