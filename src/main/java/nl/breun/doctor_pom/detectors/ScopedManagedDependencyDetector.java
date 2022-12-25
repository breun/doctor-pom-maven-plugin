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
 * Issue detector for managed dependencies with a scope.
 * Dr. Pom says you should not set a scope on a managed dependency, but only for an actual dependency,
 * so the scope is explicitly defined at the use site. This avoids confusion and mistakes.
 */
@Named
class ScopedManagedDependencyDetector implements IssueDetector {

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
                .filter(dependency -> {
                    String scope = dependency.getScope();
                    return scope != null && !scope.equals("import"); // Scope 'import' is allowed, as that is especially meant to be used in <dependencyManagement>.
                })
                .map(dependency -> new Issue("Scope '" + dependency.getScope() + "' is specified for dependency " + dependency.getGroupId() + ":" + dependency.getArtifactId() + " in dependency management, but this scope should be set at the use-site."))
                .collect(Collectors.toList());
    }
}
