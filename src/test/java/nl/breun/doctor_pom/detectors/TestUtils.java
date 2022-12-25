package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;

import nl.breun.doctor_pom.ProjectObjectModel;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.maven.model.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class TestUtils {

    /*
     * Utility methods to create a Build instance
     */

    static Build buildWithPlugins(Plugin... buildPlugins) {
        Build build = new Build();
        List<Plugin> pluginList = Arrays.asList(buildPlugins);
        build.setPlugins(pluginList);
        return build;
    }

    /*
     * Utility methods to create a Dependency instance
     */

    static Dependency dependency(String groupId, String artifactId, @Nullable String version) {
        Dependency dependency = new Dependency();
        dependency.setGroupId(groupId);
        dependency.setArtifactId(artifactId);
        dependency.setVersion(version);
        return dependency;
    }

    static Dependency dependency(String groupId, String artifactId, @Nullable String version, boolean optional) {
        Dependency dependency = dependency(groupId, artifactId, version);
        dependency.setOptional(optional);
        return dependency;
    }

    static Dependency dependency(String groupId, String artifactId, @Nullable String version, @Nullable String scope) {
        Dependency dependency = dependency(groupId, artifactId, version);
        dependency.setScope(scope);
        return dependency;
    }

    /*
     * Utility methods to create a ProjectObjectModel instance
     */

    static Model projectObjectModelWithBuildPlugins(Plugin... plugins) {
        Model model = new Model();
        Build build = buildWithPlugins(plugins);
        model.setBuild(build);
        return model;
    }

    static ProjectObjectModel projectObjectModelWithDependencies(Dependency... dependencies) {
        Model model = new Model();
        List<Dependency> dependencyList = Arrays.asList(dependencies);
        model.setDependencies(dependencyList);
        return new ProjectObjectModel(null, model);
    }

    static ProjectObjectModel projectObjectModelWithManagedBuildPlugins(Plugin... managedPlugins) {
        Model model = new Model();
        Build build = new Build();
        PluginManagement pluginManagement = new PluginManagement();
        List<Plugin> managedPluginsList = Arrays.asList(managedPlugins);
        pluginManagement.setPlugins(managedPluginsList);
        build.setPluginManagement(pluginManagement);
        model.setBuild(build);
        return new ProjectObjectModel(null, model);
    }

    static ProjectObjectModel projectObjectModelWithManagedDependencies(Dependency... managedDependencies) {
        Model model = new Model();
        DependencyManagement dependencyManagement = new DependencyManagement();
        List<Dependency> managedDependenciesList = Arrays.asList(managedDependencies);
        dependencyManagement.setDependencies(managedDependenciesList);
        model.setDependencyManagement(dependencyManagement);
        return new ProjectObjectModel(null, model);
    }

    static ProjectObjectModel projectObjectModelWithReportPlugins(ReportPlugin... reportPlugins) {
        Model model = new Model();
        Reporting reporting = reportingWithPlugins(reportPlugins);
        model.setReporting(reporting);
        return new ProjectObjectModel(null, model);
    }

    /*
     * Utility methods to create a Plugin instance
     */

    static Plugin plugin(String groupId, String artifactId, @Nullable String version) {
        Plugin plugin = new Plugin();
        plugin.setGroupId(groupId);
        plugin.setArtifactId(artifactId);
        plugin.setVersion(version);
        return plugin;
    }

    /*
     * Utility methods to create a Reporting instance
     */

    static Reporting reportingWithPlugins(ReportPlugin... reportingPlugins) {
        Reporting reporting = new Reporting();
        List<ReportPlugin> reportingPluginList = Arrays.asList(reportingPlugins);
        reporting.setPlugins(reportingPluginList);
        return reporting;
    }

    /*
     * Utility methods to create a ReportPlugin instance
     */

    static ReportPlugin reportPlugin(String groupId, String artifactId, @Nullable String version) {
        ReportPlugin reportPlugin = new ReportPlugin();
        reportPlugin.setGroupId(groupId);
        reportPlugin.setArtifactId(artifactId);
        reportPlugin.setVersion(version);
        return reportPlugin;
    }

    static List<String> getMessages(List<Issue> issues) {
        return issues.stream()
                .map(Issue::getMessage)
                .collect(Collectors.toList());
    }
}
