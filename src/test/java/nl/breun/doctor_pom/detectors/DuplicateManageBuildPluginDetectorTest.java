package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.PluginManagement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.breun.doctor_pom.detectors.TestUtils.plugin;
import static nl.breun.doctor_pom.detectors.TestUtils.modelWithManagedBuildPlugins;
import static org.assertj.core.api.Assertions.assertThat;

class DuplicateManageBuildPluginDetectorTest {

    private final DuplicateManagedBuildPluginDetector detector = new DuplicateManagedBuildPluginDetector();

    @Test
    void should_report_an_issue_for_identical_duplicate_managed_plugins() {
        Model model = modelWithManagedBuildPlugins(
                plugin("org.apache.maven.plugins", "maven-plugin-plugin", "3.6.4"),
                plugin("org.apache.maven.plugins", "maven-plugin-plugin", "3.6.4")
        );

        List<Issue> issues = detector.detectIssues(model);
        List<String> messages = TestUtils.getMessages(issues);

        assertThat(messages).containsExactly("Found 2 plugin management entries for org.apache.maven.plugins:maven-plugin-plugin, while there should be no more than 1.");
    }

    @Test
    void should_report_an_issue_for_multiple_managed_build_plugins_with_the_same_group_id_and_artifact_id() {
        Model model = modelWithManagedBuildPlugins(
                plugin("org.apache.maven.plugins", "maven-plugin-plugin", "3.6.4"),
                plugin("org.apache.maven.plugins", "maven-plugin-plugin", "3.6.3")
        );

        List<Issue> issues = detector.detectIssues(model);
        List<String> messages = TestUtils.getMessages(issues);

        assertThat(messages).containsExactly("Found 2 plugin management entries for org.apache.maven.plugins:maven-plugin-plugin, while there should be no more than 1.");
    }

    @Test
    void should_not_report_an_issue_for_different_managed_dependencies() {
        Model model = modelWithManagedBuildPlugins(
                plugin("org.apache.maven.plugins", "maven-plugin-plugin", "3.6.4"),
                plugin("org.apache.maven.plugins", "maven-pdf-plugin", "1.6.1")
        );

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_are_is_no_build_section() {
        Model model = new Model();
        model.setBuild(null);

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_are_is_no_build_plugin_management_section() {
        Model model = new Model();
        Build build = new Build();
        build.setPluginManagement(null);
        model.setBuild(build);

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_are_are_no_managed_build_plugins() {
        Model model = new Model();
        Build build = new Build();
        PluginManagement pluginManagement = new PluginManagement();
        pluginManagement.setPlugins(null);
        build.setPluginManagement(pluginManagement);
        model.setBuild(build);

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_any_issues_for_null_model() {
        List<Issue> issues = detector.detectIssues(null);

        assertThat(issues).isEmpty();
    }
}