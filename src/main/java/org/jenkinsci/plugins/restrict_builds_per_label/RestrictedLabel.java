package org.jenkinsci.plugins.restrict_builds_per_label;

import hudson.Extension;

import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class RestrictedLabel extends AbstractDescribableImpl<RestrictedLabel>
{
    private String labelName;
    private Integer maxConcurrentPerNode;

    @DataBoundConstructor
    public RestrictedLabel(String labelName, Integer maxConcurrentPerNode) {
        this.labelName = labelName;
        this.maxConcurrentPerNode = maxConcurrentPerNode == null ? new Integer(0) : maxConcurrentPerNode;
    }

    public String getLabelName() {
        return this.labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public Integer getMaxConcurrentPerNode() {
        if(this.maxConcurrentPerNode == null)
            this.maxConcurrentPerNode = new Integer(0);
        return this.maxConcurrentPerNode;
    }

    public void getMaxConcurrentPerNode(Integer maxConcurrentPerNode) {
        this.maxConcurrentPerNode = maxConcurrentPerNode == null ? new Integer(0) : maxConcurrentPerNode;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<RestrictedLabel> {
        public String getDisplayName() { return ""; }
    }
}
