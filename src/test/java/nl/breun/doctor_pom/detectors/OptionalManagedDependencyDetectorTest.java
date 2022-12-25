package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.ProjectObjectModel;

import nl.breun.doctor_pom.Issue;

import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static nl.breun.doctor_pom.detectors.TestUtils.dependency;

class OptionalManagedDependencyDetectorTest {

    private final OptionalManagedDependencyDetector detector = new OptionalManagedDependencyDetector();

    @Test
    void should_report_an_issue_for_managed_dependencies_with_optional() {
        ProjectObjectModel projectObjectModel = TestUtils.projectObjectModelWithManagedDependencies(
                TestUtils.dependency("org.springframework", "spring-beans", "5.3.20", true),
                TestUtils.dependency("junit", "junit", "4.13.2", false)
        );

        List<Issue> issues = detector.detectIssues(projectObjectModel);
        List<String> messages = TestUtils.getMessages(issues);

        assertThat(messages).contains(
                "Optional is set (true) for dependency org.springframework:spring-beans in dependency management, but optional should be set at the use-site.",
                "Optional is set (false) for dependency junit:junit in dependency management, but optional should be set at the use-site."
        );
    }

    @Test
    void should_not_report_an_issue_for_managed_dependencies_without_optional() {
        ProjectObjectModel projectObjectModel = TestUtils.projectObjectModelWithManagedDependencies(
                TestUtils.dependency("org.springframework", "spring-core", "5.3.20"),
                TestUtils.dependency("org.springframework", "spring-beans", "5.3.20")
        );

        List<Issue> issues = detector.detectIssues(projectObjectModel);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_are_is_no_dependency_management() {
        Model model = new Model();
        model.setDependencyManagement(null);
        ProjectObjectModel projectObjectModel = new ProjectObjectModel(null, model);

        List<Issue> issues = detector.detectIssues(projectObjectModel);

        assertThat(issues).isEmpty();
    }

    @Test
    void should_not_report_an_issue_when_there_are_are_no_managed_dependencies() {
        Model model = new Model();
        DependencyManagement dependencyManagement = new DependencyManagement();
        model.setDependencyManagement(dependencyManagement);
        ProjectObjectModel projectObjectModel = new ProjectObjectModel(null, model);

        List<Issue> issues = detector.detectIssues(projectObjectModel);

        assertThat(issues).isEmpty();
    }
}