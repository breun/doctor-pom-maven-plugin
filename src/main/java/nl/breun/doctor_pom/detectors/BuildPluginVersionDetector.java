package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import nl.breun.doctor_pom.IssueDetector;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Issue detector for build plugins with a version.
 * Dr. Pom says you should not set a version on a build plugin, but manage the version via plugin management
 * to ensure all modules use the same version.
 */
@Named
class BuildPluginVersionDetector implements IssueDetector {

    @Override
    public List<Issue> detectIssues(Model rawModel) {
        if (rawModel == null) {
            return Collections.emptyList();
        }

        Build build = rawModel.getBuild();
        if (build == null) {
            return Collections.emptyList();
        }

        List<Plugin> plugins = build.getPlugins();
        if (plugins == null) {
            return Collections.emptyList();
        }

        return plugins.stream()
            .filter(plugin -> plugin.getVersion() != null)
            .map(plugin -> new Issue("A version (" + plugin.getVersion() + ") is specified for build plugin " + plugin.getGroupId() + ":" + plugin.getArtifactId() + ", which should be managed via <pluginManagement> instead."))
            .collect(Collectors.toList());
    }
}
