package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.breun.doctor_pom.detectors.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

class BuildPluginVersionDetectorTest {

    private final BuildPluginVersionDetector detector = new BuildPluginVersionDetector();

    @Test
    void should_report_an_issue_for_build_plugin_with_version() {
        Model model = modelWithBuildPlugins(
                plugin("org.jetbrains.kotlin", "kotlin-maven-plugin", "1.6.21")
        );

        List<Issue> issues = detector.detectIssues(model);
        List<String> messages = getMessages(issues);

        assertThat(messages).contains(
                "A version (1.6.21) is specified for build plugin org.jetbrains.kotlin:kotlin-maven-plugin, which should be managed via <pluginManagement> instead."
        );
    }

    @Test
    void should_not_report_an_issue_for_build_plugin_without_version() {
        Model model = modelWithBuildPlugins(
                plugin("org.apache.maven.plugins", "maven-compiler-plugin", null)
        );

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_is_no_build_section() {
        Model model = new Model();
        model.setBuild(null);

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_build_section_contains_no_plugins() {
        Model model = new Model();
        Build build = new Build();
        build.setPlugins(null);
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