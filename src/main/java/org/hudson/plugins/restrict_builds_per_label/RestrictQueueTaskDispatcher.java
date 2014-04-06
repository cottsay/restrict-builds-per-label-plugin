package org.hudson.plugins.restrict_builds_per_label;

import hudson.Extension;
import hudson.matrix.MatrixConfiguration;
import hudson.model.Computer;
import hudson.model.Executor;
import hudson.model.Node;
import hudson.model.Queue.BuildableItem;
import hudson.model.Queue.Task;
import hudson.model.Label;
import hudson.model.labels.LabelAtom;
import hudson.model.queue.CauseOfBlockage;
import hudson.model.queue.QueueTaskDispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class RestrictQueueTaskDispatcher extends QueueTaskDispatcher {

    private static final Logger LOGGER = Logger.getLogger(RestrictQueueTaskDispatcher.class.getName());

    RestrictBuildsPerLabelConfig config = RestrictBuildsPerLabelConfig.get();

    @Override
    public CauseOfBlockage canTake(Node node, BuildableItem item) {
        if (item.task instanceof MatrixConfiguration) {
            return null;
        }

        Label taskl = item.getAssignedLabel();
        Set<LabelAtom> nodel = node.getAssignedLabels();
        Computer comp = node.toComputer();
        List<Executor> execs = comp == null ? new ArrayList<Executor>() : comp.getExecutors();
        List<Task> tlist = new ArrayList<Task>();
        for (Executor exec: execs) {
            if(exec.getCurrentExecutable() != null && exec.getCurrentExecutable().getParent() != null) {
                tlist.add(exec.getCurrentExecutable().getParent().getOwnerTask());
            }
        }
        LOGGER.log(Level.FINEST, String.format("discovered %d other tasks on %s", tlist.size(), node.getNodeName()));

        for (LabelAtom a : nodel) {
            for (LabelAtom b : taskl.listAtoms()) {
                if (a.getName().equals(b.getName())) {
                    int count = 1;
                    int max = config.maxConcurrentPerNode(a.getName());
                    for (Task t : tlist) {
                        for (LabelAtom c : t.getAssignedLabel().listAtoms()) {
                            if (a.getName().equals(c.getName()))
                                count++;
                        }
                    }
                    if (max != 0 && count > max) {
                        LOGGER.log(Level.FINE, String.format("%s blocked on %s on tag %s with count %d (max %d)!", item.task.getName(), node.getNodeName(), a.getName(), count, max)); 
                        return CauseOfBlockage.fromMessage(Messages._RestrictQueueTaskDispatcher_LabelLimit(max, a.getName()));
                    }
                    else
                        LOGGER.log(Level.FINER, String.format("%s allowed on %s on tag %s with count %d (max %d)!", item.task.getName(), node.getNodeName(), a.getName(), count, max)); 
                }
            }
        }

        return null;
    }
}
