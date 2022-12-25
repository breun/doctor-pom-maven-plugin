package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import nl.breun.doctor_pom.IssueDetector;
import nl.breun.doctor_pom.ProjectObjectModel;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Issue detector for duplicate managed dependencies.
 * Dr. Pom says you should have no more than 1 managed dependency for a single artifact to avoid
 * specifying multiple managed versions for the same artifact.
 */
@Named
class DuplicateManagedDependencyDetector implements IssueDetector {

    @Override
    public List<Issue> detectIssues(ProjectObjectModel projectObjectModel) {
        Model rawModel = projectObjectModel.getRawModel();
        DependencyManagement dependencyManagement = rawModel.getDependencyManagement();
        if (dependencyManagement == null) {
            return Collections.emptyList();
        }

        List<Dependency> managedDependencies = dependencyManagement.getDependencies();
        if (managedDependencies == null) {
            return Collections.emptyList();
        }

        Map<String, List<Dependency>> managedDependenciesByGroupAndArtifactId = managedDependencies.stream()
                .collect(Collectors.groupingBy(managedDependency -> managedDependency.getGroupId() + ":" + managedDependency.getArtifactId()));

        return managedDependenciesByGroupAndArtifactId
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(entry -> new Issue("Found " + entry.getValue().size() + " dependency management entries for " + entry.getKey() + ", while there should be no more than 1."))
                .collect(Collectors.toList());
    }
}