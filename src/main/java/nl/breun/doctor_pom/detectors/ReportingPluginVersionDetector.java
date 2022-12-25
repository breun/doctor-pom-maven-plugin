package nl.breun.doctor_pom.detectors;

import nl.breun.doctor_pom.Issue;
import nl.breun.doctor_pom.IssueDetector;
import nl.breun.doctor_pom.ProjectObjectModel;
import org.apache.maven.model.Model;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.Reporting;

import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Issue detector for report plugins with a version.
 * Dr. Pom says you should not set a version on a report plugin, but manage the version via plugin management
 * to ensure all report plugins use the same version.
 */
@Named
class ReportingPluginVersionDetector implements IssueDetector {

    @Override
    public List<Issue> detectIssues(ProjectObjectModel projectObjectModel) {
        Model rawModel = projectObjectModel.getRawModel();
        Reporting reporting = rawModel.getReporting();
        if (reporting == null) {
            return Collections.emptyList();
        }

        List<ReportPlugin> reportPlugins = reporting.getPlugins();
        if (reportPlugins == null) {
            return Collections.emptyList();
        }

        return reportPlugins.stream()
                .filter(reportPlugin -> reportPlugin.getVersion() != null)
                .map(reportPlugin -> new Issue("A version (" + reportPlugin.getVersion() + ") is specified for reporting plugin " + reportPlugin.getGroupId() + ":" + reportPlugin.getArtifactId() + ", which should be managed via <build><pluginManagement> instead."))
                .collect(Collectors.toList());
    }
}
