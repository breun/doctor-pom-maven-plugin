package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import nl.breun.doctor_pom.IssueDetector;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Issue detector for dependencies with a version.
 * Dr. Pom says you should not set a version on a dependency, but manage the version via dependency management
 * to ensure all modules use the same version.
 */
@Named
class DependencyVersionDetector implements IssueDetector {

    @Override
    public List<Issue> detectIssues(Model rawModel) {
        if (rawModel == null) {
            return Collections.emptyList();
        }

        List<Dependency> dependencies = rawModel.getDependencies();
        if (dependencies == null) {
            return Collections.emptyList();
        }

        return dependencies.stream()
            .filter(dependency -> dependency.getVersion() != null)
            .map(dependency -> new Issue("A version (" + dependency.getVersion() + ") is specified for dependency " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ", which should be managed via <dependencyManagement> instead."))
            .collect(Collectors.toList());
    }
}
