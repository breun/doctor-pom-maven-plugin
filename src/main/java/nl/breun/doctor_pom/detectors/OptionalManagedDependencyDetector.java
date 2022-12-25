package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import nl.breun.doctor_pom.IssueDetector;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Issue detector for managed dependencies set to be optional.
 * Dr. Pom says you should not set optionality via dependency management, but it should be set explicitly on the use
 * site of the dependency confusion and mistakes.
 */
@Named
class OptionalManagedDependencyDetector implements IssueDetector {

    @Override
    public List<Issue> detectIssues(Model rawModel) {
        if (rawModel == null) {
            return Collections.emptyList();
        }

        DependencyManagement dependencyManagement = rawModel.getDependencyManagement();
        if (dependencyManagement == null) {
            return Collections.emptyList();
        }

        List<Dependency> dependencyManagementDependencies = dependencyManagement.getDependencies();
        if (dependencyManagementDependencies == null) {
            return Collections.emptyList();
        }

        return dependencyManagementDependencies
            .stream()
            .filter(dependency -> dependency.getOptional() != null)
            .map(dependency -> new Issue("Optional is set (" + dependency.getOptional() + ") for dependency " + dependency.getGroupId() + ":" + dependency.getArtifactId() + " in dependency management, but optional should be set at the use-site."))
            .collect(Collectors.toList());
    }
}
