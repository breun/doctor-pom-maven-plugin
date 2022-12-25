package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nl.breun.doctor_pom.detectors.TestUtils.dependency;
import static nl.breun.doctor_pom.detectors.TestUtils.modelWithManagedDependencies;
import static org.assertj.core.api.Assertions.assertThat;

class DuplicateManagedDependencyDetectorTest {

    private final DuplicateManagedDependencyDetector detector = new DuplicateManagedDependencyDetector();

    @Test
    void should_report_an_issue_for_identical_duplicate_managed_dependencies() {
        Model model = modelWithManagedDependencies(
                dependency("org.springframework", "spring-beans", "5.3.20"),
                dependency("org.springframework", "spring-beans", "5.3.20")
        );

        List<Issue> issues = detector.detectIssues(model);
        List<String> messages = TestUtils.getMessages(issues);

        assertThat(messages).containsExactly(
                "Found 2 dependency management entries for org.springframework:spring-beans, while there should be no more than 1.");
    }

    @Test
    void should_report_an_issue_for_multiple_managed_dependencies_with_the_same_group_id_and_artifact_id() {
        Model model = modelWithManagedDependencies(
                dependency("org.springframework.boot", "spring-boot", "2.6.8"),
                dependency("org.springframework.boot", "spring-boot", "2.6.7")
        );

        List<Issue> issues = detector.detectIssues(model);
        List<String> messages = TestUtils.getMessages(issues);

        assertThat(messages).containsExactly(
                "Found 2 dependency management entries for org.springframework.boot:spring-boot, while there should be no more than 1.");
    }

    @Test
    void should_not_report_an_issue_for_different_managed_dependencies() {
        Model model = modelWithManagedDependencies(
                dependency("org.springframework", "spring-core", "5.3.20"),
                dependency("org.springframework", "spring-beans", "5.3.20")
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