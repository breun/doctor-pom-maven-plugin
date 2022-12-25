package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.breun.doctor_pom.detectors.TestUtils.dependency;
import static nl.breun.doctor_pom.detectors.TestUtils.modelWithManagedDependencies;
import static org.assertj.core.api.Assertions.assertThat;

class ScopedManagedDependencyDetectorTest {

    private final ScopedManagedDependencyDetector detector = new ScopedManagedDependencyDetector();

    @Test
    void should_report_an_issue_for_managed_dependencies_with_a_scope() {
        Model model = modelWithManagedDependencies(
                dependency("org.springframework", "spring-beans", "5.3.20", "compile"),
                dependency("junit", "junit", "4.13.2", "test")
        );

        List<Issue> issues = detector.detectIssues(model);
        List<String> messages = TestUtils.getMessages(issues);

        assertThat(messages).containsExactly(
                "Scope 'compile' is specified for dependency org.springframework:spring-beans in dependency management, but this scope should be set at the use-site.",
                "Scope 'test' is specified for dependency junit:junit in dependency management, but this scope should be set at the use-site."
        );
    }

    @Test
    void should_not_report_an_issue_for_managed_dependencies_without_a_scope() {
        Model model = modelWithManagedDependencies(
                dependency("org.springframework", "spring-core", "5.3.20", null),
                dependency("org.springframework", "spring-beans", "5.3.20", null)
        );

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_for_a_managed_dependency_with_import_scope() {
        Model model = modelWithManagedDependencies(
                dependency("org.springframework.boot", "spring-boot-dependencies", "2.7.3", "import")
        );

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_are_is_no_dependency_management() {
        Model model = new Model();
        model.setDependencyManagement(null);

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_are_are_no_managed_dependencies() {
        Model model = new Model();
        DependencyManagement dependencyManagement = new DependencyManagement();
        model.setDependencyManagement(dependencyManagement);

        List<Issue> issues = detector.detectIssues(model);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_any_issues_for_null_model() {
        List<Issue> issues = detector.detectIssues(null);

        assertThat(issues).isEmpty();
    }
}