package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import nl.breun.doctor_pom.IssueDetector;
import nl.breun.doctor_pom.ProjectObjectModel;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Issue detector for duplicate managed plugins.
 * Dr. Pom says you should not have the same plugin defined multiple times in plugin management to avoid
 * specifying multiple managed versions for the same plugin.
 */
@Named
class DuplicateManagedBuildPluginDetector implements IssueDetector {

    @Override
    public List<Issue> detectIssues(ProjectObjectModel projectObjectModel) {
        Model rawModel = projectObjectModel.getRawModel();
        Build build = rawModel.getBuild();
        if (build == null) {
            return Collections.emptyList();
        }

        PluginManagement pluginManagement = build.getPluginManagement();
        if (pluginManagement == null) {
            return Collections.emptyList();
        }

        List<Plugin> managedPlugins = pluginManagement.getPlugins();
        if (managedPlugins == null) {
            return Collections.emptyList();
        }

        Map<String, List<Plugin>> managedPluginsByGroupAndArtifactId = managedPlugins.stream()
                .collect(Collectors.groupingBy(managedPlugin -> managedPlugin.getGroupId() + ":" + managedPlugin.getArtifactId()));

        return managedPluginsByGroupAndArtifactId
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(entry -> new Issue("Found " + entry.getValue().size() + " plugin management entries for " + entry.getKey() + ", while there should be no more than 1."))
                .collect(Collectors.toList());
    }
}
