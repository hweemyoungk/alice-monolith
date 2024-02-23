package cards.alice.monolith.admin.services;

import java.util.List;

public interface AdminJobService {
    List<String> listScheduledTasks();

    void stopJobServiceByFqcn(String fqcn);

    void resumeJobServiceByFqcn(String fqcn);

    void modifyJobServiceSchedule(String fqcn, String cron, String zone);
}
