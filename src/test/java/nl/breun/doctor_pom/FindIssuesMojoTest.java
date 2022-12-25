package nl.breun.doctor_pom;

import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.*;

public class FindIssuesMojoTest {

    @Rule
    public MojoRule rule = new MojoRule();

    @Test
    public void buildDependencyVersionOutsideDependencyManagement() {
        assertDoctorPomExceptionWithMessages(
                "build-dependency-version-outside-dependency-management",
                "A version (1.2.3) is specified for dependency com.example:foo, which should be managed via <dependencyManagement> instead."
        );
    }

    @Test
    public void buildPluginVersionOutsidePluginManagement() {
        assertDoctorPomExceptionWithMessages(
                "build-plugin-version-outside-plugin-management",
                "A version (1.2.3) is specified for build plugin com.example:foo, which should be managed via <pluginManagement> instead."
        );
    }

    @Test
    public void reportingPluginVersionOutsidePluginManagement() {
        assertDoctorPomExceptionWithMessages(
                "reporting-plugin-version-outside-plugin-management",
                "A version (1.2.3) is specified for reporting plugin com.example:foo, which should be managed via <build><pluginManagement> instead."
        );
    }

    @Test
    public void managedDependencyWithScope() {
        assertDoctorPomExceptionWithMessages(
                "managed-dependency-with-scope",
                "Scope 'test' is specified for dependency com.example:bar in dependency management, but this scope should be set at the use-site."
        );
    }

    @Test
    public void duplicateManagedBuildDependency() {
        assertDoctorPomExceptionWithMessages(
                "duplicate-managed-build-dependency",
                "Found 2 dependency management entries for com.example:bar, while there should be no more than 1."
        );
    }

    @Test
    public void duplicateManagedBuildPlugin() {
        assertDoctorPomExceptionWithMessages(
                "duplicate-managed-build-plugin",
                "Found 2 plugin management entries for com.example:bar, while there should be no more than 1."
        );
    }

    @Test
    public void allowNotFailingOnIssues() {
        assertThatNoException().isThrownBy(() -> executeForPomXmlInTestDirectory("dont-fail-on-issues"));
    }

    @Test
    public void allowSkippingExecution() {
        assertThatNoException().isThrownBy(() -> executeForPomXmlInTestDirectory("skip"));
    }

    private void assertDoctorPomExceptionWithMessages(String directoryName, String... errorMessages) {
        Throwable throwable = catchThrowable(() -> executeForPomXmlInTestDirectory(directoryName));
        assertThat(throwable).isInstanceOf(DoctorPomException.class);
        DoctorPomException doctorPomException = (DoctorPomException) throwable;
        assertThat(doctorPomException.getIssues().stream().map(Issue::getMessage)).containsExactly(errorMessages);
    }

    private void executeForPomXmlInTestDirectory(String testResourceDirectoryName) throws Exception {
        File basedir = new File("src/test/resources/" + testResourceDirectoryName);
        FindIssuesMojo findIssuesMojo = (FindIssuesMojo) rule.lookupConfiguredMojo(basedir, "find-issues");
        findIssuesMojo.execute();
    }
}