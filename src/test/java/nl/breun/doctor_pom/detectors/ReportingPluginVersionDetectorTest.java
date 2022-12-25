package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import org.apache.maven.model.Model;
import org.apache.maven.model.Reporting;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.breun.doctor_pom.detectors.TestUtils.modelWithReportPlugins;
import static nl.breun.doctor_pom.detectors.TestUtils.reportPlugin;
import static org.assertj.core.api.Assertions.assertThat;

class ReportingPluginVersionDetectorTest {

    private final ReportingPluginVersionDetector detector = new ReportingPluginVersionDetector();

    @Test
    void should_report_an_issue_for_report_plugin_with_version() {
        Model model = modelWithReportPlugins(
                reportPlugin("org.apache.maven.plugins", "maven-project-info-reports-plugin", "3.4.1")
        );

        List<Issue> issues = detector.detectIssues(model);
        List<String> messages = TestUtils.getMessages(issues);

        assertThat(messages).containsExactly(
                "A version (3.4.1) is specified for reporting plugin org.apache.maven.plugins:maven-project-info-reports-plugin, which should be managed via <build><pluginManagement> instead."
        );
    }

    @Test
    void should_not_report_an_issue_for_report_plugin_without_version() {
        Model model = modelWithReportPlugins(
                reportPlugin("org.apache.maven.plugins", "maven-project-info-reports-plugin", null)
        );

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_is_no_reporting_section() {
        Model model = new Model();
        model.setReporting(null);

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_reporting_section_contains_no_plugins() {
        Model model = new Model();
        Reporting reporting = new Reporting();
        reporting.setPlugins(null);
        model.setReporting(reporting);

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_any_issues_for_null_model() {
        List<Issue> issues = detector.detectIssues(null);

        assertThat(issues).isEmpty();
    }
}