package nl.breun.doctor_pom;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.Result;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "find-issues", defaultPhase = LifecyclePhase.VALIDATE, threadSafe = true)
class FindIssuesMojo extends AbstractMojo {

    private final ModelBuilder modelBuilder;

    private final List<IssueDetector> issueDetectors;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "false")
    private boolean skip;

    @Parameter(defaultValue = "true")
    private boolean failOnIssues;

    @Inject
    public FindIssuesMojo(ModelBuilder modelBuilder, List<IssueDetector> issueDetectors) {
        this.modelBuilder = modelBuilder;
        this.issueDetectors = issueDetectors;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Parameter 'skip' is set to true, skipping execution");
            return;
        }

        File pomFile = mavenProject.getFile();
        ProjectObjectModel projectObjectModel = buildProjectObjectModel(pomFile);

        List<Issue> issues = runIssueDetectors(projectObjectModel);

        logIssuesIfAny(pomFile, issues);
        throwIfApplicable(pomFile, issues);
    }

    private ProjectObjectModel buildProjectObjectModel(File pomFile) throws MojoExecutionException {
        Model rawModel = getRawModel(pomFile);
        return new ProjectObjectModel(pomFile, rawModel);
    }

    private List<Issue> runIssueDetectors(ProjectObjectModel projectObjectModel) {
        List<Issue> allIssues = new ArrayList<>();
        for (IssueDetector issueDetector : issueDetectors) {
            List<Issue> issues = issueDetector.detectIssues(projectObjectModel);
            allIssues.addAll(issues);
        }
        return allIssues;
    }

    private Model getRawModel(File pomFile) throws MojoExecutionException {
        // Build raw instead of effective model, so Doctor Pom can detect issues with the source POM
        Result<? extends Model> result = modelBuilder.buildRawModel(pomFile, ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL, true);

        if (result.hasErrors()) {
            result.getProblems().forEach(problem -> getLog().error(problem.getMessage() + " (line: " + problem.getLineNumber() + ", column: " + problem.getColumnNumber() + ")"));
            throw new MojoExecutionException("Encountered problems while reading " + pomFile + ". See above for problems.");
        }

        return result.get();
    }

    private void logIssuesIfAny(File pomFile, List<Issue> issues) {
        if (!issues.isEmpty()) {
            getLog().warn("Doctor Pom found issues in " + pomFile);

            issues.forEach(issue -> {
                String logMessage = issue.getMessage();
                getLog().warn(logMessage);
            });
        }
    }

    private void throwIfApplicable(File pomFile, List<Issue> issues) {
        if (failOnIssues && !issues.isEmpty()) {
            throw new DoctorPomException(pomFile, issues, "Doctor Pom failed this build, because issues were found. Address these issues, or set 'failOnIssues' to 'false'.");
        }
    }
}
