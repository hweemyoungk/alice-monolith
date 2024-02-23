package cards.alice.monolith.admin.web.controllers;

import cards.alice.monolith.admin.services.AdminJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${cards.alice.admin.web.controllers.path.base}")
@RequiredArgsConstructor
public class AdminJobController {
    private final AdminJobService resourceJobService;

    @GetMapping(path = "${cards.alice.admin.web.controllers.path.resource.job}")
    public ResponseEntity<List<String>> listJobs() {
        return ResponseEntity.ok(resourceJobService.listScheduledTasks());
    }

    @PutMapping(path = "${cards.alice.admin.web.controllers.path.resource.job}/{fqcn}/stop")
    public ResponseEntity<Void> stopJobService(@PathVariable String fqcn) {
        resourceJobService.stopJobServiceByFqcn(fqcn);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "${cards.alice.admin.web.controllers.path.resource.job}/{fqcn}/start")
    public ResponseEntity<Void> startJobService(@PathVariable String fqcn) {
        resourceJobService.resumeJobServiceByFqcn(fqcn);
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "${cards.alice.admin.web.controllers.path.resource.job}/{fqcn}")
    public ResponseEntity<Void> modifySchedule(
            @PathVariable String fqcn,
            @RequestParam(required = false) String cron,
            @RequestParam(required = false) String zone) {
        resourceJobService.modifyJobServiceSchedule(fqcn, cron, zone);
        return ResponseEntity.ok().build();
    }
}
