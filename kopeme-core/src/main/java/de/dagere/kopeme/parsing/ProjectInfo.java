package de.dagere.kopeme.parsing;

public class ProjectInfo {
   final String artifactId, groupId;

   public ProjectInfo(final String artifactId, final String groupId) {
      this.artifactId = artifactId;
      this.groupId = groupId;
   }

   /**
    * @return the artifactId
    */
   public String getArtifactId() {
      return artifactId;
   }

   /**
    * @return the groupId
    */
   public String getGroupId() {
      return groupId;
   }
}