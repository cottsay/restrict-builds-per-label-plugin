package org.hudson.plugins.restrict_builds_per_label;

//import antlr.ANTLRException;
import hudson.Extension;
//import hudson.Util;
import hudson.model.*;
//import hudson.model.labels.LabelAtom;
//import hudson.util.FormValidation;
//import hudson.util.LogTaskListener;
import jenkins.model.GlobalConfiguration;
//import jenkins.model.Jenkins;

//import java.io.File;
import java.util.ArrayList;
import java.util.List;
//import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
//import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

@Extension
public class RestrictBuildsPerLabelConfig extends GlobalConfiguration {

    private static final Logger LOGGER = Logger.getLogger(Descriptor.class.getName());
    private List<RestrictedLabel> restrictedLabels = new ArrayList<RestrictedLabel>();

    public RestrictBuildsPerLabelConfig() {
        load();
    }

    public static RestrictBuildsPerLabelConfig get() {
        return GlobalConfiguration.all().get(RestrictBuildsPerLabelConfig.class);
    }

    @Override
    public String getDisplayName() {
        return "Restrict Builds per Label";
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        req.bindJSON(this, formData);
        save();

        return true;
    }

    public List<RestrictedLabel> getRestrictedLabels() {
        return this.restrictedLabels;
    }

    public void setRestrictedLabels(List<RestrictedLabel> restrictedLabels) {
        this.restrictedLabels = restrictedLabels == null ? new ArrayList<RestrictedLabel>() : restrictedLabels;
    }

    public Integer maxConcurrentPerNode(String label) {
        for (RestrictedLabel l : this.restrictedLabels) {
            if (l.getLabelName().equals(label)) {
                return l.getMaxConcurrentPerNode();
            }
        }
        return 0;
    }

    public static final class RestrictedLabel extends AbstractDescribableImpl<RestrictedLabel>
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
}
