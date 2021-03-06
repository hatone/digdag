package io.digdag.core.queue;

import java.util.Set;
import java.util.Map;
import com.google.inject.Inject;
import com.google.common.base.*;
import com.google.common.collect.*;
import io.digdag.spi.TaskQueue;
import io.digdag.spi.TaskQueueServer;
import io.digdag.spi.TaskQueueClient;
import io.digdag.spi.TaskQueueFactory;
import io.digdag.client.config.Config;
import io.digdag.client.config.ConfigException;
import io.digdag.client.config.ConfigFactory;
import io.digdag.core.repository.ResourceNotFoundException;

public class TaskQueueManager
{
    private final QueueSettingStoreManager qm;
    private final TaskQueue taskQueue;

    @Inject
    public TaskQueueManager(QueueSettingStoreManager qm, Config systemConfig, Set<TaskQueueFactory> factories)
    {
        this.qm = qm;

        ImmutableMap.Builder<String, TaskQueueFactory> builder = ImmutableMap.builder();
        for (TaskQueueFactory factory : factories) {
            builder.put(factory.getType(), factory);
        }
        Map<String, TaskQueueFactory> queueTypes = builder.build();

        this.taskQueue = queueTypes.get("database").getTaskQueue(systemConfig);    // TODO make this configurable?
    }

    // used by executors through TaskQueueDispatcher
    public TaskQueueServer getTaskQueueServer()
    {
        return taskQueue.getServer();
    }

    // used by agents excepting LocalAgentManager
    public TaskQueueClient getTaskQueueClient(int siteId)
    {
        TaskQueueClient client = taskQueue.getDirectClientIfSupported();
        if (client != null) {
            return client;
        }
        throw new UnsupportedOperationException("HTTP task queue client is not implemented yet");  // TODO implement TaskQueueClient that calls REST API of this server
    }

    // used by LocalAgentManager, InProcessTaskCallbackApi digdag-server (TaskResource, which is not implemented yet)
    public TaskQueueClient getInProcessTaskQueueClient()
    {
        return getTaskQueueServer();
    }
}
