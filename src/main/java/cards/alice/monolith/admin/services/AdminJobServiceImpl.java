package cards.alice.monolith.admin.services;

import cards.alice.monolith.common.services.JobService;
import cards.alice.monolith.common.web.exceptions.JobClassNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminJobServiceImpl implements AdminJobService {
    private final ApplicationContext context;
    private final ScheduledAnnotationBeanPostProcessor scheduledAnnotationBeanPostProcessor;

    @Override
    public List<String> listScheduledTasks() {
        return scheduledAnnotationBeanPostProcessor
                .getScheduledTasks().stream().map(ScheduledTask::toString).toList();
    }

    @Override
    public void stopJobServiceByFqcn(String fqcn) {
        final var bean = getJobServiceBean(fqcn);
        scheduledAnnotationBeanPostProcessor.postProcessBeforeDestruction(bean, fqcn);
    }

    @Override
    public void resumeJobServiceByFqcn(String fqcn) {
        stopJobServiceByFqcn(fqcn);
        final var bean = getJobServiceBean(fqcn);
        scheduledAnnotationBeanPostProcessor.postProcessAfterInitialization(bean, fqcn);
    }

    @Override
    public void modifyJobServiceSchedule(String fqcn, String cron, String zone) {
        // TODO: How to change cron, zone in runtime?
        // Looks like CronTask's params are pulled dynamically from spring properties, so maybe just edit property map?
        throw new UnsupportedOperationException();
    }

    private Object getJobServiceBean(String fqcn) {
        final Class<?> jobServiceClass = getJobServiceClass(fqcn);
        return context.getBean(jobServiceClass);
    }

    private Class<?> getJobServiceClass(String fqcn) {
        try {
            Class<?> clazz = Class.forName(fqcn);
            if (!JobService.class.isAssignableFrom(clazz)) {
                throw new ClassNotFoundException("Not assignable to JobService: " + fqcn);
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new JobClassNotFoundException("Failed to get job class: " + fqcn, e);
        }
    }
}
