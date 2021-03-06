package io.digdag.spi;

import org.immutables.value.Value;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.digdag.client.config.Config;
import io.digdag.client.config.ConfigFactory;

@Value.Immutable
@JsonSerialize(as = ImmutableTaskResult.class)
@JsonDeserialize(as = ImmutableTaskResult.class)
public interface TaskResult
{
    Config getSubtaskConfig();

    Config getExportParams();

    Config getStoreParams();

    TaskReport getReport();

    static ImmutableTaskResult.Builder builder()
    {
        return ImmutableTaskResult.builder();
    }

    static ImmutableTaskResult.Builder defaultBuilder(TaskRequest request)
    {
        return defaultBuilder(request.getConfig().getFactory());
    }

    static ImmutableTaskResult.Builder defaultBuilder(ConfigFactory cf)
    {
        return builder()
            .subtaskConfig(cf.create())
            .exportParams(cf.create())
            .storeParams(cf.create())
            .report(TaskReport.empty());
    }

    static TaskResult empty(TaskRequest request)
    {
        return empty(request.getConfig().getFactory());
    }

    static TaskResult empty(ConfigFactory cf)
    {
        return defaultBuilder(cf).build();
    }
}
