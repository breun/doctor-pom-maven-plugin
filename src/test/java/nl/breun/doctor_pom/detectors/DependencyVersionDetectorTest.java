package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.breun.doctor_pom.detectors.TestUtils.dependency;
import static nl.breun.doctor_pom.detectors.TestUtils.modelWithDependencies;
import static org.assertj.core.api.Assertions.assertThat;

class DependencyVersionDetectorTest {

    private final DependencyVersionDetector detector = new DependencyVersionDetector();

    @Test
    void should_report_an_issue_for_dependency_with_version() {
        Model projectObjectModel = modelWithDependencies(
                dependency("junit", "junit", "4.13.2")
        );

        List<Issue> issues = detector.detectIssues(projectObjectModel);
        List<String> messages = TestUtils.getMessages(issues);

        assertThat(messages).contains(
                "A version (4.13.2) is specified for dependency junit:junit, which should be managed via <dependencyManagement> instead.");
    }

    @Test
    void should_not_report_an_issue_for_dependency_without_version() {
        Model projectObjectModel = modelWithDependencies(
                dependency("org.junit.jupiter", "junit-jupiter-api", null)
        );

        List<Issue> issues = detector.detectIssues(projectObjectModel);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_are_no_dependencies() {
        Model model = new Model();
        model.setDependencies(null);

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_any_issues_for_null_model() {
        List<Issue> issues = detector.detectIssues(null);

        assertThat(issues).isEmpty();
    }
}