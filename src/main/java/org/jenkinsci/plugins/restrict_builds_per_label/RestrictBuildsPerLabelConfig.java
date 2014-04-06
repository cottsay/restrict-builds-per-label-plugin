package org.jenkinsci.plugins.restrict_builds_per_label;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import jenkins.model.GlobalConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

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
}
